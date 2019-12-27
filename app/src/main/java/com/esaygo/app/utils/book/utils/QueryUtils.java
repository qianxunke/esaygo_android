package com.esaygo.app.utils.book.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.logging.Logger;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import com.esaygo.app.module.query.modle.TrainBean;
import com.esaygo.app.module.query.presenter.QueryPresenter;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class QueryUtils {

	private static Logger logger = Logger.getLogger(QueryUtils.class.getName());

	/**
	 * 查询符合出发站、终点站、出发日期的列车信息的方法
	 *
	 *            用户信息
	 * @return
	 */
	public static List<TrainBean> queryTrainMessage(OkHttpClient okClient, String queryUrl, String startDay, String from_station, String to_station, String purpose_codes) {
		// 拼凑查询列车信息的URL
		Request request = new Request.Builder()
				.url(queryUrl + "?leftTicketDTO.train_date="+startDay +"&leftTicketDTO.from_station="+from_station+"&leftTicketDTO.to_station="+to_station+"&purpose_codes="+ purpose_codes)
				.addHeader("User-Agent",
						"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400")
				.addHeader("Host", "kyfw.12306.cn")
				.addHeader("X-Requested-With", "XMLHttpRequest")
				.addHeader("Accept-Language", "zh-CN,zh;q=0.9")
				.build();

		//System.out.println(queryURL);
		Response response = null;
		List<TrainBean> trainBeanList=new ArrayList<>();
		try {
			response = okClient.newCall(request).execute();
			if (response.isSuccessful()) {
				trainBeanList = QueryPresenter.formatQueryMessage(response.body().string());
			} else {
				logger.info("查询列车信息失败，请检查输入条件...");
				//System.exit(0);
			}
		} catch (Exception e) {
			logger.info("查询列车信息失败，错误信息:[" + e.toString() + "]");
		}
		return trainBeanList;
	}

	//查询满条件的任务并开抢
	/*
	public static TrainBean QueryTicket(TaskDetails.Task task){
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(currentTime);
	}

	 */



}
