package com.lmj.lmjcirclelayout;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lmj.circle.MCircle;

public class MainActivity extends Activity {

    private MCircle mc;
    int i = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mc = (MCircle) findViewById(R.id.myCircle);
    }

    public void reflushData(View v) {
        i++;
        if (i % 2 == 0) {
            mc.flushTexts(new String[]{"五杀能力", "中单能力", "打野能力", "上分能力", "带崩能力", "偷大龙"});
            mc.flushData(new int[]{2, 0, 3, 1, 2, 2});
        } else {
            mc.flushTexts(new String[]{"五杀能力", "中单能力", "打野能力", "上分能力", "带崩能力"});
            mc.flushData(new int[]{2, 0, 3, 1, 2});
        }

    }

    public void btnClick(View v) {
        Toast.makeText(MainActivity.this, ((Button) v).getText(), Toast.LENGTH_SHORT).show();
    }
}
