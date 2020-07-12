package com.gadarts.neverendingstory.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gadarts.neverendingstory.OurTaleApplication;
import com.gadarts.neverendingstory.R;
import com.gadarts.neverendingstory.model.Paragraph;
import com.gadarts.neverendingstory.model.Story;
import com.gadarts.neverendingstory.model.User;
import com.gadarts.neverendingstory.services.DataInflater;
import com.gadarts.neverendingstory.services.http.AppRequest;
import com.gadarts.neverendingstory.services.http.HttpCallTask;
import com.gadarts.neverendingstory.services.http.HttpCallTask.RequestType;
import com.gadarts.neverendingstory.services.http.OnRequestResult;
import com.gadarts.neverendingstory.services.http.OnResults;
import com.gadarts.neverendingstory.services.http.ServerResponse;
import com.google.gson.JsonObject;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;

import static com.gadarts.neverendingstory.OurTaleApplication.HOST;
import static com.gadarts.neverendingstory.StoriesListAdapter.SELECTED_STORY;

public class StoryViewActivity extends Activity {
    private static final String GET_STORY = HOST + "story";
    private static final String POST_PARAGRAPH_SUGGESTION = HOST + "paragraph_suggestion";
    private static final String KEY_REQUEST_ID = "id";
    private static final String KEY_REQUEST_STORY_ID = "story_id";
    private static final String KEY_REQUEST_PARAGRAPH = "paragraph";
    private static final String LABEL_STARTED_BY = "Started by %s";
    private static final String LABEL_BY = "By %s";
    private static final String VOTE_URL = HOST + "vote";

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_view);
        TextView title = findViewById(R.id.story_view_header);
        title.setText("LOADING");
        OurTaleApplication application = (OurTaleApplication) getApplication();
        long storyId = getIntent().getLongExtra(SELECTED_STORY, 0);
        OnResults onResults = new OnResults(
                (response, context) -> initializeStoryView(storyId, response),
                (response, context) -> {
                    if (response.getCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    Toast.makeText(context, "" + response.getCode(), Toast.LENGTH_LONG).show();
                });
        AppRequest appRequest = new AppRequest(GET_STORY, RequestType.GET, onResults);
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
            OnRequestResult onResult = (response, context) -> Toast.makeText(getApplicationContext(), "Paragraph has been suggested ", Toast.LENGTH_LONG).show();
            AppRequest request = new AppRequest(POST_PARAGRAPH_SUGGESTION, RequestType.POST, onResult);
            request.addParameter(KEY_REQUEST_STORY_ID, storyId);
            request.addParameter(KEY_REQUEST_PARAGRAPH, paragraphSuggestionEditText.getText());
            new HttpCallTask(application.getClient(), request, getApplicationContext()).execute();
            finish();
        });
    }

    private void initializeStoryView(final long storyId, final ServerResponse response) {
        TextView title = findViewById(R.id.story_view_header);
        JsonObject data = response.getData();
        OurTaleApplication application = (OurTaleApplication) getApplication();
        DataInflater dataInflater = application.getDataInflater();
        Story story = dataInflater.inflateStory(storyId, data);
        title.setText(story.getTitle());
        TextView owner = findViewById(R.id.story_view_owner);
        owner.setText(String.format(LABEL_STARTED_BY, story.getOwner().getName()));
        story.getParagraphs().forEach(paragraph -> addParagraphToParagraphsLayout(paragraph.getContent(), dataInflater.getUserFromCache(paragraph.getOwnerId()), R.drawable.regular_paragraph_view, R.id.story_view_paragraphs));
        List<Paragraph> suggestions = story.getSuggestions();
        if (!suggestions.isEmpty()) {
            Button submitVoteButton = findViewById(R.id.button_submit_vote);
            submitVoteButton.setVisibility(View.VISIBLE);
            AppRequest voteRequest = new AppRequest(VOTE_URL, RequestType.POST, (res, context) -> Toast.makeText(getApplicationContext(), "SENT", Toast.LENGTH_LONG));
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put(KEY_REQUEST_ID, 0);
            voteRequest.setParameters(parameters);
            submitVoteButton.setOnClickListener(view -> new HttpCallTask(application.getClient(), voteRequest, getApplicationContext()));
            RadioGroup radioGroup = new RadioGroup(this);
            findViewById(R.id.label_no_suggestions).setVisibility(View.GONE);
            LinearLayout linearLayout = findViewById(R.id.story_view_suggested_paragraphs);
            linearLayout.addView(radioGroup);
            suggestions.forEach(paragraph -> {
                RadioButton radioButton = new RadioButton(getApplicationContext());
                radioButton.setText(paragraph.getContent());
                radioButton.setBackground(getDrawable(R.drawable.suggestion_view));
                radioGroup.addView(radioButton);
                TextView author = new TextView(getApplicationContext());
                author.setText(String.format(LABEL_BY, dataInflater.getUserFromCache(paragraph.getOwnerId()).getName()));
                radioGroup.addView(author);
            });
        }
    }

    private LinearLayout addParagraphToParagraphsLayout(final String content,
                                                        final User user,
                                                        final int paragraphView,
                                                        final int layoutId) {
        LinearLayout linearLayout = findViewById(layoutId);
        LinearLayout paragraphLayout = new LinearLayout(getApplicationContext());
        paragraphLayout.setOrientation(LinearLayout.VERTICAL);
        Context applicationContext = getApplicationContext();
        TextView paragraph = new TextView(applicationContext);
        paragraph.setBackground(getDrawable(paragraphView));
        paragraph.setText(content);
        TextView author = new TextView(applicationContext);
        author.setText(String.format(LABEL_BY, user.getName()));
        paragraphLayout.addView(paragraph);
        paragraphLayout.addView(author);
        linearLayout.addView(paragraphLayout);
        return paragraphLayout;
    }
}
