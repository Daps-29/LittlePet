package com.little.pet.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DireccionDto implements Parcelable {
    private String idDireccion, calle, referencia, iduser, latitud, longitud, direccionLiteral;


    public DireccionDto(){}

    public DireccionDto(String idDireccion,
                        String calle,
                        String referencia,
                        String iduser,
                        String latitud,
                        String longitud,
                        String direccionLiteral) {
        this.idDireccion = idDireccion;
        this.calle = calle;
        this.referencia = referencia;
        this.iduser = iduser;
        this.latitud = latitud;
        this.longitud = longitud;
        this.direccionLiteral = direccionLiteral;
    }


    public String getIdDireccion() {
        return idDireccion;
    }

    public void setIdDireccion(String idDireccion) {
        this.idDireccion = idDireccion;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getIduser() {
        return iduser;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getDireccionLiteral() {
        return direccionLiteral;
    }

    public void setDireccionLiteral(String direccionLiteral) {
        this.direccionLiteral = direccionLiteral;
    }

    public DireccionDto(Parcel in){
        String[] data = new String[13];
        in.readStringArray(data);
        this.idDireccion = data[0];
        this.calle = data[1];
        this.referencia = data[2];
        this.iduser = data[3];
        this.latitud = data[4];
        this.longitud = data[5];
        this.direccionLiteral = data[6];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.idDireccion, this.calle, this.referencia,
                this.iduser, this.latitud, this.longitud, this.direccionLiteral
        });
    }

    public static final Creator<DireccionDto> CREATOR= new Creator<DireccionDto>() {

        @Override
        public DireccionDto createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            return new DireccionDto(source);
        }

        @Override
        public DireccionDto[] newArray(int size) {
            // TODO Auto-generated method stub
            return new DireccionDto[size];
        }
    };
}

