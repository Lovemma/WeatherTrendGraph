package xyz.lovemma.weathertrendgraph;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OO on 2017/4/17.
 */

public class WeatherTrendGraph extends View {

    private int mMargin;  //  10dp的间距
    private int mWidth; //  控件宽度
    private int mHeight;  //  控件高度
    private int max, min;  //  最大值、最小值
    private float yInterval;  //  y轴坐标间隔
    private float xInterval;  //  x轴坐标间隔


    private int mLineColor;  //  折线颜色
    private float mLineWidth; //  折线宽度
    private int mAxesColor; //  坐标轴颜色
    private float mAxesWidth; //  坐标轴宽度
    private float mCircleRadius;    //坐标点半径
    private int mCircleColor;  //  坐标点颜色
    private int mShaderColor;  //  坐标点颜色
    private float mTmpTextSize;  // 文字大小

    private Paint mLinePaint;   //折线画笔
    private Path mLinePath;     //折线路径
    private Paint mAxesPaint;   //分隔线画笔
    private Paint mCirclePaint; //坐标点画笔
    private Paint mPaintShader; //阴影画笔
    private TextPaint mTextPaint;   //文字画笔

    private List<Weather> mWeathers;

    public void setWeathers(List<Weather> weathers) {
        mWeathers = weathers;
        invalidate();
    }


    public WeatherTrendGraph(Context context) {
        super(context);
    }

    public WeatherTrendGraph(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeatherTrendGraph(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initStyle(context, attrs);
        mMargin = DensityUtils.dp2px(context, 20);
        mWeathers = new ArrayList<>();

        initPaint();
    }

    private void initStyle(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WeatherTrendGraph);
        mLineColor = typedArray.getColor(R.styleable.WeatherTrendGraph_line_color, Color.BLACK);
        mLineWidth = typedArray.getDimensionPixelSize(R.styleable.WeatherTrendGraph_line_width, 2);
        mAxesColor = typedArray.getColor(R.styleable.WeatherTrendGraph_axes_color, Color.BLACK);
        mAxesWidth = typedArray.getDimensionPixelSize(R.styleable.WeatherTrendGraph_axes_width, 1);
        mCircleRadius = typedArray.getDimensionPixelSize(R.styleable.WeatherTrendGraph_circle_radius, 12);
        mCircleColor = typedArray.getColor(R.styleable.WeatherTrendGraph_circle_color, Color.GRAY);
        mShaderColor = typedArray.getColor(R.styleable.WeatherTrendGraph_shadow_color, Color.GRAY);
        mTmpTextSize = typedArray.getDimension(R.styleable.WeatherTrendGraph_tmp_text_size, 35);
        typedArray.recycle();
    }

