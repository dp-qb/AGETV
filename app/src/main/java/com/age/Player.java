package com.age;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.content.Intent;
import android.widget.TextView;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebViewClient;
import android.content.SharedPreferences;
import android.webkit.JavascriptInterface;
import android.widget.VideoView;
import android.widget.MediaController;
import android.media.MediaPlayer;
import android.widget.RelativeLayout;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import android.view.View;
import android.widget.Toast;
import android.net.Uri;
import android.view.KeyEvent;
import android.widget.Button;
import android.os.Handler;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import java.io.IOException;
import android.os.Message;
import android.widget.LinearLayout;
import android.graphics.Color;
import java.util.List;
import android.widget.*;
import android.preference.*;
import BanGuMi;
import java.io.*;
import android.view.ViewGroup.*;
import java.net.*;
public class Player extends Activity
{
    TextView z中间提示;//左上角提示
	ScrollView 选集列表;//右边选集容器
	ScrollView 设置列表;//左边设置
    VideoView 播放器;//视频播放器
	MediaPlayer s视频控制器;
	BanGuMi b=new BanGuMi();
	Float 倍速;
	/*
	 Handler handler = new Handler();//记录播放时间的线程
	 Runnable runnable = new Runnable() {
	 public void run() {
	 b.mtime=播放器.getCurrentPosition()+"";
	 } 
	 };*/
    protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        setContentView(R.layout.player);

