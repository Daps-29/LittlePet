package com.little.pet;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.little.pet.adapter.ListaInicialAdapter;
import com.little.pet.adapter.MascotasAdapter;
import com.little.pet.model.MascotaDto;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CategoriasActivity extends AppCompatActivity {

    RecyclerView rv_subcategoria;
    RecyclerView.LayoutManager layoutManager;
    MascotasAdapter mascotasAdapter;
    String idCategoria, nombreCategoria;
    Query counterRef;
    ArrayList<MascotaDto> mascotaDtoArrayList;
    ImageView toolbarImage;
    ListaInicialAdapter listaInicialAdapter;
    LinearLayout listavacia;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.verde), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        Intent intent = getIntent();
        idCategoria = intent.getStringExtra("idCategoria");
        nombreCategoria = intent.getStringExtra("nombreCategoria");
        listavacia = findViewById(R.id.listavacia);

        toolbarImage = (ImageView) findViewById(R.id.toolbarImage);
        switch (idCategoria){
            case "0":
                toolbarImage.setImageResource(R.drawable.catulti);
                break;
            case "1":
                toolbarImage.setImageResource(R.drawable.dogulti);
                break;
            case "2":
                toolbarImage.setImageResource(R.drawable.rabbitulti);
                break;
            case "3":
                toolbarImage.setImageResource(R.drawable.birdulto);
                break;
            case "4":
                toolbarImage.setImageResource(R.drawable.hamsterulti);
                break;
            default:
                toolbarImage.setImageResource(R.drawable.perro);
                break;
        }

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
        collapsingToolbar.setTitle(nombreCategoria);
        collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);
        collapsingToolbar.setExpandedTitleColor(Color.WHITE);

        rv_subcategoria = (RecyclerView) findViewById(R.id.rv_categorias_mascotas);
        rv_subcategoria.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rv_subcategoria.setLayoutManager(layoutManager);

        //ACCIONES PARA LA LISTA
        counterRef = FirebaseDatabase.getInstance().getReference("mascotas");
        mascotaDtoArrayList = new ArrayList<>();
        listaInicialAdapter = new ListaInicialAdapter(CategoriasActivity.this, mascotaDtoArrayList);
        rv_subcategoria.setAdapter(listaInicialAdapter);


        counterRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        MascotaDto mascotaDto = postSnapshot.getValue(MascotaDto.class);
                        String categoria = mascotaDto.getCategorias();
                        if (categoria.equals(nombreCategoria)) {
                            if (mascotaDto.getVerificacion() == 1 && mascotaDto.getEstado().equals("1")) {
                                listavacia.setVisibility(View.GONE);
                                mascotaDtoArrayList.add(mascotaDto);
                                listaInicialAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    if (mascotaDtoArrayList.size() == 0){
                        listavacia.setVisibility(View.VISIBLE);
                    }
                }
                listaInicialAdapter.notifyDataSetChanged();
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

}
