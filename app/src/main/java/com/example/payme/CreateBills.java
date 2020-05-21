package com.example.payme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CreateBills extends AppCompatActivity implements RequestDialog.ExampleDialogListener{

    TextView cancleCreate;
    Button addFriendBtn,enterPriceBtn,comfirmPrice;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Friend friend = Friend.getInstance();
    private EditText titleText,priceBill,tax,serviceCharge;
    private String totalText = "";
    private double taxD = 0;
    private  double serviceD = 0;
    private  double priceD = 0;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    private DatabaseReference inputMsg;

    private EmailFormat emailFormat = new EmailFormat();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_bills);

        cancleCreate = (TextView) findViewById(R.id.cancleCreate);
        addFriendBtn = (Button)findViewById(R.id.addFriendBtn);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        titleText = (EditText) findViewById(R.id.titleText);
        priceBill = (EditText) findViewById(R.id.priceBill);
        tax = (EditText) findViewById(R.id.tax);
        serviceCharge = (EditText) findViewById(R.id.serviceCharge);
        enterPriceBtn = (Button) findViewById(R.id.enterPriceBtn);
        comfirmPrice = (Button) findViewById(R.id.comfirmPrice);

        cancleCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),PaymentStatus.class));
                friend.emptyList();
            }
        });

        addFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        enterPriceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                friend.emptyList();
                startActivity(new Intent(getApplicationContext(),PaymentStatus.class));
            }
        });

        comfirmPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!priceBill.getText().toString().isEmpty()){
                totalPrice(priceBill.getText().toString(),tax.getText().toString(),serviceCharge.getText().toString());
                }else{
                    Toast.makeText(CreateBills.this, "Please fill the price",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });



    }

    public void saveData(){

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
//        inputMsg = database.getReference(emailFormat.encodeUserEmail(mAuth.getCurrentUser().getEmail()));



        for (String email: friend.getListEmail()) {

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(emailFormat.encodeUserEmail(mAuth.getCurrentUser().getEmail())).child("lend").push();

            ref.child("price").setValue(totalText);
            ref.child("title").setValue(titleText.getText().toString());
            ref.child("Date").setValue(date());
            ref.child("Members").setValue(friend.getlistFriend());
            ref.child("oweName").setValue(email);

            String lentKey = ref.getKey();

            DatabaseReference oweRef = FirebaseDatabase.getInstance().getReference(emailFormat.encodeUserEmail(email)).child("owe").child(lentKey);
            oweRef.child("price").setValue(totalText);
            oweRef.child("title").setValue(titleText.getText().toString());
            oweRef.child("Date").setValue(date());
            oweRef.child("Members").setValue(friend.getlistFriend());
            oweRef.child("lendName").setValue(mAuth.getCurrentUser().getEmail());
        }

    }

    public String date(){
        DateFormat df = new SimpleDateFormat("dd/MM/yy");
        Date dateobj = new Date();
//        System.out.println(df.format(dateobj));
        return df.format(dateobj);
    }

    public void totalPrice(String price, String tax, String service){

        priceD = Double.valueOf(price);
        taxD = Double.valueOf(tax);
        serviceD = Double.valueOf(service);
        double total = priceD*(taxD+serviceD+100)/100.0;
        double splitFriend = total/friend.getlistFriend();
        totalText = String.format("%.2f",splitFriend);
        Log.d("Mint",totalText);
        enterPriceBtn.setText("Pay "+totalText);


    }

    public void openDialog(){
            RequestDialog requestDialog = new RequestDialog();
            requestDialog.show(getSupportFragmentManager(),"dialog");
    }

    @Override
    public void applyTexts(List<String> email) {
        mAdapter = new FriendAdapter(email);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

}
