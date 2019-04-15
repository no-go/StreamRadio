package click.dummer.have_radiosion;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import click.dummer.have_radiosion.R;

import android.graphics.Path;
import android.util.AttributeSet;

public class GrrrVisualizer extends BaseVisualizer {
    private float radiusMultiplier = 0.4f;
    private Paint paint2;
    private Paint paint4;
    private Paint paint5;

    public GrrrVisualizer(Context context) {
        super(context);
    }

    public GrrrVisualizer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GrrrVisualizer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint2 = new Paint();
        paint2.setStyle(Paint.Style.FILL);
        paint2.setColor(Color.RED);
        paint4 = new Paint();
        paint4.setStyle(Paint.Style.FILL);
        paint4.setColor(getResources().getColor(R.color.colorPrimary));
        paint5 = new Paint();
        paint5.setStyle(Paint.Style.FILL);
        paint5.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bytes != null) {
            Path path = new Path();
            Path armpath = new Path();
            paint.setStrokeWidth(0.011f*getWidth());

            double angle = 4;
            float fs1,fs2;
            float f1,f2;
            fs1 = (float) (0.4*getWidth()
                    + Math.abs(bytes[0])
                    * radiusMultiplier
                    * Math.cos(Math.toRadians(0)));
            fs2 = (float) (0.4*getWidth()
                    + Math.abs(bytes[0])
                    * radiusMultiplier
                    * Math.sin(Math.toRadians(0)));
            path.moveTo(fs1, fs2);

            for (int i = 12; i < 360; i+=12, angle+=12) {
                f1 = (float) (0.4*getWidth()
                        + Math.abs(bytes[i])
                        * radiusMultiplier
                        * Math.cos(Math.toRadians(angle)));
                f2 = (float) (0.4*getWidth()
                        + Math.abs(bytes[i])
                        * radiusMultiplier
                        * Math.sin(Math.toRadians(angle)));
                path.lineTo(f1, f2);
            }
            path.lineTo(fs1, fs2);
            canvas.drawPath(path, paint2);
            canvas.save();
            canvas.translate(getWidth()/6, 0);
            canvas.drawPath(path, paint2);
            canvas.restore();
            if (bytes[1] > 64) {
                paint.setStrokeWidth(0.03f*getWidth());
                canvas.drawCircle(0.50f*getWidth(),0.55f*getWidth(), 0.025f*getWidth(), paint);
            }
            paint.setStrokeWidth(0.011f*getWidth());
            float rocky = (float) Math.abs(bytes[300]);
            armpath.moveTo(0.24f*getWidth(), 0.74f*getWidth());
            armpath.lineTo(0.56f*getWidth(),0.85f*getWidth() - rocky - 0.05f*getWidth());
            armpath.lineTo(0.56f*getWidth(),0.85f*getWidth() - rocky + 0.05f*getWidth());
            armpath.lineTo(0.38f*getWidth(), 0.87f*getWidth());
            canvas.drawPath(armpath, paint4);
            canvas.drawPath(armpath, paint);
            canvas.drawCircle(0.56f*getWidth(),0.85f*getWidth() - rocky, 0.06f*getWidth(), paint5);
            canvas.drawCircle(0.56f*getWidth(),0.85f*getWidth() - rocky, 0.06f*getWidth(), paint);
        }
        super.onDraw(canvas);
    }
}