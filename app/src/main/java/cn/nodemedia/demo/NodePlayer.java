package cn.nodemedia.demo;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import cn.nodemedia.NodePlayerDelegate;
import cn.nodemedia.NodePlayerView;

public class NodePlayer extends AppCompatActivity implements NodePlayerDelegate {

    TextView textView;
    private cn.nodemedia.NodePlayer np;
    private NodePlayerView mNodePlayer;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.player_node);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String channelName = intent.getStringExtra("channelName");

        mNodePlayer = findViewById(R.id.node_player);
        PlayUrl(url, mNodePlayer);

        mNodePlayer.setOnClickListener(new NodePlayerView.OnClickListener() {
            @Override
            public void onClick(View view) {
            exit();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出播放",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        np.stop();
    }

    protected void onDestroy() {
        super.onDestroy();
        np.stop();
        np.release();
    }

    protected void onResume() {
        super.onResume();
        np.start();
    }

    public void onBackPressed(){
        super.onBackPressed();

    }


    @Override
    public void onEventCallback(cn.nodemedia.NodePlayer nodePlayer, int event, String msg) {
        return;
    }

    public void PlayUrl(String url, NodePlayerView npv) {
        //查询播放视图
        np = new cn.nodemedia.NodePlayer(npv.getContext());
        //设置播放视图的渲染器模式,可以使用SurfaceView或TextureView. 默认SurfaceView
        npv.setRenderType(NodePlayerView.RenderType.SURFACEVIEW);
        //设置视图的内容缩放模式
        npv.setUIViewContentMode(NodePlayerView.UIViewContentMode.ScaleAspectFit);
        //将播放视图绑定到播放器
        np.setPlayerView(npv);
        //设置事件回调代理
        np.setNodePlayerDelegate(this);
        //开启硬件解码,支持4.3以上系统,初始化失败自动切为软件解码,默认开启.
        np.setHWEnable(true);
        /**
         * 设置启动缓冲区时长,单位毫秒.此参数关系视频流连接成功开始获取数据后缓冲区存在多少毫秒后开始播放
         */
        int bufferTime = Integer.valueOf("300");
        np.setBufferTime(bufferTime);
        /**
         * 设置最大缓冲区时长,单位毫秒.此参数关系视频最大缓冲时长.
         * RTMP基于TCP协议不丢包,网络抖动且缓冲区播完,之后仍然会接受到抖动期的过期数据包.
         * 设置改参数,sdk内部会自动清理超出部分的数据包以保证不会存在累计延迟,始终与直播时间线保持最大maxBufferTime的延迟
         */
        int maxBufferTime = Integer.valueOf("1000");
        np.setMaxBufferTime(maxBufferTime);
        /**
         * 设置播放直播视频url
         */
        np.setInputUrl(url);
        np.start();
    }


}
