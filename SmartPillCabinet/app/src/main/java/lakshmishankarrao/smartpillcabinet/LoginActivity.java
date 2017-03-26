package lakshmishankarrao.smartpillcabinet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.PendingIntent;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by lakshmi on 3/1/2017.
 */

/**
 * A login screen that offers login via patientId/password.
 */
public class LoginActivity extends ActionBarActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private FetchPrescriptionTask mPrescriptionTask = null;

    // UI references.
    private AutoCompleteTextView mPatientIdView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private RadioGroup radioGroup;
    private RadioButton radioButtonSelf, radioButtonFamily;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mPatientIdView = (AutoCompleteTextView) findViewById(R.id.patientId);


        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mPatientIdSignInButton = (Button) findViewById(R.id.patientId_sign_in_button);
        mPatientIdSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(LoginActivity.this, DetailsDisplay.class);
//                startActivity(intent);
//                finish();
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroupFamilyOrSelf);
        radioButtonSelf = (RadioButton)findViewById(R.id.radioButtonSelf);
        radioButtonFamily = (RadioButton)findViewById(R.id.radioButtonFamily);
    }

/*    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }*/






    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid PatientId, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mPatientIdView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String patientId = mPatientIdView.getText().toString();
        String password = mPasswordView.getText().toString();
        String choice = "";

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid patientId address.
        if (TextUtils.isEmpty(patientId)) {
            mPatientIdView.setError(getString(R.string.error_field_required));
            focusView = mPatientIdView;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //if one of the checkbox is checked
            int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
            if (checkedRadioButtonId == -1)
            {

                Toast toast = Toast.makeText(this, "Please choose self/family", Toast.LENGTH_SHORT);
                toast.show();
                toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
            }else {
                if(checkedRadioButtonId == R.id.radioButtonFamily){
                    choice = radioButtonFamily.getText().toString();
                }else if(checkedRadioButtonId == R.id.radioButtonSelf){
                    choice = radioButtonSelf.getText().toString();
                }
                Log.d("choice", choice);
                showProgress(true);
                mAuthTask = new UserLoginTask(patientId, password, choice);
                mAuthTask.execute((Void) null);
            }
        }
    }



    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only patientId addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary patientId addresses first. Note that there won't be
                // a primary patientId address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> patientIds = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            patientIds.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(patientIds);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> patientIdAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, patientIdAddressCollection);

        mPatientIdView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mPatientId;
        private final String mPassword;
        private final String mChoice;

        UserLoginTask(String patientId, String password, String choice) {
            mPatientId = patientId;
            mPassword = password;
            mChoice = choice;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String response = HttpHandlerClass.getHttpResponseFor(UtilsClass.GET_REQ_LOGIN_URL + mPatientId + "/" + mPassword);
            Boolean parsedResponse = getParsedResponse(response);



            // TODO: register the new account here.

            return parsedResponse;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                //todo send choice too
                checkAlarmSetStatusAndUpdateDb(mPatientId,mPassword, mChoice);
                goToDetailsDisplayActivity(mPatientId);

            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private void checkAlarmSetStatusAndUpdateDb(String patientId, String password, String choice) {
        CredentialsDbHelper db = new CredentialsDbHelper(LoginActivity.this);
        int x = db.isRecordSameAndAlarmSet(patientId, password, choice);
        Log.d("isRecordSameAndAlarmSet", x+"");
        switch (x){
            case -2:    initiateAlarm(patientId, password, choice);
                        db.addCredentials(new CredentialInfo(patientId, password,1, choice,""));
                        break;
            case -1:    db.deleteCredentials(CredentialsDbHelper.TABLE_CREDENTIALS);
                        initiateAlarm(patientId, password, choice);
                        db.addCredentials(new CredentialInfo(patientId, password,1, choice, ""));
                        break;
            case 1:     Log.d("isRecordSameAndAlarmSet", 1+" yes");
        }
    }

    private void initiateAlarm(String mPatientId, String mPassword, String choice) {

        Log.d("initiateAlarm Entering", "");


       if(choice.equals(UtilsClass.CHOICE_SELF)){
            Long alarmTimeMilli1 = getAlarrmTimeMilliFor(UtilsClass.MORN_START_PERIOD);
            Log.d("", alarmTimeMilli1+"");
            setAlarm(alarmTimeMilli1, UtilsClass.PURPOSE_TIME_BASED_REMINDER);

            Long alarmTimeMilli2 = getAlarrmTimeMilliFor(UtilsClass.AFT_START_PERIOD);
            Log.d("TIME_BASED_REMINDER2", alarmTimeMilli2+"");
            setAlarm(alarmTimeMilli2, UtilsClass.PURPOSE_TIME_BASED_REMINDER);

            Long alarmTimeMilli3 = getAlarrmTimeMilliFor(UtilsClass.NIGHT_START_PERIOD);
            Log.d("TIME_BASED_REMINDER3", alarmTimeMilli3+"");
            setAlarm(alarmTimeMilli3, UtilsClass.PURPOSE_TIME_BASED_REMINDER);

            Long alarmTimeMilli = getAlarrmTimeMilliFor(UtilsClass.REFILL_CHECK_TIME);
            Log.d("REFILL_REMINDER", alarmTimeMilli+"");
            setAlarm(alarmTimeMilli, UtilsClass.PURPOSE_REFILL_REMINDER);

        }else if(choice.equals(UtilsClass.CHOICE_FAMILY)){
            Long alarmTimeMilli4 = getAlarrmTimeMilliFor(UtilsClass.MORN_END_PERIOD);
            Log.d("PILL_MISSED 1", alarmTimeMilli4+"");
            setAlarm(alarmTimeMilli4, UtilsClass.PURPOSE_PILL_MISSED_FAMILY_NOTI);

            Long alarmTimeMilli5 = getAlarrmTimeMilliFor(UtilsClass.AFT_END_PERIOD);
            Log.d("PILL_MISSED 2", alarmTimeMilli5+"");
            setAlarm(alarmTimeMilli5, UtilsClass.PURPOSE_PILL_MISSED_FAMILY_NOTI);

            Long alarmTimeMilli6 = getAlarrmTimeMilliFor(UtilsClass.NIGHT_END_PERIOD);
            Log.d("PILL_MISSED 3", alarmTimeMilli6+"");
            setAlarm(alarmTimeMilli6, UtilsClass.PURPOSE_PILL_MISSED_FAMILY_NOTI);
        }


    }

    private void setAlarm(Long alarmTimeMilli, String purpose) {

        Random r = new Random();
        int x = r.nextInt();
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(UtilsClass.PURPOSE, purpose);

        PendingIntent alarmIntent;
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        /*alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTimeMilli, PendingIntent.getBroadcast(
                this, x, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT |
                        Intent.FILL_IN_DATA));*/
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, alarmTimeMilli,
                AlarmManager.INTERVAL_DAY, PendingIntent.getBroadcast(
                        this, x, intent, PendingIntent.FLAG_UPDATE_CURRENT |
                                Intent.FILL_IN_DATA));

        x++;
        String message = "Reminder Set!";
        Log.d("rem set", message);
    }

    private Long getAlarrmTimeMilliFor(int ipHr24format) {

        Calendar curr = Calendar.getInstance();
        Long currentTimeMilli = curr.getTimeInMillis();
        int currentHour = curr.get(Calendar.HOUR_OF_DAY);
        Log.d("current hour", currentHour+"");

        Calendar alarmCalendar = Calendar.getInstance();

        if(currentHour >= ipHr24format){
            //set calendar for next day
            alarmCalendar.add(Calendar.DAY_OF_YEAR, 1);

            Log.d("month",Calendar.MONTH+"");
            /*if(curr.get(Calendar.MONTH) == 11 && curr.get(Calendar.DAY_OF_MONTH) == 31){
                alarm.set(Calendar.YEAR, +1);
            }*/
        }
            //set calendar for today 9 am
        alarmCalendar.set(Calendar.HOUR_OF_DAY, ipHr24format);
        alarmCalendar.set(Calendar.MINUTE, 0);
        alarmCalendar.set(Calendar.SECOND, 0);

        return alarmCalendar.getTimeInMillis();
        //return (curr.getTimeInMillis() + 5000L);


    }

    private class FetchPrescriptionTask extends AsyncTask<Void, Void, String> {

        private final String mURL;
        private final String mPatientId;

        private String mPatientName;
        FetchPrescriptionTask(String url, String patientId){
            mURL = url;
            mPatientId = patientId;
        }
        protected String doInBackground(Void... params) {

            String response = HttpHandlerClass.getHttpResponseFor(mURL);
            String parsedResponse = getParsedStringResponse(response);
            mPatientName = getNameFromResponse(response);
            return parsedResponse;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(final String result) {
            //showDialog("Downloaded " + result + " bytes");
            mPrescriptionTask = null;

            updateDBWithPatientName(mPatientName, mPatientId);
            Intent intent = new Intent(LoginActivity.this, DetailsDisplay.class);
            intent.putExtra(UtilsClass.INTENT_EXTRAS_MEDICINES, result);
            intent.putExtra(UtilsClass.PURPOSE, UtilsClass.PURPOSE_JUST_LOGIN);
            startActivity(intent);
            finish();
        }

        @Override
        protected void onCancelled() {
            mPrescriptionTask = null;
        }
    }

    private void updateDBWithPatientName(String patientName, String patientId) {
        CredentialsDbHelper db = new CredentialsDbHelper(this);
        db.updateDBWithPatientName(patientId, patientName);
        //db.addCredentials(new CredentialInfo(patientId, password,1, choice,""));

    }

    private void goToDetailsDisplayActivity(String patientId) {
        mPrescriptionTask = new FetchPrescriptionTask(UtilsClass.GET_REQ_FULL_DAY_PRESCRIPTIONS
                +patientId, patientId);
        mPrescriptionTask.execute((Void) null);
    }

    private String getParsedStringResponse(String response) {
        if (response.isEmpty() || response.equals("-1") ) return "";
        String[] s1 = response.split("\\{");
        String[] s2 = s1[1].split("\\}");
        String[] presc = s2[0].split("\"");
        StringBuilder res = new StringBuilder();
        for (int i = 2; i < presc.length; ++i) {
            if (!presc[i].trim().isEmpty()) {
                res.append(presc[i] + ", ");
            }
        }

        return res.toString();
    }

    private String getNameFromResponse(String response) {
        if (response.isEmpty() || response.equals("-1")) return "";
        String[] s1 = response.split("\\{");
        String[] s2 = s1[1].split("\\}");
        String[] presc = s2[0].split("\"");


        return presc[1];
    }

    private Boolean getParsedResponse(String response) {
        if (response.isEmpty()) return false;
        String[] s1 = response.split("\\{");
        String[] s2 = s1[1].split("\\}");
        Log.d(" resp ",response);
        for(int i = 0; i< s1.length; i++){
            Log.d(" - "+i, s1[i]);
        }
        Log.d("s2[2]= ",s2[0]);
        if(s2[0].equals("1")){
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.uninstall) {
            Uri packageURI = Uri.parse("package:lakshmishankarrao.smartpillcabinet");
            Intent deleteIntent = new Intent(Intent.ACTION_DELETE, packageURI);
            startActivity(deleteIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

