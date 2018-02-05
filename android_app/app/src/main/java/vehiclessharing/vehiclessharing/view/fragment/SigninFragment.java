package vehiclessharing.vehiclessharing.view.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import co.vehiclessharing.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.api.RestManager;
import vehiclessharing.vehiclessharing.api.SignInAPI;
import vehiclessharing.vehiclessharing.authentication.SessionManager;
import vehiclessharing.vehiclessharing.constant.Utils;
import vehiclessharing.vehiclessharing.view.activity.MainActivity;
import vehiclessharing.vehiclessharing.database.DatabaseHelper;
import vehiclessharing.vehiclessharing.model.SignInResult;
import vehiclessharing.vehiclessharing.model.Validation;
import vehiclessharing.vehiclessharing.utils.HashAlgorithm;
import vehiclessharing.vehiclessharing.viewscustom.CustomToast;

public class SigninFragment extends Fragment implements View.OnClickListener,SignInAPI.SignInInterfaceCallback {
    private static View view;

    public static final int RC_SIGN_IN = 0;

    private EditText txtPhone;
    private EditText txtPassword;
    private Button btnLogin;
    private TextView forgotPassword;
    private TextView signUp;
    private LinearLayout loginLayout;
    private Animation shakeAnimation;
    private FragmentManager fragmentManager;
    private RestManager mManager;
    private ProgressDialog mProgress;
    private SessionManager session;
    private int timeLoginFailed = 0;

    public SigninFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        addControls();
        addEvents();
        return view;
    }


    private void addControls() {
        fragmentManager = getActivity().getSupportFragmentManager(); // fragement manager to switch fragment another

        session = new SessionManager(getActivity());

        //[Start] Setup for progress
        mProgress = new ProgressDialog(getActivity());
        mProgress.setTitle(Utils.SignIn);
        mProgress.setMessage(Utils.PleaseWait);
        mProgress.setCancelable(false);
        mProgress.setCanceledOnTouchOutside(false);
//        //[End] Setup for progress

        txtPhone = (EditText) view.findViewById(R.id.txtPhone);
        txtPassword = (EditText) view.findViewById(R.id.txtPassword);
        btnLogin = (Button) view.findViewById(R.id.btnLogin);
        forgotPassword = (TextView) view.findViewById(R.id.forgot_password);
        signUp = (TextView) view.findViewById(R.id.createAccount);
        loginLayout = (LinearLayout) view.findViewById(R.id.login_layout);

        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.shake);
    }

    private void addEvents() {
        btnLogin.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        signUp.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                mProgress.show();
                if (checkValidation()) {
                    login();
                }
                break;

            case R.id.forgot_password:

                // Replace forgot password fragment with animation
               /* fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer,
                                new ForgotPassword_Fragment(),
                                Utils.ForgotPassword_Fragment).commit();*/
                break;
            case R.id.createAccount:

                // Replace signup frgament with animation
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, new SignUpFragment()).commit();
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private boolean checkValidation() {
        boolean checkValidate = false;

        String phoneNumber = txtPhone.getText().toString();
        String password = txtPassword.getText().toString();

        if (Validation.isEmpty(phoneNumber) || Validation.isEmpty(password)) {
            loginLayout.startAnimation(shakeAnimation);
            new CustomToast().showToast(getActivity(), view,
                    Utils.EnterBothCredentials);

        }

        else {
            Validation validatePhone = Validation.checkValidPhone(phoneNumber);
            Validation validatePassword = Validation.checkValidPassword(password);
            if (!validatePhone.getIsValid()) {
                new CustomToast().showToast(getActivity(), view,
                        validatePhone.getMessageValid());
            }
            if (!validatePassword.getIsValid()) {
                new CustomToast().showToast(getActivity(), view,
                        validatePassword.getMessageValid());
            }

            if (validatePhone.getIsValid() && validatePassword.getIsValid()) {
                checkValidate = true;

            }
        }
        return checkValidate;
    }

    private void login() {
        String password = HashAlgorithm.md5(txtPassword.getText().toString());
        Log.d("pss", "pw" + password);
        String phoneNumber = txtPhone.getText().toString();

        SignInAPI.getInstance(this).signIn(phoneNumber,password);
    }

    @Override
    public void signInSuccess(SignInResult signInResult) {
        mProgress.dismiss();
        session.createLoginSession(signInResult.getData().getUserInfo().getId(), signInResult.getData().getApiToken());
        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
        databaseHelper.insertUser(signInResult.getData().getUserInfo());

        Intent intent = new Intent(getActivity(), MainActivity.class);
        Bundle bd = new Bundle();
        intent.putExtras(bd);
        startActivity(intent);
    }

    @Override
    public void signInUnsuccess(SignInResult signInResult) {
        String warnningMessage = "";
        if (timeLoginFailed < 3) {
            warnningMessage = "Số điện thoại hoặc mật khẩu không chính xác. Vui lòng thử lại";
        } else {
            warnningMessage = "Bạn đã nhập sai thông tin đăng nhập quá 3 lần. Nếu chưa có tài khoản vui lòng vào trang đăng ký";
        }
        new CustomToast().showToast(getActivity(), view, warnningMessage);

        timeLoginFailed++;
        mProgress.dismiss();
    }

    @Override
    public void signInFailure() {
        Log.d("Signin", "onFailure");
        new CustomToast().showToast(getActivity(), view, "Số điện thoại hoặc mật khẩu không chính xác. Vui lòng thử lại");

        mProgress.dismiss();
    }
}
