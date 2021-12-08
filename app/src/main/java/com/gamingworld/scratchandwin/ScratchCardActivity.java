package com.gamingworld.scratchandwin;


import static com.gamingworld.scratchandwin.util.AppUtils.CURRENT_BALANCE;
import static com.gamingworld.scratchandwin.util.AppUtils.DAILY_LOTTERY_LIMIT;
import static com.gamingworld.scratchandwin.util.AppUtils.DAILY_SCRATCHED_COUNT;
import static com.gamingworld.scratchandwin.util.AppUtils.DIAMOND_LOTTERY_LIMIT;
import static com.gamingworld.scratchandwin.util.AppUtils.DIAMOND_SCRATCHED_COUNT;
import static com.gamingworld.scratchandwin.util.AppUtils.FLAG;
import static com.gamingworld.scratchandwin.util.AppUtils.GOLD_LOTTERY_LIMIT;
import static com.gamingworld.scratchandwin.util.AppUtils.GOLD_SCRATCHED_COUNT;
import static com.gamingworld.scratchandwin.util.AppUtils.PLATINUM_LOTTERY_LIMIT;
import static com.gamingworld.scratchandwin.util.AppUtils.PLATINUM_SCRATCHED_COUNT;
import static com.gamingworld.scratchandwin.util.AppUtils.SHARED_PREFERENCE;
import static com.gamingworld.scratchandwin.util.AppUtils.SILVER_LOTTERY_LIMIT;
import static com.gamingworld.scratchandwin.util.AppUtils.SILVER_SCRATCHED_COUNT;
import static com.gamingworld.scratchandwin.util.AppUtils.TOTAL_DAILY_CARDS;
import static com.gamingworld.scratchandwin.util.AppUtils.TOTAL_DIAMOND_CARDS;
import static com.gamingworld.scratchandwin.util.AppUtils.TOTAL_GOLD_CARDS;
import static com.gamingworld.scratchandwin.util.AppUtils.TOTAL_PLATINUM_CARDS;
import static com.gamingworld.scratchandwin.util.AppUtils.TOTAL_SILVER_CARDS;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.IUnityBannerListener;
import com.unity3d.services.banners.UnityBanners;
import com.unity3d.services.banners.view.BannerPosition;

import in.myinnos.androidscratchcard.ScratchCard;


public class ScratchCardActivity extends AppCompatActivity {

    private int totalCards = 0;
    private String scratched;
    private int lotteryLimit = 0;
    private int wonLotteryAmount = 0;
    private int alreadyScratched = 0;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    //Views
    private TextView txtBank;
    private int cardsLeft;
    private ScratchCard mScratchCard;
    private TextView txtLotteryAmount, txtBalanceScratch;
    private ImageView imgGoBack;


    private final String interstitialAdUnitId = "Scratch_Win";

