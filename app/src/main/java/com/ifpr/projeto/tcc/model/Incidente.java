package com.ifpr.projeto.tcc.model;

import com.google.firebase.database.DatabaseReference;
import com.ifpr.projeto.tcc.helper.ConfiguracaoFirebase;

public class Incidente {

    //Incidente
    private String _id;
    private String tipo;
    private String descricao;
    private String CaminhoImagem;
    private String Id_user;

    //Endereço
    private String bairro;
    private String logradouro;
    private Double latitude;
    private Double longitude;

    public Incidente() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference incidenteRef = firebaseRef.child("incidentes");
        String idIncidente = incidenteRef.push().getKey();
        set_id(idIncidente);
    }

    public Incidente(String tipo, String descricao, String caminhoImagem, String bairro, String logradouro) {
        this.tipo = tipo;
        this.descricao = descricao;
        CaminhoImagem = caminhoImagem;
        this.bairro = bairro;
        this.logradouro = logradouro;
    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference incidenteRef = firebaseRef.child("incidentes").child(get_id());
        incidenteRef.setValue(this);
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCaminhoImagem() {
        return CaminhoImagem;
    }

    public void setCaminhoImagem(String caminhoImagem) {
        CaminhoImagem = caminhoImagem;
    }

    public String getId_user() {
        return Id_user;
    }

    public void setId_user(String id_user) {
        Id_user = id_user;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
