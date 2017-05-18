package com.springboottest.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.springboottest.exception.ValidationException;
import com.springboottest.response.ReturnCode;
import com.springboottest.response.SimpleResponse;
import com.springboottest.utils.RestTemplateUtils;
import com.springboottest.utils.SensorsUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**

 *
 * @Title: BaseSensorsDataService.java
 * @Prject: sensors-data
 * @Package: com.springboottest.service
 * @Description: TODO
 * @author: hujunzheng
 * @date: 2017年4月26日 下午12:31:35
 * @version: V1.0
 */

@Service("baseSensorsDataService")
public class BaseSensorsDataService {

    private final String SUBMIT_ORDER_SQL_CONDITION = " and e.store_type='xxxx店' and e.store_status='开启中' and e.event='SubmitOrder' and e.is_refund=0 and e.order_type='xx应用线上下单' and e.service_name not in('xx', 'yy')";

    private void commonSet(String fromDate, String toDate,
                           List<String> fields, JSONObject requestData, Map<String, EventConditionNode> conditionNodeMap) throws ValidationException {
        if (fields != null) {
            for (String field : fields) {
                SensorsUtils.addByField(requestData, field);
                if (field.contains("store_id")) {
                    SensorsUtils.setBucketParams(requestData,
                            SensorsUtils.getBucketParams(Arrays.asList("event.$Anything.store_id"),
                                    Arrays.asList(SensorsUtils.getBucketParamArray(new Object[]{null}))));
                }
            }
        }

        List<JSONObject> filterConditions = new ArrayList<>();


        if (conditionNodeMap != null) {
            for (Map.Entry<String, EventConditionNode> entry : conditionNodeMap.entrySet()) {
                if (StringUtils.isNotEmpty(entry.getKey()) && EventConditionNode.eventNameSet.contains(entry.getKey()) && entry.getValue() != null) {
                    filterConditions.add(SensorsUtils.getCondition(entry.getKey(),
                            entry.getValue().getConditionName(), entry.getValue().getConditionBody()));
                }
            }
        }

        if (CollectionUtils.isNotEmpty(filterConditions)) {
            SensorsUtils.setFilter(requestData,
                    SensorsUtils.getFilter(SensorsUtils.FilterRelationName.AND, filterConditions));
        }


        if (StringUtils.isNotEmpty(fromDate) && StringUtils.isNotEmpty(toDate)) {
            SensorsUtils.setFromDate(requestData, fromDate);
            SensorsUtils.setToDate(requestData, toDate);
        } else {// 默认为当天，这里先设置为 所有天数
            SensorsUtils.setAllDay(requestData);
        }
    }

