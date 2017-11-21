package org.smartregister.bidan.controller;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.bidan.R;
import org.smartregister.bidan.activity.BidanHomeActivity;
import org.smartregister.bidan.activity.KANCSmartRegisterActivity;
import org.smartregister.bidan.activity.KChildSmartRegisterActivity;
import org.smartregister.bidan.activity.KFPSmartRegisterActivity;
import org.smartregister.bidan.activity.KMotherSmartRegisterActivity;
import org.smartregister.bidan.activity.KPNCSmartRegisterActivity;
import org.smartregister.view.controller.ANMController;
import org.smartregister.view.controller.NavigationController;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by sid-tech on 11/17/17.
 */

public class BidanNavigationController extends org.smartregister.view.controller.NavigationController {
    private Activity activity;
    private ANMController anmController;
    private org.smartregister.Context context;

    public BidanNavigationController(Activity activity, ANMController anmController) {
        super(activity,anmController);
        this.activity = activity;
        this.anmController = anmController;
    }

    public BidanNavigationController(Activity activity, ANMController anmController, org.smartregister.Context context) {
        this(activity,anmController);
        this.context=context;
    }

    /**
     * Activity for KOHORT IBU
     */
    @Override
    public void startECSmartRegistry() {
        openActivity(KMotherSmartRegisterActivity.class);
    }

    @Override
    public void startFPSmartRegistry() {
        openActivity(KFPSmartRegisterActivity.class);
    }

    @Override
    public void startANCSmartRegistry() {
        openActivity(KANCSmartRegisterActivity.class);
    }

    @Override
    public void startPNCSmartRegistry() {
        openActivity(KPNCSmartRegisterActivity.class);
    }

    @Override
    public void startChildSmartRegistry() {
        openActivity(KChildSmartRegisterActivity.class);
    }


    @Override
    public void startReports() {
        String id, pass;
        try{
            id = new JSONObject(anmController.get()).get("anmName").toString();
            pass = context.allSettings().fetchANMPassword();
        }catch(org.json.JSONException ex){
            id="noname";
            pass="null";
        }
        String uri = "http://"+id+":"+pass+"@"+activity.getApplicationContext().getString(R.string.dho_site).replace("http://","");
        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
    }

    private void openActivity(Class<?> targetClass) {
        activity.startActivity(new Intent(activity, targetClass));

        SharedPreferences sp = getDefaultSharedPreferences(this.activity);
        if(sp.getBoolean("firstlaunch", true))
            sp.edit().putBoolean("firstlaunch", false).apply();

    }

}