package com.tonimor.tcommons;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/*
 * usage in xml:
 * <com.my.namespace.ProportionalImageView
 *       android:id="@+id/img"
 *       android:src="@drawable/myCustomImage"
 *       android:layout_width="match_parent"
 *       android:layout_height="wrap_content"
 *       android:layout_alignParentTop="true"
 *       android:layout_centerHorizontal="true"
 *       android:adjustViewBounds="true"
 *   />
 */

public class AdjustImageView extends ImageView
{
    public AdjustImageView(Context context) {
        super(context);
    }

    public AdjustImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdjustImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d = getDrawable();
        if (d != null) {
            int w = MeasureSpec.getSize(widthMeasureSpec);
            int h = w * d.getIntrinsicHeight() / d.getIntrinsicWidth();
            setMeasuredDimension(w, h);
        }
        else super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
