package com.little.pet;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.little.pet.dashboard.DrawerAdapter;
import com.little.pet.dashboard.DrawerItem;
import com.little.pet.dashboard.SimpleItem;
import com.little.pet.dashboard.SpaceItem;
import com.little.pet.fragments.DashBoardFragment;
import com.little.pet.fragments.LogrosFragment;
import com.little.pet.fragments.MisMascotasFragment;
import com.little.pet.fragments.MyProfileFragment;
import com.little.pet.fragments.OrganizacionesFragment;
import com.little.pet.model.DireccionDto;
import com.little.pet.model.RegisterHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {


    private static final int POS_CLOSE = 0;
    private static final int POS_DASHBOARD = 1;
    private static final int POS_MY_PROFILE = 2;
    private static final int POS_ORGANIZACIONES = 3;
    private static final int POS_MIS_MASCOTAS = 4;
    private static final int POS_LOGROS = 5;
    private static final int POS_SALIR = 7;
    public static String CHANNEL_ID = "101";

    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;
    FirebaseAuth firebaseAuth;
    private static final String TAG = "NOTIFICACION";
    static boolean active = false;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getApplicationContext().getSharedPreferences("ubicacion", Context.MODE_PRIVATE);
        verificarDireccionUsuario();
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        topic();

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {

                if (!task.isSuccessful()) {
                    Log.d(TAG, "FALLO OBTENER TOKEN");
                } else {
                    String token = task.getResult();
                    Log.d(TAG, "TOKEN ES " + token);
                }
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firebaseAuth = FirebaseAuth.getInstance();
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withDragDistance(180)
                .withRootViewScale(0.75f)
                .withRootViewElevation(25)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.drawer_menu)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_CLOSE),
                createItemFor(POS_DASHBOARD).setChecked(true),
                createItemFor(POS_MY_PROFILE),
                createItemFor(POS_ORGANIZACIONES),
                createItemFor(POS_MIS_MASCOTAS),
                createItemFor(POS_LOGROS),
                new SpaceItem(260),
                createItemFor(POS_SALIR)
        ));
        adapter.setListener(this);

        RecyclerView list = findViewById(R.id.drawer_list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(POS_DASHBOARD);
    }

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.fondo))
                .withTextTint(color(R.color.plomo))
                .withSelectedIconTint(color(R.color.fondo))
                .withSelectedTextTint(color(R.color.fondo));
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.id_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.id_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setIcon(getResources().getDrawable(R.drawable.as));
        builder.setTitle("AVISO");
        builder.setMessage("¿Desea salir de la Aplicación?");
        builder.setPositiveButton("Sí, salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                //finish();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    public void onItemSelected(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (position == POS_DASHBOARD) {
            DashBoardFragment dashBoardFragment = new DashBoardFragment();
            transaction.replace(R.id.container, dashBoardFragment);
            verificarDireccionUsuario();
        } else if (position == POS_MY_PROFILE) {
            MyProfileFragment myProfileFragment = new MyProfileFragment();
            transaction.replace(R.id.container, myProfileFragment);
            verificarDireccionUsuario();
        } else if (position == POS_ORGANIZACIONES) {
            OrganizacionesFragment organizacionesFragment = new OrganizacionesFragment();
            transaction.replace(R.id.container, organizacionesFragment);
            verificarDireccionUsuario();
        } else if (position == POS_MIS_MASCOTAS) {
            MisMascotasFragment misMascotasFragment = new MisMascotasFragment();
            transaction.replace(R.id.container, misMascotasFragment);
            verificarDireccionUsuario();
        } else if (position == POS_LOGROS) {
            LogrosFragment logrosFragment = new LogrosFragment();
            transaction.replace(R.id.container, logrosFragment);
            verificarDireccionUsuario();
        } else if (position == POS_SALIR) {
            firebaseAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));

        }

        slidingRootNav.closeMenu();
        transaction.addToBackStack(null);
        transaction.commit();
    }

    Dialog dialog;

    private void verificarDireccionUsuario() {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (userId.length() > 0) {
            FirebaseDatabase.getInstance().getReference("usuarios")
                    .child(userId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                RegisterHelper registerHelper = dataSnapshot.getValue(RegisterHelper.class);
                                if (registerHelper != null) {
                                    if (registerHelper.getDireccion() == null || registerHelper.getDireccion().equals("")) {
                                        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        final View customLayout = inflater.inflate(R.layout.alert, null);
                                        builder.setView(customLayout);
                                        TextView tituloAlerta = (TextView) customLayout.findViewById(R.id.tituloAlerta);
                                        tituloAlerta.setText("Dirección!");
                                        TextView mensajeAlerta = (TextView) customLayout.findViewById(R.id.textoAlerta);
                                        mensajeAlerta.setText("Agrega tu dirección para poder brindarte una mejor experiencia, te recomendamos siempre actualizar tu dirección ya que te mostramos las organizaciones mas cercanas a ti.");
                                        Button btnActualizar = (Button) customLayout.findViewById(R.id.btnActualizar);
                                        btnActualizar.setText("Agregar Dirección");
                                        btnActualizar.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dameubicacion();
                                            }
                                        });
                                        builder.setCancelable(false);
                                        if (active) {
                                            dialog = builder.show();
                                        }
                                    }
                                    else {
                                        if (sharedPref.getString(getResources().getString(R.string.latitud_usuario), null) == null) {
                                            try {
                                                FirebaseDatabase.getInstance().getReference("direcciones")
                                                        .child(registerHelper.getDireccion())
                                                        .addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.exists()) {
                                                                    DireccionDto direccionDto = dataSnapshot.getValue(DireccionDto.class);
                                                                    if (direccionDto.getLatitud() != null && !direccionDto.getLatitud().equals("")) {
                                                                        Location ubicacion_usuario = new Location("Ubicacion Usuario");
                                                                        ubicacion_usuario.setLatitude(Double.parseDouble(direccionDto.getLatitud()));
                                                                        ubicacion_usuario.setLongitude(Double.parseDouble(direccionDto.getLongitud()));
                                                                        sharedPref.edit().putString(getResources().getString(R.string.latitud_usuario), direccionDto.getLatitud()).apply();
                                                                        sharedPref.edit().putString(getResources().getString(R.string.longitud_usuario), direccionDto.getLongitud()).apply();
                                                                        sharedPref.edit().putString(getResources().getString(R.string.direccion_literal), direccionDto.getDireccionLiteral()).apply();
                                                                } else {
                                                                        Toast.makeText(getApplicationContext(), "Agrega una direccion valida", Toast.LENGTH_LONG).show();
                                                                       startActivity(new Intent(MainActivity.this, UbicacionActivity.class));
                                                                    }
                                                                } else {
                                                                    Toast.makeText(getApplicationContext(), "Agrega una direccion valida", Toast.LENGTH_LONG).show();
                                                                startActivity(new Intent(MainActivity.this, UbicacionActivity.class));
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                            } catch (Exception e) {
                                                Toast.makeText(getApplicationContext(), "Agrega una direccion valida", Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(MainActivity.this, UbicacionActivity.class));
                                            }
                                        } else {
                                            Location ubicacion_usuario = new Location("Ubicacion Usuario");
                                            String latitud = sharedPref.getString(getResources().getString(R.string.latitud_usuario), "-16.5205361");
                                            String longitud = sharedPref.getString(getResources().getString(R.string.longitud_usuario), "-68.1941181");
                                            if (latitud == null || longitud == null) {
                                                Toast.makeText(getApplicationContext(), "Agrega una direccion valida", Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(MainActivity.this, UbicacionActivity.class));
                                            }
                                            if (latitud == "" || longitud == "") {
                                                Toast.makeText(getApplicationContext(), "Agrega una direccion valida", Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(MainActivity.this, UbicacionActivity.class));
                                            }
                                            String alias = sharedPref.getString(getResources().getString(R.string.alias_direccion), null);
                                            String literal = sharedPref.getString(getResources().getString(R.string.direccion_literal), null);
                                            try {
                                                ubicacion_usuario.setLatitude(Double.parseDouble(latitud));
                                                ubicacion_usuario.setLongitude(Double.parseDouble(longitud));
                                            } catch (Exception e) {
                                                Toast.makeText(getApplicationContext(), "Agrega una direccion valida", Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(MainActivity.this, UbicacionActivity.class));
                                            }

                                        }


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

    private void dameubicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this, "Gracias", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            Intent intent = new Intent(MainActivity.this, UbicacionActivity.class);
            startActivity(intent);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notificacion";
            String description = "Recibir notificacion";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void topic(){
        FirebaseMessaging.getInstance().subscribeToTopic("todos")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        //Toast.makeText(MainActivity.this, "suscrito", Toast.LENGTH_SHORT).show();
                    }
                });
    }



}
