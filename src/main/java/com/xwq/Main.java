package com.xwq;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Main {
	private static Logger LOG = Logger.getLogger(Main.class);
	private static String baseUrl;
	private static Map<String, String> pagesMap = new HashMap<String, String>();
	
	public static void main(String[] args) {
		start();
	}
	
	private static void start() {
		new File(DownloadFile.storePath).delete();
		
		readConfig();
		downPage(pagesMap, baseUrl);
		deleteSwapFiles();
	}
	
	//读取配置
	public static void readConfig() {
		Properties prop = new Properties();
		try {
			prop.load(Main.class.getClassLoader().getResourceAsStream("site.properties"));
			String urls = prop.getProperty("urls");

			LOG.debug("urls: " + urls);

			for(String url : urls.split(",")) {
				String[] strs = url.split("#");
				pagesMap.put(strs[0], strs[1]);
			}
			
			baseUrl = prop.getProperty("resBasePath");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//下载页面
	public static void downPage(Map<String,String> pagesMap, String resBasePath) {
		DownloadFile.setPagesMap(pagesMap);
		
		for(Map.Entry<String, String> entry : pagesMap.entrySet()) {
			File file = DownloadFile.downloadFile(entry.getKey(), null);
			LOG.info("generate file：" + file.getAbsolutePath());
			
			ParseHtmlContent.parseHtml(file);
			
			
			ParseCssFileContent.setConf(resBasePath);
			ParseCssFileContent.parse();
		}
	}
	
	//删除swap文件
	public static void deleteSwapFiles() {
		File sFile;
		String fileFullName, filename;
		
		//删除css swap文件
		File cssPath = new File(DownloadFile.storePath + "/" + ParseHtmlContent.CssPath);
		for(File file : cssPath.listFiles()) {
			if(file.isFile()) {
				if(file.getName().endsWith(ParseCssFileContent.CssSwap)) {
					fileFullName = file.getAbsolutePath();
					filename = fileFullName.substring(0, fileFullName.lastIndexOf(ParseCssFileContent.CssSwap));
					
					sFile = new File(filename);
					sFile.delete();
					file.renameTo(sFile);
				}
			}
		}
		
		
		//删除html swap文件
		File storePath = new File(DownloadFile.storePath);
		for(File file : storePath.listFiles()) {
			if(file.isFile()) {
				if(file.getName().endsWith(ParseHtmlContent.HtmlSwap)) {   //如果是swap文件
					fileFullName = file.getAbsolutePath();
					filename = fileFullName.substring(0, fileFullName.lastIndexOf(ParseHtmlContent.HtmlSwap));
					
					sFile = new File(filename);
					sFile.delete();
					file.renameTo(sFile);
				}
			}
		}
		
	}
}
