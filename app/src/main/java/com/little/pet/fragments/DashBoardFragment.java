package com.little.pet.fragments;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.little.pet.CategoriasActivity;
import com.little.pet.R;
import com.little.pet.TodasMascotas;
import com.little.pet.Utility.NetworkChangeListener;
import com.little.pet.adapter.CategoriesAdapter;
import com.little.pet.adapter.ListaInicialAdapter;
import com.little.pet.categorias.RecyclerItemClickListener;
import com.little.pet.model.CategoriasDto;
import com.little.pet.model.MascotaDto;
import com.little.pet.model.RegisterHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashBoardFragment extends Fragment {



    ArrayList<CategoriasDto> categoriesAdapterArrayList;
    private CategoriesAdapter categoriesAdapter;
    private RecyclerView rv_categorias;
    RecyclerView.LayoutManager categories_layoutManager;


    RecyclerView rv_mascotas;
    Query query;
    DatabaseReference database,userdatbase;
    private DatabaseReference databaseReference;
    ListaInicialAdapter listaInicialAdapter;
    ArrayList<MascotaDto> mascotaDtoArrayList;
    RecyclerView.LayoutManager layoutManager;
    FirebaseAuth mAuth;
    ConstraintLayout progress_bar;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView nombre, verTodo;
    CircleImageView perfilfoto;
    String idu;
    LinearLayout listavacia;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_dashboard, container, false);
        nombre = root.findViewById(R.id.nombreingreso);
        perfilfoto = root.findViewById(R.id.profile_user);
        verTodo = root.findViewById(R.id.verTodo);
        listavacia = root.findViewById(R.id.listavacia);


        progress_bar = (ConstraintLayout) root.findViewById(R.id.progress_bar);

        //CATEGORIAS MASCOTAS
        rv_categorias = (RecyclerView) root.findViewById(R.id.rv_categorias);
        //rv_categorias.setHasFixedSize(true);
        categories_layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
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
        rv_categorias.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), rv_categorias, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                CategoriasDto categoriasDto = categoriesAdapterArrayList.get(position);
                Intent intent = new Intent(getActivity(), CategoriasActivity.class);
                intent.putExtra("idCategoria", categoriasDto.getId_categoria()+"");
                intent.putExtra("nombreCategoria", categoriasDto.getNombre_categoria());
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));


        //LISTA PRINCIPAL
        rv_mascotas = (RecyclerView) root.findViewById(R.id.rv_mascotas);
        //rv_mascotas.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_mascotas.setLayoutManager(layoutManager);
        //ACCIONES PARA LA LISTA
        database = FirebaseDatabase.getInstance().getReference("mascotas");
        query = database.orderByKey().limitToLast(10);
        mascotaDtoArrayList = new ArrayList<>();
        listaInicialAdapter = new ListaInicialAdapter(getContext(), mascotaDtoArrayList);
        rv_mascotas.setAdapter(listaInicialAdapter);

        verTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TodasMascotas.class);
                startActivity(intent);
            }
        });


        //ACTUALIZAR LISTA
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateList();
            }
        });

        updateMascotasList();
        obtenernombres();

        return root;

    }

    private void updateList(){
        updateMascotasList();
        swipeRefreshLayout.setRefreshing(false);
    }


    private void updateMascotasList(){
        progress_bar.setVisibility(View.VISIBLE);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
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
                }

                listaInicialAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void obtenernombres(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        idu = user.getUid();
        userdatbase = FirebaseDatabase.getInstance().getReference("usuarios");

        userdatbase.child(idu).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    RegisterHelper registerHelper = snapshot.getValue(RegisterHelper.class);
                    nombre.setText(registerHelper.getNombres()+" "+registerHelper.getApellidos());
                    Picasso.get().load(registerHelper.getFoto()).placeholder(R.drawable.a).into(perfilfoto, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("PICASSO ERROR", "onError: " + e);
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
