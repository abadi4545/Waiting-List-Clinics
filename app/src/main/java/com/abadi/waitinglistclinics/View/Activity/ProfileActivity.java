package com.abadi.waitinglistclinics.View.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.abadi.waitinglistclinics.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView civAddDoctor, civProfileUser;
    private TextView tvNameUser;
    private TextView tvEmailUser;
    private FrameLayout flAddNewDoctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        civAddDoctor = findViewById(R.id.add_newdoctor);
        civProfileUser = findViewById(R.id.civ_profile);
        tvNameUser = findViewById(R.id.tv_name_profile);
        tvEmailUser = findViewById(R.id.tv_email_profile);
        flAddNewDoctor = findViewById(R.id.fl_add_newdoctor);
        ImageView btnBack = findViewById(R.id.btnback_profile);

        Intent data = getIntent();
        String userType = data.getStringExtra("usertype");
        String imageURL = data.getStringExtra("image");
        String name = data.getStringExtra("name");
        String email = data.getStringExtra("email");

        if (userType != null && imageURL != null && name != null && email != null) {
            if (userType.equals("admin")) {
                flAddNewDoctor.setVisibility(View.VISIBLE);
            }

            if (imageURL.substring(0, 4).equals("http")) {
                Picasso.get().load(imageURL).into(civProfileUser);
            } else {
                Picasso.get().load(R.drawable.icon_default_profile).into(civProfileUser);
            }
            tvNameUser.setText(name);
            tvEmailUser.setText(email);
        }

        civAddDoctor.setOnClickListener(view -> {
            startActivity(new Intent(ProfileActivity.this, AddUpdateDoctorActivity.class));
            finish();
        });

        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            finish();
        });
    }
}