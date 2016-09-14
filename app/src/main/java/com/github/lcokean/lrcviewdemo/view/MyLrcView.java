package com.github.lcokean.lrcviewdemo.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pengj
 * @version 4.3 on 2016/9/12
 */
public class MyLrcView extends View {

    private float mLrcPlayRate; // 歌词当前比例

    private int mVerOffset; // 歌词行间间距
    private int mLineOffset; // 行偏移（行切换动画用）

    private Paint mTextPaint;
    private int mPlayTextColor;
    private int mUnPlayTextColor;
    private LinearGradient mLrcGradient;

    private int mTextSize; // 歌词文字大小
    private int mTextHeight; // 字体高度

    private int mWidth, mHeight;

    private ArrayList<LrcRow> mLrcList; // 歌词
    private long mCurrTime; // 当前时间
    private int mCurrLrc; // 当前歌词
    private int mMaxDisplayLrc; // 最多显示歌词行数(上下各多少行，0仅显示当前歌词)

    private boolean isPlaying = false;

    private ValueAnimator mLrcAnimator;
    private ValueAnimator mLineAnimator;


    public MyLrcView(Context context) {
        this(context, null);
    }

    public MyLrcView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyLrcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);

        mPlayTextColor = Color.GREEN;
        mUnPlayTextColor = Color.GRAY;

        mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics());
        mTextPaint.setTextSize(mTextSize);
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        mTextHeight = (int) Math.ceil(fm.descent - fm.ascent);

        mVerOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        mLrcList = new ArrayList<>();

        mMaxDisplayLrc = Integer.MAX_VALUE;
    }

    public void setLrc(List<String> lrcs) {
        mLrcList.clear();
        long start = 0;
        for (String lrc : lrcs) {
            mLrcList.add(new LrcRow(lrc, start, start + 5000));
            start += 5000;
        }
    }

    /**
     * 开始
     */
    public void start() {
        isPlaying = true;
        invalidate();
    }

    /**
     * 暂停
     *
     * @param time
     */
    public void stop(long time) {
        isPlaying = false;
        mCurrTime = time;
        invalidate();
    }

    /**
     * 重置
     */
    public void reset() {
        isPlaying = false;
        mCurrTime = 0;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);

        int width, height;
        if (wMode == MeasureSpec.EXACTLY) {
            width = wSize;
        } else {
            width = getResources().getDisplayMetrics().widthPixels;
            if (wMode == MeasureSpec.AT_MOST) {
                width = Math.min(width, wSize);
            }
        }

        if (hMode == MeasureSpec.EXACTLY) {
            height = hSize;
        } else {
            height = getResources().getDisplayMetrics().heightPixels;
            if (hMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, hSize);
            }
        }

        setMeasuredDimension(width, height);

        mWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        mHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 中间线
        mTextPaint.setColor(Color.GREEN);
        canvas.drawLine(0, mHeight / 2.0f + getPaddingTop(),
                mWidth + getPaddingLeft() + getPaddingRight(),
                (mHeight + getPaddingTop() + getPaddingBottom()) / 2.0f, mTextPaint);
        // Padding 线
        mTextPaint.setColor(Color.RED);
        canvas.drawLine(0, getPaddingTop(), mWidth + getPaddingLeft() + getPaddingRight(), getPaddingTop(), mTextPaint);
        canvas.drawLine(0, getPaddingTop() + mHeight, mWidth + getPaddingLeft() + getPaddingRight(), getPaddingTop() + mHeight, mTextPaint);
        canvas.drawLine(getPaddingLeft(), 0, getPaddingLeft(), mHeight + getPaddingTop() + getPaddingBottom(), mTextPaint);
        canvas.drawLine(getPaddingLeft() + mWidth, 0, getPaddingLeft() + mWidth, mHeight + getPaddingTop() + getPaddingBottom(), mTextPaint);
        // 歌词为空
        if (mLrcList == null || mLrcList.isEmpty()) return;
        // 画中间歌词
        onDrawCenterLrc(canvas, mLrcList.get(mCurrLrc));
        // 画上下歌词
        int max = Math.min(mMaxDisplayLrc, (int) ((mHeight - mTextHeight) / 2.0f / (mTextHeight + mVerOffset)));
        for (int i = 1; i <= max; i++) {
            if (mCurrLrc + i < mLrcList.size()) {
                onDrawSideLrc(canvas, i, mLrcList.get(mCurrLrc + i));
            }
            if (mCurrLrc - i >= 0) {
                onDrawSideLrc(canvas, -i, mLrcList.get(mCurrLrc - i));
            }
        }
    }

    /**
     * 画中间歌词
     *
     * @param canvas
     * @param lrc    歌词内容
     */
    private void onDrawCenterLrc(Canvas canvas, LrcRow lrc) {
        String text = lrc.content;
        int textWidth = (int) mTextPaint.measureText(text);
        int offset = 0;  // 文字偏移
        if (textWidth > mWidth && mLrcPlayRate > (mWidth / 2.0f / textWidth)) {
            // mWidth / 2.0f / textWidth = 屏幕正中间对应的歌词比例
            offset = (int) (textWidth * (mLrcPlayRate - mWidth / 2.0f / textWidth));
            offset = Math.min(offset, textWidth - mWidth);
        }

        int start = (int) (mWidth / 2.0f - textWidth / 2.0f + getPaddingLeft());
        start = Math.max(start, getPaddingLeft());

        // 限定文字显示区域
        RectF f = new RectF(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + mWidth, getPaddingTop() + mHeight);
        canvas.drawRect(f, mTextPaint);
        mTextPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        mLrcGradient = new LinearGradient(start - offset, 0,
                start - offset + textWidth, 0,
                new int[]{mPlayTextColor, mUnPlayTextColor},
                new float[]{mLrcPlayRate, mLrcPlayRate},
                Shader.TileMode.CLAMP);
        mTextPaint.setShader(mLrcGradient);
        canvas.drawText(text, start - offset,
                getPaddingTop() + mHeight / 2.0f + mTextHeight / 2.0f + mLineOffset, mTextPaint);

        // 播放动画
        if (isPlaying && mCurrTime == lrc.timeStart) {
            mCurrTime++;
            startLrcAnim(textWidth, lrc.timeEnd - lrc.timeStart);
        }
        mTextPaint.setShader(null);
        mTextPaint.setXfermode(null);
    }

    /**
     * 画上下歌词
     *
     * @param canvas
     * @param line   现对于中间行的行数（上方 < 0,下方 > 0）
     * @param lrc    歌词
     */
    private void onDrawSideLrc(Canvas canvas, int line, LrcRow lrc) {
        mTextPaint.setColor(mUnPlayTextColor);
        String text = lrc.content;
        int textWidth = (int) mTextPaint.measureText(text);
        if (textWidth > mWidth) {
            int l = (int) (mWidth / (textWidth * 1.0f / text.length()));
            text = text.substring(0, l);
            textWidth = mWidth;
        }
        float height = mHeight / 2.0f + mTextHeight / 2.0f + getPaddingTop() + line * mTextHeight + line * mVerOffset;
        canvas.drawText(text, mWidth / 2.0f - textWidth / 2.0f + getPaddingLeft(),
                height + mLineOffset, mTextPaint);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void startLrcAnim(final int width, final long duration) {
        mLrcAnimator = ValueAnimator.ofInt(0, width);
        mLrcAnimator.setDuration(duration);
        mLrcAnimator.setInterpolator(new LinearInterpolator());
        mLrcAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mCurrTime = mLrcList.get(mCurrLrc).timeEnd;
                if (++mCurrLrc > mLrcList.size() - 1) {
                    mCurrLrc = mLrcList.size() - 1;
                    isPlaying = false;
                } else {
                    mLrcPlayRate = 0;
                    startLineAnim();
                }
                if (mLineAnimator != null && !mLineAnimator.isRunning()) {
                    invalidate();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        mLrcAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mLrcPlayRate = (int) valueAnimator.getAnimatedValue() / (width * 1.0f);
                invalidate();
            }
        });
        mLrcAnimator.start();
    }

    /**
     * 行动画
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void startLineAnim() {
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        int textHeight = (int) Math.ceil(fm.descent - fm.ascent);
        int height = textHeight + mVerOffset;
        mLineAnimator = ValueAnimator.ofInt(height, 0);
        mLineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mLineOffset = (int) valueAnimator.getAnimatedValue();
                if (mLrcAnimator != null && !mLrcAnimator.isRunning()) {
                    invalidate();
                }
            }
        });
        mLineAnimator.start();
    }

    /**
     * 单句歌词
     */
    class LrcRow {
        protected String content;
        protected long timeStart;
        protected long timeEnd;

        public LrcRow(String text, long start, long end) {
            this.content = text;
            this.timeStart = start;
            this.timeEnd = end;
        }

    }

}
