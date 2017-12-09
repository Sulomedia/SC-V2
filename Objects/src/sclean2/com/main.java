package sclean2.com;


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

public class main extends android.support.v7.app.ActionBarActivity implements B4AActivity{
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
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "sclean2.com", "sclean2.com.main");
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
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
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
		activityBA = new BA(this, layout, processBA, "sclean2.com", "sclean2.com.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "sclean2.com.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
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
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
public static com.rootsoft.oslibrary.OSLibrary _os = null;
public static String _date = "";
public static String _time1 = "";
public static anywheresoftware.b4a.keywords.constants.TypefaceWrapper _rfont = null;
public static String _package = "";
public static anywheresoftware.b4a.objects.Timer _t1 = null;
public anywheresoftware.b4a.phone.PackageManagerWrapper _pack = null;
public com.tchart.materialcolors.MaterialColors _mcl = null;
public sclean2.com.keyvaluestore _kvst = null;
public sclean2.com.keyvaluestore _kvsdata = null;
public sclean2.com.keyvaluestore _alist = null;
public sclean2.com.keyvaluestore _dbase = null;
public sclean2.com.keyvaluestore _abase = null;
public sclean2.com.keyvaluestore _qbase = null;
public de.amberhome.objects.appcompat.ACToolbarLightWrapper _toolbar = null;
public de.amberhome.objects.appcompat.ACActionBar _abhelper = null;
public de.amberhome.objects.appcompat.ACMenuWrapper _acm = null;
public anywheresoftware.b4a.objects.ListViewWrapper _leftlist = null;
public anywheresoftware.b4a.objects.ListViewWrapper _lv1 = null;
public anywheresoftware.b4a.objects.SlidingMenuWrapper _sm = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel1 = null;
public com.circlebuttonwrapper.CircleButtonWrapper _cb1 = null;
public de.amberhome.objects.appcompat.ACButtonWrapper _ac1 = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _sico = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _bm1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _stext = null;
public anywheresoftware.b4a.objects.LabelWrapper _subtext = null;
public static int _counter = 0;
public sclean2.com.starter _starter = null;
public sclean2.com.cmodul _cmodul = null;
public sclean2.com.animator _animator = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _ac1_click() throws Exception{
 //BA.debugLineNum = 220;BA.debugLine="Sub ac1_Click";
 //BA.debugLineNum = 221;BA.debugLine="CallSub(cmodul,\"start\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(mostCurrent._cmodul.getObject()),"start");
 //BA.debugLineNum = 222;BA.debugLine="bm1.SetVisibleAnimated(200,True)";
mostCurrent._bm1.SetVisibleAnimated((int) (200),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 223;BA.debugLine="ac1.SetVisibleAnimated(100,False)";
mostCurrent._ac1.SetVisibleAnimated((int) (100),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 224;BA.debugLine="Panel1.SetVisibleAnimated(100,False)";
mostCurrent._panel1.SetVisibleAnimated((int) (100),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 225;BA.debugLine="End Sub";
return "";
}
public static String  _acb_method() throws Exception{
 //BA.debugLineNum = 179;BA.debugLine="Sub acb_method";
 //BA.debugLineNum = 180;BA.debugLine="ac1.Text=\"Scan\"";
mostCurrent._ac1.setText(BA.ObjectToCharSequence("Scan"));
 //BA.debugLineNum = 181;BA.debugLine="ac1.Typeface=rfont";
mostCurrent._ac1.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 182;BA.debugLine="ac1.TextSize=25";
mostCurrent._ac1.setTextSize((float) (25));
 //BA.debugLineNum = 183;BA.debugLine="End Sub";
return "";
}
public static String  _activity_create(boolean _firsttime) throws Exception{
anywheresoftware.b4a.objects.PanelWrapper _lftmenu = null;
int _offset = 0;
anywheresoftware.b4a.objects.LabelWrapper _la1 = null;
anywheresoftware.b4a.objects.LabelWrapper _la2 = null;
 //BA.debugLineNum = 56;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 58;BA.debugLine="Activity.LoadLayout(\"1\")";
mostCurrent._activity.LoadLayout("1",mostCurrent.activityBA);
 //BA.debugLineNum = 59;BA.debugLine="abhelper.Initialize";
mostCurrent._abhelper.Initialize(mostCurrent.activityBA);
 //BA.debugLineNum = 60;BA.debugLine="t1.Initialize(\"t1\",1000)";
_t1.Initialize(processBA,"t1",(long) (1000));
 //BA.debugLineNum = 61;BA.debugLine="t1.Enabled=False";
_t1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 62;BA.debugLine="Activity.TitleColor=mcl.md_white_1000";
mostCurrent._activity.setTitleColor(mostCurrent._mcl.getmd_white_1000());
 //BA.debugLineNum = 63;BA.debugLine="Activity.Title=pack.GetApplicationLabel(package)";
mostCurrent._activity.setTitle(BA.ObjectToCharSequence(mostCurrent._pack.GetApplicationLabel(_package)));
 //BA.debugLineNum = 64;BA.debugLine="toolbar.SetAsActionBar";
mostCurrent._toolbar.SetAsActionBar(mostCurrent.activityBA);
 //BA.debugLineNum = 65;BA.debugLine="toolbar.InitMenuListener";
mostCurrent._toolbar.InitMenuListener();
 //BA.debugLineNum = 66;BA.debugLine="toolbar.PopupTheme=toolbar.THEME_LIGHT";
mostCurrent._toolbar.setPopupTheme(mostCurrent._toolbar.THEME_LIGHT);
 //BA.debugLineNum = 68;BA.debugLine="toolbar.SubTitle=pack.GetApplicationLabel(package";
mostCurrent._toolbar.setSubTitle(BA.ObjectToCharSequence(mostCurrent._pack.GetApplicationLabel(_package)+" V."+mostCurrent._pack.GetVersionName(_package)));
 //BA.debugLineNum = 69;BA.debugLine="sm.Initialize(\"sm\")";
mostCurrent._sm.Initialize(mostCurrent.activityBA,"sm");
 //BA.debugLineNum = 70;BA.debugLine="bm1.Initialize(\"bm1\")";
mostCurrent._bm1.Initialize(mostCurrent.activityBA,"bm1");
 //BA.debugLineNum = 71;BA.debugLine="stext.Initialize(\"stext\")";
mostCurrent._stext.Initialize(mostCurrent.activityBA,"stext");
 //BA.debugLineNum = 72;BA.debugLine="subtext.Initialize(\"subtext\")";
mostCurrent._subtext.Initialize(mostCurrent.activityBA,"subtext");
 //BA.debugLineNum = 73;BA.debugLine="subtext.Typeface=rfont";
mostCurrent._subtext.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 74;BA.debugLine="stext.Typeface=rfont";
mostCurrent._stext.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 75;BA.debugLine="stext.TextSize=40";
mostCurrent._stext.setTextSize((float) (40));
 //BA.debugLineNum = 76;BA.debugLine="stext.Gravity=Gravity.CENTER_HORIZONTAL";
mostCurrent._stext.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER_HORIZONTAL);
 //BA.debugLineNum = 77;BA.debugLine="subtext.TextSize=30";
mostCurrent._subtext.setTextSize((float) (30));
 //BA.debugLineNum = 78;BA.debugLine="subtext.Gravity=Gravity.CENTER_HORIZONTAL";
mostCurrent._subtext.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER_HORIZONTAL);
 //BA.debugLineNum = 79;BA.debugLine="stext.TextColor=Colors.ARGB(255,29,169,246)";
mostCurrent._stext.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (29),(int) (169),(int) (246)));
 //BA.debugLineNum = 80;BA.debugLine="subtext.TextColor=Colors.ARGB(255,29,169,246)";
mostCurrent._subtext.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (29),(int) (169),(int) (246)));
 //BA.debugLineNum = 81;BA.debugLine="Activity.AddView(stext,0%x,30%y,100%x,150dip)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._stext.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (30),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (150)));
 //BA.debugLineNum = 82;BA.debugLine="Activity.AddView(subtext,0%x,80dip,100%x,120dip)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._subtext.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (80)),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (120)));
 //BA.debugLineNum = 83;BA.debugLine="Activity.AddView(bm1,25%x,50%y,150dip,150dip)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._bm1.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (25),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (50),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (150)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (150)));
 //BA.debugLineNum = 84;BA.debugLine="bm1.Visible=False";
