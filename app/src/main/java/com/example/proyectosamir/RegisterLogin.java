package com.example.proyectosamir;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class RegisterLogin extends AppCompatActivity {

    private static final String TAG = "RegisterLoginActivity";
    EditText tNombre, tCorreo, tContraseña, tDireccion, tColonia, tSeccional, tMunicipio;
    Button tRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;

    // ¿porque lo pusiste en onCreate? // ¿va aqui no?
    FirebaseFirestore  fStore;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_login);

        //("t Es la variable,s bt es la etiqueta")

        tNombre = findViewById(R.id.btNombre);
        tCorreo = findViewById(R.id.btNombre);
        tContraseña = findViewById(R.id.btNombre);
        tDireccion = findViewById(R.id.btNombre);
        tColonia = findViewById(R.id.btNombre);
        tSeccional = findViewById(R.id.btNombre);
        tMunicipio = findViewById(R.id.btNombre);
        tRegisterBtn  = findViewById(R.id.registerBtn);
        //mLoginBtn = findViewById(R.id.CreateText);



        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);

        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        tRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = tCorreo.getText().toString().trim();
                String password = tContraseña.getText().toString().trim();
                String nombre = tNombre.getText().toString().trim();
                String direccion = tDireccion.getText().toString().trim();
                String colonia = tColonia.getText().toString().trim();
                String seccional = tSeccional.getText().toString().trim();
                String municipio = tMunicipio.getText().toString().trim();



                if(TextUtils.isEmpty(email)){
                    tCorreo.setError("Paso obligatorio para registrarse");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    tContraseña.setError("Paso obligatorio para registrarse");
                    return;
                }

                if (password.length()> 5 ){
                    tContraseña.setError("La contraseña debe contener al menos 5 caracteres");
                    return;
                }

                if(TextUtils.isEmpty(nombre)){
                    tNombre.setError("Paso obligatorio para registrarse");
                    return;
                }

                if(TextUtils.isEmpty(direccion)){
                    tDireccion.setError("Paso obligatorio para registrarse");
                    return;
                }

                if(TextUtils.isEmpty(colonia)){
                    tColonia.setError("Paso obligatorio para registrarse");
                    return;
                }

                if(TextUtils.isEmpty(seccional)){
                    tSeccional.setError("Paso obligatorio para registrarse");
                    return;
                }

                if(TextUtils.isEmpty(municipio)){
                    tMunicipio.setError("Paso obligatorio para registrarse");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //Registro de usuarios

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterLogin.this, "Usuario Creado.", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection( "users").document(userID);
                            Map<String,Object> user = new HashMap<>();

                            user.put("tNombre",tNombre);
                            user.put("tCorreo",tCorreo);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "El perfil de usuario ha sido creado"+ userID);
                                }
                            });

                            startActivity(new Intent(getApplicationContext(),MainActivity.class));

                        }else{
                            Toast.makeText(RegisterLogin.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();


                        }

                    }
                });

            }
        });






    }
}
