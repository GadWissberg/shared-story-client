package com.gadarts.neverendingstory.activities;

import android.os.Bundle;

import com.gadarts.neverendingstory.R;
import com.gadarts.neverendingstory.fragments.NewStoryFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

public class ListActivity extends FragmentActivity {

    public static final String HOST = "http://192.168.1.136:5000/";
    private static final String GET_STORIES = HOST + "get_stories";
    private static final String STORIES = "stories";
    public static final String NEW_STORY_FRAGMENT_TAG = "new_story_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            NewStoryFragment fragment = new NewStoryFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.main_activity, fragment, NEW_STORY_FRAGMENT_TAG);
            transaction.commit();
        });
//        OnRequestResult onSuccess = (String response, Gson gson) -> {
//            ArrayList<String> storiesNames = gson.fromJson(gson.fromJson(response, JsonObject.class).get(STORIES), ArrayList.class);
//            ArrayList<Story> stories = new ArrayList<>();
//            storiesNames.forEach(name -> stories.add(new Story(name)));
//            StoriesListAdapter adapter = new StoriesListAdapter(stories, getSupportFragmentManager());
//            StoriesListFragment fragment = new StoriesListFragment();
//            fragment.setAdapter(adapter);
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.add(R.id.main_activity, fragment, "stories_list_fragment");
//            ft.commit();
//        };
//        HttpCallTask task = new HttpCallTask(GET_STORIES, HttpCallTask.RequestTypes.GET, onSuccess);
//        task.execute();
    }


}
