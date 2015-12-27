package com.daodao.yanchao.backup;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.daodao.yanchao.backup.task.UploadTask;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    DbUtils dbUtils;
    CircularProgressBar progressbar;
    TextView backup_tag;
    Context context;
    private View.OnClickListener progressbar_clicklistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Snackbar.make(v, R.string.backup_tag, Snackbar.LENGTH_INDEFINITE).setAction("确定", new
                    View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ServerConfig config = null;
                            try {
                                config = dbUtils.findFirst(ServerConfig.class);

                            } catch ( DbException e ) {
                                e.printStackTrace();
                            }
                            if ( config == null ) {
                                Intent intent = new Intent(MainActivity.this, SetttingsActivity.class);
                                startActivity(intent);
                            } else {
                                if ( progressbar.hasOnClickListeners() ) {
                                    progressbar.setOnClickListener(null);
                                }
                                config.localdir = Environment.getExternalStorageDirectory() + "/DCIM";
                                upload(config);
                            }
                        }
                    }).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_main);
        progressbar = (CircularProgressBar) findViewById(R.id.mike);
        backup_tag = (TextView) findViewById(R.id.backup_tag);

        dbUtils = DbUtils.create(getApplicationContext());
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
        progressbar.setOnClickListener(progressbar_clicklistener);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.code, R
                .string.code);
        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_code_lab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ( drawerToggle.onOptionsItemSelected(item) )
            return true;

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if ( id == R.id.action_settings ) {
            Intent intent = new Intent(this, SetttingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }


    //private function
    private void upload(ServerConfig config) {
        new UploadTask(MainActivity.this, progressbar, backup_tag) {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                backup_tag.setVisibility(View.VISIBLE);
                progressbar.setOnClickListener(progressbar_clicklistener);
            }
        }.execute(config);
//                "smb://" + config.server + "/" + config.targetdir + "/", config.server, config.username, config.password);

    }
}
