package com.example.authapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Écran d'accueil de la maquette (optionnel) : illustration + boutons Connexion / Inscription.
 * Pour s'en passer, déplacez le filtre MAIN/LAUNCHER du manifeste vers LoginActivity.
 */
public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        FormUtils.applySystemInsets(findViewById(R.id.root));

        View btnLogin = findViewById(R.id.btnWelcomeLogin);
        View btnRegister = findViewById(R.id.btnWelcomeRegister);

        btnLogin.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));

        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }
}
