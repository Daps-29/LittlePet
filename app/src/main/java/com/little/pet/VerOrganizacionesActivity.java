package com.little.pet;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.little.pet.Utility.NetworkChangeListener;
import com.little.pet.model.OrganizacionDto;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class VerOrganizacionesActivity extends AppCompatActivity implements OnMapReadyCallback {

    String organizacionId;
    DatabaseReference organizacionInfo;
    TextView nombreOrganizacion, descripcionOrganizacion, horario, direccionLiteral;
    CircleImageView profile_image;
    ImageView fotoPortada;
    Double latitude, longitude;
    String nombremarcador, iddireccion;
    FloatingActionButton whatsapp;
    String celOrga, nombreOrga;

    private GoogleMap mMap;
    FloatingActionButton verGoogleMapsOrganizacion;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_organizacion);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.verde), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);

        //GOOGLE MAPS
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        verGoogleMapsOrganizacion = (FloatingActionButton) findViewById(R.id.verGoogleMapsOrganizacion);


        //OBTENER DATOS DE LA ANTERIOR ACTIVITY
        Intent intent = getIntent();

        organizacionId = intent.getStringExtra("id_organizacion");
//        iddireccion = intent.getStringExtra("direccion");
        organizacionInfo = FirebaseDatabase.getInstance().getReference("organizaciones").child(organizacionId);





        nombreOrganizacion = (TextView) findViewById(R.id.nombreOrganizacion);
        descripcionOrganizacion = (TextView) findViewById(R.id.descripcionOrganizacion);
        horario = (TextView) findViewById(R.id.horario);
        direccionLiteral = (TextView) findViewById(R.id.direccionLiteral);
        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        fotoPortada = (ImageView) findViewById(R.id.fotoPortadaOrganizacion);
        whatsapp = (FloatingActionButton) findViewById(R.id.whatsapp);

        //RECUPERAMOS INFORMACION PARA LLENAR EL ACTIVITY
        organizacionInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                OrganizacionDto organizacionDto = dataSnapshot.getValue(OrganizacionDto.class);
                nombreOrganizacion.setText(organizacionDto.getNombre());
                descripcionOrganizacion.setText(organizacionDto.getDescripcion());
                horario.setText(organizacionDto.getHoraen() + " - " + organizacionDto.getHorafin());
                direccionLiteral.setText(organizacionDto.getDireccion_literal());
                celOrga = organizacionDto.getContacto()+"";
                nombreOrga = organizacionDto.getNombre();
                Picasso.get().load(organizacionDto.getFoto()).placeholder(R.drawable.a).into(profile_image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("PICASSO ERROR", "onError: " + e);
                    }
                });
                Picasso.get().load(organizacionDto.getFoto_portada()).placeholder(R.drawable.a).fit().into(fotoPortada, new Callback() {
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
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //BOTON WHATSAPP
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String celOrganizacion = celOrga;
                if (celOrganizacion.substring(0, 3).equals("591")) {
                    celOrganizacion = celOrganizacion.substring(3);
                }

                if (celOrganizacion.substring(0, 4).equals("+591")) {
                    celOrganizacion = celOrganizacion.substring(4);
                }

                String mensaje = "Hola, queria mas información sobre" + nombreOrga + " ";
                String url = "https://wa.me/591" + celOrganizacion + "?text=" + mensaje;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        FirebaseDatabase.getInstance().getReference("organizaciones").child(organizacionId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            mMap = googleMap;
                            //VALIDAMOS Y RECUPERAMOS UBICACION
                            OrganizacionDto organizacionDto = dataSnapshot.getValue(OrganizacionDto.class);
                            latitude = Double.valueOf(organizacionDto.getLatitud());
                            longitude = Double.valueOf(organizacionDto.getLongitud());
                            mMap = googleMap;
                            mMap.getUiSettings().setMapToolbarEnabled(false);
                            mMap.getUiSettings();
                            nombreOrganizacion.setText(organizacionDto.getNombre());
                            nombremarcador = organizacionDto.getNombre();
                            //Agrega marcador de la ubicacion actual en el mapa
                            if(latitude != null && longitude != null){
                                verGoogleMapsOrganizacion.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(!latitude.equals("") || !longitude.equals("")){
                                            try {
                                                Intent intent = new Intent(Intent.ACTION_VIEW,
                                                        Uri.parse("geo:0,0?q="+ latitude +","+ longitude +" (Organizacion: " + nombreOrganizacion.getText().toString() + ")"));
                                                startActivity(intent);
                                            }catch (Exception e){
                                                Toast.makeText(getApplicationContext(), "Contacta con la organizacion para saber su ubicacion exacta", Toast.LENGTH_LONG).show();
                                            }

                                        }else{
                                            Toast.makeText(getApplicationContext(),"La organizacion no especificó su dirección exacta.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                //MARCAMOS EN EL MAPA LA UBICACION RECUPERADA
                                LatLng ubicacionActual = new LatLng(latitude, longitude);
                                mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource
                                        (R.drawable.ic_marcador)).anchor(0.0f , 1.0f).position(ubicacionActual).title(nombremarcador));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionActual, 15));

                            }else{
                                latitude = -16.5435752;
                                longitude = -68.0591915;
                                LatLng ubicacionLaPaz = new LatLng(latitude, longitude);
                                mMap.addMarker(new MarkerOptions().position(ubicacionLaPaz).title("Ubicación de la organizacion no registrada"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionLaPaz, 15));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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