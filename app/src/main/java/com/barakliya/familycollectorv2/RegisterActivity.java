package com.barakliya.familycollectorv2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.annotation.NonNull;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class RegisterActivity extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword,editTextUsername;
    Button btnRegister, btnLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;


    @Override
    public void onStart() {
        // If user is already logged in to application go to main activity
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);
        editTextUsername = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);
        editTextEmail = findViewById(R.id.email);
        btnRegister = findViewById(R.id.btn_register);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password, username;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                username = String.valueOf(editTextUsername.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(RegisterActivity.this, "Enter email", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "Enter password", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(RegisterActivity.this, "Enter user name", Toast.LENGTH_LONG).show();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {

                                    Toast.makeText(RegisterActivity.this, "Account Created Successfully!", Toast.LENGTH_LONG).show();
                                    userID = mAuth.getCurrentUser().getUid();
                                    DocumentReference documentReference = fStore.collection("users").document(userID);
                                    Map<String,Object> user = new HashMap<>();
                                    user.put("email",email);
                                    user.put("UserName",username);

                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d(TAG, "onSuccess: User Profile is created for " + userID);
                                        }
                                    });
                                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
   });
}
}