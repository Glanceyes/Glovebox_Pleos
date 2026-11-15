package io.memorydrive.pleos;
import android.content.*;
import android.graphics.*;
import android.media.MediaPlayer;
import android.view.*;
import android.widget.*;
import android.content.res.AssetFileDescriptor;
import java.lang.reflect.*;

public final class GloveboxVideo {
    static Object factory,update;
    static final java.util.WeakHashMap<Scene,Boolean> scenes=new java.util.WeakHashMap<>();
    public static void screenChanged(){for(Scene s:new java.util.ArrayList<>(scenes.keySet()))s.sync();}
    public static void preferencesChanged(){for(Scene s:new java.util.ArrayList<>(scenes.keySet())){if(GloveboxMotion.reduced||!GloveboxMotion.videoAuto)s.paused=true;s.sync();}}
    public static boolean render(Object memory,boolean allowed,Object modifier,Object composer){
        boolean eligible=allowed&&GloveboxMotion.flag("getDemoMode")&&"coast-2022".equals(GloveboxMotion.call(memory,"getId"));
        GloveboxMotion.call(composer,"startReplaceGroup",0x44aab921);
        try{
            if(eligible){
                if(factory==null)factory=GloveboxMotion.function("kotlin.jvm.functions.Function1",(o,m,a)->m.getName().equals("invoke")?new Scene((Context)a[0]):objectMethod(o,m,a));
                if(update==null)update=GloveboxMotion.function("kotlin.jvm.functions.Function1",(o,m,a)->m.getName().equals("invoke")?GloveboxMotion.unit():objectMethod(o,m,a));
                GloveboxMotion.call(GloveboxMotion.cls("androidx.compose.ui.viewinterop.AndroidView_androidKt"),"AndroidView",factory,modifier,update,composer,0,0);
            }
        }finally{GloveboxMotion.call(composer,"endReplaceGroup");}
        return eligible;
    }
    static Object objectMethod(Object o,Method m,Object[] a){if(m.getName().equals("hashCode"))return System.identityHashCode(o);if(m.getName().equals("equals"))return o==a[0];if(m.getName().equals("toString"))return "GloveboxVideoFactory";return null;}
    static final class Scene extends FrameLayout implements TextureView.SurfaceTextureListener {
        TextureView texture; ImageView photo; TextView play;MediaPlayer player;Surface surface;
        boolean paused=false,video=true,prepared=false; int photoIndex=0;
        final int[] photos={17,4,10};
        Scene(Context c){super(c);scenes.put(this,true);setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);setMinimumHeight(GloveboxMotion.dp(240));setBackground(GloveboxMotion.panel(0xff17191a,24));setClipToOutline(true);
            texture=new TextureView(c);texture.setSurfaceTextureListener(this);addView(texture,new LayoutParams(-1,-1));
            photo=new ImageView(c);photo.setScaleType(ImageView.ScaleType.CENTER_CROP);photo.setScaleX(1.08f);photo.setScaleY(1.08f);photo.setVisibility(GONE);addView(photo,new LayoutParams(-1,-1));
            LinearLayout controls=new LinearLayout(c);controls.setGravity(Gravity.CENTER_VERTICAL);controls.setPadding(GloveboxMotion.dp(14),GloveboxMotion.dp(6),GloveboxMotion.dp(14),GloveboxMotion.dp(6));controls.setBackground(GloveboxMotion.panel(0xb5222423,22));
            TextView label=text("주문진 · 새벽바다");controls.addView(label);play=text("일시정지");controls.addView(play);play.setOnClickListener(v->{if(!video){video=true;photo.setVisibility(GONE);texture.setVisibility(VISIBLE);paused=false;start();}else{paused=!paused;sync();}});
            TextView photosButton=text("사진 보기");controls.addView(photosButton);photosButton.setOnClickListener(v->{video=false;sync();texture.setVisibility(INVISIBLE);photo.setVisibility(VISIBLE);try{photo.setImageBitmap(BitmapFactory.decodeStream(c.getAssets().open(String.format(java.util.Locale.ROOT,"glovebox/photo-%02d.webp",photos[photoIndex++%photos.length]))));}catch(Exception e){android.util.Log.e("GloveboxDesign","photo",e);}play.setText("영상 보기");photosButton.setText("다음 사진");});
            LayoutParams lp=new LayoutParams(-2,GloveboxMotion.dp(48),Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);lp.bottomMargin=GloveboxMotion.dp(16);addView(controls,lp);
        }
        TextView text(String s){TextView v=new TextView(getContext());v.setText(s);v.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);v.setFocusable(true);v.setTextColor(0xfff9edd8);v.setTextSize(15);v.setPadding(GloveboxMotion.dp(12),0,GloveboxMotion.dp(12),0);v.setGravity(Gravity.CENTER);v.setMinHeight(GloveboxMotion.dp(44));return v;}
        void start(){if(player!=null){sync();return;}if(!texture.isAvailable())return;
            try{surface=new Surface(texture.getSurfaceTexture());player=new MediaPlayer();player.setSurface(surface);AssetFileDescriptor fd=getContext().getAssets().openFd("glovebox/jumunjin-dawn.mp4");player.setDataSource(fd.getFileDescriptor(),fd.getStartOffset(),fd.getLength());fd.close();player.setLooping(true);player.setVolume(0,0);player.setOnPreparedListener(p->{prepared=true;resize();paused=GloveboxMotion.reduced||!GloveboxMotion.videoAuto;sync();});player.setOnErrorListener((p,w,e)->{play.setText("영상 다시 열기");release();return true;});player.prepareAsync();}catch(Exception e){android.util.Log.e("GloveboxDesign","video",e);play.setText("영상 다시 열기");release();}}
        void sync(){if(player==null||!prepared)return;if(video&&!paused&&!GloveboxScreen.visible&&getWindowVisibility()==VISIBLE&&!GloveboxMotion.flag("getConfirmedNonParked")){player.start();play.setText("일시정지");}else{player.pause();if(video)play.setText("영상 재생");}}
        void resize(){if(player==null||!prepared||texture.getWidth()==0)return;float vw=player.getVideoWidth(),vh=player.getVideoHeight(),w=texture.getWidth(),h=texture.getHeight();if(vw<=0||vh<=0)return;float scale=Math.max(w/vw,h/vh);Matrix matrix=new Matrix();matrix.setScale(vw*scale/w,vh*scale/h,w/2,h/2);texture.setTransform(matrix);}
        void release(){prepared=false;if(player!=null){player.release();player=null;}if(surface!=null){surface.release();surface=null;}}
        public void onSurfaceTextureAvailable(SurfaceTexture s,int w,int h){start();}
        public void onSurfaceTextureSizeChanged(SurfaceTexture s,int w,int h){resize();}
        public boolean onSurfaceTextureDestroyed(SurfaceTexture s){release();return true;}
        public void onSurfaceTextureUpdated(SurfaceTexture s){}
        protected void onDetachedFromWindow(){release();scenes.remove(this);super.onDetachedFromWindow();}
        protected void onWindowVisibilityChanged(int visibility){super.onWindowVisibilityChanged(visibility);sync();}
    }
}
