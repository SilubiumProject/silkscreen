/**
 * Copyright:   北京互融时代软件有限公司
 * @author:      Liu Shilei
 * @version:      V1.0 
 * @Date:        2015年11月4日 下午1:22:49
 */

import java.io.FileReader;
import java.util.Properties;

/**
 * <p>
 * TODO
 * </p>
 * 
 * @author: Liu Shilei
 * @Date : 2015年11月4日 下午1:22:49
 */
public class PropertiesUtils {

	public static Properties APP = null;

	
	static {
		APP = new Properties();
		try {
			APP.load(new FileReader(PropertiesUtils.class
					.getClassLoader()
					.getResource("application-prod.properties")
					.getPath()));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
}
