package Server.util;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;

public class FileOperator {
	
	public static void inputstreamtofile( InputStream ins, File file ) throws IOException {
		
		OutputStream os = new FileOutputStream(file);
		int bytesRead = 0;
		byte[] buffer = new byte[8192];
		while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
			os.write(buffer, 0, bytesRead);
		}
		os.close();
		ins.close();
	}
	
	public static boolean buildRuntimeEnv() {
		
		InputStream is = FileOperator.class.getResourceAsStream("/default.jpg");
		if (is == null) {
			LoggerProvider.logger.error("[ ERROR ] 默认图片打开失败！");
			return false;
		}
		
		File workDir = new File("./portrait");
		if (!workDir.exists()) workDir.mkdir();
		
		File workDefaultDir = new File("./portrait/default");
		if (!workDefaultDir.exists()) workDefaultDir.mkdir();
		
		File workUserDir = new File("./portrait/user");
		if (!workUserDir.exists()) workUserDir.mkdir();
		
		File defaultFile = new File("./portrait/default/default.jpg");
		
		try {
			inputstreamtofile(is, defaultFile);
		} catch (IOException e) {
			LoggerProvider.logger.error("[ ERROR ] 默认图片创建失败！");
			return false;
		}
		
		
		return true;
	}
	
}
