package com.liakot.mywish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


public class FavouriteListItemVIewActivity extends AppCompatActivity {

    TextView favName, favTopic, favReason, favDate, favTime;
    ImageView favImage;
    Button editFavBtn;
    String name, topic, reason, date, time, imageUrl, uniqueId, imageName;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_list_item_v_iew);

        //---------for back button----------
        toolbar = findViewById(R.id.toolbarDemo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        favName = findViewById(R.id.favName);
        favTopic = findViewById(R.id.favTopic);
        favReason = findViewById(R.id.favReason);
        favTime = findViewById(R.id.favTime);
        favDate = findViewById(R.id.favDate);

        favImage = findViewById(R.id.favImage);
        editFavBtn = findViewById(R.id.editFavBtn);

        //-------get value form previous activity-------------
        name = getIntent().getStringExtra("Name");
        topic = getIntent().getStringExtra("Topic");
        reason = getIntent().getStringExtra("Reason");
        date = getIntent().getStringExtra("Date");
        time = getIntent().getStringExtra("Time");
        imageUrl = getIntent().getStringExtra("Image");
        uniqueId = getIntent().getStringExtra("UniqueID");
        imageName = getIntent().getStringExtra("ImageName");
        //---------- Set image in ImageVIew form image URL------
        Picasso
                .get()
                .load(imageUrl)
                .into(favImage);

        favName.setText(name);
        favTopic.setText(topic);
        favReason.setText(reason);
        favDate.setText(date);
        favTime.setText(time);

        favImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FavouriteListItemVIewActivity.this, FullScreenImageActivity.class);
                intent.putExtra("Image", imageUrl);
                intent.putExtra("ImageName",imageName);

                startActivity(intent);
            }
        });

        editFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Under Construction", Toast.LENGTH_SHORT).show();
                Intent intent =  new Intent(FavouriteListItemVIewActivity.this,FavouriteItemEditActivity.class);
                intent.putExtra("Name",name);
                intent.putExtra("Topic",topic);
                intent.putExtra("Reason",reason);
                intent.putExtra("Date",date);
                intent.putExtra("Time",time);
                intent.putExtra("Image",imageUrl);
                intent.putExtra("UniqueID",uniqueId);
                intent.putExtra("ImageName",imageName);
                startActivity(intent);
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
