package com.dam.stockfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class BusquedaManual extends AppCompatActivity {

    private EditText editText;
    private ImageButton botonBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda_manual);

        editText = (EditText) findViewById(R.id.editText);
        botonBuscar = (ImageButton) findViewById(R.id.searchButton);
    }


}