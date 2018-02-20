package com.tonimor.tcommons.qsort;

import java.io.File;
import java.util.Locale;

@SuppressWarnings("unused")
public class QSortFile extends QSort
{
	public QSortFile(Object[] values)
	{
		super.sort(values);
	}

	@Override
	public int compare(Object arg1, Object arg2)
	{
		File   f1 = (File)arg1;
		File   f2 = (File)arg2;
		String s1 = f1.toString().toLowerCase(Locale.getDefault());
		String s2 = f2.toString().toLowerCase(Locale.getDefault());
		int rc = s1.compareTo(s2);
		if(rc < 0)
			return -1;
		else if(rc > 0)
			return 1;
		return 0;
	}
}
