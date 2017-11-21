package org.smartregister.bidan.sync;

import org.smartregister.domain.FetchStatus;
import org.smartregister.sync.AfterFetchListener;

import static org.smartregister.event.Event.ON_DATA_FETCHED;

/**
 * Created by sid-tech on 11/21/17.
 */

public class BidanAfterFetchListener implements AfterFetchListener {
    @Override
    public void afterFetch(FetchStatus fetchStatus) {

    }

    public void partialFetch(FetchStatus fetchStatus) {
        ON_DATA_FETCHED.notifyListeners(fetchStatus);
    }
}
