package com.example.mad_smartfit_android_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.ktx.Firebase;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText mEmail , mPassword;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mEmail = findViewById(R.id.tietEmail);
        mPassword = findViewById(R.id.tietPassword);
        fAuth = FirebaseAuth.getInstance();
    }

    public void onSignInClick(View view) {

        String email = this.mEmail.getText().toString();
        String password = this.mPassword.getText().toString();
        if(TextUtils.isEmpty(email)){
            mEmail.setError("Email is required.");
            Toast.makeText(LoginActivity.this,"Email is required.",Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)){
            mPassword.setError("Password is required.");
            Toast.makeText(LoginActivity.this,"Password is required.",Toast.LENGTH_LONG).show();
            return;
        }
        fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this,"Register Successful",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                }
                else{
                    Toast.makeText(LoginActivity.this,"Error!" + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void onSignUpClick(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }
}