package com.mobilesafe.app.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**读取流的工具
 * @author Administrator
 *
 */
public class StreamUtil {
	/**
	 * @param in
	 * @return
	 * @throws IOException 
	 */
	public static String readFromStream(InputStream in) throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int len = 0;
		byte[] buffer = new byte[1024];
		while((len = in.read(buffer)) != -1){
			baos.write(buffer, 0, len);
		}
		String result = baos.toString();
		in.close();
		baos.close();
		return result;
		
	}
}
