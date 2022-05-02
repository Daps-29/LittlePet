package com.little.pet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.little.pet.Utility.NetworkChangeListener;
import com.little.pet.model.MascotaDto;
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

public class CambiarmascotaActivity extends AppCompatActivity {
    CircleImageView imag;
    TextView nombre,descripcion;
    Spinner estado;
    Button guardar;
    String idmascota,nombremascota,descripcionmascota;
    DatabaseReference mascotainfo;
    ProgressDialog progressDialog;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiarmascota);
        imag = findViewById(R.id.fotoestadp);
        nombre = findViewById(R.id.nombreestado);
        descripcion = findViewById(R.id.descripcionestado);
        estado = findViewById(R.id.Cambiar);
        guardar = findViewById(R.id.guardarestado);

        String[]Estado={"Adoptado","Encontrado"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,Estado);
        estado.setAdapter(adapter);

        //OBTENER DATOS DE LA ANTERIOR ACTIVITY
        Intent intent = getIntent();
        nombremascota = intent.getStringExtra("user");
        descripcionmascota = intent.getStringExtra("user");
        idmascota = intent.getStringExtra("id_mascota");
        mascotainfo = FirebaseDatabase.getInstance().getReference("mascotas").child(idmascota);

        mascotainfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MascotaDto mascotaDto = snapshot.getValue(MascotaDto.class);
                nombre.setText(mascotaDto.getNombre());
                descripcion.setText(mascotaDto.getDescripcion());
                Picasso.get().load(mascotaDto.getFoto1()).placeholder(R.drawable.a).into(imag, new Callback() {
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

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MascotaDto mascotaDto = new MascotaDto();
                String estadomascota = estado.getSelectedItem().toString();
                DatabaseReference base = FirebaseDatabase.getInstance().getReference("mascotas");
                Map<String,Object> actualizar = new HashMap<>();
                actualizar.put("estado",estadomascota);
                progressDialog = new ProgressDialog(CambiarmascotaActivity.this);
                progressDialog.show();
                progressDialog.setContentView(R.layout.progresdialog);
                startActivity(new Intent(CambiarmascotaActivity.this, MainActivity.class));
                Toast.makeText(CambiarmascotaActivity.this, "Cambiaste el estado de la mascota", Toast.LENGTH_SHORT).show();

                base.child(idmascota).updateChildren(actualizar);
            }
        });

    }
    @Override
    public void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);

        super.onStart();
    }

    @Override
    public void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}