package com.age;
import android.*;
import android.app.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.age.MyVideoView;
import java.text.*;
import java.util.*;
import android.content.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import android.webkit.*;
import android.transition.*;
import android.opengl.*;
import java.io.*;
import android.preference.*;

public class TvPlayer extends Activity {
    MyVideoView videoView;
    TextView j进度条当前播放时间 ,j进度条总时间;
    SeekBar j进度条;
    ImageView t图标暂停 ,t图标继续播放;
	String id,name,video_url,playid="1_1";
	//int id_x,id_y;
	int j进度 , z总时长 ,j计时器 =3000 ;
	SharedPreferences id数据持久化 ;
	LinearLayout x选集列表 , j进度条列表 ;
	RadioGroup s设置列表;
	MediaPlayer s视频控制器;
	TextView t调试 ;
	boolean b捕获链接 ,t调试开关 ,j检查旧链接=true;
	String t调试文本 ,web域名="https://www.agemys.com",
	app域名="https://web.age-spa.com:8443/#";
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
			if((x选集列表.getVisibility()==View.VISIBLE)||(s设置列表.getVisibility()==View.VISIBLE)||(j进度条列表.getVisibility()==View.VISIBLE))
			{
				if(j计时器>0){
					j计时器 =j计时器 - 500;
				}else{
					j进度条列表.setVisibility(View.GONE);
					x选集列表.setVisibility(View.GONE);
					s设置列表.setVisibility(View.GONE);
				}
			}
			if (videoView.isPlaying()) {
				j进度 = videoView.getCurrentPosition();//获取当前播放的位置。
				z总时长 = videoView.getDuration();//获取当前播放视频的总长度。
				j进度条.setProgress(j进度);//更新进度条
				j进度条当前播放时间.setText(time(j进度));//更新时间文本
				//int percent = videoView.getBufferPercentage();//获取缓冲百分比
				//j进度条.setSecondaryProgress(percent*z总时长);//设置第二进度
				//预加载下一集视频地址
				if((z总时长-j进度)>30000)
				{
					String playids[]= playid.split("_");//切片 1_1
					//getVideoUrl(app域名+"/play/" + id+"/"+playids[0]+"/"+playids[1]);
					int id_y=Integer.parseInt(playids[1]);
					id_y=id_y+1;
					String new_playid=playids[0]+"_"+id_y;
					String new_play_url=id数据持久化.getString(new_playid,null);
					if(new_play_url==null)
					{
						debug("开始预加载下一集地址");
						getVideoUrl(app域名+"/play/" + id+"/"+playids[0]+"/"+id_y,new_playid);
					}
				}
			}
            handler.postDelayed(runnable, 500);
        }
    };
	void debug(String log)
	{
		if(t调试开关)
		{
			t调试文本+="\n"+log;
			t调试.setText(t调试文本);
			t调试.setVisibility(View.VISIBLE);
		}else t调试.setVisibility(View.GONE);
	}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        setContentView(R.layout.tv_player);
		t调试=findViewById(R.id.t调试);
		videoView = findViewById(R.id.video_view);
        j进度条当前播放时间 = findViewById(R.id.tv_play_time);
		j进度条总时间 = findViewById(R.id.tv_total_time);
        t图标暂停 = findViewById(R.id.btn_play_or_pause);
		t图标继续播放 = findViewById(R.id.btn_restart_play);
		j进度条列表=findViewById(R.id.进度条列表);
		x选集列表 = findViewById(R.id.选集列表);
		s设置列表 = findViewById(R.id.设置单选列表);
		j进度条 = findViewById(R.id.time_seekBar);
		Intent intent = getIntent();  
        id = intent.getStringExtra("id");
		handler.postDelayed(runnable,500);
		SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(this) ;
		web域名=sp.getString("web域名", "https://www.agemys.com");
		app域名=sp.getString("app域名", "https://web.age-spa.com:8443/#");
		t调试开关=sp.getBoolean("key_debug",false);
		id数据持久化= getSharedPreferences(id, Activity.MODE_PRIVATE); //实例化SharedPreferences对象（第一步）
		playid=id数据持久化.getString("playid",null);//上次看到第几集
		if(playid!=null)
		{
			video_url=id数据持久化.getString(playid,null);
			if(video_url!=null)
			{
				debug("曾经看过，正在检查旧视频链接是否有效。");
				j检查旧链接=true;
				VideoPlay(video_url);//直接开始播放。
			}else
			{
				debug("曾经看过，但是没有旧视频链接？");
				String playids[]= playid.split("_");//切片 1_1
				getVideoUrl(app域名+"/play/" + id+"/"+playids[0]+"/"+playids[1],null);
			}
		}else 
		{
			t调试.setVisibility(View.VISIBLE);
		}
        
		s设置列表.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(RadioGroup radioGroup, int i)
				{
					RadioButton rb_temp = findViewById(radioGroup.getCheckedRadioButtonId());
					SharedPreferences.Editor editor = id数据持久化.edit(); //实例化SharedPreferences.Editor对象（第二步）
					String 倍速字符串=rb_temp.getText().toString();
					Float 倍速 = Float.parseFloat(倍速字符串.substring(0,倍速字符串.length()-2));  //字符串转小数
					editor.putFloat("倍速", 倍速);//保存数据
					editor.commit(); //提交当前数据 
					Toast.makeText(TvPlayer.this,"当前播放速度："+倍速, Toast.LENGTH_SHORT).show();
					改变播放速度(倍速);
				}
			});
		 //https://www.agemys.com/
		new Doc().getHtml(web域名+"/detail/" + id, new Doc.Html(){
				@Override//获取选集列表
				public void getDoc(Document doc)
				{
					//title = doc.title();
					Elements resultLinks = doc.select("div.main0 ul");//查找ul
					for (Element res:resultLinks)//遍历结果
					{
						Elements r=res.getElementsByTag("a");//查找a标签
						if (r.size() > 0)
						{
							LinearLayout ll = new LinearLayout(TvPlayer.this);
							ll.setOrientation(0);//0代表横向，1代表纵向HORIZONTAL = 0，VERTICAL = 1
							x选集列表.addView(ll);
							for (Element e:r)
							{
								String href=e.attr("href");				
								ll.addView(newbutton(e.attr("title"), href.substring(6, href.length())));
							}
						}
					}		
					name = doc.select("h4.detail_imform_name").text();//标题
					t调试.setText(doc.select("p").text());//简介
					menu();
				}
			});
    }
		//https://vip.cqkeb.com/agefans/js/app.a377c123.js
	//	Toast.makeText(TvPlayer.this,s, Toast.LENGTH_SHORT).show();
	public void menu_long(){
		if(s设置列表.getVisibility()==View.GONE){
			s设置列表.setVisibility(View.VISIBLE);
			 
			x选集列表.setVisibility(View.GONE);
			j进度条列表.setVisibility(View.GONE);
			float 速度 = id数据持久化.getFloat("倍速", 1.0f);
			int view_s=(int) (速度/0.25f)-2;
			//Toast.makeText(TvPlayer.this, s设置列表.getChildCount() +","+view_s, Toast.LENGTH_LONG).show();
			 RadioButton but =(RadioButton) s设置列表.getChildAt(view_s);
			 but.setFocusable(true);//用键盘是否能获得焦点
			 but.setFocusableInTouchMode(true);//触摸是否能获得焦点
			 but.requestFocus();//获得焦点
		}else{
			x选集列表.setVisibility(View.GONE);
			s设置列表.setVisibility(View.GONE);
			j进度条列表.setVisibility(View.GONE);
		}
	}
	public void menu()
    {
		if (x选集列表.getVisibility() == View.GONE)
		{
			//t提示.setText(p屏幕中间文本+"\n"+j简介);
			x选集列表.setVisibility(View.VISIBLE);
			j计时器=3000;
			s设置列表.setVisibility(View.GONE);
			j进度条列表.setVisibility(View.GONE);
			String playid=id数据持久化.getString("playid",null);
			if(playid!=null)
			{
				String playids[]= playid.split("_");//切片 1_1
				int but_id = Integer.parseInt(playids[0] + playids[1]);
				Button but=x选集列表.findViewById(but_id);
				but.setFocusable(true);//用键盘是否能获得焦点
				but.setFocusableInTouchMode(true);//触摸是否能获得焦点
				but.requestFocus();//获得焦点
			}else
			{
				if (x选集列表.getChildCount() > 0)
				{//横列表
					//s += "共有" + box.getChildCount() + "列控件";
					LinearLayout but_ll =(LinearLayout) x选集列表.getChildAt(0);
					if (but_ll.getChildCount() > 0)
					{//竖列表
						//s += "第一列有" + but_ll.getChildCount() + "个控件";
						Button but=(Button) but_ll.getChildAt(0);
						but.setFocusable(true);//用键盘是否能获得焦点
						but.setFocusableInTouchMode(true);//触摸是否能获得焦点
						but.requestFocus();//获得焦点
					}
				}
			}
		}
		else
		{
			x选集列表.setVisibility(View.GONE);
			s设置列表.setVisibility(View.GONE);
			j进度条显示();
		}
    }
