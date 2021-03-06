package nonoobs.cryptopricewidgets;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.widget.RemoteViews;

import nonoobs.cryptopricewidgets.service.CryptoPriceService;

/**
 * Created by Doug on 2017-05-04.
 */

public class CryptoAppWidgetConfigure extends Activity
{
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    public CryptoAppWidgetConfigure() {
        super();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setResult(RESULT_CANCELED);

        setContentView(R.layout.crypto_widget_configure);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && !hasUsageStatsPermission(getApplicationContext())) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        findViewById(R.id.button).setOnClickListener(mOnClickListener);

        NotificationChannel channel = new NotificationChannel("CryptoUpdater", "CryptoUpdater", NotificationManager.IMPORTANCE_NONE);
        channel.setDescription("Contains the crypto updater foreground service notification when it is running");
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;
        return granted;
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.crypto_widget);
            //views.setTextViewText(R.id.appwidget_text, CryptoAppWidgetProvider.getBTCValue());
            AppWidgetManager.getInstance(CryptoAppWidgetConfigure.this).updateAppWidget(mAppWidgetId, views);

            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            WidgetSettings settings = new WidgetSettings(mAppWidgetId);
            settings.setSource(WidgetSettings.SOURCE_GDAX);
            settings.setProduct("BTC-USD");
            PrefsHelper.serializeWidgetSettingsToPrefs(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()), settings);

            finish();
        }
    };
}
