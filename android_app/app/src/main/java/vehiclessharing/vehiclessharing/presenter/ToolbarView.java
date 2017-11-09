/*
package vehiclessharing.vehiclessharing.presenter;

import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.zip.Inflater;

import vehiclessharing.vehiclessharing.R;

*/
/**
 * Created by Hihihehe on 10/28/2017.
 *//*


public class ToolbarView extends AppCompatActivity {
    private Toolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.layout_toolbar);
        setupToolbar();
    }

    public void setupToolbar() {
        setContentView(R.layout.layout_toolbar);
        Inflater inflater=getLayoutInflater().inflate(R.layout.layout_toolbar)
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
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("Back button", "Back button click");
                    onBackPressed();
                }
            });
        }
    }

}
*/
