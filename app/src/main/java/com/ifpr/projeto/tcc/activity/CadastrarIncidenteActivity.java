package com.ifpr.projeto.tcc.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ifpr.projeto.tcc.R;
import com.ifpr.projeto.tcc.helper.ConfiguracaoFirebase;
import com.ifpr.projeto.tcc.helper.Permissoes;
import com.ifpr.projeto.tcc.model.Incidente;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.io.ByteArrayOutputStream;

public class CadastrarIncidenteActivity extends AppCompatActivity implements OnMapReadyCallback {

    private EditText tipo;
    private EditText bairro;
    private EditText logradouro;
    private EditText descricao;
    private Button cadastrarIncidente;
    private Double latitudeG;
    private Double longitudeG;
    private ProgressBar progressBar;

    private ImageView imageFoto;
    Bitmap image = null;
    private ImageButton imageButtonCamera;
    private ImageButton imageButtonGaleria;
    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private GoogleMap mMap;
    private String[] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    private FirebaseAuth autenticacao;

    private Incidente incidente = new Incidente();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_incidente);

        inicializarComponentes();

        //Configurações iniciais
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();

        //Validar permissões
        Permissoes.validarPermissoes(permissoes, this, 1);

        //INICIALIZA O MAPA
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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

        //EVENTO DE CLIQUE NA CAMERA
        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i,SELECAO_CAMERA);
                }
            }
        });

        //EVENTO DE CLIQUE NA GALERIA
        imageButtonGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i,SELECAO_GALERIA);
                }
            }
        });

        //CADASTRAR USUÁRIO
        progressBar.setVisibility(View.GONE);

        cadastrarIncidente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textoTipo = tipo.getText().toString();
                String textoBairro = bairro.getText().toString();
                String textoLogradouro = logradouro.getText().toString();
                String textoDescricao = descricao.getText().toString();

                if(!textoTipo.isEmpty()){
                    if(!textoBairro.isEmpty()){
                        if(!textoLogradouro.isEmpty()){
                            if(!textoDescricao.isEmpty()){

                                String idUsuario = ConfiguracaoFirebase.getIdUsuario();

                                incidente.set_id(incidente.get_id());
                                incidente.setTipo(textoTipo);
                                incidente.setBairro(textoBairro);
                                incidente.setLogradouro(textoLogradouro);
                                incidente.setDescricao(textoDescricao);
                                incidente.setId_user(idUsuario);
                                incidente.setLatitude(latitudeG);
                                incidente.setLongitude(longitudeG);
                                //SALVAR DADOS
                                cadastrar(incidente);

                                tipo.setText("");
                                descricao.setText("");
                                bairro.setText("");
                                logradouro.setText("");
                                imageFoto.setImageResource(R.drawable.padrao);

                            }else {
                                Toast.makeText(CadastrarIncidenteActivity.this,
                                        "Preencha a descrição!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(CadastrarIncidenteActivity.this,
                                    "Preencha a rua!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(CadastrarIncidenteActivity.this,
                                "Preencha o bairro!",
                                Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(CadastrarIncidenteActivity.this,
                            "Preencha o tipo do incidente!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            try {
                switch (requestCode){
                    case SELECAO_CAMERA:
                        image = (Bitmap) data.getExtras().get("data");
                        break;
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(),localImagemSelecionada);
                        break;
                }

                if(image != null){
                    imageFoto.setImageBitmap(image);

                    //Recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    //Comprimir bitmap para um formato png/jpeg
                    image.compress(Bitmap.CompressFormat.JPEG, 70,baos);
                    //converte o baos para pixel brutos em uma matriz de bytes
                    //(dados da imagem)
                    byte[] dadosImagem = baos.toByteArray();

                    //Salvar imagem no storage
                    final StorageReference imagemRef = storageReference.child("incidentes").
                            child(incidente.get_id()+".jpeg");

                    //Retorna objeto que irá controlar upload
                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CadastrarIncidenteActivity.this,
                                    "Erro ao salvar a imagem, tente novamente",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            //Recuperar local da foto
                            imagemRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    incidente.setCaminhoImagem(uri.toString());
                                }

                            });
                            Toast.makeText(CadastrarIncidenteActivity.this,
                                    "Sucesso ao salvar a imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

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

        tipo = findViewById(R.id.editTipo);
        bairro = findViewById(R.id.editBairro);
        logradouro = findViewById(R.id.editLogradouro);
        descricao = findViewById(R.id.editDescricao);
        cadastrarIncidente = findViewById(R.id.buttonIncidente);
        progressBar = findViewById(R.id.progressBarIncidente);
        imageButtonCamera = findViewById(R.id.imageButtonCamera);
        imageButtonGaleria = findViewById(R.id.imageButtonGaleria);
        imageFoto = findViewById(R.id.imageView);

    }

    //CADASTRAR INCIDENTE
    public void cadastrar(final Incidente incidente){

        progressBar.setVisibility(View.VISIBLE);
        try{
            incidente.salvar();
            new AlertDialog.Builder(CadastrarIncidenteActivity.this)
                    .setTitle("")
                    .setMessage("Incidente Registrado com Sucesso.")
                    .setIcon(R.drawable.ifpr)
                    .show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getBaseContext(), MapaActivity.class));
                    finish();
                }
            }, 3000);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Esse método é chamado após o map ser carregado
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Objeto responsável por gerenciar a localização do usuário
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            //quando a localização do usuário muda
            @Override
            public void onLocationChanged(Location location) {
                Log.d("Localização","onLocationChanged: "+ location.toString());

                //capturando a latitude e longitude
                Double lat = location.getLatitude();
                Double lon = location.getLongitude();
                latitudeG = location.getLatitude();
                longitudeG = location.getLongitude();
                mMap.clear();
                LatLng localUsuario = new LatLng(lat, lon);
                mMap.addMarker(new MarkerOptions().position(localUsuario).title("Seu Local").
                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localUsuario,15));
            }

            //quando o status do serviço de localizaçao muda
            //(quando usuario ative a permissão para o serviço de localização
            //ele método sera chamado, caso ele n ative tbm
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            //quando o usuario habilita o serviço de localização
            @Override
            public void onProviderEnabled(String provider) {

            }

            //quando o usuario desabilita o serviço de localização
            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        //Atualiza a localização do usuário a cada 5 segundos ou 1 metro de distancia percorrido
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, locationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int permissãoResultado : grantResults) {
            if (permissãoResultado == PackageManager.PERMISSION_DENIED) {
                //ALERTA
                alertaValidacaoPermissao();
            } else if (permissãoResultado == PackageManager.PERMISSION_GRANTED) {
                //RECUPERAR LOCALIZAÇÃO DO USUARIO

                /*
                 * 1) Provedor da localização
                 * 2) Tempo mínimo entre atualizacões de localização (milesegundos)
                 * 3) Distancia mínima entre atualizacões de localização (metros)
                 * 4) Location listener (para recebermos as atualizações)
                 * */

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, locationListener);
                }
            }
        }
    }

    public void alertaValidacaoPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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
