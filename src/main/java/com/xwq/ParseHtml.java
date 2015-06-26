package com.xwq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseHtml {
	private static final String cssPath = "css";
	private static final String jsPath = "js";
	private static final String imgPath = "images";
	private static final String swap = ".swap.html";
	
	private static Pattern hrefPattern = Pattern.compile("href=\"([^\"]+)\"");
	private static Pattern srcPattern = Pattern.compile("src=\"([^\"]+)\"");
	private static Pattern datamainPattern = Pattern.compile("data-main=\"([^\"]+)\"");
	private static Matcher matcher;
	
	private static BufferedReader bufferReader;
	private static PrintWriter writer;
	
	
	//处理html文件
	public static void parseHtml(File file) {
		try {
			bufferReader = new BufferedReader(new FileReader(file));
			
			File swapFile = new File(file.getAbsolutePath() + swap);
			if(!swapFile.exists()) swapFile.createNewFile();
			writer = new PrintWriter(swapFile);
			
			String line;
			while((line = bufferReader.readLine()) != null) {
				if(line.contains("rel=\"stylesheet\"")) {
					line = parseCssFile(line);
				}
				
				if(line.contains("<script") && line.contains("src=") && line.contains("</script>") && !line.contains("document.write(")) {
					line = parseJsFile(line);
				}
				
				if(line.contains("<img") && line.contains("src=")) {
					line = parseImg(line);
				}
				
				writer.println(line);
			}
			writer.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
	}
	
	
	//TODO css中import 和 img
	//处理引用的css文件
	private static String parseCssFile(String line) {
		String originLine = line;
		line = line.substring(line.indexOf("<link"));
		line = line.substring(0, line.indexOf(">"));
		
		//style href="..."
		matcher = hrefPattern.matcher(line);
		if(matcher.find()) {
			String cssUrlStr = matcher.group(1);
			
			File cssFile = DownloadFile.downloadFile(cssUrlStr, cssPath);  //下载相应的css文件
			if(cssFile != null) {
				String filename = cssUrlStr.substring(cssUrlStr.lastIndexOf("/")+1);
				originLine = originLine.replace(cssUrlStr, cssPath + "/" + filename);  //把css路径替换成自定义的路径
			}
		}
		
		return originLine;
	}
	
	
	//处理引用js文件
	private static String parseJsFile(String line) {
		String originLine = line;
		line = line.substring(line.indexOf("<script"));
		line = line.substring(0, line.indexOf("</script>"));
		
		//script src="..."
		matcher = srcPattern.matcher(line);
		if(matcher.find()) {
			String jsUrlStr = matcher.group(1);
			
			File jsFile = DownloadFile.downloadFile(jsUrlStr, jsPath);   //下载相应的js文件
			if(jsFile != null) {
				String filename = jsUrlStr.substring(jsUrlStr.lastIndexOf("/")+1);
				originLine = originLine.replace(jsUrlStr, jsPath + "/" + filename);  //把js路径替换成自定义的路径
			}
		}
		
		//script data-main="..."
		matcher = datamainPattern.matcher(line);
		if(matcher.find()) {
			String datamainUrlStr = matcher.group(1);
			
			File jsFile = DownloadFile.downloadFile(datamainUrlStr, jsPath);   //下载相应的js文件
			if(jsFile != null) {
				String filename = datamainUrlStr.substring(datamainUrlStr.lastIndexOf("/")+1);
				originLine = originLine.replace(datamainUrlStr, jsPath + "/" + filename);  //把js路径替换成自定义的路径
			}
		}
		
		return originLine;
	}
	
	
	//处理img图片
	private static String parseImg(String line) {
		String originLine = line;
		line = line.substring(line.indexOf("<img"));
		line = line.substring(0, line.indexOf(">"));
		
		//img src="..."
		matcher = srcPattern.matcher(line);
		if(matcher.find()) {
			String imgUrlStr = matcher.group(1);
			
			File imgFile = DownloadFile.downloadFile(imgUrlStr, imgPath);  //下载相应的图片文件
			
			String filename = "";
			String originImgUrlStr = imgUrlStr;
			
			if(imgFile != null) {
				if(!imgUrlStr.endsWith(".jpg") && !imgUrlStr.endsWith(".png") && !imgUrlStr.endsWith(".gif") && !imgUrlStr.endsWith(".ico")) {
					imgUrlStr = DownloadFile.getImgFilename(imgUrlStr);
				}
				filename = imgUrlStr.substring(imgUrlStr.lastIndexOf("/")+1);
				originLine = originLine.replace(originImgUrlStr, imgPath + "/" + filename);  //把图片路径替换成自定义的路径
			}
		}
		
		return originLine;
	}
}
