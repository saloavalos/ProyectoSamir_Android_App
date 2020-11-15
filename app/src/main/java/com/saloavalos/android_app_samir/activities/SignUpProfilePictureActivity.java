package com.saloavalos.android_app_samir.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.saloavalos.android_app_samir.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignUpProfilePictureActivity extends AppCompatActivity {

    private static final String TAG = "ProfilePictureActivity";
    private LinearLayout linear_layout_top_part;
    private Button btn_signup_continue;
    private ImageView iv_profile_photo;

    // Firebase - Firebase Authentication
    private FirebaseAuth firebaseAuth;

    // Firebase - Cloud Firestore
    private FirebaseFirestore firebaseFirestore;

    // Firebase - Cloud Storage
    private StorageReference storageReference;

    // User id, I'd use it when creating the folder to store photos
    private String userID;

    // These are used to get all values passed from a previous activity using Intent
    private String name;
    private String apellido_paterno;
    private String apellido_materno;
    private String direccion;
    private String colonia;
    private String municipio;
    private String seccional;
    private String role_user;
    private String email;
    private String password;
    private String ine_frente_path;
    private String ine_reverso_path;

    // To check result when trying to take or select image
    // We can give any value
    private static final int REQUEST_TAKE_PHOTO = 0;

    // Defining Permission codes.
    // We can give any value
    // but unique for each permission.
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int ALL_PERMISSIONS_CODE = 102;

    // To ask all permissions in one dialog box
    private final String[] both_permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};

    // booleans para checar si acepto los permisos de camera y storage
    private boolean isCameraPermissionGranted = false;
    private boolean isStoragePermissionGranted = false;

    // To save where is located the photo taken with the camera
    private String profile_photo_path;

    // An array to indicate options of the Alert Dialog
    private CharSequence[] alertDialogItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_profile_picture);

        // statusbar text color; black
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // statusbar bg; set color
        Window window = SignUpProfilePictureActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.color_white));

        // Hooks
        linear_layout_top_part = findViewById(R.id.linear_layout_top_part);
        btn_signup_continue = findViewById(R.id.btn_signup_continue);
        iv_profile_photo = findViewById(R.id.iv_profile_photo);

        // Firebase - Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();
        // Firebase - Cloud Firestore
        firebaseFirestore = FirebaseFirestore.getInstance();
        // Firebase - Cloud Storage
        storageReference = FirebaseStorage.getInstance().getReference();


        //--------------------------------------------------------------------
        // Get all values passed from a previous activity using Intent
        name = getIntent().getStringExtra("name");
        apellido_paterno = getIntent().getStringExtra("apellido_paterno");
        apellido_materno = getIntent().getStringExtra("apellido_materno");
        direccion = getIntent().getStringExtra("direccion");
        colonia = getIntent().getStringExtra("colonia");
        municipio = getIntent().getStringExtra("municipio");
        seccional = getIntent().getStringExtra("seccional");
        role_user = getIntent().getStringExtra("role_user");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        ine_frente_path = getIntent().getStringExtra("ine_frente");
        ine_reverso_path = getIntent().getStringExtra("ine_reverso");
        //--------------------------------------------------------------------


        // Defino los items del dialog
        alertDialogItems = new CharSequence[]{
                "Tomar foto"
        };

        iv_profile_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(SignUpProfilePictureActivity.this);
                builder.setTitle("Opciones");
                builder.setItems(alertDialogItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {
                            requestPermissions();
                        }

                    }
                });
//                builder.setBackground(getResources().getDrawable(R.drawable.btn_white, null));
                builder.show();
            }
        });


        btn_signup_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            userID = firebaseAuth.getCurrentUser().getUid();
                            // Path, where to store user
                            DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);

                            // Create a new user with a first and last name
                            Map<String, Object> user = new HashMap<>();
                            user.put("name", name);
