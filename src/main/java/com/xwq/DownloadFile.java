package com.xwq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadFile {
	private static final String storePath = "generate";
	
	//下载文件
	public static File downloadFile(String urlStr, String subPath) {
		if(subPath == null) subPath = "";
		URLConnection conn = null;
		File file = null;
		try {
			URL url = new URL(urlStr);
			conn = url.openConnection();
			
			String contentType = conn.getContentType();
			String filename = getFilename(contentType, urlStr);          //取得文件名
			file = createFile(filename, storePath + "/" + subPath);		 //创建文件
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		//下载文件内容
		BufferedReader bufferReader = null;
		PrintWriter writer = null;
		
		try {
			bufferReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			writer = new PrintWriter(file);
			String line;
			while((line = bufferReader.readLine()) != null) {
				writer.println(line);
			}
		} catch(FileNotFoundException e) { 
			System.err.println("文件不存在：" + urlStr);
			
			//TODO log
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(writer != null) writer.close();
				if(bufferReader != null) bufferReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return file;  //返回文件引用
	}
	
	//根据内容类型获得文件名
	private static String getFilename(String contentType, String urlStr) {
		if(contentType.endsWith("html")) {
			if(urlStr.endsWith("/")) return "index.html";
			
			String filename = urlStr.substring(urlStr.lastIndexOf("/")+1);
			if(filename.contains(".")) filename = filename.substring(0, filename.indexOf("."));
			return filename + ".html";
		}
		else if(contentType.endsWith("css") || contentType.endsWith("javascript") || contentType.endsWith("json")) {
			String filename = urlStr.substring(urlStr.lastIndexOf("/")+1);
			return filename;
		}
		
		return null;
	}
	
	//创建文件
	private static File createFile(String filename, String storePath) throws Exception {
		if(filename == null) throw new RuntimeException("文档类型错误");
		File path = new File(storePath);
		if(!path.exists()) path.mkdir();
		File file = new File(storePath + "/" + filename);
		if(!file.exists()) file.createNewFile();
		
		return file;
	}
}
