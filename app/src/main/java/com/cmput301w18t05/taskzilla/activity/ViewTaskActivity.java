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

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;


import android.widget.EditText;
import android.widget.Toast;

import com.cmput301w18t05.taskzilla.AppColors;
import com.cmput301w18t05.taskzilla.Bid;
import com.cmput301w18t05.taskzilla.CustomOnItemClick;
import com.cmput301w18t05.taskzilla.ExpandableBidListAdapter;
import com.cmput301w18t05.taskzilla.Notification;
import com.cmput301w18t05.taskzilla.NotificationManager;
import com.cmput301w18t05.taskzilla.Photo;
import com.cmput301w18t05.taskzilla.R;
import com.cmput301w18t05.taskzilla.RecyclerViewAdapter;
import com.cmput301w18t05.taskzilla.Task;
import com.cmput301w18t05.taskzilla.User;
import com.cmput301w18t05.taskzilla.controller.ProfileController;
import com.cmput301w18t05.taskzilla.controller.ViewTaskController;
import com.cmput301w18t05.taskzilla.currentUser;
import com.cmput301w18t05.taskzilla.request.RequestManager;
import com.cmput301w18t05.taskzilla.request.command.GetBidsByTaskIdRequest;
import com.cmput301w18t05.taskzilla.request.command.GetUserRequest;
import com.cmput301w18t05.taskzilla.request.command.RemoveBidRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Activity fro viewing a task
 *
 * @version 1.0
 */
