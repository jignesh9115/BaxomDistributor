package com.jp.baxomdistributor.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.databinding.ActivitySalesOrderDeliveryScreenBinding;

public class SalesOrderDeliveryScreen extends AppCompatActivity {


    ActivitySalesOrderDeliveryScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySalesOrderDeliveryScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imgBack.setOnClickListener(view -> finish());

    }
}