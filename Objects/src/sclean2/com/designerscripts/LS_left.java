package sclean2.com.designerscripts;
import anywheresoftware.b4a.objects.TextViewWrapper;
import anywheresoftware.b4a.objects.ImageViewWrapper;
import anywheresoftware.b4a.BA;


public class LS_left{

public static void LS_general(java.util.LinkedHashMap<String, anywheresoftware.b4a.keywords.LayoutBuilder.ViewWrapperAndAnchor> views, int width, int height, float scale) {
anywheresoftware.b4a.keywords.LayoutBuilder.setScaleRate(0.3);
anywheresoftware.b4a.keywords.LayoutBuilder.scaleAll(views);
views.get("panel2").vw.setLeft((int)((0d / 100 * width)));
views.get("panel2").vw.setWidth((int)((85d / 100 * width) - ((0d / 100 * width))));
views.get("panel2").vw.setTop((int)((0d / 100 * height)));
views.get("panel2").vw.setHeight((int)((100d / 100 * height) - ((0d / 100 * height))));
views.get("leftlist").vw.setLeft((int)((0d / 100 * width)));
views.get("leftlist").vw.setWidth((int)((85d / 100 * width) - ((0d / 100 * width))));
views.get("leftlist").vw.setTop((int)((10d / 100 * height)));
views.get("leftlist").vw.setHeight((int)((90d / 100 * height) - ((10d / 100 * height))));
views.get("ltext").vw.setTop((int)((1d / 100 * height)));
views.get("ltext").vw.setHeight((int)((10d / 100 * height) - ((1d / 100 * height))));
views.get("ltext").vw.setLeft((int)((0d / 100 * width)));
views.get("ltext").vw.setWidth((int)((85d / 100 * width) - ((0d / 100 * width))));
views.get("lstext").vw.setLeft((int)((0d / 100 * width)));
views.get("lstext").vw.setWidth((int)((85d / 100 * width) - ((0d / 100 * width))));
//BA.debugLineNum = 10;BA.debugLine="lstext.SetTopAndBottom(90%y,100%y)"[left/General script]
views.get("lstext").vw.setTop((int)((90d / 100 * height)));
views.get("lstext").vw.setHeight((int)((100d / 100 * height) - ((90d / 100 * height))));

}
}