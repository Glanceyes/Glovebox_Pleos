.class public final Lio/memorydrive/pleos/MemoryOfferPolicy;
.super Ljava/lang/Object;
.source "MemoryJourney.kt"


# annotations
.annotation system Ldalvik/annotation/SourceDebugExtension;
    value = "SMAP\nMemoryJourney.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MemoryJourney.kt\nio/memorydrive/pleos/MemoryOfferPolicy\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,147:1\n1#2:148\n774#3:149\n865#3,2:150\n1755#3,3:152\n774#3:155\n865#3,2:156\n1557#3:158\n1628#3,3:159\n*S KotlinDebug\n*F\n+ 1 MemoryJourney.kt\nio/memorydrive/pleos/MemoryOfferPolicy\n*L\n135#1:149\n135#1:150,2\n138#1:152,3\n139#1:155\n139#1:156,2\n140#1:158\n140#1:159,3\n*E\n"
.end annotation

.annotation runtime Lkotlin/Metadata;
    d1 = {
        "\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0008\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\u0008\u00c7\u0002\u0018\u00002\u00020\u0001B\t\u0008\u0003\u00a2\u0006\u0004\u0008\u0002\u0010\u0003J \u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0008\u0010\u0008\u001a\u0004\u0018\u00010\t2\u0006\u0010\n\u001a\u00020\u000bJ\u000e\u0010\u000c\u001a\u00020\r2\u0006\u0010\u0006\u001a\u00020\u0007\u00a8\u0006\u000e"
    }
    d2 = {
        "Lio/memorydrive/pleos/MemoryOfferPolicy;",
        "",
        "<init>",
        "()V",
        "shouldAnnounce",
        "",
        "memory",
        "Lio/memorydrive/pleos/Memory;",
        "journey",
        "Lio/memorydrive/pleos/MemoryJourney;",
        "context",
        "Lio/memorydrive/pleos/MemoryOfferContext;",
        "announcement",
        "",
        "app_release"
    }
    k = 0x1
    mv = {
        0x2,
        0x0,
        0x0
    }
    xi = 0x30
.end annotation


# static fields
.field public static final $stable:I

.field public static final INSTANCE:Lio/memorydrive/pleos/MemoryOfferPolicy;


# direct methods
.method static constructor <clinit>()V
    .locals 1

    new-instance v0, Lio/memorydrive/pleos/MemoryOfferPolicy;

    invoke-direct {v0}, Lio/memorydrive/pleos/MemoryOfferPolicy;-><init>()V

    sput-object v0, Lio/memorydrive/pleos/MemoryOfferPolicy;->INSTANCE:Lio/memorydrive/pleos/MemoryOfferPolicy;

    return-void
.end method