		Intent intent = getIntent();  
        b.id = intent.getStringExtra("id");
		/*
		 if (b.new_name != null)
		 {
		 播放器.setVideoURI(Uri.parse(b.new_name));
		 }*/
        //测试 = findViewById(R.id.msg);
		z中间提示 = findViewById(R.id.正中间提示);
        播放器 = findViewById(R.id.video);
		选集列表 = findViewById(R.id.选集列表);
		设置列表 = findViewById(R.id.设置列表);
        选集列表.setVisibility(View.GONE);
		设置列表.setVisibility(View.GONE);
		SharedPreferences 数据持久化= getSharedPreferences("sp", Activity.MODE_PRIVATE); //实例化SharedPreferences对象（第一步）
		倍速 = 数据持久化.getFloat("倍速", 1.0f);
		RadioGroup rgp = findViewById(R.id.倍速);
		rgp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(RadioGroup radioGroup, int i)
				{
					RadioButton rb_temp = findViewById(radioGroup.getCheckedRadioButtonId());
					Toast.makeText(Player.this, String.format("你选择了%s", rb_temp.getText().toString()), Toast.LENGTH_SHORT).show();
					SharedPreferences 数据持久化= getSharedPreferences("sp", Activity.MODE_PRIVATE); //实例化SharedPreferences对象（第一步）
					SharedPreferences.Editor editor = 数据持久化.edit(); //实例化SharedPreferences.Editor对象（第二步）
					倍速 = Float.parseFloat(rb_temp.getText().toString());//字符串转小数
					editor.putFloat("倍速", 倍速);//保存数据
					editor.commit(); //提交当前数据 
					改变播放速度(倍速);
				}
			});

		播放器.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override//视频预装完成监听
				public void onPrepared(MediaPlayer mp)
				{
					//设置屏幕提示信息为空
					Toast.makeText(Player.this, "视频装载完成ο(=•ω＜=)ρ⌒☆", Toast.LENGTH_SHORT).show();	
					MyMediaController playMediaController = new MyMediaController(Player.this);//初始化控制器
					播放器.setMediaController(playMediaController);//给播放器绑控制器
					s视频控制器 = mp;
					改变播放速度(倍速);//(0.5f, 0.75f, 1f, 1.25f, 1.5f, 1.75f, 2.0f)
					z中间提示.setText("");
					播放器.start();
					//handler.postDelayed(runnable,10000); //每10秒记录一次当前播放进度
				}
			});
		播放器.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override//视频播放完成监听
				public void onCompletion(MediaPlayer mp)
				{
					//判断视频的总长度与当前长度是否在误差区间内
					if (Math.abs(播放器.getDuration() - 播放器.getCurrentPosition()) > 1000)
					{
						z中间提示.setText("播放错误,可能无网络连接 o_O???");
					}
					else
					{
						z中间提示.setText("播放完成n(*≧▽≦*)n");
						//next(i);播放结束      20200122?playid=2_12
						String s= b.id.split("\\?|=|_")[3];
						int i = Integer.parseInt(s);
						i++;
						b.id = b.id.substring(0, b.id.length() - s.length()) + i;
						getVideoUrl("https://www.agefans.net/play/" + b.id);
					}
					//handler.removeCallbacks(runnable);
				}
			});
		播放器.setOnErrorListener(new MediaPlayer.OnErrorListener() {
				@Override//视频播放错误监听
				public boolean onError(MediaPlayer mp, int what, int extra)
				{
					z中间提示.setText("播放器发生未知错误！( ´_ゝ` )\n匹配到的视频-"+p屏幕中间文本);
					播放器.seekTo(播放器.getCurrentPosition());//跳转到暂停时保存的位置
					if (播放器.isPlaying())
					{
					}
					else
					{mp.start();}
					//点击弹框会调用播放完毕
					return true;//返回true不显示弹框
				}
			});
		播放器.setOnInfoListener(new MediaPlayer.OnInfoListener(){
				@Override//视频卡顿和停止卡顿监听
				public boolean onInfo(MediaPlayer mp, int what, int extra)
				{

					switch (what)
					{
						case MediaPlayer.MEDIA_INFO_BUFFERING_START:
							//设置屏幕显示信息，开始卡顿
							z中间提示.setText("视频卡顿，加载中....─=≡Σ((( つ•̀ω•́)つ!!!");
							break ;
						case MediaPlayer.MEDIA_INFO_BUFFERING_END:
							//设置屏幕显示信息，卡顿结束
							z中间提示.setText("");
							break ;
					}
					return true;
				}
			}) ;
		new Doc().getHtml("https://www.agefans.net/detail/" + b.getid_8(), new Doc.Html(){
				@Override
				public void getDoc(Document doc)
				{
					//title = doc.title();
					Elements resultLinks = doc.select("div.main0 ul");//查找ul
					for (Element res:resultLinks)//遍历结果
					{
						Elements r=res.getElementsByTag("a");//查找a标签
						if (r.size() > 0)
						{
							LinearLayout ll = new LinearLayout(Player.this);
							ll.setOrientation(1);
							LinearLayout box=(LinearLayout) 选集列表.getChildAt(0);
							box.addView(ll);
							for (Element e:r)
							{
								String href=e.attr("href");
								ll.addView(newbutton(e.attr("title"), href.substring(6, href.length())));
							}
						}
					}		
					b.name = doc.select("h4.detail_imform_name").text();//标题
					//doc.select("p").text();//简介
				}
			});
        getVideoUrl("https://www.agefans.net/play/" + b.id);

    }
    int 当前播放位置 = 0;
    @Override
    protected void onPause()
	{//页面转到后台
        //如果当前页面暂停则保存当前播放位置，全局变量保存
        当前播放位置 = 播放器.getCurrentPosition(); //先获取再stopPlay(),原因自己看源码
        播放器.stopPlayback(); //停止播放视频文件
        super.onPause();
    }
    @Override
    protected void onResume()
	{//页面回到前台
        if (当前播放位置 > 0)
		{
            //此处为更好的用户体验,可添加一个progressBar,有些客户端会在这个过程中隐藏底下控制栏,这方法也不错
            播放器.start();
            播放器.seekTo(当前播放位置);//跳转到暂停时保存的位置
            当前播放位置 = 0;
        }
        super.onResume();
    }

    Button newbutton(final String str, final String id)
	{
        Button but;
        but = new Button(this);
        but.setText(str);
		String ids[]= id.split("\\?|=|_");
		int i = Integer.parseInt(ids[2] + ids[3]);
		but.setId(i);
        but.setBackgroundResource(R.drawable.button_style);
        but.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v)
				{
                    b.id = id;
					b.new_name=str;
                    getVideoUrl("https://www.agefans.net/play/" + id);
                    menu();
                }
            });
        return but;
    }
	public void 改变播放速度(Float 速度)
	{
		if (s视频控制器 != null)
		{
			if (s视频控制器.isPlaying())
			{
				s视频控制器.setPlaybackParams(s视频控制器.getPlaybackParams().setSpeed(倍速));
			}
			else
			{
				s视频控制器.setPlaybackParams(s视频控制器.getPlaybackParams().setSpeed(倍速));
				播放器.start();
			}
			//(0.5f, 0.75f, 1f, 1.25f, 1.5f, 1.75f, 2.0f)
		}
	}
