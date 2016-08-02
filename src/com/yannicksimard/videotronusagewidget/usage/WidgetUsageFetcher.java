package com.yannicksimard.videotronusagewidget.usage;

import android.appwidget.AppWidgetManager;
import android.content.Context;

import com.yannicksimard.videotronusagewidget.VideotronUsageWidgetProvider;

public class WidgetUsageFetcher extends UsageFetcher {

  private final Integer appWidgetId;
  private final AppWidgetManager appWidgetManager;

  public WidgetUsageFetcher(Context context, Integer appWidgetId, String userKey) {
    super(context, userKey);
    this.appWidgetManager = AppWidgetManager.getInstance(context);
    this.appWidgetId = appWidgetId;
  }

  @Override
  protected void onPostExecute(Usage usage) {
    if (usage.getUsage() != null) {
      VideotronUsageWidgetProvider.onUsageDataFound(usage, context, appWidgetManager, appWidgetId);
    }
  }

}
