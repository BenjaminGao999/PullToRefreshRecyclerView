package com.adnonstop.normalsample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.adnonstop.normalsample.R;

public class EnterMainActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enteractivity_main);
        init();
    }

    private void init() {
        Button btnWithHeader = (Button) findViewById(R.id.id_btn_withheader);
        Button btnSimple = (Button) findViewById(R.id.id_btn_simple);
        Button btnRefresh = (Button) findViewById(R.id.id_btn_refresh);
        btnWithHeader.setOnClickListener(this);
        btnSimple.setOnClickListener(this);
        btnRefresh.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_btn_withheader:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.id_btn_simple:
                startActivity(new Intent(this, SimpleMainActivity.class));
                break;
            case R.id.id_btn_refresh:
                startActivity(new Intent(this, RefreshActivity.class));
                break;

            default:
                break;
        }
    }
}
