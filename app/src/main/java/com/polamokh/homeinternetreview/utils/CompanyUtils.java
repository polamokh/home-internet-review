package com.polamokh.homeinternetreview.utils;

import android.widget.ImageView;

import com.polamokh.homeinternetreview.R;

public final class CompanyUtils {

    public static final String WE = "WE";

    public static final String ORANGE = "Orange";

    public static final String ETISALAT = "Etisalat";

    public static final String VODAFONE = "Vodafone";

    private CompanyUtils() {

    }


    public static void setCompanyPicture(String companyName, ImageView imageView) {
        switch (companyName) {
            case CompanyUtils.WE:
                imageView.setImageResource(R.drawable.we);
                return;
            case CompanyUtils.ORANGE:
                imageView.setImageResource(R.drawable.orange);
                return;
            case CompanyUtils.VODAFONE:
                imageView.setImageResource(R.drawable.vodafone);
                return;
            case CompanyUtils.ETISALAT:
                imageView.setImageResource(R.drawable.etisalat);
                return;
            default:
        }
    }

}
