package com.xwq;

import java.io.File;

import org.apache.log4j.Logger;

public class Test {
	private static File file;
	private static Logger LOG = Logger.getLogger(Test.class);
	
	@org.junit.Test
	public void test() {
		String urlStr = "http://www.meishij.net/";
		DownloadFile.setSiteAddr(urlStr);
		file = DownloadFile.downloadFile(urlStr, null);
		LOG.info("generate file：" + file.getAbsolutePath());
		
//		file = new File("D:\\javaWorkspace\\SiteDownload\\generate\\index.html");
		ParseHtml.parseHtml(file);
	}
}
