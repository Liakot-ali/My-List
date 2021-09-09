package com.liakot.mywish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.sql.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

public class ActivityAddWish extends AppCompatActivity {
    Toolbar toolbar;
    ListView myWishList;
    ArrayList<WishItem> arrayList;
    BaseAdapter adapter;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    ProgressDialog dialog;
    ProgressBar progressBar;
    LinearLayout progressBarLayout;
    LinearLayout toolbarLayout;
    ArrayList<WishItem> arrayList1 = new ArrayList<>();
    int count=0;
    Boolean itemSelected;
    int itemSelectedPosition;
    FloatingActionButton addWishFloat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wish);

        // ---------Create all arrayList-----------------
        arrayList = new ArrayList<WishItem>();

//        wishTittleS = new ArrayList<String>();
//        wishStringS = new ArrayList<String>();
//        wishDateS = new ArrayList<String>();
//        wishTimeS = new ArrayList<String>();

        progressBar = findViewById(R.id.progressBar);
        progressBarLayout = findViewById(R.id.progressBarLayout);
        progressBar.setClickable(false);
        itemSelected = false;
        itemSelectedPosition = 1000000000;

//        dialog = new ProgressDialog(ActivityAddWish.this);
//        dialog.setTitle("Please Wait..");
//        dialog.setMessage("Your data is loading..");
//        dialog.setCancelable(false);

        //---------------- Fetched value from database------------------
        mAuth = FirebaseAuth.getInstance();
        String uniqueUserId = mAuth.getUid();
        database = FirebaseDatabase.getInstance();
        assert uniqueUserId != null;
        DatabaseReference myRef = database.getReference().child("Database").child(uniqueUserId).child("WishList");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {  //---- all value is stored in snapshot
                progressBar.setVisibility(View.VISIBLE);
                //----------- Disable user interaction-------
//                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                progressBar.setClickable(false);
                for (DataSnapshot snap : snapshot.getChildren())
                {
                    WishItem item = snap.getValue(WishItem.class);  //---- Fetched value from snapshot to arrayList
                    arrayList.add(item);
                }
                progressBar.setVisibility(View.INVISIBLE);
                //----------- Able user interaction-------
//                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                Toast.makeText(ActivityAddWish.this,"Your data is loaded Successfully",Toast.LENGTH_SHORT).show();
//                Toast.makeText(getApplicationContext(),"remove from here", Toast.LENGTH_SHORT).show();
                sortArrayList();
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Please check your internet connection",Toast.LENGTH_SHORT).show();
            }
        });


        //-----------receive arrayList from FragmentAddMyWish---------
//        wishTittleS = getIntent().getStringArrayListExtra("wish tittle");
//        wishStringS = getIntent().getStringArrayListExtra("wish string");
//        wishDateS = getIntent().getStringArrayListExtra("wish date");
//        wishTimeS = getIntent().getStringArrayListExtra("wish time");

//        ----------- Assign all the value to arrayList<WishItem> which is used to create ListView----------
//        assert wishTimeS != null;
//        for (int i = 0; i<wishTimeS.size(); i++)
//        {
//            WishItem item = new WishItem(wishTittleS.get(i),wishStringS.get(i),wishDateS.get(i),wishTimeS.get(i));
//            arrayList.add(item);
//        }

        //---------for back button----------
        toolbar = findViewById(R.id.toolbarDemo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        addWishFloat = findViewById(R.id.floatingActionButton);
//        addWishBtn = findViewById(R.id.addWishButton);
        myWishList = findViewById(R.id.myWishList);


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
                    view = inflater.inflate(R.layout.wish_item, null);
                }

                //-------------- Access all necessary textView--------
                CheckBox wishItemCheckBox = view.findViewById(R.id.wishItemCheckBox);
                ConstraintLayout wishItemLayout = view.findViewById(R.id.wishItemLayout);
//                wishItemCheckBox.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
                TextView wishTittleTV, wishTimeTV, wishHintTV, wishDateTV;
                wishTittleTV = view.findViewById(R.id.wishTittleTV);
                wishDateTV = view.findViewById(R.id.wishDateTV);
                wishHintTV = view.findViewById(R.id.wishHintTV);
                wishTimeTV = view.findViewById(R.id.wishTimeTV);

                if(itemSelected && position == itemSelectedPosition)
                {
                    wishItemLayout.setBackgroundResource(android.R.drawable.alert_light_frame);
//                    wishItemCheckBox.setVisibility(View.VISIBLE);
//                    wishItemCheckBox.setChecked(true);
                }
                else if(!itemSelected && position == itemSelectedPosition) {
                    wishItemLayout.setBackgroundResource(R.drawable.layout_background);
//                    wishItemCheckBox.setVisibility(View.VISIBLE);
//                    wishItemCheckBox.setChecked(false);
                }
                //----------- set value to textView-----------
                wishTittleTV.setText(arrayList.get(position).getWishTittle());
                wishHintTV.setText(arrayList.get(position).getWishHint());
                wishTimeTV.setText(arrayList.get(position).getTime());
                wishDateTV.setText(arrayList.get(position).getDate());

                return view;
            }
        };

