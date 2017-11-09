package vehiclessharing.vehiclessharing.controller.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.Calendar;

import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.database.DatabaseHelper;
import vehiclessharing.vehiclessharing.model.UserInfo;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private ImageView avatarUser;//Instance reference imageview inside layout
    private EditText txtFullName;//Instance reference edittext inside layout
    private EditText txtPhoneNumber;//Instance reference edittext inside layout
    private RadioButton rdMale;//Instance reference radio button inside layout
    private RadioButton rdFemale;//Instance reference radio button inside layout
    public static TextView txtBirthday;//Instance reference textview inside layout
    private EditText edAddress;//Instance reference edittext inside layout
    private static Button btnSave;//Instance reference button save profile inside layout
    private static ProgressBar progressBar;//Instance reference progress load image inside layout
    private Toolbar toolbar;

    private static int REQUEST_IMAGE_SDCARD = 100;//Value request activity pick image in SDcard
    private static int REQUEST_IAMGE_CAMERA = 200;//Value request activity take image using CAMERA
    private static int MY_CAMERA_REQUEST_CODE = 69;//Value request permission Camera
    private static Bitmap bmImageUser;//Instance to save image picked by user in SDcard / from camera

    private static boolean isFullNameChanged = false;//Controll fullname's user is changed or not
    private static boolean isPhoneNumberChanged = false;//Controll phonenumber's user is changed or not
    private static boolean isSexChanged = false;//Controll fullname's user is changed or not
    public static boolean isBirthdayChanged = false;//Controll fullname's user is changed or not
    public static boolean isAddressChanged = false;//Controll fullname's user is changed or not

    private java.util.Calendar calendar = java.util.Calendar.getInstance();
    private java.text.SimpleDateFormat sdf2 = new java.text.SimpleDateFormat("dd/MM/yyyy");

    //private static BirthDay birthDayTemp;//Instance reference Date picker

    //  private Realm realm;//Instance to work with Realm
    private DatabaseHelper mDatabase;//Instance reference Realtime Database Firebase
    private static Drawable mDrawable;//Icon edittext when text invalid
    private UserInfo userInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//DO NOT ROTATE the screen even if the user is shaking his phone like mad

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        addControlls();
        setContentUI();
        addEvents();
    }

    /**
     * Set content to all fields
     */
    private void setContentUI() {
        // String fullName="";

        txtFullName.setText(userInfo.getName());
        txtPhoneNumber.setText(userInfo.getPhone());
        //txtBirthday.setText("");
        if (userInfo.getBirthday() != null && !userInfo.equals("")) {
            txtBirthday.setText(sdf2.format(calendar));
        }
        edAddress.setText("");
        if(userInfo.getGender()==0){
            rdMale.setChecked(true);
        }else
        {
            rdFemale.setChecked(true);
        }
        String url = "";
        Log.d("image_path", String.valueOf(url));
        if (url.equals("null") || url.isEmpty()) {
            avatarUser.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.temp));
            progressBar.setVisibility(View.GONE);
        } else {
           /* if(!isOnline())
                ImageClass.loadImageOffline(url,EditProfileActivity.this,avatarUser,progressBar);
            else ImageClass.loadImageOnline(url,EditProfileActivity.this,avatarUser,progressBar);
       */
        }

      /*  if(MainActivity.currentUser.getUser().getSex().equals("Male"))
            rdMale.setChecked(true);
        else rdFemale.setChecked(true);*/
    }

    /**
     * Handle event the fields layout
     */
    private void addEvents() {
        rdFemale.setOnClickListener(this);
        rdMale.setOnClickListener(this);
        avatarUser.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        txtFullName.addTextChangedListener(this);
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
    }

    private void addControlls() {
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
        }

        avatarUser = (ImageView) findViewById(R.id.img_user);
        progressBar = (ProgressBar) findViewById(R.id.loading_progress_img);
        txtFullName = (EditText) findViewById(R.id.ed_full_name);
        txtPhoneNumber = (EditText) findViewById(R.id.ed_phone_number);
        edAddress = (EditText) findViewById(R.id.ed_address);
        txtBirthday = (TextView) findViewById(R.id.txt_birthday);
        rdMale = (RadioButton) findViewById(R.id.rd_male);
        rdFemale = (RadioButton) findViewById(R.id.rd_female);
        btnSave = (Button) findViewById(R.id.btn_save);

        //    mDatabase = FirebaseDatabase.getInstance().getReference();

        mDrawable = getResources().getDrawable(R.drawable.ic_warning_red_600_24dp);
        mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());

        mDatabase = new DatabaseHelper(this);

        loadInfoUser();
    }

    private void loadInfoUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("vsharing_is_login", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", 0);
        userInfo = mDatabase.getUser(String.valueOf(userId));
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

            }
        };

     /*   TimePickerDialog.OnTimeSetListener callBack = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                //Hiển thị sự thay đổi theo người dùng
                calendar.set(Calendar.DATE, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.YEAR);
                txtTimeStart.setText(sdf2.format(calendar.getTime()));
            }
        };

     */
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

    private void showButtonSave() {
        btnSave.setAlpha(1);
    }

    private void hideButtonSave() {
        // float alpha= (float) 0.3;
        btnSave.setAlpha(0.3f);
    }

    /**
     * Take picture using Camera
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void callIntentTakePicture() {
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    MY_CAMERA_REQUEST_CODE);
            return;
        }
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_IAMGE_CAMERA);
    }


    /**
     * Visible button when user changed profile
     */
    public static void hideOrShowButton() {
        if (isFullNameChanged || isPhoneNumberChanged ||
                isSexChanged || isBirthdayChanged || isAddressChanged)
            btnSave.setVisibility(View.VISIBLE);
        else btnSave.setVisibility(View.INVISIBLE);
    }


    /**
     * Update data profile
     */
    /**
     * Update image to Storage Firebase
     *
     * @param file a local file
     */
    private void updateImage(Uri file) {
        if (file != null) {

            progressBar.setVisibility(View.VISIBLE);
            // Create a storage reference from our app
            //     StorageReference storageRef = storage.getReference();

            // Create a child reference
            // imagesRef now points to "images"
            //   StorageReference riversRef = storageRef.child("avatar/"+ MainActivity.mUser.getUid());
          /*  UploadTask uploadTask = riversRef.putFile(file);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(EditProfileActivity.this,"Uploads image unsuccessful!",Toast.LENGTH_SHORT).show();
                    Log.e(Utils.TAG_ERROR_UPLOAD_IMAGE_FIREBASE,String.valueOf(exception.getMessage()));
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

//                    ImageClass.getUrlThumbnailImage(EditProfileActivity.this, avatarUser, new ImageCallback() {
//                        @Override
//                        public void onSuccess(String url) {
//                            Log.d("getDownloadUrl_successS",url);
//                        }
//
//                        @Override
//                        public void onError(Exception e) {
//                            Log.d("getDownloadUrl_successE",String.valueOf(e.getMessage()));
//                        }
//                    });

                    mDatabase.child("users").child(MainActivity.currentUser.getUserId()).child("image").setValue(downloadUrl.toString());

                    //Update url avatar's user Firebase
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(downloadUrl)
                            .build();
                    MainActivity.mUser.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(Utils.TAG_UPLOAD_IMAGE, "User profile updated.");
                                    }
                                    else Log.d(Utils.TAG_UPLOAD_IMAGE, "User profile update failed.");
                                }
                            });

                    //Update url avatar's user data on device
                    realm.beginTransaction();
                    MainActivity.currentUser.getUser().setImage(String.valueOf(downloadUrl));
                    realm.commitTransaction();
*/
//                    Picasso.with(EditProfileActivity.this)
//                            .load(String.valueOf(downloadUrl))
//                            .into(avatarUser, new Callback() {
//                        @Override
//                        public void onSuccess() {
//                            progressBar.setVisibility(View.GONE);
//                        }
//
//                        @Override
//                        public void onError() {
//                            progressBar.setVisibility(View.GONE);
//                            Toast.makeText(EditProfileActivity.this,"Error load image",Toast.LENGTH_SHORT).show();
//                        }
//                    });
            //   ImageClass.loadImageOnline(String.valueOf(downloadUrl),EditProfileActivity.this,avatarUser,progressBar);

          /*      }
            });
        }*/
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
        /*if (resultCode == EditProfileActivity.RESULT_OK) {
            Uri targetUri = data.getData();
            try {
                updateImage(targetUri);
//                bmImageUser = ImageClass.decodeUri(EditProfileActivity.this,targetUri,100);
//                bmImageUser = ImageClass.rotateImage(bmImageUser);//Rotating avatar's user before upload Storage Firebase
            } catch (Exception e) {
                //          Log.e(Utils.TAG_ERROR_SELECT_IMAGE,String.valueOf(e.getMessage()));
            }
//            updateImage(getByteFromBitmap());
        }*/
        Place place = PlaceAutocomplete.getPlace(this, data);
        if (requestCode == 1) {
            //    if (resultCode == 1) {
            if (edAddress != null && place != null) {
                edAddress.setText(place.getAddress());
            }

        }
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_IAMGE_CAMERA);
            }
        }
    }
