package com.huawei.java.main;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Out {

    public static final PrintWriter cout = new PrintWriter(new OutputStreamWriter(System.out));

    public static void outPutServer(String[] info) {
        for (String x : info) {
            cout.write(x + '\n');
        }
    }

    public static void outPutMigration(ArrayList<String> migrationStr) {
        cout.write("(migration, " + migrationStr.size() + ")\n");
        for (String x : migrationStr) {
            cout.write(x + '\n');
        }

    }

    public static void outPutVm(String[] info) {
        for (String x : info) {
            cout.write(x + '\n');
        }
    }
}
