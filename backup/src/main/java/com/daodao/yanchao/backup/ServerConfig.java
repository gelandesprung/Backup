package com.daodao.yanchao.backup;


import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Transient;

/**
 * Created by yanchao on 12/15/15.
 */
@Table( name = "serverconfig" )
public class ServerConfig {
    int id;
    @Column( column = "server" )
    public String server;
    @Column( column = "username" )
    public String username;
    @Column( column = "password" )
    public String password;
    @Column( column = "targetdir" )
    public String targetdir;
    @Transient
    public String localdir;
}
