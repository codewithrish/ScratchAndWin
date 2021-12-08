package com.gamingworld.scratchandwin;


import static com.gamingworld.scratchandwin.util.AppUtils.COIN_TO_RUPEE_CONVERTER;
import static com.gamingworld.scratchandwin.util.AppUtils.COLLECTION_NAME;
import static com.gamingworld.scratchandwin.util.AppUtils.CURRENT_BALANCE;
import static com.gamingworld.scratchandwin.util.AppUtils.FLAG;
import static com.gamingworld.scratchandwin.util.AppUtils.MINIMUM_REDEEM_BALANCE;
import static com.gamingworld.scratchandwin.util.AppUtils.PENDING_PAYMENT;
import static com.gamingworld.scratchandwin.util.AppUtils.SHARED_PREFERENCE;
import static com.gamingworld.scratchandwin.util.AppUtils.showShortToast;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.gamingworld.scratchandwin.adapter.MyRecyclerViewAdapter;
import com.gamingworld.scratchandwin.util.DBHelper;
import com.gamingworld.scratchandwin.util.TransactionDetail;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.IUnityBannerListener;
import com.unity3d.services.banners.UnityBanners;
import com.unity3d.services.banners.view.BannerPosition;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RedeemRewardsActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener{

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Constants
    private int currentBal;
    private String android_id;
    private int pendingTransactionCount = 0;

    //Views
    private TextView txtCoinBalance, txtRupeeBalance, txtEmptyMessage;
    private Button btnRedeemNow;
    private ImageView imgGoBack;

    //Shared Pref
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private ArrayList<TransactionDetail> transactions;

    //Database
    DBHelper mydb;


    private final String interstitialAdUnitId = "Redeem_Reward";

    private String bannerAdUnitId = "Banner_Redeem_Reward";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem_rewards);

        pref = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE);
        editor = pref.edit();


        mydb = new DBHelper(this);

        initViews();

        pref = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE);
        currentBal = pref.getInt(CURRENT_BALANCE, 0);


        txtCoinBalance.setText(getString(R.string.ph_coins, currentBal));
        txtRupeeBalance.setText(getString(R.string.ph_rupee_balance, currentBal/COIN_TO_RUPEE_CONVERTER));

        boolean isPaymentPending = pref.getBoolean(PENDING_PAYMENT, false);

        if (isPaymentPending) {
            getPendingDetails();
            btnRedeemNow.setEnabled(false);
        } else {
            btnRedeemNow.setEnabled(true);
        }

        if (currentBal > MINIMUM_REDEEM_BALANCE) {
            UnityAds.load(interstitialAdUnitId);
        }

        btnRedeemNow.setOnClickListener(v -> {
            if (currentBal > MINIMUM_REDEEM_BALANCE) {
                showPopup();
            } else {
                showShortToast(getApplicationContext(), getString(R.string.no_minimum_balance, MINIMUM_REDEEM_BALANCE));
            }
        });
        imgGoBack.setOnClickListener(v -> finish());

        refreshTransactionsList();

        if (transactions.size() != 0) {
            txtEmptyMessage.setVisibility(View.GONE);
        }


        IUnityBannerListener iUnityBannerListener = new IUnityBannerListener() {
            @Override
            public void onUnityBannerLoaded(String s, View view) {
                ((ViewGroup) findViewById(R.id.banner_container)).removeView(view);
                ((ViewGroup) findViewById(R.id.banner_container)).addView(view);
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

        editor.putInt(FLAG, 0);
        editor.apply();

    }

    private void refreshTransactionsList() {

        transactions = mydb.getAllTransactions();
        RecyclerView recyclerView = findViewById(R.id.rv_previous_transactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(this, transactions);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        showFullPaymentDetails(transactions.get(position));
    }

    private void showFullPaymentDetails(TransactionDetail transactionDetail) {
        final Dialog dialog = new Dialog(RedeemRewardsActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_payment_details);

        final TextView detCoins, detRupees, detStatus, detMedium, detDate, detDetails;
        detCoins = dialog.findViewById(R.id.det_coins);
        detRupees = dialog.findViewById(R.id.det_rupees);
        detStatus = dialog.findViewById(R.id.det_status);
        detMedium = dialog.findViewById(R.id.det_medium);
        detDate = dialog.findViewById(R.id.det_date);
        detDetails = dialog.findViewById(R.id.det_payment_details);

        detDetails.setText(transactionDetail.getTranDetails());

        detCoins.setText(getString(R.string.int_empty_string, transactionDetail.getTranCoins()));

        detRupees.setText(getString(R.string.float_empty_string, transactionDetail.getTranCoins()/COIN_TO_RUPEE_CONVERTER));
        detStatus.setText(transactionDetail.getTranStatus());
        detMedium.setText(transactionDetail.getTranMedium());
        String date = transactionDetail.getDate();
        if (date == null) {
            detDate.setText(getString(R.string.not_paid_yet));
        } else {
            detDate.setText(transactionDetail.getDate());
        }

        dialog.show();
    }

    @SuppressLint("HardwareIds")
    private void initViews() {
        txtCoinBalance = findViewById(R.id.txt_coin_balance);
        txtRupeeBalance = findViewById(R.id.txt_rupee_balance);
        txtEmptyMessage = findViewById(R.id.txt_empty_msg);
        btnRedeemNow = findViewById(R.id.btn_redeem_now);
        imgGoBack = findViewById(R.id.img_go_back_redeem);

        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

    }

    private void getPendingDetails() {
        db.collection(COLLECTION_NAME).document(android_id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    TransactionDetail transactionDetail = documentSnapshot.toObject(TransactionDetail.class);
                    if (transactionDetail.getTranStatus().equals(getResources().getString(R.string.pending))) {
                        btnRedeemNow.setEnabled(false);
                        pendingTransactionCount = 1;

                    } else {
                        //linearLayout.setVisibility(View.GONE);
                        editor = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE).edit();
                        editor.putBoolean(PENDING_PAYMENT , false);
                        editor.apply();
                        btnRedeemNow.setEnabled(true);
                        db.collection(COLLECTION_NAME).document(android_id).delete();
                    }

                    if (pendingTransactionCount == 0) {
                        mydb.updateTransaction(getResources().getString(R.string.paid), DateFormat.format("yyyy-MM-dd", new Date()).toString());
                        refreshTransactionsList();
                    }
                })
                .addOnFailureListener(e -> showShortToast(getApplicationContext(), e.getMessage()));
    }

    private void showPopup() {
        final Dialog dialog = new Dialog(this); // Context, this, etc.
        dialog.setContentView(R.layout.redeem_popup);
        dialog.setTitle(R.string.no_more_card);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        final RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup);
        final RadioButton rbPayTm = dialog.findViewById(R.id.rb_paytm);
        final RadioButton rbUPI = dialog.findViewById(R.id.rb_upi);
        final EditText enterDetails = dialog.findViewById(R.id.enter_details);
        final Button cancelBtn = dialog.findViewById(R.id.cancelRdm);
        final Button redeemBtn = dialog.findViewById(R.id.redeemRdm);

        TransactionDetail transactionDetail = new TransactionDetail();

        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            enterDetails.setHint("Select Payment Method");
            enterDetails.setEnabled(false);
        }
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            enterDetails.setEnabled(true);
            if (checkedId == R.id.rb_paytm) {
                enterDetails.setHint("Enter PayTm Number");
                //transactionDetail.setTranMedium(getResources().getString(R.string.paytm));
                transactionDetail.setTranMedium(rbPayTm.getText().toString());
            } else {
                enterDetails.setHint("Enter UPI ID");
                //transactionDetail.setTranMedium(getResources().getString(R.string.upi));
                transactionDetail.setTranMedium(rbUPI.getText().toString());
            }
        });
        transactionDetail.setTranCoins(currentBal);
        transactionDetail.setTranStatus(getResources().getString(R.string.pending));
        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        redeemBtn.setOnClickListener(v -> {

            if (!isNetworkAvailable()) {
                showShortToast(getApplicationContext(), getResources().getString(R.string.network_error_msg));
                return;
            }

            transactionDetail.setTranDetails(enterDetails.getText().toString());

            boolean flag = false;

            if (transactionDetail.getTranMedium() != null && transactionDetail.getTranMedium().equals(getResources().getString(R.string.paytm))) {
                if (!isValidNumber(transactionDetail.getTranDetails())) {
                    enterDetails.setError("Enter Valid Number");
                } else {
                    flag = true;
                }

            } else if (transactionDetail.getTranMedium() != null && transactionDetail.getTranMedium().equals(getResources().getString(R.string.upi))) {
                if (!isValidUPI(transactionDetail.getTranDetails())) {
                    enterDetails.setError("Enter Valid Upi Id");
                } else {
                    flag = true;
                }
            }
            transactionDetail.setTranDetails(enterDetails.getText().toString());

            if (flag) {
                redeemNow(transactionDetail);
                dialog.dismiss();
            } else {
                showShortToast(getApplicationContext(), getString(R.string.error_enter_payment_details));
            }
        });
    }

    private void redeemNow(TransactionDetail transactionDetail) {
        db.collection(COLLECTION_NAME).document(android_id).set(transactionDetail)
                .addOnSuccessListener(unused -> {

                    mydb.insertTransaction(transactionDetail.getTranCoins(), transactionDetail.getTranMedium(), transactionDetail.getTranDetails(), getString(R.string.pending), null);
                    refreshTransactionsList();
                    editor = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE).edit();
                    editor.putBoolean(PENDING_PAYMENT , true);
                    editor.putInt(CURRENT_BALANCE, 0);
                    editor.apply();
                    showShortToast(getApplicationContext(), getString(R.string.request_sent));
                    btnRedeemNow.setEnabled(false);
                    txtCoinBalance.setText(getString(R.string.ph_coins, 0));
                    txtRupeeBalance.setText(getString(R.string.ph_rupee_balance, 0f));

                    if (UnityAds.isReady(interstitialAdUnitId)) {
                        UnityAds.show(RedeemRewardsActivity.this, interstitialAdUnitId);
                    }

                })
                .addOnFailureListener(e -> {
                    //progressBar.setVisibility(View.GONE);
                    showShortToast(getApplicationContext(), getString(R.string.error_occurred));
                });
    }

    private boolean isValidNumber(String phone) {

        String PhonePattern = "^(\\+\\d{1,3}[- ]?)?\\d{10}$";

        Pattern pattern = Pattern.compile(PhonePattern);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }
    private boolean isValidUPI(String upiId) {

        String PhonePattern = "[a-zA-Z0-9.\\-_]{2,256}@[a-zA-Z]{2,64}";

        Pattern pattern = Pattern.compile(PhonePattern);
        Matcher matcher = pattern.matcher(upiId);
        return matcher.matches();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}