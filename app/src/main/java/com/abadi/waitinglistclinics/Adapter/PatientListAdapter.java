package com.abadi.waitinglistclinics.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.abadi.waitinglistclinics.AlarmManagement.AlarmReceiver;
import com.abadi.waitinglistclinics.Model.PatientModel;
import com.abadi.waitinglistclinics.Model.UserModel;
import com.abadi.waitinglistclinics.R;
import com.abadi.waitinglistclinics.View.Activity.PatientDetailsActivity;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PatientListAdapter extends RecyclerView.Adapter<PatientListAdapter.ViewHolder> {

    private Activity mActivity;
    private ArrayList<PatientModel> patientModelArrayList;

    private String userType;
    private UserModel userModel;
    private AlarmReceiver alarmReceiver;

    public PatientListAdapter(Activity mActivity, ArrayList<PatientModel> patientModelArrayList) {
        this.mActivity = mActivity;
        this.patientModelArrayList = patientModelArrayList;
    }

    @NonNull
    @Override
    public PatientListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.list_item_patient, parent, false);

        userModel = new UserModel();
        getTypeUser();

        alarmReceiver = new AlarmReceiver();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientListAdapter.ViewHolder holder, int position) {
        PatientModel patientModel = patientModelArrayList.get(position);

        if (!patientModel.getImageURL().equals("")) {
            if (patientModel.getImageURL().substring(0, 4).equals("http")) {
                Picasso.get().load(patientModel.getImageURL()).into(holder.civProfilePatient);
            } else {
                Picasso.get().load(R.drawable.icon_default_profile).into(holder.civProfilePatient);
            }
        } else {
            Picasso.get().load(R.drawable.icon_default_profile).into(holder.civProfilePatient);
        }

        holder.tvNamePatient.setText(patientModel.getNamaPasien());
        holder.tvEstimationFinish.setText(patientModel.getWaktuSelesai());

        if (patientModel.getWaktuSelesai().equals("SELESAI")) {
            holder.tvEstimate.setVisibility(View.GONE);
            holder.tvEstimationFinish.setTextColor(mActivity.getResources().getColor(R.color.colorAccent));
        }

        if (patientModel.getWaktuSelesai().equals("DIPROSES")) {
            holder.tvEstimate.setVisibility(View.GONE);
            holder.tvEstimationFinish.setTextColor(mActivity.getResources().getColor(R.color.colorPrimary));
        }

        int queueSort = position + 1;
        holder.tvQueueSort.setText(String.valueOf(queueSort));

        holder.itemView.setOnClickListener(view -> {
            String image = patientModel.getImageURL();
            String name = patientModel.getNamaPasien();
            String noRekamMedis = patientModel.getNoRekamMedis();
            String caraPembayaran = patientModel.getCaraPembayaran();
            String asalRujukan = patientModel.getAsalRujukan();
            String daftar = patientModel.getWaktuDaftar();
            String selesai = patientModel.getWaktuSelesai();

//            String umur = patientModel.getUmurPasien();
//            String jenis = patientModel.getJenisPasien();

            Intent toPatientDetails = new Intent(mActivity, PatientDetailsActivity.class);
            toPatientDetails.putExtra("image", image);
            toPatientDetails.putExtra("name", name);
            toPatientDetails.putExtra("noRekamMedis", noRekamMedis);
            toPatientDetails.putExtra("caraPembayaran", caraPembayaran);
            toPatientDetails.putExtra("asalRujukan", asalRujukan);
            toPatientDetails.putExtra("daftar", daftar);
            toPatientDetails.putExtra("selesai", selesai);

//            toPatientDetails.putExtra("umur", umur);
//            toPatientDetails.putExtra("jenis", jenis);
            mActivity.startActivity(toPatientDetails);
        });

        // DITUTUP DULU - WASPADA JIKA ADMIN DELETE LEWAT PATIENT LIST > BISA CRASH APPS :(

//        holder.itemView.setOnLongClickListener(view -> {
//            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//
//            assert firebaseUser != null;
////            || firebaseUser.getUid().equals(patientModel.getIdPasien()) //akses user untuk delete lewat patient list dihapus sementara
//            if (userType.equals("admin")) {
//                final Dialog dialog = new Dialog(mActivity);
//                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                dialog.setContentView(R.layout.dialog_edit_delete);
//                dialog.show();
//
//                TextView choice = dialog.findViewById(R.id.tv_choice_action);
//                Button editButton = dialog.findViewById(R.id.btnEdit);
//                Button deleteButton = dialog.findViewById(R.id.btnDelete);
//
//                //sementara btn Edit jadi Hapus
//                editButton.setText(R.string.str_delete);
//                choice.setVisibility(View.GONE);
//                deleteButton.setVisibility(View.GONE);
//
//                //apabila tombol edit diklik
//                editButton.setOnClickListener(view1 -> {
//                            dialog.dismiss();
//                            //termasuk menghapus antrian pada user
//                            showDialogAlertDelete(patientModel.getIdDokter(), patientModel.getIdAntrian(), patientModel.getIdPasien());
//                        }
//                );
//
////                //apabila tombol delete diklik
////                deleteButton.setOnClickListener(view2 -> {
////                            dialog.dismiss();
////                            showDialogAlertDelete(patientModel.getIdDokter(), patientModel.getIdPasien());
////                        }
////                );
//            }
//            return true;
//        });
    }

    @Override
    public int getItemCount() {
        return patientModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView civProfilePatient;
        private TextView tvNamePatient, tvEstimationFinish, tvQueueSort, tvEstimate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            civProfilePatient = itemView.findViewById(R.id.civ_patientlist);
            tvNamePatient = itemView.findViewById(R.id.tv_name_patientlist);
            tvEstimationFinish = itemView.findViewById(R.id.tv_estimationfinish_patientlist);
            tvQueueSort = itemView.findViewById(R.id.tv_queuesort_patientlist);
            tvEstimate = itemView.findViewById(R.id.tv_estimationfinish);
        }
    }

    private void getTypeUser() {
        DatabaseReference rootRoomChats = FirebaseDatabase.getInstance().getReference("Users");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        String userid = firebaseUser.getUid();

        rootRoomChats.child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModel = snapshot.getValue(UserModel.class);
                assert userModel != null;

                userType = userModel.getType();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    private void showDialogAlertDelete(String doctorId, String antrianId, String pasienId) {
//        DatabaseReference rootWaitingList = FirebaseDatabase.getInstance().getReference("WaitingList").child(doctorId);
////        DatabaseReference rootMyQueue = FirebaseDatabase.getInstance().getReference("MyQueue");
//
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
//        alertDialogBuilder.setTitle("HAPUS DATA");
//        alertDialogBuilder
//                .setMessage("Apakah Anda yakin ingin menghapus data pendaftaran ini ?")
//                .setCancelable(false)
//                .setPositiveButton("Ya, tentu", (dialog, id) -> {
//
//                    rootWaitingList.child(antrianId).removeValue();
//
//                    // ketika user delete antrian yang sudah selesai lewat list pasien, myqueue pada home si user juga hilang ..
////                    rootMyQueue.child(pasienId).child(doctorId).removeValue(); // JANGAN DIHIDUPIN BAHAYA
//
//                    //batalkan alarm
//                    alarmReceiver.cancelAlarm(mActivity);
//
//                    mActivity.overridePendingTransition(0, 0);
//                    mActivity.startActivity(mActivity.getIntent());
//                    mActivity.finish();
//                    mActivity.overridePendingTransition(0, 0);
//
//                    Toast.makeText(mActivity, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
//                })
//                .setNegativeButton("Gak jadi", (dialog, id) -> dialog.cancel());
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//    }
}