mostCurrent._bm1.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 85;BA.debugLine="abhelper.ShowUpIndicator = True";
mostCurrent._abhelper.setShowUpIndicator(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 86;BA.debugLine="abhelper.HomeVisible=True";
mostCurrent._abhelper.setHomeVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 87;BA.debugLine="DateTime.TimeFormat=\"HH:mm\"";
anywheresoftware.b4a.keywords.Common.DateTime.setTimeFormat("HH:mm");
 //BA.debugLineNum = 88;BA.debugLine="DateTime.DateFormat=\"dd-MM-yyyy\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("dd-MM-yyyy");
 //BA.debugLineNum = 89;BA.debugLine="date=DateTime.Date(DateTime.Now)";
_date = anywheresoftware.b4a.keywords.Common.DateTime.Date(anywheresoftware.b4a.keywords.Common.DateTime.getNow());
 //BA.debugLineNum = 90;BA.debugLine="time1=DateTime.Time(DateTime.Now)";
_time1 = anywheresoftware.b4a.keywords.Common.DateTime.Time(anywheresoftware.b4a.keywords.Common.DateTime.getNow());
 //BA.debugLineNum = 91;BA.debugLine="kvst.Initialize(File.DirInternal,\"data_time\")";
mostCurrent._kvst._initialize(processBA,anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"data_time");
 //BA.debugLineNum = 92;BA.debugLine="kvsdata.Initialize(File.DirInternal,\"data_data\")";
mostCurrent._kvsdata._initialize(processBA,anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"data_data");
 //BA.debugLineNum = 93;BA.debugLine="alist.Initialize(File.DirInternal,\"adata_data\")";
mostCurrent._alist._initialize(processBA,anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"adata_data");
 //BA.debugLineNum = 94;BA.debugLine="dbase.Initialize(File.DirInternal,\"dbase_data\")";
mostCurrent._dbase._initialize(processBA,anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"dbase_data");
 //BA.debugLineNum = 95;BA.debugLine="abase.Initialize(File.DirInternal,\"abase_data\")";
mostCurrent._abase._initialize(processBA,anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"abase_data");
 //BA.debugLineNum = 96;BA.debugLine="qbase.Initialize(File.DirInternal,\"qbase_data\")";
mostCurrent._qbase._initialize(processBA,anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"qbase_data");
 //BA.debugLineNum = 97;BA.debugLine="Dim lftMenu As Panel";
_lftmenu = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 98;BA.debugLine="lftMenu.Initialize(\"\")";
_lftmenu.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 99;BA.debugLine="lftMenu.LoadLayout(\"left\")";
_lftmenu.LoadLayout("left",mostCurrent.activityBA);
 //BA.debugLineNum = 100;BA.debugLine="Dim offset As Int = 20%x";
_offset = anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (20),mostCurrent.activityBA);
 //BA.debugLineNum = 101;BA.debugLine="sm.BehindOffset = offset";
