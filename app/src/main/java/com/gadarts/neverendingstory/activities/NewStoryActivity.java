package com.gadarts.neverendingstory.activities;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gadarts.neverendingstory.OurTaleApplication;
import com.gadarts.neverendingstory.R;
import com.gadarts.neverendingstory.services.http.AppRequest;
import com.gadarts.neverendingstory.services.http.HttpCallTask;
import com.gadarts.neverendingstory.services.http.HttpCallTask.RequestType;
import com.gadarts.neverendingstory.services.http.OnRequestResult;
import com.gadarts.neverendingstory.services.http.ServerResponse;

import java.util.HashMap;

import androidx.fragment.app.FragmentActivity;
import okhttp3.OkHttpClient;

import static com.gadarts.neverendingstory.OurTaleApplication.HOST;


public class NewStoryActivity extends FragmentActivity {


    private static final String POST_NEW_STORY = HOST + "story";
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_PARAGRAPH = "paragraph";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_story);
        Button publishButton = findViewById(R.id.new_story_publish_button);
        EditText titleEditText = findViewById(R.id.new_story_title_input);
        EditText paragraphEditText = findViewById(R.id.new_story_paragraph_input);
        publishButton.setOnClickListener(view -> {
            OnRequestResult onSuccess = (ServerResponse response, Context context) -> {
                setResult(RESULT_OK);
                finish();
                Toast.makeText(
                        NewStoryActivity.this,
                        "Story Created!",
                        Toast.LENGTH_LONG).show();
            };
            submitStory(titleEditText, paragraphEditText, onSuccess);
        });
    }

    private void submitStory(EditText titleEditText,
                             EditText paragraphEditText,
                             OnRequestResult onSuccess) {
        OkHttpClient client = ((OurTaleApplication) getApplication()).getClient();
        AppRequest request = new AppRequest(POST_NEW_STORY, RequestType.POST, onSuccess);
        HttpCallTask task = new HttpCallTask(client, request, getApplicationContext());
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(PARAMETER_TITLE, String.valueOf(titleEditText.getText()));
        parameters.put(PARAMETER_PARAGRAPH, String.valueOf(paragraphEditText.getText()));
        request.setParameters(parameters);
        task.execute();
    }


}
