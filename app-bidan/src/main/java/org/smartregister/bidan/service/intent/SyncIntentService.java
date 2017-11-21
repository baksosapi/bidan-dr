package org.smartregister.bidan.service.intent;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import org.smartregister.bidan.application.BidanApplication;
import org.smartregister.bidan.sync.BidanAfterFetchListener;
import org.smartregister.service.ActionService;
import org.smartregister.service.AllFormVersionSyncService;
import org.smartregister.service.HTTPAgent;

/**
 * Created by sid-tech on 11/21/17.
 */

public class SyncIntentService extends IntentService {

    private Context context;
    private ActionService actionService;
    private AllFormVersionSyncService allFormVersionSyncService;
    private HTTPAgent httpAgent;
    private BidanAfterFetchListener bidanAfterFetchListener;
    private static final int EVENT_FETCH_LIMIT = 50;

    public SyncIntentService() {
        super("SyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getBaseContext();
        actionService = BidanApplication.getInstance().context().actionService();
        allFormVersionSyncService = BidanApplication.getInstance().context().allFormVersionSyncService();
        httpAgent = BidanApplication.getInstance().context().getHttpAgent();
        bidanAfterFetchListener = new BidanAfterFetchListener();
        return super.onStartCommand(intent, flags, startId);
    }
}
