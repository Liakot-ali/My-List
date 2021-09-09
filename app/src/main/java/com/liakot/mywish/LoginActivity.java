package com.liakot.mywish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    public static String LOGIN_PRE = "LogInInformation";

    EditText logInEmail, logInPassword;
    TextView gotoRegister;
    Button logInBtn;
    String userEmail, userPassword;
    ProgressDialog dialog;
    FirebaseAuth mAuth;
    final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        dialog = new ProgressDialog(LoginActivity.this);
        dialog.setTitle("Please wait");
        dialog.setMessage("Your data is processing..");
        dialog.setCancelable(false);

        logInEmail = findViewById(R.id.logInUserEmail);
        logInPassword = findViewById(R.id.logInPassword);
        gotoRegister = findViewById(R.id.gotoRegister);
        logInBtn = findViewById(R.id.logInBtn);

        gotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //------------- for hide the keyboard------
                InputMethodManager methodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                methodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                Intent intent =  new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userEmail = logInEmail.getText().toString().trim();
                userPassword = logInPassword.getText().toString().trim();
                if (userEmail.isEmpty()) {
                    logInEmail.setError("Input your email");
                    logInEmail.requestFocus();
                    logInPassword.clearFocus();
                } else if (!userEmail.matches(emailPattern)) {
                    logInEmail.setError("Invalid email");
                    logInEmail.requestFocus();
                    logInPassword.clearFocus();
                } else if (userPassword.isEmpty()) {
                    logInPassword.setError("Input your password");
                    logInPassword.requestFocus();
                    logInEmail.clearFocus();
                } else if (userPassword.length() < 6) {
                    logInPassword.setError("Length must be 6 or more characters");
                    logInPassword.requestFocus();
                    logInEmail.clearFocus();
                } else {

                    //------------- for hide the keyboard------
                    InputMethodManager methodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    methodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    dialog.show();
                    mAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                // ----------- to skip login activity if user is logged in-----------
                                SharedPreferences preferences = getSharedPreferences(LoginActivity.LOGIN_PRE, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();

                                //--------- put value to shared preferences----------
                                editor.putBoolean("hasLoggedIn", true);
                                editor.putString("userEmail", userEmail);
                                editor.putString("userPassword", userPassword);
                                editor.apply();

                                logInEmail.setText("");
                                logInPassword.setText("");
                                dialog.dismiss();

                                //------- goto MainActivity if userEmail and Password in correct----
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                intent.putExtra("Email", userEmail);
//                                intent.putExtra("Password", userPassword);
                                startActivity(intent);
                                finish();
                                Toast.makeText(getApplicationContext(),"Login successful",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }
            }
        });
    }
}
