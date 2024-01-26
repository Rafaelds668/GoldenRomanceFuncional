package com.example.goldenromance.implementaciones.showCaseView.position;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.PointF;
import android.widget.ScrollView;

import androidx.annotation.Nullable;

import com.example.goldenromance.implementaciones.showCaseView.util.ActivityUtils;
import com.example.goldenromance.implementaciones.showCaseView.util.NavigationBarUtils;


public class TopRightToolbar implements ShowCasePosition {

    @Override
    public PointF getPosition(Activity activity) {
        float width = (float) activity.getWindow().getDecorView().getWidth();

        switch (ActivityUtils.getOrientation(activity)) {
            case Configuration.ORIENTATION_LANDSCAPE:
                return new PointF(
                    width - NavigationBarUtils.navigationBarMarginForRightOrientation(activity),
                    (float) ActivityUtils.statusBarHeight(activity)
                );
            default:
                return new PointF(
                    width,
                    ActivityUtils.statusBarHeight(activity)
                );
        }
    }

    @Nullable
    @Override
    public Point getScrollPosition(@Nullable ScrollView scrollView) {
        return null;
    }
}