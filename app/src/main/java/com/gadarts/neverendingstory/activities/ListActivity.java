package com.gadarts.neverendingstory.activities;

import android.os.Bundle;

import com.gadarts.neverendingstory.HttpCallTask;
import com.gadarts.neverendingstory.OnRequestResult;
import com.gadarts.neverendingstory.R;
import com.gadarts.neverendingstory.StoriesListAdapter;
import com.gadarts.neverendingstory.bl.Story;
import com.gadarts.neverendingstory.fragments.StoriesListFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

public class ListActivity extends FragmentActivity {

    public static final String HOST = "http://192.168.1.136:5000/";
    private static final String GET_STORIES = HOST + "get_stories";
    private static final String STORIES = "stories";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OnRequestResult onSuccess = (String response, Gson gson) -> {
            ArrayList<String> storiesNames = gson.fromJson(gson.fromJson(response, JsonObject.class).get(STORIES), ArrayList.class);
            ArrayList<Story> stories = new ArrayList<>();
            storiesNames.forEach(name -> stories.add(new Story(name)));
            StoriesListAdapter adapter = new StoriesListAdapter(stories, getSupportFragmentManager());
            StoriesListFragment fragment = new StoriesListFragment();
            fragment.setAdapter(adapter);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.main_activity, fragment, "stories_list_fragment");
            ft.commit();
        };
        HttpCallTask task = new HttpCallTask(GET_STORIES, HttpCallTask.RequestTypes.GET, onSuccess);
        task.execute();
    }


}
