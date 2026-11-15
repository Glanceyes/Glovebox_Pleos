.class public final Lio/memorydrive/pleos/SharedCourse$Companion;
.super Ljava/lang/Object;
.source "SharedCourse.kt"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lio/memorydrive/pleos/SharedCourse;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x19
    name = "Companion"
.end annotation

.annotation system Ldalvik/annotation/SourceDebugExtension;
    value = "SMAP\nSharedCourse.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SharedCourse.kt\nio/memorydrive/pleos/SharedCourse$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 _Strings.kt\nkotlin/text/StringsKt___StringsKt\n*L\n1#1,99:1\n1#2:100\n1557#3:101\n1628#3,2:102\n1557#3:104\n1628#3,3:105\n1630#3:108\n1557#3:109\n1628#3,3:110\n1771#4,2:113\n*S KotlinDebug\n*F\n+ 1 SharedCourse.kt\nio/memorydrive/pleos/SharedCourse$Companion\n*L\n70#1:101\n70#1:102,2\n89#1:104\n89#1:105,3\n70#1:108\n94#1:109\n94#1:110,3\n61#1:113,2\n*E\n"
.end annotation

.annotation runtime Lkotlin/Metadata;
    d1 = {
        "\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0008\u0003\n\u0002\u0010\u0008\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0008\u0002\n\u0002\u0018\u0002\n\u0000\u0008\u0086\u0003\u0018\u00002\u00020\u0001B\t\u0008\u0002\u00a2\u0006\u0004\u0008\u0002\u0010\u0003J\u000e\u0010\u0008\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bJ\u000e\u0010\u000c\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\u000eR\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"
    }
    d2 = {
        "Lio/memorydrive/pleos/SharedCourse$Companion;",
        "",
        "<init>",
        "()V",
        "MAX_BYTES",
        "",
        "uuid",
        "Lkotlin/text/Regex;",
        "read",
        "Lio/memorydrive/pleos/SharedCourse;",
        "input",
        "Ljava/io/InputStream;",
        "parse",
        "json",
        "Lorg/json/JSONObject;",
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


# direct methods
.method private constructor <init>()V
    .locals 0

    .line 39
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public synthetic constructor <init>(Lkotlin/jvm/internal/DefaultConstructorMarker;)V
    .locals 0

    invoke-direct {p0}, Lio/memorydrive/pleos/SharedCourse$Companion;-><init>()V

    return-void
.end method

.method private static final parse$identifier(Lorg/json/JSONObject;)Ljava/lang/String;
    .locals 6

    const/16 v4, 0x8

    const/4 v5, 0x0

    .line 64
    const-string v1, "id"

    const/16 v2, 0x24

    const/4 v3, 0x0

    move-object v0, p0

    invoke-static/range {v0 .. v5}, Lio/memorydrive/pleos/SharedCourse$Companion;->parse$text$default(Lorg/json/JSONObject;Ljava/lang/String;IZILjava/lang/Object;)Ljava/lang/String;

    move-result-object p0

    .line 65
    invoke-static {}, Lio/memorydrive/pleos/SharedCourse;->access$getUuid$cp()Lkotlin/text/Regex;

    move-result-object v0

    move-object v1, p0

    check-cast v1, Ljava/lang/CharSequence;

    invoke-virtual {v0, v1}, Lkotlin/text/Regex;->matches(Ljava/lang/CharSequence;)Z

    move-result v0

    if-eqz v0, :cond_0

    .line 66
    sget-object v0, Ljava/util/Locale;->ROOT:Ljava/util/Locale;

    invoke-virtual {p0, v0}, Ljava/lang/String;->toLowerCase(Ljava/util/Locale;)Ljava/lang/String;

    move-result-object p0

    const-string v0, "toLowerCase(...)"

    invoke-static {p0, v0}, Lkotlin/jvm/internal/Intrinsics;->checkNotNullExpressionValue(Ljava/lang/Object;Ljava/lang/String;)V

    return-object p0

    .line 65
    :cond_0
    new-instance p0, Ljava/lang/IllegalArgumentException;

    const-string v0, "\ucf54\uc2a4 \uc2dd\ubcc4\uc790 \ud615\uc2dd\uc774 \uc62c\ubc14\ub974\uc9c0 \uc54a\uc544\uc694"

    invoke-virtual {v0}, Ljava/lang/Object;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-direct {p0, v0}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    throw p0
.end method

.method private static final parse$text(Lorg/json/JSONObject;Ljava/lang/String;IZ)Ljava/lang/String;
    .locals 3

    .line 57
    invoke-virtual {p0, p1}, Lorg/json/JSONObject;->get(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object p0

    .line 58
    instance-of v0, p0, Ljava/lang/String;

    const-string v1, "\ucf54\uc2a4\uc758 "

    if-eqz v0, :cond_4

    .line 59
    check-cast p0, Ljava/lang/String;

    check-cast p0, Ljava/lang/CharSequence;

    invoke-static {p0}, Lkotlin/text/StringsKt;->trim(Ljava/lang/CharSequence;)Ljava/lang/CharSequence;

    move-result-object p0

    invoke-virtual {p0}, Ljava/lang/Object;->toString()Ljava/lang/String;

    move-result-object p0

    if-nez p3, :cond_0

    .line 60
    move-object p3, p0

    check-cast p3, Ljava/lang/CharSequence;

    invoke-interface {p3}, Ljava/lang/CharSequence;->length()I

    move-result p3

    if-lez p3, :cond_3

    :cond_0
    invoke-virtual {p0}, Ljava/lang/String;->length()I

    move-result p3

    const/4 v0, 0x0

    invoke-virtual {p0, v0, p3}, Ljava/lang/String;->codePointCount(II)I

    move-result p3

    if-gt p3, p2, :cond_3

    .line 61
    move-object p2, p0

    check-cast p2, Ljava/lang/CharSequence;

    .line 113
    :goto_0
    invoke-interface {p2}, Ljava/lang/CharSequence;->length()I

    move-result p3

    if-ge v0, p3, :cond_2

    invoke-interface {p2, v0}, Ljava/lang/CharSequence;->charAt(I)C

    move-result p3

    const/16 v2, 0x20

    if-ge p3, v2, :cond_1

    const/16 v2, 0xa

    if-eq p3, v2, :cond_1

    const/16 v2, 0x9

    if-ne p3, v2, :cond_3

    :cond_1
    add-int/lit8 v0, v0, 0x1

    goto :goto_0

    :cond_2
    return-object p0

    .line 61
    :cond_3
    new-instance p0, Ljava/lang/StringBuilder;

    invoke-direct {p0, v1}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {p0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object p0

    const-string p1, " \uae38\uc774 \ub610\ub294 \ud615\uc2dd\uc744 \ud655\uc778\ud574 \uc8fc\uc138\uc694"

    invoke-virtual {p0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object p0

    invoke-virtual {p0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    .line 60
    new-instance p1, Ljava/lang/IllegalArgumentException;

    invoke-virtual {p0}, Ljava/lang/Object;->toString()Ljava/lang/String;

    move-result-object p0

    invoke-direct {p1, p0}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    throw p1

    .line 58
    :cond_4
    new-instance p0, Ljava/lang/StringBuilder;

    invoke-direct {p0, v1}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {p0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object p0

    const-string p1, " \ud56d\ubaa9\uc744 \ud655\uc778\ud574 \uc8fc\uc138\uc694"

    invoke-virtual {p0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object p0

    invoke-virtual {p0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    new-instance p1, Ljava/lang/IllegalArgumentException;

    invoke-virtual {p0}, Ljava/lang/Object;->toString()Ljava/lang/String;

    move-result-object p0

    invoke-direct {p1, p0}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    throw p1
.end method

.method static synthetic parse$text$default(Lorg/json/JSONObject;Ljava/lang/String;IZILjava/lang/Object;)Ljava/lang/String;
    .locals 0

    and-int/lit8 p4, p4, 0x8

    if-eqz p4, :cond_0

    const/4 p3, 0x0

    .line 56
    :cond_0
    invoke-static {p0, p1, p2, p3}, Lio/memorydrive/pleos/SharedCourse$Companion;->parse$text(Lorg/json/JSONObject;Ljava/lang/String;IZ)Ljava/lang/String;

    move-result-object p0

    return-object p0
.end method


# virtual methods
.method public final parse(Lorg/json/JSONObject;)Lio/memorydrive/pleos/SharedCourse;
    .locals 26

    move-object/from16 v6, p1

    const-string v0, "json"

    invoke-static {v6, v0}, Lkotlin/jvm/internal/Intrinsics;->checkNotNullParameter(Ljava/lang/Object;Ljava/lang/String;)V

    .line 67
    const-string v0, "version"

    invoke-virtual {v6, v0}, Lorg/json/JSONObject;->get(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v1

    instance-of v1, v1, Ljava/lang/Number;

    if-eqz v1, :cond_15

    invoke-virtual {v6, v0}, Lorg/json/JSONObject;->getDouble(Ljava/lang/String;)D

    move-result-wide v0

    const-wide/high16 v2, 0x3ff0000000000000L    # 1.0

    cmpg-double v0, v0, v2

    if-nez v0, :cond_15

    .line 68
    const-string v0, "stops"

    invoke-virtual {v6, v0}, Lorg/json/JSONObject;->getJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;

    move-result-object v0

    .line 69
    invoke-virtual {v0}, Lorg/json/JSONArray;->length()I

    move-result v1

    const/4 v7, 0x1

    if-gt v7, v1, :cond_14

    const/16 v2, 0x15

    if-ge v1, v2, :cond_14

    .line 70
    invoke-virtual {v0}, Lorg/json/JSONArray;->length()I

    move-result v1

    const/4 v2, 0x0

    invoke-static {v2, v1}, Lkotlin/ranges/RangesKt;->until(II)Lkotlin/ranges/IntRange;

    move-result-object v1

    check-cast v1, Ljava/lang/Iterable;

    .line 101
    new-instance v3, Ljava/util/ArrayList;

    const/16 v4, 0xa

    invoke-static {v1, v4}, Lkotlin/collections/CollectionsKt;->collectionSizeOrDefault(Ljava/lang/Iterable;I)I

    move-result v5

    invoke-direct {v3, v5}, Ljava/util/ArrayList;-><init>(I)V

    check-cast v3, Ljava/util/Collection;

    .line 102
    invoke-interface {v1}, Ljava/lang/Iterable;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_0
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v5

    if-eqz v5, :cond_11

    move-object v5, v1

    check-cast v5, Lkotlin/collections/IntIterator;

    invoke-virtual {v5}, Lkotlin/collections/IntIterator;->nextInt()I

    move-result v5

    .line 71
    invoke-virtual {v0, v5}, Lorg/json/JSONArray;->getJSONObject(I)Lorg/json/JSONObject;

    move-result-object v5

    .line 72
    const-string v8, "coordinate"

    invoke-virtual {v5, v8}, Lorg/json/JSONObject;->getJSONObject(Ljava/lang/String;)Lorg/json/JSONObject;

    move-result-object v8

    .line 73
    const-string v9, "latitude"

    invoke-virtual {v8, v9}, Lorg/json/JSONObject;->get(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v10

    instance-of v10, v10, Ljava/lang/Number;

    const-string v11, "Failed requirement."

    if-eqz v10, :cond_10

    const-string v10, "longitude"

    invoke-virtual {v8, v10}, Lorg/json/JSONObject;->get(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v12

    instance-of v12, v12, Ljava/lang/Number;

    if-eqz v12, :cond_10

    .line 74
    invoke-virtual {v8, v9}, Lorg/json/JSONObject;->getDouble(Ljava/lang/String;)D

    move-result-wide v16

    invoke-virtual {v8, v10}, Lorg/json/JSONObject;->getDouble(Ljava/lang/String;)D

    move-result-wide v18

    .line 75
    invoke-static/range {v16 .. v17}, Ljava/lang/Double;->isInfinite(D)Z

    move-result v8

    if-nez v8, :cond_f

    invoke-static/range {v16 .. v17}, Ljava/lang/Double;->isNaN(D)Z

    move-result v8

    if-nez v8, :cond_f

    invoke-static/range {v18 .. v19}, Ljava/lang/Double;->isInfinite(D)Z

    move-result v8

    if-nez v8, :cond_f

    invoke-static/range {v18 .. v19}, Ljava/lang/Double;->isNaN(D)Z

    move-result v8

    if-nez v8, :cond_f

    const-wide v8, -0x3fa9800000000000L    # -90.0

    cmpg-double v8, v8, v16

    if-gtz v8, :cond_0

    const-wide v8, 0x4056800000000000L    # 90.0

    cmpg-double v8, v16, v8

    if-gtz v8, :cond_0

    move v8, v7

    goto :goto_1

    :cond_0
    move v8, v2

    :goto_1
    if-eqz v8, :cond_f

    const-wide v8, -0x3f99800000000000L    # -180.0

    cmpg-double v8, v8, v18

    if-gtz v8, :cond_1

    const-wide v8, 0x4066800000000000L    # 180.0

    cmpg-double v8, v18, v8

    if-gtz v8, :cond_1

    move v8, v7

    goto :goto_2

    :cond_1
    move v8, v2

    :goto_2
    if-eqz v8, :cond_f

    .line 76
    const-string v8, "music"

    invoke-virtual {v5, v8}, Lorg/json/JSONObject;->isNull(Ljava/lang/String;)Z

    move-result v9

    if-eqz v9, :cond_2

    const/4 v8, 0x0

    move-object/from16 v21, v8

    goto/16 :goto_5

    :cond_2
    invoke-virtual {v5, v8}, Lorg/json/JSONObject;->getJSONObject(Ljava/lang/String;)Lorg/json/JSONObject;

    move-result-object v8

    .line 77
    invoke-static {v8}, Lkotlin/jvm/internal/Intrinsics;->checkNotNull(Ljava/lang/Object;)V

    const/16 v24, 0x8

    const/16 v25, 0x0

    const-string v21, "provider"

    const/16 v22, 0x14

    const/16 v23, 0x0

    move-object/from16 v20, v8

    invoke-static/range {v20 .. v25}, Lio/memorydrive/pleos/SharedCourse$Companion;->parse$text$default(Lorg/json/JSONObject;Ljava/lang/String;IZILjava/lang/Object;)Ljava/lang/String;

    move-result-object v9

    .line 78
    const-string v21, "trackID"

    const/16 v22, 0x44

    invoke-static/range {v20 .. v25}, Lio/memorydrive/pleos/SharedCourse$Companion;->parse$text$default(Lorg/json/JSONObject;Ljava/lang/String;IZILjava/lang/Object;)Ljava/lang/String;

    move-result-object v10

    .line 79
    invoke-virtual {v9}, Ljava/lang/String;->hashCode()I

    move-result v12

    sparse-switch v12, :sswitch_data_0

    goto :goto_3

    :sswitch_0
    const-string v12, "YouTube Music"

    invoke-virtual {v9, v12}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v12

    if-nez v12, :cond_3

    goto :goto_3

    .line 80
    :cond_3
    new-instance v12, Lkotlin/text/Regex;

    const-string v13, "[A-Za-z0-9_-]{11}"

    invoke-direct {v12, v13}, Lkotlin/text/Regex;-><init>(Ljava/lang/String;)V

    move-object v13, v10

    check-cast v13, Ljava/lang/CharSequence;

    invoke-virtual {v12, v13}, Lkotlin/text/Regex;->matches(Ljava/lang/CharSequence;)Z

    move-result v12

    goto :goto_4

    .line 79
    :sswitch_1
    const-string v12, "Bluetooth"

    invoke-virtual {v9, v12}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v12

    if-nez v12, :cond_5

    goto :goto_3

    :sswitch_2
    const-string v12, "Spotify"

    invoke-virtual {v9, v12}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v12

    if-nez v12, :cond_4

    goto :goto_3

    .line 81
    :cond_4
    new-instance v12, Lkotlin/text/Regex;

    const-string v13, "[A-Za-z0-9]{22}"

    invoke-direct {v12, v13}, Lkotlin/text/Regex;-><init>(Ljava/lang/String;)V

    move-object v13, v10

    check-cast v13, Ljava/lang/CharSequence;

    invoke-virtual {v12, v13}, Lkotlin/text/Regex;->matches(Ljava/lang/CharSequence;)Z

    move-result v12

    goto :goto_4

    .line 79
    :sswitch_3
    const-string v12, "\ucc28\ub7c9 \uc74c\uc545"

    invoke-virtual {v9, v12}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v12

    if-nez v12, :cond_5

    goto :goto_3

    .line 82
    :cond_5
    sget-object v12, Lio/memorydrive/pleos/PhoneMediaPolicy;->INSTANCE:Lio/memorydrive/pleos/PhoneMediaPolicy;

    invoke-virtual {v12, v10}, Lio/memorydrive/pleos/PhoneMediaPolicy;->validId(Ljava/lang/String;)Z

    move-result v12

    if-eqz v12, :cond_6

    sget-object v12, Lio/memorydrive/pleos/PhoneMediaPolicy;->INSTANCE:Lio/memorydrive/pleos/PhoneMediaPolicy;

    invoke-virtual {v12, v10}, Lio/memorydrive/pleos/PhoneMediaPolicy;->provider(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v12

    invoke-static {v12, v9}, Lkotlin/jvm/internal/Intrinsics;->areEqual(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v12

    if-eqz v12, :cond_6

    move v12, v7

    goto :goto_4

    :cond_6
    :goto_3
    move v12, v2

    :goto_4
    if-eqz v12, :cond_e

    .line 85
    new-instance v12, Lio/memorydrive/pleos/CourseTrack;

    const/16 v24, 0x8

    const/16 v25, 0x0

    const-string v21, "title"

    const/16 v22, 0xc8

    const/16 v23, 0x0

    move-object/from16 v20, v8

    invoke-static/range {v20 .. v25}, Lio/memorydrive/pleos/SharedCourse$Companion;->parse$text$default(Lorg/json/JSONObject;Ljava/lang/String;IZILjava/lang/Object;)Ljava/lang/String;

    move-result-object v8

    invoke-direct {v12, v9, v10, v8}, Lio/memorydrive/pleos/CourseTrack;-><init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V

    move-object/from16 v21, v12

    .line 87
    :goto_5
    const-string v8, "photos"

    invoke-virtual {v5, v8}, Lorg/json/JSONObject;->optJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;

    move-result-object v8

    if-eqz v8, :cond_b

    .line 88
    invoke-virtual {v8}, Lorg/json/JSONArray;->length()I

    move-result v9

    const/16 v10, 0x3e8

    if-gt v9, v10, :cond_7

    move v9, v7

    goto :goto_6

    :cond_7
    move v9, v2

    :goto_6
    if-eqz v9, :cond_a

    .line 89
    invoke-virtual {v8}, Lorg/json/JSONArray;->length()I

    move-result v9

    invoke-static {v2, v9}, Lkotlin/ranges/RangesKt;->until(II)Lkotlin/ranges/IntRange;

    move-result-object v9

    check-cast v9, Ljava/lang/Iterable;

    .line 104
    new-instance v10, Ljava/util/ArrayList;

    invoke-static {v9, v4}, Lkotlin/collections/CollectionsKt;->collectionSizeOrDefault(Ljava/lang/Iterable;I)I

    move-result v12

    invoke-direct {v10, v12}, Ljava/util/ArrayList;-><init>(I)V

    check-cast v10, Ljava/util/Collection;

    .line 105
    invoke-interface {v9}, Ljava/lang/Iterable;->iterator()Ljava/util/Iterator;

    move-result-object v9

    :goto_7
    invoke-interface {v9}, Ljava/util/Iterator;->hasNext()Z

    move-result v12

    if-eqz v12, :cond_9

    move-object v12, v9

    check-cast v12, Lkotlin/collections/IntIterator;

    invoke-virtual {v12}, Lkotlin/collections/IntIterator;->nextInt()I

    move-result v12

    .line 89
    invoke-virtual {v8, v12}, Lorg/json/JSONArray;->getString(I)Ljava/lang/String;

    move-result-object v12

    invoke-static {}, Lio/memorydrive/pleos/SharedCourse;->access$getUuid$cp()Lkotlin/text/Regex;

    move-result-object v13

    invoke-static {v12}, Lkotlin/jvm/internal/Intrinsics;->checkNotNull(Ljava/lang/Object;)V

    move-object v14, v12

    check-cast v14, Ljava/lang/CharSequence;

    invoke-virtual {v13, v14}, Lkotlin/text/Regex;->matches(Ljava/lang/CharSequence;)Z

    move-result v13

    if-eqz v13, :cond_8

    const-string v13, "also(...)"

    invoke-static {v12, v13}, Lkotlin/jvm/internal/Intrinsics;->checkNotNullExpressionValue(Ljava/lang/Object;Ljava/lang/String;)V

    sget-object v13, Ljava/util/Locale;->ROOT:Ljava/util/Locale;

    invoke-virtual {v12, v13}, Ljava/lang/String;->toLowerCase(Ljava/util/Locale;)Ljava/lang/String;

    move-result-object v12

    const-string v13, "toLowerCase(...)"

    invoke-static {v12, v13}, Lkotlin/jvm/internal/Intrinsics;->checkNotNullExpressionValue(Ljava/lang/Object;Ljava/lang/String;)V

    .line 106
    invoke-interface {v10, v12}, Ljava/util/Collection;->add(Ljava/lang/Object;)Z

    goto :goto_7

    .line 89
    :cond_8
    new-instance v0, Ljava/lang/IllegalArgumentException;

    invoke-virtual {v11}, Ljava/lang/Object;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    throw v0

    .line 107
    :cond_9
    check-cast v10, Ljava/util/List;

    move-object/from16 v22, v10

    goto :goto_8

    .line 88
    :cond_a
    new-instance v0, Ljava/lang/IllegalArgumentException;

    invoke-virtual {v11}, Ljava/lang/Object;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    throw v0

    .line 90
    :cond_b
    invoke-static {}, Lkotlin/collections/CollectionsKt;->emptyList()Ljava/util/List;

    move-result-object v8

    move-object/from16 v22, v8

    .line 91
    :goto_8
    move-object/from16 v8, v22

    check-cast v8, Ljava/lang/Iterable;

    invoke-static {v8}, Lkotlin/collections/CollectionsKt;->distinct(Ljava/lang/Iterable;)Ljava/util/List;

    move-result-object v8

    invoke-interface {v8}, Ljava/util/List;->size()I

    move-result v8

    invoke-interface/range {v22 .. v22}, Ljava/util/List;->size()I

    move-result v9

    if-ne v8, v9, :cond_c

    move v8, v7

    goto :goto_9

    :cond_c
    move v8, v2

    :goto_9
    if-eqz v8, :cond_d

    .line 92
    new-instance v15, Lio/memorydrive/pleos/CourseStop;

    invoke-static {v5}, Lkotlin/jvm/internal/Intrinsics;->checkNotNull(Ljava/lang/Object;)V

    invoke-static {v5}, Lio/memorydrive/pleos/SharedCourse$Companion;->parse$identifier(Lorg/json/JSONObject;)Ljava/lang/String;

    move-result-object v14

    const/16 v12, 0x8

    const/4 v13, 0x0

    const-string v9, "name"

    const/16 v10, 0x50

    const/4 v11, 0x0

    move-object v8, v5

    invoke-static/range {v8 .. v13}, Lio/memorydrive/pleos/SharedCourse$Companion;->parse$text$default(Lorg/json/JSONObject;Ljava/lang/String;IZILjava/lang/Object;)Ljava/lang/String;

    move-result-object v8

    const-string v9, "note"

    const/16 v10, 0x12c

    invoke-static {v5, v9, v10, v7}, Lio/memorydrive/pleos/SharedCourse$Companion;->parse$text(Lorg/json/JSONObject;Ljava/lang/String;IZ)Ljava/lang/String;

    move-result-object v20

    move-object v13, v15

    move-object v5, v15

    move-object v15, v8

    invoke-direct/range {v13 .. v22}, Lio/memorydrive/pleos/CourseStop;-><init>(Ljava/lang/String;Ljava/lang/String;DDLjava/lang/String;Lio/memorydrive/pleos/CourseTrack;Ljava/util/List;)V

    .line 103
    invoke-interface {v3, v5}, Ljava/util/Collection;->add(Ljava/lang/Object;)Z

    goto/16 :goto_0

    .line 91
    :cond_d
    new-instance v0, Ljava/lang/IllegalArgumentException;

    invoke-virtual {v11}, Ljava/lang/Object;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    throw v0

    .line 79
    :cond_e
    new-instance v0, Ljava/lang/IllegalArgumentException;

    const-string v1, "\uc9c0\uc6d0\ud558\ub294 \uc74c\uc545 \uc11c\ube44\uc2a4\uc758 \uace1 \uc815\ubcf4\ub97c \ud655\uc778\ud574 \uc8fc\uc138\uc694"

    invoke-virtual {v1}, Ljava/lang/Object;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    throw v0

    .line 75
    :cond_f
    new-instance v0, Ljava/lang/IllegalArgumentException;

    const-string v1, "\uc7a5\uc18c\uc758 \uc88c\ud45c\uac00 \uc62c\ubc14\ub974\uc9c0 \uc54a\uc544\uc694"

    invoke-virtual {v1}, Ljava/lang/Object;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    throw v0

    .line 73
    :cond_10
    new-instance v0, Ljava/lang/IllegalArgumentException;

    invoke-virtual {v11}, Ljava/lang/Object;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    throw v0

    .line 108
    :cond_11
    move-object v8, v3

    check-cast v8, Ljava/util/List;

    .line 94
    move-object v0, v8

    check-cast v0, Ljava/lang/Iterable;

    .line 109
    new-instance v1, Ljava/util/ArrayList;

    invoke-static {v0, v4}, Lkotlin/collections/CollectionsKt;->collectionSizeOrDefault(Ljava/lang/Iterable;I)I

    move-result v2

    invoke-direct {v1, v2}, Ljava/util/ArrayList;-><init>(I)V

    check-cast v1, Ljava/util/Collection;

    .line 110
    invoke-interface {v0}, Ljava/lang/Iterable;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :goto_a
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v2

    if-eqz v2, :cond_12

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v2

    .line 111
    check-cast v2, Lio/memorydrive/pleos/CourseStop;

    .line 94
    invoke-virtual {v2}, Lio/memorydrive/pleos/CourseStop;->getId()Ljava/lang/String;

    move-result-object v2

    .line 111
    invoke-interface {v1, v2}, Ljava/util/Collection;->add(Ljava/lang/Object;)Z

    goto :goto_a

    .line 112
    :cond_12
    check-cast v1, Ljava/util/List;

    .line 109
    check-cast v1, Ljava/lang/Iterable;

    .line 94
    invoke-static {v1}, Lkotlin/collections/CollectionsKt;->distinct(Ljava/lang/Iterable;)Ljava/util/List;

    move-result-object v0

    invoke-interface {v0}, Ljava/util/List;->size()I

    move-result v0

    invoke-interface {v8}, Ljava/util/List;->size()I

    move-result v1

    if-ne v0, v1, :cond_13

    .line 95
    new-instance v9, Lio/memorydrive/pleos/SharedCourse;

    invoke-static/range {p1 .. p1}, Lio/memorydrive/pleos/SharedCourse$Companion;->parse$identifier(Lorg/json/JSONObject;)Ljava/lang/String;

    move-result-object v10

    const/16 v4, 0x8

    const/4 v5, 0x0

    const-string v1, "title"

    const/16 v2, 0x50

    const/4 v3, 0x0

    move-object/from16 v0, p1

    invoke-static/range {v0 .. v5}, Lio/memorydrive/pleos/SharedCourse$Companion;->parse$text$default(Lorg/json/JSONObject;Ljava/lang/String;IZILjava/lang/Object;)Ljava/lang/String;

    move-result-object v0

    const-string v1, "introduction"

    const/16 v2, 0x258

    invoke-static {v6, v1, v2, v7}, Lio/memorydrive/pleos/SharedCourse$Companion;->parse$text(Lorg/json/JSONObject;Ljava/lang/String;IZ)Ljava/lang/String;

    move-result-object v1

    invoke-direct {v9, v10, v0, v1, v8}, Lio/memorydrive/pleos/SharedCourse;-><init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;)V

    move-object/from16 v0, p1
    invoke-static {v0}, Lio/memorydrive/pleos/SharedAlbum;->from(Lorg/json/JSONObject;)Lorg/json/JSONObject;
    move-result-object v0
    iput-object v0, v9, Lio/memorydrive/pleos/SharedCourse;->experience:Lorg/json/JSONObject;

    return-object v9

    .line 94
    :cond_13
    new-instance v0, Ljava/lang/IllegalArgumentException;

    const-string v1, "\uc911\ubcf5\ub41c \uc7a5\uc18c \uc2dd\ubcc4\uc790\uac00 \uc788\uc5b4\uc694"

    invoke-virtual {v1}, Ljava/lang/Object;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    throw v0

    .line 69
    :cond_14
    new-instance v0, Ljava/lang/IllegalArgumentException;

    const-string v1, "\ucf54\uc2a4\uc5d0\ub294 1~20\uacf3\uc774 \ud544\uc694\ud574\uc694"

    invoke-virtual {v1}, Ljava/lang/Object;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    throw v0

    .line 67
    :cond_15
    new-instance v0, Ljava/lang/IllegalArgumentException;

    const-string v1, "\uc9c0\uc6d0\ud558\uc9c0 \uc54a\ub294 \ucf54\uc2a4 \ubc84\uc804\uc774\uc5d0\uc694"

    invoke-virtual {v1}, Ljava/lang/Object;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    throw v0

    :sswitch_data_0
    .sparse-switch
        -0x6f6ce0a8 -> :sswitch_3
        -0x13e98166 -> :sswitch_2
        -0x13331d72 -> :sswitch_1
        0x179122c8 -> :sswitch_0
    .end sparse-switch
.end method

.method public final read(Ljava/io/InputStream;)Lio/memorydrive/pleos/SharedCourse;
    .locals 5

    const-string v0, "input"

    invoke-static {p1, v0}, Lkotlin/jvm/internal/Intrinsics;->checkNotNullParameter(Ljava/lang/Object;Ljava/lang/String;)V

    .line 43
    new-instance v0, Ljava/io/ByteArrayOutputStream;

    invoke-direct {v0}, Ljava/io/ByteArrayOutputStream;-><init>()V

    const/16 v1, 0x2000

    .line 44
    new-array v1, v1, [B

    .line 46
    :goto_0
    invoke-virtual {p1, v1}, Ljava/io/InputStream;->read([B)I

    move-result v2

    if-ltz v2, :cond_1

    .line 48
    invoke-virtual {v0}, Ljava/io/ByteArrayOutputStream;->size()I

    move-result v3

    add-int/2addr v3, v2

    const v4, 0x3e800

    if-gt v3, v4, :cond_0

    const/4 v3, 0x0

    .line 49
    invoke-virtual {v0, v1, v3, v2}, Ljava/io/ByteArrayOutputStream;->write([BII)V

    goto :goto_0

    .line 48
    :cond_0
    new-instance p1, Ljava/lang/IllegalArgumentException;

    const-string v0, "\ucf54\uc2a4 \ud30c\uc77c\uc740 256KB \uc774\ub0b4\uc5ec\uc57c \ud574\uc694"

    invoke-virtual {v0}, Ljava/lang/Object;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-direct {p1, v0}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    throw p1

    .line 51
    :cond_1
    sget-object p1, Lkotlin/text/Charsets;->UTF_8:Ljava/nio/charset/Charset;

    invoke-virtual {p1}, Ljava/nio/charset/Charset;->newDecoder()Ljava/nio/charset/CharsetDecoder;

    move-result-object p1

    sget-object v1, Ljava/nio/charset/CodingErrorAction;->REPORT:Ljava/nio/charset/CodingErrorAction;

    invoke-virtual {p1, v1}, Ljava/nio/charset/CharsetDecoder;->onMalformedInput(Ljava/nio/charset/CodingErrorAction;)Ljava/nio/charset/CharsetDecoder;

    move-result-object p1

    .line 52
    sget-object v1, Ljava/nio/charset/CodingErrorAction;->REPORT:Ljava/nio/charset/CodingErrorAction;

    invoke-virtual {p1, v1}, Ljava/nio/charset/CharsetDecoder;->onUnmappableCharacter(Ljava/nio/charset/CodingErrorAction;)Ljava/nio/charset/CharsetDecoder;

    move-result-object p1

    .line 53
    new-instance v1, Lorg/json/JSONObject;

    invoke-virtual {v0}, Ljava/io/ByteArrayOutputStream;->toByteArray()[B

    move-result-object v0

    invoke-static {v0}, Ljava/nio/ByteBuffer;->wrap([B)Ljava/nio/ByteBuffer;

    move-result-object v0

    invoke-virtual {p1, v0}, Ljava/nio/charset/CharsetDecoder;->decode(Ljava/nio/ByteBuffer;)Ljava/nio/CharBuffer;

    move-result-object p1

    invoke-virtual {p1}, Ljava/nio/CharBuffer;->toString()Ljava/lang/String;

    move-result-object p1

    invoke-direct {v1, p1}, Lorg/json/JSONObject;-><init>(Ljava/lang/String;)V

    invoke-virtual {p0, v1}, Lio/memorydrive/pleos/SharedCourse$Companion;->parse(Lorg/json/JSONObject;)Lio/memorydrive/pleos/SharedCourse;

    move-result-object p1

    return-object p1
.end method
