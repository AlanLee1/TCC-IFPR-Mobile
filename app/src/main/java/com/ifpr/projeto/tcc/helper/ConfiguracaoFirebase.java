package com.ifpr.projeto.tcc.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFirebase {private static DatabaseReference database;
    private static FirebaseAuth auth;
    private static StorageReference storage;

    //RETORNA A INSTANCIA DO DATABASE
    public static DatabaseReference getFirebase(){
        if( database == null ){
            database = FirebaseDatabase.getInstance().getReference();
        }
        return database;
    }

    //RETORNA A INSTANCIA DO FIREBASEAUTH
    public static FirebaseAuth getFirebaseAutenticacao(){
        if( auth == null ){
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    //RETORNA A INSTANCIA DO STORAGE
    public static StorageReference getFirebaseStorage(){

        if(storage == null){
            storage = FirebaseStorage.getInstance().getReference();
        }
        return storage;

    }

    //RETORNA DADOS DO USUARIO LOGADO
    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario  = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }

    //RETORNA O ID DO USUARIO LOGADO
    public static String getIdUsuario(){
        return getUsuarioAtual().getUid();
    }
}
