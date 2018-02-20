package com.tonimor.tcommons;

import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

/* XML ASSOCIATED: entry_field.xml
 * ===============================
 * <?xml version="1.0" encoding="utf-8"?>
 * <EditText xmlns:android="http://schemas.android.com/apk/res/android"
 *     android:id="@+id/edit"
 *     android:layout_width="match_parent"
 *     android:layout_height="match_parent"
 *     android:gravity="center_vertical"
 *     android:inputType="text">
 * </EditText>
 *
 * USAGE FROM ACTIVITY XML
 * =======================
 * <fragment
 * 	class="com.ams.example.EntryField"
 *   	android:id="@+id/entryfield_name"
 *   	android:layout_width="match_parent"
 *   	android:layout_height="match_parent"
 *      android:tag="DDMMYYYY"/>
 *
 * USAGE FROM ACTIVITY CLASS
 * =========================
 *  public class MainActivity extends FragmentActivity implements CorreosListener {
 *
 *  @Override
 *  protected void onCreate(Bundle savedInstanceState)
 *  {
 *       super.onCreate(savedInstanceState);
 *       setContentView(R.layout.activity_main);
 *        EditField editfield =
 *       	(EditField)getSupportFragmentManager().findFragmentById(R.id.entry_field_name);
 *  }
 */

@SuppressWarnings("UnusedDeclaration")
public class EntryField extends Fragment
{
	final public static String m_MASK_INTEGER = "999999999999";
	final public static String m_MASK_DATE_1  = "DD-MM-AAAA";
	final public static String m_MASK_DATE_2  = "DD/MM/AAAA";
	final public static String m_MASK_DATE_3  = "[DD/MM/AAAA]";

	private EditText	m_edit = null;
	private String	 	m_mask = null;
	private String 		m_current = "";
	private Calendar	m_calendar = Calendar.getInstance();

