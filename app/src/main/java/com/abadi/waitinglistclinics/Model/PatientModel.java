package com.abadi.waitinglistclinics.Model;

public class PatientModel {
    private String idDokter;
    private String namaDokter;
    private String imageDoctor;
    private String poliDoctor;
    private String idAntrian;
    private String idPasien;
    private String imageURL;
    private String namaPasien;
    private String noRekamMedis;
    private String caraPembayaran;
    private String asalRujukan;
    private String tanggalDaftar;
    private String waktuDaftar;
    private String waktuSelesai;
    private String status;

//    private String umurPasien;
//    private String alamatPasien;

    public PatientModel() {
    }

    public String getIdAntrian() {
        return idAntrian;
    }

    public void setIdAntrian(String idAntrian) {
        this.idAntrian = idAntrian;
    }

    public String getPoliDoctor() {
        return poliDoctor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPoliDoctor(String poliDoctor) {
        this.poliDoctor = poliDoctor;
    }

    public String getImageDoctor() {
        return imageDoctor;
    }

    public void setImageDoctor(String imageDoctor) {
        this.imageDoctor = imageDoctor;
    }

    public String getTanggalDaftar() {
        return tanggalDaftar;
    }

    public void setTanggalDaftar(String tanggalDaftar) {
        this.tanggalDaftar = tanggalDaftar;
    }

    public String getIdDokter() {
        return idDokter;
    }

    public void setIdDokter(String idDokter) {
        this.idDokter = idDokter;
    }

    public String getNamaDokter() {
        return namaDokter;
    }

    public void setNamaDokter(String namaDokter) {
        this.namaDokter = namaDokter;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getIdPasien() {
        return idPasien;
    }

    public void setIdPasien(String idPasien) {
        this.idPasien = idPasien;
    }

    public String getNamaPasien() {
        return namaPasien;
    }

    public void setNamaPasien(String namaPasien) {
        this.namaPasien = namaPasien;
    }

    public String getNoRekamMedis() {
        return noRekamMedis;
    }

    public void setNoRekamMedis(String noRekamMedis) {
        this.noRekamMedis = noRekamMedis;
    }

    public String getCaraPembayaran() {
        return caraPembayaran;
    }

    public void setCaraPembayaran(String caraPembayaran) {
        this.caraPembayaran = caraPembayaran;
    }

    public String getAsalRujukan() {
        return asalRujukan;
    }

    public void setAsalRujukan(String asalRujukan) {
        this.asalRujukan = asalRujukan;
    }

    public String getWaktuDaftar() {
        return waktuDaftar;
    }

    public void setWaktuDaftar(String waktuDaftar) {
        this.waktuDaftar = waktuDaftar;
    }

    public String getWaktuSelesai() {
        return waktuSelesai;
    }

    public void setWaktuSelesai(String waktuSelesai) {
        this.waktuSelesai = waktuSelesai;
    }
}