    /**
     * @return
     * @Title: appAndWap
     * @author: hujunzheng
     * @Description: 根据订单的终端进行查询
     * @return: SimpleResponse
     */
    public SimpleResponse appAndWap(String city, String fromDate, String toDate, List<String> fields) {
        JSONObject requestData = SensorsUtils.getBaseRequestData();
        SimpleResponse response = new SimpleResponse();
        try {
           /* SensorsUtils.addMeasure(requestData, SensorsUtils.getMeasure("PayOrder",
                    SensorsUtils.MeasureAggregatorName.SUM, "event.PayOrder.order_amount"));
            SensorsUtils.addMeasure(requestData,
                    SensorsUtils.getMeasure("PayOrder", SensorsUtils.MeasureAggregatorName.GENRAL, null));*/

            //新修改逻辑
            SensorsUtils.addMeasures(requestData, Arrays.asList(
                    SensorsUtils.getMeasure("sum(event.PayOrder.order_amount)-sum(event.CancelOrder.order_amount)|%2f", Arrays.asList("PayOrder",
                            "CancelOrder"), "支付订单.订单金额.总和-取消订单.订单金额.总和", SensorsUtils.MeasureFormatName.FLOAT),
                    SensorsUtils.getMeasure("count(event.PayOrder)-count(event.CancelOrder)|%d", Arrays.asList("PayOrder",
                            "CancelOrder"), "支付订单.总次数-取消订单.总次数", SensorsUtils.MeasureFormatName.CEIL)
            ));
            //新修改逻辑

            //按照 终端方式 查看
            SensorsUtils.addByField(requestData, "event.$Anything.agent");

            Map<String, EventConditionNode> conditionNodeMap = new HashMap<>();
            if (StringUtils.isNotEmpty(city))
                conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_CITY, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList(city)));
            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_IS_REFUND, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList(0)));
            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_ORDER_TYPE, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList("xx应用线上下单")));
            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_STORE_STATUS, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList("开启中")));

            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_ORDER_STATUS, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList(13, 30, 40)));
            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_STORE_TYPE, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList("xx便利店")));

            commonSet(fromDate, toDate, fields, requestData, conditionNodeMap);

            System.out.println("requestData>>>>>>>>>>>>>>>>>>>>>>" + requestData);

            response.setMsg(
                    RestTemplateUtils.post(SensorsUtils.SA_SERVER_URL, requestData, MediaType.APPLICATION_JSON));
            response.setCode(ReturnCode.SUCCESS);
            System.out.println("终端支付订单数->" + response.getMsg());
        } catch (Exception e) {
            response.setMsg(e.getMessage());
            response.setCode(ReturnCode.FAILURE);
            e.printStackTrace();
        }
        return response;
    }

    /**
     * @MethodName: failOrderContent
     * @Description: 交易失败订单（取消订单不退款 + 取消订单退款 + 没有取消订单退狂）
     * @author: hujunzheng
     * @Date: 2017/5/5 下午12:11
     * @Return: com.springboottest.response.SimpleResponse
     * @Parameter: [city, fromDate, toDate]
     */
    public SimpleResponse failOrderContent(String city, String fromDate, String toDate) {
        SimpleResponse response = new SimpleResponse();

        SimpleResponse cancelOrderContentResponse = this.cancelOrderContent(city, fromDate, toDate, false);
        SimpleResponse refundOrderContentResponse = this.refundOrderContent(city, fromDate, toDate);

        try {
            if (cancelOrderContentResponse.getCode() == ReturnCode.FAILURE.getValue()) {
                throw new RuntimeException(cancelOrderContentResponse.getMsg());
            }

            if (refundOrderContentResponse.getCode() == ReturnCode.FAILURE.getValue()) {
                throw new RuntimeException(refundOrderContentResponse.getMsg());
            }
            JSONObject cancelOrderContentResponseJb = JSONObject.parseObject(cancelOrderContentResponse.getMsg());
            JSONObject refundOrderContentResponseJb = JSONObject.parseObject(refundOrderContentResponse.getMsg());

            SensorsUtils.sensorsDataAdd(cancelOrderContentResponseJb, refundOrderContentResponseJb);

            response.setMsg(cancelOrderContentResponseJb.toJSONString());
            response.setCode(ReturnCode.SUCCESS);

            System.out.println("真实取消订单内容->" + response.getMsg());
        } catch (Exception e) {
            response.setMsg(e.getMessage());
            response.setCode(ReturnCode.FAILURE);
            e.printStackTrace();
        }

        return response;
    }

    /**
     * @param city
     * @param fromDate
     * @param toDate
     * @param refund   是否包含退款(null为全部退款订单， true为取消订单并且退款的， false为取消订单没有退款的)
     * @return
     * @Title: cancelOrderContent
     * @author: hujunzheng
     * @Description: 取消订单内容（不包含取消订单退款）
     * @return: SimpleResponse
     */
    public SimpleResponse cancelOrderContent(String city, String fromDate, String toDate, Boolean refund) {
        JSONObject requestData = SensorsUtils.getBaseRequestData();
        SimpleResponse response = new SimpleResponse();

        try {
            SensorsUtils.addMeasure(requestData,
                    SensorsUtils.getMeasure("CancelOrder", SensorsUtils.MeasureAggregatorName.GENRAL, null));

            Map<String, EventConditionNode> conditionNodeMap = new HashMap<>();
            if (StringUtils.isNotEmpty(city))
                conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_CITY, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList(city)));
            if (refund != null)
                conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_IS_REFUND, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList(refund ? 1 : 0)));
            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_ORDER_TYPE, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList("xx应用线上下单")));
            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_STORE_STATUS, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList("开启中")));
            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_STORE_TYPE, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList("xx便利店")));

            commonSet(fromDate, toDate, Arrays.asList("event.$Anything.store_id", "event.$Anything.store_name"), requestData, conditionNodeMap);

            System.out.println("requestData>>>>>>>>>>>>>>>>>>>>>>" + requestData);

            response.setMsg(
                    RestTemplateUtils.post(SensorsUtils.SA_SERVER_URL, requestData, MediaType.APPLICATION_JSON));
            response.setCode(ReturnCode.SUCCESS);
            System.out.println("取消订单数->" + response.getMsg());
        } catch (Exception e) {
            response.setMsg(e.getMessage());
            response.setCode(ReturnCode.FAILURE);
            e.printStackTrace();
        }

        return response;
    }

    /**
     * @param city
     * @param fromDate
     * @param toDate
     * @return
     * @Title: refundOrderContent
     * @author: hujunzheng
     * @Description: 退款订单内容（取消订单退款+直接退款）
     * @return: SimpleResponse
     */
    public SimpleResponse refundOrderContent(String city, String fromDate, String toDate) {
        JSONObject requestData = SensorsUtils.getBaseRequestData();
        SimpleResponse response = new SimpleResponse();

        try {
            SensorsUtils.addMeasure(requestData,
                    SensorsUtils.getMeasure("RefundOrder", SensorsUtils.MeasureAggregatorName.GENRAL, null));

            Map<String, EventConditionNode> conditionNodeMap = new HashMap<>();
            if (StringUtils.isNotEmpty(city))
                conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_CITY, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList(city)));
            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_ORDER_TYPE, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList("xx应用线上下单")));
            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_STORE_STATUS, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList("开启中")));
            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_STORE_TYPE, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList("xx便利店")));

            commonSet(fromDate, toDate, Arrays.asList("event.$Anything.store_id", "event.$Anything.store_name"), requestData, conditionNodeMap);

            System.out.println("requestData>>>>>>>>>>>>>>>>>>>>>>" + requestData);

            response.setMsg(
                    RestTemplateUtils.post(SensorsUtils.SA_SERVER_URL, requestData, MediaType.APPLICATION_JSON));
            response.setCode(ReturnCode.SUCCESS);
            System.out.println("退款订单数->" + response.getMsg());
        } catch (Exception e) {
            response.setMsg(e.getMessage());
            response.setCode(ReturnCode.FAILURE);
            e.printStackTrace();
        }

        return response;
    }


    /**
     * @param city
     * @param fromDate
     * @param toDate
     * @return
     * @Title: submitOrderContent
     * @author: hujunzheng
     * @Description: 提交订单内容
     * @return: SimpleResponse
     */
    public SimpleResponse submitOrderContent(String city, String fromDate, String toDate) {
        JSONObject requestData = SensorsUtils.getBaseRequestData();
        SimpleResponse response = new SimpleResponse();
        try {
            SensorsUtils.addMeasure(requestData,
                    SensorsUtils.getMeasure("SubmitOrder", SensorsUtils.MeasureAggregatorName.GENRAL, null));
            SensorsUtils.addMeasure(requestData,
                    SensorsUtils.getMeasure("SubmitOrder", SensorsUtils.MeasureAggregatorName.UNIQUE, null));

            Map<String, EventConditionNode> conditionNodeMap = new HashMap<>();
            if (StringUtils.isNotEmpty(city))
                conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_CITY, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList(city)));
            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_ORDER_TYPE, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList("xx应用线上下单")));
            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_STORE_STATUS, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList("开启中")));

            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_STORE_TYPE, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList("xx便利店")));
            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_SERVICE_NAME, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.NOT_EQUAL, Arrays.asList("京选", "增值服务-", "增值服务-快递", "增值服务-洗衣", "快递", "增值服务-鲜花", "水果", "洗衣")));

            commonSet(fromDate, toDate, Arrays.asList("event.$Anything.store_id", "event.$Anything.store_name"), requestData, conditionNodeMap);

            System.out.println("requestData>>>>>>>>>>>>>>>>>>>>>>" + requestData);

            response.setMsg(
                    RestTemplateUtils.post(SensorsUtils.SA_SERVER_URL, requestData, MediaType.APPLICATION_JSON));
            response.setCode(ReturnCode.SUCCESS);
            System.out.println("提交订单数->" + response.getMsg());
        } catch (Exception e) {
            response.setMsg(e.getMessage());
            response.setCode(ReturnCode.FAILURE);
            e.printStackTrace();
        }

        return response;
    }

    /**
     * @param city
     * @param fromDate
     * @param toDate
     * @return
     * @Title: successOrderContent
     * @author: hujunzheng
     * @Description: 成交订单内容
     * @return: SimpleResponse
     */
    public SimpleResponse successOrderContent(String city, String fromDate, String toDate) {
        JSONObject requestData = SensorsUtils.getBaseRequestData();
        SimpleResponse response = new SimpleResponse();
        try {
            SensorsUtils.addMeasure(requestData,
                    SensorsUtils.getMeasure("PayOrder", SensorsUtils.MeasureAggregatorName.GENRAL, null));
            SensorsUtils.addMeasure(requestData,
                    SensorsUtils.getMeasure("PayOrder", SensorsUtils.MeasureAggregatorName.SUM, "event.PayOrder.order_amount"));
            SensorsUtils.addMeasure(requestData,
                    SensorsUtils.getMeasure("PayOrder", SensorsUtils.MeasureAggregatorName.SUM, "event.PayOrder.goods_amount"));

            Map<String, EventConditionNode> conditionNodeMap = new HashMap<>();
            if (StringUtils.isNotEmpty(city)) {
                conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_CITY, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList(city)));
            }
            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_IS_REFUND, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList(0)));
            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_ORDER_TYPE, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList("xx应用线上下单")));
            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_STORE_STATUS, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList("开启中")));

            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_ORDER_STATUS, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList(13, 30, 40)));
            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_STORE_TYPE, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList("xx便利店")));

            commonSet(fromDate, toDate, Arrays.asList("event.$Anything.store_id", "event.$Anything.store_name"), requestData, conditionNodeMap);

            System.out.println("requestData>>>>>>>>>>>>>>>>>>>>>>" + requestData);

            response.setMsg(
                    RestTemplateUtils.post(SensorsUtils.SA_SERVER_URL, requestData, MediaType.APPLICATION_JSON));
            response.setCode(ReturnCode.SUCCESS);
            System.out.println("提交订单数->" + response.getMsg());
        } catch (Exception e) {
            response.setMsg(e.getMessage());
            response.setCode(ReturnCode.FAILURE);
            e.printStackTrace();
        }

        return response;
    }

    /**
     * @param city
     * @param fromDate
     * @param toDate
     * @return
     * @Title: payOrderContent
     * @author: hujunzheng
     * @Description: 支付订单内容，可以添加相关请求属性
     * @return: SimpleResponse
     */
    public SimpleResponse payOrderContent(String city, String fromDate, String toDate, List<String> fields) {
        JSONObject requestData = SensorsUtils.getBaseRequestData();
        SimpleResponse response = new SimpleResponse();
        try {
           /* SensorsUtils.addMeasure(requestData, SensorsUtils.getMeasure("PayOrder",
                    SensorsUtils.MeasureAggregatorName.SUM, "event.PayOrder.order_amount"));
            SensorsUtils.addMeasure(requestData,
                    SensorsUtils.getMeasure("PayOrder", SensorsUtils.MeasureAggregatorName.GENRAL, null));
            SensorsUtils.addMeasure(requestData, SensorsUtils.getMeasure("PayOrder",
                    SensorsUtils.MeasureAggregatorName.SUM, "event.PayOrder.goods_amount"));*/


            //新修改逻辑
            SensorsUtils.addMeasures(requestData, Arrays.asList(
                    SensorsUtils.getMeasure("sum(event.PayOrder.order_amount)-sum(event.CancelOrder.order_amount)|%2f", Arrays.asList("PayOrder",
                            "CancelOrder"), "支付订单.订单金额.总和-取消订单.订单金额.总和", SensorsUtils.MeasureFormatName.FLOAT),
                    SensorsUtils.getMeasure("count(event.PayOrder)-count(event.CancelOrder)|%d", Arrays.asList("PayOrder",
                            "CancelOrder"), "支付订单.总次数-取消订单.总次数", SensorsUtils.MeasureFormatName.CEIL),
                    SensorsUtils.getMeasure("sum(event.PayOrder.goods_amount)-sum(event.CancelOrder.goods_amount)|%2f", Arrays.asList("PayOrder",
                            "CancelOrder"), "支付订单.订单商品金额.总和-取消订单.订单商品金额.总和", SensorsUtils.MeasureFormatName.FLOAT)
            ));
            //新修改逻辑

            Map<String, EventConditionNode> conditionNodeMap = new HashMap<>();
            if (StringUtils.isNotEmpty(city))
                conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_CITY, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList(city)));
            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_IS_REFUND, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList(0)));
            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_ORDER_TYPE, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList("xx应用线上下单")));
            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_STORE_STATUS, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList("开启中")));

            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_ORDER_STATUS, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList(13, 20, 21, 25, 30, 40)));
            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_STORE_TYPE, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList("xx便利店")));
            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_SERVICE_NAME, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.NOT_EQUAL, Arrays.asList("京选", "增值服务-", "增值服务-快递", "增值服务-洗衣", "快递", "增值服务-鲜花", "水果", "洗衣")));

            commonSet(fromDate, toDate, fields, requestData, conditionNodeMap);

            System.out.println("requestData>>>>>>>>>>>>>>>>>>>>>>" + requestData);

            response.setMsg(
                    RestTemplateUtils.post(SensorsUtils.SA_SERVER_URL, requestData, MediaType.APPLICATION_JSON));
            response.setCode(ReturnCode.SUCCESS);
            System.out.println("支付订单数->" + response.getMsg());
        } catch (Exception e) {
            response.setMsg(e.getMessage());
            response.setCode(ReturnCode.FAILURE);
            e.printStackTrace();
        }
        return response;
    }

    /**
     * @param city
     * @param fromDate
     * @param toDate
     * @return
     * @Title: weekAgaginBuyerRate2
     * @author: hujunzheng
     * @Description: 计算某个城市下 各个店铺的 周复购率
     * @return: SimpleResponse
     */
    public SimpleResponse weekAgaginBuyerRate2(String city, String fromDate, String toDate) {
        SimpleResponse response = new SimpleResponse();
        try {
            String sqlTempalte = "select e.store_id, count(e.distinct_id) from events e inner join users u on e.distinct_id = u.first_id " +
                    " where e.time >= {0} and e.time <= {1} "
                    + ((city != null) ? " and e.$city={2} " : " ")
                    + " {3} "
                    + " group by e.store_id, e.distinct_id";

            String sql = SensorsUtils.templateString(sqlTempalte, "'" + fromDate + "'", "'" + toDate + "'",
                    "'" + city + "'", SUBMIT_ORDER_SQL_CONDITION);
            JSONObject requestData = SensorsUtils.getQueryData(sql);
            System.out.println("weekAgaginBuyerRate2周复购率request ->" + requestData.toString());
            String result = RestTemplateUtils.post(SensorsUtils.SA_QSERVER_URL, requestData,
                    MediaType.APPLICATION_FORM_URLENCODED);

            Map<Long, Long> storeRepateCnt = new HashMap<>(), storeSingleCnt = new HashMap<>();
            Set<Long> storeIds = new HashSet<>();
            if(StringUtils.isNotEmpty(result)) {
                try (Scanner scan = new Scanner(result)) {
                    if (scan.hasNext())
                        scan.next();

                    while (scan.hasNextLine()) {
                        String content = scan.nextLine();
                        if (content.matches("\\d+\\s\\d+")) {
                            String[] contents = content.split("\\s");
                            Long storeId = Long.valueOf(contents[0]);
                            storeIds.add(storeId);
                            if (Long.valueOf(contents[1]) == 1) {
                                if (!storeSingleCnt.containsKey(storeId)) storeSingleCnt.put(storeId, 1L);
                                storeSingleCnt.put(storeId, storeSingleCnt.get(storeId) + 1);
                            } else {
                                if (!storeRepateCnt.containsKey(storeId)) storeRepateCnt.put(storeId, 1L);
                                storeRepateCnt.put(storeId, storeRepateCnt.get(storeId) + 1);
                            }
                        }
                    }
                }
            }

            JSONArray msgData = new JSONArray();
            for (Long storeId : storeIds) {
                JSONObject obj = new JSONObject();
                obj.put("storeId", storeId);
                String repeatCnt = storeRepateCnt.get(storeId) != null ? storeRepateCnt.get(storeId).toString() : null;
                String singleCnt = storeSingleCnt.get(storeId) != null ? storeSingleCnt.get(storeId).toString() : null;
                obj.put("repeatRate", SensorsUtils.bigDivision(repeatCnt, singleCnt, 2, 100) + "%");
                msgData.add(obj);
            }

            response.setMsg(msgData.toJSONString());
            response.setCode(ReturnCode.SUCCESS);
            System.out.println("weekAgaginBuyerRate2周复购率内容 ->" + response.getMsg());
        } catch (Exception e) {
            response.setMsg(e.getMessage());
            response.setCode(ReturnCode.FAILURE);
            e.printStackTrace();
        }
        return response;
    }

    /**
     * @param city
     * @param store
     * @param fromDate
     * @param toDate
     * @return
     * @Title: weekAgaginBuyerRate
     * @author: hujunzheng
     * @Description: 计算周复购率(某个城市的复购率或者某个城市下的某个店铺的复购率)
     * @return: SimpleResponse
     */
    public SimpleResponse weekAgaginBuyerRate(String city, String store, String fromDate, String toDate) {
        SimpleResponse response = new SimpleResponse();
        try {
            String sqlTempalte = "select count(*) from events e inner join users u on e.distinct_id = u.first_id"
                    + " where e.time >= {0} and e.time <= {1} "
                    + ((city != null) ? " and e.$city={2} " : " ") + ((store != null) ? " and e.store_id={3} " : " ")
                    + " {4} "
                    + " group by e.distinct_id";
            String sql = SensorsUtils.templateString(sqlTempalte, "'" + fromDate + "'", "'" + toDate + "'",
                    "'" + city + "'", "'" + store + "'", SUBMIT_ORDER_SQL_CONDITION);
            JSONObject requestData = SensorsUtils.getQueryData(sql);
            System.out.println("weekAgaginBuyerRate周复购率request ->" + requestData.toString());
            String result = RestTemplateUtils.post(SensorsUtils.SA_QSERVER_URL, requestData,
                    MediaType.APPLICATION_FORM_URLENCODED);

            Long repeatCount = 0L, singleCount = 0L;
            if(StringUtils.isNotEmpty(result)) {
                try (Scanner scan = new Scanner(result)) {
                    if (scan.hasNext())
                        scan.next();

                    while (scan.hasNextLong()) {
                        if (scan.nextLong() > 1) {
                            ++repeatCount;
                        } else {
                            ++singleCount;
                        }
                    }
                }
            }

            response.setMsg(SensorsUtils.bigDivision(repeatCount.toString(), singleCount.toString(), 2, 100) + "%");
            response.setCode(ReturnCode.SUCCESS);
            System.out.println("weekAgaginBuyerRate周复购率内容 ->" + response.getMsg());
        } catch (Exception e) {
            response.setMsg(e.getMessage());
            response.setCode(ReturnCode.FAILURE);
            e.printStackTrace();
        }
        return response;
    }

    /**
     * @param city
     * @param fromDate
     * @param toDate
     * @return
     * @Title: rankContent
     * @author: hujunzheng
     * @Description: 排行内容，可以添加相关请求属性
     * @return: SimpleResponse
     */
    public SimpleResponse rankContent(String city, String fromDate, String toDate) {
        JSONObject requestData = SensorsUtils.getBaseRequestData();
        SimpleResponse response = new SimpleResponse();
        try {
            /*SensorsUtils.addMeasure(requestData, SensorsUtils.getMeasure("PayOrder",
                    SensorsUtils.MeasureAggregatorName.SUM, "event.PayOrder.goods_amount"));*/

            //新逻辑修改
            SensorsUtils.addMeasure(requestData,
                    SensorsUtils.getMeasure("sum(event.PayOrder.goods_amount)-sum(event.CancelOrder.goods_amount)|%2f", Arrays.asList("PayOrder",
                            "CancelOrder"), "支付订单.订单金额.总和-取消订单.订单金额.总和", SensorsUtils.MeasureFormatName.FLOAT));
            //新逻辑修改

            Map<String, EventConditionNode> conditionNodeMap = new HashMap<>();
            if (StringUtils.isNotEmpty(city))
                conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_CITY, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList(city)));
            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_IS_REFUND, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList(0)));
            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_ORDER_TYPE, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList("xx应用线上下单")));
            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_STORE_STATUS, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList("开启中")));

            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_ORDER_STATUS, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList(13, 20, 21, 25, 30, 40)));
            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_STORE_TYPE, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.EQUAL, Arrays.asList("xx便利店")));
            conditionNodeMap.put(EventConditionNode.EVENT_ANYTHING_SERVICE_NAME, new EventConditionNode(SensorsUtils.FilterConditionFunctionName.NOT_EQUAL, Arrays.asList("京选", "增值服务-", "增值服务-快递", "增值服务-洗衣", "快递", "增值服务-鲜花", "水果", "洗衣")));

            commonSet(fromDate, toDate, Arrays.asList("event.$Anything.store_id", "event.$Anything.store_name"), requestData, conditionNodeMap);

            System.out.println("requestData>>>>>>>>>>>>>>>>>>>>>>" + requestData);

            response.setMsg(
                    RestTemplateUtils.post(SensorsUtils.SA_SERVER_URL, requestData, MediaType.APPLICATION_JSON));
            response.setCode(ReturnCode.SUCCESS);
            System.out.println("昨日rank内容->" + response.getMsg());
        } catch (Exception e) {
            response.setMsg(e.getMessage());
            response.setCode(ReturnCode.FAILURE);
            e.printStackTrace();
        }

        return response;
    }
}

