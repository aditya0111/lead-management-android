package com.community.jboss.leadmanagement.utils;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.community.jboss.leadmanagement.R;
import com.community.jboss.leadmanagement.SignUpActivity;
import com.community.jboss.leadmanagement.main.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity {

    Button sign_in,sign_up;
    EditText edt1,edt2;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        sign_in=findViewById(R.id.login_button);
        edt1=findViewById(R.id.username);
        edt2=findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
        sign_up=findViewById(R.id.button6);
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email1=edt1.getText().toString();
                String password1=edt2.getText().toString();
                if (email1.isEmpty()||password1.isEmpty())
                {
                    Toast.makeText(SignIn.this,"Please Enter a valid..",Toast.LENGTH_LONG).show();
                }
                else{
                    mAuth.signInWithEmailAndPassword(email1, password1)
                            .addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        //Log.d(TAG, "signInWithEmail:success");
                                        Toast.makeText(SignIn.this,"SIgn in successfull",Toast.LENGTH_LONG).show();
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUI(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        //Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(SignIn.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        updateUI(null);
                                    }

                                    // ...
                                }
                            });
                }
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn.this, SignUpActivity.class));
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if(user!= null) {
            Intent intent = new Intent(SignIn.this, MainActivity.class);
            //intent.putExtra("user",String.valueOf(user.getPhotoUrl()));
            intent.putExtra("email",edt1.getText().toString());
            startActivity(intent);
        }
    }
}
