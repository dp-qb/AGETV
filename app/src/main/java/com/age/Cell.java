package com.age;
import android.widget.FrameLayout;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.graphics.Bitmap;
import android.widget.RelativeLayout;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
import java.io.*;
import android.net.*;
import android.content.*;
import android.graphics.*;
import android.widget.*;
import android.app.*;
import java.net.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import android.view.*;
import android.preference.*;
public class Cell extends FrameLayout
{
    private View mView,bg;
    private ImageView img;
    private TextView name,new_name,new_;
	public Cell(final Context context, String zhou_name,int width,int zhou)
	{
        super(context, null, 0);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.xml.cell, this, true);
        bg = mView.findViewById(R.id.bg);
        img = mView.findViewById(R.id.img);
        name = mView.findViewById(R.id.name);
        new_name = mView.findViewById(R.id.new_name);
        new_ = mView.findViewById(R.id.new_);
        mView.setFocusable(true);//用键盘是否能获得焦点
        mView.setFocusableInTouchMode(true);//触摸是否能获得焦点
        bg.getLayoutParams().width =width ;
        name.setWidth(width - 20);
        new_name.setWidth( width- 20);
		mView.setBackgroundResource(R.drawable.button_style);
		new_name.setVisibility(View.GONE);
        new_.setVisibility(View.GONE);
        name.setText(zhou_name);
		name.setGravity(Gravity.CENTER);
		//name.setTextSize(25);
		Bitmap b ;
		switch (zhou)
		{
			case 0:
				b = BitmapFactory.decodeResource(getResources(),R.drawable.ic_7);
				break;
			case 1:
				b = BitmapFactory.decodeResource(getResources(),R.drawable.ic_1);
				break;
			case 2:
				b = BitmapFactory.decodeResource(getResources(),R.drawable.ic_2);
				break;
			case 3:
				b = BitmapFactory.decodeResource(getResources(),R.drawable.ic_3);
				break;
			case 4:
				b = BitmapFactory.decodeResource(getResources(),R.drawable.ic_4);
				break;
			case 5:
				b = BitmapFactory.decodeResource(getResources(),R.drawable.ic_5);
				break;
			case 6:
				b = BitmapFactory.decodeResource(getResources(),R.drawable.ic_6);
				break;
			default :
				b = BitmapFactory.decodeResource(getResources(),R.drawable.ic_0);
				break;
		}
		Bitmap p= Bitmap.createScaledBitmap(b,width  - 20, (int)((width - 20) * 1.4), true);
		img.setImageBitmap(p);
	}
    public Cell(final Context context,final String id,final int id_x,final int id_y,final String id_name,final String id_new_name,boolean isnew, final int width)
	{
        super(context, null, 0);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.xml.cell, this, true);
        bg = mView.findViewById(R.id.bg);
        img = mView.findViewById(R.id.img);
        name = mView.findViewById(R.id.name);
        new_name = mView.findViewById(R.id.new_name);
        new_ = mView.findViewById(R.id.new_);
        mView.setFocusable(true);//用键盘是否能获得焦点
        mView.setFocusableInTouchMode(true);//触摸是否能获得焦点
        bg.getLayoutParams().width = width;
        name.setWidth(width - 20);
        new_name.setWidth(width - 20);
		mView.setBackgroundResource(R.drawable.button_style);
        mView.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v)
				{
					Intent intent = new Intent();
                    //intent.setClass(context,Player.class);
					intent.setClass(context,TvPlayer.class);
                    intent.putExtra("id",id);
                    context.startActivity(intent);
                }
            });
		mView.setOnLongClickListener(new View.OnLongClickListener() { //其实就是增加了长按监听事件
                @Override
                public boolean onLongClick(View v)
				{
					AlertDialog alertDialog1=new AlertDialog.Builder(context)
						.setTitle("这是长按选单")//标题
						.setPositiveButton("稍后再看", new DialogInterface.OnClickListener() {//添加"Yes"按钮
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								SharedPreferences 数据持久化= context.getSharedPreferences(id, Activity.MODE_PRIVATE); //实例化SharedPreferences对象（第一步）
								SharedPreferences.Editor editor = 数据持久化.edit(); //实例化SharedPreferences.Editor对象（第二步）
								editor.putString("id", id+"?playid="+id_x+"_"+id_y);//00000000?playid=2_3
								editor.putString("name", id_name);
								editor.putString("new_name", id_new_name);
								editor.commit(); //提交当前数据 
							}})
						.setNegativeButton("删除记录>_<",new DialogInterface.OnClickListener() {//添加"NO"按钮
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								File pref_xml = new File("data/data/com.age/shared_prefs",id+ ".xml");
								if (pref_xml.exists())
								{
									pref_xml.delete();
								}
								Intent intent = new Intent(context, MainActivity.class);
								context.startActivity(intent);
							}})
						.setNeutralButton("清除缓存",new DialogInterface.OnClickListener() {//添加"备用"按钮
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								SharedPreferences sp历史记录= context.getSharedPreferences(id, Activity.MODE_PRIVATE); //实例化SharedPreferences对象（第一步）
								SharedPreferences.Editor editor = sp历史记录.edit(); //实例化SharedPreferences.Editor对象（第二步）
								editor.clear();//清空
								editor.commit(); //提交当前数据
								SharedPreferences sp对照表= context.getSharedPreferences("PIC", Activity.MODE_PRIVATE); //实例化SharedPreferences对象（第一步）
								SharedPreferences.Editor editor对照表 = sp对照表.edit(); //实例化SharedPreferences.Editor对象（第二步）
								editor对照表.remove(id);
								editor对照表.commit(); //提交当前数据
								File id_jpg = new File(context.getCacheDir(), id + ".jpg");
								if (id_jpg.exists())
								{
									id_jpg.delete();
								}
							}})
						.create();
					alertDialog1.show();
                    return true;//不再传递
                }
			});
		
		File file = new File(context.getCacheDir(), id + ".jpg");
        if (file.exists())// 如果图片存在本地缓存目录
		{
			//Toast.makeText(context,"从缓存中读到图片", Toast.LENGTH_SHORT).show();
			Uri uri =Uri.fromFile(file);//Uri.fromFile(path)这个方法能得到文件的URI
			ContentResolver cr = context.getContentResolver();
            try
			{
			InputStream is = cr.openInputStream(uri);
			if (is == null)Toast.makeText(context, "缓存图片读取失败：" + file, Toast.LENGTH_SHORT).show();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;//缩小图片，二的倍数，1/n
			Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
			Bitmap p= Bitmap.createScaledBitmap(bitmap, width - 20, (int)((width - 20) * 1.4), true);
			img.setImageBitmap(p);
			is.close();
            }
		catch (IOException e){}
		}
		else
		{
			//从对照表中读取图片链接并下载
			SharedPreferences 数据持久化= context.getSharedPreferences("PIC", Activity.MODE_PRIVATE); //实例化SharedPreferences对象（第一步）
			final String url=数据持久化.getString(id, "空");
			if (url.length()>5)
			{
				new Thread(new Runnable() {
						@Override
						public void run()
						{
							try
							{
							HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
							conn.setRequestMethod("GET");//设置请求方式为"GET"
							conn.setConnectTimeout(5 * 1000);//超时响应时间为5秒
							conn.connect();
							InputStream is = conn.getInputStream();//通过输入流获取图片数据
							final Bitmap bitmap = BitmapFactory.decodeStream(is);
							is.close();
							save_img(context,id + ".jpg",bitmap);
							if (bitmap != null)
							{
								 img.post(new Runnable(){
									public void run()
									{
										Bitmap p= Bitmap.createScaledBitmap(bitmap, width - 20, (int)((width - 20) * 1.4), true);
										img.setImageBitmap(p);
									}});
							}
						}
						catch (IOException e){}
						}
					}).start();
			}
			else
			{
				//从网页上下载图片
				new Thread(new Runnable() {
						@Override
						public void run()
						{

						SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(context);
										String url = sp.getString("web域名", "https://www.agemys.com")+"/detail/" + id;
							Connection con = Jsoup.connect(url);
							Document doc = null;
							try
							{
							doc = con.get();
							Elements res_img = doc.select("img.poster");
							String img_url=res_img.attr("abs:src");
							if(!(img_url.startsWith("http"))){img_url="https:"+res_img.attr("src");}
							//加https:
							SharedPreferences 数据持久化= context.getSharedPreferences("PIC", Activity.MODE_PRIVATE); //实例化SharedPreferences对象（第一步）
							SharedPreferences.Editor editor = 数据持久化.edit(); //实例化SharedPreferences.Editor对象（第二步）
							editor.putString(id, img_url);//保存数据
							editor.commit(); //提交当前数据 
							HttpURLConnection conn = (HttpURLConnection) new URL(img_url).openConnection();
							conn.setRequestMethod("GET");//设置请求方式为"GET"
							conn.setConnectTimeout(5 * 1000);//超时响应时间为5秒
							conn.connect();
							conn.connect();
							InputStream is = conn.getInputStream();
							final Bitmap bitmap = BitmapFactory.decodeStream(is);
							is.close();
							save_img(context,id + ".jpg",bitmap);
							if (bitmap != null)
							{
								img.post(new Runnable(){
									public void run()
									{
									    Bitmap p= Bitmap.createScaledBitmap(bitmap, width - 20, (int)((width - 20) * 1.4), true);
										img.setImageBitmap(p);
									}});
								}
							}
							catch (IOException e)
							{}}
					}).start();
			}
		}
        name.setText(id_name);
		if (id_new_name == null)new_name.setVisibility(View.GONE);
        else new_name.setText(id_new_name);
        if (isnew)
		{new_.setVisibility(View.VISIBLE);}
        else
		{new_.setVisibility(View.GONE);}
    }
	void save_img(Context context,String file_name,Bitmap img_bit)
	{
	File file = new File(context.getCacheDir(),file_name);
	// 创建一个位于SD卡上的文件 
	FileOutputStream fileOutStream=null; 
	try{
	fileOutStream=new FileOutputStream(file);
	//把位图输出到指定的文件中 
	img_bit.compress(Bitmap.CompressFormat.PNG, 100, fileOutStream); 
	fileOutStream.close(); 
	}catch(IOException e){} 
	}
}
