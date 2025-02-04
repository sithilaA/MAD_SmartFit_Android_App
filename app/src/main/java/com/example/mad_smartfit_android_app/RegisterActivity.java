package com.example.mad_smartfit_android_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mad_smartfit_android_app.utils.NetworkUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {
    TextInputEditText mUserName , mFullName , mEmail , mMobileNumber , mPassword;
    AutoCompleteTextView mGender ;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mUserName = findViewById(R.id.tietUserName);
        mFullName = findViewById(R.id.tietFullName);
        mEmail = findViewById(R.id.tietEmail);
        mMobileNumber = findViewById(R.id.tietMoblieNumber);
        mPassword = findViewById(R.id.tietPassword);


        fAuth = FirebaseAuth.getInstance();
        if(fAuth == null){
            Toast.makeText(RegisterActivity.this,"Firebase Authentication not initialized",Toast.LENGTH_LONG).show();
            return;
        }
        fStore = FirebaseFirestore.getInstance();
        if(fStore == null){
            Toast.makeText(RegisterActivity.this,"Firebase Firestore not initialized",Toast.LENGTH_LONG).show();
            return;
        }

        // Reference AutoCompleteTextView
        mGender = findViewById(R.id.acGender);

        // Gender options
        String[] genders = {"Male", "Female"};

        // Create ArrayAdapter and set it to AutoCompleteTextView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genders);
        mGender.setAdapter(adapter);

    }

    public void onSignInClick(View view) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void onSignUpClick(View view) {
        if (!NetworkUtils.isInternetAvailable(this)) {
            Toast.makeText(RegisterActivity.this, "No internet connection. Please check your network.", Toast.LENGTH_LONG).show();
            return;
        }

        String email = this.mEmail.getText().toString();
        String password = this.mPassword.getText().toString();
        String userName = this.mUserName.getText().toString();
        String fullName = this.mFullName.getText().toString();
        String mobileNumber = this.mMobileNumber.getText().toString();
        String gender = this.mGender.getText().toString();

        if (TextUtils.isEmpty(gender)){
            mGender.setError("Gender is required.");
            Toast.makeText(RegisterActivity.this, "Gender is required.", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(userName)){
            mUserName.setError("User Name is required.");
            Toast.makeText(RegisterActivity.this,"User Name is required.",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(fullName)){
            mFullName.setError("Full Name is required.");
            Toast.makeText(RegisterActivity.this,"Full Name is required.",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(mobileNumber)){
            mMobileNumber.setError("Mobile Number is required.");
            Toast.makeText(RegisterActivity.this,"Mobile Number is required.",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(email)){
            mEmail.setError("Email is required.");
            Toast.makeText(RegisterActivity.this,"Email is required.",Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)){
            mPassword.setError("Password is required.");
            Toast.makeText(RegisterActivity.this,"Password is required.",Toast.LENGTH_LONG).show();
            return;
        }
        if(password.length() < 6){
            mPassword.setError("Password must be >= 6 characters");
            Toast.makeText(RegisterActivity.this,"Password must be >= 6 characters",Toast.LENGTH_LONG).show();
            return;
        }

        fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this,"Register Successful",Toast.LENGTH_LONG).show();
                    userID = fAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = fStore.collection("users").document(userID);
                    Map<String,Object> user = new HashMap<>();
                    user.put("userName",userName);
                    user.put("fullName",fullName);
                    user.put("email",email);
                    user.put("mobileNumber",mobileNumber);
                    user.put("gender",gender);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("TAG","onSuccess: user profile is created for "+ userID);
                        }
                    });
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                }
                else{
                    Toast.makeText(RegisterActivity.this,"Error!" + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}