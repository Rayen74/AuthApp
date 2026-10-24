package com.example.authapp;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public final class FormUtils {

    private FormUtils() { }

    /** Bouton œil : affiche ou masque le mot de passe. */
    public static void bindPasswordToggle(EditText field, ImageButton button) {
        final boolean[] visible = {false};
        button.setOnClickListener(v -> {
            visible[0] = !visible[0];
            field.setTransformationMethod(visible[0]
                    ? HideReturnsTransformationMethod.getInstance()
                    : PasswordTransformationMethod.getInstance());
            field.setSelection(field.getText().length());
            button.setImageResource(visible[0]
                    ? R.drawable.ic_visibility_off
                    : R.drawable.ic_visibility);
        });
    }

    /** Évite que le contenu passe sous les barres système ou le clavier. */
    public static void applySystemInsets(View root) {
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left, bars.top, bars.right, Math.max(bars.bottom, ime.bottom));
            return WindowInsetsCompat.CONSUMED;
        });
    }
}
