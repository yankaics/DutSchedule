package com.siwe.dutschedule.activity;

/** 
 * @author Zhanglinwei
 @author xukai
 * @version 2013/3/10
 * �γ�������
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.siwe.dutschedule.R;
import com.siwe.dutschedule.infoGeter.SubjectGeter;
import com.siwe.dutschedule.setting_edit.ClearAll;
import com.umeng.analytics.MobclickAgent;

public class ClassActivity extends Activity {

	private SQLiteDatabase mysql;
	private ImageView cursor;
	private MySQLiteOpenHelper myOpenHelper;
	private int offset = 0; // ƫ����
	private int currIndex = 1; // ��ǰ�α�λ��
	private int dayOfWeek, tmpday = 4;
	private int bmpW;
	private String param;
	private GestureDetector detector;
	myGestureListener gesTouch = new myGestureListener();
	private Button top_t1, top_t2, top_t3, top_t4, top_t5, top_t6, top_t7,
			temp;
	private TextView tv11, tv12, tv21, tv22, tv31, tv32, tv41, tv42, tv51,
			tv52, calctext;
	private MyViewGroup myViewGroup;
	private LayoutInflater layoutInflater;
	private View menu_view, content_view;
	private int window_windth;// ��ť����
	private ListView lv_menu;
	private ProgressBar probar;
	private boolean hasMeasured = false;
	private static int distance; // ��������
	static int week;
	private String title[] = { "���µ���", "�������", "����" };
	private ViewTreeObserver viewTreeObserver;
	private Button[] bt_cla = new Button[5];
	int[] claId = { R.id.cla1, R.id.cla2, R.id.cla3, R.id.cla4, R.id.cla5 };

	public void onCreate(Bundle savedInstanceState) {
		System.out.println("����onCreate����");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// �������Ƽ����
		setContentView(R.layout.groupmain);
		InitMenu();
		InitDetailShow();
		InitTextView();
		InitImageView();
		detector = new GestureDetector(this, this.gesTouch);
		dayOfWeek = TimeUtils.getDayOfWeek();
		this.insertView(currIndex, dayOfWeek);
	}

	/***
	 * С����ĵĳ�ʼ��
	 * 
	 * @param ����
	 * @return ����ֵ
	 */

	private void InitImageView() {
		cursor = (ImageView) content_view.findViewById(R.id.cursor);
		probar = (ProgressBar) content_view.findViewById(R.id.progressBar1);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.cursor)
				.getWidth();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels; // ��ȡ�ֱ��ʿ���
		offset = (screenW / 7 - bmpW) / 2; // ����ƫ����
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);

	}

	/***
	 * �����ı����Լ������ĳ�ʼ������������
	 * 
	 * @param
	 * @return
	 */
	private void InitTextView() {

		tv11 = (TextView) content_view.findViewById(R.id.tv11);
		tv12 = (TextView) content_view.findViewById(R.id.tv12);
		tv21 = (TextView) content_view.findViewById(R.id.tv21);
		tv22 = (TextView) content_view.findViewById(R.id.tv22);
		tv31 = (TextView) content_view.findViewById(R.id.tv31);
		tv32 = (TextView) content_view.findViewById(R.id.tv32);
		tv41 = (TextView) content_view.findViewById(R.id.tv41);
		tv42 = (TextView) content_view.findViewById(R.id.tv42);
		tv51 = (TextView) content_view.findViewById(R.id.tv51);
		tv52 = (TextView) content_view.findViewById(R.id.tv52);

		int Ids[] = new int[] { R.id.text1, R.id.text2, R.id.text3, R.id.text4,
				R.id.text5, R.id.text6, R.id.text7 };
		// �򻯺�
		for (int i = 0; i < 7; i++) {
			temp = (i == 0 ? top_t1 : i == 1 ? top_t2 : i == 2 ? top_t3
					: i == 3 ? top_t4 : i == 4 ? top_t5 : i == 5 ? top_t6
							: top_t7);
			temp = (Button) content_view.findViewById(Ids[i]);
			temp.setOnClickListener(new MyOnClickListener(i));
		}

		// �˵�������
		Button bt = (Button) content_view.findViewById(R.id.btmenu);
		bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (MyViewGroup.isMenuOpned)
					myViewGroup.closeMenu();
				else
					myViewGroup.showMenu(); // �����˵�
			}
		});

		calctext = (TextView) findViewById(R.id.calctext);
		week = TimeUtils.getweek();
		if (week == 0) {
			calctext.setText("����");
		} else {
			System.out.println("��ǰ��" + week + "��");
			calctext.setText("�� " + week + " ��");
		}

	}

	// ��ť������
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			// mPager.setCurrentItem(index);
			System.out.println("onClick");
			insertView(currIndex, index + 1);
			currIndex = index + 1;
		}
	}

	/***
	 * ���½����ϵĿγ�����
	 * 
	 * @param int current,int gotoDay
	 * @return
	 */
	public void insertView(int current, int gotoDay) {

		System.out.println("����insertView����");
		myOpenHelper = new MySQLiteOpenHelper(this);
		mysql = myOpenHelper.getReadableDatabase();

		String[] infoArry = myOpenHelper.selectWithDay(mysql, gotoDay);
		String[] classname = infoArry[0].split("&");
		String[] address = infoArry[3].split("&");
		String[] weeks = infoArry[2].split("&");

		tv11.setText(classname[0]);
		tv21.setText(classname[1]);
		tv31.setText(classname[2]);
		tv41.setText(classname[3]);
		tv51.setText(classname[4]);

		tv12.setText("�ص�:" + address[0] + "  �Ͽ���:" + weeks[0]);
		tv22.setText("�ص�:" + address[1] + "  �Ͽ���:" + weeks[1]);
		tv32.setText("�ص�:" + address[2] + "  �Ͽ���:" + weeks[2]);
		tv42.setText("�ص�:" + address[3] + "  �Ͽ���:" + weeks[3]);
		tv52.setText("�ص�:" + address[4] + "  �Ͽ���:" + weeks[4]);

		int one = offset * 2 + bmpW;
		Animation animation = null;
		animation = new TranslateAnimation(one * (current - 1), one
				* (gotoDay - 1), 0, 0);
		animation.setFillAfter(true);
		animation.setDuration(300);
		cursor.startAnimation(animation);
		System.out.println("insertView:" + current + "����>" + gotoDay);
		mysql.close();
		currIndex = gotoDay;

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		System.out.println("����onStop����");
		super.onStop();
	}

	@Override
	protected void onResume() {

		System.out.println("����ClassActivity��onResume����");
		super.onResume();
		insertView(currIndex, currIndex);
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		System.out.println("����onPause����");
		super.onPause();
		System.gc();
		if (mysql.isOpen()) {
			mysql.close();
		}
		// ����Android SQLite - close() was never explicitly called on database�쳣
		if (myOpenHelper != null) {
			myOpenHelper.close();
		}
		MobclickAgent.onPause(this);
	}

	/***
	 * ҳ��Ļ������Ƽ���
	 * 
	 * @param
	 */
	public class myGestureListener extends SimpleOnGestureListener {

		// �������ƶ�������֮�����С����
		final int FLIP_DISTANCE = 120;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			/*
			 * �����һ�������¼���X������ڵڶ��������¼���X���곬��FLIP_DISTANCE Ҳ�������ƴ������󻬡�
			 */
			if (e1.getX() - e2.getX() > FLIP_DISTANCE) {
				System.out.println("1");
				insertView(currIndex, (currIndex += 1) == 8 ? 1 : currIndex);
				currIndex = ((currIndex) == 8 ? 1 : currIndex);
				System.out.println("currIndex=" + currIndex);
				return true;
			}

			/* * ����ڶ��������¼���X������ڵ�һ�������¼���X���곬��FLIP_DISTANCE Ҳ�������ƴ������󻬡� */

			else if (e2.getX() - e1.getX() > FLIP_DISTANCE) {
				System.out.println("2");

				insertView(currIndex, (currIndex -= 1) == 0 ? 7 : currIndex);
				currIndex = (currIndex == 0 ? 7 : currIndex);
				System.out.println("currIndex=" + currIndex);
				return true;
			}

			return false;
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (MyViewGroup.isMenuOpned && (event.getX() >= distance)) {// ���˵��򿪲��Ҵ����Ұ벿��ʱ�رղ˵�
			myViewGroup.closeMenu();
			return false;
		}
		detector.onTouchEvent(event);
		/*
		 * if(event.getAction()==MotionEvent.ACTION_UP) isFling = false;
		 */
		super.dispatchTouchEvent(event);
		return true;

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		return this.detector.onTouchEvent(event);
	}

	/***
	 * ��ʼ����ߵĲ˵���ͼ���Լ����������
	 * 
	 * @param
	 * 
	 */
	private void InitMenu() {

		// System.out.println("����InitMenu����");
		myViewGroup = (MyViewGroup) this.findViewById(R.id.vg_main);
		layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		menu_view = layoutInflater.inflate(R.layout.slide_menu, null);
		content_view = layoutInflater.inflate(R.layout.activity_class, null);
		myViewGroup.addView(menu_view);
		myViewGroup.addView(content_view);
		lv_menu = (ListView) menu_view.findViewById(R.id.lv_menu);
		lv_menu.setCacheColorHint(0);
		lv_menu.setAdapter(new ArrayAdapter<String>(this, R.layout.menuitem,
				R.id.tv_item, title));
		setListViewHeightBaseOnChildren(lv_menu);
		getMAX_WIDTH();

		// �˵������ʱ�����ķ���

		lv_menu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// �����رղ˵��Ķ�����Ч

				/*
				 * myViewGroup.setCloseAnimation(new CloseAnimation() {
				 * 
				 * @Override public void closeMenuAnimation() { if
				 * (myViewGroup.getScrollX() == -window_windth)
				 * myViewGroup.closeMenu_2(); } }); myViewGroup.closeMenu_1();
				 */

				myViewGroup.closeMenu();
				switch (position) {
				case 0: // ���µ���
					GetClassTask task = new GetClassTask();
					task.execute((Void) null);
					break;
				/*
				 * case 1: // �༭�α� startActivity(new Intent(ClassActivity.this,
				 * ClassEdit.class)); break;
				 */
				case 1: // �������

					new AlertDialog.Builder(ClassActivity.this)
							.setCancelable(false)
							.setIcon(R.drawable.ic_launcher)
							.setTitle("�������")
							.setMessage("��ȷ��Ҫ������пα��ͳɼ���Ϣ��")
							.setPositiveButton("ȷ��",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											myOpenHelper = new MySQLiteOpenHelper(
													ClassActivity.this);
											mysql = myOpenHelper
													.getReadableDatabase();
											ClearAll.clear(mysql);

											new AlertDialog.Builder(
													ClassActivity.this)
													.setCancelable(false)
													.setIcon(
															R.drawable.ic_launcher)
													.setTitle("����ɹ�")
													.setMessage("������Ϣ��ɾ����")
													.setPositiveButton(
															"ȷ��",
															new DialogInterface.OnClickListener() {
																@Override
																public void onClick(
																		DialogInterface dialog,
																		int which) {
																	dialog.cancel();

																}
															}).show();
											mysql.close();

										}

									})
							.setNegativeButton("ȡ��",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.cancel();
										}
									}).show();

					break;
				case 2: // ����
					ClassActivity.this.finish();
					break;
				default:
					break;
				}
			}
		});

	}

	/**
	 * ��д���ؼ��Ͳ˵����Ĺ���
	 * 
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (MyViewGroup.isMenuOpned) {
				myViewGroup.closeMenu();
				return true;
			} else {
				ClassActivity.this.finish();
			}
		}

		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (MyViewGroup.isMenuOpned)
				myViewGroup.closeMenu();
			else
				myViewGroup.showMenu();
		}
		return true;
	}

	/***
	 * ����iv_button�Ŀ���
	 * 
	 * @param
	 */
	public void getMAX_WIDTH() {
		System.out.println("����getMAX_WIDTH()����");
		viewTreeObserver = menu_view.getViewTreeObserver();
		viewTreeObserver.addOnPreDrawListener(new OnPreDrawListener() {

			@Override
			public boolean onPreDraw() {
				if (!hasMeasured) {
					window_windth = getWindowManager().getDefaultDisplay()
							.getWidth();
					distance = window_windth / 2;
					System.out.println(distance);
					myViewGroup.setDistance(distance);
					// ����������Ҫ����lv_menu�Ŀ��ȣ�����Ϊviewpager��listview
					// �ᷢ�ͳ�ͻ��Ч����scrollviewһ������������˽������һ��.��
					ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) lv_menu
							.getLayoutParams();
					layoutParams.width = distance;
					lv_menu.setLayoutParams(layoutParams);
					hasMeasured = true;
				}
				return true;
			}
		});

	}

	/***
	 * ��̬����listview�ĸ߶� ע����listview��scrollview��ͻ��ʱ�����ǿ������������������̬����listview�ĸ߶ȣ�
	 * ���������ﲻ�У�ԭ����ȷ�����ǻ���һ���취���ǰ�listview���ú���Ļһ���ĸ߶ȣ�
	 * ����������viewgroup��scrillview�϶�������.
	 * 
	 * @param listView
	 */

	public void setListViewHeightBaseOnChildren(ListView listView) {
		ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
		layoutParams.height = getWindowManager().getDefaultDisplay()
				.getHeight();
		listView.setLayoutParams(layoutParams);
	}

	public void InitDetailShow() {

		class detailListener implements View.OnLongClickListener {
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("detailListener����");
				int temp = v.getId();
				int no = temp == R.id.cla1 ? 1 : v.getId() == R.id.cla2 ? 3 : v
						.getId() == R.id.cla3 ? 5 : v.getId() == R.id.cla4 ? 7
						: 9;
				Intent in = new Intent(ClassActivity.this, DetailActivity.class);
				in.putExtra("no", no);
				in.putExtra("day", currIndex);
				startActivity(in);
				return true;

			}

		}

		for (int i = 0; i < 5; i++) {
			bt_cla[i] = (Button) content_view.findViewById(claId[i]);
			bt_cla[i].setOnLongClickListener(new detailListener());
		}
	}

	/**
	 * 
	 * @author linwei ��̨ˢ�¿α�����
	 * 
	 */
	public class GetClassTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			SharedPreferences sharedPrefrences = getSharedPreferences("user",
					MODE_PRIVATE);
			param = sharedPrefrences.getString("usernamepassword",
					"zjh=201281084&mm=755213");
			System.out.println("param=" + param);
			probar.setVisibility(View.VISIBLE);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Looper.prepare();
			return new SubjectGeter(ClassActivity.this, param).getTwoPageInfo();

		}

		@Override
		public void onCancelled() {
			super.onCancelled();
		}

		public void onPostExecute(Boolean success) {

			probar.setVisibility(View.GONE);
			if (success) {
				Toast.makeText(ClassActivity.this, "ˢ�³ɹ�", Toast.LENGTH_SHORT)
						.show();
				insertView(currIndex, dayOfWeek);
			} else {
				Toast.makeText(ClassActivity.this, "ˢ��ʧ��", Toast.LENGTH_SHORT)
						.show();
			}

		}

	}

}