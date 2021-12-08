package com.gamingworld.scratchandwin.util;

import android.content.Context;
import android.widget.Toast;

public class AppUtils {

    public static String COLLECTION_NAME = "transactions";

    public static float COIN_TO_RUPEE_CONVERTER = 11f;

    public static String SHARED_PREFERENCE = "shared_preference";
    public static String CURRENT_BALANCE = "current_balance";
    public static String PENDING_PAYMENT = "pending_payment";
    public static String LAST_DATE_CHECK = "last_date_check";
    public static String SHARE_COUNT = "share_count";

    public static int MINIMUM_REDEEM_BALANCE = 30000;

    public static String IS_APP_RATED = "is_app_rated";

    public static String DIAMOND_SCRATCHED_COUNT = "diamond_count";
    public static String PLATINUM_SCRATCHED_COUNT = "platinum_count";
    public static String GOLD_SCRATCHED_COUNT = "gold_count";
    public static String SILVER_SCRATCHED_COUNT = "silver_count";
    public static String DAILY_SCRATCHED_COUNT = "daily_count";

    public static int TOTAL_DIAMOND_CARDS = 5;
    public static int TOTAL_PLATINUM_CARDS = 10;
    public static int TOTAL_GOLD_CARDS = 15;
    public static int TOTAL_SILVER_CARDS = 20;
    public static int TOTAL_DAILY_CARDS = 2;

    public static int DIAMOND_LOTTERY_LIMIT = 50;
    public static int PLATINUM_LOTTERY_LIMIT = 40;
    public static int GOLD_LOTTERY_LIMIT = 30;
    public static int SILVER_LOTTERY_LIMIT = 20;
    public static int DAILY_LOTTERY_LIMIT = 80;

    public static int PERMISSION_REQUEST_CODE = 0;

    public static String FLAG = "card_type";


    public static void showShortToast (Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
