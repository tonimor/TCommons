package com.tonimor.tcommons.qsort;

import java.util.Locale;

@SuppressWarnings("unused")
public class QSortString extends QSort
{
	public QSortString(Object[] values)
	{
		super.sort(values);
	}

	@Override
	public int compare(Object arg1, Object arg2)
	{
		String s1 = ((String)arg1).toLowerCase(Locale.getDefault());
		String s2 = ((String)arg2).toLowerCase(Locale.getDefault());
		int rc = s1.compareTo(s2);
		if(rc < 0)
			return -1;
		else if(rc > 0)
			return 1;
		return 0;
	}
}
