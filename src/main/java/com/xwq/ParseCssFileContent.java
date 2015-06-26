package com.xwq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

//处理css文件中的import和img
public class ParseCssFileContent {
	//相对路径时css文件中基础路径
	private static String baseUrl;
	private static String cssFilePath = DownloadFile.storePath + "/" + ParseHtmlContent.CssPath;
	private static String cssSwap = ".swap.css";
	private static Pattern urlPattern = Pattern.compile("url\\(\"?([^\\)]+\"?)\\)"); 
	private static Matcher matcher;
	private static final String pathFlag = "../";

	private static Logger LOG = Logger.getLogger(ParseCssFileContent.class);
	
	
	public static void setConf(String _baseUrl) {
		baseUrl = _baseUrl;
	}
	
	
	public static void parse() {
		BufferedReader reader = null;
		PrintWriter writer = null;
		
		//处理css路径下各个文件包含url(...)的行
		File path = new File(cssFilePath);
		if(path.exists()) {
			File[] files = path.listFiles();
			for(File cssFile : files) {
				if(!cssFile.getName().endsWith(cssSwap)) {
						try {
							reader = new BufferedReader(new FileReader(cssFile));
							
							File swapFile = new File(cssFile.getAbsoluteFile() + cssSwap);
							if(!swapFile.exists()) swapFile.createNewFile();
							writer = new PrintWriter(swapFile);
							
							LOG.debug(">>>>>>>>>>>>>> filename: " + cssFile.getName());
							
							String line;
							while((line = reader.readLine()) != null) {
								if(line.contains("url")) {
										matcher = urlPattern.matcher(line);
										if(matcher.find()) {
											String url = matcher.group(1);
											if(url.endsWith("\"")) url = url.substring(0, url.length()-1);
											
											String originUrl = url;
											
											if(!url.startsWith("http://")) {
												url = url.replace(pathFlag, baseUrl);
											}
											
											//下载文件并替换行内容
											String filename= "";
											if(url.endsWith(".css")) {
												File file = DownloadFile.downloadFile(url, ParseHtmlContent.CssPath);
												
												if(file != null) {
													filename = url.substring(url.lastIndexOf("/")+1, url.lastIndexOf(".css")+4);
													line = line.replace(originUrl, pathFlag + ParseHtmlContent.CssPath + "/" + filename);
												}
											}
											
											if(url.endsWith(".gif") || url.endsWith(".png") || url.endsWith(".jpg") || url.endsWith(".ico")) {
												File file = DownloadFile.downloadFile(url, ParseHtmlContent.ImgPath);
												
												if(file != null) {
													filename = url.substring(url.lastIndexOf("/")+1);
													line = line.replace(originUrl, pathFlag + ParseHtmlContent.ImgPath + "/" + filename);
												}
											}
											
											writer.println(line);
										}
								}
							}
							writer.flush();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							try {
								if(writer != null) writer.close();
								if(reader != null) reader.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
				}
			}
		}
	}
}
