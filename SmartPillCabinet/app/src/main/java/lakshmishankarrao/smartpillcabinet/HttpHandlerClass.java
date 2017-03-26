package lakshmishankarrao.smartpillcabinet;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by lakshmi on 3/1/2017.
 */

public class HttpHandlerClass {

    public static String getHttpResponseFor(String req) {

        HttpURLConnection urlConnection = null;
        try {
            Log.d("URL ","setting");
            URL url = new URL("http", UtilsClass.SERVER_ADDR, UtilsClass.SERVER_PORT, req);
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
