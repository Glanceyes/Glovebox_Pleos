.class public final Lio/memorydrive/pleos/ParkedYouTubeController;
.super Ljava/lang/Object;
.source "YouTubeParkedPlayer.kt"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lio/memorydrive/pleos/ParkedYouTubeController$Companion;
    }
.end annotation

.annotation runtime Lkotlin/Metadata;
    d1 = {
        "\u0000C\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\u0008\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\u0008\u0002\n\u0002\u0018\u0002\n\u0002\u0008\u0003\n\u0002\u0008\u0008*\u0001\u0016\u0008\u0001\u0018\u0000 \u001c2\u00020\u0001:\u0001\u001cB+\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0008\u0012\u0004\u0012\u00020\t0\u0007\u00a2\u0006\u0004\u0008\n\u0010\u000bJ\u0006\u0010\u0018\u001a\u00020\tJ\u0006\u0010\u0019\u001a\u00020\tJ\u000e\u0010\u0010\u001a\u00020\t2\u0006\u0010\u001a\u001a\u00020\u000fJ\u0006\u0010\u001b\u001a\u00020\tR\u001a\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0008\u0012\u0004\u0012\u00020\t0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000c\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0011\u001a\u00020\u0012\u00a2\u0006\u0008\n\u0000\u001a\u0004\u0008\u0013\u0010\u0014R\u0010\u0010\u0015\u001a\u00020\u0016X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0017\u00a8\u0006\u001d"
    }
    d2 = {
        "Lio/memorydrive/pleos/ParkedYouTubeController;",
        "",
        "context",
        "Landroid/content/Context;",
        "id",
        "",
        "changed",
        "Lkotlin/Function1;",
        "Lio/memorydrive/pleos/YouTubePlayerState;",
        "",
        "<init>",
        "(Landroid/content/Context;Ljava/lang/String;Lkotlin/jvm/functions/Function1;)V",
        "handler",
        "Landroid/os/Handler;",
        "closed",
        "",
        "foreground",
        "view",
        "Landroid/webkit/WebView;",
        "getView",
        "()Landroid/webkit/WebView;",
        "poll",
        "io/memorydrive/pleos/ParkedYouTubeController$poll$1",
        "Lio/memorydrive/pleos/ParkedYouTubeController$poll$1;",
        "play",
        "pause",
        "value",
        "close",
        "Companion",
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

.field public static final Companion:Lio/memorydrive/pleos/ParkedYouTubeController$Companion;

.field private static final ORIGIN:Ljava/lang/String; = "https://io.memorydrive.pleos/"


# instance fields
.field private final changed:Lkotlin/jvm/functions/Function1;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Lkotlin/jvm/functions/Function1<",
            "Lio/memorydrive/pleos/YouTubePlayerState;",
            "Lkotlin/Unit;",
            ">;"
        }
    .end annotation
.end field

.field private closed:Z

.field private foreground:Z

.field private final handler:Landroid/os/Handler;

.field private final poll:Lio/memorydrive/pleos/ParkedYouTubeController$poll$1;

.field private final view:Landroid/webkit/WebView;


