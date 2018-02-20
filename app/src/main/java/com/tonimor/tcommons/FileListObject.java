package com.tonimor.tcommons;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.os.Environment;

public class FileListObject
{
	private List<File> 	m_fileList  = new ArrayList<File>();
	private File 		m_currentPath = null;
	private String 		m_fileEndsWith = null;
	private boolean 	m_onlyDirectories = false;

    public FileListObject(File i_path)
    {
        if (!i_path.exists())
        	i_path = Environment.getExternalStorageDirectory();
        loadFileList(i_path);
    }

	public long loadFileList(File i_path)
    {
        m_currentPath = i_path;
        m_fileList.clear();
        if (i_path.exists())
        {
            FilenameFilter filter = new FilenameFilter()
            {
				public boolean accept(File dir, String filename)
                {
                    File sel = new File(dir, filename);
                    if (!sel.canRead())
                    	return false;

                    if (m_onlyDirectories)
                    	return sel.isDirectory();
                    else
                    {
                        boolean endsWith = m_fileEndsWith != null ?
                        		filename.toLowerCase(Locale.getDefault()).endsWith(m_fileEndsWith) : true;
                        return endsWith || sel.isDirectory();
                    }
                }
            };

            File[] fileList = i_path.listFiles(filter);
            Collections.addAll(m_fileList, fileList);

            // QuickSort
            // http://www.vogella.com/tutorials/JavaAlgorithmsQuicksort/article.html
        }

        return m_fileList.size();
    }

	public File[] getFileList()
	{
		return (File[])m_fileList.toArray(new File[]{});
	}

	public List<String> getFileStringList()
	{
		List<String> list = new ArrayList<String>();
		for (File file : m_fileList)
		{
			list.add(file.getName());
		}

		return list;
	}

	public void setFileEndsWith(String i_string)
	{
		m_fileEndsWith = i_string != null ?
			i_string.toLowerCase(Locale.getDefault()) : i_string;
	}

    public void setOnlyDirectories(boolean i_onlyDirectories)
    {
    	m_onlyDirectories = i_onlyDirectories;
    }

    public File getCurrentFilePath()
    {
    	return m_currentPath;
    }

    public String getCurrentAbsolutePath()
    {
    	return m_currentPath.getAbsolutePath();
    }

    public File getFile(int i_position)
    {
    	return m_fileList.get(i_position);
    }

    public File getUpLevelFile(File i_file)
    {
    	return i_file.getParentFile();
    }

    public File getUpLevelCurrentPath()
    {
    	return m_currentPath.getParentFile();
    }

    public String getExtension(int i_position)
    {
    	File file = m_fileList.get(i_position);
    	if(file.isDirectory())
    		return "";
    	String[] tokens = file.toString().split("\\.(?=[^\\.]+$)");
    	if(tokens.length <= 1)
    		return "";
    	return tokens[tokens.length-1];
    }
}
