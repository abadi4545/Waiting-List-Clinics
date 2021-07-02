package com.abadi.waitinglistclinics.View.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.abadi.waitinglistclinics.Adapter.DoctorListAdapter;
import com.abadi.waitinglistclinics.Model.DoctorModel;
import com.abadi.waitinglistclinics.R;

import java.util.ArrayList;

public class DoctorListActivity extends AppCompatActivity {

    private RecyclerView rvDoctorList;
    private TextView tvEmptyDr;
    private ProgressBar progressBar;

    private DoctorListAdapter doctorListAdapter;
    private ArrayList<DoctorModel> doctorModelArrayList;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);

        ImageView btnBack = findViewById(R.id.btnback_doctorlist);
        tvEmptyDr = findViewById(R.id.tv_emptyDoctor);
        progressBar = findViewById(R.id.progressBar);
        rvDoctorList = findViewById(R.id.rv_doctorlist);
        rvDoctorList.setLayoutManager(new LinearLayoutManager(this));
        rvDoctorList.setHasFixedSize(true);
        rvDoctorList.smoothScrollToPosition(0);

        //inisialisasi
        reference = FirebaseDatabase.getInstance().getReference("Doctors");

        getAllDoctor();

        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(DoctorListActivity.this, HomeActivity.class));
            finish();
        });
    }

    private void getAllDoctor() {
        doctorModelArrayList = new ArrayList<>();
        Query query = reference.orderByChild("poliDoctor");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                doctorModelArrayList.clear();
                if (!snapshot.exists()) {
                    tvEmptyDr.setVisibility(View.VISIBLE);
                } else {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        DoctorModel doctorModel = dataSnapshot.getValue(DoctorModel.class);
                        doctorModelArrayList.add(doctorModel);
                    }

                    doctorListAdapter = new DoctorListAdapter(DoctorListActivity.this, doctorModelArrayList);
                    rvDoctorList.setAdapter(doctorListAdapter);
                    doctorListAdapter.notifyDataSetChanged();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}