# direct methods
.method static constructor <clinit>()V
    .locals 2

    new-instance v0, Lio/memorydrive/pleos/ParkedYouTubeController$Companion;

    const/4 v1, 0x0

    invoke-direct {v0, v1}, Lio/memorydrive/pleos/ParkedYouTubeController$Companion;-><init>(Lkotlin/jvm/internal/DefaultConstructorMarker;)V

    sput-object v0, Lio/memorydrive/pleos/ParkedYouTubeController;->Companion:Lio/memorydrive/pleos/ParkedYouTubeController$Companion;

    const/16 v0, 0x8

    sput v0, Lio/memorydrive/pleos/ParkedYouTubeController;->$stable:I

    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Ljava/lang/String;Lkotlin/jvm/functions/Function1;)V
    .locals 8
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/content/Context;",
            "Ljava/lang/String;",
            "Lkotlin/jvm/functions/Function1<",
            "-",
            "Lio/memorydrive/pleos/YouTubePlayerState;",
            "Lkotlin/Unit;",
            ">;)V"
        }
    .end annotation

    const-string v0, "context"

    invoke-static {p1, v0}, Lkotlin/jvm/internal/Intrinsics;->checkNotNullParameter(Ljava/lang/Object;Ljava/lang/String;)V

    const-string v0, "id"

    invoke-static {p2, v0}, Lkotlin/jvm/internal/Intrinsics;->checkNotNullParameter(Ljava/lang/Object;Ljava/lang/String;)V

    const-string v0, "changed"

    invoke-static {p3, v0}, Lkotlin/jvm/internal/Intrinsics;->checkNotNullParameter(Ljava/lang/Object;Ljava/lang/String;)V

    .line 33
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p3, p0, Lio/memorydrive/pleos/ParkedYouTubeController;->changed:Lkotlin/jvm/functions/Function1;

    .line 34
    new-instance p3, Landroid/os/Handler;

    invoke-static {}, Landroid/os/Looper;->getMainLooper()Landroid/os/Looper;

    move-result-object v0

    invoke-direct {p3, v0}, Landroid/os/Handler;-><init>(Landroid/os/Looper;)V

    iput-object p3, p0, Lio/memorydrive/pleos/ParkedYouTubeController;->handler:Landroid/os/Handler;

    const/4 v0, 0x1

    .line 36
    iput-boolean v0, p0, Lio/memorydrive/pleos/ParkedYouTubeController;->foreground:Z

    .line 37
    new-instance v7, Landroid/webkit/WebView;

    invoke-direct {v7, p1}, Landroid/webkit/WebView;-><init>(Landroid/content/Context;)V

    .line 38
    const-string p1, "youtube-official-webview"

    invoke-virtual {v7, p1}, Landroid/webkit/WebView;->setTag(Ljava/lang/Object;)V

    const/high16 p1, -0x1000000

    .line 39
    invoke-virtual {v7, p1}, Landroid/webkit/WebView;->setBackgroundColor(I)V

    .line 40
    invoke-virtual {v7}, Landroid/webkit/WebView;->getSettings()Landroid/webkit/WebSettings;

    move-result-object p1

    invoke-virtual {p1, v0}, Landroid/webkit/WebSettings;->setJavaScriptEnabled(Z)V

    .line 41
    invoke-virtual {v7}, Landroid/webkit/WebView;->getSettings()Landroid/webkit/WebSettings;

    move-result-object p1

    invoke-virtual {p1, v0}, Landroid/webkit/WebSettings;->setDomStorageEnabled(Z)V

    .line 42
    invoke-virtual {v7}, Landroid/webkit/WebView;->getSettings()Landroid/webkit/WebSettings;

    move-result-object p1

    const/4 v1, 0x0

    invoke-virtual {p1, v1}, Landroid/webkit/WebSettings;->setMediaPlaybackRequiresUserGesture(Z)V

    .line 43
    invoke-virtual {v7}, Landroid/webkit/WebView;->getSettings()Landroid/webkit/WebSettings;

    move-result-object p1

    invoke-virtual {p1, v1}, Landroid/webkit/WebSettings;->setAllowFileAccess(Z)V

    .line 44
    invoke-virtual {v7}, Landroid/webkit/WebView;->getSettings()Landroid/webkit/WebSettings;

    move-result-object p1

    invoke-virtual {p1, v1}, Landroid/webkit/WebSettings;->setAllowContentAccess(Z)V

    .line 45
    invoke-virtual {v7}, Landroid/webkit/WebView;->getSettings()Landroid/webkit/WebSettings;

    move-result-object p1

    invoke-virtual {p1, v0}, Landroid/webkit/WebSettings;->setMixedContentMode(I)V

    .line 46
    new-instance p1, Landroid/webkit/WebChromeClient;

    invoke-direct {p1}, Landroid/webkit/WebChromeClient;-><init>()V

    invoke-virtual {v7, p1}, Landroid/webkit/WebView;->setWebChromeClient(Landroid/webkit/WebChromeClient;)V

    .line 47
    new-instance p1, Lio/memorydrive/pleos/ParkedYouTubeController$view$1$1;

    invoke-direct {p1}, Lio/memorydrive/pleos/ParkedYouTubeController$view$1$1;-><init>()V

    check-cast p1, Landroid/webkit/WebViewClient;

    invoke-virtual {v7, p1}, Landroid/webkit/WebView;->setWebViewClient(Landroid/webkit/WebViewClient;)V

    .line 53
    sget-object p1, Lio/memorydrive/pleos/ParkedYouTubeController;->Companion:Lio/memorydrive/pleos/ParkedYouTubeController$Companion;

    invoke-virtual {p1, p2}, Lio/memorydrive/pleos/ParkedYouTubeController$Companion;->playerHtml$app_release(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v3

    invoke-static {v7, v3}, Lio/memorydrive/pleos/CockpitSurface;->loadOfficialPlayer(Landroid/webkit/WebView;Ljava/lang/String;)V

    .line 37
    iput-object v7, p0, Lio/memorydrive/pleos/ParkedYouTubeController;->view:Landroid/webkit/WebView;

    .line 55
    new-instance p1, Lio/memorydrive/pleos/ParkedYouTubeController$poll$1;

    invoke-direct {p1, p0}, Lio/memorydrive/pleos/ParkedYouTubeController$poll$1;-><init>(Lio/memorydrive/pleos/ParkedYouTubeController;)V

    iput-object p1, p0, Lio/memorydrive/pleos/ParkedYouTubeController;->poll:Lio/memorydrive/pleos/ParkedYouTubeController$poll$1;

    .line 72
    check-cast p1, Ljava/lang/Runnable;

    invoke-virtual {p3, p1}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    return-void
.end method

.method public static final synthetic access$getChanged$p(Lio/memorydrive/pleos/ParkedYouTubeController;)Lkotlin/jvm/functions/Function1;
    .locals 0

    .line 32
    iget-object p0, p0, Lio/memorydrive/pleos/ParkedYouTubeController;->changed:Lkotlin/jvm/functions/Function1;

    return-object p0
.end method

.method public static final synthetic access$getClosed$p(Lio/memorydrive/pleos/ParkedYouTubeController;)Z
    .locals 0

    .line 32
    iget-boolean p0, p0, Lio/memorydrive/pleos/ParkedYouTubeController;->closed:Z

    return p0
.end method

.method public static final synthetic access$getForeground$p(Lio/memorydrive/pleos/ParkedYouTubeController;)Z
    .locals 0

    .line 32
    iget-boolean p0, p0, Lio/memorydrive/pleos/ParkedYouTubeController;->foreground:Z

    return p0
.end method

.method public static final synthetic access$getHandler$p(Lio/memorydrive/pleos/ParkedYouTubeController;)Landroid/os/Handler;
    .locals 0

    .line 32
    iget-object p0, p0, Lio/memorydrive/pleos/ParkedYouTubeController;->handler:Landroid/os/Handler;

    return-object p0
.end method


# virtual methods
.method public final close()V
    .locals 2

    .line 79
    iget-boolean v0, p0, Lio/memorydrive/pleos/ParkedYouTubeController;->closed:Z

    if-eqz v0, :cond_0

    return-void

    .line 80
    :cond_0
    invoke-virtual {p0}, Lio/memorydrive/pleos/ParkedYouTubeController;->pause()V

    const/4 v0, 0x1

    iput-boolean v0, p0, Lio/memorydrive/pleos/ParkedYouTubeController;->closed:Z

    iget-object v0, p0, Lio/memorydrive/pleos/ParkedYouTubeController;->handler:Landroid/os/Handler;

    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeCallbacksAndMessages(Ljava/lang/Object;)V

    .line 81
    iget-object v0, p0, Lio/memorydrive/pleos/ParkedYouTubeController;->view:Landroid/webkit/WebView;

    invoke-virtual {v0}, Landroid/webkit/WebView;->stopLoading()V

    iget-object v0, p0, Lio/memorydrive/pleos/ParkedYouTubeController;->view:Landroid/webkit/WebView;

    const-string v1, "about:blank"

    invoke-virtual {v0, v1}, Landroid/webkit/WebView;->loadUrl(Ljava/lang/String;)V

    iget-object v0, p0, Lio/memorydrive/pleos/ParkedYouTubeController;->view:Landroid/webkit/WebView;

    invoke-virtual {v0}, Landroid/webkit/WebView;->destroy()V

    return-void
.end method

.method public final foreground(Z)V
    .locals 0

    .line 77
    iput-boolean p1, p0, Lio/memorydrive/pleos/ParkedYouTubeController;->foreground:Z

    if-nez p1, :cond_0

    invoke-virtual {p0}, Lio/memorydrive/pleos/ParkedYouTubeController;->pause()V

    :cond_0
    return-void
.end method

.method public final getView()Landroid/webkit/WebView;
    .locals 1

    .line 37
    iget-object v0, p0, Lio/memorydrive/pleos/ParkedYouTubeController;->view:Landroid/webkit/WebView;

    return-object v0
.end method

.method public final pause()V
    .locals 3

    .line 76
    iget-boolean v0, p0, Lio/memorydrive/pleos/ParkedYouTubeController;->closed:Z

    if-nez v0, :cond_0

    iget-object v0, p0, Lio/memorydrive/pleos/ParkedYouTubeController;->view:Landroid/webkit/WebView;

    const-string v1, "window.memoryPlayer && window.memoryPlayer.pauseVideo()"

    const/4 v2, 0x0

    invoke-virtual {v0, v1, v2}, Landroid/webkit/WebView;->evaluateJavascript(Ljava/lang/String;Landroid/webkit/ValueCallback;)V

    :cond_0
    return-void
.end method

.method public final play()V
    .locals 3

    .line 75
    iget-boolean v0, p0, Lio/memorydrive/pleos/ParkedYouTubeController;->closed:Z

    if-nez v0, :cond_0

    iget-boolean v0, p0, Lio/memorydrive/pleos/ParkedYouTubeController;->foreground:Z

    if-eqz v0, :cond_0

    iget-object v0, p0, Lio/memorydrive/pleos/ParkedYouTubeController;->view:Landroid/webkit/WebView;

    const-string v1, "window.memoryPlayer && window.memoryPlayer.playVideo()"

    const/4 v2, 0x0

    invoke-virtual {v0, v1, v2}, Landroid/webkit/WebView;->evaluateJavascript(Ljava/lang/String;Landroid/webkit/ValueCallback;)V

    :cond_0
    return-void
.end method
