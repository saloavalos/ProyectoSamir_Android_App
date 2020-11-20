package com.saloavalos.android_app_samir.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.saloavalos.android_app_samir.R;
import com.squareup.picasso.Picasso;

import javax.annotation.Nullable;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";
    private TextView tv_welcome_to_person;
    private ImageView iv_profile_photo;
    private TextInputEditText et_name, et_puesto, et_email;
    private Button btn_ver_mas;

    // Firebase - Firebase Authentication
    private FirebaseAuth firebaseAuth;

    // Firebase - Cloud Firestore
    private FirebaseFirestore firebaseFirestore;

    // Firebase - Cloud Storage
    private StorageReference storageReference;

    // User id, I'd use it when storing photos
    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // statusbar text color; black
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // statusbar bg; set color
        Window window = DashboardActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.color_white));

        // Hooks
        tv_welcome_to_person = findViewById(R.id.tv_welcome_to_person);
        iv_profile_photo = findViewById(R.id.iv_profile_photo);
        et_name = findViewById(R.id.et_name);
        et_puesto = findViewById(R.id.et_puesto);
        et_email = findViewById(R.id.et_email);
        btn_ver_mas = findViewById(R.id.btn_ver_mas);

        // Firebase - Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();
        // Firebase - Cloud Firestore
        firebaseFirestore = FirebaseFirestore.getInstance();
        // Firebase - Cloud Storage
        storageReference = FirebaseStorage.getInstance().getReference();

        //--------------------------------------------------------------------
        // Get data from firebase and asign it

        userID = firebaseAuth.getCurrentUser().getUid();

        // Image
        StorageReference profileRef = storageReference.child("users/" +firebaseAuth.getCurrentUser().getUid()+ "/foto_perfil.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(iv_profile_photo);
            }
        });

        // Data
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){

                    String genero = (documentSnapshot.getString("genero"));
                    String name = (documentSnapshot.getString("name"));


                    String welcome_messages;
                    if (genero.equals("Hombre")) {
                        welcome_messages = "Bienvenido " + name;
                    } else {
                        welcome_messages = "Bienvenida " + name;
                    }
                    tv_welcome_to_person.setText(welcome_messages);

                    et_name.setText(name);
                    et_puesto.setText(documentSnapshot.getString("puesto"));
                    et_email.setText(documentSnapshot.getString("email"));

                }else {
                    Log.d("tag", "onEvent: Document do not exists");
                }
            }
        });



        //--------------------------------------------------------------------


        btn_ver_mas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DashboardActivity.this, "Cargando informacion...", Toast.LENGTH_SHORT).show();
            }
        });


    }
}