package com.example.ycl.tptest;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


public class LineView extends View {
    private Paint mPaint;
    private Paint bPaint;
    private int screenWidth;
    private int screeHeight;
    private WindowManager wm;
    private int acrosscount = 20;//横条的方格数目
    private int verticalcount = 30;//竖条方格数目
    private int rectwidth;
    private int rectheight;
    private int totalcount;
    private boolean[] state;
    private Canvas canvas;
    private Bitmap bitmap;


    public FullscreenActivity fullscreenActivity=new FullscreenActivity();

    private float mov_x;//声明起点坐标
    private float mov_y;

    private float down_x;
    private float down_y;


    private float mx, my;

    private android.graphics.Path path;



    public LineView(Context context) {
        super(context);
        path = new android.graphics.Path();
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();//
        screeHeight = wm.getDefaultDisplay().getHeight();//首次测试出屏幕的高度在位实现全屏的时候
        System.out.println("YYYYYYYYYY" + screeHeight + "LLLLLLLLLLLLLLLL" + screenWidth);
        //方块的总数
        totalcount = verticalcount * 2 + 2 * acrosscount - 4;//96格子

        canvas = new Canvas();//创建画布
        canvas.setBitmap(bitmap);
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2f);

        bPaint = new Paint();
        bPaint.setColor(Color.WHITE);
        bPaint.setStyle(Paint.Style.STROKE);
        bPaint.setStrokeWidth(2f);
        //判定初始方格的状态，初始值位false
        state = new boolean[totalcount];
        for (int i = 0; i < totalcount; i++) {
            state[i] = false;
            System.out.println("yangchenglei:" + i);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //当屏幕出现改变的时候，我们可以获取到全屏的宽度和高度
        screenWidth = getWidth();
        screeHeight = getHeight();
        bitmap = Bitmap.createBitmap(screenWidth, screeHeight, Bitmap.Config.ARGB_8888);
        rectwidth = screenWidth / acrosscount;
        rectheight = screeHeight / verticalcount;
        System.out.println("YYYYYYYYYY" + screenWidth);
        System.out.println("LLLLLLLLLL" + screeHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*canvas.drawBitmap(bitmap, 0, 0, null);*/
        canvas.drawColor(Color.BLACK);
        for (int i = 0; i < totalcount; i++) {
            //设置画笔的颜色,通过方格状态
            System.out.println("total:" + i);
            if (state[i]) {
                mPaint.setColor(Color.GREEN);

            } else {
                mPaint.setColor(Color.RED);

            }

            /*画出格子的判断*/
            if (i < acrosscount) {
                canvas.drawRect(rectwidth * i, 0, (i + 1) * rectwidth, rectheight, mPaint);
                System.out.println("count1:" + i);
            } else if (i < (acrosscount + verticalcount) - 1) {
                int index = i - acrosscount;
                canvas.drawRect(0, (index + 1) * rectheight, rectwidth, (index + 2) * rectheight, mPaint);
                System.out.println("count2:index" + index);
            } else if (i < 2 * acrosscount + verticalcount - 2) {
                int index = i - acrosscount - verticalcount + 1;
                canvas.drawRect((index + 1) * rectwidth, screeHeight - rectheight, (index + 2) * rectwidth, screeHeight, mPaint);
                System.out.println("count3:+index" + index);
            } else if (i < 2 * acrosscount + 2 * verticalcount - 2) {
                int index = i - (2 * acrosscount + verticalcount - 2);
                canvas.drawRect(screenWidth - rectwidth, (index + 1) * rectheight, screenWidth, (index + 2) * rectheight, mPaint);
            }

        }

        canvas.drawPath(path, bPaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //获取按下状态的下x,y坐标
            down_x = event.getX();
            down_y = event.getY();
            touchDown(event);
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {

            //获取滑动的x，y坐标
            mov_x = event.getX();
            mov_y = event.getY();
            touchMove(event);
            if (event.getY() < rectheight) {
                for (int i = 0; i < acrosscount; i++) {
                    if (mov_x > i * rectwidth && mov_x < (i + 1) * rectwidth) {
                        state[i] = true;
                        break;
                    }
                }
            }

            if (mov_x > 0 && mov_x < rectwidth && mov_y > rectheight && mov_y < screeHeight - rectheight) {
                for (int i = 0; i < verticalcount; i++) {
                    if (mov_y > rectheight + i * rectheight && mov_y < rectheight + (i + 1) * rectheight) {
                        state[i + acrosscount] = true;
                        break;
                    }
                }
            }

            if (mov_y > screeHeight - rectheight && mov_y < screeHeight) {
                for (int i = 0; i < acrosscount; i++) {
                    if (mov_x > i * rectwidth && mov_x < rectwidth * (i + 1)) {
                        state[i + acrosscount + verticalcount - 2] = true;
                        break;
                    }
                }
            }

            if (mov_y > rectheight && mov_y < screeHeight - rectheight && mov_x > screenWidth - rectwidth && mov_x < screenWidth) {
                for (int i = 0; i < verticalcount; i++) {
                    if (mov_y > i * rectheight + rectheight && mov_y < rectheight + (i + 1) * rectheight) {
                        state[i + 2 * acrosscount + verticalcount - 2] = true;
                        break;
                    }
                }
            }
            //判断是否全部画完
            boolean isTestTure = true;
            for (int i = 0; i < totalcount; i++) {
                if (!state[i]) {
                    isTestTure = false;
                    break;
                }
            }
            //如果测试完成就实现如下
            if (isTestTure) {
                Toast.makeText(getContext(),"测试成功",Toast.LENGTH_SHORT).show();
                CloseActivity(getContext());
            }

            invalidate();
        }

        return true;
    }
    private void CloseActivity(Context context) {
        ((FullscreenActivity) context).finish();
    }

    private void touchMove(MotionEvent event) {
        mov_x = event.getX();
        mov_y = event.getY();

        float previousX = mx;
        float previousY = my;

        float dx = Math.abs(mov_x - previousX);
        float dy = Math.abs(mov_y - previousY);

        if (dx > 3 || dy > 3) {
            float cx = (mov_x + previousX) / 2;
            float cy = (mov_y + previousY) / 2;
            path.quadTo(previousX, previousY, cx, cy);
            mx = mov_x;
            my = mov_y;
        }
    }

    private void touchDown(MotionEvent event) {
        down_x = event.getX();
        down_y = event.getY();
        mx = down_x;
        my = down_y;
        path.moveTo(down_x, down_y);
    }
}
