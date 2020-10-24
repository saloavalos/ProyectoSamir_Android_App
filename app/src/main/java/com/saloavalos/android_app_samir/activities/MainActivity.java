package com.saloavalos.android_app_samir.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.saloavalos.android_app_samir.R;

public class MainActivity extends AppCompatActivity {

    private Button btn_main_signin;
    private TextView tv_signin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_main_signin = findViewById(R.id.btn_main_signup);
        tv_signin = findViewById(R.id.tv_signin);

        btn_main_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup_activity = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(signup_activity);
            }
        });

        tv_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            }
        });
    }

}
