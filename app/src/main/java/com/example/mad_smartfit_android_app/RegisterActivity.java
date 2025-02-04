package com.example.mad_smartfit_android_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegisterActivity extends AppCompatActivity {
    TextInputEditText mUserName , mFullName , mEmail , mMobileNumber , mPassword;
    AutoCompleteTextView gender ;
    FirebaseAuth fAuth;

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
        // Reference AutoCompleteTextView
        AutoCompleteTextView autoCompleteGender = findViewById(R.id.acGender);

        // Gender options
        String[] genders = {"Male", "Female"};

        // Create ArrayAdapter and set it to AutoCompleteTextView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genders);
        autoCompleteGender.setAdapter(adapter);

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