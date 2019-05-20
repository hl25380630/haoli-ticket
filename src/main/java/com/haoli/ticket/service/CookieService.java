package com.haoli.ticket.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.springframework.stereotype.Service;

import com.haoli.sdk.web.util.TimeUtil;

import net.sourceforge.jdpapi.DataProtector;


@Service
public class CookieService {
	
	static {
		System.load("C:\\Users\\10063731\\Desktop\\jdpapi-1.0.1\\jdpapi-native-1.0.1.dll");
	}
	
	static Connection conn = null;
	static String dbPath = "C:\\Users\\10063731\\AppData\\Local\\Google\\Chrome\\User Data\\Profile 1\\";
	static String dbName = dbPath + "Cookies";
	
	private final DataProtector protector;
	
	public static void main(String[] args)throws Exception {
		CookieService cs = new CookieService();
		cs.getCookieList();
	}
 
	
	public List<Cookie> getCookieList() throws Exception {
		CookieService testCoolies = new CookieService();
		Class.forName("org.sqlite.JDBC");
		conn = DriverManager.getConnection("jdbc:sqlite:" + dbName, null, null);
		conn.setAutoCommit(false);
		Statement stmt = conn.createStatement();
		stmt.setQueryTimeout(3); 
		String sql = String.format("select name,host_key,path,encrypted_value, expires_utc from cookies where host_key like '%%damai%%'");
		ResultSet rs = stmt.executeQuery(sql);
		List<Cookie> cookieList = new ArrayList<Cookie>();
		while (rs.next()) {
			String name = rs.getString("name");
			String domain = rs.getString("host_key");
			String path = rs.getString("path");
			String expiresUtc = rs.getString("expires_utc");
			Long expiresUtcLong = Long.valueOf(expiresUtc);
			Long unixTimeStamp = 0L;
			String unixTimeStampStr = "";
			if(expiresUtcLong == 0L) {
				unixTimeStampStr = null;
			}else {
				unixTimeStamp = expiresUtcLong/1000000 - 11644473600L;
				unixTimeStampStr = String.valueOf(unixTimeStamp);
			}
			InputStream inputStream = rs.getBinaryStream("encrypted_value");
			
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			int ch;
			while ((ch = inputStream.read()) != -1) {
				byteArrayOutputStream.write(ch);
			}
			byte[] b = byteArrayOutputStream.toByteArray();
			String value = testCoolies.decrypt(b);
			byteArrayOutputStream.close();
			System.out.println("name: " + name);
			System.out.println("value: " + value);
			System.out.println("domain: " + domain);
			System.out.println("path: " + path);
			System.out.println("expries: " + TimeUtil.timeStamp10ToSimpleDateFormat(unixTimeStampStr));
			System.out.println("");
			
		    BasicClientCookie cookie = new BasicClientCookie(name, value);
		    cookie.setDomain(domain);
		    cookie.setPath(path);
		    cookie.setExpiryDate(TimeUtil.timeStamp10ToDate(unixTimeStampStr));
			cookieList.add(cookie);
		}
		rs.close();
		conn.close();
		return cookieList;
	}
 
	public CookieService() {
		this.protector = new DataProtector();
	}
 
	private String decrypt(byte[] data) {
		return protector.unprotect(data);
	}
 
}