//        Toast.makeText(getApplicationContext(),"Data Fetched successfully",Toast.LENGTH_SHORT).show();

        //-------------- when press addWishBtn then back to FragmentAddWish---------
        addWishFloat.setOnClickListener(v -> onBackPressed());

        //------------ when Click specific item goto new WishListItemVIewActivity------------
        myWishList.setOnItemClickListener((parent, view, position, id) -> {
            String title, wish, date, time, uniqueID;
            WishItem item =  arrayList.get(position);
            title = item.getWishTittle();
            wish = item.getWishHint();
            date = item.getDate();
            time = item.getTime();
            uniqueID = item.getUniqueId();
            Intent intent = new Intent(ActivityAddWish.this, WishListItemVIewActivity.class);

            //--------pass necessary value------------
            intent.putExtra("Title",title);
            intent.putExtra("Wish",wish);
            intent.putExtra("Date",date);
            intent.putExtra("Time",time);
            intent.putExtra("UniqueID",uniqueID);
            startActivity(intent);
        });

        //---- to multiChoiceMode On
//        myWishList.setChoiceMode(myWishList.CHOICE_MODE_MULTIPLE_MODAL);
//        myWishList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
//            @Override
//            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
//                if(checked) {
//                    itemSelected = true;
//                    itemSelectedPosition = position;
//                    arrayList1.add(arrayList.get(position));
//                    adapter.notifyDataSetChanged();
//                }
//                else{
//                    itemSelected = false;
//                    itemSelectedPosition = position;
//                    WishItem item = arrayList.get(position);
//                    arrayList1.remove(item);
//                    adapter.notifyDataSetChanged();
//                }
//                count = arrayList1.size();
//                if(count != 0)
//                {
//                    mode.setTitle(count + " items selected");
//                }
//            }
//
//            @Override
//            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//
//                toolbarLayout = findViewById(R.id.toolbarLayout);
//                toolbarLayout.setVisibility(View.GONE);
//                toolbar.setVisibility(View.GONE);
//                MenuInflater inflater = mode.getMenuInflater();
//                inflater.inflate(R.menu.action_bar,menu);
//                return true;
//            }
//
//            @Override
//            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//
//                return false;
//            }
//
//            @Override
//            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {    //-----------access actionBar and perform operation-------------
//                if(item.getItemId() == R.id.deleteAction)
//                {
//                    progressBar.setVisibility(View.VISIBLE);
//                    for (WishItem item1  : arrayList1)
//                    {
////                        String uniqueId = item1.getUniqueId();
//                        arrayList.remove(item1);
////                        myRef.child(uniqueId).removeValue();
////                        arrayList.clear();
//                    }
//                    progressBar.setVisibility(View.INVISIBLE);
//                    Toast.makeText(ActivityAddWish.this,"Items deleted",Toast.LENGTH_SHORT).show();
//                    onDestroyActionMode(mode);
//                    return true;
//                }
//                else if(item.getItemId() == R.id.selectAllAction)
//                {
//                    Toast.makeText(getApplicationContext(),"Select all clicked",Toast.LENGTH_SHORT).show();
//                    return true;
//                }
//                return false;
//            }
//
//            @Override
//            public void onDestroyActionMode(ActionMode mode) {
//
//                for (int i = 0; i<arrayList.size(); i++)
//                {
//                    itemSelected = false;
//                    itemSelectedPosition = i;
//                    adapter.notifyDataSetChanged();
//                }
//                mode.finish();
//                count = 0;
//                arrayList1.clear();
//                CheckBox wishItemCheckBox = findViewById(R.id.wishItemCheckBox);
//                wishItemCheckBox.setChecked(false);
//                adapter.notifyDataSetChanged();
//                toolbarLayout.setVisibility(View.VISIBLE);
//                toolbar.setVisibility(View.VISIBLE);
//            }
//        });

        myWishList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final int itemNo = position;
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityAddWish.this);
                alertDialog.setIcon(R.drawable.ic_delete_black);
                alertDialog.setTitle("Are you Sure?");
                alertDialog.setMessage("Do you want to delete this wish?");
                alertDialog.setPositiveButton("No", null);
                alertDialog.setNegativeButton("Yes", (dialog, which) -> {

                    String uniqueID;
                    WishItem item = arrayList.get(itemNo);
                    uniqueID = item.getUniqueId();
                    DatabaseReference reference = myRef.child(uniqueID);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            reference.removeValue();
                            adapter.notifyDataSetChanged();
                            arrayList.clear();
                            Toast.makeText(getApplicationContext(),"Wish item is deleted",Toast.LENGTH_SHORT).show();
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

        myWishList.setAdapter(adapter);
    }

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
