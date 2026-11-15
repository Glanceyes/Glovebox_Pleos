package io.memorydrive.pleos;

import java.lang.reflect.*;
import java.util.*;

/** Presentation adapters around the shipped Compose controls. No replacement stores or services. */
public final class GloveboxFeatures {
    private static Object navigation;
    private static boolean journalMode;
    private static Object journalState;
    private static void setJournal(boolean value){journalMode=value;if(journalState!=null)call(journalState,"setValue",value);}
    public static boolean journal(Object engine,Object onOpen,Object scope){
        try{
            if(journalState==null)journalState=call(cls("androidx.compose.runtime.SnapshotStateKt"),"mutableStateOf$default",journalMode,null,2,null);
            if(!Boolean.TRUE.equals(call(journalState,"getValue")))return false;
            Object open=GloveboxMotion.function("kotlin.jvm.functions.Function1",(proxy,method,args)->{
                if(method.getName().equals("invoke")){setJournal(false);return call(onOpen,"invoke",args[0]);}
                if(method.getName().equals("hashCode"))return System.identityHashCode(proxy);
                if(method.getName().equals("equals"))return proxy==args[0];
                return "GloveboxOriginalJournal";
            });
            Constructor<?> constructor=cls("io.memorydrive.pleos.VehicleLibraryHomeKt$VehicleLibraryHome$15$1$13").getDeclaredConstructors()[0];
            constructor.setAccessible(true);
            Object original=constructor.newInstance(engine,open);
            Object content=call(cls("androidx.compose.runtime.internal.ComposableLambdaKt"),"composableLambdaInstance",-1696214406,true,original);
            call(cls("androidx.compose.foundation.lazy.LazyListScope"),"item$default",scope,null,null,content,3,null);
            return true;
        }catch(Exception e){throw new IllegalStateException("Original journal",e);}
    }
    private static final Set<String> logged=new HashSet<>();
    private static final Set<String> pages=new HashSet<>(Arrays.asList(
        "platform-library","music-library","youtube-account-library","course-library",
        "community-explore","course-composer","listening-history-connection"));
    static Object call(Object o,String n,Object... a){return GloveboxMotion.call(o,n,a);}
    static Class<?> cls(String n){return GloveboxMotion.cls(n);}
    static Object base(Object m){try{return m==null?cls("androidx.compose.ui.Modifier").getField("Companion").get(null):m;}catch(Exception e){throw new IllegalStateException(e);}}
    public static Object shape(){return call(cls("androidx.compose.foundation.shape.RoundedCornerShapeKt"),"RoundedCornerShape-0680j_4",20f);}
    public static Object control(Object m,int kind){
        m=base(m);
        // Draw modifiers keep the native hit targets, semantics, IME and all callbacks intact.
        return GloveboxMotion.modifier(m,kind);
    }
    public static Object tag(Object modifier,String tag){
        Object m=base(modifier);
        if(pages.contains(tag)){
            if(logged.add(tag))android.util.Log.i("GloveboxFeatures","screen="+tag);
            m=call(cls("androidx.compose.foundation.layout.PaddingKt"),"padding-3ABfNKs",m,18f);
            m=control(m,3);
            m=call(cls("androidx.compose.foundation.layout.PaddingKt"),"padding-3ABfNKs",m,30f);
        }
        return call(cls("androidx.compose.ui.platform.TestTagKt"),"testTag",m,tag);
    }
    public static void capture(Object root){navigation=root;}
    public static Object tab(String label,Object original){
        if(!label.equals("음악")&&!label.equals("내 기억")&&!label.equals("이어 보기")&&!label.equals("함께 나눈 길"))return original;
        if(Proxy.isProxyClass(original.getClass())&&"GloveboxNavigation".equals(original.toString()))return original;
        return GloveboxMotion.function("kotlin.jvm.functions.Function0",(proxy,method,args)->{
            if(method.getName().equals("invoke")){
                setJournal(false);
                Object result=call(original,"invoke");
                GloveboxScreen.openDesign(label.equals("음악")?"music-shelf":label.equals("내 기억")?"archive":label.equals("함께 나눈 길")?"routes":"detail");return result;
            }
            if(method.getName().equals("hashCode"))return System.identityHashCode(proxy);
            if(method.getName().equals("equals"))return proxy==args[0];
            return "GloveboxNavigation";
        });
    }
    private static Object field(String name)throws Exception{
        Field f=navigation.getClass().getDeclaredField("$"+name);f.setAccessible(true);return f.get(navigation);
    }
    private static void state(String name,Object value)throws Exception{call(field(name+"$delegate"),"setValue",value);}
    public static boolean navigate(String route){
        if(navigation==null)return false;
        try{
            if(!Arrays.asList("account","courses","music","usb","music-connect","menu","library","detail","journal").contains(route))return false;
            setJournal(route.equals("journal"));
            if(route.equals("usb")&&(GloveboxMotion.flag("getDemoMode")||call(GloveboxMotion.engine,"getActiveCourse")!=null)){
                android.widget.Toast.makeText(GloveboxMotion.context,"사진 연결은 체험 또는 코스 종료 후 내 기록에서 사용할 수 있어요.",android.widget.Toast.LENGTH_LONG).show();return false;
            }
            for(String n:new String[]{"connection","courses","musicLibrary","menu","musicHelp","nativeMusicDialog"})state(n,false);
            state("recapDraftIDs",null);
            if(route.equals("account"))state("connection",true);
            if(route.equals("courses"))state("courses",true);
            if(route.equals("music"))state("musicLibrary",true);
            if(route.equals("music-connect"))state("musicHelp",true);
            if(route.equals("library")||route.equals("journal"))state("library",true);
            if(route.equals("detail"))state("library",false);
            if(route.equals("menu"))state("menu",true);
            if(route.equals("usb"))call(field("usbPhotoAction"),"invoke");
            GloveboxMotion.interact();
            return true;
        }catch(Exception e){android.util.Log.e("GloveboxFeatures","navigation "+route,e);return false;}
    }
}
