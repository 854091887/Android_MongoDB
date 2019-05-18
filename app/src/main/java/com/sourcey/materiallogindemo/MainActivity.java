package com.sourcey.materiallogindemo;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.appbar.AppBarLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.*;
import androidx.recyclerview.widget.DefaultItemAnimator;
import 	androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import android.content.Intent;
import android.os.Bundle;
import butterknife.BindView;
import butterknife.ButterKnife;
import android.view.View;

import com.mongodb.MongoClient;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.*;
import static okhttp3.internal.Util.equal;
import static okhttp3.internal.Util.threadFactory;

import org.bson.*;
import org.w3c.dom.Text;

import com.alibaba.fastjson.JSONObject;
import com.sourcey.materiallogindemo.LoginActivity;
import com.sourcey.materiallogindemo.Adapter;
import com.sourcey.materiallogindemo.LinearSectionDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.LongToIntFunction;

public class MainActivity extends AppCompatActivity {
    private CollapsingToolbarLayout mCollapsLayout;
    private Toolbar mToolBar;
    private RecyclerView mRecyView;

    final Integer permission_adduser        = 0;
    final Integer permission_deleteuser     = 1;
    final Integer permission_viewnonadmin   = 2;
    final Integer permission_addadmin       = 3;
    final Integer permission_deleteadmin    = 4;
    final Integer permission_viewadmin      = 5;


    @BindView(R.id.textname) TextView nametext;
    @BindView(R.id.textemail) TextView emailtext;
    @BindView(R.id.textgroup) TextView grouptext;
    //@BindView(R.id.texttoolbar) TextView toolbartext;


    static int counter = 0;
    static int newcounter = 0;
    static final List<String> list = new ArrayList<>();
    public static String myEmail    = null;
    public static String myName     = null;
    public static String myGroup    = null;
    public static String myPasswd   = null;
    public static String targetname_su;
    public static String targetname_admin;
    public static String targetname;
    public static String targetgroup;
    public static boolean[] myPermissions = {false,false,false,false,false,false};
    public static long superadmin_count  = 0;
    public static long admin_count       = 0;
    public static long nonadmin_count    = 0;
    public static long count = 0;
    ArrayList<String> arrlist_superadmin = new ArrayList<String>(20);
    ArrayList<String> arrlist_admin = new ArrayList<String>(20);
    ArrayList<String> arrlist_nonadmin = new ArrayList<String>(20);
    static boolean adminvalidate    = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCollapsLayout = (CollapsingToolbarLayout) findViewById(R.id.mCollaspLayout);
        mToolBar = (Toolbar) findViewById(R.id.mToolBar);
        setSupportActionBar(mToolBar);

