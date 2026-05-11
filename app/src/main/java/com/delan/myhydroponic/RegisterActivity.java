package com.delan.myhydroponic;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText etUsername, etEmail, etPassword, etConfirmPassword;
    Button btnRegister;
    TextView txtLogin;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        txtLogin = findViewById(R.id.txtLogin);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(v -> registerUser());

        txtLogin.setOnClickListener(v -> finish()); // balik ke login
    }

    private void registerUser() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirm = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email wajib diisi");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password wajib diisi");
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password minimal 6 karakter");
            return;
        }

        if (!password.equals(confirm)) {
            etConfirmPassword.setError("Password tidak sama");
            return;
        }

        // Register ke Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        String uid = mAuth.getCurrentUser().getUid();

                        // Data user untuk Firestore
                        Map<String, Object> user = new HashMap<>();
                        user.put("email", email);
                        user.put("role", "user");

                        // Simpan ke Firestore
                        db.collection("users")
                                .document(uid)
                                .set(user)
                                .addOnSuccessListener(unused -> {

                                    Toast.makeText(RegisterActivity.this,
                                            "Register berhasil!",
                                            Toast.LENGTH_SHORT).show();

                                    Intent intent =
                                            new Intent(RegisterActivity.this,
                                                    MainActivity.class);

                                    startActivity(intent);

                                    finish();

                                })
                                .addOnFailureListener(e -> {

                                    Toast.makeText(RegisterActivity.this,
                                            "Gagal simpan data: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();

                                });

                    } else {

                        Toast.makeText(RegisterActivity.this,
                                "Register gagal: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }

                });
    }
}