package activitytest.exmaple.com.swipetest;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private List<News> list;
    private int lastVisibleItem = 0;
    private final int PAGE_COUNT = 9;
    private GridLayoutManager mLayoutManager;
    private MyAdapter adapter;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        findView();
        initRefreshLayout();
        initRecyclerView();
    }

    private void initRecyclerView() {
        // 初始化RecyclerView的Adapter
        // 第一个参数为数据，上拉加载的原理就是分页，所以我设置常量PAGE_COUNT=10，即每次加载10个数据
        // 第二个参数为Context
        // 第三个参数为hasMore，是否有新数据
        adapter = new MyAdapter(getDatas(0, PAGE_COUNT), this, getDatas(0, PAGE_COUNT).size() > 0 ? true : false);
        mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 如果没有隐藏footView，那么最后一个条目的位置就比我们的getItemCount少1，自己可以算一下
                    if (adapter.isFadeTips() == false && lastVisibleItem + 1 == adapter.getItemCount()) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 然后调用updateRecyclerview方法更新RecyclerView
                                updateRecyclerView(adapter.getRealLastPosition(), adapter.getRealLastPosition() + PAGE_COUNT);
                            }
                        }, 500);
                    }
                    // 如果隐藏了提示条，我们又上拉加载时，那么最后一个条目就要比getItemCount要少2
                    if (adapter.isFadeTips() == true && lastVisibleItem + 2 == adapter.getItemCount()) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 然后调用updateRecyclerview方法更新RecyclerView
                                updateRecyclerView(adapter.getRealLastPosition(), adapter.getRealLastPosition() + PAGE_COUNT);
                            }
                        }, 500);
                    }
                }

            }
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // 在滑动完成后，拿到最后一个可见的item的位置
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    private void initData() {
        list = new ArrayList<>();
        list.clear();

        HttpConnect httpConnect=new HttpConnect("https://news-at.zhihu.com/api/4/news/before/"+20181212);
        httpConnect.sendRequestWithHttpURLConnection(new HttpConnect.Callback() {
            @Override
            public void finish(String respone) {
                parseJSON(respone);//手解版

            }
        });
        HttpConnect httpConnect1=new HttpConnect("https://news-at.zhihu.com/api/4/news/before/"+20181211);
        httpConnect1.sendRequestWithHttpURLConnection(new HttpConnect.Callback() {
            @Override
            public void finish(String respone) {
                parseJSON(respone);//手解版

            }
        });
        HttpConnect httpConnect2=new HttpConnect("https://news-at.zhihu.com/api/4/news/before/"+20181210);
        httpConnect2.sendRequestWithHttpURLConnection(new HttpConnect.Callback() {
            @Override
            public void finish(String respone) {
                parseJSON(respone);//手解版

            }
        });
        HttpConnect httpConnect3=new HttpConnect("https://news-at.zhihu.com/api/4/news/before/"+20181209);
        httpConnect3.sendRequestWithHttpURLConnection(new HttpConnect.Callback() {
            @Override
            public void finish(String respone) {
                parseJSON(respone);//手解版

            }
        });
        
    }
    private void parseJSON(String respone) {
        try {
//            Log.d("bbbbb", respone);

            JSONObject jsonObject=new JSONObject(respone);

//            JSONObject jsonObjectOne=new JSONObject(jsonObject.getString("data"));


            JSONArray jsonArray=new JSONArray(jsonObject.getString("stories"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                JSONArray jsonArray00 = new JSONArray(jsonObject2.getString("images"));
                Log.d("bbbbb","wocao"+jsonArray00.getString(0));

                News data=new News(jsonObject2.getString("title"),jsonObject2.getInt("id"),
                        jsonArray00.getString(0));
//                Log.d("aaaaa", jsonObject2.getString("images"));



                list.add(data);
            }


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void findView() {
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshlayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

    }
    private void initRefreshLayout() {
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        refreshLayout.setOnRefreshListener(this);
    }
    private List<News> getDatas(final int firstIndex, final int lastIndex) {
        List<News> resList = new ArrayList<>();
        for (int i = firstIndex; i < lastIndex; i++) {
            if (i < list.size()) {
                resList.add(list.get(i));
            }
        }
        return resList;
    }
    // 上拉加载时调用的更新RecyclerView的方法
    private void updateRecyclerView(int fromIndex, int toIndex) {
        // 获取从fromIndex到toIndex的数据
        List<News> newDatas = getDatas(fromIndex, toIndex);
        if (newDatas.size() > 0) {
            // 然后传给Adapter，并设置hasMore为true
            adapter.updateList(newDatas, true);
        } else {
            adapter.updateList(null, false);
        }
    }

    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(true);
        adapter.resetDatas();
        updateRecyclerView(0, PAGE_COUNT);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

    }

