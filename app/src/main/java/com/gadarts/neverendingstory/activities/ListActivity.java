package com.gadarts.neverendingstory.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import com.gadarts.neverendingstory.PolyTaleApplication;
import com.gadarts.neverendingstory.R;
import com.gadarts.neverendingstory.StoriesListAdapter;
import com.gadarts.neverendingstory.models.Story;
import com.gadarts.neverendingstory.models.User;
import com.gadarts.neverendingstory.services.DataInflater;
import com.gadarts.neverendingstory.services.http.AppRequest;
import com.gadarts.neverendingstory.services.http.HttpCallTask;
import com.gadarts.neverendingstory.services.http.OnRequestResult;
import com.gadarts.neverendingstory.services.http.ServerResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Optional;

import androidx.annotation.Nullable;

import static com.gadarts.neverendingstory.PolyTaleApplication.HOST;
import static com.gadarts.neverendingstory.services.DataInflater.KEY_OWNER;
import static com.gadarts.neverendingstory.services.DataInflater.KEY_TITLE;

public class ListActivity extends Activity {
    private static final int REQUEST_CODE_NEW_STORY = 0;
    private static final String GET_STORIES = HOST + "story";
    private static final String KEY_STORIES = "stories";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Button logout = findViewById(R.id.button_logout);
        logout.setOnClickListener(view -> {
            SharedPreferences prefs = getSharedPreferences(LoginActivity.PREFS_LOGIN, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(LoginActivity.KEY_MAIL, null);
            editor.putString(LoginActivity.KEY_PASS, null);
            editor.apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        addFloatingButton();
        retrieveStories();
    }

    private void retrieveStories() {
        ((ListView) findViewById(R.id.stories_list)).setAdapter(new StoriesListAdapter());
        OnRequestResult onSuccess = (ServerResponse response, Context context) -> {
            JsonObject jsonObject = response.getData().get(KEY_STORIES).getAsJsonObject();
            ArrayList<Story> stories = new ArrayList<>();
            jsonObject.entrySet().forEach((storyEntry) -> {
                JsonObject storyJsonObject = storyEntry.getValue().getAsJsonObject();
                long id = Long.parseLong(storyEntry.getKey());
                String title = storyJsonObject.get(KEY_TITLE).getAsString();
                DataInflater dataInflater = ((PolyTaleApplication) getApplication()).getDataInflater();
                JsonObject ownerJsonObject = storyJsonObject.get(KEY_OWNER).getAsJsonObject();
                User owner = dataInflater.inflateUser(ownerJsonObject);
                stories.add(new Story(id, title, owner));
            });
            inflateStoriesListView(stories);
        };
        AppRequest request = new AppRequest(GET_STORIES, HttpCallTask.RequestType.GET, onSuccess);
        new HttpCallTask(
                ((PolyTaleApplication) getApplication()).getClient(),
                request,
                getApplicationContext()).execute();
    }

    private void addFloatingButton() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, NewStoryActivity.class);
            startActivityForResult(intent, REQUEST_CODE_NEW_STORY);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_NEW_STORY && resultCode == RESULT_OK) {
            clearStories();
            retrieveStories();
        }
    }

    private void clearStories() {
        ListView listView = findViewById(R.id.stories_list);
        Optional.ofNullable(listView).ifPresent(view ->
                ((StoriesListAdapter) view.getAdapter()).clear());
    }

    private void inflateStoriesListView(ArrayList<Story> stories) {
        ListView listView = findViewById(R.id.stories_list);
        StoriesListAdapter adapter = (StoriesListAdapter) listView.getAdapter();
        adapter.setList(stories);
        runOnUiThread(adapter::notifyDataSetChanged);
    }


}