    @Override
    public View onCreateView(
    	LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
    	View view;

    	m_mask = getTag();
    	if(m_MASK_INTEGER.substring(0, m_mask.length()).equals(m_mask))
    		view = inflater.inflate(R.layout.entry_field_integer, container, false);
    	else if(m_mask.equals(m_MASK_DATE_1))
    		view = inflater.inflate(R.layout.entry_field_date, container, false);
    	else if(m_mask.equals(m_MASK_DATE_2))
    		view = inflater.inflate(R.layout.entry_field_date, container, false);
    	else if(m_mask.equals(m_MASK_DATE_3))
    		view = inflater.inflate(R.layout.entry_field_date, container, false);
    	else
    		return null;

    	m_edit = (EditText)view.findViewById(R.id.edit);

        m_edit.addTextChangedListener(new TextWatcher()
		{
 		   	public void beforeTextChanged(CharSequence s, int start, int count, int after)
 		   	{
        		doBeforeChanged(s, start, count, after);
 		   	}

        	public void afterTextChanged(Editable s)
        	{
        		doAfterChanged(s);
        	}

        	public void onTextChanged(CharSequence s, int start, int before, int count)
        	{
        		if(m_mask == null)
        			return;

        		if(m_mask.equals(m_MASK_INTEGER))
        			doOnIntegerChanged(s, start, before, count);
        		else if(m_mask.equals(m_MASK_DATE_1) || m_mask.equals(m_MASK_DATE_2))
        			doOnDateChanged(s, start, before, count);
        		else if(m_mask.equals(m_MASK_DATE_3))
        			doOnDateChangedExt(s, start, before, count);
         	}
		});

        m_edit.setOnLongClickListener(new OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                m_current = "";
                m_edit.setText(m_current);
                return true;
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle state)
    {
        super.onActivityCreated(state);
    }

    public void setInputType(int i_inputType)
    {
    	m_edit.setInputType(i_inputType);
    }

    public void setHint(String i_hint)
    {
    	m_edit.setHint(i_hint);
    }

    public EditText getEdit()
    {
    	return m_edit;
    }

    public void setEditBackground(int i_drawableId)
    {
    	m_edit.setBackgroundResource(i_drawableId);
    }

    public void setTextSize(float i_size)
    {
    	m_edit.setTextSize(i_size);
    }

    public void setTextColor(int i_color)
    {
    	m_edit.setTextColor(i_color);
    }

    public void setHintTextColor(int i_color)
    {
    	m_edit.setHintTextColor(i_color);
    }

    public void setMargins(int i_left, int i_top, int i_right, int i_bottom)
    {
    	RelativeLayout.LayoutParams params =
    		(RelativeLayout.LayoutParams)m_edit.getLayoutParams();
        params.setMargins(i_left, i_top, i_right, i_bottom);
    	m_edit.setLayoutParams(params);
    }

    public void setEditLayoutWidth(int i_width)
    {
    	ViewGroup.LayoutParams params = m_edit.getLayoutParams();
    	params.width = i_width;
    	m_edit.setLayoutParams(params);
    }

    public void setEditEms(int i_ems)
    {
    	//  An em width is the width of the widest (M) character in the selected font
    	m_edit.setMinEms(i_ems);
    	m_edit.setMaxEms(i_ems);
    }

    public String getValue()
    {
    	return m_edit.getText().toString();
    }

    public void setValue(String i_text)
    {
    	m_edit.setText(i_text);
    }

    public final int getEditHeight(boolean i_toDP)
    {
    	DisplayMetrics displaymetrics = new DisplayMetrics();
    	Activity activity = getActivity();
    	WindowManager wndManager = activity.getWindowManager();
    	wndManager.getDefaultDisplay().getMetrics(displaymetrics);

    	int height_pix = m_edit.getHeight();
    	int height_dp = (160 * height_pix) / displaymetrics.densityDpi;
    	if(i_toDP)
    		return height_dp;
    	else
    		return height_pix;
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////
// internals

    private void doBeforeChanged(CharSequence i_cs, int i_start, int i_count, int i_after)
    {
    }

    private void doAfterChanged(Editable i_ed)
    {
    }

    private void doOnIntegerChanged(CharSequence i_cs, int i_start, int i_before, int i_count)
    {
    }

    private void doOnDateChanged(CharSequence i_cs, int i_start, int i_before, int i_count)
    {
        if (!i_cs.toString().equals(m_current))
        {
        	int 	pos = 0;
            String 	current = i_cs.toString().replaceAll("[^\\d]", "");
            String 	before  = m_current.replaceAll("[^\\d]", "");
            if(!current.isEmpty())
            {
            	final String blanks = "        "; // eitght blanks
	            pos = getOnDateCursorPosition(current, before);
	            if (current.length() < 8)
	            	current = current + blanks.substring(current.length());
	            if(!verifyDateFields(current))
	            {
	            	current = before + blanks.substring(before.length());
	            	pos = getOnDateCursorPosition(before, before);
	            }

	            String template = "%s-%s-%s";
	            if(m_mask.equals(m_MASK_DATE_1))
	            	template = "%s-%s-%s";
	            else if(m_mask.equals(m_MASK_DATE_2))
	            	template = "%s/%s/%s";

	            current = String.format(template,
	        		current.substring(0, 2),
	        		current.substring(2, 4),
	        		current.substring(4, 8));
            }
            m_current = current;
            m_edit.setText(m_current);
            m_edit.setSelection(pos < m_current.length() ? pos : m_current.length());
        }
    }

    private void doOnDateChangedExt(CharSequence i_cs, int i_start, int i_before, int i_count)
    {
        if (!i_cs.toString().equals(m_current))
        {
            String current = i_cs.toString().replaceAll("[^\\d]", "");
            String before  = m_current.replaceAll("[^\\d]", "");

            int cl = current.length();
            int sel = cl;
            for (int i = 2; i <= cl && i < 6; i += 2)
                sel++;
            if (current.equals(before)) // fix for pressing delete next to a forward separator
            	sel--;

            final String mask = "DDMMAAAA";
            if (current.length() < 8)
            	current = current + mask.substring(current.length());
            else
            {
               // This part makes sure that when we finish entering numbers
               // the date is correct, fixing it otherwise
               int day  = Integer.parseInt(current.substring(0,2));
               int mon  = Integer.parseInt(current.substring(2,4));
               int year = Integer.parseInt(current.substring(4,8));

               if(mon > 12) mon = 12;
               m_calendar.set(Calendar.MONTH, mon-1);
               day = (day > m_calendar.getActualMaximum(Calendar.DATE)) ?
            		   m_calendar.getActualMaximum(Calendar.DATE) : day;
               year = (year<1900)?1900:(year>2100) ? 2100 : year;
               current = String.format(Locale.getDefault(), "%02d%02d%02d",day, mon, year);
            }

            current = String.format("%s/%s/%s",
            		current.substring(0, 2),
            		current.substring(2, 4),
            		current.substring(4, 8));

            sel = sel < 0 ? 0 : sel;
            m_current = current;
            m_edit.setText(m_current);
            m_edit.setSelection(sel < m_current.length() ? sel : m_current.length());
        }
    }

    @SuppressWarnings("EmptyCatchBlock")
    private boolean verifyDateFields(String i_fields)
    {
    	String sd = i_fields.substring(0, 2).trim();
    	String sm = i_fields.substring(2, 4).trim();
    	String sy = i_fields.substring(4, 8).trim();

    	try
    	{
    		int d = Integer.parseInt(sd);
        	if(sd.length() == 1 && d > 3)
        		return false;
        	else if(d > 31 || d < 0)
    			return false;
    	}
        catch (NumberFormatException e){}

    	try
    	{
    		int m = Integer.parseInt(sm);
    		if(sm.length() == 1 && m > 1)
    			return false;
    		else if(m > 12  || m < 0)
    			return false;
    	}
        catch (NumberFormatException e){}

    	try
    	{
    		int y = Integer.parseInt(sy);
    		if(sy.length() == 1 && y > 2)
    			return false;
    		else if(y > 2100)
    			return false;
    	}
        catch (NumberFormatException e){}

    	return true;
    }

    private int getOnDateCursorPosition(String i_current, String i_before)
    {
    	int pos;
    	int clen = i_current.length();
    	int blen = i_before.length();
        switch(clen)
        {
        	case 0 : pos = 0; break;
        	case 1 : pos = 1; break;
        	case 2 : pos = (clen > blen ? 3 : 2); break;
        	case 3 : pos = 4; break;
        	case 4 : pos = (clen > blen ? 6 : 5); break;
        	case 5 : pos = 7; break;
        	case 6 : pos = 8; break;
        	case 7 : pos = 9; break;
        	default: pos = 10;break;
        }

        return pos;
    }
}
