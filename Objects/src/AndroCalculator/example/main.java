package AndroCalculator.example;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "AndroCalculator.example", "AndroCalculator.example.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "AndroCalculator.example", "AndroCalculator.example.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "AndroCalculator.example.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public anywheresoftware.b4a.objects.ButtonWrapper _cmd0 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _cmd1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _cmd2 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _cmd3 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _cmd4 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _cmd5 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _cmd6 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _cmd7 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _cmd8 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _cmd9 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _cmdadd = null;
public anywheresoftware.b4a.objects.ButtonWrapper _cmdbackspace = null;
public anywheresoftware.b4a.objects.ButtonWrapper _cmdclearall = null;
public anywheresoftware.b4a.objects.ButtonWrapper _cmdclearentry = null;
public anywheresoftware.b4a.objects.ButtonWrapper _cmddecimal = null;
public anywheresoftware.b4a.objects.ButtonWrapper _cmddivide = null;
public anywheresoftware.b4a.objects.ButtonWrapper _cmdequal = null;
public anywheresoftware.b4a.objects.ButtonWrapper _cmdinverse = null;
public anywheresoftware.b4a.objects.ButtonWrapper _cmdmultiply = null;
public anywheresoftware.b4a.objects.ButtonWrapper _cmdpowerof = null;
public anywheresoftware.b4a.objects.ButtonWrapper _cmdsign = null;
public anywheresoftware.b4a.objects.ButtonWrapper _cmdsqrroot = null;
public anywheresoftware.b4a.objects.ButtonWrapper _cmdsubtract = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtinput = null;
public static double _str1 = 0;
public static double _str2 = 0;
public static String _str = "";
public static boolean _equalaction = false;
public AndroCalculator.example.starter _starter = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 54;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 57;BA.debugLine="Activity.LoadLayout(\"frmdesign\")";
mostCurrent._activity.LoadLayout("frmdesign",mostCurrent.activityBA);
 //BA.debugLineNum = 59;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 65;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 67;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 61;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 63;BA.debugLine="End Sub";
