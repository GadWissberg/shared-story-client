package com.gadarts.neverendingstory.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;

import static com.gadarts.neverendingstory.OurTaleApplication.HOST;
import static com.gadarts.neverendingstory.StoriesListAdapter.SELECTED_STORY;

public class StoryViewActivity extends Activity {
    public static final int TIMER_COUNT_DOWN_INTERVAL = 1000;
    private static final String GET_STORY = HOST + "story";
    private static final String POST_PARAGRAPH_SUGGESTION = HOST + "paragraph_suggestion";
    private static final String KEY_REQUEST_ID = "id";
    private static final String KEY_REQUEST_STORY_ID = "story_id";
    private static final String KEY_REQUEST_PARAGRAPH = "paragraph";
    private static final String LABEL_STARTED_BY = "Started by %s";
    private static final String LABEL_BY = "By %s";
    private static final String LABEL_VOTES = "Votes: %s";
    private static final String VOTE_URL = HOST + "vote";
    private static final int PADDING_SUGGESTION = 10;
    private static final int PADDING_BOTTOM_SUGGESTION = 80;
    private static final int MARGIN_SUGGESTION = 10;
    private static final String TIMES_UP = "00:00 - Time's up!";
    private static final String LABEL_TIME_LEFT = "Time left: ";
    private static final String SUB_LABEL_AND = "and";
    private static final String LABEL_DAYS = "day";

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

            long deadline = story.getDeadline();
            TextView timer = findViewById(R.id.timer);
            timer.setVisibility(View.VISIBLE);
            long timeLeft = deadline * 1000 - (System.currentTimeMillis());
            StringBuilder stringBuilder = new StringBuilder();
            if (timeLeft > 0) {
                new CountDownTimer(timeLeft, TIMER_COUNT_DOWN_INTERVAL) {

                    public void onTick(final long millisUntilFinished) {
                        long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                        long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millisUntilFinished));
                        long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished));
                        long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished));
                        stringBuilder.setLength(0);
                        stringBuilder.append(LABEL_TIME_LEFT);
                        if (days > 0) {
                            stringBuilder.append(days).append(" ").append(LABEL_DAYS);
                            if (days > 1) {
                                stringBuilder.append('s');
                            }
                            stringBuilder.append(" ").append(SUB_LABEL_AND).append(" ");
                        }
                        if (hours <= 9) {
                            stringBuilder.append('0');
                        }
                        stringBuilder.append(hours).append(':');
                        if (minutes <= 9) {
                            stringBuilder.append('0');
                        }
                        stringBuilder.append(minutes).append(':');
                        if (seconds <= 9) {
                            stringBuilder.append('0');
                        }
                        stringBuilder.append(seconds);
                        timer.setText(stringBuilder);
                    }

                    public void onFinish() {
                    }

                }.start();
            } else {
                timer.setText(TIMES_UP);
            }

            Button submitVoteButton = findViewById(R.id.button_submit_vote);
            submitVoteButton.setVisibility(View.VISIBLE);
            AppRequest voteRequest = new AppRequest(VOTE_URL, RequestType.PUT, (res, context) -> Toast.makeText(getApplicationContext(), res.getMessage(), Toast.LENGTH_LONG));
            HashMap<String, Object> parameters = new HashMap<>();
            voteRequest.setParameters(parameters);
            RadioGroup radioGroup = new RadioGroup(this);
            submitVoteButton.setOnClickListener(view -> {
                parameters.put(KEY_REQUEST_ID, radioGroup.getCheckedRadioButtonId());
                new HttpCallTask(application.getClient(), voteRequest, getApplicationContext()).execute();
            });
            findViewById(R.id.label_no_suggestions).setVisibility(View.GONE);
            LinearLayout linearLayout = findViewById(R.id.story_view_suggested_paragraphs);
            linearLayout.addView(radioGroup);
            suggestions.forEach(paragraph -> {
                RadioButton radioButton = new RadioButton(getApplicationContext());
                radioButton.setId((int) paragraph.getId());
                radioButton.setText(paragraph.getContent());
                radioButton.setBackground(getDrawable(R.drawable.suggestion_view));
                radioButton.setPadding(PADDING_SUGGESTION, PADDING_SUGGESTION, PADDING_SUGGESTION, PADDING_SUGGESTION);
                radioGroup.addView(radioButton);
                TextView author = new TextView(getApplicationContext());
                author.setPadding(PADDING_SUGGESTION, PADDING_SUGGESTION, PADDING_SUGGESTION, PADDING_SUGGESTION);
                radioGroup.addView(author);
                author.setText(String.format(LABEL_BY, dataInflater.getUserFromCache(paragraph.getOwnerId()).getName()));
                TextView votes = new TextView(getApplicationContext());
                votes.setText(String.format(LABEL_VOTES, paragraph.getVotes()));
                votes.setPadding(PADDING_SUGGESTION, PADDING_SUGGESTION, PADDING_SUGGESTION, PADDING_BOTTOM_SUGGESTION);
                radioGroup.addView(votes);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(MARGIN_SUGGESTION, MARGIN_SUGGESTION, MARGIN_SUGGESTION, MARGIN_SUGGESTION);
                radioGroup.setLayoutParams(params);
            });
        }
    }

    private void addParagraphToParagraphsLayout(final String content,
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
    }
}
