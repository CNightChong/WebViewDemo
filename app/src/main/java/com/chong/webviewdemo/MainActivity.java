package com.chong.webviewdemo;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private WebView mWebView;
    private Button mBtn;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = (WebView) findViewById(R.id.web_view);
        mBtn = (Button) findViewById(R.id.btn);
        mProgressDialog = new ProgressDialog(this);
        // js演示
        jsDemo();

        /*
        WebViewClient:在影响View的事件到来时，会通过WebViewClient中的方法回调通知用户。
        WebChromeClient：当影响浏览器的事件到来时，就会通过WebChromeClient中的方法回调通知用法。
         */

        // WebViewClient演示
//        WebViewClientDemo1();
//        WebViewClientDemo2();
//        WebViewClientDemo3();

        // WebChromeClient演示
//        WebChromeClientDemo1();
//        WebChromeClientDemo2();
//        WebChromeClientDemo3();
    }


    /**
     * WebViewClient演示
     */
    private void WebViewClientDemo1() {
        mBtn.setVisibility(View.GONE);
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.loadUrl("http://blog.csdn.net/harvic880925");
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;

                // 拦截 url = blog.csdn.net
                // 写法1 start
//                if (url.contains("blog.csdn.net")) {
//                    view.loadUrl("http://www.baidu.com");
//                } else {
//                    view.loadUrl(url);
//                }
//                return true;
                // 写法1 end

                // 写法2，与写法1效果相同
//                if (url.contains("blog.csdn.net")) {
//                    view.loadUrl("http://www.baidu.com");
//                }
//                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgressDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressDialog.hide();
            }
        });

    }

    private void WebViewClientDemo2() {
        mBtn.setVisibility(View.GONE);
        mWebView.getSettings().setJavaScriptEnabled(true);

        // 加载一个错误页面
        mWebView.loadUrl("http://www.baidu");
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                // 加载返回错误时，重新加载错误页面
                mWebView.loadUrl("file:///android_asset/error.html");
            }
        });
    }

    private void WebViewClientDemo3() {
        mBtn.setVisibility(View.GONE);
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.loadUrl("file:///android_asset/web2.html");
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                try {
                    // 拦截网络图片
                    if (url.equals("http://localhost/img.png")) {
                        AssetFileDescriptor fileDescriptor = getAssets().openFd("img.png");
                        InputStream stream = fileDescriptor.createInputStream();
                        return new WebResourceResponse("image/png", "UTF-8", stream);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return super.shouldInterceptRequest(view, url);
            }
        });
    }

    /**
     * WebChromeClient演示
     */
    private void WebChromeClientDemo1() {
        mBtn.setVisibility(View.GONE);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
        // 设置WebChromeClient实例
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.loadUrl("file:///android_asset/web3.html");
    }

    private void WebChromeClientDemo2() {
        mBtn.setVisibility(View.GONE);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
        // 设置WebChromeClient实例
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebChromeClient(new WebChromeClient() {
            // 拦截js中alert()方法
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                // 表示点击了弹出框的确定按钮
                result.confirm();
                // 表示告诉WebView已经拦截了alert()方法
                return true;
            }

            // 拦截js中控制台输出
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Toast.makeText(MainActivity.this, consoleMessage.message(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        mWebView.loadUrl("file:///android_asset/web3.html");
    }

    private void WebChromeClientDemo3() {
        mBtn.setVisibility(View.GONE);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
        // 设置WebChromeClient实例
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                Log.e("WebView", "progress == " + newProgress);
                super.onProgressChanged(view, newProgress);
            }
        });
        mWebView.loadUrl("http://blog.csdn.net/harvic880925");
    }


    /**
     * js演示
     */
    private void jsDemo() {
        // 设置WebViewClient实例
        mWebView.setWebViewClient(new WebViewClient());

        WebSettings webSettings = mWebView.getSettings();
        // 开启javascript支持
        webSettings.setJavaScriptEnabled(true);

//        // 优先使用缓存
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        // 不使用缓存
//        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//
//        //设置任意比例缩放
//        webSettings.setUseWideViewPort(true);
//        webSettings.setLoadWithOverviewMode(true);
//
//        // 设置可以支持缩放
//        webSettings.setSupportZoom(true);
//        // 设置出现缩放工具
//        webSettings.setBuiltInZoomControls(true);
//
//        // 设置支持获取手势焦点，用于手动输入用户名、密码或其他
//        mWebView.requestFocusFromTouch();
//
        mWebView.addJavascriptInterface(new JSBridge(), "android");
        mWebView.loadUrl("file:///android_asset/web1.html");

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mWebView.loadUrl("http://www.w3school.com.cn/");
                // 加载本地assets中文件
//                mWebView.loadUrl("file:///android_asset/web1.html");

                // 调用js中sum方法
//                mWebView.loadUrl("javascript:sum(3,8)");

                // 接收js中getGreetings方法返回值
                testEvaluateJavascript(mWebView);
            }
        });
    }

    public class JSBridge {
        @JavascriptInterface
        public void toastMessage(String message) {
            Toast.makeText(getApplicationContext(), "通过Native传递的Toast:" + message, Toast.LENGTH_LONG).show();
        }

        // 4.4以前的版本需要 java调用js方法，js再调用java方法返回计算的值
        @JavascriptInterface
        public void onSumResult(int result) {
            Toast.makeText(getApplicationContext(), "received result = " + result, Toast.LENGTH_SHORT).show();
        }

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void testEvaluateJavascript(WebView webView) {
        // 4.4以后的版本通过evaluateJavascript调用JS中的getGreetings()方法，可以向其中添加结果回调，来接收JS的return值
        webView.evaluateJavascript("getGreetings()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Toast.makeText(getApplicationContext(), "onReceiveValue value = " + value, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //改写物理返回键的逻辑
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();//返回上一页面
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
