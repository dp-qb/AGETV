package com.age;
import android.widget.FrameLayout;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import BanGuMi;
import android.graphics.Bitmap;
import android.widget.RelativeLayout;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
public class Cell extends FrameLayout {
    private Context mContext;
    private View mView,bg;
    private ImageView img;
    private TextView name,new_name,new_;
    public Cell(Context context, BanGuMi b,final int t) {
        super(context, null, 0);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.cell, this, true);
        bg=mView.findViewById(R.id.bg);
        img = mView.findViewById(R.id.img);
        name = mView.findViewById(R.id.name);
        new_name = mView.findViewById(R.id.new_name);
        new_ = mView.findViewById(R.id.new_);
        mView.setFocusable(true);//用键盘是否能获得焦点
        mView.setFocusableInTouchMode(true);//触摸是否能获得焦点
		
        bg.getLayoutParams().width = t;
        name.setWidth(t-20);
        new_name.setWidth(t-20);
        new Doc().getPic(b.getid_8(), mContext, new Doc.Pic(){
                @Override
                public void getImg(Bitmap b) {
                   Bitmap p= Bitmap.createScaledBitmap(b,t-20,(int)((t-20)*1.4),true);
                    img.setImageBitmap(p);
                }
            });
        name.setText(b.getname());
		if(b.getnew_name()==null)new_name.setVisibility(View.GONE);
        else new_name.setText(b.getnew_name());
        if(b.getisnew()){new_.setVisibility(View.VISIBLE);}
        else{new_.setVisibility(View.GONE);}
    }
}
