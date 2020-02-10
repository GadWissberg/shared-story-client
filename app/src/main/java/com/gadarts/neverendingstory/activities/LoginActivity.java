package com.gadarts.neverendingstory.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.gadarts.neverendingstory.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

public class LoginActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Button loginButton = findViewById(R.id.button_login);
        loginButton.setOnClickListener(button -> Toast
                .makeText(LoginActivity.this, "TEST", Toast.LENGTH_LONG)
                .show());
    }
}