mostCurrent._sm.setBehindOffset(_offset);
 //BA.debugLineNum = 102;BA.debugLine="sm.Menu.AddView(lftMenu, 0, 0, 100%x-offset, 100%";
mostCurrent._sm.getMenu().AddView((android.view.View)(_lftmenu.getObject()),(int) (0),(int) (0),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)-_offset),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 103;BA.debugLine="sm.Mode = sm.LEFT";
mostCurrent._sm.setMode(mostCurrent._sm.LEFT);
 //BA.debugLineNum = 104;BA.debugLine="lv1.Initialize(\"lv1\")";
mostCurrent._lv1.Initialize(mostCurrent.activityBA,"lv1");
 //BA.debugLineNum = 105;BA.debugLine="Dim la1,la2 As Label";
_la1 = new anywheresoftware.b4a.objects.LabelWrapper();
_la2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 106;BA.debugLine="la2.Initialize(\"la2\")";
_la2.Initialize(mostCurrent.activityBA,"la2");
 //BA.debugLineNum = 107;BA.debugLine="la1.Initialize(\"la1\")";
_la1.Initialize(mostCurrent.activityBA,"la1");
 //BA.debugLineNum = 108;BA.debugLine="la1=lv1.TwoLinesAndBitmap.Label";
_la1 = mostCurrent._lv1.getTwoLinesAndBitmap().Label;
 //BA.debugLineNum = 109;BA.debugLine="la2=lv1.TwoLinesAndBitmap.SecondLabel";
_la2 = mostCurrent._lv1.getTwoLinesAndBitmap().SecondLabel;
 //BA.debugLineNum = 110;BA.debugLine="la1.TextSize=18";
_la1.setTextSize((float) (18));
 //BA.debugLineNum = 111;BA.debugLine="la2.TextSize=15";
_la2.setTextSize((float) (15));
 //BA.debugLineNum = 112;BA.debugLine="la1.Typeface=rfont";
_la1.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 113;BA.debugLine="la2.Typeface=rfont";
_la2.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 114;BA.debugLine="la1.TextColor=mcl.md_white_1000";
_la1.setTextColor(mostCurrent._mcl.getmd_white_1000());
 //BA.debugLineNum = 115;BA.debugLine="la2.TextColor=Colors.ARGB(255,255,255,255)";
_la2.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (255),(int) (255),(int) (255)));
 //BA.debugLineNum = 116;BA.debugLine="lv1.TwoLinesAndBitmap.ImageView.Height=48dip";
mostCurrent._lv1.getTwoLinesAndBitmap().ImageView.setHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (48)));
 //BA.debugLineNum = 117;BA.debugLine="lv1.TwoLinesAndBitmap.ImageView.Width=48dip";
mostCurrent._lv1.getTwoLinesAndBitmap().ImageView.setWidth(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (48)));
 //BA.debugLineNum = 118;BA.debugLine="lv1.TwoLinesAndBitmap.ItemHeight=65dip";
mostCurrent._lv1.getTwoLinesAndBitmap().setItemHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (65)));
 //BA.debugLineNum = 119;BA.debugLine="If FirstTime=True Then";
if (_firsttime==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 120;BA.debugLine="stext.Text=alist.ListKeys.Size&CRLF&\"Apps instal";
mostCurrent._stext.setText(BA.ObjectToCharSequence(BA.NumberToString(mostCurrent._alist._listkeys().getSize())+anywheresoftware.b4a.keywords.Common.CRLF+"Apps installiert"));
 //BA.debugLineNum = 121;BA.debugLine="subtext.Text=\"Hallo!\"";
mostCurrent._subtext.setText(BA.ObjectToCharSequence("Hallo!"));
 //BA.debugLineNum = 122;BA.debugLine="CallSub(cmodul,\"info_remote\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(mostCurrent._cmodul.getObject()),"info_remote");
 };
 //BA.debugLineNum = 124;BA.debugLine="counter=0";
_counter = (int) (0);
 //BA.debugLineNum = 125;BA.debugLine="left";
_left();
 //BA.debugLineNum = 126;BA.debugLine="acb_method";
_acb_method();
 //BA.debugLineNum = 127;BA.debugLine="End Sub";
return "";
}
public static String  _activity_createmenu(de.amberhome.objects.appcompat.ACMenuWrapper _menu) throws Exception{
anywheresoftware.b4a.objects.drawable.BitmapDrawable _eim = null;
anywheresoftware.b4a.objects.drawable.BitmapDrawable _fim1 = null;
anywheresoftware.b4a.objects.drawable.BitmapDrawable _climg1 = null;
de.amberhome.objects.appcompat.ACMenuItemWrapper _item = null;
de.amberhome.objects.appcompat.ACMenuItemWrapper _item2 = null;
de.amberhome.objects.appcompat.ACMenuItemWrapper _item3 = null;
 //BA.debugLineNum = 138;BA.debugLine="Sub Activity_CreateMenu(menu As ACMenu)";
 //BA.debugLineNum = 139;BA.debugLine="Dim eim As BitmapDrawable";
_eim = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 140;BA.debugLine="eim.Initialize(LoadBitmap(File.DirAssets,\"ic_exit";
_eim.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"ic_exit_to_app_white_48dp.png").getObject()));
 //BA.debugLineNum = 141;BA.debugLine="Dim fim1,climg1 As BitmapDrawable";
