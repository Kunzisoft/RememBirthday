package com.kunzisoft.remembirthday.animation;

import android.animation.Animator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import io.codetail.animation.ViewAnimationUtils;

/**
 * Builder class for make generic circle animation on view
 */
public class AnimationCircle {

    private View view;
    private int startPointX;
    private int startPointY;
    private int duration;
    private float radius;
    private Interpolator interpolator;

    private AnimationCircle(View view, int startPointX, int startPointY) {
        this.view = view;

        this.startPointX = startPointX;
        this.startPointY = startPointY;

        // get the final radius for the clipping circle
        this.duration = 320;
        int dx = Math.max(startPointX, view.getWidth() - startPointX);
        int dy = Math.max(startPointY, view.getHeight() - startPointY);
        this.radius = (float) Math.hypot(dx, dy);
        this.interpolator = new AccelerateDecelerateInterpolator();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide();
            }
        });
    }

    public AnimationCircle animate() {
        if(view.getVisibility() == View.VISIBLE) {
            hide();
        } else {
            show();
        }
        return this;
    }

    public void hide() {
        // Android native animator
        Animator animatorRevealMenu =
                ViewAnimationUtils.createCircularReveal(view, startPointX, startPointY, radius, 0);
        animatorRevealMenu.setInterpolator(interpolator);
        animatorRevealMenu.setDuration(duration);
        animatorRevealMenu.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
        animatorRevealMenu.start();
    }

    public void show() {
        // Android native animator
        Animator animatorRevealMenu =
                ViewAnimationUtils.createCircularReveal(view, startPointX, startPointY, 0, radius);
        animatorRevealMenu.setInterpolator(interpolator);
        animatorRevealMenu.setDuration(duration);
        animatorRevealMenu.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {}

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
        animatorRevealMenu.start();
    }

    public static AnimationCircle build(View view, int startPointX, int startPointY) {
        return new AnimationCircle(view, startPointX, startPointY);
    }

    public AnimationCircle duration(int duration) {
        this.duration = duration;
        return this;
    }

    public AnimationCircle radius(int radius) {
        this.radius = radius;
        return this;
    }

    public AnimationCircle interpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
        return this;
    }
}
