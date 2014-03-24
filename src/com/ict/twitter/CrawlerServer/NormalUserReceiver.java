package com.ict.twitter.CrawlerServer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import com.ict.twitter.MessageBus.*;
import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.plantform.PlatFormMain;
import com.ict.twitter.tools.SimpleXmlAnalyser;

public class NormalUserReceiver extends Receiver {

	public NormalUserReceiver(javax.jms.Connection connection,String queue, CrawlerServer _server,
			boolean isTopic) {
		super(connection,isTopic,queue, _server,false,null);

	}

	public void onMessage(Message resmsg) {
		if (isNode) {
			LogSys.crawlerServLogger.debug("【NODE】接收到消息" + resmsg.toString());
		} else {
			//<count>20</count><name>rying_yuqing</name><sum>1000</sum><name>1101860417</name><sum>1000</sum><name>szstupidcool</name><sum>1000</sum><name>zhang1605</name><sum>1000</sum><name>gongyaobin</name><sum>1000</sum><name>duijiudangge</name><sum>1000</sum><name>cmsky1</name><sum>1000</sum><name>_0886608866842</name><sum>1000</sum><name>yf_sky</name><sum>1000</sum><name>xmmksj</name><sum>1000</sum><name>qinlang616</name><sum>1000</sum><name>sble250</name><sum>1000</sum><name>dante1029</name><sum>1000</sum><name>MrNine2012</name><sum>1000</sum><name>onlychujian</name><sum>1000</sum><name>lxl88</name><sum>1000</sum><name>zhangliang2</name><sum>1000</sum><name>fengzhigu</name><sum>1000</sum><name>victorhooo</name><sum>1000</sum><name>shanxizhouyang</name><sum>1000</sum>
			if (resmsg instanceof TextMessage) {
				String str = null;
				try {
					str = ((TextMessage) resmsg).getText();
					LogSys.crawlerServLogger.debug("【Server】接收到新NormalUserID消息" + str);
					if (str.startsWith("<count>")) {
						SimpleXmlAnalyser simxml = new SimpleXmlAnalyser(str);
						String count = simxml.getFirstValueByTag("count");

						if (Integer.parseInt(count) <= 0) {
							
							return;
						}
						String[] users = simxml.getValueByTag("name");
						String[] sums = simxml.getValueByTag("sum");
						if (users.length != sums.length) {
							return;
						} else {
							for (int i = 0; i < users.length; i++) {
								server.addNormalUser(new NormalUser(users[i],Integer.parseInt(sums[i])));
							}
							LogSys.crawlerServLogger.debug("server 端NormalUserSize大小"+server.showNormalUserSize());
						}

					}
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					LogSys.crawlerServLogger.error("当前添加NormalUser消息是"+str);
					e.printStackTrace();
					return;
				}
			}//end if(resmsg TextMessage)
			
		}

	}

}
