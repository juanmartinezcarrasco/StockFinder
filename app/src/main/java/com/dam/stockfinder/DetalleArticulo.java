package com.dam.stockfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Application;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class DetalleArticulo extends AppCompatActivity {

    private ImageView imagen;
    private TextView idText;
    private TextView nombreText;
    private TextView descripcionText;
    private TextView categoriaText;
    private TextView tallaText;
    private TextView stockText;
    private TextView precioText;
    private TextView composicionText;
    private TextView proveedorText;

    private Button botonSolicitar;

    private Button botonVender;
    private String id_prenda;
    private Prenda prenda;
    private String nombreTienda;
    private String URLsolicitud;

    private EditText campoCantidad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_articulo);

        imagen = (ImageView) findViewById(R.id.image);
        idText = (TextView) findViewById(R.id.id);
        nombreText = (TextView) findViewById(R.id.nombre);
        descripcionText = (TextView) findViewById(R.id.description);
        categoriaText = (TextView) findViewById(R.id.categoria);
        tallaText = (TextView) findViewById(R.id.talla);
        stockText = (TextView) findViewById(R.id.stock);
        precioText = (TextView) findViewById(R.id.precio);
        composicionText = (TextView) findViewById(R.id.composicion);
        proveedorText = (TextView) findViewById(R.id.proveedor);
        botonSolicitar = (Button) findViewById(R.id.btnSolicitar);
        botonVender = (Button) findViewById(R.id.btnVender);
        SharedPreferences prefs = getSharedPreferences("PreferenciasUsuario", MODE_PRIVATE);
        nombreTienda = prefs.getString("nombre_tienda", "valor por defecto si no existe");
        campoCantidad = (EditText) findViewById(R.id.cantidadSolicitar);
        //Recogemos el id para poder buscar la prenda
        id_prenda = getIntent().getStringExtra("id_prenda");
        idText.setText(id_prenda);
        final String URLrequest="https://proyectotiendas1.azurewebsites.net/prenda?id="+id_prenda+"&tienda="+nombreTienda;
        final ProgressDialog dlg = ProgressDialog.show(
                this,
                "Obteniendo los datos REST",
                "Por favor, espere...", true);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,URLrequest, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dlg.dismiss();
                        try{
                            Toast.makeText(getApplicationContext(),
                                    "Prenda recibida de forma correcta",
                                    Toast.LENGTH_SHORT).show();
                            if(true){
                                //Se ha recibido una prenda
                                Toast.makeText(getApplicationContext(),
                                        "Prenda recibida de forma correcta",
                                        Toast.LENGTH_SHORT).show();

                                //Obtener objetos

                                nombreText.setText(response.getString("nombre"));
                                stockText.setText(response.getString("stock"));
                                composicionText.setText(response.getString("composicion"));
                                proveedorText.setText(response.getString("proveedor"));
                                tallaText.setText(response.getString("talla"));
                                categoriaText.setText(response.getString("categoria"));
                                precioText.setText(response.getString("precio"));
                                descripcionText.setText(response.getString("descripcion"));
                                Picasso.get().load(response.getString("imagen")).into(imagen);


                            } else {
                                //Problema en la previsión
                                Toast.makeText(getApplicationContext(),
                                        "No se ha recibido la prenda", Toast.LENGTH_SHORT).show();
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

        botonSolicitar.setOnClickListener(v->
                {
                    URLsolicitud="https://proyectotiendas1.azurewebsites.net/creasolicitud";
                    hacerSolicitud(URLsolicitud);

                }

        );
        botonVender.setOnClickListener(v->
                {
                    URLsolicitud="https://proyectotiendas1.azurewebsites.net/restar?id="+id_prenda+"&tienda="+nombreTienda;
                    hacerPeticion(URLsolicitud);

                }

        );


        // add the request object to the queue to be executed
        RESTapplication.getInstance().getRequestQueue().add(request);
    }

    private void hacerPeticion(String urlServidor) {
        final ProgressDialog dlg2 = ProgressDialog.show(
                this,
                "Realizando peticion",
                "Por favor, espere...", true);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, urlServidor,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dlg2.dismiss();
                        // Handle the response here
                        Log.d("DetalleArticulo", "Respuesta: " + response);
                        if(response.contains("Incrementado")) {
                            stockText.setText(String.valueOf(Integer.parseInt((String) stockText.getText()) + 1));
                        }else{
                            stockText.setText(String.valueOf(Integer.parseInt((String) stockText.getText()) - 1));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error cases here
                Log.e("DetalleArticulo", "Error: " + error.getMessage());
            }
        });
        RESTapplication.getInstance().getRequestQueue().add(stringRequest);
    }

    private void hacerSolicitud(String urlServidor) {

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("id", generadorID());
            jsonBody.put("id_prenda", id_prenda);
            jsonBody.put("origen", nombreTienda);
            jsonBody.put("cantidad", campoCantidad.getText().toString());


            final ProgressDialog dlg2 = ProgressDialog.show(
                    this,
                    "Realizando peticion",
                    "Por favor, espere...", true);
            final String mRequestBody = jsonBody.toString();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, urlServidor,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            dlg2.dismiss();
                            // Handle the response here
                            Log.d("DetalleArticulo", "Respuesta: " + response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Handle error cases here
                    Log.e("DetalleArticulo", "Error: " + error.getMessage());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Codificacion incorrecta intentando obtener los bytes de %s usando %s", mRequestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {

                        responseString = String.valueOf(response.statusCode);

                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };
            RESTapplication.getInstance().getRequestQueue().add(stringRequest);
        } catch (JSONException e) {
            Log.e("DetalleArticulo", "Error: " + e.getMessage());
        }
    }
    //Generador de IDs
    public static int generadorID() {
        Random random = new Random();
        return random.nextInt(900000) + 100000; // Generates a random number between 1000 and 9999
    }


}