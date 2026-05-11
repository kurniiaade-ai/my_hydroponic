package com.delan.myhydroponic;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView txtRegister;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            startActivity(new Intent(
                    LoginActivity.this,
                    MainActivity.class));

            finish();
        }

        setContentView(R.layout.activity_login); // sesuaikan nama file XML kamu

        // Inisialisasi komponen
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);

        mAuth = FirebaseAuth.getInstance();

        // Event tombol login
        btnLogin.setOnClickListener(v -> loginUser());

        // Event register (sementara)
        txtRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void loginUser() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validasi email
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email tidak boleh kosong");
            etEmail.requestFocus();
            return;
        }

        // Validasi password
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password tidak boleh kosong");
            etPassword.requestFocus();
            return;
        }

        // Login Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        Toast.makeText(LoginActivity.this,
                                "Login berhasil",
                                Toast.LENGTH_SHORT).show();

                        Intent intent =
                                new Intent(LoginActivity.this,
                                        MainActivity.class);

                        startActivity(intent);

                        overridePendingTransition(
                                R.anim.slide_in,
                                R.anim.fade_out);

                        finish();

                    } else {

                        Toast.makeText(LoginActivity.this,
                                "Login gagal: "
                                        + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}