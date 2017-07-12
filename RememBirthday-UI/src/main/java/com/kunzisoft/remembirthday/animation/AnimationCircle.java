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
    private boolean customRadius;
    private float radius;
    private Interpolator interpolator;
    private Animator.AnimatorListener animatorListener;

    private AnimationCircle(View view) {
        this.view = view;

        customRadius = false;
        startPoint(0, 0);
        duration(320);
        interpolator(new AccelerateDecelerateInterpolator());

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
            public void onAnimationStart(Animator animator) {
                if(animatorListener != null)
                    animatorListener.onAnimationStart(animator);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setVisibility(View.GONE);
                if(animatorListener != null)
                    animatorListener.onAnimationEnd(animator);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                if(animatorListener != null)
                    animatorListener.onAnimationCancel(animator);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                if(animatorListener != null)
                    animatorListener.onAnimationRepeat(animator);
            }
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
                if(animatorListener != null)
                    animatorListener.onAnimationStart(animator);
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if(animatorListener != null)
                    animatorListener.onAnimationEnd(animator);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                if(animatorListener != null)
                    animatorListener.onAnimationCancel(animator);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                if(animatorListener != null)
                    animatorListener.onAnimationRepeat(animator);
            }
        });
        animatorRevealMenu.start();
    }

    public static AnimationCircle build(View view) {
        return new AnimationCircle(view);
    }

    public AnimationCircle startPoint(int startPointX, int startPointY) {
        this.startPointX = startPointX;
        this.startPointY = startPointY;
        if(!customRadius) {
            int dx = Math.max(startPointX, view.getWidth() - startPointX);
            int dy = Math.max(startPointY, view.getHeight() - startPointY);
            this.radius = (float) Math.hypot(dx, dy);
        }
        return this;
    }

    public AnimationCircle duration(int duration) {
        this.duration = duration;
        return this;
    }

    public AnimationCircle radius(float radius) {
        this.customRadius = true;
        this.radius = radius;
        return this;
    }

    public AnimationCircle interpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
        return this;
    }

    public AnimationCircle setAnimatorListener(Animator.AnimatorListener animatorListener) {
        this.animatorListener = animatorListener;
        return this;
    }
}
