package com.age;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.*;
import android.content.*;
import android.widget.*;

public class SettingsActivity extends PreferenceActivity
{
	private PreferenceManager d当前界面;
	private SharedPreferences sp;
	//private SharedPreferences.Editor preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.setting_layout);
		d当前界面 = getPreferenceManager();
		sp = getPreferenceScreen().getSharedPreferences();
		//SharedPreferences.Editor tsp = sp.edit(); //实例化SharedPreferences.Editor对象（第二步）
		findPreference("web域名").setSummary(sp.getString("web域名","空"));
		findPreference("bt_web域名").setSummary(sp.getString("bt_web域名", "空"));
		findPreference("app域名").setSummary(sp.getString("app域名","空"));
		findPreference("key_debug").setSummary(sp.getBoolean("key_debug", false)?"显示调试信息":"隐藏调试信息");
		d当前界面.findPreference("web域名").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
				@Override
				public boolean onPreferenceChange(Preference 被点击的控件, Object objValue)
				{
					被点击的控件.setSummary(String.valueOf(objValue));
					return true; // 保存更新值
				}
			});
		d当前界面.findPreference("bt_web域名").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
				@Override
				public boolean onPreferenceChange(Preference 被点击的控件, Object objValue)
				{
					sp.edit().putString("web域名",sp.getString("bt_web域名", ""));
					sp.edit().commit();
					//被点击的控件.setSummary(String.valueOf(objValue));
					return true; // 保存更新值
				}
			});
		d当前界面.findPreference("app域名").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
				@Override
				public boolean onPreferenceChange(Preference 被点击的控件, Object objValue)
				{
					被点击的控件.setSummary(String.valueOf(objValue));
					return true; // 保存更新值
				}
			});
		d当前界面.findPreference("key_app_web").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
				@Override
				public boolean onPreferenceChange(Preference 被点击的控件, Object objValue)
				{
					//preference.setSummary(String.valueOf(objValue));
					return true; // 保存更新值
				}
			});
		d当前界面.findPreference("key_debug").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
				@Override
				public boolean onPreferenceClick(Preference 被点击的控件)
				{
					被点击的控件.setSummary(sp.getBoolean("key_debug", false)?"显示调试信息":"隐藏调试信息");
					return true;
				}
			});
    }
}
