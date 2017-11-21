package org.smartregister.bidan.activity;

import android.database.Cursor;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.smartregister.Context;
import org.smartregister.bidan.R;
import org.smartregister.bidan.controller.BidanNavigationController;
import org.smartregister.bidan.utils.AllConstantsINA;
import org.smartregister.bidan.utils.formula.Support;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.enketo.view.fragment.DisplayFormFragment;
import org.smartregister.event.Listener;
import org.smartregister.service.PendingFormSubmissionService;
import org.smartregister.view.activity.SecuredActivity;
import org.smartregister.view.contract.HomeContext;
import org.smartregister.view.controller.NativeAfterANMDetailsFetchListener;
import org.smartregister.view.controller.NativeUpdateANMDetailsTask;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.valueOf;
import static org.smartregister.event.Event.ACTION_HANDLED;
import static org.smartregister.event.Event.FORM_SUBMITTED;
import static org.smartregister.event.Event.SYNC_COMPLETED;
import static org.smartregister.event.Event.SYNC_STARTED;


/**
 * Created by sid-tech on 11/17/17.
 */

public class BidanHomeActivity extends SecuredActivity {

    private static final String TAG = BidanHomeActivity.class.getName();
    private TextView ecRegisterClientCountView;
    private TextView kartuIbuANCRegisterClientCountView;
    private TextView kartuIbuPNCRegisterClientCountView;
    private TextView kohortKbCountView;
    private TextView anakRegisterClientCountView;
    private TextView ibuRegisterClientCountView;

    // MENU
    private MenuItem updateMenuItem;
    private MenuItem remainingFormsToSyncMenuItem;

    private static int kicount;
    private int childcount;

    private PendingFormSubmissionService pendingFormSubmissionService;


    @Override
    protected void onCreation() {

        setContentView(R.layout.smart_registers_home_bidan);

        navigationController = new BidanNavigationController(this, anmController, context());
        setupViews();
        initialize();

        DisplayFormFragment.formInputErrorMessage = getResources().getString(R.string.forminputerror);
        DisplayFormFragment.okMessage = getResources().getString(R.string.okforminputerror);

    }


    @Override
    protected void onResumption() {
        updateRegisterCounts();
        updateSyncIndicator();
        updateRemainingFormsToSyncCount();
    }


    // SYNC

    private void updateSyncIndicator() {
        if (updateMenuItem != null) {
            if (context().allSharedPreferences().fetchIsSyncInProgress()) {
                updateMenuItem.setActionView(R.layout.progress);
            } else
                updateMenuItem.setActionView(null);
        }
    }

    private Listener<Boolean> onSyncStartListener = new Listener<Boolean>() {
        @Override
        public void onEvent(Boolean data) {
            Support.ONSYNC = true;
            AllConstantsINA.SLEEP_TIME = 15000;
            if (updateMenuItem != null) {
                updateMenuItem.setActionView(R.layout.progress);
            }
        }
    };

    private Listener<Boolean> onSyncCompleteListener = new Listener<Boolean>() {
        @Override
        public void onEvent(Boolean data) {
            //#TODO: RemainingFormsToSyncCount cannot be updated from a back ground thread!!
            updateRemainingFormsToSyncCount();
            if (updateMenuItem != null) {
                updateMenuItem.setActionView(null);
            }
            updateRegisterCounts();

            AllConstantsINA.SLEEP_TIME = AllConstantsINA.WAITING_TIME;
            flagActivator();

        }
    };

    private Listener<String> onFormSubmittedListener = new Listener<String>() {
        @Override
        public void onEvent(String instanceId) {
            updateRegisterCounts();
        }
    };

    private Listener<String> updateANMDetailsListener = new Listener<String>() {
        @Override
        public void onEvent(String data) {
            updateRegisterCounts();
        }
    };


    private void flagActivator(){
        Log.i(LOG_TAG, "flag activator executed");
        new Thread(){
            public void run(){
                try{
                    while(AllConstantsINA.SLEEP_TIME>0){
                        sleep(1000);
                        if(AllConstantsINA.IDLE)
                            AllConstantsINA.SLEEP_TIME-=1000;
                    }
                    Support.ONSYNC=false;
                }catch (InterruptedException ie){

                }
            }
        }.start();
    }

    private void updateRegisterCounts() {
        NativeUpdateANMDetailsTask task = new NativeUpdateANMDetailsTask(Context.getInstance().anmController());
        task.fetch(new NativeAfterANMDetailsFetchListener() {
            @Override
            public void afterFetch(HomeContext anmDetails) {
                updateRegisterCounts(anmDetails);
            }
        });
    }

