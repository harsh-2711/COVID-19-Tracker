package com.developer.abhinavraj.covid19tracker.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.developer.abhinavraj.covid19tracker.R;

public class AboutActivity extends AppCompatActivity {

    LinearLayout contactUs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        contactUs = findViewById(R.id.contactUs);
        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = "mailto:dscdaiict@gmail.com";
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(mail));
                intent.putExtra(Intent.EXTRA_SUBJECT, "COVID-19 Tracker User Query");
                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });
    }
}
