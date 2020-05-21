package com.example.payme;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.ArrayList;
import java.util.List;

public class RequestDialog extends AppCompatDialogFragment {

    private EditText edit_username;
    private FirebaseAuth mAuth;
    private Friend friend;
    private ExampleDialogListener listener;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.layout_dialog,null);

        edit_username  = view.findViewById(R.id.edit_username);
        mAuth = FirebaseAuth.getInstance();

        builder.setView(view)
                .setTitle("Enter your friend's email")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })

                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String email = edit_username.getText().toString();
                        mAuth.fetchSignInMethodsForEmail(email)
                                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                        if (task.isSuccessful()) {

                                            boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                                            if (isNewUser) {
                                                Toast.makeText(view.getContext(),"This email not exist",Toast.LENGTH_SHORT).show();
                                                return;

                                            } else {
                                                friend = Friend.getInstance();
                                                friend.setEmail(email);
                                                friend.addEmail(friend.getEmail());
                                                Toast.makeText(view.getContext(),"Added",Toast.LENGTH_SHORT).show();
                                                listener.applyTexts(friend.getListEmail());
                                            }
                                        }else{
                                            Toast.makeText(view.getContext(),"Bad email format",Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                    }
                                });
                    }
                });


            return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ExampleDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface ExampleDialogListener {
        void applyTexts(List<String> email);
    }



}
