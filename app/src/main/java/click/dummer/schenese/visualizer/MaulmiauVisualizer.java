/*
* Copyright (C) 2017 Gautam Chibde
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package click.dummer.schenese.visualizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import click.dummer.schenese.BaseVisualizer;
import click.dummer.schenese.R;

import android.graphics.Path;
import android.util.AttributeSet;

/**
 * Custom view that creates a Bar visualizer effect for
 * the android {@link android.media.MediaPlayer}
 *
 * Created by gautam chibde on 28/10/17.
 */

public class MaulmiauVisualizer extends BaseVisualizer {
    private float radiusMultiplier = 0.8f;
    private Paint paint2;
    private Paint paint3;

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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bytes != null) {
            Path path = new Path();
            Path path2 = new Path();
            paint.setStrokeWidth(5.0f);

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
                paint.setStrokeWidth(15.0f);
                canvas.drawCircle(0.41f*getWidth(),0.25f*getWidth(), 12, paint);
                canvas.drawCircle(0.53f*getWidth(),0.22f*getWidth(), 12, paint);
            }

        }
        super.onDraw(canvas);
    }
}