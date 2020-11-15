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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.saloavalos.android_app_samir.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SignUpIneBackActivity extends AppCompatActivity {

    private static final String TAG = "SignUpIneBackActivity";
    private LinearLayout linear_layout_top_part;
    private Button btn_signup_continue;
    private ImageView iv_ine_reverso;

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

    // To check result when trying to take or select image
    // We can give any value
    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_GALLERY_PHOTO = 2;

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
    private String ine_reverso_path;

    // An array to indicate options of the Alert Dialog
    private CharSequence[] alertDialogItems;

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
        iv_ine_reverso = findViewById(R.id.iv_ine_reverso);


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
        //--------------------------------------------------------------------


        // Defino los items del dialog
        alertDialogItems = new CharSequence[]{
                "Tomar foto",
                "Elegir foto"
        };

        iv_ine_reverso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(SignUpIneBackActivity.this);
                builder.setTitle("Opciones");
                builder.setItems(alertDialogItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {
                            requestPermissions();
                        }

                        if (which == 1) {
                            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(gallery, REQUEST_GALLERY_PHOTO);
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
                //--------------------------------------------------------------------
                Intent intent = new Intent(getApplicationContext(), SignUpProfilePictureActivity.class);

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
                intent.putExtra("ine_frente", ine_frente_path);
                // Incluye INE Reverso
                // paso la ruta donde esta la imagen, para que en una siguiente activity (donde se necesite la imagen), pueda crear un URI para generar una referencia de la imagen
                intent.putExtra("ine_reverso", ine_reverso_path);

                // shared transitions
                Pair[] pairs = new Pair[2];

                pairs[0] = new Pair<View, String>(linear_layout_top_part, "transition_main_title");
                pairs[1] = new Pair<View, String>(btn_signup_continue, "transition_btn_continue");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpIneBackActivity.this, pairs);

                startActivity(intent, options.toBundle());
                //--------------------------------------------------------------------
            }
        });

    }


    //--------------------------------------------------------------------
    // Metodos para tomar o escoger foto de galeria

    private void requestPermissions() {

        // guardo en booleans la respuesta de si el permiso esta concedido o no, para tener un acomodo mas limpio
        boolean isCameraGranted = ContextCompat.checkSelfPermission(SignUpIneBackActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean isStorageGranted = ContextCompat.checkSelfPermission(SignUpIneBackActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        //--------------------------------------------------------------------
        // if camera and storage permissions are not granted (both)
        if (!isCameraGranted && !isStorageGranted) {

            //--------------------------------------------------------------------
            // Both permissions were denied the first time, this dialog will appears since the second time
            if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpIneBackActivity.this, Manifest.permission.CAMERA)
                    && ActivityCompat.shouldShowRequestPermissionRationale(SignUpIneBackActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                new AlertDialog.Builder(SignUpIneBackActivity.this)
                    // TODO remplazar con un string
                    .setMessage("Es necesario el permiso de la camara y almacenamiento")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(SignUpIneBackActivity.this, both_permissions, ALL_PERMISSIONS_CODE);
                        }
                    })
                    .show();

                // First time both permissions are requested
            } else {
                ActivityCompat.requestPermissions(SignUpIneBackActivity.this, both_permissions, ALL_PERMISSIONS_CODE);
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
            if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpIneBackActivity.this, Manifest.permission.CAMERA)) {

                new AlertDialog.Builder(SignUpIneBackActivity.this)
                    // TODO remplazar con un string
                    .setMessage("Es necesario el permiso de la camara")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(SignUpIneBackActivity.this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                        }
                    })
                    .show();

            // First time camera permission was requested
            } else {
                ActivityCompat.requestPermissions(SignUpIneBackActivity.this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
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
            if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpIneBackActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                new AlertDialog.Builder(SignUpIneBackActivity.this)
                    // TODO remplazar con un string
                    .setMessage("Es necesario el permiso del almacenamiento")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(SignUpIneBackActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .show();

                // First time storage permission was requested
            } else {
                ActivityCompat.requestPermissions(SignUpIneBackActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
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
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(SignUpIneBackActivity.this, Manifest.permission.CAMERA)
                        && !ActivityCompat.shouldShowRequestPermissionRationale(SignUpIneBackActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        new AlertDialog.Builder(SignUpIneBackActivity.this)
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
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(SignUpIneBackActivity.this, Manifest.permission.CAMERA)) {
                        new AlertDialog.Builder(SignUpIneBackActivity.this)
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
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(SignUpIneBackActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        new AlertDialog.Builder(SignUpIneBackActivity.this)
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
                File f = new File(ine_reverso_path);
                iv_ine_reverso.setImageURI(Uri.fromFile(f));
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

        }

        else if (requestCode == REQUEST_GALLERY_PHOTO && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "." + getFileExt(contentUri);
                Log.d(TAG, "onActivityResult: Gallery Image Uri:  " + imageFileName);
                iv_ine_reverso.setImageURI(contentUri);

                btn_signup_continue.setEnabled(true);
            } else {

                Toast.makeText(this, "Hubo un problema, intenta de nuevo", Toast.LENGTH_SHORT).show();
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
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
        ine_reverso_path = image.getAbsolutePath();
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
    //--------------------------------------------------------------------


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
        //To support reverse transition when user clicks the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //--------------------------------------------------------------------
}