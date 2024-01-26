package com.example.goldenromance.implementaciones.showCaseView.step;

import android.content.Context;


import androidx.annotation.ColorRes;

import com.example.goldenromance.R;
import com.example.goldenromance.implementaciones.showCaseView.position.ShowCasePosition;
import com.example.goldenromance.implementaciones.showCaseView.radius.Radius;
import com.example.goldenromance.implementaciones.showCaseView.radius.ShowCaseRadius;
import com.example.goldenromance.implementaciones.showCaseView.util.ViewUtils;


public class ShowCaseStep {

    private ShowCasePosition position;
    private ShowCaseRadius radius;
    private String message;

    @ColorRes
    private int color;

    public ShowCasePosition getPosition() {
        return position;
    }

    public ShowCaseRadius getRadius() {
        return radius;
    }

    public String getMessage() {
        return message;
    }

    public @ColorRes
    int getColor() {
        return color;
    }

    public static class Builder {

        private final static int DEFAULT_RADIUS_DP = 70;

        private ShowCaseRadius radius;
        private ShowCasePosition position;
        private String message;

        @ColorRes
        private int color = R.color.black;

        public Builder withTypedRadius(ShowCaseRadius radius) {
            this.radius = radius;
            return this;
        }

        public Builder withTypedPosition(ShowCasePosition position) {
            this.position = position;
            return this;
        }

        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder withColor(@ColorRes int overlayColor) {
            color = overlayColor;
            return this;
        }

        public ShowCaseStep build(Context context) {
            checkRequiredFields();

            ShowCaseStep step = new ShowCaseStep();
            if (radius == null) {
                radius = new Radius(
                    ViewUtils.convertDpToPx(context, DEFAULT_RADIUS_DP)
                );
            }
            step.position = position;
            step.radius = radius;
            step.message = message;
            step.color = color;

            return step;
        }

        private void checkRequiredFields() {
            if (position == null) {
                throw new IllegalArgumentException(
                    "position is required field for ShowCaseStep builder"
                );
            }

            if (message == null) {
                throw new IllegalArgumentException(
                    "message is required field for ShowCaseStep builder"
                );
            }
        }
    }
}
