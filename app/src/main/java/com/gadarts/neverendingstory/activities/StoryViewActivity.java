package com.gadarts.neverendingstory.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gadarts.neverendingstory.OurTaleApplication;
import com.gadarts.neverendingstory.R;
import com.gadarts.neverendingstory.models.Paragraph;
import com.gadarts.neverendingstory.models.Story;
import com.gadarts.neverendingstory.services.DataInflater;
import com.gadarts.neverendingstory.services.http.AppRequest;
import com.gadarts.neverendingstory.services.http.HttpCallTask;
import com.gadarts.neverendingstory.services.http.HttpCallTask.RequestType;
import com.gadarts.neverendingstory.services.http.ServerResponse;
import com.google.gson.JsonObject;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import static com.gadarts.neverendingstory.OurTaleApplication.HOST;
import static com.gadarts.neverendingstory.StoriesListAdapter.SELECTED_STORY;

public class StoryViewActivity extends FragmentActivity {
    private static final String GET_STORY = HOST + "story";
    private static final String POST_PARAGRAPH_SUGGESTION = HOST + "paragraph_suggestion";
    private static final String KEY_REQUEST_ID = "id";
    private static final String KEY_REQUEST_STORY_ID = "story_id";
    private static final String KEY_REQUEST_PARAGRAPH = "paragraph";
    private static final String LABEL_BY = "By %s";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_view);
        TextView title = findViewById(R.id.story_view_header);
        title.setText("LOADING");
        OurTaleApplication application = (OurTaleApplication) getApplication();
        long storyId = getIntent().getLongExtra(SELECTED_STORY, 0);
        AppRequest appRequest = new AppRequest(GET_STORY, RequestType.GET, (response, context) -> initializeStoryView(storyId, response));
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
            AppRequest request = new AppRequest(POST_PARAGRAPH_SUGGESTION, RequestType.POST,
                    (response, context) -> Toast.makeText(
                            context,
                            "Paragraph has been suggested ",
                            Toast.LENGTH_LONG).show());
            request.addParameter(KEY_REQUEST_STORY_ID, storyId);
            request.addParameter(KEY_REQUEST_PARAGRAPH, paragraphSuggestionEditText.getText());
            new HttpCallTask(application.getClient(), request, getApplicationContext()).execute();
            finish();
        });
    }

    private void initializeStoryView(long storyId, ServerResponse response) {
        TextView title = findViewById(R.id.story_view_header);
        JsonObject data = response.getData();
        DataInflater dataInflater = ((OurTaleApplication) getApplication()).getDataInflater();
        Story story = dataInflater.inflateStory(storyId, data);
        title.setText(story.getTitle());
        TextView owner = findViewById(R.id.story_view_owner);
        owner.setText(String.format(LABEL_BY, story.getOwner().getName()));
        story.getParagraphs().forEach(paragraph -> addTextViewToParagraphsLayout(paragraph.getContent()));
        List<Paragraph> suggestions = story.getSuggestions();
        if (!suggestions.isEmpty()) {
            addTextViewToParagraphsLayout("SUGGESTIONS:");
            suggestions.forEach(paragraph -> addTextViewToParagraphsLayout(paragraph.getContent()));
        }
    }

    private void addTextViewToParagraphsLayout(String content) {
        LinearLayout linearLayout = findViewById(R.id.story_view_paragraphs);
        TextView textView = new TextView(getApplicationContext());
        textView.setText(content);
        linearLayout.addView(textView);
    }
}
