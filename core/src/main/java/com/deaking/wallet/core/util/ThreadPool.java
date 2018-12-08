/**
 * Copyright:   北京互融时代软件有限公司
 * @author:      Liu Shilei
 * @version:      V1.0 
 * @Date:        2016年6月8日 下午5:27:28
 */
package com.deaking.wallet.core.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p> TODO</p>
 * @author:         Liu Shilei 
 * @Date :          2016年6月8日 下午5:27:28 
 */
public class ThreadPool {
	
	 private static ExecutorService executors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());  
	 
	 public static ExecutorService getExecutor(){
		 return executors;
	 }
	 
	 public static void exe(Runnable run){
		 executors.execute(run);
	 };

}
