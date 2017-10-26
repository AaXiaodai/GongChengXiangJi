package example.com.gongchengxiangji.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by PC19 on 2017/8/2.
 */
public class RectOnCamera extends View {
    private int mScreenWidth;
    private int mScreenHeight;
    private Paint mPaint;
    private RectF mRectF;
    // 圆
    private Point centerPoint;
    private int radio;

    public RectOnCamera(Context context) {
        this(context, null);
    }

    public RectOnCamera(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RectOnCamera(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getScreenMetrix(context);
        initView(context);
    }

    private void getScreenMetrix(Context context) {
        WindowManager WM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WM.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
    }

    private void initView(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);// 抗锯齿
        mPaint.setDither(true);// 防抖动
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(0);
        mPaint.setStyle(Paint.Style.STROKE);// 空心
        int marginLeft = (int) (mScreenWidth*0.15);
        int marginTop = (int) (mScreenHeight * 0.25);
        mRectF = new RectF(marginLeft, marginTop, mScreenWidth - marginLeft, mScreenHeight - marginTop);

        centerPoint = new Point(mScreenWidth/2, mScreenHeight/2);
        radio = (int) (mScreenWidth*0.1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.RED);
        canvas.drawRect(mRectF, mPaint);
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(centerPoint.x,centerPoint.y, radio,mPaint);// 外圆
        canvas.drawCircle(centerPoint.x,centerPoint.y, radio - 20,mPaint); // 内圆
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                int x = (int) event.getX();
                int y = (int) event.getY();
                centerPoint = new Point(x, y);
                invalidate();
                if (mIAutoFocus != null){
                    mIAutoFocus.autoFocus();
                }
                return true;
        }
        return true;
    }
    private IAutoFocus mIAutoFocus;
    /** 聚焦的回调接口 */
    public interface  IAutoFocus{
        void autoFocus();
    }

    public void setIAutoFocus(IAutoFocus mIAutoFocus) {
        this.mIAutoFocus = mIAutoFocus;
    }
}
