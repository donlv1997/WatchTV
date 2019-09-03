package cn.nodemedia.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class Menu extends AppCompatActivity {

    List<Item> itemList = new ArrayList<>();
    int[] mFirstVisibleItems = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_recycler_view);
        getSupportActionBar().hide();
        final LinearLayout topBar = findViewById(R.id.title);
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        final LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) topBar.getLayoutParams();
        final int titleHeight = lp.height;

        initChannel();
        //LinearLayoutManager layoutManager = new LinearLayoutManager(this);//线性布局
        //layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);//让布局横行排列（默认纵向排列）
        StaggeredGridLayoutManager layoutManager = new
                StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);//使布局纵向排列
        //StaggeredGridLayoutManager构造函数接收2个参数：1、指定布局的列数；2、指定布局的排列方向
        recyclerView.setLayoutManager(layoutManager);//LinearLayoutManager（线性布局）用于指定RecyclerView的布局方式

        ItemAdapter adapter = new ItemAdapter(itemList);
        recyclerView.setAdapter(adapter);
        ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(adapter);
        scaleInAnimationAdapter.setDuration(750);
//        alphaInAnimationAdapter.setInterpolator(new DecelerateInterpolator());
        scaleInAnimationAdapter.setFirstOnly(false);
        AlphaInAnimationAdapter alphaInAnimationAdapter = new AlphaInAnimationAdapter(scaleInAnimationAdapter);
        alphaInAnimationAdapter.setDuration(1000);
        alphaInAnimationAdapter.setFirstOnly(false);
        recyclerView.setAdapter(alphaInAnimationAdapter);

// topbar是自定义的标题栏,下面这段实现了标题栏随着拖动渐隐和出现
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView rv, int newState) {
                super.onScrollStateChanged(rv, newState);
            }

            @Override
            public void onScrolled(RecyclerView rv, int dx, int dy) {
                super.onScrolled(rv, dx, dy);
                float alpha;
                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                // 第一个可见Item的位置
                mFirstVisibleItems = layoutManager.findFirstVisibleItemPositions(mFirstVisibleItems);
                // 是第一项才去渐变
                if (mFirstVisibleItems[0] == 0) {
                    // 注意此操作如果第一项划出屏幕外,拿到的是空的，所以必须是position是0的时候才能调用
                    View firstView = layoutManager.findViewByPosition(mFirstVisibleItems[0]);
                    // 第一项Item的高度
                    int firstItemHeight = firstView.getHeight();
                    // 距离顶部的距离，是负数，也就是说-top就是它离顶部的距离
                    int scrollY = -firstView.getTop();
                    // 要在第一个Item一半的时候开始渐变
                    int changeHeight = firstItemHeight / 2;
                    // 小于changeHeight，隐藏标题栏
                    if (scrollY <= changeHeight) {
                        topBar.setVisibility(View.VISIBLE);
                        lp.height = titleHeight;
                        topBar.setLayoutParams(lp);
                        topBar.setAlpha(1);
                    } else {
                        topBar.setVisibility(View.VISIBLE);
                        // 设置了一条分割线，渐变的时候分割线先GONE掉，要不不好看
//                        topBar.getViewGrayLine().setVisibility(View.GONE);
                        // 透明度从0开始计算
                        if (scrollY <= 2 * changeHeight) {
                            alpha = 2f - (scrollY * 1f / changeHeight);
                        } else
                            alpha = 0;
                        lp.height = (int) (titleHeight * alpha);
                        topBar.setLayoutParams(lp);
                        topBar.setAlpha(alpha);
                    }
                    // 其他的时候就设置都可见，透明度是1
                } else {
                    lp.height = 0;
                    topBar.setLayoutParams(lp);
                    topBar.setVisibility(View.INVISIBLE);
//                    topBar.getViewGrayLine().setVisibility(View.VISIBLE);
                }
            }
        });
