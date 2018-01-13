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
public static de.amberhome.materialdialogs.MaterialDialogWrapper _infodia = null;
public static com.rootsoft.oslibrary.OSLibrary _os = null;
public static String _date = "";
public static String _time1 = "";
public static anywheresoftware.b4a.keywords.constants.TypefaceWrapper _rfont = null;
public static String _package = "";
public static anywheresoftware.b4a.objects.Timer _t1 = null;
public static Object _ion = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _ani1 = null;
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
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _sico = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _bm1 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _bm2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _stext = null;
public anywheresoftware.b4a.objects.LabelWrapper _subtext = null;
public static int _counter = 0;
public anywheresoftware.b4a.objects.CSBuilder _cs = null;
public anywheresoftware.b4a.objects.ListViewWrapper _lv2 = null;
public de.donmanfred.ProcessButtonWrapper _pcb1 = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel2 = null;
public de.amberhome.objects.appcompat.ACButtonWrapper _pb = null;
public b4a.example.osstats _stats = null;
public com.maximussoft.msos.MSOS _ram = null;
public de.donmanfred.NumberProgressBarWrapper _prog1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _maintext = null;
public anywheresoftware.b4a.objects.LabelWrapper _ltext = null;
public anywheresoftware.b4a.objects.LabelWrapper _lstext = null;
public anywheresoftware.b4a.objects.collections.List _proclist = null;
public anywheresoftware.b4a.objects.collections.List _procname = null;
public anywheresoftware.b4a.objects.collections.List _procpid = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn = null;
public anywheresoftware.b4a.objects.PanelWrapper _appan = null;
public de.amberhome.objects.appcompat.ACFlatButtonWrapper _es = null;
public de.amberhome.objects.appcompat.ACFlatButtonWrapper _back = null;
public anywheresoftware.b4a.objects.ListViewWrapper _applist = null;
public de.amberhome.objects.appcompat.ACButtonWrapper _ac1 = null;
public sclean2.com.starter _starter = null;
public sclean2.com.animator _animator = null;
public sclean2.com.sub2 _sub2 = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (sub2.mostCurrent != null);
return vis;}
public static String  _about() throws Exception{
de.amberhome.materialdialogs.MaterialDialogBuilderWrapper _builder = null;
anywheresoftware.b4a.objects.drawable.BitmapDrawable _inf = null;
anywheresoftware.b4a.objects.LabelWrapper _l1 = null;
anywheresoftware.b4a.objects.LabelWrapper _l2 = null;
anywheresoftware.b4a.objects.PanelWrapper _pnl = null;
 //BA.debugLineNum = 566;BA.debugLine="Sub about";
 //BA.debugLineNum = 567;BA.debugLine="Dim Builder As MaterialDialogBuilder";
_builder = new de.amberhome.materialdialogs.MaterialDialogBuilderWrapper();
 //BA.debugLineNum = 568;BA.debugLine="Dim inf As BitmapDrawable";
_inf = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 569;BA.debugLine="inf=pack.GetApplicationIcon(package)";
_inf.setObject((android.graphics.drawable.BitmapDrawable)(mostCurrent._pack.GetApplicationIcon(_package)));
 //BA.debugLineNum = 570;BA.debugLine="Dim l1,l2 As Label";
_l1 = new anywheresoftware.b4a.objects.LabelWrapper();
_l2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 571;BA.debugLine="Dim pnl As Panel";
_pnl = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 572;BA.debugLine="pnl.Initialize(\"pnl\")";
_pnl.Initialize(mostCurrent.activityBA,"pnl");
 //BA.debugLineNum = 573;BA.debugLine="l1.Initialize(\"\")";
_l1.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 574;BA.debugLine="l2.Initialize(\"\")";
_l2.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 575;BA.debugLine="l2.TextSize=15";
_l2.setTextSize((float) (15));
 //BA.debugLineNum = 576;BA.debugLine="l1.TextSize=15";
_l1.setTextSize((float) (15));
 //BA.debugLineNum = 577;BA.debugLine="l1.textcolor=mcl.md_black_1000";
_l1.setTextColor(mostCurrent._mcl.getmd_black_1000());
 //BA.debugLineNum = 578;BA.debugLine="l1.Text=pack.GetApplicationLabel(package)&CRLF&\"V";
_l1.setText(BA.ObjectToCharSequence(mostCurrent._pack.GetApplicationLabel(_package)+anywheresoftware.b4a.keywords.Common.CRLF+"Version: "+mostCurrent._pack.GetVersionName(_package)+anywheresoftware.b4a.keywords.Common.CRLF+"© 2017-2018 Sulomedia"+anywheresoftware.b4a.keywords.Common.CRLF+"Hilfe und Infomationen"+anywheresoftware.b4a.keywords.Common.CRLF+"www.sulomedia.de"));
 //BA.debugLineNum = 579;BA.debugLine="l2.textcolor=Colors.ARGB(255,0,0,0)";
_l2.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (0),(int) (0),(int) (0)));
 //BA.debugLineNum = 580;BA.debugLine="l1.Gravity=Gravity.TOP";
_l1.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.TOP);
 //BA.debugLineNum = 581;BA.debugLine="l1.Typeface=rfont";
_l1.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 582;BA.debugLine="l2.Typeface=rfont";
_l2.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 583;BA.debugLine="Builder.Initialize(\"abdia\")";
_builder.Initialize(mostCurrent.activityBA,"abdia");
 //BA.debugLineNum = 584;BA.debugLine="Builder.Title(\"App Info\").TitleColor(mcl.md_black";
_builder.Title(BA.ObjectToCharSequence("App Info")).TitleColor(mostCurrent._mcl.getmd_black_1000()).Icon((android.graphics.drawable.Drawable)(_inf.getObject())).LimitIconToDefaultSize().Theme(_builder.THEME_LIGHT).Content(BA.ObjectToCharSequence(_l1.getText())).ContentLineSpacing((float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1)))).Cancelable(anywheresoftware.b4a.keywords.Common.True).Typeface((android.graphics.Typeface)(_rfont.getObject()),(android.graphics.Typeface)(_rfont.getObject())).PositiveText(BA.ObjectToCharSequence("Ok")).PositiveColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (0),(int) (0),(int) (0))).ContentGravity(_builder.GRAVITY_START);
 //BA.debugLineNum = 585;BA.debugLine="infodia=Builder.Show";
_infodia = _builder.Show();
 //BA.debugLineNum = 586;BA.debugLine="infodia.Show";
_infodia.Show();
 //BA.debugLineNum = 587;BA.debugLine="End Sub";
return "";
}
public static String  _ac1_click() throws Exception{
 //BA.debugLineNum = 374;BA.debugLine="Sub ac1_Click";
 //BA.debugLineNum = 375;BA.debugLine="ac1.SetVisibleAnimated(210,False)";
mostCurrent._ac1.SetVisibleAnimated((int) (210),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 376;BA.debugLine="lv2.SetVisibleAnimated(210,False)";
mostCurrent._lv2.SetVisibleAnimated((int) (210),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 377;BA.debugLine="bm2.SetVisibleAnimated(210,False)";
mostCurrent._bm2.SetVisibleAnimated((int) (210),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 378;BA.debugLine="abhelper.Hide";
mostCurrent._abhelper.Hide();
 //BA.debugLineNum = 379;BA.debugLine="prog1.SetVisibleAnimated(120,True)";
mostCurrent._prog1.SetVisibleAnimated((int) (120),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 380;BA.debugLine="subtext.SetVisibleAnimated(100,True)";
mostCurrent._subtext.SetVisibleAnimated((int) (100),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 381;BA.debugLine="maintext.SetVisibleAnimated(100,True)";
mostCurrent._maintext.SetVisibleAnimated((int) (100),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 382;BA.debugLine="prog1.ReachedBarColor=mcl.md_light_blue_A700";
mostCurrent._prog1.setReachedBarColor(mostCurrent._mcl.getmd_light_blue_A700());
 //BA.debugLineNum = 383;BA.debugLine="ani1.StartAnim(bm1)";
mostCurrent._ani1.StartAnim((android.view.View)(mostCurrent._bm1.getObject()));
 //BA.debugLineNum = 384;BA.debugLine="maintext.Text=\"Prüfe Anwendungen:\"";
mostCurrent._maintext.setText(BA.ObjectToCharSequence("Prüfe Anwendungen:"));
 //BA.debugLineNum = 385;BA.debugLine="stext.Text=\"...\"";
mostCurrent._stext.setText(BA.ObjectToCharSequence("..."));
 //BA.debugLineNum = 386;BA.debugLine="CallSub(Starter,\"start\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(mostCurrent._starter.getObject()),"start");
 //BA.debugLineNum = 387;BA.debugLine="End Sub";
return "";
}
public static String  _acb_method() throws Exception{
 //BA.debugLineNum = 287;BA.debugLine="Sub acb_method";
 //BA.debugLineNum = 288;BA.debugLine="ac1.Text=\"Reinigung starten:\"";
mostCurrent._ac1.setText(BA.ObjectToCharSequence("Reinigung starten:"));
 //BA.debugLineNum = 289;BA.debugLine="ac1.Typeface=rfont";
mostCurrent._ac1.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 290;BA.debugLine="ac1.Gravity=Gravity.CENTER";
mostCurrent._ac1.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 291;BA.debugLine="ac1.TextSize=17";
mostCurrent._ac1.setTextSize((float) (17));
 //BA.debugLineNum = 293;BA.debugLine="End Sub";
return "";
}
public static String  _activity_create(boolean _firsttime) throws Exception{
anywheresoftware.b4a.objects.PanelWrapper _lftmenu = null;
int _offset = 0;
anywheresoftware.b4a.objects.LabelWrapper _la1 = null;
anywheresoftware.b4a.objects.LabelWrapper _la2 = null;
 //BA.debugLineNum = 72;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 73;BA.debugLine="Activity.LoadLayout(\"1\")";
mostCurrent._activity.LoadLayout("1",mostCurrent.activityBA);
 //BA.debugLineNum = 74;BA.debugLine="abhelper.Initialize";
mostCurrent._abhelper.Initialize(mostCurrent.activityBA);
 //BA.debugLineNum = 75;BA.debugLine="t1.Initialize(\"t1\",1000)";
_t1.Initialize(processBA,"t1",(long) (1000));
 //BA.debugLineNum = 76;BA.debugLine="t1.Enabled=False";
_t1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 77;BA.debugLine="Activity.TitleColor=mcl.md_white_1000";
mostCurrent._activity.setTitleColor(mostCurrent._mcl.getmd_white_1000());
 //BA.debugLineNum = 78;BA.debugLine="Activity.Title=pack.GetApplicationLabel(package)";
mostCurrent._activity.setTitle(BA.ObjectToCharSequence(mostCurrent._pack.GetApplicationLabel(_package)));
 //BA.debugLineNum = 79;BA.debugLine="toolbar.SetAsActionBar";
mostCurrent._toolbar.SetAsActionBar(mostCurrent.activityBA);
 //BA.debugLineNum = 80;BA.debugLine="toolbar.InitMenuListener";
mostCurrent._toolbar.InitMenuListener();
 //BA.debugLineNum = 81;BA.debugLine="toolbar.PopupTheme=toolbar.THEME_LIGHT";
mostCurrent._toolbar.setPopupTheme(mostCurrent._toolbar.THEME_LIGHT);
 //BA.debugLineNum = 82;BA.debugLine="toolbar.Title=pack.GetApplicationLabel(package)";
mostCurrent._toolbar.setTitle(BA.ObjectToCharSequence(mostCurrent._pack.GetApplicationLabel(_package)));
 //BA.debugLineNum = 84;BA.debugLine="abhelper.HomeVisible=True";
mostCurrent._abhelper.setHomeVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 85;BA.debugLine="abhelper.ShowUpIndicator = True";
mostCurrent._abhelper.setShowUpIndicator(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 86;BA.debugLine="abhelper.UpIndicatorBitmap=LoadBitmapSample(File.";
mostCurrent._abhelper.setUpIndicatorBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmapSample(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"menu-1.png",anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (40)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (40))).getObject()));
 //BA.debugLineNum = 87;BA.debugLine="sm.Initialize(\"sm\")";
mostCurrent._sm.Initialize(mostCurrent.activityBA,"sm");
 //BA.debugLineNum = 88;BA.debugLine="bm1.Initialize(\"bm1\")";
mostCurrent._bm1.Initialize(mostCurrent.activityBA,"bm1");
 //BA.debugLineNum = 89;BA.debugLine="bm2.Initialize(\"bm2\")";
mostCurrent._bm2.Initialize(mostCurrent.activityBA,"bm2");
 //BA.debugLineNum = 90;BA.debugLine="stext.Initialize(\"stext\")";
mostCurrent._stext.Initialize(mostCurrent.activityBA,"stext");
 //BA.debugLineNum = 91;BA.debugLine="subtext.Initialize(\"subtext\")";
mostCurrent._subtext.Initialize(mostCurrent.activityBA,"subtext");
 //BA.debugLineNum = 92;BA.debugLine="prog1.Initialize(\"prog1\")";
mostCurrent._prog1.Initialize(processBA,"prog1");
 //BA.debugLineNum = 93;BA.debugLine="proclist.Initialize";
mostCurrent._proclist.Initialize();
 //BA.debugLineNum = 94;BA.debugLine="procname.Initialize";
mostCurrent._procname.Initialize();
 //BA.debugLineNum = 95;BA.debugLine="procpid.Initialize";
mostCurrent._procpid.Initialize();
 //BA.debugLineNum = 96;BA.debugLine="pn.Initialize(\"pn\")";
mostCurrent._pn.Initialize(mostCurrent.activityBA,"pn");
 //BA.debugLineNum = 97;BA.debugLine="applist.Initialize(\"applist\")";
mostCurrent._applist.Initialize(mostCurrent.activityBA,"applist");
 //BA.debugLineNum = 98;BA.debugLine="appan.Initialize(\"appan\")";
mostCurrent._appan.Initialize(mostCurrent.activityBA,"appan");
 //BA.debugLineNum = 100;BA.debugLine="es.Initialize(\"es\")";
mostCurrent._es.Initialize(mostCurrent.activityBA,"es");
 //BA.debugLineNum = 101;BA.debugLine="back.Initialize(\"back\")";
mostCurrent._back.Initialize(mostCurrent.activityBA,"back");
 //BA.debugLineNum = 102;BA.debugLine="bm1.Gravity=Gravity.FILL";
mostCurrent._bm1.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.FILL);
 //BA.debugLineNum = 103;BA.debugLine="bm2.Gravity=Gravity.FILL";
mostCurrent._bm2.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.FILL);
 //BA.debugLineNum = 104;BA.debugLine="bm2.Bitmap=LoadBitmap(File.DirAssets,\"sc-logo.png";
mostCurrent._bm2.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"sc-logo.png").getObject()));
 //BA.debugLineNum = 105;BA.debugLine="pcb1.Initialize(\"pcb1\")";
