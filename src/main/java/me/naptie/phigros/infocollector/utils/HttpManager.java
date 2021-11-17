package me.naptie.phigros.infocollector.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpManager {

	public static HttpURLConnection readUrl(String url, boolean post) throws IOException {
		if (url.contains("zh.moegirl")) {
			if (Math.random() > 0.5) {
				url = url.replace("zh.moegirl", "mzh.moegirl");
			}
		}
		HttpURLConnection request = (HttpURLConnection) (new URL(url)).openConnection();
		String host = url.split("://")[1].split("/")[0];
		request.setRequestProperty("Host", host);
		request.setRequestProperty("Referer", url);
		request.setRequestProperty("User-Agent", UserAgentManager.getRandomUserAgent());
		request.setRequestProperty("Accept", "*/*");
		if (post) {
			request.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
			request.setRequestMethod("POST");
		}
//		System.out.println("正在访问 " + url);
		request.connect();
		return request;
	}

	public static JSONObject readJsonFromUrl(String url, boolean post) throws IOException {
		return JSON.parseObject(IOUtils.toString((InputStream) readUrl(url, post).getContent(), StandardCharsets.UTF_8));
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException {
		return readJsonFromUrl(url, false);
	}
}
