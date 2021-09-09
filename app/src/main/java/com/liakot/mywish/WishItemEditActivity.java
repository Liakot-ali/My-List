package com.liakot.mywish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class WishItemEditActivity extends AppCompatActivity {
    TextView editWishTitle,editWish;
    Button updateWish;
    String wishString, wishTitle, wishLocation, wishDate, wishTime;
    String userUniqueId;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    Toolbar toolbar;
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_item_edit);

        //---------for back button----------
        toolbar = findViewById(R.id.toolbarDemo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        wishTitle = getIntent().getStringExtra("Title");
        wishString = getIntent().getStringExtra("Wish");
        wishDate = getIntent().getStringExtra("Date");
        wishTime = getIntent().getStringExtra("Time");
        wishLocation = getIntent().getStringExtra("UniqueID");

        mAuth = FirebaseAuth.getInstance();
        userUniqueId = mAuth.getUid();
        database = FirebaseDatabase.getInstance();

        editWish = findViewById(R.id.editWish);
        editWishTitle = findViewById(R.id.editWishTitle);
        updateWish = findViewById(R.id.updateWish);
        checkBox = findViewById(R.id.updateWishTimeCheck);

        editWish.setText(wishString);
        editWishTitle.setText(wishTitle);

        progressDialog  =  new ProgressDialog(WishItemEditActivity.this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Your wish is updating..");
        progressDialog.setCancelable(false);

        updateWish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String wishTittle, wish;
                wishTittle = editWishTitle.getText().toString();
                wish = editWish.getText().toString();
                if(checkBox.isChecked())
                {
                    Date date = new Date();
                    wishDate = DateFormat.format("dd/MM/yyyy", date).toString();
                    wishTime = DateFormat.format("HH:mm:ss a", date).toString();
                }
                DatabaseReference myRef = database.getReference().child("Database").child(userUniqueId).child("WishList").child(wishLocation);
                WishItem item = new WishItem(wishTittle, wish, wishDate, wishTime, wishLocation);
                myRef.setValue(item).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Your wish is updated", Toast.LENGTH_SHORT).show();
                        editWish.setText("");
                        editWishTitle.setText("");
                        checkBox.setChecked(false);
                        Intent intent = new Intent(WishItemEditActivity.this, ActivityAddWish.class);

                        //------To clear before visited activity------
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Something Went Wrong. Please try again..", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
