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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

public class FavouriteItemEditActivity extends AppCompatActivity {

    EditText editFavTopic, editFavName, editFavReason;
    Button editFavUpdate;
    ImageView editFavImageView;
    CheckBox editFavCheckBox;
    String name, topic, reason, date, time, imageUrl, uniqueId,imageName;
    FirebaseDatabase database;
    FirebaseStorage storage;
    FirebaseAuth mAuth;
    String userUniqueId;
    Toolbar toolbar;
    Uri imageUri;
    ProgressDialog dialog;
    final static int PICK_IMAGE = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_item_edit);

        //---------for back button----------
        toolbar = findViewById(R.id.toolbarDemo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //-------get value form previous activity-------------
        name = getIntent().getStringExtra("Name");
        topic = getIntent().getStringExtra("Topic");
        reason = getIntent().getStringExtra("Reason");
        date = getIntent().getStringExtra("Date");
        time = getIntent().getStringExtra("Time");
        imageUrl = getIntent().getStringExtra("Image");
        uniqueId = getIntent().getStringExtra("UniqueID");
        imageName = getIntent().getStringExtra("ImageName");

        editFavTopic = findViewById(R.id.editFavTopic);
        editFavName = findViewById(R.id.editFavName);
        editFavReason = findViewById(R.id.editFavReason);
        editFavImageView = findViewById(R.id.editFavImage);
        editFavUpdate = findViewById(R.id.editFavUpdateBtn);
        editFavCheckBox = findViewById(R.id.editFavCheck);

        dialog = new ProgressDialog(FavouriteItemEditActivity.this);
        dialog.setTitle("Please wait");
        dialog.setMessage("Your favourite is updating..");
        dialog.setCancelable(false);

        imageUri = null;
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userUniqueId = mAuth.getUid();

        editFavTopic.setText(topic);
        editFavName.setText(name);
        editFavReason.setText(reason);
        Picasso.get().load(imageUrl).into(editFavImageView);

        editFavImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //------------- for hide the keyboard------
                InputMethodManager methodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                methodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        editFavUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
//                Toast.makeText(getApplicationContext(), "Under Construction", Toast.LENGTH_SHORT).show();
                String topicSt, nameSt, reasonSt;
                topicSt = editFavTopic.getText().toString();
                nameSt = editFavName.getText().toString();
                reasonSt = editFavReason.getText().toString();

                if (editFavCheckBox.isChecked()) {
                    Date date1 = new Date();
                    date = DateFormat.format("dd/MM/yyyy", date1).toString();
                    time = DateFormat.format("HH:mm:ss a", date1).toString();
                }
                if (topicSt.isEmpty()) {
                    dialog.dismiss();
                    editFavTopic.setError("This field is Empty.");
                    editFavTopic.requestFocus();
                    editFavReason.clearFocus();
                    editFavName.clearFocus();
                } else if (nameSt.isEmpty()) {
                    dialog.dismiss();
                    editFavName.setError("This field is Empty.");
                    editFavReason.clearFocus();
                    editFavName.requestFocus();
                    editFavTopic.clearFocus();
                } else if (reasonSt.isEmpty()) {
                    dialog.dismiss();
                    editFavReason.setError("This field is Empty.");
                    editFavReason.clearFocus();
                    editFavName.clearFocus();
                    editFavReason.requestFocus();
                }
                else {

                    DatabaseReference myRef = database.getReference().child("Database").child(userUniqueId).child("FavouriteList").child(uniqueId);
                    if (imageUri == null) {
//                        Toast.makeText(getApplicationContext(), "No image selected", Toast.LENGTH_SHORT).show();
                        WishItem item = new WishItem(topicSt, reasonSt, date, time, nameSt, imageUrl, uniqueId, imageName);

                        //------------ store value in database--------
                        myRef.setValue(item).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Your favourite is updated", Toast.LENGTH_SHORT).show();
                                editFavReason.setText("");
                                editFavTopic.setText("");
                                editFavName.setText("");
                                Intent intent = new Intent(FavouriteItemEditActivity.this, ActivityFavouriteList.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            } else {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {
                        String imageName1;
                        //---------------generate unique ID------------
                        StorageReference sRef = storage.getReference().child(userUniqueId).child("FavouritePhoto");
//                        imageName1 = System.currentTimeMillis() + "." + getFileExtension(imageUri);  //---------getFileExtension() for access image Extension----
                        StorageReference reference = sRef.child(imageName);

                        reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //---------get image URL---------
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
//                                    Toast.makeText(getContext(),"Image Uploaded Successfully",Toast.LENGTH_SHORT).show();
                                        editFavImageView.setImageBitmap(null);
                                        editFavImageView.setImageResource(R.drawable.image_view);
                                        String imageLink = uri.toString();
                                        WishItem item = new WishItem(topicSt, reasonSt, date, time, nameSt, imageLink, uniqueId, imageName);

                                        //------------ store value in database--------
                                        myRef.setValue(item).addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                dialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Your favourite item is updated", Toast.LENGTH_SHORT).show();
                                                editFavReason.setText("");
                                                editFavTopic.setText("");
                                                editFavName.setText("");
                                                Intent intent = new Intent(FavouriteItemEditActivity.this, ActivityFavouriteList.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);

                                            } else {
                                                dialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                        }).addOnProgressListener(snapshot -> dialog.show())
                                .addOnFailureListener(e -> {
                                    dialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Updating failed", Toast.LENGTH_SHORT).show();
                                });
//                favTopic.add(topic);
//                favName.add(name);
//                favHint.add(hint);
//                favDate.add(date);
//                favTime.add(time);
//
//                favTopicET.setText("");
//                favNameET.setText("");
//                favHintET.setText("");

//                    InputMethodManager methodManager = (InputMethodManager) Activity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    methodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);

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
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
//            Bitmap bitmap = null;
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            editFavImageView.setImageBitmap(bitmap);
            Picasso.get().load(imageUri).into(editFavImageView);
//            Toast.makeText(getApplicationContext(), "Image load", Toast.LENGTH_SHORT).show();
        }
        else{
//            Toast.makeText(getApplicationContext(),"image not load",Toast.LENGTH_SHORT).show();
            imageUri = null;
        }
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
