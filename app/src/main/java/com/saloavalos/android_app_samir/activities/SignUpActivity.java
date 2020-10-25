package com.saloavalos.android_app_samir.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.saloavalos.android_app_samir.R;


public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "RegisterLoginActivity";

    private TextInputLayout ti_role_user;
    private AutoCompleteTextView actv_role_user;
    private Button btn_signup_continue1;
    private TextView title;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // statusbar text color; black
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // statusbar bg; set color
        Window window = SignUpActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(SignUpActivity.this, R.color.color_white));

        // hooks
        ti_role_user = findViewById(R.id.ti_role_user);
        actv_role_user = findViewById(R.id.actv_role_user);
        btn_signup_continue1 = findViewById(R.id.btn_signup_continue1);
        title = findViewById(R.id.title);


        //--------------------------------------------------------------------
        // Cargar texto de cada item en el dropdown "ti_role_user"
        String[] dropdown_items = new String[] {
                "Promotor",
                "Coordinador de zona",
                "Coordinador general"
        };

        ArrayAdapter<String> adapter_dropdown_item = new ArrayAdapter<>(
                SignUpActivity.this,
                R.layout.dropdown_item,
                dropdown_items
        );

        actv_role_user.setAdapter(adapter_dropdown_item);
        //--------------------------------------------------------------------


        btn_signup_continue1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup_ine_front_activity = new Intent(getApplicationContext(), SignUpIneFrontActivity.class);

                //--------------------------------------------------------------------
                // shared transitions
                Pair[] pairs = new Pair[1];

                pairs[0] = new Pair<View, String>(title, "title_transition");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivity.this, pairs);

                startActivity(signup_ine_front_activity, options.toBundle());
                //--------------------------------------------------------------------



            }
        });


    }

    public void goBack(View view) {
        finish();
    }
}
