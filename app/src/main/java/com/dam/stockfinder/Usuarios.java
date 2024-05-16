package com.dam.stockfinder;

public class Usuarios {
    private String ciudad;
    private String nombre;
    private String password;



    public Usuarios(String ciudad, String nombre, String password) {
        this.ciudad = ciudad;
        this.nombre = nombre;
        this.password = password;
    }



    public String getCiudad() {
        return ciudad;
    }
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }


    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
