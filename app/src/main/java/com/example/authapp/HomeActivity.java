package com.example.authapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FormUtils.applySystemInsets(findViewById(R.id.root));

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        TextView tvEmail = findViewById(R.id.tvEmail);

        // Données transmises par LoginActivity via Intent
        String prenom = getIntent().getStringExtra(LoginActivity.EXTRA_PRENOM);
        String email = getIntent().getStringExtra(LoginActivity.EXTRA_EMAIL);

        tvWelcome.setText(getString(R.string.home_welcome, prenom != null ? prenom : ""));
        tvEmail.setText(email != null ? email : "");

        // Demande confirmation avant de se déconnecter
        findViewById(R.id.btnLogout).setOnClickListener(v -> confirmLogout());
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.logout_confirm_title)
                .setMessage(R.string.logout_confirm_message)
                .setNegativeButton(R.string.btn_cancel, null)
                .setPositiveButton(R.string.btn_logout, (dialog, which) -> logout())
                .show();
    }

    /** Retour à l'écran de connexion ; la pile d'écrans est vidée. */
    private void logout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
