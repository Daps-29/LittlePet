package com.little.pet.fragments;

import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.little.pet.R;
import com.little.pet.Utility.NetworkChangeListener;
import com.little.pet.adapter.OrganizacionAdapter;
import com.little.pet.model.DireccionDto;
import com.little.pet.model.OrganizacionDto;
import com.little.pet.model.RegisterHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrganizacionesFragment extends Fragment {

    RecyclerView rv_subcategoria;
    DatabaseReference database;
    OrganizacionAdapter organizacionAdapter;
    ArrayList<OrganizacionDto> organizacionDtoList;

    RecyclerView.LayoutManager layoutManager;
    String id;
    private FirebaseAuth mAuth;
    SwipeRefreshLayout swipeRefreshLayout;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_organizaciones, container, false);

        //Para la lista organizaciones
        rv_subcategoria = (RecyclerView) root.findViewById(R.id.rv_organizaciones);
        rv_subcategoria.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        rv_subcategoria.setLayoutManager(layoutManager);

        //ACCIONES PARA LA LISTA
        database = FirebaseDatabase.getInstance().getReference("organizaciones");

        organizacionDtoList = new ArrayList<>();
        organizacionAdapter = new OrganizacionAdapter(getContext(),organizacionDtoList);
        rv_subcategoria.setAdapter(organizacionAdapter);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser urs = mAuth.getCurrentUser();
        id = urs.getUid();

        //ACTUALIZAR LISTA
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateList();
            }
        });



        CargarOrganizaciones();

        return root;
    }

    private void updateList(){
        CargarOrganizaciones();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void CargarOrganizaciones() {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                organizacionDtoList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    OrganizacionDto organizacionDto = dataSnapshot.getValue(OrganizacionDto.class);

                    int estado = organizacionDto.getEstado_organizacion();

                    Location ubicacionOrganizacion = new Location("Ubicacion Organizacion");
                    ubicacionOrganizacion.setLatitude(Double.parseDouble(organizacionDto.getLatitud()));
                    ubicacionOrganizacion.setLongitude(Double.parseDouble(organizacionDto.getLongitud()));

                    if (estado == 1) {

                        FirebaseDatabase.getInstance().getReference("usuarios")
                                .child(id)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            RegisterHelper registerHelper = dataSnapshot.getValue(RegisterHelper.class);
                                            FirebaseDatabase.getInstance().getReference("direcciones")
                                                    .child(registerHelper.getDireccion())
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.exists()){
                                                                DireccionDto direccionDto = dataSnapshot.getValue(DireccionDto.class);
                                                                Location ubicacionUsuario = new Location("Ubicacion Usuario");
                                                                ubicacionUsuario.setLatitude(Double.parseDouble(direccionDto.getLatitud()));
                                                                ubicacionUsuario.setLongitude(Double.parseDouble(direccionDto.getLongitud()));

                                                                if (direccionDto.getLongitud() == null || direccionDto.getLatitud() == null){
                                                                    return;
                                                                }
                                                                float distance = ubicacionUsuario.distanceTo(ubicacionOrganizacion);
                                                                if (distance < 8000){
                                                                    organizacionDtoList.add(organizacionDto);
                                                                    organizacionAdapter.notifyDataSetChanged();
                                                                }else{
                                                                    return;
                                                                }
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
                organizacionAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        requireActivity().registerReceiver(networkChangeListener,filter);

        super.onStart();
    }

    @Override
    public void onStop() {
        requireActivity().unregisterReceiver(networkChangeListener);
        super.onStop();
    }


}
