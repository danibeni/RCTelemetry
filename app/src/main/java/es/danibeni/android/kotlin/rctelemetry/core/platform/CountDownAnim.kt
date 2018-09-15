package es.danibeni.android.kotlin.rctelemetry.core.platform

import android.os.Handler
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.TextView

/**
 * Defines a count down animation to be shown on a [TextView].
 *
 * @author Ivan Ridao Freitas
 */
class CountDownAnimation(private val mTextView: TextView, var startCount: Int) {
    private var mAnimation: Animation? = null
    private var mCurrentCount: Int = 0
    private var mListener: CountDownListener? = null

    private val mHandler = Handler()

    private val mCountDown = Runnable {
        if (mCurrentCount > 0) {
            mTextView.text = mCurrentCount.toString()
            mTextView.startAnimation(mAnimation)
            if (mListener != null) {
                mListener!!.onCountDownTick(mCurrentCount)
            }
            mCurrentCount--
        } else {
            mTextView.visibility = View.GONE
            if (mListener != null) {
                mListener!!.onCountDownEnd(this@CountDownAnimation)
                mListener!!.onCountDownTick(mCurrentCount)
            }
        }
    }

    /**
     * Returns the animation used during the count down.
     */
    /**
     * Sets the animation used during the count down. If the duration of the
     * animation for each number is not set, one second will be defined.
     */
    var animation: Animation?
        get() = mAnimation
        set(animation) {
            this.mAnimation = animation
            if (mAnimation!!.duration == 0L)
                mAnimation!!.duration = 1000
        }

    init {

        mAnimation = AlphaAnimation(1.0f, 0.0f)
        mAnimation!!.duration = 1000
    }

    /**
     * Starts the count down animation.
     */
    fun start() {
        mHandler.removeCallbacks(mCountDown)

        mTextView.text = startCount.toString() + ""
        mTextView.visibility = View.VISIBLE

        mCurrentCount = startCount

        mHandler.post(mCountDown)
        for (i in 1..startCount) {
            mHandler.postDelayed(mCountDown, (i * 1000).toLong())
        }
    }

    /**
     * Cancels the count down animation.
     */
    fun cancel() {
        mHandler.removeCallbacks(mCountDown)

        mTextView.text = ""
        mTextView.visibility = View.GONE
    }

    /**
     * Binds a listener to this count down animation. The count down listener is
     * notified of events such as the end of the animation.
     *
     * @param listener
     * The count down listener to be notified
     */
    fun setCountDownListener(listener: CountDownListener) {
        mListener = listener
    }

    /**
     * A count down listener receives notifications from a count down animation.
     * Notifications indicate count down animation related events, such as the
     * end of the animation.
     */
    interface CountDownListener {
        /**
         * Notifies the end of the count down animation.
         *
         * @param animation
         * The count down animation which reached its end.
         */
        fun onCountDownEnd(animation: CountDownAnimation)

        fun onCountDownTick(count: Int)
    }
}