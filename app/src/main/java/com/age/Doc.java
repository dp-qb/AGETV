package com.age;
import BanGuMi;
import android.os.AsyncTask;
import android.graphics.Bitmap;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.io.IOException;
import java.io.InputStream;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import java.io.File;
import android.net.Uri;
import android.content.Context;
import android.content.ContentResolver;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import android.widget.Toast;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;
import android.webkit.*;
import android.os.*;
import android.content.*;
import android.app.*;
import java.util.regex.*;
import java.util.*;
import com.google.gson.*;
import com.google.gson.reflect.*;


public class Doc
{
	public Doc() { }  
    public interface Pic {
        void getImg(Bitmap b);
    }
    public interface Html{
        void getDoc(Document d);
    }
    Context main;
    String name;
	
    Pic img_show;
    Html html_str;
	
    void getPic(final String id,Context c,Pic p)
    {
        //获取图片
        main=c;
        name = id+ ".jpg";
        img_show=p;
        File file = new File(main.getCacheDir(), name);
        // 如果图片存在本地缓存目录，则不去服务器下载 
        ContentResolver cr = c.getContentResolver();
        if (file.exists()) {
            Uri uri =Uri.fromFile(file);//Uri.fromFile(path)这个方法能得到文件的URI
            try {
                InputStream is = cr.openInputStream(uri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize =1;//缩小图片，二的倍数，1/n
                Bitmap bitmap = BitmapFactory.decodeStream(is,null,options);
                img_show.getImg(bitmap);
                //Toast.makeText(main,"缓存中找到了"+file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {}
        } else {
            new Thread(new Runnable() {
                    @Override
                    public void run() {new DownImgAsyncTask().execute(id);}
                }).start();
        }
    }
    void getHtml(final String ul,Html h)
    {
		//获取静态网页
        html_str=h;
        new Thread(new Runnable(){
            @Override
            public void run(){new DownDocAsyncTask().execute(ul);}
        }).start();
    }
	
    class DownDocAsyncTask extends AsyncTask<String, Void, Document> {

        @Override
        protected Document doInBackground(String... params) {
            Connection conn = Jsoup.connect(params[0]);
            //conn.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/    20100101 Firefox/32.0");
            Document doc = null;
            try {
                doc = conn.get();
            } catch (IOException e) {}
            return doc;
        }

        @Override
        protected void onPostExecute(Document result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            html_str.getDoc(result);
        }
    }
	//通过id在服务器下载图片
    class DownImgAsyncTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub
            URL imgUrl = null;
            Bitmap bitmap = null;
            String url = "https://www.agefans.net/detail/"+params[0];
            Connection con = Jsoup.connect(url);
            Document doc = null;
            try {
                doc = con.get();
                Elements res_img = doc.select("img.poster");
                String img_url=res_img.attr("abs:src");
                imgUrl = new URL(/*"https:"+*/img_url);
                //String img_name=res_img.attr("alt");
            } catch (IOException e) {}
            try {
                //imgUrl = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) imgUrl
                    .openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);
                is.close();
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (result != null) {
                img_show.getImg(result);
                File file = new File(main.getCacheDir(),name);
                // 创建一个位于SD卡上的文件 
                FileOutputStream fileOutStream=null; 
                try { 
                    fileOutStream=new FileOutputStream(file); 
                    //把位图输出到指定的文件中 
                    result.compress(Bitmap.CompressFormat.PNG, 100, fileOutStream); 
                    fileOutStream.close(); 
                } catch (IOException io) { 
                    io.printStackTrace(); 
                } 
                Toast.makeText(main,"刚下载到"+file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }
        }

    }
	private SharedPreferences preferences;  
	private SharedPreferences.Editor editor;  
	public Doc(Context mContext, String preferenceName) {  
			preferences = mContext.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);  
			editor = preferences.edit();  
		}  

		/** 
		 * 保存List 
		 * @param tag 
		 * @param datalist 
		 */  
		public <T> void setDataList(String tag, List<T> datalist) {  
			if (null == datalist || datalist.size() <= 0)  
				return;  

			Gson gson = new Gson();  
			//转换成json数据，再保存  
			String strJson = gson.toJson(datalist);  
			editor.clear();  
			editor.putString(tag, strJson);  
			editor.commit();  

		}  

		/** 
		 * 获取List 
		 * @param tag 
		 * @return 
		 */  
		public <T> List<T> getDataList(String tag) {  
			List<T> datalist=new ArrayList<T>();  
			String strJson = preferences.getString(tag, null);  
			if (null == strJson) {  
				return datalist;  
			}  
			Gson gson = new Gson();  
			datalist = gson.fromJson(strJson, new TypeToken<List<T>>() {  }.getType());  
			return datalist;  
		}  
	
}
