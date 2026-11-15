.class public final Lio/memorydrive/pleos/LibraryExperienceKt;
.super Ljava/lang/Object;
.source "LibraryExperience.kt"


# annotations
.annotation system Ldalvik/annotation/SourceDebugExtension;
    value = "SMAP\nLibraryExperience.kt\nKotlin\n*S Kotlin\n*F\n+ 1 LibraryExperience.kt\nio/memorydrive/pleos/LibraryExperienceKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,68:1\n1#2:69\n295#3,2:70\n1557#3:72\n1628#3,3:73\n774#3:76\n865#3,2:77\n2341#3,14:79\n295#3,2:93\n295#3,2:95\n*S KotlinDebug\n*F\n+ 1 LibraryExperience.kt\nio/memorydrive/pleos/LibraryExperienceKt\n*L\n21#1:70,2\n30#1:72\n30#1:73,3\n31#1:76\n31#1:77,2\n32#1:79,14\n34#1:93,2\n35#1:95,2\n*E\n"
.end annotation

.annotation runtime Lkotlin/Metadata;
    d1 = {
        "\u0000>\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0010\"\n\u0002\u0008\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\u0008\u0002\n\u0002\u0010\u0006\n\u0002\u0008\u0003\u001a\u000e\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003\u001aL\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u000c\u0010\u0006\u001a\u0008\u0012\u0004\u0012\u00020\u00030\u00072\u000c\u0010\u0008\u001a\u0008\u0012\u0004\u0012\u00020\u00010\t2\u0008\u0010\n\u001a\u0004\u0018\u00010\u00012\u0008\u0010\u000b\u001a\u0004\u0018\u00010\u000c2\u0006\u0010\r\u001a\u00020\u000e2\n\u0008\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0010\u001a\u000e\u0010\u0011\u001a\u00020\u00012\u0006\u0010\u0012\u001a\u00020\u0013\u001a\u000e\u0010\u0014\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003\u001a\u000e\u0010\u0015\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003\u00a8\u0006\u0016"
    }
    d2 = {
        "libraryMemoryTitle",
        "",
        "memory",
        "Lio/memorydrive/pleos/Memory;",
        "chooseLibraryFeature",
        "Lio/memorydrive/pleos/LibraryFeature;",
        "memories",
        "",
        "bookmarks",
        "",
        "selectedId",
        "fix",
        "Lio/memorydrive/pleos/LibraryFix;",
        "now",
        "",
        "journey",
        "Lio/memorydrive/pleos/MemoryJourney;",
        "nearbyDistance",
        "distance",
        "",
        "musicConnectionLabel",
        "musicConnectionExplanation",
        "app_release"
    }
    k = 0x2
    mv = {
        0x2,
        0x0,
        0x0
    }
    xi = 0x30
.end annotation


