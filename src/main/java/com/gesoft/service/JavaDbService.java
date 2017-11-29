package com.gesoft.service;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.gesoft.config.Config;
import com.gesoft.thread.TaskThread;
import com.gesoft.utils.ConnectionPool;
import com.gesoft.utils.LogUtil;

public class JavaDbService
{
	
	 
	private Config mConfig = null;
	private ConnectionPool mConnPool = null;
	private int delay = 5;
	private String[] pkgFlys = null;
	private String[] prcFlys = null;

	public JavaDbService(Config mConfig)
	{
		this.mConfig = mConfig;
	}
	
	
	
	public void start()
	{
		this.init();
		this.runThreadPool();
	}
	
	
	private void init()
	{
		try
		{
			System.out.println(mConfig.getProperty("SQLURL"));
			
			pkgFlys = mConfig.getProperty("PKG_CONFIG").split("\\,");
			prcFlys = mConfig.getProperty("PRC_CONFIG").split("\\,");
			
			// 数据库连接对象
			mConnPool = new ConnectionPool("oracle.jdbc.OracleDriver", "jdbc:oracle:thin:@" + mConfig.getProperty("SQLURL") + ":" + mConfig.getProperty("SQLDB"), mConfig.getProperty("SQLOP"), mConfig.getProperty("SQLPASS"));
			LogUtil.getLogger().info("创建数据库连接池");
			mConnPool.createPool();
			LogUtil.getLogger().info("创建数据库连接池完成！");
			
			delay = Integer.parseInt(mConfig.getProperty("EXP_DELAY")); 
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			LogUtil.getLogger().error(e.getMessage());
		}
		
	}
	
	
	private void runThreadPool()
	{
		ScheduledThreadPoolExecutor mScheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(Integer.parseInt(mConfig.getProperty("MAX_THREAD"))); 
		for (String pkg : pkgFlys)
		{
			for (String prc : prcFlys)
			{
				StringBuffer buffer = new StringBuffer();
				buffer.append("{call ")
				.append(pkg)
				.append(".")
				.append(prc)
				.append("}");
				mScheduledThreadPoolExecutor.scheduleWithFixedDelay(new TaskThread(mConnPool, buffer.toString()), 0, delay, TimeUnit.MINUTES);
			}
		}
	}
	
	
}
