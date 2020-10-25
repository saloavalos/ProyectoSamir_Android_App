package com.saloavalos.android_app_samir.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.saloavalos.android_app_samir.R;

public class SignUpIneFrontActivity extends AppCompatActivity {

    private Button btn_signup_continue2;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_ine_front);

        // statusbar text color; black
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // statusbar bg; set color
        Window window = SignUpIneFrontActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(SignUpIneFrontActivity.this, R.color.color_white));

        // Hooks
        btn_signup_continue2 = findViewById(R.id.btn_signup_continue2);
        title = findViewById(R.id.title);


        btn_signup_continue2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup_ine_back_activity = new Intent(SignUpIneFrontActivity.this, SignUpIneBackActivity.class);

                //--------------------------------------------------------------------
                // shared transitions
                Pair[] pairs = new Pair[1];

                pairs[0] = new Pair<View, String>(title, "title_transition");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpIneFrontActivity.this, pairs);

                startActivity(signup_ine_back_activity, options.toBundle());
                //--------------------------------------------------------------------
            }
        });

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