// 有几个坑要踩，因为我们全局用一个distanceY记录了坐标
//        1、如果你的recyclerView会有代码调用的scrollTo，scrollToPosition类似的方法，记得处理好你的distanceY
//        2、我这里是有一个点击底部的导航栏去回到顶部并刷新界面的操作：rvMain.scrollToPosition(0),所以回到顶部必须把distanceY置为0，并且topbar要GONE掉
    }


    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }

    public int dp2px(Context context, float dpValue) {
        //获得当前屏幕，获得屏幕原型，获得屏幕密度
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float scale = metrics.density;
        return (int) (dpValue * scale);
    }

    private void initChannel() {
        Item cctv1 = new Item
                ("CCTV1", "http://223.110.241.204:6610/cntv/live1/HD-2500k-1080P-cctv1/HD-2500k-1080P-cctv1", R.drawable.cctv1);
        itemList.add(cctv1);
        Item cctv3 = new Item
                ("CCTV3", "http://ivi.bupt.edu.cn/hls/cctv3hd.m3u8", R.drawable.cctv3);
        itemList.add(cctv3);
        Item cctv5 = new Item
                ("CCTV5", "http://223.110.241.204:6610/cntv/live1/cctv-5/cctv-5", R.drawable.cctv5);
        itemList.add(cctv5);
        Item cctv5p = new Item
                ("CCTV5+", "http://ivi.bupt.edu.cn/hls/cctv5phd.m3u8", R.drawable.cctv5p);
        itemList.add(cctv5p);
        Item cctv6 = new Item
                ("CCTV6", "http://223.110.241.204:6610/cntv/live1/HD-2500k-1080P-cctv6/HD-2500k-1080P-cctv6", R.drawable.cctv6);
        itemList.add(cctv6);
        Item cctv8 = new Item
                ("CCTV8", "http://223.110.241.204:6610/cntv/live1/HD-2500k-1080P-cctv8/HD-2500k-1080P-cctv8", R.drawable.cctv8);
        itemList.add(cctv8);
        Item cctv10 = new Item
                ("CCTV10", "http://223.110.241.204:6610/cntv/live1/cctv-10/cctv-10", R.drawable.cctv10);
        itemList.add(cctv10);
        Item cctv13 = new Item
                ("CCTV13", "http://223.110.241.204:6610/gitv/live1/G_CCTV-13-HQ/G_CCTV-13-HQ/", R.drawable.cctv13);
        itemList.add(cctv13);
        Item cctv14 = new Item
                ("CCTV8", "http://223.110.241.204:6610/cntv/live1/cctv-14/cctv-14", R.drawable.cctv14);
        itemList.add(cctv14);

        Item zhejiang = new Item
                ("浙江卫视", "http://223.110.241.204:6610/cntv/live1/HD-2500k-1080P-zhejiangstv/HD-2500k-1080P-zhejiangstv", R.drawable.zhejiang);
        itemList.add(zhejiang);
        Item jiangsu = new Item
                ("江苏卫视", "http://223.110.241.204:6610/cntv/live1/HD-2500k-1080P-jiangsustv/HD-2500k-1080P-jiangsustv", R.drawable.jiangsu);
        itemList.add(jiangsu);
        Item hunan = new Item
                ("湖南卫视", "http://223.110.241.204:6610/cntv/live1/HD-2500k-1080P-hunanstv/HD-2500k-1080P-hunanstv", R.drawable.hunan);
        itemList.add(hunan);
        Item dongfang = new Item
                ("北京卫视", "http://223.110.241.204:6610/cntv/live1/HD-2500k-1080P-beijingstv/HD-2500k-1080P-beijingstv", R.drawable.beijing);
        itemList.add(dongfang);
        Item beijing = new Item
                ("东方卫视", "http://223.110.241.204:6610/cntv/live1/HD-2500k-1080P-dongfangstv/HD-2500k-1080P-dongfangstv", R.drawable.beijing);
        itemList.add(beijing);
        Item shenzhen = new Item
                ("深圳卫视", "http://223.110.241.204:6610/cntv/live1/HD-2500k-1080P-shenzhenstv/HD-2500k-1080P-shenzhenstv", R.drawable.shenzhen);
        itemList.add(shenzhen);
        Item qinghua = new Item
                ("清华大学电台", "http://v.cic.tsinghua.edu.cn:8080/live/tsinghuatv.flv", R.drawable.qinghua);
        itemList.add(qinghua);
        Item fudan1 = new Item
                ("张江色相头1（仅内网）", "rtsp://10.134.142.230:10554/cameragun", R.drawable.fudan);
        itemList.add(fudan1);
        Item fudan2 = new Item
                ("张江色相头2（仅内网）", "rtsp://10.134.142.230:10554/cameraball", R.drawable.fudan);
        itemList.add(fudan2);

    }
}