//暂停或恢复
    void 暂停或恢复() 
    {
        if (播放器.isPlaying())
		{
            播放器.pause();
		}
        else
		{
			播放器.start();
			z中间提示.setText("");
		}
    }
    public void menu()
    {
		if (选集列表.getVisibility() != View.VISIBLE)
		{
			选集列表.setVisibility(View.VISIBLE);
			设置列表.setVisibility(View.VISIBLE);
			if(b.id.length()>=19)
			{
			String ids[]= b.id.split("\\?|=|_");
			int i = Integer.parseInt(ids[2] + ids[3]);
			Button but=选集列表.findViewById(i);
			but.setFocusable(true);//用键盘是否能获得焦点
			but.setFocusableInTouchMode(true);//触摸是否能获得焦点
			but.requestFocus();//获得焦点
			}else
			{
				选集列表.setVisibility(View.VISIBLE);
				设置列表.setVisibility(View.VISIBLE);
				//String s="";
				LinearLayout box=(LinearLayout) 选集列表.getChildAt(0);
				if (box.getChildCount() > 0)
				{//横列表
					//s += "共有" + box.getChildCount() + "列控件";
					LinearLayout but_ll =(LinearLayout) box.getChildAt(0);
					if (but_ll.getChildCount() > 0)
					{//竖列表
						//s += "第一列有" + but_ll.getChildCount() + "个控件";
						Button but=(Button) but_ll.getChildAt(0);
						but.setFocusable(true);//用键盘是否能获得焦点
						but.setFocusableInTouchMode(true);//触摸是否能获得焦点
						but.requestFocus();//获得焦点
						//Toast.makeText(this, but.getText() + "获得焦点：", Toast.LENGTH_SHORT).show();

					}
				}
			}
		}
		else
		{
			选集列表.setVisibility(View.GONE);
			设置列表.setVisibility(View.GONE);
		}
    }
    private long s上次点返回时 = 0;//两次返回键间隔时间
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
	{
        switch (keyCode)
		{
            case KeyEvent.KEYCODE_M:menu();return true;
			case KeyEvent.KEYCODE_MENU:menu();return true;
			case KeyEvent.KEYCODE_DPAD_CENTER:暂停或恢复();return true;
            case KeyEvent.KEYCODE_SPACE:暂停或恢复();return true;
            case KeyEvent.KEYCODE_ENTER:暂停或恢复();return true;
            //case KeyEvent.KEYCODE_DPAD_UP:暂停或恢复();return true;
            //case KeyEvent.KEYCODE_DPAD_DOWN:暂停或恢复();return true;
            //case KeyEvent.KEYCODE_DPAD_LEFT:();return true;
            //case KeyEvent.KEYCODE_DPAD_RIGHT:暂停或恢复();return true;
            case KeyEvent.KEYCODE_BACK:
                if ((System.currentTimeMillis() - s上次点返回时) > 1000)
				{
                    Toast.makeText(Player.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    s上次点返回时 = System.currentTimeMillis();
					menu();
                }
				else
				{
                    finish();
                    System.exit(0);
                }
                return true;
            default:
                //Toast.makeText(this, "未知按键，继续传递键值码："+keyCode, Toast.LENGTH_SHORT).show();
				return super.onKeyDown(keyCode, event);
        }
    }
	void savejj(BanGuMi jb)
	{
		SharedPreferences 数据持久化= getSharedPreferences(jb.getid_8(), Activity.MODE_PRIVATE); //实例化SharedPreferences对象（第一步）
		SharedPreferences.Editor editor = 数据持久化.edit(); //实例化SharedPreferences.Editor对象（第二步）
		//Toast.makeText(Player.this,jb.id+":"+jb.wd+":"+jb.name+":"+jb.mtime+":"+jb.new_name, Toast.LENGTH_SHORT).show();
		editor.putBoolean("isnew", jb.isnew); //保存数据 
		editor.putString("id", jb.id);
		editor.putString("wd", jb.wd);
		editor.putString("name", jb.name);
		editor.putString("mtime", jb.mtime);
		editor.putString("new_name", jb.new_name);
		editor.commit(); //提交当前数据 
	}
    void g关闭浏览器调用播放器(WebView v, String t)
    {
		savejj(b);
        播放器.setVideoURI(Uri.parse(t));
        //Toast.makeText(Player.this,t+":"+Uri.parse(t), Toast.LENGTH_SHORT).show();
        v.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
        v.setVisibility(View.GONE);//隐藏控件
        v.stopLoading();//停止加载
        v.pauseTimers();//暂停计时器
        v.clearCache(true);//清除缓存
        v.clearHistory();//清除记录
        v.destroy();//破坏控件
        v.removeAllViews();//删除所有视图
        v = null;
    }
    void getVideoUrl(String url)
	{
		WebView webView=new WebView(this); 
        WebSettings mWebSettings=webView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setDefaultTextEncodingName("utf-8");
        mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebSettings.setPluginState(WebSettings.PluginState.ON);
        mWebSettings.setDisplayZoomControls(false);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setAllowContentAccess(true);
        mWebSettings.setSupportZoom(true);
        mWebSettings.setAllowContentAccess(true);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setBuiltInZoomControls(true);// 隐藏缩放按钮
        mWebSettings.setUseWideViewPort(true);// 可任意比例缩放
        mWebSettings.setLoadWithOverviewMode(true);// setUseWideViewPort方法设置webview推荐使用的窗口。setLoadWithOverviewMode方法是设置webview加载的页面的模式。
        mWebSettings.setSavePassword(true);
        mWebSettings.setSaveFormData(true);// 保存表单数据
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setTextZoom(100);
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setSupportMultipleWindows(true);// 不知是啥总之得加上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
            mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
		{
            mWebSettings.setMediaPlaybackRequiresUserGesture(true);
        }
        if (Build.VERSION.SDK_INT >= 16)
		{
            mWebSettings.setAllowFileAccessFromFileURLs(true);
            mWebSettings.setAllowUniversalAccessFromFileURLs(true);
        }
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebSettings.setLoadsImagesAutomatically(true);
        mWebSettings.setAppCacheEnabled(true);
        mWebSettings.setAppCachePath(this.getCacheDir().getAbsolutePath());
        mWebSettings.setDatabaseEnabled(true);
        mWebSettings.setGeolocationDatabasePath(this.getDir("database", 0).getPath());
        mWebSettings.setGeolocationEnabled(true);
        //启用cookie
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
                public void onLoadResource(WebView view, String url)
				{
					Uri l链接 = Uri.parse(url);
/*
					// 协议
					String scheme = mUri.getScheme();
					// 域名+端口号+路径+参数
					String scheme_specific_part = mUri.getSchemeSpecificPart();
					// 域名+端口号
					String authority = mUri.getAuthority();
					// fragment
					String fragment = mUri.getFragment();
					// 域名
					String host = mUri.getHost();
					// 端口号
					int port = mUri.getPort();
					// 路径
					String path = mUri.getPath();
					// 参数
					String query = mUri.getQuery();
					*/
					String host = l链接.getHost();
					switch (host)
					{
						case "play.agefans.net":
							z中间提示. setText( "视频API-" +l链接);
							break ;
						case "www.agefans.net":
							z中间提示. setText("本站-" + l链接);
							//p屏幕中间文本 ="本站-" + l链接+"\n"+p屏幕中间文本;
							break ;
						case "p0.pstatp.com":
							z中间提示. setText( "顶图-" + l链接);
							break ;
						case "s3.pstatp.com":
							z中间提示. setText("加载js文件-" + l链接);
							break ;
						case "cdn.radius-america.com":
							z中间提示. setText("加载js文件-" + l链接);
							break ;
						case "hm.baidu.com":
							z中间提示. setText("百度统计-" + l链接);
							break ;
						case "pic.rmb.bdstatic.com":
							z中间提示. setText("封面图服务器-" + l链接);
							break ;
						case "sc04.alicdn.com":
							z中间提示. setText("缩略图服务器-" + l链接);
							break ;
						case "p0.pstatp.com":
							z中间提示. setText("广告-" + l链接);
							break ;
						case "v1.shankuwang.com":
							z中间提示. setText("啊哦！已经开启防盗！请联系管理员.-" + l链接);
							break ;
						case "s0.pstatp.com":
							z中间提示. setText("JS-" + l链接);
							break ;
						case "s1.pstatp.com":
							z中间提示. setText("JS-" + l链接);
							break ;
						case "s2.pstatp.com":
							z中间提示. setText("JS-" + l链接);
							break ;
						case "mat1.gtimg.com":
							z中间提示. setText("JS-" + l链接);
							break ;
						default:
							p屏幕中间文本 =l链接+"\n"+p屏幕中间文本;
							g关闭浏览器调用播放器(view,url);
					}
					z中间提示. setText(p屏幕中间文本);
					/*
					if (url.length() > 45)
                    {
						String s=url.substring(0, 45);
						switch (s)
						{
							case "https://www.agefans.net/age/player/ckx1/?url=":
								String s视频链接=URLDecoder.decode(url.substring(45, url.length()));
								txt = "匹配到视频链接-" + url.substring(45, url.length()) + "\n" + txt;
								//findMp4Url(view,s视频链接);
								break ;
							case "https://www.agefans.net/age/player/ckx1/null&":
								txt = "页面不存在-" + url + "\n" + txt;
								break ;
							case "https://cdn.radius-america.com/age/player/ckx":
								//txt = "加载js文件-" + url + "\n" + txt;
								break ;
							case "https://cdn.radius-america.com/age/static/js/":
								//txt = "加载js文件-" + url + "\n" + txt;
								break ;
							case "https://s3.pstatp.com/cdn/expire-1-M/jquery/1":
								//txt = "加载js文件-" + url + "\n" + txt;
								break ;
							case "https://cdn.radius-america.com/age/static/css":
								//txt = "加载js文件-" + url + "\n" + txt;
								break ;
							default:
								//txt = "页面不匹配-" + url + "\n" + txt;
								url = URLDecoder.decode(url);
								txt = url + "\n" + txt;
								测试.setText(txt);
								String ui=url.substring(45, url.length());
								Pattern jm = Pattern.compile("http.*(mp4|m3u8)"); 
								Matcher m = jm.matcher(url);  
								Pattern jk=Pattern.compile("http.*mp4.(label|ptype|rkey|vkey|dis|vt).*");
								Matcher k=jk.matcher(url);
								Pattern jj=Pattern.compile("http.*(mp4|m3u8).*");
								Matcher j=jj.matcher(ui);
								if (m.matches())
								{
									findMp4Url(view, m.group());
								}
								else if (k.matches())
								{
									findMp4Url(view, k.group());
								}
								else if (j.matches())
								{
									Matcher mm = jm.matcher(ui);
									if (mm.find())
									{
										findMp4Url(view, mm.group());
									}
								}
						}

                    }*/
                    super.onLoadResource(view, url);
                }
            });
        webView.loadUrl(url);
    }
	String p屏幕中间文本="使用菜单键或返回键更换选集";//用于调试
}

