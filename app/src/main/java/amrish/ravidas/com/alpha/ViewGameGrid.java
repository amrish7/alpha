package amrish.ravidas.com.alpha;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.Nullable;

import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class ViewGameGrid extends View {
    private Paint mPaint, mPaintOnClickAnimation;
    private int mOnTouchColor = getResources().getColor(R.color.colorWhite);
    private ValueAnimator mGridLinesAnimator, mOnClickAnimator;
    private float mGridLineFraction, mOnClickFraction = 0.0f;
    private int mCanvasWidth = 0;
    private int mCanvasHeight = 0;
    private float[] mLastTouchDownXY;
    private int onClickAnimatePosition;
    private final float[] mRangeX = new float[3];
    private final float[] mRangeY = new float[3];
    private Path mPathRoundedRect, mPathClickAnimation, mPath;
    private Path[] mPathBlocks = new Path[9];

    private final ValueAnimator.AnimatorUpdateListener mGridLinesAnimatorListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            mGridLineFraction = animation.getAnimatedFraction();
            invalidate();
        }
    };

    private final ValueAnimator.AnimatorUpdateListener mOnClickAnimationListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            mOnClickFraction = animation.getAnimatedFraction();
            invalidate();
        }
    };

    public ViewGameGrid(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupPaint();
        startAnimatorForGridLines();
        mPathClickAnimation = new Path();
        mPath = new Path();
    }

    private void startAnimatorForGridLines() {
        mGridLinesAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        mGridLinesAnimator.setInterpolator(new DecelerateInterpolator(2.5f));
        mGridLinesAnimator.setDuration(2000);
        mGridLinesAnimator.addUpdateListener(mGridLinesAnimatorListener);
        mGridLinesAnimator.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        mCanvasWidth = w;
        mCanvasHeight = h;
        mRangeX[0] = w / 3;
        mRangeX[1] = w * 2/3;
        mRangeX[2] = w;
        mRangeY[0] = h / 3;
        mRangeY[1] = h * 2/3;
        mRangeY[2] = h;
        final RectF rectF = new RectF(0, mCanvasHeight, mCanvasWidth, 0);
        mPathRoundedRect = new Path();
        mPathRoundedRect.addRoundRect(rectF, 100, 100, Path.Direction.CW);
        for (int i=0; i<mPathBlocks.length; i++) {
            mPathBlocks[i] = new Path();
        }
        RectF rect = new RectF(0, mRangeY[0], mRangeX[0], 0);
        mPathBlocks[0].addRoundRect(rect, 0, 0, Path.Direction.CW);
        mPathBlocks[0].op(mPathRoundedRect, Path.Op.INTERSECT);
        mPathBlocks[1].addRoundRect(new RectF(mRangeX[0], mRangeY[0], mRangeX[1], 0), 0, 0, Path.Direction.CW);
        mPathBlocks[2].addRoundRect(new RectF(mRangeX[1], mRangeY[0], mRangeX[2], 0), 0, 0, Path.Direction.CW);
        mPathBlocks[2].op(mPathRoundedRect, Path.Op.INTERSECT);
        mPathBlocks[3].addRoundRect(new RectF(0, mRangeY[1], mRangeX[0], mRangeY[0]), 0, 0, Path.Direction.CW);
        mPathBlocks[4].addRoundRect(new RectF(mRangeX[0], mRangeY[1], mRangeX[1], mRangeY[0]), 0, 0, Path.Direction.CW);
        mPathBlocks[5].addRoundRect(new RectF(mRangeX[1], mRangeY[1], mRangeX[2], mRangeY[0]), 0, 0, Path.Direction.CW);
        mPathBlocks[6].addRoundRect(new RectF(0, mRangeY[2], mRangeX[0], mRangeY[1]), 0, 0, Path.Direction.CW);
        mPathBlocks[6].op(mPathRoundedRect, Path.Op.INTERSECT);
        mPathBlocks[7].addRoundRect(new RectF(mRangeX[0], mRangeY[2], mRangeX[1], mRangeY[1]), 0, 0, Path.Direction.CW);
        mPathBlocks[8].addRoundRect(new RectF(mRangeX[1], mRangeY[2], mRangeX[2], mRangeY[1]), 0, 0, Path.Direction.CW);
        mPathBlocks[8].op(mPathRoundedRect, Path.Op.INTERSECT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPath.reset();
        if (mOnClickFraction > 0 && mOnClickFraction < 1) {
            mPathClickAnimation.reset();
            mPathClickAnimation.addCircle(mLastTouchDownXY[0], mLastTouchDownXY[1], mCanvasWidth * mOnClickFraction/2, Path.Direction.CW);
            mPathClickAnimation.op(mPathBlocks[onClickAnimatePosition], Path.Op.INTERSECT);
            canvas.drawPath(mPathClickAnimation, mPaintOnClickAnimation);
        }

        PathUtils.drawHorizontalLine(canvas, mPath, mPaint, mCanvasWidth / 2, mCanvasHeight / 3, mCanvasWidth * mGridLineFraction);
        PathUtils.drawHorizontalLine(canvas, mPath, mPaint, mCanvasWidth / 2, mCanvasHeight * 2 / 3, mCanvasWidth * mGridLineFraction);
        PathUtils.drawVerticalLine(canvas, mPath, mPaint, mCanvasWidth / 3, mCanvasHeight / 2, mCanvasHeight * mGridLineFraction);
        PathUtils.drawVerticalLine(canvas, mPath, mPaint, mCanvasWidth * 2/ 3, mCanvasHeight / 2, mCanvasHeight * mGridLineFraction);
        canvas.drawPath(mPathRoundedRect, mPaint);
    }

    // Setup mPaint with color and stroke styles
    private void setupPaint() {
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#FFDCDADB")); //EE82EE
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mPaintOnClickAnimation = new Paint();
        mPaintOnClickAnimation.setColor(mOnTouchColor);
        mPaintOnClickAnimation.setAlpha(75);
        mPaintOnClickAnimation.setAntiAlias(true);
        mPaintOnClickAnimation.setStrokeWidth(6);
        mPaintOnClickAnimation.setStyle(Paint.Style.FILL);
        mPaintOnClickAnimation.setStrokeJoin(Paint.Join.ROUND);
        mPaintOnClickAnimation.setStrokeCap(Paint.Cap.ROUND);
    }

    public void startClickAnimation(int position, float[] lastTouchDownXY) {
        mLastTouchDownXY = lastTouchDownXY;
        onClickAnimatePosition = position;
        mOnClickAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        mOnClickAnimator.setInterpolator(new DecelerateInterpolator(1.5f));
        mOnClickAnimator.setDuration(500);
        mOnClickAnimator.addUpdateListener(mOnClickAnimationListener);
        mOnClickAnimator.start();
    }

    /**
     * Given a position in the grid which is virtually divided into 9 section (nonant). This function
     * returns the index of the section in which the point would belong to. The index starts from top
     * left corner and increments in steps. For e.g. Position 0x0 is 0, 1x0 is 3, 2x2 is 8
     */
    public int getNonantPosition(float x, float y) {
        int row = -1, col = -1, i = 0, j = 0;
        while (i < 3) {
            if (x < mRangeX[i]) {
                col = i;
                break;
            }
            i++;
        }
        while (j < 3) {
            if (y < mRangeY[j]) {
                row = j;
                break;
            }
            j++;
        }
        return (row >= 0 && col >= 0) ? row * 3 + col : -1;
    }
}
