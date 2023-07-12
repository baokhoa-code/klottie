package com.example.androidlottieapp.activities;

import static com.example.androidlottieapp.KLottieApplication.adapter;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidlottieapp.R;

public class ShowCaseActivity extends AppCompatActivity {

    private RecyclerView rclvKlottieItemList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_case);

        rclvKlottieItemList = findViewById(R.id.rclvKlottieItemList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rclvKlottieItemList.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rclvKlottieItemList.addItemDecoration(dividerItemDecoration);

        rclvKlottieItemList.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }
}