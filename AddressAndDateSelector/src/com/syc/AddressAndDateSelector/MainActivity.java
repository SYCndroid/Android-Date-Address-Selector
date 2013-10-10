package com.syc.AddressAndDateSelector;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	EditText text1 ;
	EditText text2 ;
	TextView but1 ;
	TextView but2 ;
	String address = null;
	String dateAndTime = null;
	public static final int REQUSET_Rq = 2 ;
	public static final int REQUSET_Dq = 1 ;
	String[] dateType = new String[]{"年","月","日","时","分"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		text1 = (EditText) findViewById(R.id.text1);
		text2 = (EditText) findViewById(R.id.text2);
		but1 = (TextView) findViewById(R.id.but1);
		but2 = (TextView) findViewById(R.id.but2);
		but1.setOnClickListener(this);
		but2.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.but1:
			intent = new Intent(MainActivity.this,RqxxActivity.class);
			startActivityForResult(intent, REQUSET_Rq);
			break;
		case R.id.but2:
			intent = new Intent(getApplicationContext(),DqxxActivity.class);
			startActivityForResult(intent, REQUSET_Dq);
			break;
		default:
			break;
		}
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUSET_Dq && resultCode == RESULT_OK) {  
			address = data.getStringExtra("address");
			System.out.println("address=="+address);
			text2.setText(address);
		}
		if (requestCode == REQUSET_Rq && resultCode == RESULT_OK) {  
			dateAndTime = data.getStringExtra("date");
			String[] dateArr = dateAndTime.split("-");
			StringBuilder sb = new StringBuilder();
			for(int i=0;i<dateArr.length;i++){
				sb.append(dateArr[i]).append(dateType[i]);
			}
			StringBuilder sb2 = new StringBuilder();
			for(int i=0;i<dateArr.length;i++){
				if(i==0){
					sb2.append(dateArr[0]);
				}else if(dateArr[i].length()==1){
					sb2.append("0"+dateArr[i]);
				}else if(dateArr[i].length()==2){
					sb2.append(dateArr[i]);
				}
			}
			System.out.println("appointment=="+sb2.toString());
			text1.setText(sb.toString());
		}
	};
}