        mCollapsLayout.setTitle("Collapsing");
        mCollapsLayout.setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD);
        mCollapsLayout.setCollapsedTitleTextColor(Color.WHITE);
        mCollapsLayout.setExpandedTitleTypeface(Typeface.DEFAULT_BOLD);
        mCollapsLayout.setExpandedTitleColor(Color.TRANSPARENT);

        mRecyView = (RecyclerView) findViewById(R.id.mRecyView);
        mRecyView.setItemAnimator(new DefaultItemAnimator());
        mRecyView.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mRecyView.setLayoutManager(manager);

        //Login First
        if(counter == 0){
            counter++;
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        //Logged In
        ButterKnife.bind(this);
        if(counter == 2){
            Intent intent_trans = getIntent();
            myEmail = intent_trans.getStringExtra("email");
            counter++;
            new Thread(runnable).start();
            new Thread(runnable_count).start();

        }


    }

    Runnable runnable=new Runnable() {

        @Override
        public void run() {
            MongoDBUtil.MongoDBConnect();
            MongoDBUtil.DatabaseSelect("android_permission");
            MongoDBUtil.CollectionSelect("android");
            MongoDBUtil.Query("email",MongoDBUtil.DocGen("$eq",myEmail));
            MongoDBUtil.CollectionSelect("android").find(eq("email", myEmail) ).forEach(PrintBlock);
            MongoDBUtil.close();
        }
    };

    Runnable runnable_count=new Runnable() {

        @Override
        public void run() {
            try {
                MongoClientURI connectionString = new MongoClientURI("mongodb://182.254.149.49:27017");
                MongoClient mongoClient = new MongoClient(connectionString);
                MongoDatabase database = mongoClient.getDatabase("android_permission");
                MongoCollection<Document> collection = database.getCollection("android");
                //background_toast("CONNECTION ESTABLISHED");
                //collection.find(exists(email)  );
                Document query = new Document("group", new Document("$eq", "nonadmin"));
                long count_temp = collection.countDocuments(query);
                runOnUiThread(new Runnable() {
                    public void run() {
                        count = count_temp;
                    }
                });
                collection.find(eq("group","superadmin")).forEach(Getsuperadmin);
                collection.find(eq("group","admin")).forEach(Getadmin);
                collection.find(eq("group","nonadmin")).forEach(Getnonadmin);

                if(equal(myGroup,"superadmin")){
                    list.addAll(arrlist_superadmin);
                    list.addAll(arrlist_admin);
                    list.addAll(arrlist_nonadmin);
                }
                else if(equal(myGroup,"admin")){
                    list.addAll(arrlist_admin);
                    list.addAll(arrlist_nonadmin);
                }else if(equal(myGroup,"nonadmin")){
                    list.addAll(arrlist_nonadmin);
                }
                //list.addAll(arrlist_nonadmin);
                Adapter adapter = new Adapter(list);
                mRecyView.setAdapter(adapter);
                newcounter++;
                adapter.setOnItemClickListener(new Adapter.onItemClickListener() {
                    @Override
                    public void onItemClick(View view) {
                        //Intent i = new Intent(CollapsingActivity.this, MainActivity.class);
                        //startActivity(i);
                    }
                });
                //background_toast(Long.toString(viewcount));

            } catch (Exception e) {
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean quit = false;
    @Override
    public void onBackPressed() {
        if (!quit) {
            //Snackbar.make(coordinatorLayout,"tap",2000).show();
            //background_toast("tap again");
            new Timer(true).schedule(new TimerTask() {
                @Override
                public void run() {
                    quit = false;
                }
            }, 2000);
            quit = true;
        } else {
            super.onBackPressed();
            counter = 0;
            finish();
        }
    }



    public void background_toast(String msg){
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getBaseContext(),msg,Toast.LENGTH_SHORT).show();
            }
        });
    }


    Block<Document> PrintBlock = new Block<Document>() {
        @Override
        public void apply(final Document document) {
            String Json2String = document.toJson();
            //Get The Passwd
            JSONObject jsonObject = JSONObject.parseObject(Json2String);
            String passwd = jsonObject.getString("passwd");
            myGroup = jsonObject.getString("group");
            myEmail = jsonObject.getString("email");
            myName = jsonObject.getString("name");
            myPermissions[permission_adduser]       = Boolean.getBoolean(jsonObject.getString("permission_adduser"));
            myPermissions[permission_deleteuser]    = Boolean.getBoolean(jsonObject.getString("permission_deleteuser"));
            myPermissions[permission_viewnonadmin]  = Boolean.getBoolean(jsonObject.getString("permission_viewnonadmin"));
            myPermissions[permission_addadmin]      = Boolean.getBoolean(jsonObject.getString("permission_addadmin"));
            myPermissions[permission_deleteadmin]   = Boolean.getBoolean(jsonObject.getString("permission_deleteadmin"));
            myPermissions[permission_viewadmin]     = Boolean.getBoolean(jsonObject.getString("permission_viewadmin"));
            //background_toast(passwd+email+user);
            nametext.setText(myName);
            emailtext.setText(myEmail);
            grouptext.setText(myGroup);
            //toolbartext.setText(user);
            mCollapsLayout.setTitle(myName+" | "+myGroup);

        }
    };

    Block<Document> Getsuperadmin = new Block<Document>() {
        @Override
        public void apply(final Document document) {
            String Json2String = document.toJson();
            //Get The Passwd
            JSONObject jsonObject = JSONObject.parseObject(Json2String);
            runOnUiThread(new Runnable() {
                public void run() {
                    targetname_su = jsonObject.getString("name");
                    arrlist_superadmin.add(targetname_su);

                }
            });
        }
    };

    Block<Document> Getadmin = new Block<Document>() {
        @Override
        public void apply(final Document document) {
            String Json2String = document.toJson();
            //Get The Passwd
            JSONObject jsonObject = JSONObject.parseObject(Json2String);
            runOnUiThread(new Runnable() {
                public void run() {
                    targetname_admin = jsonObject.getString("name");
                    arrlist_admin.add(targetname_admin);

                }
            });
        }
    };

    Block<Document> Getnonadmin = new Block<Document>() {
        @Override
        public void apply(final Document document) {
            String Json2String = document.toJson();
            //Get The Passwd
            JSONObject jsonObject = JSONObject.parseObject(Json2String);
            runOnUiThread(new Runnable() {
                public void run() {
                    targetname = jsonObject.getString("name");
                    targetgroup = jsonObject.getString("group");
                    arrlist_nonadmin.add(targetname);

                }
            });
        }
    };
}
