package com.little.pet.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.little.pet.R;
import com.little.pet.VerOrganizacionesActivity;
import com.little.pet.model.OrganizacionDto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrganizacionAdapter extends RecyclerView.Adapter<OrganizacionAdapter.MyViewHolder> {

    private ArrayList<OrganizacionDto> data;
    private int mYear, mMonth, mDay, mHour, mMinute;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nombre, horafin, horaen, abierto, contacto, direccion_literal;
        ImageView foto;
        Context context;
        CircleImageView organizacion_foto;


        public MyViewHolder(@NonNull View v) {
            super(v);
            nombre = (TextView) v.findViewById(R.id.nombreOrganizacion);
            horaen = (TextView) v.findViewById(R.id.entrada);
            horafin = (TextView) v.findViewById(R.id.salida);
            direccion_literal = (TextView) v.findViewById(R.id.ubicacionOrganizacion);
            foto = (ImageView) v.findViewById(R.id.organizacionImage);
            abierto = (TextView) v.findViewById(R.id.abierto);
            organizacion_foto = (CircleImageView) v.findViewById(R.id.organizacion_foto);
            context = v.getContext();
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public OrganizacionAdapter(Context organizacionesActivity, ArrayList<OrganizacionDto> myDataset) {
        data = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view

        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_organizaciones, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final OrganizacionDto currentItem = data.get(position);

        holder.nombre.setText(currentItem.getNombre());
        holder.horaen.setText(currentItem.getHoraen());
        holder.horafin.setText(currentItem.getHorafin());
        holder.direccion_literal.setText(currentItem.getDireccion_literal());

        Picasso.get().load(currentItem.getFoto_portada()).placeholder(R.drawable.a).into(holder.foto, new Callback() {
            @Override public void onSuccess() {

            }
            @Override
            public void onError(Exception e) {
                Log.e("PICASSO ERROR", "onError: "+ e);
            }
        });

        Picasso.get().load(currentItem.getFoto()).placeholder(R.drawable.a).into(holder.organizacion_foto, new Callback() {
            @Override public void onSuccess() {

            }
            @Override
            public void onError(Exception e) {
                Log.e("PICASSO ERROR", "onError: "+ e);
            }
        });

        FirebaseDatabase.getInstance().getReference("organizaciones").child(currentItem.getId_organizacion()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                OrganizacionDto organizacionDto = dataSnapshot.getValue(OrganizacionDto.class);
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                int mYearHora = c.get(Calendar.YEAR);
                int mMonthHora = c.get(Calendar.MONTH)+1;
                int mDayHora = c.get(Calendar.DAY_OF_MONTH);
                String fechaHoy = mDayHora + "/" + mMonthHora + "/" + mYearHora;

                String horasdia = mHour + ":" + mMinute;
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                if (c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY && currentItem.getLunes().equals("Si")) {
                    if(horasdia.compareTo(currentItem.getHoraen()) > 0 && horasdia.compareTo(currentItem.getHorafin()) < 0) {
                        holder.abierto.setText("Abierto");
                    }else {
                        holder.abierto.setText("Cerrado");
                        holder.abierto.setTextColor(Color.RED);
                    }
                } else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY && currentItem.getMartes().equals("Si")) {
                    if(horasdia.compareTo(currentItem.getHoraen()) > 0 && horasdia.compareTo(currentItem.getHorafin()) < 0) {
                        holder.abierto.setText("Abierto");
                    }else {
                        holder.abierto.setText("Cerrado");
                        holder.abierto.setTextColor(Color.RED);
                    }
                }else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY && currentItem.getMiercoles().equals("Si")) {
                    if(horasdia.compareTo(currentItem.getHoraen()) > 0 && horasdia.compareTo(currentItem.getHorafin()) < 0) {
                        holder.abierto.setText("Abierto");
                    }else {
                        holder.abierto.setText("Cerrado");
                        holder.abierto.setTextColor(Color.RED);
                    }
                }else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY && currentItem.getJueves().equals("Si")) {
                    if(horasdia.compareTo(currentItem.getHoraen()) > 0 && horasdia.compareTo(currentItem.getHorafin()) < 0) {
                        holder.abierto.setText("Abierto");
                    }else {
                        holder.abierto.setText("Cerrado");
                        holder.abierto.setTextColor(Color.RED);
                    }
                }else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && currentItem.getViernes().equals("Si")) {
                    if(horasdia.compareTo(currentItem.getHoraen()) > 0 && horasdia.compareTo(currentItem.getHorafin()) < 0) {
                        holder.abierto.setText("Abierto");
                    }else {
                        holder.abierto.setText("Cerrado");
                        holder.abierto.setTextColor(Color.RED);
                    }
                }else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && currentItem.getSabado().equals("Si")) {
                    if(horasdia.compareTo(currentItem.getHoraen()) > 0 && horasdia.compareTo(currentItem.getHorafin()) < 0) {
                        holder.abierto.setText("Abierto");
                    }else {
                        holder.abierto.setText("Cerrado");
                        holder.abierto.setTextColor(Color.RED);
                    }
                }else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY && currentItem.getDomingo().equals("Si")) {
                    if(horasdia.compareTo(currentItem.getHoraen()) > 0 && horasdia.compareTo(currentItem.getHorafin()) < 0) {
                        holder.abierto.setText("Abierto");
                    }else {
                        holder.abierto.setText("Cerrado");
                        holder.abierto.setTextColor(Color.RED);
                    }
                }else {
                    holder.abierto.setText("Cerrado");
                    holder.abierto.setTextColor(Color.RED);
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.context.getApplicationContext(), VerOrganizacionesActivity.class);
                intent.putExtra("id_organizacion", currentItem.getId_organizacion());
                intent.putExtra("direccion", currentItem.getDireccion());
                holder.context.startActivity(intent);
            }
        });


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return data.size();
    }
}
