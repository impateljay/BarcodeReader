package com.jay.barcodereader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.crash.FirebaseCrash;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RewardedVideoAdListener {

    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private AdView mAdView;
    private RewardedVideoAd mAd;
    private Button copy;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        copy = findViewById(R.id.btn_copy);
        result = findViewById(R.id.text_view_result);

        findViewById(R.id.btn_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkAndRequestPermissions()) {
                    new IntentIntegrator(MainActivity.this).initiateScan();
                }
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) MainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText(getString(R.string.app_name), result.getText().toString());
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(MainActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "failed to copy result", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (result.getText().toString().trim().equalsIgnoreCase("Scan the QR code to see Result")) {
            copy.setVisibility(View.GONE);
        } else {
            copy.setVisibility(View.VISIBLE);
        }

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                mAdView.setVisibility(View.VISIBLE);
                FirebaseCrash.log("onAdLoaded()");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                FirebaseCrash.log("onAdFailedToLoad() : " + errorCode);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                FirebaseCrash.log("onAdOpened()");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                FirebaseCrash.log("onAdLeftApplication()");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
                FirebaseCrash.log("onAdClosed()");
            }
        });
        mAd = MobileAds.getRewardedVideoAdInstance(this);
        mAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mAd.isLoaded()) {
            mAd.show();
        } else {
            loadRewardedVideoAd();
            if (mAd.isLoaded()) {
                mAd.show();
            }
            mAd.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_exit) {
            if (mAd.isLoaded()) {
                mAd.show();
            } else {
                loadRewardedVideoAd();
                if (mAd.isLoaded()) {
                    mAd.show();
                }
                mAd.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        mAd.resume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mAd.pause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mAd.isLoaded()) {
            mAd.show();
        } else {
            loadRewardedVideoAd();
            if (mAd.isLoaded()) {
                mAd.show();
            }
            mAd.show();
        }
        mAd.destroy(this);
        super.onDestroy();
    }

    private void loadRewardedVideoAd() {
        mAd.loadAd("ca-app-pub-7577307801270101/7401484749", new AdRequest.Builder().build());
    }


    private void transparentToolbar() {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {
            //we have a result
            String scanContent = scanningResult.getContents();
            // process received data
            if (scanContent != null && !scanContent.isEmpty()) {
                ((TextView) findViewById(R.id.text_view_result)).setText(scanningResult.getContents());
            } else {
                Alerter.create(MainActivity.this)
                        .setTitle("QR & Barcode Scanner")
                        .setText("Scan Cancelled")
                        .setBackgroundColorRes(R.color.colorPrimaryDark)
                        .show();
            }
        } else {
            Alerter.create(MainActivity.this)
                    .setTitle("QR & Barcode Scanner")
                    .setText("No scan data received!")
                    .setBackgroundColorRes(R.color.colorPrimaryDark)
                    .show();
        }
    }

    private boolean checkAndRequestPermissions() {
        int camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new IntentIntegrator(MainActivity.this).initiateScan();
                }
            }
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        FirebaseCrash.log("onRewardedVideoAdLoaded()");
    }

    @Override
    public void onRewardedVideoAdOpened() {
        FirebaseCrash.log("onRewardedVideoAdOpened()");
    }

    @Override
    public void onRewardedVideoStarted() {
        FirebaseCrash.log("onRewardedVideoStarted()");
    }

    @Override
    public void onRewardedVideoAdClosed() {
        FirebaseCrash.log("onRewardedVideoAdClosed()");
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        FirebaseCrash.log("onRewarded() : " + rewardItem);
        try {
            Alerter.create(MainActivity.this)
                    .setTitle("QR & Barcode Scanner")
                    .setText("Thanks for watching ad:)\nClick on ad to help developer")
                    .setBackgroundColorRes(R.color.colorPrimaryDark)
                    .show();
        } catch (Exception e) {
            FirebaseCrash.log(e.getMessage());
        }
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        FirebaseCrash.log("onRewardedVideoAdLeftApplication()");
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        FirebaseCrash.log("onRewardedVideoAdFailedToLoad() : " + i);
    }
}
