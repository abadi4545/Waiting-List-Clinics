package com.abadi.waitinglistclinics.View.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.abadi.waitinglistclinics.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorDetailsActivity extends AppCompatActivity {

    private String getDoctorId, getNameDoctor, getImageURL, getPoliDoctor, getWorkday, getTimeStart, getTimeFinish, getPatientLimit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_details);

        CircleImageView civProfileDoctor = findViewById(R.id.civ_profile_doctordetails);

        TextView tvNameDoctor = findViewById(R.id.tv_namedoctor_doctordetails);
        TextView tvPoliDoctor = findViewById(R.id.tv_polidoctor_doctordetails);
        TextView tvWorkDay = findViewById(R.id.tv_workday_doctordetails);
        TextView tvTimeStart = findViewById(R.id.tv_worktimestart_doctordetails);
        TextView tvTimeFinish = findViewById(R.id.tv_worktimefinish_doctordetails);
        TextView tvPatientLimit = findViewById(R.id.tv_patientlimit_doctordetails);
        TextView btnShowpatient = findViewById(R.id.tv_btnshowpatient);
        ImageView btnBack = findViewById(R.id.btnback_doctordetails);

        getDataFromIntentList();

        if (getNameDoctor != null && getImageURL != null && getPoliDoctor != null && getWorkday != null &&
                getTimeStart != null && getTimeFinish != null && getPatientLimit != null) {

            if (getImageURL.substring(0, 4).equals("http")) {
                Picasso.get().load(getImageURL).into(civProfileDoctor);
            } else {
                Picasso.get().load(R.drawable.icon_default_profile).into(civProfileDoctor);
            }
            tvNameDoctor.setText(getNameDoctor);
            tvPoliDoctor.setText(getPoliDoctor);
            tvWorkDay.setText(getWorkday);
            tvTimeStart.setText(getTimeStart);
            tvTimeFinish.setText(getTimeFinish);
            tvPatientLimit.setText(getPatientLimit);
        }

        btnShowpatient.setOnClickListener(view -> {
            Intent toListPatient = new Intent(DoctorDetailsActivity.this, PatientListActivity.class);
            toListPatient.putExtra("id_dokter", getDoctorId);
            startActivity(toListPatient);
        });

        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(DoctorDetailsActivity.this, HomeActivity.class));
            finish();
        });
    }

    private void getDataFromIntentList() {
        Intent data = getIntent();
        getDoctorId = data.getStringExtra("id");
        getNameDoctor = data.getStringExtra("name");
        getImageURL = data.getStringExtra("imgprofile");
        getPoliDoctor = data.getStringExtra("poliDoctor");
        getWorkday = data.getStringExtra("workday");
        getTimeStart = data.getStringExtra("timestart");
        getTimeFinish = data.getStringExtra("timefinish");
        getPatientLimit = data.getStringExtra("limit");
    }
}