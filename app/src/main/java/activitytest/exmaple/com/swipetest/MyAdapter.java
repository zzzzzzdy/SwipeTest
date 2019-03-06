package activitytest.exmaple.com.swipetest;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<News> datas;
    private Context context;
    private int normalType = 0;
    private boolean hasMore = true;
    private boolean fadeTips = false;
    private  int footType = 1;
    private Handler mHandler = new Handler(Looper.getMainLooper()); //获取主线程的Handler


    public MyAdapter(List<News> datas, Context context, boolean hasMore) {
        this.datas = datas;
        this.context = context;
        this.hasMore = hasMore;
    }
    class NormalHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView fruitImage;
        TextView fruitName;

        public NormalHolder(@NonNull View itemView) {

            super(itemView);
            cardView = (CardView) itemView;
            fruitImage = itemView.findViewById(R.id.fruit_image);
            fruitName = itemView.findViewById(R.id.fruit_name);
        }
    }
    class FootHolder extends RecyclerView.ViewHolder{
        private TextView tips;

        public FootHolder(@NonNull View itemView) {
            super(itemView);
            tips = itemView.findViewById(R.id.tips);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == normalType) {
            return new NormalHolder(LayoutInflater.from(context).inflate(R.layout.item, null));
        } else {
            return new FootHolder(LayoutInflater.from(context).inflate(R.layout.footview, null));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof NormalHolder)
        {
            News news = datas.get(position);
            ((NormalHolder) holder).fruitName.setText(news.getTitle());
            Glide.with(context).load(news.getUrl()).into(((NormalHolder) holder).fruitImage);
        }else {
            ((FootHolder) holder).tips.setVisibility(View.VISIBLE);
            if(hasMore == true){
                fadeTips = false;
                if(datas.size()>0){
                    ((FootHolder) holder).tips.setText("正在加载");
                }
            }else {
                if (datas.size()>0){
                    ((FootHolder) holder).tips.setText("没有更多数据了");
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 隐藏提示条
                            ((FootHolder) holder).tips.setVisibility(View.GONE);
                            // 将fadeTips设置true
                            fadeTips = true;
                            // hasMore设为true是为了让再次拉到底时，会先显示正在加载更多
                            hasMore = true;
                        }
                    },500);
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return datas.size()+1;
    }
    public int getRealLastPosition() {
        return datas.size();
    }
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return footType;
        } else {
            return normalType;
        }
    }
    // 暴露接口，改变fadeTips的方法
    public boolean isFadeTips() {
        return fadeTips;
    }

    // 暴露接口，下拉刷新时，通过暴露方法将数据源置为空
    public void resetDatas() {
        datas = new ArrayList<>();
    }
    // 暴露接口，更新数据源，并修改hasMore的值，如果有增加数据，hasMore为true，否则为false
    public void updateList(List<News> newDatas, boolean hasMore) {
        // 在原有的数据之上增加新数据
        if (newDatas != null) {
            datas.addAll(newDatas);
        }
        this.hasMore = hasMore;
        notifyDataSetChanged();
    }
}
