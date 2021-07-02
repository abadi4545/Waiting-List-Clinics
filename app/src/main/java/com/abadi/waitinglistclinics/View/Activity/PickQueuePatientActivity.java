package com.abadi.waitinglistclinics.View.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.abadi.waitinglistclinics.AlarmManagement.AlarmReceiver;
import com.abadi.waitinglistclinics.Model.PatientModel;
import com.abadi.waitinglistclinics.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PickQueuePatientActivity extends AppCompatActivity {

    private CircleImageView civ_profilepatient;
    private EditText et_patientname, et_norekammedis, et_carapembayaran, et_asalrujukan, et_pickdoctor;
//    private EditText et_patientcomplain, et_patientgender;

    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private SharedPreferences preferences;
    private Calendar calendar;

    private String imagePasien, namaDokter, idDokter, fotoDokter, poliDoctor, lastTimePatient;
    private String profile, name, nomerRekamMedis, asalRujukan, caraPembayaran;
//    private String keluhan, alamat, umur, kelamin;

    //setAlarm Notification
    private AlarmReceiver alarmReceiver;
    //set Estimate called for admin
    private EditText etEstimateCalled;
    private String estimate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickqueue_patient);

        //inisialisasi
        reference = FirebaseDatabase.getInstance().getReference("WaitingList");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        calendar = Calendar.getInstance();
        alarmReceiver = new AlarmReceiver();

        civ_profilepatient = findViewById(R.id.civ_imageProfilePatient);
        et_patientname = findViewById(R.id.et_patientname);

//        Data sebelumnya
//        et_numberphone = findViewById(R.id.et_numberphone);
//        et_patientcomplain = findViewById(R.id.et_patientcomplains);
//        et_patientage = findViewById(R.id.et_patientage);
//        et_patientaddress = findViewById(R.id.et_patientaddress);
//        et_patientgender = findViewById(R.id.et_patientgender);

