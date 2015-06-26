package com.xwq;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import org.apache.log4j.Logger;

public class DownloadFile {
	private static final String storePath = "generate";
	private static String site = "";
	private static Logger LOG = Logger.getLogger(DownloadFile.class);
	
	public static void setSiteAddr(String siteAddr) {
		site = siteAddr;
	}
	
	//下载文件
	public static File downloadFile(String urlStr, String subPath) {
		if(!urlStr.startsWith("http://")) urlStr = site + urlStr;
		if(subPath == null) subPath = "";
		
		URLConnection conn = null;
		File file = null;
		String contentType = "";
		try {
			URL url = new URL(urlStr);
			conn = url.openConnection();
			
			LOG.debug("url:" + urlStr);
			
			contentType = conn.getContentType();
			LOG.debug("contentType: " + contentType);
			
			String filename = getFilename(contentType, urlStr);          //取得文件名
			LOG.debug("filename: " + filename);
			
			file = createFile(filename, storePath + "/" + subPath);		 //创建文件
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return null;
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		//下载文件内容
		if(contentType.contains("image")) {   		//下载图片
			return downImage(conn, file);
		}
		else {										//下载文本文件
			return downText(conn, file);
		}
	}
	
	//根据内容类型获得文件名
	private static String getFilename(String contentType, String urlStr) {
		String filename = "";
		if(contentType.contains("html")) {
			if(urlStr.endsWith("/")) return "index.html";
			
			filename = urlStr.substring(urlStr.lastIndexOf("/")+1);
			if(filename.contains(".")) filename = filename.substring(0, filename.indexOf("."));
			return filename + ".html";
		}
		else if(contentType.contains("css")) {
			urlStr = urlStr.substring(0, urlStr.lastIndexOf(".css")+4);
			filename = urlStr.substring(urlStr.lastIndexOf("/")+1);
			return filename;
		}
		else if(contentType.contains("javascript") || contentType.contains("json")) {
			filename = urlStr.substring(urlStr.lastIndexOf("/")+1);
			return filename;
		}
		else if(contentType.contains("image")) {
			if(!urlStr.endsWith(".jpg") && !urlStr.endsWith(".png") && !urlStr.endsWith(".gif") && !urlStr.endsWith(".ico")) {
				urlStr = getImgFilename(urlStr);
			}
			filename = urlStr.substring(urlStr.lastIndexOf("/")+1);
			
			return filename;
		}
		
		return null;
	}
	
	//截取图片文件名
	public static String getImgFilename(String urlStr) {
		if(urlStr.contains(".jpg")) {
			urlStr = urlStr.substring(0, urlStr.lastIndexOf(".jpg")+4);
		}
		else if(urlStr.contains(".png")) {
			urlStr = urlStr.substring(0, urlStr.lastIndexOf(".png")+4);
		}
		else if(urlStr.contains(".gif")) {
			urlStr = urlStr.substring(0, urlStr.lastIndexOf(".gif")+4);
		}
		else if(urlStr.contains(".ico")) {
			urlStr = urlStr.substring(0, urlStr.lastIndexOf(".ico")+4);
		}
		else {
			urlStr = new Date().getTime() + ".jpg";
		}
		return urlStr;
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
	
	
	//下载文本文件
	private static File downText(URLConnection conn, File file) {
		BufferedReader bufferReader = null;
		PrintWriter writer = null;
		
		try {
			bufferReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
//			System.out.println("filename:" + file.getAbsolutePath());
			
			writer = new PrintWriter(file);
			String line;
			while((line = bufferReader.readLine()) != null) {
				writer.println(line);
			}
			writer.flush();
		} catch(FileNotFoundException e) { 
			e.printStackTrace();
			
			LOG.error("文件不存在：" + conn.getURL().toString(), e);
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
		
		return file;
	}
	
	
	//下载图片
	private static File downImage(URLConnection conn, File file) {
		BufferedInputStream bufferInputStream = null;
		FileOutputStream fos = null;
		
		try {
			bufferInputStream = new BufferedInputStream(conn.getInputStream());
			fos = new FileOutputStream(file);
			
			byte[] buff = new byte[1024];
			while(bufferInputStream.read(buff, 0, buff.length) != -1) {
				fos.write(buff, 0, buff.length);
			}
			fos.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			
			LOG.error("文件不存在：" + conn.getURL().toString(), e);
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(fos != null) fos.close();
				if(bufferInputStream != null) bufferInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return file;
	}
}
