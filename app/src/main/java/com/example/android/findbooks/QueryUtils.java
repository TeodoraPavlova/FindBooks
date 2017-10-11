package com.example.android.findbooks;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    private static Context mContext;
    private static final String LOG = QueryUtils.class.getName();
    private static Bitmap bitmap=null;
    private static String linkToImage;

    private QueryUtils(Context context){mContext=context;}

    public static List<Books> fetchBooksData(String requestUrl){
        URL url= createUrl(requestUrl);
        String jsonResponse="";
        try {
            jsonResponse = makeHttpRequest(url);
        }catch (IOException e){
            e.printStackTrace();
            Log.e(LOG,"Request error: ",e.getCause());
        }
     List<Books> booksList = extractBooks(jsonResponse);
        return booksList;
    }

    private static URL createUrl(String requestUrl){
        URL url = null;
        try{
            url = new URL(requestUrl);
        }catch (MalformedURLException ex){
            ex.printStackTrace();
            Log.e(LOG,"ERROR creation Url: ",ex.getCause());
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG, "Problem retrieving JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static List<Books> extractBooks(String jsonResponse){
        if(TextUtils.isEmpty(jsonResponse)){
        return null;
    }

    List<Books> books = new ArrayList<Books>();

    try {
        JSONObject baseJsonResponse = new JSONObject(jsonResponse);
        JSONArray items = new JSONArray();

        if(baseJsonResponse.has("items")){
            items = baseJsonResponse.getJSONArray("items");
        }

        for (int i = 0; i < items.length(); i++) {

            JSONObject currentBook = items.getJSONObject(i);

            JSONObject volume = currentBook.getJSONObject("volumeInfo");
            String title = volume.getString("title");

            String author="";
            if(volume.has("authors")) {
                JSONArray authorArray = volume.getJSONArray("authors");
                author = authorArray.toString();
            }else{
                author = "No author found.";
            }

            String url = volume.getString("infoLink");
            if(volume.has("imageLinks")) {
                JSONObject image = volume.getJSONObject("imageLinks");
                linkToImage = image.getString("thumbnail");
                try {
                    URL url1 = new URL(linkToImage);
                    bitmap = BitmapFactory.decodeStream(url1.openConnection().getInputStream());
                } catch (MalformedURLException e) {
                    Log.e(LOG, "Error catching image: ", e.getCause());
                } catch (IOException e) {
                    Log.e(LOG, "Error bit mapping: ", e.getCause());
                }
            }else{
                bitmap = BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.no_cover_found);
            }
            books.add(new Books(title,author,url,bitmap));
        }
        }catch (JSONException e){
        e.printStackTrace();
        Log.e(LOG,"Error parsing: "+e.getMessage());
    }
    return books;
}

}
