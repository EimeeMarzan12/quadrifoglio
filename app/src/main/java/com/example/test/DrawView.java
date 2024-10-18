package com.example.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DrawView extends View {
    private List<RectF> boundingBoxes = new ArrayList<>();
    private List<String> labels = new ArrayList<>();
    private Paint paint;

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(android.graphics.Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5.0f);
        paint.setTextSize(40.0f);
    }

    public void clear() {
        boundingBoxes.clear();
        labels.clear();
        invalidate();
    }

    public void drawBoundingBox(float left, float top, float right, float bottom, String label) {
        boundingBoxes.add(new RectF(left, top, right, bottom));
        labels.add(label);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < boundingBoxes.size(); i++) {
            RectF box = boundingBoxes.get(i);
            canvas.drawRect(box, paint);
            canvas.drawText(labels.get(i), box.left, box.top - 10, paint);
        }
    }
}
