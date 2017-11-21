package org.smartregister.bidan.utils;

import org.smartregister.AllConstants;
import org.smartregister.bidan.BuildConfig;

/**
 * Created by sid-tech on 11/17/17.
 */

public class BidanConstants extends AllConstants {

    public static final boolean TIME_CHECK = BuildConfig.TIME_CHECK;
    public static final long MAX_SERVER_TIME_DIFFERENCE = BuildConfig.MAX_SERVER_TIME_DIFFERENCE;

    public static final int DATABASE_VERSION = BuildConfig.DATABASE_VERSION;

    public static final String CHILD_TABLE_NAME = "ec_child";
    public static final String MOTHER_TABLE_NAME = "ec_mother";


}
