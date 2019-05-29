
package cn.com.caoyue.tinynote.vest.pafacedetector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import cn.com.caoyue.tinynote.vest.utils.Constants;


public class CircleProgressBar extends View {

    private static final int STD_WIDTH = 20;
    private static final int STD_RADIUS = 75;

    private Context mContext;
    private final TextPaint textPaint;
    SweepGradient sweepGradient = null;
    private int progress = 300;
    private int max = 300;
    private Paint paint;
    private RectF oval;
    private int mWidth = STD_WIDTH;
    private int mRadius = STD_RADIUS;
    private Bitmap bit;

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        paint = new Paint();
        oval = new RectF();
        textPaint = new TextPaint();
        sweepGradient = new SweepGradient(getWidth() / 2, getHeight() / 2, new int[]{0xfffe9a8e, 0xff3fd1e4
                , 0xffdc968e}, null);
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int use = width > height ? height : width;
        int sum = STD_WIDTH + STD_RADIUS;
        try {
            mWidth = (STD_WIDTH * use) / (2 * sum);
            mRadius = (STD_RADIUS * use) / (2 * sum);
        } catch (Exception e) {
            mWidth = 1;
            mRadius = 1;
        }
        setMeasuredDimension(use, use);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setAntiAlias(true);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0xff000000);
        paint.setStrokeWidth(mWidth);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(mWidth + mRadius, mWidth + mRadius, mRadius, paint);
        paint.setColor(Color.parseColor(Constants.LIVENESS_COLOR_PRIMARY));
        oval.set(mWidth, mWidth, mRadius * 2 + mWidth, (mRadius * 2 + mWidth));
        canvas.drawArc(oval, -90, ((float) progress / max) * 360, false, paint);
        paint.reset();

    }
}
