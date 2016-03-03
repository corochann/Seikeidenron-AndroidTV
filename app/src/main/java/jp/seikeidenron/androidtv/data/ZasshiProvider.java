package jp.seikeidenron.androidtv.data;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.ArrayList;

import jp.seikeidenron.androidtv.model.Zasshi;

/**
 * Data manager for seikeidenron denshizasshi
 */
public class ZasshiProvider {

    /* Constant */
    private static final String TAG = ZasshiProvider.class.getSimpleName();

    public  static final String ZASSHI_LIST_JSON_URL = "http://seikeidenron.jp/androidtv/data/seikeidenron_androidtv_contents.json";

    private static String TAG_ZASSHI_LIST  = "seikeidenron_zasshi_list";
    /* Inside zasshi_list JSONArray */
    private static String TAG_ZASSHI_ID = "id";                             // id          Currently not used.
    private static String TAG_ZASSHI_TITLE = "title";                       // title
    private static String TAG_ZASSHI_SUBTITLE = "subtitle";                 // for studio. Currently not used.
    private static String TAG_ZASSHI_DESCRIPTIOIN = "description";          // description of this zasshi. Currently not used.
    private static String TAG_ZASSHI_MAIN_CONTENT_URL = "main_content_url"; // URL for main zasshi contents
    private static String TAG_ZASSHI_THUMBNAIL_URL = "thumbnail_url";       // URL for thumbnail image
    private static String TAG_ZASSHI_BACKGROUND_URL = "background_url";     // URL for background image

    /* Attribute */
    private static ArrayList<Zasshi> sZasshiList;

    /* public method */
    public static ArrayList<Zasshi> createZasshiList(Context context) throws JSONException {
        return createZasshiList(context, ZASSHI_LIST_JSON_URL);
    }

    public static ArrayList<Zasshi> createZasshiList(Context context, String url) throws JSONException {
        if (null != sZasshiList) {
            return sZasshiList;
        }

        Log.v(TAG, "createZasshiList, URL = " + url);
        sZasshiList = new ArrayList<>();

        JSONObject jsonObj = parseUrl(url);
        //JSONObject jsonObj = parseFile(context, "seikeidenron_androidtv_contents.json");

        if (null == jsonObj) {
            Log.e(TAG, "An error occurred parseUrl for json file.");
            return sZasshiList;
        }

        JSONArray zasshiListArray = jsonObj.getJSONArray(TAG_ZASSHI_LIST);

        if (null != zasshiListArray) {
            final int zasshiLength = zasshiListArray.length();
            Log.d(TAG, "ZasshiList #: " + zasshiLength);
            long id = -1;
            String title = "";
            String subtitle = "";
            String description = "";
            String zasshiUrl = "";
            String thumbImageUrl = "";
            String bgImageUrl = "";

            String studio;
            for (int index = 0; index < zasshiLength; index++) {
                Log.v(TAG, "Parsing zasshi " + index);
                JSONObject zasshiJSONObj = zasshiListArray.getJSONObject(index);
                if (zasshiJSONObj.has(TAG_ZASSHI_ID)) {
                    id = zasshiJSONObj.getLong(TAG_ZASSHI_ID);
                } else {
                    Log.w(TAG, "zasshi id not found");
                }

                title = checkGetString(zasshiJSONObj, TAG_ZASSHI_TITLE);
                subtitle = checkGetString(zasshiJSONObj, TAG_ZASSHI_SUBTITLE);
                description = checkGetString(zasshiJSONObj, TAG_ZASSHI_DESCRIPTIOIN);
                zasshiUrl = checkGetString(zasshiJSONObj, TAG_ZASSHI_MAIN_CONTENT_URL);
                thumbImageUrl = checkGetString(zasshiJSONObj, TAG_ZASSHI_THUMBNAIL_URL);
                bgImageUrl = checkGetString(zasshiJSONObj, TAG_ZASSHI_BACKGROUND_URL);

                Zasshi zasshi = new Zasshi(id, title, subtitle, description, bgImageUrl, thumbImageUrl, zasshiUrl);
                sZasshiList.add(zasshi);
            }
        }
        return sZasshiList;
    }

    private static String checkGetString(JSONObject jsonObject, String key) throws JSONException {
        String result = "";
        if(jsonObject.has(key)) {
            result = jsonObject.getString(key);
            Log.v(TAG, key + ": " + result);
        } else {
            Log.w(TAG, "key " + key + " not found.");
        }
        return result;
    }

    protected static JSONObject parseUrl(String urlString) {
        Log.v(TAG, "Parse URL: " + urlString);
        BufferedReader reader = null;

        try {
            java.net.URL url = new java.net.URL(urlString);
            URLConnection urlConnection = url.openConnection();
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            //urlConnection.getInputStream(), "iso-8859-1"));
                    //urlConnection.getInputStream(), "utf-8"));
            return convertBufferedReaderToJSONObject(reader);
        } catch (Exception e) {
            Log.d(TAG, "Failed to parse the json for media list", e);
            return null;
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.d(TAG, "JSON feed closed", e);
                }
            }
        }
    }

    protected static JSONObject parseFile(Context context, String filePath) {
        Log.d(TAG, "Parse File: " + filePath);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(filePath)));

            return convertBufferedReaderToJSONObject(reader);
        } catch (Exception e) {
            Log.d(TAG, "Failed to parse the json for media list", e);
            return null;
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.d(TAG, "JSON feed closed", e);
                }
            }
        }
    }

    public static JSONObject convertBufferedReaderToJSONObject(BufferedReader reader)
            throws IOException, JSONException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String json = sb.toString();
        return new JSONObject(json);
    }
}