mostCurrent._pcb1.Initialize(processBA,"pcb1");
 //BA.debugLineNum = 106;BA.debugLine="pcb1.Visible=False";
mostCurrent._pcb1.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 107;BA.debugLine="maintext.Initialize(\"maintext\")";
mostCurrent._maintext.Initialize(mostCurrent.activityBA,"maintext");
 //BA.debugLineNum = 108;BA.debugLine="pb.Initialize(\"pb\")";
mostCurrent._pb.Initialize(mostCurrent.activityBA,"pb");
 //BA.debugLineNum = 109;BA.debugLine="lv1.Initialize(\"lv1\")";
mostCurrent._lv1.Initialize(mostCurrent.activityBA,"lv1");
 //BA.debugLineNum = 111;BA.debugLine="Activity.AddView(maintext,2%x,5%y,95%x,100dip)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._maintext.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (2),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (5),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (95),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (100)));
 //BA.debugLineNum = 112;BA.debugLine="Activity.AddView(prog1,10%x,15%y,78%x,100dip)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._prog1.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (10),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (15),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (78),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (100)));
 //BA.debugLineNum = 113;BA.debugLine="Activity.AddView(bm2,37%x,12%y,80dip,80dip)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._bm2.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (37),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (12),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (80)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (80)));
 //BA.debugLineNum = 114;BA.debugLine="Activity.AddView(bm1,37%x,34%y,80dip,80dip)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._bm1.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (37),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (34),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (80)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (80)));
 //BA.debugLineNum = 115;BA.debugLine="Activity.AddView(subtext,5%x,65%y,90%x,20%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._subtext.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (65),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (90),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (20),mostCurrent.activityBA));
 //BA.debugLineNum = 116;BA.debugLine="Activity.AddView(stext,5%x,55%y,90%x,20%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._stext.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (55),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (90),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (20),mostCurrent.activityBA));
 //BA.debugLineNum = 117;BA.debugLine="Activity.AddView(pn,0%x,10%y,100%x,100%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._pn.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 118;BA.debugLine="Activity.AddView(appan,0%x,10%y,100%x,100%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._appan.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 119;BA.debugLine="pn.AddView(es,0%x,82%y,100%x,90%y)";
mostCurrent._pn.AddView((android.view.View)(mostCurrent._es.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (82),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (90),mostCurrent.activityBA));
 //BA.debugLineNum = 120;BA.debugLine="pn.AddView(lv1,1%x,1%y,100%x,80%y)";
mostCurrent._pn.AddView((android.view.View)(mostCurrent._lv1.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (1),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (1),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (80),mostCurrent.activityBA));
 //BA.debugLineNum = 121;BA.debugLine="pn.Visible=False";
mostCurrent._pn.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 122;BA.debugLine="appan.Visible=False";
mostCurrent._appan.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 123;BA.debugLine="appan.AddView(applist,1%x,1%y,96%x,80%y)";
mostCurrent._appan.AddView((android.view.View)(mostCurrent._applist.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (1),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (1),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (96),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (80),mostCurrent.activityBA));
 //BA.debugLineNum = 124;BA.debugLine="appan.AddView(back,0%x,82%y,100%x,90%y)";
mostCurrent._appan.AddView((android.view.View)(mostCurrent._back.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (82),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (90),mostCurrent.activityBA));
 //BA.debugLineNum = 125;BA.debugLine="appan.Color=Colors.ARGB(210,0,0,0)";
mostCurrent._appan.setColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (210),(int) (0),(int) (0),(int) (0)));
 //BA.debugLineNum = 126;BA.debugLine="applist.Color=Colors.Transparent";
mostCurrent._applist.setColor(anywheresoftware.b4a.keywords.Common.Colors.Transparent);
 //BA.debugLineNum = 128;BA.debugLine="prog1.Visible=False";
mostCurrent._prog1.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 129;BA.debugLine="prog1.Color=Colors.Transparent";
mostCurrent._prog1.setColor(anywheresoftware.b4a.keywords.Common.Colors.Transparent);
 //BA.debugLineNum = 130;BA.debugLine="prog1.ProgressTextColor=mcl.md_black_1000";
mostCurrent._prog1.setProgressTextColor(mostCurrent._mcl.getmd_black_1000());
 //BA.debugLineNum = 131;BA.debugLine="prog1.ProgressTextSize=25";
mostCurrent._prog1.setProgressTextSize((float) (25));
 //BA.debugLineNum = 132;BA.debugLine="prog1.ReachedBarColor=mcl.md_light_blue_A700";
mostCurrent._prog1.setReachedBarColor(mostCurrent._mcl.getmd_light_blue_A700());
 //BA.debugLineNum = 133;BA.debugLine="prog1.ReachedBarHeight=20";
mostCurrent._prog1.setReachedBarHeight((float) (20));
 //BA.debugLineNum = 134;BA.debugLine="prog1.UnreachedBarHeight=10";
mostCurrent._prog1.setUnreachedBarHeight((float) (10));
 //BA.debugLineNum = 135;BA.debugLine="prog1.UnreachedBarColor=mcl.md_amber_A400";
mostCurrent._prog1.setUnreachedBarColor(mostCurrent._mcl.getmd_amber_A400());
 //BA.debugLineNum = 136;BA.debugLine="prog1.Suffix=\"%\"";
mostCurrent._prog1.setSuffix("%");
 //BA.debugLineNum = 139;BA.debugLine="maintext.TextSize=22";
mostCurrent._maintext.setTextSize((float) (22));
 //BA.debugLineNum = 140;BA.debugLine="maintext.Typeface=rfont";
mostCurrent._maintext.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 141;BA.debugLine="maintext.Gravity=Gravity.CENTER_HORIZONTAL";
mostCurrent._maintext.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER_HORIZONTAL);
 //BA.debugLineNum = 142;BA.debugLine="maintext.TextColor=mcl.md_black_1000";
mostCurrent._maintext.setTextColor(mostCurrent._mcl.getmd_black_1000());
 //BA.debugLineNum = 143;BA.debugLine="maintext.Visible=False";
mostCurrent._maintext.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 145;BA.debugLine="DateTime.TimeFormat=\"HH:mm\"";
anywheresoftware.b4a.keywords.Common.DateTime.setTimeFormat("HH:mm");
 //BA.debugLineNum = 146;BA.debugLine="DateTime.DateFormat=\"dd-MM-yyyy\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("dd-MM-yyyy");
 //BA.debugLineNum = 147;BA.debugLine="date=DateTime.Date(DateTime.Now)";
_date = anywheresoftware.b4a.keywords.Common.DateTime.Date(anywheresoftware.b4a.keywords.Common.DateTime.getNow());
 //BA.debugLineNum = 148;BA.debugLine="time1=DateTime.Time(DateTime.Now)";
_time1 = anywheresoftware.b4a.keywords.Common.DateTime.Time(anywheresoftware.b4a.keywords.Common.DateTime.getNow());
 //BA.debugLineNum = 149;BA.debugLine="kvst.Initialize(File.DirInternal,\"data_time\")";
mostCurrent._kvst._initialize(processBA,anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"data_time");
 //BA.debugLineNum = 150;BA.debugLine="kvsdata.Initialize(File.DirInternal,\"data_data\")";
mostCurrent._kvsdata._initialize(processBA,anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"data_data");
 //BA.debugLineNum = 151;BA.debugLine="alist.Initialize(File.DirInternal,\"adata_data\")";
mostCurrent._alist._initialize(processBA,anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"adata_data");
 //BA.debugLineNum = 152;BA.debugLine="dbase.Initialize(File.DirInternal,\"dbase_data\")";
mostCurrent._dbase._initialize(processBA,anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"dbase_data");
 //BA.debugLineNum = 153;BA.debugLine="abase.Initialize(File.DirInternal,\"abase_data\")";
mostCurrent._abase._initialize(processBA,anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"abase_data");
 //BA.debugLineNum = 154;BA.debugLine="qbase.Initialize(File.DirInternal,\"qbase_data\")";
mostCurrent._qbase._initialize(processBA,anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"qbase_data");
 //BA.debugLineNum = 155;BA.debugLine="Dim lftMenu As Panel";
_lftmenu = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 156;BA.debugLine="lftMenu.Initialize(\"\")";
_lftmenu.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 157;BA.debugLine="lftMenu.LoadLayout(\"left\")";
_lftmenu.LoadLayout("left",mostCurrent.activityBA);
 //BA.debugLineNum = 158;BA.debugLine="Dim offset As Int = 25%x";
_offset = anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (25),mostCurrent.activityBA);
 //BA.debugLineNum = 159;BA.debugLine="sm.BehindOffset = offset";
mostCurrent._sm.setBehindOffset(_offset);
 //BA.debugLineNum = 160;BA.debugLine="sm.Menu.AddView(lftMenu, 0, 0, 100%x-offset, 100%";
mostCurrent._sm.getMenu().AddView((android.view.View)(_lftmenu.getObject()),(int) (0),(int) (0),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)-_offset),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 161;BA.debugLine="sm.Mode = sm.LEFT";
mostCurrent._sm.setMode(mostCurrent._sm.LEFT);
 //BA.debugLineNum = 163;BA.debugLine="Dim la1,la2 As Label";
_la1 = new anywheresoftware.b4a.objects.LabelWrapper();
_la2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 164;BA.debugLine="la2.Initialize(\"\")";
_la2.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 165;BA.debugLine="la1.Initialize(\"\")";
_la1.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 166;BA.debugLine="la1=lv1.TwoLinesAndBitmap.Label";
_la1 = mostCurrent._lv1.getTwoLinesAndBitmap().Label;
 //BA.debugLineNum = 167;BA.debugLine="la2=lv1.TwoLinesAndBitmap.SecondLabel";
_la2 = mostCurrent._lv1.getTwoLinesAndBitmap().SecondLabel;
 //BA.debugLineNum = 168;BA.debugLine="la1.TextSize=20";
_la1.setTextSize((float) (20));
 //BA.debugLineNum = 169;BA.debugLine="la2.TextSize=25";
_la2.setTextSize((float) (25));
 //BA.debugLineNum = 170;BA.debugLine="la1.Typeface=rfont";
_la1.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 171;BA.debugLine="la2.Typeface=rfont";
_la2.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 172;BA.debugLine="la1.TextColor=mcl.md_white_1000";
_la1.setTextColor(mostCurrent._mcl.getmd_white_1000());
 //BA.debugLineNum = 173;BA.debugLine="la2.TextColor=Colors.ARGB(255,255,255,255)";
_la2.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (255),(int) (255),(int) (255)));
 //BA.debugLineNum = 174;BA.debugLine="lv1.TwoLinesAndBitmap.ImageView.Height=48dip";
mostCurrent._lv1.getTwoLinesAndBitmap().ImageView.setHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (48)));
 //BA.debugLineNum = 175;BA.debugLine="lv1.TwoLinesAndBitmap.ImageView.Width=48dip";
mostCurrent._lv1.getTwoLinesAndBitmap().ImageView.setWidth(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (48)));
 //BA.debugLineNum = 176;BA.debugLine="lv1.TwoLinesAndBitmap.ItemHeight=65dip";
