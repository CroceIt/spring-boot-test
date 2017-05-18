package com.springboottest.test;

import com.springboottest.App;
import com.springboottest.service.BaseSensorsDataService;
import com.springboottest.service.SensorsDataService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**  



 *

 * @Title: AppTest.java

 * @Prject: sensors-data

 * @Package: com.springboottest.test

 * @Description: TODO

 * @author: hujunzheng 

 * @date: 2017年4月21日 下午4:50:32

 * @version: V1.0  

 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {App.class})
public class AppTest {
	@Autowired
	private BaseSensorsDataService baseSensorsDataService;
	
	@Autowired
	private SensorsDataService sensorsDataService;

	@Autowired
	private TestConfigure testConfigure;

	@Autowired
	private TestConfigure2 testConfigure2;

	@Autowired
	private TestConfigure3 testConfigure3;

	@Test
	public void test() {

//		sensorsDataService.oneDay("济南", "2017-1-1");

//		sensorsDataService.cityDataPandect("济南", "2016-1-1");

//		baseSensorsDataService.successOrderContent("济南", "2017-5-4", "2017-5-4");

//		baseSensorsDataService.payOrderContent("济南", "2017-1-1", "2017-3-31",   Arrays.asList("event.$Anything.store_id", "event.$Anything.store_name"));
//
// 		sensorsDataService.cityStoreHistoryData("济南", "2017-1-1");

//		sensorsDataService.cityStoreNotAbnormal("济南", "2017-4-25");

//		baseSensorsDataService.realCancelOrderContent("济南", "2017-1-1", "2017-1-2");
// 		baseSensorsDataService.weekAgaginBuyerRate("济南", null,"2016-1-1", "2017-1-1");

//		System.out.println(testConfigure);

//		testConfigure2.function();

		testConfigure3.function();
	}

}