//选集列表按钮
    Button newbutton(final String str, final String id)
	{
        Button but;
        but = new Button(this);
		but.setHeight(12);
        but.setText(str);
		final String ids[]= id.split("\\?|=|_");//切片 20210008?playid=1_1
		int i = Integer.parseInt(ids[2] + ids[3]);
		//final String buturl="https://web.age-spa.com:8443/#"+"/play/" + ids[0]+"/"+ids[2]+"/"+ids[3];
		but.setId(i);
        but.setBackgroundResource(R.drawable.button_style);
        but.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v)
				{
					playid=ids[2]+"_"+ids[3];
					video_url = id数据持久化.getString(playid, null);
					if (video_url != null)
					{
						j检查旧链接=true;
						VideoPlay(video_url);
						t调试.setVisibility(View.VISIBLE);
						t调试.setText("尝试播放旧链接，长时间无反应可能是旧视频地址已失效，请尝试长按删除当前集记录。");
					}else
					{
						String playids[]= playid.split("_");//切片 1_1
						getVideoUrl(app域名+"/play/" + id+"/"+playids[0]+"/"+playids[1],null);
						//getVideoUrl("https://m.yhdmp.live/vp/12099-1-0.html");
						//网页https://www.agefans.live/play/20210008?playid=1_1
						//客户端https://web.age-spa.com:8443/#/play/20210008/1/1
						debug("旧播放链接为空。");
					}
					SharedPreferences.Editor editor = id数据持久化.edit(); //实例化SharedPreferences.Editor对象（第二步）
					editor.putString("new_name",str);
					editor.commit(); //提交当前数据
                }
            });
		but.setOnLongClickListener(new View.OnLongClickListener() { //其实就是增加了长按监听事件
                @Override
                public boolean onLongClick(View v)
				{
					AlertDialog alertDialog1=new AlertDialog.Builder(TvPlayer.this)
						.setTitle("这是长按选单")//标题
						.setPositiveButton("记住这里", new DialogInterface.OnClickListener() {//添加"Yes"按钮
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								SharedPreferences.Editor editor = id数据持久化.edit(); //实例化SharedPreferences.Editor对象（第二步）
								editor.putString("playid",playid);
								editor.commit(); //提交当前数据 
							}})
						.setNegativeButton("删除当前集记录",new DialogInterface.OnClickListener() {//添加"NO"按钮
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								SharedPreferences.Editor editor = id数据持久化.edit(); //实例化SharedPreferences.Editor对象（第二步）
								editor.remove(playid);
								editor.commit(); //提交当前数据		
							}})
						.setNeutralButton("无视缓存，重新捕获视频地址",new DialogInterface.OnClickListener() {//添加"备用"按钮
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								String playids[]= playid.split("_");//切片 1_1
								getVideoUrl(app域名+"/play/" + id+"/"+playids[0]+"/"+playids[1],null);
							}})
						.create();
					alertDialog1.show();
                    return true;//不再传递
                }
			});
        return but;
    }
	
    /**
     * 时间转换方法
     * @param millionSeconds
     * @return
     */
    protected String time(long millionSeconds) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millionSeconds);
        return simpleDateFormat.format(c.getTime());
    }

    /**
     * 初始化VideoView
     */
    private void VideoPlay(final String url) {
		Uri uri = Uri.parse(url);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override//视频预装完成监听
				public void onPrepared(MediaPlayer mp) {
					s视频控制器 = mp;
					video_url=url;
					改变播放速度(id数据持久化.getFloat("倍速", 1.0f));
					SharedPreferences.Editor editor = id数据持久化.edit();
					editor.putString("id",id);
					editor.putString("name", name);
					//editor.putString("mtime", jb.mtime);
					if(playid!=null)editor.putString("playid", playid);
					editor.putString(playid,url);
					editor.commit(); //提交当前数据 
					z总时长 = videoView.getDuration();//获取视频的总时长
					j进度条总时间.setText(stringForTime(z总时长));
					videoView.start();
					t调试.setVisibility(View.GONE);
					debug("当前以开启调试模式");
					j进度条.setMax(z总时长);
					j进度条.setKeyProgressIncrement(10000);//一次快进的进度
					j进度条.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
							public void onStartTrackingTouch(SeekBar bar) //开始跟踪触摸
							{
							}
							public void onProgressChanged(SeekBar bar, int progress,boolean fromuser) //进度发生变化
							{
								if (!fromuser) {return;}
								videoView.seekTo(progress);//从第几毫秒开始播放。
							}
							public void onStopTrackingTouch(SeekBar bar) //停止跟踪触摸
							{
								int position = videoView.getCurrentPosition();//获取当前播放的位置。
								j进度条.setProgress(position);//设置进度条进度
							}
						});
				}
			});
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override//视频播放完成监听
				public void onCompletion(MediaPlayer mp) {
					if((z总时长 - j进度)>10000)
					{
						Toast.makeText(TvPlayer.this,"缓冲出错，重新加载并跳转至"+(j进度/60000), Toast.LENGTH_SHORT).show();
						VideoPlay(url);
						videoView.seekTo(j进度);//跳转到暂停时保存的位置
					}else
					{
						Toast.makeText(TvPlayer.this,"自动下一集", Toast.LENGTH_SHORT).show();
						if(x选集列表.getChildCount() > 0)
						{
							String playids[]= playid.split("_");//切片 1_1
							//getVideoUrl(app域名+"/play/" + id+"/"+playids[0]+"/"+playids[1]);
							int id_y=Integer.parseInt(playids[1]);
							id_y=id_y+1;
							int but_id = Integer.parseInt(playids[0]+""+id_y);
							Button but=x选集列表.findViewById(but_id);
							if(but!=null)
							{
								but.callOnClick();
							}
						}
					}
				}
			});
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
				@Override//视频播放错误监听
				public boolean onError(MediaPlayer mp, int what, int extra) {
					debug("播放出错");
					if(j检查旧链接)
					{
						debug("旧链接已失效，尝试重新捕获视频链接。");
						String playids[]= playid.split("_");//切片 1_1
						getVideoUrl(app域名+"/play/" + id+"/"+playids[0]+"/"+playids[1],null);
						j检查旧链接=false;
					}
					//return false;
					return true;
				}
			});
    }
    /**
     * 改变视频播放速度(0.5f, 0.75f, 1f, 1.25f, 1.5f, 1.75f, 2.0f)
     * @param 速度
     */
	public void 改变播放速度(Float 速度)
	{
		if (s视频控制器 != null){
			if (s视频控制器.isPlaying()){
				s视频控制器.setPlaybackParams(s视频控制器.getPlaybackParams().setSpeed(速度));
			}else{
				s视频控制器.setPlaybackParams(s视频控制器.getPlaybackParams().setSpeed(速度));
				videoView.start();
			}
		}
	}
    /**
     * 控制视频是  播放还是暂停  或者是重播
     * @param isPlay
     * @param keys
     */
    private void 暂停或恢复(boolean isPlay) {
                if (isPlay) {//暂停
                    t图标暂停.setBackground(getResources().getDrawable(R.drawable.player));
                    t图标暂停.setVisibility(View.VISIBLE);
                    videoView.pause();
                } else {//继续播放
					t图标暂停.setBackground(getResources().getDrawable(R.drawable.pause));
                    t图标暂停.setVisibility(View.VISIBLE);
                    // 开始线程，更新进度条的刻度
                    //handler.postDelayed(runnable, 0);
                    videoView.start();
                    new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								t图标暂停.setVisibility(View.INVISIBLE);
							}
						}, 1500);
                }
    }

    //将长度转换为时间
    StringBuilder mFormatBuilder = new StringBuilder();
    Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }
    @Override
    protected void onPause()
	{//页面转到后台
        //如果当前页面暂停则保存当前播放位置，全局变量保存
        j进度 = videoView.getCurrentPosition(); //先获取再stopPlay(),原因自己看源码
        videoView.stopPlayback(); //停止播放视频文件
        super.onPause();
    }
    @Override
    protected void onResume()
	{//页面回到前台
        if (j进度 > 0)
		{
            //此处为更好的用户体验,可添加一个progressBar,有些客户端会在这个过程中隐藏底下控制栏,这方法也不错
            videoView.start();
            videoView.seekTo(j进度);//跳转到暂停时保存的位置
        }
        super.onResume();
    }
    void j进度条显示()
	{
		j进度条列表.setVisibility(View.VISIBLE);
		j计时器=3000;
		j进度条.setFocusable(true);//用键盘是否能获得焦点
		j进度条.setFocusableInTouchMode(true);//触摸是否能获得焦点
		j进度条.requestFocus();//获得焦点
	}
	/***触摸事件
	*0   x
	*
	*y
	***/
	float touch_x=0,touch_y=0;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()){
			case MotionEvent.ACTION_DOWN:
				j计时器=999999999;
					touch_x=event.getX();
					touch_y=event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				j计时器=3000;
				//Toast.makeText(TvPlayer.this, event.getX()+"，"+event.getY(), Toast.LENGTH_SHORT).show();
				break;
			case MotionEvent.ACTION_UP:
				j计时器=3000;
				float x_x=event.getX()-touch_x,y_y=event.getY()-touch_y;
				if((x_x+y_y)==0)
				{//点击屏幕
					if(j进度条列表.getVisibility() == View.VISIBLE){
						暂停或恢复(videoView.isPlaying());
					}else{
						j进度条显示();
					}
				}else
				{
					//Toast.makeText(TvPlayer.this, x_x+"，"+y_y, Toast.LENGTH_SHORT).show();
					menu();
				}
				break;
		}
		return false;
	}
    /**
     * 遥控器按键监听
     * @param keyCode
     * @param event
     * @return
     */
	private boolean Press = true;
    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		j计时器=3000;
		switch (keyCode)
		{
			case KeyEvent.KEYCODE_M:Press = true;menu_long();return true;
			case KeyEvent.KEYCODE_E:Press = true;menu_long();return true;
			case KeyEvent.KEYCODE_MENU:Press = true;menu_long();return true;
			case KeyEvent.KEYCODE_SETTINGS:Press = true;menu_long();return true;
			default:
				return super.onKeyDown(keyCode, event);
		}
    }
    private long s上次点返回时 = 0;//两次返回键间隔时间
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		j计时器=3000;
		if((x选集列表.getVisibility()!=View.VISIBLE)&&(s设置列表.getVisibility()!=View.VISIBLE)&&(j进度条列表.getVisibility()!=View.VISIBLE))
		{
			switch (keyCode)
			{//底部有控件不处理方向键
				case KeyEvent.KEYCODE_DPAD_UP:j进度条显示();return true;
				case KeyEvent.KEYCODE_DPAD_DOWN:j进度条显示();return true;
				case KeyEvent.KEYCODE_DPAD_LEFT:j进度条显示();return true;
				case KeyEvent.KEYCODE_DPAD_RIGHT:j进度条显示();return true;
			}
		}
		switch (keyCode)
		{
            case KeyEvent.KEYCODE_M:event.startTracking();return true;
			case KeyEvent.KEYCODE_E:event.startTracking();return true;
			case KeyEvent.KEYCODE_MENU:event.startTracking();return true;
			case KeyEvent.KEYCODE_SETTINGS:event.startTracking();return true;
			case KeyEvent.KEYCODE_DPAD_CENTER:暂停或恢复(videoView.isPlaying());return true;
            case KeyEvent.KEYCODE_SPACE:j进度条显示();暂停或恢复(videoView.isPlaying());return true;
            case KeyEvent.KEYCODE_ENTER:j进度条显示();暂停或恢复(videoView.isPlaying());return true;
            case KeyEvent.KEYCODE_BACK:
                if ((System.currentTimeMillis() - s上次点返回时) > 1000)
				{
                    Toast.makeText(TvPlayer.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    s上次点返回时 = System.currentTimeMillis();
					menu_long();
                }
				else
				{
                    finish();
                    System.exit(0);
                }
                return true;
            default:
        return super.onKeyDown(keyCode, event);
		}
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
		j计时器=3000;
		if(Press){
			Press= false;
			return true;
		}
		switch (keyCode)
		{
            case KeyEvent.KEYCODE_M:menu();return true;
			case KeyEvent.KEYCODE_E:menu_long();return true;
			case KeyEvent.KEYCODE_MENU:menu();return true;
			case KeyEvent.KEYCODE_SETTINGS:menu();return true;
            default:
			return super.onKeyDown(keyCode, event);
        }
    }
	void getVideoUrl(String url,final String new_playid)
	{
		debug("开始捕获视频链接");
		b捕获链接=true;
		WebView webView=new WebView(this);
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setPluginState(WebSettings.PluginState.ON);
		webSettings.setUseWideViewPort(true); // 关键点
		webSettings.setAllowFileAccess(true); // 允许访问文件
		webSettings.setSupportZoom(true); // 支持缩放
		webSettings.setLoadWithOverviewMode(true);
		//webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 不加载缓存内容
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
					s筛选连接(url,new_playid);
                    super.onLoadResource(view, url);
                }
            });
        webView.loadUrl(url);
    }
    private void s筛选连接(final String url,final String new_playid) {
		Uri uri = Uri.parse(url);  
		final MediaPlayer mediaPlayer = new MediaPlayer(); 
		try
		{
			mediaPlayer.setDataSource(this, uri);
		}
		catch (IOException e)
		{}
		catch (IllegalStateException e)
		{}
		catch (SecurityException e)
		{}
		catch (IllegalArgumentException e)
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
							debug("成功捕获链接"+url);
							b捕获链接=false;
							if(new_playid!=null)
							{
								SharedPreferences.Editor editor = id数据持久化.edit();
								editor.putString(new_playid,url);
								editor.commit(); //提交当前数据 
							}else
							{
								VideoPlay(url);
							}
						}
					}
					mediaPlayer.release();
				}
			});
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
				@Override//视频播放错误监听
				public boolean onError(MediaPlayer mp, int what, int extra) {
					debug("排除"+url);
					mediaPlayer.release();
					return true;
				}
			});
    }
}

