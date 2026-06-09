package com.bank.mobile;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        TextView tvBalance = findViewById(R.id.tvBalance);

        String username = getIntent().getStringExtra("USERNAME");
        double balance = getIntent().getDoubleExtra("BALANCE", 0.0);

        tvWelcome.setText("Welcome, " + username);
        tvBalance.setText(String.format("$%.2f", balance));
    }
}
