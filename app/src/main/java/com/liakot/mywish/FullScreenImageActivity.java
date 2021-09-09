package com.liakot.mywish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

public class FullScreenImageActivity extends AppCompatActivity {
    ImageView fullScreenImageView;
    ImageButton closeFullScreenBtn;
    PhotoView photoView;
    String imageUrl, imageName;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        database = FirebaseDatabase.getInstance();

        imageUrl = getIntent().getStringExtra("Image");
        imageName = getIntent().getStringExtra("ImageName");

        photoView = findViewById(R.id.fullScreenImageView);
        closeFullScreenBtn = findViewById(R.id.closeFullScreenBtn);

        //------load image in imageVIew from image URL------
        Picasso.get().load(imageUrl).into(photoView);

//        photoView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//
//                StorageReference reference = FirebaseStorage.getInstance().getReference().child("FavouritePhoto").child(imageName);
//
////                File rootPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), imageName);
////                if(!rootPath.exists())
////                {
////                    rootPath.mkdirs();
////                }
//
//                File localFile = null;
//                try {
//                    localFile =File.createTempFile("images","jpg");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                final long ONE_MEGA = 1024 * 1024;
//                assert localFile != null;
//                reference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                        Toast.makeText(FullScreenImageActivity.this,"Downloading Image",Toast.LENGTH_SHORT).show();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(FullScreenImageActivity.this,"Downloading error",Toast.LENGTH_SHORT).show();
//                    }
//                });
//                return true;
//            }
//        });

        closeFullScreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
