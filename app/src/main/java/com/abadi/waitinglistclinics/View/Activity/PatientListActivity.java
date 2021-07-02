package com.abadi.waitinglistclinics.View.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.abadi.waitinglistclinics.Adapter.PatientListAdapter;
import com.abadi.waitinglistclinics.Model.PatientModel;
import com.abadi.waitinglistclinics.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class PatientListActivity extends AppCompatActivity {

    private RecyclerView rvPatientList;
    private ProgressBar progressBar;
    private TextView tvEmptyPatient;

    private PatientListAdapter patientListAdapter;
    private DatabaseReference reference;
    private ArrayList<PatientModel> patientModelArrayList;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        progressBar = findViewById(R.id.progressBar);
        tvEmptyPatient = findViewById(R.id.tv_emptyPatient);
        ImageView btnBack = findViewById(R.id.btnback_patientlist);
        rvPatientList = findViewById(R.id.rv_patientlist);
        rvPatientList.setHasFixedSize(true);
        rvPatientList.setLayoutManager(new LinearLayoutManager(this));
        rvPatientList.smoothScrollToPosition(0);

        Intent data = getIntent();
        String dokterId = data.getStringExtra("id_dokter");

        //inisialisasi
        if (dokterId != null) {
            reference = FirebaseDatabase.getInstance().getReference("WaitingList").child(dokterId);
        }
        calendar = Calendar.getInstance();

        getAllPatient();

        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(PatientListActivity.this, HomeActivity.class));
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public String getCurrentLocalDateStamp() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd MMM, yyyy");
        return currentDate.format(calendar.getTime());
    }

    private void getAllPatient() {
        patientModelArrayList = new ArrayList<>();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                patientModelArrayList.clear();
                if (!snapshot.exists()) {
                    tvEmptyPatient.setVisibility(View.VISIBLE);
                } else {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        PatientModel patientModel = dataSnapshot.getValue(PatientModel.class);

                        assert patientModel != null;
                        if (patientModel.getTanggalDaftar().equals(getCurrentLocalDateStamp())) {
                            patientModelArrayList.add(patientModel);
                        }
                        // belum sesuai ekspektasi
//                        else {
//                            if (countNotif == 1) {
//                                Toast.makeText(PatientListActivity.this, "Belum ada pasien untuk hari ini", Toast.LENGTH_SHORT).show();
//                            }
//                        }
                    }
                    patientListAdapter = new PatientListAdapter(PatientListActivity.this, patientModelArrayList);
                    rvPatientList.setAdapter(patientListAdapter);
                    patientListAdapter.notifyDataSetChanged();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}