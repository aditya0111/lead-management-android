package com.community.jboss.leadmanagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.community.jboss.leadmanagement.main.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String TAG = "Sign Up";
    private ProgressDialog progressDialog;
    String usertext,userpass,phone;
    private EditText username, password, number,otp;
    boolean mVerificationInProgress=false;
    String mVerificationId;
    public static String password_text;
    FirebaseUser user;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    Button btn,submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        username = findViewById(R.id.user_text);
        password = findViewById(R.id.password_text);
        number = findViewById(R.id.phone_number);
        btn = findViewById(R.id.sign_up_btn);
        submit = findViewById(R.id.submit_btn);
        otp = findViewById(R.id.enter_otp);
        username.setVisibility(View.VISIBLE);
        password.setVisibility(View.VISIBLE);
        number.setVisibility(View.VISIBLE);
        btn.setVisibility(View.VISIBLE);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Registering You!!");
                progressDialog.show();
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                intent.putExtra("email",user.getEmail());
                startActivity(intent);
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp.getText().toString());
                mAuth.getCurrentUser().linkWithCredential(credential)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "linkWithCredential:success");
                                    FirebaseUser user = task.getResult().getUser();
                                    progressDialog.hide();
                                    updateUI(user);
                                } else {
                                    Log.w(TAG, "linkWithCredential:failure", task.getException());
                                    Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    progressDialog.hide();
                                    updateUI(null);
                                }

                            }
                        });
            }
        });
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(SignUpActivity.this, "Number Verified Successfully", Toast.LENGTH_SHORT).show();
                updateUI(user);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(SignUpActivity.this, "Verification Failed. Please Try Again", Toast.LENGTH_SHORT).show();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(SignUpActivity.this, "Invalid Mobile Number", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(SignUpActivity.this, "Execution Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                Toast.makeText(SignUpActivity.this, "OTP has been Sent", Toast.LENGTH_SHORT).show();
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                username.setVisibility(View.GONE);
                password.setVisibility(View.GONE);
                number.setVisibility(View.GONE);
                btn.setVisibility(View.GONE);
                otp.setVisibility(View.VISIBLE);
                submit.setVisibility(View.VISIBLE);
            }
        };
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = number.getText().toString().trim();
                usertext = username.getText().toString().trim();
                userpass = password.getText().toString().trim();
                password_text=password.getText().toString().trim();
                if (TextUtils.isEmpty(usertext)) {
                    Toast.makeText(SignUpActivity.this, "Please enter a valid email", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(userpass)) {
                    Toast.makeText(SignUpActivity.this, "Please enter a valid password", Toast.LENGTH_LONG).show();
                } else {
                    int l = usertext.length();
                    boolean flag = false;
                    for (int i = 0; i < l; i++) {
                        if (usertext.charAt(i) == '@') {
                            flag = true;
                        }
                    }
                    if (flag) {
                        progressDialog.setMessage("Registering You!!");
                        progressDialog.show();
                        mAuth.createUserWithEmailAndPassword(usertext, userpass)
                                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SignUpActivity.this, "Email Registered Successfully", Toast.LENGTH_LONG).show();
                                            user = mAuth.getCurrentUser();
                                            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                                    "+91" + phone,        // Phone number to verify
                                                    60,                 // Timeout duration
                                                    TimeUnit.SECONDS,   // Unit of timeout
                                                    SignUpActivity.this,               // Activity (for callback binding)
                                                    mCallbacks);        // OnVerificationStateChangedCallbacks
                                            progressDialog.hide();
                                        } else {
                                            Toast.makeText(SignUpActivity.this, "Failed", Toast.LENGTH_LONG).show();
                                            updateUI(null);
                                            progressDialog.hide();
                                        }
                                    }
                                });
                    }
                }
            }

        });
    }


    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            intent.putExtra("email",currentUser.getEmail());
            intent.putExtra("uid",currentUser.getUid());
            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(SignUpActivity.this,"We are sorry for your inconvenience... Please Try Again!",Toast.LENGTH_LONG).show();}
    }
}
