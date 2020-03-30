package com.gadarts.neverendingstory.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gadarts.neverendingstory.HttpCallTask;
import com.gadarts.neverendingstory.HttpCallTask.RequestTypes;
import com.gadarts.neverendingstory.R;

import java.util.HashMap;
import java.util.Optional;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

public class LoginActivity extends FragmentActivity {
    private static final String LOGIN = ListActivity.HOST + "login";
    public static final String PREFS_LOGIN = "login";
    static final String KEY_MAIL = "email";
    static final String KEY_PASSWORD = "password";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_LOGIN, MODE_PRIVATE);
        String mail = sharedPreferences.getString(KEY_MAIL, null);
        String password = sharedPreferences.getString(KEY_PASSWORD, null);
        if (Optional.ofNullable(mail).isPresent() && Optional.ofNullable(password).isPresent())
            performLogin(mail, password);
        else setLoginView();
    }

    private void setLoginView() {
        setContentView(R.layout.activity_login);
        Button loginButton = findViewById(R.id.button_login);
        EditText mailInput = findViewById(R.id.mail);
        EditText passwordInput = findViewById(R.id.password);
        loginButton.setOnClickListener(button -> performLogin(
                String.valueOf(mailInput.getText()),
                String.valueOf(passwordInput.getText())));
    }

    private void performLogin(String mailInput, String passwordInput) {
        HttpCallTask task = new HttpCallTask(LOGIN, RequestTypes.POST,
                (response) -> {
                    SharedPreferences prefs = getSharedPreferences(PREFS_LOGIN, MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(KEY_MAIL, String.valueOf(mailInput));
                    editor.putString(KEY_PASSWORD, String.valueOf(passwordInput));
                    editor.apply();
                    Intent intent = new Intent(this, ListActivity.class);
                    startActivity(intent);
                },
                (response) -> LoginActivity.this.runOnUiThread(() -> Toast.makeText(
                        LoginActivity.this,
                        response.getMessage(),
                        Toast.LENGTH_LONG).show()));
        task.setParameters(createLoginParameters(mailInput, passwordInput));
        task.execute();
    }

    private HashMap<String, String> createLoginParameters(String mailInput, String passwordInput) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(KEY_MAIL, String.valueOf(mailInput));
        parameters.put(KEY_PASSWORD, String.valueOf(passwordInput));
        return parameters;
    }
}