return "";
}
public static String  _cmd0_click() throws Exception{
 //BA.debugLineNum = 223;BA.debugLine="Sub cmd0_Click";
 //BA.debugLineNum = 224;BA.debugLine="If EqualAction = True Then";
if (_equalaction==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 225;BA.debugLine="txtInput.Text = \"\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(""));
 };
 //BA.debugLineNum = 227;BA.debugLine="txtInput.Text = txtInput.Text & \"0.0\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(mostCurrent._txtinput.getText()+"0.0"));
 //BA.debugLineNum = 228;BA.debugLine="EqualAction = False";
_equalaction = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 229;BA.debugLine="End Sub";
return "";
}
public static String  _cmd1_click() throws Exception{
 //BA.debugLineNum = 215;BA.debugLine="Sub cmd1_Click";
 //BA.debugLineNum = 216;BA.debugLine="If EqualAction = True Then";
if (_equalaction==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 217;BA.debugLine="txtInput.Text = \"\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(""));
 };
 //BA.debugLineNum = 219;BA.debugLine="txtInput.Text = txtInput.Text & \"1.0\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(mostCurrent._txtinput.getText()+"1.0"));
 //BA.debugLineNum = 220;BA.debugLine="EqualAction = False";
_equalaction = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 221;BA.debugLine="End Sub";
return "";
}
public static String  _cmd2_click() throws Exception{
 //BA.debugLineNum = 207;BA.debugLine="Sub cmd2_Click";
 //BA.debugLineNum = 208;BA.debugLine="If EqualAction = True Then";
if (_equalaction==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 209;BA.debugLine="txtInput.Text = \"\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(""));
 };
 //BA.debugLineNum = 211;BA.debugLine="txtInput.Text = txtInput.Text & \"2.0\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(mostCurrent._txtinput.getText()+"2.0"));
 //BA.debugLineNum = 212;BA.debugLine="EqualAction = False";
_equalaction = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 213;BA.debugLine="End Sub";
return "";
}
public static String  _cmd3_click() throws Exception{
 //BA.debugLineNum = 199;BA.debugLine="Sub cmd3_Click";
 //BA.debugLineNum = 200;BA.debugLine="If EqualAction = True Then";
if (_equalaction==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 201;BA.debugLine="txtInput.Text = \"\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(""));
 };
 //BA.debugLineNum = 203;BA.debugLine="txtInput.Text = txtInput.Text & \"3.0\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(mostCurrent._txtinput.getText()+"3.0"));
 //BA.debugLineNum = 204;BA.debugLine="EqualAction = False";
_equalaction = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 205;BA.debugLine="End Sub";
return "";
}
public static String  _cmd4_click() throws Exception{
 //BA.debugLineNum = 191;BA.debugLine="Sub cmd4_Click";
 //BA.debugLineNum = 192;BA.debugLine="If EqualAction = True Then";
if (_equalaction==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 193;BA.debugLine="txtInput.Text = \"\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(""));
 };
 //BA.debugLineNum = 195;BA.debugLine="txtInput.Text = txtInput.Text & \"4.0\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(mostCurrent._txtinput.getText()+"4.0"));
 //BA.debugLineNum = 196;BA.debugLine="EqualAction = False";
_equalaction = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 197;BA.debugLine="End Sub";
return "";
}
public static String  _cmd5_click() throws Exception{
 //BA.debugLineNum = 183;BA.debugLine="Sub cmd5_Click";
 //BA.debugLineNum = 184;BA.debugLine="If EqualAction = True Then";
if (_equalaction==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 185;BA.debugLine="txtInput.Text = \"\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(""));
 };
 //BA.debugLineNum = 187;BA.debugLine="txtInput.Text = txtInput.Text & \"5.0\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(mostCurrent._txtinput.getText()+"5.0"));
 //BA.debugLineNum = 188;BA.debugLine="EqualAction = False";
_equalaction = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 189;BA.debugLine="End Sub";
return "";
}
public static String  _cmd6_click() throws Exception{
 //BA.debugLineNum = 175;BA.debugLine="Sub cmd6_Click";
 //BA.debugLineNum = 176;BA.debugLine="If EqualAction = True Then";
if (_equalaction==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 177;BA.debugLine="txtInput.Text = \"\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(""));
 };
 //BA.debugLineNum = 179;BA.debugLine="txtInput.Text = txtInput.Text & \"6.0\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(mostCurrent._txtinput.getText()+"6.0"));
 //BA.debugLineNum = 180;BA.debugLine="EqualAction = False";
_equalaction = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 181;BA.debugLine="End Sub";
return "";
}
public static String  _cmd7_click() throws Exception{
 //BA.debugLineNum = 167;BA.debugLine="Sub cmd7_Click";
 //BA.debugLineNum = 168;BA.debugLine="If EqualAction = True Then";
if (_equalaction==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 169;BA.debugLine="txtInput.Text = \"\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(""));
 };
 //BA.debugLineNum = 171;BA.debugLine="txtInput.Text = txtInput.Text & \"7.0\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(mostCurrent._txtinput.getText()+"7.0"));
 //BA.debugLineNum = 172;BA.debugLine="EqualAction = False";
_equalaction = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 173;BA.debugLine="End Sub";
return "";
}
public static String  _cmd8_click() throws Exception{
 //BA.debugLineNum = 159;BA.debugLine="Sub cmd8_Click";
 //BA.debugLineNum = 160;BA.debugLine="If EqualAction = True Then";
if (_equalaction==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 161;BA.debugLine="txtInput.Text = \"\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(""));
 };
 //BA.debugLineNum = 163;BA.debugLine="txtInput.Text = txtInput.Text & \"8.0\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(mostCurrent._txtinput.getText()+"8.0"));
 //BA.debugLineNum = 164;BA.debugLine="EqualAction = False";
_equalaction = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 165;BA.debugLine="End Sub";
return "";
}
public static String  _cmd9_click() throws Exception{
 //BA.debugLineNum = 151;BA.debugLine="Sub cmd9_Click";
 //BA.debugLineNum = 152;BA.debugLine="If EqualAction = True Then";
if (_equalaction==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 153;BA.debugLine="txtInput.Text = \"\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(""));
 };
 //BA.debugLineNum = 155;BA.debugLine="txtInput.Text = txtInput.Text & \"9.0\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(mostCurrent._txtinput.getText()+"9.0"));
 //BA.debugLineNum = 156;BA.debugLine="EqualAction = False";
_equalaction = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 157;BA.debugLine="End Sub";
return "";
}
public static String  _cmdadd_click() throws Exception{
 //BA.debugLineNum = 145;BA.debugLine="Sub cmdAdd_Click";
 //BA.debugLineNum = 146;BA.debugLine="str1 = (txtInput.Text)";
_str1 = (double)(Double.parseDouble((mostCurrent._txtinput.getText())));
 //BA.debugLineNum = 147;BA.debugLine="txtInput.Text = \"\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 148;BA.debugLine="str = cmdAdd.Text";
mostCurrent._str = mostCurrent._cmdadd.getText();
 //BA.debugLineNum = 149;BA.debugLine="End Sub";
return "";
}
public static String  _cmdbackspace_click() throws Exception{
int _loc = 0;
 //BA.debugLineNum = 231;BA.debugLine="Sub cmdbackspace_Click";
 //BA.debugLineNum = 232;BA.debugLine="Dim loc As Int";
_loc = 0;
 //BA.debugLineNum = 233;BA.debugLine="If txtInput.Text.Length > 0 Then";
if (mostCurrent._txtinput.getText().length()>0) { 
 //BA.debugLineNum = 234;BA.debugLine="loc = txtInput.Text.Length";
_loc = mostCurrent._txtinput.getText().length();
 };
 //BA.debugLineNum = 237;BA.debugLine="End Sub";
return "";
}
public static String  _cmdclearall_click() throws Exception{
 //BA.debugLineNum = 141;BA.debugLine="Sub cmdClearAll_Click";
 //BA.debugLineNum = 142;BA.debugLine="txtInput.Text = \"\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 143;BA.debugLine="End Sub";
return "";
}
public static String  _cmdclearentry_click() throws Exception{
 //BA.debugLineNum = 137;BA.debugLine="Sub cmdClearEntry_Click";
 //BA.debugLineNum = 138;BA.debugLine="txtInput.Text = \"\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 139;BA.debugLine="End Sub";
return "";
}
public static String  _cmddecimal_click() throws Exception{
 //BA.debugLineNum = 122;BA.debugLine="Sub cmdDecimal_Click";
 //BA.debugLineNum = 123;BA.debugLine="If txtInput.Text = \"0.\" Then";
if ((mostCurrent._txtinput.getText()).equals("0.")) { 
 //BA.debugLineNum = 124;BA.debugLine="txtInput.Text = \".\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence("."));
 }else if((mostCurrent._txtinput.getText()).equals(BA.NumberToString(_str2))) { 
 //BA.debugLineNum = 126;BA.debugLine="txtInput.Text = \".\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence("."));
 }else if((mostCurrent._txtinput.getText()).equals(mostCurrent._str)) { 
 //BA.debugLineNum = 128;BA.debugLine="txtInput.Text = \".\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence("."));
 }else {
 //BA.debugLineNum = 130;BA.debugLine="If txtInput.Text.Contains(\".\") Then";
if (mostCurrent._txtinput.getText().contains(".")) { 
 }else {
 //BA.debugLineNum = 132;BA.debugLine="txtInput.Text = txtInput.Text & \".\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(mostCurrent._txtinput.getText()+"."));
 };
 };
 //BA.debugLineNum = 135;BA.debugLine="End Sub";
return "";
}
public static String  _cmddivide_click() throws Exception{
 //BA.debugLineNum = 116;BA.debugLine="Sub cmdDivide_Click";
 //BA.debugLineNum = 117;BA.debugLine="str1 = (txtInput.Text)";
_str1 = (double)(Double.parseDouble((mostCurrent._txtinput.getText())));
 //BA.debugLineNum = 118;BA.debugLine="txtInput.Text = \"\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 119;BA.debugLine="str = cmdDivide.Text";
mostCurrent._str = mostCurrent._cmddivide.getText();
 //BA.debugLineNum = 120;BA.debugLine="End Sub";
return "";
}
public static String  _cmdequal_click() throws Exception{
 //BA.debugLineNum = 100;BA.debugLine="Sub cmdEqual_Click";
 //BA.debugLineNum = 101;BA.debugLine="str2 = (txtInput.Text)";
_str2 = (double)(Double.parseDouble((mostCurrent._txtinput.getText())));
 //BA.debugLineNum = 102;BA.debugLine="If str = \"-\" Then";
if ((mostCurrent._str).equals("-")) { 
 //BA.debugLineNum = 103;BA.debugLine="txtInput.Text = str1 - str2";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(_str1-_str2));
 }else if((mostCurrent._str).equals("+")) { 
 //BA.debugLineNum = 105;BA.debugLine="txtInput.Text = str1 + str2";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(_str1+_str2));
 }else if((mostCurrent._str).equals("*")) { 
 //BA.debugLineNum = 107;BA.debugLine="txtInput.Text = str1 * str2";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(_str1*_str2));
 }else if((mostCurrent._str).equals("/")) { 
 //BA.debugLineNum = 109;BA.debugLine="txtInput.Text = str1 / str2";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(_str1/(double)_str2));
 }else if((mostCurrent._str).equals("PowerOf")) { 
 //BA.debugLineNum = 111;BA.debugLine="txtInput.Text = Power( str1 , str2 )";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Power(_str1,_str2)));
 };
 //BA.debugLineNum = 113;BA.debugLine="EqualAction = True";
_equalaction = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 114;BA.debugLine="End Sub";
return "";
}
public static String  _cmdinverse_click() throws Exception{
 //BA.debugLineNum = 96;BA.debugLine="Sub cmdInverse_Click";
 //BA.debugLineNum = 97;BA.debugLine="txtInput.Text = \"1\" / txtInput.Text";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence((double)(Double.parseDouble("1"))/(double)(double)(Double.parseDouble(mostCurrent._txtinput.getText()))));
 //BA.debugLineNum = 98;BA.debugLine="End Sub";
return "";
}
public static String  _cmdmultiply_click() throws Exception{
 //BA.debugLineNum = 90;BA.debugLine="Sub cmdMultiply_Click";
 //BA.debugLineNum = 91;BA.debugLine="str1 = (txtInput.Text)";
_str1 = (double)(Double.parseDouble((mostCurrent._txtinput.getText())));
 //BA.debugLineNum = 92;BA.debugLine="txtInput.Text = \"\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 93;BA.debugLine="str = cmdMultiply.Text";
mostCurrent._str = mostCurrent._cmdmultiply.getText();
 //BA.debugLineNum = 94;BA.debugLine="End Sub";
return "";
}
public static String  _cmdpowerof_click() throws Exception{
 //BA.debugLineNum = 84;BA.debugLine="Sub cmdPowerOf_Click";
 //BA.debugLineNum = 85;BA.debugLine="str1 = (txtInput.Text)";
_str1 = (double)(Double.parseDouble((mostCurrent._txtinput.getText())));
 //BA.debugLineNum = 86;BA.debugLine="txtInput.Text = \"\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 87;BA.debugLine="str = \"PowerOf\"";
mostCurrent._str = "PowerOf";
 //BA.debugLineNum = 88;BA.debugLine="End Sub";
return "";
}
public static String  _cmdsign_click() throws Exception{
 //BA.debugLineNum = 80;BA.debugLine="Sub cmdSign_Click";
 //BA.debugLineNum = 81;BA.debugLine="txtInput.Text = \"-1\" * txtInput.Text";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence((double)(Double.parseDouble("-1"))*(double)(Double.parseDouble(mostCurrent._txtinput.getText()))));
 //BA.debugLineNum = 82;BA.debugLine="End Sub";
return "";
}
public static String  _cmdsqrroot_click() throws Exception{
 //BA.debugLineNum = 74;BA.debugLine="Sub cmdSqrRoot_Click";
 //BA.debugLineNum = 75;BA.debugLine="str1 = (txtInput.Text)";
_str1 = (double)(Double.parseDouble((mostCurrent._txtinput.getText())));
 //BA.debugLineNum = 76;BA.debugLine="str1 = Sqrt(str1)";
_str1 = anywheresoftware.b4a.keywords.Common.Sqrt(_str1);
 //BA.debugLineNum = 77;BA.debugLine="txtInput.Text = str1";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(_str1));
 //BA.debugLineNum = 78;BA.debugLine="End Sub";
return "";
}
public static String  _cmdsubtract_click() throws Exception{
 //BA.debugLineNum = 68;BA.debugLine="Sub cmdSubtract_Click";
 //BA.debugLineNum = 69;BA.debugLine="str1 = (txtInput.Text)";
_str1 = (double)(Double.parseDouble((mostCurrent._txtinput.getText())));
 //BA.debugLineNum = 70;BA.debugLine="txtInput.Text = \"\"";
mostCurrent._txtinput.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 71;BA.debugLine="str = cmdSubtract.Text";
mostCurrent._str = mostCurrent._cmdsubtract.getText();
 //BA.debugLineNum = 72;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 20;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 24;BA.debugLine="Private cmd0 As Button";
mostCurrent._cmd0 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Private cmd1 As Button";
mostCurrent._cmd1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Private cmd2 As Button";
mostCurrent._cmd2 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Private cmd3 As Button";
mostCurrent._cmd3 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Private cmd4 As Button";
mostCurrent._cmd4 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Private cmd5 As Button";
mostCurrent._cmd5 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Private cmd6 As Button";
mostCurrent._cmd6 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Private cmd7 As Button";
mostCurrent._cmd7 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Private cmd8 As Button";
mostCurrent._cmd8 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Private cmd9 As Button";
mostCurrent._cmd9 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 34;BA.debugLine="Private cmdAdd As Button";
mostCurrent._cmdadd = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 35;BA.debugLine="Private cmdBackspace As Button";
mostCurrent._cmdbackspace = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 36;BA.debugLine="Private cmdClearAll As Button";
mostCurrent._cmdclearall = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 37;BA.debugLine="Private cmdClearEntry As Button";
mostCurrent._cmdclearentry = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 38;BA.debugLine="Private cmdDecimal As Button";
mostCurrent._cmddecimal = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 39;BA.debugLine="Private cmdDivide As Button";
mostCurrent._cmddivide = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 40;BA.debugLine="Private cmdEqual As Button";
mostCurrent._cmdequal = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 41;BA.debugLine="Private cmdInverse As Button";
mostCurrent._cmdinverse = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 42;BA.debugLine="Private cmdMultiply As Button";
mostCurrent._cmdmultiply = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 43;BA.debugLine="Private cmdPowerOf As Button";
mostCurrent._cmdpowerof = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 44;BA.debugLine="Private cmdSign As Button";
mostCurrent._cmdsign = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 45;BA.debugLine="Private cmdSqrRoot As Button";
mostCurrent._cmdsqrroot = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 46;BA.debugLine="Private cmdSubtract As Button";
mostCurrent._cmdsubtract = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 47;BA.debugLine="Private txtInput As EditText";
mostCurrent._txtinput = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 48;BA.debugLine="Dim str1 As Double";
_str1 = 0;
 //BA.debugLineNum = 49;BA.debugLine="Dim str2 As Double";
_str2 = 0;
 //BA.debugLineNum = 50;BA.debugLine="Dim str As String";
mostCurrent._str = "";
 //BA.debugLineNum = 51;BA.debugLine="Dim EqualAction As Boolean";
_equalaction = false;
 //BA.debugLineNum = 52;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
starter._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 18;BA.debugLine="End Sub";
return "";
}
}
