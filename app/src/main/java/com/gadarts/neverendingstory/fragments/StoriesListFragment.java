package com.gadarts.neverendingstory.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gadarts.neverendingstory.R;
import com.gadarts.neverendingstory.StoriesListAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class StoriesListFragment extends Fragment {
    private StoriesListAdapter adapter;

    public StoriesListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView list = view.findViewById(R.id.stories_list);
        list.setAdapter(adapter);
    }

    public void setAdapter(StoriesListAdapter adapter) {
        this.adapter = adapter;
    }

}
