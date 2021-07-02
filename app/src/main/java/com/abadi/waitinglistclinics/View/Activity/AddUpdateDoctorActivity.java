package com.abadi.waitinglistclinics.View.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.abadi.waitinglistclinics.Model.DoctorModel;
import com.abadi.waitinglistclinics.R;
import com.abadi.waitinglistclinics.AlarmManagement.TimePickerFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddUpdateDoctorActivity extends AppCompatActivity implements View.OnClickListener, TimePickerFragment.DialogTimeListener {

    private CircleImageView civProfileDoctor;
    private Uri mImageUri;
    private ImageView imgUpload, imgDelete;
    private EditText etNameDr, etPoliDoctor, etWorkDay, etTimeStart, etTimeFinish, etLimitSeat;

    final String TIMESTART_PICKER_TAG = "TIME START";
    final String TIMEFINISH_PICKER_TAG = "TIME FINISH";

    private DatabaseReference reference;
    private StorageReference storageReference;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;
    private DoctorModel doctorModel;

    private String getDoctorId, getName, getImageURL, getPoliDoctor, getWorkday, getTimeStart, getTimeFinish, getPatientLimit, getLastPatient;
    private boolean isDeleteProfile = false;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doctor);

        ImageView btnBack = findViewById(R.id.btnback_adddoctor);
        TextView tvTitle = findViewById(R.id.titlebar_adddoctor);
        TextView tvAddDoctor = findViewById(R.id.tv_adddoctor);
        civProfileDoctor = findViewById(R.id.civ_imageProfileDoctor);
        imgUpload = findViewById(R.id.iv_imageUploadProfileDoctor);
        imgDelete = findViewById(R.id.iv_imageDeleteProfileDoctor);
        etNameDr = findViewById(R.id.et_username_adddoctor);
        etPoliDoctor = findViewById(R.id.et_polidoctor_adddoctor);
        etWorkDay = findViewById(R.id.et_workday_adddoctor);

        etTimeStart = findViewById(R.id.et_timestart);
        etTimeFinish = findViewById(R.id.et_timefinish);
        etLimitSeat = findViewById(R.id.et_limitseat);
        ImageView imgTimePickerStart = findViewById(R.id.img_picktimestart);
        ImageView imgTimePickerFinish = findViewById(R.id.img_picktimefinish);
        CardView btnAddDoctor = findViewById(R.id.cv_btnadddoctor);

        //inisialisasi
        reference = FirebaseDatabase.getInstance().getReference("Doctors");
        storageReference = FirebaseStorage.getInstance().getReference("Profile");
        doctorModel = new DoctorModel();
        calendar = Calendar.getInstance();

        etWorkDay.setText(R.string.str_openeveryday);
        etLimitSeat.setText("--");
        getDataFromIntentList();

        if (getDoctorId != null && getName != null && getImageURL != null && getPoliDoctor != null && getWorkday != null &&
                getTimeStart != null && getTimeFinish != null && getPatientLimit != null) {

            tvTitle.setText(R.string.titlebar_updatedoctor);
            tvAddDoctor.setText(R.string.str_update);

            if (getImageURL.substring(0, 4).equals("http")) {
                Picasso.get().load(getImageURL).into(civProfileDoctor);
                imgUpload.setVisibility(View.GONE);
                imgDelete.setVisibility(View.VISIBLE);
            } else {
                Picasso.get().load(R.drawable.icon_default_profile).into(civProfileDoctor);
            }
            etNameDr.setText(getName);
            etPoliDoctor.setText(getPoliDoctor);
            etWorkDay.setText(getWorkday);
            etTimeStart.setText(getTimeStart);
            etTimeFinish.setText(getTimeFinish);
            etLimitSeat.setText(getPatientLimit);
        }

        imgUpload.setOnClickListener(this);
        imgDelete.setOnClickListener(this);
        imgTimePickerStart.setOnClickListener(this);
        imgTimePickerFinish.setOnClickListener(this);
        btnAddDoctor.setOnClickListener(this);

        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(AddUpdateDoctorActivity.this, HomeActivity.class));
            finish();
        });
    }

    private void getDataFromIntentList() {
        Intent data = getIntent();
        getDoctorId = data.getStringExtra("id");
        getName = data.getStringExtra("name");
        getImageURL = data.getStringExtra("imgprofile");
        getPoliDoctor = data.getStringExtra("poliDoctor");
        getWorkday = data.getStringExtra("workday");
        getTimeStart = data.getStringExtra("timestart");
        getTimeFinish = data.getStringExtra("timefinish");
        getPatientLimit = data.getStringExtra("limit");
        getLastPatient = data.getStringExtra("lastpatient");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_imageUploadProfileDoctor:
                imgUpload.setVisibility(View.GONE);
                imgDelete.setVisibility(View.VISIBLE);
                isDeleteProfile = false;
                CropImage.activity().setAspectRatio(1, 1).start(this);
                break;
            case R.id.iv_imageDeleteProfileDoctor:
                imgUpload.setVisibility(View.VISIBLE);
                imgDelete.setVisibility(View.GONE);
                isDeleteProfile = true;
                civProfileDoctor.setImageResource(R.drawable.icon_default_profile);
                mImageUri = null;
                break;
            case R.id.img_picktimestart:
                TimePickerFragment timeStart = new TimePickerFragment();
                timeStart.show(getSupportFragmentManager(), TIMESTART_PICKER_TAG);
                break;
            case R.id.img_picktimefinish:
                TimePickerFragment timeFinish = new TimePickerFragment();
                timeFinish.show(getSupportFragmentManager(), TIMEFINISH_PICKER_TAG);
                break;
            case R.id.cv_btnadddoctor:
                String name = etNameDr.getText().toString();
                String poliDoctor = etPoliDoctor.getText().toString();
                String workday = etWorkDay.getText().toString();
                String timestart = etTimeStart.getText().toString();
                String timefinish = etTimeFinish.getText().toString();
                String limit = etLimitSeat.getText().toString();

                if (getDoctorId != null) {
                    if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(poliDoctor) && !TextUtils.isEmpty(workday) &&
                            !TextUtils.isEmpty(timestart) && !TextUtils.isEmpty(timefinish) && !TextUtils.isEmpty(limit)) {

                        updateDataDoctor(getDoctorId, name, poliDoctor, workday, timestart, timefinish, limit);

                    } else {
                        Toast.makeText(this, "Tolong lengkapi semua fieldnya ya :)", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(poliDoctor) && !TextUtils.isEmpty(workday) &&
                            !TextUtils.isEmpty(timestart) && !TextUtils.isEmpty(timefinish) && !TextUtils.isEmpty(limit)) {

                        AddNewDoctor(name, poliDoctor, workday, timestart, timefinish, limit);

                    } else {
                        Toast.makeText(this, "Tolong lengkapi semua fieldnya ya :)", Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            default:
                break;
        }
    }

    private void AddNewDoctor(String name, String poliDoctor, String workday, String timestart, String timefinish, String limit) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Tunggu sebentar ya..");
        pd.show();
        pd.setCancelable(false);

        // generate id dokter
        String doctorId = reference.push().getKey();
        doctorModel.setId(doctorId);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", doctorModel.getId());
        hashMap.put("name", name);
        hashMap.put("poliDoctor", poliDoctor);
        hashMap.put("workday", workday);
        hashMap.put("worktimestart", timestart);
        hashMap.put("worktimefinish", timefinish);
        hashMap.put("limit", limit);
        hashMap.put("imageURL", "default");
        hashMap.put("lastPatient", "kosong");
        hashMap.put("lastDate", getCurrentLocalDateStamp());

        reference.child(doctorModel.getId()).setValue(hashMap).addOnCompleteListener(task -> {
            if (mImageUri != null) {
                StorageReference fileReference = storageReference.child("img-doctor-" + name.toLowerCase()
                        + "-" + System.currentTimeMillis() + ".jpg");

                uploadTask = fileReference.putFile(mImageUri);
                uploadTask.continueWithTask(task1 -> {
                    if (!task1.isSuccessful()) {
                        throw Objects.requireNonNull(task1.getException());
                    }
                    return fileReference.getDownloadUrl();
                }).addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        Uri downloadUri = task2.getResult();
                        assert downloadUri != null;
                        String mUri = downloadUri.toString();

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", mUri);
                        reference.child(doctorModel.getId()).updateChildren(map);

                        pd.dismiss();
                        startActivity(new Intent(AddUpdateDoctorActivity.this, DoctorListActivity.class));
                        finish();

                        Toast.makeText(AddUpdateDoctorActivity.this, "Dokter berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(AddUpdateDoctorActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                pd.dismiss();
                startActivity(new Intent(AddUpdateDoctorActivity.this, DoctorListActivity.class));
                finish();

                Toast.makeText(AddUpdateDoctorActivity.this, "Upload tanpa foto profile", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateDataDoctor(String doctorId, String name, String poliDoctor, String workday, String timestart, String timefinish, String limit) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Tunggu sebentar ya..");
        pd.show();
        pd.setCancelable(false);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", doctorId);
        hashMap.put("name", name);
        hashMap.put("poliDoctor", poliDoctor);
        hashMap.put("workday", workday);
        hashMap.put("worktimestart", timestart);
        hashMap.put("worktimefinish", timefinish);
        hashMap.put("limit", limit);
        hashMap.put("open", "false");
        hashMap.put("imageURL", "default");
        hashMap.put("lastPatient", getLastPatient);
        hashMap.put("lastDate", getCurrentLocalDateStamp());

        reference.child(doctorId).setValue(hashMap).addOnCompleteListener(task -> {
            if (!isDeleteProfile) {
                if (mImageUri != null) {
                    StorageReference fileReference = storageReference.child("img-doctor-" + name.toLowerCase()
                            + "-" + System.currentTimeMillis() + ".jpg");

                    uploadTask = fileReference.putFile(mImageUri);
                    uploadTask.continueWithTask(task1 -> {
                        if (!task1.isSuccessful()) {
                            throw Objects.requireNonNull(task1.getException());
                        }
                        return fileReference.getDownloadUrl();
                    }).addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            Uri downloadUri = task2.getResult();
                            assert downloadUri != null;
                            String mUri = downloadUri.toString();

                            HashMap<String, Object> map = new HashMap<>();
                            map.put("imageURL", mUri);
                            reference.child(doctorId).updateChildren(map);

                            pd.dismiss();
                            startActivity(new Intent(AddUpdateDoctorActivity.this, DoctorListActivity.class));
                            finish();

                            Toast.makeText(AddUpdateDoctorActivity.this, "Update data berhasil", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> Toast.makeText(AddUpdateDoctorActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                } else {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("imageURL", getImageURL);
                    reference.child(doctorId).updateChildren(map);

                    pd.dismiss();
                    startActivity(new Intent(AddUpdateDoctorActivity.this, DoctorListActivity.class));
                    finish();

                    Toast.makeText(AddUpdateDoctorActivity.this, "Update berhasil", Toast.LENGTH_SHORT).show();
                }
            } else {
                HashMap<String, Object> map = new HashMap<>();
                map.put("imageURL", "default");
                reference.child(doctorId).updateChildren(map);

                pd.dismiss();
                startActivity(new Intent(AddUpdateDoctorActivity.this, DoctorListActivity.class));
                finish();

                Toast.makeText(this, "Berhasil menghapus foto profile", Toast.LENGTH_SHORT).show();
            }

        });
    }

    public String getCurrentLocalDateStamp() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd MMM, yyyy");
        return currentDate.format(calendar.getTime());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            assert result != null;
            mImageUri = result.getUri();
            civProfileDoctor.setImageURI(mImageUri);
        } else {
            Toast.makeText(this, "Tambah foto dibatalkan", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDialogTimeSet(String tag, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        if (TIMESTART_PICKER_TAG.equals(tag)) {
            etTimeStart.setText(dateFormat.format(calendar.getTime()));
        } else {
            etTimeFinish.setText(dateFormat.format(calendar.getTime()));
        }
    }
}