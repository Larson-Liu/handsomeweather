package com.xiaoshuai.handsomeweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.xiaoshuai.handsomeweather.fragment.ChooseAreaFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //动态加载碎片布局，静态加载不出碎片布局
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.choose_area_fragment,new ChooseAreaFragment());
        transaction.commit();
    }
}