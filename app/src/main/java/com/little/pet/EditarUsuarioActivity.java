package com.little.pet;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.little.pet.Utility.NetworkChangeListener;
import com.little.pet.model.RegisterHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarUsuarioActivity extends AppCompatActivity {
    EditText telefono;
    TextView nombre,correo,cambio,direccion;
    Spinner genero;
    CircleImageView perfil;
    String id;
    Button editar;
    String iduser;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_usuario);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");
        nombre = findViewById(R.id.nombreperfil);
        correo = findViewById(R.id.correoperfil);
        telefono = findViewById(R.id.editartelefono);
        genero = findViewById(R.id.editargenero);
        direccion =findViewById(R.id.editardireccion);
        perfil = findViewById(R.id.fotoperfil);
        editar = findViewById(R.id.editarperfil);
        cambio = findViewById(R.id.cambio);

        direccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dameubicacion();
                startActivity(new Intent(EditarUsuarioActivity.this, com.little.pet.UbicacionActivity.class));
            }
        });
        cambio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditarUsuarioActivity.this, com.little.pet.ResetPasswordActivity.class));
            }
        });
        String[]Estado={"Hombre","Mujer","Otro"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,Estado);
        genero.setAdapter(adapter);

        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(MyProfileFragment.this.getContext(), EditarUsuarioActivity.class));
                DatabaseReference base = FirebaseDatabase.getInstance().getReference("usuarios");
                String telef = telefono.getText().toString();
                String gen = genero.getSelectedItem().toString();
                Map<String,Object> update = new HashMap<>();
                update.put("telefono",telef);
                update.put("sexo",gen);
                base.child(id).updateChildren(update);
                progressDialog = new ProgressDialog(EditarUsuarioActivity.this);
                progressDialog.show();
                progressDialog.setContentView(R.layout.progresdialog);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
                startActivity(new Intent(EditarUsuarioActivity.this, com.little.pet.MainActivity.class));
                Toast.makeText(EditarUsuarioActivity.this, "Editado Con exito", Toast.LENGTH_SHORT).show();
            }
        });


        FirebaseUser urs = mAuth.getCurrentUser();
        id = urs.getUid();

        databaseReference.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RegisterHelper registerHelper = snapshot.getValue(RegisterHelper.class);
                String name = registerHelper.getNombres();
                nombre.setText(name+" "+registerHelper.getApellidos());
                correo.setText(registerHelper.getCorreo());
                telefono.setText(registerHelper.getTelefono());
                //genero.setText(registerHelper.getSexo());
                Picasso.get().load(registerHelper.getFoto()).placeholder(R.drawable.a).into(perfil, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("PICASSO ERROR", "onError: " + e);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void dameubicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(EditarUsuarioActivity.this, com.little.pet.UbicacionActivity.class);
            startActivity(intent);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }



}