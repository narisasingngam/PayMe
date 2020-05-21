package com.example.payme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Map;

public class Profile extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private TextView userName,accountText;
    private EmailFormat emailFormat = new EmailFormat();
    private DatabaseReference ref;
    private EditText inputAccount;
    private Button updateProfileBtn;
    private DatabaseReference myRef,mdatabaseRef;
    private ImageButton imageButton;
    private Firebase mRootRef;
    private StorageReference mStorage;
    public static final int READ_EXTERNAL_STORAGE = 0;
    private static final int GALLERY_INTENT = 2;
    private Uri mImageUri = null;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Firebase.setAndroidContext(this);

        userName = (TextView) findViewById(R.id.userName);
        mAuth = FirebaseAuth.getInstance();
        inputAccount = (EditText) findViewById(R.id.inputAccount);
        updateProfileBtn = (Button) findViewById(R.id.updateProfileBtn);
        accountText = (TextView) findViewById(R.id.accountText);
        imageButton = (ImageButton) findViewById(R.id.imageButton);

        // initialise the progress bar
        mProgressDialog = new ProgressDialog(Profile.this);

        String name = emailFormat.decodeUserEmail(mAuth.getCurrentUser().getEmail()).replaceAll("@hotmail.com","");
        userName.setText(name);

        myRef = FirebaseDatabase.getInstance().getReference(emailFormat.encodeUserEmail(mAuth.getCurrentUser().getEmail()));

        if(myRef.child("accounts") != null){

            myRef.child("accounts").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    String value = dataSnapshot.getValue(String.class);
                    Log.d("test", "Value is: " + value);
                    accountText.setText(value);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("bug", "Failed to read value.", error.toException());
                }
            });
        }

        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBankAccount();
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check permission
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Call for permission", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
                    }
                } else {
                    callGallery();
                }
            }
        });

        if(myRef.child("User_Detail") != null){

            myRef.child("User_Detail").child("Image_URL").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
//                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    String map = dataSnapshot.getValue(String.class);
                    Log.d("mint", "Value is: " + map);

                    if(map == null){
                        imageButton.setImageResource(R.drawable.ic_account_circle_black_125dp);
                    }else{
                        Glide.with(getApplicationContext())
                                .load(map)
                                .crossFade()
                                .thumbnail(01.f)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(imageButton);
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("test", "Failed to read value.", error.toException());
                }
            });

            imageButton.setImageURI(mImageUri);

        }

        //initialise firebase
        mdatabaseRef = FirebaseDatabase.getInstance().getReference();
        mRootRef = new Firebase("https://payme-241a2.firebaseio.com/").child(emailFormat.encodeUserEmail(mAuth.getCurrentUser().getEmail())).child("User_Detail");
        mStorage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://payme-241a2.appspot.com/");



    }

    public void onRequestPermissionResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permission, grantResults);
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    callGallery();
                return;
        }
        Toast.makeText(getApplicationContext(), "...", Toast.LENGTH_SHORT).show();
    }

    // if access is granted, gallery will be opened
    private void callGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }

    // after select image
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            imageButton.setImageURI(mImageUri);
            StorageReference filePath = mStorage.child("User_Images").child(mImageUri.getLastPathSegment());

            mProgressDialog.setMessage("Uploading...");
            mProgressDialog.show();

            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if (taskSnapshot.getMetadata() != null) {
                        if (taskSnapshot.getMetadata().getReference() != null) {
                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    mRootRef.child("Image_URL").setValue(imageUrl);
                                    Glide.with(getApplicationContext())
                                            .load(uri)
                                            .crossFade()
//                                          .placeholder(R.drawable.loading)
                                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                            .into(imageButton);
                                    Toast.makeText(getApplicationContext(), "Updated...", Toast.LENGTH_SHORT).show();
                                    mProgressDialog.dismiss();
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    public void updateBankAccount(){
        ref = FirebaseDatabase.getInstance().getReference(emailFormat.encodeUserEmail(mAuth.getCurrentUser().getEmail()));

        ref.child("accounts").setValue(inputAccount.getText().toString());
    }
}
