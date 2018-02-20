package com.tonimor.tcommons;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;

import java.util.ArrayList;

// http://blog.automatonic.net/2010/08/android-advanced-radiobutton-layout_18.html

/*
 * Usage:
 * =====
 * 1.- in your Activity implement handleMessage for:
 *     Handler handler = new Handler(Looper.getMainLooper()
 *     to process click events on RadioButtons with ON_RADIOBUTTON_CLICK message
 * 2.- in your Activity declare:
 *     RadioGroupExt rgroup = new RadioGroupExt(this, handler);
 * 3.- in OnCreate set options by RadioButton Id:
 * 	   rgroup.setRadioButtonID(R.id.one);
 *     rgroup.setRadioButtonID(R.id.two);
 *     etc...
 */

@SuppressWarnings("unused")
public class RadioGroupExt
{
	private ArrayList<Integer> 	m_options 	= new ArrayList<Integer>();
	private Activity			m_activity	= null;
	private Handler 			m_handler 	= null;

	final public static int 	ON_RADIOBUTTON_CLICK = 31001;

	public RadioGroupExt(Activity i_activity, Handler i_handler)
	{
		this.m_activity = i_activity;
		this.m_handler  = i_handler;
	}

	@SuppressLint("UseValueOf")
	public void setRadioButtonID(int i_id)
	{
		Integer intOption = new Integer(i_id);
		m_options.add(intOption);
		RadioButton radio = (RadioButton)m_activity.findViewById(i_id);
		if(radio != null)
		{
			radio.setTag(intOption);
			radio.setOnClickListener(new OnClickListener()
            {
               public void onClick(View view)
               {
            	   Integer intOption = (Integer)view.getTag();
            	   exclusivelySetOption(intOption.intValue());
               }
            });
		}
	}

	public void exclusivelySetOption(int selectedOption)
	{
		// Cycle through all options
		for(int iIndex = 0; iIndex < m_options.size(); iIndex++)
		{
			int	rbid = m_options.get(iIndex).intValue();
			RadioButton radio = (RadioButton)m_activity.findViewById(rbid);
			if (radio != null)
			{
				//Check or uncheck as needed and inform Activity
				boolean checked = (rbid == selectedOption);
				radio.setChecked(checked);
				if(checked)
					postClickMessage(rbid);
			}
		}
	}

	private void postClickMessage(int i_checkedId)
	{
		Message msg = m_handler.obtainMessage(ON_RADIOBUTTON_CLICK, i_checkedId);
		msg.sendToTarget();

		/*
		final Handler handler =  new Handler(Looper.getMainLooper());
		Message msg = handler.obtainMessage(ON_RADIOBUTTON_CLICK, i_checkedId);
		handler.sendMessage(msg);
		*/

	}
}
