/*
 * Copyright 2018 (c) Andy Li, Colin Choi, James Sun, Jeremy Ng, Micheal Nguyen, Wyatt Praharenka
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.cmput301w18t05.taskzilla.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cmput301w18t05.taskzilla.R;
import com.cmput301w18t05.taskzilla.Task;
import com.cmput301w18t05.taskzilla.activity.ViewTaskActivity;
import com.cmput301w18t05.taskzilla.currentUser;
import com.cmput301w18t05.taskzilla.request.RequestManager;
import com.cmput301w18t05.taskzilla.request.command.GetTasksByProviderUsernameRequest;

import java.util.ArrayList;


/**
 * Child fragment of TasksFragment
 * Tasks that the user is providing for appear here
 *
 * @author Colin
 * @version 1.0
 */
public class TasksProviderFragment extends Fragment {
    // List of Tasks
    private ArrayList<Task> taskList;
    private ListView taskListView;
    private ArrayAdapter<Task> adapter;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private GetTasksByProviderUsernameRequest requestTasks;

    public TasksProviderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Set up listview and adapter
        View v = inflater.inflate(R.layout.fragment_tasks_provider, container, false);
        taskList = new ArrayList<>();
        taskListView = v.findViewById(R.id.ProviderTasksListView);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, taskList);
        taskListView.setAdapter(adapter);

        requestTasks = new GetTasksByProviderUsernameRequest(currentUser.getInstance().getUsername());
        RequestManager.getInstance().invokeRequest(getContext(), requestTasks);

        taskList.addAll(requestTasks.getResult());

        adapter.notifyDataSetChanged();

        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                viewTask(taskList.get(position).getId());
            }
        });
        return v;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mySwipeRefreshLayout = view.findViewById(R.id.swiperefreshProvider);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        updatePList();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(mySwipeRefreshLayout.isRefreshing()) {
                                    mySwipeRefreshLayout.setRefreshing(false);
                                }
                            }
                        }, 1000);
                    }
                }
        );
    }
    // Taken from https://stackoverflow.com/questions/41655797/refresh-fragment-when-change-between-tabs?noredirect=1&lq=1
    // 2018-04-01

    /**
     * update the tasksprovider fragment when visible
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            updatePList();
        }
    }

    /**
     * retrieve the provider list from the elastic search using request manager
     * updating the task list
     */
    public void updatePList(){
        requestTasks = new GetTasksByProviderUsernameRequest(currentUser.getInstance().getUsername());
        RequestManager.getInstance().invokeRequest(getContext(), requestTasks);
        taskList.clear();
        taskList.addAll(requestTasks.getResult());
        adapter.notifyDataSetChanged();
    }

    /**
     * Switches to ViewTaskActivity
     * @param id id of the task to be view is passed in as a String
     */
    public void viewTask(String id){
        Intent intent = new Intent(getActivity(), ViewTaskActivity.class);
        intent.putExtra("TaskId",id);
        startActivity(intent);
    }
}
