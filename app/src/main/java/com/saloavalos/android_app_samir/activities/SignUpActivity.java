package com.saloavalos.android_app_samir.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.saloavalos.android_app_samir.R;


public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "RegisterLoginActivity";
    EditText tNombre, tCorreo, tContraseña, tDireccion, tColonia, tSeccional, tMunicipio;
    Button tRegisterBtn;
    TextView mLoginBtn;
    ProgressBar progressBar;


    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //("t Es la variable,s bt es la etiqueta")

//        tNombre = findViewById(R.id.btNombre);
//        tCorreo = findViewById(R.id.btNombre);
//        tContraseña = findViewById(R.id.btNombre);
//        tDireccion = findViewById(R.id.btNombre);
//        tColonia = findViewById(R.id.btNombre);
//        tSeccional = findViewById(R.id.btNombre);
//        tMunicipio = findViewById(R.id.btNombre);
//        tRegisterBtn  = findViewById(R.id.registerBtn);



//        tRegisterBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String email = tCorreo.getText().toString().trim();
//                String password = tContraseña.getText().toString().trim();
//
//                if(TextUtils.isEmpty(email)){
//                    tCorreo.setError("Paso obligatorio para registrarse");
//                    return;
//                }
//
//                if(TextUtils.isEmpty(password)){
//                    tContraseña.setError("Paso obligatorio para registrarse");
//                    return;
//                }
//
//                if (password.length()> 5 ){
//                    tContraseña.setError("La contraseña debe contener al menos 5 caracteres");
//                    return;
//                }
//
//
//            }
//        });


    }
}
