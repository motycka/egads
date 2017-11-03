package com.yahoo.egads.control;

import com.yahoo.egads.data.Anomaly;
import com.yahoo.egads.data.TimeSeries;

import java.util.ArrayList;
import java.util.Properties;

public class AnalysisResult {

    private TimeSeries.DataSequence originalData;
    private TimeSeries.DataSequence predictedData;
    private ArrayList<Anomaly> anomalyList;
    private Properties config;

    AnalysisResult(TimeSeries.DataSequence originalData, TimeSeries.DataSequence predictedData, ArrayList<Anomaly> anomalyList, Properties config) {
        this.originalData = originalData;
        this.predictedData = predictedData;
        this.anomalyList = anomalyList;
        this.config = config;
    }

    public TimeSeries.DataSequence getOriginalData() {
        return originalData;
    }

    public TimeSeries.DataSequence getPredictedData() {
        return predictedData;
    }

    public ArrayList<Anomaly> getAnomalyList() {
        return anomalyList;
    }

    public Properties getConfig() {
        return config;
    }
}
