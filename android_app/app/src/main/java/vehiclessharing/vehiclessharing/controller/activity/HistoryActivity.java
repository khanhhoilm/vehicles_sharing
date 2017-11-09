package vehiclessharing.vehiclessharing.controller.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.adapter.HistoryFragmentPagerAdapter;

public class HistoryActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    public static Activity activity;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

/*
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.history));
*/
        addControls();
        addEvents();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }

    private void addEvents() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Back button", "Back button click");
                onBackPressed();
            }
        });
    }

    private void addControls() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_General);
        toolbar.setTitle(getString(R.string.edit_profile));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
            // if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }
            //    }

        }


        /*TabHost tabHost= (TabHost) findViewById(R.id.tabHost);//
        tabHost.setup();//lệnh này đẻ tạo 1 tabHost la nơi chứa đựng các tab
        TabHost.TabSpec tab1=tabHost.newTabSpec("tab_graber");//tạo 1 tab mới
        tab1.setIndicator("History go with needer");//chỉ hiển thị chuỗi hoặc hình nếu vừa hình vừa chuối thì phải làm 1 hình có sẵn chuỗi trong đó
        //tab1.setIndicator("",getResources().getDrawable(R.drawable.ic_accessibility_orange_a700_24dp));//hiển thị hình ảnh(hoặc hình có chứa hình và chữ)
        tab1.setContent(R.id.tabGraber);//nội dung bên trong tab này ở đây id.tab1 là linear đã include man hinh1
        tabHost.addTab(tab1);//thêm tab mới vào tabhost

        TabHost.TabSpec tab2=tabHost.newTabSpec("tab_needer");
        tab2.setIndicator("History go with graber");
        //tab2.setIndicator("",getResources().getDrawable(R.drawable.ic_motorcycle_green_900_24dp));
        tab2.setContent(R.id.tabNeeder);
        tabHost.addTab(tab2);*/
        activity=HistoryActivity.this;
        viewPager= (ViewPager) findViewById(R.id.viewpager);
        tabLayout= (TabLayout) findViewById(R.id.sliding_tabs);

        viewPager.setAdapter(new HistoryFragmentPagerAdapter(getSupportFragmentManager(),this));
        tabLayout.setupWithViewPager(viewPager);
    }
}
