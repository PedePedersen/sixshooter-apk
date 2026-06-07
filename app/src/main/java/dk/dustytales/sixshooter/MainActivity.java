package dk.dustytales.sixshooter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Fullscreen WebView-skal omkring sixshooter.html (i assets/, med
 * assets/-undermappen til fonte/lyde/baggrund).
 *
 * Konfigurationen matcher præcis det, appen bruger:
 *  - JavaScript (template literals, arrow functions): setJavaScriptEnabled
 *  - localStorage (gemte hold + seneste tilstand): setDomStorageEnabled —
 *    PÅKRÆVET, ellers er localStorage null på file:// i Android WebView
 *  - <audio>-elementer (spin/bang/klik) afspillet fra knap-tap:
 *    setMediaPlaybackRequiresUserGesture(false) så lyden aldrig blokeres
 *  - file:///android_asset-load: setAllowFileAccess (API 30+ defaulter false)
 *  - meta viewport: setUseWideViewPort + setLoadWithOverviewMode
 *  - vw-baseret layout: setTextZoom(100) så systemets tekstskalering ikke
 *    forvrider proportionerne
 * Ingen netværksadgang: manifestet har ingen INTERNET-permission, og
 * setBlockNetworkLoads(true) gør det eksplicit i WebView'en.
 */
public class MainActivity extends Activity {

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Lad vinduet strække sig ind i display-cutout'en (kamerahul/notch).
        // Androids DEFAULT-mode letterboxer fullscreen-vinduer udenom
        // cutout'en, så HTML'ens viewport-fit=cover ellers aldrig får
        // noget areal at male på dér. SHORT_EDGES (API 28+) åbner området;
        // env(safe-area-inset-*) i sixshooter.html holder UI'et fri af det.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }

        webView = new WebView(this);
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setUseWideViewPort(true);
        s.setLoadWithOverviewMode(true);
        s.setTextZoom(100);
        s.setBlockNetworkLoads(true);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        // Pergament-agtig baggrund bag WebView'en mens siden initialiseres,
        // så der ikke blinker hvidt ved opstart.
        webView.setBackgroundColor(0xFFD8C59A);

        setContentView(webView);
        webView.loadUrl("file:///android_asset/sixshooter.html");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) skjulSystemBarer();
    }

    /** Immersive sticky fullscreen — barerne kan swipes frem og forsvinder selv igen. */
    private void skjulSystemBarer() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) webView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) webView.onResume();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) webView.destroy();
        super.onDestroy();
    }
}
