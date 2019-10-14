package com.techomite.math.pluggr;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by Steven Yu on 6/6/2019.
 */

public class MathJaxWebView extends WebView {
    public MathJaxWebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setBackgroundColor(Color.TRANSPARENT);
        getSettings().setJavaScriptEnabled(true);
        getSettings().setBuiltInZoomControls(false);
        setHorizontalScrollBarEnabled(false);
    }

    public void setText(final String text) {
        loadDataWithBaseURL("http://bar",
                "<script type=\"text/x-mathjax-config\">" +
                        "  MathJax.Hub.Config({" +
                        "extensions: [\"tex2jax.js\"],messageStyle:\"none\"," +
                        "jax: [\"input/TeX\",\"output/HTML-CSS\"]," +
                        "tex2jax: {inlineMath: [['$','$'],['\\\\(','\\\\)']]}" +
                        "});" +
                        "</script>" +
                        "<script type=\"text/javascript\" async src=\"file:///android_asset/MathJax/MathJax.js?config=TeX-AMS-MML_HTMLorMML\"></script>" +
                        "" +
                        "</head>" +
                        "" +
                        "<body>" +
                        text +
                        "</body>" +
                        "</html>", "text/html", "utf-8", "");
    }
}
