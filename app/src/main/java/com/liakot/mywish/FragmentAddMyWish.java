package com.liakot.mywish;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.UUID;


public class FragmentAddMyWish extends Fragment {
    private Button addThisButton, gotoWishListBtn;
    private EditText addWishTittleET, addWishStringET;
    private ArrayList<WishItem> arrayList;
    private View view;
    TextView wishUserFirstName;
    private ProgressDialog progressDialog;
    private FirebaseDatabase database;
    String userUniqueId;
    FirebaseAuth mAuth;

    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_add_wish, container, false);

        addWishTittleET = view.findViewById(R.id.addWishTittleET);
        addWishStringET = view.findViewById(R.id.addWishStringET);
        addThisButton = view.findViewById(R.id.addThisWishBtn);
        gotoWishListBtn = view.findViewById(R.id.gotoWishListBtn);
        wishUserFirstName = view.findViewById(R.id.wishUserFirstName);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("We working on your wish..");
        progressDialog.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();
        userUniqueId = mAuth.getUid();
        database = FirebaseDatabase.getInstance();

        DatabaseReference ref = database.getReference().child("Database").child(userUniqueId).child("UserProfile");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfile profile = snapshot.getValue(UserProfile.class);
                assert profile != null;
                wishUserFirstName.setText("Hey " + profile.getFirstName() + "..!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        addThisButton.setOnClickListener(v -> {
            progressDialog.show();
            String wishTittle, wishString, dateString, timeString, uniqueID;
            Date date = new Date();
            wishTittle = addWishTittleET.getText().toString();
            wishString = addWishStringET.getText().toString();
            dateString = DateFormat.format("dd/MM/yyyy",date).toString();
            timeString = DateFormat.format("HH:mm:ss a",date).toString();
            if(wishTittle.isEmpty())
            {
                progressDialog.dismiss();
                addWishTittleET.setError("This field is Empty.");
            }
            else if(wishString.isEmpty())
            {
                progressDialog.dismiss();
                addWishStringET.setError("This field is Empty");
            }
            else {
                uniqueID = UUID.randomUUID().toString();
                DatabaseReference myRef = database.getReference().child("Database").child(userUniqueId).child("WishList").child(uniqueID);

                WishItem item = new WishItem(wishTittle, wishString, dateString, timeString, uniqueID);
                myRef.setValue(item).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "New wish Added.", Toast.LENGTH_SHORT).show();
                        addWishStringET.setText("");
                        addWishTittleET.setText("");
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Something Went Wrong. Please try again..", Toast.LENGTH_SHORT).show();
                    }
                });
                //--------To hide the keyboard window-----------
                InputMethodManager methodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                methodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        });

//        arrayList = new ArrayList<WishItem>();
//        wishDateS = new ArrayList<String>();
//        wishStringS = new ArrayList<String>();
//        wishTimeS = new ArrayList<String>();
//        wishTittleS = new ArrayList<String>();
//
//        database1 = FirebaseDatabase.getInstance();
        gotoWishListBtn.setOnClickListener(v -> {
//            int total = 0;
//            progressDialog.show();
//            DatabaseReference myRefe = database1.getReference().child("WishList");
//
//            myRefe.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {  //---- all value is stored in snapshot
//
//                    int i=0;
//                    for (DataSnapshot snap : snapshot.getChildren())
//                    {
//                        WishItem item = snap.getValue(WishItem.class);  //---- Fetched value from snapshot to arrayList
//                        arrayList.add(i,item);
//                        i++;
//                    }
//                    progressDialog.dismiss();
//                    Toast.makeText(getContext(),"Data fetched Successfully",Toast.LENGTH_SHORT).show();
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    progressDialog.dismiss();
//                    Toast.makeText(getContext(),"Data not fetched..",Toast.LENGTH_SHORT).show();
//                }
//            });
//
//            for (int i=0; i<arrayList.size(); i++)
//            {
//                WishItem item = arrayList.get(i);
//                wishTittleS.add(item.getWishTittle());
//                wishStringS.add(item.getWishHint());
//                wishDateS.add(item.getDate());
//                wishTimeS.add(item.getTime());
//            }
              Intent intent = new Intent(getActivity(), ActivityAddWish.class);
            //-------send the arrayList to activityAddWish----------
//            intent.putExtra("wish tittle", wishTittleS);
//            intent.putExtra("wish string",wishStringS);
//            intent.putExtra("wish date",wishDateS);
//            intent.putExtra("wish time",wishTimeS);
            startActivity(intent);
        });
        return view;
    }

}
