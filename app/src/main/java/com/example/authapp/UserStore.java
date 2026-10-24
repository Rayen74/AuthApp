package com.example.authapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Stockage STATIQUE des comptes, en mémoire (Exercices 5, 6 et 7).
 *
 * Les données vivent tant que le processus de l'application est en vie :
 * si l'application est fermée complètement, la liste est recréée
 * et les comptes inscrits pendant l'exécution sont perdus.
 */
public final class UserStore {

    private static final List<User> users = new ArrayList<>();

    static {
        // Compte de test défini au démarrage (Exercice 5) — adaptez-le au tableau de votre énoncé
        users.add(new User("Ben Salah", "Ahmed", "ahmed@test.com", "123456"));
    }

    private UserStore() {
    }

    /** Retourne true si un compte existe déjà avec cet e-mail. */
    public static boolean emailExists(String email) {
        return findByEmail(email) != null;
    }

    /** Cherche un compte par e-mail (insensible à la casse). */
    public static User findByEmail(String email) {
        if (email == null) return null;
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email.trim())) {
                return u;
            }
        }
        return null;
    }

    /** Retourne le compte si l'e-mail et le mot de passe correspondent, sinon null. */
    public static User authenticate(String email, String password) {
        User u = findByEmail(email);
        if (u != null && u.getPassword().equals(password)) {
            return u;
        }
        return null;
    }

    /** Ajoute un compte à la collection. */
    public static void add(User user) {
        users.add(user);
    }
}
