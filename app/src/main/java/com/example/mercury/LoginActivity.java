package com.example.mercury;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    Button signIn_button, create_button;
    EditText login_email, login_password;
    FirebaseAuth auth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth=FirebaseAuth.getInstance();

        signIn_button=findViewById(R.id.signIn_button);
        create_button=findViewById(R.id.create_button);
        login_email=findViewById(R.id.login_email);
        login_password=findViewById(R.id.login_password);

        signIn_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=login_email.getText().toString();
                String password=login_password.getText().toString();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
                {
                    Toast.makeText(LoginActivity.this, "Either Email or Password is Empty", Toast.LENGTH_SHORT).show();
                }else if (!email.matches(emailPattern))
                {
                    login_email.setError("Invalid Email");
                    Toast.makeText(LoginActivity.this, "Please enter a valid email!", Toast.LENGTH_SHORT).show();
                }else if (password.length()<6)
                {
                    login_password.setError("Invalid Password");
                    Toast.makeText(LoginActivity.this, "Enter atleast 6 character password", Toast.LENGTH_SHORT).show();
                }else {
                    auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                startActivity(new Intent(LoginActivity.this,ChatActivity.class));
                            }else{
                                Toast.makeText(LoginActivity.this, "Error in Login", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });
    }

}