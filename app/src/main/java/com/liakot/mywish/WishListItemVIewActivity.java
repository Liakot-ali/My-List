package com.liakot.mywish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WishListItemVIewActivity extends AppCompatActivity {

    TextView wishTitle, wishText, wishDate, wishTime;
    Button editWishBtn;
    String title, wish, date, time, uniqueID;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list_item_view);

        //---------for back button----------
        toolbar = findViewById(R.id.toolbarDemo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        wishTitle = findViewById(R.id.wishTitle);
        wishText = findViewById(R.id.wishText);
        wishDate = findViewById(R.id.wishDate);
        wishTime = findViewById(R.id.wishTime);
        editWishBtn = findViewById(R.id.editWishBtn);

        title = getIntent().getStringExtra("Title");
        wish = getIntent().getStringExtra("Wish");
        date = getIntent().getStringExtra("Date");
        time = getIntent().getStringExtra("Time");
        uniqueID = getIntent().getStringExtra("UniqueID");

        wishTitle.setText(title);
        wishText.setText(wish);
        wishDate.setText(date);
        wishTime.setText(time);

        editWishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(WishListItemVIewActivity.this, WishItemEditActivity.class);
                //--------pass necessary value------------
                intent.putExtra("Title",title);
                intent.putExtra("Wish",wish);
                intent.putExtra("Date",date);
                intent.putExtra("Time",time);
                intent.putExtra("UniqueID",uniqueID);
                startActivity(intent);
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.fragment_add_wish, new FragmentAddMyWish()).commit();
            }
        });
    }

    //---------for back to previous activity-------------
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
