package com.huawei.java.main;
import java.util.*;

public class Server {
    public LinearRegression priceLr;
    public LinearRegression dailyCostLr;

    public long cost = 0;

    public int migrationCount = 0;

    final int[] serverPrice;

    public Server() {
        int index = 0;
        serverPrice = new int[type.size()];
        Set<Integer> keys = type.keySet();
        for (int x : keys) {
            serverPrice[index++] = x;
        }
        setPriceLr();
        setDailyCostLr();
    }


    public static final Map<String, Integer> name = new HashMap<>();
    public static final Map<Integer, String> type = new HashMap<>();
    public static final Map<Integer, int[]> typeInfo = new HashMap<>();


    public final Map<Integer, ServerBean> runningServers = new HashMap<>();
    public final ArrayList<ServerBean> availableServerBean = new ArrayList<>();


    public int server_num = 0;
    public int realIdSet = 0;


    public synchronized void purchase(ServerBean serverBean) {
        runningServers.put(server_num, serverBean);
        availableServerBean.add(serverBean);
        server_num++;
    }


    public String[] collectServer(Queue<ServerBean> serverBeans) {
        Map<Integer, ArrayList<ServerBean>> count = new HashMap<>();
        while (!serverBeans.isEmpty()) {
            ServerBean peek = serverBeans.poll();
            int typeID = peek.TypeID;
            count.computeIfAbsent(typeID, key -> new ArrayList<>());
            count.get(typeID).add(peek);
        }
        String[] str = new String[count.size() + 1];
        int total = count.size();
        str[0] = "(purchase, " + total + ")";
        int index = 1;
        Set<Integer> keys = count.keySet();
        for (int x : keys) {
            String name = Server.type.get(x);
            ArrayList<ServerBean> temp = count.get(x);
            for (ServerBean y : temp) {
                y.setRealID(realIdSet++);
            }
            String num = temp.size() + "";
            str[index++] = "(" + name + ", " + num + ")";
        }
        return str;
    }


    public String[] collectVm(Queue<VMBean> VMBeans) {
        String[] str = new String[VMBeans.size()];
        int index = 0;
        while (!VMBeans.isEmpty()) {
            VMBean peek = VMBeans.poll();
            ServerBean s = runningServers.get(peek.ServerID);
            if (peek.AorB == -1) {
                str[index++] = "(" + s.realID + ")";
            } else {
                if (peek.AorB == 0) {
                    str[index++] = "(" + s.realID + ", " + "A)";

                } else {
                    str[index++] = "(" + s.realID + ", " + "B)";

                }
            }
        }
        return str;
    }

    public void Install(VMBean vmBean, ServerBean serverBean, int order) {
        int[] VmInfo = Vm.typeInfo.get(vmBean.typeID);
        int cpu = VmInfo[0];
        int mem = VmInfo[1];
        int type = VmInfo[2];
        int serverID = serverBean.ID;
        if (type == 0) {
            if (order == 0) {
                if (serverBean.A_cpu >= cpu && serverBean.A_mem >= mem) {
                    serverBean.A_cpu -= cpu;
                    serverBean.A_mem -= mem;
                    vmBean.setServerID_0(serverID, 0);
                    vmBean.preServerID = serverID;
                    serverBean.VMs.put(vmBean.ID, 0);
                } else {
                    serverBean.B_cpu -= cpu;
                    serverBean.B_mem -= mem;
                    vmBean.setServerID_0(serverID, 1);
                    vmBean.preServerID = serverID;
                    serverBean.VMs.put(vmBean.ID, 1);
                }
            } else if (order == 1) {
                if (serverBean.B_cpu >= cpu && serverBean.B_mem >= mem) {
                    serverBean.B_cpu -= cpu;
                    serverBean.B_mem -= mem;
                    vmBean.setServerID_0(serverID, 1);
                    vmBean.preServerID = serverID;
                    serverBean.VMs.put(vmBean.ID, 1);
                } else {
                    serverBean.A_cpu -= cpu;
                    serverBean.A_mem -= mem;
                    vmBean.setServerID_0(serverID, 0);
                    vmBean.preServerID = serverID;
                    serverBean.VMs.put(vmBean.ID, 0);
                }
            }
        } else {
            serverBean.A_cpu -= cpu / 2;
            serverBean.A_mem -= mem / 2;
            serverBean.B_cpu -= cpu / 2;
            serverBean.B_mem -= mem / 2;
            vmBean.setServerID_1(serverID);
            serverBean.VMs.put(vmBean.ID, -1);
        }
    }

