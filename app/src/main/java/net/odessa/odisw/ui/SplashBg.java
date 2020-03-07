package net.odessa.odisw.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;

import androidx.core.content.ContextCompat;

import net.odessa.odisw.R;

public class SplashBg extends ShapeDrawable {

    Context context;

    public SplashBg(Context context){
        super();
        this.context  = context;
        setShape(new SplashBgShape());
    }

    private  class SplashBgShape extends Shape {

        @Override
        public void draw(Canvas canvas, Paint paint) {

            Path path = new Path();
            path.setFillType(Path.FillType.INVERSE_EVEN_ODD);
            Path path1 = new Path();
            path1.setFillType(Path.FillType.INVERSE_EVEN_ODD);

            paint.setStrokeWidth(0);
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);

            int color2 = ContextCompat.getColor(context, R.color.color_bg_cyan);
            paint.setColor(color2);

            Point a1 = new Point(0, 0);
            Point b1 = new Point((int)getWidth(),0);
            Point c1 = new Point((int)getWidth(), (int)getHeight()-(int)getWidth());

            path1.moveTo(a1.x, a1.y);
            path1.lineTo(b1.x, b1.y);
            path1.lineTo(c1.x, c1.y);
            path1.close();
            canvas.drawPath(path1, paint);

            int color1 = ContextCompat.getColor(context, R.color.color_bg_white);
            paint.setColor(color1);

            Point a = new Point(0, (int)getHeight()/6);
            Point b = new Point(0, (int) getHeight());
            Point c = new Point((int)getWidth()+(int)getWidth(), (int)getHeight());

            path.moveTo(a.x, a.y);
            path.lineTo(b.x, b.y);
            path.lineTo(c.x, c.y);
            path.close();
            canvas.drawPath(path, paint);

        }
    }
}