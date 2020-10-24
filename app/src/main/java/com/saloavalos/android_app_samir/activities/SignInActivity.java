package com.saloavalos.android_app_samir.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.saloavalos.android_app_samir.R;

public class SignInActivity extends AppCompatActivity {

    private EditText name, password;
    private TextView tv_info;
    private Button btn_signin;
    private int counter = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        name = findViewById(R.id.etName);
        password = findViewById(R.id.etPassword);
        tv_info = findViewById(R.id.tv_info);
        btn_signin = findViewById(R.id.btn_signin);


        tv_info.setText("Numero de Intentos Restantes: 5");

        btn_signin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                validateInputs();
            }
        });

    }

    private void validateInputs (){
        // validar inputs
        String username = name.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if (username.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Campos vacios", Toast.LENGTH_SHORT).show();
            return; // esta algun campo vacio asi que se brinca el codigo restante de esta funcion
        }

        if ((username.equals("Admin")) && (pass.equals("1234"))) {
            Intent intent = new Intent(this, SecondActivity.class);
            startActivity(intent);
        } else {
            counter--;

            tv_info.setText("Numero de Intentos Restantes: " + String.valueOf(counter));

            if (counter == 0) {
                btn_signin.setEnabled(false);
            }
        }
    }
}