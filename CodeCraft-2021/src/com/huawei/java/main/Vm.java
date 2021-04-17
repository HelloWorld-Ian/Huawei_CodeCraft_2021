package com.huawei.java.main;


import java.util.*;


public class Vm {
    public static final Map<String, Integer> type = new HashMap<>();
    public static final Map<Integer, int[]> typeInfo = new HashMap<>();

    public final Set<Integer> allUsedVM_0 = new HashSet<>();
    public final Set<Integer> allUsedVM_1 = new HashSet<>();


    public static final Map<String, VMBean> userVMs = new HashMap<>();

    private final Server s;

    public int count=1;

    public Vm(Server server, In in) {
        this.s = server;
        setAllUsedVM(in.requestList);
    }

    public void setAllUsedVM(ArrayList<ArrayList<String[]>> requestList) {
        for (ArrayList<String[]> x : requestList) {
            for (String[] y : x) {
                String name = y[0];
                if (name.equals("add")) {
                    int num = Vm.type.get(y[1]);
                    int type = Vm.typeInfo.get(num)[2];
                    if (type == 0) {
                        allUsedVM_0.add(num);
                    } else if (type == 1) {
                        allUsedVM_1.add(num);
                    }
                }
            }
        }
    }

    public void dealWithDailyRequest(List<String[]> request, int day) {

        ArrayList<VMBean> add = new ArrayList<>();
        ArrayList<VMBean> remove = new ArrayList<>();


        s.limit = (int) Math.floor((double) userVMs.size() * 3 / 100);
        ArrayList<String> migrationStr = new ArrayList<>(s.migration(userVMs));
        if(request.size()>=10000&&count>0){
            s.limit=Integer.MAX_VALUE;
            migrationStr.addAll(s.migration(userVMs));
            count--;
        }
        for (Object[] x : request) {
            if (x[0].equals("add")) {
                String name = (String) x[1];
                String ID = (String) x[2];
                int typeID = Vm.type.get(name);
                int type = Vm.typeInfo.get(typeID)[2];
                VMBean vm = new VMBean(ID, typeID, type);
                add.add(vm);
                userVMs.put(vm.ID, vm);

            } else if (x[0].equals("del")) {
                String name = (String) x[1];
                remove.add(userVMs.get(name));
            }
        }
        ArrayList<ServerBean> purchase = setVMs(add, day);
        deleteVMs(remove);

        Queue<ServerBean> serversPurchase = new LinkedList<>(purchase);
        Queue<VMBean> vmBeanSet = new LinkedList<>(add);
        Out.outPutServer(s.collectServer(serversPurchase));
        Out.outPutMigration(migrationStr);
        Out.outPutVm(s.collectVm(vmBeanSet));
    }

    public ArrayList<ServerBean> setVMs(ArrayList<VMBean> vmBeans, int day) {
        Map<Integer, int[]> vmInfo = Vm.typeInfo;
        LinkedList<VMBean> vm_Bean_uncontainable = new LinkedList<>();

        ArrayList<VMBean> sortedvmBeans = new ArrayList<>(vmBeans);
        sortedvmBeans.sort((o1, o2) -> {
            int[] info1 = vmInfo.get(o1.typeID);
            int[] info2 = vmInfo.get(o2.typeID);
            return (info2[0] + info2[1]) - (info1[0] + info1[1]);
        });
        ArrayList<ServerBean> availableServerBean = new ArrayList<>(s.availableServerBean);

        int y_cpu, y_mem, y_index, y_A, y_B;
        boolean a, b;

        for (VMBean x : sortedvmBeans) {
            int[] info = Vm.typeInfo.get(x.typeID);
            ServerBean serverBean = null;
            int minValue = Integer.MAX_VALUE;
            userVMs.put(x.ID, x);
            for (ServerBean y : availableServerBean) {
                if (x.type == 1) {
                    if (y.A_cpu >= info[0] / 2 && y.A_mem >= info[1] / 2 && y.B_cpu >= info[0] / 2 && y.B_mem >= info[1] / 2) {
                        y_cpu = (y.A_cpu + y.B_cpu) - info[0];
                        y_mem = (y.A_mem + y.B_mem) - info[1];
                    } else continue;
                    y_index = Math.max(y_cpu >> 1, y_mem >> 1);
                } else {
                    y_A = Integer.MAX_VALUE;
                    y_B = Integer.MAX_VALUE;
                    a = y.A_cpu >= info[0] && y.A_mem >= info[1];
                    b = y.B_cpu >= info[0] && y.B_mem >= info[1];
                    if (!a && !b) continue;
                    if (a) y_A = Math.max(y.A_cpu - info[0], (y.A_mem - info[1]));
                    if (b) y_B = Math.max(y.B_cpu - info[0], (y.B_mem - info[1]));
                    y_index = Math.min(y_A, y_B);
                }
                if (y_index <= minValue) {
                    minValue = y_index;
                    serverBean = y;
                }
            }
            if (serverBean != null) {
                if (x.type == 0) {
                    double s_A = serverBean.A_cpu >= info[0] && serverBean.A_mem >= info[1] ? (double)(serverBean.A_cpu - info[0]+(serverBean.A_mem - info[1])) : Double.MAX_VALUE;
                    double s_B = serverBean.B_cpu >= info[0] && serverBean.B_mem >= info[1] ? (double)(serverBean.B_cpu - info[0]+(serverBean.B_mem - info[1])) : Double.MAX_VALUE;
                    if (s_A < s_B) {
                        s.Install(x, serverBean, 0);
                    } else {
                        s.Install(x, serverBean, 1);
                    }
                } else {
                    s.Install(x, serverBean, 0);
                }
            }
            if (x.ServerID == -1) {
                vm_Bean_uncontainable.addLast(x);
            }
        }

        double param = 2000;

        ArrayList<ServerBean> purchase = new ArrayList<>();

        if (vm_Bean_uncontainable.size() > param) {
            //分批前先打乱顺序
            Collections.shuffle(vm_Bean_uncontainable);
            int size = vm_Bean_uncontainable.size();
            int l = (int) Math.ceil(size / param);
            for (int i = 0; i < l; i++) {
                int start = (int) param * i;
                int end = Math.min(start + (int) param, size);
                LinkedList<VMBean> part = new LinkedList<>(vm_Bean_uncontainable.subList(start, end));
                //对分批结果重新排序
                part.sort((o1, o2) -> {
                    int[] info1 = vmInfo.get(o1.typeID);
                    int[] info2 = vmInfo.get(o2.typeID);
                    return (info2[0] + info2[1]) - (info1[0] + info1[1]);
                });
                purchase.addAll(s.purchaseBestServers(part, day));
            }
        } else
            purchase.addAll(s.purchaseBestServers(vm_Bean_uncontainable, day));
        return purchase;
    }

    public void deleteVMs(ArrayList<VMBean> vmBeans) {
        for (VMBean x : vmBeans) {
            ServerBean serverBean = s.runningServers.get(x.ServerID);
            s.Uninstall(x, serverBean);
            userVMs.remove(x.ID);
            ServerBean serverBean1 = s.runningServers.get(x.ServerID);
            serverBean1.VMs.remove(x.ID);
        }
    }
}
