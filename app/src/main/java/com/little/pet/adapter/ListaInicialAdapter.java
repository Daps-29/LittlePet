package com.little.pet.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.little.pet.MascotaDetalleActivity;
import com.little.pet.R;
import com.little.pet.model.DireccionDto;
import com.little.pet.model.MascotaDto;
import com.little.pet.model.RegisterHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.little.pet.model.MascotaDto;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ListaInicialAdapter extends RecyclerView.Adapter<ListaInicialAdapter.MyViewHolder> {

    private ArrayList<MascotaDto> data;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nombre, edad,estado,ubicacion;
        ImageView foto;
        Context context;


        public MyViewHolder(@NonNull View v) {
            super(v);
            nombre = (TextView) v.findViewById(R.id.nombreMascota);
            edad = (TextView) v.findViewById(R.id.edad);
            estado = (TextView) v.findViewById(R.id.estadomascota);
            ubicacion = (TextView) v.findViewById(R.id.ubicacion);
            foto = (ImageView) v.findViewById(R.id.mascotaImage);
            context = v.getContext();
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListaInicialAdapter(Context mainActivity, ArrayList<MascotaDto> myDataset) {
        data = myDataset;
    }


    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view

        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_lista_inicial, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final MascotaDto currentItem = data.get(position);

        holder.nombre.setText(currentItem.getNombre());
        holder.estado.setText(currentItem.getEstadoperdida());
        holder.edad.setText(currentItem.getEdad()+" "+ currentItem.getTiempo());


        FirebaseDatabase.getInstance().getReference("usuarios").child(currentItem.getUser()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RegisterHelper registerHelper = dataSnapshot.getValue(RegisterHelper.class);
                if(registerHelper.getDireccion() != null && registerHelper.getDireccion() != ""){
                    FirebaseDatabase.getInstance().getReference("direcciones").child(registerHelper.getDireccion()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            DireccionDto direccionDto = dataSnapshot.getValue(DireccionDto.class);
                            holder.ubicacion.setText(direccionDto.getDireccionLiteral());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.context.getApplicationContext(), MascotaDetalleActivity.class);
                intent.putExtra("user", currentItem.getUser());
                intent.putExtra("id_mascota", currentItem.getId_mascota());
                intent.putExtra("ubicacion", currentItem.getUbicacion());
                holder.context.startActivity(intent);
           }
        });

        Picasso.get().load(currentItem.getFoto1()).placeholder(R.drawable.a).into(holder.foto, new Callback() {
            @Override public void onSuccess() {

            }
            @Override
            public void onError(Exception e) {
                Log.e("PICASSO ERROR", "onError: "+ e);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return data.size();
    }
}
