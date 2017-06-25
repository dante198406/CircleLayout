package com.lmj.circle;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
//import com.lmj.circle.R;

/**
 * Created by lmj on 2016/11/15 0015. on 下午 7:54
 * limengjie
 */
public class MCircle extends View {
    private int mHeight = 0;//控件的高度
    private int mWidth = 0;//控件的宽度
    private String[] mIndexStr = null;
    private int defaultUnit = 50;//单位间隔
    private int firstRadius = 100;
    private int textSize = 0;
    private int textColor = 0;
    private int lineColor = 0;
    private int rectColor = 0;
    private int[] initValue = {2, 0, 3, 1, 0};
    private Paint rectPain;//绘制线条paint
    private Paint textPain;
    private Paint solidPain;//绘制属性矩形paint
    private int textWidth;
    private int textHeight;

    public MCircle(Context context) {
        this(context, null);
    }

    public MCircle(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        try {
            TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MCircle, defStyleAttr, 0);
            for (int i = 0; i < ta.getIndexCount(); i++) {
                int attr = ta.getIndex(i);
                if (attr == R.styleable.MCircle_FirstR) {
                    firstRadius = ta.getDimensionPixelSize(attr, DensityUtil.dip2px(context, 20));
                } else if (attr == R.styleable.MCircle_unitR) {
                    defaultUnit = ta.getDimensionPixelSize(attr, DensityUtil.dip2px(context, 20));

                } else if (attr == R.styleable.MCircle_textSize) {
                    textSize = ta.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()));
                } else if (attr == R.styleable.MCircle_textColor) {
                    textColor = ta.getColor(attr, Color.BLACK);
                } else if (attr == R.styleable.MCircle_lineColor) {
                    lineColor = ta.getColor(attr, Color.BLACK);
                } else if (attr == R.styleable.MCircle_rectColor) {
                    rectColor = ta.getColor(attr, Color.BLACK);
                } else if (attr == R.styleable.MCircle_attrs) {
                    String ar = ta.getString(attr);
                    if (TextUtils.isEmpty(ar)) {
                        mIndexStr = new String[]{"五杀能力", "中单能力", "打野能力", "协作能力", "带崩能力"};
                    }
                } else if (attr == R.styleable.MCircle_datas) {
                    String dr = ta.getString(attr);
                    if (TextUtils.isEmpty(dr)) {
                        initValue = new int[]{2, 0, 3, 1, 0};
                    } else {
                        String[] dar = dr.split(",");
                        initValue = new int[dar.length];
                        for (int index = 0; index < dar.length; index++) {
                            initValue[index] = Integer.parseInt(dar[index]);
                        }
                    }
                }
            }
            ta.recycle();
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            int wMode = MeasureSpec.getMode(widthMeasureSpec);
            int wSize = MeasureSpec.getSize(widthMeasureSpec);
            if (wMode != MeasureSpec.EXACTLY) {
                wSize = defaultUnit * 2 * initValue.length + textWidth * 3 + firstRadius * 2;
            }
            int hMode = MeasureSpec.getMode(heightMeasureSpec);
            int hSize = MeasureSpec.getSize(heightMeasureSpec);
            if (hMode != MeasureSpec.EXACTLY) {
                hSize = defaultUnit * 2 * initValue.length + textHeight * 2 + firstRadius * 2;
            }
            setMeasuredDimension(wSize, hSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {
        rectPain = new Paint();
        rectPain.setAntiAlias(true);
        rectPain.setStyle(Paint.Style.STROKE);
        rectPain.setStrokeWidth(3);
        rectPain.setColor(lineColor);
        textPain = new Paint();
        textPain.setAntiAlias(true);
        textPain.setTextSize(textSize);
        textPain.setColor(textColor);
        solidPain = new Paint();
        solidPain.setAntiAlias(true);
        solidPain.setStyle(Paint.Style.FILL);
        solidPain.setColor(rectColor);

        Rect rect = new Rect();
        textPain.getTextBounds(mIndexStr[0], 0, mIndexStr[0].length(), rect);
        textWidth = rect.width();
        textHeight = rect.height();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //将画布坐标系移动到view的中心
        canvas.translate(mWidth / 2, mHeight / 2);
        drawRect(canvas);
    }

    /*
        绘制多边形
         */
    private void drawRect(Canvas canvas) {
        Path path_rect = new Path();//绘制多边形的路径
        Path path_line = new Path();//绘制圆心与顶点的连线
        Path path_sloid = new Path();//绘制属性值的路径
        for (int i = 0; i < mIndexStr.length; i++) {
            int radus = firstRadius + i * defaultUnit;//每一个多边形的外切圆的半径

            for (int j = 0; j < mIndexStr.length; j++) {
                int angle = j * 360 / mIndexStr.length;//我们的原则是第一个点在x轴正半轴
                // 每一个点对应的角度
                if (initValue.length % 2 != 0) {
                    angle += 360 / initValue.length - 88;//如果是边数是奇数的情况，本来是-90，88是我调整了一下

                }                                   //如果是偶数边，就没有必要进行偏移，
                // 因为我们的原则是第一个点在x轴正半轴，这个时候多边形是正的
                double radain = Math.PI * angle / 180;
                float x = (float) (Math.cos(radain) * radus);
                float y = (float) (Math.sin(radain) * radus);
                if (j == 0) {
                    path_rect.moveTo(x, y);
                } else {
                    path_rect.lineTo(x, y);
                }
                if (i == mIndexStr.length - 1) { //最后一圈的时候绘制属性
                    //最后一个多边形，画上中心与顶点的连线
                    path_line.lineTo(x, y);
                    canvas.drawPath(path_line, rectPain);
                    path_line.reset();

                    //绘制文字
                    Rect rect = new Rect();
                    textPain.getTextBounds(mIndexStr[j], 0, mIndexStr[j].length(), rect);
                    if (x < 0) {
                        x = x - rect.width() - 20;
                    } else if (x == 0) {
                        x = x - rect.width() / 2;
                    } else {
                        x += 20;
                    }
                    canvas.drawText(mIndexStr[j], x, y, textPain);
                    //
                    int radus2 = firstRadius + initValue[j] * defaultUnit;
                    float x2 = (float) (Math.cos(radain) * radus2);
                    float y2 = (float) (Math.sin(radain) * radus2);
                    if (j == 0) {
                        path_sloid.moveTo(x2, y2);
                    } else {
                        path_sloid.lineTo(x2, y2);
                    }

                }
            }
            path_rect.close();
            canvas.drawPath(path_rect, rectPain);
            path_rect.reset();


        }
        path_sloid.close();
        canvas.drawPath(path_sloid, solidPain);
    }

    /**
     * 刷新数据
     */
    public void flushData(int[] values) {
        this.initValue = values;
        requestLayout();
        postInvalidate();
    }

    /**
     * 属性属性名称
     */
    public void flushTexts(String[] attrs) {
        mIndexStr = attrs;
        requestLayout();
        postInvalidate();
    }

}
