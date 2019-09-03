package cn.nodemedia.demo;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    //继承自RecyclerView.Adapter，泛型为FruitAdapter.ViewHolder，其中ViewHolder是在FruitAdapter中定义的内部类
    private List<Item> mItemList;

    public ItemAdapter(List<Item> itemList) {//FruitAdapter的构造函数，接收要展示的数据源
        mItemList = itemList;//将数据源赋值给全局变量mFruitList
    }

    @Override//用于创建ViewHolder实例
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())//用于加载fruit_item布局，LayoutInflater.from()方法可以构造出一个 LayoutInflater对象（布局服务）
                .inflate(R.layout.channel_item_waterfall, parent, false);//再调用inflate()方法就可以动态地加载一个布局文件了，参数1是要加载的布局文件的id,参数2是给加载好的布局再添加一个父布局
        // 第三个参数false，就不会为这个View添加父布局，这样这个View才能被添加到ListView中
        final ViewHolder holder = new ViewHolder(view);//创建一个ViewHolder实例并返回

        holder.ItemImage.setOnClickListener(new View.OnClickListener() {//对子项中的ImageView注册了点击事件
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Item item = mItemList.get(position);
                Intent intent = new Intent(holder.itemView.getContext(), NodePlayer.class);
                intent.putExtra("url", item.getUrl());
                intent.putExtra("channelName", item.getName());
                holder.itemView.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override//用于对RecyclerView子项进行赋值，会在每个子项被滚动到屏幕内的时候执行
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = mItemList.get(position);//通过position得到当前项的Fruit实例，再将数据设置到ViewHolder的ImageView和TextView中即可
        holder.ItemImage.setImageResource(item.getImageId());
        holder.ItemName.setText(item.getName() + item.getBlank());
        holder.ItemBlank.setText(item.getBlank());
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }//告诉RecyclerView一共有多少子项，直接返回数据源长度

    static class ViewHolder extends RecyclerView.ViewHolder {
        View ItemView;
        ImageView ItemImage;
        TextView ItemName;
        TextView ItemBlank;

        public ViewHolder(View view) {//ViewHolder构造函数中传入一个View参数，一般是RecyclerView的外布局
            super(view);//之后就可以通过view.findViewById()方法来获取到布局中的ImageView和TextView的实例了
            ItemView = view;//用来保存子项最外层布局的实例
            ItemImage = view.findViewById(R.id.item_image);
            ItemName = view.findViewById(R.id.item_name);
            ItemBlank = view.findViewById(R.id.item_blank);
        }
    }
}