//                            user.put("apellido_paterno", apellido_paterno);
//                            user.put("apellido_materno", apellido_materno);
//                            user.put("direccion", direccion);
//                            user.put("colonia", colonia);
//                            user.put("municipio", municipio);
//                            user.put("seccional", seccional);
//                            user.put("role_user", role_user);

                            // Add the user
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: User created, ID: " + userID);
//                                    Toast.makeText(SignUpProfilePictureActivity.this, "Usuario registrado", Toast.LENGTH_SHORT).show();

                                    //--------------------------------------------------------------------
                                    // INE frente
                                    // creo una URI para generar una referencia de la imagen
                                    File file_ine_frente = new File(ine_frente_path);
                                    Uri uri_ine_frente = Uri.fromFile(file_ine_frente);

                                    uploadImageToFirebase("ine_frente.jpg", uri_ine_frente);
                                    //--------------------------------------------------------------------

                                    //--------------------------------------------------------------------
                                    // INE reverso
                                    // creo una URI para generar una referencia de la imagen
                                    File file_ine_reverso = new File(ine_reverso_path);
                                    Uri uri_ine_reverso = Uri.fromFile(file_ine_reverso);

                                    uploadImageToFirebase("ine_reverso.jpg", uri_ine_reverso);
                                    //--------------------------------------------------------------------

                                    //--------------------------------------------------------------------
                                    // Profile photo
                                    // creo una URI para generar una referencia de la imagen
                                    File file_profile_photo = new File(profile_photo_path);
                                    Uri uri_profile_photo = Uri.fromFile(file_profile_photo);

                                    uploadImageToFirebase("profile_photo.jpg", uri_profile_photo);
                                    //--------------------------------------------------------------------


