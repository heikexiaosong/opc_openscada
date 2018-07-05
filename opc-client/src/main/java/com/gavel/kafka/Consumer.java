package com.gavel.kafka;

import com.alibaba.fastjson.JSONArray;
import com.gavel.PropertiesUtil;
import com.gavel.opcclient.DataPointItem;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class Consumer {

    static Logger log = Logger.getLogger(Producer.class);


    private static KafkaConsumer<String,String> consumer = null;

    static {
        Properties configs = initConfig();
        consumer = new KafkaConsumer<String, String>(configs);
    }

    private static Properties initConfig(){
        Properties properties = new Properties();
        properties.put("bootstrap.servers", PropertiesUtil.getValue("kafka.bootstrap.servers", "192.168.30.101:9092"));
        properties.put("group.id","0");
        properties.put("key.deserializer", StringDeserializer.class);
        properties.put("value.deserializer", StringDeserializer.class);
        properties.setProperty("enable.auto.commit", "true");
        properties.setProperty("auto.offset.reset", "earliest");

        return properties;
    }

    public static void main(String[] args) {
        consumer.subscribe(Collections.singleton(PropertiesUtil.getValue("kafka.topic", "device_status")));
        while ( true ) {
            ConsumerRecords<String, String> records = consumer.poll(10);
            for (ConsumerRecord<String, String> record : records) {
                String jsonStr = record.value();
                List<DataPointItem> dataPointItemList = JSONArray.parseArray(jsonStr, DataPointItem.class);
                System.out.println(record);
                for (DataPointItem dataPointItem : dataPointItemList) {
                    System.out.println(dataPointItem);
                }
            }
        }
    }
}
