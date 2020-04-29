package com.gadarts.neverendingstory.activities;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gadarts.neverendingstory.PolyTaleApplication;
import com.gadarts.neverendingstory.R;
import com.gadarts.neverendingstory.http.AppRequest;
import com.gadarts.neverendingstory.http.HttpCallTask;
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
    private static final String KEY_REQUEST_ID = "id";
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
        AppRequest appRequest = new AppRequest(GET_STORY, HttpCallTask.RequestType.GET, (response, context) -> {
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
        appRequest.addParameter(KEY_REQUEST_ID, getIntent().getLongExtra(SELECTED_STORY, 0));
        HttpCallTask task = new HttpCallTask(application.getClient(), appRequest, this);
        task.execute();
    }
}
