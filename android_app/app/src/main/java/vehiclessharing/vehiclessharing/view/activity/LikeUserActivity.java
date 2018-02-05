package vehiclessharing.vehiclessharing.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.api.FavoriteUserAPI;

public class LikeUserActivity extends AppCompatActivity implements View.OnClickListener, FavoriteUserAPI.FavoriteCallback {

    private TextView txtLike, txtIntermediate;
    public static String PARTNER_ID = "partner_id";
    private int partnerId;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_user);
        partnerId = getIntent().getExtras().getInt(PARTNER_ID, 0);
        addControls();
        addEvents();
    }

    private void addEvents() {
        txtLike.setOnClickListener(this);
        txtIntermediate.setOnClickListener(this);
    }

    private void addControls() {
        txtLike = findViewById(R.id.txtLike);
        txtIntermediate = findViewById(R.id.txtIntermediate);
        progressBar=findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.txtLike) {
            progressBar.setVisibility(View.VISIBLE);
            addToFavorite();
        } else if (view.getId() == R.id.txtIntermediate) {
            backToMainActivity();
        }
    }

    private void backToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void addToFavorite() {
        if (partnerId != 0) {
            FavoriteUserAPI favoriteUserAPI = new FavoriteUserAPI(this);
            favoriteUserAPI.like(MainActivity.sessionId, partnerId);
        }
    }

    @Override
    public void favoriteSuccess() {
        progressBar.setVisibility(View.GONE);
        backToMainActivity();
    }

    @Override
    public void favoriteFailure() {
        Toast.makeText(this, "failure", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
    }
}
