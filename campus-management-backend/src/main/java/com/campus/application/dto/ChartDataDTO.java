package com.campus.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 图表数据传输对象
 * 用于封装各种图表显示的数据
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-18
 */
public class ChartDataDTO {

    // 图表基本信息
    private String chartType;  // 图表类型：line, bar, pie, area等
    private String title;      // 图表标题
    private String subtitle;   // 图表副标题
    private String description; // 图表描述

    // 简化的数据点（用于测试兼容性）
    private String label;      // 数据标签
    private Object value;      // 数据值

    // 数据系列
    private List<DataSeries> series;
    
    // 坐标轴配置
    private AxisConfig xAxis;
    private AxisConfig yAxis;
    
    // 图表配置
    private Map<String, Object> options;
    
    // 时间信息
    private LocalDateTime dataTime;
    private String timeRange;
    
    // 数据统计
    private Long totalDataPoints;
    private Object maxValue;
    private Object minValue;
    private Object avgValue;

    // 构造函数
    public ChartDataDTO() {
        this.dataTime = LocalDateTime.now();
    }

    public ChartDataDTO(String chartType, String title) {
        this();
        this.chartType = chartType;
        this.title = title;
    }

    public ChartDataDTO(String label, Object value) {
        this();
        this.label = label;
        this.value = value;
    }

    public ChartDataDTO(String label, Object value, String color) {
        this(label, value);
        // 可以将color存储在options中或者扩展字段
        if (this.options == null) {
            this.options = new java.util.HashMap<>();
        }
        this.options.put("color", color);
    }

    // 数据系列内部类
    public static class DataSeries {
        private String name;
        private String type;
        private List<Object> data;
        private String color;
        private Map<String, Object> style;

        public DataSeries() {}

        public DataSeries(String name, List<Object> data) {
            this.name = name;
            this.data = data;
        }

        // Getter 和 Setter
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<Object> getData() {
            return data;
        }

        public void setData(List<Object> data) {
            this.data = data;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public Map<String, Object> getStyle() {
            return style;
        }

        public void setStyle(Map<String, Object> style) {
            this.style = style;
        }
    }

    // 坐标轴配置内部类
    public static class AxisConfig {
        private String title;
        private String type;  // category, value, time, log
        private List<String> categories;
        private Object min;
        private Object max;
        private String format;

        public AxisConfig() {}

        public AxisConfig(String title, String type) {
            this.title = title;
            this.type = type;
        }

        // Getter 和 Setter
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<String> getCategories() {
            return categories;
        }

        public void setCategories(List<String> categories) {
            this.categories = categories;
        }

        public Object getMin() {
            return min;
        }

        public void setMin(Object min) {
            this.min = min;
        }

        public Object getMax() {
            return max;
        }

        public void setMax(Object max) {
            this.max = max;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }
    }

    // 主类的 Getter 和 Setter 方法

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<DataSeries> getSeries() {
        return series;
    }

    public void setSeries(List<DataSeries> series) {
        this.series = series;
    }

    public AxisConfig getxAxis() {
        return xAxis;
    }

    public void setxAxis(AxisConfig xAxis) {
        this.xAxis = xAxis;
    }

    public AxisConfig getyAxis() {
        return yAxis;
    }

    public void setyAxis(AxisConfig yAxis) {
        this.yAxis = yAxis;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

    public LocalDateTime getDataTime() {
        return dataTime;
    }

    public void setDataTime(LocalDateTime dataTime) {
        this.dataTime = dataTime;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

    public Long getTotalDataPoints() {
        return totalDataPoints;
    }

    public void setTotalDataPoints(Long totalDataPoints) {
        this.totalDataPoints = totalDataPoints;
    }

    public Object getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Object maxValue) {
        this.maxValue = maxValue;
    }

    public Object getMinValue() {
        return minValue;
    }

    public void setMinValue(Object minValue) {
        this.minValue = minValue;
    }

    public Object getAvgValue() {
        return avgValue;
    }

    public void setAvgValue(Object avgValue) {
        this.avgValue = avgValue;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ChartDataDTO{" +
                "chartType='" + chartType + '\'' +
                ", title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", dataTime=" + dataTime +
                ", totalDataPoints=" + totalDataPoints +
                '}';
    }
}
