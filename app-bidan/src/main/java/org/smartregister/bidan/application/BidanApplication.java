package org.smartregister.bidan.application;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.bidan.activity.LoginActivity;
import org.smartregister.bidan.receiver.BidanSyncBroadcastReceiver;
import org.smartregister.bidan.repos.BidanRepository;
import org.smartregister.bidan.utils.BidanConstants;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;
import org.smartregister.sync.DrishtiSyncScheduler;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.receiver.TimeChangedBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

import static org.smartregister.util.Log.logError;

/**
 * Created by sid-tech on 11/17/17.
 */

public class BidanApplication extends DrishtiApplication
        implements TimeChangedBroadcastReceiver.OnTimeChangedListener {

    private static final String TAG = BidanApplication.class.getName();
    private static CommonFtsObject commonFtsObject;
    private EventClientRepository eventClientRepository;


    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject());

        //Initialize Modules
        CoreLibrary.init(context());

        DrishtiSyncScheduler.setReceiverClass(BidanSyncBroadcastReceiver.class);

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

    @Override
    public Repository getRepository() {

        try {
            if (repository == null) {
                repository = new BidanRepository(getInstance().getApplicationContext(), context());
//                uniqueIdRepository();
//                dailyTalliesRepository();
//                monthlyTalliesRepository();
//                hIA2IndicatorsRepository();
                eventClientRepository();
//                stockRepository();
            }
        } catch (UnsatisfiedLinkError e) {
            logError("Error on getRepository: " + e);

        }
        return repository;
    }

    public Context context() {
        return context;
    }

    private static String[] getFtsTables() {
        return new String[]{"ec_kartu_ibu", "ec_anak", "ec_ibu", "ec_pnc" };
    }

    public static CommonFtsObject createCommonFtsObject() {
        if (commonFtsObject == null) {
            commonFtsObject = new CommonFtsObject(getFtsTables());
            for (String ftsTable : commonFtsObject.getTables()) {
                commonFtsObject.updateSearchFields(ftsTable, getFtsSearchFields(ftsTable));
                commonFtsObject.updateSortFields(ftsTable, getFtsSortFields(ftsTable));
                // OPTIONAL
                commonFtsObject.updateMainConditions(ftsTable, getFtsMainConditions(ftsTable));
            }
        }

        return commonFtsObject;
    }

    private static String[] getFtsSearchFields(String tableName) {
        if(tableName.equals("ec_kartu_ibu")){
            String[] ftsSearchFields =  { "namalengkap", "namaSuami" };
            return ftsSearchFields;
        } else if(tableName.equals("ec_anak")){
            String[] ftsSearchFields =  { "namaBayi" };
            return ftsSearchFields;
        } else if (tableName.equals("ec_ibu")){
            String[] ftsSearchFields =  { "namalengkap", "namaSuami"};
            return ftsSearchFields;
        }
        else if (tableName.equals("ec_pnc")) {
            String[] ftsSearchFields = {"namalengkap", "namaSuami"};
            return ftsSearchFields;
        }
        return null;
    }

    private static String[] getFtsSortFields(String tableName) {

        if(tableName.equals("ec_kartu_ibu")) {
            String[] sortFields = { "namalengkap", "umur",  "noIbu", "htp"};
            return sortFields;
        } else if(tableName.equals("ec_anak")){
            String[] sortFields = { "namaBayi", "tanggalLahirAnak" };
            return sortFields;
        } else if(tableName.equals("ec_ibu")){
            String[] sortFields = { "namalengkap", "umur", "noIbu", "pptest" , "htp" };
            return sortFields;
        } else if(tableName.equals("ec_pnc")){
            String[] sortFields = { "namalengkap", "umur", "noIbu", "keadaanIbu"};
            return sortFields;
        }
        return null;
    }

    private  static String[] getFtsMainConditions(String tableName){
        if(tableName.equals("ec_kartu_ibu")) {
            String[] mainConditions = { "is_closed", "jenisKontrasepsi" };
            return mainConditions;
        } else if(tableName.equals("ec_anak")){
            String[] mainConditions = { "is_closed", "relational_id" };
            return mainConditions;
        } else if(tableName.equals("ec_ibu")){
            String[] mainConditions = { "is_closed", "type", "pptest" , "kartuIbuId" };
            return mainConditions;
        } else if(tableName.equals("ec_pnc")){
            String[] mainConditions = { "is_closed","keadaanIbu" , "type"};
            return mainConditions;
        }
        return null;
    }


    public EventClientRepository eventClientRepository() {
        if (eventClientRepository == null) {
            eventClientRepository = new EventClientRepository(getRepository());
        }
        return eventClientRepository;
    }

}
