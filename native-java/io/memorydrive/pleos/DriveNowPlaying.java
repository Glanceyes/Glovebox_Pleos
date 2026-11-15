package io.memorydrive.pleos;

import android.app.Activity;
import android.content.ContentUris;
import android.content.res.ColorStateList;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.*;
import android.webkit.WebResourceResponse;
import android.widget.*;
import java.io.InputStream;
import java.util.Locale;
import org.json.JSONObject;

final class DriveNowPlaying extends LinearLayout {
    private final Activity host;
    private final AlbumPlayer library;
    private final ImageView art;
    private final TextView heading,artist,elapsed,playState;
    private final TransportButton action;
    private final ProgressBar progress;
    private String artId;
    private final int ivory=0xfff9f0df,gold=0xffe9c38b;
    private int dp(float x){ return Math.round(x*getResources().getDisplayMetrics().density); }
    private GradientDrawable shape(int color,int radius){GradientDrawable bg=new GradientDrawable();bg.setColor(color);bg.setCornerRadius(dp(radius));return bg;}
    private TextView text(int size,int color,boolean bold){
        TextView t=new TextView(host);t.setTextSize(size);t.setTextColor(color);t.setSingleLine();t.setEllipsize(TextUtils.TruncateAt.END);
        if(bold)t.setTypeface(Typeface.create("sans-serif-medium",0));return t;
    }
    DriveNowPlaying(Activity activity,AlbumPlayer catalog,Runnable toggle){
        super(activity);host=activity;library=catalog;
        setOrientation(HORIZONTAL);setGravity(Gravity.CENTER_VERTICAL);setPadding(dp(19),dp(14),dp(19),dp(14));
        GradientDrawable bg=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{0xff514338,0xff302c2a,0xff252526});bg.setCornerRadius(dp(20));bg.setStroke(dp(1),0xff77624b);setBackground(bg);
        art=new ImageView(activity);art.setScaleType(ImageView.ScaleType.CENTER_CROP);art.setBackground(shape(0xff635242,13));art.setClipToOutline(true);
        addView(art,new LayoutParams(dp(145),dp(145)));
        LinearLayout detail=new LinearLayout(activity);detail.setOrientation(VERTICAL);detail.setGravity(Gravity.CENTER_VERTICAL);
        LayoutParams column=new LayoutParams(0,-1,1);column.leftMargin=dp(21);column.rightMargin=dp(16);addView(detail,column);
        playState=text(16,gold,true);detail.addView(playState);
        heading=text(36,ivory,true);artist=text(19,0xffe9cdb0,false);elapsed=text(14,0xffd3c4af,false);
        detail.addView(heading);detail.addView(artist);
        progress=new ProgressBar(activity,null,android.R.attr.progressBarStyleHorizontal);progress.setMax(1000);progress.setProgressTintList(ColorStateList.valueOf(gold));progress.setProgressBackgroundTintList(ColorStateList.valueOf(0xff736454));
        LayoutParams bar=new LayoutParams(-1,dp(5));bar.topMargin=dp(15);bar.bottomMargin=dp(4);detail.addView(progress,bar);detail.addView(elapsed);
        action=new TransportButton(activity);action.setOnClickListener(v->toggle.run());addView(action,new LayoutParams(dp(60),dp(60)));setVisibility(GONE);
    }
    @Override protected void onMeasure(int widthSpec,int heightSpec){
        int width=MeasureSpec.getSize(widthSpec),height=MeasureSpec.getSize(heightSpec);
        if(width>0&&height>0){
            int artSide=Math.min((int)(height*.74),(int)(width*.24)),pad=(int)(width*.038);
            setPadding(pad,0,pad,0);art.getLayoutParams().width=artSide;art.getLayoutParams().height=artSide;
            LayoutParams column=(LayoutParams)getChildAt(1).getLayoutParams();column.leftMargin=pad;column.rightMargin=pad;
            int button=Math.min((int)(height*.31),(int)(width*.095));action.getLayoutParams().width=button;action.getLayoutParams().height=button;
            int px=android.util.TypedValue.COMPLEX_UNIT_PX;
            heading.setTextSize(px,Math.min(height*.19f,width*.06f));artist.setTextSize(px,height*.092f);
            playState.setTextSize(px,height*.062f);elapsed.setTextSize(px,height*.059f);
            progress.getLayoutParams().height=Math.max(dp(3),(int)(height*.016));
        }
        super.onMeasure(widthSpec,heightSpec);
    }
    private static final class TransportButton extends android.widget.ImageButton {
        private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path triangle=new Path();
        private boolean playing;
        TransportButton(Activity activity){
            super(activity);
            GradientDrawable circle=new GradientDrawable();circle.setShape(GradientDrawable.OVAL);circle.setColor(0xffe9c38b);
            setPadding(0,0,0,0);
            setBackground(new android.graphics.drawable.RippleDrawable(ColorStateList.valueOf(0x55342a20),circle,null));
            setFocusable(true);
        }
        void setPlaying(boolean on){if(playing!=on){playing=on;invalidate();}}
        @Override protected void onDraw(Canvas c){
            super.onDraw(c);
            c.save();c.translate(getWidth()*.5f,getHeight()*.5f);
            float scale=Math.min(getWidth(),getHeight())/48f;c.scale(scale,scale);paint.setColor(0xff31271f);
            if(playing){
                c.drawRoundRect(-8.5f,-10,-2.5f,10,1.3f,1.3f,paint);
                c.drawRoundRect(2.5f,-10,8.5f,10,1.3f,1.3f,paint);
            }else{
                triangle.reset();triangle.moveTo(-6.5f,-10.5f);triangle.lineTo(10,0);triangle.lineTo(-6.5f,10.5f);triangle.close();
                c.drawPath(triangle,paint);
            }
            c.restore();
        }
    }
    private static final class RecordSleeve extends Drawable {
        private final Paint pen=new Paint(Paint.ANTI_ALIAS_FLAG);
        @Override public void draw(Canvas canvas){
            Rect bounds=getBounds();
            float cx=bounds.exactCenterX(),cy=bounds.exactCenterY(),radius=Math.min(bounds.width(),bounds.height())*.39f;
            canvas.drawColor(0xff51463c);
            pen.setStyle(Paint.Style.FILL);pen.setColor(0xff252322);canvas.drawCircle(cx,cy,radius,pen);
            pen.setStyle(Paint.Style.STROKE);pen.setStrokeWidth(Math.max(1,radius*.009f));pen.setColor(0xff635c52);
            for(int i=0;i<5;i++)canvas.drawCircle(cx,cy,radius*(.54f+i*.09f),pen);
            pen.setStyle(Paint.Style.FILL);pen.setColor(0xffcfac79);canvas.drawCircle(cx,cy,radius*.32f,pen);
            pen.setColor(0xff443a30);canvas.drawCircle(cx,cy,radius*.055f,pen);
        }
        @Override public void setAlpha(int alpha){}
        @Override public void setColorFilter(ColorFilter filter){}
        @Override public int getOpacity(){return PixelFormat.OPAQUE;}
        @Override public int getIntrinsicWidth(){return 256;}
        @Override public int getIntrinsicHeight(){return 256;}
    }
    static String clock(long ms){return String.format(Locale.ROOT,"%d:%02d",ms/60000,ms/1000%60);}
    void show(JSONObject track,JSONObject status,String cover,String message,String provider){
        if(track==null){setVisibility(GONE);return;}
        boolean confirmed=status.has("track"),on=confirmed&&status.optBoolean("playing");
        boolean external=!provider.isEmpty()&&!confirmed;
        action.setVisibility(external?INVISIBLE:VISIBLE);
        playState.setText(external?provider:confirmed?(on?"재생 중":"일시정지"):"추천곡");
        heading.setText(track.optString("title"));artist.setText(track.optString("artist").replace(" - Topic",""));
        long duration=track.optLong("duration"),pos=status.optLong("position",-1);
        if(duration>0)pos=Math.min(pos,duration);
        progress.setVisibility(confirmed&&pos>=0&&duration>0?VISIBLE:INVISIBLE);
        progress.setProgress(duration>0&&pos>=0?(int)(pos*1000/duration):0);
        String time=pos>=0?clock(pos)+(duration>0?"  /  "+clock(duration):""):"";
        elapsed.setText(confirmed?time:(message.isEmpty()?"재생 준비":message));
        action.setEnabled(!confirmed||status.optBoolean("canToggle",true));
        action.setPlaying(on);action.setContentDescription(on?"음악 일시정지":"음악 재생");
        if(!cover.equals(artId)){artId=cover;art.setImageDrawable(new RecordSleeve());new Thread(()->{
            try{Bitmap b;
                if(cover.matches("[0-9]+"))b=host.getContentResolver().loadThumbnail(ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,Long.parseLong(cover)),new android.util.Size(384,384),null);
                else{WebResourceResponse r=library.thumbnail(host,cover);if(r==null)return;try(InputStream in=r.getData()){b=BitmapFactory.decodeStream(in);}}
                host.runOnUiThread(()->{if(b!=null&&cover.equals(artId))art.setImageBitmap(b);});
            }catch(Exception ignored){}
        },"drive-art").start();}
        setVisibility(VISIBLE);
    }
}
