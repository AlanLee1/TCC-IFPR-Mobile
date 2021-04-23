package com.ifpr.projeto.tcc.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.ifpr.projeto.tcc.R;
import com.ifpr.projeto.tcc.helper.ConfiguracaoFirebase;
import com.ifpr.projeto.tcc.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText senha;
    private Button entrar;
    private ProgressBar progressBar;

    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        verificarUsuarioLogado();
        inicializarComponentes();

        //FAZER LOGIN DO USUARIO
        progressBar.setVisibility(View.GONE);
        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textoEmail = email.getText().toString();
                String textoSenha = senha.getText().toString();

                if(!textoEmail.isEmpty()){
                    if(!textoSenha.isEmpty()){
                        usuario = new Usuario();
                        usuario.setEmail(textoEmail);
                        usuario.setSenha(textoSenha);
                        validarLogin(usuario);

                    }else {
                        Toast.makeText(LoginActivity.this,
                                "Preencha a senha!",
                                Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(LoginActivity.this,
                            "Preencha o e-mail!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void verificarUsuarioLogado(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if(autenticacao.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MapaActivity.class));
            finish();
        }
    }

    //VALIDAR O LOGIN
    public void validarLogin(Usuario usuario){
        progressBar.setVisibility(View.VISIBLE);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(getApplicationContext(),MapaActivity.class));
                    finish();
                }else {
                    progressBar.setVisibility(View.GONE);

                    String erroExcecao = "";
                    try {
                        throw task.getException();
                    }catch ( FirebaseAuthInvalidUserException e){
                        erroExcecao = "Usuário não cadastrado!";
                    }catch ( FirebaseAuthInvalidCredentialsException e){
                        erroExcecao = "E-mail e senha não correspondem a nenhum cadastro!";
                    }catch ( Exception e){
                        erroExcecao = "Erro ao logar: " +e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this,
                            "Erro: "+ erroExcecao,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void abrirCadastro(View view){
        Intent i = new Intent(LoginActivity.this,CreateUserActivity.class);
        startActivity(i);
    }

    //INICIALIZAR TODOS OS COMPONENTES
    public void inicializarComponentes(){
        email = findViewById(R.id.editLoginEmail);
        senha = findViewById(R.id.editLoginSenha);
        entrar = findViewById(R.id.buttonLogar);
        progressBar = findViewById(R.id.progressBarLogin);

        email.requestFocus();
    }

}
