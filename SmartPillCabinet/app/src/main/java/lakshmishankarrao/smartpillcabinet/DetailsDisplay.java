package lakshmishankarrao.smartpillcabinet;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lakshmi on 3/2/2017.
 */

public class DetailsDisplay extends ActionBarActivity {

    Button saveme;
    TextView textViewHeading, textViewPatientName, textViewHeadingPatientName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_display);

        setTitle("Prescription Details");

        textViewHeadingPatientName = (TextView)findViewById(R.id.textViewHeadingPatientName);
        textViewPatientName = (TextView)findViewById(R.id.textViewPatientName);
        updateNameBlock(textViewPatientName, textViewHeadingPatientName);

        textViewHeading = (TextView)findViewById(R.id.textViewHeading);
        String  purpose = getIntent().getStringExtra(UtilsClass.PURPOSE);
        switch(purpose){
            case UtilsClass.PURPOSE_TIME_BASED_REMINDER:
                textViewHeading.setText("Below pill/s are due now!");
                break;
            case UtilsClass.PURPOSE_REFILL_REMINDER:
                textViewHeading.setText("The following pill/s need a refill");

                break;
            case UtilsClass.PURPOSE_PILL_MISSED_FAMILY_NOTI:
                textViewHeading.setText("Below pill/s not taken");
                break;
            case UtilsClass.PURPOSE_JUST_LOGIN:
                textViewHeading.setText("Pills for the Day");
                break;
            default:
                textViewHeading.setText("Upcoming Pills");
        }

        String  medicine_msg_ip = getIntent().getStringExtra(UtilsClass.INTENT_EXTRAS_MEDICINES);
        String[] medicine_msgs = getMsgToDisplay(medicine_msg_ip);

        final List<Prescription> medicines = new ArrayList<>();

        if(medicine_msgs == null){
            medicines.add(new Prescription("No medicines to show"));
        }else {
            for (int i = 0; i < medicine_msgs.length; i++) {
                if(!medicine_msgs[i].trim().isEmpty()) {
                    medicines.add(new Prescription(medicine_msgs[i].toUpperCase()));
                }
            }
        }


        ListView listview = (ListView) findViewById(R.id.listViewPrescriptionList);

        listview.setAdapter(new PrescriptionAdapter(this, R.layout.prescription_custom_row, medicines));

//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//
//        StrictMode.setThreadPolicy(policy);


    }

    private void updateNameBlock(TextView textViewPatientName, TextView textViewHeadingPatientName) {
        CredentialsDbHelper dbHelper = new CredentialsDbHelper(this);
        CredentialInfo info = dbHelper.getCredentialsInfo();
        if(info == null){
            return;
        }else{
            if(info.patient_name == null || info.patient_name.isEmpty()){
                if(info.patient_id == null || info.patient_id.isEmpty()){
                    return;
                }else{
                    textViewHeadingPatientName.setText("Patient ID: ");
                    textViewPatientName.setText(info.patient_id);
                }
            }else{
                textViewHeadingPatientName.setText("Patient Name: ");
                textViewPatientName.setText(info.patient_name);
            }
        }
    }

    private String[] getMsgToDisplay(String medicine_msg_ip) {
        if(medicine_msg_ip == null || medicine_msg_ip.isEmpty() || medicine_msg_ip.equals("")){
            return null;
        }
        String[] x = medicine_msg_ip.split(",");
        return x;
    }

    private String getHttpResponseFor(String s) {

        HttpURLConnection urlConnection = null;
        try {
            Log.d("URL ","setting");
            URL url = new URL("http", "54.153.76.72", 8080, "/info.mourya.goiot/espTrigger/1000/10/m");
            Log.d("sent url", url.toString());
            Log.d("URL ","sending");
            urlConnection = (HttpURLConnection) url.openConnection();
            Log.d("input ","read");
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            StringBuilder res = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                res.append(line);
            }
            Log.d("done reading",res.toString());
            return res.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return null;

    }


}
