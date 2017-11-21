package org.smartregister.bidan.repos;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.bidan.application.BidanApplication;
import org.smartregister.bidan.utils.BidanConstants;
import org.smartregister.repository.Repository;

/**
 * Created by sid-tech on 11/21/17.
 */

public class BidanRepository extends Repository {

    private SQLiteDatabase readableDatabase;
    private SQLiteDatabase writableDatabase;
    private final Context context;

    public BidanRepository(Context context, org.smartregister.Context opensrpContext) {
        super(context, BidanConstants.DATABASE_NAME, BidanConstants.DATABASE_VERSION, opensrpContext.session(),
                BidanApplication.createCommonFtsObject(), opensrpContext.sharedRepositoriesArray());
        this.context = context;
    }
}
