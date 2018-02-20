package com.tonimor.tcommons.qsort;

/* Usage:
 * File[] arrayToSort;
 * QSortFile qs = new QSortFile(arrayToSort);
 * Object[] arraySorted = qs.getSortedArray();
 */

@SuppressWarnings("unused")
public abstract class QSort
{
	private Object[] m_array;

	public Object[] getSortedArray()
	{
		return m_array;
	}

	protected void sort(Object[] values)
	{
		// check for empty or null array
	    if (values == null || values.length==0)
	      return;

	    m_array = values;
	    int length = values.length;
	    quicksort(0, length-1);
	}

	private void quicksort(int low, int high)
	{
		int i = low, j = high;

	    // Get the pivot element from the middle of the list
	    Object pivot = m_array[low + (high-low)/2];

	    // Divide into two lists
	    while(i <= j)
	    {
	    	// If the current value from the left list is smaller then the pivot
	    	// element then get the next element from the left list
	    	while(compare(m_array[i], pivot) == -1)
	    	{
	    		i++;
	    	}

	    	// If the current value from the right list is larger then the pivot
	    	// element then get the next element from the right list
	    	while(compare(m_array[j], pivot) == 1)
	    	{
	    		j--;
	    	}

	    	// If we have found a values in the left list which is larger then
	    	// the pivot element and if we have found a value in the right list
	    	// which is smaller then the pivot element then we exchange the
	    	// values.
	    	// As we are done we can increase i and j
	    	if (i <= j)
	    	{
	    		exchange(i, j);
	    		i++;
	    		j--;
	    	}
	    } // end while

	    // Recursion
	    if(low < j)
	      quicksort(low, j);
	    if(i < high)
	      quicksort(i, high);
	  }

	  private void exchange(int i, int j)
	  {
		  Object temp = m_array[i];
		  m_array[i] = m_array[j];
		  m_array[j] = temp;
	  }

	  abstract protected int compare(Object arg1, Object arg2);
}
