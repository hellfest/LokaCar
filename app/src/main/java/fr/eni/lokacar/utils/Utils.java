package fr.eni.lokacar.utils;

import android.text.TextUtils;

/**
 * Created by lbouvet2016 on 28/06/2017.
 */

public class Utils {
    public static boolean isEmailValid(String email) {
        return !(email == null || TextUtils.isEmpty(email)) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
