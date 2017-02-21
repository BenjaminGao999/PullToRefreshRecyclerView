package com.adnonstop.ptrrecyclerviewbeta2.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.adnonstop.ptrrecyclerviewbeta2.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        Button mbtnPtr = (Button) findViewById(R.id.id_btn_ptr);
        mbtnPtr.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.id_btn_ptr) {
            startActivity(new Intent(this, PTRActivity.class));
        }
    }
}
