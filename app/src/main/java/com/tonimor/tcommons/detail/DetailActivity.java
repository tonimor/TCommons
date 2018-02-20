package com.tonimor.tcommons.detail;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.tonimor.tcommons.EntryField;
import com.tonimor.tcommons.TC;

import java.lang.ref.WeakReference;

@SuppressWarnings("unused")
public class DetailActivity extends AppCompatActivity
{
    private static final String m_TAG 	= "DetailActivity";

	protected boolean m_titleProgressBar = false;

	public enum Mode {VIEW, CREATE, MODIFY}
	
	private Persistent 	m_persistent = null;
	protected Mode 		m_mode = null;
	protected boolean	m_protected = true;
	protected boolean	m_isNew = false;

	protected boolean   m_handleDefaultExceptions = true;

///////////////////////////////////////////////////////////////////////////////
// Handle messages

	final public static int ON_DETAIL_CREATED	= 31995;
	final public static int ON_DETAIL_SAVED 	= 31996;
    final public static int ON_DETAIL_RESULT	= 31997;
	final public static int ON_UPDATE_DETAIL	= 31998;
	final public static int ON_FINALIZE_DETAIL	= 31999;

    /**
     * Clase interna estática
     */
    private static class DetailHandler extends Handler
    {
        private final WeakReference<DetailActivity> m_wref;

        private DetailHandler(DetailActivity i_activity)
        {
            m_wref = new WeakReference<>(i_activity);
        }

        @Override
        public void handleMessage(Message i_msg)
        {
            /*
             * handleMessage() defines the operations to perform when
             * the Handler receives a new Message to process.
             */
            DetailActivity activity = m_wref.get();
            if (activity != null)
            {
                Object object = i_msg.obj;
                int arg1 	  = i_msg.arg1;
                int arg2 	  = i_msg.arg2;

                Bundle bundle;
                switch(i_msg.what)
                {
                    case ON_DETAIL_CREATED:
                        Bundle savedInstanceState = null;
                        if(object != null)
                            savedInstanceState = (Bundle)object;
                        activity.onDetailCreated(savedInstanceState);
                        break;

                    case ON_DETAIL_SAVED:
                        activity.onDetailSaved();
                        break;

                    case ON_DETAIL_RESULT:
                        bundle = (Bundle)object;
                        activity.onDetailResult(bundle, arg1, arg2);
                        break;

                    case ON_UPDATE_DETAIL:
                        activity.onUpdateDetail();
                        break;

                    case ON_FINALIZE_DETAIL:
						bundle = (Bundle)object;
                        activity.onFinalizeDetail(bundle, arg2);
                        break;

                    default:
                        activity.onUserMessage(i_msg.what, object, arg1, arg2);
                        break;
                }
            }

            super.handleMessage(i_msg);
        }
    }

    private final DetailHandler m_handler = new DetailHandler(this);

	protected void postMessage(int i_messageID, int i_arg1, int i_arg2, Object i_object)
	{
		Message msg = m_handler.obtainMessage(i_messageID, i_arg1, i_arg2, i_object);
		msg.sendToTarget();
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////
// activity methods

    @Override protected void onCreate(Bundle savedInstanceState)
    {
		// progress bar on title-bar
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		super.onCreate(savedInstanceState);

        int orientation = getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_PORTRAIT)
        {
        }
        else if(orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
        }

        postMessage(ON_DETAIL_CREATED, 0, 0, savedInstanceState);
    }

