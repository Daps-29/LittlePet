package com.little.pet.model;

public class ImageModel {
    String foto1,foto2,foto3;

    public ImageModel(String foto1, String foto2, String foto3) {
        this.foto1 = foto1;
        this.foto2 = foto2;
        this.foto3 = foto3;
    }

    public String getFoto1() {
        return foto1;
    }

    public void setFoto1(String foto1) {
        this.foto1 = foto1;
    }

    public String getFoto2() {
        return foto2;
    }

    public void setFoto2(String foto2) {
        this.foto2 = foto2;
    }

    public String getFoto3() {
        return foto3;
    }

    public void setFoto3(String foto3) {
        this.foto3 = foto3;
    }
}
