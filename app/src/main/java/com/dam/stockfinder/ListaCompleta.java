package com.dam.stockfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

    //DEPRECATED -> SUSTITUIDA POR Lista.class

public class ListaCompleta extends AppCompatActivity {

    private ArrayList<PrendaList> datos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_completa);


        final String URLrequest="https://proyectotiendas1.azurewebsites.net/prendas";
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

                                viewcreator();
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



        /* Con el adapter, el ListView se rellenará automáticamente con todas las
        entradas de la lista de prendas. Cada entrada tendrá el layout prenda.xml
        De esta forma ahorramos elementos en el layout de esta actividad,
        usando para ello un único listView */


    }

    public void viewcreator(){
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new Lista_adaptador(this, R.layout.prenda, datos){

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

                Intent returnIntent = new Intent(ListaCompleta.this, DetalleArticulo.class);
                returnIntent.putExtra("id_prenda", textoId.getText().toString().substring(4));
                startActivity(returnIntent);
            }
        });



    }


    }

