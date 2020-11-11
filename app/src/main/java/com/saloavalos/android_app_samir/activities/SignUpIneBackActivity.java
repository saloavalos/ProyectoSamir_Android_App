package com.saloavalos.android_app_samir.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.saloavalos.android_app_samir.R;

import java.io.File;

public class SignUpIneBackActivity extends AppCompatActivity {

    private LinearLayout linear_layout_top_part;
    private Button btn_signup_continue;
    private TextView title;
    private ImageView iv_ine_reverso;

    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_ine_back);

        // statusbar text color; black
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // statusbar bg; set color
        Window window = SignUpIneBackActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.color_white));

        // Hooks
        linear_layout_top_part = findViewById(R.id.linear_layout_top_part);
        btn_signup_continue = findViewById(R.id.btn_signup_continue);
        title = findViewById(R.id.title);
        iv_ine_reverso = findViewById(R.id.iv_ine_reverso);


        btn_signup_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent signup_ine_front_activity = new Intent(getApplicationContext(), SignUpActivity.class);
//                startActivity(signup_ine_front_activity);
            }
        });



        // Get all values passed from a previous activity using Intent
//        Intent intent = getIntent();
//        Bitmap bitmap = (Bitmap) intent.getParcelableExtra("ine_frontal");
//        iv_ine_reverso.setImageBitmap(bitmap);



        String currentPhotoPath = getIntent().getStringExtra("ine_frontal");
        Log.d("BackIne", "onCreate: "+currentPhotoPath);
        File f = new File(currentPhotoPath);
        iv_ine_reverso.setImageURI(Uri.fromFile(f));
    }


    //--------------------------------------------------------------------
    // si vuelve a la activity anterior
    public void goBack(View view) {
        //finish();
        //To support reverse transitions when user clicks the device back button
        supportFinishAfterTransition();
    }

    @Override
    public void onBackPressed() {
        //To support reverse transitions when user clicks the device back button
        supportFinishAfterTransition();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //To support reverse transition when user clicks the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //--------------------------------------------------------------------
}