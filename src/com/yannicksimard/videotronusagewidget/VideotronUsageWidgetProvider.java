package com.yannicksimard.videotronusagewidget;

/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;

import com.yannicksimard.videotronusagewidget.usage.Usage;
import com.yannicksimard.videotronusagewidget.usage.UsageFetcher;
import com.yannicksimard.videotronusagewidget.usage.WidgetUsageFetcher;

public class VideotronUsageWidgetProvider extends AppWidgetProvider {

  public static String USER_KEY_CHANGED = "userKeyChanged";

  public VideotronUsageWidgetProvider() {

  }

  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equals(USER_KEY_CHANGED)) {
      AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);

      onUpdate(context, widgetManager, (int[]) intent.getExtras().get(AppWidgetManager.EXTRA_APPWIDGET_IDS));
    }
    super.onReceive(context, intent);
  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    final int N = appWidgetIds.length;

    try {

      // fetch data then update all widgets
      // if data cannot be fetch do not update widgets

      for (int i = 0; i < N; i++) {
        int appWidgetId = appWidgetIds[i];
        String userKey = ConfigurationActivity.loadUserKeyPreference(context, appWidgetId);

        if (userKey != null) {
          // TODO: add code to pop activity that display more data about
          // internet connection
          updateWidget(context, appWidgetId, ConfigurationActivity.loadUserKeyPreference(context, appWidgetId));
        }

      }
    } catch (Exception e) {

    }
    super.onUpdate(context, appWidgetManager, appWidgetIds);
  }

  public static void updateWidget(Context context, int appWidgetId, String userKey) {
    UsageFetcher fetcher = new WidgetUsageFetcher(context, appWidgetId, userKey);
    fetcher.execute("VideotronUsage");
  }

  public static void onUsageDataFound(Usage usage, Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

    // TODO: move this code to the manager in a separate method.

    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

    Float combinedUsage = Float.valueOf(usage.getUsage());

    int color = Color.GREEN;

    Long daysLeft = usage.getDaysToEnd() + 1;

    Long periodLength = usage.getDaysFromStart() + usage.getDaysToEnd() + 1;

    // calculate standard average

    Long standardAverage = usage.getMaximumUsage() / periodLength;

    // calculate standard total to this day (standard average * (daysFromStart +
    // 1))
    Long standardTotal = standardAverage * (usage.getDaysFromStart() + 1);

    // calculate current total
    Long currentTotal = usage.getDownloadedBytes() + usage.getUploadedBytes();
    // calculate diff between current total and standard total.
    Long totalsDiff = currentTotal - standardTotal;

    // calculate the diff % of the max total ((diff / max) * 100)
    Double diffPercent = (Double.valueOf(totalsDiff) / Double.valueOf(usage.getMaximumUsage())) * 100.0;

    // calculate current average
    Long currentAverage = currentTotal / (usage.getDaysFromStart() + 1);

    // calculate predicted total (current average * max days (31))
    Long predictedTotal = currentAverage * periodLength;
    /*
     * Log.i("VideotronUsage", "standardTotal: " +
     * humanReadableByteCount(standardTotal)); Log.i("VideotronUsage",
     * "currentTotal: " + humanReadableByteCount(currentTotal));
     * Log.i("VideotronUsage", "standardAverage: " +
     * humanReadableByteCount(standardAverage)); Log.i("VideotronUsage",
     * "currentAverage: " + humanReadableByteCount(currentAverage));
     * Log.i("VideotronUsage", "TotalsDiff: " +
     * humanReadableByteCount(totalsDiff));
     * 
     * Log.i("VideotronUsage", "predicted total: " +
     * humanReadableByteCount(predictedTotal));
     */
    if (diffPercent > 50 || currentTotal > usage.getMaximumUsage()) {
      color = Color.RED;
    } else if (diffPercent > 25) {
      color = Color.rgb(255, 140, 0);
    } else if (totalsDiff > 0) {
      color = Color.YELLOW;
    }

    // display predicted total based on average

    views.setTextColor(R.id.usageView, color);
    views.setTextViewText(R.id.usageView, humanReadableByteCount(currentTotal));
    views.setTextColor(R.id.predictedTotal, color);
    views.setTextViewText(R.id.predictedTotal, humanReadableByteCount(predictedTotal));
    views.setTextViewText(R.id.maximumUsage, humanReadableByteCount(usage.getMaximumUsage()));

    views.setProgressBar(R.id.progressBar, 100, Math.round(combinedUsage), false);

    views.setTextViewText(R.id.daysLeft, daysLeft + "");

    /*
     * Intent launchActivity = new Intent(context, MainActivity.class);
     * PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
     * launchActivity, 0); views.setOnClickPendingIntent(R.id.widget,
     * pendingIntent);
     */
    appWidgetManager.updateAppWidget(appWidgetId, views);
  }

  private static String humanReadableByteCount(long bytes) {
    int unit = 1024;
    if (bytes < unit)
      return bytes + " B";
    int exp = (int) (Math.log(bytes) / Math.log(unit));
    String pre = "kMGTPE".charAt(exp - 1) + "";
    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
  }

}