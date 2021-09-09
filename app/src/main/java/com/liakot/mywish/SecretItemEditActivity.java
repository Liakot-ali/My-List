package com.liakot.mywish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class SecretItemEditActivity extends AppCompatActivity {
    EditText editTopic, editTitle, editPass;
    Button editUpdateBtn;
    CheckBox checkBox;
    String title, topic, pass, date, time, uniqueId;
    Toolbar toolbar;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    String userUniqueId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret_item_edit);

        //---------for back button----------
        toolbar = findViewById(R.id.toolbarDemo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        title = getIntent().getStringExtra("Title");
        topic = getIntent().getStringExtra("Topic");
        pass = getIntent().getStringExtra("Pass");
        date = getIntent().getStringExtra("Date");
        time = getIntent().getStringExtra("Time");
        uniqueId = getIntent().getStringExtra("UniqueID");

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userUniqueId = mAuth.getUid();

        editTopic = findViewById(R.id.editSecretTopic);
        editTitle = findViewById(R.id.editSecretTitle);
        editPass = findViewById(R.id.editSecretPass);
        checkBox = findViewById(R.id.updateSecretTimeCheck);
        editUpdateBtn = findViewById(R.id.editSecretUpdateBtn);

        progressDialog = new ProgressDialog(SecretItemEditActivity.this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Your secret is updating..");
        progressDialog.setCancelable(false);

        editTopic.setText(topic);
        editTitle.setText(title);
        editPass.setText(pass);

        editUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String secTitle, secTopic, secPass;
                secTitle = editTitle.getText().toString();
                secTopic = editTopic.getText().toString();
                secPass = editPass.getText().toString();

                if(checkBox.isChecked())
                {
                    Date date1 = new Date();
                    date = DateFormat.format("dd/MM/yyyy", date1).toString();
                    time = DateFormat.format("HH:mm:ss a", date1).toString();
                }
                DatabaseReference myRef = database.getReference().child("Database").child(userUniqueId).child("SecretList").child(uniqueId);
                WishItem item = new WishItem(secTitle, secTopic, date, time, secPass, uniqueId);

                myRef.setValue(item).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Your secret is updated.", Toast.LENGTH_SHORT).show();
                        editTopic.setText("");
                        editTitle.setText("");
                        editPass.setText("");
                        checkBox.setChecked(false);

                        Intent intent = new Intent(SecretItemEditActivity.this, ActivitySecretList.class);
                        //------To clear before visited activity------
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Something Went Wrong. Please try again..", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
