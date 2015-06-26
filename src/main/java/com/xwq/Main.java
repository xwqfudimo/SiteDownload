package com.xwq;

import java.io.File;

public class Main {
	private static File file;
	
	public static void main(String[] args) throws Exception {
		
//		String urlStr = "http://www.renrenche.com/";
//		file = DownloadFile.downloadFile(urlStr, null);
//		System.out.println(file.getAbsolutePath());
		
		file = new File("D:\\javaWorkspace\\SiteDownload\\generate\\index.html");
		ParseHtml.parseHtml(file);
	}
}
