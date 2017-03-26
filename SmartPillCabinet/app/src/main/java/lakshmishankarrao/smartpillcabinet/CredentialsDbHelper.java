package lakshmishankarrao.smartpillcabinet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by lakshmi on 3/1/2017.
 */
public class CredentialsDbHelper extends SQLiteOpenHelper {

    static private final int VERSION = 3;
    static private final String DB_NAME = "credentialsDatabase.db";
    Context context;

    public CredentialsDbHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.context = context;
    }

    static public final String TABLE_CREDENTIALS = "credentials";
    static public final String ID = "_id";
    static public final String PATIENT_ID = "patient_id";
    static public final String PASSWORD = "password";
    static public final String ALARM_INITIATED = "alarm_initiated";
    static public final String CHOICE = "choice";
    static public final String PATIENT_NAME = "patient_name";



    static private final String SQL_CREATE_CREDENTIALS_TABLE =
            "CREATE TABLE "+TABLE_CREDENTIALS+" (" +
                    "  "+ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "  "+PATIENT_ID+" TEXT," +
                    "  "+PASSWORD+" TEXT," +
                    "  "+ALARM_INITIATED+" INTEGER," +
                    "  "+PATIENT_NAME+" TEXT," +
                    "  "+CHOICE+" TEXT);";

    static private final String SQL_DROP_TABLE = "DROP TABLE credentials";


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_CREDENTIALS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL(SQL_DROP_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CREDENTIALS_TABLE);

    }

    public long addCredentials(CredentialInfo ci) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PATIENT_ID, ci.patient_id);
        contentValues.put(PASSWORD, ci.password);
        contentValues.put(ALARM_INITIATED, ci.alarm_initiated);
        contentValues.put(CHOICE, ci.choice);
        contentValues.put(PATIENT_NAME, ci.patient_name);


        //alarm ini to be added.
        return db.insert(TABLE_CREDENTIALS, null, contentValues);
    }

    public void deleteCredentials(String table_name) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from " + table_name);
    }



    public Cursor fetchAndGetCurrCredentialWithPatientID(String patient_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+TABLE_CREDENTIALS+" WHERE "+PATIENT_ID+"='" + patient_id +"';", null);
    }
    public Cursor fetchCurrCredentials() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+TABLE_CREDENTIALS+";", null);
    }
    public CredentialInfo getCredentialsInfo(){
        Cursor c = fetchCurrCredentials();
        if (c == null || c.getCount() < 1) {//rec doesn't exist
            Log.d("getCredentialsInfo","null or cnt <1");

            return null;
        }else{
            c.moveToFirst();
            Log.d("num of rec in db",c.getCount()+"");
            String pat_id = c.getString(c.getColumnIndex(PATIENT_ID));
            String pwd = c.getString(c.getColumnIndex(PASSWORD));
            int alarm_set = c.getInt(c.getColumnIndex(ALARM_INITIATED));
            String choyce = c.getString(c.getColumnIndex(CHOICE));
            String pat_name = c.getString(c.getColumnIndex(PATIENT_NAME));


            CredentialInfo info = new CredentialInfo(pat_id,pwd,alarm_set, choyce, pat_name);
            return info;
        }
    }

    public int isRecordSameAndAlarmSet(String patientId, String password, String choice) {
        Cursor c = fetchAndGetCurrCredentialWithPatientID(patientId);
        if (c == null || c.getCount() < 1) {//rec doesn't exist

            return -2;
        }else{//exists
            c.moveToFirst();
            String pwd = (c.getString(c.getColumnIndex(CredentialsDbHelper.PASSWORD)));
            int alarm_initiated = (c.getInt(c.getColumnIndex(CredentialsDbHelper.ALARM_INITIATED)));
            String choyce = (c.getString(c.getColumnIndex(CredentialsDbHelper.CHOICE)));
            String patName = (c.getString(c.getColumnIndex(CredentialsDbHelper.PATIENT_NAME)));



            if(pwd.equals(password)) {//same record?
                if (alarm_initiated == 1) {//alarm set?
                    if(choyce.equals(choice)) {
                        return 1;
                    }
                }
//                else {// If alarm not initiated as good as diff rec. Same steps delete entry,
// init alarm and add to db. Only add given no delete.
//                    return 0;
//                }
            }

            return -1;//not same record
            //check if record.password is the same.
            //if same return true
            //else return false
        }
    }


    public boolean updateDBWithPatientName(String patientId, String patientName) {
        Cursor c = fetchAndGetCurrCredentialWithPatientID(patientId);
        if (c == null || c.getCount() < 1) {//rec doesn't exist

            return false;
        }else {//exists
            c.moveToFirst();
            String pwd = (c.getString(c.getColumnIndex(CredentialsDbHelper.PASSWORD)));
            int alarm_initiated = (c.getInt(c.getColumnIndex(CredentialsDbHelper.ALARM_INITIATED)));
            String choyce = (c.getString(c.getColumnIndex(CredentialsDbHelper.CHOICE)));
            deleteCredentials(TABLE_CREDENTIALS);
            addCredentials(new CredentialInfo(patientId,pwd,alarm_initiated,choyce, patientName));
        }
        return true;
    }
}
