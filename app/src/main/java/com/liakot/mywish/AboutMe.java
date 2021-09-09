package com.liakot.mywish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

public class AboutMe extends AppCompatActivity {

    Toolbar toolbar;
    ImageView profilePic;
    TextView firstName, lastName, email, phoneNumber, work;
    Button updateUserProfile;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    String userUniqueId;
    String userFirstName, userLastName, userPhone, userEmail, userWork, userProfilePic, userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me2);

        updateUserProfile = findViewById(R.id.proFileEditBtn);

        //---------for back button----------
        toolbar = findViewById(R.id.toolbarDemo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mAuth = FirebaseAuth.getInstance();
        userUniqueId = mAuth.getUid();
        database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("Database").child(userUniqueId).child("UserProfile");

        firstName = findViewById(R.id.profileFirstName);
        lastName = findViewById(R.id.profileLastName);
        phoneNumber = findViewById(R.id.profileUserPhoneNumber);
        email = findViewById(R.id.profileUserEmail);
        work = findViewById(R.id.profileUserWork);
        profilePic = findViewById(R.id.profileUserPic);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfile profile = snapshot.getValue(UserProfile.class);

                userFirstName = profile.getFirstName();
                userLastName = profile.getLastName();
                userPhone = profile.getPhoneNumber();
                userEmail = profile.getEmail();
                userWork = profile.getWork();
                userProfilePic = profile.getProfilePicRef();
                userPassword = profile.getPassword();

                if(!userProfilePic.isEmpty())
                {
                    Picasso.get().load(userProfilePic).into(profilePic);
                }

                if(userPhone.isEmpty())
                {
                    phoneNumber.setText("Empty");
                }
                else{
                    phoneNumber.setText(userPhone);
                }

                if(userWork.isEmpty())
                {
                    work.setText("Empty");
                }
                else{
                    String workType,workPlace;
                    workType = userWork.substring(0,userWork.indexOf('*'));
                    workPlace = userWork.substring(userWork.indexOf('*')+1, userWork.length());
                    work.setText(workType + " , " + workPlace);
                }
                firstName.setText(userFirstName);
                lastName.setText(userLastName);
                email.setText(userEmail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AboutMe.this,"Something went wrong",Toast.LENGTH_LONG).show();
            }
        });



            profilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!userProfilePic.isEmpty()) {
                        Intent intent = new Intent(AboutMe.this, FullScreenImageActivity.class);

                        intent.putExtra("Image", userProfilePic);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "No image to show",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        updateUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AboutMe.this,UpdateProfile.class);
                intent.putExtra("FirstName",userFirstName);
                intent.putExtra("LastName",userLastName);
                intent.putExtra("PhoneNumber",userPhone);
                intent.putExtra("Email",userEmail);
                intent.putExtra("Work",userWork);
                intent.putExtra("ProfilePic",userProfilePic);
                intent.putExtra("Password",userPassword);

                startActivity(intent);
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