_fim1 = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
_climg1 = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 142;BA.debugLine="fim1.Initialize(LoadBitmap(File.DirAssets,\"ic_set";
_fim1.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"ic_settings_applications_white_36dp.png").getObject()));
 //BA.debugLineNum = 143;BA.debugLine="climg1.Initialize(LoadBitmap(File.DirAssets,\"ic_a";
_climg1.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"ic_apps_white_36dp.png").getObject()));
 //BA.debugLineNum = 144;BA.debugLine="menu.Clear";
_menu.Clear();
 //BA.debugLineNum = 145;BA.debugLine="Dim item,item2,item3 As ACMenuItem";
_item = new de.amberhome.objects.appcompat.ACMenuItemWrapper();
_item2 = new de.amberhome.objects.appcompat.ACMenuItemWrapper();
_item3 = new de.amberhome.objects.appcompat.ACMenuItemWrapper();
 //BA.debugLineNum = 146;BA.debugLine="item3=toolbar.Menu.Add2(0, 0, \"Menu\", climg1)";
_item3 = mostCurrent._toolbar.getMenu().Add2((int) (0),(int) (0),BA.ObjectToCharSequence("Menu"),(android.graphics.drawable.Drawable)(_climg1.getObject()));
 //BA.debugLineNum = 147;BA.debugLine="item=toolbar.Menu.Add2(1, 1, \"SMenu\", fim1)";
_item = mostCurrent._toolbar.getMenu().Add2((int) (1),(int) (1),BA.ObjectToCharSequence("SMenu"),(android.graphics.drawable.Drawable)(_fim1.getObject()));
 //BA.debugLineNum = 148;BA.debugLine="item2=toolbar.Menu.Add2(2, 2, \"Exit\", eim)";
_item2 = mostCurrent._toolbar.getMenu().Add2((int) (2),(int) (2),BA.ObjectToCharSequence("Exit"),(android.graphics.drawable.Drawable)(_eim.getObject()));
 //BA.debugLineNum = 149;BA.debugLine="item.ShowAsAction = item.SHOW_AS_ACTION_ALWAYS";
_item.setShowAsAction(_item.SHOW_AS_ACTION_ALWAYS);
 //BA.debugLineNum = 150;BA.debugLine="item2.ShowAsAction = item2.SHOW_AS_ACTION_ALWAYS";
_item2.setShowAsAction(_item2.SHOW_AS_ACTION_ALWAYS);
 //BA.debugLineNum = 151;BA.debugLine="item3.ShowAsAction = item3.SHOW_AS_ACTION_ALWAYS";
_item3.setShowAsAction(_item3.SHOW_AS_ACTION_ALWAYS);
 //BA.debugLineNum = 152;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
 //BA.debugLineNum = 131;BA.debugLine="Sub Activity_KeyPress (KeyCode As Int) As Boolean";
 //BA.debugLineNum = 132;BA.debugLine="If KeyCode=KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
 //BA.debugLineNum = 133;BA.debugLine="exit_click";
_exit_click();
 };
 //BA.debugLineNum = 135;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 136;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 174;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 176;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 170;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 171;BA.debugLine="t1.Enabled=False";
_t1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 172;BA.debugLine="End Sub";
return "";
}
public static String  _c_start() throws Exception{
 //BA.debugLineNum = 227;BA.debugLine="Sub c_start";
 //BA.debugLineNum = 228;BA.debugLine="ProgressDialogShow(\"bereinige \"&alist.ListKeys.Si";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,BA.ObjectToCharSequence("bereinige "+BA.NumberToString(mostCurrent._alist._listkeys().getSize())+" App´s ("+BA.ObjectToString(mostCurrent._kvsdata._get("cz"))+"), bitte warten.."));
 //BA.debugLineNum = 229;BA.debugLine="CallSub(cmodul,\"start_c\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(mostCurrent._cmodul.getObject()),"start_c");
 //BA.debugLineNum = 230;BA.debugLine="t1.Enabled=True";
_t1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 231;BA.debugLine="End Sub";
return "";
}
public static String  _closedia_buttonpressed(de.amberhome.materialdialogs.MaterialDialogWrapper _dialog,String _action) throws Exception{
 //BA.debugLineNum = 302;BA.debugLine="Sub closedia_ButtonPressed (Dialog As MaterialDial";
 //BA.debugLineNum = 303;BA.debugLine="Select Action";
switch (BA.switchObjectToInt(_action,_dialog.ACTION_POSITIVE,_dialog.ACTION_NEGATIVE,_dialog.ACTION_NEUTRAL)) {
case 0: {
 //BA.debugLineNum = 305;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 306;BA.debugLine="Animator.setanimati(\"extra_in\", \"extra_out\")";
mostCurrent._animator._setanimati(mostCurrent.activityBA,"extra_in","extra_out");
 break; }
case 1: {
 break; }
case 2: {
 break; }
}
;
 //BA.debugLineNum = 312;BA.debugLine="End Sub";
return "";
}
public static String  _exit_click() throws Exception{
de.amberhome.materialdialogs.MaterialDialogWrapper _infodia = null;
de.amberhome.materialdialogs.MaterialDialogBuilderWrapper _builder = null;
anywheresoftware.b4a.objects.drawable.BitmapDrawable _inf = null;
anywheresoftware.b4a.objects.LabelWrapper _l1 = null;
anywheresoftware.b4a.objects.LabelWrapper _l2 = null;
anywheresoftware.b4a.objects.PanelWrapper _pnl = null;
 //BA.debugLineNum = 279;BA.debugLine="Sub exit_click";
 //BA.debugLineNum = 280;BA.debugLine="Dim infodia As MaterialDialog";
_infodia = new de.amberhome.materialdialogs.MaterialDialogWrapper();
 //BA.debugLineNum = 281;BA.debugLine="Dim Builder As MaterialDialogBuilder";
_builder = new de.amberhome.materialdialogs.MaterialDialogBuilderWrapper();
 //BA.debugLineNum = 282;BA.debugLine="Dim inf As BitmapDrawable";
_inf = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 283;BA.debugLine="inf.Initialize(LoadBitmap(File.DirAssets,\"ic_http";
_inf.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"ic_https_white_36dp.png").getObject()));
 //BA.debugLineNum = 284;BA.debugLine="Dim l1,l2 As Label";
