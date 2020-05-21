package com.example.payme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUp extends AppCompatActivity {

    FirebaseAuth mAuth;
    TextView goToSignIn;
    Button signUpBtn;
    EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        goToSignIn = (TextView) findViewById(R.id.goToSignIn);
        signUpBtn = (Button) findViewById(R.id.signUpBtn);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(getApplicationContext(), PaymentStatus.class));
        }

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getEmail = email.getText().toString();
                String getPassword = password.getText().toString();
                callSignUp(getEmail, getPassword);
            }
        });

        goToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    private void callSignUp(final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    userProfile();
                    Toast.makeText(SignUp.this, "Account create",
                            Toast.LENGTH_SHORT).show();
                    Log.d("Test", "Account Created");
                    callsignIn(email,password);

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(SignUp.this, "Sign up fail" + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void userProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(email.getText().toString()).build();
            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("Test", "User profile update");
                    }
                }
            });
        }

    }

    private void callsignIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("Test","Sign in is successful" + task.isSuccessful());
                if(!task.isSuccessful()){
                    Log.d("Test","Sign in with email failed", task.getException());
                    Toast.makeText(SignUp.this,"Login Faild", Toast.LENGTH_SHORT).show();
                }else{
                    Intent i = new Intent(SignUp.this, PaymentStatus.class);
                    finish();
                    startActivity(i);
                }
            }
        });

    }
}