    public boolean Install(LinkedList<VMBean> vmBeans, ServerBean serverBean) {

        LinkedList<VMBean> copyVmBeans = new LinkedList<>(vmBeans);
        int s_A, s_B;
        for (VMBean vmBean : copyVmBeans) {
            if (checkServerIsFull(serverBean)) break;
            if (isContainable(serverBean, vmBean)) {
                if (vmBean.type == 0) {
                    s_A = serverBean.A_cpu + serverBean.A_mem;
                    s_B = serverBean.B_cpu + serverBean.B_mem;
                    if (s_A <= s_B) {
                        Install(vmBean, serverBean, 0);
                    } else {
                        Install(vmBean, serverBean, 1);
                    }
                } else {
                    Install(vmBean, serverBean, 0);
                }
            }
        }
        return serverBean.VMs.size() != 0;
    }

    public void Uninstall(VMBean vmBean, ServerBean serverBean) {
        int[] VmInfo = Vm.typeInfo.get(vmBean.typeID);
        int cpu = VmInfo[0];
        int mem = VmInfo[1];
        int type = VmInfo[2];
        int AorB = vmBean.AorB;
        if (type == 0) {
            if (AorB == 0) {
                serverBean.A_cpu += cpu;
                serverBean.A_mem += mem;
            } else {
                serverBean.B_cpu += cpu;
                serverBean.B_mem += mem;
            }
        } else {
            serverBean.A_cpu += cpu / 2;
            serverBean.A_mem += mem / 2;
            serverBean.B_cpu += cpu / 2;
            serverBean.B_mem += mem / 2;
        }
        serverBean.VMs.remove(vmBean.ID);
    }

    public boolean checkServerIsFull(ServerBean serverBean) {
        return ((double) serverBean.A_cpu <= 0 || (double) serverBean.A_mem <= 0) &&
                ((double) serverBean.B_cpu <= 0 || (double) serverBean.B_mem <= 0);
    }

    public boolean isContainable(ServerBean serverBean, VMBean vmBean) {
        int[] info = Vm.typeInfo.get(vmBean.typeID);
        int cpu = info[0];
        int mem = info[1];
        int type = info[2];
        if (type == 0) {
            return (serverBean.A_cpu >= cpu && serverBean.A_mem >= mem) ||
                    (serverBean.B_cpu >= cpu && serverBean.B_mem >= mem);
        } else {
            return (serverBean.A_cpu >= cpu / 2 && serverBean.A_mem >= mem / 2) &&
                    (serverBean.B_cpu >= cpu / 2 && serverBean.B_mem >= mem / 2);
        }
    }

    public ArrayList<ServerBean> purchaseBestServers(LinkedList<VMBean> vmBeans, int day) {
        ArrayList<ServerBean> list = new ArrayList<>();
        while (!vmBeans.isEmpty()) {
            ServerBean bestServerBean = null;
            double minIndex = Double.MAX_VALUE;

            for (int typeID : serverPrice) {
                ServerBean serverBean = new ServerBean(server_num, typeID);
                if (!Install(vmBeans, serverBean)) {
                    continue;
                }
                double index = getServerIndex(serverBean, day);
                if (index < minIndex) {
                    bestServerBean = serverBean;
                    minIndex = index;
                }
            }

            int size = vmBeans.size();
            for (int i = 0; i < size; i++) {
                VMBean vmBean = vmBeans.poll();
                assert bestServerBean != null;
                Map<String, Integer> VMs = bestServerBean.VMs;
                int serverID = bestServerBean.ID;
                assert vmBean != null;
                String ID = vmBean.ID;
                if (VMs.containsKey(ID)) {
                    int AorB = VMs.get(ID);
                    if (AorB == -1) {
                        vmBean.setServerID_1(serverID);
                    } else {
                        vmBean.setServerID_0(serverID, AorB);
                    }
                } else {
                    vmBeans.addLast(vmBean);
                }
            }
            purchase(bestServerBean);
            list.add(bestServerBean);
            assert bestServerBean != null;
            cost += bestServerBean.price;
        }
        return list;
    }


