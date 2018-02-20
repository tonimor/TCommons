package com.tonimor.tcommons.detail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GLauncher 
{
	final protected static String 	m_TAG 	= "GLauncher";

	private static GLauncher m_instance = null;

	private Map<String, String> m_keyMap = new HashMap<String, String>();
	
	public static GLauncher getInstance()
	{
		if(m_instance == null) {
			m_instance = new GLauncher();
		}
		return m_instance;
	}
	
	protected GLauncher()
	{
	}

	public String newKey()
	{
		// Generates unique Id
		String uniqueKeyID = UUID.randomUUID().toString();
		m_keyMap.put(uniqueKeyID, uniqueKeyID);
		return uniqueKeyID;
	}

	public void removeKey(String i_key)
	{
		m_keyMap.remove(i_key);
	}

	public Parcelable retrieveParcelable(Bundle i_bundle)
	{
		Parcelable parcel = null;
        for (Map.Entry<String, String> entry : m_keyMap.entrySet())
		{
			String key = entry.getKey();
			if((parcel = i_bundle.getParcelable(key)) != null)
			{
				removeKey(key);
				return parcel;
			}
		}

		return null;
	}

	public Bundle newBundle(Parcelable i_parcel)
	{
    	Bundle 	bundle = new Bundle();
    	bundle.putParcelable(newKey(), i_parcel);
    	return bundle;
	}

	public Intent newIntent(Parcelable i_parcel)
	{
		Intent intent = new Intent();
		Bundle bundle = newBundle(i_parcel);
		intent.putExtras(bundle);
		return intent;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Activity Launcher Method
	
	public void LaunchActivity(Class<?> i_activityClass, Activity i_from, int i_rc, Persistent i_persistent)
	{
		String key = "";
        Bundle bundle = new Bundle();
		if(i_persistent != null) {
			key = newKey();
			bundle.putParcelable(key, i_persistent);
		}
        Intent intent = new Intent(i_from, i_activityClass);
        intent.putExtras(bundle);
		
		try
		{
            i_from.startActivityForResult(intent, i_rc);
		}
		catch(Exception e)
		{
			removeKey(key);
			Log.e(m_TAG, Log.getStackTraceString(e));
			throw(e);
		}
	}

	public void LaunchActivity(Class<?> i_activityClass, Fragment i_from, int i_rc, Persistent i_persistent)
	{
		String key = "";
		Bundle bundle = new Bundle();
		if(i_persistent != null) {
			key = newKey();
			bundle.putParcelable(key, i_persistent);
		}
		Intent intent = new Intent(i_from.getActivity(), i_activityClass);
		intent.putExtras(bundle);

		try
		{
			i_from.startActivityForResult(intent, i_rc);
		}
		catch(Exception e)
		{
			removeKey(key);
			Log.e(m_TAG, Log.getStackTraceString(e));
			throw(e);
		}
	}
}
