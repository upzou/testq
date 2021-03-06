package fastandroid.fast.com.cn.fastandroid.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fastandroid.fast.com.cn.fastandroid.R;
import fastandroid.fast.com.cn.fastandroid.adapter.NewsAdapter;
import fastandroid.fast.com.cn.fastandroid.bean.MenuDetail;
import fastandroid.fast.com.cn.fastandroid.bean.News;
import fastandroid.fast.com.cn.fastandroid.db.DBHelper;

import static fastandroid.fast.com.cn.fastandroid.fragment.HomeFragmenrt.homeAppAdapter;

/**
 * Created by zzs on 2017/4/24
 */

public class PushNewsNoticeActivity extends Activity {

    private List<News> mNewsData;
    public static final String TAG = "PushNewsNoticeActivity";
    public static NewsAdapter newsAdapter;
    private List<MenuDetail> menus;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_newsnotice);

        initData();

        initView();
    }

    private void initData() {

        Intent intent = getIntent();
        //获取传过来的MenuDetail集合
        menus = (List<MenuDetail>) intent.getSerializableExtra("menu");


        mNewsData = new ArrayList<>();

//        for (int i = 0; i < 10; i++) {
//            News news = new News("新闻标题" + i, "新闻内容新闻内容新闻内容新闻内容新闻内容新闻内容新" + i);
//            mNewsData.add(news);
//        }

        DBHelper dbHelper = new DBHelper(this, getString(R.string.DB_NEWS));

        Cursor cursor = dbHelper.query(getString(R.string.TABLE_NEWS));
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String url = cursor.getString(cursor.getColumnIndex("url"));
            String isRead = cursor.getString(cursor.getColumnIndex("isRead"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            String appid = cursor.getString(cursor.getColumnIndex("appid"));
            if (appid==null||appid.equals("1")||appid.equals("")) {//

                News news = new News();
                news.setTitle(title);
                news.setContent(content);
                news.setUrl(url);
                news.setTime(time);
                //从数据库取数据时判断是否已读
                if (isRead.equals("1")) {
                    news.setRead(false);
                } else if (isRead.equals("0")) {
                    news.setRead(true);
                }

                mNewsData.add(0, news);
                Log.d(TAG, "initData2: " + mNewsData);
            }
        }

        cursor.close();
        dbHelper.close();
    }


    private void initView() {
        ListView lv_push_news_notice = (ListView) findViewById(R.id.lv_push_news_notice);
        LinearLayout ll_push_news_notice_empty = (LinearLayout) findViewById(R.id.ll_push_news_notice_empty);
        Button bt_one = (Button) findViewById(R.id.bt_one);
        Button bt_two = (Button) findViewById(R.id.bt_two);

        ImageView left_image = (ImageView) findViewById(R.id.left_image);
        LinearLayout view_left = (LinearLayout) findViewById(R.id.view_left);
        left_image.setImageResource(R.drawable.angle_left);
        view_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView mMID_title = (TextView) findViewById(R.id.midle_title);
        mMID_title.setText("推送");

        newsAdapter = new NewsAdapter(this, mNewsData);
        lv_push_news_notice.setAdapter(newsAdapter);
        lv_push_news_notice.setEmptyView(ll_push_news_notice_empty);

        lv_push_news_notice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent1 = new Intent(PushNewsNoticeActivity.this, DetailActivity.class);
                intent1.putExtra("detail_url", mNewsData.get(i).getUrl());
                startActivity(intent1);

                mNewsData.get(i).setRead(true);//点击后设为已读
                newsAdapter.notifyDataSetChanged();
                homeAppAdapter.notifyDataSetChanged();

                //同时更改数据库中isRead标签
                DBHelper dbHelper = new DBHelper(PushNewsNoticeActivity.this, getString(R.string.DB_NEWS));
                ContentValues contentValues = new ContentValues();
                contentValues.put("isRead", 0);
                dbHelper.update(contentValues, "title=?", new String[]{mNewsData.get(i).getTitle()}, getString(R.string.TABLE_NEWS));
                dbHelper.close();
            }
        });

        bt_one.setText(menus.get(0).getName());
        bt_two.setText(menus.get(1).getName());

        bt_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplication(), DetailActivity.class);
                String url = menus.get(0).getUrl();//将对应的url传到详情界面
                intent1.putExtra("detail_url", url);
                startActivity(intent1);
            }
        });

        bt_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplication(), DetailActivity.class);
                String url = menus.get(1).getUrl();//将对应的url传到详情界面
                intent1.putExtra("detail_url", url);
                startActivity(intent1);
            }
        });

    }
}
