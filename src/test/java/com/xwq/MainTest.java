package com.xwq;

import java.io.File;

import org.apache.log4j.Logger;
import org.junit.Test;

public class MainTest {
	private static File file;
	private static Logger LOG = Logger.getLogger(MainTest.class);
	
	@Test
	public void test() {
		String urlStr = "http://www.meishij.net/";
		file = DownloadFile.downloadFile(urlStr, null);
		LOG.info("generate file：" + file.getAbsolutePath());
		
		ParseHtmlContent.parseHtml(file);
		
		String _baseUrl = "http://css.meishij.net/n/";
		ParseCssFileContent.setConf(_baseUrl);
		
		ParseCssFileContent.parse();
	}
	
	@Test
	public void testParseCssFile() {
		String line = "<link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"http://css.meishij.net/n/css/ss_base.css?v=1517\"/><link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"http://css.meishij.net/n/css/main.css?v=1039\"/>";
		line = ParseHtmlContent.parseCssFile(line);
		System.out.println(line);
	}
	
	@Test
	public void testParseJsFile() {
		String line = "<script type=\"text/javascript\" src=\"http://css.meishij.net/n/js/jquery-1.7.2.min.js\"></script><script type=\"text/javascript\" src=\"http://css.meishij.net/n/js/list.js?v=1108\"></script><script type=\"text/javascript\" src=\"http://css.meishij.net/n/js/main.js?v=1507\"></script>";
		line = ParseHtmlContent.parseJsFile(line);
		System.out.println(line);
	}
	
	@Test
	public void testParseImg() {
		String line = "<img alt=\"粉丝蒸娃娃菜\" src=\"http://images.meishij.net/p/20150623/72bd3e2c29f10c03ae35a4e0071625cc.jpg\" /></a><img alt=\"牛肉蒸面\" src=\"http://images.meishij.net/p/20150525/731982a7a37352cd9cdf355c3ce87aef.jpg\" /></a>";
		line = ParseHtmlContent.parseImg(line);
		System.out.println(line);
	}
	
	@Test
	public void testParseCssFileContent() {
		String _baseUrl = "http://css.meishij.net/n/";
		ParseCssFileContent.setConf(_baseUrl);
		
		ParseCssFileContent.parse();
	}
	
	@Test
	public void testReadConfig() {
		Main.readConfig();
	}
	
	@Test
	public void testDeleteSwapFiles() {
		Main.deleteSwapFiles();
	}
}
