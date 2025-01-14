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

package com.cmput301w18t05.taskzilla.request.command;

/**
 * Created by Andy on 4/5/2018.
 */


import com.cmput301w18t05.taskzilla.Notification;
import com.cmput301w18t05.taskzilla.controller.ElasticSearchController;
import com.cmput301w18t05.taskzilla.request.InsertionRequest;

/**
 * Request for adding notifications to elastic search
 * @author Andy
 * @see ElasticSearchController
 * @version 1.0
 */
public class AddNotificationRequest extends InsertionRequest {
    ElasticSearchController.AddNotification task;
    Notification notificationData;

    public AddNotificationRequest(Notification notification) {
        this.notificationData = notification;
        //queueReady = true;
    }

    /**
     * Add the notification into elasticsearch
     */
    @Override
    public void execute() {
        System.out.println("Trying to add notification");
        task = new ElasticSearchController.AddNotification();
        task.execute(notificationData);
    }

    @Override
    public void executeOffline() {
    }

    @Override
    public boolean requiresConnection() {
        return true;
    }

    public void getResult() {
    }

}