//        Revisi ke 4
        et_norekammedis = findViewById(R.id.et_no_rekammedis);
        et_carapembayaran = findViewById(R.id.et_cara_pembayaran);
        et_asalrujukan = findViewById(R.id.et_asal_rujukan);
        et_pickdoctor = findViewById(R.id.et_pick_polindoctor);

        CardView btn_regis = findViewById(R.id.cv_btnpatientregis);
        CardView btn_searchdoctor = findViewById(R.id.cv_btn_pickpoliklinik);
        ImageView btnBack = findViewById(R.id.btnback_registry);

        //for admin
        etEstimateCalled = findViewById(R.id.et_estimateAdmin);
        ImageView btnSaveEstimate = findViewById(R.id.iv_saveEstimateAdmin);
        LinearLayout estimateAdmin = findViewById(R.id.ll_estimateAdmin);

        //get intent dari list dokter
        Intent data = getIntent();
        String namaPasien = data.getStringExtra("namapasien");
        String userType = data.getStringExtra("usertype");
        String estimateStart = data.getStringExtra("estimate");

        idDokter = data.getStringExtra("id_doctor");
        namaDokter = data.getStringExtra("name_doctor");
        fotoDokter = data.getStringExtra("image_doctor");
        poliDoctor = data.getStringExtra("poliDoctor");
        imagePasien = data.getStringExtra("imagepasien");
        lastTimePatient = data.getStringExtra("last_time");


        if (userType != null && estimateStart != null) {
            if (userType.equals("admin")) {
                estimateAdmin.setVisibility(View.VISIBLE);
            }
            etEstimateCalled.setText(estimateStart);
        }

        if (namaPasien != null && imagePasien != null) {
            if (imagePasien.substring(0, 4).equals("http")) {
                Picasso.get().load(imagePasien).into(civ_profilepatient);
            } else {
                Picasso.get().load(R.drawable.icon_default_profile).into(civ_profilepatient);
            }
            et_patientname.setText(namaPasien);
        } else {
            getPreference();
        }

        if (idDokter != null && namaDokter != null) {
            et_pickdoctor.setText(namaDokter);
        }

        btnSaveEstimate.setOnClickListener(view -> {
            String estimate = etEstimateCalled.getText().toString();
            if (!TextUtils.isEmpty(estimate)) {
                setEstimate(estimate);
            } else {
                Toast.makeText(PickQueuePatientActivity.this, "Gak boleh kosong ya :(", Toast.LENGTH_SHORT).show();
            }

            // autohide after click SAVE
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        });

        btn_searchdoctor.setOnClickListener(view -> {
            setPreference();
            Intent toListDoctor = new Intent(PickQueuePatientActivity.this, DoctorListActivity.class);
            toListDoctor.putExtra("daftar", "daftar");
            startActivity(toListDoctor);
        });

        btn_regis.setOnClickListener(view -> {
            String noRekamMedis = et_norekammedis.getText().toString();
            String caraPembayaran = et_carapembayaran.getText().toString();
            String asalRujukan = et_asalrujukan.getText().toString();

//            String keluhan = et_patientcomplain.getText().toString();
//            String jenis = et_patientgender.getText().toString();

            int estimateFinish = 10;
            if (estimate != null) {
                estimateFinish = Integer.parseInt(estimate);
            }

            if (!et_patientname.getText().toString().equals("kosong")) {
                if (!TextUtils.isEmpty(noRekamMedis) && !TextUtils.isEmpty(caraPembayaran) && !TextUtils.isEmpty(asalRujukan)) {
                    if (!TextUtils.isEmpty(et_pickdoctor.getText())) {
                        if (lastTimePatient.equals("kosong")) {
                            daftarPatient(noRekamMedis, caraPembayaran, asalRujukan, namaDokter, estimateFinish, 0);
                        } else {
                            daftarPatient(noRekamMedis, caraPembayaran, asalRujukan, namaDokter, 0, estimateFinish);
                        }
                    } else {
                        Toast.makeText(this, "Silahkan pilih dokter terlebih dahulu", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Tolong lengkapi semua fieldnya ya", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "!! NAMA ANDA KOSONG !!\nSilahkan kembali ke halaman home\nterlebih dahulu !", Toast.LENGTH_SHORT).show();
            }

        });


        if (profile != null) {
            initPrefRegistPatient();
        }

        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(PickQueuePatientActivity.this, HomeActivity.class));
            finish();
        });
    }

    private void setEstimate(String plus) {
        DatabaseReference dbRefEstimate = FirebaseDatabase.getInstance().getReference("Estimate");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("estimate", plus);
        dbRefEstimate.setValue(hashMap);

        Toast.makeText(this, "Estimasi selesai per pasien\nBerhasil diperbaharui", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void setPreference() {
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("profile", imagePasien);
        editor.putString("namepasien", et_patientname.getText().toString());
        editor.putString("norekammedis", et_norekammedis.getText().toString());
        editor.putString("asalrujukan", et_asalrujukan.getText().toString());
        editor.putString("carapembayaran", et_carapembayaran.getText().toString());
        editor.putString("estimate", etEstimateCalled.getText().toString());
//        editor.putString("kelamin", et_patientgender.getText().toString());
//        editor.putString("keluhan", et_patientcomplain.getText().toString());
        editor.apply();
    }

    private void getPreference() {
        preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        profile = preferences.getString("profile", "");
        estimate = preferences.getString("estimate", "");
        name = preferences.getString("namepasien", "");
        nomerRekamMedis = preferences.getString("norekammedis", "");
        asalRujukan = preferences.getString("asalrujukan", "");
        caraPembayaran = preferences.getString("carapembayaran", "");

//        keluhan = preferences.getString("keluhan", "");
//        kelamin = preferences.getString("kelamin", "");
    }

    private void removePreference() {
        preferences.edit().remove("profile").apply();
        preferences.edit().remove("namepasien").apply();
        preferences.edit().remove("norekammedis").apply();
        preferences.edit().remove("asalrujukan").apply();
        preferences.edit().remove("carapembayaran").apply();

//        preferences.edit().remove("keluhan").apply();
//        preferences.edit().remove("kelamin").apply();
    }

    private void initPrefRegistPatient() {
        if (!TextUtils.isEmpty(profile)) {
            if (!profile.equals("default")) {
                Picasso.get().load(profile).into(civ_profilepatient);
            } else {
                Picasso.get().load(R.drawable.icon_default_profile).into(civ_profilepatient);
            }
            et_patientname.setText(name);
        } else {
            Picasso.get().load(R.drawable.icon_default_profile).into(civ_profilepatient);
            et_patientname.setText(R.string.str_null);
        }

        et_norekammedis.setText(nomerRekamMedis);
        et_asalrujukan.setText(asalRujukan);
        et_carapembayaran.setText(caraPembayaran);

//        et_patientcomplain.setText(keluhan);
//        et_patientgender.setText(kelamin);
    }


    public String getCurrentLocalTimeStamp(int plus) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        calendar.add(Calendar.MINUTE, plus);
        return currentTime.format(calendar.getTime());
    }

    public String getCurrentLocalDateStamp() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd MMM, yyyy");
        return currentDate.format(calendar.getTime());
    }

    // Hmmm LOGIKA MANUAL CUK :(
    public String setLastTimePatient(String lastTime, int plus) {
        int hour = Integer.parseInt(lastTime.substring(0, 2));
        int minute = Integer.parseInt(lastTime.substring(3, 5));

        int estimateMinute = minute + plus;
        String nol = "0";

        if (estimateMinute > 59) {
            String getLastMinute = String.valueOf(estimateMinute);
            String lastMinute = getLastMinute.substring(1, 2);

            estimateMinute = 0;

            int addHour = hour + 1;
            String addHours = String.valueOf(addHour);
            int lengthHours = addHours.length();

            if (lengthHours != 1) {

                if (addHour > 23) {
                    addHour = 0;
                    return nol + addHour + ":" + estimateMinute + lastMinute;
                } else {
                    return addHour + ":" + estimateMinute + lastMinute;
                }

            } else {
                return nol + addHour + ":" + estimateMinute + lastMinute;
            }

        } else {
            if (String.valueOf(estimateMinute).length() != 1) {
                return lastTime.substring(0, 2) + ":" + estimateMinute;
            } else {
                return lastTime.substring(0, 2) + ":" + nol + estimateMinute;
            }
        }
    }

    private void daftarPatient(String nomerRekamMedis, String caraPembayaran, String asalRujukan, String namaDokter,
                               int plus, int plusnotnull) {
        String userId = firebaseUser.getUid();
        PatientModel patientModel = new PatientModel();

        String idAntrian = reference.child(idDokter).push().getKey();
        patientModel.setIdAntrian(idAntrian);

        if (plus != 0) {
            HashMap<String, Object> daftarPatient = new HashMap<>();
            daftarPatient.put("idPasien", userId);
            daftarPatient.put("idAntrian", patientModel.getIdAntrian());
            daftarPatient.put("idDokter", idDokter);
            daftarPatient.put("namaDokter", namaDokter);
            daftarPatient.put("noRekamMedis", nomerRekamMedis);
            daftarPatient.put("caraPembayaran", caraPembayaran);
            daftarPatient.put("asalRujukan", asalRujukan);
            daftarPatient.put("waktuDaftar", getCurrentLocalTimeStamp(0));
            // set estimate +10 minute
            String estimateTime = getCurrentLocalTimeStamp(plus);
            daftarPatient.put("waktuSelesai", estimateTime);
            daftarPatient.put("tanggalDaftar", getCurrentLocalDateStamp());

//            daftarPatient.put("keluhanPasien", keluhan);
//            daftarPatient.put("alamatPasien", alamat);

            if (profile != null && name != null) {
                daftarPatient.put("imageURL", profile);
                daftarPatient.put("namaPasien", name);
            } else {
                daftarPatient.put("imageURL", "default");
                daftarPatient.put("namaPasien", "default");
            }

            assert idAntrian != null;
            reference.child(idDokter).child(idAntrian).setValue(daftarPatient);

            // buat list antrian di home activity
            if (fotoDokter != null && poliDoctor != null) {
                daftarPatient.put("imageDoctor", fotoDokter);
                daftarPatient.put("poliDoctor", poliDoctor);
                daftarPatient.put("status", "MENUNGGU");

                DatabaseReference dbRefMyQueue = FirebaseDatabase.getInstance().getReference("MyQueue");
                dbRefMyQueue.child(userId).child(idDokter).setValue(daftarPatient);
                Toast.makeText(this, "Data berhasil mendaftar", Toast.LENGTH_SHORT).show();
                removePreference();

                //buat update dokter
                HashMap<String, Object> hashDoctor = new HashMap<>();
                hashDoctor.put("lastPatient", estimateTime);
                DatabaseReference dbRefDoctor = FirebaseDatabase.getInstance().getReference("Doctors");
                dbRefDoctor.child(idDokter).updateChildren(hashDoctor);

                Intent toListPatient = new Intent(PickQueuePatientActivity.this, PatientListActivity.class);
                toListPatient.putExtra("id_dokter", idDokter);
                startActivity(toListPatient);
                finish();
            }
        } else {
            String estimateTime = setLastTimePatient(lastTimePatient, plusnotnull);

            HashMap<String, Object> daftarPatient = new HashMap<>();
            daftarPatient.put("idPasien", userId);
            daftarPatient.put("idAntrian", patientModel.getIdAntrian());
            daftarPatient.put("idDokter", idDokter);
            daftarPatient.put("namaDokter", namaDokter);
            daftarPatient.put("noRekamMedis", nomerRekamMedis);
            daftarPatient.put("caraPembayaran", caraPembayaran);
            daftarPatient.put("asalRujukan", asalRujukan);
            daftarPatient.put("waktuDaftar", getCurrentLocalTimeStamp(0));
            daftarPatient.put("tanggalDaftar", getCurrentLocalDateStamp());

//            daftarPatient.put("keluhanPasien", keluhan);
//            daftarPatient.put("alamatPasien", alamat);

            int lastPatient = Integer.parseInt(estimateTime.substring(0, 2) + estimateTime.substring(3, 5));
            int currentTime = Integer.parseInt(getCurrentLocalTimeStamp(0).substring(0, 2) +
                    getCurrentLocalTimeStamp(0).substring(3, 5));

            if (lastPatient > currentTime) {
                daftarPatient.put("waktuSelesai", estimateTime);
            } else {
                daftarPatient.put("waktuSelesai", getCurrentLocalTimeStamp(plusnotnull));
            }

            if (profile != null && name != null) {
                daftarPatient.put("imageURL", profile);
                daftarPatient.put("namaPasien", name);
            } else {
                daftarPatient.put("imageURL", "default");
                daftarPatient.put("namaPasien", "default");
            }

            assert idAntrian != null;
            reference.child(idDokter).child(idAntrian).setValue(daftarPatient);

            // buat list antrian di home activity
            if (fotoDokter != null && poliDoctor != null) {
                daftarPatient.put("imageDoctor", fotoDokter);
                daftarPatient.put("poliDoctor", poliDoctor);
                daftarPatient.put("status", "MENUNGGU");

                DatabaseReference dbRefMyQueue = FirebaseDatabase.getInstance().getReference("MyQueue");
                dbRefMyQueue.child(userId).child(idDokter).setValue(daftarPatient);
                Toast.makeText(this, "Data berhasil terdaftar", Toast.LENGTH_SHORT).show();
                removePreference();

                //buat update dokter
                HashMap<String, Object> hashDoctor = new HashMap<>();

                // cek kalau waktu estimasi sudah kelewat dari waktu sekarang
                String onceTime;
                if (lastPatient > currentTime) {
                    hashDoctor.put("lastPatient", estimateTime);
                    onceTime = estimateTime;
                } else {
                    hashDoctor.put("lastPatient", getCurrentLocalTimeStamp(0));
                    onceTime = getCurrentLocalTimeStamp(0);
                }

                DatabaseReference dbRefDoctor = FirebaseDatabase.getInstance().getReference("Doctors");
                dbRefDoctor.child(idDokter).updateChildren(hashDoctor);

                //SET ALARM NOTIFICATION
                String onceMessage = "Halo, " + name + " sudah giliran anda nih :)";
                alarmReceiver.setOneTimeAlarm(this, AlarmReceiver.TYPE_ONE_TIME, onceTime, onceMessage);

                Intent toListPatient = new Intent(PickQueuePatientActivity.this, PatientListActivity.class);
                toListPatient.putExtra("id_dokter", idDokter);
                startActivity(toListPatient);
                finish();
            }
        }
    }
}