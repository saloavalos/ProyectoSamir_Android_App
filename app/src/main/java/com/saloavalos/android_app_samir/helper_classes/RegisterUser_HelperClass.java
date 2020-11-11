package com.saloavalos.android_app_samir.helper_classes;

public class RegisterUser_HelperClass {

    String name, apellido_paterno, apellido_materno, direccion, colonia, municipio, seccional, cargo_user, email, pass;

    // Generate empty constructor (genero el de algun String y le borro su contenido)
    public RegisterUser_HelperClass() {

    }

    // Generate Constructor
    public RegisterUser_HelperClass(String name, String apellido_paterno, String apellido_materno, String direccion, String colonia, String municipio, String seccional, String cargo_user, String email, String pass) {
        this.name = name;
        this.apellido_paterno = apellido_paterno;
        this.apellido_materno = apellido_materno;
        this.direccion = direccion;
        this.colonia = colonia;
        this.municipio = municipio;
        this.seccional = seccional;
        this.cargo_user = cargo_user;
        this.email = email;
        this.pass = pass;
    }

    // Generate Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getapellido_paterno() {
        return apellido_paterno;
    }

    public void setapellido_paterno(String apellido_paterno) {
        this.apellido_paterno = apellido_paterno;
    }

    public String getapellido_materno() {
        return apellido_materno;
    }

    public void setapellido_materno(String apellido_materno) {
        this.apellido_materno = apellido_materno;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getSeccional() {
        return seccional;
    }

    public void setSeccional(String seccional) {
        this.seccional = seccional;
    }

    public String getCargo_user() {
        return cargo_user;
    }

    public void setCargo_user(String cargo_user) {
        this.cargo_user = cargo_user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
