package com.example.androidlabs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;
import static org.xmlpull.v1.XmlPullParser.TEXT;

public class WeatherForecast extends AppCompatActivity {
    ProgressBar loader;
    TextView current_temperature, min_temperature, max_temperature, uv_rating;
    ImageView weatherImage;
    String max, min, value, windUV, icon=null;
    String valueUV;
    Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        loader = findViewById(R.id.progressBar);
        weatherImage = (ImageView) findViewById(R.id.imageView);
        current_temperature=(TextView)findViewById(R.id.textView1);
        min_temperature=(TextView)findViewById(R.id.textView2);
        max_temperature=(TextView)findViewById(R.id.textView3);
        uv_rating = (TextView)findViewById(R.id.textView4);



        ForecastQuery newsTask = new ForecastQuery();
        newsTask.execute();
        loader.setVisibility(View.VISIBLE);
    }


    private class ForecastQuery extends AsyncTask<String, Integer, String>{

        @Override                       //Type 1
        protected String doInBackground(String... strings) {
            String ret = null;

            Bitmap bitmap;
            String queryURL = "http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=7e943c97096a9784391a981c4d878b22&mode=xml&units=metric";
            String queryURL2 = "http://api.openweathermap.org/data/2.5/uvi?appid=7e943c97096a9784391a981c4d878b22&lat=45.348945&lon=-75.759389";
            String urlString;

            JSONObject jObject;

            try {
                URL url2 = new URL(queryURL2);
                HttpURLConnection urlConnection2 = (HttpURLConnection) url2.openConnection();
                InputStream inStream2 = urlConnection2.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream2, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                String result = sb.toString();

                jObject = new JSONObject(result);

                valueUV = jObject.getString("value");
                Log.i("UV is:", ""+ valueUV);



                // Connect to the server:
                URL url = new URL(queryURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inStream = urlConnection.getInputStream();

                //Set up the XML parser:
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput( inStream  , "UTF-8");

                //Iterate over the XML tags:
                int EVENT_TYPE;         //While not the end of the document:
                while((EVENT_TYPE = xpp.getEventType()) != XmlPullParser.END_DOCUMENT)
                {
                    switch(EVENT_TYPE)
                    {
                        case START_TAG:         //This is a start tag < ... >
                            String tagName = xpp.getName(); // What kind of tag?
                            if(tagName.equals("temperature"))
                            {
                                max = xpp.getAttributeValue(null, "max"); //What is the String associated with message?
                                publishProgress(25);

                                min = xpp.getAttributeValue(null, "min"); //What is the String associated with message?
                                publishProgress(50);

                                value = xpp.getAttributeValue(null, "value"); //What is the String associated with message?
                                publishProgress(75);
                            }

                            else if(tagName.equals("weather")){
                                 icon = xpp.getAttributeValue(null, "icon"); //What is the String associated with message?
                                 urlString = "http://openweathermap.org/img/w/" + icon + ".png";


                                if (fileExistance(icon + ".png")) {
                                    Log.i( "Looking for file", icon + ".png");
                                    Log.i("Found", "Weather image exists");
                                    FileInputStream fis = null;
                                    try {    fis = openFileInput(icon + ".png");   }
                                    catch (FileNotFoundException e) {    e.printStackTrace();  }
                                    image = BitmapFactory.decodeStream(fis);}
                                else {
                                    image = null;
                                    Log.i( "Looking for file", icon + ".png");
                                    Log.i("Not Found", "Weather image downloading");
                                URL urlnew = new URL(urlString);
                                HttpURLConnection connection = (HttpURLConnection) urlnew.openConnection();
                                connection.connect();
                                int responseCode = connection.getResponseCode();
                                if (responseCode == 200) {
                                    image = BitmapFactory.decodeStream(connection.getInputStream());
                                }

                                FileOutputStream outputStream = openFileOutput( icon + ".png", Context.MODE_PRIVATE);
                                image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                                outputStream.flush();
                                outputStream.close();}

                                publishProgress(100);
                                Thread.sleep(2000);

                            }
                            break;
                        case END_TAG:           //This is an end tag: </ ... >
                            break;
                        case TEXT:              //This is text between tags < ... > Hello world </ ... >
                            break;
                    }
                    xpp.next(); // move the pointer to next XML element
                }



            }
            catch(MalformedURLException mfe){ ret = "Malformed URL exception"; }
            catch(IOException ioe)          { ret = "IO Exception. Is the Wifi connected?";}
            catch(XmlPullParserException pe){ ret = "XML Pull exception. The XML is not properly formed" ;}
            catch (JSONException e) { e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            //What is returned here will be passed as a parameter to onPostExecute:
            return ret;
        }

        @Override                   //Type 3
        protected void onPostExecute(String sentFromDoInBackground) {
            //super.onPostExecute(sentFromDoInBackground);
            //update GUI Stuff:
            current_temperature.setText("Current Temperature: "+value);
            min_temperature.setText("Lowest Temperature: "+min);
            max_temperature.setText("Highest Temperature: "+max);
            uv_rating.setText("UV value: " +valueUV);
            weatherImage.setImageBitmap(image);
            loader.setVisibility(View.INVISIBLE);

        }

        @Override                       //Type 2
        protected void onProgressUpdate(Integer... values) {
            Log.i("AsyncTask", "update:" + values[0]);
            loader.setVisibility(View.VISIBLE);
            loader.setProgress(values[0]);

        }

    }

        public boolean fileExistance(String fname){
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();   }

}