mostCurrent._lv1.getTwoLinesAndBitmap().setItemHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (65)));
 //BA.debugLineNum = 178;BA.debugLine="stats.Initialize(400,100,Me,\"Stats\")";
mostCurrent._stats._initialize(mostCurrent.activityBA,(int) (400),(int) (100),main.getObject(),"Stats");
 //BA.debugLineNum = 179;BA.debugLine="stats.StartStats";
mostCurrent._stats._startstats();
 //BA.debugLineNum = 181;BA.debugLine="ani1.SlideFromLeftToRight(\"ani1\",300,1)";
mostCurrent._ani1.SlideFromLeftToRight(mostCurrent.activityBA,"ani1",(float) (300),(long) (1));
 //BA.debugLineNum = 182;BA.debugLine="If FirstTime=True Then";
if (_firsttime==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 183;BA.debugLine="get_data";
_get_data();
 };
 //BA.debugLineNum = 186;BA.debugLine="counter=0";
_counter = (int) (0);
 //BA.debugLineNum = 187;BA.debugLine="left";
_left();
 //BA.debugLineNum = 188;BA.debugLine="proc_pack";
_proc_pack();
 //BA.debugLineNum = 189;BA.debugLine="acb_method";
_acb_method();
 //BA.debugLineNum = 190;BA.debugLine="text_meth";
_text_meth();
 //BA.debugLineNum = 191;BA.debugLine="sub_list";
_sub_list();
 //BA.debugLineNum = 192;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
 //BA.debugLineNum = 196;BA.debugLine="Sub Activity_KeyPress (KeyCode As Int) As Boolean";
 //BA.debugLineNum = 197;BA.debugLine="If KeyCode=KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
 //BA.debugLineNum = 198;BA.debugLine="get_data";
_get_data();
 //BA.debugLineNum = 199;BA.debugLine="acb_method";
_acb_method();
 //BA.debugLineNum = 200;BA.debugLine="sub_list";
_sub_list();
 //BA.debugLineNum = 201;BA.debugLine="stats.EndStats";
mostCurrent._stats._endstats();
 //BA.debugLineNum = 202;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 };
 //BA.debugLineNum = 204;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 205;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 237;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 238;BA.debugLine="If UserClosed=False Then";
if (_userclosed==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 239;BA.debugLine="t1.Enabled=False";
_t1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 240;BA.debugLine="text_meth";
_text_meth();
 //BA.debugLineNum = 241;BA.debugLine="get_data";
_get_data();
 };
 //BA.debugLineNum = 243;BA.debugLine="Return";
if (true) return "";
 //BA.debugLineNum = 244;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 223;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 224;BA.debugLine="If alist.ListKeys.Size>0 Then";
if (mostCurrent._alist._listkeys().getSize()>0) { 
 //BA.debugLineNum = 225;BA.debugLine="text_meth";
_text_meth();
 }else {
 //BA.debugLineNum = 228;BA.debugLine="stext.Text=\"Hallo..!\"";
mostCurrent._stext.setText(BA.ObjectToCharSequence("Hallo..!"));
 //BA.debugLineNum = 229;BA.debugLine="subtext.Text=\"Tipp: benutze den Reinigun Button\"";
mostCurrent._subtext.setText(BA.ObjectToCharSequence("Tipp: benutze den Reinigun Button"+anywheresoftware.b4a.keywords.Common.CRLF+"für einene ersten Scan!"));
 };
 //BA.debugLineNum = 231;BA.debugLine="get_data";
_get_data();
 //BA.debugLineNum = 232;BA.debugLine="acb_method";
_acb_method();
 //BA.debugLineNum = 233;BA.debugLine="sub_list";
_sub_list();
 //BA.debugLineNum = 234;BA.debugLine="stats.StartStats";
mostCurrent._stats._startstats();
 //BA.debugLineNum = 235;BA.debugLine="End Sub";
return "";
}
public static String  _app_info() throws Exception{
anywheresoftware.b4a.objects.drawable.BitmapDrawable _ai = null;
anywheresoftware.b4a.objects.LabelWrapper _la1 = null;
anywheresoftware.b4a.objects.LabelWrapper _la2 = null;
String _i = "";
 //BA.debugLineNum = 613;BA.debugLine="Sub app_info";
 //BA.debugLineNum = 614;BA.debugLine="es.Text=\"Ok\"";
mostCurrent._es.setText(BA.ObjectToCharSequence("Ok"));
 //BA.debugLineNum = 615;BA.debugLine="es.Typeface=rfont";
mostCurrent._es.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 616;BA.debugLine="es.TextColor=mcl.md_white_1000";
mostCurrent._es.setTextColor(mostCurrent._mcl.getmd_white_1000());
 //BA.debugLineNum = 617;BA.debugLine="es.Color=mcl.md_light_blue_A700";
mostCurrent._es.setColor(mostCurrent._mcl.getmd_light_blue_A700());
 //BA.debugLineNum = 618;BA.debugLine="es.Gravity=Gravity.CENTER_HORIZONTAL";
mostCurrent._es.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER_HORIZONTAL);
 //BA.debugLineNum = 619;BA.debugLine="Dim ai As BitmapDrawable";
_ai = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 620;BA.debugLine="pn.Color=Colors.ARGB(220,0,0,0)";
mostCurrent._pn.setColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (220),(int) (0),(int) (0),(int) (0)));
 //BA.debugLineNum = 621;BA.debugLine="Dim la1,la2 As Label";
_la1 = new anywheresoftware.b4a.objects.LabelWrapper();
_la2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 622;BA.debugLine="la2.Initialize(\"\")";
_la2.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 623;BA.debugLine="la1.Initialize(\"\")";
_la1.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 624;BA.debugLine="la1=lv1.TwoLinesAndBitmap.Label";
_la1 = mostCurrent._lv1.getTwoLinesAndBitmap().Label;
 //BA.debugLineNum = 625;BA.debugLine="la2=lv1.TwoLinesAndBitmap.SecondLabel";
_la2 = mostCurrent._lv1.getTwoLinesAndBitmap().SecondLabel;
 //BA.debugLineNum = 626;BA.debugLine="la1.TextSize=15";
_la1.setTextSize((float) (15));
 //BA.debugLineNum = 627;BA.debugLine="la2.TextSize=12";
_la2.setTextSize((float) (12));
 //BA.debugLineNum = 628;BA.debugLine="la1.Typeface=rfont";
_la1.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 629;BA.debugLine="la2.Typeface=rfont";
_la2.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 630;BA.debugLine="la1.TextColor=mcl.md_white_1000";
_la1.setTextColor(mostCurrent._mcl.getmd_white_1000());
 //BA.debugLineNum = 631;BA.debugLine="la2.TextColor=Colors.ARGB(255,255,255,255)";
_la2.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (255),(int) (255),(int) (255)));
 //BA.debugLineNum = 632;BA.debugLine="lv1.TwoLinesAndBitmap.ImageView.Height=48dip";
mostCurrent._lv1.getTwoLinesAndBitmap().ImageView.setHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (48)));
 //BA.debugLineNum = 633;BA.debugLine="lv1.TwoLinesAndBitmap.ImageView.Width=48dip";
mostCurrent._lv1.getTwoLinesAndBitmap().ImageView.setWidth(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (48)));
 //BA.debugLineNum = 634;BA.debugLine="lv1.TwoLinesAndBitmap.ItemHeight=55dip";
mostCurrent._lv1.getTwoLinesAndBitmap().setItemHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (55)));
 //BA.debugLineNum = 635;BA.debugLine="lv1.Clear";
mostCurrent._lv1.Clear();
 //BA.debugLineNum = 636;BA.debugLine="If alist.ListKeys.Size>=0 Then";
if (mostCurrent._alist._listkeys().getSize()>=0) { 
 //BA.debugLineNum = 637;BA.debugLine="For Each i As String In  alist.ListKeys";
{
final anywheresoftware.b4a.BA.IterableList group24 = mostCurrent._alist._listkeys();
final int groupLen24 = group24.getSize()
;int index24 = 0;
;
for (; index24 < groupLen24;index24++){
_i = BA.ObjectToString(group24.Get(index24));
 //BA.debugLineNum = 638;BA.debugLine="Log(alist.Get(i))";
anywheresoftware.b4a.keywords.Common.Log(BA.ObjectToString(mostCurrent._alist._get(_i)));
 //BA.debugLineNum = 639;BA.debugLine="ai=pack.GetApplicationIcon(i)";
_ai.setObject((android.graphics.drawable.BitmapDrawable)(mostCurrent._pack.GetApplicationIcon(_i)));
 //BA.debugLineNum = 640;BA.debugLine="lv1.AddTwoLinesAndBitmap(pack.GetApplicationLabe";
mostCurrent._lv1.AddTwoLinesAndBitmap(BA.ObjectToCharSequence(mostCurrent._pack.GetApplicationLabel(_i)),BA.ObjectToCharSequence(_i+", "+_formatfilesize((float)(BA.ObjectToNumber(mostCurrent._alist._get(_i))))),_ai.getBitmap());
 }
};
 }else {
 //BA.debugLineNum = 643;BA.debugLine="lv1.AddTwoLinesAndBitmap(\"Leer:\",\"keine daten ge";
mostCurrent._lv1.AddTwoLinesAndBitmap(BA.ObjectToCharSequence("Leer:"),BA.ObjectToCharSequence("keine daten gefunden"),(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"push-pin.png").getObject()));
 };
 //BA.debugLineNum = 645;BA.debugLine="If pn.Visible=True Then";
