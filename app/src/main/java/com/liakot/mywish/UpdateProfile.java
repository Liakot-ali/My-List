package com.liakot.mywish;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UpdateProfile extends AppCompatActivity {

    Toolbar toolbar;
    EditText updateLastName,updatePhoneNumber,updateFirstName,updateWorkType,updateWorkPlace;
    TextView updateEmail;
    Button updateProfileBtn,updateEditPhotoBtn;
    ImageView updateProfilePicture;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    String userUniqueId;
    String userFirstName, userLastName, userPhone, userEmail, userWork, userProfilePicUrl,userPassword;
    Uri imageUri;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        //---------for back button----------
        toolbar = findViewById(R.id.toolbarDemo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mAuth = FirebaseAuth.getInstance();
        userUniqueId = mAuth.getUid();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        userFirstName = getIntent().getStringExtra("FirstName");
        userLastName = getIntent().getStringExtra("LastName");
        userEmail = getIntent().getStringExtra("Email");
        userPhone = getIntent().getStringExtra("PhoneNumber");
        userWork = getIntent().getStringExtra("Work");
        userProfilePicUrl = getIntent().getStringExtra("ProfilePic");
        userPassword = getIntent().getStringExtra("Password");

        dialog = new ProgressDialog(UpdateProfile.this);
        dialog.setTitle("Please wait");
        dialog.setMessage("Your profile is updating..");
        dialog.setCancelable(false);

        updateProfileBtn = findViewById(R.id.updateUpdateBtn);
        updateEditPhotoBtn = findViewById(R.id.updateEditPhotoBtn);
        updateProfilePicture = findViewById(R.id.updateProfilePicture);

        updateFirstName = findViewById(R.id.updateFirstName);
        updateLastName = findViewById(R.id.updateLastName);
        updateEmail = findViewById(R.id.updateUserEmail);
        updatePhoneNumber = findViewById(R.id.updateUserPhone);
        updateWorkType = findViewById(R.id.updateWorkType);
        updateWorkPlace = findViewById(R.id.updateWorkPlace);

        updateFirstName.setText(userFirstName);
        updateLastName.setText(userLastName);
        updateEmail.setText("Email: "+ userEmail);
        updatePhoneNumber.setText(userPhone);

        String workType = null, workPlace = null;
        if(!userWork.isEmpty())
        {
            workType = userWork.substring(0,userWork.indexOf('*'));
            workPlace = userWork.substring(userWork.indexOf('*')+1, userWork.length());
        }
        if(!userProfilePicUrl.isEmpty())
        {
            Picasso.get().load(userProfilePicUrl).into(updateProfilePicture);
        }
        updateWorkType.setText(workType);
        updateWorkPlace.setText(workPlace);

        updateEditPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //------------- for hide the keyboard------
                InputMethodManager methodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                methodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                //---------for open gallery----------
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
            }
        });

            updateProfilePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imageUri != null || !userProfilePicUrl.isEmpty()) {
                        //------------- for hide the keyboard------
                        InputMethodManager methodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        methodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                        Intent intent = new Intent(UpdateProfile.this, FullScreenImageActivity.class);
                        if (imageUri != null) {
                            userProfilePicUrl = imageUri.toString();
                        }
                        intent.putExtra("Image", userProfilePicUrl);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"No image to show",Toast.LENGTH_LONG).show();
                    }
                }
            });

        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName, lastName, phoneNumber, imageUrl, workType, workPlace;
                firstName = updateFirstName.getText().toString();
                lastName = updateLastName.getText().toString();
                phoneNumber = updatePhoneNumber.getText().toString();
                workType = updateWorkType.getText().toString();
                workPlace = updateWorkPlace.getText().toString();

                if(firstName.isEmpty())
                {
                    updateFirstName.setError("Must have first name");
                    updateFirstName.requestFocus();
                    updateLastName.clearFocus();
                    updatePhoneNumber.clearFocus();
                    updateWorkPlace.clearFocus();
                    updateWorkType.clearFocus();
                }
                else if(!phoneNumber.isEmpty() && phoneNumber.length()!=11)
                {
                    updatePhoneNumber.setError("Number must be 11 digits");
                    updateFirstName.clearFocus();
                    updateLastName.clearFocus();
                    updatePhoneNumber.requestFocus();
                    updateWorkPlace.clearFocus();
                    updateWorkType.clearFocus();
                }
                else {
                    dialog.show();
                    DatabaseReference myRef = database.getReference().child("Database").child(userUniqueId).child("UserProfile");

                    String work;
                    if(!workType.isEmpty() || !workPlace.isEmpty()) {
                        work = workType + "*" + workPlace;
                    }
                    else{
                        work = "";
                    }
                    if (imageUri == null) {
                        UserProfile profile = new UserProfile(firstName, lastName, userEmail, userPassword, phoneNumber, userProfilePicUrl, work);

                        myRef.setValue(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(UpdateProfile.this, AboutMe.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(getApplicationContext(), "Profile is updated", Toast.LENGTH_LONG).show();
                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    else{
                        dialog.show();
                        String imageName = userUniqueId + getFileExtension(imageUri);
                        StorageReference sRef = storage.getReference().child(userUniqueId).child("ProfilePhoto").child(imageName);

                        sRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        UserProfile profile = new UserProfile(firstName, lastName, userEmail, userPassword, phoneNumber, uri.toString(), work);
                                        myRef.setValue(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    dialog.dismiss();
                                                    Intent intent = new Intent(UpdateProfile.this, AboutMe.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                    Toast.makeText(getApplicationContext(),"Profile is updated",Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    dialog.dismiss();
                                                    Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                dialog.show();
                            }
                        });
                    }
                }
            }
        });
    }

    //----------for access Image Extension-----------
    private String getFileExtension(Uri imageUri) {
        ContentResolver resolver = getApplicationContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(resolver.getType(imageUri));
    }


    //-------------show image in imageView----------
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(updateProfilePicture);
        }
        else{
            imageUri = null;
        }
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
