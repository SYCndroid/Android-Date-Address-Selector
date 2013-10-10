package com.syc.AddressAndDateSelector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

public class DqxxActivity extends Activity implements OnClickListener {
	private ImageButton btn_submit, btn_cancel;
	private static final String DBNAME = "dqxx.db";
	private SQLiteDatabase db;
	private ArrayList<ContentValues> provinceArr;// 省
	private ArrayList<ContentValues> cityArr;// 城市
	private ArrayList<ContentValues> areaArr;// 地区

	private Map<String, Integer> provinceMap;// 省
	private Map<String, Integer> cityMap;// 城市
	private Map<String, Integer> areaMap;// 地区

	private String[] provinceArray;
	private String[] cityArray;
	private String[] areaArray;
	private String[] full_areaArray;

	private WheelView provinceWheelView;
	private WheelView cityWheelView;
	private WheelView areaWheelView;

	private ProviceCityAreaAdapter provinceAdapter;
	private ProviceCityAreaAdapter cityAdapter;
	private ProviceCityAreaAdapter areaAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		setContentView(R.layout.activity_dqxx);

		initWheelView();
	}

	public void initWheelView() {

		provinceWheelView = (WheelView)findViewById(R.id.provice);
		cityWheelView = (WheelView) findViewById(R.id.city);
		areaWheelView = (WheelView) findViewById(R.id.area);
		btn_cancel = (ImageButton) findViewById(R.id.cancel);
		btn_submit = (ImageButton) findViewById(R.id.submit);
		btn_submit.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		// 初始化省滚轮列表选择器
		initProviceMap();
		provinceAdapter = new ProviceCityAreaAdapter(this, provinceArray, 0);
		provinceWheelView.setViewAdapter(provinceAdapter);
		provinceWheelView.setCurrentItem(0);
		provinceWheelView.addScrollingListener(privinceScrollListener);

		// 初始化城市滚轮列表选择器
		String provinceName = provinceArray[0];
		int province_id = provinceMap.get(provinceName);
		if (provinceName.endsWith("市")) {
			initCityMap(province_id, false);
		} else {
			initCityMap(province_id, true);
		}
		cityAdapter = new ProviceCityAreaAdapter(this, cityArray, 0);
		cityWheelView.setViewAdapter(cityAdapter);
		cityWheelView.setCurrentItem(0);
		cityWheelView.addScrollingListener(cityScrollListener);

		// 初始化地区滚轮列表选择器
		String cityName = cityArray[0];
		int city_id = cityMap.get(cityName);
		provinceName = cityArray[0];
		if (provinceName.endsWith("市")) {
			city_id = city_id * 100 + 1;
		}
		initAreaMap(city_id);
		areaAdapter = new ProviceCityAreaAdapter(DqxxActivity.this, areaArray, 0);
		areaWheelView.setViewAdapter(areaAdapter);
		areaWheelView.setCurrentItem(0);

	}

	OnWheelScrollListener privinceScrollListener = new OnWheelScrollListener() {

		@Override
		public void onScrollingStarted(WheelView wheel) {
		}

		@Override
		public void onScrollingFinished(WheelView wheel) {
			int currentItem = wheel.getCurrentItem();
			String provinceName = provinceArray[currentItem];
			int province_id = provinceMap.get(provinceName);
			if (provinceName.endsWith("市")) {
				initCityMap(province_id, false);
			} else {
				initCityMap(province_id, true);
			}

			cityAdapter = new ProviceCityAreaAdapter(DqxxActivity.this,
					cityArray, 0);
			cityWheelView.setViewAdapter(cityAdapter);
			cityWheelView.setCurrentItem(0);

			String cityName = cityArray[0];
			int city_id = cityMap.get(cityName);
			if (provinceName.endsWith("市")) {
				city_id = city_id * 100 + 1;
			}
			initAreaMap(city_id);
			areaAdapter = new ProviceCityAreaAdapter(DqxxActivity.this,
					areaArray, 0);
			areaWheelView.setViewAdapter(areaAdapter);
			areaWheelView.setCurrentItem(0);
		}
	};

	OnWheelScrollListener cityScrollListener = new OnWheelScrollListener() {

		@Override
		public void onScrollingStarted(WheelView wheel) {
		}

		@Override
		public void onScrollingFinished(WheelView wheel) {
			String provinceName = provinceArray[provinceWheelView
					.getCurrentItem()];
			int city_id = cityMap.get(cityArray[wheel.getCurrentItem()]);
			if (provinceName.endsWith("市")) {
				city_id = city_id * 100 + 1;
			}
			initAreaMap(city_id);
			areaAdapter = new ProviceCityAreaAdapter(DqxxActivity.this, areaArray, 0);
			areaWheelView.setViewAdapter(areaAdapter);
			areaWheelView.setCurrentItem(0);
		}
	};

	public void initProviceMap() {
		DqxxUtils.copyDqdbIfNeeded(this);
		if (db == null) {
			db = this.openOrCreateDatabase(DqxxActivity.this.getFilesDir()
					.getAbsolutePath() + "/" + DBNAME, Context.MODE_PRIVATE,
					null);
		}
		provinceArr = DqxxUtils.getProvince(db);
		provinceMap = new HashMap<String,Integer>();
		provinceArray = new String[provinceArr.size()];
		for (int i = 0; i < provinceArr.size(); i++) {
			provinceMap.put(provinceArr.get(i).getAsString("province"),provinceArr.get(i).getAsInteger("province_id"));
			provinceArray[i] = provinceArr.get(i).getAsString("province");
		}
	}

	public void initCityMap(int province_id, boolean municipalities) {
		DqxxUtils.copyDqdbIfNeeded(DqxxActivity.this);
		if (db == null) {
			db = DqxxActivity.this.openOrCreateDatabase(DqxxActivity.this.getFilesDir()
					.getAbsolutePath() + "/" + DBNAME, Context.MODE_PRIVATE,
					null);
		}
		cityArr = DqxxUtils.getCity(db, province_id, municipalities);
		cityArray = new String[cityArr.size()];
		cityMap = new HashMap<String,Integer>();
		for (int i = 0; i < cityArr.size(); i++) {
			cityMap.put(cityArr.get(i).getAsString("city"),cityArr.get(i).getAsInteger("city_id"));
			cityArray[i] = cityArr.get(i).getAsString("city");
		}
	}

	public void initAreaMap(int province_id) {
		DqxxUtils.copyDqdbIfNeeded(DqxxActivity.this);
		if (db == null) {
			db = DqxxActivity.this.openOrCreateDatabase(DqxxActivity.this.getFilesDir()
					.getAbsolutePath() + "/" + DBNAME, Context.MODE_PRIVATE,
					null);
		}
		areaArr = DqxxUtils.getArea(db, province_id);
		areaMap = new HashMap<String,Integer>();
		areaArray = new String[areaArr.size()];
		full_areaArray = new String[areaArr.size()];
		for (int i = 0; i < areaArr.size(); i++) {
			areaMap.put(areaArr.get(i).getAsString("area"),areaArr.get(i).getAsInteger("area_id"));
			areaArray[i] = areaArr.get(i).getAsString("area");
			full_areaArray[i] = areaArr.get(i).getAsString("full_area");			
		}
	}

	public class ProviceCityAreaAdapter extends ArrayWheelAdapter<String> {
		private int currentItem;
		private int currentValue;

		public ProviceCityAreaAdapter(Context context, String[] items,
				int current) {
			super(context, items);
			this.currentValue = current;
		}

		public void setCurrentValue(int value) {
			this.currentValue = value;
		}

		@Override
		protected void configureTextView(TextView view) {
			super.configureTextView(view);
			view.setTypeface(Typeface.SANS_SERIF);
		}

		@Override
		public View getItem(int index, View convertView, ViewGroup parent) {
			currentItem = index;
			return super.getItem(index, convertView, parent);
		}

	}

	@Override
	protected void onDestroy() {
		if (db != null) {
			db.close();
			db = null;
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.submit:
			String address = full_areaArray[areaWheelView.getCurrentItem()];
			ContentValues provinceValues=provinceArr.get(provinceWheelView.getCurrentItem());
			ContentValues cityValues=cityArr.get(cityWheelView.getCurrentItem());
			ContentValues areaValues=areaArr.get(areaWheelView.getCurrentItem());
			
//			Toast.makeText(
//					DqxxActivity.this,
//					address+ " key:"+ DqxxUtils.findPrimaryKey(db,address)+""+areaValues.getAsString("area")+""+areaValues.getAsInteger("area_id"),
//					Toast.LENGTH_SHORT).show();
			Intent intent = new Intent();
			intent.putExtra("province_id", provinceValues.getAsInteger("province_id"));
			intent.putExtra("province", provinceValues.getAsString("province"));
			intent.putExtra("city_id", cityValues.getAsInteger("city_id"));
			intent.putExtra("city", cityValues.getAsString("city"));
			intent.putExtra("area_id", areaValues.getAsInteger("area_id"));
			intent.putExtra("area", areaValues.getAsString("area"));
			intent.putExtra("address", address);
			setResult(RESULT_OK, intent);
			finish();
			break;
		case R.id.cancel:
			finish();
			break;
		default:
			break;
		}

	}
}
