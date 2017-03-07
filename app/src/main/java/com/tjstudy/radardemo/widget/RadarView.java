package com.tjstudy.radardemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.tjstudy.radardemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 雷达图
 * Created by tjstudy on 2017/3/7.
 */
public class RadarView extends View {

    private Paint basePaint;
    private int height;
    private int width;
    private List<Radar> data;
    private int baseLineNums;
    private Paint coverPaint;
    private int maxValue;
    private Paint mTxtPaint;
    private int mBaseLineColor;
    private int mCoverColor;
    private int mTxtColor;
    private int mTxtSize;
    private int mCoverPointColor;
    private int mCoverAlpha;
    private TypedArray typedArray;

    public RadarView(Context context) {
        this(context, null);
    }

    public RadarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RadarView, defStyleAttr, 0);
        initData();
        initPaint();
        typedArray.recycle();
    }

    private void initPaint() {
        //基线画笔
        basePaint = new Paint();
        basePaint.setColor(mBaseLineColor);
        basePaint.setStyle(Paint.Style.STROKE);
        basePaint.setStrokeWidth(4);
        basePaint.setAntiAlias(true);

        coverPaint = new Paint();
        coverPaint.setAntiAlias(true);
        coverPaint.setColor(mCoverColor);
        coverPaint.setStrokeWidth(4);
        coverPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mTxtPaint = new Paint();
        mTxtPaint.setAntiAlias(true);
        mTxtPaint.setColor(mTxtColor);
        mTxtPaint.setTextSize(mTxtSize);
    }

    private void initData() {
        //模拟数据
        data = new ArrayList<>();
        Radar radar1 = new Radar("语文", 20);
        Radar radar2 = new Radar("数学", 40);
        Radar radar3 = new Radar("英语", 80);
        Radar radar4 = new Radar("化学", 50);
        Radar radar5 = new Radar("物理", 100);
        Radar radar6 = new Radar("生物", 60);

        data.add(radar1);
        data.add(radar2);
        data.add(radar3);
        data.add(radar4);
        data.add(radar5);
        data.add(radar6);

        baseLineNums = typedArray.getInteger(R.styleable.RadarView_baseLineNums, 5);
        maxValue = typedArray.getInteger(R.styleable.RadarView_maxValue, 100);
        mBaseLineColor = typedArray.getColor(R.styleable.RadarView_mBaseLineColor, 0xFFE1E1E1);
        mCoverColor = typedArray.getColor(R.styleable.RadarView_mCoverColor, 0xFF9400D3);
        mTxtColor = typedArray.getColor(R.styleable.RadarView_mTxtColor, 0xFFE1E1E1);
        mTxtSize = (int) typedArray.getDimension(R.styleable.RadarView_mTxtSize, 30);
        mCoverPointColor = typedArray.getColor(R.styleable.RadarView_mCoverPointColor, 0xFF8A2BE2);
        mCoverAlpha = typedArray.getInteger(R.styleable.RadarView_mCoverAlpha, 127);
    }

    public List<Radar> getData() {
        return data;
    }

    public void setData(List<Radar> data) {
        this.data = data;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST) {
            width = 600;
        } else {
            width = MeasureSpec.getSize(widthMeasureSpec);
        }
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST) {
            height = 600;
        } else {
            height = MeasureSpec.getSize(heightMeasureSpec);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //1、将画布移到控件的中心
        canvas.translate(width / 2, height / 2);

        //2、计算最外层的那几个点
        float radius = width / 2 - getTxtMaxWidth() - 4;//微调4
        float eachDegree = 360 * 1.0f / data.size();
        float gapValue = radius / baseLineNums;

        //3、画底层 正多边形 --path
        Path path = new Path();
        for (int j = 0; j < baseLineNums; j++) {//循环多次 画多个
            for (int i = 0; i < data.size(); i++) {//负责画一个
                float currentRadius = radius - j * gapValue;
                //计算点的位置
                float currentX = (float) (currentRadius * Math.cos(Math.toRadians(eachDegree * i)));
                float currentY = (float) (currentRadius * Math.sin(Math.toRadians(eachDegree * i)));
                if (i == 0) {
                    path.moveTo(currentX, currentY);
                } else {
                    path.lineTo(currentX, currentY);
                }
            }
            path.close();//封闭
            canvas.drawPath(path, basePaint);
        }
        //4、画圆心到正多边形定点的线 --line
        canvas.save();//涉及旋转 先保存下状态
        for (int i = 0; i < data.size(); i++) {
            canvas.drawLine(0, 0, radius, 0, basePaint);
            canvas.rotate(eachDegree);
        }
        canvas.restore();

        //5、根据值 画区域 --path
        float itemValue = maxValue * 1.0f / data.size();//每一个间隔 代表多少值的data
        float itemRadius = radius * 1.0f / data.size();//每一个间隔 代表多少值的半径
        Path areaPath = new Path();
        coverPaint.setAlpha(255);
        Rect txtRect = new Rect();
        for (int i = 0; i < data.size(); i++) {
            //求值所在位置的半径
            Radar radar = data.get(i);
            //当前值
            float currentRadius = radar.getValue() * itemRadius * 1.0f / itemValue;//减去要画的圆的半径
            float curDegreeRadian = (float) Math.toRadians(i * eachDegree);
            //计算上述值所在的位置
            //画圆圈和path
            float x = (float) (currentRadius * Math.cos(curDegreeRadian));
            float y = (float) (currentRadius * Math.sin(curDegreeRadian));
            coverPaint.setColor(mCoverPointColor);
            canvas.drawCircle(x, y, 4, coverPaint);
            if (i == 0) {
                areaPath.moveTo(x, y);
            } else {
                areaPath.lineTo(x, y);
            }

            //6、画文本 --text
            mTxtPaint.getTextBounds(radar.getName(), 0, radar.getName().length(), txtRect);
            float txtRadius = radius + 4 + txtRect.width() / 2;//微调4
            //文本的位置
            canvas.drawText(radar.getName(),
                    (float) (txtRadius * Math.cos(curDegreeRadian)) - txtRect.width() / 2,
                    (float) (txtRadius * Math.sin(curDegreeRadian)) + txtRect.height() / 2,
                    mTxtPaint);
        }
        areaPath.close();
        coverPaint.setColor(mCoverColor);
        coverPaint.setAlpha(mCoverAlpha);
        canvas.drawPath(areaPath, coverPaint);
    }

    /**
     * 获取文本中最长的字体的长度
     *
     * @return
     */
    private int getTxtMaxWidth() {
        int maxWidth = 0;
        Rect rect = new Rect();
        for (int i = 0; i < data.size(); i++) {
            String name = data.get(i).getName();
            mTxtPaint.getTextBounds(name, 0, name.length(), rect);
            if (rect.width() > maxWidth) {
                maxWidth = rect.width();
            }
        }
        return maxWidth;
    }
}
