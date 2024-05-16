package com.dam.stockfinder;


import android.app.Application;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RESTapplication extends Application {

        private static RESTapplication sInstance;
        private RequestQueue mRequestQueue;
        @Override
        public void onCreate() {
            super.onCreate();
            mRequestQueue = Volley.newRequestQueue(this);
            sInstance = this;
        }
        public synchronized static RESTapplication
        getInstance() {
            return sInstance;
        }
        public RequestQueue getRequestQueue() {
            return mRequestQueue;
        }


    }