    @Override
	public void onConfigurationChanged(Configuration i_newConfig)
	{
		/*
		 * You also have to edit the appropriate element in your manifest
		 * file to include the android:configChanges Just see the code below:
		 * <activity android:name=".MyActivity"
		 *         android:configChanges="orientation|keyboardHidden"
		 *         android:label="@string/app_name">
		 * NOTE: with Android 3.2 (API level 13) or higher ,
		 * the "screen size" also changes when the device switches between
		 * portrait and landscape orientation. Thus, if you want to prevent
		 * runtime restarts due to orientation change when developing
		 * for API level 13 or higher, you must declare
		 * android:configChanges="orientation|screenSize" for
		 * API level 13 or higher.
		 */
	    super.onConfigurationChanged(i_newConfig);

	    // Checks the orientation of the screen
	    if (i_newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
			onConfigurationLandscape(i_newConfig);
	    else if (i_newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
			onConfigurationPortrait(i_newConfig);
	}

    @Override
    public void startActivityForResult(Intent i_intent, int i_requestCode)
    {
    	if(m_titleProgressBar)
    		activateTitleProgressBar(true);
        super.startActivityForResult(i_intent, i_requestCode);
    }

	@Override
	protected void onActivityResult(int i_requestCode, int i_resultCode, Intent i_data)
	{
		if(m_titleProgressBar)
			activateTitleProgressBar(false);

        Bundle bundle = null;
        if(i_data != null)
		    bundle = i_data.getExtras();

        postMessage(ON_DETAIL_RESULT, i_requestCode, i_resultCode, bundle);
	}

	@Override protected void onStart()
	{
		super.onStart();
	}

	@Override protected void onStop()
	{
		super.onStop();
	}

	@Override protected void onResume()
	{
		super.onResume();
	}

	@Override protected void onDestroy()
	{
		super.onDestroy();
	}

///////////////////////////////////////////////////////////////////////////////
// save & restore persistent state methods

	@Override
    protected void onSaveInstanceState(Bundle o_outState)
	{
		super.onSaveInstanceState(o_outState);
        try {
            doTheSets();
        }
        catch (DetailActivityException e)
        {
            Log.e(m_TAG, Log.getStackTraceString(e));
            TC.NotifyMessage(getApplicationContext(), e.getMessage(), 5000);
        }
        o_outState.putParcelable("onSaveInstanceState_PERSISTENT", m_persistent);
		o_outState.putSerializable("onSaveInstanceState_MODE", m_mode);
    }

	@Override
	protected void onRestoreInstanceState(Bundle i_savedInstanceState)
	{
		super.onRestoreInstanceState(i_savedInstanceState);
        m_persistent = i_savedInstanceState.getParcelable("onSaveInstanceState_PERSISTENT");
		m_mode = (Mode)i_savedInstanceState.getSerializable("onSaveInstanceState_MODE");
	}

///////////////////////////////////////////////////////////////////////////////
// detail methods managed by DetailHandler

    protected void onDetailCreated(Bundle savedInstanceState)
    {
        try
        {
            boolean mustDoTheGets = true;
            if(savedInstanceState == null)
            {
                Bundle bundle = getIntent().getExtras();
                if(bundle == null || bundle.isEmpty()) {
                    setMode(Mode.CREATE);
                    m_persistent = createPersistent();
                    mustDoTheGets = false;
                }
                else {
                    setMode(Mode.VIEW);
                    m_persistent = loadPersistent(bundle);
                }
            }
            else {
                m_persistent = restorePersistent(savedInstanceState);
            }

            if(m_persistent == null)
                throw new DetailActivityException("onDetilCreated null persistent");

            m_isNew = m_persistent.isNew();

            if(mustDoTheGets) {
                doTheGets();
            }
        }
        catch(DetailActivityException e)
        {
            Log.e(m_TAG, Log.getStackTraceString(e));
            TC.NotifyMessage(getApplicationContext(), e.getMessage(), 5000);
        }
        finally
        {
            setProtected(m_mode == Mode.VIEW);
            updateInterface();
        }
    }

    protected void onDetailSaved()
    {
        try
        {
            m_persistent.loadState();
            m_isNew = m_persistent.isNew();
            setMode(Mode.VIEW);
            doTheGets();
        }
        catch(DetailActivityException e)
        {
            Log.e(m_TAG, Log.getStackTraceString(e));
            TC.NotifyMessage(getApplicationContext(), e.getMessage(), 5000);
        }
        finally
        {
            setProtected(true);
            updateInterface();
        }
    }
	
    protected void onUpdateDetail()
	{
		updateInterface();
	}

	protected void onFinalizeDetail(Object i_object, int i_resultCode)
	{
		Bundle bundle = (Bundle)i_object;
		finalizeDetail(bundle, i_resultCode);
	}
	
///////////////////////////////////////////////////////////////////////////////	

	protected boolean saveDetail() throws DetailActivityException
	{
		boolean noErrors = true;

	    try
	    {
			verifyErrors();
			doTheSets();
	        m_persistent.saveState();
	        postMessage(ON_DETAIL_SAVED, 0, 0, m_persistent);
	    }
	    catch(DetailActivityException e)
	    {
	    	Log.e(m_TAG, Log.getStackTraceString(e));
	    	if(!m_handleDefaultExceptions)
                throw(e);
	    	TC.NotifyMessage(getApplicationContext(), e.getMessage(), 5000);
			noErrors = false;
	    }

		return noErrors;
	}
	
	protected Persistent getPersistent()
    {
        return m_persistent;
    }
	
	protected void setPersistent(Persistent i_persistent)
    {
        m_persistent = i_persistent;
    }

	protected void setMode(Mode i_mode)
	{
		m_mode = i_mode;
	}
	
	protected Mode getMode()
	{
		return m_mode;
	}
	
	protected void setProtected(boolean i_protected)
	{
		m_protected = i_protected;
	}
	
	protected boolean isNew()
	{
		return m_isNew;
	}

	protected void finalizeDetail(Bundle i_bundle, int i_resultCode)
	{
		Intent intent = new Intent();
		intent.putExtras(i_bundle);
		finalizeDetail(intent, i_resultCode);
	}

	protected void finalizeDetail(Intent i_intent, int i_resultCode)
	{
		setResult(i_resultCode, i_intent);
		finish();
	}

	protected void activateTitleProgressBar(boolean i_activate)
	{
		setProgressBarIndeterminateVisibility(i_activate);
	}

///////////////////////////////////////////////////////////////////////////////
// data exchange

    final protected boolean updateData(boolean i_ctrlToVar)
    {
        boolean result = true;
        try
        {
            doDataExchange(i_ctrlToVar);
        }
        catch(DetailActivityException e)
        {
            Log.e(m_TAG, Log.getStackTraceString(e));
            TC.NotifyMessage(getApplicationContext(), e.getMessage(), 5000);
            result = false;
        }

        return result;
    }

    protected void doDataExchange(boolean i_ctrlToVar) throws DetailActivityException {}

    @SuppressWarnings("UnusedDeclaration")
    final protected String ddx_TextView(
            boolean i_ctrlToVar, int i_ctrlID, String i_var) throws DetailActivityException
    {
        TextView textView = (TextView)findViewById(i_ctrlID);
        if(textView == null)
            throw new DetailActivityException("doDataExchange TextView exception");
        if(i_ctrlToVar)
            i_var = textView.getText().toString();
        else
            textView.setText(i_var);
        return i_var;
    }

    @SuppressWarnings("UnusedDeclaration")
    final protected int ddx_TextView(
            boolean i_ctrlToVar, int i_ctrlID, int i_var) throws DetailActivityException
    {
        TextView textView = (TextView)findViewById(i_ctrlID);
        if(textView == null)
            throw new DetailActivityException("doDataExchange TextView exception");
        if(i_ctrlToVar)
            i_var = Integer.parseInt(textView.getText().toString());
        else
            textView.setText(String.valueOf(i_var));
        return i_var;
    }

    final protected String ddx_EditText(
            boolean i_ctrlToVar, int i_ctrlID, String i_var) throws DetailActivityException
    {
        EditText editText = (EditText)findViewById(i_ctrlID);
        if(editText == null)
            throw new DetailActivityException("doDataExchange EditText exception");
        if(i_ctrlToVar)
            i_var = editText.getText().toString();
        else
            editText.setText(i_var);
        return i_var;
    }

    final protected int ddx_EditText(
            boolean i_ctrlToVar, int i_ctrlID, int i_var) throws DetailActivityException
    {
        EditText editText = (EditText)findViewById(i_ctrlID);
        if(editText == null)
            throw new DetailActivityException("doDataExchange EditText exception");
        if(i_ctrlToVar)
        {
            if(!editText.getText().toString().isEmpty())
                i_var = Integer.parseInt(editText.getText().toString());
            else
                i_var = 0;
        }
        else
            editText.setText(String.valueOf(i_var));
        return i_var;
    }

    final protected int ddx_RadioGroup(
            boolean i_ctrlToVar, int i_ctrlID, int i_var) throws DetailActivityException
    {
        RadioGroup radioGroup = (RadioGroup)findViewById(i_ctrlID);
        if(radioGroup == null)
            throw new DetailActivityException("doDataExchange RadioGroup exception");
        if(i_ctrlToVar)
            i_var = radioGroup.getCheckedRadioButtonId();
        else
            radioGroup.check(i_var);
        return i_var;
    }

    final protected int ddx_Spinner(
            boolean i_ctrlToVar, int i_ctrlID, int i_var) throws DetailActivityException
    {
        Spinner spinner = (Spinner)findViewById(i_ctrlID);
        if(spinner == null)
            throw new DetailActivityException("doDataExchange Spinner exception");
        if(i_ctrlToVar)
            i_var = spinner.getSelectedItemPosition();
            // i_var = position starting at 0, or INVALID_POSITION if there is nothing selected.
        else {
            if(i_var < 0 || i_var >= spinner.getCount())
                spinner.setSelection(Spinner.INVALID_POSITION);
            else
                spinner.setSelection(i_var);
        }
        return i_var;
    }

    final protected String ddx_EditField(
            boolean i_ctrlToVar, int i_ctrlID, String i_var) throws DetailActivityException
    {
        EntryField editField = (EntryField)getFragmentManager().findFragmentById(i_ctrlID);
        if(editField == null)
            throw new DetailActivityException("doDataExchange EditField exception");
        if(i_ctrlToVar)
            i_var = editField.getValue();
        else
            editField.setValue(i_var);
        return i_var;
    }

///////////////////////////////////////////////////////////////////////////////
// Overridables

	protected Persistent createPersistent()
	{
        return null;
	}
	
	protected Persistent loadPersistent(Bundle i_bundle)
	{
        return null;
	}
	
    protected Persistent restorePersistent(Bundle i_savedInstanceState) throws DetailActivityException
    {
        m_persistent = null;
        return null;
	}
	
    protected void doTheGets() throws DetailActivityException 
	{
		if(m_persistent == null)
			throw new DetailActivityException("onDetilCreated null persistent");	
	}
	
	protected void doTheSets() throws DetailActivityException 
	{
		if(m_persistent == null)
			throw new DetailActivityException("onDetilCreated null persistent");	
	}

    protected void verifyErrors() throws DetailActivityException
    {
    }
	
    protected void updateInterface()
	{
	}

	protected void onDetailResult(Bundle i_bundle, int i_requestCode, int i_resultCode)
    {
        switch(i_resultCode) {
            case RESULT_OK:
                onDetailResultOk(i_bundle, i_requestCode);
                break;
            case RESULT_CANCELED:
                onDetailResultCanceled(i_bundle, i_requestCode);
                break;
        }
    }

    protected void onDetailResultOk(Bundle i_bundle, int i_requestCode)
    {
    }

    protected void onDetailResultCanceled(Bundle i_bundle, int i_requestCode)
    {
    }

    protected void onConfigurationPortrait(Configuration i_newConfig)
	{
	}
	
	protected void onConfigurationLandscape(Configuration i_newConfig) 
	{
	}
				
	protected void onUserMessage(long i_messgeId, Object i_object, int i_arg1, int i_arg_2) 
	{
	}
}
