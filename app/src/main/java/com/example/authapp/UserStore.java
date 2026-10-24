package com.example.authapp;

import java.util.ArrayList;
import java.util.List;

public final class UserStore {

    private static final List<User> users = new ArrayList<>();

    static {
        users.add(new User("Ben Salah", "Ahmed", "ahmed@test.com", "123456"));
    }

    private UserStore() { }

    public static void add(User user) {
        users.add(user);
    }

    public static boolean emailExists(String email) {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) return true;
        }
        return false;
    }

    /** Retourne l'utilisateur si e-mail et mot de passe correspondent, sinon null. */
    public static User authenticate(String email, String password) {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }
}
