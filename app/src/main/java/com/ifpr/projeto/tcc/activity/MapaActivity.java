package com.ifpr.projeto.tcc.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ifpr.projeto.tcc.R;
import com.ifpr.projeto.tcc.helper.ConfiguracaoFirebase;
import com.ifpr.projeto.tcc.helper.Permissoes;
import com.ifpr.projeto.tcc.model.Incidente;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.List;
public class MapaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseAuth autenticacao;
    List<Incidente> incidentes = new ArrayList<>();
    private LocationManager locationManager;
    private LocationListener locationListener;
    private DatabaseReference incidentesRef;
    private String[] permissoes = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);


        //VALIDAR PERMISSOES
        Permissoes.validarPermissoes(permissoes, this, 1);

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

        //INICIALIZA O MAPA
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        incidentesRef = FirebaseDatabase.getInstance().getReference().child("incidentes");

        incidentesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Incidente incidente = ds.getValue(Incidente.class);
                    incidentes.add(incidente);

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

    //Esse método é chamado após o map ser carregado
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //LatLng abelardo = new LatLng(-26.568009, -52.329541);
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(abelardo,15));

        LatLng palmas = new LatLng(-26.483302, -51.992045);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(palmas,13));

        //Objeto responsavel por gerenciar a localização do usuario
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                for (Incidente incidente: incidentes){
                    Log.d("teste","tipo"+incidente.getTipo());
                    LatLng localincidente = new LatLng(incidente.getLatitude(), incidente.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(localincidente).title(incidente.getTipo())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    );

                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            locationManager.requestLocationUpdates(

                    LocationManager.GPS_PROVIDER,
                    1,
                    0,
                    locationListener
            );
        }

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

    //METODO DE PERMISSÃO
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults) {
            //PERMISSAO DENIED(NEGADA)
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                //ALERTA
                alertavalidacaopermissao();
            } else if (permissaoResultado == PackageManager.PERMISSION_GRANTED) {
                //RECUPERA LOCALIZAÇÃO DO USUARIO

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                    locationManager.requestLocationUpdates(

                            LocationManager.GPS_PROVIDER,
                            0,
                            0,
                            locationListener

                    );
                    return;
                }

            }
        }
    }
    public void alertavalidacaopermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }
}

