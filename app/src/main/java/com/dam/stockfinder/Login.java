package com.dam.stockfinder;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Login extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Boolean authenticate;
    private ArrayList<Usuarios> datos;
    private String ciudad;
    private String nombreBBDD;
    private String passwordBBDD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);

        ciudad = null;
        nombreBBDD = null;
        passwordBBDD = null;
        final BBDD bbdd = new BBDD(getApplicationContext());
        datos = bbdd.obtenerUsuarios();
    }

    public void login(View view) {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        for (Usuarios usuario : datos) {
            if (usuario.getNombre().equals(username)) {
                ciudad = usuario.getCiudad();
                nombreBBDD = usuario.getNombre();
                passwordBBDD = usuario.getPassword();
                break; // No es necesario seguir buscando una vez que se ha encontrado el nombre
            }
        }

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, introduce tu nombre de usuario y contraseña", Toast.LENGTH_SHORT).show();
        } else {
            if (username.equals(nombreBBDD) && password.equals(passwordBBDD)) {
                authenticate = true;
            }
            else {
                authenticate = false;
            }
        }
    }
    public void IraMainActivity(View view) {
        login(view);
        if (authenticate) {
            // Guardar en SharedPreferences
            SharedPreferences prefs = getSharedPreferences("PreferenciasUsuario", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("nombre_tienda", ciudad);
            editor.putString("nombre_usuario", nombreBBDD);
            editor.apply();

            // Iniciar MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }
    }
}
