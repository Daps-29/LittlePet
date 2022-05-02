package com.little.pet;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.little.pet.Utility.NetworkChangeListener;
import com.little.pet.model.DireccionDto;
import com.little.pet.model.MascotaDto;
import com.little.pet.model.RegisterHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.DefaultSliderView;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MascotaDetalleActivity extends AppCompatActivity {
    CircleImageView imag;
    TextView nombreuser,edad,categoria,estado,nombremascotainfo,descipcion,raza,vacuna, DistanciaUbicacion,ubi;
    ImageView foto1,foto2,back;
    String mascotaid,iduser,nombremascota;
    DatabaseReference mascotainfo, infouser;
    FloatingActionButton whatsapp;
    FirebaseAuth mAuth;
    SliderLayout sliderLayout;
    String telf, id;
    String estadomasco;
    String estadomasco1;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_mascota_detalle);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.verde), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser urs = mAuth.getCurrentUser();
        id = urs.getUid();
        edad = findViewById(R.id.edadinfo);
        categoria = findViewById(R.id.categoriainfo);
        raza = findViewById(R.id.raza);
        vacuna = findViewById(R.id.vacunas);
        estado = findViewById(R.id.estadoinfo);
        nombremascotainfo = findViewById(R.id.nombremascotainfo);
        descipcion = findViewById(R.id.descripcioninfo);
        whatsapp = findViewById(R.id.telf);
        DistanciaUbicacion = findViewById(R.id.DistanciaUbicacion);
        ubi = findViewById(R.id.ubi);

        imag = findViewById(R.id.imagendetalle);
        nombreuser = findViewById(R.id.nombredetalleuser);
        back = findViewById(R.id.back);
        sliderLayout = findViewById(R.id.image_slider);
        sliderLayout.setIndicatorAnimation(IndicatorAnimations.WORM);
        sliderLayout.setPagerIndicatorVisibility(true);
        sliderLayout.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderLayout.setScrollTimeInSec(5);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MascotaDetalleActivity.this, com.little.pet.MainActivity.class));
            }
        });


        //OBTENER DATOS DE LA ANTERIOR ACTIVITY
        Intent intent = getIntent();
        iduser = intent.getStringExtra("user");
        mascotaid = intent.getStringExtra("id_mascota");
        mascotainfo = FirebaseDatabase.getInstance().getReference("mascotas").child(mascotaid);
        infouser = FirebaseDatabase.getInstance().getReference("usuarios").child(iduser);
        FirebaseUser user = mAuth.getCurrentUser();

        //RECUPERAMOS INFORMACION PARA LLENAR EL ACTIVITY
        infouser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RegisterHelper registerHelper = dataSnapshot.getValue(RegisterHelper.class);

                nombreuser.setText(registerHelper.getNombres() + " " + registerHelper.getApellidos());
                telf = registerHelper.getTelefono() + "";
                Picasso.get().load(registerHelper.getFoto()).placeholder(R.drawable.a).into(imag, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("PICASSO ERROR", "onError: " + e);
                    }
                });

                FirebaseDatabase.getInstance().getReference("direcciones")
                        .child(registerHelper.getDireccion())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    DireccionDto direccionDto = dataSnapshot.getValue(DireccionDto.class);
                                    Location ubicacionMascota = new Location("Ubicacion mascota");
                                    ubicacionMascota.setLatitude(Double.parseDouble(direccionDto.getLatitud()));
                                    ubicacionMascota.setLongitude(Double.parseDouble(direccionDto.getLongitud()));

                                    FirebaseDatabase.getInstance().getReference("usuarios")
                                            .child(id)
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        RegisterHelper registerHelper = dataSnapshot.getValue(RegisterHelper.class);
                                                        FirebaseDatabase.getInstance().getReference("direcciones")
                                                                .child(registerHelper.getDireccion())
                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                        if (dataSnapshot.exists()) {
                                                                            DireccionDto direccionDto = dataSnapshot.getValue(DireccionDto.class);
                                                                            Location ubicacionUsuario = new Location("Ubicacion Usuario");
                                                                            ubicacionUsuario.setLatitude(Double.parseDouble(direccionDto.getLatitud()));
                                                                            ubicacionUsuario.setLongitude(Double.parseDouble(direccionDto.getLongitud()));

                                                                            if (direccionDto.getLongitud() == null || direccionDto.getLatitud() == null) {
                                                                                return;
                                                                            }
                                                                            float distance = ubicacionMascota.distanceTo(ubicacionUsuario);
                                                                            double distancia = distance / 1000;
                                                                            distancia = Math.round(distancia * 100.0) / 100.0;
                                                                            DistanciaUbicacion.setText(String.valueOf(distancia) + " Km");

                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                    }
                                                                });

                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                }
                                            });

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mascotainfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MascotaDto mascotaDto = snapshot.getValue(MascotaDto.class);
                nombremascotainfo.setText(mascotaDto.getNombre());
                edad.setText(mascotaDto.getEdad() + " " + mascotaDto.getTiempo());
                categoria.setText(mascotaDto.getCategorias());
                descipcion.setText(mascotaDto.getDescripcion());
                estado.setText(mascotaDto.getSexo());
                raza.setText(mascotaDto.getRaza());
                vacuna.setText(mascotaDto.getVacuna());
                ubi.setText(mascotaDto.getUbicacion());
                estadomasco = mascotaDto.getEstadoperdida();
                estadomasco1 = mascotaDto.getEstado();
                if (estadomasco.equals("Desaparecido") || estadomasco.equals("Adopción")) {
                    whatsapp.setVisibility(View.VISIBLE);
                }
                if (estadomasco1.equals("Encontrado") || estadomasco1.equals("Adoptado")){
                    whatsapp.setVisibility(View.INVISIBLE);
                }
                for (int i = 0; i < 3; i++) {
                    DefaultSliderView sliderView = new DefaultSliderView(MascotaDetalleActivity.this);
                    switch (i) {
                        case 0:
                            sliderView.setImageUrl(mascotaDto.getFoto1());
                            break;
                        case 1:
                            sliderView.setImageUrl(mascotaDto.getFoto2());
                            break;
                        case 2:
                            sliderView.setImageUrl(mascotaDto.getFoto3());
                            break;
                    }
                    sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                    sliderLayout.addSliderView(sliderView);
                }


            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //BOTON WHATSAPP
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String telefono = telf;
                if (telefono.substring(0, 3).equals("591")) {
                    telefono = telefono.substring(3);
                }

                if (telefono.substring(0, 4).equals("+591")) {
                    telefono = telefono.substring(4);
                }
                if (estadomasco.equals("Desaparecido")){
                String mensaje = "Encontre a tu mascota: ";
                String url = "https://wa.me/591" + telefono + "?text=" + mensaje;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                }
                if (estadomasco.equals("Adopción")){
                    String mensaje = "Quiero adoptar a esa mascota: ";
                    String url = "https://wa.me/591" + telefono + "?text=" + mensaje;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
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
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}