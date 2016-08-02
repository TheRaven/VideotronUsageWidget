package com.yannicksimard.videotronusagewidget.usage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class UsageFetcher extends AsyncTask<String, Void, Usage> {

  private static final String ACCOUNTS_KEY = "internetAccounts";
  private static final String COMBINED_PERCENT_KEY = "combinedPercent";
  private static final String MAX_COMBINED_BYTES_KEY = "maxCombinedBytes";
  private static final String DAYS_TO_END_KEY = "daysToEnd";
  private static final String DAYS_FROM_START_KEY = "daysFromStart";
  private static final String DOWNLOADED_BYTES_KEY = "downloadedBytes";
  private static final String UPLOADED_BYTES_KEY = "uploadedBytes";

  private static final String API_URL = "https://www.videotron.com/api/1.0/internet/usage/wired/%USER_KEY%.json?lang=en&caller=videotron-mac.pommepause.com";

  protected Context context;

  private final String userKey;

  public UsageFetcher(Context context, String userKey) {
    this.context = context;

    this.userKey = userKey;

  }

  @Override
  public void onPreExecute() {

  }

  public boolean isOnline() {
    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    if (netInfo != null && netInfo.isConnected()) {
      return true;
    }
    return false;
  }

  @Override
  protected Usage doInBackground(String... screens) {
    Usage usage = new Usage(null, 0L, 0L, 0L, 0L, 0L);

    if (userKey == null || userKey.equals("")) {
      return usage;
    }

    try {
      if (isOnline()) {

        String json = readUrl(API_URL.replaceAll("%USER_KEY%", userKey));

        JSONObject obj = (JSONObject) JSONValue.parse(json);

        if (obj != null) {
          if (obj.containsKey(DAYS_TO_END_KEY)) {
            usage.setDaysToEnd((Long) obj.get(DAYS_TO_END_KEY));
          }
          if (obj.containsKey(DAYS_FROM_START_KEY)) {
            usage.setDaysFromStart((Long) obj.get(DAYS_FROM_START_KEY));
          }

          if (obj.containsKey(ACCOUNTS_KEY)) {
            JSONArray accounts = (JSONArray) obj.get(ACCOUNTS_KEY);

            if (accounts.size() > 0) {
              JSONObject firstAccount = (JSONObject) accounts.get(0);
              if (firstAccount != null) {

                if (firstAccount.containsKey(DOWNLOADED_BYTES_KEY)) {
                  usage.setDownloadedBytes((Long) firstAccount.get(DOWNLOADED_BYTES_KEY));
                }
                if (firstAccount.containsKey(UPLOADED_BYTES_KEY)) {
                  usage.setUploadedBytes((Long) firstAccount.get(UPLOADED_BYTES_KEY));
                }
                if (firstAccount.containsKey(COMBINED_PERCENT_KEY)) {
                  usage.setUsage((String) firstAccount.get(COMBINED_PERCENT_KEY));
                }
                if (firstAccount.containsKey(MAX_COMBINED_BYTES_KEY)) {
                  usage.setMaximumUsage((Long) firstAccount.get(MAX_COMBINED_BYTES_KEY));
                }
              }
            }
          }
        }
      }
    } catch (Exception e) {
      Log.e("VideotronUsage", e.getMessage(), e);
    }

    return usage;
  }

  private String readUrl(String urlString) throws Exception {
    BufferedReader reader = null;
    try {
      URL url = new URL(urlString);
      reader = new BufferedReader(new InputStreamReader(url.openStream()));
      StringBuffer buffer = new StringBuffer();
      int read;
      char[] chars = new char[1024];
      while ((read = reader.read(chars)) != -1)
        buffer.append(chars, 0, read);

      return buffer.toString();
    } finally {
      if (reader != null)
        reader.close();
    }
  }
}