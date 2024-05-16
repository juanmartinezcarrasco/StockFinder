package com.dam.stockfinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.dam.stockfinder.databinding.ActivityListaBinding;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import android.app.ProgressDialog;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.squareup.picasso.Picasso;
import android.text.TextWatcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Lista extends AppCompatActivity {

    private ArrayList<PrendaList> datos = new ArrayList<>();
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityListaBinding binding;

    private ArrayAdapter<PrendaList> adapter;
    private EditText barraBuscar;

    private Button botonNoClickable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityListaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarLista.toolbar);
        binding.appBarLista.fab.setOnClickListener(new View.OnClickListener() {
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
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_lista);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        findViewById(R.id.botonNoClicable).setClickable(false); //Hacemos el boton solo estetico

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

        final String URLrequest="https://proyectotiendas1.azurewebsites.net/prendas?tienda="+nombreTienda;
        final ProgressDialog dlg = ProgressDialog.show(
                this,
                "Obteniendo los datos REST",
                "Por favor, espere...", true);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,URLrequest, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            dlg.dismiss();
                            //   int codigo=response.getInt();
                            if(true){   //placeholder
                                //Se ha recibido una lista
                                Toast.makeText(getApplicationContext(),
                                        "Lista recibida de forma correcta",
                                        Toast.LENGTH_SHORT).show();

                                //Obtener objetos

                                for (int i = 0; i < response.length(); i++) {
                                    // creating a new json object and
                                    // getting each object from our json array.

                                    // we are getting each json object.
                                    JSONObject objeto = response.getJSONObject(i);
                                    PrendaList prenda = new PrendaList(objeto.getString("id"),objeto.getString("nombre"),
                                            objeto.getString("stock"),objeto.getString("imagen"),objeto.getString("categoria"));
                                    datos.add(prenda);
                                }

                                viewcreator(datos);
                            } else {
                                //Problema en la obtencion de la lista
                                Toast.makeText(getApplicationContext(),
                                        "No se ha recibido la lista", Toast.LENGTH_SHORT).show();
                            } } catch (JSONException e){
                            e.printStackTrace();
                        }
                        VolleyLog.v("Response:%n %s", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dlg.dismiss();
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "No se ha recibido la prenda", Toast.LENGTH_SHORT).show();
            }
        });
        // add the request object to the queue to be executed
        RESTapplication.getInstance().getRequestQueue().add(request);


        barraBuscar = findViewById(R.id.barraBuscar);

        barraBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });






        /* Con el adapter, el ListView se rellenará automáticamente con todas las
        entradas de la lista de prendas. Cada entrada tendrá el layout prenda.xml
        De esta forma ahorramos elementos en el layout de esta actividad,
        usando para ello un único listView */


    }
    public void viewcreator(ArrayList<PrendaList> lista){
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new Lista_adaptador(this, R.layout.prenda, lista){

            @Override
            /* Este método se encarga de introducir cada entrada una a una,
            y de colocar los detalles en el layout */
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {
                    ImageView imagen = (ImageView) view.findViewById(R.id.article_image);
                    if (imagen != null){
                        Picasso.get().load(((PrendaList) entrada).getImagen()).into(imagen);
                    }
                    TextView texto_id = (TextView) view.findViewById(R.id.article_id);
                    if (texto_id != null)
                        texto_id.setText(String.format("Id: %s", ((PrendaList) entrada).getId()));

                    TextView texto_nombre = (TextView) view.findViewById(R.id.article_name);
                    if (texto_nombre != null)
                        texto_nombre.setText(String.format("Nombre: %s", ((PrendaList) entrada).getNombre()));

                    TextView texto_categoria = (TextView) view.findViewById(R.id.article_category);
                    if (texto_categoria != null)
                        texto_categoria.setText(String.format("Categoría: %s", ((PrendaList) entrada).getCategoria()));

                    TextView texto_stock = (TextView) view.findViewById(R.id.article_stock);
                    if (texto_stock != null)
                        texto_stock.setText(String.format("Stock: %s", ((PrendaList) entrada).getStock()));
                }
            }
        });






        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                TextView textoId = (TextView) view.findViewById(R.id.article_id);

                Intent returnIntent = new Intent(Lista.this, DetalleArticulo.class);
                returnIntent.putExtra("id_prenda", textoId.getText().toString().substring(4));
                startActivity(returnIntent);
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.lista, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_lista);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    public void abrirListaCompleta(MenuItem item) {
        Intent intent = new Intent(this, Lista.class);
        startActivity(intent);
    }
    ActivityResultLauncher<ScanOptions> resultadoScan = registerForActivityResult(new ScanContract(),result->{

        if(result.getContents() !=null){
            Intent intent = new Intent(this, DetalleArticulo.class);
            intent.putExtra("id_prenda", result.getContents());
            startActivity(intent);
        }


    });
    public void scanCode(MenuItem item) {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Escanee el código");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(LectorQR.class);

        resultadoScan.launch(options);
    }
    public void abrirMenuPrincipal(MenuItem item)  {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void filter(String text) {
        ArrayList<PrendaList> datosfiltrados = new ArrayList<>();

        // Filter itemList based on the entered text
        for (PrendaList item : datos) {
            if (item.getId().toLowerCase().contains(text.toLowerCase())) {
                datosfiltrados.add(item);
            }
        }

        viewcreator(datosfiltrados);
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