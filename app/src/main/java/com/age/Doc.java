package com.age;
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
	public Doc()
	{ }  
    public interface Pic
	{
        void getImg(Bitmap b);
    }
    public interface Html
	{
        void getDoc(Document d);
    }
    Context main;

    Pic img_show;
    Html html_str;

    
    void getHtml(final String ul, Html h)
    {
		//获取静态网页
        html_str = h;
        new Thread(new Runnable(){
				@Override
				public void run()
				{new DownDocAsyncTask().execute(ul);}
			}).start();
    }

    class DownDocAsyncTask extends AsyncTask<String, Void, Document>
	{

        @Override
        protected Document doInBackground(String... params)
		{
            Connection conn = Jsoup.connect(params[0]);
            //conn.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/    20100101 Firefox/32.0");
            Document doc = null;
            try
			{
                doc = conn.get();
            }
			catch (IOException e)
			{}
            return doc;
        }

        @Override
        protected void onPostExecute(Document result)
		{
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            html_str.getDoc(result);
        }
    }
}
