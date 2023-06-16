package com.example.fetchexercise;

import static java.security.AccessController.getContext;

import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class JsonParsing extends AsyncTask<URL, Void, Void> {
    public static List<String> listOfIds = new ArrayList<String>();
    @Override
    protected Void doInBackground(URL... urls) {
        URL url = null;
        try {
            url = new URL("https://fetch-hiring.s3.amazonaws.com/hiring.json");
            HttpURLConnection httpURLConnection = null;
            httpURLConnection = (HttpURLConnection) url.openConnection();
            //httpURLConnection.connect();
            InputStream inputStream = null;
            inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            String data = "";
            while ((line = bufferedReader.readLine()) != null) {
                data = data + line;
            }
            if (!data.isEmpty()) {
                JsonElement jelement = new JsonParser().parse(data);
                JsonArray jsonArray = jelement.getAsJsonArray();
                JsonArray integerSortedJsonArray = new JsonArray();
                List<JsonObject> jsonValues = new ArrayList<JsonObject>();

                JsonArray fullySortedArray = new JsonArray();
                for (int i = 0; i < jsonArray.size(); i++) {
                    jsonValues.add((JsonObject) jsonArray.get(i));
                }
                Collections.sort(jsonValues, new Comparator<JsonObject>() {
                    private static final String KEY_NAME = "listId";

                    @Override
                    public int compare(JsonObject jsonObjectA, JsonObject jsonObjectB) {
                        int compare = 0;
                        int keyA = Integer.parseInt(String.valueOf(jsonObjectA.get(KEY_NAME)));
                        int keyB = Integer.parseInt(String.valueOf(jsonObjectB.get(KEY_NAME)));
                        compare = Integer.compare(keyA, keyB);
                        return compare;
                    }
                });
                for (int i = 0; i < jsonArray.size(); i++) {
                    integerSortedJsonArray.add((JsonObject) jsonValues.get(i));
                }
                List<JsonObject> jsonValues2 = new ArrayList<JsonObject>();
                for (int i = 0; i < integerSortedJsonArray.size(); i++) {
                    jsonValues2.add((JsonObject) integerSortedJsonArray.get(i));
                }
                Collections.sort(jsonValues2, new Comparator<JsonObject>() {
                    private static final String KEY_NAME = "name";

                    @Override
                    public int compare(JsonObject o1, JsonObject o2) {
                        String valA = new String();
                        String valB = new String();
                        valA = String.valueOf(o1.get(KEY_NAME));
                        valB = String.valueOf(o2.get(KEY_NAME));
                        return valA.compareTo(valB);
                    }
                });

                for (int i = 0; i < jsonArray.size(); i++) {
                    fullySortedArray.add((JsonObject) jsonValues2.get(i));
                }

                for (int i = 0; i < fullySortedArray.size(); i++) {
                    if (!fullySortedArray.get(i).getAsJsonObject().get("name").isJsonNull()) {
                        if (fullySortedArray.get(i).getAsJsonObject().get("name").toString().length() > 2) {
                            listOfIds.add(fullySortedArray.get(i).getAsJsonObject().get("listId").toString());
                        }
                    }
                }


            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    @Override
    protected void onPostExecute(Void aVoid){
        super.onPostExecute(aVoid);
        ArrayAdapter<String> arr = new ArrayAdapter<String>(MainActivity.l.getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, listOfIds);
        MainActivity.l.setAdapter(arr);
    }
}
