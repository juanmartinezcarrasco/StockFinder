package com.dam.stockfinder;

import android.app.Instrumentation;
import android.widget.TextView;
import android.content.SharedPreferences;


import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.google.zxing.client.android.Intents;
import com.dam.stockfinder.databinding.ActivityMainBinding;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    Button btn_scan;
    Button btn_manual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        SharedPreferences prefs = getSharedPreferences("PreferenciasUsuario", MODE_PRIVATE);
        String nombreTienda = prefs.getString("nombre_tienda", "valor por defecto si no existe");
        String nombreUsuario = prefs.getString("nombre_usuario", "valor por defecto si no existe");
        View headerView = navigationView.getHeaderView(0);
        // Encuentra el TextView para el nav_header_subtitle y actualiza su texto
        TextView navHeaderSubtitle = headerView.findViewById(R.id.textView);
        navHeaderSubtitle.setText(nombreUsuario);
        // Encuentra el TextView para el nav_header_subtitle y actualiza su texto
        TextView navHeaderTitle = headerView.findViewById(R.id.textotienda);
        navHeaderTitle.setText(nombreTienda);

        btn_scan = findViewById(R.id.qrButton);

        btn_manual = findViewById(R.id.detailButton);

        btn_scan.setOnClickListener(v->
                {

                    scanCode();

                }

                );

        btn_manual.setOnClickListener(v->
                {

                    IraDetalleArticulo();

                }

        );


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void scanCode() {

        ScanOptions options = new ScanOptions();
        options.setPrompt("Escanee el código");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(LectorQR.class);

        resultadoScan.launch(options);
    }

    ActivityResultLauncher<ScanOptions> resultadoScan = registerForActivityResult(new ScanContract(),result->{

    if(result.getContents() !=null){
        Intent intent = new Intent(this, DetalleArticulo.class);
        intent.putExtra("id_prenda", result.getContents());
        startActivity(intent);
    }


    });



    public void IraDetalleArticulo() {
         EditText editText = (EditText) findViewById(R.id.barcodeButton);
        String id = editText.getText().toString().trim();
        /* Si se introduce un ID, se abrirá la nueva vista, en caso contrario,
        no lo hará y mostrará error */
        if (!id.isEmpty()) {
            Intent intent = new Intent(this, DetalleArticulo.class);
            //Con la siguiente funcion enviamos el id a la siguiente vista
            intent.putExtra("id_prenda", id);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, "Por favor, introduce un ID", Toast.LENGTH_SHORT).show();
        }
    }
    public void abrirListaCompleta(View view){
        Intent intent = new Intent(this, Lista.class);
        startActivity(intent);
    }

    public void abrirListaCompleta(MenuItem item) {
        Intent intent = new Intent(this, Lista.class);
        startActivity(intent);
    }
    public void abrirMenuPrincipal(MenuItem item) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void scanCode(MenuItem item) {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Escanee el código");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(LectorQR.class);

        resultadoScan.launch(options);
    }
    public void CerrarSesion(MenuItem item) {
        // Borrar SharedPreferences
        SharedPreferences prefs = getSharedPreferences("PreferenciasUsuario", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear(); // Esto eliminará todos los datos de SharedPreferences
        editor.apply();

        // Iniciar Login
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Esto limpiará la pila de actividades
        startActivity(intent);
        finish(); // Esto cerrará la actividad actual
    }
}