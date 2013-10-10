package com.syc.AddressAndDateSelector;

import java.util.Calendar;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class RqxxActivity extends Activity implements OnClickListener {

	private ImageButton btn_submit, btn_cancel;
	public  String age;
	private DateNumericAdapter monthAdapter, dayAdapter, yearAdapter,hourAdapter, minuteAdapter;
	private WheelView year, month, day, hour, minute;
	private int mCurYear = 80, mCurMonth = 5, mCurDay = 14, mCurHour = 11,mCurMinute = 30;
	SharedPreferences preferences ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		setContentView(R.layout.activity_rqxx);

		this.age = "2013-10-1-10-30";//默认时间
		
		btn_cancel = (ImageButton) findViewById(R.id.cancel);
		btn_submit = (ImageButton) findViewById(R.id.submit);
		year = (WheelView) findViewById(R.id.year);
		month = (WheelView)findViewById(R.id.month);
		day = (WheelView) findViewById(R.id.day);
		hour = (WheelView) findViewById(R.id.hour);
		minute = (WheelView) findViewById(R.id.minute);

		btn_submit.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		
		Calendar calendar = Calendar.getInstance();//使用默认时区和语言环境获得一个日历。
		
		OnWheelChangedListener listener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				updateDays(year, month, day, hour, minute);

			}
		};
		int curYear = calendar.get(Calendar.YEAR);
		if (age != null && age.contains("-")) {
			String str[] = age.split("-");
			mCurYear = 100 - (curYear - Integer.parseInt(str[0]));
			mCurMonth = Integer.parseInt(str[1]) - 1;
			mCurDay = Integer.parseInt(str[2]) - 1;
			mCurHour = Integer.parseInt(str[3]) - 1;
			mCurMinute = Integer.parseInt(str[4]) - 1;
			;
		}
		monthAdapter = new DateNumericAdapter(this, 1, 12, 5);
		month.setViewAdapter(monthAdapter);
		month.setCurrentItem(mCurMonth);
		month.addChangingListener(listener);
		// year

		yearAdapter = new DateNumericAdapter(this, curYear - 100,
				curYear + 100, 100 - 20);
		year.setViewAdapter(yearAdapter);
		year.setCurrentItem(mCurYear);
		year.addChangingListener(listener);
		// day

//		updateDays(year, month, day, hour, minute);
		day.setCurrentItem(mCurDay);
		updateDays(year, month, day, hour, minute);
		day.addChangingListener(listener);

		hourAdapter = new DateNumericAdapter(this, 1, 24, 5);
		hour.setViewAdapter(hourAdapter);
		hour.setCurrentItem(mCurHour);
		updateDays(year, month, day, hour, minute);
		hour.addChangingListener(listener);

		minuteAdapter = new DateNumericAdapter(this, 1, 60, 30);
		minute.setViewAdapter(minuteAdapter);
		minute.setCurrentItem(mCurMinute);
		updateDays(year, month, day, hour, minute);
		minute.addChangingListener(listener);

	}
	private void updateDays(WheelView year, WheelView month, WheelView day,
			WheelView hour, WheelView minute) {

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR,calendar.get(Calendar.YEAR) + year.getCurrentItem());// 将给定的日历字段设置为给定值。
		calendar.set(Calendar.MONTH, month.getCurrentItem());

		int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);//给定此 Calendar 的时间值，返回指定日历字段可能拥有的最大值。
		dayAdapter = new DateNumericAdapter(this, 1, maxDays,calendar.get(Calendar.DAY_OF_MONTH) - 1);//calendar.get(Calendar.DAY_OF_MONTH)返回给定日历字段的值。
		day.setViewAdapter(dayAdapter);

		int curDay = Math.min(maxDays, day.getCurrentItem() + 1);// 返回两个
																	// int值中较大的一个。

		day.setCurrentItem(curDay - 1, true);
		int years = calendar.get(Calendar.YEAR) - 100;
		age = years + "-" + (month.getCurrentItem() + 1) + "-"
				+ (day.getCurrentItem() + 1) + "-"
				+ (hour.getCurrentItem() + 1) + "-"
				+ (minute.getCurrentItem() + 1);

	}

	/**
	 * Adapter for numeric wheels. Highlights the current value.
	 */
	private class DateNumericAdapter extends NumericWheelAdapter {
		int currentItem;
		int currentValue;

		public DateNumericAdapter(Context context, int minValue, int maxValue,
				int current) {
			super(context, minValue, maxValue);
			this.currentValue = current;
			setTextSize(18);
		}

		protected void configureTextView(TextView view) {
			super.configureTextView(view);
			view.setTypeface(Typeface.SANS_SERIF);
		}

		public CharSequence getItemText(int index) {
			currentItem = index;
			return super.getItemText(index);
		}

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.submit:
			Intent intent = new Intent();
			intent.putExtra("date", age);
			setResult(RESULT_OK, intent);
			this.finish();
			break;
		case R.id.cancel:
			this.finish();
			break;
		default:
			break;
		}
	}
}
