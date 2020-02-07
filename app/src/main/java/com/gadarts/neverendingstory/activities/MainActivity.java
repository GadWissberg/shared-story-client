package com.gadarts.neverendingstory.activities;

import android.os.Build;
import android.os.Bundle;

import com.gadarts.neverendingstory.HttpCallTask;
import com.gadarts.neverendingstory.OnSuccessRequest;
import com.gadarts.neverendingstory.R;
import com.gadarts.neverendingstory.StoriesListAdapter;
import com.gadarts.neverendingstory.bl.Story;
import com.gadarts.neverendingstory.fragments.StoriesListFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import okhttp3.OkHttpClient;

public class MainActivity extends FragmentActivity {

    private static final String GET_STORIES = "http://192.168.1.136:5000/get_stories";
    private static final String STORIES = "stories";
    private OkHttpClient client;
    private Gson gson = new Gson();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new OkHttpClient();
        OnSuccessRequest onSuccess = (String response, Gson gson) -> {
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
        HttpCallTask task = new HttpCallTask(client, gson, GET_STORIES, onSuccess);
        task.execute();
    }


}
