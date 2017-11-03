package com.yahoo.egads.utilities;

import com.yahoo.egads.control.AnalysisResult;
import com.yahoo.egads.data.Anomaly;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ReportingUtils {

    public static void render(AnalysisResult analysisResult) {

        switch (analysisResult.getConfig().getProperty("OUTPUT")) {

            case "ANOMALY_DB":
                throw new NotImplementedException();

            case "GUI":
                GUIUtils.plotResults(analysisResult);
                break;

            case "PLOT":
                for (Anomaly anomaly : analysisResult.getAnomalyList()) {
                    System.out.print(anomaly.toPlotString());
                }
                break;

            default:
                for (Anomaly anomaly : analysisResult.getAnomalyList()) {
                    System.out.print(anomaly.toPerlString());
                }
                break;
        }
    }

}
