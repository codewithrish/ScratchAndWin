package com.gamingworld.scratchandwin;


import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static com.gamingworld.scratchandwin.util.AppUtils.CURRENT_BALANCE;
import static com.gamingworld.scratchandwin.util.AppUtils.DAILY_SCRATCHED_COUNT;
import static com.gamingworld.scratchandwin.util.AppUtils.DIAMOND_SCRATCHED_COUNT;
import static com.gamingworld.scratchandwin.util.AppUtils.FLAG;
import static com.gamingworld.scratchandwin.util.AppUtils.GOLD_SCRATCHED_COUNT;
import static com.gamingworld.scratchandwin.util.AppUtils.IS_APP_RATED;
import static com.gamingworld.scratchandwin.util.AppUtils.LAST_DATE_CHECK;
import static com.gamingworld.scratchandwin.util.AppUtils.PERMISSION_REQUEST_CODE;
import static com.gamingworld.scratchandwin.util.AppUtils.PLATINUM_SCRATCHED_COUNT;
import static com.gamingworld.scratchandwin.util.AppUtils.SHARED_PREFERENCE;
import static com.gamingworld.scratchandwin.util.AppUtils.SHARE_COUNT;
import static com.gamingworld.scratchandwin.util.AppUtils.SILVER_SCRATCHED_COUNT;
import static com.gamingworld.scratchandwin.util.AppUtils.TOTAL_DAILY_CARDS;
import static com.gamingworld.scratchandwin.util.AppUtils.TOTAL_DIAMOND_CARDS;
import static com.gamingworld.scratchandwin.util.AppUtils.TOTAL_GOLD_CARDS;
import static com.gamingworld.scratchandwin.util.AppUtils.TOTAL_PLATINUM_CARDS;
import static com.gamingworld.scratchandwin.util.AppUtils.TOTAL_SILVER_CARDS;
import static com.gamingworld.scratchandwin.util.AppUtils.showShortToast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.IUnityBannerListener;
import com.unity3d.services.banners.UnityBanners;
import com.unity3d.services.banners.view.BannerPosition;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Intent intent;

    //Constants
    int flag = 0;
    boolean doubleBackToExitPressedOnce = false;

    private int lastCheck = 0;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    //Views
    private TextView txtBalance;
    private TextView remainingDiamondCard, remainingPlatinumCard, remainingGoldCard, remainingSilverCard, remainingDailyCard;
    private int currentBal = 0;

    private Context mContext;

    private LocationManager locationManager;


    private final String unityGameID = "3293029";
    private final String bannerAdUnitId = "Banner_Main";
    private final String interstitialAdUnitId = "Android_Interstitial";
    private final Boolean testMode = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE);
        editor = pref.edit();

        mContext = getApplicationContext();
        initViews();


        if (isNetworkAvailable()) {
            if (checkPermission()) {
                proceedWithApp();
            } else {
                requestPermission();
            }
        } else {

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setMessage("No internet access Try again");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Try Again",
                    (dialog, which) -> onRestart());
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
        

        UnityAds.initialize(this, unityGameID, testMode);
        IUnityAdsListener iUnityAdsListener = new IUnityAdsListener() {
            @Override
            public void onUnityAdsReady(String s) {
            }

            @Override
            public void onUnityAdsStart(String s) {
            }

            @Override
            public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {
                int check = pref.getInt(FLAG, 0);

                if (check != 0) {
                    if (check > 0 && check < 6) {
                        goToScratchActivity();
                    } else if (check == 6)
                        goToRedeemRewards();
                }
            }

            @Override
            public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {
            }
        };
        UnityAds.setListener(iUnityAdsListener);

        IUnityBannerListener iUnityBannerListener = new IUnityBannerListener() {
            @Override
            public void onUnityBannerLoaded(String s, View view) {
                ((ViewGroup) findViewById(R.id.banner_container_main)).removeView(view);
                ((ViewGroup) findViewById(R.id.banner_container_main)).addView(view);
            }

            @Override
            public void onUnityBannerUnloaded(String s) {

            }

            @Override
            public void onUnityBannerShow(String s) {

            }

            @Override
            public void onUnityBannerClick(String s) {
           }

            @Override
            public void onUnityBannerHide(String s) {

            }

            @Override
            public void onUnityBannerError(String s) {

            }
        };
        UnityBanners.setBannerListener(iUnityBannerListener);
        UnityBanners.loadBanner(this, bannerAdUnitId);

        UnityAds.load(interstitialAdUnitId);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        if (!isNetworkAvailable()) {
            showShortToast(getApplicationContext(), "No Internet Connection");
            return;
        }
        switch (v.getId()) {
            case R.id.diamond_card: flag = 1; break;
            case R.id.platinum_card: flag = 2; break;
            case R.id.gold_card: flag = 3; break;
            case R.id.silver_card: flag = 4; break;
            case R.id.daily_card: flag = 5; break;
            case R.id.redeem_card: flag = 6; break;
            case R.id.share_app_card: flag = 0; shareAppMethod(); break;
            case R.id.rate_app_card: flag = 0; rateApp(); break;
            case R.id.privacy_policy_card: flag = 0; goToPrivacyPolicy(); break;
            case R.id.more_apps_card: flag = 0; moreApps(); break;
        }

        editor.putInt(FLAG, flag);
        editor.apply();

        if (flag !=0)
        if (UnityAds.isReady(interstitialAdUnitId)) {
            UnityAds.show(MainActivity.this, interstitialAdUnitId);
        } else {
            if (flag > 0 && flag < 6)
                goToScratchActivity();
            else if (flag == 6)
                goToRedeemRewards();
        }

    }

    private void goToRedeemRewards() {
        startActivity(new Intent(MainActivity.this, RedeemRewardsActivity.class));
    }

    private void goToScratchActivity() {
        startActivity(intent);
    }

    //Share App
    ActivityResultLauncher<Intent> shareActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                int share_reward = (int)(Math.random()*20);
                editor.putInt(CURRENT_BALANCE, (currentBal + share_reward));
                editor.apply();
                currentBal = pref.getInt(CURRENT_BALANCE, 0);
                txtBalance.setText(getString(R.string.balance, currentBal));

            });

    //rate App
    ActivityResultLauncher<Intent> rateAppResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                boolean isRated = pref.getBoolean(IS_APP_RATED, false);

                if (isRated) {
                    showShortToast(getApplicationContext(), "Reward Already Claimed");
                } else {

                    int rate_app_reward = (int)(Math.random()*200);
                    editor = pref.edit();
                    editor.putBoolean(IS_APP_RATED, true);
                    editor.putInt(CURRENT_BALANCE, currentBal + rate_app_reward);
                    editor.apply();
                    currentBal = pref.getInt(CURRENT_BALANCE, 0);
                    txtBalance.setText(getString(R.string.balance, currentBal));
                }
            });

    //Permission
    ActivityResultLauncher<Intent> permissionResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (checkPermission()) {
                    proceedWithApp();
                } else {
                    requestPermission();
                }
            });

    private void goToPrivacyPolicy() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://gamingworldinfinite.blogspot.com/2021/09/privacy-policy-scratch-and-win.html")));
    }

    private void moreApps() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=4871427689531609193")));
    }

    private void showGoToSettingsDialog(String message) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {

                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    permissionResultLauncher.launch(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> finish())
                .setCancelable(false)
                .create()
                .show();
    }

    private void rateApp() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName()));
        rateAppResultLauncher.launch(intent);
    }

    private void proceedWithApp() {

        // Constants
        currentBal = pref.getInt(CURRENT_BALANCE, 0);

        txtBalance.setText(getString(R.string.balance, currentBal));

        //GET THE LAST CHECKED DATE FROM SHARED PREFERENCES, WE ARE USING DAY OF A MONTH
        lastCheck = pref.getInt(LAST_DATE_CHECK, 0);

        if (isNewDay()) {
            editor.putInt(LAST_DATE_CHECK, lastCheck);
            editor.putInt(SHARE_COUNT, 0);
            editor.putInt(DIAMOND_SCRATCHED_COUNT, 0);
            editor.putInt(PLATINUM_SCRATCHED_COUNT, 0);
            editor.putInt(GOLD_SCRATCHED_COUNT, 0);
            editor.putInt(SILVER_SCRATCHED_COUNT, 0);
            editor.putInt(DAILY_SCRATCHED_COUNT, 0);
            editor.commit();
        }

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {

            boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

            if (locationAccepted)
                showShortToast(getApplicationContext(), "Permission Granted, Now you can access location data and camera.");
            else {
                showShortToast(getApplicationContext(), "Permission Denied, You cannot access location data and camera.");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION)) {
                        showMessageOKCancel(getString(R.string.request_permission_message),
                                (dialog, which) -> requestPermissions(new String[]{ACCESS_COARSE_LOCATION},
                                        PERMISSION_REQUEST_CODE));
                        //return;
                    } else {
                        showGoToSettingsDialog(getString(R.string.permission_reason));
                    }
                }

            }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", (dialog, which) -> finish())
                .setCancelable(false)
                .create()
                .show();
    }

    public boolean isNewDay() {

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Location myLocation = getLastKnownLocation();
        long networkTS = 0;
        if(myLocation != null) {
            networkTS = myLocation.getTime();
        }
        int today = Integer.parseInt(DateFormat.format("dd", new Date(networkTS)).toString());
        boolean ret = lastCheck == 0 || today > lastCheck || (lastCheck == 31 && today == 1) || (lastCheck == 30 && today == 1) || (lastCheck == 28 && today == 1);
        lastCheck = today;
        return ret;
    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            @SuppressLint("MissingPermission") Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void shareAppMethod() {
        //GET THE NUMBER OF TIMES THE ACTIVITY WAS OPENED FROM SHARED PREFERENCES
        int count = pref.getInt(SHARE_COUNT, 0);

        if (count >= 3) {
            showShortToast(mContext, "You can share only 3 Time, Try Tomorrow");
        } else {
            count++;
            editor.putInt(SHARE_COUNT, count);
            editor.apply();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Hey check out my app at: https://play.google.com/store/apps/details?id=" + getPackageName());
            sendIntent.setType("text/plain");
            shareActivityResultLauncher.launch(sendIntent);
        }
    }

    private void initViews() {
        final CardView diamondCard = findViewById(R.id.diamond_card);
        final CardView platinumCard = findViewById(R.id.platinum_card);
        final CardView goldCard = findViewById(R.id.gold_card);
        final CardView silverCard = findViewById(R.id.silver_card);
        final CardView dailyCard = findViewById(R.id.daily_card);
        final CardView redeemRewardsCard = findViewById(R.id.redeem_card);
        final CardView shareAppCard = findViewById(R.id.share_app_card);
        final CardView rateAppCard = findViewById(R.id.rate_app_card);
        final CardView privacyPolicyCard = findViewById(R.id.privacy_policy_card);
        final CardView moreAppsCard = findViewById(R.id.more_apps_card);

        remainingDiamondCard = findViewById(R.id.remaining_diamond_cards);
        remainingPlatinumCard = findViewById(R.id.remaining_platinum_cards);
        remainingGoldCard = findViewById(R.id.remaining_gold_cards);
        remainingSilverCard = findViewById(R.id.remaining_silver_cards);
        remainingDailyCard = findViewById(R.id.remaining_daily_cards);

        txtBalance = findViewById(R.id.txt_balance);


        diamondCard.setOnClickListener(this);
        platinumCard.setOnClickListener(this);
        goldCard.setOnClickListener(this);
        silverCard.setOnClickListener(this);
        dailyCard.setOnClickListener(this);
        redeemRewardsCard.setOnClickListener(this);
        shareAppCard.setOnClickListener(this);
        rateAppCard.setOnClickListener(this);
        privacyPolicyCard.setOnClickListener(this);
        moreAppsCard.setOnClickListener(this);

        intent = new Intent(this, ScratchCardActivity.class);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();

        currentBal = pref.getInt(CURRENT_BALANCE, 0);

        txtBalance.setText((getString(R.string.balance, currentBal)));

        remainingDiamondCard.setText(getResources().getString(R.string.txt_remaining_cards) + (TOTAL_DIAMOND_CARDS - pref.getInt( DIAMOND_SCRATCHED_COUNT ,0)));
        remainingPlatinumCard.setText(getResources().getString(R.string.txt_remaining_cards) + (TOTAL_PLATINUM_CARDS - pref.getInt( PLATINUM_SCRATCHED_COUNT ,0)));
        remainingGoldCard.setText(getResources().getString(R.string.txt_remaining_cards) + (TOTAL_GOLD_CARDS - pref.getInt( GOLD_SCRATCHED_COUNT ,0)));
        remainingSilverCard.setText(getResources().getString(R.string.txt_remaining_cards) + (TOTAL_SILVER_CARDS - pref.getInt( SILVER_SCRATCHED_COUNT ,0)));
        remainingDailyCard.setText(getResources().getString(R.string.txt_remaining_cards) + (TOTAL_DAILY_CARDS - pref.getInt( DAILY_SCRATCHED_COUNT ,0)));


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        this.recreate();
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        showShortToast(getApplicationContext(), "Please click BACK again to exit");

        new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }
}