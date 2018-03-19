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

package com.cmput301w18t05.taskzilla;

import android.app.Activity;
import android.app.Dialog;
import android.support.design.widget.FloatingActionButton;
import android.test.ActivityInstrumentationTestCase2;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.cmput301w18t05.taskzilla.activity.MainActivity;
import com.cmput301w18t05.taskzilla.activity.NewTaskActivity;
import com.cmput301w18t05.taskzilla.activity.SignUpActivity;
import com.cmput301w18t05.taskzilla.activity.ViewTaskActivity;
import com.cmput301w18t05.taskzilla.activity.WelcomeActivity;
import com.cmput301w18t05.taskzilla.fragment.TasksRequesterFragment;
import com.robotium.solo.Solo;

/**
 * Created by Micheal-Nguyen on 2018-03-18.
 */

public class UserTaskActivityTest extends ActivityInstrumentationTestCase2 {
    private Solo solo;
    private TasksRequesterFragment fragment;


    public UserTaskActivityTest(){
        super(MainActivity.class);
    }

    public void setUp() throws Exception{
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testStart() throws Exception {
        Activity activity = getActivity();
    }

    public void testClickTaskList(){
        MainActivity activity = (MainActivity)solo.getCurrentActivity();
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.usernameText), "TestUser");

        solo.clickOnText("Sign Up");
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
        solo.enterText((EditText) solo.getView(R.id.usernameField), "TestUser");
        solo.enterText((EditText) solo.getView(R.id.nameField), "TestName");
        solo.enterText((EditText) solo.getView(R.id.emailField), "Test@Email.com");
        solo.enterText((EditText) solo.getView(R.id.phoneField), "1234567890");
        solo.clickOnButton("Sign Up");

        solo.clickOnButton("Log In");
        solo.assertCurrentActivity("Wrong Activity", WelcomeActivity.class);

        // taken from https://stackoverflow.com/questions/33125017/how-to-access-floatingactionmenu-and-floating-action-button-in-robotium
        // 2018-3-17


      //  View v = inflater.inflate(R.layout.fragment_tasks_requester, container, false);

    }
}