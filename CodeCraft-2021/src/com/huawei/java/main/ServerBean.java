package com.huawei.java.main;

import java.util.HashMap;
import java.util.Map;

public  class ServerBean {


    //A、B节点的cpu、mem剩余量
    public int A_cpu;
    public int A_mem;
    public int B_cpu;
    public int B_mem;

    //服务器的价格和日常消费
    public final int price;
    public final int dailyCost;

    //服务器ID以及输出时需要的realID
    public final int ID;
    public int realID;

    public void setRealID(int realID) {
        this.realID = realID;
    }

    //服务器的类型编号
    public final int TypeID;

    //服务器上安装的虚拟机
    public final Map<String,Integer>VMs=new HashMap<>();

    public ServerBean(int ID, int typeID) {
        Map<Integer,int[]>server= Server.typeInfo;
        int[]info=server.get(typeID);
        A_cpu = info[0]/2;
        A_mem = info[1]/2;
        B_cpu = info[0]/2;
        B_mem = info[1]/2;

        price=info[2];
        dailyCost=info[3];
        this.ID = ID;
        TypeID = typeID;
    }
}
