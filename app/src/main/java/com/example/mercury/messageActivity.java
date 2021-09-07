package com.example.mercury;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class messageActivity extends AppCompatActivity {

    String ReceiverImage, ReceiverUID, ReceiverName,SenderUID;
    CircleImageView user_image;
    TextView receiver_name;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    public static String sImage;
    public static String rImage;

    CardView send_button;
    EditText edMessage;

    String senderRoom, receiverRoom;

    RecyclerView messageAdapter;
    ArrayList<Messages> messagesArrayList;

    MessagesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        database=FirebaseDatabase.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        ReceiverName=getIntent().getStringExtra("name");
        ReceiverImage=getIntent().getStringExtra("ReceiverImage");
        ReceiverUID=getIntent().getStringExtra("uid");

        messagesArrayList=new ArrayList<>();

        user_image=findViewById(R.id.user_image);
        Picasso.get().load(ReceiverImage).into(user_image);

        messageAdapter=findViewById(R.id.messageAdapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageAdapter.setLayoutManager(linearLayoutManager);
        adapter=new MessagesAdapter(messageActivity.this,messagesArrayList);
        messageAdapter.setAdapter(adapter);

        send_button=findViewById(R.id.send_button);
        edMessage=findViewById(R.id.edMessage);

        receiver_name=findViewById(R.id.receiver_name);
        receiver_name.setText(""+ReceiverName);

        SenderUID=firebaseAuth.getUid();

        senderRoom=SenderUID+ReceiverUID;
        receiverRoom=ReceiverUID+SenderUID;

        DatabaseReference reference=database.getReference().child("user").child(firebaseAuth.getUid());
        DatabaseReference chatReference=database.getReference().child("chats").child(senderRoom).child("messages");

        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messagesArrayList.clear();

                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Messages messages=dataSnapshot.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sImage=snapshot.child("imageUri").getValue().toString();
                rImage=ReceiverImage;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message=edMessage.getText().toString();
                if(message.isEmpty())
                {
                    return;
                }
                edMessage.setText("");
                Date date=new Date();

                Messages messages=new Messages(message,SenderUID,date.getTime());

                database.getReference().child("chats").child(senderRoom).child("messages").push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        database.getReference().child("chats").child(receiverRoom).child("messages").push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                    }
                });
            }
        });

    }
}