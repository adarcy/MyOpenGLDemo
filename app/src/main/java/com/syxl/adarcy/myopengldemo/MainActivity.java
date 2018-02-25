package com.syxl.adarcy.myopengldemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.syxl.adarcy.myopengldemo.opengl.render.FGLView;
import com.syxl.adarcy.myopengldemo.opengl.render.FGLViewActivity;


public class MainActivity extends AppCompatActivity {

    private FGLView mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.bt1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FGLViewActivity.class);
                startActivity(intent);
            }
        });
    }
}
