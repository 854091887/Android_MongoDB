package com.sourcey.materiallogindemo;

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

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

import java.util.ArrayList;
import java.util.List;
import com.mongodb.MongoClient;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.*;
import com.alibaba.fastjson.JSONObject;

import static com.mongodb.client.model.Filters.eq;
import com.sourcey.materiallogindemo.MongoDBUtil;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @BindView(R.id.input_name) EditText _nameText;
    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup) Button _signupButton;
    @BindView(R.id.link_login) TextView _loginLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        //_signupButton.setEnabled(false);  DO NOT DISABLE IT

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        //NO ANIMATION
        //progressDialog.setIndeterminate(true);
        //progressDialog.setMessage("Creating Account...");
        //progressDialog.show();


        // TODO: Implement your own signup logic here.
        new Thread(runnable).start();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    //THREAD
    Runnable runnable=new Runnable() {

        @Override
        public void run() {
            Document document = new Document();
            String name = _nameText.getText().toString();
            String email = _emailText.getText().toString();
            String password = _passwordText.getText().toString();
            //String reEnterPassword = _reEnterPasswordText.getText().toString(); //useless in here
            // TODO Auto-generated method stub

            try {
                MongoClientURI connectionString = new MongoClientURI("mongodb://182.254.149.49:27017");
                MongoClient mongoClient = new MongoClient(connectionString);
                MongoDatabase database = mongoClient.getDatabase("android_permission");
                MongoCollection<Document> collection = database.getCollection("android");
                //background_toast("CONNECTION ESTABLISHED");
                //collection.find(exists(email)  );
                Document query = new Document("email", new Document("$eq", email));
                long count = collection.countDocuments(query);
                if(count != 0){
                    background_toast("Email Already Registered!");
                }
                else{
                    document.put("name", name);
                    document.put("email", email);
                    document.put("passwd", password);
                    document.put("group", "nonadmin");
                    document.put("permission_adduser", false);
                    document.put("permission_deleteuser", false);
                    document.put("permission_grantpermission", false);
                    collection.insertOne(document);

                    background_toast("Register Succeed!\nLogin As "+name);
                    onSignupSuccess();
                }
            } catch (Exception e) {
            }

        }
    };

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        //Data Transfer
        Intent intent_trans = new Intent(SignupActivity.this, MainActivity.class);
        intent_trans.putExtra("email",  _emailText.getText().toString());
        startActivity(intent_trans);
        MainActivity.counter++;
        finish();
    }

    public void onSignupFailed() {
        //Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

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

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }

    public void background_toast(String msg){
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getBaseContext(),msg,Toast.LENGTH_SHORT).show();
            }
        });
    }
}