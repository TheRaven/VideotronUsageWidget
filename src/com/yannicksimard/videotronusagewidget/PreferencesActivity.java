package com.yannicksimard.videotronusagewidget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

public class PreferencesActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.preferences);
    getPreferenceManager().setSharedPreferencesName(ConfigurationActivity.PREFS_NAME);
    PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case android.R.id.home:
      NavUtils.navigateUpFromSameTask(this);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
    ComponentName widgetComponent = new ComponentName(this, VideotronUsageWidgetProvider.class);
    int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);
    Intent update = new Intent();
    update.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
    update.setAction(VideotronUsageWidgetProvider.USER_KEY_CHANGED);
    this.sendBroadcast(update);

  }

}
