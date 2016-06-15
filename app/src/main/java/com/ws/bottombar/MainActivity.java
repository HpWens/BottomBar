package com.ws.bottombar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ws.bottombar.view.BottomBar;
import com.ws.bottombar.view.BottomBarTab;

public class MainActivity extends AppCompatActivity {

    Button bt;

    private BottomBar mBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt = (Button) findViewById(R.id.bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "触摸人手按钮", Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt.performClick();
            }
        });

        mBar= (BottomBar) findViewById(R.id.bar);

        mBar.addItem(new BottomBarTab(this,R.mipmap.ic_account_circle_white_24dp)).
                addItem(new BottomBarTab(this,R.mipmap.ic_discover_white_24dp)).
                addItem(new BottomBarTab(this,R.mipmap.ic_arrow_forward_white_24dp)).
                addItem(new BottomBarTab(this,R.mipmap.ic_arrow_back_white_24dp));

    }
}
