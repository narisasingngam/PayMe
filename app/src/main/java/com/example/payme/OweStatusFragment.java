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


public class OweStatusFragment extends Fragment {

//    RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference imageRef;
    private FirebaseRecyclerAdapter<ViewSingleStatusItem, ShowDataViewHolder> mFirebaseAdapter;

    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    private DatabaseReference inputMsg;
     RecyclerView recyclerView;
    DatabaseReference ref, deleteRef;
    View view;

    private EmailFormat emailFormat = new EmailFormat();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        ref = FirebaseDatabase.getInstance().getReference(emailFormat.encodeUserEmail(mAuth.getCurrentUser().getEmail())).child("lend");

        view = inflater.inflate(R.layout.fragment_status, container, false);
        final FragmentActivity c = getActivity();
        recyclerView = (RecyclerView) view.findViewById(R.id.viewPostStatusData);
        LinearLayoutManager layoutManager = new LinearLayoutManager(c);
        recyclerView.setLayoutManager(layoutManager);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<ViewSingleStatusItem, ShowDataViewHolder>(ViewSingleStatusItem.class, R.layout.view_single_status, ShowDataViewHolder.class, ref) {

            @Override
            protected void populateViewHolder(final ShowDataViewHolder showDataViewHolder, final ViewSingleStatusItem viewSingleStatusItem, final int i) {
                showDataViewHolder.Image_Title(viewSingleStatusItem.getTitle());
                showDataViewHolder.Owe_Name(viewSingleStatusItem.getOweName());
                showDataViewHolder.Owe_Price(viewSingleStatusItem.getPrice());
                showDataViewHolder.Owe_Date(viewSingleStatusItem.getDate());


                imageRef = FirebaseDatabase.getInstance().getReference(emailFormat.encodeUserEmail(viewSingleStatusItem.getOweName()));

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

                //Click at any row of the list and we will delete that row out of the database

                showDataViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Delete this history means you already received your money. Are you sure to confirm?").setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int selectedItems = i;
                                        mFirebaseAdapter.getRef(selectedItems).removeValue();
                                        mFirebaseAdapter.notifyItemRemoved(selectedItems);
                                        recyclerView.invalidate();

                                        //delete another user
                                        final DatabaseReference gameRef= getRef(i);
                                        final String postKey = gameRef.getKey();
                                        deleteRef = FirebaseDatabase.getInstance().getReference(emailFormat.encodeUserEmail(viewSingleStatusItem.getOweName())).child("owe").child(postKey);
                                        deleteRef.removeValue();

                                        onStart();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });

            }

        };

        recyclerView.setAdapter(mFirebaseAdapter);

    }

    public static class ShowDataViewHolder extends RecyclerView.ViewHolder {

        private final TextView image_title,oweName,owePrice,oweDate;
        private final ImageView imageView;

        public ShowDataViewHolder(final View itemView) {
            super(itemView);
            image_title = (TextView) itemView.findViewById(R.id.fetch_image_title);
            oweName = (TextView) itemView.findViewById(R.id.NameUser);
            owePrice = (TextView) itemView.findViewById(R.id.owe_price);
            oweDate = (TextView) itemView.findViewById(R.id.owe_date);
            imageView = (ImageView) itemView.findViewById(R.id.imageViewIcon);
        }

        private void Image_Title(String title) {
            image_title.setText(title);
        }

        private void Owe_Name(String name){
           name =  name.replaceAll("@hotmail.com","");
            oweName.setText(name);
        }

        private void Owe_Price(String price){
            owePrice.setText(price+"฿");
        }

        private void Owe_Date(String date){
            oweDate.setText(date);
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