if (mostCurrent._pn.getVisible()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 646;BA.debugLine="pn.SetVisibleAnimated(350,False)";
mostCurrent._pn.SetVisibleAnimated((int) (350),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 647;BA.debugLine="backtrue";
_backtrue();
 }else {
 //BA.debugLineNum = 649;BA.debugLine="pn.SetVisibleAnimated(350,True)";
mostCurrent._pn.SetVisibleAnimated((int) (350),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 650;BA.debugLine="pn.BringToFront";
mostCurrent._pn.BringToFront();
 //BA.debugLineNum = 651;BA.debugLine="ac1.SetVisibleAnimated(350,False)";
mostCurrent._ac1.SetVisibleAnimated((int) (350),anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 653;BA.debugLine="End Sub";
return "";
}
public static String  _app_share() throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _i = null;
 //BA.debugLineNum = 764;BA.debugLine="Sub app_share";
 //BA.debugLineNum = 765;BA.debugLine="Dim i As Intent";
_i = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 766;BA.debugLine="i.Initialize(i.ACTION_SEND, \"\")";
_i.Initialize(_i.ACTION_SEND,"");
 //BA.debugLineNum = 767;BA.debugLine="i.SetType(\"text/plain\")";
_i.SetType("text/plain");
 //BA.debugLineNum = 768;BA.debugLine="i.PutExtra(\"android.intent.extra.TEXT\", \"https://";
_i.PutExtra("android.intent.extra.TEXT",(Object)("https://s-cleaner.de.uptodown.com/android"));
 //BA.debugLineNum = 769;BA.debugLine="i.PutExtra(\"android.intent.extra.SUBJECT\", Applic";
_i.PutExtra("android.intent.extra.SUBJECT",(Object)(anywheresoftware.b4a.keywords.Common.Application.getLabelName()+"-"+anywheresoftware.b4a.keywords.Common.Application.getVersionName()+anywheresoftware.b4a.keywords.Common.CRLF+"bereinigt dein Telefon einfach und schnell.."));
 //BA.debugLineNum = 770;BA.debugLine="StartActivity(i)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_i.getObject()));
 //BA.debugLineNum = 771;BA.debugLine="End Sub";
return "";
}
public static String  _applist_itemclick(int _position,Object _value) throws Exception{
String _p = "";
int _res = 0;
String _name = "";
anywheresoftware.b4a.objects.drawable.BitmapDrawable _icon = null;
anywheresoftware.b4a.objects.IntentWrapper _in = null;
anywheresoftware.b4a.objects.IntentWrapper _i = null;
 //BA.debugLineNum = 715;BA.debugLine="Sub applist_ItemClick (Position As Int, Value As O";
 //BA.debugLineNum = 716;BA.debugLine="For Each p As String In pack.GetInstalledPackages";
{
final anywheresoftware.b4a.BA.IterableList group1 = mostCurrent._pack.GetInstalledPackages();
final int groupLen1 = group1.getSize()
;int index1 = 0;
;
for (; index1 < groupLen1;index1++){
_p = BA.ObjectToString(group1.Get(index1));
 //BA.debugLineNum = 717;BA.debugLine="Dim res As Int";
_res = 0;
 //BA.debugLineNum = 718;BA.debugLine="Dim name As String=pack.GetApplicationLabel(p)";
_name = mostCurrent._pack.GetApplicationLabel(_p);
 //BA.debugLineNum = 719;BA.debugLine="Try";
try { //BA.debugLineNum = 720;BA.debugLine="Dim icon As BitmapDrawable=pack.GetApplicationIc";
_icon = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
_icon.setObject((android.graphics.drawable.BitmapDrawable)(mostCurrent._pack.GetApplicationIcon(_p)));
 //BA.debugLineNum = 721;BA.debugLine="If Value=p Then";
if ((_value).equals((Object)(_p))) { 
 //BA.debugLineNum = 722;BA.debugLine="res=Msgbox2(\"Option:\"&CRLF&\"Starten (öffnet die";
_res = anywheresoftware.b4a.keywords.Common.Msgbox2(BA.ObjectToCharSequence("Option:"+anywheresoftware.b4a.keywords.Common.CRLF+"Starten (öffnet die Anwendung in einem neuen Fenster)"+anywheresoftware.b4a.keywords.Common.CRLF+"Entfernen (Löscht die Anwendung von ihrem Telefon!)"),BA.ObjectToCharSequence(_name),"Starten","Ende","Entfernen",_icon.getBitmap(),mostCurrent.activityBA);
 };
 } 
       catch (Exception e10) {
			processBA.setLastException(e10); //BA.debugLineNum = 725;BA.debugLine="Log(LastException.Message)";
anywheresoftware.b4a.keywords.Common.Log(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage());
 };
 //BA.debugLineNum = 728;BA.debugLine="If res=DialogResponse.POSITIVE Then";
if (_res==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 729;BA.debugLine="Dim in As Intent";
_in = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 730;BA.debugLine="in=pack.GetApplicationIntent(p)";
_in = mostCurrent._pack.GetApplicationIntent(_p);
 //BA.debugLineNum = 731;BA.debugLine="If in.IsInitialized Then";
if (_in.IsInitialized()) { 
 //BA.debugLineNum = 732;BA.debugLine="ToastMessageShow(\"Starte \"&name,False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Starte "+_name),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 733;BA.debugLine="appan.SetVisibleAnimated(300,False)";
mostCurrent._appan.SetVisibleAnimated((int) (300),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 734;BA.debugLine="backtrue";
_backtrue();
 //BA.debugLineNum = 735;BA.debugLine="StartActivity(in)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_in.getObject()));
 };
 }else {
 //BA.debugLineNum = 738;BA.debugLine="If res=DialogResponse.NEGATIVE Then";
if (_res==anywheresoftware.b4a.keywords.Common.DialogResponse.NEGATIVE) { 
 //BA.debugLineNum = 739;BA.debugLine="Dim i As Intent";
_i = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 740;BA.debugLine="i.Initialize(\"android.intent.action.DELETE\",\"";
_i.Initialize("android.intent.action.DELETE","package:"+_p);
 //BA.debugLineNum = 741;BA.debugLine="appan.SetVisibleAnimated(300,False)";
mostCurrent._appan.SetVisibleAnimated((int) (300),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 742;BA.debugLine="backtrue";
_backtrue();
 //BA.debugLineNum = 743;BA.debugLine="ToastMessageShow(\"Deinstalation gestartet..\",";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Deinstalation gestartet.."),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 744;BA.debugLine="StartActivity(i)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_i.getObject()));
 };
 };
 }
};
 //BA.debugLineNum = 748;BA.debugLine="Return";
if (true) return "";
 //BA.debugLineNum = 749;BA.debugLine="End Sub";
return "";
}
public static String  _back_click() throws Exception{
 //BA.debugLineNum = 751;BA.debugLine="Sub back_Click";
 //BA.debugLineNum = 752;BA.debugLine="appan.SetVisibleAnimated(300,False)";
mostCurrent._appan.SetVisibleAnimated((int) (300),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 753;BA.debugLine="backtrue";
_backtrue();
 //BA.debugLineNum = 754;BA.debugLine="End Sub";
return "";
}
public static String  _backtrue() throws Exception{
 //BA.debugLineNum = 756;BA.debugLine="Sub backtrue";
 //BA.debugLineNum = 757;BA.debugLine="If ac1.Visible=True Then";
if (mostCurrent._ac1.getVisible()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 758;BA.debugLine="ac1.SetVisibleAnimated(300,False)";
mostCurrent._ac1.SetVisibleAnimated((int) (300),anywheresoftware.b4a.keywords.Common.False);
 }else {
 //BA.debugLineNum = 760;BA.debugLine="ac1.SetVisibleAnimated(300,True)";
mostCurrent._ac1.SetVisibleAnimated((int) (300),anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 762;BA.debugLine="End Sub";
return "";
}
public static String  _c_start() throws Exception{
 //BA.debugLineNum = 390;BA.debugLine="Sub c_start";
 //BA.debugLineNum = 393;BA.debugLine="task_service";
_task_service();
 //BA.debugLineNum = 394;BA.debugLine="t1.Enabled=True";
_t1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 396;BA.debugLine="End Sub";
return "";
}
public static String  _clean_c() throws Exception{
anywheresoftware.b4a.objects.LabelWrapper _l1 = null;
anywheresoftware.b4a.objects.LabelWrapper _l2 = null;
 //BA.debugLineNum = 399;BA.debugLine="Sub clean_c";
 //BA.debugLineNum = 400;BA.debugLine="Dim l1,l2 As Label";
_l1 = new anywheresoftware.b4a.objects.LabelWrapper();
_l2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 401;BA.debugLine="l1.Initialize(\"\")";
_l1.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 402;BA.debugLine="l2.Initialize(\"\")";
_l2.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 403;BA.debugLine="l2.TextSize=18";
_l2.setTextSize((float) (18));
 //BA.debugLineNum = 404;BA.debugLine="l1.TextSize=18";
_l1.setTextSize((float) (18));
 //BA.debugLineNum = 405;BA.debugLine="l1.textcolor=mcl.md_black_1000";
_l1.setTextColor(mostCurrent._mcl.getmd_black_1000());
 //BA.debugLineNum = 406;BA.debugLine="l1.Text=FormatFileSize(kvsdata.Get(\"cp\"))";
_l1.setText(BA.ObjectToCharSequence(_formatfilesize((float)(BA.ObjectToNumber(mostCurrent._kvsdata._get("cp"))))));
 //BA.debugLineNum = 407;BA.debugLine="l2.Text=\"bereinige..\"&alist.ListKeys.Size";
_l2.setText(BA.ObjectToCharSequence("bereinige.."+BA.NumberToString(mostCurrent._alist._listkeys().getSize())));
 //BA.debugLineNum = 408;BA.debugLine="l2.textcolor=Colors.ARGB(255,0,0,0)";
_l2.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (0),(int) (0),(int) (0)));
 //BA.debugLineNum = 409;BA.debugLine="l1.Gravity=Gravity.TOP";
_l1.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.TOP);
 //BA.debugLineNum = 410;BA.debugLine="l1.Typeface=rfont";
_l1.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 411;BA.debugLine="l2.Typeface=rfont";
_l2.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 412;BA.debugLine="stext.Text=cs.Initialize.Size(20).Color(mcl.md_de";
mostCurrent._stext.setText(BA.ObjectToCharSequence(mostCurrent._cs.Initialize().Size((int) (20)).Color(mostCurrent._mcl.getmd_deep_purple_600()).Append(BA.ObjectToCharSequence(_l2.getText())).Pop().Append(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.CRLF+_l1.getText())).PopAll().getObject()));
 //BA.debugLineNum = 413;BA.debugLine="subtext.Text=kvsdata.Get(\"cz\")";
mostCurrent._subtext.setText(BA.ObjectToCharSequence(mostCurrent._kvsdata._get("cz")));
 //BA.debugLineNum = 414;BA.debugLine="End Sub";
return "";
}
public static String  _closedia_buttonpressed(de.amberhome.materialdialogs.MaterialDialogWrapper _dialog,String _action) throws Exception{
 //BA.debugLineNum = 553;BA.debugLine="Sub closedia_ButtonPressed (Dialog As MaterialDial";
 //BA.debugLineNum = 554;BA.debugLine="Select Action";
switch (BA.switchObjectToInt(_action,_dialog.ACTION_POSITIVE,_dialog.ACTION_NEGATIVE,_dialog.ACTION_NEUTRAL)) {
case 0: {
 //BA.debugLineNum = 556;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 557;BA.debugLine="Animator.setanimati(\"extra_in\", \"extra_out\")";
mostCurrent._animator._setanimati(mostCurrent.activityBA,"extra_in","extra_out");
 break; }
case 1: {
 break; }
case 2: {
 break; }
}
;
 //BA.debugLineNum = 563;BA.debugLine="End Sub";
return "";
}
public static String  _es_click() throws Exception{
 //BA.debugLineNum = 655;BA.debugLine="Sub es_Click";
 //BA.debugLineNum = 656;BA.debugLine="If pn.Visible=True Then";
if (mostCurrent._pn.getVisible()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 657;BA.debugLine="pn.SetVisibleAnimated(350,False)";
mostCurrent._pn.SetVisibleAnimated((int) (350),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 658;BA.debugLine="backtrue";
_backtrue();
 }else {
 //BA.debugLineNum = 660;BA.debugLine="pn.SetVisibleAnimated(350,True)";
mostCurrent._pn.SetVisibleAnimated((int) (350),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 661;BA.debugLine="pn.BringToFront";
mostCurrent._pn.BringToFront();
 //BA.debugLineNum = 662;BA.debugLine="ac1.SetVisibleAnimated(350,False)";
mostCurrent._ac1.SetVisibleAnimated((int) (350),anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 664;BA.debugLine="End Sub";
return "";
}
public static String  _exit_click() throws Exception{
de.amberhome.materialdialogs.MaterialDialogBuilderWrapper _builder = null;
anywheresoftware.b4a.objects.drawable.BitmapDrawable _inf = null;
anywheresoftware.b4a.objects.LabelWrapper _l1 = null;
anywheresoftware.b4a.objects.LabelWrapper _l2 = null;
anywheresoftware.b4a.objects.PanelWrapper _pnl = null;
 //BA.debugLineNum = 531;BA.debugLine="Sub exit_click";
 //BA.debugLineNum = 532;BA.debugLine="Dim Builder As MaterialDialogBuilder";
_builder = new de.amberhome.materialdialogs.MaterialDialogBuilderWrapper();
 //BA.debugLineNum = 533;BA.debugLine="Dim inf As BitmapDrawable";
_inf = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 534;BA.debugLine="inf.Initialize(LoadBitmap(File.DirAssets,\"ic_new_";
_inf.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"ic_new_releases_black_18dp.png").getObject()));
 //BA.debugLineNum = 535;BA.debugLine="Dim l1,l2 As Label";
_l1 = new anywheresoftware.b4a.objects.LabelWrapper();
_l2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 536;BA.debugLine="Dim pnl As Panel";
_pnl = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 537;BA.debugLine="pnl.Initialize(\"pnl\")";
_pnl.Initialize(mostCurrent.activityBA,"pnl");
 //BA.debugLineNum = 538;BA.debugLine="l1.Initialize(\"\")";
_l1.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 539;BA.debugLine="l2.Initialize(\"\")";
_l2.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 540;BA.debugLine="l2.TextSize=16";
_l2.setTextSize((float) (16));
 //BA.debugLineNum = 541;BA.debugLine="l1.TextSize=15";
_l1.setTextSize((float) (15));
 //BA.debugLineNum = 542;BA.debugLine="l1.textcolor=mcl.md_black_1000";
_l1.setTextColor(mostCurrent._mcl.getmd_black_1000());
 //BA.debugLineNum = 543;BA.debugLine="l1.Text=\"S-Cleaner beenden?\"";
_l1.setText(BA.ObjectToCharSequence("S-Cleaner beenden?"));
 //BA.debugLineNum = 544;BA.debugLine="l2.textcolor=Colors.ARGB(255,0,0,0)";
_l2.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (0),(int) (0),(int) (0)));
 //BA.debugLineNum = 545;BA.debugLine="l1.Gravity=Gravity.TOP";
_l1.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.TOP);
 //BA.debugLineNum = 546;BA.debugLine="l1.Typeface=rfont";
_l1.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 547;BA.debugLine="l2.Typeface=rfont";
_l2.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 548;BA.debugLine="Builder.Initialize(\"closedia\")";
_builder.Initialize(mostCurrent.activityBA,"closedia");
 //BA.debugLineNum = 549;BA.debugLine="Builder.Title(\"Beenden:?\").TitleColor(mcl.md_red_";
_builder.Title(BA.ObjectToCharSequence("Beenden:?")).TitleColor(mostCurrent._mcl.getmd_red_800()).Icon((android.graphics.drawable.Drawable)(_inf.getObject())).LimitIconToDefaultSize().Theme(_builder.THEME_LIGHT).Content(BA.ObjectToCharSequence(_l1.getText())).ContentLineSpacing((float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1)))).Cancelable(anywheresoftware.b4a.keywords.Common.True).NeutralText(BA.ObjectToCharSequence("Abbrechen")).Typeface((android.graphics.Typeface)(_rfont.getObject()),(android.graphics.Typeface)(_rfont.getObject())).NeutralColor(mostCurrent._mcl.getmd_blue_A700()).PositiveText(BA.ObjectToCharSequence("Ja bitte")).PositiveColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (217),(int) (87),(int) (217))).ContentGravity(_builder.GRAVITY_START);
 //BA.debugLineNum = 550;BA.debugLine="infodia=Builder.Show";
