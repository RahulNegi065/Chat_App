package com.example.mercury;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    FirebaseAuth auth;
    RecyclerView chat_list;
    UserAdapter adapter;
    FirebaseDatabase database;
    ArrayList<Users> usersArrayList;
    ImageView settings_button;
    private boolean doubleBackToExitPressedOnce=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        usersArrayList=new ArrayList<>();

        DatabaseReference reference=database.getReference().child("user");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Users users=dataSnapshot.getValue(Users.class);
                    if (!users.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                        usersArrayList.add(users);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        chat_list=findViewById(R.id.chat_list);
        chat_list.setLayoutManager(new LinearLayoutManager(this));
        adapter=new UserAdapter(ChatActivity.this,usersArrayList);
        chat_list.setAdapter(adapter);
        settings_button=findViewById(R.id.settings_button);


        if (auth.getCurrentUser()==null)
        {
            startActivity(new Intent(ChatActivity.this,RegistrationActivity.class));
        }

        settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatActivity.this,Settings_Activity.class));
            }
        });

        settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatActivity.this, Settings_Activity.class));
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        Toast.makeText(this, "Click back again to exit", Toast.LENGTH_SHORT).show();
        doubleBackToExitPressedOnce=true;
    }
}