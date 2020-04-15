package com.gadarts.neverendingstory.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gadarts.neverendingstory.PolyTaleApplication;
import com.gadarts.neverendingstory.R;
import com.gadarts.neverendingstory.http.HttpCallTask;
import com.gadarts.neverendingstory.http.HttpCallTask.RequestType;
import com.gadarts.neverendingstory.http.OnRequestResult;
import com.gadarts.neverendingstory.http.ServerResponse;

import java.util.HashMap;

import androidx.fragment.app.FragmentActivity;
import okhttp3.OkHttpClient;

public class NewStoryActivity extends FragmentActivity {


    private static final String POST_NEW_STORY = ListActivity.HOST + "begin_story";
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
            OnRequestResult onSuccess = (ServerResponse response) -> {
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
        OkHttpClient client = ((PolyTaleApplication) getApplication()).getClient();
        HttpCallTask task = new HttpCallTask(client, POST_NEW_STORY, RequestType.POST, onSuccess);
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(PARAMETER_TITLE, String.valueOf(titleEditText.getText()));
        parameters.put(PARAMETER_PARAGRAPH, String.valueOf(paragraphEditText.getText()));
        task.setParameters(parameters);
        task.execute();
    }


}