_infodia = _builder.Show();
 //BA.debugLineNum = 551;BA.debugLine="infodia.Show";
_infodia.Show();
 //BA.debugLineNum = 552;BA.debugLine="End Sub";
return "";
}
public static String  _finish_modul() throws Exception{
 //BA.debugLineNum = 500;BA.debugLine="Sub finish_modul";
 //BA.debugLineNum = 502;BA.debugLine="ac1.SetVisibleAnimated(150,True)";
mostCurrent._ac1.SetVisibleAnimated((int) (150),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 503;BA.debugLine="lv2.SetVisibleAnimated(180,True)";
mostCurrent._lv2.SetVisibleAnimated((int) (180),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 504;BA.debugLine="bm2.SetVisibleAnimated(180,True)";
mostCurrent._bm2.SetVisibleAnimated((int) (180),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 505;BA.debugLine="subtext.SetVisibleAnimated(100,False)";
mostCurrent._subtext.SetVisibleAnimated((int) (100),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 506;BA.debugLine="maintext.SetVisibleAnimated(100,False)";
mostCurrent._maintext.SetVisibleAnimated((int) (100),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 507;BA.debugLine="stext.SetVisibleAnimated(100,True)";
mostCurrent._stext.SetVisibleAnimated((int) (100),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 508;BA.debugLine="Activity.Color=Colors.ARGB(255,255,255,255)";
mostCurrent._activity.setColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (255),(int) (255),(int) (255)));
 //BA.debugLineNum = 510;BA.debugLine="prog1.SetVisibleAnimated(200,False)";
mostCurrent._prog1.SetVisibleAnimated((int) (200),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 511;BA.debugLine="abhelper.Show";
mostCurrent._abhelper.Show();
 //BA.debugLineNum = 512;BA.debugLine="ani1.StopAnin(bm1)";
mostCurrent._ani1.StopAnin((android.view.View)(mostCurrent._bm1.getObject()));
 //BA.debugLineNum = 513;BA.debugLine="text_meth";
_text_meth();
 //BA.debugLineNum = 514;BA.debugLine="sub_list";
_sub_list();
 //BA.debugLineNum = 515;BA.debugLine="t_line";
_t_line();
 //BA.debugLineNum = 516;BA.debugLine="acb_method";
_acb_method();
 //BA.debugLineNum = 517;BA.debugLine="get_data";
_get_data();
 //BA.debugLineNum = 518;BA.debugLine="End Sub";
return "";
}
public static String  _formatfilesize(float _bytes) throws Exception{
String[] _unit = null;
double _po = 0;
double _si = 0;
int _i = 0;
 //BA.debugLineNum = 589;BA.debugLine="Sub FormatFileSize(Bytes As Float) As String";
 //BA.debugLineNum = 590;BA.debugLine="Private Unit() As String = Array As String(\" Byte";
_unit = new String[]{" Byte"," KB"," MB"," GB"," TB"," PB"," EB"," ZB"," YB"};
 //BA.debugLineNum = 591;BA.debugLine="If Bytes = 0 Then";
if (_bytes==0) { 
 //BA.debugLineNum = 592;BA.debugLine="Return \"0 Bytes\"";
if (true) return "0 Bytes";
 }else {
 //BA.debugLineNum = 594;BA.debugLine="Private Po, Si As Double";
_po = 0;
_si = 0;
 //BA.debugLineNum = 595;BA.debugLine="Private I As Int";
_i = 0;
 //BA.debugLineNum = 596;BA.debugLine="Bytes = Abs(Bytes)";
_bytes = (float) (anywheresoftware.b4a.keywords.Common.Abs(_bytes));
 //BA.debugLineNum = 597;BA.debugLine="I = Floor(Logarithm(Bytes, 1024))";
_i = (int) (anywheresoftware.b4a.keywords.Common.Floor(anywheresoftware.b4a.keywords.Common.Logarithm(_bytes,1024)));
 //BA.debugLineNum = 598;BA.debugLine="Po = Power(1024, I)";
_po = anywheresoftware.b4a.keywords.Common.Power(1024,_i);
 //BA.debugLineNum = 599;BA.debugLine="Si = Bytes / Po";
_si = _bytes/(double)_po;
 //BA.debugLineNum = 600;BA.debugLine="Return NumberFormat(Si, 1, 2) & Unit(I)";
if (true) return anywheresoftware.b4a.keywords.Common.NumberFormat(_si,(int) (1),(int) (2))+_unit[_i];
 };
 //BA.debugLineNum = 602;BA.debugLine="End Sub";
return "";
}
public static String  _get_data() throws Exception{
anywheresoftware.b4a.objects.LabelWrapper _la = null;
anywheresoftware.b4a.objects.LabelWrapper _la5 = null;
anywheresoftware.b4a.objects.collections.List _plist = null;
 //BA.debugLineNum = 356;BA.debugLine="Sub get_data";
 //BA.debugLineNum = 357;BA.debugLine="lv2.Clear";
mostCurrent._lv2.Clear();
 //BA.debugLineNum = 358;BA.debugLine="Dim la,la5 As Label";
_la = new anywheresoftware.b4a.objects.LabelWrapper();
_la5 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 359;BA.debugLine="la5.Initialize(\"\")";
_la5.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 360;BA.debugLine="la.Initialize(\"\")";
_la.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 361;BA.debugLine="la=lv2.SingleLineLayout.Label";
_la = mostCurrent._lv2.getSingleLineLayout().Label;
 //BA.debugLineNum = 362;BA.debugLine="la.TextSize=14";
_la.setTextSize((float) (14));
 //BA.debugLineNum = 363;BA.debugLine="la.Typeface=rfont";
_la.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 364;BA.debugLine="la.TextColor=mcl.md_black_1000";
_la.setTextColor(mostCurrent._mcl.getmd_black_1000());
 //BA.debugLineNum = 365;BA.debugLine="la.Gravity=Gravity.CENTER_HORIZONTAL";
_la.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER_HORIZONTAL);
 //BA.debugLineNum = 366;BA.debugLine="lv2.SingleLineLayout.ItemHeight=50dip";
mostCurrent._lv2.getSingleLineLayout().setItemHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 367;BA.debugLine="Dim plist As List";
_plist = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 368;BA.debugLine="plist.Initialize";
_plist.Initialize();
 //BA.debugLineNum = 369;BA.debugLine="plist=pack.GetInstalledPackages";
_plist = mostCurrent._pack.GetInstalledPackages();
 //BA.debugLineNum = 370;BA.debugLine="lv2.AddSingleLine2(plist.Size&\"(System)/\"&applist";
mostCurrent._lv2.AddSingleLine2(BA.ObjectToCharSequence(BA.NumberToString(_plist.getSize())+"(System)/"+BA.NumberToString(mostCurrent._applist.getSize())+"(Nutzer)"+anywheresoftware.b4a.keywords.Common.CRLF+"Anwendungen auf "+_os.getDevice()+" instaliert"),(Object)(0));
 //BA.debugLineNum = 371;BA.debugLine="End Sub";
return "";
}
public static Object  _get_respath(String _pac) throws Exception{
anywheresoftware.b4a.agraham.reflection.Reflection _r = null;
 //BA.debugLineNum = 815;BA.debugLine="Sub get_respath(pac As String) As Object";
 //BA.debugLineNum = 816;BA.debugLine="Dim r As Reflector";
_r = new anywheresoftware.b4a.agraham.reflection.Reflection();
 //BA.debugLineNum = 817;BA.debugLine="r.Target = r.GetContext";
_r.Target = (Object)(_r.GetContext(processBA));
 //BA.debugLineNum = 818;BA.debugLine="r.Target = r.RunMethod(\"getPackageManager\")";
_r.Target = _r.RunMethod("getPackageManager");
 //BA.debugLineNum = 819;BA.debugLine="r.Target = r.RunMethod3(\"getApplicationInfo\", pac";
_r.Target = _r.RunMethod3("getApplicationInfo",_pac,"java.lang.String",BA.NumberToString(0x00000001),"java.lang.int");
 //BA.debugLineNum = 820;BA.debugLine="Return r.GetField(\"dataDir\")";
if (true) return _r.GetField("dataDir");
 //BA.debugLineNum = 821;BA.debugLine="End Sub";
return null;
}
public static Object  _getactivitiesinfo(String _pac) throws Exception{
anywheresoftware.b4a.agraham.reflection.Reflection _r = null;
 //BA.debugLineNum = 797;BA.debugLine="Sub GetActivitiesInfo(pac As String) As Object";
 //BA.debugLineNum = 798;BA.debugLine="Dim r As Reflector";
_r = new anywheresoftware.b4a.agraham.reflection.Reflection();
 //BA.debugLineNum = 799;BA.debugLine="r.Target = r.GetContext";
_r.Target = (Object)(_r.GetContext(processBA));
 //BA.debugLineNum = 800;BA.debugLine="r.Target = r.RunMethod(\"getPackageManager\")";
_r.Target = _r.RunMethod("getPackageManager");
 //BA.debugLineNum = 801;BA.debugLine="r.Target = r.RunMethod3(\"getPackageInfo\", pac, \"j";
_r.Target = _r.RunMethod3("getPackageInfo",_pac,"java.lang.String",BA.NumberToString(0x00000001),"java.lang.int");
 //BA.debugLineNum = 802;BA.debugLine="Return r.GetField(\"applicationInfo\")";
if (true) return _r.GetField("applicationInfo");
 //BA.debugLineNum = 803;BA.debugLine="End Sub";
return null;
}
public static String  _getfilename(String _fullpath) throws Exception{
 //BA.debugLineNum = 774;BA.debugLine="Sub GetFileName(FullPath As String) As String";
 //BA.debugLineNum = 775;BA.debugLine="Return FullPath.SubString(FullPath.LastIndexOf(\"/";
if (true) return _fullpath.substring((int) (_fullpath.lastIndexOf("/")+1));
 //BA.debugLineNum = 776;BA.debugLine="End Sub";
return "";
}
public static String  _getparentpath(String _path) throws Exception{
String _path1 = "";
String _l = "";
 //BA.debugLineNum = 779;BA.debugLine="Sub GetParentPath(path As String) As String";
 //BA.debugLineNum = 780;BA.debugLine="Dim Path1 As String";
_path1 = "";
 //BA.debugLineNum = 781;BA.debugLine="If path = \"/\" Then";
if ((_path).equals("/")) { 
 //BA.debugLineNum = 782;BA.debugLine="Return \"/\"";
if (true) return "/";
 };
 //BA.debugLineNum = 784;BA.debugLine="L = path.LastIndexOf(\"/\")";
_l = BA.NumberToString(_path.lastIndexOf("/"));
 //BA.debugLineNum = 785;BA.debugLine="If L = path.Length - 1 Then";
if ((_l).equals(BA.NumberToString(_path.length()-1))) { 
 //BA.debugLineNum = 786;BA.debugLine="Path1 = path.SubString2(0,L)";
_path1 = _path.substring((int) (0),(int)(Double.parseDouble(_l)));
 }else {
 //BA.debugLineNum = 788;BA.debugLine="Path1 = path";
_path1 = _path;
 };
 //BA.debugLineNum = 790;BA.debugLine="L = path.LastIndexOf(\"/\")";
_l = BA.NumberToString(_path.lastIndexOf("/"));
 //BA.debugLineNum = 791;BA.debugLine="If L = 0 Then";
if ((_l).equals(BA.NumberToString(0))) { 
 //BA.debugLineNum = 792;BA.debugLine="L = 1";
_l = BA.NumberToString(1);
 };
 //BA.debugLineNum = 794;BA.debugLine="Return Path1.SubString2(0,L)";
if (true) return _path1.substring((int) (0),(int)(Double.parseDouble(_l)));
 //BA.debugLineNum = 795;BA.debugLine="End Sub";
return "";
}
public static String  _getsourcedir(Object _appinfo) throws Exception{
anywheresoftware.b4a.agraham.reflection.Reflection _r = null;
 //BA.debugLineNum = 805;BA.debugLine="Sub GetSourceDir(AppInfo As Object) As String";
 //BA.debugLineNum = 806;BA.debugLine="Try";
try { //BA.debugLineNum = 807;BA.debugLine="Dim r As Reflector";
_r = new anywheresoftware.b4a.agraham.reflection.Reflection();
 //BA.debugLineNum = 808;BA.debugLine="r.Target = AppInfo";
_r.Target = _appinfo;
 //BA.debugLineNum = 809;BA.debugLine="Return r.GetField(\"sourceDir\")";
if (true) return BA.ObjectToString(_r.GetField("sourceDir"));
 } 
       catch (Exception e6) {
			processBA.setLastException(e6); //BA.debugLineNum = 811;BA.debugLine="Return \"\"";
if (true) return "";
 };
 //BA.debugLineNum = 813;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 37;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 38;BA.debugLine="Private ani1 As ICOSSlideAnimation";
mostCurrent._ani1 = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
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
 //BA.debugLineNum = 50;BA.debugLine="Private sico As BitmapDrawable";
mostCurrent._sico = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 51;BA.debugLine="Private bm1,bm2 As ImageView";
mostCurrent._bm1 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._bm2 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 52;BA.debugLine="Private stext,subtext As Label";
mostCurrent._stext = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._subtext = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 53;BA.debugLine="Dim counter As Int";
_counter = 0;
 //BA.debugLineNum = 54;BA.debugLine="Dim cs As CSBuilder";
mostCurrent._cs = new anywheresoftware.b4a.objects.CSBuilder();
 //BA.debugLineNum = 55;BA.debugLine="Private lv2 As ListView";
mostCurrent._lv2 = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 56;BA.debugLine="Private pcb1 As ProcessButton";
mostCurrent._pcb1 = new de.donmanfred.ProcessButtonWrapper();
 //BA.debugLineNum = 57;BA.debugLine="Private Panel2 As Panel";
mostCurrent._panel2 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 58;BA.debugLine="Dim pb As ACButton";
mostCurrent._pb = new de.amberhome.objects.appcompat.ACButtonWrapper();
 //BA.debugLineNum = 59;BA.debugLine="Private stats As OSStats";
mostCurrent._stats = new b4a.example.osstats();
 //BA.debugLineNum = 60;BA.debugLine="Private ram As  MSOS";
mostCurrent._ram = new com.maximussoft.msos.MSOS();
 //BA.debugLineNum = 61;BA.debugLine="Private prog1 As NumberProgressBar";
mostCurrent._prog1 = new de.donmanfred.NumberProgressBarWrapper();
 //BA.debugLineNum = 62;BA.debugLine="Private maintext As Label";
mostCurrent._maintext = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 63;BA.debugLine="Private ltext As Label";
mostCurrent._ltext = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 64;BA.debugLine="Private lstext As Label";
mostCurrent._lstext = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 65;BA.debugLine="Private proclist,procname,procpid As List";
mostCurrent._proclist = new anywheresoftware.b4a.objects.collections.List();
mostCurrent._procname = new anywheresoftware.b4a.objects.collections.List();
mostCurrent._procpid = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 66;BA.debugLine="Dim pn,appan As Panel";
mostCurrent._pn = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._appan = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 67;BA.debugLine="Dim es,back As ACFlatButton";
mostCurrent._es = new de.amberhome.objects.appcompat.ACFlatButtonWrapper();
mostCurrent._back = new de.amberhome.objects.appcompat.ACFlatButtonWrapper();
 //BA.debugLineNum = 68;BA.debugLine="Private applist As ListView";
mostCurrent._applist = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 69;BA.debugLine="Private ac1 As ACButton";
mostCurrent._ac1 = new de.amberhome.objects.appcompat.ACButtonWrapper();
 //BA.debugLineNum = 70;BA.debugLine="End Sub";
return "";
}
public static String  _left() throws Exception{
anywheresoftware.b4a.objects.LabelWrapper _la1 = null;
anywheresoftware.b4a.objects.LabelWrapper _la2 = null;
 //BA.debugLineNum = 295;BA.debugLine="Sub left";
 //BA.debugLineNum = 296;BA.debugLine="Dim la1,la2 As Label";
_la1 = new anywheresoftware.b4a.objects.LabelWrapper();
_la2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 297;BA.debugLine="la2.Initialize(\"\")";
_la2.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 298;BA.debugLine="la1.Initialize(\"\")";
_la1.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 299;BA.debugLine="la1=leftlist.TwoLinesAndBitmap.Label";
_la1 = mostCurrent._leftlist.getTwoLinesAndBitmap().Label;
 //BA.debugLineNum = 300;BA.debugLine="la2=leftlist.TwoLinesAndBitmap.SecondLabel";
_la2 = mostCurrent._leftlist.getTwoLinesAndBitmap().SecondLabel;
 //BA.debugLineNum = 301;BA.debugLine="leftlist.TwoLinesAndBitmap.ImageView.Height=34dip";
mostCurrent._leftlist.getTwoLinesAndBitmap().ImageView.setHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (34)));
 //BA.debugLineNum = 302;BA.debugLine="leftlist.TwoLinesAndBitmap.ImageView.Width=34dip";
mostCurrent._leftlist.getTwoLinesAndBitmap().ImageView.setWidth(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (34)));
 //BA.debugLineNum = 304;BA.debugLine="la1.TextSize=15";
_la1.setTextSize((float) (15));
 //BA.debugLineNum = 305;BA.debugLine="la2.TextSize=13";
_la2.setTextSize((float) (13));
 //BA.debugLineNum = 306;BA.debugLine="la1.Typeface=rfont";
_la1.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 307;BA.debugLine="la2.Typeface=rfont";
_la2.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 308;BA.debugLine="la1.TextColor=mcl.md_grey_900";
_la1.setTextColor(mostCurrent._mcl.getmd_grey_900());
 //BA.debugLineNum = 309;BA.debugLine="la2.TextColor=Colors.ARGB(180,255,255,255)";
_la2.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (180),(int) (255),(int) (255),(int) (255)));
 //BA.debugLineNum = 310;BA.debugLine="ltext.TextColor=mcl.md_grey_900";
