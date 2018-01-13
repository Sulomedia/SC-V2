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
//BA.debugLineNum = 5;BA.debugLine="ac1.SetTopAndBottom(90%y,100%y)"[1/General script]
views.get("ac1").vw.setTop((int)((90d / 100 * height)));
views.get("ac1").vw.setHeight((int)((100d / 100 * height) - ((90d / 100 * height))));
//BA.debugLineNum = 6;BA.debugLine="ac1.SetLeftAndRight(0%x,100%x)"[1/General script]
views.get("ac1").vw.setLeft((int)((0d / 100 * width)));
views.get("ac1").vw.setWidth((int)((100d / 100 * width) - ((0d / 100 * width))));
//BA.debugLineNum = 7;BA.debugLine="lv2.SetLeftAndRight(1%x,99%x)"[1/General script]
views.get("lv2").vw.setLeft((int)((1d / 100 * width)));
views.get("lv2").vw.setWidth((int)((99d / 100 * width) - ((1d / 100 * width))));
//BA.debugLineNum = 8;BA.debugLine="lv2.SetTopAndBottom(80%y,90%y)"[1/General script]
views.get("lv2").vw.setTop((int)((80d / 100 * height)));
views.get("lv2").vw.setHeight((int)((90d / 100 * height) - ((80d / 100 * height))));
//BA.debugLineNum = 9;BA.debugLine="Panel1.SetLeftAndRight(2%x,98%x)"[1/General script]
views.get("panel1").vw.setLeft((int)((2d / 100 * width)));
views.get("panel1").vw.setWidth((int)((98d / 100 * width) - ((2d / 100 * width))));
//BA.debugLineNum = 10;BA.debugLine="Panel1.SetTopAndBottom(5%y,75%y)"[1/General script]
views.get("panel1").vw.setTop((int)((5d / 100 * height)));
views.get("panel1").vw.setHeight((int)((75d / 100 * height) - ((5d / 100 * height))));

}
}