    private String bannerAdUnitId = "Banner_Scratch_Card";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scratch_card);

        pref = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE);
        editor = pref.edit();


        int flag = pref.getInt(FLAG, 0);

        switch (flag) {
            case 1:
                totalCards = TOTAL_DIAMOND_CARDS;
                scratched = DIAMOND_SCRATCHED_COUNT;
                lotteryLimit = DIAMOND_LOTTERY_LIMIT;
                break;
            case 2:
                totalCards = TOTAL_PLATINUM_CARDS;
                scratched = PLATINUM_SCRATCHED_COUNT;
                lotteryLimit = PLATINUM_LOTTERY_LIMIT;
                break;
            case 3:
                totalCards = TOTAL_GOLD_CARDS;
                scratched = GOLD_SCRATCHED_COUNT;
                lotteryLimit = GOLD_LOTTERY_LIMIT;
                break;
            case 4:
                totalCards = TOTAL_SILVER_CARDS;
                scratched = SILVER_SCRATCHED_COUNT;
                lotteryLimit = SILVER_LOTTERY_LIMIT;
                break;
            case 5:
                totalCards = TOTAL_DAILY_CARDS;
                scratched = DAILY_SCRATCHED_COUNT;
                lotteryLimit = DAILY_LOTTERY_LIMIT;
                break;

        }

        editor.putInt(FLAG, 0);
        editor.apply();

        alreadyScratched = pref.getInt( scratched ,0);

        initViews();

        txtBalanceScratch.setText(getString(R.string.balance, pref.getInt(CURRENT_BALANCE, 0)));
        cardsLeft = totalCards - alreadyScratched;
        txtBank.setText("You Have " + cardsLeft + " Cards Left");
        if (cardsLeft <= 0) {
            showDismissDialogue();
        } else {
            wonLotteryAmount = (int) (Math.random()*(lotteryLimit-(lotteryLimit-20)) + (lotteryLimit-20));
            txtLotteryAmount.setText(String.valueOf(wonLotteryAmount));

            mScratchCard = findViewById(R.id.scratchCard);
            mScratchCard.setScratchWidth(100f);
            mScratchCard.setOnScratchListener((scratchCard, visiblePercent) -> {
                if (visiblePercent > 0.6) {
                    mScratchCard.setVisibility(View.GONE);
                    addToBalance(wonLotteryAmount);
                }
            });

        }

        imgGoBack.setOnClickListener(v -> finish());

        UnityAds.load(interstitialAdUnitId);

        IUnityBannerListener iUnityBannerListener = new IUnityBannerListener() {
            @Override
            public void onUnityBannerLoaded(String s, View view) {
                ((ViewGroup) findViewById(R.id.banner_scratch_card)).removeView(view);
                ((ViewGroup) findViewById(R.id.banner_scratch_card)).addView(view);
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

    }

    private void addToBalance(int wonLotteryAmount) {
        final Dialog dialog = new Dialog(ScratchCardActivity.this);

        dialog.setContentView(R.layout.win_dialogue);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final Button addToBalanceBtn = dialog.findViewById(R.id.add_to_balance);
        final TextView txtLotteryAmount = dialog.findViewById(R.id.lottery_amount);

        txtLotteryAmount.setText(getString(R.string.int_empty_string, wonLotteryAmount));

        addToBalanceBtn.setOnClickListener(v -> {
            int currentBalance = pref.getInt(CURRENT_BALANCE, 0);
            editor.putInt(CURRENT_BALANCE, (currentBalance + wonLotteryAmount));
            editor.putInt(scratched, ++alreadyScratched);
            editor.apply();
            dialog.dismiss();

            if (UnityAds.isReady(interstitialAdUnitId)) {
                UnityAds.show(ScratchCardActivity.this, interstitialAdUnitId);
            }
        });
        dialog.show();

        dialog.setOnDismissListener(dialog1 -> {
            cardsLeft = totalCards-alreadyScratched;
            txtBank.setText(getString(R.string.cards_left, (totalCards-alreadyScratched)));
            txtBalanceScratch.setText(getString(R.string.balance, pref.getInt(CURRENT_BALANCE, 0)));
            if (cardsLeft <= 0) {
                showDismissDialogue();
            } else {
                resetCard();
            }
        });
    }

    @SuppressLint("InflateParams")
    private void resetCard() {

        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);

        constraintLayout.removeView(mScratchCard);

        mScratchCard = (ScratchCard) LayoutInflater.from(this).inflate(R.layout.scratch_card, null);
        constraintLayout.addView(mScratchCard);

        mScratchCard.setScratchWidth(100f);
        mScratchCard.setOnScratchListener((scratchCard, visiblePercent) -> {
            if (visiblePercent > 0.6) {
                mScratchCard.setVisibility(View.GONE);
                addToBalance(wonLotteryAmount);
            }
        });

        wonLotteryAmount = (int) (Math.random()*(lotteryLimit-(lotteryLimit-20)) + (lotteryLimit-20));
        txtLotteryAmount.setText(getString(R.string.int_empty_string, wonLotteryAmount));
    }

    private void initViews() {
        txtBank = findViewById(R.id.txt_bank);
        txtLotteryAmount = findViewById(R.id.txt_lottery_amount);
        imgGoBack = findViewById(R.id.img_go_back_scratch);

        txtBalanceScratch = findViewById(R.id.balance_scratch);
    }

    private void showDismissDialogue() {
        final Dialog dialog = new Dialog(ScratchCardActivity.this); // Context, this, etc.
        dialog.setContentView(R.layout.no_card_left);
        dialog.setTitle(R.string.no_more_card);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button finish = dialog.findViewById(R.id.dialog_ok);
        finish.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}