# direct methods
.method public static final chooseLibraryFeature(Ljava/util/List;Ljava/util/Set;Ljava/lang/String;Lio/memorydrive/pleos/LibraryFix;JLio/memorydrive/pleos/MemoryJourney;)Lio/memorydrive/pleos/LibraryFeature;
    .locals 8
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/List<",
            "Lio/memorydrive/pleos/Memory;",
            ">;",
            "Ljava/util/Set<",
            "Ljava/lang/String;",
            ">;",
            "Ljava/lang/String;",
            "Lio/memorydrive/pleos/LibraryFix;",
            "J",
            "Lio/memorydrive/pleos/MemoryJourney;",
            ")",
            "Lio/memorydrive/pleos/LibraryFeature;"
        }
    .end annotation

    const-string v0, "memories"

    invoke-static {p0, v0}, Lkotlin/jvm/internal/Intrinsics;->checkNotNullParameter(Ljava/lang/Object;Ljava/lang/String;)V

    const-string v0, "bookmarks"

    invoke-static {p1, v0}, Lkotlin/jvm/internal/Intrinsics;->checkNotNullParameter(Ljava/lang/Object;Ljava/lang/String;)V

    .line 17
    invoke-interface {p0}, Ljava/util/List;->isEmpty()Z

    move-result v0

    const/4 v1, 0x0

    if-eqz v0, :cond_0

    return-object v1

    :cond_0
    if-eqz p6, :cond_5

    .line 20
    invoke-virtual {p6}, Lio/memorydrive/pleos/MemoryJourney;->getProtectsMemory()Z

    move-result v0

    if-eqz v0, :cond_1

    goto :goto_0

    :cond_1
    move-object p6, v1

    :goto_0
    if-eqz p6, :cond_5

    .line 21
    move-object v0, p0

    check-cast v0, Ljava/lang/Iterable;

    .line 70
    invoke-interface {v0}, Ljava/lang/Iterable;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :cond_2
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v2

    if-eqz v2, :cond_3

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v2

    move-object v3, v2

    check-cast v3, Lio/memorydrive/pleos/Memory;

    .line 21
    invoke-virtual {v3}, Lio/memorydrive/pleos/Memory;->getId()Ljava/lang/String;

    move-result-object v3

    invoke-virtual {p6}, Lio/memorydrive/pleos/MemoryJourney;->getMemoryId()Ljava/lang/String;

    move-result-object v4

    invoke-static {v3, v4}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v3

    if-eqz v3, :cond_2

    goto :goto_1

    :cond_3
    move-object v2, v1

    :goto_1
    move-object v0, v2

    check-cast v0, Lio/memorydrive/pleos/Memory;

    if-eqz v0, :cond_5

    .line 22
    new-instance v1, Lio/memorydrive/pleos/LibraryFeature;

    invoke-virtual {p6}, Lio/memorydrive/pleos/MemoryJourney;->getPhase()Lio/memorydrive/pleos/JourneyPhase;

    move-result-object p0

    sget-object p1, Lio/memorydrive/pleos/JourneyPhase;->DEFERRED:Lio/memorydrive/pleos/JourneyPhase;

    if-ne p0, p1, :cond_4

    .line 23
    const-string p0, "\uc8fc\ucc28 \ud6c4 \uc774\uc5b4 \ubcf4\ub824\uace0 \ub0a8\uae34 \uae30\uc5b5"

    goto :goto_2

    :cond_4
    const-string p0, "\uc9c0\uae08 \uc774\uc5b4 \ubcfc \uae30\uc5b5"

    :goto_2
    move-object p2, p0

    const/4 p4, 0x4

    const/4 p5, 0x0

    const/4 p3, 0x0

    move-object p0, v1

    move-object p1, v0

    .line 22
    invoke-direct/range {p0 .. p5}, Lio/memorydrive/pleos/LibraryFeature;-><init>(Lio/memorydrive/pleos/Memory;Ljava/lang/String;Ljava/lang/Double;ILkotlin/jvm/internal/DefaultConstructorMarker;)V

    return-object v1

    :cond_5
    if-eqz p3, :cond_6

    .line 26
    invoke-virtual {p3}, Lio/memorydrive/pleos/LibraryFix;->getTimestampMillis()J

    move-result-wide v2

    invoke-static {v2, v3, p4, p5}, Lio/memorydrive/pleos/MemoryKt;->isFreshLocation(JJ)Z

    move-result p4

    if-eqz p4, :cond_6

    .line 27
    invoke-virtual {p3}, Lio/memorydrive/pleos/LibraryFix;->getLatitude()D

    move-result-wide p4

    invoke-static {p4, p5}, Ljava/lang/Double;->isInfinite(D)Z

    move-result p6

    if-nez p6, :cond_6

    invoke-static {p4, p5}, Ljava/lang/Double;->isNaN(D)Z

    move-result p4

    if-nez p4, :cond_6

    invoke-virtual {p3}, Lio/memorydrive/pleos/LibraryFix;->getLongitude()D

    move-result-wide p4

    invoke-static {p4, p5}, Ljava/lang/Double;->isInfinite(D)Z

    move-result p6

    if-nez p6, :cond_6

    invoke-static {p4, p5}, Ljava/lang/Double;->isNaN(D)Z

    move-result p4

    if-nez p4, :cond_6

    .line 28
    invoke-virtual {p3}, Lio/memorydrive/pleos/LibraryFix;->getLatitude()D

    move-result-wide p4

    const-wide v2, -0x3fa9800000000000L    # -90.0

    cmpg-double p6, v2, p4

    if-gtz p6, :cond_6

    const-wide v2, 0x4056800000000000L    # 90.0

    cmpg-double p4, p4, v2

    if-gtz p4, :cond_6

    invoke-virtual {p3}, Lio/memorydrive/pleos/LibraryFix;->getLongitude()D

    move-result-wide p4

    const-wide v2, -0x3f99800000000000L    # -180.0

    cmpg-double p6, v2, p4

    if-gtz p6, :cond_6

    const-wide v2, 0x4066800000000000L    # 180.0

    cmpg-double p4, p4, v2

    if-gtz p4, :cond_6

    goto :goto_3

    :cond_6
    move-object p3, v1

    :goto_3
    if-eqz p3, :cond_e

    .line 30
    move-object p4, p0

    check-cast p4, Ljava/lang/Iterable;

    .line 72
    new-instance p5, Ljava/util/ArrayList;

    const/16 p6, 0xa

    invoke-static {p4, p6}, Lkotlin/collections/CollectionsKt;->collectionSizeOrDefault(Ljava/lang/Iterable;I)I

    move-result p6

    invoke-direct {p5, p6}, Ljava/util/ArrayList;-><init>(I)V

    check-cast p5, Ljava/util/Collection;

    .line 73
    invoke-interface {p4}, Ljava/lang/Iterable;->iterator()Ljava/util/Iterator;

    move-result-object p4

    :goto_4
    invoke-interface {p4}, Ljava/util/Iterator;->hasNext()Z

    move-result p6

    if-eqz p6, :cond_7

    invoke-interface {p4}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object p6

    .line 74
    check-cast p6, Lio/memorydrive/pleos/Memory;

    .line 30
    sget-object v2, Lio/memorydrive/pleos/MemoryRoutePolicy;->INSTANCE:Lio/memorydrive/pleos/MemoryRoutePolicy;

    invoke-virtual {p3}, Lio/memorydrive/pleos/LibraryFix;->getLatitude()D

    move-result-wide v4

    invoke-virtual {p3}, Lio/memorydrive/pleos/LibraryFix;->getLongitude()D

    move-result-wide v6

    move-object v3, p6

    invoke-virtual/range {v2 .. v7}, Lio/memorydrive/pleos/MemoryRoutePolicy;->distance(Lio/memorydrive/pleos/Memory;DD)D

    move-result-wide v2

    invoke-static {v2, v3}, Ljava/lang/Double;->valueOf(D)Ljava/lang/Double;

    move-result-object v0

    invoke-static {p6, v0}, Lkotlin/TuplesKt;->to(Ljava/lang/Object;Ljava/lang/Object;)Lkotlin/Pair;

    move-result-object p6

    .line 74
    invoke-interface {p5, p6}, Ljava/util/Collection;->add(Ljava/lang/Object;)Z

    goto :goto_4

    .line 75
    :cond_7
    check-cast p5, Ljava/util/List;

    .line 30
    check-cast p5, Ljava/lang/Iterable;

    .line 76
    new-instance p3, Ljava/util/ArrayList;

    invoke-direct {p3}, Ljava/util/ArrayList;-><init>()V

    check-cast p3, Ljava/util/Collection;

    .line 77
    invoke-interface {p5}, Ljava/lang/Iterable;->iterator()Ljava/util/Iterator;

    move-result-object p4

    :cond_8
    :goto_5
    invoke-interface {p4}, Ljava/util/Iterator;->hasNext()Z

    move-result p5

    if-eqz p5, :cond_9

    invoke-interface {p4}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object p5

    move-object p6, p5

    check-cast p6, Lkotlin/Pair;

    .line 31
    invoke-virtual {p6}, Lkotlin/Pair;->getSecond()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/lang/Number;

    invoke-virtual {v0}, Ljava/lang/Number;->doubleValue()D

    move-result-wide v2

    invoke-static {v2, v3}, Ljava/lang/Double;->isInfinite(D)Z

    move-result v0

    if-nez v0, :cond_8

    invoke-static {v2, v3}, Ljava/lang/Double;->isNaN(D)Z

    move-result v0

    if-nez v0, :cond_8

    invoke-virtual {p6}, Lkotlin/Pair;->getSecond()Ljava/lang/Object;

    move-result-object p6

    check-cast p6, Ljava/lang/Number;

    invoke-virtual {p6}, Ljava/lang/Number;->doubleValue()D

    move-result-wide v2

    const-wide v4, 0x40d86a0000000000L    # 25000.0

    cmpg-double p6, v2, v4

    if-gtz p6, :cond_8

    .line 77
    invoke-interface {p3, p5}, Ljava/util/Collection;->add(Ljava/lang/Object;)Z

    goto :goto_5

    .line 78
    :cond_9
    check-cast p3, Ljava/util/List;

    .line 31
    check-cast p3, Ljava/lang/Iterable;

    .line 79
    invoke-interface {p3}, Ljava/lang/Iterable;->iterator()Ljava/util/Iterator;

    move-result-object p3

    .line 80
    invoke-interface {p3}, Ljava/util/Iterator;->hasNext()Z

    move-result p4

    if-nez p4, :cond_a

    move-object p4, v1

    goto :goto_6

    .line 81
    :cond_a
    invoke-interface {p3}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object p4

    .line 82
    invoke-interface {p3}, Ljava/util/Iterator;->hasNext()Z

    move-result p5

    if-nez p5, :cond_b

    goto :goto_6

    .line 83
    :cond_b
    move-object p5, p4

    check-cast p5, Lkotlin/Pair;

    .line 32
    invoke-virtual {p5}, Lkotlin/Pair;->getSecond()Ljava/lang/Object;

    move-result-object p5

    check-cast p5, Ljava/lang/Number;

    invoke-virtual {p5}, Ljava/lang/Number;->doubleValue()D

    move-result-wide p5

    .line 85
    :cond_c
    invoke-interface {p3}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    .line 86
    move-object v2, v0

    check-cast v2, Lkotlin/Pair;

    .line 32
    invoke-virtual {v2}, Lkotlin/Pair;->getSecond()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Ljava/lang/Number;

    invoke-virtual {v2}, Ljava/lang/Number;->doubleValue()D

    move-result-wide v2

    .line 87
    invoke-static {p5, p6, v2, v3}, Ljava/lang/Double;->compare(DD)I

    move-result v4

    if-lez v4, :cond_d

    move-object p4, v0

    move-wide p5, v2

    .line 91
    :cond_d
    invoke-interface {p3}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-nez v0, :cond_c

    .line 32
    :goto_6
    check-cast p4, Lkotlin/Pair;

    if-eqz p4, :cond_e

    new-instance p0, Lio/memorydrive/pleos/LibraryFeature;

    invoke-virtual {p4}, Lkotlin/Pair;->getFirst()Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Lio/memorydrive/pleos/Memory;

    invoke-virtual {p4}, Lkotlin/Pair;->getSecond()Ljava/lang/Object;

    move-result-object p2

    check-cast p2, Ljava/lang/Double;

    const-string p3, "\uc774 \uc8fc\ubcc0\uc5d0 \ub0a8\uae34 \uae30\uc5b5"

    invoke-direct {p0, p1, p3, p2}, Lio/memorydrive/pleos/LibraryFeature;-><init>(Lio/memorydrive/pleos/Memory;Ljava/lang/String;Ljava/lang/Double;)V

    return-object p0

    .line 34
    :cond_e
    move-object p3, p0

    check-cast p3, Ljava/lang/Iterable;

    .line 93
    invoke-interface {p3}, Ljava/lang/Iterable;->iterator()Ljava/util/Iterator;

    move-result-object p4

    :cond_f
    invoke-interface {p4}, Ljava/util/Iterator;->hasNext()Z

    move-result p5

    if-eqz p5, :cond_10

    invoke-interface {p4}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object p5

    move-object p6, p5

    check-cast p6, Lio/memorydrive/pleos/Memory;

    .line 34
    invoke-virtual {p6}, Lio/memorydrive/pleos/Memory;->getId()Ljava/lang/String;

    move-result-object p6

    invoke-interface {p1, p6}, Ljava/util/Set;->contains(Ljava/lang/Object;)Z

    move-result p6

    if-eqz p6, :cond_f

    goto :goto_7

    :cond_10
    move-object p5, v1

    :goto_7
    move-object v3, p5

    check-cast v3, Lio/memorydrive/pleos/Memory;

    if-eqz v3, :cond_11

    new-instance p0, Lio/memorydrive/pleos/LibraryFeature;

    const/4 v6, 0x4

    const/4 v7, 0x0

    const-string v4, "\ub2e4\uc2dc \ubcf4\uace0 \uc2f6\uc5b4 \ubcf4\uad00\ud55c \uae30\uc5b5"

    const/4 v5, 0x0

    move-object v2, p0

    invoke-direct/range {v2 .. v7}, Lio/memorydrive/pleos/LibraryFeature;-><init>(Lio/memorydrive/pleos/Memory;Ljava/lang/String;Ljava/lang/Double;ILkotlin/jvm/internal/DefaultConstructorMarker;)V

    return-object p0

    .line 95
    :cond_11
    invoke-interface {p3}, Ljava/lang/Iterable;->iterator()Ljava/util/Iterator;

    move-result-object p1

    :cond_12
    invoke-interface {p1}, Ljava/util/Iterator;->hasNext()Z

    move-result p3

    if-eqz p3, :cond_13

    invoke-interface {p1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object p3

    move-object p4, p3

    check-cast p4, Lio/memorydrive/pleos/Memory;

    .line 35
    invoke-virtual {p4}, Lio/memorydrive/pleos/Memory;->getId()Ljava/lang/String;

    move-result-object p4

    invoke-static {p4, p2}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result p4

    if-eqz p4, :cond_12

    move-object v1, p3

    :cond_13
    move-object p1, v1

    check-cast p1, Lio/memorydrive/pleos/Memory;

    if-eqz p1, :cond_14

    new-instance p6, Lio/memorydrive/pleos/LibraryFeature;

    const/4 p4, 0x4

    const/4 p5, 0x0

    const-string p2, "\uc9c0\uae08 \uc774\uc5b4 \ubcfc \uae30\uc5b5"

    const/4 p3, 0x0

    move-object p0, p6

    invoke-direct/range {p0 .. p5}, Lio/memorydrive/pleos/LibraryFeature;-><init>(Lio/memorydrive/pleos/Memory;Ljava/lang/String;Ljava/lang/Double;ILkotlin/jvm/internal/DefaultConstructorMarker;)V

    return-object p6

    .line 36
    :cond_14
    new-instance p1, Lio/memorydrive/pleos/LibraryFeature;

    invoke-static {p0}, Lkotlin/collections/CollectionsKt;->first(Ljava/util/List;)Ljava/lang/Object;

    move-result-object p0

    move-object v1, p0

    check-cast v1, Lio/memorydrive/pleos/Memory;

    const/4 v4, 0x4

    const/4 v5, 0x0

    const-string v2, "\ub0b4 \uae30\ub85d\uc5d0\uc11c \ub2e4\uc2dc \ub9cc\ub098\ub294 \uc7a5\uba74"

    const/4 v3, 0x0

    move-object v0, p1

    invoke-direct/range {v0 .. v5}, Lio/memorydrive/pleos/LibraryFeature;-><init>(Lio/memorydrive/pleos/Memory;Ljava/lang/String;Ljava/lang/Double;ILkotlin/jvm/internal/DefaultConstructorMarker;)V

    return-object p1
.end method

.method public static synthetic chooseLibraryFeature$default(Ljava/util/List;Ljava/util/Set;Ljava/lang/String;Lio/memorydrive/pleos/LibraryFix;JLio/memorydrive/pleos/MemoryJourney;ILjava/lang/Object;)Lio/memorydrive/pleos/LibraryFeature;
    .locals 7

    and-int/lit8 p7, p7, 0x20

    if-eqz p7, :cond_0

    const/4 p6, 0x0

    :cond_0
    move-object v6, p6

    move-object v0, p0

    move-object v1, p1

    move-object v2, p2

    move-object v3, p3

    move-wide v4, p4

    .line 15
    invoke-static/range {v0 .. v6}, Lio/memorydrive/pleos/LibraryExperienceKt;->chooseLibraryFeature(Ljava/util/List;Ljava/util/Set;Ljava/lang/String;Lio/memorydrive/pleos/LibraryFix;JLio/memorydrive/pleos/MemoryJourney;)Lio/memorydrive/pleos/LibraryFeature;

    move-result-object p0

    return-object p0
.end method

.method public static final libraryMemoryTitle(Lio/memorydrive/pleos/Memory;)Ljava/lang/String;
    .locals 3

    const-string v0, "memory"

    invoke-static {p0, v0}, Lkotlin/jvm/internal/Intrinsics;->checkNotNullParameter(Ljava/lang/Object;Ljava/lang/String;)V

    .line 9
    invoke-virtual {p0}, Lio/memorydrive/pleos/Memory;->getPlace()Ljava/lang/String;

    move-result-object v0

    const-string v1, "\uc0ac\uc9c4\uc5d0 \ub0a8\uc740 \uc7a5\uc18c"

    invoke-static {v0, v1}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v0

    if-nez v0, :cond_0

    invoke-virtual {p0}, Lio/memorydrive/pleos/Memory;->getPlace()Ljava/lang/String;

    move-result-object p0

    return-object p0

    .line 10
    :cond_0
    new-instance v0, Lkotlin/text/Regex;

    const-string v1, "^(\\d{4})[.:-](\\d{2})[.:-](\\d{2})$"

    invoke-direct {v0, v1}, Lkotlin/text/Regex;-><init>(Ljava/lang/String;)V

    invoke-virtual {p0}, Lio/memorydrive/pleos/Memory;->getDate()Ljava/lang/String;

    move-result-object v1

    check-cast v1, Ljava/lang/CharSequence;

    invoke-virtual {v0, v1}, Lkotlin/text/Regex;->matchEntire(Ljava/lang/CharSequence;)Lkotlin/text/MatchResult;

    move-result-object v0

    if-eqz v0, :cond_1

    .line 11
    invoke-interface {v0}, Lkotlin/text/MatchResult;->getGroupValues()Ljava/util/List;

    move-result-object v1

    const/4 v2, 0x2

    invoke-interface {v1, v2}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Ljava/lang/String;

    invoke-static {v1}, Ljava/lang/Integer;->parseInt(Ljava/lang/String;)I

    move-result v1

    invoke-interface {v0}, Lkotlin/text/MatchResult;->getGroupValues()Ljava/util/List;

    move-result-object v0

    const/4 v2, 0x3

    invoke-interface {v0, v2}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/lang/String;

    invoke-static {v0}, Ljava/lang/Integer;->parseInt(Ljava/lang/String;)I

    move-result v0

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v2, v1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string v2, "\uc6d4 "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1, v0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v0

    const-string v1, "\uc77c\uc758 \uc7a5\uba74"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    if-nez v0, :cond_2

    :cond_1
    invoke-virtual {p0}, Lio/memorydrive/pleos/Memory;->getPlace()Ljava/lang/String;

    move-result-object v0

    :cond_2
    return-object v0
.end method

.method public static final musicConnectionExplanation(Lio/memorydrive/pleos/Memory;)Ljava/lang/String;
    .locals 3

    const-string v0, "memory"

    invoke-static {p0, v0}, Lkotlin/jvm/internal/Intrinsics;->checkNotNullParameter(Ljava/lang/Object;Ljava/lang/String;)V

    .line 54
    invoke-virtual {p0}, Lio/memorydrive/pleos/Memory;->getImported()Z

    move-result v0

    if-nez v0, :cond_0

    const-string p0, ""

    goto/16 :goto_3

    .line 55
    :cond_0
    invoke-virtual {p0}, Lio/memorydrive/pleos/Memory;->getMusicAssociation()Ljava/lang/String;

    move-result-object v0

    const-string v1, "shared_course"

    invoke-static {v0, v1}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_1

    const-string p0, "\ucf54\uc2a4\ub97c \ub098\ub208 \uc0ac\ub78c\uc774 \uc774 \uc7a5\uc18c\uc640 \ud568\uaed8 \uc804\ub2ec\ud55c \uc815\ubcf4\uc608\uc694. \ub098\uc758 \uacfc\uac70 \ubc29\ubb38\uc774\ub098 \uccad\ucde8 \uae30\ub85d\uc744 \ub73b\ud558\uc9c0 \uc54a\uc544\uc694."

    goto/16 :goto_3

    .line 56
    :cond_1
    invoke-virtual {p0}, Lio/memorydrive/pleos/Memory;->getLocationSource()Ljava/lang/String;

    move-result-object v0

    const-string v1, "vehicle"

    invoke-static {v0, v1}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v0

    const-string v1, "none"

    if-eqz v0, :cond_2

    invoke-virtual {p0}, Lio/memorydrive/pleos/Memory;->getMusicProvider()Ljava/lang/String;

    move-result-object v0

    invoke-static {v0, v1}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_2

    const-string p0, "\ucc28\ub7c9\uc774 \uad00\uce21\ud55c \uc774\ub3d9 \uacbd\ub85c\uc640 \uc8fc\ucc28 \uc704\uce58\ub85c \ub0a8\uae34 \uacbd\ud5d8\uc774\uc5d0\uc694. \ubc29\ubb38 \ubaa9\uc801\uc774\ub098 \ub3d9\ud589\uc790\ub294 \ucd94\uce21\ud558\uc9c0 \uc54a\uc544\uc694."

    goto/16 :goto_3

    .line 57
    :cond_2
    invoke-virtual {p0}, Lio/memorydrive/pleos/Memory;->getMusicProvider()Ljava/lang/String;

    move-result-object v0

    invoke-static {v0, v1}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_3

    const-string p0, "\uc0ac\uc9c4\uc5d0 \ub0a8\uc740 \ub0a0\uc9dc\uc640 \uc7a5\uc18c\ub85c \uad6c\uc131\ud55c \uae30\uc5b5\uc774\uc5d0\uc694. \uc5f0\uacb0\ub41c \uccad\ucde8 \uae30\ub85d\uc740 \uc5c6\uc5b4\uc694."

    goto/16 :goto_3

    .line 58
    :cond_3
    invoke-virtual {p0}, Lio/memorydrive/pleos/Memory;->getMusicEvidence()Lio/memorydrive/pleos/MusicEvidence;

    move-result-object v0

    const/4 v1, 0x0

    if-eqz v0, :cond_4

    invoke-virtual {v0}, Lio/memorydrive/pleos/MusicEvidence;->getSource()Ljava/lang/String;

    move-result-object v0

    goto :goto_0

    :cond_4
    move-object v0, v1

    :goto_0
    const-string v2, "vehicle_playback"

    invoke-static {v0, v2}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_7

    invoke-virtual {p0}, Lio/memorydrive/pleos/Memory;->getMusicEvidence()Lio/memorydrive/pleos/MusicEvidence;

    move-result-object p0

    invoke-virtual {p0}, Lio/memorydrive/pleos/MusicEvidence;->getExplanation()Ljava/lang/String;

    move-result-object p0

    if-eqz p0, :cond_6

    move-object v0, p0

    check-cast v0, Ljava/lang/CharSequence;

    invoke-static {v0}, Lkotlin/text/StringsKt;->isBlank(Ljava/lang/CharSequence;)Z

    move-result v0

    if-nez v0, :cond_5

    goto :goto_1

    :cond_5
    move-object p0, v1

    :goto_1
    if-nez p0, :cond_d

    .line 59
    :cond_6
    const-string p0, "\uc5f0\uacb0\ub41c \uc74c\uc545 \uc571\uc5d0\uc11c \uac19\uc740 \uace1\uc758 \uc7ac\uc0dd \uc704\uce58\uac00 \uc9c4\ud589\ud55c \uac83\uc744 \ucc28\ub7c9\uc5d0\uc11c \ud655\uc778\ud588\uc5b4\uc694. \uc804\uccb4 \uace1\uc744 \ub05d\uae4c\uc9c0 \ub4e4\uc5c8\ub2e4\ub294 \ub73b\uc740 \uc544\ub2c8\uc5d0\uc694."

    goto/16 :goto_3

    .line 60
    :cond_7
    invoke-virtual {p0}, Lio/memorydrive/pleos/Memory;->getMusicEvidence()Lio/memorydrive/pleos/MusicEvidence;

    move-result-object v0

    if-eqz v0, :cond_8

    invoke-virtual {p0}, Lio/memorydrive/pleos/Memory;->getMusicAssociation()Ljava/lang/String;

    move-result-object v0

    const-string v2, "temporal_history"

    invoke-static {v0, v2}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_8

    .line 61
    invoke-virtual {p0}, Lio/memorydrive/pleos/Memory;->getMusicProvider()Ljava/lang/String;

    move-result-object p0

    new-instance v0, Ljava/lang/StringBuilder;

    const-string v1, "\uc0ac\uc9c4 \ucd2c\uc601 \uc2dc\uac04\ub300\uc640 \uac00\uae4c\uc6b4 "

    invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object p0

    const-string v0, " \uccad\ucde8 \uae30\ub85d\uc5d0\uc11c \ucc3e\uc558\uc5b4\uc694. \uc774 \uc7a5\uc18c\uc5d0\uc11c \ub4e4\uc5c8\ub2e4\ub294 \ub73b\uc740 \uc544\ub2c8\uc5d0\uc694."

    invoke-virtual {p0, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object p0

    invoke-virtual {p0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    goto :goto_3

    .line 62
    :cond_8
    invoke-virtual {p0}, Lio/memorydrive/pleos/Memory;->getMusicEvidence()Lio/memorydrive/pleos/MusicEvidence;

    move-result-object v0

    if-eqz v0, :cond_9

    invoke-virtual {p0}, Lio/memorydrive/pleos/Memory;->getMusicAssociation()Ljava/lang/String;

    move-result-object v0

    const-string v2, "period_history"

    invoke-static {v0, v2}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_9

    .line 63
    invoke-virtual {p0}, Lio/memorydrive/pleos/Memory;->getMusicProvider()Ljava/lang/String;

    move-result-object p0

    new-instance v0, Ljava/lang/StringBuilder;

    const-string v1, "\uc0ac\uc9c4\uc744 \ub0a8\uae34 \uc2dc\uae30 \uc804\ud6c4\uc5d0 \uc5ec\ub7ec \ub0a0 \ubc18\ubcf5\ud574 \ub4e4\uc740 "

    invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object p0

    const-string v0, " \uace1\uc774\uc5d0\uc694. \ucd2c\uc601 \ub2f9\uc77c\uc5d0 \ub4e4\uc5c8\ub2e4\uace0 \ub2e8\uc815\ud558\uc9c0 \uc54a\uc544\uc694."

    invoke-virtual {p0, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object p0

    invoke-virtual {p0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    goto :goto_3

    .line 64
    :cond_9
    invoke-virtual {p0}, Lio/memorydrive/pleos/Memory;->getMusicAssociation()Ljava/lang/String;

    move-result-object v0

    const-string v2, "recommendation"

    invoke-static {v0, v2}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_c

    invoke-virtual {p0}, Lio/memorydrive/pleos/Memory;->getMusicEvidence()Lio/memorydrive/pleos/MusicEvidence;

    move-result-object p0

    if-eqz p0, :cond_b

    invoke-virtual {p0}, Lio/memorydrive/pleos/MusicEvidence;->getExplanation()Ljava/lang/String;

    move-result-object p0

    if-eqz p0, :cond_b

    move-object v0, p0

    check-cast v0, Ljava/lang/CharSequence;

    invoke-static {v0}, Lkotlin/text/StringsKt;->isBlank(Ljava/lang/CharSequence;)Z

    move-result v0

    if-nez v0, :cond_a

    goto :goto_2

    :cond_a
    move-object p0, v1

    :goto_2
    if-nez p0, :cond_d

    .line 65
    :cond_b
    const-string p0, "\ud568\uaed8 \ub4e3\ub3c4\ub85d \ucd94\ucc9c\ud55c \uace1\uc774\uc5d0\uc694. \ub2f9\uc2dc\uc758 \uccad\ucde8 \uae30\ub85d\uc740 \uc544\ub2d9\ub2c8\ub2e4."

    goto :goto_3

    .line 66
    :cond_c
    const-string p0, "\uc774 \uc7a5\uba74\uc5d0 \uc9c1\uc811 \uc5f0\uacb0\ud55c \uace1\uc774\uc5d0\uc694. \uc790\ub3d9\uc73c\ub85c \ud655\uc778\ud55c \ub2f9\uc2dc \uccad\ucde8 \uae30\ub85d\uacfc\ub294 \uad6c\ubd84\ud574\uc694."

    :cond_d
    :goto_3
    return-object p0
.end method

.method public static final musicConnectionLabel(Lio/memorydrive/pleos/Memory;)Ljava/lang/String;
    .locals 2

    const-string v0, "memory"

    invoke-static {p0, v0}, Lkotlin/jvm/internal/Intrinsics;->checkNotNullParameter(Ljava/lang/Object;Ljava/lang/String;)V

    .line 43
    invoke-virtual {p0}, Lio/memorydrive/pleos/Memory;->getImported()Z

    move-result v0

    if-nez v0, :cond_0

    const-string p0, "\uc568\ubc94\uc5d0 \ub2f4\uae34 \uc74c\uc545"

    goto/16 :goto_1

    .line 44
    :cond_0
    invoke-virtual {p0}, Lio/memorydrive/pleos/Memory;->getMusicAssociation()Ljava/lang/String;

    move-result-object v0

    const-string v1, "shared_course"

    invoke-static {v0, v1}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v0

    const-string v1, "none"

    if-eqz v0, :cond_2

    invoke-virtual {p0}, Lio/memorydrive/pleos/Memory;->getMusicProvider()Ljava/lang/String;

    move-result-object p0

    invoke-static {p0, v1}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result p0

    if-eqz p0, :cond_1

    const-string p0, "\uc7a5\uc18c \uc774\uc57c\uae30"

    goto :goto_1

    :cond_1
    const-string p0, "\ucf54\uc2a4\uc5d0 \ub2f4\uae34 \uace1"

    goto :goto_1

    .line 45
    :cond_2
    invoke-virtual {p0}, Lio/memorydrive/pleos/Memory;->getMusicProvider()Ljava/lang/String;

    move-result-object v0

    invoke-static {v0, v1}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_3

    const-string p0, "\uc5f0\uacb0\ub41c \uccad\ucde8 \uae30\ub85d \uc5c6\uc74c"

    goto :goto_1

    .line 46
    :cond_3
    invoke-virtual {p0}, Lio/memorydrive/pleos/Memory;->getMusicEvidence()Lio/memorydrive/pleos/MusicEvidence;

    move-result-object v0

    if-eqz v0, :cond_4

    invoke-virtual {v0}, Lio/memorydrive/pleos/MusicEvidence;->getSource()Ljava/lang/String;

    move-result-object v0

    goto :goto_0

    :cond_4
    const/4 v0, 0x0

    :goto_0
    const-string v1, "vehicle_playback"

    invoke-static {v0, v1}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_5

    const-string p0, "\uc774\ub3d9 \uc911 \uc7ac\uc0dd\uc744 \ud655\uc778\ud55c \uace1"

    goto :goto_1

    .line 47
    :cond_5
    invoke-virtual {p0}, Lio/memorydrive/pleos/Memory;->getMusicEvidence()Lio/memorydrive/pleos/MusicEvidence;

    move-result-object v0

    if-eqz v0, :cond_6

    invoke-virtual {p0}, Lio/memorydrive/pleos/Memory;->getMusicAssociation()Ljava/lang/String;

    move-result-object v0

    const-string v1, "temporal_history"

    invoke-static {v0, v1}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_6

    const-string p0, "\ucd2c\uc601 \uc2dc\uac04\ub300\uc5d0 \ub4e4\uc740 \uace1"

    goto :goto_1

    .line 48
    :cond_6
    invoke-virtual {p0}, Lio/memorydrive/pleos/Memory;->getMusicEvidence()Lio/memorydrive/pleos/MusicEvidence;

    move-result-object v0

    if-eqz v0, :cond_7

    invoke-virtual {p0}, Lio/memorydrive/pleos/Memory;->getMusicAssociation()Ljava/lang/String;

    move-result-object v0

    const-string v1, "period_history"

    invoke-static {v0, v1}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_7

    const-string p0, "\uadf8 \uc2dc\uae30\uc5d0 \uc790\uc8fc \ub4e4\uc740 \uace1"

    goto :goto_1

    .line 49
    :cond_7
    invoke-virtual {p0}, Lio/memorydrive/pleos/Memory;->getMusicAssociation()Ljava/lang/String;

    move-result-object p0

    const-string v0, "recommendation"

    invoke-static {p0, v0}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result p0

    if-eqz p0, :cond_8

    const-string p0, "\uc774 \uc7a5\uba74\uc5d0 \ucd94\ucc9c\ud55c \uace1"

    goto :goto_1

    .line 50
    :cond_8
    const-string p0, "\uc9c1\uc811 \uc5f0\uacb0\ud55c \uace1"

    :goto_1
    return-object p0
.end method

.method public static final nearbyDistance(D)Ljava/lang/String;
    .locals 3

    const-wide v0, 0x408f400000000000L    # 1000.0

    cmpg-double v0, p0, v0

    if-gez v0, :cond_0

    double-to-int p0, p0

    .line 40
    div-int/lit8 p0, p0, 0xa

    mul-int/lit8 p0, p0, 0xa

    new-instance p1, Ljava/lang/StringBuilder;

    invoke-direct {p1}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {p1, p0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object p0

    const-string p1, "m"

    invoke-virtual {p0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object p0

    invoke-virtual {p0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    goto :goto_0

    :cond_0
    sget-object v0, Lkotlin/jvm/internal/StringCompanionObject;->INSTANCE:Lkotlin/jvm/internal/StringCompanionObject;

    sget-object v0, Ljava/util/Locale;->KOREA:Ljava/util/Locale;

    const/16 v1, 0x3e8

    int-to-double v1, v1

    div-double/2addr p0, v1

    invoke-static {p0, p1}, Ljava/lang/Double;->valueOf(D)Ljava/lang/Double;

    move-result-object p0

    filled-new-array {p0}, [Ljava/lang/Object;

    move-result-object p0

    const/4 p1, 0x1

    invoke-static {p0, p1}, Ljava/util/Arrays;->copyOf([Ljava/lang/Object;I)[Ljava/lang/Object;

    move-result-object p0

    const-string p1, "%.1fkm"

    invoke-static {v0, p1, p0}, Ljava/lang/String;->format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object p0

    const-string p1, "format(...)"

    invoke-static {p0, p1}, Lkotlin/jvm/internal/Intrinsics;->checkNotNullExpressionValue(Ljava/lang/Object;Ljava/lang/String;)V

    :goto_0
    return-object p0
.end method
