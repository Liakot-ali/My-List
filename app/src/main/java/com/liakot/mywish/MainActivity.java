package com.liakot.mywish;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ImageView navProfilePic;
    TextView navUserFirstName, navUserEmail, navUserWork;
    String email, password;
    FirebaseAuth mAuth;
    String  uniqueUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        uniqueUserId = mAuth.getUid();

//        email = getIntent().getStringExtra("Email");
//        password = getIntent().getStringExtra("Password");

        toolbar = findViewById(R.id.toolbarDemo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        //------------access imageView from header----------
        View view = navigationView.getHeaderView(0);
        navProfilePic = view.findViewById(R.id.navProfilePic);
        navUserFirstName = view.findViewById(R.id.navUserFirstName);
        navUserEmail = view.findViewById(R.id.navUserEmail);
        navUserWork = view.findViewById(R.id.navUserWork);

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Database").child(uniqueUserId).child("UserProfile");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfile profile = snapshot.getValue(UserProfile.class);
                if(!profile.getProfilePicRef().isEmpty())
                {
                    Picasso.get().load(profile.getProfilePicRef()).into(navProfilePic);
                }
                String userWork = profile.getWork();
                String type = null, place = null;

                if(!userWork.isEmpty()) {
                    type = userWork.substring(0, userWork.indexOf('*'));
                    place = userWork.substring(userWork.indexOf('*') + 1, userWork.length());
                    navUserWork.setText(type + " , " + place);
                }
                else{
                    navUserWork.setText("");
                }
                navUserFirstName.setText(profile.getFirstName());
                navUserEmail.setText(profile.getEmail());

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_LONG).show();

            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentAddMyWish()).commit();
            navigationView.setCheckedItem(R.id.addWish);
        }

        navProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }

                //------------- for hide the keyboard------
                InputMethodManager methodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                methodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                Intent intent = new Intent(MainActivity.this, AboutMe.class);
                startActivity(intent);
            }
        });
    }

    private long onBackPressedTime;
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            //---------- double back press to exit
            if(onBackPressedTime + 2000 > System.currentTimeMillis())
            {
                super.onBackPressed();
                return;
            }
            else{
                Toast.makeText(getBaseContext(),"Press back again to exit",Toast.LENGTH_SHORT).show();
            }
            onBackPressedTime = System.currentTimeMillis();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_main_menu, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        TextView toolbarText;
        toolbarText = findViewById(R.id.toolbarTextView);

        switch (item.getItemId()) {
            case R.id.addWish:
                toolbarText.setText("Add Wish Item");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentAddMyWish()).commit();
//                navigationView.setCheckedItem(R.id.addWish);
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                break;

            case R.id.myFavourite:
                toolbarText.setText("Add Favourite Item");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentMyFavourite()).commit();
//                navigationView.setCheckedItem(R.id.addWish);
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                break;

            case R.id.mySecrets:
                toolbarText.setText("Add Secret Item");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentMySecrets()).commit();
//                navigationView.setCheckedItem(R.id.addWish);
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                break;
            case R.id.aboutMe:
                Intent intent;
                intent=new Intent(MainActivity.this,AboutMe.class);
                startActivity(intent);

                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                break;
            case  R.id.developer:
                Intent intent1=new Intent(MainActivity.this,DeveloperActivity.class);
                startActivity(intent1);

                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                break;
            case R.id.signOut:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setIcon(R.drawable.sign_out);
                alertDialog.setTitle("Are you Sure?");
                alertDialog.setMessage("Do you want to Sign Out?");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mAuth.signOut();
                        // ----------- Do not skip login activity if user is Log Out-----------
                        SharedPreferences preferences = getSharedPreferences(LoginActivity.LOGIN_PRE, 0);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("hasLoggedIn",false);
                        editor.commit();

                        Toast.makeText(getApplicationContext(), "Log out successful", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
                alertDialog.setNegativeButton("No", null);
                alertDialog.show();

                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}

