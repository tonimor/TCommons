package com.tonimor.tcommons.qsort;

@SuppressWarnings("unused")
public class QSortInt extends QSort
{
	public QSortInt(int[] values)
	{
		Integer[] array = new Integer[values.length];
		int i = 0;
		for (int value : values)
			array[i++] = value;
		super.sort(array);
	}

	public QSortInt(Object[] values)
	{
		super.sort(values);
	}

	@Override
	public int compare(Object arg1, Object arg2)
	{
		int i1 = (Integer)arg1;
		int i2 = (Integer)arg2;
		if(i1 > i2)
			return 1;
		else if(i1 < i2)
			return -1;
		return 0;
	}
}