    private void updateRegisterCounts(HomeContext homeContext) {
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder();

        Cursor kicountcursor = context().commonrepository("ec_kartu_ibu").rawCustomQueryForAdapter(
                sqb.queryForCountOnRegisters("ec_kartu_ibu_search", "ec_kartu_ibu_search.is_closed=0"));
        kicountcursor.moveToFirst();
        kicount= kicountcursor.getInt(0);
//        kicount = 10;
        kicountcursor.close();

        Cursor kbcountcursor = context().commonrepository("ec_kartu_ibu").rawCustomQueryForAdapter(
                sqb.queryForCountOnRegisters(
                        "ec_kartu_ibu_search",
                        "ec_kartu_ibu_search.is_closed=0 and jenisKontrasepsi !='0'" ));
        kbcountcursor.moveToFirst();
        int kbcount = kbcountcursor.getInt(0);
        Log.e(TAG, "updateRegisterCounts: "+ kbcount );
        kbcountcursor.close();


        // ec_ibu_search no table
        Cursor anccountcursor = context().commonrepository("ec_ibu").rawCustomQueryForAdapter(
                sqb.queryForCountOnRegisters(
                        "ec_ibu_search",
                        "ec_ibu_search.is_closed=0 "));
        anccountcursor.moveToFirst();
        int anccount = anccountcursor.getInt(0);
        anccountcursor.close();
//
        // No pnc_search
        Cursor pnccountcursor = context().commonrepository("ec_pnc").rawCustomQueryForAdapter(
                sqb.queryForCountOnRegisters(
                        "ec_pnc_search",
                        "ec_pnc_search.is_closed=0 AND (ec_pnc_search.keadaanIbu ='hidup' OR ec_pnc_search.keadaanIbu IS NULL) ")); // and ec_pnc_search.keadaanIbu LIKE '%hidup%'
        pnccountcursor.moveToFirst();
        int pnccount = pnccountcursor.getInt(0);
        pnccountcursor.close();

        Cursor childcountcursor = context().commonrepository("anak").rawCustomQueryForAdapter(
                sqb.queryForCountOnRegisters(
                        "ec_anak_search",
                        "ec_anak_search.is_closed=0"));
        childcountcursor.moveToFirst();
        int childcount = childcountcursor.getInt(0);
        childcountcursor.close();

        ecRegisterClientCountView.setText(valueOf(kicount));
        kartuIbuANCRegisterClientCountView.setText(valueOf(anccount));
        kartuIbuPNCRegisterClientCountView.setText(valueOf(pnccount));
        anakRegisterClientCountView.setText(valueOf(childcount));
        kohortKbCountView.setText(valueOf(kbcount));
    }


    private void updateRemainingFormsToSyncCount() {
        if (remainingFormsToSyncMenuItem == null) {
            return;
        }

        long size = pendingFormSubmissionService.pendingFormSubmissionCount();
        if (size > 0) {
            remainingFormsToSyncMenuItem.setTitle(valueOf(size) + " " + getString(R.string.unsynced_forms_count_message));
            remainingFormsToSyncMenuItem.setVisible(true);
        } else {
            remainingFormsToSyncMenuItem.setVisible(false);
        }
    }



    private void setupViews() {
        findViewById(R.id.btn_reporting).setOnClickListener(onButtonsClickListener);

        anakRegisterClientCountView = (TextView) findViewById(R.id.txt_vaksinator_register_client_count);
        ibuRegisterClientCountView = (TextView) findViewById(R.id.txt_TT_vaksinator_register_client_count);

        findViewById(R.id.btn_kartu_ibu_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_kartu_ibu_anc_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_kartu_ibu_pnc_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_anak_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_kohort_kb_register).setOnClickListener(onRegisterStartListener);

        findViewById(R.id.btn_reporting).setOnClickListener(onButtonsClickListener);
//        findViewById(R.id.btn_videos).setOnClickListener(onButtonsClickListener);

        ecRegisterClientCountView = (TextView) findViewById(R.id.txt_kartu_ibu_register_client_count);
        kartuIbuANCRegisterClientCountView = (TextView) findViewById(R.id.txt_kartu_ibu_anc_register_client_count);
        kartuIbuPNCRegisterClientCountView = (TextView) findViewById(R.id.txt_kartu_ibu_pnc_register_client_count);
        anakRegisterClientCountView = (TextView) findViewById(R.id.txt_anak_client_count);
        kohortKbCountView = (TextView) findViewById(R.id.txt_kohort_kb_register_count);
    }

    private void initialize() {
        pendingFormSubmissionService = context().pendingFormSubmissionService();
        // SYNC
        SYNC_STARTED.addListener(onSyncStartListener);
        SYNC_COMPLETED.addListener(onSyncCompleteListener);
        FORM_SUBMITTED.addListener(onFormSubmittedListener);
        ACTION_HANDLED.addListener(updateANMDetailsListener);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(getResources().getDrawable(R.mipmap.logo));
        getSupportActionBar().setLogo(R.mipmap.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        LoginActivity.setLanguage();
//        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.action_bar_background));
    }

    private View.OnClickListener onButtonsClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_reporting:
//                    navigationController.startReports();
                    break;

//                case R.id.btn_videos:
//                    navigationController.startVideos();
//                    break;
            }
        }
    };

    private View.OnClickListener onRegisterStartListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {

                case R.id.btn_kartu_ibu_register:
                    navigationController.startECSmartRegistry();
                    break;

                case R.id.btn_kohort_kb_register:
                    navigationController.startFPSmartRegistry();
                    break;

                case R.id.btn_kartu_ibu_anc_register:
                    navigationController.startANCSmartRegistry();
                    break;

                case R.id.btn_kartu_ibu_pnc_register:
                    navigationController.startPNCSmartRegistry();
                    break;

                case R.id.btn_anak_register:
                    navigationController.startChildSmartRegistry();
                    break;

            }

        }
    };



}
