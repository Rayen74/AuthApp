package com.example.authapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_EMAIL = "email";
    public static final String EXTRA_PRENOM = "prenom";
    public static final String EXTRA_REGISTERED = "registered";

    private EditText etEmail;
    private EditText etPassword;
    private TextView tvEmailError;
    private TextView tvPasswordError;
    private TextView tvBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FormUtils.applySystemInsets(findViewById(R.id.root));

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvEmailError = findViewById(R.id.tvEmailError);
        tvPasswordError = findViewById(R.id.tvPasswordError);
        tvBanner = findViewById(R.id.tvBanner);

        // Flèche de retour : seulement si on peut revenir à un écran précédent
        View btnBack = findViewById(R.id.btnBack);
        if (!isTaskRoot()) {
            btnBack.setVisibility(View.VISIBLE);
            btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        }

        // Dès que l'utilisateur corrige un champ, son erreur disparaît
        FormUtils.clearOnType(etEmail, () -> {
            FormUtils.clearError(etEmail, tvEmailError);
            hideBanner();
        });
        FormUtils.clearOnType(etPassword, () -> {
            FormUtils.clearError(etPassword, tvPasswordError);
            hideBanner();
        });

        FormUtils.bindPasswordToggle(etPassword, findViewById(R.id.btnTogglePassword));

        // Bouton "Se connecter" et touche "OK" du clavier
        findViewById(R.id.btnLogin).setOnClickListener(v -> attemptLogin());
        etPassword.setOnEditorActionListener((tv, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptLogin();
                return true;
            }
            return false;
        });

        // Lien "Créer un compte" -> RegisterActivity (Intent explicite)
        findViewById(R.id.tvCreateAccount).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        // Éléments de la maquette, pas encore fonctionnels
        View.OnClickListener soon = v ->
                Toast.makeText(this, R.string.msg_coming_soon, Toast.LENGTH_SHORT).show();
        findViewById(R.id.tvForgot).setOnClickListener(soon);
        findViewById(R.id.btnGoogle).setOnClickListener(soon);
        findViewById(R.id.btnFacebook).setOnClickListener(soon);
        findViewById(R.id.btnApple).setOnClickListener(soon);

        handleIntent(getIntent());
    }

    /** Appelée quand RegisterActivity renvoie vers cet écran déjà ouvert. */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    /** Après une inscription : e-mail pré-rempli, message de succès, curseur sur le mot de passe. */
    private void handleIntent(Intent intent) {
        String email = intent.getStringExtra(EXTRA_EMAIL);
        if (email == null) return;

        etEmail.setText(email);
        etPassword.setText("");
        if (intent.getBooleanExtra(EXTRA_REGISTERED, false)) {
            showBanner(getString(R.string.msg_register_ok), true);
        }
        FormUtils.focusAndShowKeyboard(this, etPassword);

        // Évite de rejouer ces actions après une rotation de l'écran
        intent.removeExtra(EXTRA_EMAIL);
        intent.removeExtra(EXTRA_REGISTERED);
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        FormUtils.clearError(etEmail, tvEmailError);
        FormUtils.clearError(etPassword, tvPasswordError);
        hideBanner();

        // 1. Champs non vides (le focus finit sur le premier champ à corriger)
        boolean emptyField = false;
        if (password.isEmpty()) {
            FormUtils.showError(etPassword, tvPasswordError, getString(R.string.err_password_required));
            etPassword.requestFocus();
            emptyField = true;
        }
        if (email.isEmpty()) {
            FormUtils.showError(etEmail, tvEmailError, getString(R.string.err_email_required));
            etEmail.requestFocus();
            emptyField = true;
        }
        if (emptyField) {
            showBanner(getString(R.string.msg_fill_login), false);
            return;
        }

        // Format de l'e-mail : inutile de chercher un compte si l'adresse est mal écrite
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            FormUtils.showError(etEmail, tvEmailError, getString(R.string.err_email_format));
            etEmail.requestFocus();
            return;
        }

        // 2-3. Comparaison avec les comptes en mémoire
        User user = UserStore.authenticate(email, password);

        // 4. Identifiants incorrects : on efface le mot de passe et on laisse l'utilisateur réessayer
        if (user == null) {
            etPassword.setText("");
            FormUtils.markInvalid(etEmail);
            FormUtils.markInvalid(etPassword);
            showBanner(getString(R.string.msg_login_failed), false);
            etPassword.requestFocus();
            return;
        }

        // 5. Succès : ouvre l'accueil (Intent explicite + données transmises)
        FormUtils.hideKeyboard(this);
        Toast.makeText(this, R.string.msg_login_ok, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(EXTRA_PRENOM, user.getPrenom());
        intent.putExtra(EXTRA_EMAIL, user.getEmail());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // ---------- Bandeau de message ----------

    private void showBanner(String message, boolean success) {
        tvBanner.setText(message);
        tvBanner.setBackgroundResource(success
                ? R.drawable.bg_banner_success
                : R.drawable.bg_banner_error);
        tvBanner.setTextColor(ContextCompat.getColor(this,
                success ? R.color.success : R.color.error));
        tvBanner.setVisibility(View.VISIBLE);
    }

    private void hideBanner() {
        tvBanner.setVisibility(View.GONE);
    }
}
