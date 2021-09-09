package com.liakot.mywish;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ColorSpace;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class FragmentMyFavourite extends Fragment {

    private EditText favTopicET, favNameET, favHintET;
    private Button addImageBtn, addThisFavBtn, gotoFavListBtn;
    private ImageView imageView1;
    TextView favUserFirstName;
    private View view;
    FirebaseDatabase database;
    FirebaseStorage storage;
    FirebaseAuth mAuth;
    ProgressDialog dialog;
    Uri imageUri;
    String imageName, userUniqueId;
    private final int PICK_IMAGE = 10;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_my_favourite, container, false);

        //---------------declare the arrayList---------
//        favTopic = new ArrayList<String>();
//        favDate = new ArrayList<String>();
//        favTime = new ArrayList<String>();
//        favName = new ArrayList<String>();
//        favHint = new ArrayList<String>();

        favTopicET = view.findViewById(R.id.favouriteTopicET);
        favNameET = view.findViewById(R.id.favouriteNameET);
        favHintET = view.findViewById(R.id.favouriteHintET);

        addThisFavBtn = view.findViewById(R.id.addThisFavouriteBtn);
        gotoFavListBtn = view.findViewById(R.id.gotoFavListBtn);
        imageView1 = view.findViewById(R.id.favouriteImage);
        favUserFirstName = view.findViewById(R.id.favUserFirstName);

        mAuth = FirebaseAuth.getInstance();
        userUniqueId = mAuth.getUid();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        DatabaseReference ref = database.getReference().child("Database").child(userUniqueId).child("UserProfile");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfile profile = snapshot.getValue(UserProfile.class);
                favUserFirstName.setText("Hey " + profile.getFirstName() + "..!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //------------create new dialog window---------
        dialog = new ProgressDialog(getContext());
        dialog.setTitle("Please Wait");
        dialog.setMessage("We are working on your favourite item..");
        dialog.setCancelable(false);

        //-----------for open gallery--------------
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //------------- for hide the keyboard------
                InputMethodManager methodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                methodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);

//                Toast.makeText(getContext(),"Image View Clicked",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                }
            }
        });

        //-----------add wish in Firebase realtime Database----------
        addThisFavBtn.setOnClickListener(v -> {
            dialog.show();
            String topic, date, name, time, hint;
            Date date1 = new Date();
            topic = favTopicET.getText().toString();
            name = favNameET.getText().toString();
            hint = favHintET.getText().toString();
            date = DateFormat.format("dd/MM/yyyy", date1).toString();
            time = DateFormat.format("HH:mm:ss a", date1).toString();

            if (topic.isEmpty()) {
                dialog.dismiss();
                favTopicET.setError("This field is Empty.");
                favHintET.requestFocus();
                favNameET.clearFocus();
                favHintET.clearFocus();
            } else if (name.isEmpty()) {
                dialog.dismiss();
                favNameET.setError("This field is Empty.");
                favHintET.clearFocus();
                favNameET.requestFocus();
                favHintET.clearFocus();
            } else if (hint.isEmpty()) {
                dialog.dismiss();
                favHintET.setError("This field is Empty.");
                favHintET.clearFocus();
                favNameET.clearFocus();
                favHintET.requestFocus();
            } else if (imageUri == null) {
                dialog.dismiss();
                Toast.makeText(getContext(), "Please Select an Image", Toast.LENGTH_SHORT).show();
            }
            else {
                String uniqueID;
                uniqueID = UUID.randomUUID().toString(); //---------------generate unique ID------------
                DatabaseReference myRef = database.getReference().child("Database").child(userUniqueId).child("FavouriteList").child(uniqueID);
                StorageReference sRef = storage.getReference().child(userUniqueId).child("FavouritePhoto");
                imageName = System.currentTimeMillis() + "." + getFileExtension(imageUri); //---------getFileExtension() for access image Extension----
                StorageReference reference = sRef.child(imageName);

                reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //---------get image URL---------
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
//                                    Toast.makeText(getContext(),"Image Uploaded Successfully",Toast.LENGTH_SHORT).show();
                                imageView1.setImageBitmap(null);
                                imageView1.setImageResource(R.drawable.image_view);
                                String imageLink = uri.toString();
                                WishItem item = new WishItem(topic, hint, date, time, name, imageLink, uniqueID, imageName);

                                //------------ store value in database--------
                                myRef.setValue(item).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        dialog.dismiss();
                                        Toast.makeText(getContext(), "New favourite item added", Toast.LENGTH_SHORT).show();
                                        favHintET.setText("");
                                        favTopicET.setText("");
                                        favNameET.setText("");
                                    } else {
                                        dialog.dismiss();
                                        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }).addOnProgressListener(snapshot -> dialog.show())
                        .addOnFailureListener(e -> {
                            dialog.dismiss();
                            Toast.makeText(getContext(), "Uploading Failed", Toast.LENGTH_SHORT).show();
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

                //------------- for hide the keyboard------
                InputMethodManager methodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                methodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);

            }
        });

        gotoFavListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityFavouriteList.class);

                //---------send the data to ActivityMyFavourite---------
//                intent.putExtra("topic",favTopic);
//                intent.putExtra("name",favName);
//                intent.putExtra("hint",favHint);
//                intent.putExtra("date",favDate);
//                intent.putExtra("time",favTime);
                startActivity(intent);
            }
        });
        return view;
    }

    //----------for access Image Extension-----------
    private String getFileExtension(Uri imageUri) {
        ContentResolver resolver = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(resolver.getType(imageUri));
    }

    //-------------show image in imageView----------
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView1.setImageBitmap(bitmap);
        }
    }
}