    public double getServerIndex(ServerBean serverBean, int day) {
        int[] info = Server.typeInfo.get(serverBean.TypeID);
        double A_cpu = serverBean.A_cpu;
        double B_cpu = serverBean.B_cpu;
        double A_mem = serverBean.A_mem;
        double B_mem = serverBean.B_mem;
        double costCPU = info[0] - A_cpu - B_cpu;
        double costMem = info[1] - A_mem - B_mem;
        double price = serverBean.price;
        return (price + serverBean.dailyCost * day) / (costCPU + costMem);
    }

    public int limit = 0;

    public ArrayList<String> migration(Map<String, VMBean> userVMs) {
        //重要参数！！！！决定要遍历的虚拟机的空余比例下限（利用率上限）。该值越小，成本越低但是时间会增加！
        final double serverLimit = 0.009;

        Map<Integer, int[]> serverInfo = Server.typeInfo;

        Map<Integer, int[]> VmTypeInfo = Vm.typeInfo;

        ArrayList<String> str = new ArrayList<>();

        ArrayList<VMBean> move;

        ArrayList<ServerBean> queue = new ArrayList<>();
        ArrayList<ServerBean> availableSet = new ArrayList<>();

        for (ServerBean x : availableServerBean) {
            //将非空且没有过满的服务器加入到二叉排序树中
            int[] xInfo = serverInfo.get(x.TypeID);
            double index_A = (double) x.A_cpu / xInfo[0] * 0.5 + (double) x.A_mem / xInfo[1] * 0.5;
            double index_B = (double) x.B_cpu / xInfo[0] * 0.5 + (double) x.B_mem / xInfo[1] * 0.5;
            if (!x.VMs.isEmpty() && (index_A >= serverLimit || index_B >= serverLimit)) {
                queue.add(x);
                availableSet.add(x);
            }
        }
        //对要提取虚拟机的服务器进行排序，空的在前
        queue.sort((o1, o2) -> {
            int[] info1 = serverInfo.get(o1.TypeID);
            int[] info2 = serverInfo.get(o2.TypeID);

            double index_1 = Math.max((double) o1.A_cpu / info1[0] * 0.75 + (double) o1.B_cpu / info1[0] * 0.25
                    , (double) o1.A_mem / info1[1] * 0.75 + (double) o1.B_mem / info1[1] * 0.25);
            double index_2 = Math.max((double) o2.A_cpu / info2[0] * 0.75 + (double) o2.B_cpu / info2[0] * 0.25
                    , (double) o2.A_mem / info2[1] * 0.75 + (double) o2.B_mem / info2[1] * 0.25);
            return Double.compare(o1.VMs.size() * 30 - index_1, o2.VMs.size() * 30 - index_2);
        });
        ServerBean last = null;
        Set<String> set = new HashSet<>();
        int serverIndex = 0;
        while (limit > 0 && serverIndex < queue.size()) {
            move = new ArrayList<>();
            if (last != null) {
                Map<String, Integer> VMs = last.VMs;
                if (VMs.size() != 0) {
                    Set<String> keys = VMs.keySet();
                    for (String x : keys) {
                        if (limit <= 0) {
                            break;
                        }
                        VMBean vmX = userVMs.get(x);
                        if (!set.contains(x)) {
                            set.add(x);
                            move.add(vmX);
                            limit--;
                        }
                    }
                }
            }
            ServerBean peek = queue.get(serverIndex++);
            last = peek;
            Map<String, Integer> VMs = peek.VMs;
            Set<String> keys = VMs.keySet();
            for (String x : keys) {
                if (limit <= 0) {
                    break;
                }
                VMBean vmX = userVMs.get(x);
                set.add(x);
                move.add(vmX);
                limit--;
            }
            move.sort((o2, o1) -> {
                int[] info1 = VmTypeInfo.get(o1.typeID);
                int[] info2 = VmTypeInfo.get(o2.typeID);
                return (info2[0] + info2[1]) - (info1[0] + info1[1]);
            });

            int y_index, y_cpu, y_mem, y_A, y_B;
            boolean a, b;

            for (VMBean x : move) {
                int[] info = VmTypeInfo.get(x.typeID);
                ServerBean pre = runningServers.get(x.ServerID);
                ServerBean s = null;
                int min_index = x.type == 1 ? (Math.max(pre.A_cpu + pre.B_cpu, pre.A_mem + pre.B_mem)) >> 1
                        : (x.AorB == 0 ? Math.max(pre.A_cpu, pre.A_mem) : Math.max(pre.B_cpu, pre.B_mem));
                for (ServerBean y : availableSet) {
                    if (y.ID != x.ServerID) {
                        if (x.type == 1) {
                            if (y.A_cpu >= (info[0] >> 1) && y.A_mem >= (info[1] >> 1) && y.B_cpu >= (info[0] >> 1) && y.B_mem >= (info[1] >> 1)) {
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
                        if (y_index <= min_index) {
                            min_index = y_index;
                            s = y;
                        }
                    }
                }
                if (s != null) {
                    //真正开始迁移
                    Uninstall(x, pre);
                    if (x.type == 0) {
                        double s_A = s.A_cpu >= info[0] && s.A_mem >= info[1] ? (double)(s.A_cpu - info[0]+ s.A_mem - info[1]) :Double.MAX_VALUE;
                        double s_B = s.B_cpu >= info[0] && s.B_mem >= info[1] ? (double)(s.B_cpu - info[0]+ s.B_mem - info[1]) : Double.MAX_VALUE;
                        if (s_A < s_B) {
                            Install(x, s, 0);
                        } else {
                            Install(x, s, 1);
                        }
                    } else {
                        Install(x, s, 0);
                    }

                    //判断是否需要从availableSet删除虚拟机以减少运行时间
                    if (pre.VMs.isEmpty()) {
                        availableSet.remove(pre);
                    }
                    int[] sInfo = serverInfo.get(s.TypeID);
                    double index_A = (double) s.A_cpu / sInfo[0] * 0.5 + (double) s.A_mem / sInfo[1] * 0.5;
                    double index_B = (double) s.B_cpu / sInfo[0] * 0.5 + (double) s.B_mem / sInfo[1] * 0.5;
                    if (index_A < serverLimit && index_B < serverLimit) {
                        availableSet.remove(s);
                    }

                    migrationCount++;
                    if (x.type == 0) {
                        str.add("(" + x.ID + "," + s.realID + "," + (char) ('A' + x.AorB) + ")");
                    } else {
                        str.add("(" + x.ID + "," + s.realID + ")");
                    }

                } else {
                    limit++;
                }
            }
        }
        return str;
    }

    public void setPriceLr() {
        int n = typeInfo.size();
        Set<Integer> keyset = typeInfo.keySet();
        double[][] features = new double[n][3];
        double[] results = new double[n];
        int i = 0;
        for (Integer key : keyset) {
            features[i][0] = typeInfo.get(key)[0] / 100.0;
            features[i][1] = typeInfo.get(key)[1] / 100.0;
            results[i++] = typeInfo.get(key)[2] / 100.0;
        }
        priceLr = new LinearRegression(features, results);
    }

    public void setDailyCostLr() {
        int n = typeInfo.size();
        Set<Integer> keyset = typeInfo.keySet();
        double[][] features = new double[n][3];
        double[] results = new double[n];
        int i = 0;
        for (Integer key : keyset) {
            features[i][0] = typeInfo.get(key)[0] / 100.0;
            features[i][1] = typeInfo.get(key)[1] / 100.0;
            results[i++] = typeInfo.get(key)[3] / 100.0;
        }
        dailyCostLr = new LinearRegression(features, results);
    }
}