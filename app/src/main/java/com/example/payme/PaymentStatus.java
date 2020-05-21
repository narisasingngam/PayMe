package com.example.payme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

public class PaymentStatus extends AppCompatActivity {

    TextView signOut;
    private FirebaseAuth mAuth;
    SpaceNavigationView navigationView;
    FirebaseDatabase database;
    private DatabaseReference inputMsg;
    private Button profile;

    private EmailFormat emailFormat = new EmailFormat();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_status);

        navigationView = findViewById(R.id.space);
        navigationView.initWithSaveInstanceState(savedInstanceState);
        navigationView.addSpaceItem(new SpaceItem("Owe Status", R.drawable.ic_monetization_on_black_24dp));
        navigationView.addSpaceItem(new SpaceItem("Lent Status", R.drawable.ic_assignment));
        profile = (Button) findViewById(R.id.profile);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new OweStatusFragment()).commit();

        navigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                startActivity(new Intent(getApplicationContext(),CreateBills.class));
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                Toast.makeText(PaymentStatus.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();

                Fragment selectFragment = null;

                switch (itemName){
                    case "Owe Status":
                        selectFragment = new OweStatusFragment();
                        break;
                    case "Lent Status":
                        selectFragment = new LendStatusFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectFragment).commit();
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                Toast.makeText(PaymentStatus.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }
        });

        mAuth = FirebaseAuth.getInstance();


        signOut = (TextView) findViewById(R.id.signOut);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PaymentStatus.this, MainActivity.class);
                mAuth.signOut();
                finish();
                startActivity(i);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PaymentStatus.this,Profile.class));
            }
        });

    }
}
