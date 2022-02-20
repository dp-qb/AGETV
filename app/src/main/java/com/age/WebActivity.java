package com.age;
import android.*;
import android.app.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.text.*;
import java.util.*;
import android.content.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import android.webkit.*;
import android.transition.*;
import android.opengl.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.*;

public class WebActivity extends Activity {
	TextView t调试 ;
	WebView webView;
	VideoView videoView; 
	boolean b捕获链接=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        setContentView(R.layout.web);
		t调试=findViewById(R.id.t调试);
		webView = findViewById(R.id.web_view);
		videoView = findViewById(R.id.video_view);
		getVideoUrl("https://web.age-spa.com:8443/#/play/20050031/2/1");
		
    }
	String s="";
    private void s筛选连接(final String url) {
		final MediaPlayer mediaPlayer = new MediaPlayer();
        try
		{
			mediaPlayer.setDataSource(url);
		}
		catch (IllegalStateException e)
		{}
		catch (SecurityException e)
		{}
		catch (IllegalArgumentException e)
		{}
		catch (IOException e)
		{}
		mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override//视频预装完成监听
				public void onPrepared(MediaPlayer mp) { 
					//z总时长 = videoView.getDuration();//获取视频的总时长
					//videoView.start();
					if(mediaPlayer.getDuration()>1000)
					{
						if(b捕获链接)
						{
							s="成功捕获链接"+url+"\n"+s;
							t调试.setText(s);
							videoView.setVideoPath(url);
							videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
								@Override//视频预装完成监听
								public void onPrepared(MediaPlayer mp) {
									videoView.start();
									}
							});
							b捕获链接=false;
						}
						//webView.loadData("<h1>Hello webView</h1>","text/html","utf-8");
						//webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
						//webView.setVisibility(View.GONE);//隐藏控件
						//webView.stopLoading();//停止加载     好像没用
						//webView.pauseTimers();//暂停计时器   js不能执行了
						//webView.clearCache(true);//清除缓存  没啥用
						//webView.clearHistory();//清除记录    没啥用
						webView.destroy();//破坏控件           成功销毁控件
						//webView.removeAllViews();//删除所有视图 成功删除
						//webView = null;                     没啥用
						mediaPlayer.release();
					}
				}
			});
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override//视频播放完成监听
				public void onCompletion(MediaPlayer mp) {
				}
			});
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
				@Override//视频播放错误监听
				public boolean onError(MediaPlayer mp, int what, int extra) {
					s="排除"+url+"\n"+s;
					t调试.setText(s);
					mediaPlayer.release();
					return true;
				}
			});
    }
	void getVideoUrl(String url)
	{
		//z中间列表.removeAllViews();
		//WebView webView=new WebView(this);
		//z中间列表.addView(webView);
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setPluginState(WebSettings.PluginState.ON);
		webSettings.setUseWideViewPort(true); // 关键点
		webSettings.setAllowFileAccess(true); // 允许访问文件
		webSettings.setSupportZoom(true); // 支持缩放
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 不加载缓存内容
		webSettings.setDatabaseEnabled(true);//是否开启数据库存储API权限
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {  
			webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);  
		}
		webView.getSettings().setUseWideViewPort(true);
		webSettings.setJavaScriptEnabled(true);//允许运行执行js脚本
		webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
		webSettings.setPluginState(WebSettings.PluginState.ON);//是否支持插件
		webSettings.setJavaScriptEnabled(true);//是否可以和js进行交互
		webSettings.setDomStorageEnabled(true);//设置是否开启DOM存储API权限，默认false，未开启，设置为true，WebView能够使用DOM storage API
		webSettings.setSupportMultipleWindows(false);//是否支持多窗口
		webSettings.setMediaPlaybackRequiresUserGesture(false);//不需要手势即可触发媒体播放
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
		{
			webSettings.setMediaPlaybackRequiresUserGesture(true);
		}
		if (Build.VERSION.SDK_INT >= 16)
		{
			webSettings.setAllowFileAccessFromFileURLs(true);
			webSettings.setAllowUniversalAccessFromFileURLs(true);
		}
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);//设置脚本是否允许自动打开弹窗，默认false，不允许
		//webSettings.setLoadsImagesAutomatically(true);//是否需要加载图片
		webSettings.setLoadsImagesAutomatically(false);//是否需要加载图片
		CookieManager instance = CookieManager.getInstance();
		if (Build.VERSION.SDK_INT < 21)
		{
			CookieSyncManager.createInstance(this.getApplicationContext());
		}
		instance.setAcceptCookie(true);
		if (Build.VERSION.SDK_INT >= 21)
		{
			instance.setAcceptThirdPartyCookies(webView, true);
		}
		webView.setWebViewClient(new WebViewClient(){
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) 
				{
					view.loadUrl(url);                
					return true;
				}
                @Override   
                public void onLoadResource(WebView view, String url)
				//该方法在加载页面资源时会回调，每一个资源（比如图片）的加载都会调用一次。
				{
					s筛选连接(url);
                    super.onLoadResource(view, url);
                }
            });
        webView.loadUrl(url);
    }
}

