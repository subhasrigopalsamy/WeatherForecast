package com.kulu.weatherforecast;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    TextView mLocation;
    ImageView mDescImage;
    TextView mDesc;
    TextView mTemp;
    TextView mTempUnit;
    TextView mCurrentTime;
    TextView mCurrentWind;
    TextView mCurrentClouds;
    TextView mCurrentRain;
    TextView mCurrentHumidity;
    TextView mCurrentPressure;
    LinearLayout mUpcomingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorStatusbar));

        mUpcomingView =(LinearLayout) findViewById(R.id.futureForecast);
        mLocation =(TextView) findViewById(R.id.location);
        mDescImage = (ImageView) findViewById(R.id.descImage);
        mDesc =(TextView) findViewById(R.id.desc);
        mTemp =(TextView) findViewById(R.id.temp);
        mTempUnit=(TextView) findViewById(R.id.tempUnit);
        mCurrentTime=(TextView)findViewById(R.id.time);
        mCurrentWind=(TextView)findViewById(R.id.wind);
        mCurrentClouds=(TextView)findViewById(R.id.clouds);
        mCurrentRain=(TextView)findViewById(R.id.rain);
        mCurrentPressure=(TextView)findViewById(R.id.pressure);
        mCurrentHumidity=(TextView)findViewById(R.id.humidity);

        new RetrieveFeedTask().execute();
    }

    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {

        }

        protected String doInBackground(Void... urls) {

            try {
                String urlString = "http://api.openweathermap.org/data/2.5/forecast?q=cambridge,gb&units=imperial&appid=b712c2f585c93e6c3d9e7c57ab4e1803";
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if (response == null) {
                response = "THERE WAS AN ERROR";
                Toast.makeText(getApplicationContext(),
                        "Connectivity Error ",
                        Toast.LENGTH_LONG)
                        .show();
                return;
            }

            try {
                mLocation.setText("Cambridge");
                JSONObject jsonobj = new JSONObject(response);
                JSONArray list = jsonobj.getJSONArray("list");

                //current details
                JSONObject mainTemp = list.getJSONObject(0);
                mTemp.setText(String.valueOf(mainTemp.getJSONObject("main").getInt("temp")));
                mTempUnit.setText("\u2109");

                JSONArray weatherList = mainTemp.getJSONArray("weather");
                mDescImage.setImageDrawable(getDrawable(getImageDrawableName(weatherList.getJSONObject(0).getString("icon"))));
                mDesc.setText(weatherList.getJSONObject(0).getString("main")+", "+weatherList.getJSONObject(0).getString("description"));

                //clouds %
                if(mainTemp.getJSONObject("clouds").has("all") && mainTemp.getJSONObject("clouds").getInt("all")>0) {
                    mCurrentClouds.setText(mainTemp.getJSONObject("clouds").getInt("all") + "%");
                    mCurrentClouds.setVisibility(View.VISIBLE);
                }else
                    mCurrentClouds.setVisibility(View.GONE);

                //wind speed in mph
                if(mainTemp.getJSONObject("wind").has("speed") && mainTemp.getJSONObject("wind").getInt("speed")>0) {
                    mCurrentWind.setText(mainTemp.getJSONObject("wind").getInt("speed") + " mph");
                    mCurrentWind.setVisibility(View.VISIBLE);
                }else
                    mCurrentWind.setVisibility(View.GONE);

                //rain in mm
                if(mainTemp.getJSONObject("rain").has("3h")) {
                    mCurrentRain.setText(mainTemp.getJSONObject("rain").getDouble("3h")+" mm");
                    mCurrentRain.setVisibility(View.VISIBLE);
                }else {
                    mCurrentRain.setVisibility(View.GONE);
                }

                //humidity %
                if(mainTemp.getJSONObject("main").has("humidity")){
                    mCurrentHumidity.setText(mainTemp.getJSONObject("main").getInt("humidity")+"%");
                    mCurrentHumidity.setVisibility(View.VISIBLE);
                }else
                    mCurrentHumidity.setVisibility(View.GONE);

                //pressure hPa
                if(mainTemp.getJSONObject("main").has("pressure")){
                    mCurrentPressure.setText(mainTemp.getJSONObject("main").getInt("pressure")+" hPa");
                    mCurrentPressure.setVisibility(View.VISIBLE);
                }else
                    mCurrentPressure.setVisibility(View.GONE);

                //next day forecasts
                mUpcomingView.removeAllViews();
                loadFutureForecasts(list);

            } catch (final JSONException e) {
                Log.e("Error", "Json parsing error: " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Json parsing error: " + e.getMessage(),
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });


            }
        }

        private void loadFutureForecasts(JSONArray list) {
            if(list.length()<=1)
                return;

            //time and date
            Calendar cal = Calendar.getInstance();
            TimeZone tz = TimeZone.getTimeZone("UTC");

            SimpleDateFormat formatter = new SimpleDateFormat("EEE",Locale.getDefault());
            formatter.setTimeZone(tz);

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            timeFormat.setTimeZone(tz);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
            Date today = new Date();

            for(int i=1;i<list.length();i++){
            View v =  LayoutInflater.from(getBaseContext()).inflate(R.layout.listitem_nextdays,mUpcomingView,false);
            TextView mDayName = (TextView)v.findViewById(R.id.dayName);
            TextView mTimeName =(TextView)v.findViewById(R.id.timeName);
            ImageView mTempIcon = (ImageView)v.findViewById(R.id.tempIcon);
            TextView mMaxTemp = (TextView)v.findViewById(R.id.maxTemp);
            TextView mMinTemp = (TextView)v.findViewById(R.id.minTemp);

                try {
                    Date d1 = new Date(list.getJSONObject(i).getLong("dt")*1000L);
                    if(dateFormat.format(d1).equals(dateFormat.format(today))) {
                        mDayName.setText("Today");
                    }
                    else{
                        mDayName.setText(formatter.format(d1));
                    }
                        mTimeName.setText(timeFormat.format(d1));
                        JSONObject mainTemp = list.getJSONObject(i);
                        mMaxTemp.setText(String.valueOf(mainTemp.getJSONObject("main").getInt("temp_max")));
                        mMinTemp.setText(String.valueOf(mainTemp.getJSONObject("main").getInt("temp_min")));

                    //icons
                    JSONArray weatherList = mainTemp.getJSONArray("weather");
                    int drawableId =  getImageDrawableName(weatherList.getJSONObject(0).getString("icon"));
                    mTempIcon.setImageDrawable(getDrawable(drawableId));


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mUpcomingView.addView(v);
            }
        }

        //get local drawable id for the API icon
        private int getImageDrawableName(String icon) {
            switch (icon){
                case "01d":
                    return R.drawable.ic_01d;
                case "01n":
                    return R.drawable.ic_01n;
                case "02d":
                    return R.drawable.ic_02d;
                case "02n":
                    return R.drawable.ic_02n;
                case "03d":
                case "03n":
                    return R.drawable.ic_03d;
                case "04d":
                case "04n":
                    return R.drawable.ic_04d;
                case "09d":
                case "09n":
                    return R.drawable.ic_09d;
                case "10d":
                    return R.drawable.ic_10d;
                case "10n":
                    return R.drawable.ic_10n;
                case "11d":
                case "11n":
                    return R.drawable.ic_11d;
                case "13d":
                case "13n":
                    return R.drawable.ic_13d;
                case "50d":
                case "50n":
                    return R.drawable.ic_50d;

            }
            return R.drawable.ic_cloud;
        }
    }
}
