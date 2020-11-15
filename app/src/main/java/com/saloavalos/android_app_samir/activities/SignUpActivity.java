package com.saloavalos.android_app_samir.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.saloavalos.android_app_samir.R;


public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "RegisterLoginActivity";

    private AutoCompleteTextView actv_role_user;
    private Button btn_signup_continue;
    private LinearLayout linear_layout_top_part;
    private TextView title;

    // Inputs
    private TextInputLayout et_name, et_apellido_paterno, et_apellido_materno, et_direccion, et_colonia, et_municipio, et_seccional, et_role_user, et_email, et_password, et_confirm_password;

    // Firebase - Firebase Authentication
    private FirebaseAuth firebaseAuth;

    // Check if email already exists
    boolean isEmailAlredyRegistered;


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

        //--------------------------------------------------------------------
        // Hooks
        actv_role_user = findViewById(R.id.actv_role_user);
        btn_signup_continue = findViewById(R.id.btn_signup_continue);
        linear_layout_top_part = findViewById(R.id.linear_layout_top_part);
        title = findViewById(R.id.title);

        // Firebase - Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();

        //------ Inputs
        et_name = findViewById(R.id.et_name);
        et_apellido_paterno = findViewById(R.id.et_apellido_paterno);
        et_apellido_materno = findViewById(R.id.et_apellido_materno);
        et_direccion = findViewById(R.id.et_direccion);
        et_colonia = findViewById(R.id.et_colonia);
        et_municipio = findViewById(R.id.et_municipio);
        et_seccional = findViewById(R.id.et_seccional);
        et_role_user = findViewById(R.id.et_role_user);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_confirm_password = findViewById(R.id.et_confirm_password);
        //--------------------------------------------------------------------


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


        // Continue to Sign up step 2
        btn_signup_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Ejecuta los Boolean methods para checar que los campos esten bien llenados
                //con este parametro de or "|" para ejecutar todos los metodos y que no se detenga en alguno
