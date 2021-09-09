package com.liakot.mywish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreenActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.splashProgressBar);
        progressBar.setVisibility(View.VISIBLE);
         new Handler().postDelayed(new Runnable() {
             @Override
             public void run() {
                 SharedPreferences preferences = getSharedPreferences(LoginActivity.LOGIN_PRE, Context.MODE_PRIVATE);
                 boolean hasLoggedIn = preferences.getBoolean("hasLoggedIn", false);

                 if(hasLoggedIn) {
                     //---------- get value from shared preferences-----------
                     String email = preferences.getString("userEmail", "example123@emmail.com");
                     String password = preferences.getString("userPassword", "ThisIsExample");

                     mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                         @Override
                         public void onComplete(@NonNull Task<AuthResult> task) {
                             if (task.isSuccessful())
                             {
                                 progressBar.setVisibility(View.GONE);
                                 Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
//                                 intent.putExtra("Email",email);
//                                 intent.putExtra("Password",password);
                                 startActivity(intent);
                                 finish();
                             }
                             else{
                                 progressBar.setVisibility(View.GONE);
                                 Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                                 startActivity(intent);
                                 finish();
                             }
                         }
                     });
                 }
                 else{
                     progressBar.setVisibility(View.GONE);
                     Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                     startActivity(intent);
                     finish();
                 }
             }
         }, 500);
    }

    //------- double back press to exit------
    private long onBackPressedTime;
    @Override
    public void onBackPressed() {
        //---------- double back press to exit
        if(onBackPressedTime + 1000 > System.currentTimeMillis())
        {
            super.onBackPressed();
            return;
        }
        onBackPressedTime = System.currentTimeMillis();
    }
}

