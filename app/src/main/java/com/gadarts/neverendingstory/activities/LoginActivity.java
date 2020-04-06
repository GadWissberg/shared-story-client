package com.gadarts.neverendingstory.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gadarts.neverendingstory.HttpCallTask;
import com.gadarts.neverendingstory.HttpCallTask.RequestTypes;
import com.gadarts.neverendingstory.R;
import com.gadarts.neverendingstory.ServerResponse;

import java.util.HashMap;
import java.util.Optional;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

public class LoginActivity extends FragmentActivity {
    public static final String PREFS_LOGIN = "login";
    static final String KEY_MAIL = "email";
    static final String KEY_PASS = "password";
    private static final String LOGIN = ListActivity.HOST + "login";
    private static final String VALIDATION_MSG_EMPTY = "The field %s cannot be empty.";
    private static final String VALIDATION_MSG_INVALID = "The given e-mail is invalid";
    private static final int PASS_MIN_SIZE = 8;
    private static final String VALIDATION_MSG_PASS_SHORT = "The given password is too short. It has to be atleast 8 characters.";
    private static final String OPTION_AUTO_LOGIN = "auto_login";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_LOGIN, MODE_PRIVATE);
        String mail = sharedPreferences.getString(KEY_MAIL, null);
        String password = sharedPreferences.getString(KEY_PASS, null);
        decideAutoLoginOrLoginPage(mail, password);
    }

    private void decideAutoLoginOrLoginPage(String mail, String password) {
        boolean opt = getIntent().getBooleanExtra(OPTION_AUTO_LOGIN, true);
        if (opt && Optional.ofNullable(mail).isPresent() && Optional.ofNullable(password).isPresent())
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
        String validation = validateLocallyCredentials(mailInput, passwordInput);
        if (!Optional.ofNullable(validation).isPresent()) {
            executeLoginRequest(mailInput, passwordInput);
        } else {
            Toast.makeText(LoginActivity.this, validation, Toast.LENGTH_LONG).show();
        }
    }

    private void executeLoginRequest(String mailInput, String passwordInput) {
        HttpCallTask task = new HttpCallTask(LOGIN, RequestTypes.POST,
                (response) -> {
                    SharedPreferences prefs = getSharedPreferences(PREFS_LOGIN, MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(KEY_MAIL, mailInput);
                    editor.putString(KEY_PASS, passwordInput);
                    editor.apply();
                    Intent intent = new Intent(this, ListActivity.class);
                    startActivity(intent);
                    finish();
                },
                (response) -> {
                    LoginActivity.this.runOnUiThread(() -> Toast.makeText(
                            LoginActivity.this,
                            response.getMessage(),
                            Toast.LENGTH_LONG).show());
                    goToLoginIfNoResponseWasFound(response);
                });
        task.setParameters(createLoginParameters(mailInput, passwordInput));
        task.execute();
    }

    private void goToLoginIfNoResponseWasFound(ServerResponse response) {
        if (response.getCode() == 0) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra(OPTION_AUTO_LOGIN, false);
            startActivity(intent);
            finish();
        }
    }

    private String validateLocallyCredentials(String mail, String pass) {
        if (mail.isEmpty()) return String.format(VALIDATION_MSG_EMPTY, KEY_MAIL);
        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) return VALIDATION_MSG_INVALID;
        if (pass.isEmpty()) return String.format(VALIDATION_MSG_EMPTY, KEY_PASS);
        if (pass.length() < PASS_MIN_SIZE) return VALIDATION_MSG_PASS_SHORT;
        return null;
    }

    private HashMap<String, String> createLoginParameters(String mailInput, String passwordInput) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(KEY_MAIL, String.valueOf(mailInput));
        parameters.put(KEY_PASS, String.valueOf(passwordInput));
        return parameters;
    }
}
