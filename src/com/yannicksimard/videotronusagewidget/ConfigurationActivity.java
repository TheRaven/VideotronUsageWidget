package com.yannicksimard.videotronusagewidget;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ConfigurationActivity extends Activity {

  private EditText userKeyTextField;
  private int usageWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
  public static final String PREFS_NAME = "com.yannicksimard.videotronusagewidget.configuration";
  private static final String PREF_USER_KEY = "userKey_";
  private AlertDialog alert;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_configuration);
    setResult(RESULT_CANCELED);
    userKeyTextField = (EditText) findViewById(R.id.userKeyField);

    findViewById(R.id.save_button).setOnClickListener(saveBtnClickListener);

    findViewById(R.id.cancel_button).setOnClickListener(cancelBtnClickListener);

    buildAlertDialog();

    Intent intent = getIntent();
    Bundle extras = intent.getExtras();
    if (extras != null) {
      usageWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }
    String userKey = ConfigurationActivity.loadUserKeyPreference(this, usageWidgetId);
    if (userKey != null) {
      userKeyTextField.setText(userKey);
    }
    // If they gave us an intent without the widget id, just bail.
    if (usageWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
      finish();
    }

  }

  private void buildAlertDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Configuration error!").setMessage("The userKey must be specified").setCancelable(false).setNeutralButton("Ok", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int id) {
        dialog.dismiss();
      }
    });
    alert = builder.create();
  }

  View.OnClickListener saveBtnClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      final Context context = ConfigurationActivity.this;

      // When the button is clicked, save the string in our prefs and return
      // that they
      // clicked OK.
      String userKey = userKeyTextField.getText().toString();
      if (userKey != null && !userKey.equals("")) {

        saveUserKeyPreference(context, usageWidgetId, userKey);
        VideotronUsageWidgetProvider.updateWidget(context, usageWidgetId, userKey);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, usageWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
      } else {
        alert.show();
      }
    }
  };

  View.OnClickListener cancelBtnClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      setResult(RESULT_CANCELED);
      finish();
    }
  };

  static void saveUserKeyPreference(Context context, int appWidgetId, String text) {
    SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
    prefs.putString(PREF_USER_KEY + appWidgetId, text);
    prefs.commit();
  }

  static String loadUserKeyPreference(Context context, int appWidgetId) {
    SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
    String userKey = prefs.getString(PREF_USER_KEY + appWidgetId, null);

    return userKey;
  }

}
