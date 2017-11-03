/*
 * Copyright 2015, Yahoo Inc.
 * Copyrights licensed under the GPL License.
 * See the accompanying LICENSE file for terms.
 */

// A template for doing Anomaly Detection.

package com.yahoo.egads.control;

import com.yahoo.egads.data.Anomaly;
import com.yahoo.egads.data.TimeSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DetectAnomalyProcessable implements ProcessableObject {

    private ModelAdapter ma;
    private AnomalyDetector ad;
    private Properties config;
    private ArrayList<Anomaly> anomalyList;

    private List<AnalysisResult> analysisResults = new ArrayList<>();

    @Override
    public List<AnalysisResult> getAnalysisResults() {
        return this.analysisResults;
    }

    DetectAnomalyProcessable(ModelAdapter ma, AnomalyDetector ad, Properties config) {
        this.ma = ma;
        this.ad = ad;
        this.config = config;
        anomalyList = new ArrayList<>();
    }

    public void process() throws Exception {

        // Resetting the models
        ma.reset();

        // Training the model with the whole metric
        ma.train();

        // Finding the expected values
        ArrayList<TimeSeries.DataSequence> list = ma.forecast(
                ma.metric.startTime(), ma.metric.lastTime());

        // For each model's prediction in the ModelAdapter

        for (TimeSeries.DataSequence ds : list) {
            // Reseting the anomaly detectors
            ad.reset();

            // Unsupervised tuning of the anomaly detectors
            ad.tune(ds, null);

            // Detecting anomalies for each anomaly detection model in anomaly detector
            anomalyList = ad.detect(ad.metric, ds);

            AnalysisResult analysisResult = new AnalysisResult(ma.metric.data, ds, anomalyList, config);
            analysisResults.add(analysisResult);

        }
    }

    public ArrayList<Anomaly> result() throws Exception {
        return anomalyList;
    }
}
