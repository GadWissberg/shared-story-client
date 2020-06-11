package com.gadarts.neverendingstory.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.util.Patterns;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.Optional;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import okhttp3.OkHttpClient;

import static com.gadarts.neverendingstory.PolyTaleApplication.HOST;

public class SignUpActivity extends FragmentActivity {
    static final String PREFS_LOGIN = "login";
    static final String KEY_MAIL = "email";
    static final String KEY_PASS = "password";
    static final String KEY_NAME = "name";
    private static final String SIGNUP = HOST + "signup";
    private static final String VALIDATION_MSG_EMPTY = "The field %s cannot be empty.";
    private static final String VALIDATION_MSG_INVALID = "The given e-mail is invalid";
    private static final int PASS_MIN_SIZE = 8;
    private static final int USER_MIN_SIZE = 3;
    private static final String VALIDATION_MSG_PASS_SHORT = "The given password is too short. It has to be atleast 8 characters.";
    private static final String VALIDATION_MSG_NAME_SHORT = "The given user-name is too short. It has to be atleast 3 characters.";
    private static final String OPTION_AUTO_LOGIN = "auto_login";
    private static final String MSG_PASS_DONT_MATCH = "The given passwords do not match.";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        TextView loginInsteadLink = findViewById(R.id.link_login);
        loginInsteadLink.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        Button signupButton = initializeSignupButton();
        TextView userNameInput = findViewById(R.id.input_user_name);
        userNameInput.setOnEditorActionListener((v, actionId, event) -> {
            if ((actionId == EditorInfo.IME_ACTION_DONE)) {
                signupButton.callOnClick();
            }
            return false;
        });
    }

    @NotNull
    private Button initializeSignupButton() {
        EditText mailInput = findViewById(R.id.input_mail);
        EditText passwordInput = findViewById(R.id.input_password);
        EditText repeatPasswordInput = findViewById(R.id.input_repeat_password);
        EditText userNamePasswordInput = findViewById(R.id.input_user_name);
        Button signupButton = findViewById(R.id.button_signup);
        signupButton.setOnClickListener(button -> {
            String passwordInputText = String.valueOf(passwordInput.getText());
            if (passwordInputText.equals(String.valueOf(repeatPasswordInput.getText()))) {
                Editable mailInputText = mailInput.getText();
                Editable userNameInputText = userNamePasswordInput.getText();
                String validationMsg = validateLocallyCredentials(mailInputText, passwordInputText, userNameInputText);
                Optional<String> validation = Optional.ofNullable(validationMsg);
                if (!validation.isPresent()) {
                    executeSignupRequest(mailInputText, passwordInputText, userNameInputText);
                } else {
                    Toast.makeText(SignUpActivity.this, validation.get(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), MSG_PASS_DONT_MATCH, Toast.LENGTH_LONG).show();
            }
        });
        return signupButton;
    }

    private void executeSignupRequest(Editable mailInput, String passwordInput, Editable userNameInputText) {
        OnResults onRequestResults = new OnResults((response, context) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_LOGIN, MODE_PRIVATE).edit();
            editor.putString(KEY_MAIL, String.valueOf(mailInput));
            editor.putString(KEY_PASS, String.valueOf(passwordInput));
            editor.apply();
            Intent intent = new Intent(this, LoginActivity.class);
            Toast.makeText(getApplicationContext(), "Welcome!", Toast.LENGTH_LONG).show();
            startActivity(intent);
            finish();
        },
                (response, context) -> {
                    SignUpActivity.this.runOnUiThread(() -> Toast.makeText(
                            SignUpActivity.this,
                            response.getMessage(),
                            Toast.LENGTH_LONG).show());
                    goToLoginIfNoResponseWasFound(response);
                });
        OkHttpClient client = ((PolyTaleApplication) getApplication()).getClient();
        AppRequest request = new AppRequest(SIGNUP, RequestType.POST, onRequestResults);
        request.setParameters(createSignupParameters(mailInput, passwordInput, userNameInputText));
        HttpCallTask task = new HttpCallTask(client, request, getApplicationContext());
        task.execute();
    }

    private void goToLoginIfNoResponseWasFound(ServerResponse response) {
        if (response.getCode() == 0) {
            Intent intent = new Intent(this, SignUpActivity.class);
            intent.putExtra(OPTION_AUTO_LOGIN, false);
            startActivity(intent);
            finish();
        }
    }

    private String validateLocallyCredentials(Editable mail, String pass, Editable userName) {
        if (mail.length() == 0) return String.format(VALIDATION_MSG_EMPTY, KEY_MAIL);
        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) return VALIDATION_MSG_INVALID;
        if (pass.length() == 0) return String.format(VALIDATION_MSG_EMPTY, KEY_PASS);
        if (pass.length() < PASS_MIN_SIZE) return VALIDATION_MSG_PASS_SHORT;
        if (userName.length() < USER_MIN_SIZE) return VALIDATION_MSG_NAME_SHORT;
        return null;
    }

    private HashMap<String, Object> createSignupParameters(Editable mailInput,
                                                           String passwordInput,
                                                           Editable userNameInputText) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(KEY_MAIL, String.valueOf(mailInput));
        parameters.put(KEY_PASS, String.valueOf(passwordInput));
        parameters.put(KEY_NAME, String.valueOf(userNameInputText));
        return parameters;
    }
}
