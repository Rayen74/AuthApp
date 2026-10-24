package com.example.authapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FormUtils.applySystemInsets(findViewById(R.id.root));

        String prenom = getIntent().getStringExtra(LoginActivity.EXTRA_PRENOM);
        String email = getIntent().getStringExtra(LoginActivity.EXTRA_EMAIL);

        ((TextView) findViewById(R.id.tvWelcome)).setText(getString(R.string.home_welcome, prenom));
        ((TextView) findViewById(R.id.tvEmail)).setText(email);

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
