package com.gadarts.neverendingstory.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gadarts.neverendingstory.HttpCallTask;
import com.gadarts.neverendingstory.HttpCallTask.RequestTypes;
import com.gadarts.neverendingstory.R;

import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

public class LoginActivity extends FragmentActivity {
    private static final String LOGIN = ListActivity.HOST + "login";
    private static final String KEY_MAIL = "email";
    private static final String KEY_PASSWORD = "password";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button loginButton = findViewById(R.id.button_login);
        EditText mailInput = findViewById(R.id.mail);
        EditText passwordInput = findViewById(R.id.password);
        loginButton.setOnClickListener(button -> performLogin(mailInput, passwordInput));
    }

    private void performLogin(EditText mailInput, EditText passwordInput) {
        HttpCallTask task = new HttpCallTask(LOGIN, RequestTypes.POST,
                (response, gson) -> {
                    Intent intent = new Intent(this, ListActivity.class);
                    startActivity(intent);
                },
                (response, gson) -> Toast.makeText(
                        LoginActivity.this,
                        "Login Failed",
                        Toast.LENGTH_LONG).show());
        task.setParameters(createLoginParameters(mailInput, passwordInput));
        task.execute();
    }

    private HashMap<String, String> createLoginParameters(EditText mailInput, EditText passwordInput) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(KEY_MAIL, String.valueOf(mailInput.getText()));
        parameters.put(KEY_PASSWORD, String.valueOf(passwordInput.getText()));
        return parameters;
    }
}
