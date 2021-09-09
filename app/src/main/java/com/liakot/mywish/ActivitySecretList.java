package com.liakot.mywish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class ActivitySecretList extends AppCompatActivity {

    Toolbar toolbar;
    ListView mySecretList;

    ArrayList<WishItem> arrayList;
    ArrayList<String> topic, tittle, password, date, time;
    BaseAdapter adapter;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    ProgressDialog dialog;
    ProgressBar progressBar;
    LinearLayout progressBarLayout;
    FloatingActionButton addSecretFloat;
    String userUniqueId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret_list);

        arrayList = new ArrayList<WishItem>();
        topic = new ArrayList<String>();
        tittle = new ArrayList<String>();
        password = new ArrayList<String>();
        date = new ArrayList<String>();
        time = new ArrayList<String>();

//        topic = getIntent().getStringArrayListExtra("topic");
//        tittle = getIntent().getStringArrayListExtra("tittle");
//        password = getIntent().getStringArrayListExtra("password");
//        date = getIntent().getStringArrayListExtra("date");
//        time = getIntent().getStringArrayListExtra("time");

        progressBar = findViewById(R.id.progressBar);
        progressBarLayout = findViewById(R.id.progressBarLayout);
        progressBar.setClickable(false);

//        dialog = new ProgressDialog(ActivitySecretList.this);
//        dialog.setMessage("Your data is loading...");
//        dialog.setTitle("Please wait");
//        dialog.setCancelable(false);

        mySecretList = findViewById(R.id.mySecretsList);
        addSecretFloat = findViewById(R.id.floatingActionButton);

        mAuth = FirebaseAuth.getInstance();
        userUniqueId = mAuth.getUid();
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("Database").child(userUniqueId).child("SecretList");


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                dialog.show();
//                progressBarLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                for (DataSnapshot snap : snapshot.getChildren())
                {
                    WishItem item = snap.getValue(WishItem.class);
                    arrayList.add(item);
                }
//                dialog.dismiss();
//                progressBarLayout.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                sortArrayList();
//                Toast.makeText(getApplicationContext(),"Your data is loaded Successfully",Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Please check your internet connection",Toast.LENGTH_SHORT).show();
            }
        });
        //---------for back button----------
        toolbar = findViewById(R.id.toolbarDemo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

//        for (int i = 0; i < topic.size(); i++) {
//            WishItem item = new WishItem(topic.get(i), tittle.get(i), date.get(i), time.get(i), password.get(i));
//            arrayList.add(item);
//        }

        adapter = new BaseAdapter() {

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            @Override
            public int getCount() {
                return arrayList.size();
            }

            @Override
            public Object getItem(int position) {
                return arrayList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @SuppressLint("InflateParams")
            @Override
            public View getView(int position, View view, ViewGroup viewGroup) {
                if (view == null) {
                    view = inflater.inflate(R.layout.favourite_item, null);

                }

                TextView favouriteTopic, favouriteDate, favouriteName, favouriteHint, favouriteTime;
                favouriteTopic = view.findViewById(R.id.favouriteTopic);
                favouriteDate = view.findViewById(R.id.favouriteDate);
                favouriteName = view.findViewById(R.id.favouriteName);
                favouriteHint = view.findViewById(R.id.favouriteHint);
                favouriteTime = view.findViewById(R.id.favouriteTime);

                //---------set the value of listView------------
                favouriteTopic.setText(arrayList.get(position).getWishTittle());
                favouriteDate.setText(arrayList.get(position).getDate());
                favouriteName.setText(arrayList.get(position).getWishName());
                favouriteTime.setText(arrayList.get(position).getTime());
                favouriteHint.setText(arrayList.get(position).getWishHint());

                return view;
            }
        };

        mySecretList.setAdapter(adapter);

        mySecretList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title, topic, pass, date, time, uniqueId;
                WishItem item =  arrayList.get(position);
                title = item.getWishTittle();
                topic = item.getWishHint();
                pass = item.getWishName();
                date = item.getDate();
                time = item.getTime();
                uniqueId = item.getUniqueId();
                Intent intent = new Intent(ActivitySecretList.this, SecretListItemViewActivity.class);

                //--------pass necessary value------------
                intent.putExtra("Title",title);
                intent.putExtra("Topic",topic);
                intent.putExtra("Pass",pass);
                intent.putExtra("Date",date);
                intent.putExtra("Time",time);
                intent.putExtra("UniqueID",uniqueId);
                startActivity(intent);
            }
        });

        mySecretList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final int itemNo = position;
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivitySecretList.this);
                alertDialog.setIcon(R.drawable.ic_delete_black);
                alertDialog.setTitle("Are you Sure?");
                alertDialog.setMessage("Do you want to delete this Secret?");
                alertDialog.setPositiveButton("No", null);
                alertDialog.setNegativeButton("Yes", (dialog, which) -> {

                    String uniqueID;
                    WishItem item = arrayList.get(itemNo);
                    uniqueID = item.getUniqueId();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Database").child(userUniqueId).child("SecretList").child(uniqueID);

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            reference.removeValue();
                            adapter.notifyDataSetChanged();
                            arrayList.clear();
                            Toast.makeText(getApplicationContext(),"Secret item is deleted",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(),"Something went wrong. Please try again..", Toast.LENGTH_SHORT).show();
                        }
                    });

                });
                alertDialog.show();
                return true;
            }
        });

        addSecretFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    //---------- sorting arrayList depends on time
    public void sortArrayList()
    {
        Collections.sort(arrayList, new Comparator<WishItem>() {
            @Override
            public int compare(WishItem o1, WishItem o2) {
                String obj1Date = null;
                String obj2Date = null;
                Date date1, date2;
                long d1, d2;
                try {
                    date1 = new SimpleDateFormat("dd/MM/yyyy").parse(o1.getDate());
                    date2 = new SimpleDateFormat("dd/MM/yyyy").parse(o2.getDate());

                    d1 = date1.getTime() + new SimpleDateFormat("HH:mm:ss a").parse(o1.getTime()).getTime();
                    d2 = date2.getTime() + new SimpleDateFormat("HH:mm:ss a").parse(o2.getTime()).getTime();

                    obj1Date = String.valueOf(d1);
                    obj2Date = String.valueOf(d2);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return obj1Date.compareTo(obj2Date);
            }
        });
        Collections.reverse(arrayList);
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
