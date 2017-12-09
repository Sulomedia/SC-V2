package sclean2.com.designerscripts;
import anywheresoftware.b4a.objects.TextViewWrapper;
import anywheresoftware.b4a.objects.ImageViewWrapper;
import anywheresoftware.b4a.BA;


public class LS_1{

public static void LS_general(java.util.LinkedHashMap<String, anywheresoftware.b4a.keywords.LayoutBuilder.ViewWrapperAndAnchor> views, int width, int height, float scale) {
anywheresoftware.b4a.keywords.LayoutBuilder.setScaleRate(0.3);
//BA.debugLineNum = 2;BA.debugLine="AutoScaleAll"[1/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleAll(views);
//BA.debugLineNum = 3;BA.debugLine="toolbar.SetLeftAndRight(0,100%x)"[1/General script]
views.get("toolbar").vw.setLeft((int)(0d));
views.get("toolbar").vw.setWidth((int)((100d / 100 * width) - (0d)));
//BA.debugLineNum = 4;BA.debugLine="toolbar.SetTopAndBottom(0,10%y)"[1/General script]
views.get("toolbar").vw.setTop((int)(0d));
views.get("toolbar").vw.setHeight((int)((10d / 100 * height) - (0d)));
//BA.debugLineNum = 5;BA.debugLine="Panel1.SetLeftAndRight(20%x,80%x)"[1/General script]
views.get("panel1").vw.setLeft((int)((20d / 100 * width)));
views.get("panel1").vw.setWidth((int)((80d / 100 * width) - ((20d / 100 * width))));
//BA.debugLineNum = 6;BA.debugLine="Panel1.SetTopAndBottom(45%y,85%y)"[1/General script]
views.get("panel1").vw.setTop((int)((45d / 100 * height)));
views.get("panel1").vw.setHeight((int)((85d / 100 * height) - ((45d / 100 * height))));
//BA.debugLineNum = 7;BA.debugLine="ac1.SetLeftAndRight(10%x,50%x)"[1/General script]
views.get("ac1").vw.setLeft((int)((10d / 100 * width)));
views.get("ac1").vw.setWidth((int)((50d / 100 * width) - ((10d / 100 * width))));
//BA.debugLineNum = 8;BA.debugLine="ac1.SetTopAndBottom(8%y,35%y)"[1/General script]
views.get("ac1").vw.setTop((int)((8d / 100 * height)));
views.get("ac1").vw.setHeight((int)((35d / 100 * height) - ((8d / 100 * height))));

}
}