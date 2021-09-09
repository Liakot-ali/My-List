package com.liakot.mywish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.security.keystore.UserNotAuthenticatedException;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

public class ActivityFavouriteList extends AppCompatActivity {

    Toolbar toolbar;
    ListView myFavouriteList;

    ArrayList<WishItem> arrayList;
    BaseAdapter adapter;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    ProgressDialog dialog;
    String userUniqueId;

    ProgressBar progressBar;
    LinearLayout progressBarLayout;
    FloatingActionButton addFavFloat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_list);

        arrayList = new ArrayList<WishItem>();
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userUniqueId = mAuth.getUid();

//        topic = new ArrayList<String>();
//        date = new ArrayList<String>();
//        name = new ArrayList<String>();
//        time = new ArrayList<String>();
//        hint = new ArrayList<String>();


//        dialog = new ProgressDialog(ActivityFavouriteList.this);
//        dialog.setTitle("Please Wait..");
//        dialog.setMessage("Your data is loading..");
//        dialog.setCancelable(false);

        progressBar = findViewById(R.id.progressBar);
        progressBarLayout = findViewById(R.id.progressBarLayout);
        progressBar.setClickable(false);

        addFavFloat = findViewById(R.id.floatingActionButton);
        myFavouriteList = findViewById(R.id.myFavouriteList);


        //-----------receive the ArrayList From fragment-----------
//        topic = getIntent().getStringArrayListExtra("topic");
//        date = getIntent().getStringArrayListExtra("date");
//        name = getIntent().getStringArrayListExtra("name");
//        time = getIntent().getStringArrayListExtra("time");
//        hint = getIntent().getStringArrayListExtra("hint");


        //---------for back button----------
        toolbar = findViewById(R.id.toolbarDemo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

//        for (int i = 0; i < topic.size(); i++) {
//            WishItem item = new WishItem(topic.get(i), hint.get(i), date.get(i), time.get(i), name.get(i));
//            arrayList.add(item);
//        }
        //--------------Data fetched from database---------
        DatabaseReference myRef = database.getReference().child("Database").child(userUniqueId).child("FavouriteList");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                progressBarLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                for (DataSnapshot snap : snapshot.getChildren())
                {
                    WishItem item = snap.getValue(WishItem.class);
                    arrayList.add(item);
                }
//                progressBarLayout.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                sortArrayList();
//                Toast.makeText(ActivityFavouriteList.this,"Your data is loaded Successfully",Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Please check your internet connection",Toast.LENGTH_SHORT).show();
            }
        });
        //-----------------Custom Adaptor for create ListVIew Form arrayList value-------------
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

                //-------------- Access all necessary textView from favourite_item--------
                TextView favouriteTopic, favouriteDate, favouriteName, favouriteHint, favouriteTime, favouriteImageUri;
                favouriteTopic =view.findViewById(R.id.favouriteTopic);
                favouriteDate = view.findViewById(R.id.favouriteDate);
                favouriteName = view.findViewById(R.id.favouriteName);
                favouriteHint = view.findViewById(R.id.favouriteHint);
                favouriteTime = view.findViewById(R.id.favouriteTime);
                favouriteImageUri = view.findViewById(R.id.imageUri);
//                favouriteImageUri.setVisibility(View.VISIBLE);


                //---------set the value of Favourite_item------------
                favouriteTopic.setText(arrayList.get(position).getWishTittle());
                favouriteDate.setText(arrayList.get(position).getDate());
                favouriteName.setText(arrayList.get(position).getWishName());
                favouriteTime.setText(arrayList.get(position).getTime());
                favouriteHint.setText(arrayList.get(position).getWishHint());
//                favouriteImageUri.setText(arrayList.get(position).getImageUri());

                return view;
            }
        };
        myFavouriteList.setAdapter(adapter);

        myFavouriteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name, topic, reason, date, time,image, uniqueId, imageName;
                WishItem item =  arrayList.get(position);
                topic = item.getWishTittle();
                reason = item.getWishHint();
                name = item.getWishName();
                date = item.getDate();
                time = item.getTime();
                image = item.getImageUri();
                uniqueId = item.getUniqueId();
                imageName = item.getImageName();
                Intent intent = new Intent(ActivityFavouriteList.this, FavouriteListItemVIewActivity.class);

                //--------pass necessary value------------
                intent.putExtra("Name",name);
                intent.putExtra("Topic",topic);
                intent.putExtra("Reason",reason);
                intent.putExtra("Date",date);
                intent.putExtra("Time",time);
                intent.putExtra("Image",image);
                intent.putExtra("UniqueID",uniqueId);
                intent.putExtra("ImageName",imageName);
                startActivity(intent);
            }
        });

        myFavouriteList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityFavouriteList.this);
                alertDialog.setIcon(R.drawable.ic_delete_black);
                alertDialog.setTitle("Are you Sure?");
                alertDialog.setMessage("Do you want to delete this Favourite?");
                alertDialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String uniqueId;
                        WishItem item = arrayList.get(position);
                        uniqueId = item.getUniqueId();
                        DatabaseReference reference = database.getReference().child("Database").child(userUniqueId).child("FavouriteList").child(uniqueId);
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                reference.removeValue();
                                adapter.notifyDataSetChanged();
                                arrayList.clear();
                                Toast.makeText(getApplicationContext(),"Favourite item is deleted",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getApplicationContext(),"Something went wrong. Please try again..", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
                alertDialog.setPositiveButton("No", null);
                alertDialog.show();
                return true;
            }
        });

        //-------------- when press addWishBtn then back to FragmentMyFavourite---------
        addFavFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    //-------------to sort the arrayList depends on time---------
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

    //---------for back to previous activity-------------
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
