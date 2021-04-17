package com.huawei.java.main;


public class LinearRegression {
    public double[] results;
    public double[][] features;
    public double[] parameters = new double[]{1, 1, 1, 1};
    public double loss = 0.0;

    private void SGD(double[][] features, double[] results, double learningRate, double[] parameters) {
        for (int j = 0; j < results.length; j++) {
            double gradient = (parameters[0] * features[j][0] + parameters[1] * features[j][1]
                    + parameters[2] * features[j][2] + parameters[3] - results[j]) * features[j][0];
            parameters[0] = parameters[0] - 2 * learningRate * gradient;

            gradient = (parameters[0] * features[j][0] + parameters[1] * features[j][1] + parameters[2] * features[j][2]
                    + parameters[3] - results[j]) * features[j][1];
            parameters[1] = parameters[1] - 2 * learningRate * gradient;

            gradient = (parameters[0] * features[j][0] + parameters[1] * features[j][1] + parameters[2] * features[j][2]
                    + parameters[3] - results[j]) * features[j][2];
            parameters[2] = parameters[2] - 2 * learningRate * gradient;

            gradient = (parameters[0] * features[j][0] + parameters[1] * features[j][1] + parameters[2] * features[j][2]
                    + parameters[3] - results[j]);
            parameters[3] = parameters[3] - 2 * learningRate * gradient;
        }

        double totalLoss = 0;
        for (int j = 0; j < results.length; j++) {
            totalLoss = totalLoss + Math.pow((parameters[0] * features[j][0] + parameters[1] * features[j][1]
                    + parameters[2] * features[j][2] + parameters[3] - results[j]), 2);
        }
//        System.out.println(parameters[0] + " " + parameters[1] + " " + parameters[2] + " " + parameters[3]);
//        System.out.println("totalLoss:" + totalLoss);
        loss = totalLoss;
    }

    private void BGD(double[][] features, double[] results, double learningRate, double[] parameters) {
        double sum = 0;
        for (int j = 0; j < results.length; j++) {
            sum = sum + (parameters[0] * features[j][0] + parameters[1] * features[j][1]
                    + parameters[2] * features[j][2] + parameters[3] - results[j]) * features[j][0];
        }
        double updateValue = 2 * learningRate * sum / results.length;
        parameters[0] = parameters[0] - updateValue;

        sum = 0;
        for (int j = 0; j < results.length; j++) {
            sum = sum + (parameters[0] * features[j][0] + parameters[1] * features[j][1]
                    + parameters[2] * features[j][2] + parameters[3] - results[j]) * features[j][1];
        }
        updateValue = 2 * learningRate * sum / results.length;
        parameters[1] = parameters[1] - updateValue;

        sum = 0;
        for (int j = 0; j < results.length; j++) {
            sum = sum + (parameters[0] * features[j][0] + parameters[1] * features[j][1]
                    + parameters[2] * features[j][2] + parameters[3] - results[j]) * features[j][2];
        }
        updateValue = 2 * learningRate * sum / results.length;
        parameters[2] = parameters[2] - updateValue;

        sum = 0;
        for (int j = 0; j < results.length; j++) {
            sum = sum + (parameters[0] * features[j][0] + parameters[1] * features[j][1]
                    + parameters[2] * features[j][2] + parameters[3] - results[j]);
        }
        updateValue = 2 * learningRate * sum / results.length;
        parameters[3] = parameters[3] - updateValue;

        double totalLoss = 0;
        for (int j = 0; j < results.length; j++) {
            totalLoss = totalLoss + Math.pow((parameters[0] * features[j][0] + parameters[1] * features[j][1]
                    + parameters[2] * features[j][2] + parameters[3] - results[j]), 2);
        }
//        System.out.println(parameters[0] + " " + parameters[1] + " " + parameters[2] + " " + parameters[3]);
        loss = totalLoss;
    }

    public LinearRegression(double[][] features, double[] results) {
        if (features.length != results.length) {
            System.out.println("输入不合法！");
            return;
        }
        this.features = features;
        this.results = results;
        double learningRate = 1E-4;
        for (int i = 0; i < 30000; i++) {
//            BGD(this.features, this.results, learningRate, this.parameters);
            SGD(this.features, this.results, learningRate, parameters);
        }
    }
}