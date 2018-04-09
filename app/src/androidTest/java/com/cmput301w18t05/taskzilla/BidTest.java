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

import android.test.ActivityInstrumentationTestCase2;

import com.cmput301w18t05.taskzilla.activity.MainActivity;
import com.cmput301w18t05.taskzilla.request.RequestManager;
import com.cmput301w18t05.taskzilla.request.command.AddTaskRequest;
import com.cmput301w18t05.taskzilla.request.command.AddUserRequest;
import com.cmput301w18t05.taskzilla.request.command.GetTaskRequest;

import java.text.DecimalFormat;


/**
 * Created by Jeremy
 */

public class BidTest extends ActivityInstrumentationTestCase2 {
    public BidTest(){
        super(MainActivity.class);
    }

    /**
     * Test for comparing a bid to another bid
     *
     * greater than returns 1
     * equality returns 0
     * less than returns -1
     */

    public void testCompareTo() {

        User user1 = new User();
        Task task = new Task("Task name", user1, "Task description");
        float bidAmount1 = 10.00f;
        Bid bid1 = new Bid(user1.getId(), task.getId(), bidAmount1);

        User user2 = new User();
        float bidAmount2 = 1.00f;
        Bid bid2 = new Bid(user2.getId(), task.getId(), bidAmount2);
        assertEquals(bid1.compareTo(bid2), 1);

        User user3 = new User();
        float bidAmount3 = 10.00f;
        Bid bid3 = new Bid(user3.getId(), task.getId(), bidAmount3);
        assertEquals(bid1.compareTo(bid3), 0);

        User user4 = new User();
        float bidAmount4 = 20.00f;
        Bid bid4 = new Bid(user4.getId(), task.getId(), bidAmount4);
        assertEquals(bid1.compareTo(bid4), -1);
    }

    /**
     *  Test the toString method
     */
    public void testToString() {
        User user = new User();

        Task task = new Task("Task name", user, "Task description");
        AddTaskRequest addTaskRequest = new AddTaskRequest(task);
        RequestManager.getInstance().invokeRequest(addTaskRequest);

        float bidAmount = 1.00f;
        Bid bid = new Bid(user.getId(), task.getId(), bidAmount);
        DecimalFormat cents = new DecimalFormat("#0.00");

        String result = "Task: Task name \nRequester: " + user.getName() + " \nStatus: " + task.getStatus() + "\nBid amount: $" + cents.format(bidAmount) + " Lowest bid: $" + cents.format(task.getBestBid());

        assertEquals(bid.toString(), result);
    }
}
