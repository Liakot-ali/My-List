package com.liakot.mywish;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.UUID;

public class FragmentMySecrets extends Fragment {
    private EditText secTopic, secTittle, secPass;
    private Button addThisSecBtn, gotoSecListBtn;
    private ArrayList<String> topic, tittle, password, date, time;
    private ArrayList<WishItem> arrayList;
    private View view;
    TextView secretUserFirstName;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    String userUniqueId;
    ProgressDialog dialog;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_my_secrets, container, false);

        topic = new ArrayList<String>();
        tittle = new ArrayList<String>();
        password = new ArrayList<String>();
        date = new ArrayList<String>();
        time = new ArrayList<String>();
        arrayList = new ArrayList<WishItem>();

        secTopic = view.findViewById(R.id.secretTopicET);
        secTittle = view.findViewById(R.id.secretTittleET);
        secPass = view.findViewById(R.id.secretPassET);
        addThisSecBtn = view.findViewById(R.id.addThisSecBtn);
        gotoSecListBtn = view.findViewById(R.id.gotoSecretBtn);
        secretUserFirstName = view.findViewById(R.id.secretUserFirstName);

        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();
        userUniqueId = mAuth.getUid();
        database = FirebaseDatabase.getInstance();

        DatabaseReference  ref = database.getReference().child("Database").child(userUniqueId).child("UserProfile");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfile profile = snapshot.getValue(UserProfile.class);
                assert profile != null;
                secretUserFirstName.setText("Hey " + profile.getFirstName() + "..!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addThisSecBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                String secTopicSt, secTittleSt, secPassSt, secDateSt, secTimeSt;
                Date date1 = new Date();

                secTopicSt = secTopic.getText().toString();
                secTittleSt = secTittle.getText().toString();
                secPassSt = secPass.getText().toString();
                secDateSt = DateFormat.format("dd/MM/yyyy", date1).toString();
                secTimeSt = DateFormat.format("HH:mm:ss a", date1).toString();
                if (secTopicSt.isEmpty())
                {
                    dialog.dismiss();
                    secTopic.setError("This field is Empty.");
                }
                else if (secTittleSt.isEmpty())
                {
                    dialog.dismiss();
                    secTittle.setError("This field is Empty.");
                }
                else if (secPassSt.isEmpty())
                {
                    dialog.dismiss();
                    secPass.setError("This field is Empty.");
                }
                else {
                    String uniqueID;
                    uniqueID = UUID.randomUUID().toString();  //-----generate unique ID----------
                    WishItem item = new WishItem(secTittleSt, secTopicSt, secDateSt, secTimeSt, secPassSt, uniqueID);

                    DatabaseReference myRef = database.getReference().child("Database").child(userUniqueId).child("SecretList").child(uniqueID);
                    myRef.setValue(item).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            Toast.makeText(getContext(), "New secret item added", Toast.LENGTH_SHORT).show();
                            secTittle.setText("");
                            secTopic.setText("");
                            secPass.setText("");
                        } else {
                            dialog.dismiss();
                            Toast.makeText(getContext(), "Something Went Wrong. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });

//                topic.add(secTopicSt);
//                tittle.add(secTittleSt);
//                password.add(secPassSt);
//                date.add(secDateSt);
//                time.add(secTimeSt);
//
//
//                secTopic.setText("");
//                secTittle.setText("");
//                secPass.setText("");

                    //--------To hide the keyboard window-----------
                    InputMethodManager methodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    methodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                }

//                Toast.makeText(getContext(), "New Secret Added", Toast.LENGTH_SHORT).show();
            }
        });

        gotoSecListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivitySecretList.class);
//                intent.putExtra("topic", topic);
//                intent.putExtra("tittle", tittle);
//                intent.putExtra("password", password);
//                intent.putExtra("date", date);
//                intent.putExtra("time", time);
                startActivity(intent);
            }
        });
        return view;
    }
}
