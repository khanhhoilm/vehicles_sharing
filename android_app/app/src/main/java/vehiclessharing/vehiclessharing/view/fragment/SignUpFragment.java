package vehiclessharing.vehiclessharing.view.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.api.RestManager;
import vehiclessharing.vehiclessharing.api.SignUpAPI;
import vehiclessharing.vehiclessharing.constant.Utils;
import vehiclessharing.vehiclessharing.database.DatabaseHelper;
import vehiclessharing.vehiclessharing.model.Validation;
import vehiclessharing.vehiclessharing.utils.HashAlgorithm;
import vehiclessharing.vehiclessharing.viewscustom.CustomToast;


public class SignUpFragment extends Fragment implements View.OnClickListener, SignUpAPI.SignUpInterface {


    private ProgressDialog mProgress;
    private Drawable mDrawable;

    private View view;
    private EditText txtFullName;
    private EditText txtPhone;
    private EditText txtPassword;
    private EditText txtConfirmPassword;
    private RadioButton rdMale, rdFemale;
    private TextView login;
    private Button btnSignup;
    private RestManager mManager;
    private Activity mActivity;

    DatabaseHelper db;

    private Validation validation = null;

    public SignUpFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signup, container, false);
        addControls();
        addEvents();
        return view;
    }

    private void addControls() {

        mActivity = getActivity();
        //[Start] Setup for progress
        mProgress = new ProgressDialog(getActivity());
        mProgress.setTitle(Utils.SignUp);
        mProgress.setMessage(Utils.PleaseWait);
        mProgress.setCancelable(false);
        mProgress.setCanceledOnTouchOutside(false);
        //[End] Setup for progress

        txtFullName = (EditText) view.findViewById(R.id.txtFullName);
        rdMale = (RadioButton) view.findViewById(R.id.rdMale);
        rdFemale = (RadioButton) view.findViewById(R.id.rdFemale);
        txtPhone = (EditText) view.findViewById(R.id.txtPhone);
        txtPassword = (EditText) view.findViewById(R.id.txtPassword);
        txtConfirmPassword = (EditText) view.findViewById(R.id.txtConfirmPassword);
        btnSignup = (Button) view.findViewById(R.id.btnSignup);
        login = (TextView) view.findViewById(R.id.already_user);
        mDrawable = getResources().getDrawable(R.drawable.ic_warning_red_600_24dp);
        mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
    }

    private void addEvents() {
        btnSignup.setOnClickListener(this);
        login.setOnClickListener(this);
        txtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (Validation.isEmpty(txtFullName.getText().toString()))
                    txtFullName.setError("Fullname is required", mDrawable);
                if (Validation.isEmpty(txtPhone.getText().toString())) {
                    txtPhone.setError("Phone is required", mDrawable);
                } else {
                    validation = Validation.checkValidPhone(txtPhone.getText().toString());
                    if (!validation.getIsValid()) {
                        txtPhone.setError(validation.getMessageValid(), mDrawable);
                    }
                }
            }
        });

        txtPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (Validation.isEmpty(txtFullName.getText().toString()))
                    txtFullName.setError("Fullname is required", mDrawable);
            }
        });

        txtConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (Validation.isEmpty(txtFullName.getText().toString()))
                    txtFullName.setError("Fullname is required", mDrawable);
                if (Validation.isEmpty(txtPhone.getText().toString())) {
                    txtPhone.setError("Phone is required", mDrawable);
                } else {
                    validation = Validation.checkValidPhone(txtPhone.getText().toString());
                    if (!validation.getIsValid()) {
                        txtPhone.setError(validation.getMessageValid(), mDrawable);
                    }
                }
                if (Validation.isEmpty(txtPassword.getText().toString())) {
                    txtPassword.setError("Password is required", mDrawable);
                } else {
                    validation = Validation.checkValidPassword(txtPassword.getText().toString());
                    if (!validation.getIsValid()) {
                        txtPassword.setError(validation.getMessageValid(), mDrawable);
                    }
                }
            }
        });

        rdFemale.setOnClickListener(this);
        rdMale.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignup:
                checkValidation();
                break;
            case R.id.already_user:
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                        .replace(R.id.frameContainer, new SigninFragment()).commit();

                break;
            case R.id.rdFemale:
                rdMale.setChecked(false);
                rdFemale.setChecked(true);
                break;

            case R.id.rdMale:
                rdMale.setChecked(true);
                rdFemale.setChecked(false);
                break;
        }

    }

    private void checkValidation() {

        String fullName = txtFullName.getText().toString();
        String mobilePhone = txtPhone.getText().toString();
        String password = txtPassword.getText().toString();
        String confirmPassword = txtConfirmPassword.getText().toString();
        Validation validation;

        if (Validation.isEmpty(fullName)
                || (!rdMale.isChecked() && !rdFemale.isChecked())
                || Validation.isEmpty(mobilePhone)
                || Validation.isEmpty(password)
                || Validation.isEmpty(confirmPassword))

            new CustomToast().showToast(getActivity(), view,
                    "All fields are required.");
        else {
            validation = Validation.checkValidPhone(mobilePhone);
            if (!validation.getIsValid())
                new CustomToast().showToast(getActivity(), view,
                        validation.getMessageValid());
            else {
                validation = Validation.checkValidPassword(password);
                if (!validation.getIsValid())
                    new CustomToast().showToast(getActivity(), view,
                            validation.getMessageValid());
                else {
                    validation = Validation.checkValidConfirmPassword(password, confirmPassword);
                    if (!validation.getIsValid())
                        new CustomToast().showToast(getActivity(), view,
                                validation.getMessageValid());
                    else {
                        int gender = 0;
                        if (rdMale.isChecked()) {
                            gender = 0;
                        }
                        if (rdFemale.isChecked()) {
                            gender = 1;
                        }

                        mProgress.show();
                        password = HashAlgorithm.md5(password);

                        SignUpAPI.getInstance(this).signUp(mobilePhone, fullName, password, gender);
                    }
                }
            }
        }

    }

    @Override
    public void signUpSuccess() {
        mProgress.dismiss();
        Toast.makeText(getActivity(), "Đăng ký thành công", Toast.LENGTH_SHORT).show();
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                .replace(R.id.frameContainer, new SigninFragment()).commit();
    }

    @Override
    public void signUpUnsuccess() {
        new CustomToast().showToast(getActivity(), view,
                getResources().getString(R.string.user_exists));
        mProgress.dismiss();
    }

    @Override
    public void signUpFailure() {
        mProgress.dismiss();
        Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();

    }
}

