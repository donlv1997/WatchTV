package cn.nodemedia.demo;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Title extends LinearLayout {
    public Title(Context context, AttributeSet attrs) {
        super(context, attrs);
        //动态加载一个title.xml的布局文件，this给Title布局添加一个父布局
        LayoutInflater.from(context).inflate(R.layout.title, this);
        ImageView buttonBackward = findViewById(R.id.button_backward);
        buttonBackward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) getContext()).finish();  //销毁该Activity
                System.exit(0);
            }
        });
    }

}
