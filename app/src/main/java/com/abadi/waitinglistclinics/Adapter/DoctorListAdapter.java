package com.abadi.waitinglistclinics.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.abadi.waitinglistclinics.Model.DoctorModel;
import com.abadi.waitinglistclinics.Model.UserModel;
import com.abadi.waitinglistclinics.R;
import com.abadi.waitinglistclinics.View.Activity.AddUpdateDoctorActivity;
import com.abadi.waitinglistclinics.View.Activity.DoctorDetailsActivity;
import com.abadi.waitinglistclinics.View.Activity.PickQueuePatientActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorListAdapter extends RecyclerView.Adapter<DoctorListAdapter.ViewHolder> {

    private Activity mActivity;
    private ArrayList<DoctorModel> doctorModelArrayList;

    private String userType;
    private UserModel userModel;
    private String isDaftar;
    private Calendar calendar;

    public DoctorListAdapter(Activity mActivity, ArrayList<DoctorModel> doctorModelArrayList) {
        this.mActivity = mActivity;
        this.doctorModelArrayList = doctorModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.list_item_doctor, parent, false);
        userModel = new UserModel();
        getTypeUser();

        //pick doctor name and id
        Intent data = mActivity.getIntent();
        isDaftar = data.getStringExtra("daftar");
        calendar = Calendar.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DoctorModel doctorModel = doctorModelArrayList.get(position);
        if (doctorModel.getImageURL().substring(0, 4).equals("http")) {
            Picasso.get().load(doctorModel.getImageURL()).into(holder.civProfileDoctor);
        } else {
            Picasso.get().load(R.drawable.icon_default_profile).into(holder.civProfileDoctor);
        }

        holder.tvNameDr.setText(doctorModel.getName());
        holder.tvSpesialis.setText(doctorModel.getPoliDoctor());

        if (isDaftar != null) {
            holder.tvOpen.setText(doctorModel.getWorktimestart());
            holder.tvClose.setText(doctorModel.getWorktimefinish());
            holder.openClose.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(view -> {
            String doctorId = doctorModel.getId();
            String namedr = doctorModel.getName();
            String imgProfile = doctorModel.getImageURL();
            String poliDoctor = doctorModel.getPoliDoctor();
            String workday = doctorModel.getWorkday();
            String timestart = doctorModel.getWorktimestart();
            String timefinish = doctorModel.getWorktimefinish();
            String limit = doctorModel.getLimit();

            if (isDaftar != null) {
                //ketika hari sekarang tidak sama hari pada pasien terakhir di dokter tersebut
                if (!doctorModel.getLastDate().equals(getCurrentLocalDateStamp())) {
                    //update tgl sekarang
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("lastDate", getCurrentLocalDateStamp());
                    DatabaseReference dbRefDoctors = FirebaseDatabase.getInstance().getReference("Doctors");
                    dbRefDoctors.child(doctorId).updateChildren(hashMap);

                    int convertTimeStart = Integer.parseInt(doctorModel.getWorktimestart().substring(0, 2) +
                            doctorModel.getWorktimestart().substring(3, 5));
                    int convertTimeFinish = Integer.parseInt(doctorModel.getWorktimefinish().substring(0, 2) +
                            doctorModel.getWorktimefinish().substring(3, 5));
                    int convertCurrentTime = Integer.parseInt(getCurrentLocalTimeStamp(0).substring(0, 2) +
                            getCurrentLocalTimeStamp(0).substring(3, 5));

                    if (convertCurrentTime >= convertTimeStart && convertCurrentTime <= convertTimeFinish) {
                        Intent toRegis = new Intent(mActivity, PickQueuePatientActivity.class);
                        toRegis.putExtra("id_doctor", doctorId);
                        toRegis.putExtra("name_doctor", namedr);
                        toRegis.putExtra("image_doctor", imgProfile);
                        toRegis.putExtra("poliDoctor", poliDoctor);
                        toRegis.putExtra("last_time", "kosong"); // update ke kosong
                        mActivity.startActivity(toRegis);
                        mActivity.finish();
                    } else {
                        Toast.makeText(mActivity, "Maaf, " + doctorModel.getName() + " sudah tutup", Toast.LENGTH_SHORT).show();
                    }
                } else { // ketika sama maka gak di upadate kosong
                    int convertTimeStart = Integer.parseInt(doctorModel.getWorktimestart().substring(0, 2) +
                            doctorModel.getWorktimestart().substring(3, 5));
                    int convertTimeFinish = Integer.parseInt(doctorModel.getWorktimefinish().substring(0, 2) +
                            doctorModel.getWorktimefinish().substring(3, 5));
                    int convertCurrentTime = Integer.parseInt(getCurrentLocalTimeStamp(0).substring(0, 2) +
                            getCurrentLocalTimeStamp(0).substring(3, 5));

                    if (convertCurrentTime >= convertTimeStart && convertCurrentTime <= convertTimeFinish) {
                        String lastTimePatient = doctorModel.getLastPatient();

                        Intent toRegis = new Intent(mActivity, PickQueuePatientActivity.class);
                        toRegis.putExtra("id_doctor", doctorId);
                        toRegis.putExtra("name_doctor", namedr);
                        toRegis.putExtra("image_doctor", imgProfile);
                        toRegis.putExtra("poliDoctor", poliDoctor);
                        toRegis.putExtra("last_time", lastTimePatient);
                        mActivity.startActivity(toRegis);
                        mActivity.finish();
                    } else {
                        Toast.makeText(mActivity, "Maaf, " + doctorModel.getName() + " sudah tutup", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Intent toDetails = new Intent(mActivity, DoctorDetailsActivity.class);
                toDetails.putExtra("id", doctorId);
                toDetails.putExtra("name", namedr);
                toDetails.putExtra("imgprofile", imgProfile);
                toDetails.putExtra("poliDoctor", poliDoctor);
                toDetails.putExtra("workday", workday);
                toDetails.putExtra("timestart", timestart);
                toDetails.putExtra("timefinish", timefinish);
                toDetails.putExtra("limit", limit);
                mActivity.startActivity(toDetails);
            }
        });

        holder.itemView.setOnLongClickListener(view -> {
            if (userType.equals("admin")) {
                final Dialog dialog = new Dialog(mActivity);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.dialog_edit_delete);
                dialog.show();

                Button editButton = dialog.findViewById(R.id.btnEdit);
                Button deleteButton = dialog.findViewById(R.id.btnDelete);

                //apabila tombol edit diklik
                editButton.setOnClickListener(view1 -> {
                            dialog.dismiss();
                            String doctorId = doctorModel.getId();
                            String namedr = doctorModel.getName();
                            String imgProfile = doctorModel.getImageURL();
                            String poliDoctor = doctorModel.getPoliDoctor();
                            String workday = doctorModel.getWorkday();
                            String timestart = doctorModel.getWorktimestart();
                            String timefinish = doctorModel.getWorktimefinish();
                            String limit = doctorModel.getLimit();
                            String lastPatient = doctorModel.getLastPatient();

                            Intent toUpdate = new Intent(mActivity, AddUpdateDoctorActivity.class);
                            toUpdate.putExtra("id", doctorId);
                            toUpdate.putExtra("name", namedr);
                            toUpdate.putExtra("imgprofile", imgProfile);
                            toUpdate.putExtra("poliDoctor", poliDoctor);
                            toUpdate.putExtra("workday", workday);
                            toUpdate.putExtra("timestart", timestart);
                            toUpdate.putExtra("timefinish", timefinish);
                            toUpdate.putExtra("limit", limit);
                            toUpdate.putExtra("lastpatient", lastPatient);
                            mActivity.startActivity(toUpdate);
                        }
                );

                //apabila tombol delete diklik
                deleteButton.setOnClickListener(view2 -> {
                            dialog.dismiss();
                            showDialogAlertDelete(doctorModel.getId());
                        }
                );
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return doctorModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView civProfileDoctor;
        private TextView tvNameDr, tvSpesialis, tvOpen, tvClose;
        private LinearLayout openClose;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            civProfileDoctor = itemView.findViewById(R.id.civ_doctorlist);
            tvNameDr = itemView.findViewById(R.id.tv_name_doctorlist);
            tvSpesialis = itemView.findViewById(R.id.tv_spesialis_doctorlist);
            openClose = itemView.findViewById(R.id.ll_openclose_doctorlist);
            tvOpen = itemView.findViewById(R.id.tv_open_doctorlist);
            tvClose = itemView.findViewById(R.id.tv_close_doctorlist);
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

    public String getCurrentLocalTimeStamp(int plus) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        calendar.add(Calendar.MINUTE, plus);
        return currentTime.format(calendar.getTime());
    }

    public String getCurrentLocalDateStamp() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd MMM, yyyy");
        return currentDate.format(calendar.getTime());
    }

    private void showDialogAlertDelete(String key) {
        DatabaseReference rootDoctors = FirebaseDatabase.getInstance().getReference("Doctors");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setTitle("HAPUS DATA DOKTER");
        alertDialogBuilder
                .setMessage("Apakah Anda yakin ingin menghapus data user ini ?")
                .setCancelable(false)
                .setPositiveButton("Ya, tentu", (dialog, id) -> {
                    rootDoctors.child(key).removeValue();

                    mActivity.overridePendingTransition(0, 0);
                    mActivity.startActivity(mActivity.getIntent());
                    mActivity.overridePendingTransition(0, 0);

                    Toast.makeText(mActivity, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Gak jadi", (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
