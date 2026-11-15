package io.memorydrive.pleos;

import android.animation.ValueAnimator;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.lang.reflect.*;
import java.util.*;

/** Native rendering additions; the original Compose controls retain all actions. */
public final class GloveboxMotion {
    static Context context;
    static Object engine, frameState;
    static Bitmap landscape, logo;
    static ValueAnimator clock;
    static boolean reduced, videoAuto=true;
    static float intensity=.72f, speed=.65f;
    static final Paint paint=new Paint(3);
    static final Map<String,Method> methods=new java.util.concurrent.ConcurrentHashMap<>();
    static long screenStart=SystemClock.uptimeMillis();
    static String memoryId="";
    static boolean musicEmpty=false;
    static final Map<Object,Object[]> modifierCache=Collections.synchronizedMap(new WeakHashMap<>());

    public static Object call(Object obj,String name,Object... args) {
        try {
            Class<?> type=obj instanceof Class?(Class<?>)obj:obj.getClass();
            String key=type.getName()+"#"+name+"/"+args.length;
            Method m=methods.get(key);
            if(m==null) {
                for(Method candidate:type.getMethods()) if(candidate.getName().equals(name)&&candidate.getParameterCount()==args.length) {
                    Class<?>[] types=candidate.getParameterTypes(); boolean match=true;
                    for(int i=0;i<args.length;i++) if(args[i]!=null&&!types[i].isPrimitive()&&!types[i].isInstance(args[i])) match=false;
                    if(match){m=candidate;break;}
                }
                if(m==null)throw new NoSuchMethodException(key);
                m.setAccessible(true);methods.put(key,m);
            }
            return m.invoke(obj instanceof Class?null:obj,args);
        }catch(Exception e){throw new IllegalStateException(name,e);}
    }
    static Class<?> cls(String n){try{return Class.forName(n);}catch(Exception e){throw new IllegalStateException(e);}}
    static Object unit(){try{return cls("kotlin.Unit").getField("INSTANCE").get(null);}catch(Exception e){throw new IllegalStateException(e);}}
    static Object function(String type,InvocationHandler handler){return Proxy.newProxyInstance(GloveboxMotion.class.getClassLoader(),new Class[]{cls(type)},handler);}
    public static void engine(Object value){engine=value;}
    public static void interact(){screenStart=SystemClock.uptimeMillis();musicEmpty=false;}
    public static void text(String value){if(value!=null&&value.equals("Bluetooth audio disconnected"))musicEmpty=true;}
    static boolean flag(String name){try{return Boolean.TRUE.equals(call(engine,name));}catch(Exception e){return false;}}

