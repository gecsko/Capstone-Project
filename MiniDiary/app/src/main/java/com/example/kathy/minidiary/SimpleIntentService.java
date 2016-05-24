package com.example.kathy.minidiary;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Debug;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SimpleIntentService extends IntentService {
    public static final String PARAM_IN_LAT_MSG = "ilatmsg";
    public static final String PARAM_IN_LON_MSG = "ilonmsg";
    public static final String PARAM_OUT_MSG = "omsg";

    public SimpleIntentService() {
        super("SimpleIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long latmsg = intent.getLongExtra(PARAM_IN_LAT_MSG, 0);
        long lonmsg = intent.getLongExtra(PARAM_IN_LON_MSG, 0);

        Log.d("HandleIntent", Long.toString(latmsg));
        Log.d("HandleIntent", Long.toString(lonmsg));
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String weatherJsonStr = null;

        String format = "json";
        //String units = "metric";
        //int numDays = 14;

        String weather;
        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            final String FORECAST_BASE_URL =
                    "http://api.openweathermap.org/data/2.5/weather?";
            final String LAT_PARAM = "lat";
            final String LON_PARAM = "lon";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(LAT_PARAM, Long.toString(latmsg))
                    .appendQueryParameter(LON_PARAM, Long.toString(lonmsg))
                    .appendQueryParameter("APPID", "bde61e667c6dd574c0508f9e768badbe")
                    .build();

            URL url = new URL(builtUri.toString());

            Log.d("tag", builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            //InputStream inputStream = urlConnection.getInputStream();
            InputStream inputStream = null;
            int status = urlConnection.getResponseCode();

            if(status >= HttpURLConnection.HTTP_BAD_REQUEST) {
                inputStream = urlConnection.getErrorStream();
            } else {
                inputStream = urlConnection.getInputStream();
            }

            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) {

                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            weatherJsonStr = buffer.toString();
            weather = getWeatherDataFromJson(weatherJsonStr);

            // processing done here….
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(AddDiaryFragment.ACTION_RESP);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(PARAM_OUT_MSG, weather);
            sendBroadcast(broadcastIntent);

        } catch (IOException e) {
            // processing done here….
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(AddDiaryFragment.ACTION_RESP);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(PARAM_OUT_MSG, getString(R.string.no_network));
            sendBroadcast(broadcastIntent);

            Log.e("Get Weather", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            Log.e("Get Weather", e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("getWeather", "Error closing stream", e);
                }
            }
        }


    }

    private String getWeatherDataFromJson(String forecastJsonStr) throws JSONException
    {
        final String OWM_WEATHER = "weather";
        final String OWM_DESCRIPTION = "main";
        String weather = null;

        try {
            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherObjectArray = forecastJson.getJSONArray(OWM_WEATHER);
            JSONObject weatherObject = weatherObjectArray.optJSONObject(0);
            weather = weatherObject.getString(OWM_DESCRIPTION);

        } catch (JSONException e) {
            Log.e("Get Weather", e.getMessage(), e);
            e.printStackTrace();
        }

        return weather;
    }
}
