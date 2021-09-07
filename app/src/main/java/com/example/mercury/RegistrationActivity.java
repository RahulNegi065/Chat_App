package com.example.mercury;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {

    CircleImageView profile_icon;
    EditText reg_name,reg_email,reg_password, reg_cPassword;
    Button signup_button;
    TextView alUser_buton;
    FirebaseAuth auth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Uri imageUri;
    FirebaseDatabase database;
    FirebaseStorage storage;
    String imageURI;
    String status="Hey there! I'm using Mercury App";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();

        profile_icon=findViewById(R.id.profile_icon);
        reg_name=findViewById(R.id.reg_name);
        reg_email=findViewById(R.id.reg_email);
        reg_password=findViewById(R.id.reg_password);
        reg_cPassword=findViewById(R.id.reg_cPassword);
        signup_button=findViewById(R.id.signup_button);
        alUser_buton=findViewById(R.id.alUser_buton);

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=reg_name.getText().toString();
                String email=reg_email.getText().toString();
                String password=reg_password.getText().toString();
                String cPassword=reg_cPassword.getText().toString();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(cPassword))
                {
                    Toast.makeText(RegistrationActivity.this, "You left something empty!", Toast.LENGTH_SHORT).show();
                }else if (!email.matches(emailPattern))
                {
                    reg_email.setError("Invalid Email");
                    Toast.makeText(RegistrationActivity.this, "Please enter a valid email!", Toast.LENGTH_SHORT).show();
                }else if (!password.equals(cPassword))
                {
                    Toast.makeText(RegistrationActivity.this, "password doesn't matched!", Toast.LENGTH_SHORT).show();
                }else if (password.length()<6)
                {
                    Toast.makeText(RegistrationActivity.this, "Enter atleast 6 character password!", Toast.LENGTH_SHORT).show();
                }else {
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                DatabaseReference databaseReference=database.getReference().child("user").child(auth.getUid());
                                StorageReference storageReference=storage.getReference().child("upload").child(auth.getUid());

                                if (imageUri!=null)
                                {
                                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful())
                                            {
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageURI=uri.toString();
                                                        Users users=new Users(auth.getUid(),name,email,imageURI,status);
                                                        databaseReference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful())
                                                                {
                                                                    startActivity(new Intent(RegistrationActivity.this,ChatActivity.class));
                                                                }else {
                                                                    Toast.makeText(RegistrationActivity.this, "Error while creating user!", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }else {
                                    String status="Hey there! I'm using Mercury App";
                                    imageURI="https://firebasestorage.googleapis.com/v0/b/mercury-chatting-app.appspot.com/o/profile_icon.png?alt=media&token=512b2228-f1b6-43de-b0fb-fb364665830b";
                                    Users users=new Users(auth.getUid(),name,email,imageURI,status);
                                    databaseReference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                startActivity(new Intent(RegistrationActivity.this,ChatActivity.class));
                                            }else {
                                                Toast.makeText(RegistrationActivity.this, "Error while creating user!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }

                            }else {
                                Toast.makeText(RegistrationActivity.this, "something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
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

        alUser_buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
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
                imageUri=data.getData();
                profile_icon.setImageURI(imageUri);
            }
        }
    }
}