    private void initPaint() {
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(mLineWidth);
        mLinePaint.setColor(mLineColor);
        mLinePath = new Path();

        mAxesPaint = new Paint();
        mAxesPaint.setStrokeWidth(mAxesWidth);
        mAxesPaint.setColor(mAxesColor);

        mCirclePaint = new Paint();
        mCirclePaint.setColor(mCircleColor);

        mPaintShader = new Paint();
        mPaintShader.setColor(mShaderColor);
        mPaintShader.setAntiAlias(true);
        mPaintShader.setStrokeWidth(2f);

        mTextPaint = new TextPaint();
        mTextPaint.setTextSize(mTmpTextSize);
        mTextPaint.setColor(Color.WHITE);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mWidth = getWidth();
            mHeight = getHeight() - mMargin * 2 - DensityUtils.sp2px(getContext(), mTmpTextSize);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mWeathers == null || mWeathers.size() == 0) {
            return;
        }
        max = getMaxTmp();
        min = getMinTmp();
        //每个像素占的比率
        float ratio = (float) mHeight / (max - min);
        xInterval = (float) mWidth / (mWeathers.size() - 2);
        float X, Y;
        float Y1;
        String tmp;
        //记录高温折线路径
        for (int i = 1; i < mWeathers.size() - 1; i++) {
            tmp = mWeathers.get(i).getHighTmp() + "°";
            X = (i - 1) * xInterval + xInterval / 2;
            Y = (max - mWeathers.get(i).getHighTmp()) * ratio + mMargin + DensityUtils.sp2px(getContext(), mTmpTextSize);
            if (i == 1) {
                Y1 = ((max - mWeathers.get(0).getHighTmp()) * ratio + mMargin + Y) / 2;
                mLinePath.moveTo(0, Y1);
            }
            mLinePath.lineTo(X, Y);
            if (i == mWeathers.size() - 2) {
                Y1 = ((max - mWeathers.get(i + 1).getHighTmp()) * ratio + mMargin + Y) / 2;
                mLinePath.lineTo(getWidth(), Y1);
            }
            //绘制每一个温度点
            canvas.drawCircle(X, Y, mCircleRadius, mCirclePaint);
            //绘制分隔线
            canvas.drawLine(X + xInterval / 2, 0,
                    X + xInterval / 2, getHeight(),
                    mAxesPaint);
            //绘制温度
            canvas.drawText(tmp, X - mTextPaint.measureText(tmp) / 2, Y - 2 * mCircleRadius, mTextPaint);
            //绘制时间
            canvas.drawText(mWeathers.get(i).getWeek(),
                    X - mTextPaint.measureText(mWeathers.get(i).getWeek()) / 2,
                    DensityUtils.sp2px(getContext(), mTmpTextSize),
                    mTextPaint);
            //绘制天气所对应的图片
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sunny);
            switch (mWeathers.get(i).getCond()) {
                case "晴":
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sunny);
                    break;
                case "阴":
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.overcast);
                    break;
                case "雨":
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rain);
                    break;
                case "多云":
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cloudy);
                    break;
            }
            canvas.drawBitmap(bitmap, X - bitmap.getWidth() / 2, 0, mCirclePaint);
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }

        //记录低温折线路径
        for (int i = mWeathers.size() - 2; i > 0; i--) {
            tmp = mWeathers.get(i).getLowTmp() + "°";
            X = (i - 1) * xInterval + xInterval / 2;
            Y = (max - mWeathers.get(i).getLowTmp()) * ratio + mMargin + DensityUtils.sp2px(getContext(), mTmpTextSize);
            if (i == mWeathers.size() - 2) {
                Y1 = ((max - mWeathers.get(i + 1).getLowTmp()) * ratio + mMargin + Y) / 2;
                mLinePath.lineTo(getWidth(), Y1);
            }
            mLinePath.lineTo(X, Y);
            if (i == 1) {
                Y1 = ((max - mWeathers.get(0).getLowTmp()) * ratio + mMargin + Y) / 2;
                mLinePath.lineTo(0, Y1);
                mLinePath.lineTo(0, 0);
                mLinePath.close();
            }
            canvas.drawCircle(X, Y, mCircleRadius, mCirclePaint);
            canvas.drawText(tmp,
                    X - mTextPaint.measureText(tmp) / 2,
                    Y + 4 * mCircleRadius,
                    mTextPaint);
        }

        canvas.drawPath(mLinePath, mPaintShader);
    }

    private int getMaxTmp() {
        int max = mWeathers.get(0).getHighTmp();
        for (int i = 0; i < mWeathers.size(); i++) {
            if (max < mWeathers.get(i).getHighTmp()) {
                max = mWeathers.get(i).getHighTmp();
            }
        }
        return max;
    }

    private int getMinTmp() {
        int min = mWeathers.get(0).getLowTmp();
        for (int i = 0; i < mWeathers.size(); i++) {
            if (min > mWeathers.get(i).getLowTmp()) {
                min = mWeathers.get(i).getLowTmp();
            }
        }
        return min;
    }
}
