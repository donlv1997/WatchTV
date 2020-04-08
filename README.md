# WatchTV是什么？
<br> 
一个有菜单动画效果的看电视APP
<br> 
<div align=center><img width="300" height="300" src="https://github.com/donlv1997/WatchTV/raw/master/app/src/main/res/drawable/tv.jpg"/></div>
<br> <br> 

## 内置播放器NodeMediaClient
<br> <br> 
NodeMediaClient的Demo可以在[GitHub](https://github.com/NodeMedia/NodeMediaClient-Android/tree/2.x)上找到，这里简要介绍Tips：
<br> 
* 必须将包名与原包名保持一致即`cn.nodemedia.demo`，否则会因版权问题无法使用
* 如何使用NodeMediaClient？

    首先把`NodeMediaClient-2.3.7.aar`包放在app/libs目录下，然后再app的build.gradle中添加依赖


```javascript
 
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    repositories {
        flatDir {
            dirs 'libs'
        }
    }
```

```javascript

	dependencies {
		implementation fileTree(dir: 'libs', include: ['*.jar'])
		implementation(name: 'NodeMediaClient-2.3.7', ext: 'aar')
	}
```
<br> 
## 在播放器的Activity界面，本APP利用了PlayUrl函数来实现对指定地址（RTSP、HTTP等直播源）进行播放
<br> 

```javascript
 
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
```

* 还要注意，不要忘记重写回调函数，该回调函数可以实现播放状态的获取，具体可以看GitHub上的Demo程序。本APP没有实现这些复杂的功能

```javascript
 
     @Override
    public void onEventCallback(cn.nodemedia.NodePlayer nodePlayer, int event, String msg) {
        return;
    }	
```
<br> <br> 

## 瀑布流式的滚动菜单
<br> <br> 
本项目主要使用了RecyclerView来实现3列的瀑布流滚动效果，具体实现方式可以参考`《第一行代码》`的第三章内容，这里主要介绍滚动动画的实现方式。

首先必须要添加依赖，项目源码可以在[GitHub](https://github.com/wasabeef/recyclerview-animators)上获取，在[简书](https://www.jianshu.com/p/ce4a8a1743f1)中也有相关介绍
<br> 
```javascript
	dependencies {
		implementation 'com.android.support:recyclerview-v7:28.0.0'
		implementation 'jp.wasabeef:recyclerview-animators:2.3.0'
	}
```

本APP主要利用了其中的adaptor animator，使用了放大Adaptor和逐渐出现adaptor的两级动画嵌套，更多用法请参考项目源码

```javascript
        ItemAdapter adapter = new ItemAdapter(itemList);
        recyclerView.setAdapter(adapter);
        ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(adapter);
        scaleInAnimationAdapter.setDuration(750);
        scaleInAnimationAdapter.setFirstOnly(false);
        AlphaInAnimationAdapter alphaInAnimationAdapter = new AlphaInAnimationAdapter(scaleInAnimationAdapter);
        alphaInAnimationAdapter.setDuration(1000);
        alphaInAnimationAdapter.setFirstOnly(false);
        recyclerView.setAdapter(alphaInAnimationAdapter);
```

关于标题栏渐隐的实现方法，也在下面给出：

```javascript
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
                // 第一个可见Item的位置，注意这里是StaggeredGridLayoutManager要传入一个数组，其他两张Manager都只需要传入一个int
                mFirstVisibleItems = layoutManager.findFirstVisibleItemPositions(mFirstVisibleItems);
                // 是第一项才去渐变，而mFirstVisibleItems[0]就是屏幕所显示的第一个Item的position值
                if (mFirstVisibleItems[0] == 0) {
                    // 注意此操作如果第一项划出屏幕外,拿到的是空的，所以必须是position是0的时候才能调用
                    View firstView = layoutManager.findViewByPosition(mFirstVisibleItems[0]);
                    // 第一项Item的高度
                    int firstItemHeight = firstView.getHeight();
                    // 距离顶部的距离，是负数，也就是说-top就是它离顶部的距离
                    int scrollY = -firstView.getTop();
                    // 要在第一个Item一半的时候开始渐变
                    int changeHeight = firstItemHeight / 2;
                    // 小于changeHeight，显示标题栏
                    if (scrollY <= changeHeight) {
                        topBar.setVisibility(View.VISIBLE);
                        lp.height = titleHeight;
                        topBar.setLayoutParams(lp);
                        topBar.setAlpha(1);
                    } else {
                        topBar.setVisibility(View.VISIBLE);
                        // 透明度与相对位置的函数，这里我用了个双曲函数
                        if (scrollY <= 2 * changeHeight) {
                            alpha = 2f - (scrollY * 1f / changeHeight);
                        } else
                            alpha = 0;
                        lp.height = (int) (titleHeight * alpha);
                        topBar.setLayoutParams(lp);
                        topBar.setAlpha(alpha);
                    }
                    // 其他的时候就设置不可见
                } else {
                    lp.height = 0;
                    topBar.setLayoutParams(lp);
                    topBar.setVisibility(View.INVISIBLE);
//                    topBar.getViewGrayLine().setVisibility(View.VISIBLE);
                }
            }
        });
//用layoutManager可以动态修改自定义标题栏的LinearLayout高度
    }

```

# 如何更新信源

请在cn.nodeMedia.demo中的Menu.java的initChannel()函数中添加新频道。初始化格式为(电台名称，电台地址，图标)，电台的图标放在drawable文件夹中，请看示例:

```javascript
        Item cctv1 = new Item
                ("CCTV1", "http://223.110.241.204:6610/cntv/live1/HD-2500k-1080P-cctv1/HD-2500k-1080P-cctv1", R.drawable.cctv1);
        itemList.add(cctv1);
```


# 播放效果演示

![demo](http://chuantu.xyz/t6/702/1567511971x3030586988.png)
<br> 

![demo](http://chuantu.xyz/t6/702/1567512075x3030586988.png)
