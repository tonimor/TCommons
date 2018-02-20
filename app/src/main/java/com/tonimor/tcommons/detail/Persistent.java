package com.tonimor.tcommons.detail;

import android.database.Cursor;
import android.database.SQLException;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

@SuppressWarnings("unused")
public class Persistent implements Parcelable
{
	public static final String m_TAG = "Persistent";
	public static final int NULL_DBOID = -1;

	private static HashMap<String, String> m_infoMap = new  HashMap<String, String>();
	
	private long m_dboid  = NULL_DBOID;
	private int  m_typeId = 0;

	public Persistent()
	{
	}

	public long getOid()
	{
		return m_dboid;
	}

	public void setOid(long i_dboid)
	{
		m_dboid = i_dboid;
	}

	@SuppressWarnings("UnusedDeclaration")
    public int getTypeId()
	{
		return m_typeId;
	}

	public void setTypeId(int i_typeId)
	{
		m_typeId = i_typeId;
	}

	public boolean isNew()
	{
		return (m_dboid == NULL_DBOID);
	}

	@SuppressWarnings("UnusedDeclaration")
    public String getKey()
	{
		return String.format(Locale.getDefault(), "PERSISTENT_%d", m_typeId);
	}

//////////////////////////////////////////////////////////////////////////////////
// database interfaces

	public void loadState() throws SQLException
	{
		try
		{
			dbRead();
		}
		catch(SQLException e)
		{
			Log.e(m_TAG, Log.getStackTraceString(e));
			throw(e);
		}
	}

	public void saveState() throws SQLException
	{
		try
		{
			verifyErrors();
			
			if(isNew())
				m_dboid = dbInsert();
			else
				dbUpdate();
		}
		catch(SQLException e)
		{
			Log.e(m_TAG, Log.getStackTraceString(e));
			throw(e);
		}
	}

	public void deleteState() throws SQLException
	{
		try
		{
			dbDelete();
		}
		catch(SQLException e)
		{
			Log.e(m_TAG, Log.getStackTraceString(e));
			throw(e);
		}
	}
	
	public boolean equals(Persistent i_persistent)
	{
		if(i_persistent != null && i_persistent.getTypeId() == this.getTypeId())
			return (i_persistent.getOid() == this.getOid());
		return false;
	}

	protected void		 verifyErrors() throws PersistentException {}
	
	protected Cursor dbOpenCursor(String i_condition, String[] i_fields, String i_orderBy){ return null; }
	protected Persistent dbGetNext(Cursor io_cursor){ return null; }  
	
	protected long 		 dbCount(){ return 0; }
	protected boolean 	 dbRead(){ return false; }
	protected long 		 dbInsert(){ return NULL_DBOID; }
	protected void 		 dbUpdate(){}
	protected void 		 dbDelete(){}

//////////////////////////////////////////////////////////////////////////////////
// Generic info storage

    public static void keepInfo(String i_key, String i_info)
    {
        m_infoMap.put(i_key, i_info);
    }

    public static String retrieveInfo(String i_key)
    {
        String info = null;
        if(m_infoMap.containsKey(i_key)) {
            info = m_infoMap.get(i_key);
            removeInfo(i_key);
        }
        return info;
    }

    public static void removeInfo(String i_key)
    {
        m_infoMap.remove(i_key);
    }

//////////////////////////////////////////////////////////////////////////////////
// Parcelable methods

    public Persistent(Parcel in)
    {
        readFromParcel(in);
    }

    public static final Parcelable.Creator<Persistent> CREATOR = new Parcelable.Creator<Persistent>()
    {
        public Persistent createFromParcel(Parcel in)
        {
            return new Persistent(in);
        }

        public Persistent[] newArray(int size)
        {
            return new Persistent[size];
        }
    };

    public void readFromParcel(Parcel in)
    {
    	m_dboid	= in.readLong();
    	m_typeId = in.readInt();
    }

    public void writeToParcel(Parcel out, int flags)
    {
    	out.writeLong(m_dboid);
    	out.writeInt(m_typeId);
    }

    public int describeContents()
    {
        return 0;
    }

////////////////////////////////////////////////////////////////////////////
}
