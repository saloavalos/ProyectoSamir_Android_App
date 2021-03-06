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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.saloavalos.android_app_samir.R;
import com.saloavalos.android_app_samir.helper_classes.RegisterUser_HelperClass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class SignUpIneFrontActivity extends AppCompatActivity {

    private static final String TAG = "SignUpIneFrontActivity";
    private LinearLayout linear_layout_top_part;
    private Button btn_signup_continue;
    private ImageView iv_ine_frente;

    // Firebase - Firebase Authentication
    private FirebaseAuth firebaseAuth;

    // Firebase - Cloud Storage
    private StorageReference storageReference;

    // User id, I'd use it when storing photos
    private String userID;

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
    private String currentPhotoPath;
    private Bitmap image;

    // An array to indicate options of the Alert Dialog
    private CharSequence[] alertDialogItems;


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
        linear_layout_top_part = findViewById(R.id.linear_layout_top_part);
        btn_signup_continue = findViewById(R.id.btn_signup_continue);
        iv_ine_frente = findViewById(R.id.iv_ine_frente);


        // Firebase - Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();
        // Firebase - Cloud Storage
        storageReference = FirebaseStorage.getInstance().getReference();




        // Defino los items del dialog
        alertDialogItems = new CharSequence[]{
                "Tomar foto",
                "Elegir foto"
        };

        iv_ine_frente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(SignUpIneFrontActivity.this);
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

                final ProgressBar p = findViewById(R.id.progressbar);
                p.setVisibility(View.VISIBLE);

                // Impide que se detecte algun click en la pantalla, me sirve para que el usuario no vuelve a dar click a un boton que ya se presiono o si hay algun proceso de registro/actualizacion de datos
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                File file_current_photo = new File(currentPhotoPath);
                Uri uri_current_photo = Uri.fromFile(file_current_photo);

                userID = firebaseAuth.getCurrentUser().getUid();

                final StorageReference image = storageReference.child("users/" + userID + "/" + "ine_frente.jpg");
                image.putFile(uri_current_photo).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        // Hide progress bar and allow clicks in window
                        p.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        //--------------------------------------------------------------------
                        Intent intent = new Intent(getApplicationContext(), SignUpIneBackActivity.class);

                        // shared transitions
                        Pair[] pairs = new Pair[2];

                        pairs[0] = new Pair<View, String>(linear_layout_top_part, "transition_main_title");
                        pairs[1] = new Pair<View, String>(btn_signup_continue, "transition_btn_continue");

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpIneFrontActivity.this, pairs);

                        startActivity(intent, options.toBundle());
                        //--------------------------------------------------------------------

//                        image1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                Log.d(TAG, "onSuccess: Uploaded Image URl is " + uri.toString());
//                            }
//                        });

                        Toast.makeText(getApplicationContext(), "Image uploaded.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        // Hide progress bar and allow clicks in window
                        p.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        Toast.makeText(getApplicationContext(), "Upload failed.", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

    }

    //--------------------------------------------------------------------
    // Metodos para tomar o escoger foto de galeria

    private void requestPermissions() {

        // guardo en booleans la respuesta de si el permiso esta concedido o no, para tener un acomodo mas limpio
        boolean isCameraGranted = ContextCompat.checkSelfPermission(SignUpIneFrontActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean isStorageGranted = ContextCompat.checkSelfPermission(SignUpIneFrontActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        //--------------------------------------------------------------------
        // if camera and storage permissions are not granted (both)
        if (!isCameraGranted && !isStorageGranted) {

            //--------------------------------------------------------------------
            // Both permissions were denied the first time, this dialog will appears since the second time
            if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpIneFrontActivity.this, Manifest.permission.CAMERA)
                    && ActivityCompat.shouldShowRequestPermissionRationale(SignUpIneFrontActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                new AlertDialog.Builder(SignUpIneFrontActivity.this)
                    // TODO remplazar con un string
                    .setMessage("Es necesario el permiso de la camara y almacenamiento")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(SignUpIneFrontActivity.this, both_permissions, ALL_PERMISSIONS_CODE);
                        }
                    })
                    .show();

                // First time both permissions are requested
            } else {
                ActivityCompat.requestPermissions(SignUpIneFrontActivity.this, both_permissions, ALL_PERMISSIONS_CODE);
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
            if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpIneFrontActivity.this, Manifest.permission.CAMERA)) {

                new AlertDialog.Builder(SignUpIneFrontActivity.this)
                    // TODO remplazar con un string
                    .setMessage("Es necesario el permiso de la camara")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(SignUpIneFrontActivity.this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                        }
                    })
                    .show();

            // First time camera permission was requested
            } else {
                ActivityCompat.requestPermissions(SignUpIneFrontActivity.this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
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
            if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpIneFrontActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                new AlertDialog.Builder(SignUpIneFrontActivity.this)
                    // TODO remplazar con un string
                    .setMessage("Es necesario el permiso del almacenamiento")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(SignUpIneFrontActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .show();

                // First time storage permission was requested
            } else {
                ActivityCompat.requestPermissions(SignUpIneFrontActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
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
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(SignUpIneFrontActivity.this, Manifest.permission.CAMERA)
                        && !ActivityCompat.shouldShowRequestPermissionRationale(SignUpIneFrontActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        new AlertDialog.Builder(SignUpIneFrontActivity.this)
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
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(SignUpIneFrontActivity.this, Manifest.permission.CAMERA)) {
                        new AlertDialog.Builder(SignUpIneFrontActivity.this)
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
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(SignUpIneFrontActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        new AlertDialog.Builder(SignUpIneFrontActivity.this)
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
                File f = new File(currentPhotoPath);
                iv_ine_frente.setImageURI(Uri.fromFile(f));
                Log.d(TAG, "Absolute Url of Image is " + Uri.fromFile(f));

                // Add the photo to a gallery - making it available in the Android Gallery application and to other apps.
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

                //uploadImageToFirebase(f.getName(), contentUri);

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
                iv_ine_frente.setImageURI(contentUri);

                //uploadImageToFirebase(imageFileName, contentUri);

                btn_signup_continue.setEnabled(true);
            } else {

                Toast.makeText(this, "Hubo un problema, intenta de nuevo", Toast.LENGTH_SHORT).show();
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    // es otro metodo de subir imagen, pero lo mas seguro que no lo use
//    private void uploadImage() {
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//
//        final String random = UUID.randomUUID().toString();
//        StorageReference imageRef = storageReference.child("images/" + random);
//
//        byte[] b = stream.toByteArray();
//        imageRef.putBytes(b)
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                Uri downloadUri = uri;
//                            }
//                        });
//
//                        Toast.makeText(getApplicationContext(), "Photo Uploaded", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                        Toast.makeText(getApplicationContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }





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
        currentPhotoPath = image.getAbsolutePath();
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
        Toast.makeText(getApplicationContext(),"There is no back action",Toast.LENGTH_LONG).show();
    }


    @Override
    public void onBackPressed() {
        //To support reverse transitions when user clicks the device back button
//        supportFinishAfterTransition();
        // super.onBackPressed();
        Toast.makeText(getApplicationContext(),"There is no back action",Toast.LENGTH_LONG).show();
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