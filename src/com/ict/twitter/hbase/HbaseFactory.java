package com.ict.twitter.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HBaseAdmin;

public class HbaseFactory {
	static Configuration conf = null;
	static {
		System.out.println("IMCONFIG");
        conf = HBaseConfiguration.create();
    }
}
