package com.daodao.yanchao.backup.task;

import android.content.Context;
import android.os.AsyncTask;

import com.daodao.yanchao.backup.utils.NetUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

/**
 * Created by yanchao on 12/15/15.
 */
public class SmbFindTask extends AsyncTask<Void, Void, List<String>> {
    Context context;

    public SmbFindTask(Context context) {
        this.context = context;
    }

    @Override
    protected List<String> doInBackground(Void... params) {
        StringBuilder server = new StringBuilder("smb://");
        List<String> list = new ArrayList<>();
        if (NetUtils.checkEnable(context)) {
            String ip = NetUtils.getLocalIpAddress(context);
            server.append(ip.substring(0, ip.lastIndexOf('.') + 1));
            if (ip != null) {
                for (int i = 0; i < 256; i++) {
                    String file_path = server.toString() + i + '/';
                    try {
                        SmbFile file = new SmbFile(file_path);
                        file.connect();
                        list.add(file_path);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (SmbException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                list.remove(ip);
            }
        }
        return list;
    }
}
