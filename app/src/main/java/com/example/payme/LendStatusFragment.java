package com.example.payme;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LendStatusFragment extends Fragment {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    private FirebaseRecyclerAdapter<ViewSingleLendItem, ShowDataViewHolder> mFirebaseAdapter;

    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    private DatabaseReference inputMsg;
    RecyclerView recyclerView;
    DatabaseReference ref, imageRef;
    View view;

    private EmailFormat emailFormat = new EmailFormat();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        ref = FirebaseDatabase.getInstance().getReference(emailFormat.encodeUserEmail(mAuth.getCurrentUser().getEmail())).child("owe");

        view = inflater.inflate(R.layout.fragment_lend, container, false);
        final FragmentActivity c = getActivity();
        recyclerView = (RecyclerView) view.findViewById(R.id.viewPostLendData);
        LinearLayoutManager layoutManager = new LinearLayoutManager(c);
        recyclerView.setLayoutManager(layoutManager);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<ViewSingleLendItem, ShowDataViewHolder>(ViewSingleLendItem.class, R.layout.view_single_lend,ShowDataViewHolder.class, ref) {

            @Override
            protected void populateViewHolder(final ShowDataViewHolder showDataViewHolder, final ViewSingleLendItem viewSingleStatusItem, final int i) {
                showDataViewHolder.Image_Title(viewSingleStatusItem.getTitle());
                showDataViewHolder.Lend_Name(viewSingleStatusItem.getLendName());
                showDataViewHolder.Lend_Price(viewSingleStatusItem.getPrice());
                showDataViewHolder.Lend_Date(viewSingleStatusItem.getDate());


                imageRef = FirebaseDatabase.getInstance().getReference(emailFormat.encodeUserEmail(viewSingleStatusItem.getLendName()));

                if(imageRef.child("User_Detail") != null){

                    imageRef.child("User_Detail").child("Image_URL").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            String map = dataSnapshot.getValue(String.class);
                            Log.d("mint", "Value is: " + map);

                            if(map == null){
                                showDataViewHolder.Image_icon();
                            }else{
                                showDataViewHolder.Image_URL(map);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w("test", "Failed to read value.", error.toException());
                        }
                    });
                }


                showDataViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        myRef = FirebaseDatabase.getInstance().getReference(emailFormat.encodeUserEmail(viewSingleStatusItem.getLendName())).child("accounts");

                        if(myRef != null) {

                            myRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    String value = dataSnapshot.getValue(String.class);
                                    Log.d("test", "Value is: " + value);

                                    if(value == null){
                                        builder.setMessage("No account").setCancelable(false)
                                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                    }
                                                });
                                    }
                                    else{
                                        builder.setMessage(value).setCancelable(false)
                                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                    }
                                                });
                                    }
                                    AlertDialog dialog = builder.create();
                                    dialog.setTitle("Bank Accounts");
                                    dialog.show();

                                }
                                @Override
                                public void onCancelled(DatabaseError error) {
                                    // Failed to read value
                                    Log.w("bug", "Failed to read value.", error.toException());
                                }
                            });

                        }

                    }
                });

            }

        };

        recyclerView.setAdapter(mFirebaseAdapter);

    }


    public static class ShowDataViewHolder extends RecyclerView.ViewHolder {

        private final TextView image_title,lendName,lendPrice,lendDate;
        private final ImageView imageView;

        public ShowDataViewHolder(final View itemView) {
            super(itemView);
            image_title = (TextView) itemView.findViewById(R.id.lend_title);
            lendName = (TextView) itemView.findViewById(R.id.NameUserLend);
            lendPrice = (TextView) itemView.findViewById(R.id.lend_price);
            lendDate = (TextView) itemView.findViewById(R.id.lend_date);
            imageView = (ImageView) itemView.findViewById(R.id.imageViewIconLend);
        }

        private void Image_Title(String title) {
            image_title.setText(title);
        }

        private void Lend_Name(String name){
            name =  name.replaceAll("@hotmail.com","");
            lendName.setText(name);
        }

        private void Lend_Price(String price){
            lendPrice.setText(price+"฿");
        }

        private void Lend_Date(String date){
            lendDate.setText(date);
        }

        private void Image_URL(String title) {
            Glide.with(itemView.getContext())
                    .load(title)
                    .crossFade()
                    .thumbnail(01.f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }

        private void Image_icon(){
            imageView.setImageResource(R.drawable.ic_account_circle_black_24dp);
        }

    }
}
