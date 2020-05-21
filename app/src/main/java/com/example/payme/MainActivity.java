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

public class MainActivity extends AppCompatActivity {

    TextView goToSignUp;
    Button signIn;
    EditText userEmail, userPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        goToSignUp = (TextView)findViewById(R.id.goToSignUp);
        signIn = (Button)findViewById(R.id.signIn);
        userEmail = (EditText)findViewById(R.id.userEmail);
        userPassword = (EditText)findViewById(R.id.userPassword);

        //        check if the user is login
        if(mAuth.getCurrentUser() != null){
//            if user not login
            finish();
            startActivity(new Intent(getApplicationContext(),PaymentStatus.class));

        }

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getEmail = userEmail.getText().toString();
                String getPassword = userPassword.getText().toString();
                callsignIn(getEmail,getPassword);
            }
        });

        goToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SignUp.class));
            }
        });
    }


    private void callsignIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("Test","Sign in is successful" + task.isSuccessful());
                if(!task.isSuccessful()){
                    Log.d("Test","Sign in with email failed", task.getException());
                    Toast.makeText(MainActivity.this,"Login Faild", Toast.LENGTH_SHORT).show();
                }else{
                    Intent i = new Intent(MainActivity.this, PaymentStatus.class);
                    finish();
                    startActivity(i);
                }
            }
        });

    }

}
