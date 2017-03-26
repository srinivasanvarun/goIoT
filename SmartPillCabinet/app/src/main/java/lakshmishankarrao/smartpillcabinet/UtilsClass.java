package lakshmishankarrao.smartpillcabinet;

/**
 * Created by lakshmi on 3/14/2017.
 */
public class UtilsClass {

    public static final String GET_REQ_LOGIN_URL = "/info.mourya.goiot/androidLoginDetails/";
    public static final String GET_REQ_TIMELY_REM = "/info.mourya.goiot/alarmInput/";
    public static final String GET_REQ_PILL_REFILL = "/info.mourya.goiot/refillCheck/";
    public static final String GET_REQ_FULL_DAY_PRESCRIPTIONS = "/info.mourya.goiot/prescriptions/";


    //public static final int


    public static final String PURPOSE_TIME_BASED_REMINDER = "time_based_rem";
    public static final String PURPOSE_REFILL_REMINDER = "refill_rem";
    public static final String PURPOSE_PILL_MISSED_FAMILY_NOTI = "pill_miss_fmly_noti";
    public static final String PURPOSE_JUST_LOGIN = "just_login";



    public static final String PURPOSE = "purpose";

    public static final String MORN_SLOT = "/m";
    public static final String AFT_SLOT = "/a";
    public static final String NIGHT_SLOT = "/e";

    public static final int MORN_START_PERIOD = 8;
    public static final int AFT_START_PERIOD = 13;
    public static final int NIGHT_START_PERIOD = 19;

    public static final int MORN_END_PERIOD = 10;
    public static final int AFT_END_PERIOD = 15;
    public static final int NIGHT_END_PERIOD = 21;

    public static final int REFILL_CHECK_TIME = 17;

    public static final long INTERVAL_TIME = 300000L;//testing purposes

    public static final String CHOICE_SELF = "Self";
    public static final String CHOICE_FAMILY = "Family";


    public static final String INTENT_EXTRAS_MEDICINES = "medicines";

    public static final String SERVER_ADDR = "35.167.209.121";
    public static final Integer SERVER_PORT = 8080;





}
