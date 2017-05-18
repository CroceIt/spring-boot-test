package com.springboottest.response.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**

 *
 * @Prject: sensors-data
 * @Package: com.springboottest.response.vo
 * @Description: TODO
 * @author: hujunzheng
 * @Date: 2017/5/6 2017/5/6
 * @version: V1.0
 */
public class SensorsDataVO implements Serializable {

    private static final long serialVersionUID = -1079891080519183427L;

    public static class Rows implements Serializable {

        private static final long serialVersionUID = -2255982473671260758L;
        public List<List<BigDecimal>> values;
        public List<Object> byValues;

        public List<List<BigDecimal>> getValues() {
            return values;
        }

        public void setValues(List<List<BigDecimal>> values) {
            this.values = values;
        }

        public List<Object> getByValues() {
            return byValues;
        }

        public void setByValues(List<Object> byValues) {
            this.byValues = byValues;
        }

        @Override
        public String toString() {
            return "rows{" +
                    "values=" + values +
                    ", byValues=" + byValues +
                    '}';
        }
    }

    private List<Rows> rows;
    private List<String> byFields;
    private List<String> series;
    private Long numRows;
    private Boolean truncated;
    private String reportUpdateTime;
    private String dataUpdateTime;
    private String dataSufficientUpdateTime;

    public List<Rows> getRows() {
        return rows;
    }

    public void setRows(List<Rows> rows) {
        this.rows = rows;
    }

    public List<String> getByFields() {
        return byFields;
    }

    public void setByFields(List<String> byFields) {
        this.byFields = byFields;
    }

    public List<String> getSeries() {
        return series;
    }

    public void setSeries(List<String> series) {
        this.series = series;
    }

    public Long getNumRows() {
        return numRows;
    }

    public void setNumRows(Long numRows) {
        this.numRows = numRows;
    }

    public Boolean getTruncated() {
        return truncated;
    }

    public void setTruncated(Boolean truncated) {
        this.truncated = truncated;
    }

    public String getReportUpdateTime() {
        return reportUpdateTime;
    }

    public void setReportUpdateTime(String reportUpdateTime) {
        this.reportUpdateTime = reportUpdateTime;
    }

    public String getDataUpdateTime() {
        return dataUpdateTime;
    }

    public void setDataUpdateTime(String dataUpdateTime) {
        this.dataUpdateTime = dataUpdateTime;
    }

    public String getDataSufficientUpdateTime() {
        return dataSufficientUpdateTime;
    }

    public void setDataSufficientUpdateTime(String dataSufficientUpdateTime) {
        this.dataSufficientUpdateTime = dataSufficientUpdateTime;
    }

    @Override
    public String toString() {
        return "SensorsDataVO{" +
                "byFields=" + byFields +
                ", series=" + series +
                ", numRows=" + numRows +
                ", truncated=" + truncated +
                ", rows=" + rows +
                ", reportUpdateTime='" + reportUpdateTime + '\'' +
                ", dataUpdateTime='" + dataUpdateTime + '\'' +
                ", dataSufficientUpdateTime='" + dataSufficientUpdateTime + '\'' +
                '}';
    }
}
