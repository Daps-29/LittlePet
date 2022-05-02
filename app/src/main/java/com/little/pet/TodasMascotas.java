package com.little.pet;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.little.pet.Utility.NetworkChangeListener;
import com.little.pet.adapter.CategoriesAdapter;
import com.little.pet.adapter.TodasMascotasAdapter;
import com.little.pet.categorias.RecyclerItemClickListener;
import com.little.pet.model.CategoriasDto;
import com.little.pet.model.MascotaDto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TodasMascotas extends AppCompatActivity {

    ArrayList<CategoriasDto> categoriesAdapterArrayList;
    private CategoriesAdapter categoriesAdapter;
    private RecyclerView rv_categorias;
    RecyclerView.LayoutManager categories_layoutManager;


    RecyclerView rv_mascotas;
    DatabaseReference database;
    private DatabaseReference databaseReference;
    TodasMascotasAdapter todasMascotasAdapter;
    ArrayList<MascotaDto> mascotaDtoArrayList;
    RecyclerView.LayoutManager layoutManager;
    ConstraintLayout progress_bar;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout listavacia;
    SearchView searchView;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todas_mascotas);

        progress_bar = (ConstraintLayout) findViewById(R.id.progress_bar);
        listavacia = (LinearLayout) findViewById(R.id.listavacia);
        searchView = (SearchView) findViewById(R.id.search_mascota);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.verde), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);

        //CATEGORIAS MASCOTAS
        rv_categorias = (RecyclerView) findViewById(R.id.rv_categorias);
        //rv_categorias.setHasFixedSize(true);
        categories_layoutManager = new LinearLayoutManager(TodasMascotas.this, LinearLayoutManager.HORIZONTAL, false);
        rv_categorias.setLayoutManager(categories_layoutManager);

        categoriesAdapterArrayList = new ArrayList<>();
        CategoriasDto categoria1 = new CategoriasDto(0,"Gatos", getResources().getDrawable(R.drawable.catulti));
        CategoriasDto categoria2 = new CategoriasDto(1,"Perros", getResources().getDrawable(R.drawable.dogulti));
        CategoriasDto categoria3 = new CategoriasDto(2,"Conejos", getResources().getDrawable(R.drawable.rabbitulti));
        CategoriasDto categoria4 = new CategoriasDto(3,"Aves", getResources().getDrawable(R.drawable.birdulto));
        CategoriasDto categoria5 = new CategoriasDto(4,"Hamsters", getResources().getDrawable(R.drawable.hamsterulti));
        categoriesAdapterArrayList.add(categoria1);
        categoriesAdapterArrayList.add(categoria2);
        categoriesAdapterArrayList.add(categoria3);
        categoriesAdapterArrayList.add(categoria4);
        categoriesAdapterArrayList.add(categoria5);

        categoriesAdapter = new CategoriesAdapter(categoriesAdapterArrayList);
        rv_categorias.setAdapter(categoriesAdapter);

        rv_categorias.addOnItemTouchListener(new RecyclerItemClickListener(this, rv_categorias, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                CategoriasDto categoriasDto = categoriesAdapterArrayList.get(position);
                Intent intent = new Intent(TodasMascotas.this, CategoriasActivity.class);
                intent.putExtra("idCategoria", categoriasDto.getId_categoria()+"");
                intent.putExtra("nombreCategoria", categoriasDto.getNombre_categoria());
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));


        //LISTA PRINCIPAL
        rv_mascotas = (RecyclerView) findViewById(R.id.rv_mascotas);
        //rv_mascotas.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(TodasMascotas.this, LinearLayoutManager.VERTICAL, false);
        rv_mascotas.setLayoutManager(layoutManager);
        //ACCIONES PARA LA LISTA
        database = FirebaseDatabase.getInstance().getReference("mascotas");
        mascotaDtoArrayList = new ArrayList<>();
        todasMascotasAdapter = new TodasMascotasAdapter(TodasMascotas.this, mascotaDtoArrayList);
        rv_mascotas.setAdapter(todasMascotasAdapter);

        //BUSACADOR
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });


        //ACTUALIZAR LISTA
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateList();
            }
        });

        updateMascotasList();
    }

    private void filter(String newText) {
        ArrayList<MascotaDto> filteredList = new ArrayList<>();
        for (MascotaDto item : mascotaDtoArrayList){
            if (item.getNombre().toLowerCase().contains(newText.toLowerCase())){
                filteredList.add(item);
            }
        }
        todasMascotasAdapter.filterList(filteredList);
    }

    private void updateList(){
        updateMascotasList();
        swipeRefreshLayout.setRefreshing(false);
    }
    
    private void updateMascotasList(){
        progress_bar.setVisibility(View.VISIBLE);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mascotaDtoArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MascotaDto mascotaDto = dataSnapshot.getValue(MascotaDto.class);

                    if (mascotaDto.getVerificacion() == 1 && mascotaDto.getEstado().equals("1")) {
                        progress_bar.setVisibility(View.GONE);
                        listavacia.setVisibility(View.GONE);
                        mascotaDtoArrayList.add(mascotaDto);

                    }
                }
                if (mascotaDtoArrayList.size() == 0){
                    progress_bar.setVisibility(View.GONE);
                    listavacia.setVisibility(View.VISIBLE);
                }

                todasMascotasAdapter.notifyDataSetChanged();
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