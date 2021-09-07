package com.example.mercury;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings_Activity extends AppCompatActivity {

    ImageView logOut_button;
    CircleImageView profile_icon;
    EditText settings_name, settings_status;
    TextView settings_done;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri setImgURI;
    String email;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();

        profile_icon=findViewById(R.id.profile_icon);
        settings_name=findViewById(R.id.settings_name);
        settings_status=findViewById(R.id.settings_status);
        settings_done=findViewById(R.id.settings_done);
        logOut_button=findViewById(R.id.logOut_button);

        DatabaseReference reference=database.getReference().child("user").child(auth.getUid());
        StorageReference storageReference=storage.getReference().child("upload").child(auth.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email=snapshot.child("email").getValue().toString();
                String name=snapshot.child("name").getValue().toString();
                String status=snapshot.child("status").getValue().toString();
                String image=snapshot.child("imageUri").getValue().toString();

                settings_name.setText(name);
                settings_status.setText(status);
                Picasso.get().load(image).into(profile_icon);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        profile_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
            }
        });

        settings_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.show();

                String name=settings_name.getText().toString();
                String status=settings_status.getText().toString();

                if (setImgURI!=null)
                {
                    storageReference.putFile(setImgURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String finalImgUri=uri.toString();
                                    Users users=new Users(auth.getUid(),name,email,finalImgUri,status);

                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                progressDialog.dismiss();
                                                Toast.makeText(Settings_Activity.this, "Data Successfully Updated", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(Settings_Activity.this,ChatActivity.class));
                                            }
                                            else {
                                                progressDialog.dismiss();
                                                Toast.makeText(Settings_Activity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
                else {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String finalImgUri=uri.toString();
                            Users users=new Users(auth.getUid(),name,email,finalImgUri,status);

                            reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        progressDialog.dismiss();
                                        Toast.makeText(Settings_Activity.this, "Data Successfully Updated", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Settings_Activity.this,ChatActivity.class));
                                    }
                                    else {
                                        progressDialog.dismiss();
                                        Toast.makeText(Settings_Activity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });

        logOut_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog=new Dialog(Settings_Activity.this,R.style.Dialog);

                dialog.setContentView(R.layout.dialog_layout);

                TextView no_button, exit_button;

                no_button=dialog.findViewById(R.id.no_button);
                exit_button=dialog.findViewById(R.id.exit_button);

                no_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                exit_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(Settings_Activity.this,LoginActivity.class));
                    }
                });

                dialog.show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==10)
        {
            if (data!=null)
            {
                setImgURI=data.getData();
                profile_icon.setImageURI(setImgURI);
            }
        }
    }
}