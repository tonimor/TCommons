package com.tonimor.tcommons;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

@SuppressWarnings("unused")
public class TC
{
	public static MediaPlayer m_mplayer;

	public static void LOGCAT(String i_tag, String i_message)
	{
		Log.e(i_tag, i_message);
	}

	public static void LOGCAT(String i_tag, Exception i_exception)
	{
		Log.e(i_tag, Log.getStackTraceString(i_exception));
	}

	public static class PleaseWait implements Runnable
	{
		private final String m_message;
		private final String m_title;
		private final Context m_actv;

		public ProgressDialog progressDialog = null;

		public PleaseWait(String title, String message, Context ctx)
		{
			m_message = message;
			m_title = title;
			m_actv = ctx;
		}
		public void cancel()
		{
			progressDialog.cancel();
		}
		public void run()
		{
			progressDialog = ProgressDialog.show(m_actv, m_title, m_message, true);
		}
	}

    public static class RunnableMessageBox implements Runnable
    {
    	private final String m_message;
    	private final String m_title;
    	private final int m_icon;
    	private final Activity m_actv;

    	public RunnableMessageBox(String message, String title, int icon, Activity actv)
    	{
    		m_message = message;
    		m_title = title;
    		m_icon = icon;
    		m_actv = actv;
    	}
    	public void run()
    	{
    		DisplayMessageBox(m_message, m_title, m_icon, m_actv);
    	}
    }

    public static void AfxMessageBox(String message, String title, int icon, Activity actv)
    {
    	RunnableMessageBox rmb = new RunnableMessageBox(message, title, icon, actv);
    	rmb.run();
    }

    public static void AfxMessageBox(Activity actv, String message, String title)
    {
    	AfxMessageBox(message, title, actv);
    }

    public static void AfxMessageBox(String message, String title, Activity actv)
    {
        final String lMessage = message;
        final Context lContext = actv;
        final String lTitle = title;
        actv.runOnUiThread(new Runnable()
        {
                public void run()
                {
                	DisplayMessageBox(lMessage, lTitle, 0, lContext);
                }
        });
    }

