package com.nstut.fast_food_shop.util;

import java.text.NumberFormat;
import java.util.Locale;

public class Utils {
    public static String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
        return formatter.format(amount);
    }
}