_l1 = new anywheresoftware.b4a.objects.LabelWrapper();
_l2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 285;BA.debugLine="Dim pnl As Panel";
_pnl = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 286;BA.debugLine="pnl.Initialize(\"pnl\")";
_pnl.Initialize(mostCurrent.activityBA,"pnl");
 //BA.debugLineNum = 287;BA.debugLine="l1.Initialize(\"\")";
_l1.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 288;BA.debugLine="l2.Initialize(\"\")";
_l2.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 289;BA.debugLine="l2.TextSize=16";
_l2.setTextSize((float) (16));
 //BA.debugLineNum = 290;BA.debugLine="l1.TextSize=15";
_l1.setTextSize((float) (15));
 //BA.debugLineNum = 291;BA.debugLine="l1.textcolor=mcl.md_black_1000";
_l1.setTextColor(mostCurrent._mcl.getmd_black_1000());
 //BA.debugLineNum = 292;BA.debugLine="l1.Text=\"S-Cleaner beenden?\"";
_l1.setText(BA.ObjectToCharSequence("S-Cleaner beenden?"));
 //BA.debugLineNum = 293;BA.debugLine="l2.textcolor=Colors.ARGB(255,0,0,0)";
_l2.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (0),(int) (0),(int) (0)));
 //BA.debugLineNum = 294;BA.debugLine="l1.Gravity=Gravity.TOP";
_l1.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.TOP);
 //BA.debugLineNum = 295;BA.debugLine="l1.Typeface=rfont";
_l1.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 296;BA.debugLine="l2.Typeface=rfont";
_l2.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 297;BA.debugLine="Builder.Initialize(\"closedia\")";
_builder.Initialize(mostCurrent.activityBA,"closedia");
 //BA.debugLineNum = 298;BA.debugLine="Builder.Title(\"App Info:?\").TitleColor(mcl.md_red";
_builder.Title(BA.ObjectToCharSequence("App Info:?")).TitleColor(mostCurrent._mcl.getmd_red_800()).Icon((android.graphics.drawable.Drawable)(_inf.getObject())).LimitIconToDefaultSize().Theme(_builder.THEME_LIGHT).Content(BA.ObjectToCharSequence(_l1.getText())).ContentLineSpacing((float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1)))).Cancelable(anywheresoftware.b4a.keywords.Common.True).NeutralText(BA.ObjectToCharSequence("Abbrechen")).Typeface((android.graphics.Typeface)(_rfont.getObject()),(android.graphics.Typeface)(_rfont.getObject())).NeutralColor(mostCurrent._mcl.getmd_blue_A700()).PositiveText(BA.ObjectToCharSequence("Ja bitte")).PositiveColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (217),(int) (87),(int) (217))).ContentGravity(_builder.GRAVITY_START);
 //BA.debugLineNum = 299;BA.debugLine="infodia=Builder.Show";
_infodia = _builder.Show();
 //BA.debugLineNum = 300;BA.debugLine="infodia.Show";
_infodia.Show();
 //BA.debugLineNum = 301;BA.debugLine="End Sub";