*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rd_male: {
        /*        if(MainActivity.currentUser.getUser().getSex().equals("Female"))
                    isSexChanged = true;
                else
                    isSexChanged = false;
                rdMale.setChecked(true);
                rdFemale.setChecked(false);
                hideOrShowButton();
        */
                break;
            }
            case R.id.rd_female: {
          /*      if(MainActivity.currentUser.getUser().getSex().equals("Male"))
                    isSexChanged = true;
                else
                    isSexChanged = false;
                rdMale.setChecked(false);
                rdFemale.setChecked(true);
                hideOrShowButton();
                break;
          */
            }
            case R.id.btn_save: {
                if (isOnline())
            /*        new AlertDialog.Builder(EditProfileActivity.this)
                            .setTitle("Confirm edit profile")
                            .setMessage("Are you sure you want to save profile?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    uploadProfileData();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                else Toast.makeText(EditProfileActivity.this,"Internet is disable, can't update!",Toast.LENGTH_SHORT).show();
            */ break;
            }
            case R.id.img_user: {
                selectImage();
                break;
            }
            case R.id.txt_birthday: {
                showDatePicker();
                break;
            }
            case R.id.ed_address: {
                try {
                    //   txtCurLocation.setText("");
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(this);
                    startActivityForResult(intent, 1);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
                break;
            }
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        /*if(edFullName.hasFocus()){//text changed for edittext fullname
            String fullname = edFullName.getText().toString();
            if(fullname.isEmpty())
                edFullName.setError("Fullname is required", mDrawable);
            else {
                if(!fullname.equals(MainActivity.currentUser.getUser().getFullName()))
                    isFullNameChanged = true;
                else isFullNameChanged = false;
            }
        }
        if(edPhoneNumber.hasFocus()){
            String phonenumber = edPhoneNumber.getText().toString();
            if(phonenumber.isEmpty()) edPhoneNumber.setError("Phone number is required", mDrawable);
            else {
                Validation validation = Validation.checkValidPhone(phonenumber);
                if(!validation.getIsValid())
                    edPhoneNumber.setError(validation.getMessageValid(),mDrawable);
                else {
                    if(phonenumber.equals(MainActivity.currentUser.getUser().getPhoneNumber()))
                        isPhoneNumberChanged = false;
                    else isPhoneNumberChanged = true;
                }
            }
        }*/
        hideOrShowButton();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
