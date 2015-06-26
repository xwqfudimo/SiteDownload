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
	private static final String swap = ".swap";
	
	private static Pattern cssPattern = Pattern.compile("href=\"([\\w:/.-]+)\"");
	private static Pattern jsPattern = Pattern.compile("src=\"([\\w:/.-]+)\"");
	private static Pattern datamainPattern = Pattern.compile("data-main=\"([\\w:/.-]+)\"");
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
//					writer.println(line);
				}
				
				if(line.contains("<script") && line.contains("src=") && !line.contains("document.write(")) {
					line = parseJsFile(line);
//					writer.println(line);
				}
				
//				writer.println(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
		}
	}
	
	
	//TODO css中import 和 img
	//处理引用的css文件
	private static String parseCssFile(String line) {
		String originLine = line;
		line = line.substring(line.indexOf("<link"));
		line = line.substring(0, line.indexOf(">"));
		
		//href="..."
		matcher = cssPattern.matcher(line);
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
		
		//src="..."
		matcher = jsPattern.matcher(line);
		if(matcher.find()) {
			String jsUrlStr = matcher.group(1);
			
			File jsFile = DownloadFile.downloadFile(jsUrlStr, jsPath);   //下载相应的js文件
			if(jsFile != null) {
				String filename = jsUrlStr.substring(jsUrlStr.lastIndexOf("/")+1);
				originLine = originLine.replace(jsUrlStr, jsPath + "/" + filename);  //把js路径替换成自定义的路径
			}
		}
		
		//data-main="..."
		matcher = datamainPattern.matcher(line);
		if(matcher.find()) {
			String datamainUrlStr = matcher.group(1);
			DownloadFile.downloadFile(datamainUrlStr, jsPath);   //下载相应的js文件
			
			String filename = datamainUrlStr.substring(datamainUrlStr.lastIndexOf("/")+1);
			originLine = originLine.replace(datamainUrlStr, jsPath + "/" + filename);  //把js路径替换成自定义的路径
		}
		
		return originLine;
	}
}
