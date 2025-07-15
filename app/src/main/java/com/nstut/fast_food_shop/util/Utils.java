package com.nstut.fast_food_shop.util;

import java.text.NumberFormat;
import java.util.Locale;

public class Utils {
    public static String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return formatter.format(amount) + "đ";
    }
}

