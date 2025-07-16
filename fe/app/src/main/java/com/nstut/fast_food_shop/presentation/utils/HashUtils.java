package com.nstut.fast_food_shop.presentation.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class HashUtils {
    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public static boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.verifyer().verify(password.toCharArray(), hashedPassword).verified;
    }
}