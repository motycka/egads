/*
 * Copyright 2015, Yahoo Inc.
 * Copyrights licensed under the GPL License.
 * See the accompanying LICENSE file for terms.
 */

// A factory to create tasks based on the data and the config.

package com.yahoo.egads.control;

import com.yahoo.egads.data.TimeSeries;
import com.yahoo.egads.models.adm.AnomalyDetectionAbstractModel;
import com.yahoo.egads.models.tsmm.TimeSeriesAbstractModel;

import java.lang.reflect.Constructor;
import java.util.Properties;

public class ProcessableObjectFactory {

    public static ProcessableObject create(TimeSeries ts, Properties config) {

        if (config.getProperty("OP_TYPE") == null) {
            throw new IllegalArgumentException("OP_TYPE is NULL");
        }

        switch (config.getProperty("OP_TYPE")) {
            case "DETECT_ANOMALY": {
                ModelAdapter ma = ProcessableObjectFactory.buildTSModel(ts, config);
                AnomalyDetector ad = ProcessableObjectFactory.buildAnomalyModel(ts, config);
                return new DetectAnomalyProcessable(ma, ad, config);
            }
            case "UPDATE_MODEL": {
                ModelAdapter ma = ProcessableObjectFactory.buildTSModel(ts, config);
                return new UpdateModelProcessable(ma, ts.data, config);
            }
            case "TRANSFORM_INPUT": {
                ModelAdapter ma = ProcessableObjectFactory.buildTSModel(ts, config);
                return new TransformInputProcessable(ma, config);
            }
            default:
                // Should not be here.
                System.err.println("Unknown OP_TYPE, returning UPDATE_MODEL ProcessableObject");
                ModelAdapter ma = ProcessableObjectFactory.buildTSModel(ts, config);
                return new UpdateModelProcessable(ma, ts.data, config);
        }
    }

    private static ModelAdapter buildTSModel(TimeSeries ts, Properties config) {
        ModelAdapter ma = null;
        try {
            Long period = (long) -1;
            if (config.getProperty("PERIOD") != null) {
              period = new Long(config.getProperty("PERIOD"));
            }
            if (period == 0) {
              if (ts.size() > 1) {
                period = ts.data.get(1).time - ts.data.get(0).time;
              } else {
                period = (long) 1;
              }
            }
            ma = new ModelAdapter(ts, period);
            String modelType = config.getProperty("TS_MODEL");

            Class<?> tsModelClass = Class.forName("com.yahoo.egads.models.tsmm." + modelType);
            Constructor<?> constructor = tsModelClass.getConstructor(Properties.class);
            TimeSeriesAbstractModel m = (TimeSeriesAbstractModel) constructor.newInstance(config);
            ma.addModel(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ma;
    }

    private static AnomalyDetector buildAnomalyModel(TimeSeries ts, Properties config) {
        AnomalyDetector ad = null;
        try {
            Long period = (long) -1;
            if (config.getProperty("PERIOD") != null) {
              period = new Long(config.getProperty("PERIOD"));
            }
            if (period == 0) {
              if (ts.size() > 1) {
                period = ts.data.get(1).time - ts.data.get(0).time;
              } else {
                period = (long) 1;
              }
            }
            ad = new AnomalyDetector(ts, period);
            String modelType = config.getProperty("AD_MODEL");

            Class<?> tsModelClass = Class.forName("com.yahoo.egads.models.adm." + modelType);
            Constructor<?> constructor = tsModelClass.getConstructor(Properties.class);
            ad.addModel((AnomalyDetectionAbstractModel) constructor.newInstance(config));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ad;
    }
}
