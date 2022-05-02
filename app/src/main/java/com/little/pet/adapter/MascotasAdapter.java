package com.little.pet.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.little.pet.CambiarmascotaActivity;
import com.little.pet.R;
import com.little.pet.model.MascotaDto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MascotasAdapter extends RecyclerView.Adapter<MascotasAdapter.MyviewHolder> {
    Context context;
    ArrayList<MascotaDto> list;
    DatabaseReference databaseReference;
    Dialog dialog;
    String mascotaid;

    public MascotasAdapter(Context context, ArrayList<MascotaDto> list) {
        this.context = context;
        this.list = list;
        dialog = new Dialog(this.context);
    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(context).inflate(R.layout.card_mascota,parent,false);
      return new MyviewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder holder, int position) {
        MascotaDto mascotaDto = list.get(position);
        holder.nombre.setText(mascotaDto.getNombre());
        holder.descipcion.setText(mascotaDto.getDescripcion());
        holder.estamo.setText(mascotaDto.getEstadoperdida());
        mascotaid = mascotaDto.getId_mascota();

        holder.delte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boorardialog();

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.context.getApplicationContext(), CambiarmascotaActivity.class);
                intent.putExtra("id_mascota", mascotaDto.getId_mascota());
                intent.putExtra("nombre", mascotaDto.getNombre());
                intent.putExtra("descripcion", mascotaDto.getDescripcion());
                holder.context.startActivity(intent);
            }
        });
        Picasso.get().load(mascotaDto.getFoto1()).placeholder(R.drawable.a).into(holder.img, new Callback() {
            @Override public void onSuccess() {

            }
            @Override
            public void onError(Exception e) {
                Log.e("PICASSO ERROR", "onError: "+ e);
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyviewHolder extends RecyclerView.ViewHolder{

        Context context;
        TextView nombre,descipcion,estamo;
        ImageView img;
        ImageButton delte;
        FloatingActionButton btn;
        public MyviewHolder(@NonNull View itemView) {
            super(itemView);

            nombre = (TextView) itemView.findViewById(R.id.nombrem);
            estamo = (TextView) itemView.findViewById(R.id.estamo);
            descipcion = (TextView) itemView.findViewById(R.id.descripcionm);
            img = (ImageView) itemView.findViewById(R.id.imagen);
            delte = (ImageButton) itemView.findViewById(R.id.delete);
            btn = (FloatingActionButton) itemView.findViewById(R.id.nuevamascota);
            context = itemView.getContext();


        }
    }
    public void boorardialog(){
        dialog.setContentView(R.layout.dialogeliminar);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView btnclose = dialog.findViewById(R.id.closebtn);
        Button aceptar = dialog.findViewById(R.id.btnaceptar);

        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MascotaDto mascotaDto = new MascotaDto();
                DatabaseReference base = FirebaseDatabase.getInstance().getReference("mascotas");
                Map<String,Object> eliminar = new HashMap<>();
                eliminar.put("estado","0");
                base.child(mascotaid).updateChildren(eliminar);
                dialog.dismiss();

            }
        });
        dialog.show();

    }


}
