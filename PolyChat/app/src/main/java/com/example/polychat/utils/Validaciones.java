package com.example.polychat.utils;

public class Validaciones {
    public static String newUser(String username, String email, String password, String confirmPassword) {
        String errors = "";
        if (username.length() < 4) {
            errors += "El nombre de usuario tiene que tener mas de 3 letras.\n";
        }
        if (!email.matches("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$")) {
            errors += "El email no es correcto.\n";
        }
        if (!password.equals(confirmPassword)) {
            errors += "Las contraseñas no coinciden.\n";
        }
        if (!password.matches("^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+!=])"
                + "(?=\\S+$).{7,20}$")) {
            errors += "La contraseña tiene que tener números, mayusculas, minusculas, un simbolo especial y de 8 a 20 caracteres.\n";
        }
        return errors;
    }
}
