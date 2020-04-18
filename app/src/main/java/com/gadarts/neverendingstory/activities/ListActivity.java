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
import java.util.Optional;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

public class ListActivity extends FragmentActivity {
    public static final String HOST = "http://192.168.1.136:5000/";
    public static final String FRAGMENT_NAME_STORIES_LIST = "stories_list";
    private static final String GET_STORIES = HOST + "get_stories";
    private static final String KEY_STORIES = "stories";
    static final int REQUEST_CODE_NEW_STORY = 0;
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
        StoriesListFragment fragment = (StoriesListFragment) getSupportFragmentManager()
                .findFragmentByTag(FRAGMENT_NAME_STORIES_LIST);
        Optional.ofNullable(fragment).ifPresent(frag -> frag.getAdapter().clear());
    }

    private void inflateStoriesList(HashMap<String, String> storiesMap) {
        StoriesListFragment fragment = (StoriesListFragment) getSupportFragmentManager()
                .findFragmentByTag(FRAGMENT_NAME_STORIES_LIST);
        ArrayList<Story> stories = new ArrayList<>();
        storiesMap.forEach((id, title) -> stories.add(new Story(Long.parseLong(id), title)));
        if (!Optional.ofNullable(fragment).isPresent()) createStoriesList(stories);
        else fragment.getAdapter().setList(stories);
    }

    private void createStoriesList(ArrayList<Story> stories) {
        StoriesListFragment fragment;
        StoriesListAdapter adapter = new StoriesListAdapter(stories, getSupportFragmentManager());
        fragment = new StoriesListFragment(adapter);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.activity_main, fragment, FRAGMENT_NAME_STORIES_LIST);
        ft.commit();
    }


}
