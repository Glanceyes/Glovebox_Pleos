.class public final Lio/memorydrive/pleos/JournalRoadLayer;
.super Ljava/lang/Object;
.implements Lkotlin/jvm/functions/Function1;
.field private final points:Ljava/util/List;
.method private constructor <init>(Ljava/util/List;)V
 .locals 0
 invoke-direct {p0}, Ljava/lang/Object;-><init>()V
 iput-object p1,p0,Lio/memorydrive/pleos/JournalRoadLayer;->points:Ljava/util/List;
 return-void
.end method
.method public static show(Landroidx/compose/ui/Modifier;Ljava/util/List;Landroidx/compose/runtime/Composer;)V
 .locals 6
 new-instance v0,Lio/memorydrive/pleos/JournalRoadLayer;
 const/4 v1,0x0
 invoke-direct {v0,v1},Lio/memorydrive/pleos/JournalRoadLayer;-><init>(Ljava/util/List;)V
 move-object v1,p0
 new-instance v2,Lio/memorydrive/pleos/JournalRoadLayer;
 invoke-direct {v2,p1},Lio/memorydrive/pleos/JournalRoadLayer;-><init>(Ljava/util/List;)V
 move-object v3,p2
 const/4 v4,0x0
 const/4 v5,0x0
 invoke-static/range {v0 .. v5},Landroidx/compose/ui/viewinterop/AndroidView_androidKt;->AndroidView(Lkotlin/jvm/functions/Function1;Landroidx/compose/ui/Modifier;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;II)V
 return-void
.end method
.method public invoke(Ljava/lang/Object;)Ljava/lang/Object;
 .locals 12
 iget-object v10,p0,Lio/memorydrive/pleos/JournalRoadLayer;->points:Ljava/util/List;
 if-nez v10,:update
 check-cast p1,Landroid/content/Context;
 new-instance v0,Lio/memorydrive/pleos/CommunityMapView;
 invoke-direct {v0,p1},Lio/memorydrive/pleos/CommunityMapView;-><init>(Landroid/content/Context;)V
 invoke-virtual {v0},Lio/memorydrive/pleos/CommunityMapView;->enableJournalDisplay()V
 return-object v0
 :update
 check-cast p1,Lio/memorydrive/pleos/CommunityMapView;
 invoke-virtual {p1},Landroid/view/View;->getTag()Ljava/lang/Object;
 move-result-object v1
 invoke-interface {v10,v1},Ljava/util/List;->equals(Ljava/lang/Object;)Z
 move-result v1
 if-nez v1,:done
 invoke-virtual {p1,v10},Landroid/view/View;->setTag(Ljava/lang/Object;)V
 new-instance v1,Ljava/util/ArrayList;
 invoke-direct {v1},Ljava/util/ArrayList;-><init>()V
 invoke-interface {v10},Ljava/util/List;->iterator()Ljava/util/Iterator;
 move-result-object v9
 :loop
 invoke-interface {v9},Ljava/util/Iterator;->hasNext()Z
 move-result v8
 if-eqz v8,:draw
 invoke-interface {v9},Ljava/util/Iterator;->next()Ljava/lang/Object;
 move-result-object v8
 check-cast v8,Lio/memorydrive/pleos/DrivePoint;
 invoke-virtual {v8},Lio/memorydrive/pleos/DrivePoint;->getLatitude()D
 move-result-wide v2
 invoke-virtual {v8},Lio/memorydrive/pleos/DrivePoint;->getLongitude()D
 move-result-wide v4
 new-instance v6,Lio/memorydrive/pleos/RoadPoint;
 invoke-direct {v6,v2,v3,v4,v5},Lio/memorydrive/pleos/RoadPoint;-><init>(DD)V
 invoke-interface {v1,v6},Ljava/util/List;->add(Ljava/lang/Object;)Z
 goto :loop
 :draw
 new-instance v0,Lio/memorydrive/pleos/RoadRoute;
 const-wide/16 v2,0x0
 const-wide/16 v4,0x0
 invoke-static {},Ljava/util/Collections;->emptyList()Ljava/util/List;
 move-result-object v6
 invoke-direct/range {v0 .. v6},Lio/memorydrive/pleos/RoadRoute;-><init>(Ljava/util/List;DDLjava/util/List;)V
 const/4 v7,0x0
 invoke-virtual {p1,v6,v6,v7,v0},Lio/memorydrive/pleos/CommunityMapView;->update(Ljava/util/List;Ljava/util/List;Ljava/lang/String;Lio/memorydrive/pleos/RoadRoute;)V
 invoke-virtual {p1},Lio/memorydrive/pleos/CommunityMapView;->fit()V
 const-string v7,"주행한 길의 도로 지도"
 invoke-virtual {p1,v7},Landroid/view/View;->setContentDescription(Ljava/lang/CharSequence;)V
 :done
 sget-object v0,Lkotlin/Unit;->INSTANCE:Lkotlin/Unit;
 return-object v0
.end method
