package com.gadarts.neverendingstory.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gadarts.neverendingstory.PolyTaleApplication;
import com.gadarts.neverendingstory.R;
import com.gadarts.neverendingstory.http.AppRequest;
import com.gadarts.neverendingstory.http.HttpCallTask;
import com.gadarts.neverendingstory.http.HttpCallTask.RequestType;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import static com.gadarts.neverendingstory.PolyTaleApplication.HOST;
import static com.gadarts.neverendingstory.StoriesListAdapter.SELECTED_STORY;
import static com.gadarts.neverendingstory.activities.ListActivity.KEY_TITLE;

public class StoryViewActivity extends FragmentActivity {
    public static final String KEY_OWNER = "owner";
    private static final String GET_STORY = HOST + "story";
    private static final String POST_PARAGRAPH = HOST + "paragraph";
    private static final String KEY_REQUEST_ID = "id";
    private static final String KEY_REQUEST_STORY_ID = "story_id";
    private static final String KEY_REQUEST_PARAGRAPH = "paragraph";
    private static final String KEY_NAME = "name";
    private static final String RESPONSE_KEY_PARAGRAPHS = "paragraphs";
    private static final String RESPONSE_KEY_CONTENT = "content";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_view);
        TextView title = findViewById(R.id.story_view_header);
        title.setText("LOADING");
        PolyTaleApplication application = (PolyTaleApplication) getApplication();
        AppRequest appRequest = new AppRequest(GET_STORY, RequestType.GET, (response, context) -> {
            JsonObject data = response.getData();
            title.setText(data.get(KEY_TITLE).getAsString());
            TextView owner = findViewById(R.id.story_view_owner);
            owner.setText(data.get(KEY_OWNER).getAsJsonObject().get(KEY_NAME).getAsString());
            LinearLayout linearLayout = findViewById(R.id.story_view_paragraphs);
            JsonArray paragraphsJsonArray = data.get(RESPONSE_KEY_PARAGRAPHS).getAsJsonArray();
            paragraphsJsonArray.forEach(paragraphJsonObject -> {
                TextView textView = new TextView(context);
                textView.setText(paragraphJsonObject.getAsJsonObject().get(RESPONSE_KEY_CONTENT).getAsString());
                linearLayout.addView(textView);
            });
        });
        long storyId = getIntent().getLongExtra(SELECTED_STORY, 0);
        appRequest.addParameter(KEY_REQUEST_ID, storyId);
        HttpCallTask task = new HttpCallTask(application.getClient(), appRequest, this);
        task.execute();
        Button suggestButton = findViewById(R.id.button_suggest);
        EditText paragraphSuggestionEditText = findViewById(R.id.edit_text_paragraph_suggestion);
        suggestButton.setOnClickListener(view -> {
            paragraphSuggestionEditText.setVisibility(View.VISIBLE);
            findViewById(R.id.button_submit_suggestion).setVisibility(View.VISIBLE);
        });
        Button submitSuggestionButton = findViewById(R.id.button_submit_suggestion);
        submitSuggestionButton.setOnClickListener(view -> {
            AppRequest request = new AppRequest(POST_PARAGRAPH, RequestType.POST,
                    (response, context) -> Toast.makeText(
                            context,
                            "Suggested paragraph",
                            Toast.LENGTH_LONG).show());
            request.addParameter(KEY_REQUEST_STORY_ID, storyId);
            request.addParameter(KEY_REQUEST_PARAGRAPH, paragraphSuggestionEditText.getText());
            new HttpCallTask(application.getClient(), request, getApplicationContext()).execute();
        });
    }
}
