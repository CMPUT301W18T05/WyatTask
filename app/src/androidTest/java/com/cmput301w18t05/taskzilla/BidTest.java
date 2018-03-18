package com.cmput301w18t05.taskzilla;

import android.test.ActivityInstrumentationTestCase2;

import com.cmput301w18t05.taskzilla.activity.MainActivity;

/**
 * Created by Jeremy
 */

public class BidTest extends ActivityInstrumentationTestCase2 {
    public BidTest(){
        super(MainActivity.class);
    }

    /**
     * Test for setting the bid
     */
    public void testSetBid() {
        User user = new User();
        Task task = new Task();
        float bidAmount = 10.00f;
        Bid bid = new Bid(user.getId(), task.getId(), 10.00f);
        assertEquals(bid.getBidAmount(), bidAmount);
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
        Task task = new Task();
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
}
