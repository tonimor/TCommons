package com.tonimor.tcommons;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.LinearLayout;

// https://argillander.wordpress.com/2011/11/24/scale-image-into-imageview-then-resize-imageview-to-match-the-image/

public class ScaleImageObject
{
	/*******************************************
	 * Usage:
    ImageView view1 = (ImageView)findViewById(R.id.test1);
    ImageView view2 = (ImageView)findViewById(R.id.test2);
    scaleImage(view1, 250); // in dp
    scaleImage(view2, 100); // in dp
	********************************************/

	Context m_context = null;

	ScaleImageObject(Context i_context)
	{
		m_context = i_context;
	}

	@SuppressWarnings("unused")
	private void scaleImage(ImageView view, int boundBoxInDp)
	{
	    // Get the ImageView and its bitmap
	    Drawable drawing = view.getDrawable();
	    Bitmap bitmap = ((BitmapDrawable)drawing).getBitmap();

	    // Get current dimensions
	    int width = bitmap.getWidth();
	    int height = bitmap.getHeight();

	    // Determine how much to scale: the dimension requiring less scaling is
	    // closer to the its side. This way the image always stays inside your
	    // bounding box AND either x/y axis touches it.
	    float xScale = ((float) boundBoxInDp) / width;
	    float yScale = ((float) boundBoxInDp) / height;
	    float scale = (xScale <= yScale) ? xScale : yScale;

	    // Create a matrix for the scaling and add the scaling data
	    Matrix matrix = new Matrix();
	    matrix.postScale(scale, scale);

	    // Create a new bitmap and convert it to a format understood by the ImageView
	    Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	    BitmapDrawable result = new BitmapDrawable(m_context.getResources(), scaledBitmap);
	    width = scaledBitmap.getWidth();
	    height = scaledBitmap.getHeight();

	    // Apply the scaled bitmap
	    view.setImageDrawable(result);

	    // Now change ImageView's dimensions to match the scaled image
	    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
	    params.width = width;
	    params.height = height;
	    view.setLayoutParams(params);
	}

	@SuppressWarnings("unused")
	private int dpToPx(int dp)
	{
	    float density = m_context.getResources().getDisplayMetrics().density;
	    return Math.round((float)dp * density);
	}

}
