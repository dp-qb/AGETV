package com.age;
import android.widget.RadioGroup;
import android.content.Context;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton;

public class Radio extends RadioGroup {
    Checked che;
    boolean 默认第一个勾选;
    public interface Checked {
        void getChecked(String b);
    }
    public Radio(final Context context,String n, String[] str,Checked c) {
        super(context);
        che=c;
        setOrientation(HORIZONTAL);
        TextView t_name=new TextView(context);
        t_name.setText( n);
        addView(t_name);
        默认第一个勾选=true;
        for(String s:str)
        {
            RadioButton rad_but=new RadioButton(context);
            rad_but.setText(s);
            rad_but.setId(View.generateViewId());
            rad_but.setBackgroundResource(R.drawable.button_style);
            if(默认第一个勾选){
                rad_but.setChecked(true);
                默认第一个勾选=false;}
            addView(rad_but);
        }
        setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton radbtn = (RadioButton) findViewById(checkedId);
                    che.getChecked(radbtn.getText().toString());
                    Toast.makeText(context, "按钮组值发生改变,你选了" + radbtn.getText(), Toast.LENGTH_LONG).show();
                }     
            });
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取最大宽度
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        //获取Group中的Child数量
        int childCount = getChildCount();
        //设置Group的左边距，下面也会使用x计算每行所占的宽度
        int x = 0;
        //设置Group的上边距，下面也会使用y计算Group所占的高度
        int y = 30;
        //设置Group的行数
        int row = 0;
        for (int index = 0; index < childCount; index++) {
            final View child = getChildAt(index);
            if (child.getVisibility() != View.GONE) {
                child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                //重新计算child的宽高
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                //添加到X中，(width+10) 设置child左边距
                x += (width + 10);
                //行数*child高度+这次child高度=现在Group的高度,(height + 10)设置child上边距
                y = row * (height + 10) + (height + 10);
                //当前行宽X大于Group的最大宽度时，进行换行
                if (x > maxWidth) {
                    //当index不为0时，进行row++，防止FirstChild出现大于maxWidth时,提前进行row++
                    if (index != 0)
                        row++;
                    //child的width大于maxWidth时，重新设置child的width为最大宽度
                    if (width >= maxWidth) {
                        width = maxWidth - 30;
                    }
                    //重新设置当前X
                    x = (width + 20);
                    //重新设置现在Group的高度
                    y = row * (height + 10) + (height + 10);
                }
            }
        }
        // 设置容器所需的宽度和高度
        setMeasuredDimension(maxWidth, y);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = getChildCount();
        int maxWidth = r - l;
        int x = 10;
        int y = 0;
        int row = 0;
        for (int i = 0; i < childCount; i++) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                x += (width + 10);
                y = row * (height + 10) + (height + 10);
                if (x > maxWidth) {
                    if (i != 0)
                        row++;
                    if (width >= maxWidth) {
                        width = maxWidth - 30;
                    }
                    x = (width + 20);
                    y = row * (height + 10) + (height + 10);
                }
                child.layout(x - width, y - height, x, y);
            }
        }
    }
}
