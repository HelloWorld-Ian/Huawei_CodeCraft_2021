package com.huawei.java.main;

public class VMBean {

    public final String ID;

    public int preServerID;

    public int ServerID;

    public int AorB = -1;

    public final int type;

    public void setServerID_0(int serverID, int AorB) {
        this.ServerID = serverID;
        this.AorB = AorB;
        preServerID = serverID;
    }

    public void setServerID_1(int serverID) {
        ServerID = serverID;
        preServerID = serverID;
    }

    public final int typeID;

    public VMBean(String ID, int typeID, int type) {
        this.ID = ID;
        this.typeID = typeID;
        ServerID = -1;
        this.type = type;
    }
}
