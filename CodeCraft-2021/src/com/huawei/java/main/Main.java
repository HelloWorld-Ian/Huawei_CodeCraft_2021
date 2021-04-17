package com.huawei.java.main;

public class Main {
    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();   //获取开始时间

        In in = new In();
        try {
            in.PrepareData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Server server = new Server();

        Vm vm = new Vm(server, in);
        int InIndex = 0;


        for (int day = 0; day < in.T; day++) {
            vm.dealWithDailyRequest(in.requestList.get(day), in.T - day);
            //统计每日能耗成本
            for (ServerBean s : server.runningServers.values())
                if (!s.VMs.isEmpty())
                    server.cost += s.dailyCost;
            Out.cout.flush(); //清空输出缓存
            //读取新一天的数据
            if (InIndex < in.T - in.K) {
                try {
                    in.getTodayData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            InIndex++;
        }

        long endTime = System.currentTimeMillis(); //获取结束时间
        System.out.println("cost: " + server.cost);
        System.out.println("迁移次数： " + server.migrationCount);
        System.out.println("程序运行时间 ： " + (endTime - startTime) / 1000.0 + "s");
        System.out.println("服务器总量： " + server.runningServers.size());
    }

}
