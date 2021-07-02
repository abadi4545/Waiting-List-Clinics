package com.abadi.waitinglistclinics.View.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.text.LineBreaker;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abadi.waitinglistclinics.R;

public class HelpActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        TextView tvQuestion1 = findViewById(R.id.question1);
        TextView tvQuestion2 = findViewById(R.id.question2);
        TextView tvQuestion3 = findViewById(R.id.question3);
        TextView tvQuestion4 = findViewById(R.id.question4);
        TextView tvAnswer1 = findViewById(R.id.answer1);
        TextView tvAnswer2 = findViewById(R.id.answer2);
        TextView tvAnswer3 = findViewById(R.id.answer3);
        TextView tvAnswer4 = findViewById(R.id.answer4);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tvQuestion1.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
            tvQuestion2.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
            tvQuestion3.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
            tvQuestion4.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
            tvAnswer1.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
            tvAnswer2.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
            tvAnswer3.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
            tvAnswer4.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }

        LinearLayout developer = findViewById(R.id.developer);
        LinearLayout designer = findViewById(R.id.icondesign);

        developer.setOnClickListener(view -> {
            String url = "https://www.instagram.com/wahyoearts/";

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        designer.setOnClickListener(view -> {
            String url = "https://www.flaticon.com/authors/flat-icons";

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });
    }
}