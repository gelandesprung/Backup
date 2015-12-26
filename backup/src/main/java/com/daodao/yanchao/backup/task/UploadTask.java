package com.daodao.yanchao.backup.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.daodao.yanchao.backup.ServerConfig;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

/**
 * Created by yanchao on 12/13/15.
 */
public class UploadTask extends AsyncTask< ServerConfig, Integer, Void > {
    private CircularProgressBar numberProgressBar;
    TextView backup_tag;
    Map< String, File > localFiles = new HashMap<>();
    Map< String, SmbFile > nasFiles = new HashMap<>();
    Context context;
    NtlmPasswordAuthentication authentication;

    public UploadTask(Context context, CircularProgressBar progressBar, TextView tag) {
        this.context = context;
        numberProgressBar = progressBar;
        backup_tag = tag;
    }

    @Override
    protected Void doInBackground(ServerConfig... params) {
        // TODO: 12/13/15 检查本地照片的数量
        ServerConfig config = params[0];
        String rootpath = "smb://" + config.server + "/" + config.targetdir + "/";
        File externalRoot = new File(config.localdir);
        if ( config.username != null && config.password != null ) {
            authentication = new NtlmPasswordAuthentication(config.server, config.username, config.password);
        } else {
            authentication = NtlmPasswordAuthentication.ANONYMOUS;
        }
        appendFiles(externalRoot, localFiles);
        Log.d("yanchao", "localFiles:" + localFiles.keySet());
        // TODO: 12/13/15 检查NAS照片的数量
        try {
            SmbFile smbFileRoot = new SmbFile(rootpath, authentication);
            if ( !smbFileRoot.exists() ) {
                smbFileRoot.mkdirs();
            }
            appendSmbFiles(smbFileRoot, nasFiles);
            Log.d("yanchao", "localFiles:" + nasFiles.keySet());
        } catch ( MalformedURLException e ) {
            e.printStackTrace();
        } catch ( SmbException e ) {
            e.printStackTrace();
        }
        // TODO: 12/13/15 对比找出没有上传的照片
        Set< String > targetkeys = localFiles.keySet();
        targetkeys.removeAll(nasFiles.keySet());
        // TODO: 12/13/15 上传照片开始，及时更新进度
        int process = 1;
        int count = targetkeys.size();
        for ( String key : targetkeys ) {
            publishProgress(process * 100 / targetkeys.size(), process, count);
            try {
                FileInputStream in = new FileInputStream(localFiles.get(key));
                String fileName = rootpath + localFiles.get(key).getPath().replace(config.localdir, "");
                SmbFile subDir = new SmbFile(fileName.replace(key, ""));
                if ( !subDir.exists() ) {
                    subDir.mkdirs();
                }
                SmbFileOutputStream out = new SmbFileOutputStream(new SmbFile(fileName, authentication));

                byte[] b = new byte[8192];
                int n, tot = 0;
                while ( (n = in.read(b)) > 0 ) {
                    out.write(b, 0, n);
                    tot += n;
                }
            } catch ( FileNotFoundException e ) {
                e.printStackTrace();
            } catch ( MalformedURLException e ) {
                e.printStackTrace();
            } catch ( UnknownHostException e ) {
                e.printStackTrace();
            } catch ( SmbException e ) {
                e.printStackTrace();
            } catch ( IOException e ) {
                e.printStackTrace();
            }
            process++;
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        numberProgressBar.setProgress(values[0]);
        if ( values[0] == 100 ) {
            backup_tag.setText("备份完成");
        } else {
            backup_tag.setText(values[1] + "/" + values[2]);
        }
    }

    private void appendFiles(File dir, Map< String, File > container) {
        for ( File file : dir.listFiles() ) {
            if ( file.isDirectory() ) {
                appendFiles(file, container);
            }
            if ( file.isFile() && !file.isHidden() ) {
                container.put(file.getName(), file);
            }
        }
    }

    private void appendSmbFiles(SmbFile dir, Map< String, SmbFile > container) throws SmbException {
        for ( SmbFile file : dir.listFiles() ) {
            if ( file.isDirectory() ) {
                appendSmbFiles(file, container);
            }
            if ( file.isFile() && !file.isHidden() ) {
                container.put(file.getName(), file);
            }
        }
    }
}
