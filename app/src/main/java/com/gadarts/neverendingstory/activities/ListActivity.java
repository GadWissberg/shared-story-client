package com.gadarts.neverendingstory.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import com.gadarts.neverendingstory.PolyTaleApplication;
import com.gadarts.neverendingstory.R;
import com.gadarts.neverendingstory.StoriesListAdapter;
import com.gadarts.neverendingstory.bl.Story;
import com.gadarts.neverendingstory.fragments.StoriesListFragment;
import com.gadarts.neverendingstory.http.HttpCallTask;
import com.gadarts.neverendingstory.http.OnRequestResult;
import com.gadarts.neverendingstory.http.ServerResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

public class ListActivity extends FragmentActivity {
    public static final String HOST = "http://192.168.1.136:5000/";
    public static final String FRAGMENT_NAME_STORIES_LIST_FRAGMENT = "stories_list_fragment";
    private static final String GET_STORIES = HOST + "get_stories";
    private static final String KEY_STORIES = "stories";
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        OnRequestResult onSuccess = (ServerResponse response) -> {
            JsonObject jsonObject = response.getData().get(KEY_STORIES).getAsJsonObject();
            HashMap<String, String> storiesMap = gson.fromJson(jsonObject, HashMap.class);
            inflateStoriesList(storiesMap);
        };
        HttpCallTask task = new HttpCallTask(
                ((PolyTaleApplication) getApplication()).getClient(),
                GET_STORIES,
                HttpCallTask.RequestType.GET,
                onSuccess);
        task.execute();
    }

    private void addFloatingButton() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, NewStoryActivity.class);
            startActivity(intent);
        });
    }

    private void inflateStoriesList(HashMap<String, String> storiesMap) {
        ArrayList<Story> stories = new ArrayList<>();
        storiesMap.forEach((id, title) -> stories.add(new Story(Long.parseLong(id), title)));
        StoriesListFragment fragment = new StoriesListFragment();
        fragment.setAdapter(new StoriesListAdapter(stories, getSupportFragmentManager()));
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.activity_main, fragment, FRAGMENT_NAME_STORIES_LIST_FRAGMENT);
        ft.commit();
    }


}
