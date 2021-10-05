package com.jp.baxomdistributor.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.jp.baxomdistributor.databinding.ActivityChooseLanguageBinding;


public class ChooseLanguageActivity extends AppCompatActivity {

    ActivityChooseLanguageBinding binding;

    SharedPreferences sp_multi_lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseLanguageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sp_multi_lang = getSharedPreferences("Language", Context.MODE_PRIVATE);

        binding.btnNext.setOnClickListener(v -> {

            if (binding.rbEngChooseLang.isChecked()) {

                SharedPreferences.Editor editor = sp_multi_lang.edit();
                editor.putString("lang", "ENG");
                editor.apply();

            } else if (binding.rbGujChooseLang.isChecked()) {

                SharedPreferences.Editor editor = sp_multi_lang.edit();
                editor.putString("lang", "GUJ");
                editor.apply();

            } else if (binding.rbHindiChooseLang.isChecked()) {

                SharedPreferences.Editor editor = sp_multi_lang.edit();
                editor.putString("lang", "HINDI");
                editor.apply();
            }

            Intent intent = new Intent(ChooseLanguageActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        });
    }
}