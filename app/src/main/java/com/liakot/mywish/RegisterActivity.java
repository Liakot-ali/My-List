package com.liakot.mywish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;

public class RegisterActivity extends AppCompatActivity {
    EditText registerFirstName, registerLastName, registerEmail, registerPassword, registerConfirmPassword;
    TextView gotoSignIn;
    Button registerBtn;
    String firstName, lastName, userEmail, userPassword, userConfirmPassword;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    ProgressDialog dialog;
    final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        dialog = new ProgressDialog(RegisterActivity.this);
        dialog.setTitle("Please wait");
        dialog.setMessage("Your data is processing..");
        dialog.setCancelable(false);

        registerFirstName = findViewById(R.id.userFirstName);
        registerLastName = findViewById(R.id.userLastName);
        registerEmail = findViewById(R.id.registerUserEmail);
        registerPassword = findViewById(R.id.registerPassword);
        registerConfirmPassword = findViewById(R.id.registerConfirmPassword);
        gotoSignIn = findViewById(R.id.gotoSignIn);
        registerBtn = findViewById(R.id.registerBtn);


        gotoSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //------------- for hide the keyboard------
                InputMethodManager methodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                methodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                Intent intent =  new Intent(RegisterActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference userRef = database.getReference().child("User");
                DatabaseReference databaseRef = database.getReference().child("Database");

                //---------- get the value from editText--------
                firstName = registerFirstName.getText().toString();
                lastName = registerLastName.getText().toString();
                userEmail = registerEmail.getText().toString().trim();
                userPassword = registerPassword.getText().toString().trim();
                userConfirmPassword = registerConfirmPassword.getText().toString().trim();

                if(firstName.isEmpty())
                {
                    registerFirstName.setError("Enter your first name");
                    registerFirstName.requestFocus();
                    registerLastName.clearFocus();
                    registerEmail.clearFocus();
                    registerPassword.clearFocus();
                    registerConfirmPassword.clearFocus();
                }
                else if(userEmail.isEmpty())
                {
                    registerEmail.setError("Enter your email");
                    registerFirstName.clearFocus();
                    registerLastName.clearFocus();
                    registerEmail.requestFocus();
                    registerPassword.clearFocus();
                    registerConfirmPassword.clearFocus();
                }
                else if(!userEmail.matches(emailPattern))
                {
                    registerEmail.setError("Enter a valid email");
                    registerFirstName.clearFocus();
                    registerLastName.clearFocus();
                    registerEmail.requestFocus();
                    registerPassword.clearFocus();
                    registerConfirmPassword.clearFocus();
                }
                else if(userPassword.isEmpty())
                {
                    registerPassword.setError("Create a password");
                    registerFirstName.clearFocus();
                    registerLastName.clearFocus();
                    registerEmail.clearFocus();
                    registerPassword.requestFocus();
                    registerConfirmPassword.clearFocus();
                }
                else if(userPassword.length() < 6)
                {
                    registerPassword.setError("Password must be more than 6 characters");
                    registerFirstName.clearFocus();
                    registerLastName.clearFocus();
                    registerEmail.clearFocus();
                    registerPassword.requestFocus();
                    registerConfirmPassword.clearFocus();
                }
                else if(!userConfirmPassword.matches(userPassword))
                {
                    registerConfirmPassword.setError("Password is not matched");
                    registerFirstName.clearFocus();
                    registerLastName.clearFocus();
                    registerEmail.clearFocus();
                    registerPassword.clearFocus();
                    registerConfirmPassword.requestFocus();
                }
                else{
                    //------------- for hide the keyboard------
                    InputMethodManager methodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    methodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    dialog.show();
                    userRef.child("UserEmail").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean ind = true;
                            for (DataSnapshot data : snapshot.getChildren()) {
                                //TODO
                                UserProfile temp = data.getValue(UserProfile.class);
                                if (temp.getEmail().equals(userEmail)) {
                                    ind = false;
                                    break;
                                }
                            }
                            if(!ind) {
                                dialog.dismiss();
                                registerEmail.setError("This email is already register");
                                registerFirstName.clearFocus();
                                registerLastName.clearFocus();
                                registerEmail.requestFocus();
                                registerPassword.clearFocus();
                                registerConfirmPassword.clearFocus();
                            }
                            else{
                                mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            //------------ createUserWith method------
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        //--------- get user ID from fireBaseAuth-----------
                                        String uniqueUserId = mAuth.getUid();
                                        DatabaseReference uniqueUserRef = databaseRef.child(uniqueUserId);

                                        //------------- put userEmail and Password in 'User' database section---
                                        UserProfile userEmailAndPassword = new UserProfile(userEmail,userPassword);
                                        userRef.child("UserEmail").child(uniqueUserId).setValue(userEmailAndPassword);

                                        //----------- put all value to UserProfile database---------
                                        UserProfile userProfile = new UserProfile(firstName, lastName,userEmail, userPassword,"","","");
                                        uniqueUserRef.child("UserProfile").setValue(userProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {
                                                    // ----------- to skip login activity if user is logged in-----------
                                                    SharedPreferences preferences = getSharedPreferences(LoginActivity.LOGIN_PRE, Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = preferences.edit();
                                                    editor.putBoolean("hasLoggedIn", true);
                                                    editor.putString("userEmail", userEmail);
                                                    editor.putString("userPassword", userPassword);
                                                    editor.apply();

                                                    dialog.dismiss();
                                                    //----- goto MainActivity------
                                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
//                                                    intent.putExtra("Email", userEmail);
//                                                    intent.putExtra("Password", userPassword);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    Toast.makeText(getApplicationContext(), "Welcome to My List",Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                                else {
                                                    dialog.dismiss();
                                                    Toast.makeText(getApplicationContext(),"Registration unsuccessful",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    //------------ createUserWith method------
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.dismiss();
                                        Toast.makeText(getApplicationContext(),"Something went wrong. Try again",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                        //---------- addListenerForSingleValueEvent method----
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Check your internet connection",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