    public static void install(Activity activity){
        context=activity.getApplicationContext();
        GloveboxSamples.selectFirstVisit(context);
        GloveboxScreen.install(activity);
        SharedPreferences pref=context.getSharedPreferences("glovebox-appearance",0);
        reduced=pref.getBoolean("reduced",false);videoAuto=pref.getBoolean("video",true);
        intensity=pref.getFloat("intensity",.72f);speed=pref.getFloat("speed",.65f);
        try {
            BitmapFactory.Options options=new BitmapFactory.Options();options.inSampleSize=2;
            landscape=BitmapFactory.decodeStream(context.getAssets().open("glovebox/landscape.webp"),null,options);
            logo=BitmapFactory.decodeStream(context.getAssets().open("glovebox/logo.png"));
            frameState=call(cls("androidx.compose.runtime.SnapshotStateKt"),"mutableStateOf$default",0f,null,2,null);
            clock=ValueAnimator.ofFloat(0,1);clock.setDuration(18000);clock.setRepeatCount(-1);
            clock.setInterpolator(new android.view.animation.LinearInterpolator());
            final long[] last={0};
            clock.addUpdateListener(a->{long now=SystemClock.uptimeMillis();if(now-last[0]>=32&&!flag("getConfirmedNonParked")){last[0]=now;call(frameState,"setValue",a.getAnimatedFraction());}});
            if(!reduced&&ValueAnimator.areAnimatorsEnabled())clock.start();
            activity.getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks(){
                public void onActivityResumed(Activity a){if(a==activity&&clock!=null&&!reduced&&!GloveboxScreen.visible){if(clock.isPaused())clock.resume();else if(!clock.isStarted())clock.start();}}
                public void onActivityPaused(Activity a){if(a==activity&&clock!=null)clock.pause();}
                public void onActivityDestroyed(Activity a){if(a==activity){if(clock!=null)clock.cancel();activity.getApplication().unregisterActivityLifecycleCallbacks(this);}}
                public void onActivityCreated(Activity a,Bundle b){} public void onActivityStarted(Activity a){} public void onActivityStopped(Activity a){} public void onActivitySaveInstanceState(Activity a,Bundle b){}
            });
            activity.getWindow().getDecorView().post(()->{
                FrameLayout layer=new FrameLayout(activity);layer.setClipChildren(false);
                TextView settings=new TextView(activity);settings.setText("화면");settings.setTextColor(0xffedcda5);settings.setTextSize(16);settings.setGravity(Gravity.CENTER);settings.setContentDescription("화면과 모션 설정");
                settings.setBackground(panel(0x802f2922,18));
                FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(dp(64),dp(48),Gravity.TOP|Gravity.RIGHT);lp.topMargin=dp(24);lp.rightMargin=dp(108);
                layer.addView(settings,lp);settings.setOnClickListener(v->{if(!flag("getConfirmedNonParked"))settings(activity);});
                activity.addContentView(layer,new ViewGroup.LayoutParams(-1,-1));
            });
        }catch(Exception e){android.util.Log.e("GloveboxDesign","install",e);}
    }
    static int dp(float f){return Math.round(f*(context==null?1:context.getResources().getDisplayMetrics().density));}
    public static float brandWidth(){return centeredBrand()?16:190;}
    public static float brandAlpha(){return centeredBrand()?0:1;}
    static boolean centeredBrand(){return context!=null&&context.getResources().getDisplayMetrics().widthPixels>=dp(1200);}
    static GradientDrawable panel(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));d.setStroke(dp(1),0x50f2d2a6);return d;}
    static void persist(){context.getSharedPreferences("glovebox-appearance",0).edit().putBoolean("reduced",reduced).putBoolean("video",videoAuto).putFloat("intensity",intensity).putFloat("speed",speed).apply();}
    static void settings(Activity a){
        Dialog dialog=new Dialog(a);LinearLayout body=new LinearLayout(a);body.setOrientation(1);body.setPadding(dp(36),dp(28),dp(36),dp(28));body.setBackground(panel(0xe823211e,30));
        TextView title=new TextView(a);title.setText("나에게 맞는 화면");title.setTextColor(0xfffaf7f0);title.setTextSize(28);body.addView(title);
        TextView sub=new TextView(a);sub.setText("빛의 농도와 움직임을 편안하게 조절하세요.");sub.setTextSize(16);sub.setTextColor(0xffc8bba9);sub.setPadding(0,dp(10),0,dp(20));body.addView(sub);
        addSlider(a,body,"유리 표면의 빛",Math.round(intensity*100),v->{intensity=v/100f;persist();});
        addSlider(a,body,"모션 속도 · 천천히",Math.round(speed*100),v->{speed=Math.max(.15f,v/100f);persist();});
        Switch motion=new Switch(a);motion.setText("움직임 줄이기");motion.setTextColor(0xfffaf7f0);motion.setTextSize(20);motion.setPadding(0,dp(20),0,dp(20));motion.setChecked(reduced);body.addView(motion);
        tint(motion);
        motion.setOnCheckedChangeListener((b,on)->{reduced=on;if(on)clock.pause();else if(clock.isStarted())clock.resume();else clock.start();GloveboxVideo.preferencesChanged();persist();});
        Switch video=new Switch(a);video.setText("풍경 영상 자동 재생");video.setTextColor(0xfffaf7f0);video.setTextSize(20);video.setPadding(0,dp(20),0,dp(20));video.setChecked(videoAuto);body.addView(video);video.setOnCheckedChangeListener((b,on)->{videoAuto=on;GloveboxVideo.preferencesChanged();persist();});
        tint(video);
        TextView info=new TextView(a);info.setText("주행 상태에서는 기존 앱의 사진·영상 제한이 우선 적용됩니다.");info.setTextColor(0xffbcaf9d);info.setTextSize(15);body.addView(info);
        Button close=new Button(a);close.setText("이대로 보기");close.setTextSize(17);close.setTextColor(0xff292117);close.setBackground(panel(0xffdfbc85,18));LinearLayout.LayoutParams closeParams=new LinearLayout.LayoutParams(-1,dp(52));closeParams.topMargin=dp(20);body.addView(close,closeParams);close.setOnClickListener(v->dialog.dismiss());
        dialog.setContentView(body);Window w=dialog.getWindow();if(w!=null){w.setBackgroundDrawableResource(android.R.color.transparent);w.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);w.setDimAmount(.35f);if(Build.VERSION.SDK_INT>=31)w.setBackgroundBlurRadius(32);}
        dialog.show();if(w!=null)w.setLayout(dp(600),-2);
    }
    interface Changed{void changed(int value);}
    static void tint(Switch s){s.setThumbTintList(new android.content.res.ColorStateList(new int[][]{new int[]{android.R.attr.state_checked},new int[]{}},new int[]{0xffedc991,0xffa39b90}));s.setTrackTintList(new android.content.res.ColorStateList(new int[][]{new int[]{android.R.attr.state_checked},new int[]{}},new int[]{0x907d6342,0x7045413b}));}
    static void addSlider(Activity a,LinearLayout body,String label,int progress,Changed action){TextView text=new TextView(a);text.setText(label);text.setTextColor(0xffead2af);text.setTextSize(18);body.addView(text);SeekBar bar=new SeekBar(a);bar.setMax(100);bar.setProgress(progress);bar.setProgressTintList(android.content.res.ColorStateList.valueOf(0xffd9b37c));bar.setThumbTintList(android.content.res.ColorStateList.valueOf(0xffefd1a3));body.addView(bar);bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar s,int p,boolean user){if(user)action.changed(p);}public void onStartTrackingTouch(SeekBar s){}public void onStopTrackingTouch(SeekBar s){}});}

    public static Object modifier(Object base,int mode){
        try {
            if(base==null)base=cls("androidx.compose.ui.Modifier").getField("Companion").get(null);
            Object[] cached=modifierCache.get(base);if(cached!=null&&cached[mode]!=null){Object live=((java.lang.ref.WeakReference<?>)cached[mode]).get();if(live!=null)return live;}
            Draw draw=new Draw(mode);
            Object element=Proxy.newProxyInstance(GloveboxMotion.class.getClassLoader(),new Class[]{cls("androidx.compose.ui.draw.DrawModifier")},draw);
            Object result=call(base,"then",element);
            if(cached==null){cached=new Object[7];modifierCache.put(base,cached);}cached[mode]=new java.lang.ref.WeakReference<>(result);
            return result;
        }catch(Exception e){android.util.Log.e("GloveboxDesign","modifier",e);return base;}
    }
    static final class Draw implements InvocationHandler {
        final int mode; final long created=SystemClock.uptimeMillis(); final Paint p=new Paint(3); final Matrix rotate=new Matrix();
        final RectF rect=new RectF(); final Path clip=new Path();
        Object frost;
        Draw(int mode){this.mode=mode;}
        public Object invoke(Object proxy,Method method,Object[] args)throws Throwable{
            String n=method.getName();
            if(n.equals("hashCode"))return System.identityHashCode(proxy);
            if(n.equals("equals"))return proxy==args[0];
            if(n.equals("toString"))return "GloveboxGlass("+mode+")";
            if(n.equals("foldIn"))return call(args[1],"invoke",args[0],proxy);
            if(n.equals("foldOut"))return call(args[1],"invoke",proxy,args[0]);
            if(n.equals("all")||n.equals("any"))return call(args[0],"invoke",proxy);
            if(n.equals("then"))return cls("androidx.compose.ui.CombinedModifier").getConstructor(cls("androidx.compose.ui.Modifier"),cls("androidx.compose.ui.Modifier")).newInstance(proxy,args[0]);
            if(n.equals("draw")){draw(args[0]);return unit();}
            return null;
        }
        void draw(Object scope){
            boolean content=false;
            try {
                float phase=frameState==null?0:((Number)call(frameState,"getValue")).floatValue();
                long packed=((Number)call(scope,"getSize-NH-jbRc")).longValue();float w=Float.intBitsToFloat((int)(packed>>32)),h=Float.intBitsToFloat((int)packed);
                if(w<=0||h<=0){call(scope,"drawContent");return;}
                Object dc=call(scope,"getDrawContext"),canvas=call(dc,"getCanvas");Canvas c=(Canvas)call(cls("androidx.compose.ui.graphics.AndroidCanvas_androidKt"),"getNativeCanvas",canvas);
                boolean photosAllowed=flag("getPhotoAllowed")&&!flag("getConfirmedNonParked");
                float t=flag("getConfirmedNonParked")?0:phase*18f*speed;
                if(mode>=3){
                    float rad=dp(mode==3?32:20);rect.set(1,1,w-1,h-1);
                    p.setShader(new LinearGradient(0,0,w,h,mode==3?new int[]{0xd0342b24,0xe3191919}:new int[]{0x123b3024,0x08282523},null,Shader.TileMode.CLAMP));
                    c.drawRoundRect(rect,rad,rad,p);p.setShader(null);
                    // Only page entrances animate. Text inputs and dialogs never move under a finger.
                    call(scope,"drawContent");content=true;
                    if(mode==5)return;
                    p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(dp(1));
                    p.setShader(new LinearGradient(0,0,w,h,new int[]{0x70efd6b2,0x18efd6b2,0x38bb8c56},new float[]{0,.55f,1},Shader.TileMode.CLAMP));
                    c.drawRoundRect(rect,rad,rad,p);p.setShader(null);p.setStyle(Paint.Style.FILL);
                    if(mode==3){p.setColor(0x50e6ba7e);c.drawRoundRect(dp(30),dp(2),Math.min(w-dp(30),dp(160)),dp(3),1,1,p);}
                    return;
                }
                if(mode==1){
                    rect.set(0,0,w,h);p.setShader(null);p.setColor(0xff161716);c.drawRect(rect,p);
                    if(landscape!=null&&photosAllowed){float scale=Math.max(w/landscape.getWidth(),h/landscape.getHeight());float iw=landscape.getWidth()*scale,ih=landscape.getHeight()*scale;p.setAlpha(150);c.drawBitmap(landscape,null,new RectF((w-iw)/2,(h-ih)/2,(w+iw)/2,(h+ih)/2),p);p.setAlpha(255);}
                    p.setShader(new LinearGradient(0,0,w,h,new int[]{0xb52c231b,0xa315191a,0xe10e1113},null,Shader.TileMode.CLAMP));c.drawRect(rect,p);p.setShader(null);
                }
                if(photosAllowed&&(mode==0||mode==2)&&w>dp(300)&&h>dp(100)&&Build.VERSION.SDK_INT>=31&&c.isHardwareAccelerated())frost=Frost.draw(c,w,h,landscape,frost);
                float entrance=reduced?1:Math.min(1,(SystemClock.uptimeMillis()-(mode==1?screenStart:created))/360f);
                int alpha=Math.round(255*((mode==1?.94f:.86f)+(mode==1?.06f:.14f)*entrance));
                int save=alpha<255?c.saveLayerAlpha(0,0,w,h,alpha):c.save();
                call(scope,"drawContent");content=true;c.restoreToCount(save);
                if(mode==1&&centeredBrand()&&logo!=null){p.setShader(null);p.setAlpha(255);float lw=dp(190),lh=dp(52);c.drawBitmap(logo,null,new RectF((w-lw)/2,dp(24),(w+lw)/2,dp(24)+lh),p);}
                if(mode==0||mode==2){
                    float radius=Math.min(dp(25),h*.48f);rect.set(1,1,w-1,h-1);clip.reset();clip.addRoundRect(rect,radius,radius,Path.Direction.CW);int count=c.save();c.clipPath(clip);
                    // Broad specular band, restrained edge light, and a moving bezel highlight.
                    p.setShader(new LinearGradient(0,0,w*.45f,h,new int[]{(Math.round(26*intensity)<<24)|0xffe7c3,0x00ffefda,0x09000000},null,Shader.TileMode.CLAMP));p.setStyle(Paint.Style.FILL);c.drawRoundRect(rect,radius,radius,p);
                    p.setShader(null);p.setStrokeWidth(dp(1));p.setStyle(Paint.Style.STROKE);p.setColor(0x45e7c79d);c.drawRoundRect(rect,radius,radius,p);
                    float x=(float)((Math.sin(t*.22)+1)*.5*w);
                    p.setShader(new LinearGradient(x-w*.24f,0,x+w*.24f,0,new int[]{0x00fff8e7,0xa0fff0cd,0x00fff8e7},null,Shader.TileMode.CLAMP));c.drawRoundRect(rect,radius,radius,p);p.setShader(null);p.setStyle(Paint.Style.FILL);c.restoreToCount(count);
                }
                if(mode==2&&w>900&&h>110){
                    float cx=w*.65f,cy=h*.42f,r=Math.min(h*.27f,dp(64));
                    p.setColor(0x604a3c30);c.drawCircle(cx,cy,r,p);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(dp(1.3f));p.setColor(0x60e5c393);c.drawCircle(cx,cy,r,p);
                    Shader sweep=new SweepGradient(cx,cy,new int[]{0x00f0d29d,0x00f0d29d,0xc0ffe3ad,0x00f0d29d},new float[]{0,.72f,.91f,1});rotate.setRotate(t*18,cx,cy);sweep.setLocalMatrix(rotate);p.setShader(sweep);p.setStrokeWidth(dp(3));c.drawCircle(cx,cy,r-dp(4),p);p.setShader(null);p.setStyle(Paint.Style.FILL);p.setTextSize(dp(12));p.setTextAlign(Paint.Align.CENTER);p.setColor(0xffd9c8ae);c.drawText("MEMORY",cx,cy-2,p);c.drawText("SOUND",cx,cy+dp(15),p);
                    boolean playing=flag("isSpeaking");if(context!=null)playing|=((AudioManager)context.getSystemService(Context.AUDIO_SERVICE)).isMusicActive();
                    float start=w*.73f,end=w*.87f,space=(end-start)/32f;
                    for(int i=0;i<32;i++){float amp=playing?(float)(.3+.5*Math.abs(Math.sin(t*2.2+i*.7)*Math.cos(t*.9-i*.2))):(float)(.08+.025*Math.sin(t*.6+i*.16));float bh=h*.45f*amp;p.setColor(0xcce4bd86);c.drawRoundRect(start+i*space,cy-bh/2,start+i*space+space*.36f,cy+bh/2,2,2,p);}
                }
                if(mode==1&&musicEmpty&&w>dp(1500)){
                    float cx=w*.79f,cy=h*.42f,r=dp(105);
                    p.setShader(null);p.setStyle(Paint.Style.FILL);p.setColor(0x68352e25);c.drawCircle(cx,cy,r,p);
                    p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(dp(1));p.setColor(0x70ddc29c);c.drawCircle(cx,cy,r,p);c.drawCircle(cx,cy,r-dp(11),p);
                    Shader sweep=new SweepGradient(cx,cy,new int[]{0x00ffedcb,0x00ffedcb,0xd0ffe9b9,0x00ffedcb},new float[]{0,.72f,.93f,1});rotate.setRotate(t*18,cx,cy);sweep.setLocalMatrix(rotate);p.setShader(sweep);p.setStrokeWidth(dp(3));c.drawCircle(cx,cy,r-dp(5),p);p.setShader(null);p.setStyle(Paint.Style.FILL);p.setTextAlign(Paint.Align.CENTER);p.setTextSize(dp(14));p.setColor(0xffdcc8a9);c.drawText("MEMORY SOUND",cx,cy,p);p.setTextSize(dp(12));p.setColor(0xffb6aa96);c.drawText("연결을 기다리는 중",cx,cy+dp(24),p);
                    for(int i=0;i<40;i++){float bh=dp(8)+(float)(dp(9)*(1+Math.sin(t*.7+i*.19)));float x=cx-dp(130)+i*dp(6.5f);p.setColor(0x8ae8c38d);c.drawRoundRect(x,cy+r+dp(35)-bh/2,x+dp(2),cy+r+dp(35)+bh/2,2,2,p);}
                }
            }catch(Throwable e){android.util.Log.e("GloveboxDesign","draw",e);if(!content)call(scope,"drawContent");}
        }
    }
    /** Kept in an API-gated class so the original Android 28 floor remains supported. */
    static final class Frost {
        static Object draw(Canvas c,float w,float h,Bitmap image,Object cached){
            if(image==null)return cached;
            android.graphics.RenderNode node=(android.graphics.RenderNode)cached;
            if(node==null||node.getWidth()!=(int)w||node.getHeight()!=(int)h){
                node=new android.graphics.RenderNode("Glovebox frosted landscape");node.setPosition(0,0,(int)w,(int)h);
                RecordingCanvas recording=node.beginRecording((int)w,(int)h);Paint p=new Paint(3);p.setAlpha(90);recording.drawBitmap(image,null,new RectF(0,0,w,h),p);node.endRecording();
                node.setRenderEffect(RenderEffect.createBlurEffect(dp(28),dp(28),Shader.TileMode.CLAMP));
            }
            int save=c.save();Path boundary=new Path();boundary.addRoundRect(new RectF(0,0,w,h),dp(24),dp(24),Path.Direction.CW);c.clipPath(boundary);c.drawRenderNode(node);c.restoreToCount(save);return node;
        }
    }
}
