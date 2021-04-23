package com.ifpr.projeto.tcc.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.ifpr.projeto.tcc.R;
import com.ifpr.projeto.tcc.helper.ConfiguracaoFirebase;
import com.ifpr.projeto.tcc.model.Usuario;
import com.santalu.maskedittext.MaskEditText;

public class CreateUserActivity extends AppCompatActivity {

    private EditText nome;
    private MaskEditText telefone;
    private EditText email;
    private EditText senha;
    private EditText confirmarSenha;
    private Button cadastrar;
    private ProgressBar progressBar;

    private Usuario usuario;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        inicializarComponentes();

        //CADASTRAR USUÁRIO
        progressBar.setVisibility(View.GONE);
        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textoNome = nome.getText().toString();
                String textoTelefone = telefone.getText().toString();
                String textoEmail = email.getText().toString();
                String textoSenha = senha.getText().toString();
                String textoConfirmarSenha = confirmarSenha.getText().toString();

                if(!textoNome.isEmpty()){
                    if(!textoTelefone.isEmpty()){
                        if(!textoEmail.isEmpty()){
                            if(!textoSenha.isEmpty()){
                                if(!textoConfirmarSenha.isEmpty()){
                                    if(textoSenha.equals(textoConfirmarSenha)){

                                        usuario = new Usuario();
                                        usuario.setNome(textoNome);
                                        usuario.setTelefone(textoTelefone);
                                        usuario.setEmail(textoEmail);
                                        usuario.setSenha(textoSenha);
                                        cadastrar(usuario);

                                    }else {
                                        Toast.makeText(CreateUserActivity.this,
                                                "Senhas diferentes!",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Toast.makeText(CreateUserActivity.this,
                                            "Preencha o confirmar senha!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(CreateUserActivity.this,
                                        "Preencha a senha!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(CreateUserActivity.this,
                                    "Preencha o email!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(CreateUserActivity.this,
                                "Preencha o telefone!",
                                Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(CreateUserActivity.this,
                            "Preencha o nome!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //CADASTRAR USUARIO
    public void cadastrar(final Usuario usuario){

        progressBar.setVisibility(View.VISIBLE);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    try {
                        progressBar.setVisibility(View.GONE);

                        //Salvar os dados do usuario no Firebase
                        String idUsuario = task.getResult().getUser().getUid();
                        usuario.set_id(idUsuario);
                        usuario.salvar();

                        Toast.makeText(CreateUserActivity.this,
                                "Cadastro com sucesso",
                                Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),MapaActivity.class));
                        finish();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    progressBar.setVisibility(View.GONE);

                    String erroExcecao = "";
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        erroExcecao = "Digite uma senha mais forte!";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        erroExcecao = "Por favor, digite um e-mail válido!";
                    }catch (FirebaseAuthUserCollisionException e){
                        erroExcecao = "Esta conta já foi cadastrada!";
                    }catch (Exception e){
                        erroExcecao = "Erro ao cadastrar usuário: "+ e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(CreateUserActivity.this,
                            "Erro: "+ erroExcecao,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //INICIALIZAR TODOS OS COMPONENTES
    public void inicializarComponentes(){
        nome = findViewById(R.id.editCadastroNome);
        telefone = findViewById(R.id.editCadastroTelefone);
        email = findViewById(R.id.editCadastroEmail);
        senha = findViewById(R.id.editCadastroSenha);
        confirmarSenha = findViewById(R.id.editConfirmarSenha);
        cadastrar = findViewById(R.id.buttonCadastrar);
        progressBar = findViewById(R.id.progressBarCadastro);

        nome.requestFocus();
    }
}
