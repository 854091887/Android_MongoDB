package com.sourcey.materiallogindemo;

import com.sourcey.materiallogindemo.MongoDBUtil;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.mongodb.MongoClient;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.*;
import static okhttp3.internal.Util.equal;
import org.bson.*;
import com.alibaba.fastjson.JSONObject;

import java.sql.Connection;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login) Button _loginButton;
    @BindView(R.id.link_signup) TextView _signupLink;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                //finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }


        //_loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        //progressDialog.setIndeterminate(true);
        //progressDialog.setMessage("Authenticating...");
        //progressDialog.show();
        new Thread(runnable).start();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 3000);
    }



    Runnable runnable=new Runnable() {

        @Override
        public void run() {
            String email = _emailText.getText().toString();
            String password = _passwordText.getText().toString();
            // TODO Auto-generated method stub
            MongoDBUtil.MongoDBConnect();
            MongoDBUtil.DatabaseSelect("android_permission");
            MongoDBUtil.CollectionSelect("android");
            MongoDBUtil.Query("email",MongoDBUtil.DocGen("$eq",email));
            long count = MongoDBUtil.count(MongoDBUtil.CollectionSelect("android"),MongoDBUtil.Query("email",MongoDBUtil.DocGen("$eq",email)));
            userexitance();
            MongoDBUtil.CollectionSelect("android").find(eq("email", email) ).forEach(PasswordValidateBlock);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically


                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        //Data Transfer
        Intent intent_trans = new Intent(LoginActivity.this, MainActivity.class);
        intent_trans.putExtra("email",  _emailText.getText().toString());
        startActivity(intent_trans);
        MainActivity.counter++;
        finish();
    }

    public void onLoginFailed() {
        //Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("plz enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }
        return valid;
    }

    public void userexitance(){
        if(MongoDBUtil.exits()){

        }
        else{
            onLoginFailed();
            background_toast("User Not Found!\nWanna Register?");
        }
    }

    public void background_toast(String msg){
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getBaseContext(),msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Block
    Block<Document> PasswordValidateBlock = new Block<Document>() {
        @Override
        public void apply(final Document document) {
            String Json2String = document.toJson();
            String password = _passwordText.getText().toString();
            //Get The Passwd
            JSONObject jsonObject = JSONObject.parseObject(Json2String);
            String passwd = jsonObject.getString("passwd");
            String user = jsonObject.getString("name");
            //Validate the Passwd
            if( equal(passwd,password)){
                background_toast("Welcome! "+user);
                onLoginSuccess();
            }
            else{
                background_toast("Wrong Password");
                onLoginFailed();
            }
        }
    };
}
