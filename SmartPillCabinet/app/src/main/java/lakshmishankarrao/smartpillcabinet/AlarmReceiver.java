package lakshmishankarrao.smartpillcabinet;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by lakshmi on 3/1/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {


    public final int VISIBILITY_PRIVATE = 0;
    CredentialInfo credentialInfo;
    private FetchPillInfoTask mAuthTask = null;

    private Context mContext = null;

    private static int notificationId = 1111;

    @Override
    public void onReceive(Context context, Intent intent) {
        int requestCode = 1253;
        mContext = context;

        Log.i("AlarmReceiver","entering");


        credentialInfo = getCredentialsInfo(context);

        getApprovalAndSendNoti(intent, context);
    }





    private CredentialInfo getCredentialsInfo(Context context) {
        CredentialsDbHelper dbHelper = new CredentialsDbHelper(context);
        CredentialInfo info = dbHelper.getCredentialsInfo();
        return info;
    }

    private boolean getApprovalAndSendNoti(Intent intent, Context context) {
        CredentialsDbHelper db = new CredentialsDbHelper(context);

        String purpose = intent.getStringExtra(UtilsClass.PURPOSE);

        CredentialsDbHelper dbHelper = new CredentialsDbHelper(context);
        CredentialInfo info = dbHelper.getCredentialsInfo();

        if(info == null){
            Log.d("getApprovalAndSendNoti","False! No rec! so no patient id");
            return false;
        }
        if(info.patient_id == null || info.patient_id.isEmpty()){
            return false;
        }


        switch (purpose) {
            case UtilsClass.PURPOSE_REFILL_REMINDER:
                if (mAuthTask != null) {
                    Log.d("mAuthTask", "not null");
                    return false;
                }
                mAuthTask = new FetchPillInfoTask(UtilsClass.GET_REQ_PILL_REFILL+info.patient_id, purpose);
                mAuthTask.execute((Void) null);
            case UtilsClass.PURPOSE_TIME_BASED_REMINDER:
                if (mAuthTask != null) {
                    Log.d("mAuthTask", "not null");
                    return false;
                }
                String slot = getSlot();

                mAuthTask = new FetchPillInfoTask(UtilsClass.GET_REQ_TIMELY_REM+
                        info.patient_id+slot, purpose);
                mAuthTask.execute((Void) null);
            case UtilsClass.PURPOSE_PILL_MISSED_FAMILY_NOTI:
                if (mAuthTask != null) {
                    Log.d("mAuthTask", "not null");
                    return false;
                }
                String slott = getSlot();

                mAuthTask = new FetchPillInfoTask(UtilsClass.GET_REQ_TIMELY_REM+
                        info.patient_id+slott, purpose);
                mAuthTask.execute((Void) null);

        }
        return false;
    }

    private String getSlot() {
        Calendar c = Calendar.getInstance();
        Long currentTimeMilli = c.getTimeInMillis();
        int currentHour = c.get(Calendar.HOUR_OF_DAY);

        if(currentHour  == UtilsClass.MORN_START_PERIOD || currentHour == UtilsClass.MORN_END_PERIOD){
            return UtilsClass.MORN_SLOT;
        }else if(currentHour == UtilsClass.AFT_START_PERIOD || currentHour == UtilsClass.AFT_END_PERIOD){
            return UtilsClass.AFT_SLOT;
        }else if(currentHour == UtilsClass.NIGHT_START_PERIOD || currentHour == UtilsClass.NIGHT_END_PERIOD){
            return UtilsClass.NIGHT_SLOT;
        }
        return  UtilsClass.MORN_SLOT;


    }

    private String getParsedResponse(String response) {
        if (response.isEmpty()) return "";

        String[] s1 = response.split("\\{");
        String[] s2 = s1[1].split("\\}");
        String[] presc = s2[0].split("\"");
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < presc.length; ++i) {
            if (!presc[i].trim().isEmpty()) {
                res.append(presc[i]);
                if (i < (presc.length - 1)) {
                    res.append(", ");
                }
            }
        }
        return res.toString();

    }


    NotificationCompat.InboxStyle createBigContent(String contentTitle, List<String> contentText) {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(contentTitle);
            inboxStyle.addLine(contentText.get(0));
        inboxStyle.setSummaryText(contentText.get(1));
        return inboxStyle;
    }


    private class FetchPillInfoTask extends AsyncTask<Void, Void, String> {

        private final String mURL;
        private final String mPurpose;
        FetchPillInfoTask(String url, String purpose){
            mURL = url;
            mPurpose = purpose;
        }
        protected String doInBackground(Void... params) {

            String response = HttpHandlerClass.getHttpResponseFor(mURL);
            String parsedResponse = getParsedResponse(response);
            Log.d("Response", parsedResponse+"abraca");
            return parsedResponse;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(final String result) {
            //showDialog("Downloaded " + result + " bytes");
            mAuthTask = null;
            Log.d("result", result+"abraca");

            if(result.equals("1") || result.equals("0") || result.equals("-1")){
                //no need of notification
                Log.d("No noti",result);
            }else{
                String title = "";
                String title2 = "";
                String contentTitle = "";
                Intent resultIntent = new Intent(mContext, DetailsDisplay.class);
                resultIntent.putExtra(UtilsClass.PURPOSE, mPurpose);
                resultIntent.putExtra(UtilsClass.INTENT_EXTRAS_MEDICINES, result);
                if (mPurpose.equals(UtilsClass.PURPOSE_REFILL_REMINDER)) {
                    title = "Time to order Refill";
                     title2 = "Click to see details";
                     contentTitle = "Pill Refill Notification";
                } else if (mPurpose.equals(UtilsClass.PURPOSE_TIME_BASED_REMINDER)){
                    // (mPurpose.equals(UtilsClass.PURPOSE_TIME_BASED_REMINDER))
                    title = "Click to view pills due";
                     title2 = "Click to see details";
                     contentTitle = "Time for pills";
                }else if (mPurpose.equals(UtilsClass.PURPOSE_PILL_MISSED_FAMILY_NOTI)){
                    title = "Family care at your fingertips";
                    title2 = "Click to see details";
                    contentTitle = "Pill missed Notification";
                }
                List<String> contentText = new ArrayList<>();
                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(mContext, notificationId, resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);

                Calendar calendar = Calendar.getInstance();
                long when = calendar.getTimeInMillis();


                contentText.add(title);
                contentText.add(title2);


                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(mContext)
                                .setSmallIcon(R.drawable.reminder_icon)
                                .setColor(0xFF080D41)
                                .setContentTitle(contentTitle)
                                .setContentText(contentText.get(0))
                                .setContentIntent(resultPendingIntent)
                                .setWhen(when)
                                .setVisibility(VISIBILITY_PRIVATE)
                                .setAutoCancel(true)
                                .setSound(uri)
                                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher));


                mBuilder.setStyle(createBigContent(contentTitle,contentText ));

                NotificationManager mNotificationManager =
                        (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(notificationId, mBuilder.build());
                notificationId++;

            }

        }


        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}
