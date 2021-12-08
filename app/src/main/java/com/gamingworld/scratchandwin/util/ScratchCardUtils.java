package com.gamingworld.scratchandwin.util;

import android.content.Context;

public class ScratchCardUtils {

    public static float dipToPx (Context context, Float dipValue) {
        Float density = context.getResources().getDisplayMetrics().density;
        return density * dipValue;
    }
}