    public static void DisplayMessageBox(String message, String title, int icon, Context context)
    {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if(icon > 0)
			builder.setIcon(icon);
        builder.setTitle(title).setMessage(message).show();
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void BEGIN_TRANSACTION(SQLiteDatabase i_db)
    {
        if(!i_db.inTransaction())
        	i_db.beginTransaction();
    }

    public static void CLOSE_TRANSACTION(SQLiteDatabase i_db, boolean i_commit)
    {
        if(i_db.inTransaction())
        {
     	   if(i_commit)
     		   i_db.setTransactionSuccessful();
     	   i_db.endTransaction();
        }
    }

    public static void NotifyMessage(Context i_context, String i_message)
    {
    	int duration = Toast.LENGTH_SHORT;
        Toast.makeText(i_context, i_message, duration).show();
    }

    public static void NotifyMessage(Context i_context, String i_message, long i_duration)
    {
    	int duration = Toast.LENGTH_LONG;
    	final Toast toast = Toast.makeText(i_context, i_message, duration);
    	toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				toast.cancel();
			}
		}, i_duration/*milisegundos*/);
    }

	public static void WaitMessage(String message, Activity actv)
	{
		ProgressDialog dialog = new ProgressDialog(actv);
		dialog.setMessage(message);
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		dialog.show();
	}

	public void reproducirDesdeAssets(String recurso, Activity actv)
	{
	    try
	    {
	        // Si está creado lo matamos
	        if(m_mplayer != null)
	        {
	        	m_mplayer.stop();
	        	m_mplayer.release();
	        	m_mplayer = null;
	        }

	        // Creamos el nuevo reproductor
	        m_mplayer = new MediaPlayer();

	        // Cargamos un archivo desde el directorio assets
	        AssetFileDescriptor afd = actv.getAssets().openFd(recurso);
	        FileDescriptor fd = afd.getFileDescriptor();

	        // Reproducimos
	        if(fd != null)
	        {
	        	m_mplayer.setDataSource(fd, afd.getStartOffset(), afd.getLength());
	        	m_mplayer.prepare();
	        	m_mplayer.start();
	        }
	        else
	        	Toast.makeText(actv.getBaseContext(), "Error reproduciendo " +
	        		recurso + "! Recurso nulo...", Toast.LENGTH_LONG).show();
	    }
	    catch (Exception e)
	    {
	        Toast.makeText(actv.getBaseContext(), "Error reproduciendo "+
	        	recurso+"! "+ e.getMessage(), Toast.LENGTH_LONG).show();
	    }
	}

    public static String convertStreamToString(InputStream is) throws Exception
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        is.close();

        return sb.toString();
    }

    public static AnimationDrawable startAnimation(AnimationDrawable animation, int R_id, int R_drawable, Activity act)
    {
		// Load the ImageView that will host the animation and
		// set its background to our AnimationDrawable XML resource.
		ImageView img = (ImageView)act.findViewById(R_id);
		img.setBackgroundResource(R_drawable);
		img.setImageDrawable(animation);

		// Get the background, which has been compiled to an AnimationDrawable object.
		animation = (AnimationDrawable) img.getBackground();

		// Start the animation (looped playback by default).
		animation.start();

		return animation;
    }

    public static void stopAnimation(AnimationDrawable animation)
    {
		animation.stop();
		animation.selectDrawable(0);
		animation.setVisible(false, true);
    }

    public static void underlineText(
    		TextView i_textView, String i_text, boolean i_bold, boolean i_italic)
    {
		SpannableString spanString = new SpannableString(i_text);
		spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
		if(i_bold)
			spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
		if(i_italic)
			spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
		i_textView.setText(spanString);
    }

    public static ProgressDialog showProgressDialog(Context context, String title, String text)
    {
		return ProgressDialog.show(context, title, text, true, false, null);
    	/*
    	 * to close the dialog:
    	 * progressDlg.dismiss();
    	 */
    }

    public static String calculateMD5TokenHash(String i_token, boolean i_toHex)
    {
          String token = i_token;
          String strhash      = "";
          try
          {
             // Get hash
             MessageDigest md5 = null;
             md5 = MessageDigest.getInstance("MD5");
             md5.update(token.getBytes());
             byte[] hash_value = md5.digest();

             if(i_toHex)
             {
	             // Get HEX hash
	             StringBuffer sb = new StringBuffer(hash_value.length * 2);
	             for (int x = 0; x < hash_value.length; x++)
	             {
					 String str = "00" + Integer.toHexString(0xff & hash_value[x]);
					 sb.append(str.substring(str.length() - 2, str.length()));
	             }
	             strhash = sb.toString();
             }
             else
            	 strhash = hash_value.toString();
          }
          catch (Exception e){ strhash = null; }

          return strhash;
    }

    @SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public static int getRealOrientation(Activity i_current)
    {
    	Display display = i_current.getWindowManager().getDefaultDisplay();
    	int rotation = display.getRotation();

    	Point size = new Point();
    	if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
    		display.getSize(size);
    	else
    	{
    		int width  = display.getWidth();
    		int height = display.getHeight();
    		size.x = width;
    		size.y = height;
    	}

    	int orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    	if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)
    	{
    		// if rotation is 0 or 180, and width is greater than height, we have a tablet
    		if (size.x > size.y)
    		{
    			if (rotation == Surface.ROTATION_0)
    				orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    			else
    				orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
    		}
    		else
    		{
    			// in that case we have a phone
    			if (rotation == Surface.ROTATION_0)
    				orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    			else
    				orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
    		}
    	}
    	else
    	{
    		// if rotation is 90 or 270 and width is greater than height, we have a phone
    		if (size.x > size.y)
    		{
    			if (rotation == Surface.ROTATION_90)
    				orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    			else
    				orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
    		}
    		else
    		{
    			// we have a tablet
    			if (rotation == Surface.ROTATION_90)
    				orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
    			else
    				orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    		}
    	}

    	return orientation;
    }

	public static boolean isPackageInstalled(String packagename, PackageManager packageManager) {
		try {
			packageManager.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
			return true;
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
	}

	public static void vibrate(Context i_context, long i_millis)
	{
		/*
		 * Don't forget to include permission in AndroidManifest.xml file:
		 * <uses-permission android:name="android.permission.VIBRATE"/>
		 */
		Vibrator v = (Vibrator) i_context.getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(i_millis);
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// arrays

	public static String[] concat(String[] i_array1, String[] i_array2)
	{
        ArrayList<String> list = new ArrayList<String>(Arrays.asList(i_array1));
        list.addAll(Arrays.asList(i_array2));
        String[] result = new String[list.size()];
        list.toArray(result);
        return result;
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// date-time

    public static Calendar getCalendar(int i_year, int i_month, int i_day)
    {
    	final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, i_year);
		calendar.set(Calendar.MONTH, i_month);
		calendar.set(Calendar.DAY_OF_MONTH, i_day);
		return calendar;
    }

    public static boolean isValidDateTime(String i_pattern, String timeToFormat)
    {
    	SimpleDateFormat iso8601Format = new SimpleDateFormat(i_pattern, Locale.getDefault());

        Date date = null;
        if (timeToFormat != null)
        {
            try {
                date = iso8601Format.parse(timeToFormat);
            } catch (ParseException e) {
                date = null;
            }
        }

        return (date != null);
    }

    @SuppressLint("SimpleDateFormat")
    public static String DateToString(Date i_date, String i_format /*yyyy/MM/dd HH:mm:ss*/)
    {
        DateFormat df = new SimpleDateFormat(i_format);
        return df.format(i_date);
    }

    @SuppressLint("SimpleDateFormat")
	public static Date stringToDate(String i_strdate, String i_format) throws ParseException
    {
    	DateFormat df = new SimpleDateFormat(i_format);
		return df.parse(i_strdate);
    }

    @SuppressLint("SimpleDateFormat")
	public static String calendarToString(Calendar i_calendar, String i_format)
    {
    	DateFormat df = new SimpleDateFormat(i_format);
    	String date = df.format(i_calendar.getTime());
    	return date;
    }

    @SuppressLint("SimpleDateFormat")
	public static Calendar stringToCalendar(String i_strdate, String i_format) throws ParseException
    {
    	java.util.Date date = stringToDate(i_strdate, i_format);
    	final Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	return calendar;
    }

	public static String getCurrentDateString(String i_format)
	{
		DateFormat df = new SimpleDateFormat(i_format);
        final Calendar calendar = Calendar.getInstance();
        return df.format(calendar.getTime());
	}

    // current time: hour
    public static int currentTimeHour()
    {
    	long millis = System.currentTimeMillis();
    	int hour = (int) ((millis / (1000*60*60)) % 24);
    	return hour;
    }

    // current time: minutes
    public static int currentTimeMinutes()
    {
    	long millis = System.currentTimeMillis();
    	int minutes = (int) ((millis / (1000*60)) % 60);
    	return minutes;
    }

    // current time: seconds
    public static int currentTimeSeconds()
    {
    	long millis = System.currentTimeMillis();
    	int seconds = (int) (millis / 1000) % 60;
    	return seconds;
    }

    public static String sqlUnformatDateTime(Context i_context, String i_dateTime)
    {
        String finalDateTime = "";

        SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        Date date = null;
        if (i_dateTime != null) {
            try {
                date = iso8601Format.parse(i_dateTime);
            } catch (ParseException e) {
                date = null;
            }

            if (date != null)
            {
                long when = date.getTime();
                int flags = 0;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_TIME;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_DATE;
                flags |= android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_YEAR;

                finalDateTime = android.text.format.DateUtils.formatDateTime(i_context,
                		when + TimeZone.getDefault().getOffset(when), flags);
            }
        }
        return finalDateTime;
    }

    public static String sqlUnformatDateTime(String i_patternOut, String i_dateTime)
    {
        String finalDateTime = "";

        SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        Date date = null;
        if (i_dateTime != null) {
            try {
                date = iso8601Format.parse(i_dateTime);
            } catch (ParseException e) {
                date = null;
            }

            if (date != null)
            {
            	SimpleDateFormat dateFormat = new SimpleDateFormat(
            			i_patternOut, Locale.getDefault());
            	finalDateTime = dateFormat.format(date);
            }
        }
        return finalDateTime;
    }

    public static String sqlFormatDateTime(String i_patternIn, String i_dateTime)
    {
    	String finalDateTime = "";

    	SimpleDateFormat dateFormat = new SimpleDateFormat(i_patternIn, Locale.getDefault());

        Date date = null;
        if (i_dateTime != null) {
            try {
                date = dateFormat.parse(i_dateTime);
            } catch (ParseException e) {
                date = null;
            }

            if (date != null)
            {
            	SimpleDateFormat iso8601Format = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            	finalDateTime = iso8601Format.format(date);
            }
        }

        return finalDateTime;
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// pixels, dp's & density methods

	// Get the screen's density scale
	public static float getDensity()
	{
	    float density = Resources.getSystem().getDisplayMetrics().density;
	    return density;
	}

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float dipToPix(float dp)
    {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @return A float value to represent dp equivalent to px value
     */
    public static float pixToDip(float px)
    {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		return px / (metrics.densityDpi / 160f);
    }

    public static int dpToPx(Resources resources, int dp)
	{
	    float density = resources.getDisplayMetrics().density;
	    return Math.round((float)dp * density);
	}

    public static float getScaleFactor(Resources i_resources)
    {
		/*
		 * dpi is the normal, or default. Its scale factor is 1.0.
		 *
		 * ldpi = 0.75
		 * mdpi = 1.0
		 * hdpi = 1.5
		 * xhdpi = 2.0
		 * xxhdpi = 3.0
		 * xxxhdpi = 4.0
		 */
    	final DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		return metrics.density;
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Image stuff

    public static void fitImage(
    	Activity i_activity, ImageView i_imageView, int i_imageResId)
    {
    	// this method fits the image given by i_imageResId into the i_imageView
    	// always respecting the image aspect ratio
    	// http://n3wt0n.com/blog/fit-width-of-background-image-but-keep-aspect-ratio-in-android/

        DisplayMetrics metrics = new DisplayMetrics();
        i_activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        BitmapDrawable bmap = (BitmapDrawable)i_activity.getResources().getDrawable(i_imageResId);
		if(bmap == null)
			return;

        float bmapWidth = bmap.getBitmap().getWidth();
        float bmapHeight = bmap.getBitmap().getHeight();

        float wRatio = width / bmapWidth;
        float hRatio = height / bmapHeight;

        float ratioMultiplier = wRatio;
        // Untested conditional though I expect this might work for landscape mode
        if (hRatio < wRatio) {
        	ratioMultiplier = hRatio;
        }

        int newBmapWidth = (int) (bmapWidth*ratioMultiplier);
        int newBmapHeight = (int) (bmapHeight*ratioMultiplier);

        i_imageView.setLayoutParams(new LinearLayout.LayoutParams(newBmapWidth, newBmapHeight));

    }

    public static void adjustImageSize(
    	ImageView i_imageView, 	// control ImageView
    	int i_imageWidth, 		// ancho de la imagen en píxels
    	int i_imageHeight, 		// alto  de la imagen en píxels
    	double i_widthPercent)	// valor de reducción de la imagen en tanto por uno respecto del ancho de la pantalla
    {
		DisplayMetrics 	metrics 	= Resources.getSystem().getDisplayMetrics();
		final double 	width    	= i_imageWidth;
		final double 	height    	= i_imageHeight;
		final int	 	viewWidth	= (int)(metrics.widthPixels * i_widthPercent);
        final double 	aspect 		= height/width; // image height / image width
        i_imageView.getLayoutParams().width = viewWidth;
        i_imageView.getLayoutParams().height = (int)(viewWidth * aspect);
    }

	public static void adjustImageSize(
			ImageView i_imageView, 	// control ImageView
			Bitmap	  i_bitmap,		// bitmap
			double i_widthPercent)	// valor de reducción de la imagen en tanto por uno respecto del ancho de la pantalla
	{
		DisplayMetrics 	metrics 	= Resources.getSystem().getDisplayMetrics();
		final double 	width    	= i_bitmap.getWidth();
		final double 	height    	= i_bitmap.getHeight();
		final int	 	viewWidth	= (int)(metrics.widthPixels * i_widthPercent);
		final double 	aspect 		= height/width; // image height / image width
		i_imageView.getLayoutParams().width = viewWidth;
		i_imageView.getLayoutParams().height = (int)(viewWidth * aspect);
	}

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth)
    {
    	int width = bm.getWidth();
    	int height = bm.getHeight();
    	float scaleWidth = ((float) newWidth) / width;
    	float scaleHeight = ((float) newHeight) / height;

    	// CREATE A MATRIX FOR THE MANIPULATION
    	Matrix matrix = new Matrix();

    	// RESIZE THE BIT MAP
    	matrix.postScale(scaleWidth, scaleHeight);

    	// RECREATE THE NEW BITMAP
    	Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

    	return resizedBitmap;
   	}

    public static void scaleImage(ImageView view, double boundWidthPerOne)
	{
		DisplayMetrics 	metrics = Resources.getSystem().getDisplayMetrics();
		final int	 	viewWidth	= (int)(metrics.widthPixels * boundWidthPerOne);
		scaleImage(view, viewWidth);
	}

    public static void scaleImage(ImageView view, int boundBoxInDp, Resources resources)
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
	    width = scaledBitmap.getWidth();
	    height = scaledBitmap.getHeight();

	    // Apply the scaled bitmap
	    view.setImageBitmap(scaledBitmap);

	    // Now change ImageView's dimensions to match the scaled image
	    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
	    params.width = width;
	    params.height = height;
	    view.setLayoutParams(params);
	}

	public static void applyColourOnImage(int touch_x, int touch_y, WindowManager i_wndManager,
		Context i_context, int i_imgId/*R.drawable.i_imgId*/, ImageView i_imgView)
	{
        DisplayMetrics metrics = new DisplayMetrics();
        i_wndManager.getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        // los valores (x,y) son las coordenadas con respecto al origen,
        // siendo el origen el centro de la pantalla
        int x = (width/2)-touch_x;
        int y = (height/2)-touch_y;

    	// saco la arcotangente y lo paso a grados
    	double angle = Math.atan2(y, x);
        angle = (double)Math.toDegrees(angle);
        if(angle < 0)
        	angle += 360;

        applyColourOnImage(i_context, i_imgId, i_imgView, angle);
	}

    public static void applyColourOnImage(Context i_context, int i_imgId/*R.drawable.i_imgId*/, ImageView i_imgView, double i_degrees)
    {
    	Bitmap mBitmap = BitmapFactory.decodeResource(i_context.getResources(), i_imgId);
    	int mPhotoWidth = mBitmap.getWidth();
    	int mPhotoHeight = mBitmap.getHeight();

    	int[] pix = new int[mPhotoWidth * mPhotoHeight];
    	mBitmap.getPixels(pix, 0, mPhotoWidth, 0, 0, mPhotoWidth, mPhotoHeight);

    	double angle = (3.14159d * (double)i_degrees) / 180.0d;
    	int S = (int)(256.0d * Math.sin(angle));
    	int C = (int)(256.0d * Math.cos(angle));

    	int r, g, b, index;
    	int RY, BY, RYY, GYY, BYY, R, G, B, Y;

    	for (int y = 0; y < mPhotoHeight; y++) {
    	    for (int x = 0; x < mPhotoWidth; x++) {
    	        index = y * mPhotoWidth + x;
    	        r = (pix[index] >> 16) & 0xff;
    	        g = (pix[index] >> 8) & 0xff;
    	        b = pix[index] & 0xff;
    	        RY = (70 * r - 59 * g - 11 * b) / 100;
    	        BY = (-30 * r - 59 * g + 89 * b) / 100;
    	        Y = (30 * r + 59 * g + 11 * b) / 100;
    	        RYY = (S * BY + C * RY) / 256;
    	        BYY = (C * BY - S * RY) / 256;
    	        GYY = (-51 * RYY - 19 * BYY) / 100;
    	        R = Y + RYY;
    	        R = (R < 0) ? 0 : ((R > 255) ? 255 : R);
    	        G = Y + GYY;
    	        G = (G < 0) ? 0 : ((G > 255) ? 255 : G);
    	        B = Y + BYY;
    	        B = (B < 0) ? 0 : ((B > 255) ? 255 : B);
    	        pix[index] = 0xff000000 | (R << 16) | (G << 8) | B;
    	    }
    	}

    	Bitmap bm = Bitmap.createBitmap(mPhotoWidth, mPhotoHeight, Bitmap.Config.ARGB_8888);
    	bm.setPixels(pix, 0, mPhotoWidth, 0, 0, mPhotoWidth, mPhotoHeight);

    	if ( mBitmap != null) {
    	    mBitmap.recycle();
    	}
    	mBitmap = bm;

    	// Put the updated bitmap into the main view
    	i_imgView.setImageBitmap(mBitmap);
    	i_imgView.invalidate();

    	pix = null;
    }

	// http://stackoverflow.com/questions/2661536/how-to-programmatically-take-a-screenshot-in-android
	public static void takeScreenshot(Activity i_activity) {
		Date now = new Date();
		android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

		try {
			// image naming and path  to include sd card  appending name you choose for file
			String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

			// create bitmap screen capture
			View v1 = i_activity.getWindow().getDecorView().getRootView();
			v1.setDrawingCacheEnabled(true);
			v1.buildDrawingCache(true);
			Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
			v1.setDrawingCacheEnabled(false);

			File imageFile = new File(mPath);

			FileOutputStream outputStream = new FileOutputStream(imageFile);
			final int quality = 100;
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
			outputStream.flush();
			outputStream.close();

			openScreenshot(imageFile, i_activity);

		} catch (Throwable e) {
			// Several error may come out with file handling or OOM
			e.printStackTrace();
		}
	}

	public static void openScreenshot(File imageFile, Activity i_from) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		Uri uri = Uri.fromFile(imageFile);
		intent.setDataAndType(uri, "image/*");
		i_from.startActivity(intent);
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////
// views stuff

	public static void expand(final View v) {
		v.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
		final int targetHeight = v.getMeasuredHeight();

		// Older versions of android (pre API 21) cancel animations for views with a height of 0.
		v.getLayoutParams().height = 1;
		v.setVisibility(View.VISIBLE);
		Animation a = new Animation()
		{
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				v.getLayoutParams().height = interpolatedTime == 1
						? WindowManager.LayoutParams.WRAP_CONTENT
						: (int)(targetHeight * interpolatedTime);
				v.requestLayout();
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		// 1dp/ms
		a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
		v.startAnimation(a);
	}

	public static void collapse(final View v) {
		final int initialHeight = v.getMeasuredHeight();

		Animation a = new Animation()
		{
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				if(interpolatedTime == 1){
					v.setVisibility(View.GONE);
				}else{
					v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
					v.requestLayout();
				}
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		// 1dp/ms
		a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
		v.startAnimation(a);
	}

	/**
	 * Get all the views which matches the given Tag recursively
	 * @param root parent view. for e.g. Layouts
	 * @param tag tag to look for
	 * @return List of views
	 */
	public static List<View> findViewWithTagRecursively(ViewGroup root, Object tag){
		List<View> allViews = new ArrayList<View>();

		final int childCount = root.getChildCount();
		for(int i=0; i<childCount; i++){
			final View childView = root.getChildAt(i);

			if(childView instanceof ViewGroup){
				allViews.addAll(findViewWithTagRecursively((ViewGroup)childView, tag));
			}
			else{
				final Object tagView = childView.getTag();
				if(tagView != null && tagView.equals(tag))
					allViews.add(childView);
			}
		}

		return allViews;
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////
// Files

	public static boolean isFileNameValid(String file) {
        File f = new File(file);
        try {
            f.getCanonicalPath();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
