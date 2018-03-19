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

package com.cmput301w18t05.taskzilla.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.cmput301w18t05.taskzilla.controller.ProfileController;
import com.cmput301w18t05.taskzilla.R;
import com.cmput301w18t05.taskzilla.User;

/**
 * Activity for viewing user profile
 */
public class ProfileActivity extends AppCompatActivity {
    private ProfileController profileController;
    private String userID;
    private User user;

    private TextView nameField;
    private TextView emailField;
    private TextView phoneField;
    private TextView numRequestsField;
    private TextView numTasksDoneField;
    private TextView providerRatingField;
    private TextView requesterRatingField;

    private String name;
    private String email;
    private String phone;
    private String numRequests;
    private String numTasksDone;



    /**
     * onCreate
     * Set fields in the activity_profile.xml
     * using ProfileController to retrieve the data of
     * the user through Elastic Search
     *
     * @author Micheal-Nguyen
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userID = getIntent().getStringExtra("user id");
        this.profileController = new ProfileController(this.findViewById(android.R.id.content),this);
        profileController.setUserID(userID);
        profileController.getUserRequest();
        user = profileController.getUser();

        name = user.getName();
        try {
            email = user.getEmail().toString();
            phone = user.getPhone().toString();
        }
        catch(Exception e){
            email = "No Email";
            phone = "No Number";
        }
        numRequests = "99";
        numTasksDone = "99";

        nameField = findViewById(R.id.NameField);
        emailField = findViewById(R.id.EmailField);
        phoneField = findViewById(R.id.PhoneField);
        numRequestsField = findViewById(R.id.NumRequestsField);
        numTasksDoneField = findViewById(R.id.NumTasksDoneField);

        nameField.setText(name);
        emailField.setText(email);
        phoneField.setText(phone);
        numRequestsField.setText(numRequests);
        numTasksDoneField.setText(numTasksDone);
    }
}