//                if (!validateName() | !validateApellidoPaterno() | !validateApellidoMaterno() | !validateDireccion() | !validateColonia() | !validateMunicipio() | !validateSeccional() | !validateRoleUser() | !validateEmail() | !validatePassword() | !validateConfirmPassword()) {
//                    return;     // si algun campo esta mal, omite el codigo restante
//                }

                // Get all the values to store in database
                String name = et_name.getEditText().getText().toString().trim();
                String apellido_paterno = et_apellido_paterno.getEditText().getText().toString().trim();
                String apellido_materno = et_apellido_materno.getEditText().getText().toString().trim();
                String direccion = et_direccion.getEditText().getText().toString().trim();
                String colonia = et_colonia.getEditText().getText().toString().trim();
                String municipio = et_municipio.getEditText().getText().toString().trim();
                String seccional = et_seccional.getEditText().getText().toString().trim();
                String role_user = et_role_user.getEditText().getText().toString().trim();
                String email = et_email.getEditText().getText().toString().trim();
                String password = et_password.getEditText().getText().toString().trim();


                //--------------------------------------------------------------------
                Intent intent = new Intent(getApplicationContext(), SignUpIneFrontActivity.class);

                // Pass all values to the next activity
                intent.putExtra("name", name);
                intent.putExtra("apellido_paterno", apellido_paterno);
                intent.putExtra("apellido_materno", apellido_materno);
                intent.putExtra("direccion", direccion);
                intent.putExtra("colonia", colonia);
                intent.putExtra("municipio", municipio);
                intent.putExtra("seccional", seccional);
                intent.putExtra("role_user", role_user);
                intent.putExtra("email", email);
                intent.putExtra("password", password);

                // shared transitions
                Pair[] pairs = new Pair[2];

                pairs[0] = new Pair<View, String>(linear_layout_top_part, "transition_main_title");
                pairs[1] = new Pair<View, String>(btn_signup_continue, "transition_btn_continue");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivity.this, pairs);

                startActivity(intent, options.toBundle());
                //--------------------------------------------------------------------



            }
        });


    }

    public void goBack(View view) {
        finish();
    }

    //--------------------------------------------------------------------
    // Methods to validate inputs
    private Boolean validateName () {
        String input = et_name.getEditText().getText().toString().trim();

        if (input.isEmpty()) {
            et_name.setError("Campo vacio");
            return false;
        } else {
            // si ya lo corrigio y esta bien el campo, quita el mensaje de error
            et_name.setError(null);
            et_name.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateApellidoPaterno () {
        String input = et_apellido_paterno.getEditText().getText().toString().trim();

        if (input.isEmpty()) {
            et_apellido_paterno.setError("Campo vacio");
            return false;
        } else {
            // si ya lo corrigio y esta bien el campo, quita el mensaje de error
            et_apellido_paterno.setError(null);
            et_apellido_paterno.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateApellidoMaterno () {
        String input = et_apellido_materno.getEditText().getText().toString().trim();

        if (input.isEmpty()) {
            et_apellido_materno.setError("Campo vacio");
            return false;
        } else {
            // si ya lo corrigio y esta bien el campo, quita el mensaje de error
            et_apellido_materno.setError(null);
            et_apellido_materno.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateDireccion () {
        String input = et_direccion.getEditText().getText().toString().trim();

        if (input.isEmpty()) {
            et_direccion.setError("Campo vacio");
            return false;
        } else {
            // si ya lo corrigio y esta bien el campo, quita el mensaje de error
            et_direccion.setError(null);
            et_direccion.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateColonia () {
        String input = et_colonia.getEditText().getText().toString().trim();

        if (input.isEmpty()) {
            et_colonia.setError("Campo vacio");
            return false;
        } else {
            // si ya lo corrigio y esta bien el campo, quita el mensaje de error
            et_colonia.setError(null);
            et_colonia.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateMunicipio () {
        String input = et_municipio.getEditText().getText().toString().trim();

        if (input.isEmpty()) {
            et_municipio.setError("Campo vacio");
            return false;
        } else {
            // si ya lo corrigio y esta bien el campo, quita el mensaje de error
            et_municipio.setError(null);
            et_municipio.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateSeccional () {
        String input = et_seccional.getEditText().getText().toString().trim();

        if (input.isEmpty()) {
            et_seccional.setError("Campo vacio");
            return false;
        } else {
            // si ya lo corrigio y esta bien el campo, quita el mensaje de error
            et_seccional.setError(null);
            et_seccional.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateRoleUser () {
        String input = et_role_user.getEditText().getText().toString().trim();

        if (input.isEmpty()) {
            et_role_user.setError("Campo vacio");
            return false;
        } else {
            // si ya lo corrigio y esta bien el campo, quita el mensaje de error
            et_role_user.setError(null);
            et_role_user.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateEmail () {
        String input = et_email.getEditText().getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        // this one is longer, Example: @school.udf.com
        String emailPatternLonger = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+";


        if (input.isEmpty()) {
            et_email.setError("Campo vacio");
            return false;
        } else if (!input.matches(emailPattern) && !input.matches(emailPatternLonger)) {
            et_email.setError("Email invalido");
            return false;
        } else {

            firebaseAuth.fetchSignInMethodsForEmail(input).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                @Override
                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                    Log.d(TAG,""+task.getResult().getSignInMethods().size());
                    if (task.getResult().getSignInMethods().size() == 0){
                        // email not existed
                        isEmailAlredyRegistered = false;
                    }else {
                        // email existed
                        et_email.setError("Correo no disponible");
                        isEmailAlredyRegistered = true;
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignUpActivity.this, "Hubo un problema al validar el email", Toast.LENGTH_SHORT).show();
                    // Dejo este boolean para que se detenga aqui y no continue con el registro
                    isEmailAlredyRegistered = true;
                    e.printStackTrace();
                }
            });

            if (isEmailAlredyRegistered) {
                return false;
            } else {
                // si esta bin el correo desde la primera vez
                // o si ya lo corrigio y esta bien el campo, quita el mensaje de error
                et_email.setError(null);
                et_email.setErrorEnabled(false);
                return true;
            }
        }
    }

    private Boolean validatePassword () {
        String input = et_password.getEditText().getText().toString().trim();
        String passwordVal = "^" +
                "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=_])" +    //at least 1 special character
                //"(?=\\S+$)" +           //no white spaces
                ".{8,}" +               //at least 8 characters
                "$";

        if (input.isEmpty()) {
            et_password.setError("Campo vacio");
            return false;
        } else if (!input.matches(passwordVal)) {
            et_password.setError("Contraseña no valida");
            return false;
        } else {
            // si ya lo corrigio y esta bien el campo, quita el mensaje de error
            et_password.setError(null);
            et_password.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateConfirmPassword () {
        String pass = et_password.getEditText().getText().toString().trim();
        String confirm_pass = et_confirm_password.getEditText().getText().toString().trim();
        String passwordVal = "^" +
                "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=_])" +    //at least 1 special character
                //"(?=\\S+$)" +           //no white spaces
                ".{8,}" +               //at least 8 characters
                "$";

        if (confirm_pass.isEmpty()) {
            et_confirm_password.setError("Campo vacio");
            return false;

        } else if (!confirm_pass.matches(passwordVal) && pass.equals(confirm_pass)) {
            et_confirm_password.setError("Contraseña no valida");
            return false;
        } else if (!pass.equals(confirm_pass)) {
            et_confirm_password.setError("Contraseña no coincide");
            return false;
        } else {
            // si ya lo corrigio y esta bien el campo, quita el mensaje de error
            et_confirm_password.setError(null);
            et_confirm_password.setErrorEnabled(false);
            return true;
        }
    }

    //--------------------------------------------------------------------
}
