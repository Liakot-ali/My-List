package com.liakot.mywish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SecretListItemViewActivity extends AppCompatActivity {
    TextView secTitle,secTopic,secPass,secDate,secTime;
    Button editSecretBtn;
    String title, topic, pass, date, time, uniqueId;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret_list_item_view);


        //---------for back button----------
        toolbar = findViewById(R.id.toolbarDemo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        secTitle = findViewById(R.id.secretTitle);
        secTopic = findViewById(R.id.secretTopic);
        secPass = findViewById(R.id.secretPass);
        secDate = findViewById(R.id.secretDate);
        secTime = findViewById(R.id.secretTime);
        editSecretBtn = findViewById(R.id.editSecretBtn);

        title = getIntent().getStringExtra("Title");
        topic = getIntent().getStringExtra("Topic");
        pass = getIntent().getStringExtra("Pass");
        date = getIntent().getStringExtra("Date");
        time = getIntent().getStringExtra("Time");
        uniqueId = getIntent().getStringExtra("UniqueID");

        secTitle.setText(title);
        secTopic.setText(topic);
        secPass.setText(pass);
        secDate.setText(date);
        secTime.setText(time);

        editSecretBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),"Under Construction",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SecretListItemViewActivity.this, SecretItemEditActivity.class);
                intent.putExtra("Title",title);
                intent.putExtra("Topic",topic);
                intent.putExtra("Pass",pass);
                intent.putExtra("Date",date);
                intent.putExtra("Time",time);
                intent.putExtra("UniqueID",uniqueId);
                startActivity(intent);
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.fragment_add_wish, new FragmentAddMyWish()).commit();
            }
        });






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