.method private constructor <init>()V
    .locals 0

    .line 113
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public final announcement(Lio/memorydrive/pleos/Memory;)Ljava/lang/String;
    .locals 14

    const-string v0, "memory"

    invoke-static {p1, v0}, Lkotlin/jvm/internal/Intrinsics;->checkNotNullParameter(Ljava/lang/Object;Ljava/lang/String;)V

    .line 125
    sget-object v1, Lio/memorydrive/pleos/MemoryRoutePolicy;->INSTANCE:Lio/memorydrive/pleos/MemoryRoutePolicy;

    const/4 v5, 0x2

    const/4 v6, 0x0

    const-wide/16 v3, 0x0

    move-object v2, p1

    invoke-static/range {v1 .. v6}, Lio/memorydrive/pleos/MemoryRoutePolicy;->verifiedVehicle$default(Lio/memorydrive/pleos/MemoryRoutePolicy;Lio/memorydrive/pleos/Memory;JILjava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_1

    .line 126
    sget-object v1, Lio/memorydrive/pleos/MemoryRoutePolicy;->INSTANCE:Lio/memorydrive/pleos/MemoryRoutePolicy;

    const/4 v5, 0x2

    const/4 v6, 0x0

    const-wide/16 v3, 0x0

    move-object v2, p1

    invoke-static/range {v1 .. v6}, Lio/memorydrive/pleos/MemoryRoutePolicy;->verifiedVehiclePlayback$default(Lio/memorydrive/pleos/MemoryRoutePolicy;Lio/memorydrive/pleos/Memory;JILjava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_0

    const-string p1, " \uadf8\ub54c \ucc28\uc5d0\uc11c \uc7ac\uc0dd\ud55c \uace1\ub3c4 \uc788\uc5b4\uc694."

    goto :goto_0

    .line 127
    :cond_0
    const-string p1, ""

    :goto_0
    const-string v0, "\uc774 \uadfc\ucc98\uc5d0 \ucc28\ub97c \uc138\uc6e0\ub358 \uae30\ub85d\uc774 \uc788\uc5b4\uc694."

    invoke-virtual {v0, p1}, Ljava/lang/String;->concat(Ljava/lang/String;)Ljava/lang/String;

    move-result-object p1

    return-object p1

    .line 128
    :cond_1
    invoke-virtual {p1}, Lio/memorydrive/pleos/Memory;->getMusicEvidence()Lio/memorydrive/pleos/MusicEvidence;

    move-result-object v0

    .line 129
    invoke-virtual {p1}, Lio/memorydrive/pleos/Memory;->getCapturedAt()Ljava/lang/Double;

    move-result-object v1

    const/4 v2, 0x1

    const/4 v3, 0x0

    if-eqz v0, :cond_4

    .line 130
    invoke-virtual {v0}, Lio/memorydrive/pleos/MusicEvidence;->getProvider()Ljava/lang/String;

    move-result-object v4

    .line 131
    const-string v5, "YouTube Music"

    invoke-static {v4, v5}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v5

    if-eqz v5, :cond_2

    new-instance v4, Lkotlin/text/Regex;

    const-string v5, "[A-Za-z0-9_-]{11}"

    invoke-direct {v4, v5}, Lkotlin/text/Regex;-><init>(Ljava/lang/String;)V

    invoke-virtual {v0}, Lio/memorydrive/pleos/MusicEvidence;->getTrackID()Ljava/lang/String;

    move-result-object v5

    check-cast v5, Ljava/lang/CharSequence;

    invoke-virtual {v4, v5}, Lkotlin/text/Regex;->matches(Ljava/lang/CharSequence;)Z

    move-result v4

    goto :goto_1

    .line 132
    :cond_2
    const-string v5, "Spotify"

    invoke-static {v4, v5}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v4

    if-eqz v4, :cond_3

    new-instance v4, Lkotlin/text/Regex;

    const-string v5, "[A-Za-z0-9]{22}"

    invoke-direct {v4, v5}, Lkotlin/text/Regex;-><init>(Ljava/lang/String;)V

    invoke-virtual {v0}, Lio/memorydrive/pleos/MusicEvidence;->getTrackID()Ljava/lang/String;

    move-result-object v5

    check-cast v5, Ljava/lang/CharSequence;

    invoke-virtual {v4, v5}, Lkotlin/text/Regex;->matches(Ljava/lang/CharSequence;)Z

    move-result v4

    goto :goto_1

    :cond_3
    move v4, v3

    :goto_1
    if-eqz v4, :cond_4

    move v4, v2

    goto :goto_2

    :cond_4
    move v4, v3

    :goto_2
    const/4 v5, 0x0

    if-eqz v0, :cond_5

    .line 135
    invoke-virtual {v0}, Lio/memorydrive/pleos/MusicEvidence;->getEventTimes()Ljava/util/List;

    move-result-object v6

    goto :goto_3

    :cond_5
    move-object v6, v5

    :goto_3
    if-nez v6, :cond_6

    invoke-static {}, Lkotlin/collections/CollectionsKt;->emptyList()Ljava/util/List;

    move-result-object v6

    :cond_6
    check-cast v6, Ljava/lang/Iterable;

    .line 149
    new-instance v7, Ljava/util/ArrayList;

    invoke-direct {v7}, Ljava/util/ArrayList;-><init>()V

    check-cast v7, Ljava/util/Collection;

    .line 150
    invoke-interface {v6}, Ljava/lang/Iterable;->iterator()Ljava/util/Iterator;

    move-result-object v6

    :cond_7
    :goto_4
    invoke-interface {v6}, Ljava/util/Iterator;->hasNext()Z

    move-result v8

    if-eqz v8, :cond_a

    invoke-interface {v6}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v8

    move-object v9, v8

    check-cast v9, Ljava/lang/Number;

    invoke-virtual {v9}, Ljava/lang/Number;->doubleValue()D

    move-result-wide v9

    .line 135
    invoke-static {v9, v10}, Ljava/lang/Double;->isInfinite(D)Z

    move-result v11

    if-nez v11, :cond_8

    invoke-static {v9, v10}, Ljava/lang/Double;->isNaN(D)Z

    move-result v11

    if-nez v11, :cond_8

    move v11, v2

    goto :goto_5

    :cond_8
    move v11, v3

    :goto_5
    if-eqz v11, :cond_9

    const-wide/16 v11, 0x0

    cmpl-double v9, v9, v11

    if-ltz v9, :cond_9

    move v9, v2

    goto :goto_6

    :cond_9
    move v9, v3

    :goto_6
    if-eqz v9, :cond_7

    .line 150
    invoke-interface {v7, v8}, Ljava/util/Collection;->add(Ljava/lang/Object;)Z

    goto :goto_4

    .line 151
    :cond_a
    check-cast v7, Ljava/util/List;

    if-eqz v4, :cond_16

    if-eqz v0, :cond_b

    .line 136
    invoke-virtual {v0}, Lio/memorydrive/pleos/MusicEvidence;->getProvider()Ljava/lang/String;

    move-result-object v5

    :cond_b
    invoke-virtual {p1}, Lio/memorydrive/pleos/Memory;->getMusicProvider()Ljava/lang/String;

    move-result-object v0

    invoke-static {v5, v0}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_16

    if-eqz v1, :cond_16

    invoke-virtual {v1}, Ljava/lang/Double;->doubleValue()D

    move-result-wide v4

    invoke-static {v4, v5}, Ljava/lang/Double;->isInfinite(D)Z

    move-result v0

    if-nez v0, :cond_c

    invoke-static {v4, v5}, Ljava/lang/Double;->isNaN(D)Z

    move-result v0

    if-nez v0, :cond_c

    move v0, v2

    goto :goto_7

    :cond_c
    move v0, v3

    :goto_7
    if-eqz v0, :cond_16

    .line 137
    invoke-virtual {p1}, Lio/memorydrive/pleos/Memory;->getMusicAssociation()Ljava/lang/String;

    move-result-object p1

    .line 138
    const-string v0, "temporal_history"

    invoke-static {p1, v0}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v0

    const/16 v4, 0x7080

    if-eqz v0, :cond_10

    check-cast v7, Ljava/lang/Iterable;

    .line 152
    instance-of p1, v7, Ljava/util/Collection;

    if-eqz p1, :cond_d

    move-object p1, v7

    check-cast p1, Ljava/util/Collection;

    invoke-interface {p1}, Ljava/util/Collection;->isEmpty()Z

    move-result p1

    if-eqz p1, :cond_d

    goto/16 :goto_d

    .line 153
    :cond_d
    invoke-interface {v7}, Ljava/lang/Iterable;->iterator()Ljava/util/Iterator;

    move-result-object p1

    :cond_e
    invoke-interface {p1}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_15

    invoke-interface {p1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/lang/Number;

    invoke-virtual {v0}, Ljava/lang/Number;->doubleValue()D

    move-result-wide v5

    .line 138
    invoke-virtual {v1}, Ljava/lang/Double;->doubleValue()D

    move-result-wide v7

    int-to-double v9, v4

    sub-double/2addr v7, v9

    const/16 v0, 0x384

    int-to-double v9, v0

    sub-double/2addr v7, v9

    cmpl-double v0, v5, v7

    if-ltz v0, :cond_f

    invoke-virtual {v1}, Ljava/lang/Double;->doubleValue()D

    move-result-wide v7

    add-double/2addr v7, v9

    cmpg-double v0, v5, v7

    if-gtz v0, :cond_f

    move v0, v2

    goto :goto_8

    :cond_f
    move v0, v3

    :goto_8
    if-eqz v0, :cond_e

    goto/16 :goto_c

    .line 139
    :cond_10
    const-string v0, "period_history"

    invoke-static {p1, v0}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_15

    check-cast v7, Ljava/lang/Iterable;

    .line 155
    new-instance p1, Ljava/util/ArrayList;

    invoke-direct {p1}, Ljava/util/ArrayList;-><init>()V

    check-cast p1, Ljava/util/Collection;

    .line 156
    invoke-interface {v7}, Ljava/lang/Iterable;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :cond_11
    :goto_9
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v5

    if-eqz v5, :cond_13

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v5

    move-object v6, v5

    check-cast v6, Ljava/lang/Number;

    invoke-virtual {v6}, Ljava/lang/Number;->doubleValue()D

    move-result-wide v6

    .line 139
    invoke-virtual {v1}, Ljava/lang/Double;->doubleValue()D

    move-result-wide v8

    const v10, 0x278d00

    int-to-double v10, v10

    sub-double/2addr v8, v10

    int-to-double v12, v4

    sub-double/2addr v8, v12

    cmpl-double v8, v6, v8

    if-ltz v8, :cond_12

    invoke-virtual {v1}, Ljava/lang/Double;->doubleValue()D

    move-result-wide v8

    add-double/2addr v8, v10

    cmpg-double v6, v6, v8

    if-gtz v6, :cond_12

    move v6, v2

    goto :goto_a

    :cond_12
    move v6, v3

    :goto_a
    if-eqz v6, :cond_11

    .line 156
    invoke-interface {p1, v5}, Ljava/util/Collection;->add(Ljava/lang/Object;)Z

    goto :goto_9

    .line 157
    :cond_13
    check-cast p1, Ljava/util/List;

    .line 155
    check-cast p1, Ljava/lang/Iterable;

    .line 158
    new-instance v0, Ljava/util/ArrayList;

    const/16 v1, 0xa

    invoke-static {p1, v1}, Lkotlin/collections/CollectionsKt;->collectionSizeOrDefault(Ljava/lang/Iterable;I)I

    move-result v1

    invoke-direct {v0, v1}, Ljava/util/ArrayList;-><init>(I)V

    check-cast v0, Ljava/util/Collection;

    .line 159
    invoke-interface {p1}, Ljava/lang/Iterable;->iterator()Ljava/util/Iterator;

    move-result-object p1

    :goto_b
    invoke-interface {p1}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_14

    invoke-interface {p1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    .line 160
    check-cast v1, Ljava/lang/Number;

    invoke-virtual {v1}, Ljava/lang/Number;->doubleValue()D

    move-result-wide v4

    const v1, 0x15180

    int-to-double v6, v1

    div-double/2addr v4, v6

    .line 140
    invoke-static {v4, v5}, Ljava/lang/Math;->floor(D)D

    move-result-wide v4

    double-to-long v4, v4

    invoke-static {v4, v5}, Ljava/lang/Long;->valueOf(J)Ljava/lang/Long;

    move-result-object v1

    .line 160
    invoke-interface {v0, v1}, Ljava/util/Collection;->add(Ljava/lang/Object;)Z

    goto :goto_b

    .line 161
    :cond_14
    check-cast v0, Ljava/util/List;

    .line 158
    check-cast v0, Ljava/lang/Iterable;

    .line 140
    invoke-static {v0}, Lkotlin/collections/CollectionsKt;->distinct(Ljava/lang/Iterable;)Ljava/util/List;

    move-result-object p1

    invoke-interface {p1}, Ljava/util/List;->size()I

    move-result p1

    const/4 v0, 0x3

    if-lt p1, v0, :cond_15

    :goto_c
    move p1, v2

    goto :goto_e

    :cond_15
    :goto_d
    move p1, v3

    :goto_e
    if-eqz p1, :cond_16

    goto :goto_f

    :cond_16
    move v2, v3

    :goto_f
    if-eqz v2, :cond_17

    .line 144
    const-string p1, " \uadf8 \uc2dc\uae30\uc758 \uc74c\uc545\ub3c4 \ucc3e\uc558\uc5b4\uc694."

    goto :goto_10

    :cond_17
    const-string p1, ""

    :goto_10
    const-string v0, "\uc774 \uadfc\ucc98\uc5d0 \uc0ac\uc9c4\uc73c\ub85c \ub0a8\uae34 \uae30\uc5b5\uc774 \uc788\uc5b4\uc694."

    invoke-virtual {v0, p1}, Ljava/lang/String;->concat(Ljava/lang/String;)Ljava/lang/String;

    move-result-object p1

    return-object p1
.end method

.method public final shouldAnnounce(Lio/memorydrive/pleos/Memory;Lio/memorydrive/pleos/MemoryJourney;Lio/memorydrive/pleos/MemoryOfferContext;)Z
    .locals 6

    const-string v0, "memory"

    invoke-static {p1, v0}, Lkotlin/jvm/internal/Intrinsics;->checkNotNullParameter(Ljava/lang/Object;Ljava/lang/String;)V

    const-string v0, "context"

    invoke-static {p3, v0}, Lkotlin/jvm/internal/Intrinsics;->checkNotNullParameter(Ljava/lang/Object;Ljava/lang/String;)V

    .line 115
    invoke-virtual {p1}, Lio/memorydrive/pleos/Memory;->getImported()Z

    move-result v0

    if-eqz v0, :cond_2

    invoke-virtual {p1}, Lio/memorydrive/pleos/Memory;->getAutomatic()Z

    move-result v0

    if-eqz v0, :cond_2

    .line 116
    invoke-virtual {p1}, Lio/memorydrive/pleos/Memory;->getLocationSource()Ljava/lang/String;

    move-result-object v0

    const-string v1, "photo"

    invoke-static {v0, v1}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_0

    invoke-virtual {p1}, Lio/memorydrive/pleos/Memory;->getDateSource()Ljava/lang/String;

    move-result-object v0

    invoke-static {v0, v1}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v0

    if-nez v0, :cond_1

    .line 117
    :cond_0
    sget-object v0, Lio/memorydrive/pleos/MemoryRoutePolicy;->INSTANCE:Lio/memorydrive/pleos/MemoryRoutePolicy;

    invoke-virtual {p3}, Lio/memorydrive/pleos/MemoryOfferContext;->getNow()J

    move-result-wide v1

    invoke-virtual {v0, p1, v1, v2}, Lio/memorydrive/pleos/MemoryRoutePolicy;->vehicleReadyForRediscovery(Lio/memorydrive/pleos/Memory;J)Z

    move-result v0

    if-eqz v0, :cond_2

    .line 118
    :cond_1
    invoke-virtual {p1}, Lio/memorydrive/pleos/Memory;->getCapturedAt()Ljava/lang/Double;

    move-result-object v0

    if-eqz v0, :cond_2

    check-cast v0, Ljava/lang/Number;

    invoke-virtual {v0}, Ljava/lang/Number;->doubleValue()D

    move-result-wide v0

    invoke-static {v0, v1}, Ljava/lang/Double;->isInfinite(D)Z

    move-result v2

    if-nez v2, :cond_2

    invoke-static {v0, v1}, Ljava/lang/Double;->isNaN(D)Z

    move-result v2

    if-nez v2, :cond_2

    const-wide/16 v2, 0x0

    cmpl-double v2, v0, v2

    if-ltz v2, :cond_2

    invoke-virtual {p3}, Lio/memorydrive/pleos/MemoryOfferContext;->getNow()J

    move-result-wide v2

    long-to-double v2, v2

    const-wide v4, 0x408f400000000000L    # 1000.0

    div-double/2addr v2, v4

    cmpg-double v0, v0, v2

    if-gtz v0, :cond_2

    if-eqz p2, :cond_2

    .line 119
    invoke-virtual {p2}, Lio/memorydrive/pleos/MemoryJourney;->getMemoryId()Ljava/lang/String;

    move-result-object v0

    invoke-virtual {p1}, Lio/memorydrive/pleos/Memory;->getId()Ljava/lang/String;

    move-result-object p1

    invoke-static {v0, p1}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_2

    invoke-virtual {p2}, Lio/memorydrive/pleos/MemoryJourney;->getFromLocation()Z

    move-result p1

    if-eqz p1, :cond_2

    invoke-virtual {p2}, Lio/memorydrive/pleos/MemoryJourney;->getOfferAnnounced()Z

    move-result p1

    if-nez p1, :cond_2

    .line 120
    invoke-virtual {p2}, Lio/memorydrive/pleos/MemoryJourney;->getNarrated()Z

    move-result p1

    if-nez p1, :cond_2

    invoke-virtual {p2}, Lio/memorydrive/pleos/MemoryJourney;->getPhase()Lio/memorydrive/pleos/JourneyPhase;

    move-result-object p1

    sget-object v0, Lio/memorydrive/pleos/JourneyPhase;->OFFERED:Lio/memorydrive/pleos/JourneyPhase;

    if-ne p1, v0, :cond_2

    invoke-virtual {p2}, Lio/memorydrive/pleos/MemoryJourney;->getDiscoveredAt()J

    move-result-wide v0

    const-wide/16 v2, 0x0

    cmp-long p1, v0, v2

    if-lez p1, :cond_2

    .line 121
    invoke-virtual {p3}, Lio/memorydrive/pleos/MemoryOfferContext;->getNow()J

    move-result-wide v0

    invoke-virtual {p2}, Lio/memorydrive/pleos/MemoryJourney;->getDiscoveredAt()J

    move-result-wide p1

    sub-long/2addr v0, p1

    cmp-long p1, v2, v0

    if-gtz p1, :cond_2

    const-wide/16 p1, 0x3a99

    cmp-long p1, v0, p1

    if-gez p1, :cond_2

    invoke-virtual {p3}, Lio/memorydrive/pleos/MemoryOfferContext;->getFreshAndNear()Z

    move-result p1

    if-eqz p1, :cond_2

    invoke-virtual {p3}, Lio/memorydrive/pleos/MemoryOfferContext;->getTtsReady()Z

    move-result p1

    if-eqz p1, :cond_2

    .line 122
    invoke-virtual {p3}, Lio/memorydrive/pleos/MemoryOfferContext;->getQuiet()Z

    move-result p1

    if-nez p1, :cond_2

    invoke-virtual {p3}, Lio/memorydrive/pleos/MemoryOfferContext;->getBusy()Z

    move-result p1

    if-nez p1, :cond_2

    invoke-virtual {p3}, Lio/memorydrive/pleos/MemoryOfferContext;->getAudioIdle()Z

    move-result p1

    if-eqz p1, :cond_2

    invoke-virtual {p3}, Lio/memorydrive/pleos/MemoryOfferContext;->getActiveCourse()Z

    move-result p1

    if-nez p1, :cond_2

    invoke-virtual {p3}, Lio/memorydrive/pleos/MemoryOfferContext;->getVehicleKnown()Z

    move-result p1

    if-eqz p1, :cond_2

    const/4 p1, 0x1

    goto :goto_0

    :cond_2
    const/4 p1, 0x0

    :goto_0
    return p1
.end method