//                                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
//                                    startActivity(intent);
                                }
                            });
                        }
                    }
                });
            }
        });

    }


    //--------------------------------------------------------------------
    // Metodos para tomar o escoger foto de galeria

    private void requestPermissions() {

        // guardo en booleans la respuesta de si el permiso esta concedido o no, para tener un acomodo mas limpio
        boolean isCameraGranted = ContextCompat.checkSelfPermission(SignUpProfilePictureActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean isStorageGranted = ContextCompat.checkSelfPermission(SignUpProfilePictureActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        //--------------------------------------------------------------------
        // if camera and storage permissions are not granted (both)
        if (!isCameraGranted && !isStorageGranted) {

            //--------------------------------------------------------------------
            // Both permissions were denied the first time, this dialog will appears since the second time
            if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpProfilePictureActivity.this, Manifest.permission.CAMERA)
                    && ActivityCompat.shouldShowRequestPermissionRationale(SignUpProfilePictureActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                new AlertDialog.Builder(SignUpProfilePictureActivity.this)
                    // TODO remplazar con un string
                    .setMessage("Es necesario el permiso de la camara y almacenamiento")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(SignUpProfilePictureActivity.this, both_permissions, ALL_PERMISSIONS_CODE);
                        }
                    })
                    .show();

                // First time both permissions are requested
            } else {
                ActivityCompat.requestPermissions(SignUpProfilePictureActivity.this, both_permissions, ALL_PERMISSIONS_CODE);
            }
            // Omitir el codigo restante, osea las opciones restantes
            return;

            // Both permissions were granted
        } else if (isCameraGranted && isStorageGranted) {
            dispatchTakePictureIntent();
        }
        //--------------------------------------------------------------------

        //--------------------------------------------------------------------
        // if camera permission is not granted
        if (!isCameraGranted) {

            // Camera permission was denied the first time, this dialog will appears since the second time
            if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpProfilePictureActivity.this, Manifest.permission.CAMERA)) {

                new AlertDialog.Builder(SignUpProfilePictureActivity.this)
                    // TODO remplazar con un string
                    .setMessage("Es necesario el permiso de la camara")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(SignUpProfilePictureActivity.this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                        }
                    })
                    .show();

                // First time camera permission was requested
            } else {
                ActivityCompat.requestPermissions(SignUpProfilePictureActivity.this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            }

            // Camera permission was granted
        } else {
            isCameraPermissionGranted = true;
        }

        //--------------------------------------------------------------------

        //--------------------------------------------------------------------
        // if storage permission is not granted
        if (!isStorageGranted){

            // Storage permission was denied the first time, this dialog will appears since the second time
            if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpProfilePictureActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                new AlertDialog.Builder(SignUpProfilePictureActivity.this)
                    // TODO remplazar con un string
                    .setMessage("Es necesario el permiso del almacenamiento")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(SignUpProfilePictureActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .show();

                // First time storage permission was requested
            } else {
                ActivityCompat.requestPermissions(SignUpProfilePictureActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            }

            // if storage permission was granted
        } else {
            isStoragePermissionGranted = true;
        }
        //--------------------------------------------------------------------

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case ALL_PERMISSIONS_CODE:

                int contador_permisos_checados = 0;
                int contador_permisos_permitidos = 0;

                for (int i = 0; i < permissions.length; i++) {

                    contador_permisos_checados += 1;

                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED){
                        contador_permisos_permitidos += 1;
                    }

                }

                // si se aceptaron todos los permisos
                if (contador_permisos_permitidos == permissions.length) {
                    dispatchTakePictureIntent();
                }

                // si se checaron todos los permisos pero
                if (contador_permisos_checados == permissions.length) {

                    // y todos fueron permanentemente denegados
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(SignUpProfilePictureActivity.this, Manifest.permission.CAMERA)
                            && !ActivityCompat.shouldShowRequestPermissionRationale(SignUpProfilePictureActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        new AlertDialog.Builder(SignUpProfilePictureActivity.this)
                            .setMessage("Para poder tomar la foto, permite a la aplicación el acceso a tu camara y almacenamiento. Configuraciones > Permisos, y permite la Camara y Almacenamiento.")
                            .setPositiveButton("Configuraciones", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    gotoApplicationSettings();
                                }
                            })
                            .setNegativeButton("Ahora no", null)
                            .setCancelable(false)
                            .show();
                    }

                }

                break;

            case CAMERA_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isCameraPermissionGranted = true;
                } else {
                    isCameraPermissionGranted = false;

                    //This block here means PERMANENTLY DENIED PERMISSION
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(SignUpProfilePictureActivity.this, Manifest.permission.CAMERA)) {
                        new AlertDialog.Builder(SignUpProfilePictureActivity.this)
                            .setMessage("Para poder tomar la foto, permite a la aplicación el acceso a tu camara. Configuraciones > Permisos, y permite la Camara.")
                            .setPositiveButton("Configuraciones", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    gotoApplicationSettings();
                                }
                            })
                            .setNegativeButton("Ahora no", null)
                            .setCancelable(false)
                            .show();

                    }
                }

                break;

            case STORAGE_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isStoragePermissionGranted = true;
                } else {
                    isStoragePermissionGranted = false;

                    //This block here means PERMANENTLY DENIED PERMISSION
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(SignUpProfilePictureActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        new AlertDialog.Builder(SignUpProfilePictureActivity.this)
                            .setMessage("Para poder tomar la foto, permite a la aplicación el acceso a tu almacenamiento. Configuraciones > Permisos, y permite el Almacenamiento.")
                            .setPositiveButton("Configuraciones", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    gotoApplicationSettings();
                                }
                            })
                            .setNegativeButton("Ahora no", null)
                            .setCancelable(false)
                            .show();
                    }
                }

                break;
        }

        if (isCameraPermissionGranted && isStoragePermissionGranted) {
            dispatchTakePictureIntent();
        }

    }


    private void gotoApplicationSettings() {

        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                File f = new File(profile_photo_path);
                iv_profile_photo.setImageURI(Uri.fromFile(f));
                Log.d(TAG, "Absolute Url of Image is " + Uri.fromFile(f));

                // Add the photo to a gallery - making it available in the Android Gallery application and to other apps.
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

                btn_signup_continue.setEnabled(true);

            } else {

                Toast.makeText(this, "Hubo un problema, intenta de nuevo", Toast.LENGTH_SHORT).show();
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        // se le agrega la fecha y hora para que el nombre de la foto se unico
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        profile_photo_path = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File...
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                // La foto ine frente
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.saloavalos.android_app_samir.fileprovider",
                        photoFile);

                // might be unnecessary
//                this.grantUriPermission("com.saloavalos.android_app_samir", photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    private void uploadImageToFirebase(final String name, Uri contentUri) {
        final ProgressBar p = findViewById(R.id.progressbar);
        p.setVisibility(View.VISIBLE);

        // Impide que se detecte algun click en la pantalla, me sirve para que el usuario no vuelve a dar click a un boton que ya se presiono o si hay algun proceso de registro/actualizacion de datos
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        final StorageReference image = storageReference.child("users/" + userID + "/"+ name);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                p.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "onSuccess: Uploaded Image URl is " + uri.toString());
                    }
                });

                Toast.makeText(getApplicationContext(), "Image (" + name + ") is uploaded.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                p.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                Toast.makeText(getApplicationContext(), "Upload of " + name + " failed.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //--------------------------------------------------------------------

    public void goBack(View view) {
        finish();
    }

}