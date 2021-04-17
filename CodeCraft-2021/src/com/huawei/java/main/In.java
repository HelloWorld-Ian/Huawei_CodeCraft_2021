
package com.huawei.java.main;

import java.io.*;
import java.util.ArrayList;


public class In {

    public In() {
        requestList = new ArrayList<>();
    }

    public final ArrayList<ArrayList<String[]>> requestList;

    BufferedReader cin;

    int T, K;

    public void PrepareData() throws Exception {
        try {
            System.setIn(new FileInputStream("C:\\Users\\86153\\Desktop\\fusai\\training-1.txt"));
        } catch (FileNotFoundException ignored) {
        }
        cin = new BufferedReader(new InputStreamReader(System.in));
        int N = Integer.parseInt(cin.readLine());
        for (int i = 0; i < N; i++) {
            String line = cin.readLine();
            line = line.substring(1, line.length() - 1);
            String[] info = line.split(", ");
            int size = Server.type.size();
            int index = size + 1;
            Server.type.put(index, info[0]);
            Server.name.put(info[0], index);
            int[] data = new int[4];
            for (int m = 0; m < 4; m++) {
                int num = Integer.parseInt(info[m + 1]);
                data[m] = num;
            }
            Server.typeInfo.put(index, data);
        }
        int M = Integer.parseInt(cin.readLine());
        for (int i = 0; i < M; i++) {
            String line = cin.readLine();
            line = line.substring(1, line.length() - 1);
            String[] info = line.split(", ");
            int size = Vm.type.size();
            int index = size + 1;
            Vm.type.put(info[0], index);
            int[] data = new int[3];
            for (int m = 0; m < 3; m++) {
                int num = Integer.parseInt(info[m + 1]);
                data[m] = num;
            }
            Vm.typeInfo.put(index, data);
        }
        String[] TAndK = cin.readLine().split(" ");
        T = Integer.parseInt(TAndK[0]);
        K = Integer.parseInt(TAndK[1]);
        for (int i = 0; i < K; i++) {
            ArrayList<String[]> todayRequest = new ArrayList<>();
            int R = Integer.parseInt(cin.readLine());
            for (int j = 0; j < R; j++) {
                String line = cin.readLine();
                line = line.substring(1, line.length() - 1);
                String[] command = line.split(", ");
                todayRequest.add(command);
            }
            requestList.add(todayRequest);
        }
    }

    public void getTodayData() throws Exception {
        ArrayList<String[]> todayRequest = new ArrayList<>();
        int R = Integer.parseInt(cin.readLine());
        for (int j = 0; j < R; j++) {
            String line = cin.readLine();
            line = line.substring(1, line.length() - 1);
            String[] command = line.split(", ");
            todayRequest.add(command);
        }
        requestList.add(todayRequest);
    }
}
