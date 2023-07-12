package com.example.androidlottieapp.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidlottieapp.R;

public class testActivity extends AppCompatActivity {

    private RecyclerView rclv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

//        rclv = findViewById(R.id.rclv);
//
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        rclv.setLayoutManager(linearLayoutManager);
//
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        rclv.addItemDecoration(dividerItemDecoration);
//
//        ArrayList<EditOption> list = new ArrayList<>();
//
//        list.add(new EditOption(EditOption.SPEED));
//        list.add(new EditOption(EditOption.BACK_GROUND));
//        list.add(new EditOption(EditOption.TRIM));
//        list.add(new EditOption(EditOption.OPTION));
//
//        EditOptionAdapter adapter = new EditOptionAdapter(this,list, new KLottieDrawable());
//
//        rclv.setAdapter(adapter);
    }
}