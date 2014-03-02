package com.ict.twitter.tools.filemonitor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.ict.twitter.WebOperationAjax;
import com.ict.twitter.plantform.LogSys;

public class WebOpLogFlagCallBack implements FileChangeListener{

	public final String variableName="webop.savetofileflag";
	@Override
	public void onchange(String fileName) {
		Properties property=new Properties();
		try {
			property.load(new FileInputStream(fileName));
			boolean flag=Boolean.parseBoolean((String) property.get(variableName));
			WebOperationAjax.debug=flag;
			LogSys.nodeLogger.error("WebOperationAjax.debug="+flag);			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
