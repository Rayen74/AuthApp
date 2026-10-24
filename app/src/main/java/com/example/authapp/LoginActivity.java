package com.example.authapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_EMAIL = "email";
    public static final String EXTRA_PRENOM = "prenom";

    private EditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FormUtils.applySystemInsets(findViewById(R.id.root));

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        // E-mail pré-rempli après une inscription
        String email = getIntent().getStringExtra(EXTRA_EMAIL);
        if (email != null) etEmail.setText(email);

        FormUtils.bindPasswordToggle(etPassword, findViewById(R.id.btnTogglePassword));

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnLogin).setOnClickListener(v -> login());
        findViewById(R.id.tvCreateAccount).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.msg_fill_login, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, R.string.err_email_format, Toast.LENGTH_SHORT).show();
            return;
        }

        User user = UserStore.authenticate(email, password);
        if (user == null) {
            Toast.makeText(this, R.string.msg_login_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(EXTRA_PRENOM, user.getPrenom());
        intent.putExtra(EXTRA_EMAIL, user.getEmail());
        startActivity(intent);
        finish();
    }
}
