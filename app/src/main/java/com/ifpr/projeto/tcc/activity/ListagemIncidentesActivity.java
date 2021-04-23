package com.ifpr.projeto.tcc.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ifpr.projeto.tcc.R;
import com.ifpr.projeto.tcc.adapter.Adapter;
import com.ifpr.projeto.tcc.helper.ConfiguracaoFirebase;
import com.ifpr.projeto.tcc.model.Incidente;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.List;

public class ListagemIncidentesActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private DatabaseReference firebaseRef;
    private List<Incidente> listaIncidentes = new ArrayList<>();
    private RecyclerView recyclerView;
    private Adapter adapter;
    Incidente incidente = new Incidente();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listagem_incidentes);

        //INICIALIZAÇÃO INICIAL
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();

        //Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("ConsertaPalmas");
        setSupportActionBar(toolbar);

        //Configurar bottom navigation view
        configuraBottomNavigarionView();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //Configurações de objetos
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //Define adapter
        this.prepararListagem();
        Adapter adapter = new Adapter(listaIncidentes);

        //DEFINIR LAYOUT
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.setAdapter(adapter);

    }

    //PREENCHE OS ITENS PARA A LISTAGEM
    //ENVIAR A LISTA DE INCIDENTES CARREGADA PARA O ADAPTER
    public void prepararListagem(){

        firebaseRef = FirebaseDatabase.getInstance().getReference().child("incidentes");

        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaIncidentes.clear();
                for(DataSnapshot ds :dataSnapshot.getChildren()){
                    Incidente incidentez = ds.getValue(Incidente.class);
                    listaIncidentes.add(incidentez);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void configuraBottomNavigarionView(){

        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavigation);

        //Configurações iniciais dos botoes
        bottomNavigationViewEx.enableAnimation(true);
        bottomNavigationViewEx.enableItemShiftingMode(true);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(true);

        //Habilitar navegação
        habilitarNavegacao(bottomNavigationViewEx);
    }

    private void habilitarNavegacao(BottomNavigationViewEx viewEx){
        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch (item.getItemId()){

                    case R.id.ic_mapa:
                        startActivity(new Intent(getApplicationContext(), MapaActivity.class));
                        finish();
                        return true;

                    case R.id.ic_cadastrarIncidente:
                        startActivity(new Intent(getApplicationContext(), CadastrarIncidenteActivity.class));
                        finish();
                        return true;

                    case R.id.ic_listarIncidente:
                        startActivity(new Intent(getApplicationContext(), ListagemIncidentesActivity.class));
                        finish();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_sair:
                deslogarUsuario();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario(){
        try {
            autenticacao.signOut();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //INICIALIZAR TODOS OS COMPONENTES
    public void inicializarComponentes(){

        recyclerView = findViewById(R.id.recyclerView);

    }

    //MÉTODO CASO CLIQUE NO BOTÃO VOLTAR
    public void onBackPressed() {

        /*
        AlertDialog.Builder alert = new AlertDialog.Builder(this)
                .setTitle("Alerta")
                .setMessage("Deseja realmente sair?")
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = alert.create();
        dialog.show();

         */
    }
}
