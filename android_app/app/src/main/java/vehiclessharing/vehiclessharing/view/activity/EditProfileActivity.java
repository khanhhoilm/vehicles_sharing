package vehiclessharing.vehiclessharing.view.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.io.File;
import java.util.Calendar;

import co.vehiclessharing.R;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import vehiclessharing.vehiclessharing.api.UserInfoAPI;
import vehiclessharing.vehiclessharing.api.UpdateImageAPI;
import vehiclessharing.vehiclessharing.asynctask.GetAvatar;
import vehiclessharing.vehiclessharing.authentication.SessionManager;
import vehiclessharing.vehiclessharing.constant.Utils;
import vehiclessharing.vehiclessharing.database.DatabaseHelper;
import vehiclessharing.vehiclessharing.model.InfomationUser;
import vehiclessharing.vehiclessharing.model.User;
import vehiclessharing.vehiclessharing.permission.CheckCamera;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener,
        TextWatcher, UpdateImageAPI.UpdateImageCallback, UserInfoAPI.GetInfoUserCallback,GetAvatar.GetBitMapAvatarInterface {

    private ImageView avatarUser;
    private EditText txtFullName;
    private EditText txtPhoneNumber;
    private RadioButton rdMale;
    private RadioButton rdFemale;
    private RadioGroup groupGender;
    private EditText edEmail;

    public TextView txtBirthday;
    private EditText edAddress;
    private Button btnSave;
    private Toolbar toolbar;

    private static int REQUEST_IMAGE_SDCARD = 100;//Value request activity pick image in SDcard
    private static int REQUEST_IMAGE_CAMERA = 200;//Value request activity take image using CAMERA
    private static int MY_CAMERA_REQUEST_CODE = 69;//Value request permission Camera
    public static int REQUEST_CHOOSE_ADDRESS = 1;


    private java.util.Calendar calendar = java.util.Calendar.getInstance();
    private java.text.SimpleDateFormat sdf2 = new java.text.SimpleDateFormat("dd-MM-yyyy");

    private DatabaseHelper mDatabase;//Instance reference Realtime Database Firebase
    private static Drawable mDrawable;//Icon edittext when text invalid
    private User userInfo;

    private String apiToken = "";

    private UpdateImageAPI updateImageAPI;
    private String avatarLink = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//DO NOT ROTATE the screen even if the user is shaking his phone like mad

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        addControlls();
        setContentUI();
        addEvents();
    }

    /**
     * Set content to all fields
     */
    private void setContentUI() {
        txtFullName.setText(userInfo.getName());
        txtPhoneNumber.setText(userInfo.getPhone());
        if (userInfo.getBirthday() != null && !userInfo.equals("")) {
            txtBirthday.setText(userInfo.getBirthday());
        }
        if (userInfo.getAddress() != null && !userInfo.equals("")) {
            edAddress.setText(userInfo.getAddress());
        }

        if (userInfo.getGender() != null && userInfo.getGender() == 0) {
            rdMale.setChecked(true);
        } else {
            rdFemale.setChecked(true);
        }
    }

    private void addEvents() {
        rdFemale.setOnClickListener(this);
        rdMale.setOnClickListener(this);
        avatarUser.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        txtFullName.addTextChangedListener(this);
        edEmail.setOnClickListener(this);
        txtPhoneNumber.addTextChangedListener(this);
        rdMale.setOnClickListener(this);
        rdFemale.setOnClickListener(this);
        txtBirthday.setOnClickListener(this);
        edAddress.setOnClickListener(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Back button", "Back button click");
                onBackPressed();
            }
        });
        updateImageAPI = new UpdateImageAPI(this);
    }

    private void addControlls() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_General);
        toolbar.setTitle(getString(R.string.edit_profile));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        }

        avatarUser = (ImageView) findViewById(R.id.img_user);
        txtFullName = (EditText) findViewById(R.id.ed_full_name);
        txtPhoneNumber = (EditText) findViewById(R.id.ed_phone_number);
        edEmail = findViewById(R.id.edEmail);
        edAddress = (EditText) findViewById(R.id.ed_address);
        txtBirthday = (TextView) findViewById(R.id.txt_birthday);

        groupGender = (RadioGroup) findViewById(R.id.rdgGender);
        rdMale = (RadioButton) findViewById(R.id.rd_male);
        rdFemale = (RadioButton) findViewById(R.id.rd_female);
        btnSave = (Button) findViewById(R.id.btn_save);

        mDrawable = getResources().getDrawable(R.drawable.ic_warning_red_600_24dp);
        mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());

        mDatabase = new DatabaseHelper(this);
        loadInfoUser();
    }

    private void loadInfoUser() {
        SharedPreferences sharedPreferences = getSharedPreferences(SessionManager.PREF_NAME_LOGIN, MODE_PRIVATE);
        int userId = sharedPreferences.getInt(SessionManager.USER_ID, 0);
        apiToken = sharedPreferences.getString(SessionManager.KEY_SESSION, "");
        userInfo = mDatabase.getUser(userId);
        UserInfoAPI userInfoAPI=new UserInfoAPI(this);
       userInfoAPI.getMyInfo(apiToken);
    }

    private void showDatePicker() {
        DatePickerDialog.OnDateSetListener callBack = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //Hiển thị sự thay đổi theo người dùng
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.YEAR, year);
                txtBirthday.setText(sdf2.format(calendar.getTime()));
                displayButtonUpdate(true);

            }
        };


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, callBack, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        //nếu đối số cuối =true thì định dạng 24h, =false định dạng 12h
        datePickerDialog.show();
    }


    /**
     * Pick image in SDcard
     */
    private void callIntentPickImg() {
        Intent intent = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent, "Select File"),
                REQUEST_IMAGE_SDCARD);
    }

    /**
     * Take picture using Camera
     */
   // @RequiresApi(api = Build.VERSION_CODES.M)
    private void callIntentTakePicture() {
        if (CheckCamera.checkCamera(this, this)) {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAMERA);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == CheckCamera.REQUEST_CAMERA && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAMERA);
            // permission was g
        }

    }

    /**
     * Visible button when user changed profile
     */
    public void displayButtonUpdate(boolean isShow) {
        if (isShow) {
            btnSave.setAlpha(1);
            btnSave.setEnabled(true);
        } else {
            btnSave.setAlpha(0.2f);
            btnSave.setEnabled(false);
        }
    }


    private void uploadFile(Uri fileUri) {
        avatarUser.setImageURI(fileUri);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) avatarUser.getDrawable();
        userInfo.setPicture(bitmapDrawable.getBitmap());
        Log.d("Update Image", "path Image: " + fileUri.toString());

        File file = new File(getRealPathFromUri(this, fileUri));
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("sampleFile", file.getName(), reqFile);
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload_test");

        updateImageAPI.getURLImage(body);

    }

    public String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Internet is avaibalility
     *
     * @return true if can access internet
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Select image from Library or take picture using Camera
     */
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    callIntentTakePicture();
                } else if (items[item].equals("Choose from Library")) {
                    callIntentPickImg();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHOOSE_ADDRESS) {
            getNewAddress(data);
        } else {
            if (resultCode == EditProfileActivity.RESULT_OK) {
                Uri targetUri = data.getData();
                try {
                    uploadFile(targetUri);
              } catch (Exception e) {
                    Log.e(Utils.TAG_ERROR_SELECT_IMAGE, String.valueOf(e.getMessage()));
                }
           }
        }
    }

    private void getNewAddress(Intent data) {
        Place place = PlaceAutocomplete.getPlace(this, data);

        if (edAddress != null && place != null) {
            edAddress.setText(place.getAddress());
            userInfo.setAddress((String) place.getAddress());
            displayButtonUpdate(true);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rd_male: {
                displayButtonUpdate(true);
                break;
            }
            case R.id.rd_female: {
                displayButtonUpdate(true);
                break;
            }
            case R.id.btn_save: {
                updateProfileUser();
                break;
            }
            case R.id.img_user: {
                selectImage();
                break;
            }
            case R.id.edEmail:
            {
                displayButtonUpdate(true);
                break;
            }
            case R.id.txt_birthday: {
                showDatePicker();
                break;
            }
            case R.id.ed_address: {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(this);
                    startActivityForResult(intent, REQUEST_CHOOSE_ADDRESS);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
                break;
            }
        }
    }

    private void updateProfileUser() {

        int radId = groupGender.getCheckedRadioButtonId();
        if (radId == R.id.rd_male) {
            userInfo.setGender(0);
        } else {
            userInfo.setGender(1);
        }
        userInfo.setName(txtFullName.getText().toString());

        userInfo.setBirthday(txtBirthday.getText().toString());
        String email = "";
        email = edEmail.getText().toString();

        updateImageAPI.updateInfoUser(apiToken, userInfo.getName(), email,avatarLink, userInfo.getGender(), userInfo.getAddress(), userInfo.getBirthday());
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        displayButtonUpdate(true);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void getURLImageSuccess(String url) {
        avatarLink = url;
        displayButtonUpdate(true);
    }

    @Override
    public void getURLImageFailure() {

    }

    @Override
    public void updateImageSuccess() {
        Toast.makeText(this, getResources().getString(R.string.update_avatar_success), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateImageFailure() {
        Toast.makeText(this, getResources().getString(R.string.update_avatar_fail), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateInfoUserSuccess() {
        Toast.makeText(EditProfileActivity.this, getResources().getString(R.string.update_info_success), Toast.LENGTH_SHORT).show();
        mDatabase.updateInfoUser(userInfo);
        finish();
    }

    @Override
    public void updateInfoUserFailure() {
        Toast.makeText(EditProfileActivity.this, getResources().getString(R.string.update_info_fail), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getInfoUserSuccess(InfomationUser userInfo) {
        if (userInfo.getUserInfo().getAvatarLink() != null && !userInfo.getUserInfo().getAvatarLink().equals("")) {
            Glide.with(this).load(userInfo.getUserInfo().getAvatarLink()).placeholder(getResources().getDrawable(R.drawable.temp))
                    .centerCrop().into(avatarUser);
            new GetAvatar(this,this).execute(userInfo.getUserInfo().getAvatarLink());
        }

        if (userInfo.getUserInfo().getEmail() != null && !userInfo.getUserInfo().getEmail().equals("")) {
            edEmail.setText(userInfo.getUserInfo().getEmail());
        }
        if (userInfo.getUserInfo().getGender()==0){
            rdMale.setChecked(true);
        }else {
            rdFemale.setChecked(true);
        }
    }

    @Override
    public void getUserInfoFailure(String message) {
    }

    @Override
    public void getBitMapSuccess(Bitmap bitmap) {
        if(bitmap!=null) {
            avatarUser.setImageBitmap(bitmap);
        }
    }
}
