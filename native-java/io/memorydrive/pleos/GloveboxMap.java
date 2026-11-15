package io.memorydrive.pleos;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;
import java.util.*;

/** Presentation around the shipped map renderer and road-preview service. */
public final class GloveboxMap extends FrameLayout {
    final Activity activity; final Runnable close,account;
    Object renderer; List<?> courses=Collections.emptyList(),stops=Collections.emptyList();
    TextView status,summary; int generation; boolean disposed;
    Typeface bodyFont,headingFont;
    GradientDrawable glass(){GradientDrawable d=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{0xd054493e,0xe023221f,0xe02d2822});d.setCornerRadius(26);d.setStroke(1,0x70f2d6ad);return d;}
    static Object call(Object o,String name,Object... args){return GloveboxMotion.call(o,name,args);}
    static Class<?> cls(String name){return GloveboxMotion.cls("io.memorydrive.pleos."+name);}
    TextView text(String value,int size){TextView v=new TextView(activity);v.setText(value);v.setTextSize(size);v.setTypeface(bodyFont);v.setTextColor(0xfff8f1e6);v.setLineSpacing(4,1);v.setPadding(18,12,18,12);return v;}
    TextView button(String title,Runnable action){TextView b=text(title,18);b.setGravity(Gravity.CENTER);GradientDrawable face=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{0x604e4438,0x353f3830});face.setCornerRadius(17);face.setStroke(1,0x60f4d9b3);b.setBackground(new RippleDrawable(android.content.res.ColorStateList.valueOf(0x40e9c184),face,null));b.setOnClickListener(v->action.run());b.setContentDescription(title);return b;}
    void addButton(LinearLayout panel,String title,Runnable action){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,62);p.bottomMargin=12;panel.addView(button(title,action),p);}
    void themeText(View view){if(view instanceof TextView){((TextView)view).setTypeface(bodyFont);((TextView)view).setTextColor(0xfff5e7d4);}if(view instanceof ViewGroup){ViewGroup group=(ViewGroup)view;for(int i=0;i<group.getChildCount();i++)themeText(group.getChildAt(i));}}
    final class GlassBuilder extends AlertDialog.Builder {
        GlassBuilder(Context context){super(context,android.R.style.Theme_Material_Dialog_Alert);}
        @Override public AlertDialog show(){AlertDialog d=super.show();d.getWindow().setBackgroundDrawable(glass());themeText(d.getWindow().getDecorView());for(int which:new int[]{-1,-2,-3}){Button b=d.getButton(which);if(b!=null)b.setTextColor(0xffe7bd83);}return d;}
    }
    public GloveboxMap(Activity a,Runnable exit,Runnable connect){
        super(a);activity=a;close=exit;account=connect;setBackgroundColor(0xff191715);
        try{bodyFont=Typeface.createFromAsset(a.getAssets(),"glovebox/body.ttf");headingFont=Typeface.createFromAsset(a.getAssets(),"glovebox/flower-tree.ttf");}catch(Exception ignored){bodyFont=Typeface.create("sans-serif",Typeface.NORMAL);headingFont=bodyFont;}
        ImageView atmosphere=new ImageView(a);atmosphere.setScaleType(ImageView.ScaleType.CENTER_CROP);try(java.io.InputStream in=a.getAssets().open("glovebox/landscape.webp")){atmosphere.setImageBitmap(BitmapFactory.decodeStream(in));}catch(Exception ignored){}atmosphere.setAlpha(.24f);addView(atmosphere,new LayoutParams(-1,-1));
        LinearLayout layout=new LinearLayout(a);layout.setPadding(24,18,24,22);layout.setOrientation(LinearLayout.VERTICAL);addView(layout,new LayoutParams(-1,-1));
        FrameLayout header=new FrameLayout(a);header.setBackground(glass());LinearLayout.LayoutParams headerSpace=new LinearLayout.LayoutParams(-1,84);headerSpace.bottomMargin=20;layout.addView(header,headerSpace);
        TextView section=text("EXPLORE  /  함께 나눈 길",17);section.setTextColor(0xffe8c28d);FrameLayout.LayoutParams hp=new FrameLayout.LayoutParams(-2,-2,Gravity.LEFT|Gravity.CENTER_VERTICAL);hp.leftMargin=14;header.addView(section,hp);
        ImageView logo=new ImageView(a);logo.setScaleType(ImageView.ScaleType.FIT_CENTER);try(java.io.InputStream in=a.getAssets().open("glovebox/logo.png")){logo.setImageBitmap(BitmapFactory.decodeStream(in));}catch(Exception ignored){}header.addView(logo,new FrameLayout.LayoutParams(190,52,Gravity.CENTER));
        FrameLayout.LayoutParams cp=new FrameLayout.LayoutParams(100,52,Gravity.RIGHT|Gravity.CENTER_VERTICAL);cp.rightMargin=16;header.addView(button("닫기  ×",close),cp);
        LinearLayout body=new LinearLayout(a);layout.addView(body,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout panel=new LinearLayout(a);panel.setOrientation(LinearLayout.VERTICAL);panel.setPadding(16,20,16,20);panel.setBackground(glass());LinearLayout.LayoutParams panelSpace=new LinearLayout.LayoutParams(350,-1);panelSpace.rightMargin=20;body.addView(panel,panelSpace);
        TextView eyebrow=text("THE NEXT JOURNEY",12);eyebrow.setLetterSpacing(.16f);eyebrow.setTextColor(0xffddba87);panel.addView(eyebrow);
        TextView title=text("다음에 만날 길",34);title.setTypeface(headingFont);panel.addView(title);
        summary=text("좋았던 장소들을 이어\n다음 드라이브를 그려 보세요.",17);summary.setTextColor(0xffd0c3b2);panel.addView(summary);
        addButton(panel,"보관한 코스 선택",this::chooseCourse);
        addButton(panel,"도로 경로 보기",this::road);
        View primary=panel.getChildAt(panel.getChildCount()-1);GradientDrawable gold=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{0xfff1d2a0,0xffd4a25f});gold.setCornerRadius(17);gold.setStroke(1,0xffffe6bd);primary.setBackground(new RippleDrawable(android.content.res.ColorStateList.valueOf(0x30ffffff),gold,null));((TextView)primary).setTextColor(0xff382b1a);
        addButton(panel,"내 계정 연결",account);
        addButton(panel,"전체 경로 맞추기",()->{if(renderer!=null)call(renderer,"fit");});
        LinearLayout zoom=new LinearLayout(a);zoom.addView(button("－",()->call(renderer,"changeZoom",-1)),new LinearLayout.LayoutParams(0,62,1));zoom.addView(button("＋",()->call(renderer,"changeZoom",1)),new LinearLayout.LayoutParams(0,62,1));panel.addView(zoom);
        View space=new View(a);panel.addView(space,new LinearLayout.LayoutParams(1,0,1));
        status=text("지도를 불러오는 중입니다.",14);status.setTextColor(0xffd8c4a4);panel.addView(status);
        TextView attribution=text("© OpenStreetMap contributors\n코스 미리보기 · 실제 주행 이력은 ‘주행 기록’에서",12);attribution.setTextColor(0xffb2a493);panel.addView(attribution);
        try{
            renderer=cls("CommunityMapView").getConstructor(Context.class).newInstance(a);
            try{java.lang.reflect.Field f=renderer.getClass().getDeclaredField("tilePaint");f.setAccessible(true);((Paint)f.get(renderer)).setColorFilter(new ColorMatrixColorFilter(new float[]{-.2f,-.4f,-.07f,0,201,-.2f,-.4f,-.07f,0,190,-.2f,-.4f,-.07f,0,174,0,0,0,1,0}));}catch(Exception e){android.util.Log.w("GloveboxMap","map palette",e);}
            call(renderer,"setOnTileState",GloveboxMotion.function("kotlin.jvm.functions.Function1",(proxy,method,args)->{
                if(method.getName().equals("invoke")){String state=String.valueOf(args[0]);status.setText(state.equals("FAILED")?"배경 지도를 불러오지 못했어요. 인터넷 연결을 확인하세요.":state.equals("LOADING")?"지도를 불러오는 중입니다.":"지도를 드래그하거나 확대해 보세요.");return GloveboxMotion.unit();}
                if(method.getName().equals("hashCode"))return System.identityHashCode(proxy);
                if(method.getName().equals("equals"))return proxy==args[0];return "GloveboxMapState";
            }));
            FrameLayout mapCard=new FrameLayout(a);mapCard.setBackground(glass());mapCard.setClipToOutline(true);mapCard.setPadding(1,1,1,1);mapCard.addView((View)renderer,new FrameLayout.LayoutParams(-1,-1));body.addView(mapCard,new LinearLayout.LayoutParams(0,-1,1));
            call(renderer,"update",Collections.emptyList(),stops,null,null);
        }catch(Exception e){status.setText("지도를 열지 못했어요.");android.util.Log.e("GloveboxMap","open",e);}
    }
    void chooseCourse(){
        try{
            Object store=cls("CourseStore").getConstructor(Context.class).newInstance(activity);
            courses=(List<?>)call(store,"all");
            if(courses.isEmpty()){new GlassBuilder(activity).setTitle("보관한 코스가 없어요").setMessage("함께 나눈 길에서 코스를 보관하거나 만들어 주세요. 배경 지도는 지금도 둘러볼 수 있습니다.").setPositiveButton("확인",null).show();return;}
            String[] titles=new String[courses.size()];for(int i=0;i<titles.length;i++)titles[i]=String.valueOf(call(courses.get(i),"getTitle"));
            new GlassBuilder(activity).setTitle("보관한 코스").setItems(titles,(d,index)->{
                generation++;Object course=courses.get(index);stops=(List<?>)call(course,"getStops");
                call(renderer,"update",Collections.emptyList(),stops,null,null);
                String endpoints=stops.isEmpty()?"장소 없음":String.valueOf(call(stops.get(0),"getName"))+" → "+String.valueOf(call(stops.get(stops.size()-1),"getName"));
                summary.setText(titles[index]+"\n"+endpoints+"\n"+stops.size()+"개 경유지");
                try{
                    Object road=AlbumPlayer.savedRoad(activity,stops);
                    if(road!=null){call(renderer,"update",Collections.emptyList(),stops,null,road);call(renderer,"fit");summary.append(String.format(Locale.KOREA,"\n%.1f km · 약 %d분",((Number)call(road,"getMeters")).doubleValue()/1000,Math.round(((Number)call(road,"getSeconds")).doubleValue()/60)));}
                    else if(CourseRoads.allowed(activity)||CourseRoads.connectedServiceAllowed(activity))fetchRoad();
                }catch(Exception err){android.util.Log.w("GloveboxMap","saved road",err);}
            }).setNegativeButton("취소",null).show();
        }catch(Exception e){status.setText("코스를 읽지 못했어요.");android.util.Log.e("GloveboxMap","courses",e);}
    }
    void road(){
        if(stops.size()<2){new GlassBuilder(activity).setMessage("장소가 2개 이상인 보관한 코스를 먼저 선택해 주세요.").setPositiveButton("코스 선택",(d,w)->chooseCourse()).setNegativeButton("닫기",null).show();return;}
        CourseRoads.authorize(activity,this::fetchRoad);
    }
    void fetchRoad(){
        final int request=++generation;final List<?> selected=stops;status.setText("도로 경로를 확인하고 있어요.");
        new Thread(()->{try{
            Object route=CourseRoads.fetch(activity.getApplicationContext(),selected);
            post(()->{if(disposed||request!=generation)return;call(renderer,"update",Collections.emptyList(),selected,null,route);summary.append(String.format(Locale.KOREA,"\n도로 %.1f km · 약 %d분",((Number)call(route,"getMeters")).doubleValue()/1000,Math.round(((Number)call(route,"getSeconds")).doubleValue()/60)));status.setText("선택한 코스의 도로 경로입니다.");});
        }catch(Exception e){post(()->{if(!disposed&&request==generation)status.setText("도로 경로를 불러오지 못했어요. 네트워크 연결을 확인해 주세요.");});android.util.Log.e("GloveboxMap","road",e);}},"glovebox-road-preview").start();
    }
    public void dispose(){disposed=true;generation++;}
}