mostCurrent._ltext.setTextColor(mostCurrent._mcl.getmd_grey_900());
 //BA.debugLineNum = 311;BA.debugLine="ltext.TextSize=18";
mostCurrent._ltext.setTextSize((float) (18));
 //BA.debugLineNum = 312;BA.debugLine="ltext.Typeface=rfont";
mostCurrent._ltext.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 313;BA.debugLine="ltext.Gravity=Gravity.CENTER";
mostCurrent._ltext.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 314;BA.debugLine="ltext.Text=\"Hauptmenü\"";
mostCurrent._ltext.setText(BA.ObjectToCharSequence("Hauptmenü"));
 //BA.debugLineNum = 315;BA.debugLine="lstext.TextColor=mcl.md_white_1000";
mostCurrent._lstext.setTextColor(mostCurrent._mcl.getmd_white_1000());
 //BA.debugLineNum = 316;BA.debugLine="lstext.TextSize=13";
mostCurrent._lstext.setTextSize((float) (13));
 //BA.debugLineNum = 317;BA.debugLine="lstext.Typeface=rfont";
mostCurrent._lstext.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 318;BA.debugLine="lstext.Gravity=Gravity.CENTER";
mostCurrent._lstext.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 319;BA.debugLine="lstext.Text=\"Version: \"&pack.GetVersionName(packa";
mostCurrent._lstext.setText(BA.ObjectToCharSequence("Version: "+mostCurrent._pack.GetVersionName(_package)));
 //BA.debugLineNum = 320;BA.debugLine="leftlist.AddTwoLinesAndBitmap2(\"App Verwaltung\",\"";
mostCurrent._leftlist.AddTwoLinesAndBitmap2(BA.ObjectToCharSequence("App Verwaltung"),BA.ObjectToCharSequence("Anwendungen verwalten"),(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"app.png").getObject()),(Object)(3));
 //BA.debugLineNum = 321;BA.debugLine="leftlist.AddTwoLinesAndBitmap2(\"Log\",\"zeige letzt";
mostCurrent._leftlist.AddTwoLinesAndBitmap2(BA.ObjectToCharSequence("Log"),BA.ObjectToCharSequence("zeige letzte Ergebnisse"),(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"list.png").getObject()),(Object)(0));
 //BA.debugLineNum = 322;BA.debugLine="leftlist.AddTwoLinesAndBitmap2(\"Info\",\"Über S-Cle";
mostCurrent._leftlist.AddTwoLinesAndBitmap2(BA.ObjectToCharSequence("Info"),BA.ObjectToCharSequence("Über S-Cleaner"),(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"push-pin.png").getObject()),(Object)(1));
 //BA.debugLineNum = 323;BA.debugLine="leftlist.AddTwoLinesAndBitmap2(\"Teilen\",\"via Soci";
mostCurrent._leftlist.AddTwoLinesAndBitmap2(BA.ObjectToCharSequence("Teilen"),BA.ObjectToCharSequence("via Social-Network teilen"),(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"share-2.png").getObject()),(Object)(4));
 //BA.debugLineNum = 324;BA.debugLine="leftlist.AddTwoLinesAndBitmap2(\"Exit\",\"Programm b";
mostCurrent._leftlist.AddTwoLinesAndBitmap2(BA.ObjectToCharSequence("Exit"),BA.ObjectToCharSequence("Programm beenden"),(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"exit.png").getObject()),(Object)(2));
 //BA.debugLineNum = 325;BA.debugLine="End Sub";
return "";
}
public static String  _leftlist_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 327;BA.debugLine="Sub leftlist_ItemClick (Position As Int, Value As";
 //BA.debugLineNum = 328;BA.debugLine="Select Value";
switch (BA.switchObjectToInt(_value,(Object)(0),(Object)(1),(Object)(2),(Object)(3),(Object)(4))) {
case 0: {
 //BA.debugLineNum = 330;BA.debugLine="sm.HideMenus";
mostCurrent._sm.HideMenus();
 //BA.debugLineNum = 331;BA.debugLine="app_info";
_app_info();
 //BA.debugLineNum = 332;BA.debugLine="Animator.setanimati(\"extra_in\", \"extra_out\")";
mostCurrent._animator._setanimati(mostCurrent.activityBA,"extra_in","extra_out");
 break; }
case 1: {
 //BA.debugLineNum = 334;BA.debugLine="sm.HideMenus";
mostCurrent._sm.HideMenus();
 //BA.debugLineNum = 335;BA.debugLine="about";
_about();
 //BA.debugLineNum = 336;BA.debugLine="Animator.setanimati(\"extra_in\", \"extra_out\")";
mostCurrent._animator._setanimati(mostCurrent.activityBA,"extra_in","extra_out");
 break; }
case 2: {
 //BA.debugLineNum = 338;BA.debugLine="sm.HideMenus";
mostCurrent._sm.HideMenus();
 //BA.debugLineNum = 339;BA.debugLine="exit_click";
_exit_click();
 break; }
case 3: {
 //BA.debugLineNum = 341;BA.debugLine="sm.HideMenus";
mostCurrent._sm.HideMenus();
 //BA.debugLineNum = 342;BA.debugLine="pack_manager";
_pack_manager();
 break; }
case 4: {
 //BA.debugLineNum = 345;BA.debugLine="sm.HideMenus";
mostCurrent._sm.HideMenus();
 //BA.debugLineNum = 346;BA.debugLine="app_share";
_app_share();
 break; }
}
;
 //BA.debugLineNum = 348;BA.debugLine="End Sub";
return "";
}
public static String  _lv2_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 604;BA.debugLine="Sub lv2_ItemClick (Position As Int, Value As Objec";
 //BA.debugLineNum = 605;BA.debugLine="If Value=Position Then";
if ((_value).equals((Object)(_position))) { 
 //BA.debugLineNum = 607;BA.debugLine="ProgressDialogShow(\"Bitte warte...\")";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,BA.ObjectToCharSequence("Bitte warte..."));
 //BA.debugLineNum = 608;BA.debugLine="pack_manager";
_pack_manager();
 //BA.debugLineNum = 609;BA.debugLine="Log(\"pck\")";
anywheresoftware.b4a.keywords.Common.Log("pck");
 };
 //BA.debugLineNum = 611;BA.debugLine="End Sub";
