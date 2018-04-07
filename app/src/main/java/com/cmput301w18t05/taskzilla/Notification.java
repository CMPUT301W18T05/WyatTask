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

import android.content.Intent;

/**
 * Created by Andy on 4/4/2018.
 */

public class Notification {
    private String title;
    private String context;
    private Intent intent;
    private String providerId;
    private String requesterId;
    private User user;

    private String id;

    public Notification(String nTitle, String nContext, Intent nIntent, String nProviderId, String nRequesterId, User nUser) {
        this.title = nTitle;
        this.context = nContext;
        this.intent = nIntent;
        this.providerId = nProviderId;
        this.requesterId = nRequesterId;
        this.user = nUser;
    }

    public String getTitle() {
        return this.title;
    }

    public String getContext() {
        return this.context;
    }

    public Intent getNotificationIntent() {
        return this.intent;
    }

    public String getProviderId() {
        return this.providerId;
    }

    public String getRequesterId() {
        return this.requesterId;
    }

    public String toString() {
        return "Title: " + this.title + "\nContext: " + this.context + "\nBy: " + this.providerId;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String nid) {
        this.id = nid;
    }

    public User getUser() {
        return this.user;
    }

    public int compareTo(Notification notification) {
        return this.getId().compareTo(notification.getId());
    }
}

