package com.example.authapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNom, etPrenom, etEmail, etPassword, etConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        FormUtils.applySystemInsets(findViewById(R.id.root));

        etNom = findViewById(R.id.etNom);
        etPrenom = findViewById(R.id.etPrenom);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirm = findViewById(R.id.etConfirmPassword);

        FormUtils.bindPasswordToggle(etPassword, findViewById(R.id.btnTogglePassword));
        FormUtils.bindPasswordToggle(etConfirm, findViewById(R.id.btnToggleConfirm));

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.tvHaveAccount).setOnClickListener(v -> finish());
        findViewById(R.id.btnRegister).setOnClickListener(v -> register());
    }

    private void register() {
        String nom = etNom.getText().toString().trim();
        String prenom = etPrenom.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirm = etConfirm.getText().toString();

        if (nom.isEmpty()) { error(R.string.err_nom_required); return; }
        if (prenom.isEmpty()) { error(R.string.err_prenom_required); return; }
        if (email.isEmpty()) { error(R.string.err_email_required); return; }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { error(R.string.err_email_format); return; }
        if (UserStore.emailExists(email)) { error(R.string.err_email_used); return; }
        if (password.length() < 6) { error(R.string.err_password_short); return; }
        if (!password.equals(confirm)) { error(R.string.err_password_mismatch); return; }

        UserStore.add(new User(nom, prenom, email, password));

        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_EMAIL, email);
        startActivity(intent);
        finish();
    }

    private void error(int messageRes) {
        Toast.makeText(this, messageRes, Toast.LENGTH_SHORT).show();
    }
}