return "";
}
public static String  _finish_modul() throws Exception{
 //BA.debugLineNum = 272;BA.debugLine="Sub finish_modul";
 //BA.debugLineNum = 273;BA.debugLine="stext.Text=\"clean!\"";
mostCurrent._stext.setText(BA.ObjectToCharSequence("clean!"));
 //BA.debugLineNum = 274;BA.debugLine="bm1.SetVisibleAnimated(150,False)";
mostCurrent._bm1.SetVisibleAnimated((int) (150),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 275;BA.debugLine="ac1.SetVisibleAnimated(150,True)";
mostCurrent._ac1.SetVisibleAnimated((int) (150),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 276;BA.debugLine="Panel1.SetVisibleAnimated(150,True)";
mostCurrent._panel1.SetVisibleAnimated((int) (150),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 277;BA.debugLine="End Sub";
return "";
}
public static String  _formatfilesize(float _bytes) throws Exception{
String[] _unit = null;
double _po = 0;
double _si = 0;
int _i = 0;
 //BA.debugLineNum = 314;BA.debugLine="Sub FormatFileSize(Bytes As Float) As String";
 //BA.debugLineNum = 315;BA.debugLine="Private Unit() As String = Array As String(\" Byte";
_unit = new String[]{" Byte"," KB"," MB"," GB"," TB"," PB"," EB"," ZB"," YB"};
 //BA.debugLineNum = 316;BA.debugLine="If Bytes = 0 Then";
if (_bytes==0) { 
 //BA.debugLineNum = 317;BA.debugLine="Return \"0 Bytes\"";
if (true) return "0 Bytes";
 }else {
 //BA.debugLineNum = 319;BA.debugLine="Private Po, Si As Double";
_po = 0;
_si = 0;
 //BA.debugLineNum = 320;BA.debugLine="Private I As Int";
_i = 0;
 //BA.debugLineNum = 321;BA.debugLine="Bytes = Abs(Bytes)";
_bytes = (float) (anywheresoftware.b4a.keywords.Common.Abs(_bytes));
 //BA.debugLineNum = 322;BA.debugLine="I = Floor(Logarithm(Bytes, 1024))";
_i = (int) (anywheresoftware.b4a.keywords.Common.Floor(anywheresoftware.b4a.keywords.Common.Logarithm(_bytes,1024)));
 //BA.debugLineNum = 323;BA.debugLine="Po = Power(1024, I)";
_po = anywheresoftware.b4a.keywords.Common.Power(1024,_i);
 //BA.debugLineNum = 324;BA.debugLine="Si = Bytes / Po";
_si = _bytes/(double)_po;
 //BA.debugLineNum = 325;BA.debugLine="Return NumberFormat(Si, 1, 2) & Unit(I)";
if (true) return anywheresoftware.b4a.keywords.Common.NumberFormat(_si,(int) (1),(int) (2))+_unit[_i];
 };
 //BA.debugLineNum = 327;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 36;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 39;BA.debugLine="Private pack As PackageManager";
mostCurrent._pack = new anywheresoftware.b4a.phone.PackageManagerWrapper();
 //BA.debugLineNum = 40;BA.debugLine="Private mcl As MaterialColors";
mostCurrent._mcl = new com.tchart.materialcolors.MaterialColors();
 //BA.debugLineNum = 41;BA.debugLine="Private kvst,kvsdata,alist,dbase,abase,qbase As K";
mostCurrent._kvst = new sclean2.com.keyvaluestore();
mostCurrent._kvsdata = new sclean2.com.keyvaluestore();
mostCurrent._alist = new sclean2.com.keyvaluestore();
mostCurrent._dbase = new sclean2.com.keyvaluestore();
mostCurrent._abase = new sclean2.com.keyvaluestore();
mostCurrent._qbase = new sclean2.com.keyvaluestore();
 //BA.debugLineNum = 42;BA.debugLine="Private toolbar As ACToolBarLight";
mostCurrent._toolbar = new de.amberhome.objects.appcompat.ACToolbarLightWrapper();
 //BA.debugLineNum = 43;BA.debugLine="Private abhelper As ACActionBar";
mostCurrent._abhelper = new de.amberhome.objects.appcompat.ACActionBar();
 //BA.debugLineNum = 44;BA.debugLine="Dim acm As ACMenu";
mostCurrent._acm = new de.amberhome.objects.appcompat.ACMenuWrapper();
 //BA.debugLineNum = 45;BA.debugLine="Dim leftlist,lv1 As ListView";
mostCurrent._leftlist = new anywheresoftware.b4a.objects.ListViewWrapper();
mostCurrent._lv1 = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 46;BA.debugLine="Dim sm As SlidingMenu";
mostCurrent._sm = new anywheresoftware.b4a.objects.SlidingMenuWrapper();
 //BA.debugLineNum = 47;BA.debugLine="Private Panel1 As Panel";
mostCurrent._panel1 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 48;BA.debugLine="Private cb1 As Circlebutton";
mostCurrent._cb1 = new com.circlebuttonwrapper.CircleButtonWrapper();
 //BA.debugLineNum = 49;BA.debugLine="Private ac1 As ACButton";
mostCurrent._ac1 = new de.amberhome.objects.appcompat.ACButtonWrapper();
 //BA.debugLineNum = 50;BA.debugLine="Private sico As BitmapDrawable";
mostCurrent._sico = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 51;BA.debugLine="Private bm1 As ImageView";
mostCurrent._bm1 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 52;BA.debugLine="Private stext,subtext As Label";
mostCurrent._stext = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._subtext = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 53;BA.debugLine="Dim counter As Int";
_counter = 0;
 //BA.debugLineNum = 54;BA.debugLine="End Sub";
return "";
}
public static String  _left() throws Exception{
anywheresoftware.b4a.objects.LabelWrapper _la1 = null;
anywheresoftware.b4a.objects.LabelWrapper _la2 = null;
 //BA.debugLineNum = 185;BA.debugLine="Sub left";
 //BA.debugLineNum = 186;BA.debugLine="Dim la1,la2 As Label";
_la1 = new anywheresoftware.b4a.objects.LabelWrapper();
_la2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 187;BA.debugLine="la2.Initialize(\"la2\")";
_la2.Initialize(mostCurrent.activityBA,"la2");
 //BA.debugLineNum = 188;BA.debugLine="la1.Initialize(\"la1\")";
_la1.Initialize(mostCurrent.activityBA,"la1");
 //BA.debugLineNum = 189;BA.debugLine="la1=leftlist.TwoLinesAndBitmap.Label";
_la1 = mostCurrent._leftlist.getTwoLinesAndBitmap().Label;
 //BA.debugLineNum = 190;BA.debugLine="la2=leftlist.TwoLinesAndBitmap.SecondLabel";
_la2 = mostCurrent._leftlist.getTwoLinesAndBitmap().SecondLabel;
 //BA.debugLineNum = 191;BA.debugLine="leftlist.TwoLinesAndBitmap.ImageView.Height=48dip";
mostCurrent._leftlist.getTwoLinesAndBitmap().ImageView.setHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (48)));
 //BA.debugLineNum = 192;BA.debugLine="leftlist.TwoLinesAndBitmap.ImageView.Width=48dip";
mostCurrent._leftlist.getTwoLinesAndBitmap().ImageView.setWidth(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (48)));
 //BA.debugLineNum = 193;BA.debugLine="leftlist.TwoLinesAndBitmap.ItemHeight=65dip";
mostCurrent._leftlist.getTwoLinesAndBitmap().setItemHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (65)));
 //BA.debugLineNum = 194;BA.debugLine="la1.TextSize=18";
