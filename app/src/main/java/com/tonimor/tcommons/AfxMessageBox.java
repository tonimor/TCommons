package com.tonimor.tcommons;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

@SuppressWarnings("UnusedDeclaration")
public class AfxMessageBox
{
	private Context m_context = null;
	private Handler m_handler = null;
	private long	m_reqcode = 0;

	final public static int ON_OK     = 32002;
	final public static int ON_CANCEL = 32003;

	public AfxMessageBox(Context i_context, String i_message, Handler i_handler, long i_reqcode)
	{
		m_context = i_context;
		m_handler = i_handler;
		m_reqcode = i_reqcode;
		AlertDialog alertDlg = buildAlertDialog(i_message, null);
		alertDlg.show();
	}

	public AfxMessageBox(Context i_context, String i_message, Drawable i_icon, Handler i_handler)
	{
		m_context = i_context;
		m_handler = i_handler;
		AlertDialog alertDlg = buildAlertDialog(i_message, i_icon);
		alertDlg.show();
	}

	private AlertDialog buildAlertDialog(String i_message, Drawable i_icon)
	{
	    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(m_context);
	    alertDialogBuilder.setMessage(i_message);
	    if(i_icon != null)
	    	alertDialogBuilder.setIcon(i_icon);

	    // OnClickListener para el boton "Aceptar"
	    DialogInterface.OnClickListener listenerOk = new DialogInterface.OnClickListener()
	    {
	        @Override
	        public void onClick(DialogInterface dialog, int which)
	        {
	    		Message msg = m_handler.obtainMessage(ON_OK, m_reqcode);
	    		msg.sendToTarget();
	        }
	    };

	    // OnClickListener para el boton "Cancelar"
	    DialogInterface.OnClickListener listenerCancelar = new DialogInterface.OnClickListener()
	    {
	        @Override
	        public void onClick(DialogInterface dialog, int which)
	        {
	    		Message msg = m_handler.obtainMessage(ON_CANCEL, m_reqcode);
	    		msg.sendToTarget();
	        }
	    };

	    // Asignamos los botones positivo y negativo a sus respectivos listeners
		String accept = m_context.getResources().getString(R.string.accept);
		String cancel =  m_context.getResources().getString(R.string.cancel);
	    alertDialogBuilder.setPositiveButton(accept, listenerOk);
	    alertDialogBuilder.setNegativeButton(cancel, listenerCancelar);
	    return alertDialogBuilder.create();
	}

}
