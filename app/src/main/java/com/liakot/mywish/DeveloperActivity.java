package com.liakot.mywish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeveloperActivity extends AppCompatActivity {
    Toolbar toolbar;
    EditText userComment;
    Button commentBtn;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    ImageView developerImage;
    String userUniqueId, comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);

        //---------for back button----------
        toolbar = findViewById(R.id.toolbarDemo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userUniqueId = mAuth.getUid();

        userComment = findViewById(R.id.userComment);
        commentBtn = findViewById(R.id.commentBtn);
        developerImage = findViewById(R.id.developerImage);

        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                comment = userComment.getText().toString();
                if(!comment.isEmpty()) {
                    DatabaseReference myRef = database.getReference().child("User").child("UserComment").child(userUniqueId);
                    myRef.setValue(comment);
                    Toast.makeText(getApplicationContext(),"Thanks for your feedback",Toast.LENGTH_SHORT).show();
                    userComment.setText("");
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please write something",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //---------for back to home-------------
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
