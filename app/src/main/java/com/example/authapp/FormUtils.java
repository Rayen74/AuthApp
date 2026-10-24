package com.example.authapp;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Petits utilitaires d'interface partagés par les écrans :
 * erreurs sous les champs, clavier, bouton œil, marges système.
 */
public final class FormUtils {

    private FormUtils() {
    }

    // ---------- Erreurs de champ ----------

    /** Marque le champ en erreur (contour rouge) et affiche le message dessous. */
    public static void showError(EditText field, TextView errorView, String message) {
        field.setActivated(true);
        if (errorView != null) {
            errorView.setText(message);
            errorView.setVisibility(View.VISIBLE);
        }
    }

    /** Marque le champ en erreur sans message (le message est ailleurs, ex. bandeau). */
    public static void markInvalid(EditText field) {
        field.setActivated(true);
    }

    /** Retire l'erreur d'un champ. */
    public static void clearError(EditText field, TextView errorView) {
        field.setActivated(false);
        if (errorView != null) {
            errorView.setVisibility(View.GONE);
        }
    }

    /** Appelle onClear dès que l'utilisateur modifie un champ qui était en erreur. */
    public static void clearOnType(EditText field, Runnable onClear) {
        field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (field.isActivated()) {
                    onClear.run();
                }
            }
        });
    }

    // ---------- Clavier ----------

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view == null) return;
        InputMethodManager imm =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /** Demande le focus sur le champ et ouvre le clavier. */
    public static void focusAndShowKeyboard(Activity activity, EditText field) {
        field.requestFocus();
        field.post(() -> {
            InputMethodManager imm =
                    (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(field, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    // ---------- Mot de passe ----------

    /** Le bouton œil affiche ou masque le texte du champ mot de passe. */
    public static void bindPasswordToggle(EditText field, ImageButton button) {
        final boolean[] visible = {false};
        button.setOnClickListener(v -> {
            visible[0] = !visible[0];
            int cursor = field.getSelectionEnd();
            field.setTransformationMethod(visible[0]
                    ? HideReturnsTransformationMethod.getInstance()
                    : PasswordTransformationMethod.getInstance());
            if (cursor >= 0) field.setSelection(cursor);
            button.setImageResource(visible[0]
                    ? R.drawable.ic_visibility_off
                    : R.drawable.ic_visibility);
            button.setContentDescription(button.getContext().getString(visible[0]
                    ? R.string.cd_hide_password
                    : R.string.cd_show_password));
        });
    }

    // ---------- Marges système (barre d'état, navigation, clavier) ----------

    /**
     * Applique les marges système au conteneur racine : le contenu ne passe pas
     * sous la barre d'état ou la barre de navigation (Android 15 : affichage bord à bord),
     * et remonte quand le clavier s'ouvre.
     */
    public static void applySystemInsets(View root) {
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, windowInsets) -> {
            Insets bars = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            Insets ime = windowInsets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left, bars.top, bars.right, Math.max(bars.bottom, ime.bottom));
            return WindowInsetsCompat.CONSUMED;
        });
    }
}
