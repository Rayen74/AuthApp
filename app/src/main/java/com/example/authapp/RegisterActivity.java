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

public class RegisterActivity extends AppCompatActivity {

    private static final int MIN_PASSWORD_LENGTH = 6;

    private EditText etNom, etPrenom, etEmail, etPassword, etConfirm;
    private TextView tvNomError, tvPrenomError, tvEmailError,
            tvPasswordError, tvPasswordHelper, tvConfirmError;

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
        tvNomError = findViewById(R.id.tvNomError);
        tvPrenomError = findViewById(R.id.tvPrenomError);
        tvEmailError = findViewById(R.id.tvEmailError);
        tvPasswordError = findViewById(R.id.tvPasswordError);
        tvPasswordHelper = findViewById(R.id.tvPasswordHelper);
        tvConfirmError = findViewById(R.id.tvConfirmError);

        // Flèche de retour
        findViewById(R.id.btnBack).setOnClickListener(v ->
                getOnBackPressedDispatcher().onBackPressed());

        // L'erreur d'un champ disparaît dès qu'on le corrige
        FormUtils.clearOnType(etNom, () -> FormUtils.clearError(etNom, tvNomError));
        FormUtils.clearOnType(etPrenom, () -> FormUtils.clearError(etPrenom, tvPrenomError));
        FormUtils.clearOnType(etEmail, () -> FormUtils.clearError(etEmail, tvEmailError));
        FormUtils.clearOnType(etPassword, () -> {
            FormUtils.clearError(etPassword, tvPasswordError);
            tvPasswordHelper.setVisibility(View.VISIBLE);
        });
        FormUtils.clearOnType(etConfirm, () -> FormUtils.clearError(etConfirm, tvConfirmError));

        // Vérification dès qu'on quitte un champ rempli (sans harceler sur les champs vides)
        etEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && !text(etEmail).isEmpty()) checkEmail();
        });
        etPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && !etPassword.getText().toString().isEmpty()) checkPassword();
        });
        etConfirm.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && !etConfirm.getText().toString().isEmpty()) checkConfirm();
        });

        FormUtils.bindPasswordToggle(etPassword, findViewById(R.id.btnTogglePassword));
        FormUtils.bindPasswordToggle(etConfirm, findViewById(R.id.btnToggleConfirm));

        // Bouton "S'inscrire" et touche "OK" du clavier
        findViewById(R.id.btnRegister).setOnClickListener(v -> attemptRegister());
        etConfirm.setOnEditorActionListener((tv, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptRegister();
                return true;
            }
            return false;
        });

        // "J'ai déjà un compte" -> retour à la connexion
        findViewById(R.id.tvHaveAccount).setOnClickListener(v -> goToLogin(null));

        View.OnClickListener soon = v ->
                Toast.makeText(this, R.string.msg_coming_soon, Toast.LENGTH_SHORT).show();
        findViewById(R.id.btnGoogle).setOnClickListener(soon);
        findViewById(R.id.btnFacebook).setOnClickListener(soon);
        findViewById(R.id.btnApple).setOnClickListener(soon);
    }

    // ---------- Validation champ par champ (chaque méthode retourne true si valide) ----------

    private boolean checkNom() {
        if (text(etNom).isEmpty()) {
            return fail(etNom, tvNomError, R.string.err_nom_required);
        }
        return true;
    }

    private boolean checkPrenom() {
        if (text(etPrenom).isEmpty()) {
            return fail(etPrenom, tvPrenomError, R.string.err_prenom_required);
        }
        return true;
    }

    private boolean checkEmail() {
        String email = text(etEmail);
        if (email.isEmpty()) {
            return fail(etEmail, tvEmailError, R.string.err_email_required);
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return fail(etEmail, tvEmailError, R.string.err_email_format);
        }
        if (UserStore.emailExists(email)) {
            return fail(etEmail, tvEmailError, R.string.err_email_used);
        }
        return true;
    }

    private boolean checkPassword() {
        String password = etPassword.getText().toString();
        if (password.isEmpty()) {
            return failPassword(R.string.err_password_required);
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return failPassword(R.string.err_password_short);
        }
        return true;
    }

    private boolean checkConfirm() {
        String confirm = etConfirm.getText().toString();
        if (confirm.isEmpty()) {
            return fail(etConfirm, tvConfirmError, R.string.err_confirm_required);
        }
        if (!confirm.equals(etPassword.getText().toString())) {
            return fail(etConfirm, tvConfirmError, R.string.err_password_mismatch);
        }
        return true;
    }

    private void attemptRegister() {
        // Tous les champs sont vérifiés d'un coup pour afficher toutes les erreurs ensemble
        boolean nomOk = checkNom();
        boolean prenomOk = checkPrenom();
        boolean emailOk = checkEmail();
        boolean passwordOk = checkPassword();
        boolean confirmOk = checkConfirm();

        // Le focus va sur le premier champ à corriger
        if (!nomOk) { etNom.requestFocus(); return; }
        if (!prenomOk) { etPrenom.requestFocus(); return; }
        if (!emailOk) { etEmail.requestFocus(); return; }
        if (!passwordOk) { etPassword.requestFocus(); return; }
        if (!confirmOk) { etConfirm.requestFocus(); return; }

        // Création de l'objet User et ajout à la collection
        String email = text(etEmail);
        UserStore.add(new User(text(etNom), text(etPrenom), email,
                etPassword.getText().toString()));

        // Retour à la connexion avec l'e-mail déjà rempli et un message de confirmation
        FormUtils.hideKeyboard(this);
        goToLogin(email);
    }

    // ---------- Helpers ----------

    private String text(EditText field) {
        return field.getText().toString().trim();
    }

    private boolean fail(EditText field, TextView errorView, int messageRes) {
        FormUtils.showError(field, errorView, getString(messageRes));
        return false;
    }

    /** Pour le mot de passe, l'erreur remplace le texte d'aide. */
    private boolean failPassword(int messageRes) {
        tvPasswordHelper.setVisibility(View.GONE);
        return fail(etPassword, tvPasswordError, messageRes);
    }

    /**
     * Retourne vers LoginActivity (Intent explicite).
     * CLEAR_TOP | SINGLE_TOP réutilise l'écran de connexion déjà ouvert.
     */
    private void goToLogin(String registeredEmail) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (registeredEmail != null) {
            intent.putExtra(LoginActivity.EXTRA_EMAIL, registeredEmail);
            intent.putExtra(LoginActivity.EXTRA_REGISTERED, true);
        }
        startActivity(intent);
        finish();
    }
}
