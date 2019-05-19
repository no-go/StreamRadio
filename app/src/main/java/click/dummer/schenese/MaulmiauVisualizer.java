package click.dummer.schenese;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.graphics.Path;
import android.util.AttributeSet;

public class MaulmiauVisualizer extends BaseVisualizer {
    private float radiusMultiplier = 0.9f;
    private Paint paint2;
    private Paint paint3;
    private Paint paint4;
    private Paint paint5;
    private Paint paint6;

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
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint2 = new Paint();
        paint2.setStyle(Paint.Style.FILL);
        paint2.setColor(Color.RED);
        paint3 = new Paint();
        paint3.setStyle(Paint.Style.FILL);
        paint3.setColor(getResources().getColor(R.color.colorAccent2));
        paint4 = new Paint();
        paint4.setStyle(Paint.Style.FILL);
        paint4.setColor(getResources().getColor(R.color.colorPrimary));
        paint5 = new Paint();
        paint5.setStyle(Paint.Style.FILL);
        paint5.setColor(Color.WHITE);
        paint6 = new Paint();
        paint6.setStyle(Paint.Style.FILL);
        paint6.setColor(getResources().getColor(R.color.colorGras));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bytes != null) {
            float barWidth = getWidth() / 20;
            float div = bytes.length / 20;
            paint6.setStrokeWidth(barWidth - 5);

            Path path = new Path();
            Path path2 = new Path();
            Path armpath = new Path();
            paint.setStrokeWidth(0.011f*getWidth());

            double angle = 4;
            float fs1,fs2;
            float f1,f2;
            fs1 = (float) (getWidth() / 2
                    + Math.abs(bytes[0])
                    * radiusMultiplier
                    * Math.cos(Math.toRadians(0)));
            fs2 = (float) (getWidth() / 2
                    + Math.abs(bytes[0])
                    * radiusMultiplier/1.8f
                    * Math.sin(Math.toRadians(0)));
            path.moveTo(fs1, fs2);
            path2.moveTo(fs1, fs2);

            for (int i = 4; i < 360; i+=4, angle+=4) {
                f1 = (float) (getWidth() / 2
                        + Math.abs(bytes[i])
                        * radiusMultiplier
                        * Math.cos(Math.toRadians(angle)));
                f2 = (float) (getWidth() / 2
                        + Math.abs(bytes[i])
                        * radiusMultiplier/1.8f
                        * Math.sin(Math.toRadians(angle)));
                path.lineTo(f1, f2);
                if (i<120) path2.lineTo(f1, f2);
            }
            path.lineTo(fs1, fs2);
            path2.quadTo(getWidth()/2,getWidth()/2, fs1, fs2);
            canvas.save();
            canvas.rotate(-15f,getWidth()/2,getWidth()/2);
            canvas.drawPath(path, paint2);
            canvas.drawPath(path, paint);
            canvas.drawPath(path2, paint3);
            canvas.drawPath(path2, paint);
            canvas.restore();
            if (bytes[1] > 64) {
                paint.setStrokeWidth(0.03f*getWidth());
                canvas.drawCircle(0.41f*getWidth(),0.25f*getWidth(), 0.025f*getWidth(), paint);
                canvas.drawCircle(0.53f*getWidth(),0.22f*getWidth(), 0.025f*getWidth(), paint);
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

            for (int i = 0; i < 20; i++) {
                int bytePosition = (int) Math.ceil(i * div);
                int top = getHeight() + ((byte) (Math.abs(bytes[bytePosition]) + 128)) * getHeight() / 512;
                float barX = (i * barWidth) + (barWidth / 2);
                canvas.drawLine(barX, getHeight()-18, barX, top-18, paint6);
            }
        }
        super.onDraw(canvas);
    }
}