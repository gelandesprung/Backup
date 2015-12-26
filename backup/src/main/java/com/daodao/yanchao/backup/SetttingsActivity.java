package com.daodao.yanchao.backup;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

/**
 * Created by yanchao on 12/15/15.
 */
public class SetttingsActivity extends AppCompatActivity {

    private static final String TAG = "yanchao";
    Context context;
    private DbUtils dbUtils;
    ServerConfig serverConfig;
    ExecutorService pool = Executors.newSingleThreadExecutor();

    EditText server, username, password, target;
    TextView help_button;
    Button check;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        context = this;

        server = (EditText) findViewById(R.id.server_ipaddr);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        target = (EditText) findViewById(R.id.target_dir);
        check = (Button) findViewById(R.id.check);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.settings);

        getSearch_help().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pool.execute(checkTask);
            }
        });
        dbUtils = DbUtils.create(getApplicationContext());
        try {
            serverConfig = dbUtils.findFirst(ServerConfig.class);
            if ( serverConfig == null ) {
                serverConfig = new ServerConfig();
            } else {
                server.setText(serverConfig.server);
                username.setText(serverConfig.username);
                password.setText(serverConfig.password);
                target.setText(serverConfig.targetdir);
            }
        } catch ( DbException e ) {
            e.printStackTrace();
        }
    }


    private TextView getSearch_help() {
        return (TextView) findViewById(R.id.search_help);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ( android.R.id.home == item.getItemId() ) {
            this.finish();
            return true;
        }
        switch ( item.getItemId() ) {
            case R.id.save_config:
                EditText addr = server;
                if ( !TextUtils.isEmpty(addr.getText()) &&
                        !TextUtils.isEmpty(target.getText()) ) {
                    serverConfig.server = addr.getText().toString();
                    serverConfig.targetdir = target.getText().toString();
                    if ( !TextUtils.isEmpty(username.getText()) && !TextUtils.isEmpty(password.getText()) ) {
                        serverConfig.username = username.getText().toString();
                        serverConfig.password = password.getText().toString();
                    }
                    try {
                        dbUtils.saveOrUpdate(serverConfig);
                    } catch ( DbException e ) {
                        e.printStackTrace();
                    }
                    finish();
                }
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Runnable checkTask = new Runnable() {
        @Override
        public void run() {

            try {
                NtlmPasswordAuthentication authentication;
                if ( username.getText().toString().isEmpty() || password.getText().toString().isEmpty() ) {
                    authentication = NtlmPasswordAuthentication.ANONYMOUS;
                } else {
                    authentication = new NtlmPasswordAuthentication(server.getText().toString(), username.getText().toString(), password.getText().toString());
                }
                new SmbFile("smb://" + server.getText().toString(), authentication).connect();
            } catch ( Exception e ) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "配置有误，请检查重试", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            EditText addr = server;
            if ( !TextUtils.isEmpty(addr.getText()) &&
                    !TextUtils.isEmpty(target.getText()) ) {
                serverConfig.server = addr.getText().toString();
                serverConfig.targetdir = target.getText().toString();
                if ( !TextUtils.isEmpty(username.getText()) && !TextUtils.isEmpty(password.getText()) ) {
                    serverConfig.username = username.getText().toString();
                    serverConfig.password = password.getText().toString();
                }
                try {
                    dbUtils.saveOrUpdate(serverConfig);
                } catch ( DbException e ) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }
        }
    };
}
