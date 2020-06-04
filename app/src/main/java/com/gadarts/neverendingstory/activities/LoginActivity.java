package com.gadarts.neverendingstory.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gadarts.neverendingstory.PolyTaleApplication;
import com.gadarts.neverendingstory.R;
import com.gadarts.neverendingstory.services.http.AppRequest;
import com.gadarts.neverendingstory.services.http.HttpCallTask;
import com.gadarts.neverendingstory.services.http.HttpCallTask.RequestType;
import com.gadarts.neverendingstory.services.http.OnResults;
import com.gadarts.neverendingstory.services.http.ServerResponse;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import okhttp3.OkHttpClient;

import static com.gadarts.neverendingstory.PolyTaleApplication.HOST;

public class LoginActivity extends FragmentActivity {
    static final String PREFS_LOGIN = "login";
    static final String KEY_MAIL = "email";
    static final String KEY_PASS = "password";
    private static final String LOGIN = HOST + "login";
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
        Locale.getDefault().getDisplayLanguage();
    }

    private void decideAutoLoginOrLoginPage(String mail, String password) {
        boolean opt = getIntent().getBooleanExtra(OPTION_AUTO_LOGIN, true);
        if (opt && Optional.ofNullable(mail).isPresent() && Optional.ofNullable(password).isPresent())
            performLogin(mail, password);
        else setLoginView();
    }

    private void setLoginView() {
        setContentView(R.layout.activity_login);
        EditText mailInput = findViewById(R.id.mail);
        EditText passwordInput = findViewById(R.id.password);
        Button loginButton = initializeLoginButton(mailInput, passwordInput);
        passwordInput.setOnEditorActionListener((v, actionId, event) -> {
            if ((actionId == EditorInfo.IME_ACTION_DONE)) {
                loginButton.callOnClick();
            }
            return false;
        });
    }

    @NotNull
    private Button initializeLoginButton(EditText mailInput, EditText passwordInput) {
        Button loginButton = findViewById(R.id.button_login);
        loginButton.setOnClickListener(button -> performLogin(
                String.valueOf(mailInput.getText()),
                String.valueOf(passwordInput.getText())));
        return loginButton;
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
        OnResults onRequestResults = new OnResults((response, context) -> {
            SharedPreferences prefs = getSharedPreferences(PREFS_LOGIN, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_MAIL, mailInput);
            editor.putString(KEY_PASS, passwordInput);
            editor.apply();
            Intent intent = new Intent(this, ListActivity.class);
            startActivity(intent);
            finish();
        },
                (response, context) -> {
                    LoginActivity.this.runOnUiThread(() -> Toast.makeText(
                            LoginActivity.this,
                            response.getMessage(),
                            Toast.LENGTH_LONG).show());
                    goToLoginIfNoResponseWasFound(response);
                });
        OkHttpClient client = ((PolyTaleApplication) getApplication()).getClient();
        AppRequest request = new AppRequest(LOGIN, RequestType.POST, onRequestResults);
        request.setParameters(createLoginParameters(mailInput, passwordInput));
        HttpCallTask task = new HttpCallTask(client, request, getApplicationContext());
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

    private HashMap<String, Object> createLoginParameters(String mailInput, String passwordInput) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(KEY_MAIL, String.valueOf(mailInput));
        parameters.put(KEY_PASS, String.valueOf(passwordInput));
        return parameters;
    }
}
