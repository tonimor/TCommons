package com.tonimor.tcommons.detail;

import android.database.SQLException;

@SuppressWarnings("unused")
public class PersistentException extends SQLException
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public PersistentException(String i_message)
	{
	     super(i_message);
	}
}
