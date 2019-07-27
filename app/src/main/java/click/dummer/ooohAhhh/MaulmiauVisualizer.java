package click.dummer.ooohAhhh;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.graphics.Path;
import android.util.AttributeSet;

public class MaulmiauVisualizer extends BaseVisualizer {
    private float radiusMultiplier = 0.6f;
    private Paint paint2;
    private Paint paint3;
    private Paint paint4;
    private Paint paint5;
    public float mouthSize;
    public boolean inbreath;
    public float speed;

    public MaulmiauVisualizer(Context context) {
        super(context);
    }

    public MaulmiauVisualizer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaulmiauVisualizer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        mouthSize = 0;
        speed = 3.0f;
        inbreath = true;
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint2 = new Paint();
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setColor(getResources().getColor(R.color.colorAccent));
        paint3 = new Paint();
        paint3.setStyle(Paint.Style.FILL);
        paint3.setColor(getResources().getColor(R.color.colorAccent2));
        paint4 = new Paint();
        paint4.setStyle(Paint.Style.FILL);
        paint4.setColor(getResources().getColor(R.color.colorGras));
        paint5 = new Paint();
        paint5.setStyle(Paint.Style.FILL);
        paint5.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bytes != null) {
            if (inbreath) {
                mouthSize = mouthSize+speed;
                if (mouthSize>90) inbreath = false;
            } else {
                mouthSize = mouthSize-speed;
                if (mouthSize<20) inbreath = true;
            }

            Path path = new Path();
            Path armpath = new Path();
            Path wizzardstick = new Path();
            Path armpath2 = new Path();
            paint3.setStrokeWidth(0.011f*getWidth());

            float f1,f2;

            for (int i = 0; i < 120; i+=4) {
                f1 = 0.195f*getWidth() + i*getWidth()/200.0f;
                f2 = 0.10f*getWidth() + Math.abs(bytes[i]) * radiusMultiplier;
                if (i==0) {
                    path.moveTo(f1, 0.26f*getHeight());
                }else if (i==116) {
                    path.lineTo(f1, 0.255f*getHeight());
                } else {
                    path.lineTo(f1, f2);

                }
            }

            canvas.drawPath(path, paint3);
            canvas.drawPath(path, paint);


            canvas.drawCircle(0.51f*getWidth(),0.62f*getWidth(), 0.0007f*getWidth()*mouthSize, paint5);
            canvas.drawCircle(0.51f*getWidth(),0.62f*getWidth(), 0.0007f*getWidth()*mouthSize, paint);

            float rocky = (float) Math.abs(bytes[300]);

            paint2.setStrokeWidth(0.011f*getWidth());
            wizzardstick.moveTo(0.1f*getWidth(),0.95f*getWidth() - rocky);
            wizzardstick.lineTo(0.25f*getWidth(),0.65f*getWidth() - rocky);
            canvas.drawPath(wizzardstick, paint2);


            paint.setStrokeWidth(0.011f*getWidth());
            armpath.moveTo(0.34f*getWidth(), 0.74f*getWidth());
            armpath.lineTo(0.16f*getWidth(),0.85f*getWidth() - rocky - 0.05f*getWidth());
            armpath.lineTo(0.16f*getWidth(),0.85f*getWidth() - rocky + 0.05f*getWidth());
            armpath.lineTo(0.30f*getWidth(), 0.87f*getWidth());
            canvas.drawPath(armpath, paint4);
            canvas.drawPath(armpath, paint);
            canvas.drawCircle(0.16f*getWidth(),0.85f*getWidth() - rocky, 0.06f*getWidth(), paint5);
            canvas.drawCircle(0.16f*getWidth(),0.85f*getWidth() - rocky, 0.06f*getWidth(), paint);

            armpath2.moveTo(0.70f*getWidth(), 0.745f*getWidth());
            armpath2.lineTo(0.93f*getWidth(),0.85f*getWidth() - rocky - 0.05f*getWidth());
            armpath2.lineTo(0.93f*getWidth(),0.85f*getWidth() - rocky + 0.05f*getWidth());
            armpath2.lineTo(0.74f*getWidth(), 0.87f*getWidth());
            canvas.drawPath(armpath2, paint4);
            canvas.drawPath(armpath2, paint);

            canvas.drawCircle(0.93f*getWidth(),0.85f*getWidth() - rocky, 0.06f*getWidth(), paint5);
            canvas.drawCircle(0.93f*getWidth(),0.85f*getWidth() - rocky, 0.06f*getWidth(), paint);

        }
        super.onDraw(canvas);
    }
}