_la1.setTextSize((float) (18));
 //BA.debugLineNum = 195;BA.debugLine="la2.TextSize=15";
_la2.setTextSize((float) (15));
 //BA.debugLineNum = 196;BA.debugLine="la1.Typeface=rfont";
_la1.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 197;BA.debugLine="la2.Typeface=rfont";
_la2.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 198;BA.debugLine="la1.TextColor=mcl.md_black_1000";
_la1.setTextColor(mostCurrent._mcl.getmd_black_1000());
 //BA.debugLineNum = 199;BA.debugLine="la2.TextColor=Colors.ARGB(180,255,255,255)";
_la2.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (180),(int) (255),(int) (255),(int) (255)));
 //BA.debugLineNum = 200;BA.debugLine="lv1.TwoLinesAndBitmap.ImageView.Height=48dip";
mostCurrent._lv1.getTwoLinesAndBitmap().ImageView.setHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (48)));
 //BA.debugLineNum = 201;BA.debugLine="lv1.TwoLinesAndBitmap.ImageView.Width=48dip";
mostCurrent._lv1.getTwoLinesAndBitmap().ImageView.setWidth(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (48)));
 //BA.debugLineNum = 202;BA.debugLine="lv1.TwoLinesAndBitmap.ItemHeight=65dip";
mostCurrent._lv1.getTwoLinesAndBitmap().setItemHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (65)));
 //BA.debugLineNum = 203;BA.debugLine="leftlist.AddTwoLinesAndBitmap2(\"App List\",\"Instal";
mostCurrent._leftlist.AddTwoLinesAndBitmap2(BA.ObjectToCharSequence("App List"),BA.ObjectToCharSequence("Installierte Programme entfernen oder ändern"),(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"ic_settings_applications_white_36dp.png").getObject()),(Object)(0));
 //BA.debugLineNum = 204;BA.debugLine="leftlist.AddTwoLinesAndBitmap2(\"Feedback\",\"Sende";
mostCurrent._leftlist.AddTwoLinesAndBitmap2(BA.ObjectToCharSequence("Feedback"),BA.ObjectToCharSequence("Sende uns Vorschläge"),(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"ic_message_white_36dp.png").getObject()),(Object)(1));
 //BA.debugLineNum = 205;BA.debugLine="Return";
if (true) return "";
 //BA.debugLineNum = 206;BA.debugLine="End Sub";
return "";
}
public static String  _leftlist_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 208;BA.debugLine="Sub leftlist_ItemClick (Position As Int, Value As";
 //BA.debugLineNum = 209;BA.debugLine="Select Value";
switch (BA.switchObjectToInt(_value,(Object)(0),(Object)(1))) {
case 0: {
 break; }
case 1: {
 //BA.debugLineNum = 213;BA.debugLine="sm.HideMenus";
mostCurrent._sm.HideMenus();
 //BA.debugLineNum = 215;BA.debugLine="Animator.setanimati(\"extra_in\", \"extra_out\")";
mostCurrent._animator._setanimati(mostCurrent.activityBA,"extra_in","extra_out");
 break; }
}
;
 //BA.debugLineNum = 217;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
starter._process_globals();
cmodul._process_globals();
animator._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 26;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 29;BA.debugLine="Private os As OperatingSystem";
_os = new com.rootsoft.oslibrary.OSLibrary();
 //BA.debugLineNum = 30;BA.debugLine="Dim date,time1 As String";
_date = "";
_time1 = "";
 //BA.debugLineNum = 31;BA.debugLine="Private rfont As Typeface= rfont.LoadFromAssets(\"";
_rfont = new anywheresoftware.b4a.keywords.constants.TypefaceWrapper();
_rfont.setObject((android.graphics.Typeface)(_rfont.LoadFromAssets("Flaticon.ttf")));
 //BA.debugLineNum = 32;BA.debugLine="Private package As String=\"sclean2.com\"";
_package = "sclean2.com";
 //BA.debugLineNum = 33;BA.debugLine="Dim t1 As Timer";
_t1 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 34;BA.debugLine="End Sub";
return "";
}
public static String  _t1_tick() throws Exception{
 //BA.debugLineNum = 258;BA.debugLine="Sub t1_Tick";
 //BA.debugLineNum = 259;BA.debugLine="counter=counter+1";
_counter = (int) (_counter+1);
 //BA.debugLineNum = 260;BA.debugLine="If counter > 0 Then CallSub(cmodul,\"start_c\")";
if (_counter>0) { 
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(mostCurrent._cmodul.getObject()),"start_c");};
 //BA.debugLineNum = 261;BA.debugLine="If counter = 3 Then";
