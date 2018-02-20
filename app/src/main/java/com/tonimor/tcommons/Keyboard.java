package com.tonimor.tcommons;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

@SuppressWarnings("unused")
public class Keyboard
{
    public static void toggle(Activity activity, View i_view)
    {
		InputMethodManager manager =
			(InputMethodManager)activity.getSystemService(
				Activity.INPUT_METHOD_SERVICE);
		if (manager.isActive(i_view))
			manager.toggleSoftInput(
				InputMethodManager.HIDE_IMPLICIT_ONLY, 0); // hide
		else
			manager.toggleSoftInput(
				0, InputMethodManager.HIDE_IMPLICIT_ONLY); // show
    }

    public static void show(Activity i_activity, View i_view)
    {
	    InputMethodManager manager =
	    	(InputMethodManager)i_activity.getSystemService(
	    		Activity.INPUT_METHOD_SERVICE);
	    manager.showSoftInput(
	    		i_view, InputMethodManager.SHOW_IMPLICIT);
    }
    public static void hide(Activity i_activity, View i_view)
    {
	    InputMethodManager manager = (InputMethodManager)
	    	i_activity.getSystemService(
	    		Activity.INPUT_METHOD_SERVICE);
	    manager.hideSoftInputFromWindow(
	    		i_view.getWindowToken(), 0);
    }
}
