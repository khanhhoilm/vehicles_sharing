package vehiclessharing.vehiclessharing.controller.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import co.vehiclessharing.R;

public class RatingActivity extends AppCompatActivity implements View.OnClickListener,View.OnFocusChangeListener,RatingBar.OnRatingBarChangeListener {
    private TextView txtName, txtSource, txtDes, txtTime, txtType, txtComment;
    private ImageView imgAvatar;
    private RatingBar ratingBar;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addControls();
        addEvents();
    }

    private void addEvents() {
        btnSend.setOnClickListener(this);
        txtComment.setOnFocusChangeListener(this);
        ratingBar.setOnRatingBarChangeListener(this);
    }

    private void addControls() {
        txtName = (TextView) findViewById(R.id.txtFullName);
        imgAvatar = (ImageView) findViewById(R.id.imgAvatar);
        txtSource = (TextView) findViewById(R.id.txtSourceLocation);
        txtDes = (TextView) findViewById(R.id.txtDesLocation);
        txtTime = (TextView) findViewById(R.id.txtTimeStartEnd);
        txtType = (TextView) findViewById(R.id.txtVehicleType);
        ratingBar = (RatingBar) findViewById(R.id.rbRating);
        txtComment = (TextView) findViewById(R.id.txtWriteComment);
        btnSend = (Button) findViewById(R.id.btnSendRating);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnSendRating:

                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        btnSend.setAlpha(1);
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        btnSend.setAlpha(1);
    }
}
