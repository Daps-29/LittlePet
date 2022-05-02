package com.little.pet;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.little.pet.Utility.NetworkChangeListener;
import com.little.pet.model.MascotaDto;
import com.little.pet.model.RegisterHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MascotaNuevaActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_IMAGE_REQUEST2 = 2;
    private static final int PICK_IMAGE_REQUEST3 = 3;
    private static final int STORAGE_PERMISION_CODE = 1;
    private static final int STORAGE_PERMISION_CODE2 = 2;
    private static final int STORAGE_PERMISION_CODE3 = 3;
    Button guardarmascota;
    ImageView imagen1,imagen2,imagen3;
    Uri foto1,foto2,foto3;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    private FirebaseAuth mFirebaseAuth;
    private EditText nombre,descripcion,ubicacion,edad,raza,vacuna;
    private Spinner estado,categoria,tiempo,sexo;
    private TextView mostrarfecha;
    private Button fecha;

    int dia,mes,ano;
    private int e;
    Dialog dialog;
    String id_foto;
    ProgressDialog progressDialog;
    private DatePickerDialog datePickerDialog;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    static boolean active = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mascota_nueva);
        storageReference = FirebaseStorage.getInstance().getReference("mascotas");
        databaseReference = FirebaseDatabase.getInstance().getReference("mascotas");
        verificarTelefonoUsuario();
        mFirebaseAuth = FirebaseAuth.getInstance();
        dialog = new Dialog(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.verde), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);


        nombre = findViewById(R.id.nombremasco);
        descripcion = findViewById(R.id.descripcionmascota);
        ubicacion = findViewById(R.id.ubicacionmascota);
        fecha = findViewById(R.id.fecha);
        edad = findViewById(R.id.edadmascota);
        raza = findViewById(R.id.raza);
        vacuna = findViewById(R.id.vacuna);
        estado = findViewById(R.id.etsadospinner);
        categoria = findViewById(R.id.categoriaspinner);
        tiempo = findViewById(R.id.tiempo);
        sexo = findViewById(R.id.sexomascota);
        mostrarfecha = findViewById(R.id.mostrarfecha);


        String[]Estado={"Adopción","Desaparecido"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,Estado);
        estado.setAdapter(adapter);

        String[]Categorias={"Perros","Gatos","Aves","Hamsters","Conejos"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,Categorias);
        categoria.setAdapter(adapter1);

        String[]Tiempo={"Semanas","Meses","Años"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,Tiempo);
        tiempo.setAdapter(adapter2);
        String[]Sexo={"Macho","Hembra"};
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,Sexo);
        sexo.setAdapter(adapter3);

        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                updateCalendar();
            }

            private void updateCalendar() {
                String format = "dd/MM/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
                mostrarfecha.setText(sdf.format(calendar.getTime()));
            }
        };
        fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MascotaNuevaActivity.this,date,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        guardarmascota = findViewById(R.id.guardarmascota);
        imagen1 = findViewById(R.id.imagen1);
        imagen2 = findViewById(R.id.imagen2);
        imagen3 = findViewById(R.id.imagen3);

        imagen1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MascotaNuevaActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                    abririmagen1();
                }else{
                    permisos();
                }
            }
        });
        imagen2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MascotaNuevaActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                    abririmagen2();
                }else{
                    permisos2();
                }
            }
        });
        imagen3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MascotaNuevaActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                    abririmagen3();
                }else{
                    permisos3();
                }
            }
        });

        //boton guardar
        guardarmascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                guardar();

            }
        });


    }
    public void abririmagen1(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }
    public void abririmagen2(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST2);
    }
    public void abririmagen3(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST3);
    }

    private String extension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    public void guardar(){

        String publicacion = estado.getSelectedItem().toString();
        String cate = categoria.getSelectedItem().toString();
        String tiem = tiempo.getSelectedItem().toString();
        String se = sexo.getSelectedItem().toString();
        String nom = nombre.getText().toString();
        String des = descripcion.getText().toString();
        String ubi = ubicacion.getText().toString();
        String fec = mostrarfecha.getText().toString();
        String ed = edad.getText().toString();
        //e = Integer.parseInt(edad.getText().toString());
        String ra = raza.getText().toString();
        String vacu = vacuna.getText().toString();

        int edaa = 1;
        String estadoeli = "1";
        int verificacion = 0;
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        id_foto = user.getUid();
        String idu = user.getUid();
        if ( !ed.isEmpty() && foto1 != null && foto2 != null && foto3 != null && !publicacion.isEmpty() && !cate.isEmpty() && !tiem.isEmpty() && !se.isEmpty() && !nom.isEmpty()
                && !des.isEmpty() && !ubi.isEmpty() && !fec.isEmpty() && !ra.isEmpty() && !vacu.isEmpty()) {

            progressDialog = new ProgressDialog(MascotaNuevaActivity.this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progresdialog);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." +
                    extension(foto1));
            StorageReference fileReference1 = storageReference.child(System.currentTimeMillis() + "." +
                    extension(foto2));
            StorageReference fileReference2 = storageReference.child(System.currentTimeMillis() + "." +
                    extension(foto3));
            fileReference.putFile(foto1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(@NonNull Uri uri1) {
                            final String fourl1 = String.valueOf(uri1);
                            fileReference1.putFile(foto2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    fileReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(@NonNull Uri uri2) {
                                        final String ur = uri2.toString();
                                         fileReference2.putFile(foto3).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                             @Override
                                             public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                 fileReference2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                     @Override
                                                     public void onSuccess(@NonNull Uri uri3) {
                                                         String url3 = uri3.toString();

                                                         String mascotaid = databaseReference.push().getKey();

                                                         MascotaDto mascotaDto = new MascotaDto(mascotaid, Integer.parseInt(ed), estadoeli, publicacion, tiem, verificacion, cate, fec, ra, se, fourl1, ur, url3, ubi, vacu, idu, nom, des);
                                                         databaseReference.child("").child(mascotaid).setValue(mascotaDto);
                                                         opendialog();

                                                     }
                                                 });
                                             }
                                         });
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MascotaNuevaActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }else {
            progressDialog = new ProgressDialog(MascotaNuevaActivity.this);
            progressDialog.dismiss();
            Toast.makeText(MascotaNuevaActivity.this, "Complete los campos", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData()!=null){
            foto1 = data.getData();
            Picasso.get().load(foto1).into(imagen1, new Callback() {
                @Override
                public void onSuccess() {
                }
                @Override
                public void onError(Exception e) {
                    Log.e("PICASSO ERROR", "onError: " + e);
                }
            });
            //imagen1.setImageURI(foto1);
        }
        if (requestCode == PICK_IMAGE_REQUEST2 && resultCode == RESULT_OK && data != null && data.getData()!=null){
            foto2 = data.getData();
            Picasso.get().load(foto2).into(imagen2, new Callback() {
                @Override
                public void onSuccess() {
                }
                @Override
                public void onError(Exception e) {
                    Log.e("PICASSO ERROR", "onError: " + e);
                }
            });
            //imagen1.setImageURI(foto1);
        }
        if (requestCode == PICK_IMAGE_REQUEST3 && resultCode == RESULT_OK && data != null && data.getData()!=null){
            foto3 = data.getData();
            Picasso.get().load(foto3).into(imagen3, new Callback() {
                @Override
                public void onSuccess() {
                }
                @Override
                public void onError(Exception e) {
                    Log.e("PICASSO ERROR", "onError: " + e);
                }
            });
            //imagen1.setImageURI(foto1);
        }
    }
    public void opendialog(){

        dialog.setContentView(R.layout.dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView btnclose = dialog.findViewById(R.id.closebtn);
        Button aceptar = dialog.findViewById(R.id.btnaceptar);

        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MascotaNuevaActivity.this, MainActivity.class));
            }
        });
        dialog.show();

    }
    @Override
    public void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);

        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
        active = false;
    }
    Dialog dialog1;
    private void verificarTelefonoUsuario(){

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(userId.length() > 0){
            FirebaseDatabase.getInstance().getReference("usuarios")
                    .child(userId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                RegisterHelper registerHelper = dataSnapshot.getValue(RegisterHelper.class);
                                if(registerHelper != null){
                                    if(registerHelper.getTelefono() == null || registerHelper.getTelefono().equals("")){

                                        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MascotaNuevaActivity.this);
                                        LayoutInflater inflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                                        final View customLayout = inflater.inflate(R.layout.alert, null);
                                        builder.setView(customLayout);
                                        TextView tituloAlerta = (TextView) customLayout.findViewById(R.id.tituloAlerta);
                                        tituloAlerta.setText("Número de telefono!");
                                        TextView mensajeAlerta = (TextView) customLayout.findViewById(R.id.textoAlerta);
                                        mensajeAlerta.setText("Para brindar una mejor experiencia debes agregar tu número de celular.");
                                        Button btnActualizar = (Button) customLayout.findViewById(R.id.btnActualizar);
                                        btnActualizar.setText("Agregar Número");
                                        btnActualizar.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog1.dismiss();
                                                Intent intent = new Intent(MascotaNuevaActivity.this, EditarUsuarioActivity.class);
                                                startActivity(intent);
                                            }
                                        });
                                        builder.setCancelable(false);
                                        if (active){
                                            dialog1 = builder.show();
                                        }
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(), "Por favor manten tu número de celular actualizado", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }

    }
    public void permisos(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this).setTitle("Permiso denegado").setMessage("El permiso a galeria fue denegado").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions(MascotaNuevaActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISION_CODE);
                }
            }).setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialog.dismiss();
                }
            }).create().show();
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISION_CODE);
        }

    }
    public void permisos2(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this).setTitle("Permiso denegado").setMessage("El permiso a galeria fue denegado").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions(MascotaNuevaActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISION_CODE2);
                }
            }).setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialog.dismiss();
                }
            }).create().show();
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISION_CODE2);
        }

    }
    public void permisos3(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this).setTitle("Permiso denegado").setMessage("El permiso a galeria fue denegado").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions(MascotaNuevaActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISION_CODE3);
                }
            }).setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialog.dismiss();
                }
            }).create().show();
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISION_CODE3);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISION_CODE){
            if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MascotaNuevaActivity.this, "Permiso Concedido", Toast.LENGTH_SHORT).show();
                abririmagen1();
            }else{
                Toast.makeText(MascotaNuevaActivity.this, "Permiso Denegado", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == STORAGE_PERMISION_CODE2){
            if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MascotaNuevaActivity.this, "Permiso Concedido", Toast.LENGTH_SHORT).show();
                abririmagen2();
            }else{
                Toast.makeText(MascotaNuevaActivity.this, "Permiso Denegado", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == STORAGE_PERMISION_CODE3){
            if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MascotaNuevaActivity.this, "Permiso Concedido", Toast.LENGTH_SHORT).show();
                abririmagen3();
            }else{
                Toast.makeText(MascotaNuevaActivity.this, "Permiso Denegado", Toast.LENGTH_SHORT).show();
            }
        }

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}