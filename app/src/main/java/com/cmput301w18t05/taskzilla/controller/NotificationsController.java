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

package com.cmput301w18t05.taskzilla.controller;

import android.content.Context;

import com.cmput301w18t05.taskzilla.Notification;
import com.cmput301w18t05.taskzilla.User;
import com.cmput301w18t05.taskzilla.fragment.NotificationsFragment;
import com.cmput301w18t05.taskzilla.request.RequestManager;
import com.cmput301w18t05.taskzilla.request.command.GetNotificationsByUserIdRequest;
import com.cmput301w18t05.taskzilla.request.command.GetTaskRequest;
import com.cmput301w18t05.taskzilla.request.command.RemoveNotificationRequest;

import java.util.ArrayList;

/**
 * Created by Andy on 4/5/2018.
 */

/**
 *  Controller that handles interaction between the NotificationFragment and elasticsearch.
 *
 *  @author Andy
 *
 *  @see    Notification
 *  @see    NotificationsFragment
 *  @see    com.cmput301w18t05.taskzilla.NotificationManager
 *
 *  @version 1
 */

public class NotificationsController {
    private ArrayList<Notification> notificationList;
    private NotificationsFragment view;
    private Context ctx;
    private User cUser;

    public NotificationsController(NotificationsFragment notificationsFragment, Context context, User user) {
        this.notificationList = new ArrayList<>();
        this.ctx = context;
        this.cUser = user;
        this.view = notificationsFragment;
    }

    /**
     * clear notification list
     */

    public void clearNotifications() {
        notificationList.clear();
        view.notifyChange();
    }

    /**
     * @return  Notification list
     */

    public ArrayList<Notification> getResults() {
        return this.notificationList;
    }

    /**
     * Sends a notification request to the manager which gets all notifications by userid
     */

    public void getNotificationsRequest() {
        GetNotificationsByUserIdRequest request = new GetNotificationsByUserIdRequest(cUser.getId());
        RequestManager.getInstance().invokeRequest(ctx, request);

        notificationList.clear();
        notificationList.addAll(request.getResult());

        view.notifyChange();
    }

    /**
     *  Returns boolean value which checks if task user put in exists in the elasticsearch server
     *
     * @param taskId    Task to be checked
     * @return          Boolean value
     */

    public boolean checkTaskExistRequest(String taskId) {
        GetTaskRequest getTaskRequest = new GetTaskRequest(taskId);
        RequestManager.getInstance().invokeRequest(ctx, getTaskRequest);

        if(getTaskRequest.getResult() == null)
            return false;
        else
            return true;
    }

    /**
     *  Removes notification user clicked on.
     *
     * @param id    Notification to be removed from elasticsearch server
     * @param pos   Position in arraylist containing the notification
     */

    public void removeNotificationRequest(String id, Integer pos) {
        RemoveNotificationRequest request = new RemoveNotificationRequest(id);
        RequestManager.getInstance().invokeRequest(ctx, request);

        notificationList.remove(pos);

        view.notifyChange();
    }

    /**
     *  Removes all notifications from the elasticsearch server and clears arraylist containing all
     *  notifications
     */

    public void removeAllNotificationRequest() {
        GetNotificationsByUserIdRequest request = new GetNotificationsByUserIdRequest(cUser.getId());
        RequestManager.getInstance().invokeRequest(request);

        for(Notification n : request.getResult()) {
            RemoveNotificationRequest removeNotificationRequest = new RemoveNotificationRequest(n.getId());
            RequestManager.getInstance().invokeRequest(removeNotificationRequest);
        }
        notificationList.clear();

        view.notifyChange();
    }
}