public class ViewTaskActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Task task;
    private String taskName;
    private String taskID;
    private String currentUserId;
    private String taskUserId;
    private String description;
    private User TaskRequester;
    private User TaskProvider;
    private ArrayList<Bid> BidList;
    private Bid selectedBid;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private ViewTaskController viewTaskController;

    private TextView ProviderName;
    private TextView DescriptionView;
    private TextView RequesterName;
    private TextView TaskName;
    private TextView TaskStatus;
    private TextView NoLocation;

    private ImageButton EditButton;
    private ImageButton DeleteButton;
    private ImageButton ProviderPicture;
    private ImageButton RequesterPicture;

    private ExpandableListView BidslistView;
    private Button BlueButton;
    private Button YellowButton;
    private Button GreenButton;
    private Button RedButton;
    private Button PinkButton;
    private Button OrangeButton;
    private ScrollView scrollView;

    private RecyclerView recyclerPhotosView;
    private RecyclerView.Adapter recyclerPhotosViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private LinearLayout linearLayout;
    private ArrayList<Photo> photos;
    private AppColors appColors;

    /**onCreate
     * Retrieve the task using the task id that was sent using
     * intent into the activity updating the information on the
     * activity_ViewTaskActivity while checking if the task has
     * a provider, what the status is and if the user viewing
     * is the owner of the task
     *
     * @param savedInstanceState
     * @author Micheal-Nguyen, myapplestory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);
        appColors = AppColors.getInstance();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(appColors.getActionBarColor())));
        actionBar.setTitle(Html.fromHtml("<font color='"+ appColors.getActionBarTextColor() + "'>Taskzilla</font>"));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.dragdropMap);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        findViews();

        // starts the activity at the very top
        scrollView.setFocusableInTouchMode(true);
        scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

        // gets the task id
        this.viewTaskController = new ViewTaskController(this.findViewById(android.R.id.content), this);
        taskID = getIntent().getStringExtra("TaskId");

        setValues();
        setRequesterField();
        setProviderField();

        photos = task.getPhotos();
        NoLocation = findViewById(R.id.NoLocationText);
        NoLocation.setVisibility(View.INVISIBLE);
        if(task.getLocation()==null) {
            NoLocation.setVisibility(View.VISIBLE);
            //mapFragment.s
        }
        linearLayout = findViewById(R.id.Photos);
        recyclerPhotosView = findViewById(R.id.listOfPhotos);
        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerPhotosView.setLayoutManager(layoutManager);
        recyclerPhotosViewAdapter = new RecyclerViewAdapter(getApplicationContext(), photos, new CustomOnItemClick() {
            @Override
            public void onColumnClicked(int position) {
                Intent intent = new Intent(getApplicationContext(),ZoomImageActivity.class);
                intent.putExtra("Photo",photos.get(position).toString());
                startActivity(intent);
            }
        });
        recyclerPhotosView.setAdapter(recyclerPhotosViewAdapter);

        setVisibility();
        setUpBidsList();
    }

    /**
     * deleteButtonOnClick
     * in the activity_view_taskxml, when the delete button is pressed
     * prompt user with a confirmation dialog.
     * upon confirmation call vieTaskController to remove
     * the task through elastic search
     *
     * @author Micheal-Nguyen
     */
    public void deleteButtonOnClick(android.view.View view) {
// taken from https://stackoverflow.com/questions/2115758/how-do-i-display-an-alert-dialog-on-android
        // 2018-03-16
        AlertDialog.Builder alert = new AlertDialog.Builder(ViewTaskActivity.this);
        alert.setTitle("Delete Task");
        alert.setMessage("Are you sure you want to delete this task?");

        //DELETE CODE
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                viewTaskController.RemoveTaskRequest(task);
                dialogInterface.dismiss();

                Intent intent = new Intent();
                intent.putExtra("result", true);
                setResult(RESULT_OK, intent);

                finish();
            }
        });
        //DELETE CANCEL CODE
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.show();
    }

    /**
     * editButtonOnClick
     * in the activity_view_taskxml, when the edit button is pressed
     * got to the edit task activity
     * upon confirmation call vieTaskController to edit
     * the task through elastic search
     *
     * @author Micheal-Nguyen
     */
    public void editButtonOnClick(android.view.View view){
        Intent intent = new Intent(view.getContext(), EditTaskActivity.class);
        intent.putExtra("task Name", taskName);
        intent.putExtra("Description", description);
        try {
            intent.putExtra("Lat", Double.toString(task.getLocation().latitude));
            intent.putExtra("Lon", Double.toString(task.getLocation().longitude));
        } catch (Exception e) {
            intent.putExtra("Lat", "0.0");
            intent.putExtra("Lon", "0.0");
        }
        ArrayList<String> photosString = new ArrayList<String>();
        for(int i = 0;i < photos.size(); i++){
            photosString.add(photos.get(i).toString());
        }
        intent.putStringArrayListExtra("photos",photosString);
        startActivityForResult(intent, 1);
    }

    /**
     * RequesterPictureOnClick
     * when requester picture clicked in
     * activity_view_task.xml pass user id through intent
     * and start the ProfileActivity
     *
     * @author Micheal-Nguyen
     */
    public void requesterPictureOnClick(android.view.View view) {
        try {
            Intent intent = new Intent(view.getContext(), ProfileActivity.class);
            intent.putExtra("user id", TaskRequester.getId());
            startActivity(intent);
        } catch (Exception e) {}
    }

    /**
     * ProviderPictureOnClick
     * when providerpicture clicked in
     * activity_view_task.xml pass user id through intent
     * and start the ProfileActivity
     *
     * @author Micheal-Nguyen
     */
    public void providerPictureOnClick(android.view.View view) {
        try {
            Intent intent = new Intent(view.getContext(), ProfileActivity.class);
            if (task.getStatus().equals("bidded")) {
                intent.putExtra("user id", task.getBestBidder());
            } else {
                intent.putExtra("user id", TaskProvider.getId());
            }
            startActivity(intent);
        } catch (Exception e) {}
    }

    /**
     * @param view pretty much the page it's on
     * @author myapplestory
     * theBlueButton
     * upon pressing place button on task page
     * prompts user to enter in a bid amount
     * if valid input, will add bid to task
     */
    public void theBlueButton(android.view.View view) {
        final AlertDialog mBuilder = new AlertDialog.Builder(ViewTaskActivity.this).create();
        final View mView = getLayoutInflater().inflate(R.layout.dialog_place_bid,null);
        final EditText incomingBidText = mView.findViewById(R.id.place_bid_edittext);
        // Taken from https://gist.github.com/gaara87/3607765
        // 2018-03-19
        // Limits the number of decimals allowed in input
        incomingBidText.setFilters(new InputFilter[] {
            new DigitsKeyListener(Boolean.FALSE, Boolean.TRUE) {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                    StringBuilder builder = new StringBuilder(dest);
                    builder.insert(dstart, source);
                    String temp=builder.toString();
                    if (temp.contains(".")) {
                        temp = temp.substring(temp.indexOf(".") + 1);
                        if (temp.length() > 2) {return "";}
                    }
                    return super.filter(source, start, end, dest, dstart, dend);
                }
            }
        });

        //bring up keyboard when user taps place bid
        InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        final Button submitBidButton = mView.findViewById(R.id.submit_bid_button);
        submitBidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Float incomingBidFloat;
                try {
                    incomingBidFloat = Float.parseFloat(incomingBidText.getText().toString());
                    incomingBidFloat = (float) (Math.round(incomingBidFloat * 100.0) / 100.0);
                } catch (Exception exception) {
                    Toast.makeText(ViewTaskActivity.this,
                            "Please enter in a valid bid amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                // do stuff here to actually add bid
                if (updateBestBid(incomingBidFloat) == -1) {
                    return;
                } else {
                    task.addBid(new Bid(currentUserId, taskID, incomingBidFloat));
                    task.setStatus("bidded");
                    TaskStatus.setText("Bidded");
                }
                setProviderField();

                Toast.makeText(ViewTaskActivity.this, "Bid placed", Toast.LENGTH_SHORT).show();

                // hide keyboard upon pressing button
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(submitBidButton.getWindowToken(), 0);

                mBuilder.dismiss();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateBidsList();
                    }
                }, 1500);
            }
        });
        mBuilder.setView(mView);
        mBuilder.show();
    }

    /**
     * theYellowButton
     * when the task is bidded or requested
     * the requester can accept an existing bid from the list of existing bids
     * onclick a dialog shows up with a list view of existing bids and a button to confirm acceptance
     *
     * @param view
     * @author myapplestory
     */
    public void theYellowButton(android.view.View view) {
        final AlertDialog mBuilder = new AlertDialog.Builder(ViewTaskActivity.this).create();
        final View mView = getLayoutInflater().inflate(R.layout.dialog_accept_bid,null);
        final ListView acceptBidListView = mView.findViewById(R.id.AcceptBidList);
        final Button acceptBidButton = mView.findViewById(R.id.AcceptBidButton);
        ArrayList<String> tempBidList = new ArrayList<>();
        selectedBid = null;

        if (BidList.isEmpty()) {
            tempBidList.add("No bids :'(");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, tempBidList);
            acceptBidListView.setAdapter(adapter);
            acceptBidButton.setText("SAD");
            acceptBidButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mBuilder.dismiss();
                }
            });
        } else {
            for (Bid bid : BidList) {
                ProfileController controller = new ProfileController(mView, getBaseContext());
                controller.setUserID(bid.getUserId());
                controller.getUserRequest();
                DecimalFormat cents = new DecimalFormat("#0.00");
                tempBidList.add("Best bidder: " + controller.getUser().getName() + "\nBid Amount: $" +
                        cents.format(bid.getBidAmount()));
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_single_choice, tempBidList);
            acceptBidListView.setAdapter(adapter);
            acceptBidListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            acceptBidButton.setText("ACCEPT BID");

            acceptBidListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedBid = BidList.get(i);
                }
            });

            // upon clicking accepting, take the bids that was selected and update task
            acceptBidButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectedBid == null) {
                        Toast.makeText(ViewTaskActivity.this,
                                "Select one of the bids before accepting", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ProfileController controller = new ProfileController(mView, getBaseContext());
                    controller.setUserID(selectedBid.getUserId());
                    controller.getUserRequest();
                    TaskProvider = controller.getUser();
                    task.setTaskProvider(TaskProvider);
                    task.setStatus("assigned");
                    TaskStatus.setText("Assigned");
                    updateBidsList();
                    EditButton.setVisibility(View.INVISIBLE);
                    setProviderField();
                    mBuilder.dismiss();
                    RedButton.setVisibility(View.VISIBLE);
                    GreenButton.setVisibility(View.VISIBLE);
                    PinkButton.setVisibility(View.INVISIBLE);
                    YellowButton.setVisibility(View.INVISIBLE);
                    BidslistView.setVisibility(View.INVISIBLE);
                }
            });
        }
        mBuilder.setView(mView);
        mBuilder.show();
    }

    /**
     * thePinkButton
     * when the task is requested or bidded
     * there will be a pink button where the requester can decline one of the existing bids
     * onclick it shows a dialog with a listview of existing bids and a button to confirm declination
     *
     * @param view the view this button is in
     * @author myapplestory
     */
    public void thePinkButton(android.view.View view) {
        final AlertDialog mBuilder = new AlertDialog.Builder(ViewTaskActivity.this).create();
        final View mView = getLayoutInflater().inflate(R.layout.dialog_decline_bid,null);
        final ListView declineBidListView = mView.findViewById(R.id.DeclineBidList);
        final Button declineBidButton = mView.findViewById(R.id.DeclineBidButton);
        ArrayList<String> tempList = new ArrayList<>();
        selectedBid = null;

        if (BidList.isEmpty()) {
            tempList.add("No bids :'(");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, tempList);
            declineBidListView.setAdapter(adapter);
            declineBidButton.setText("SAD");
            declineBidButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mBuilder.dismiss();
                }
            });
        } else {
            for (Bid bid : BidList) {
                ProfileController controller = new ProfileController(mView, getBaseContext());
                controller.setUserID(bid.getUserId());
                controller.getUserRequest();
                DecimalFormat cents = new DecimalFormat("#0.00");
                tempList.add("Best bidder: " + controller.getUser().getName() + "\nBid Amount: $" +
                        cents.format(bid.getBidAmount()));
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_single_choice, tempList);
            declineBidListView.setAdapter(adapter);
            declineBidListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            declineBidButton.setText("DECLINE BID");
            declineBidListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedBid = BidList.get(i);
                }
            });

            declineBidButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectedBid == null) {
                        Toast.makeText(ViewTaskActivity.this,
                                "Select a bid before declining", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    RemoveBidRequest removeRequest = new RemoveBidRequest(selectedBid);
                    RequestManager.getInstance().invokeRequest(removeRequest);

                    String temp = "Your bid has been declined!";

                    Notification notification = new Notification("Bid Declined", task.getRequesterId(), selectedBid.getUserId(), taskID, taskName, temp, currentUser.getInstance());
                    NotificationManager.getInstance().sendNotification(notification);

                    BidList.remove(selectedBid);
                    updateBidsList();

                    if (BidList.size() == 1) {
                        EditButton.setVisibility(View.VISIBLE);
                        task.setStatus("requested");
                        TaskStatus.setText("requested");
                        updateBidsList();
                    } else {
                        Float bestBidTemp = -1.0f;
                        String bestBidderIdTemp = "-1";
                        for(Bid bid: BidList){
                            if(bestBidTemp == -1.0f){
                                bestBidTemp = bid.getBidAmount();
                                GetUserRequest request = new GetUserRequest(bid.getUserId());
                                RequestManager.getInstance().invokeRequest(getApplicationContext(), request);
                                User tempBidder = request.getResult();
                                bestBidderIdTemp = tempBidder.getId();
                            }

                            if(bid.getBidAmount() < bestBidTemp && !task.getBestBidder().equals(bid.getUserId())){
                                Log.i("CHANGE",bid.getBidAmount().toString());
                                bestBidTemp = bid.getBidAmount();
                                GetUserRequest request = new GetUserRequest(bid.getUserId());
                                RequestManager.getInstance().invokeRequest(getApplicationContext(), request);
                                User tempBidder = request.getResult();
                                bestBidderIdTemp = tempBidder.getId();
                            }
                        }
                        task.setBestBid(bestBidTemp);
                        task.setBestBidder(bestBidderIdTemp);
                        task.updateThis();
                    }
                    setProviderField();
                    mBuilder.dismiss();
                }
            });
        }
        mBuilder.setView(mView);
        mBuilder.show();
    }

    /**
     * theOrangeButton
     * upon completing a task, a orange button appears which enables the requester/provider
     * to review the other user
     * goes to another activity to write a review
     * @param view the view this button is in
     * @author myapplestory
     */
    public void theOrangeButton(android.view.View view) {
        Intent intent = new Intent(view.getContext(), NewReviewActivity.class);
        if (currentUserId.equals(task.getRequesterId())) {
            intent.putExtra("who", "p");
            intent.putExtra("id", task.getProviderId());
        } else {
            intent.putExtra("who", "r");
            intent.putExtra("id", task.getRequesterId());
        }
        startActivity(intent);
    }

    /**
     * theRedButton
     * when the task is assigned
     * there will be a red button where the requester is
     * able to unassign the provider from the task
     * and reset the task back to requested with no bids
     * @author wyatt
     */
    public void theRedButton(android.view.View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(ViewTaskActivity.this);
        alert.setTitle("Unassign Provider");
        alert.setMessage("Are you sure you want to unassign this provider?");

        //DELETE CODE
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                task.unassignProvider();
                RedButton.setVisibility(View.INVISIBLE);
                GreenButton.setVisibility(View.INVISIBLE);
                PinkButton.setVisibility(View.VISIBLE);
                YellowButton.setVisibility(View.VISIBLE);
                TaskStatus.setText("Requested");
                if (currentUserId.equals(task.getRequesterId())) {
                    OrangeButton.setText("REVIEW PROVIDER");
                } else {
                    OrangeButton.setText("REVIEW REQUESTER");
                }
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.show();
    }

    /**
     * theGreenButton
     * when the task is assigned
     * there will be a green button where the requester is
     * able to complete the task
     *
     * @author wyatt
     */
    public void theGreenButton(android.view.View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(ViewTaskActivity.this);
        alert.setTitle("Complete task");
        alert.setMessage("Are you sure you want to set this task as completed?");

        //DELETE CODE
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                task.completeTask();
                RedButton.setVisibility(View.INVISIBLE);
                GreenButton.setVisibility(View.INVISIBLE);
                OrangeButton.setVisibility(View.VISIBLE);
                TaskStatus.setText("Completed");
                if (currentUserId.equals(task.getRequesterId())) {
                    OrangeButton.setText("REVIEW PROVIDER");
                } else {
                    OrangeButton.setText("REVIEW REQUESTER");
                }
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.show();
    }

    /**
     * updateBestBid
     * updates best bidder field if new bid has value less than the current best bid
     *
     * @param incomingBidFloat the bid amount the new bid will be
     * @return 0 if best bidder needs update, -1 if not
     * @author myapplestory, Micheal-Nguyen
     */
    public Integer updateBestBid(Float incomingBidFloat) {
        Log.i("CURRENTBESTBIDDER",task.getBestBidder());
        Log.i("CURRENTUSER",currentUserId);

        if (task.getBestBid() > incomingBidFloat || task.getBestBid() == -1.0f) {
            Log.i("in","1");
            task.setBestBidder(currentUserId);
            task.setBestBid(incomingBidFloat);
            task.updateThis();
        } else if (task.getBestBid().equals(incomingBidFloat)) {
            Toast.makeText(ViewTaskActivity.this,
                    "A similar bid already exists. Please bid another value",
                    Toast.LENGTH_SHORT).show();
            return -1;
        } else if (task.getBestBidder().equals(currentUserId)) {
            Float bestBidTemp = incomingBidFloat;
            String bestBidderIdTemp = currentUserId;
            for(Bid bid: BidList) {
                if(bid.getBidAmount()<bestBidTemp && !task.getBestBidder().equals(bid.getUserId())){
                    Log.i("CHANGE",bid.getBidAmount().toString());
                    bestBidTemp = bid.getBidAmount();
                    GetUserRequest request = new GetUserRequest(bid.getUserId());
                    RequestManager.getInstance().invokeRequest(getApplicationContext(), request);
                    User tempBidder = request.getResult();
                    bestBidderIdTemp = tempBidder.getId();
                }
            }
            task.setBestBid(bestBidTemp);
            task.setBestBidder(bestBidderIdTemp);
        }
        task.updateThis();
        return 0;
    }

    /**
     * setRequesterField
     * sets the text and picture in the requester field
     * @author myapplestory
     */
    public void setRequesterField() {
        String text = "Requester: " + TaskRequester.getName();
        RequesterName.setText(text);
        try {
            RequesterPicture.setImageBitmap(TaskRequester.getPhoto().StringToBitmap());
        } catch (Exception e) {
            Photo defaultPhoto = new Photo("");
            RequesterPicture.setImageBitmap(defaultPhoto.StringToBitmap());
        }
    }

    /**
     * setPoviderField
     * sets the provider text and picture accordingly
     * if the task status is bidded then the best bidder will appear on the field
     * else if the task status is assigned or completed then the provider will appear
     * @author myapplestory
     */
    public void setProviderField() {
        if (task.getStatus().equals("requested")) {
            Photo defaultPhoto = new Photo("");
            ProviderPicture.setImageBitmap(defaultPhoto.StringToBitmap());
            ProviderName.setText("No bidders :'(");
        } else if (task.getStatus().equals("bidded")) {
            ProfileController profileController = new ProfileController(this.findViewById(android.R.id.content),this);
            profileController.setUserID(task.getBestBidder());
            profileController.getUserRequest();
            User tempUser = profileController.getUser();
            String text = "Best bidder: " + tempUser.getName() + "\nBid amount: " + "$" +
                    String.format(Locale.CANADA, "%.2f",task.getBestBid());
            ProviderName.setText(text);
            try {
                ProviderPicture.setImageBitmap(tempUser.getPhoto().StringToBitmap());
            } catch (Exception e) {
                Photo defaultPhoto = new Photo("");
                ProviderPicture.setImageBitmap(defaultPhoto.StringToBitmap());
            }
        } else if (task.getStatus().equals("assigned") || task.isComplete()) {
            String text = "Provider: " + TaskProvider.getName();
            ProviderName.setText(text);
            try {
                ProviderPicture.setImageBitmap(TaskProvider.getPhoto().StringToBitmap());
            } catch (Exception e) {
                Photo defaultPhoto = new Photo("");
                ProviderPicture.setImageBitmap(defaultPhoto.StringToBitmap());
            }
        }
    }

    /**
     * findViews
     * assign variables to their respective views upon init
     * @author myapplestory
     */
    public void findViews(){
        EditButton = findViewById(R.id.EditButton);
        DeleteButton = findViewById(R.id.DeleteButton);
        ProviderPicture = findViewById(R.id.ProviderPicture);
        RequesterPicture = findViewById(R.id.RequesterPicture);
        ProviderName = findViewById(R.id.ProviderName);
        DescriptionView = findViewById(R.id.Description);
        RequesterName = findViewById(R.id.RequesterName);
        TaskName = findViewById(R.id.TaskName);
        TaskStatus = findViewById(R.id.TaskStatus);
        BidslistView = findViewById(R.id.BidsListView);
        scrollView = findViewById(R.id.ViewTaskScrollView);
        BlueButton = findViewById(R.id.BlueButton);
        YellowButton = findViewById(R.id.YellowButton);
        GreenButton = findViewById(R.id.CompleteTaskButton);
        RedButton = findViewById(R.id.AbortTaskButton);
        PinkButton = findViewById(R.id.PinkButton);
        OrangeButton = findViewById(R.id.orangeButton);
    }

    /**
     * setValues
     * set the values of the respective variables upon init
     * @author myapplestory
     */
    public void setValues(){
        viewTaskController.setTaskID(taskID);
        viewTaskController.getTaskRequest();
        task = viewTaskController.getTask();
        currentUserId = currentUser.getInstance().getId();
        taskUserId = task.getTaskRequester().getId();
        taskName = task.getName();
        description = task.getDescription();
        TaskRequester = task.getTaskRequester();
        TaskProvider = task.getTaskProvider();
        BidList = new ArrayList<>();
        TaskName.setText(taskName);
        TaskStatus.setText(task.getStatus());
        DescriptionView.setText(description);

    }

    /**
     * setVisibility
     * sets the visibilities of every button according to the task status and viewer upon init
     * @author myapplestory
     */
    public void setVisibility(){
        // taken from https://stackoverflow.com/questions/3465841/how-to-change-visibility-of-layout-programmatically
        // 2018-03-14
        if (currentUserId.equals(taskUserId)) {
            DeleteButton.setVisibility(View.VISIBLE);
            BlueButton.setVisibility(View.INVISIBLE);
            if (task.getStatus().equals("requested")) {
                EditButton.setVisibility(View.VISIBLE);
                ProviderPicture.setVisibility(View.INVISIBLE);
            }
            else if (task.getStatus().equals("assigned")) {
                EditButton.setVisibility(View.INVISIBLE);
                GreenButton.setVisibility(View.VISIBLE);
                RedButton.setVisibility(View.VISIBLE);
                YellowButton.setVisibility(View.INVISIBLE);
                PinkButton.setVisibility(View.INVISIBLE);
                BidslistView.setVisibility(View.INVISIBLE);
                BidslistView.setVisibility(View.INVISIBLE);
            }
            else {
                EditButton.setVisibility(View.INVISIBLE);
            }
        } else {
            DeleteButton.setVisibility(View.INVISIBLE);
            EditButton.setVisibility(View.INVISIBLE);
            YellowButton.setVisibility(View.INVISIBLE);
            PinkButton.setVisibility(View.INVISIBLE);
            GreenButton.setVisibility(View.INVISIBLE);
            RedButton.setVisibility(View.INVISIBLE);
        }
        if (task.isComplete()) {
            YellowButton.setVisibility(View.INVISIBLE);
            BlueButton.setVisibility(View.INVISIBLE);
            RedButton.setVisibility(View.INVISIBLE);
            GreenButton.setVisibility(View.INVISIBLE);
            PinkButton.setVisibility(View.INVISIBLE);
            OrangeButton.setVisibility(View.VISIBLE);
            BidslistView.setVisibility(View.INVISIBLE);
            if (currentUserId.equals(task.getRequesterId())) {
                OrangeButton.setText("REVIEW PROVIDER");
            } else {
                OrangeButton.setText("REVIEW REQUESTER");
            }
        }
    }

    /**
     * updateBidsList
     * updates bbids list view upon call
     * @author myapplestory
     */
    public void updateBidsList(){
        BidList.clear();
        GetBidsByTaskIdRequest getBidsByTaskIdRequest = new GetBidsByTaskIdRequest(this.taskID);
        RequestManager.getInstance().invokeRequest(getBidsByTaskIdRequest);
        BidList.addAll(getBidsByTaskIdRequest.getResult());
        ExpandableListAdapter expandableListAdapter= new ExpandableBidListAdapter(this, BidList);
        BidslistView.setAdapter(expandableListAdapter);
    }

    /**
     * setUpBidsList
     * get all of this task's bids and pass it into expandable list to display
     * @author myapplestory
     */
    public void setUpBidsList(){
        BidList.clear();
        GetBidsByTaskIdRequest getBidsByTaskIdRequest = new GetBidsByTaskIdRequest(this.taskID);
        RequestManager.getInstance().invokeRequest(getBidsByTaskIdRequest);
        BidList.addAll(getBidsByTaskIdRequest.getResult());
        ExpandableListAdapter expandableListAdapter = new ExpandableBidListAdapter(this, BidList);
        BidslistView.setAdapter(expandableListAdapter);
    }

    /**
     * Add a marker with the location of the task on the map fragment
     * If map is clicked, switch to MapActivity
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                if(task.getLocation()!=null) {
                    Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                    intent.putExtra("lat",Double.toString(task.getLocation().latitude));
                    intent.putExtra("lon",Double.toString(task.getLocation().longitude));
                    intent.putExtra("TaskName",task.getName());
                    startActivity(intent);
                }

            }
        });
        if (task.getLocation()!=null) {

            //mMap.getUiSettings()
            // Add a marker to a location and move the camera
            LatLng taskLocation = task.getLocation();
            mMap.addMarker(new MarkerOptions().position(taskLocation));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(taskLocation));
            moveToCurrentLocation(taskLocation);
            //mMap.
        }else{

        }

    }

    /**
     * Move the camera to a Location
     *
     * @param currentLocation
     */
    private void moveToCurrentLocation(LatLng currentLocation) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);

    }

    /**
     * onActivityResult
     * upon return from EditTaskActivity update
     * the details of teh task and call viewTaskController
     * to update the details of the task
     * also update the activity_profile.xml to reflect
     * the changes in the task
     *
     * @author Micheal-Nguyen
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case (1): {
                //code to add to ESC
                if (resultCode == RESULT_OK) {
                    taskName = data.getStringExtra("Task Name");
                    description = data.getStringExtra("Description");
                    Double latt = Double.parseDouble(data.getStringExtra("Lat"));
                    Double lonn = Double.parseDouble(data.getStringExtra("Lon"));
                    ArrayList<String> photosString = data.getStringArrayListExtra("photos");
                    photos.clear();
                    Bitmap image;
                    for(int i=0; i<photosString.size(); i++){
                        Log.i("test",photosString.get(i));
                        photos.add(new Photo(photosString.get(i)));
                    }
                    recyclerPhotosViewAdapter.notifyDataSetChanged();
                    task.setName(taskName);
                    task.setDescription(description);
                    task.setLocation(new LatLng(latt,lonn));
                    viewTaskController.updateTaskRequest(task);
                    TextView DescriptionView = findViewById(R.id.Description);
                    TextView TaskNameView = findViewById(R.id.TaskName);
                    TaskNameView.setText(taskName);
                    LatLng taskLocation = new LatLng(latt,lonn);
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(taskLocation).title("Your Location"));
                    moveToCurrentLocation(taskLocation);
                    if (description.length() > 0) {
                        DescriptionView.setText(description);
                    } else {
                        DescriptionView.setText("No Description");
                    }
                }
            }
        }
    }
}