class EventConditionNode {
    public static final String EVENT_ANYTHING_ORDER_STATUS = "event.$Anything.order_status";
    public static final String EVENT_ANYTHING_STORE_STATUS = "event.$Anything.store_status";
    public static final String EVENT_ANYTHING_STORE_TYPE = "event.$Anything.store_type";
    public static final String EVENT_ANYTHING_ORDER_TYPE = "event.$Anything.order_type";
    public static final String EVENT_ANYTHING_SERVICE_NAME = "event.$Anything.service_name";
    public static final String EVENT_ANYTHING_IS_REFUND = "event.$Anything.is_refund";
    public static final String EVENT_ANYTHING_CITY = "event.$Anything.$city";

    public static final Set<String> eventNameSet = new HashSet<>();

    static {
        Class<?> cls = EventConditionNode.class;
        Field[] fields = cls.getFields();

        try {
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.getType().toString().endsWith("java.lang.String") &&
                        Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                    eventNameSet.add((String) field.get(EventConditionNode.class));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String conditionName;
    private List<Object> conditionBody;

    public EventConditionNode(String conditionName, List<Object> conditionBody) {
        this.conditionName = conditionName;
        this.conditionBody = conditionBody;
    }

    public String getConditionName() {
        return conditionName;
    }

    public void setConditionName(String conditionName) {
        this.conditionName = conditionName;
    }

    public List<Object> getConditionBody() {
        return conditionBody;
    }

    public void setConditionBody(List<Object> conditionBody) {
        this.conditionBody = conditionBody;
    }
}