return "";
}
public static String  _pack_manager() throws Exception{
 //BA.debugLineNum = 667;BA.debugLine="Sub pack_manager";
 //BA.debugLineNum = 668;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 //BA.debugLineNum = 669;BA.debugLine="ac1.SetVisibleAnimated(200,False)";
mostCurrent._ac1.SetVisibleAnimated((int) (200),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 670;BA.debugLine="appan.SetVisibleAnimated(300,True)";
mostCurrent._appan.SetVisibleAnimated((int) (300),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 671;BA.debugLine="back.Text=\"zurück\"";
mostCurrent._back.setText(BA.ObjectToCharSequence("zurück"));
 //BA.debugLineNum = 672;BA.debugLine="back.Typeface=rfont";
mostCurrent._back.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 673;BA.debugLine="back.TextColor=mcl.md_white_1000";
mostCurrent._back.setTextColor(mostCurrent._mcl.getmd_white_1000());
 //BA.debugLineNum = 674;BA.debugLine="back.Color=mcl.md_light_blue_A700";
mostCurrent._back.setColor(mostCurrent._mcl.getmd_light_blue_A700());
 //BA.debugLineNum = 675;BA.debugLine="back.Gravity=Gravity.CENTER_HORIZONTAL";
mostCurrent._back.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER_HORIZONTAL);
 //BA.debugLineNum = 676;BA.debugLine="proc_pack";
_proc_pack();
 //BA.debugLineNum = 677;BA.debugLine="End Sub";
return "";
}
public static String  _pb_click() throws Exception{
 //BA.debugLineNum = 283;BA.debugLine="Sub pb_Click";
 //BA.debugLineNum = 284;BA.debugLine="ac1.SetVisibleAnimated(350,True)";
mostCurrent._ac1.SetVisibleAnimated((int) (350),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 285;BA.debugLine="End Sub";
return "";
}
public static String  _proc_pack() throws Exception{
anywheresoftware.b4a.objects.LabelWrapper _la1 = null;
anywheresoftware.b4a.objects.LabelWrapper _la2 = null;
anywheresoftware.b4a.agraham.reflection.Reflection _obj1 = null;
anywheresoftware.b4a.agraham.reflection.Reflection _obj2 = null;
anywheresoftware.b4a.agraham.reflection.Reflection _obj3 = null;
int _size = 0;
int _i = 0;
int _flags = 0;
String _name = "";
anywheresoftware.b4a.objects.drawable.BitmapDrawable _icon = null;
 //BA.debugLineNum = 679;BA.debugLine="Sub proc_pack";
 //BA.debugLineNum = 680;BA.debugLine="Dim la1,la2 As Label";
_la1 = new anywheresoftware.b4a.objects.LabelWrapper();
_la2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 681;BA.debugLine="la2.Initialize(\"\")";
_la2.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 682;BA.debugLine="la1.Initialize(\"\")";
_la1.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 683;BA.debugLine="la1=applist.TwoLinesAndBitmap.Label";
_la1 = mostCurrent._applist.getTwoLinesAndBitmap().Label;
 //BA.debugLineNum = 684;BA.debugLine="la2=applist.TwoLinesAndBitmap.SecondLabel";
_la2 = mostCurrent._applist.getTwoLinesAndBitmap().SecondLabel;
 //BA.debugLineNum = 685;BA.debugLine="la1.TextSize=15";
_la1.setTextSize((float) (15));
 //BA.debugLineNum = 686;BA.debugLine="la2.TextSize=12";
_la2.setTextSize((float) (12));
 //BA.debugLineNum = 687;BA.debugLine="la1.Typeface=rfont";
_la1.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 688;BA.debugLine="la2.Typeface=rfont";
_la2.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 689;BA.debugLine="la2.TextColor=mcl.md_amber_700'Colors.ARGB(255,25";
_la2.setTextColor(mostCurrent._mcl.getmd_amber_700());
 //BA.debugLineNum = 690;BA.debugLine="applist.TwoLinesAndBitmap.ImageView.Height=48dip";
mostCurrent._applist.getTwoLinesAndBitmap().ImageView.setHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (48)));
 //BA.debugLineNum = 691;BA.debugLine="applist.TwoLinesAndBitmap.ImageView.Width=48dip";
mostCurrent._applist.getTwoLinesAndBitmap().ImageView.setWidth(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (48)));
 //BA.debugLineNum = 692;BA.debugLine="applist.TwoLinesAndBitmap.ItemHeight=70dip";
mostCurrent._applist.getTwoLinesAndBitmap().setItemHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (70)));
 //BA.debugLineNum = 693;BA.debugLine="applist.Clear";
mostCurrent._applist.Clear();
 //BA.debugLineNum = 694;BA.debugLine="Dim Obj1, Obj2, Obj3 As Reflector";
_obj1 = new anywheresoftware.b4a.agraham.reflection.Reflection();
_obj2 = new anywheresoftware.b4a.agraham.reflection.Reflection();
_obj3 = new anywheresoftware.b4a.agraham.reflection.Reflection();
 //BA.debugLineNum = 695;BA.debugLine="Dim size, i, flags As Int";
_size = 0;
_i = 0;
_flags = 0;
 //BA.debugLineNum = 696;BA.debugLine="Dim name As String";
_name = "";
 //BA.debugLineNum = 697;BA.debugLine="Obj1.Target = Obj1.GetContext";
_obj1.Target = (Object)(_obj1.GetContext(processBA));
 //BA.debugLineNum = 698;BA.debugLine="Obj1.Target = Obj1.RunMethod(\"getPackageManager\")";
_obj1.Target = _obj1.RunMethod("getPackageManager");
 //BA.debugLineNum = 699;BA.debugLine="Obj1.Target = Obj1.RunMethod2(\"getInstalledPackag";
_obj1.Target = _obj1.RunMethod2("getInstalledPackages",BA.NumberToString(0),"java.lang.int");
 //BA.debugLineNum = 700;BA.debugLine="size = Obj1.RunMethod(\"size\")";
_size = (int)(BA.ObjectToNumber(_obj1.RunMethod("size")));
 //BA.debugLineNum = 701;BA.debugLine="For i = 0 To size -1";
{
final int step22 = 1;
final int limit22 = (int) (_size-1);
_i = (int) (0) ;
for (;(step22 > 0 && _i <= limit22) || (step22 < 0 && _i >= limit22) ;_i = ((int)(0 + _i + step22))  ) {
 //BA.debugLineNum = 702;BA.debugLine="Obj2.Target = Obj1.RunMethod2(\"get\", i, \"java.la";
_obj2.Target = _obj1.RunMethod2("get",BA.NumberToString(_i),"java.lang.int");
 //BA.debugLineNum = 703;BA.debugLine="name = Obj2.GetField(\"packageName\")";
_name = BA.ObjectToString(_obj2.GetField("packageName"));
 //BA.debugLineNum = 704;BA.debugLine="Obj3.Target = Obj2.GetField(\"applicationInfo\") '";
_obj3.Target = _obj2.GetField("applicationInfo");
 //BA.debugLineNum = 705;BA.debugLine="flags = Obj3.GetField(\"flags\")";
_flags = (int)(BA.ObjectToNumber(_obj3.GetField("flags")));
 //BA.debugLineNum = 706;BA.debugLine="If Bit.And(flags, 1)  = 0 Then";
if (anywheresoftware.b4a.keywords.Common.Bit.And(_flags,(int) (1))==0) { 
 //BA.debugLineNum = 709;BA.debugLine="Dim icon As BitmapDrawable=pack.GetApplicationI";
_icon = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
_icon.setObject((android.graphics.drawable.BitmapDrawable)(mostCurrent._pack.GetApplicationIcon(_name)));
 //BA.debugLineNum = 710;BA.debugLine="applist.AddTwoLinesAndBitmap2(pack.GetApplicati";
mostCurrent._applist.AddTwoLinesAndBitmap2(BA.ObjectToCharSequence(mostCurrent._pack.GetApplicationLabel(_name)+" - "+mostCurrent._pack.GetVersionName(_name)),BA.ObjectToCharSequence("Pfad: "+BA.ObjectToString(_get_respath(_name))+anywheresoftware.b4a.keywords.Common.CRLF+_name),_icon.getBitmap(),(Object)(_name));
 };
 }
};
 //BA.debugLineNum = 713;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
starter._process_globals();
animator._process_globals();
sub2._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 27;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 28;BA.debugLine="Dim infodia As MaterialDialog";
_infodia = new de.amberhome.materialdialogs.MaterialDialogWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Private os As OperatingSystem";
_os = new com.rootsoft.oslibrary.OSLibrary();
 //BA.debugLineNum = 30;BA.debugLine="Dim date,time1 As String";
_date = "";
_time1 = "";
 //BA.debugLineNum = 31;BA.debugLine="Private rfont As Typeface= rfont.LoadFromAssets(\"";
_rfont = new anywheresoftware.b4a.keywords.constants.TypefaceWrapper();
_rfont.setObject((android.graphics.Typeface)(_rfont.LoadFromAssets("micross.ttf")));
 //BA.debugLineNum = 32;BA.debugLine="Private package As String=\"sclean2.com\"";
_package = "sclean2.com";
 //BA.debugLineNum = 33;BA.debugLine="Dim t1 As Timer";
_t1 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 34;BA.debugLine="Dim ion As Object";
_ion = new Object();
 //BA.debugLineNum = 35;BA.debugLine="End Sub";
return "";
}
public static String  _resdia_buttonpressed(de.amberhome.materialdialogs.MaterialDialogWrapper _dialog,String _action) throws Exception{
 //BA.debugLineNum = 271;BA.debugLine="Sub resdia_ButtonPressed (Dialog As MaterialDialog";
 //BA.debugLineNum = 272;BA.debugLine="Select Action";
switch (BA.switchObjectToInt(_action,_dialog.ACTION_POSITIVE,_dialog.ACTION_NEGATIVE,_dialog.ACTION_NEUTRAL)) {
case 0: {
 //BA.debugLineNum = 274;BA.debugLine="app_info";
_app_info();
 //BA.debugLineNum = 275;BA.debugLine="Animator.setanimati(\"popup_enter\", \"popup_exit\"";
mostCurrent._animator._setanimati(mostCurrent.activityBA,"popup_enter","popup_exit");
 break; }
case 1: {
 break; }
case 2: {
 break; }
}
;
 //BA.debugLineNum = 281;BA.debugLine="End Sub";
return "";
}
public static String  _stats_update(float[] _cpuefficiency,float _ramusage) throws Exception{
int _ru = 0;
 //BA.debugLineNum = 431;BA.debugLine="Sub stats_Update(CPUEfficiency() As Float, RAMUsag";
 //BA.debugLineNum = 432;BA.debugLine="Dim ru As Int";
_ru = 0;
 //BA.debugLineNum = 434;BA.debugLine="ru=RAMUsage*1024*1024*10-ram.getSystemTotalMemory";
_ru = (int) (_ramusage*1024*1024*10-mostCurrent._ram.getSystemTotalMemorySize(mostCurrent.activityBA));
 //BA.debugLineNum = 435;BA.debugLine="dbase.Put(\"ru\",ru)";
mostCurrent._dbase._put("ru",(Object)(_ru));
 //BA.debugLineNum = 436;BA.debugLine="End Sub";
return "";
}
public static String  _sub_list() throws Exception{
 //BA.debugLineNum = 350;BA.debugLine="Sub sub_list";
 //BA.debugLineNum = 351;BA.debugLine="stext.TextSize=18";
mostCurrent._stext.setTextSize((float) (18));
 //BA.debugLineNum = 352;BA.debugLine="bm1.Bitmap=LoadBitmap(File.DirAssets,\"smartphone";
mostCurrent._bm1.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"smartphone.png").getObject()));
 //BA.debugLineNum = 353;BA.debugLine="stext.Text=cs.Initialize.Size(18).Append(\"letzte";
mostCurrent._stext.setText(BA.ObjectToCharSequence(mostCurrent._cs.Initialize().Size((int) (18)).Append(BA.ObjectToCharSequence("letzte reinigung:")).Pop().Color(mostCurrent._mcl.getmd_light_blue_A700()).Append(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.CRLF+" App(s): "+BA.NumberToString(mostCurrent._alist._listkeys().getSize())+anywheresoftware.b4a.keywords.Common.CRLF+"Daten: "+BA.ObjectToString(mostCurrent._kvsdata._get("cz")))).PopAll().getObject()));
 //BA.debugLineNum = 354;BA.debugLine="End Sub";
return "";
}
public static String  _t_line() throws Exception{
de.amberhome.materialdialogs.MaterialDialogBuilderWrapper _builder = null;
anywheresoftware.b4a.objects.drawable.BitmapDrawable _inf = null;
anywheresoftware.b4a.objects.LabelWrapper _l1 = null;
anywheresoftware.b4a.objects.LabelWrapper _l2 = null;
anywheresoftware.b4a.objects.PanelWrapper _pnl = null;
 //BA.debugLineNum = 248;BA.debugLine="Sub t_line";
 //BA.debugLineNum = 249;BA.debugLine="Dim Builder As MaterialDialogBuilder";
_builder = new de.amberhome.materialdialogs.MaterialDialogBuilderWrapper();
 //BA.debugLineNum = 250;BA.debugLine="Dim inf As BitmapDrawable";
_inf = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 251;BA.debugLine="inf.Initialize(LoadBitmap(File.DirAssets,\"push-pi";
_inf.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"push-pin.png").getObject()));
 //BA.debugLineNum = 252;BA.debugLine="Dim l1,l2 As Label";
_l1 = new anywheresoftware.b4a.objects.LabelWrapper();
_l2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 253;BA.debugLine="Dim pnl As Panel";
_pnl = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 254;BA.debugLine="pnl.Initialize(\"pnl\")";
_pnl.Initialize(mostCurrent.activityBA,"pnl");
 //BA.debugLineNum = 255;BA.debugLine="l1.Initialize(\"\")";
_l1.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 256;BA.debugLine="l2.Initialize(\"\")";
_l2.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 257;BA.debugLine="l2.TextSize=18";
_l2.setTextSize((float) (18));
 //BA.debugLineNum = 258;BA.debugLine="l1.TextSize=20";
_l1.setTextSize((float) (20));
 //BA.debugLineNum = 259;BA.debugLine="l1.textcolor=mcl.md_black_1000";
_l1.setTextColor(mostCurrent._mcl.getmd_black_1000());
 //BA.debugLineNum = 260;BA.debugLine="l1.Text=\"Apps: \"&alist.ListKeys.Size&CRLF&\"Daten:";
_l1.setText(BA.ObjectToCharSequence("Apps: "+BA.NumberToString(mostCurrent._alist._listkeys().getSize())+anywheresoftware.b4a.keywords.Common.CRLF+"Daten: "+BA.ObjectToString(mostCurrent._kvsdata._get("cz"))+anywheresoftware.b4a.keywords.Common.CRLF+"RAM: "+_formatfilesize((float)(BA.ObjectToNumber(mostCurrent._dbase._get("ru"))))+" optimiert"));
 //BA.debugLineNum = 261;BA.debugLine="l2.textcolor=Colors.ARGB(255,0,0,0)";
