package com.gesoft.thread;

import java.sql.CallableStatement;
import java.sql.Connection;

import com.gesoft.utils.ConnectionPool;
import com.gesoft.utils.LogUtil;

public class TaskThread extends Thread
{
	private String sql;
	private ConnectionPool mConnectionPool = null;

	public TaskThread(ConnectionPool mConnectionPool, String sql)
	{
		this.mConnectionPool = mConnectionPool;
		this.sql = sql;
	}
	
	@Override
	public void run()
	{
		Connection conn = null;
		try
		{
			conn = mConnectionPool.getConnection();
			CallableStatement cs = conn.prepareCall(sql);
			cs.execute();
			LogUtil.getLogger().info(String.format("已成功执行-%s", sql));
		}
		catch (Exception e) 
		{
			LogUtil.getLogger().error(e.getMessage());
		}
		finally
		{
			mConnectionPool.returnConnection(conn);
		}
		
	}
}
