package phuonglam.mobile.webservice.helper;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;

import phuonglam.mobile.webservice.R;
import phuonglam.mobile.webservice.activity.ListFriendActivity;
import phuonglam.mobile.webservice.activity.ListPictureActivity;
import phuonglam.mobile.webservice.activity.ListUserActivity;
import phuonglam.mobile.webservice.activity.MainActivity;
import phuonglam.mobile.webservice.activity.NewsfeedActivity;
import phuonglam.mobile.webservice.activity.ProfileActivity;
import phuonglam.mobile.webservice.activity.SearchActivity;

/**
 * Created by Phuo on 4/21/2016.
 */
public class NavigationHelper {
    public static void Navigation(int id, Activity activity){
        Intent  intent;
        switch (id){
            case R.id.action_myprofile:
                intent = new Intent(activity.getApplicationContext(), ProfileActivity.class);
                intent.putExtra("userAuth", activity.getIntent().getStringExtra("userAuth"));
                intent.putExtra("userId", activity.getIntent().getStringExtra("userId"));
                intent.putExtra("currentUserId", activity.getIntent().getStringExtra("userId"));
                activity.startActivity(intent);
                break;
            case R.id.action_friendlist:
                intent = new Intent(activity.getApplicationContext(), ListFriendActivity.class);
                intent.putExtra("userAuth", activity.getIntent().getStringExtra("userAuth"));
                intent.putExtra("userId", activity.getIntent().getStringExtra("userId"));
                activity.startActivity(intent);
                break;
            case R.id.action_mypic:
                intent = new Intent(activity.getApplicationContext(), ListPictureActivity.class);
                intent.putExtra("userAuth", activity.getIntent().getStringExtra("userAuth"));
                intent.putExtra("userId", activity.getIntent().getStringExtra("userId"));
                intent.putExtra("currentUserId", activity.getIntent().getStringExtra("userId"));
                activity.startActivity(intent);
                break;
            case R.id.action_newsfeed:
                intent = new Intent(activity.getApplicationContext(),  NewsfeedActivity.class);
                intent.putExtra("userAuth", activity.getIntent().getStringExtra("userAuth"));
                intent.putExtra("userId", activity.getIntent().getStringExtra("userId"));
                activity.startActivity(intent);
                break;
            case R.id.action_search:
                intent = new Intent(activity.getApplicationContext(), SearchActivity.class);
                intent.putExtra("userAuth", activity.getIntent().getStringExtra("userAuth"));
                intent.putExtra("userId", activity.getIntent().getStringExtra("userId"));
                activity.startActivity(intent);
                break;
            case R.id.action_signout:
                intent = new Intent(activity.getApplicationContext(), MainActivity.class);
                activity.startActivity(intent);
                break;
            case R.id.action_about:
                break;
        }
    }
}
