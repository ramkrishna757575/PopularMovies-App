package com.ramkrishna.android.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ramkr on 17-Apr-16.
 * <p>
 * Manages the Network Operations like fetching raw JSON data.
 */
public class NetworkDataManager{
    private String url;
    private URL _url;
    private Context context;
    private boolean isNetworkInitComplete;
    private String rawData;
    private Toast toast;

    //Constructor to initialize variables
    NetworkDataManager(Context context, String apiUri)
    {
        this.context = context;
        isNetworkInitComplete = false;
        rawData = null;
        url = apiUri;
        _url = null;
        initNetwork();
    }

    //Initializes the Newtwork and returns the status of network initialization
    private boolean initNetwork()
    {
        if (checkNetwork())
        {
            if (constructURL())
            {
                isNetworkInitComplete = true;
                return true;
            }
        }
        return false;
    }

    //Checks if Network is available or not
    private boolean checkNetwork()
    {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
        {
            return true;
        } else
        {
            toast = Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
    }

    //Forms the URL from the string containing url
    private boolean constructURL()
    {
        try
        {
            _url = new URL(url);
            return true;
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
            toast = Toast.makeText(context, "Incorrect Url", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
    }

    //Fetches string data(JSON) from the API
    public void fetchRawString()
    {
        String webPage = "", data = "";
        if (isNetworkInitComplete)
        {
            try
            {
                HttpURLConnection urlConnection = (HttpURLConnection) _url.openConnection();
                InputStream is = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                while ((data = reader.readLine()) != null)
                {
                    webPage += data + "\n";
                }
                urlConnection.disconnect();
                is.close();
                rawData = webPage;
            } catch (IOException e)
            {
                e.printStackTrace();
                Toast toast = Toast.makeText(context, "Unable to fetch data", Toast.LENGTH_SHORT);
                toast.show();
                rawData = null;
            }
        }
    }

    //Returns the raw string data obtained from the API
    public String getRawData()
    {
        return rawData;
    }
}
