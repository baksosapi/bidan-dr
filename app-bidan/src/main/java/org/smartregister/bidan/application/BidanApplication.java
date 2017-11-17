package org.smartregister.bidan.application;

import android.content.Intent;
import android.widget.Toast;

import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.bidan.activity.LoginActivity;
import org.smartregister.bidan.utils.BidanConstants;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.sync.DrishtiSyncScheduler;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.receiver.TimeChangedBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sid-tech on 11/17/17.
 */

public class BidanApplication extends DrishtiApplication
        implements TimeChangedBroadcastReceiver.OnTimeChangedListener {

    private static CommonFtsObject commonFtsObject;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject());

        //Initialize Modules
        CoreLibrary.init(context());

        TimeChangedBroadcastReceiver.init(this);
        TimeChangedBroadcastReceiver.getInstance().addOnTimeChangedListener(this);

//        applyUserLanguagePreference();
//        cleanUpSyncState();
//        initOfflineSchedules();
//        setCrashlyticsUser(context);
//        setAlarms(this);

    }

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

    public static synchronized BidanApplication getInstance() {
        return (BidanApplication) mInstance;
    }


    public Context context() {
        return context;
    }

    private static String[] getFtsTables() {
        return new String[]{BidanConstants.CHILD_TABLE_NAME, BidanConstants.MOTHER_TABLE_NAME};
    }

    public static CommonFtsObject createCommonFtsObject() {
        if (commonFtsObject == null) {
            commonFtsObject = new CommonFtsObject(getFtsTables());
            for (String ftsTable : commonFtsObject.getTables()) {
                commonFtsObject.updateSearchFields(ftsTable, getFtsSearchFields(ftsTable));
                commonFtsObject.updateSortFields(ftsTable, getFtsSortFields(ftsTable));
            }
        }

        return commonFtsObject;
    }
    
    private static String[] getFtsSearchFields(String tableName) {
        if (tableName.equals(BidanConstants.CHILD_TABLE_NAME)) {
            return new String[]{"zeir_id", "epi_card_number", "first_name", "last_name"};
        } else if (tableName.equals(BidanConstants.MOTHER_TABLE_NAME)) {
            return new String[]{"zeir_id", "epi_card_number", "first_name", "last_name", "father_name", "husband_name", "contact_phone_number"};
        }
        return null;
    }

    private static String[] getFtsSortFields(String tableName) {

        if (tableName.equals(BidanConstants.CHILD_TABLE_NAME)) {
//            ArrayList<VaccineRepo.Vaccine> vaccines = VaccineRepo.getVaccines("child");
//            List<String> names = new ArrayList<>();
//            names.add("first_name");
//            names.add("dob");
//            names.add("zeir_id");
//            names.add("last_interacted_with");
//            names.add("inactive");
//            names.add("lost_to_follow_up");
//            names.add(BidanConstants.EC_CHILD_TABLE.DOD);
//
//            for (VaccineRepo.Vaccine vaccine : vaccines) {
//                names.add("alerts." + VaccinateActionUtils.addHyphen(vaccine.display()));
//            }

//            return names.toArray(new String[names.size()]);
            return new String[]{"first_name", "dob", "zeir_id", "last_interacted_with", "inactive"};

        } else if (tableName.equals(BidanConstants.MOTHER_TABLE_NAME)) {
            return new String[]{"first_name", "dob", "zeir_id", "last_interacted_with"};
        }
        return null;
    }


}
