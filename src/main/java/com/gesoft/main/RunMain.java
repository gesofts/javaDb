package com.gesoft.main;

import java.io.File;

import com.gesoft.config.Config;
import com.gesoft.service.JavaDbService;

public class RunMain
{
	private Config mConfig = null;
	
	public static void main(String[] args)
	{
		RunMain mRunMain = new RunMain();
		mRunMain.startServer();
	}
	
	public RunMain()
	{
		String rootpath = System.getProperty("user.dir");
		Config.setConfigFileName(rootpath+File.separatorChar+"app.properties");
		mConfig = Config.getInstance();
	}
	
	public void startServer()
	{
		JavaDbService mJavaDbService = new JavaDbService(mConfig);
		mJavaDbService.start();
	}
	
}
