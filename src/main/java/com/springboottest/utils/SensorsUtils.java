package com.springboottest.utils;
/**

 *
 * @Title: SensorsUtils.java
 * @Prject: sensors-data
 * @Package: com.springboottest.utils
 * @Description: TODO
 * @author: hujunzheng
 * @date: 2017年4月21日 下午1:43:25
 * @version: V1.0
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.NameFilter;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.springboottest.exception.ValidationException;
import com.springboottest.response.vo.SensorsDataVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class SensorsUtils {
    // 组装请求数据
    public static final String SA_SERVER_URL = "http://xxx:8007/api/events/report?project=default&token=xxx";
    public static final String SA_QSERVER_URL = "http://xxx:8007/api/sql/query?project=default&token=xxx";

    public static final String THE_EARLY_DATE = "2016-01-01";

    private static final String MEASURES = "measures";
    private static final String UNIT = "unit";
    private static final String FILTER = "filter";
    private static final String BY_FIELDS = "by_fields";
    private static final String BUCKET_PARAMS = "bucket_params";
    private static final String SAMPLING_FACTOR = "sampling_factor";
    private static final String FROM_DATE = "from_date";
    private static final String TO_DATE = "to_date";
    private static final String T_TYPE = "t_type";
    private static final String AXIS_CONFIG = "axis_config";
    private static final String USE_CACHE = "use_cache";
    private static final String EVENT_NAME = "event_name";
    private static final String AGGREGATOR = "aggregator";
    private static final String FIELD = "field";
    private static final String RELATION = "relation";
    private static final String CONDITIONS = "conditions";
    private static final String CONDITIONS_FIELD = "field";
    private static final String CONDITIONS_FUNCTION = "function";
    private static final String CONDITIONS_PARAMS = "params";
    private static final String MEASURE_NAME = "name";
    private static final String MEASURE_EXPRESSION = "expression";
    private static final String MEASURE_EVENTS = "events";
    private static final String MEASURE_FORMAT = "format";
    private static final String LIMIT = "limit";

    /**
     * @ClassName: FilterConditionFunctionName
     * @Description: filter中的condition总的function对应的名称
     * @author: hujunzheng
     * @date: 2017年4月21日 下午4:04:07
     */
    public static class FilterConditionFunctionName {
        public static final String EQUAL = "equal";
        public static final String NOT_EQUAL = "notEqual";
        public static final String IS_TRUE = "isTrue";
        public static final String IS_FALSE = "isFalse";
        public static final String IS_SET = "isSet";
        public static final String NOT_SET = "notSet";
        public static final String INCLUDE = "include";

        public static final String CONTAIN = "contain";
        public static final String NOT_CONTAIN = "notContain";
        public static final String ABSOLUTE_BETWEEN = "absoluteBetween";
        public static final String RELATIVE_BEFORE = "relativeBefore";
        public static final String RELATIVE_WITHIN = "relativeWithin";

        public static final String LIKE = "rlike";
        public static final String NOT_LIKE = "notrlike";
    }

    /**
     * @ClassName: FilterRelationName
     * @Description: filter relation name
     * @author: hujunzheng
     * @date: 2017年4月21日 下午4:15:23
     */
    public static class FilterRelationName {
        public static final String AND = "and";
        public static final String OR = "or";
    }

    /**
     * @ClassName: MeasureAggregatorName
     * @Description: measure aggregtor name
     * @author: hujunzheng
     * @date: 2017年4月21日 下午4:15:37
     */
    public static class MeasureAggregatorName {
        public static final String GENRAL = "general";
        public static final String SUM = "sum";
        public static final String MAX = "max";
        public static final String MIN = "min";
        public static final String AVG = "avg";
        public static final String UNIQ_AVG = "uniqAvg";
        public static final String UNIQUE = "unique";
        public static final String AVERAGE = "average";
    }

    /**
     * @ClassName: MeasureFormatName
     * @Description: measure format name
     * @author: hujunzheng
     * @date: 2017年4月21日 下午4:15:37
     */
    public static class MeasureFormatName{
        public static final String PERCENT = "%p";
        public static final String CEIL = "%d";
        public static final String FLOAT = "%f";
    }

    /**
     * @ClassName: SensorsUnitName
     * @Description: 时间单位
     * @author: hujunzheng
     * @date: 2017年4月21日 下午4:17:28
     */
    public static class SensorsUnitName {
        public static final String HOUR = "hour";
        public static final String DAY = "day";
        public static final String WEEK = "week";
        public static final String MONTH = "month";
    }

    public static final String BASE_API_EVENT_REPORT_SENSORS_JSON = "{\n" + "    \"measures\":[\n" + "    ],\n"
            + "    \"unit\":\"day\",\n" + "    \"filter\":{\n" + "    },\n" + "    \"by_fields\":[\n" + "    ],\n"
            + "    \"sampling_factor\":64,\n" + "    \"from_date\": \"\",\n" + "    \"to_date\": \"\",\n"
            + "    \"t_type\": \"old\",\n" + "    \"axis_config\":{\n" + "    },\n" + "    \"use_cache\":true\n" + "}";

    public static final String BASE_MEASURE_JSON = "{\n" + "    \"event_name\":\"\",\n" + "    \"aggregator\":\"\",\n"
            + "}";

    public static final String BASE_MESURE_JSON2 = "{\n" +
            "    \"expression\": null,\n" +
            "    \"events\":[],\n" +
            "    \"name\": null,\n" +
            "    \"format\":null\n" +
            "}";

    public static final String BASE_FILTER_JSON = "{\n" + "    \"conditions\":[]\n" + "}";

    public static final String BASE_CONDITION_JSON = "{\n" + "    \"field\":\"\",\n" + "    \"function\":\"\",\n"
            + "    \"params\":[\n" + "    ]\n" + "}";

    private static void checkKeyContains(JSONObject json, String key) throws ValidationException {
        if (!json.containsKey(key))
            throw new ValidationException(9999, "json中无 " + key + " 对应的字段");
    }

    private static void checkDate(String date) throws ParseException {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter.parse(date);
        } catch (ParseException e) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            formatter.parse(date);
        }
    }

    /**
     * @param field
     * @param function
     * @param params
     * @return
     * @Title: getCondition
     * @author: hujunzheng
     * @Description: 获取 filter中的condition
     * @return: JSONObject
     */
    public static JSONObject getCondition(String field, String function, List<Object> params) {
        JSONObject condition = JSONObject.parseObject(BASE_CONDITION_JSON);
        if (StringUtils.isEmpty(field))
            throw new RuntimeException("field 不能为空");
        if (function == null)
            throw new RuntimeException("field 不能为null");
        condition.put(CONDITIONS_FIELD, field);
        condition.put(CONDITIONS_FUNCTION, function);
        condition.put(CONDITIONS_PARAMS, params);
        return condition;
    }

    /**
     * @param relation
     * @param conditions
     * @return
     * @Title: getFilter
     * @author: hujunzheng
     * @Description: 获取 filter
     * @return: JSONObject
     */
    public static JSONObject getFilter(String relation, List<JSONObject> conditions) {
        JSONObject filter = JSONObject.parseObject(BASE_FILTER_JSON);

        if (StringUtils.isNotEmpty(relation))
            filter.put(RELATION, relation);
        filter.put(CONDITIONS, conditions);

        return filter;
    }

    /**
     * @param eventName  不为空
     * @param aggregator 不为空
     * @param field
     * @return
     * @Title: getMeasure
     * @author: hujunzheng
     * @Description: 获取 分析measure
     * @return: JSONObject
     */
    public static JSONObject getMeasure(String eventName, String aggregator, String field) {
        if (StringUtils.isEmpty(eventName) || StringUtils.isEmpty(aggregator)) {
            throw new RuntimeException("eventName 和 aggregator 不能为空。");
        }
        JSONObject measure = JSONObject.parseObject(BASE_MEASURE_JSON);

        measure.put(EVENT_NAME, eventName);
        measure.put(AGGREGATOR, aggregator);
        if (StringUtils.isNoneEmpty(field))
            measure.put(FIELD, field);
        return measure;
    }

    public static JSONObject getMeasure(String expression, List<String> events, String name, String format) {
        if(StringUtils.isEmpty(expression)) throw new RuntimeException("expression 不能为空。");
        if(CollectionUtils.isEmpty(events)) throw new RuntimeException("events 不能为空。");
        if(StringUtils.isEmpty(name)) throw new RuntimeException("name 不能为空。");
        if(StringUtils.isEmpty(format)) throw new RuntimeException("format 不能为空。");

        JSONObject measure = JSONObject.parseObject(BASE_MESURE_JSON2);
        measure.put(MEASURE_EXPRESSION, expression);
        measure.put(MEASURE_NAME, name);
        measure.put(MEASURE_FORMAT, format);
        events.forEach(obj->{measure.getJSONArray(MEASURE_EVENTS).add(obj);});
        return measure;
    }

    /**
     * @param requestData
     * @param filter
     * @Title: setFilter
     * @author: hujunzheng
     * @Description: 设置过滤条件
     * @return: void
     */
    public static void setFilter(JSONObject requestData, JSONObject filter) {
        requestData.put(FILTER, filter);
    }

    /**
     * @param requestData
     * @param bucketParams
     * @Title: setBucketParams
     * @author: hujunzheng
     * @Description: 设置桶条件
     * @return: void
     */
    public static void setBucketParams(JSONObject requestData, JSONObject bucketParams) {
        requestData.put(BUCKET_PARAMS, bucketParams);
    }

    /**
     * @param names
     * @param params
     * @return
     * @Title: getBucketParams
     * @author: hujunzheng
     * @Description: 构造 桶条件
     * @return: JSONObject
     */
    public static JSONObject getBucketParams(List<String> names, List<JSONArray> params) {
        if (names.size() != params.size())
            throw new RuntimeException("names 和 params长度不一致");
        JSONObject bucketParams = new JSONObject();
        IntStream.range(0, names.size()).forEach(i -> {
            bucketParams.put(names.get(i), params.get(i));
        });
        return bucketParams;
    }

    /**
     * @param params
     * @return
     * @Title: getBucketParamArray
     * @author: hujunzheng
     * @Description: 构造 getBucketParams 中的参数 params
     * @return: JSONArray
     */
    public static JSONArray getBucketParamArray(Object[] params) {
        JSONArray paramArray = new JSONArray();
        Arrays.asList(params).stream().forEach(object -> {
            paramArray.add(object);
        });
        return paramArray;
    }

    /**
     * @param requestData
     * @param unit
     * @Title: setUnit
     * @author: hujunzheng
     * @Description: 设置时间单元
     * @return: void
     */
    public static void setUnit(JSONObject requestData, String unit) {
        requestData.put(UNIT, unit);
    }

    /**
     * @param requestData
     * @param field
     * @throws ValidationException
     * @Title: addByField
     * @author: hujunzheng
     * @Description: 添加单个分组
     * @return: void
     */
    public static void addByField(JSONObject requestData, String field) throws ValidationException {
        checkKeyContains(requestData, BY_FIELDS);
        requestData.getJSONArray(BY_FIELDS).add(field);
    }

    /**
     * @param requestData
     * @param fields
     * @throws ValidationException
     * @Title: addByFields
     * @author: hujunzheng
     * @Description: 添加多个分组
     * @return: void
     */
    public static void addByFields(JSONObject requestData, List<String> fields) throws ValidationException {
        checkKeyContains(requestData, BY_FIELDS);
        requestData.getJSONArray(BY_FIELDS).addAll(fields);
    }

    /**
     * @return
     * @Title: getBaseRequestData
     * @author: hujunzheng
     * @Description: 获取最基础的神策请求 json
     * @return: JSONObject
     */
    public static JSONObject getBaseRequestData() {
        JSONObject requestData = JSONObject.parseObject(BASE_API_EVENT_REPORT_SENSORS_JSON);
        String date = DateUtils.getTodayDate();
        setOneDay(requestData, date);
        return requestData;
    }

    /**
     * @param requestData
     * @param date
     * @Title: setOneDay
     * @author: hujunzheng
     * @Description: 设置开始和结束的时间 为 某一天
     * @return: void
     */
    public static void setOneDayWithTime(JSONObject requestData, String date) {
        SensorsUtils.setFromDate(requestData, date + " 00:00:00");
        SensorsUtils.setToDate(requestData, date + " 23:59:59");
    }

    public static void setOneDay(JSONObject requestData, String date) {
        SensorsUtils.setFromDate(requestData, date);
        SensorsUtils.setToDate(requestData, date);
    }

    /**
     * @param requestData
     * @Title: setAllDay
     * @author: hujunzheng
     * @Description: 设置最早开始时间 和 最晚结束时间
     * @return: void
     */
    public static void setAllDay(JSONObject requestData) {
        SensorsUtils.setFromDate(requestData, THE_EARLY_DATE);
        SensorsUtils.setToDate(requestData, DateUtils.getTodayDate() + " 23:59:59");
    }

    /**
     * @param requestData
     * @param measure
     * @throws ValidationException
     * @Title: addMeasure
     * @author: hujunzheng
     * @Description: 设置单个 measure
     * @return: void
     */
    public static void addMeasure(JSONObject requestData, JSONObject measure) throws ValidationException {
        checkKeyContains(requestData, MEASURES);
        requestData.getJSONArray(MEASURES).add(measure);
    }

    /**
     * @param requestData
     * @param measures
     * @throws ValidationException
     * @Title: addMeasures
     * @author: hujunzheng
     * @Description: 设置多个 measure
     * @return: void
     */
    public static void addMeasures(JSONObject requestData, List<JSONObject> measures) throws ValidationException {
        checkKeyContains(requestData, MEASURES);
        requestData.getJSONArray(MEASURES).addAll(measures);
    }

    /**
     * @param requestData
     * @param date
     * @Title: setFromDate
     * @author: hujunzheng
     * @Description: 设置开始时间
     * @return: void
     */
    public static void setFromDate(JSONObject requestData, String date) {
        try {
            checkDate(date);
        } catch (Exception e) {
            throw new RuntimeException("日期格式错误。正确格式为：yyyy-MM-dd HH:mm:ss");
        }
        requestData.put(FROM_DATE, date);
    }

    /**
     * @param requestData
     * @param date
     * @Title: setToDate
     * @author: hujunzheng
     * @Description: 设置结束时间
     * @return: void
     */
    public static void setToDate(JSONObject requestData, String date) {
        try {
            checkDate(date);
        } catch (Exception e) {
            throw new RuntimeException("日期格式错误。正确格式为：yyyy-MM-dd HH:mm:ss");
        }
        requestData.put(TO_DATE, date);
    }

    /**
     * @param requestData
     * @param useCache
     * @Title: setUseCache
     * @author: hujunzheng
     * @Description: 设置 启用缓存
     * @return: void
     */
    public static void setUseCache(JSONObject requestData, boolean useCache) {
        requestData.put(USE_CACHE, useCache);
    }

    public static void setAxisConfig(JSONObject requestData, JSONObject config) {
        requestData.put(AXIS_CONFIG, config);
    }

    public static void setSamplingFactor(JSONObject requestData, Integer factor) {
        requestData.put(SAMPLING_FACTOR, factor);
    }

    public static void setTtype(JSONObject requestData, String type) {
        requestData.put(T_TYPE, type);
    }

    public static void setLimit(JSONObject requestData, Integer limit) {
        requestData.put(LIMIT, limit);
    }

    // 解析数据

	/*
     * { "by_fields": [ ], "series": [ ], "rows": [ { "values": [ [ ], [ ] ],
	 * "by_values": [ ] } ], "num_rows": 4 }
	 */

    /**
     * @ClassName: JsonSerializeNameFilter
     * @Description: bo 序列化 对 name 属性值得处理
     * @author: hujunzheng
     * @date: 2017年4月24日 下午2:12:51
     */
    public static class JsonSerializeNameFilter implements NameFilter {
        private String underscoreName(String name) {
            if (StringUtils.isEmpty(name)) {
                return "";
            }
            StringBuilder result = new StringBuilder();
            result.append(name.substring(0, 1).toLowerCase());
            for (int i = 1; i < name.length(); ++i) {
                String s = name.substring(i, i + 1);
                String slc = s.toLowerCase();
                if (!(s.equals(slc))) {
                    result.append("_").append(slc);
                } else {
                    result.append(s);
                }
            }
            return result.toString();
        }

        @Override
        public String process(Object object, String name, Object value) {
            return underscoreName(name);
        }
    }

    /**
     * @ClassName: JsonSerializeNameFilter2
     * @Description: 将属性值 带 下划线的 转换成 驼峰
     * @author: hujunzheng
     * @date: 2017年4月24日 下午2:12:51
     */
    public static class JsonSerializeNameFilter2 implements NameFilter {
        private String withoutUnderscoreName(String name) {
            if (StringUtils.isEmpty(name)) {
                return "";
            }
            StringBuilder result = new StringBuilder();
            result.append(name.substring(0, 1).toLowerCase());
            boolean underscore = false;
            for (int i = 1; i < name.length(); ++i) {
                String s = name.substring(i, i + 1);
                if("_".equals(s)) {
                    underscore = true;
                    continue;
                } else {
                    if(underscore) s = s.toUpperCase();
                    underscore = false;
                }
                result.append(s);
            }
            return result.toString();
        }

        @Override
        public String process(Object object, String name, Object value) {
            return withoutUnderscoreName(name);
        }
    }

    /**
     * @ClassName: JsonSerializePropertyFilter
     * @Description: 对自定义属性进行过滤
     * @author: hujunzheng
     * @date: 2017年4月26日 下午7:40:19
     */
    public static class JsonSerializePropertyFilter implements PropertyFilter {

        @Override
        public boolean apply(Object object, String name, Object value) {
            return !StringUtils.startsWith(name, "self");
        }

    }

    public static final String SERIES = "series";
    public static final String ROWS = "rows";
    public static final String VALUES = "values";
    public static final String BY_VALUES = "by_values";
    public static final String NUM_ROWS = "num_rows";

    public static JSONArray getSeries(JSONObject responseData) {
        return responseData.getJSONArray(SERIES);
    }

    public static JSONArray getByFields(JSONObject responseData) {
        return responseData.getJSONArray(BY_FIELDS);
    }

    public static JSONArray getRows(JSONObject responseData) {
        return responseData.getJSONArray(ROWS);
    }

    public static Integer getNumRows(JSONObject responseData) {
        return responseData.getInteger(NUM_ROWS);
    }

    public static void setNumRows(JSONObject responseData, Integer numRows) { responseData.put(NUM_ROWS, numRows); }

    public static JSONArray getValuesOfRows(JSONObject rows) {
        return rows.getJSONArray(VALUES);
    }

    public static JSONArray getByvaluesOfRows(JSONObject rows) {
        return rows.getJSONArray(BY_VALUES);
    }

    /**
     * @param values
     * @return
     * @Title: sumRowValues
     * @author: hujunzheng
     * @Description: 多个 unit（小时，天，月，年）的值进行合并
     * @return: Optional<Object>
     */
    public static Optional<Object> sumRowValues(JSONArray values) {
        return values.stream().reduce((firstArray, secondArray) -> {
            JSONArray fa = (JSONArray) firstArray;
            JSONArray sa = (JSONArray) secondArray;
            IntStream.range(0, Math.min(sa.size(), fa.size())).forEach(i -> {
                try {
                    sa.set(i, sa.getBigDecimal(i).add(fa.getBigDecimal(i)));
                } catch (NumberFormatException e) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return sa;
        });
    }

    /**
     * @param rows
     * @return
     * @Title: sumRows
     * @author: hujunzheng
     * @Description: rows对应的数组的values 相加
     * @return: Optional<Object>
     */
    public static Optional<Object> sumRows(JSONArray rows) {
        return rows.stream().reduce((firstArray, secondArray) -> {
            JSONObject fo = (JSONObject) firstArray;
            JSONObject so = (JSONObject) secondArray;
            JSONArray foa = fo.getJSONArray(VALUES);
            JSONArray soa = so.getJSONArray(VALUES);

            IntStream.range(0, Math.min(foa.size(), soa.size())).forEach(i -> {
                JSONArray foaa = foa.getJSONArray(i), soaa = soa.getJSONArray(i);
                IntStream.range(0, Math.min(foaa.size(), soaa.size())).forEach(j -> {
                    try {
                        soaa.set(j, soaa.getBigDecimal(j).add(foaa.getBigDecimal(j)));
                    } catch (NumberFormatException e) {
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            });
            return so;
        });
    }

    /**
     * @param num1
     * @param num2
     * @param scale
     * @param rate  乘数需要(1 或者 100)
     * @return
     * @Title: bigDivision
     * @author: hujunzheng
     * @Description: 大数 除法
     * @return: String
     */
    public static String bigDivision(String num1, String num2, int scale, int rate) {
        if (StringUtils.isEmpty(num1)) {
            return "0";
        } else if (StringUtils.isEmpty(num2) || new BigDecimal(num2).compareTo(new BigDecimal(0)) == 0) {
            return "100";
        } else {
            return
                    String.valueOf(new BigDecimal(num1).multiply(new BigDecimal(rate))
                            .divide(new BigDecimal(num2), scale, BigDecimal.ROUND_HALF_UP).doubleValue());
        }
    }

    /**
     * @param data
     * @return
     * @Title: sensorsDataToJsonWithFilter
     * @author: hujunzheng
     * @Description: json 序列化处理
     * @return: String
     */
    public static String sensorsDataToJsonWithFilter(Object data) {
        return JSON.toJSONString(data, new SerializeFilter[]{new SensorsUtils.JsonSerializeNameFilter(), new SensorsUtils.JsonSerializePropertyFilter()}, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullNumberAsZero,
                SerializerFeature.WriteNonStringValueAsString, SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullStringAsEmpty);
    }

    public static String sensorsDataToJsonNoFilter(Object data) {
        return JSON.toJSONString(data, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullNumberAsZero,
                SerializerFeature.WriteNonStringValueAsString, SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullStringAsEmpty);
    }

    /**
     * @param sql
     * @return
     * @Title: getQueryData
     * @author: hujunzheng
     * @Description: 获取查询sql
     * @return: JSONObject
     */
    public static JSONObject getQueryData(String sql) {
        JSONObject queryData = new JSONObject();
        queryData.put("q", sql);
        queryData.put("format", "csv");
        return queryData;
    }

    /**
     * @param template
     * @param arguments
     * @return
     * @Title: templateString
     * @author: hujunzheng
     * @Description: 模板字符串
     * @return: String
     */
    public static String templateString(String template, Object... arguments) {
        return MessageFormat.format(template, arguments);
    }

    /**
     * @MethodName: formatDataToBean
     * @Description: 将神策数据分析
     * @author: hujunzheng
     * @Date: 2017/5/6 下午5:22
     *
     * @Return: com.springboottest.response.vo.SensorsDataVO
     * @Parameter: [responseData]
     */
    public static SensorsDataVO formatDataToBean(String responseData) {
        String formatJson = JSON.toJSONString(JSON.parseObject(responseData), new SensorsUtils.JsonSerializeNameFilter2());
        SensorsDataVO vo = JSON.toJavaObject(JSON.parseObject(formatJson), SensorsDataVO.class);
        return vo;
    }

    /**
     * @MethodName: sensorsDataAdd
     * @Description: 将神策 返回的 多个 数据进行相加（if possible），相加结果是第一个参数
     * @author: hujunzheng
     * @Date: 2017/5/6 下午6:44
     *
     * @Return: void
     * @Parameter: [jsonObject1, jsonObject2]
     */
    public static void sensorsDataAdd(JSONObject jsonObject1, JSONObject jsonObject2) {
        //映射 时间  对应 value 的 index 索引
        Map<String, Integer> jsonObject1MapSeriesToValuesIndex = new HashMap<>(), jsonObject2MapSeriesToValuesIndex = new HashMap<>();

        JSONArray jsonObject1Series = SensorsUtils.getSeries(jsonObject1);
        for (int i = 0; i < jsonObject1Series.size(); ++i) {
            jsonObject1MapSeriesToValuesIndex.put(jsonObject1Series.getString(i), i);
        }

        JSONArray jsonObject2Series = SensorsUtils.getSeries(jsonObject2);
        for (int i = 0; i < jsonObject2Series.size(); ++i) {
            jsonObject2MapSeriesToValuesIndex.put(jsonObject2Series.getString(i), i);
        }

        //将 jsonObject2Series 不在 jsonObject1Series中的 放入 jsonObject1Series
        Set<String> seriesSet = new HashSet<>(jsonObject1MapSeriesToValuesIndex.keySet());
        seriesSet.removeAll(jsonObject2MapSeriesToValuesIndex.keySet());
        if (seriesSet.size() > 0) {
            jsonObject1Series.addAll(seriesSet);
            int jsonObject1SeriesIndex = jsonObject1MapSeriesToValuesIndex.size();
            for (String series : seriesSet) {
                jsonObject1MapSeriesToValuesIndex.put(series, jsonObject1SeriesIndex++);
            }
        }

        JSONArray jsonObject1Rows = SensorsUtils.getRows(jsonObject1);
        JSONArray jsonObject2Rows = SensorsUtils.getRows(jsonObject2);

        Map<Long, JSONObject> mapIdToValues = new HashMap<>();
        jsonObject2Rows.forEach(object -> {
            JSONObject jsonObject = (JSONObject) object;
            JSONArray byValues = SensorsUtils.getByvaluesOfRows(jsonObject);
            mapIdToValues.put(byValues.getLong(0), jsonObject);
        });

        jsonObject1Rows.forEach(object -> {
            JSONObject jsonObject = (JSONObject) object;
            JSONArray jsonObject1Values = SensorsUtils.getValuesOfRows(jsonObject);
            JSONArray byValues = SensorsUtils.getByvaluesOfRows(jsonObject);
            Long id = byValues.getLong(0);
            if (mapIdToValues.containsKey(id)) {
                JSONArray jsonObject2Values = SensorsUtils.getValuesOfRows(mapIdToValues.get(id));
                for (Object series : jsonObject1Series) {
                    if (jsonObject1MapSeriesToValuesIndex.containsKey(series) && jsonObject2MapSeriesToValuesIndex.containsKey(series)) {
                        JSONArray jsonObject1Value = jsonObject1Values.getJSONArray(jsonObject1MapSeriesToValuesIndex.get(series));
                        JSONArray jsonObject2Value = jsonObject2Values.getJSONArray(jsonObject2MapSeriesToValuesIndex.get(series));
                        for (int i = 0; i < jsonObject1Value.size() && i < jsonObject2Value.size(); ++i) {
                            if (SensorsUtils.isDouble(jsonObject1Value.getString(i)) && SensorsUtils.isDouble(jsonObject2Value.getString(i))) {
                                jsonObject1Value.set(i, new BigDecimal(jsonObject1Value.getString(i)).add(new BigDecimal(jsonObject2Value.getString(i))));
                            }
                        }
                    }
                }
                mapIdToValues.remove(id);
            }
        });
        //添加 jsonObject2中不能合并的数据
        jsonObject1Rows.addAll(mapIdToValues.values());
        SensorsUtils.setNumRows(jsonObject1, jsonObject1Rows.size());
    }

    /**
     * @MethodName: sensorsDataSub
     * @Description: 将神策 返回的 多个 数据进行相减（if possible），相减结果为 第一个参数
     * @author: hujunzheng
     * @Date: 2017/5/6 下午6:44
     *
     * @Return: void
     * @Parameter: [jsonObject1, jsonObject2]
     */
    public static void sensorsDataSub(JSONObject jsonObject1, JSONObject jsonObject2) {
        //映射 时间  对应 value 的 index 索引
        Map<String, Integer> jsonObject1MapSeriesToValuesIndex = new HashMap<>(), jsonObject2MapSeriesToValuesIndex = new HashMap<>();

        JSONArray jsonObject1Series = SensorsUtils.getSeries(jsonObject1);
        for (int i = 0; i < jsonObject1Series.size(); ++i) {
            jsonObject1MapSeriesToValuesIndex.put(jsonObject1Series.getString(i), i);
        }

        JSONArray jsonObject2Series = SensorsUtils.getSeries(jsonObject2);
        for (int i = 0; i < jsonObject2Series.size(); ++i) {
            jsonObject2MapSeriesToValuesIndex.put(jsonObject2Series.getString(i), i);
        }

        JSONArray jsonObject1Rows = SensorsUtils.getRows(jsonObject1);
        JSONArray jsonObject2Rows = SensorsUtils.getRows(jsonObject2);

        Map<Long, JSONObject> mapIdToValues = new HashMap<>();
        jsonObject2Rows.forEach(object -> {
            JSONObject jsonObject = (JSONObject) object;
            JSONArray byValues = SensorsUtils.getByvaluesOfRows(jsonObject);
            mapIdToValues.put(byValues.getLong(0), jsonObject);
        });

        jsonObject1Rows.forEach(object -> {
            JSONObject jsonObject = (JSONObject) object;
            JSONArray jsonObject1Values = SensorsUtils.getValuesOfRows(jsonObject);
            JSONArray byValues = SensorsUtils.getByvaluesOfRows(jsonObject);
            Long id = byValues.getLong(0);
            if (mapIdToValues.containsKey(id)) {
                JSONArray jsonObject2Values = SensorsUtils.getValuesOfRows(mapIdToValues.get(id));
                for (Object series : jsonObject1Series) {
                    if (jsonObject1MapSeriesToValuesIndex.containsKey(series) && jsonObject2MapSeriesToValuesIndex.containsKey(series)) {
                        JSONArray jsonObject1Value = jsonObject1Values.getJSONArray(jsonObject1MapSeriesToValuesIndex.get(series));
                        JSONArray jsonObject2Value = jsonObject2Values.getJSONArray(jsonObject2MapSeriesToValuesIndex.get(series));
                        for (int i = 0; i < jsonObject1Value.size() && i < jsonObject2Value.size(); ++i) {
                            if (SensorsUtils.isDouble(jsonObject1Value.getString(i)) && SensorsUtils.isDouble(jsonObject2Value.getString(i))) {
                                jsonObject1Value.set(i, new BigDecimal(jsonObject1Value.getString(i)).subtract(new BigDecimal(jsonObject2Value.getString(i))));
                            }
                        }
                    }
                }
                mapIdToValues.remove(id);
            }
        });
    }

    public static boolean isDouble(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }
}