if (_counter==3) { 
 //BA.debugLineNum = 262;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 //BA.debugLineNum = 263;BA.debugLine="t1.Enabled=False";
_t1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 264;BA.debugLine="finish_modul";
_finish_modul();
 //BA.debugLineNum = 265;BA.debugLine="counter=0";
_counter = (int) (0);
 //BA.debugLineNum = 266;BA.debugLine="subtext.Text=kvsdata.Get(\"cz\")&\" bereinigt\"";
mostCurrent._subtext.setText(BA.ObjectToCharSequence(BA.ObjectToString(mostCurrent._kvsdata._get("cz"))+" bereinigt"));
 //BA.debugLineNum = 267;BA.debugLine="stext.TextSize=45";
mostCurrent._stext.setTextSize((float) (45));
 //BA.debugLineNum = 268;BA.debugLine="stext.Text=alist.ListKeys.Size&CRLF&\" Anwendunge";
mostCurrent._stext.setText(BA.ObjectToCharSequence(BA.NumberToString(mostCurrent._alist._listkeys().getSize())+anywheresoftware.b4a.keywords.Common.CRLF+" Anwendungen"));
 };
 //BA.debugLineNum = 270;BA.debugLine="End Sub";
return "";
}
public static String  _toolbar_menuitemclick(de.amberhome.objects.appcompat.ACMenuItemWrapper _item) throws Exception{
 //BA.debugLineNum = 158;BA.debugLine="Sub toolbar_MenuItemClick (Item As ACMenuItem)";
 //BA.debugLineNum = 159;BA.debugLine="Select Item.Id";
switch (BA.switchObjectToInt(_item.getId(),(int)(Double.parseDouble("0")),(int)(Double.parseDouble("1")),(int)(Double.parseDouble("2")))) {
case 0: {
 break; }
case 1: {
 break; }
case 2: {
 break; }
}
;
 //BA.debugLineNum = 167;BA.debugLine="End Sub";
return "";
}
public static String  _toolbar_navigationitemclick() throws Exception{
 //BA.debugLineNum = 154;BA.debugLine="Sub toolbar_NavigationItemClick";
 //BA.debugLineNum = 155;BA.debugLine="exit_click";
_exit_click();
 //BA.debugLineNum = 156;BA.debugLine="End Sub";
return "";
}
public static String  _update_modul() throws Exception{
String _dd = "";
int _c1 = 0;
int _c2 = 0;
int _sum = 0;
 //BA.debugLineNum = 233;BA.debugLine="Sub update_modul";
 //BA.debugLineNum = 234;BA.debugLine="Dim dd As String";
_dd = "";
 //BA.debugLineNum = 235;BA.debugLine="Dim c1,c2,sum As Int";
_c1 = 0;
_c2 = 0;
_sum = 0;
 //BA.debugLineNum = 236;BA.debugLine="c1=kvsdata.Get(\"c\")";
_c1 = (int)(BA.ObjectToNumber(mostCurrent._kvsdata._get("c")));
 //BA.debugLineNum = 237;BA.debugLine="c2=kvsdata.Get(\"to\")";
_c2 = (int)(BA.ObjectToNumber(mostCurrent._kvsdata._get("to")));
 //BA.debugLineNum = 238;BA.debugLine="dd=qbase.Get(c1)";
_dd = BA.ObjectToString(mostCurrent._qbase._get(BA.NumberToString(_c1)));
 //BA.debugLineNum = 239;BA.debugLine="sum=100/c2*c1";
_sum = (int) (100/(double)_c2*_c1);
 //BA.debugLineNum = 240;BA.debugLine="stext.TextSize=60";
mostCurrent._stext.setTextSize((float) (60));
 //BA.debugLineNum = 241;BA.debugLine="stext.Text=sum&\"%\"";
mostCurrent._stext.setText(BA.ObjectToCharSequence(BA.NumberToString(_sum)+"%"));
 //BA.debugLineNum = 242;BA.debugLine="bm1.Gravity=Gravity.FILL";
mostCurrent._bm1.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.FILL);
 //BA.debugLineNum = 243;BA.debugLine="Try";
try { //BA.debugLineNum = 244;BA.debugLine="sico=pack.GetApplicationIcon(dd)";
mostCurrent._sico.setObject((android.graphics.drawable.BitmapDrawable)(mostCurrent._pack.GetApplicationIcon(_dd)));
 //BA.debugLineNum = 245;BA.debugLine="If c1<c2 Then";
if (_c1<_c2) { 
 //BA.debugLineNum = 246;BA.debugLine="bm1.Bitmap=sico.Bitmap";
mostCurrent._bm1.setBitmap(mostCurrent._sico.getBitmap());
 //BA.debugLineNum = 247;BA.debugLine="subtext.Text=dd";
mostCurrent._subtext.setText(BA.ObjectToCharSequence(_dd));
 };
 } 
       catch (Exception e17) {
			processBA.setLastException(e17); //BA.debugLineNum = 250;BA.debugLine="Log(LastException.Message)";
anywheresoftware.b4a.keywords.Common.Log(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage());
 };
 //BA.debugLineNum = 252;BA.debugLine="If c1=c2 Then";
if (_c1==_c2) { 
 };
 //BA.debugLineNum = 256;BA.debugLine="End Sub";
return "";
}
}
