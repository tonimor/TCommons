package com.tonimor.tcommons;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

@SuppressWarnings("unused")
public class AssetsPlayer
{
    MediaPlayer p = null;
    Context m_ctx = null;

    public AssetsPlayer(Context i_ctx)
    {
    	m_ctx = i_ctx;
    }

    public void playSound(String fileName)
    {
        p = new MediaPlayer();
        try
        {
            AssetFileDescriptor afd = m_ctx.getAssets().openFd(fileName);
            p.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            p.setLooping(false);
            afd.close();
            p.prepare();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        p.start();
    }
}
