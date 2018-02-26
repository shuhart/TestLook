package com.shuhart.testlook.widget

import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.os.Parcelable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import com.shuhart.testlook.R

/**
 * Progress bar with some nice animation provided by several interpolators
 * under the hood.
 * For the sake of simplicity some values are hard coded.
 */
class SmoothProgressBar : ProgressBar {

    private var progressAnimatorSet: AnimatorSet? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,
            android.R.style.Widget_DeviceDefault_ProgressBar_Horizontal)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        max = 1000
        progressDrawable = ContextCompat.getDrawable(context, R.drawable.progress_bar_drawable)

        if (isInEditMode) {
            progress = 300
            return
        }

        progress = 0
        visibility = View.INVISIBLE
    }

    fun smoothStart() {
        if (progressAnimatorSet?.isRunning == true) {
            progressAnimatorSet?.cancel()
        }

        visibility = View.VISIBLE

        progressAnimatorSet = AnimatorSet()

        val updateListener = ValueAnimator.AnimatorUpdateListener { animation ->
            Log.d(javaClass.simpleName, "Progress=${animation.animatedValue as Int}")
            progress = animation.animatedValue as Int
        }

        val firstSectionAnimator = ValueAnimator.ofInt(0, 500)
        firstSectionAnimator.duration = 5000
        firstSectionAnimator.interpolator = DecelerateInterpolator()
        firstSectionAnimator.addUpdateListener(updateListener)

        val secondSectionAnimator = ValueAnimator.ofInt(501, 750)
        secondSectionAnimator.duration = 15000
        secondSectionAnimator.interpolator = LinearInterpolator()
        secondSectionAnimator.addUpdateListener(updateListener)

        val thirdSectionAnimator = ValueAnimator.ofInt(751, 1000)
        thirdSectionAnimator.duration = 25000
        thirdSectionAnimator.interpolator = LinearInterpolator()
        thirdSectionAnimator.addUpdateListener(updateListener)

        progressAnimatorSet?.playSequentially(firstSectionAnimator,
                secondSectionAnimator, thirdSectionAnimator)
        progressAnimatorSet?.start()
    }

    fun smoothStop(animatorListener: AnimatorListenerAdapter? = null) {
        if (progressAnimatorSet != null && progressAnimatorSet!!.isRunning) {
            progressAnimatorSet!!.cancel()
        }

        if (progress == 1000) {
            visibility = View.INVISIBLE
            animatorListener?.onAnimationEnd(ValueAnimator())
            return
        }

        if (visibility == View.INVISIBLE) {
            animatorListener?.onAnimationEnd(ValueAnimator())
            return
        }

        val finishAnimator = ValueAnimator.ofInt(progress, 1000)
        finishAnimator.duration = 300
        finishAnimator.interpolator = AccelerateInterpolator()
        if (animatorListener != null) {
            finishAnimator.addListener(animatorListener)
        }
        finishAnimator.addUpdateListener { animation ->
            progress = animation.animatedValue as Int
        }
        finishAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator?) {
                visibility = View.INVISIBLE
                progress = 0
            }
        })
        finishAnimator.start()
    }

    fun stop() {
        progressAnimatorSet?.apply {
            if (isRunning) cancel()
        }
        progress = 0
        visibility = View.INVISIBLE
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        progress = 0
    }
}