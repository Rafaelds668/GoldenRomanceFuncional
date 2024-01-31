package com.example.goldenromance.implementaciones.cardStackView.internal;

import android.view.animation.Interpolator;

import com.example.goldenromance.implementaciones.cardStackView.Direction;


public interface AnimationSetting {
    Direction getDirection();
    int getDuration();
    Interpolator getInterpolator();
}
