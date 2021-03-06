package com.example.reviewmobile.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reviewmobile.R;
import com.example.reviewmobile.adapter.MyAdapter;
import com.example.reviewmobile.bean.GoodMan;
import com.example.reviewmobile.contactlistindex.FancyIndexer;
import com.example.reviewmobile.contactlistindex.FancyIndexer.OnTouchLetterChangedListener;
import com.example.reviewmobile.utils.Utils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sdf.jazzylistviewdemo.romainpiel.shimmer.ShimmerTextView;
import com.twotoasters.jazzylistview.JazzyListView;
import com.twotoasters.jazzylistview.effects.HelixEffect;

public class ContactList extends Activity {

	private static final int SENDMESSAGE = 1;
	@ViewInject(R.id.tv_app_title)
	ShimmerTextView stv;
	private ArrayList<GoodMan> persons;
	private JazzyListView lv_contact;
	private TextView tv_index_center;
	private ContacatAdapter adapter;
	boolean lvState = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_list);
		
		Bundle bundle = getIntent().getExtras();
		String className = bundle.getString("name");
		
		if(className.equals("setup3")){
			
		}else if(className.equals("blackNumber")){
			
		}
		ViewUtils.inject(this);
		Utils.start(stv);
		Toast.makeText(this, "按住右边字母上下滑动可进行索引-_-'", 1).show();

		initUI();
	}

	// 没办法了只能写个接口来返回联系人了
	public interface OnActivityMsg {
		void contactPhone(String phone);
	}

	static OnActivityMsg oam;

	public static void SetActivityMsg(OnActivityMsg moam) {
		oam = moam;
	}

	// 初始化ui
	public void initUI() {
		tv_index_center = (TextView) findViewById(R.id.tv_index_center);
		lv_contact = (JazzyListView) findViewById(R.id.lv_contact);
		initData();

		mHandler.sendEmptyMessage(SENDMESSAGE);

		FancyIndexer mFancyIndexer = (FancyIndexer) findViewById(R.id.bar);
		mFancyIndexer.setOnTouchLetterChangedListener(new OnTouchLetterChangedListener() {

			@Override
			public void onTouchLetterChanged(String letter) {
				// System.out.println("letter: " + letter);
				// Util.showToast(getApplicationContext(), letter);

				// showLetter(letter);

				for (int i = 0; i < persons.size(); i++) {
					GoodMan goodMan = persons.get(i);

					String s = goodMan.getPinyin().charAt(0) + "";
					if (TextUtils.equals(s, letter)) {
						if (lvState) {
							lv_contact.setSelection(i);
						}
						break;
					}
				}
			}
		});

		lv_contact.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case SCROLL_STATE_IDLE:
					lvState = true;
					break;
				case SCROLL_STATE_FLING:
					lvState = false;
					break;
				case SCROLL_STATE_TOUCH_SCROLL:
					lvState = false;
				default:
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});
		lv_contact.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub

				// 获取当前点击对象，并拿到值
				GoodMan gm = (GoodMan) lv_contact.getItemAtPosition(position);
				String phone = gm.getNumber();
				oam.contactPhone(phone);
//				// 把点击获取到的值返回到接受页面
//				Intent intent = new Intent();
//				// 保存值
//				intent.putExtra("number", phone);
//				// 传递响应码和值
//				setResult(Activity.RESULT_OK, intent);
				// 关闭当前页面
				finish();
			}
		});

	}

	// 初始化数据
	public void initData() {
		persons = readContact1();
		adapter = new ContacatAdapter(persons, ContactList.this);
	}

	// 适配器
	class ContacatAdapter extends MyAdapter<GoodMan> {

		public ContacatAdapter(List<GoodMan> lists, Context mContext) {
			super(lists, mContext);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if (convertView == null) {
				view = View.inflate(ContactList.this, R.layout.contact_list_item, null);
			} else {
				view = convertView;
			}

			ViewHolder holder = ViewHolder.getHolder(view);
			GoodMan goodMan = lists.get(position);

			// 当前汉字拼音的手写字母
			String currentLetter = goodMan.getPinyin().charAt(0) + "";
			String indexStr = null;
			if (position == 0) {
				indexStr = currentLetter;
			} else {
				String preLetter = lists.get(position - 1).getPinyin().charAt(0) + "";
				if (!TextUtils.equals(currentLetter, preLetter)) {
					indexStr = currentLetter;
				}
			}

			holder.tv_index.setVisibility(indexStr == null ? View.GONE : View.VISIBLE);
			holder.tv_index.setText(indexStr);
			holder.tv_number.setText("Number: " + goodMan.getNumber());
			holder.tv_number.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
			holder.tv_number.getPaint().setAntiAlias(true);// 抗锯齿
			holder.tv_name.setText("Name: " + goodMan.getName());
			return view;
		}

	}

	static class ViewHolder {
		public TextView tv_index;
		public TextView tv_name;
		public TextView tv_number;

		public static ViewHolder getHolder(View view) {
			ViewHolder holder = (ViewHolder) view.getTag();

			if (holder == null) {
				holder = new ViewHolder();
				holder.tv_index = (TextView) view.findViewById(R.id.tv_index);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_contact_name);
				holder.tv_number = (TextView) view.findViewById(R.id.tv_contact_number);
				view.setTag(holder);
			}
			return holder;
		}
	}

	// 消息处理器
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			lv_contact.setAdapter(adapter);
			lv_contact.setTransitionEffect(new HelixEffect());
		}
	};

	protected void showLetter(String letter) {
		tv_index_center.setVisibility(View.VISIBLE);
		tv_index_center.setText(letter);

		mHandler.removeCallbacksAndMessages(null);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				tv_index_center.setVisibility(View.GONE);
			}
		}, 1000);

	}

	// 获取联系人集合
	private ArrayList<GoodMan> readContact1() {
		// 通过内容提供者获取联系人列表
		Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null,
				null);
		ArrayList<GoodMan> data = new ArrayList<GoodMan>();
		// 从联系人列表中获取需要的数据
		while (cursor.moveToNext()) {
			String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			GoodMan goodman = new GoodMan(name);
			goodman.setName(name);
			goodman.setNumber(number);
			data.add(goodman);
		}
		Collections.sort(data);
		cursor.close();
		return data;
	}

}
