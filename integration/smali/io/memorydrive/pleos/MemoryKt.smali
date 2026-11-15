.class public final Lio/memorydrive/pleos/MemoryKt;
.super Ljava/lang/Object;
.source "Memory.kt"


# annotations
.annotation runtime Lkotlin/Metadata;
    d1 = {
        "\u0000*\n\u0000\n\u0002\u0018\u0002\n\u0002\u0008\u0003\n\u0002\u0010\u000b\n\u0002\u0008\u0002\n\u0002\u0010\u0008\n\u0002\u0008\u0007\n\u0002\u0010\t\n\u0002\u0008\u0004\n\u0002\u0010 \n\u0002\u0008\u0003\u001a\u001f\u0010\u0004\u001a\u00020\u00052\u0008\u0010\u0006\u001a\u0004\u0018\u00010\u00052\u0008\u0010\u0007\u001a\u0004\u0018\u00010\u0008\u00a2\u0006\u0002\u0010\t\u001a\u001d\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\u00052\u0008\u0010\u000c\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\r\u001a\u0016\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0010\"\u0011\u0010\u0000\u001a\u00020\u0001\u00a2\u0006\u0008\n\u0000\u001a\u0004\u0008\u0002\u0010\u0003\"\u0011\u0010\u0012\u001a\u00020\u0001\u00a2\u0006\u0008\n\u0000\u001a\u0004\u0008\u0013\u0010\u0003\"\u0017\u0010\u0014\u001a\u0008\u0012\u0004\u0012\u00020\u00010\u0015\u00a2\u0006\u0008\n\u0000\u001a\u0004\u0008\u0016\u0010\u0017\u00a8\u0006\u0018"
    }
    d2 = {
        "coastMemory",
        "Lio/memorydrive/pleos/Memory;",
        "getCoastMemory",
        "()Lio/memorydrive/pleos/Memory;",
        "mayShowPhoto",
        "",
        "requiresOptimization",
        "activeRestrictions",
        "",
        "(Ljava/lang/Boolean;Ljava/lang/Integer;)Z",
        "mayShowParkedPhoto",
        "uxAllows",
        "gearParked",
        "(ZLjava/lang/Boolean;)Z",
        "isFreshLocation",
        "timestampMillis",
        "",
        "nowMillis",
        "emptyMemory",
        "getEmptyMemory",
        "demoMemories",
        "",
        "getDemoMemories",
        "()Ljava/util/List;",
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


# static fields
.field private static final coastMemory:Lio/memorydrive/pleos/Memory;

.field private static final demoMemories:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List<",
            "Lio/memorydrive/pleos/Memory;",
            ">;"
        }
    .end annotation
.end field

.field private static final emptyMemory:Lio/memorydrive/pleos/Memory;


