package com.example.bookly;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText firstNameEditText, lastNameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button signupButton;
    private TextView loginRedirectText;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.signupEmailEditText);
        passwordEditText = findViewById(R.id.signupPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signupButton = findViewById(R.id.signupButton);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        signupButton.setOnClickListener(v -> {
            String firstName = firstNameEditText.getText().toString().trim();
            String lastName = lastNameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            // Validate inputs
            if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) ||
                    TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(SignupActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(SignupActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a new user in Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Save additional user details to Firestore
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();
                                Map<String, Object> userDetails = new HashMap<>();
                                userDetails.put("firstName", firstName);
                                userDetails.put("lastName", lastName);
                                userDetails.put("email", email);

                                db.collection("users").document(userId)
                                        .set(userDetails)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Toast.makeText(SignupActivity.this, "Signup Successful! Please log in.", Toast.LENGTH_SHORT).show();
                                                // Redirect to LoginActivity after successful signup
                                                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(SignupActivity.this, "Error saving user details: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(SignupActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        loginRedirectText.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }
}