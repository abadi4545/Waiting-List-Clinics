package com.abadi.waitinglistclinics.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.abadi.waitinglistclinics.AlarmManagement.AlarmReceiver;
import com.abadi.waitinglistclinics.Model.PatientModel;
import com.abadi.waitinglistclinics.R;
import com.abadi.waitinglistclinics.View.Activity.PatientDetailsActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyQueueAdapter extends RecyclerView.Adapter<MyQueueAdapter.ViewHolder> {

    private Activity mActivity;
    private ArrayList<PatientModel> patientModelArrayList;
    private AlarmReceiver alarmReceiver;

    private Calendar calendar;

    public MyQueueAdapter(Activity mActivity, ArrayList<PatientModel> patientModelArrayList) {
        this.mActivity = mActivity;
        this.patientModelArrayList = patientModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.list_item_queue, parent, false);

        alarmReceiver = new AlarmReceiver();
        calendar = Calendar.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PatientModel patientModel = patientModelArrayList.get(position);

        if (patientModel.getImageDoctor().substring(0, 4).equals("http")) {
            Picasso.get().load(patientModel.getImageDoctor()).into(holder.civProfileDoctor);
        } else {
            Picasso.get().load(R.drawable.icon_default_profile).into(holder.civProfileDoctor);
        }

        holder.tvNameDoctor.setText(patientModel.getNamaDokter());
        holder.tvSpesialisDoctor.setText(patientModel.getPoliDoctor());
        holder.tvDateRegist.setText(patientModel.getTanggalDaftar());
        holder.tvTimeFinish.setText(patientModel.getWaktuSelesai());

        int concatFinishTime = Integer.parseInt(patientModel.getWaktuSelesai().substring(0, 2) +
                patientModel.getWaktuSelesai().substring(3, 5));
        int concatCurrentTime = Integer.parseInt(getCurrentLocalTimeStamp(0).substring(0, 2) +
                getCurrentLocalTimeStamp(0).substring(3, 5));

        if (concatCurrentTime >= concatFinishTime) {
            holder.tvStatus.setTextColor(mActivity.getResources().getColor(R.color.colorAccent));
            //update di my queue
            DatabaseReference rootMyQueue = FirebaseDatabase.getInstance().getReference("MyQueue")
                    .child(patientModel.getIdPasien());
            HashMap<String, Object> myQueue = new HashMap<>();
            myQueue.put("status", "DIPROSES");
            rootMyQueue.child(patientModel.getIdDokter()).updateChildren(myQueue);
            holder.tvStatus.setText(patientModel.getStatus());

            //update di list pasien
            DatabaseReference rootWaitingList = FirebaseDatabase.getInstance().getReference("WaitingList")
                    .child(patientModel.getIdDokter());
            HashMap<String, Object> waitingList = new HashMap<>();
            waitingList.put("waktuSelesai", "DIPROSES");
            rootWaitingList.child(patientModel.getIdAntrian()).updateChildren(waitingList);
        } else {
            holder.tvStatus.setTextColor(mActivity.getResources().getColor(R.color.colorPrimary));
            //update di my queue wkkw
            DatabaseReference rootMyQueue = FirebaseDatabase.getInstance().getReference("MyQueue")
                    .child(patientModel.getIdPasien());
            HashMap<String, Object> myQueue = new HashMap<>();
            myQueue.put("status", "MENUNGGU");
            rootMyQueue.child(patientModel.getIdDokter()).updateChildren(myQueue);
            holder.tvStatus.setText(patientModel.getStatus());
        }

        holder.ivFinish.setOnClickListener(view -> {
            holder.tvStatus.setText(patientModel.getStatus());
            String myQueueId = patientModel.getIdAntrian();

            int lastPos = patientModelArrayList.size() - 1;
            PatientModel lastPatientModel = patientModelArrayList.get(lastPos);
            showDialogAlertUpdate(myQueueId, patientModel.getIdDokter(), patientModel.getNamaDokter(), lastPatientModel.getIdPasien());
        });

        holder.ivCancel.setOnClickListener(view -> {
            String myQueueId = patientModel.getIdAntrian();

            int lastPos = patientModelArrayList.size() - 1;
            PatientModel lastPatientModel = patientModelArrayList.get(lastPos);
            showDialogAlertCancel(myQueueId, patientModel.getIdDokter(), patientModel.getNamaDokter(), lastPatientModel.getIdPasien());
        });

        holder.tvBtnDetails.setOnClickListener(view -> {
            String image = patientModel.getImageURL();
            String name = patientModel.getNamaPasien();
            String noRekamMedis = patientModel.getNoRekamMedis();
            String caraPembayaran = patientModel.getCaraPembayaran();
            String asalRujukan = patientModel.getAsalRujukan();
            String daftar = patientModel.getWaktuDaftar();
            String selesai = patientModel.getWaktuSelesai();
//            String umur = patientModel.getUmurPasien();
//            String jenis = patientModel.getJenisPasien();

            //FOR MY QUEUE DETAILS
            String imageDoctor = patientModel.getImageDoctor();
            String nameDoctor = patientModel.getNamaDokter();
            String poliDoctor = patientModel.getPoliDoctor();
            String dateRegist = patientModel.getTanggalDaftar();

            Intent toPatientDetails = new Intent(mActivity, PatientDetailsActivity.class);
            toPatientDetails.putExtra("image", image);
            toPatientDetails.putExtra("name", name);
            toPatientDetails.putExtra("asalRujukan", asalRujukan);
            toPatientDetails.putExtra("noRekamMedis", noRekamMedis);
            toPatientDetails.putExtra("caraPembayaran", caraPembayaran);
            toPatientDetails.putExtra("daftar", daftar);
            toPatientDetails.putExtra("selesai", selesai);

//            toPatientDetails.putExtra("umur", umur);
//            toPatientDetails.putExtra("jenis", jenis);

            //my queue details
            toPatientDetails.putExtra("imagedoctor", imageDoctor);
            toPatientDetails.putExtra("namedoctor", nameDoctor);
            toPatientDetails.putExtra("poliDoctor", poliDoctor);
            toPatientDetails.putExtra("dateregist", dateRegist);
            mActivity.startActivity(toPatientDetails);
        });

    }

    @Override
    public int getItemCount() {
        return patientModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView civProfileDoctor;
        private TextView tvNameDoctor, tvSpesialisDoctor;
        private TextView tvTimeFinish, tvDateRegist;
        private ImageView ivFinish, ivCancel;
        private TextView tvBtnDetails;
        private TextView tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            civProfileDoctor = itemView.findViewById(R.id.civ_mydoctor_queuelist);
            tvNameDoctor = itemView.findViewById(R.id.tv_namedoctor_queuelist);
            tvSpesialisDoctor = itemView.findViewById(R.id.tv_spesialisdoctor_queuelist);
            tvDateRegist = itemView.findViewById(R.id.tv_dateRegist_queuelist);
            tvTimeFinish = itemView.findViewById(R.id.tv_start_estimation_queuelist);
            tvStatus = itemView.findViewById(R.id.tv_status_queuelist);

            ivFinish = itemView.findViewById(R.id.checklist_queuelist);
            ivCancel = itemView.findViewById(R.id.cancel_queuelist);
            tvBtnDetails = itemView.findViewById(R.id.tv_btnCheck_queuelist);
        }
    }

    private void showDialogAlertUpdate(String myQueueId, String dokterId, String namaDokter, String lastPatientId) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        String userid = firebaseUser.getUid();
        DatabaseReference rootMyQueue = FirebaseDatabase.getInstance().getReference("MyQueue").child(userid);
        DatabaseReference rootWaitingList = FirebaseDatabase.getInstance().getReference("WaitingList").child(dokterId);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setTitle("SELESAI");
        alertDialogBuilder
                .setMessage("Apakah Anda telah selesai diperiksa oleh dokter " + namaDokter + " ?")
                .setCancelable(false)
                .setPositiveButton("Sudah", (dialog, id) -> {

                    //sementara gini aja dah
                    rootMyQueue.child(dokterId).removeValue();

//                    //cek apakah saya pasient terakhir ? //set waktu sekarang buat diberi ke pasien selanjutnya
//                    if (lastPatientId.equals(userid)) {
//                        HashMap<String, Object> hashMap = new HashMap<>();
//                        hashMap.put("lastPatient", getCurrentLocalTimeStamp(0));
//                        DatabaseReference dbRefDoctors = FirebaseDatabase.getInstance().getReference("Doctors");
//                        dbRefDoctors.child(dokterId).updateChildren(hashMap);
//                    }

                    //for patientlist per doctor
                    HashMap<String, Object> waitingList = new HashMap<>();
                    waitingList.put("waktuSelesai", "SELESAI");
                    rootWaitingList.child(myQueueId).updateChildren(waitingList);
                    //batalkan alarm
                    alarmReceiver.cancelAlarm(mActivity);

                    mActivity.overridePendingTransition(0, 0);
                    mActivity.startActivity(mActivity.getIntent());
                    mActivity.overridePendingTransition(0, 0);

                    Toast.makeText(mActivity, "Data berhasil diupdate", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Belum", (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showDialogAlertCancel(String myQueueId, String dokterId, String namaDokter, String lastPatientId) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        String userid = firebaseUser.getUid();
        DatabaseReference rootMyQueue = FirebaseDatabase.getInstance().getReference("MyQueue").child(userid);
        DatabaseReference rootWaitingList = FirebaseDatabase.getInstance().getReference("WaitingList").child(dokterId);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setTitle("BATAL");
        alertDialogBuilder
                .setMessage("Apakah Anda ingin membatalkan antrian tiket pada dokter " + namaDokter + " ?")
                .setCancelable(false)
                .setPositiveButton("iya", (dialog, id) -> {

//                    //set waktu sekarang buat diberi ke pasien selanjutnya
//                    if (lastPatientId.equals(userid)) {
//                        HashMap<String, Object> hashMap = new HashMap<>();
//                        hashMap.put("lastPatient", getCurrentLocalTimeStamp(0));
//                        DatabaseReference dbRefDoctors = FirebaseDatabase.getInstance().getReference("Doctors");
//                        dbRefDoctors.child(dokterId).updateChildren(hashMap);
//                    }

                    //fix delete
                    rootMyQueue.child(dokterId).removeValue();
                    rootWaitingList.child(myQueueId).removeValue();
                    alarmReceiver.cancelAlarm(mActivity);

                    mActivity.overridePendingTransition(0, 0);
                    mActivity.startActivity(mActivity.getIntent());
                    mActivity.overridePendingTransition(0, 0);

                    Toast.makeText(mActivity, "Data berhasil diupdate", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("tidak", (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public String getCurrentLocalTimeStamp(int plus) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        calendar.add(Calendar.MINUTE, plus);
        return currentTime.format(calendar.getTime());
    }
}