# direct methods
.method static constructor <clinit>()V
    .locals 97

    .line 51
    new-instance v33, Lio/memorydrive/pleos/Memory;

    move-object/from16 v64, v33

    move-object/from16 v32, v33

    move-object/from16 v0, v33

    const v30, 0x7fffc00

    const/16 v31, 0x0

    const-string v1, "coast-2022"

    const-string v2, "\uac15\ub989 \ud574\uc548\uae38"

    const-string v3, "2022\ub144 \uc5ec\ub984"

    const-string v4, "\uc7a0\uae50 \uba48\ucdc4\ub358 \ubc14\ub2e4"

    const-string v5, "\ubaa9\uc801\uc9c0\uc5d0 \ub3c4\ucc29\ud558\uae30 \uc804, \ubc14\ub2e4\uac00 \ubcf4\uc5ec \uc7a0\uae50 \ucc28\ub97c \uc138\uc6e0\ub2e4.\n\ubc14\ub78c\uc774 \uc7a6\uc544\ub4e4 \ub54c\uae4c\uc9c0 \uc544\ubb34 \ub9d0 \uc5c6\uc774 \uc549\uc544 \uc788\uc5c8\ub2e4."

    const-wide v6, 0x4042e2b9f559b3d0L    # 37.7713

    const-wide v8, 0x40601e5604189375L    # 128.948

    const/high16 v10, 0x43960000    # 300.0f

    const-string v11, "\uc5ec\ub984\uc758 \uc794\ud5a5"

    const-string v12, ""

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

    const/16 v26, 0x0

    const/16 v27, 0x0

    const/16 v28, 0x0

    const/16 v29, 0x0

    invoke-direct/range {v0 .. v31}, Lio/memorydrive/pleos/Memory;-><init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;DDFLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZLjava/lang/Double;Ljava/lang/String;Lio/memorydrive/pleos/MusicEvidence;Ljava/util/List;Ljava/util/List;Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;ILkotlin/jvm/internal/DefaultConstructorMarker;)V

    sput-object v33, Lio/memorydrive/pleos/MemoryKt;->coastMemory:Lio/memorydrive/pleos/Memory;

    .line 76
    new-instance v0, Lio/memorydrive/pleos/Memory;

    move-object/from16 v65, v0

    const v95, 0x7feec00

    const/16 v96, 0x0

    const-string v66, "empty"

    const-string v67, "\uc5f0\uacb0\ub41c \uae30\uc5b5\uc774 \uc5c6\uc5b4\uc694"

    const-string v68, ""

    const-string v69, ""

    const-string v70, ""

    const-wide/16 v71, 0x0

    const-wide/16 v73, 0x0

    const/high16 v75, 0x43960000    # 300.0f

    const-string v76, "\ud568\uaed8 \ub4e4\uc744 \uc74c\uc545"

    const-string v77, "\ucc28\ub7c9\uc5d0\uc11c \ub0b4 \uacbd\ud5d8\uc744 \ub0a8\uaca8 \ubcf4\uc138\uc694"

    const/16 v78, 0x0

    const/16 v79, 0x0

    const-string v80, "none"

    const/16 v81, 0x0

    const/16 v82, 0x0

    const/16 v83, 0x0

    const/16 v84, 0x1

    const/16 v85, 0x0

    const/16 v86, 0x0

    const/16 v87, 0x0

    const/16 v88, 0x0

    const/16 v89, 0x0

    const/16 v90, 0x0

    const/16 v91, 0x0

    const/16 v92, 0x0

    const/16 v93, 0x0

    const/16 v94, 0x0

    invoke-direct/range {v65 .. v96}, Lio/memorydrive/pleos/Memory;-><init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;DDFLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZLjava/lang/Double;Ljava/lang/String;Lio/memorydrive/pleos/MusicEvidence;Ljava/util/List;Ljava/util/List;Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;ILkotlin/jvm/internal/DefaultConstructorMarker;)V

    sput-object v0, Lio/memorydrive/pleos/MemoryKt;->emptyMemory:Lio/memorydrive/pleos/Memory;

    const/4 v0, 0x3

    .line 81
    new-array v0, v0, [Lio/memorydrive/pleos/Memory;

    const/4 v1, 0x0

    aput-object v33, v0, v1

    const v62, 0x7ffff80

    const/16 v63, 0x0

    .line 82
    const-string v33, "lake-2021"

    const-string v34, "\uacbd\ud3ec\ud638 \uc0b0\ucc45\uae38"

    const-string v35, "2021\ub144 \uac00\uc744"

    const-string v36, "\ud638\uc218 \uacc1\uc5d0 \ub0a8\uae34 \uc7a5\uba74"

    const-string v37, ""

    const-wide v38, 0x4042e5c28f5c28f6L    # 37.795

    const-wide v40, 0x40601ccccccccccdL    # 128.9

    const/16 v42, 0x0

    const/16 v43, 0x0

    const/16 v44, 0x0

    const/16 v45, 0x0

    const/16 v46, 0x0

    const/16 v47, 0x0

    const/16 v48, 0x0

    const/16 v49, 0x0

    const/16 v50, 0x0

    const/16 v51, 0x0

    const/16 v52, 0x0

    const/16 v53, 0x0

    const/16 v54, 0x0

    const/16 v55, 0x0

    const/16 v56, 0x0

    const/16 v57, 0x0

    const/16 v58, 0x0

    const/16 v59, 0x0

    const/16 v60, 0x0

    const/16 v61, 0x0

    invoke-static/range {v32 .. v63}, Lio/memorydrive/pleos/Memory;->copy$default(Lio/memorydrive/pleos/Memory;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;DDFLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZLjava/lang/Double;Ljava/lang/String;Lio/memorydrive/pleos/MusicEvidence;Ljava/util/List;Ljava/util/List;Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;ILjava/lang/Object;)Lio/memorydrive/pleos/Memory;

    move-result-object v1

    const/4 v2, 0x1

    aput-object v1, v0, v2

    const v94, 0x7ffff80

    const/16 v95, 0x0

    .line 85
    const-string v65, "harbor-2020"

    const-string v66, "\uc548\ubaa9\ud56d"

    const-string v67, "2020\ub144 \ubd04"

    const-string v68, "\ud56d\uad6c\uc5d0 \ub0a8\uae34 \uc7a5\uba74"

    const-string v69, ""

    const-wide v70, 0x4042e2d0e5604189L    # 37.772

    const-wide v72, 0x40601e6666666666L    # 128.95

    const/16 v74, 0x0

    const/16 v75, 0x0

    const/16 v76, 0x0

    const/16 v77, 0x0

    const/16 v80, 0x0

    const/16 v83, 0x0

    const/16 v84, 0x0

    const/16 v85, 0x0

    invoke-static/range {v64 .. v95}, Lio/memorydrive/pleos/Memory;->copy$default(Lio/memorydrive/pleos/Memory;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;DDFLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZLjava/lang/Double;Ljava/lang/String;Lio/memorydrive/pleos/MusicEvidence;Ljava/util/List;Ljava/util/List;Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;ILjava/lang/Object;)Lio/memorydrive/pleos/Memory;

    move-result-object v1

    const/4 v2, 0x2

    aput-object v1, v0, v2

    .line 81
    invoke-static {v0}, Lkotlin/collections/CollectionsKt;->listOf([Ljava/lang/Object;)Ljava/util/List;

    move-result-object v0

    sput-object v0, Lio/memorydrive/pleos/MemoryKt;->demoMemories:Ljava/util/List;

    return-void
.end method

.method public static final getCoastMemory()Lio/memorydrive/pleos/Memory;
    .locals 1

    .line 51
    sget-object v0, Lio/memorydrive/pleos/MemoryKt;->coastMemory:Lio/memorydrive/pleos/Memory;

    return-object v0
.end method

.method public static final getDemoMemories()Ljava/util/List;
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/List<",
            "Lio/memorydrive/pleos/Memory;",
            ">;"
        }
    .end annotation

    .line 81
    sget-object v0, Lio/memorydrive/pleos/MemoryKt;->demoMemories:Ljava/util/List;

    invoke-static {v0}, Lio/memorydrive/pleos/GloveboxSamples;->list(Ljava/util/List;)Ljava/util/List;

    move-result-object v0

    return-object v0
.end method

.method public static final getEmptyMemory()Lio/memorydrive/pleos/Memory;
    .locals 1

    .line 76
    sget-object v0, Lio/memorydrive/pleos/MemoryKt;->emptyMemory:Lio/memorydrive/pleos/Memory;

    return-object v0
.end method

.method public static final isFreshLocation(JJ)Z
    .locals 2

    const-wide/16 v0, 0x0

    cmp-long v0, p0, v0

    if-lez v0, :cond_0

    const v0, 0x1d4c0

    int-to-long v0, v0

    sub-long v0, p2, v0

    cmp-long v0, p0, v0

    if-ltz v0, :cond_0

    const/16 v0, 0x1388

    int-to-long v0, v0

    add-long/2addr p2, v0

    cmp-long p0, p0, p2

    if-gtz p0, :cond_0

    const/4 p0, 0x1

    goto :goto_0

    :cond_0
    const/4 p0, 0x0

    :goto_0
    return p0
.end method

.method public static final mayShowParkedPhoto(ZLjava/lang/Boolean;)Z
    .locals 1

    if-eqz p0, :cond_0

    const/4 p0, 0x1

    .line 63
    invoke-static {p0}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    move-result-object v0

    invoke-static {p1, v0}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_0

    goto :goto_0

    :cond_0
    const/4 p0, 0x0

    :goto_0
    return p0
.end method

.method public static final mayShowPhoto(Ljava/lang/Boolean;Ljava/lang/Integer;)Z
    .locals 2

    const/4 v0, 0x0

    .line 61
    invoke-static {v0}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    move-result-object v1

    invoke-static {p0, v1}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result p0

    if-eqz p0, :cond_1

    if-nez p1, :cond_0

    goto :goto_0

    :cond_0
    invoke-virtual {p1}, Ljava/lang/Integer;->intValue()I

    move-result p0

    if-nez p0, :cond_1

    const/4 v0, 0x1

    :cond_1
    :goto_0
    return v0
.end method
