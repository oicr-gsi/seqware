/*
 * Copyright (C) 2012 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.seqware.queryengine.tutorial;

import com.github.seqware.queryengine.impl.HBaseStorage;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;


/**
 *
 * @author dyuen
 */
public class PrintUserCount {
    public static void main(String[] args) throws Exception {

        Configuration conf = HBaseConfiguration.create();
        HBaseStorage.configureHBaseConfig(conf);
        HTable htable = new HTable(conf, "summary_user");

        Scan scan = new Scan();
        ResultScanner scanner = htable.getScanner(scan);
        Result r;
        while (((r = scanner.next()) != null)) {
            ImmutableBytesWritable b = r.getBytes();
            byte[] key = r.getRow();
            int userId = Bytes.toInt(key);
            byte[] totalValue = r.getValue(Bytes.toBytes("details"), Bytes.toBytes("total"));
            int count = Bytes.toInt(totalValue);

            System.out.println("key: " + userId+ ",  count: " + count);
        }
        scanner.close();
        htable.close();
    }
}