_l2.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (0),(int) (0),(int) (0)));
 //BA.debugLineNum = 262;BA.debugLine="l1.Gravity=Gravity.TOP";
_l1.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.TOP);
 //BA.debugLineNum = 263;BA.debugLine="l1.Typeface=rfont";
_l1.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 264;BA.debugLine="l2.Typeface=rfont";
_l2.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 265;BA.debugLine="Builder.Initialize(\"resdia\")";
_builder.Initialize(mostCurrent.activityBA,"resdia");
 //BA.debugLineNum = 266;BA.debugLine="Builder.Title(\"Log:\").TitleColor(mcl.md_black_100";
_builder.Title(BA.ObjectToCharSequence("Log:")).TitleColor(mostCurrent._mcl.getmd_black_1000()).Icon((android.graphics.drawable.Drawable)(_inf.getObject())).LimitIconToDefaultSize().Theme(_builder.THEME_LIGHT).Content(BA.ObjectToCharSequence(_l1.getText())).ContentColor(mostCurrent._mcl.getmd_black_1000()).ContentLineSpacing((float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1)))).Cancelable(anywheresoftware.b4a.keywords.Common.False).PositiveText(BA.ObjectToCharSequence("Beenden")).PositiveColor(mostCurrent._mcl.getmd_light_blue_A700()).ContentGravity(_builder.GRAVITY_START);
 //BA.debugLineNum = 267;BA.debugLine="infodia=Builder.Show";
_infodia = _builder.Show();
 //BA.debugLineNum = 268;BA.debugLine="infodia.Show";
_infodia.Show();
 //BA.debugLineNum = 269;BA.debugLine="End Sub";
return "";
}
public static String  _t1_tick() throws Exception{
 //BA.debugLineNum = 474;BA.debugLine="Sub t1_Tick";
 //BA.debugLineNum = 475;BA.debugLine="counter=counter+1";
_counter = (int) (_counter+1);
 //BA.debugLineNum = 477;BA.debugLine="If counter > 1 Then";
if (_counter>1) { 
 //BA.debugLineNum = 478;BA.debugLine="CallSub(Starter,\"start_c\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(mostCurrent._starter.getObject()),"start_c");
 };
 //BA.debugLineNum = 481;BA.debugLine="If counter > 2 Then";
if (_counter>2) { 
 //BA.debugLineNum = 482;BA.debugLine="maintext.Text=\"Beende Hintergrund Tasks..\"";
mostCurrent._maintext.setText(BA.ObjectToCharSequence("Beende Hintergrund Tasks.."));
 //BA.debugLineNum = 483;BA.debugLine="stext.Text=\"Beende Hintergrund Tasks..\"";
mostCurrent._stext.setText(BA.ObjectToCharSequence("Beende Hintergrund Tasks.."));
 };
 //BA.debugLineNum = 486;BA.debugLine="If counter > 4 Then";
if (_counter>4) { 
 //BA.debugLineNum = 489;BA.debugLine="ani1.StopAnin(bm1)";
mostCurrent._ani1.StopAnin((android.view.View)(mostCurrent._bm1.getObject()));
 //BA.debugLineNum = 490;BA.debugLine="maintext.Text=\"Fertig..!\"";
mostCurrent._maintext.setText(BA.ObjectToCharSequence("Fertig..!"));
 //BA.debugLineNum = 491;BA.debugLine="task_close";
_task_close();
 };
 //BA.debugLineNum = 493;BA.debugLine="If counter = 6 Then";
if (_counter==6) { 
 //BA.debugLineNum = 494;BA.debugLine="t1.Enabled=False";
_t1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 495;BA.debugLine="counter=0";
_counter = (int) (0);
 //BA.debugLineNum = 496;BA.debugLine="finish_modul";
_finish_modul();
 };
 //BA.debugLineNum = 498;BA.debugLine="End Sub";
return "";
}
public static String  _task_close() throws Exception{
 //BA.debugLineNum = 426;BA.debugLine="Sub task_close";
 //BA.debugLineNum = 427;BA.debugLine="prog1.ReachedBarColor=Colors.ARGB(255,24,228,68)";
mostCurrent._prog1.setReachedBarColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (24),(int) (228),(int) (68)));
 //BA.debugLineNum = 428;BA.debugLine="bm1.Bitmap=LoadBitmap(File.DirAssets,\"success.png";
mostCurrent._bm1.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"success.png").getObject()));
 //BA.debugLineNum = 429;BA.debugLine="End Sub";
return "";
}
public static String  _task_service() throws Exception{
String _h = "";
 //BA.debugLineNum = 416;BA.debugLine="Sub task_service";
 //BA.debugLineNum = 417;BA.debugLine="alist.Remove(package)";
mostCurrent._alist._remove(_package);
 //BA.debugLineNum = 418;BA.debugLine="Log(package&\" removed\")";
anywheresoftware.b4a.keywords.Common.Log(_package+" removed");
 //BA.debugLineNum = 419;BA.debugLine="For Each h As String In alist.ListKeys";
{
final anywheresoftware.b4a.BA.IterableList group3 = mostCurrent._alist._listkeys();
final int groupLen3 = group3.getSize()
;int index3 = 0;
;
for (; index3 < groupLen3;index3++){
_h = BA.ObjectToString(group3.Get(index3));
 //BA.debugLineNum = 420;BA.debugLine="os.killBackgroundProcesses(h)";
_os.killBackgroundProcesses(_h);
 }
};
 //BA.debugLineNum = 422;BA.debugLine="stext.Text=\"optimiere \"&FormatFileSize(dbase.Get(";
mostCurrent._stext.setText(BA.ObjectToCharSequence("optimiere "+_formatfilesize((float)(BA.ObjectToNumber(mostCurrent._dbase._get("ru"))))+" RAM"));
 //BA.debugLineNum = 423;BA.debugLine="subtext.Text=\"bereinige Anwendungsdaten \"&CRLF&kv";
mostCurrent._subtext.setText(BA.ObjectToCharSequence("bereinige Anwendungsdaten "+anywheresoftware.b4a.keywords.Common.CRLF+BA.ObjectToString(mostCurrent._kvsdata._get("cz"))));
 //BA.debugLineNum = 424;BA.debugLine="End Sub";
return "";
}
public static String  _text_meth() throws Exception{
 //BA.debugLineNum = 520;BA.debugLine="Sub text_meth";
 //BA.debugLineNum = 521;BA.debugLine="subtext.Typeface=rfont";
mostCurrent._subtext.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 522;BA.debugLine="stext.Typeface=rfont";
mostCurrent._stext.setTypeface((android.graphics.Typeface)(_rfont.getObject()));
 //BA.debugLineNum = 523;BA.debugLine="stext.Gravity=Gravity.CENTER_HORIZONTAL";
mostCurrent._stext.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER_HORIZONTAL);
 //BA.debugLineNum = 524;BA.debugLine="subtext.TextSize=15";
mostCurrent._subtext.setTextSize((float) (15));
 //BA.debugLineNum = 525;BA.debugLine="subtext.Gravity=Gravity.CENTER_HORIZONTAL";
mostCurrent._subtext.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER_HORIZONTAL);
 //BA.debugLineNum = 526;BA.debugLine="stext.TextColor=Colors.ARGB(255,0,0,0)";
mostCurrent._stext.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (0),(int) (0),(int) (0)));
 //BA.debugLineNum = 527;BA.debugLine="subtext.TextColor=Colors.ARGB(255,0,0,0)";
mostCurrent._subtext.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (0),(int) (0),(int) (0)));
 //BA.debugLineNum = 528;BA.debugLine="End Sub";
return "";
}
public static String  _toolbar_menuitemclick(de.amberhome.objects.appcompat.ACMenuItemWrapper _item) throws Exception{
 //BA.debugLineNum = 211;BA.debugLine="Sub toolbar_MenuItemClick (Item As ACMenuItem)";
 //BA.debugLineNum = 212;BA.debugLine="Select Item.Id";
switch (BA.switchObjectToInt(_item.getId(),(int)(Double.parseDouble("0")),(int)(Double.parseDouble("1")),(int)(Double.parseDouble("2")))) {
case 0: {
 break; }
case 1: {
 break; }
case 2: {
 break; }
}
;
 //BA.debugLineNum = 220;BA.debugLine="End Sub";
return "";
}
public static String  _toolbar_navigationitemclick() throws Exception{
 //BA.debugLineNum = 207;BA.debugLine="Sub toolbar_NavigationItemClick";
 //BA.debugLineNum = 208;BA.debugLine="sm.ShowMenu";
mostCurrent._sm.ShowMenu();
 //BA.debugLineNum = 209;BA.debugLine="End Sub";
return "";
}
public static String  _update_modul() throws Exception{
String _dd = "";
int _c1 = 0;
int _c2 = 0;
int _sum = 0;
 //BA.debugLineNum = 439;BA.debugLine="Sub update_modul";
 //BA.debugLineNum = 440;BA.debugLine="Dim dd As String";
_dd = "";
 //BA.debugLineNum = 441;BA.debugLine="Dim c1,c2,sum As Int";
_c1 = 0;
_c2 = 0;
_sum = 0;
 //BA.debugLineNum = 442;BA.debugLine="c1=kvsdata.Get(\"c\")";
_c1 = (int)(BA.ObjectToNumber(mostCurrent._kvsdata._get("c")));
 //BA.debugLineNum = 443;BA.debugLine="c2=kvsdata.Get(\"to\")";
_c2 = (int)(BA.ObjectToNumber(mostCurrent._kvsdata._get("to")));
 //BA.debugLineNum = 444;BA.debugLine="dd=qbase.Get(c1)";
_dd = BA.ObjectToString(mostCurrent._qbase._get(BA.NumberToString(_c1)));
 //BA.debugLineNum = 445;BA.debugLine="stext.TextSize=18";
mostCurrent._stext.setTextSize((float) (18));
 //BA.debugLineNum = 446;BA.debugLine="subtext.TextSize=18";
mostCurrent._subtext.setTextSize((float) (18));
 //BA.debugLineNum = 447;BA.debugLine="sum=100/c2*c1";
_sum = (int) (100/(double)_c2*_c1);
 //BA.debugLineNum = 448;BA.debugLine="prog1.Progress=sum";
mostCurrent._prog1.setProgress(_sum);
 //BA.debugLineNum = 449;BA.debugLine="bm1.Gravity=Gravity.FILL";
mostCurrent._bm1.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.FILL);
 //BA.debugLineNum = 450;BA.debugLine="bm1.Bitmap=LoadBitmap(File.DirAssets,\"smartphone-";
mostCurrent._bm1.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"smartphone-11.png").getObject()));
 //BA.debugLineNum = 452;BA.debugLine="Try";
try { //BA.debugLineNum = 453;BA.debugLine="sico=pack.GetApplicationIcon(dd)";
mostCurrent._sico.setObject((android.graphics.drawable.BitmapDrawable)(mostCurrent._pack.GetApplicationIcon(_dd)));
 //BA.debugLineNum = 455;BA.debugLine="If c1<=c2 Then";
if (_c1<=_c2) { 
 //BA.debugLineNum = 456;BA.debugLine="bm1.Bitmap=sico.Bitmap";
mostCurrent._bm1.setBitmap(mostCurrent._sico.getBitmap());
 //BA.debugLineNum = 457;BA.debugLine="stext.Text=pack.GetApplicationLabel(dd)";
mostCurrent._stext.setText(BA.ObjectToCharSequence(mostCurrent._pack.GetApplicationLabel(_dd)));
 //BA.debugLineNum = 458;BA.debugLine="subtext.Text=dd";
mostCurrent._subtext.setText(BA.ObjectToCharSequence(_dd));
 };
 } 
       catch (Exception e20) {
			processBA.setLastException(e20); //BA.debugLineNum = 462;BA.debugLine="Log(LastException.Message)";
anywheresoftware.b4a.keywords.Common.Log(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage());
 };
 //BA.debugLineNum = 464;BA.debugLine="If c1=c2 Then";
if (_c1==_c2) { 
 //BA.debugLineNum = 465;BA.debugLine="prog1.ReachedBarColor=mcl.md_amber_A400'Colors.A";
mostCurrent._prog1.setReachedBarColor(mostCurrent._mcl.getmd_amber_A400());
 //BA.debugLineNum = 466;BA.debugLine="bm1.Bitmap=LoadBitmap(File.DirAssets,\"warning.pn";
mostCurrent._bm1.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"warning.png").getObject()));
 //BA.debugLineNum = 467;BA.debugLine="CallSub(Starter,\"start_c\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(mostCurrent._starter.getObject()),"start_c");
 };
 //BA.debugLineNum = 469;BA.debugLine="Return";
if (true) return "";
 //BA.debugLineNum = 470;BA.debugLine="End Sub";
return "";
}
}
