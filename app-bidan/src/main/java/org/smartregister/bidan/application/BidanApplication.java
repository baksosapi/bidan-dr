package org.smartregister.bidan.application;

import android.content.Intent;
import android.widget.Toast;

import org.smartregister.bidan.activity.LoginActivity;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.receiver.TimeChangedBroadcastReceiver;

/**
 * Created by sid-tech on 11/17/17.
 */

public class BidanApplication extends DrishtiApplication
        implements TimeChangedBroadcastReceiver.OnTimeChangedListener {


    @Override
    public void logoutCurrentUser() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getApplicationContext().startActivity(intent);
        context.userService().logoutSession();

    }

    @Override
    public void onTimeChanged() {
//        Toast.makeText(this, R.string.device_time_changed, Toast.LENGTH_LONG).show();
        context.userService().forceRemoteLogin();
        logoutCurrentUser();
    }

    @Override
    public void onTimeZoneChanged() {

    }
}
