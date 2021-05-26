package com.age;

import android.app.*;
import android.os.*;
import android.widget.GridLayout;
import android.widget.Button;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.widget.Toast;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.jsoup.Connection;
import java.io.IOException;
import org.jsoup.nodes.Element;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import android.widget.TextView;
import java.util.ArrayList;
import android.view.WindowId;
import android.widget.EditText;
import android.widget.TextView.OnEditorActionListener;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;
import android.text.Html;
import android.graphics.drawable.Drawable;
import android.content.SharedPreferences;
import android.content.Intent;
import android.widget.LinearLayout;
import android.graphics.Color;
import java.sql.Time;
import java.util.Date;
import java.util.Calendar;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ScrollView;
import android.widget.ImageButton;
import BanGuMi;
import android.widget.RelativeLayout;
import android.widget.GridLayout.Spec;
import android.net.*;
import java.io.*;
import java.util.*;
import android.content.*;
public class MainActivity extends Activity
{
    ScrollView longbox;//下拉框
    List<BanGuMi> bangumi;
    TextView txt;//调试用文本
    String title="调试信息！";//标记当前页面
    List<BanGuMi> 一周间;
    int w=1024,yemax=999;
    catalog cata=new catalog("all", "all", "all", "all", "all", "all", "all", "all", "time", 1);
    @Override
    protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        setContentView(R.layout.main);
		findViewById(R.id.lisi).requestFocus();//获得焦点
        longbox = findViewById(R.id.longbox);//获取下拉框
        //grid = findViewById(R.id.grid); //获取GridLayout控件
        txt = findViewById(R.id.txt);
        EditText edit=findViewById(R.id.edit);
        edit.setOnEditorActionListener(new OnEditorActionListener() {
                @Override  
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
				{  
                    if (actionId == EditorInfo.IME_ACTION_SEARCH)
					{
                        title = v.getText().toString().trim();
                        cata.当前页 = 1;
                        newye(title);
                        return true;
                    }
                    return false;  
                }  
            }); 
        w = getWindowManager().getDefaultDisplay().getWidth();
        //getWindowManager().getDefaultDisplay().getHeight(); 
        lisi(null);
    }
    void newye(final String 关键词)
    {
        new Doc().getHtml("https://www.agefans.net/search?query=" + 关键词 + "&page=" + cata.当前页, new Doc.Html(){
                @Override
                public void getDoc(Document doc)
                {
                    title = doc.title();
                    txt.setText(title);
                    longbox.removeAllViews();
                    GridLayout grid=new GridLayout(MainActivity.this);
                    grid.setColumnCount(6);
                    longbox.addView(grid);
                    Elements resultLinks = doc.select("a.cell_poster");
                    for (Element res:resultLinks)
                    {
                        Document body = Jsoup.parseBodyFragment(res.html());
                        Element img = body.select("img").first();
                        Element span = body.select("span").first();
                        BanGuMi b= new BanGuMi("false", res.attr("href").substring(8), null, img.attr("alt"), null, span.text());
                        grid.addView(newbutton(b, 6));
                        //grid.addView(newbutton(res.attr("href").substring(8),img.attr("alt"),6));
                    }
                    Elements li=doc.select("#result_count");
                    Button butl=new Button(MainActivity.this);
                    Button butr=new Button(MainActivity.this);
                    TextView t=new TextView(MainActivity.this);
                    String s="";
                    for (Element res:li)
                    {
                        String str=res.text();
                        s = str.substring(0, str.length() - 2);
                    }
                    double d=Integer.parseInt(s);
                    yemax = (int) Math.ceil(d / 24);
                    t.setText("当前第" + cata.当前页 + "页\n共" + yemax + "页");
                    butl.setText("上一页");
                    butr.setText("下一页");
                    butl.setOnClickListener(new Button.OnClickListener() {
                            @Override
                            public void onClick(View v)
							{
                                if (cata.当前页 > 1)
								{
                                    cata.当前页 -= 1;
									newye(关键词);
                                }
                                else
								{Toast.makeText(MainActivity.this, "已经是第一页了！", Toast.LENGTH_SHORT).show();}
                            }
                        });
                    butr.setOnClickListener(new Button.OnClickListener() {
                            @Override
                            public void onClick(View v)
							{
                                if (cata.当前页 < yemax)
								{
                                    cata.当前页 += 1;
									newye(关键词);}
                                else
								{Toast.makeText(MainActivity.this, "已经是第一页了！", Toast.LENGTH_SHORT).show();}
                            }
                        });
                    grid.addView(t);
                    grid.addView(butl);
                    grid.addView(butr);
                }
            });
    }
    View newbutton(final BanGuMi b, int f)
	{
        final Cell but=new Cell(this, b, (w / f));
        //but.setWidth(w/f);
        but.setBackgroundResource(R.drawable.button_style);
        but.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v)
				{
					Intent intent = new Intent();
                    intent.setClass(MainActivity.this, Player.class);
                    intent.putExtra("id", b.getid());
                    startActivity(intent);
                }
            });
        but.setOnLongClickListener(new View.OnLongClickListener() { //其实就是增加了长按监听事件
                @Override
                public boolean onLongClick(View v)
				{
				AlertDialog alertDialog1=new AlertDialog.Builder(MainActivity.this)
				.setTitle("修改记录")//标题
                .setPositiveButton("特别关注(*^▽^*)", new DialogInterface.OnClickListener() {//添加"Yes"按钮
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					//TODO
				}})
						.setNegativeButton("删除记录>_<",new DialogInterface.OnClickListener() {//添加"NO"按钮
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								SharedPreferences 数据持久化= getSharedPreferences(b.getid_8(), Activity.MODE_PRIVATE); //实例化SharedPreferences对象（第一步）
								File pref_xml = new File("data/data/com.age/shared_prefs", b.getid_8() + ".xml");
								if (pref_xml.exists())
								{
									pref_xml.delete();
								}
								if (数据持久化 != null)
								{
									SharedPreferences.Editor editor = 数据持久化.edit(); //实例化SharedPreferences.Editor对象（第二步）
									editor.clear();
									editor.commit();
								}   
								finish();
								Intent intent = new Intent(MainActivity.this, MainActivity.class);
								startActivity(intent);
							}})
						.setNeutralButton("备用按钮o_O???",new DialogInterface.OnClickListener() {//添加"备用"按钮
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								//TODO
							}})
			.create();
		alertDialog1.show();
                    return true;//不再传递
                }
			});
        return but;
    }
    public void lisi(View v)
    {
		File file = new File("data/data/com.age/shared_prefs");
		longbox.removeAllViews();
        GridLayout grid=new GridLayout(MainActivity.this);
        grid.setColumnCount(5);
        longbox.addView(grid);
		if (file.exists())
		{
			File[] files = file.listFiles();
			Arrays.sort(files, new Comparator<File>() {
					public int compare(File f1, File f2) {
						long diff = f1.lastModified() - f2.lastModified();
						if (diff > 0)
							return -1;
						else if (diff == 0)
							return 0;
						else
							return 1;//如果 if 中修改为 返回-1 同时此处修改为返回 1  排序就会是递减
					}
				});
			for (File f:files)
			{
				if (f.getName().length() == 12)
				{
					SharedPreferences 数据持久化= getSharedPreferences(f.getName().substring(0, 8), Activity.MODE_PRIVATE); //实例化SharedPreferences对象（第一步）
					BanGuMi jb = new BanGuMi();
					jb.setid(数据持久化.getString("id", null));
					//Toast.makeText(this,jb.id+":"+jb.wd+":"+jb.name+":"+jb.mtime+":"+jb.new_name, Toast.LENGTH_SHORT).show();
					if (jb.id != null)
					{
						jb.isnew = 数据持久化.getBoolean("isnew", false);
						jb.id = 数据持久化.getString("id", null);
						jb.wd = 数据持久化.getString("wd", null);
						jb.name = 数据持久化.getString("name", null);
						jb.mtime = 数据持久化.getString("mtime", null);
						jb.new_name = 数据持久化.getString("new_name", null);
						grid.addView(newbutton(jb, 5));
						//Toast.makeText(this,jb.id+":"+jb.wd+":"+jb.name+":"+jb.mtime+":"+jb.new_name, Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
		else
		{
			Toast.makeText(this, "没有历史记录", Toast.LENGTH_LONG).show();
		}
    }
    public void neplay (View v)
	{
        //new Thread(runnable).start();
        new Doc().getHtml("https://www.agefans.net", new Doc.Html(){
                @Override
                public void getDoc(Document doc)
                {
                    String str=null;
                    title = doc.title();
                    txt.setText(title);
                    longbox.removeAllViews();
                    GridLayout grid=new GridLayout(MainActivity.this);
                    grid.setColumnCount(7);
                    longbox.addView(grid);
                    LinearLayout ll1 = new LinearLayout(MainActivity.this);
                    LinearLayout ll2 = new LinearLayout(MainActivity.this);
                    LinearLayout ll3 = new LinearLayout(MainActivity.this);
                    LinearLayout ll4 = new LinearLayout(MainActivity.this);
                    LinearLayout ll5 = new LinearLayout(MainActivity.this);
                    LinearLayout ll6 = new LinearLayout(MainActivity.this);
                    LinearLayout ll0 = new LinearLayout(MainActivity.this);
                    ll1.setWeightSum(1);
                    ll2.setWeightSum(1);
                    ll3.setWeightSum(1);
                    ll4.setWeightSum(1);
                    ll5.setWeightSum(1);
                    ll6.setWeightSum(1);
                    ll0.setWeightSum(1);
                    ll1.setOrientation(1);
                    ll2.setOrientation(1);
                    ll3.setOrientation(1);
                    ll4.setOrientation(1);
                    ll5.setOrientation(1);
                    ll6.setOrientation(1);
                    ll0.setOrientation(1);
                    grid.addView(ll1);
                    grid.addView(ll2);
                    grid.addView(ll3);
                    grid.addView(ll4);
                    grid.addView(ll5);
                    grid.addView(ll6);
                    grid.addView(ll0);
                    Date today = new Date();
                    Calendar c = Calendar.getInstance();
                    c.setTime(today);
                    int weekday = c.get(Calendar.DAY_OF_WEEK);
                    switch (weekday)
                    {
                        case 2:
                            ll1.setBackgroundColor(Color.RED);
                            break;
                        case 3:
                            ll2.setBackgroundColor(Color.RED);
                            break;
                        case 4:
                            ll3.setBackgroundColor(Color.RED);
                            break;
                        case 5:
                            ll4.setBackgroundColor(Color.RED);
                            break;
                        case 6:
                            ll5.setBackgroundColor(Color.RED);
                            break;
                        case 7:
                            ll6.setBackgroundColor(Color.RED);
                            break;
                        case 1:
                            ll0.setBackgroundColor(Color.RED);
                            break;
                        default :
                            txt.setText("预料之外的日子！") ;
                            break;

                    }
                    Element masthead = doc.select("div.blockcontent script").first();
                    String divstr=masthead.toString();
                    Pattern p=Pattern.compile("(\\[).*?(\\];)");
                    Matcher m=p.matcher(divstr);
                    m.find();
                    str = m.group(); 
                    str = str.substring(0, str.length() - 1);
                    try
					{
                        JSONArray jsonArray = new JSONArray(str);
						for (int i=0; i < jsonArray.length(); i++)
						{
							JSONObject jsonObject = jsonArray.getJSONObject(i);
							BanGuMi b=new BanGuMi(
								jsonObject.getString("isnew"),
								jsonObject.getString("id"),
								jsonObject.getString("wd"),
								jsonObject.getString("name"),
								jsonObject.getString("mtime"),
								jsonObject.getString("namefornew")
							);
							switch (jsonObject.getString("wd"))
							{
								case "0":
									ll0.addView(newbutton(b, 7));
									break;
								case "1":
									ll1.addView(newbutton(b, 7));
									break;
								case "2":
									ll2.addView(newbutton(b, 7));
									break;
								case "3":
									ll3.addView(newbutton(b, 7));
									break;
								case "4":
									ll4.addView(newbutton(b, 7));
									break;
								case "5":
									ll5.addView(newbutton(b, 7));
									break;
								case "6":
									ll6.addView(newbutton(b, 7));
									break;
								default :
									txt.setText(jsonObject.getString("name") + "不知道这部番剧是周几播出！" + jsonObject.getString("id")) ;
									break;

							}
						}
					}
					catch (JSONException e)
					{}
                }
            });
    }
	public void weekly(View v)
	{
		new Doc().getHtml("https://www.agefans.net", new Doc.Html(){
                @Override
                public void getDoc(Document doc)
                {
                    String str=null;
                    title = doc.title();
                    txt.setText(title);
                    longbox.removeAllViews();//清空滑动列表
                    //GridLayout grid=new GridLayout(MainActivity.this);
                    //grid.setColumnCount(7);
					LinearLayout ll = new LinearLayout(MainActivity.this);
					ll.setOrientation(LinearLayout.VERTICAL); 
                    longbox.addView(ll);
                    GridLayout gl0 = new GridLayout(MainActivity.this);
                    GridLayout gl1 = new GridLayout(MainActivity.this);
                    GridLayout gl2 = new GridLayout(MainActivity.this);
                    GridLayout gl3 = new GridLayout(MainActivity.this);
                    GridLayout gl4 = new GridLayout(MainActivity.this);
                    GridLayout gl5 = new GridLayout(MainActivity.this);
                    GridLayout gl6 = new GridLayout(MainActivity.this);
                    gl0.setColumnCount(7);
                    gl1.setColumnCount(7);
                    gl2.setColumnCount(7);
                    gl3.setColumnCount(7);
                    gl4.setColumnCount(7);
                    gl5.setColumnCount(7);
                    gl6.setColumnCount(7);
                    Date today = new Date();
                    Calendar c = Calendar.getInstance();
                    c.setTime(today);
                    int weekday = c.get(Calendar.DAY_OF_WEEK);
                    switch (weekday)//把今天标红
                    {
                        case 1:
                            gl0.setBackgroundColor(Color.RED);
							ll.addView(gl0);
							ll.addView(gl1);
							ll.addView(gl2);
							ll.addView(gl3);
							ll.addView(gl4);
							ll.addView(gl5);
							ll.addView(gl6);
                            break;
                        case 2:
                            gl1.setBackgroundColor(Color.RED);
							ll.addView(gl1);
							ll.addView(gl2);
							ll.addView(gl3);
							ll.addView(gl4);
							ll.addView(gl5);
							ll.addView(gl6);
							ll.addView(gl0);
                            break;
                        case 3:
                            gl2.setBackgroundColor(Color.RED);
							ll.addView(gl2);
							ll.addView(gl3);
							ll.addView(gl4);
							ll.addView(gl5);
							ll.addView(gl6);
							ll.addView(gl0);
							ll.addView(gl1);
                            break;
                        case 4:
                            gl3.setBackgroundColor(Color.RED);
							ll.addView(gl3);
							ll.addView(gl4);
							ll.addView(gl5);
							ll.addView(gl6);
							ll.addView(gl0);
							ll.addView(gl1);
							ll.addView(gl2);
                            break;
                        case 5:
                            gl4.setBackgroundColor(Color.RED);
							ll.addView(gl4);
							ll.addView(gl5);
							ll.addView(gl6);
							ll.addView(gl0);
							ll.addView(gl1);
							ll.addView(gl2);
							ll.addView(gl3);
                            break;
                        case 6:
                            gl5.setBackgroundColor(Color.RED);
							ll.addView(gl5);
							ll.addView(gl6);
							ll.addView(gl0);
							ll.addView(gl1);
							ll.addView(gl2);
							ll.addView(gl3);
							ll.addView(gl4);
                            break;
                        case 7:
                            gl6.setBackgroundColor(Color.RED);
							ll.addView(gl6);
							ll.addView(gl0);
							ll.addView(gl1);
							ll.addView(gl2);
							ll.addView(gl3);
							ll.addView(gl4);
							ll.addView(gl5);
                            break;
                        default :
                            txt.setText("预料之外的日子！") ;
                            break;

                    }
                    Element masthead = doc.select("div.blockcontent script").first();
                    String divstr=masthead.toString();
                    Pattern p=Pattern.compile("(\\[).*?(\\];)");
                    Matcher m=p.matcher(divstr);
                    m.find();
                    str = m.group(); 
                    str = str.substring(0, str.length() - 1);
                    try
					{
                        JSONArray jsonArray = new JSONArray(str);
						for (int i=0; i < jsonArray.length(); i++)
						{
							JSONObject jsonObject = jsonArray.getJSONObject(i);
							BanGuMi b=new BanGuMi(
								jsonObject.getString("isnew"),
								jsonObject.getString("id"),
								jsonObject.getString("wd"),
								jsonObject.getString("name"),
								jsonObject.getString("mtime"),
								jsonObject.getString("namefornew")
							);
							switch (jsonObject.getString("wd"))
							{
								case "0":
									gl0.addView(newbutton(b, 7));
									break;
								case "1":
									gl1.addView(newbutton(b, 7));
									break;
								case "2":
									gl2.addView(newbutton(b, 7));
									break;
								case "3":
									gl3.addView(newbutton(b, 7));
									break;
								case "4":
									gl4.addView(newbutton(b, 7));
									break;
								case "5":
									gl5.addView(newbutton(b, 7));
									break;
								case "6":
									gl6.addView(newbutton(b, 7));
									break;
								default :
									txt.setText(jsonObject.getString("name") + "不知道这部番剧是周几播出！" + jsonObject.getString("id")) ;
									break;

							}
						}
					}
					catch (JSONException e)
					{}
                }
            });
	}
    //TV-2021-A-搞笑-BDRIP-name-1-日本-1-连载
    void newleibie(final GridLayout grid, final catalog c)
    {
        new Doc().getHtml("https://www.agefans.net/catalog/" + cata.getcata(), new Doc.Html(){
                @Override
                public void getDoc(Document doc)
                {
                    title = doc.title();
                    txt.setText(title);
                    grid.removeAllViews();
                    Elements resultLinks = doc.select("a.cell_poster");
                    for (Element res:resultLinks)
                    {
                        Document body = Jsoup.parseBodyFragment(res.html());
                        Element img = body.select("img").first();
                        Element span = body.select("span").first();
                        BanGuMi b= new BanGuMi("false", res.attr("href").substring(8), null, img.attr("alt"), null, span.text());
                        grid.addView(newbutton(b, 6));
                        //grid.addView(newbutton(res.attr("href").substring(8),img.attr("alt"),6));
                    }
                    Elements li=doc.select("#result_count");
                    Button butl=new Button(MainActivity.this);
                    Button butr=new Button(MainActivity.this);
                    TextView t=new TextView(MainActivity.this);
                    String s="";
                    for (Element res:li)
                    {
                        String str=res.text();
                        s = str.substring(0, str.length() - 2);
                    }
                    double d=Integer.parseInt(s);
                    yemax = (int) Math.ceil(d / 24);
                    t.setText("当前第" + cata.当前页 + "页\n共" + yemax + "页");
                    butl.setText("上一页");
                    butr.setText("下一页");
                    butl.setOnClickListener(new Button.OnClickListener() {
                            @Override
                            public void onClick(View v)
							{
                                if (cata.当前页 > 1)
								{
                                    cata.当前页 -= 1;
                                    newleibie(grid, cata);
                                }
                                else
								{Toast.makeText(MainActivity.this, "已经是第一页了！", Toast.LENGTH_SHORT).show();}
                            }
                        });
                    butr.setOnClickListener(new Button.OnClickListener() {
                            @Override
                            public void onClick(View v)
							{
                                if (cata.当前页 < yemax)
								{
                                    cata.当前页 += 1;
                                    newleibie(grid, cata);}
                                else
								{Toast.makeText(MainActivity.this, "已经是第一页了！", Toast.LENGTH_SHORT).show();}
                            }
                        });
                    grid.addView(t);
                    grid.addView(butl);
                    grid.addView(butr);
                }
            });
    }
    public void leibie(View v)
    {
        longbox.removeAllViews();
        LinearLayout ll=new LinearLayout(MainActivity.this);
        ll.setOrientation(ll.VERTICAL);
        longbox.addView(ll);
        final GridLayout grid=new GridLayout(MainActivity.this);
        grid.setColumnCount(6);
        String[] 地区={"all","日本","中国","欧美"};
        String[] 版本={"all","TV","剧场版","OVA"};
        String[] 首字母={"all","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        String[] 年份={"all","2021","2020","2019","2018","2017","2016","2015","2014","2013","2012","2011","2010","2009","2008","2007","2006","2005","2004","2003","2002","2001","2000"};
        String[] 季度={"all","1","4","7","10"};
        String[] 状态={"all","连载","完结","未播放"};
        String[] 类型={"all","搞笑","运动","励志","热血","战斗","竞技","校园","青春","爱情","冒险","后宫","百合","治愈","萝莉","魔法","悬疑","推理","奇幻","科幻","游戏","神魔","恐怖","血腥","机战","战争","犯罪","历史","社会","职场","剧情","伪娘","耽美","童年","教育","亲子","真人","歌舞","肉番","美少女","轻小说","吸血鬼","女性向","泡面番","欢乐向",};
        String[] 资源={"all","BDRIP","AGE-RIP"};
        String[] 排序={"time","name","点击量"};
        View[] redio={
            new Radio(MainActivity.this, "地区：", 地区, new Radio.Checked(){
                    @Override
                    public void getChecked(String b)
					{
                        cata.地区 = b;
                        cata.当前页 = 1;
                        newleibie(grid, cata);
                    }
                }),
            new Radio(MainActivity.this, "版本：", 版本, new Radio.Checked(){
                    @Override
                    public void getChecked(String b)
					{
                        cata.版本 = b;
                        cata.当前页 = 1;
                        newleibie(grid, cata);
                    }
                }),
            new Radio(MainActivity.this, "首字母：", 首字母, new Radio.Checked(){
                    @Override
                    public void getChecked(String b)
					{
                        cata.首字母 = b;
                        cata.当前页 = 1;
                        newleibie(grid, cata);
                    }
                }),
            new Radio(MainActivity.this, "年份：", 年份, new Radio.Checked(){
                    @Override
                    public void getChecked(String b)
					{
                        cata.年份 = b;
                        cata.当前页 = 1;
                        newleibie(grid, cata);
                    }
                }),
            new Radio(MainActivity.this, "季度：", 季度, new Radio.Checked(){
                    @Override
                    public void getChecked(String b)
					{
                        cata.季度 = b;
                        cata.当前页 = 1;
                        newleibie(grid, cata);
                    }
                }),
            new Radio(MainActivity.this, "状态：", 状态, new Radio.Checked(){
                    @Override
                    public void getChecked(String b)
					{
                        cata.状态 = b;
                        cata.当前页 = 1;
                        newleibie(grid, cata);
                    }
                }),
            new Radio(MainActivity.this, "类型：", 类型, new Radio.Checked(){
                    @Override
                    public void getChecked(String b)
					{
                        cata.类型 = b;
                        cata.当前页 = 1;
                        newleibie(grid, cata);
                    }
                }),
            new Radio(MainActivity.this, "资源：", 资源, new Radio.Checked(){
                    @Override
                    public void getChecked(String b)
					{
                        cata.资源 = b;
                        cata.当前页 = 1;
                        newleibie(grid, cata);
                    }
                }),
            new Radio(MainActivity.this, "排序：", 排序, new Radio.Checked(){
                    @Override
                    public void getChecked(String b)
					{
                        cata.排序 = b;
                        cata.当前页 = 1;
                        newleibie(grid, cata);
                    }
                }),
        };
        for (int i = 0; i < redio.length; i++)
        {
            ll.addView(redio[i]);

        }
        ll.addView(grid);
        cata.当前页 = 1;
        newleibie(grid, cata);
    }
    class catalog
    {
        String 地区;
        String 版本;
        String 首字母;
        String 年份;
        String 季度;
        String 状态;
        String 类型;
        String 资源;
        String 排序;
        int 当前页;
        catalog(String 地, String 版, String 首, String 年, String 季, String 装, String 类, String 资, String 排, int 当)
        {
            地区 = 地;
            版本 = 版;
            首字母 = 首;
            年份 = 年;
            季度 = 季;
            状态 = 装;
            类型 = 类;
            资源 = 资;
            排序 = 排;
            当前页 = 当;
        }
        //TV-2021-A-搞笑-BDRIP-name-1-日本-1-连载
        String getcata()
        {
            return 版本 + "-" + 年份 + "-" + 首字母 + "-" + 类型 + "-" + 资源 + "-" + 排序 + "-" + 当前页 + "-" + 地区 + "-" + 季度 + "-" + 状态;
        }
    }
}
