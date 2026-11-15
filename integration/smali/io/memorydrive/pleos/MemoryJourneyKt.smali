.class public final Lio/memorydrive/pleos/MemoryJourneyKt;
.super Ljava/lang/Object;
.source "MemoryJourney.kt"


# annotations
.annotation system Ldalvik/annotation/SourceDebugExtension;
    value = "SMAP\nMemoryJourney.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MemoryJourney.kt\nio/memorydrive/pleos/MemoryJourneyKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,147:1\n1#2:148\n1557#3:149\n1628#3,3:150\n774#3:153\n865#3,2:154\n774#3:156\n865#3,2:157\n*S KotlinDebug\n*F\n+ 1 MemoryJourney.kt\nio/memorydrive/pleos/MemoryJourneyKt\n*L\n58#1:149\n58#1:150,3\n58#1:153\n58#1:154,2\n88#1:156\n88#1:157,2\n*E\n"
.end annotation

.annotation runtime Lkotlin/Metadata;
    d1 = {
        "\u0000\u001e\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\u0008\u0006\n\u0002\u0010\u0008\n\u0002\u0008\u0003\u001a4\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0008\u0008\u0002\u0010\u0006\u001a\u00020\u00052\u0008\u0008\u0002\u0010\u0007\u001a\u00020\u00052\u0008\u0008\u0002\u0010\u0008\u001a\u00020\u0005\u001a\u0018\u0010\t\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u00012\u0006\u0010\u000b\u001a\u00020\u000cH\u0002\u001a\u0012\u0010\r\u001a\u00020\u0005*\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u0003\u00a8\u0006\u000f"
    }
    d2 = {
        "memoryNarration",
        "",
        "memory",
        "Lio/memorydrive/pleos/Memory;",
        "includeMusic",
        "",
        "parked",
        "nearby",
        "externalCurrentTrack",
        "spokenRecordExcerpt",
        "story",
        "limit",
        "",
        "sameExperienceContent",
        "other",
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
.method public static final memoryNarration(Lio/memorydrive/pleos/Memory;ZZZZ)Ljava/lang/String;
    .locals 26

    const-string v0, "memory"

    move-object/from16 v7, p0

    invoke-static {v7, v0}, Lkotlin/jvm/internal/Intrinsics;->checkNotNullParameter(Ljava/lang/Object;Ljava/lang/String;)V

    .line 38
    invoke-virtual/range {p0 .. p0}, Lio/memorydrive/pleos/Memory;->getLocationSource()Ljava/lang/String;

    move-result-object v0

    const-string v1, "shared_course"

    invoke-static {v0, v1}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v0

    .line 39
    invoke-virtual/range {p0 .. p0}, Lio/memorydrive/pleos/Memory;->getAutomatic()Z

    move-result v1

    const/4 v8, 0x1

    const/4 v9, 0x0

    if-eqz v1, :cond_0

    invoke-virtual/range {p0 .. p0}, Lio/memorydrive/pleos/Memory;->getLocationSource()Ljava/lang/String;

    move-result-object v1

    const-string v2, "photo"

    invoke-static {v1, v2}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v1

    if-eqz v1, :cond_0

    invoke-virtual/range {p0 .. p0}, Lio/memorydrive/pleos/Memory;->getDateSource()Ljava/lang/String;

    move-result-object v1

    invoke-static {v1, v2}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v1

    if-eqz v1, :cond_0

    move v10, v8

    goto :goto_0

    :cond_0
    move v10, v9

    .line 40
    :goto_0
    invoke-virtual/range {p0 .. p0}, Lio/memorydrive/pleos/Memory;->getLocationSource()Ljava/lang/String;

    move-result-object v1

    const-string v2, "vehicle"

    invoke-static {v1, v2}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v1

    if-nez v1, :cond_2

    invoke-virtual/range {p0 .. p0}, Lio/memorydrive/pleos/Memory;->getDateSource()Ljava/lang/String;

    move-result-object v1

    invoke-static {v1, v2}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v1

    if-eqz v1, :cond_1

    goto :goto_1

    :cond_1
    move v11, v9

    goto :goto_2

    :cond_2
    :goto_1
    move v11, v8

    .line 41
    :goto_2
    sget-object v1, Lio/memorydrive/pleos/MemoryRoutePolicy;->INSTANCE:Lio/memorydrive/pleos/MemoryRoutePolicy;

    const/4 v5, 0x2

    const/4 v6, 0x0

    const-wide/16 v3, 0x0

    move-object/from16 v2, p0

    invoke-static/range {v1 .. v6}, Lio/memorydrive/pleos/MemoryRoutePolicy;->verifiedVehicle$default(Lio/memorydrive/pleos/MemoryRoutePolicy;Lio/memorydrive/pleos/Memory;JILjava/lang/Object;)Z

    move-result v12

    .line 43
    invoke-virtual/range {p0 .. p0}, Lio/memorydrive/pleos/Memory;->getImported()Z

    move-result v1

    if-nez v1, :cond_3

    const-string v1, ""

    :goto_3
    move-object v13, v1

    goto :goto_4

    :cond_3
    if-eqz v0, :cond_4

    .line 44
    const-string v1, "\ucf54\uc2a4\ub97c \ub098\ub208 \uc0ac\ub78c\uc774 \ub0a8\uae34 \uc7a5\uc18c \uc774\uc57c\uae30\uc608\uc694."

    goto :goto_3

    :cond_4
    if-eqz v12, :cond_5

    if-eqz p3, :cond_5

    .line 45
    const-string v1, "\uc774 \uadfc\ucc98\uc5d0 \ucc28\ub97c \uc138\uc6e0\ub358 \uae30\ub85d\uc774\uc5d0\uc694."

    goto :goto_3

    :cond_5
    if-eqz v12, :cond_6

    .line 46
    const-string v1, "\ucc28\ub7c9\uc774 \ub0a8\uae34 \uc774\ub3d9 \uae30\ub85d\uc774\uc5d0\uc694."

    goto :goto_3

    :cond_6
    if-eqz v11, :cond_7

    .line 47
    const-string v1, "\uc5f0\uacb0\ub41c \uc7a5\uc18c \uae30\ub85d\uc774\uc5d0\uc694."

    goto :goto_3

    :cond_7
    if-eqz v10, :cond_8

    if-eqz p3, :cond_8

    .line 48
    const-string v1, "\uc774 \uadfc\ucc98\uc5d0 \uc0ac\uc9c4\uc73c\ub85c \ub0a8\uae34 \uae30\ub85d\uc774\uc5d0\uc694."

    goto :goto_3

    :cond_8
    if-eqz v10, :cond_9

    .line 49
    const-string v1, "\uc0ac\uc9c4\uc5d0 \ub0a8\uc740 \uae30\ub85d\uc774\uc5d0\uc694."

    goto :goto_3

    .line 50
    :cond_9
    const-string v1, "\uc9c1\uc811 \ub0a8\uae34 \uae30\ub85d\uc774\uc5d0\uc694."

    goto :goto_3

    .line 53
    :goto_4
    const-string v14, ""

    if-eqz v12, :cond_a

    const-string v1, "\uc774\ub0a0 \uc774\uacf3\uc5d0\uc11c \uc774\ub3d9\uc744 \ub9c8\uce5c \uae30\ub85d\uc774 \uc788\uc5b4\uc694."

    goto :goto_6

    :cond_a
    if-eqz v11, :cond_b

    move-object v1, v14

    goto :goto_6

    .line 55
    :cond_b
    invoke-virtual/range {p0 .. p0}, Lio/memorydrive/pleos/Memory;->getStory()Ljava/lang/String;

    move-result-object v1

    if-eqz v0, :cond_c

    const/16 v2, 0x78

    goto :goto_5

    :cond_c
    const/16 v2, 0xa0

    :goto_5
    invoke-static {v1, v2}, Lio/memorydrive/pleos/MemoryJourneyKt;->spokenRecordExcerpt(Ljava/lang/String;I)Ljava/lang/String;

    move-result-object v1

    .line 57
    :goto_6
    invoke-virtual/range {p0 .. p0}, Lio/memorydrive/pleos/Memory;->getDate()Ljava/lang/String;

    move-result-object v2

    const/4 v3, 0x0

    if-nez v0, :cond_d

    invoke-virtual/range {p0 .. p0}, Lio/memorydrive/pleos/Memory;->getDateSource()Ljava/lang/String;

    move-result-object v4

    const-string v5, "unknown"

    invoke-static {v4, v5}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v4

    if-nez v4, :cond_d

    goto :goto_7

    :cond_d
    move-object v2, v3

    :goto_7
    if-nez v2, :cond_e

    move-object v2, v14

    :cond_e
    const/4 v15, 0x2

    .line 58
    new-array v4, v15, [Ljava/lang/String;

    aput-object v2, v4, v9

    invoke-virtual/range {p0 .. p0}, Lio/memorydrive/pleos/Memory;->getPlace()Ljava/lang/String;

    move-result-object v2

    aput-object v2, v4, v8

    invoke-static {v4}, Lkotlin/collections/CollectionsKt;->listOf([Ljava/lang/Object;)Ljava/util/List;

    move-result-object v2

    check-cast v2, Ljava/lang/Iterable;

    .line 149
    new-instance v4, Ljava/util/ArrayList;

    const/16 v5, 0xa

    invoke-static {v2, v5}, Lkotlin/collections/CollectionsKt;->collectionSizeOrDefault(Ljava/lang/Iterable;I)I

    move-result v5

    invoke-direct {v4, v5}, Ljava/util/ArrayList;-><init>(I)V

    check-cast v4, Ljava/util/Collection;

    .line 150
    invoke-interface {v2}, Ljava/lang/Iterable;->iterator()Ljava/util/Iterator;

    move-result-object v2

    :goto_8
    invoke-interface {v2}, Ljava/util/Iterator;->hasNext()Z

    move-result v5

    if-eqz v5, :cond_f

    invoke-interface {v2}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v5

    .line 151
    check-cast v5, Ljava/lang/String;

    .line 58
    check-cast v5, Ljava/lang/CharSequence;

    invoke-static {v5}, Lkotlin/text/StringsKt;->trim(Ljava/lang/CharSequence;)Ljava/lang/CharSequence;

    move-result-object v5

    invoke-virtual {v5}, Ljava/lang/Object;->toString()Ljava/lang/String;

    move-result-object v5

    .line 151
    invoke-interface {v4, v5}, Ljava/util/Collection;->add(Ljava/lang/Object;)Z

    goto :goto_8

    .line 152
    :cond_f
    check-cast v4, Ljava/util/List;

    .line 149
    check-cast v4, Ljava/lang/Iterable;

    .line 153
    new-instance v2, Ljava/util/ArrayList;

    invoke-direct {v2}, Ljava/util/ArrayList;-><init>()V

    check-cast v2, Ljava/util/Collection;

    .line 154
    invoke-interface {v4}, Ljava/lang/Iterable;->iterator()Ljava/util/Iterator;

    move-result-object v4

    :cond_10
    :goto_9
    invoke-interface {v4}, Ljava/util/Iterator;->hasNext()Z

    move-result v5

    if-eqz v5, :cond_11

    invoke-interface {v4}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v5

    move-object v6, v5

    check-cast v6, Ljava/lang/String;

    .line 58
    check-cast v6, Ljava/lang/CharSequence;

    invoke-interface {v6}, Ljava/lang/CharSequence;->length()I

    move-result v6

    if-lez v6, :cond_10

    .line 154
    invoke-interface {v2, v5}, Ljava/util/Collection;->add(Ljava/lang/Object;)Z

    goto :goto_9

    .line 155
    :cond_11
    check-cast v2, Ljava/util/List;

    .line 153
    move-object/from16 v16, v2

    check-cast v16, Ljava/lang/Iterable;

    .line 58
    const-string v2, ", "

    move-object/from16 v17, v2

    check-cast v17, Ljava/lang/CharSequence;

    const/16 v23, 0x3e

    const/16 v24, 0x0

    const/16 v18, 0x0

    const/16 v19, 0x0

    const/16 v20, 0x0

    const/16 v21, 0x0

    const/16 v22, 0x0

    invoke-static/range {v16 .. v24}, Lkotlin/collections/CollectionsKt;->joinToString$default(Ljava/lang/Iterable;Ljava/lang/CharSequence;Ljava/lang/CharSequence;Ljava/lang/CharSequence;ILjava/lang/CharSequence;Lkotlin/jvm/functions/Function1;ILjava/lang/Object;)Ljava/lang/String;

    move-result-object v2

    .line 61
    const-string v16, " "

    if-eqz v10, :cond_12

    move-object v4, v1

    check-cast v4, Ljava/lang/CharSequence;

    invoke-interface {v4}, Ljava/lang/CharSequence;->length()I

    move-result v4

    if-lez v4, :cond_12

    :goto_a
    move-object v10, v1

    goto :goto_e

    .line 62
    :cond_12
    new-array v4, v15, [Ljava/lang/String;

    move-object v5, v2

    check-cast v5, Ljava/lang/CharSequence;

    invoke-interface {v5}, Ljava/lang/CharSequence;->length()I

    move-result v5

    if-lez v5, :cond_13

    goto :goto_b

    :cond_13
    move-object v2, v3

    :goto_b
    if-eqz v2, :cond_14

    new-instance v5, Ljava/lang/StringBuilder;

    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v5, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    const-string v5, "."

    invoke-virtual {v2, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    goto :goto_c

    :cond_14
    move-object v2, v3

    :goto_c
    aput-object v2, v4, v9

    move-object v2, v1

    check-cast v2, Ljava/lang/CharSequence;

    invoke-interface {v2}, Ljava/lang/CharSequence;->length()I

    move-result v2

    if-lez v2, :cond_15

    goto :goto_d

    :cond_15
    move-object v1, v3

    :goto_d
    aput-object v1, v4, v8

    invoke-static {v4}, Lkotlin/collections/CollectionsKt;->listOf([Ljava/lang/Object;)Ljava/util/List;

    move-result-object v1

    check-cast v1, Ljava/lang/Iterable;

    .line 63
    invoke-static {v1}, Lkotlin/collections/CollectionsKt;->filterNotNull(Ljava/lang/Iterable;)Ljava/util/List;

    move-result-object v1

    move-object/from16 v17, v1

    check-cast v17, Ljava/lang/Iterable;

    move-object/from16 v18, v16

    check-cast v18, Ljava/lang/CharSequence;

    const/16 v24, 0x3e

    const/16 v25, 0x0

    const/16 v19, 0x0

    const/16 v20, 0x0

    const/16 v21, 0x0

    const/16 v22, 0x0

    const/16 v23, 0x0

    invoke-static/range {v17 .. v25}, Lkotlin/collections/CollectionsKt;->joinToString$default(Ljava/lang/Iterable;Ljava/lang/CharSequence;Ljava/lang/CharSequence;Ljava/lang/CharSequence;ILjava/lang/CharSequence;Lkotlin/jvm/functions/Function1;ILjava/lang/Object;)Ljava/lang/String;

    move-result-object v1

    goto :goto_a

    :goto_e
    if-eqz p1, :cond_1e

    .line 64
    invoke-virtual/range {p0 .. p0}, Lio/memorydrive/pleos/Memory;->getMusicProvider()Ljava/lang/String;

    move-result-object v1

    const-string v2, "none"

    invoke-static {v1, v2}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v1

    if-eqz v1, :cond_16

    goto :goto_f

    :cond_16
    if-eqz p4, :cond_17

    .line 65
    const-string v14, "\uc9c0\uae08 \ub4e3\ub358 \uc74c\uc545\uc744 Mac\uc5d0\uc11c \uc774\uc5b4 \ub4e4\ub824\ub4dc\ub9b4\uac8c\uc694."

    goto :goto_f

    :cond_17
    if-eqz v0, :cond_18

    .line 66
    const-string v14, "\ucf54\uc2a4\uc5d0 \ub2f4\uae34 \uc74c\uc545\uc744 \uc774\uc5b4 \ub4e4\ub824\ub4dc\ub9b4\uac8c\uc694."

    goto :goto_f

    :cond_18
    if-eqz v11, :cond_19

    .line 67
    sget-object v1, Lio/memorydrive/pleos/MemoryRoutePolicy;->INSTANCE:Lio/memorydrive/pleos/MemoryRoutePolicy;

    const/4 v5, 0x2

    const/4 v6, 0x0

    const-wide/16 v3, 0x0

    move-object/from16 v2, p0

    invoke-static/range {v1 .. v6}, Lio/memorydrive/pleos/MemoryRoutePolicy;->verifiedVehiclePlayback$default(Lio/memorydrive/pleos/MemoryRoutePolicy;Lio/memorydrive/pleos/Memory;JILjava/lang/Object;)Z

    move-result v1

    if-eqz v1, :cond_1e

    .line 68
    const-string v14, "\uadf8\ub54c \ucc28\uc5d0\uc11c \uc7ac\uc0dd\ud55c \uace1\uc744 \uc774\uc5b4 \ub4e4\ub824\ub4dc\ub9b4\uac8c\uc694."

    goto :goto_f

    .line 69
    :cond_19
    invoke-virtual/range {p0 .. p0}, Lio/memorydrive/pleos/Memory;->getMusicEvidence()Lio/memorydrive/pleos/MusicEvidence;

    move-result-object v1

    if-eqz v1, :cond_1a

    invoke-virtual/range {p0 .. p0}, Lio/memorydrive/pleos/Memory;->getMusicAssociation()Ljava/lang/String;

    move-result-object v1

    const-string v2, "temporal_history"

    invoke-static {v1, v2}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v1

    if-eqz v1, :cond_1a

    .line 70
    const-string v14, "\ucd2c\uc601 \uc2dc\uac04\ub300\uc758 \uccad\ucde8 \uae30\ub85d\uc5d0\uc11c \ucc3e\uc740 \uace1\uc744 \uc774\uc5b4 \ub4e4\ub824\ub4dc\ub9b4\uac8c\uc694."

    goto :goto_f

    .line 71
    :cond_1a
    invoke-virtual/range {p0 .. p0}, Lio/memorydrive/pleos/Memory;->getMusicEvidence()Lio/memorydrive/pleos/MusicEvidence;

    move-result-object v1

    if-eqz v1, :cond_1b

    invoke-virtual/range {p0 .. p0}, Lio/memorydrive/pleos/Memory;->getMusicAssociation()Ljava/lang/String;

    move-result-object v1

    const-string v2, "period_history"

    invoke-static {v1, v2}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v1

    if-eqz v1, :cond_1b

    .line 72
    const-string v14, "\uadf8 \uc2dc\uae30\uc5d0 \uc5ec\ub7ec \ub0a0 \ub4e4\uc740 \uace1\uc744 \uc774\uc5b4 \ub4e4\ub824\ub4dc\ub9b4\uac8c\uc694."

    goto :goto_f

    .line 73
    :cond_1b
    invoke-virtual/range {p0 .. p0}, Lio/memorydrive/pleos/Memory;->getMusicAssociation()Ljava/lang/String;

    move-result-object v1

    const-string v2, "recommendation"

    invoke-static {v1, v2}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v1

    if-eqz v1, :cond_1c

    const-string v14, "\uc774 \uc7a5\uba74\uc5d0 \ucd94\ucc9c\ud55c \uace1\uc744 \uc774\uc5b4 \ub4e4\ub824\ub4dc\ub9b4\uac8c\uc694."

    goto :goto_f

    .line 74
    :cond_1c
    invoke-virtual/range {p0 .. p0}, Lio/memorydrive/pleos/Memory;->getImported()Z

    move-result v1

    if-nez v1, :cond_1d

    const-string v14, "\uc774\uc5b4\uc11c \uc7ac\uc0dd\ud560\uac8c\uc694."

    goto :goto_f

    .line 75
    :cond_1d
    const-string v14, "\uc774 \uae30\ub85d\uc5d0 \uc5f0\uacb0\ud55c \uace1\uc744 \uc774\uc5b4 \ub4e4\ub824\ub4dc\ub9b4\uac8c\uc694."

    :cond_1e
    :goto_f
    if-eqz v0, :cond_20

    if-eqz p2, :cond_1f

    .line 78
    const-string v0, "\ucf54\uc2a4\uc758 \uc774\uc57c\uae30\ub3c4 \ud654\uba74\uc5d0\uc11c \uc774\uc5b4 \ubcfc \uc218 \uc788\uc5b4\uc694."

    goto :goto_10

    :cond_1f
    const-string v0, ""

    goto :goto_10

    :cond_20
    if-eqz v12, :cond_22

    .line 79
    invoke-virtual/range {p0 .. p0}, Lio/memorydrive/pleos/Memory;->getPhotoCached()Z

    move-result v0

    if-nez v0, :cond_22

    if-eqz p2, :cond_21

    const-string v0, "\uadf8\ub0a0 \uae30\ub85d\ub41c \uc774\ub3d9 \uacbd\ub85c\ub97c \ud568\uaed8 \ubcfc \uc218 \uc788\uc5b4\uc694."

    goto :goto_10

    .line 80
    :cond_21
    const-string v0, ""

    goto :goto_10

    :cond_22
    if-eqz v11, :cond_24

    .line 81
    invoke-virtual/range {p0 .. p0}, Lio/memorydrive/pleos/Memory;->getPhotoCached()Z

    move-result v0

    if-nez v0, :cond_24

    if-eqz p2, :cond_23

    const-string v0, "\ub0a0\uc9dc\uc640 \uc7a5\uc18c \uae30\ub85d\uc744 \ud654\uba74\uc5d0\uc11c \ubcfc \uc218 \uc788\uc5b4\uc694."

    goto :goto_10

    .line 82
    :cond_23
    const-string v0, ""

    goto :goto_10

    .line 83
    :cond_24
    invoke-virtual/range {p0 .. p0}, Lio/memorydrive/pleos/Memory;->getPhotoCached()Z

    move-result v0

    if-nez v0, :cond_26

    if-eqz p2, :cond_25

    const-string v0, "\uc0ac\uc9c4\uc774 \uc544\uc9c1 \uc900\ube44\ub418\uc9c0 \uc54a\uc544 \ub0a0\uc9dc\uc640 \uc7a5\uc18c \uae30\ub85d\uc744 \uba3c\uc800 \ubcf4\uc5ec\ub4dc\ub824\uc694."

    goto :goto_10

    .line 84
    :cond_25
    const-string v0, "\uc0ac\uc9c4\uc774 \uc5c6\ub294 \ubc29\ubb38 \uae30\ub85d\uc785\ub2c8\ub2e4."

    goto :goto_10

    :cond_26
    if-eqz p2, :cond_27

    .line 85
    const-string v0, "\uc774 \uae30\uc5b5\uc758 \uc0ac\uc9c4\uc744 \ud568\uaed8 \ubcfc \uc218 \uc788\uc5b4\uc694."

    goto :goto_10

    .line 86
    :cond_27
    const-string v0, ""

    :goto_10
    const/4 v1, 0x4

    .line 88
    new-array v1, v1, [Ljava/lang/String;

    aput-object v13, v1, v9

    aput-object v10, v1, v8

    aput-object v14, v1, v15

    const/4 v2, 0x3

    aput-object v0, v1, v2

    invoke-static {v1}, Lkotlin/collections/CollectionsKt;->listOf([Ljava/lang/Object;)Ljava/util/List;

    move-result-object v0

    check-cast v0, Ljava/lang/Iterable;

    .line 156
    new-instance v1, Ljava/util/ArrayList;

    invoke-direct {v1}, Ljava/util/ArrayList;-><init>()V

    check-cast v1, Ljava/util/Collection;

    .line 157
    invoke-interface {v0}, Ljava/lang/Iterable;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :cond_28
    :goto_11
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v2

    if-eqz v2, :cond_29

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v2

    move-object v3, v2

    check-cast v3, Ljava/lang/String;

    .line 88
    check-cast v3, Ljava/lang/CharSequence;

    invoke-interface {v3}, Ljava/lang/CharSequence;->length()I

    move-result v3

    if-lez v3, :cond_28

    .line 157
    invoke-interface {v1, v2}, Ljava/util/Collection;->add(Ljava/lang/Object;)Z

    goto :goto_11

    .line 158
    :cond_29
    check-cast v1, Ljava/util/List;

    .line 156
    move-object v2, v1

    check-cast v2, Ljava/lang/Iterable;

    .line 88
    move-object/from16 v3, v16

    check-cast v3, Ljava/lang/CharSequence;

    const/16 v9, 0x3e

    const/4 v10, 0x0

    const/4 v4, 0x0

    const/4 v5, 0x0

    const/4 v6, 0x0

    const/4 v7, 0x0

    const/4 v8, 0x0

    invoke-static/range {v2 .. v10}, Lkotlin/collections/CollectionsKt;->joinToString$default(Ljava/lang/Iterable;Ljava/lang/CharSequence;Ljava/lang/CharSequence;Ljava/lang/CharSequence;ILjava/lang/CharSequence;Lkotlin/jvm/functions/Function1;ILjava/lang/Object;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public static synthetic memoryNarration$default(Lio/memorydrive/pleos/Memory;ZZZZILjava/lang/Object;)Ljava/lang/String;
    .locals 1

    and-int/lit8 p6, p5, 0x4

    const/4 v0, 0x0

    if-eqz p6, :cond_0

    move p2, v0

    :cond_0
    and-int/lit8 p6, p5, 0x8

    if-eqz p6, :cond_1

    move p3, v0

    :cond_1
    and-int/lit8 p5, p5, 0x10

    if-eqz p5, :cond_2

    move p4, v0

    .line 36
    :cond_2
    invoke-static {p0, p1, p2, p3, p4}, Lio/memorydrive/pleos/MemoryJourneyKt;->memoryNarration(Lio/memorydrive/pleos/Memory;ZZZZ)Ljava/lang/String;

    move-result-object p0

    return-object p0
.end method

.method public static final sameExperienceContent(Lio/memorydrive/pleos/Memory;Lio/memorydrive/pleos/Memory;)Z
    .locals 34

    move-object/from16 v0, p1

    move-object/from16 v1, p0

    const-string v2, "<this>"

    move-object/from16 v3, p0

    invoke-static {v3, v2}, Lkotlin/jvm/internal/Intrinsics;->checkNotNullParameter(Ljava/lang/Object;Ljava/lang/String;)V

    const-string v2, "other"

    move-object/from16 v3, p1

    invoke-static {v3, v2}, Lkotlin/jvm/internal/Intrinsics;->checkNotNullParameter(Ljava/lang/Object;Ljava/lang/String;)V

    .line 106
    invoke-static {}, Lkotlin/collections/CollectionsKt;->emptyList()Ljava/util/List;

    move-result-object v26

    const v31, 0x7bff3ff

    const/16 v32, 0x0

    const/4 v2, 0x0

    const/4 v3, 0x0

    const/4 v4, 0x0

    const/4 v5, 0x0

    const/4 v6, 0x0

    const-wide/16 v7, 0x0

    const-wide/16 v9, 0x0

    const/4 v11, 0x0

    const/4 v12, 0x0

    const/4 v13, 0x0

    const/4 v14, 0x0

    const/4 v15, 0x0

    const/16 v16, 0x0

    const/16 v17, 0x0

    const/16 v18, 0x0

    const/16 v19, 0x0

    const/16 v20, 0x0

    const/16 v21, 0x0

    const/16 v22, 0x0

    const/16 v23, 0x0

    const/16 v24, 0x0

    const/16 v25, 0x0

    const/16 v27, 0x0

    const/16 v28, 0x0

    const/16 v29, 0x0

    const/16 v30, 0x0

    invoke-static/range {v1 .. v32}, Lio/memorydrive/pleos/Memory;->copy$default(Lio/memorydrive/pleos/Memory;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;DDFLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZLjava/lang/Double;Ljava/lang/String;Lio/memorydrive/pleos/MusicEvidence;Ljava/util/List;Ljava/util/List;Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;ILjava/lang/Object;)Lio/memorydrive/pleos/Memory;

    move-result-object v15

    invoke-static {}, Lkotlin/collections/CollectionsKt;->emptyList()Ljava/util/List;

    move-result-object v25

    const v30, 0x7bff3ff

    const/16 v31, 0x0

    const/4 v1, 0x0

    const-wide/16 v6, 0x0

    const-wide/16 v8, 0x0

    const/4 v10, 0x0

    const/4 v11, 0x0

    move-object/from16 v33, v15

    move-object/from16 v15, v16

    const/16 v19, 0x0

    const/16 v21, 0x0

    const/16 v26, 0x0

    invoke-static/range {v0 .. v31}, Lio/memorydrive/pleos/Memory;->copy$default(Lio/memorydrive/pleos/Memory;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;DDFLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZLjava/lang/Double;Ljava/lang/String;Lio/memorydrive/pleos/MusicEvidence;Ljava/util/List;Ljava/util/List;Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;ILjava/lang/Object;)Lio/memorydrive/pleos/Memory;

    move-result-object v0

    move-object/from16 v1, v33

    invoke-static {v1, v0}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v0

    return v0
.end method

.method private static final spokenRecordExcerpt(Ljava/lang/String;I)Ljava/lang/String;
    .locals 4

    .line 92
    check-cast p0, Ljava/lang/CharSequence;

    new-instance v0, Lkotlin/text/Regex;

    const-string v1, "[\\p{Cc}\\p{Cf}]"

    invoke-direct {v0, v1}, Lkotlin/text/Regex;-><init>(Ljava/lang/String;)V

    const-string v1, " "

    invoke-virtual {v0, p0, v1}, Lkotlin/text/Regex;->replace(Ljava/lang/CharSequence;Ljava/lang/String;)Ljava/lang/String;

    move-result-object p0

    check-cast p0, Ljava/lang/CharSequence;

    new-instance v0, Lkotlin/text/Regex;

    const-string v2, "\\s+"

    invoke-direct {v0, v2}, Lkotlin/text/Regex;-><init>(Ljava/lang/String;)V

    invoke-virtual {v0, p0, v1}, Lkotlin/text/Regex;->replace(Ljava/lang/CharSequence;Ljava/lang/String;)Ljava/lang/String;

    move-result-object p0

    check-cast p0, Ljava/lang/CharSequence;

    invoke-static {p0}, Lkotlin/text/StringsKt;->trim(Ljava/lang/CharSequence;)Ljava/lang/CharSequence;

    move-result-object p0

    invoke-virtual {p0}, Ljava/lang/Object;->toString()Ljava/lang/String;

    move-result-object p0

    .line 94
    check-cast p0, Ljava/lang/CharSequence;

    new-instance v0, Lkotlin/text/Regex;

    const-string v2, "(?<=[.!?\u3002])\\s+"

    invoke-direct {v0, v2}, Lkotlin/text/Regex;-><init>(Ljava/lang/String;)V

    const/4 v2, 0x0

    invoke-virtual {v0, p0, v2}, Lkotlin/text/Regex;->split(Ljava/lang/CharSequence;I)Ljava/util/List;

    move-result-object p0

    check-cast p0, Ljava/lang/Iterable;

    const/4 v0, 0x2

    invoke-static {p0, v0}, Lkotlin/collections/CollectionsKt;->take(Ljava/lang/Iterable;I)Ljava/util/List;

    move-result-object p0

    invoke-interface {p0}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object p0

    const-string v0, ""

    :goto_0
    invoke-interface {p0}, Ljava/util/Iterator;->hasNext()Z

    move-result v2

    if-eqz v2, :cond_1

    invoke-interface {p0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Ljava/lang/String;

    .line 95
    move-object v3, v0

    check-cast v3, Ljava/lang/CharSequence;

    invoke-interface {v3}, Ljava/lang/CharSequence;->length()I

    move-result v3

    if-nez v3, :cond_0

    goto :goto_1

    :cond_0
    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v3, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    .line 98
    :goto_1
    invoke-virtual {v2}, Ljava/lang/String;->length()I

    move-result v3

    if-gt v3, p1, :cond_1

    move-object v0, v2

    goto :goto_0

    :cond_1
    return-object v0
.end method
