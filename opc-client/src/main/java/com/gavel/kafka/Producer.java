package com.gavel.kafka;

import com.gavel.PropertiesUtil;
import com.gavel.opcclient.DataPointItem;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class Producer {

    private static Logger log = Logger.getLogger(Producer.class);

    private static Gson gson = new Gson();

    /*
初始化配置
 */
    private static Properties initConfig(){

        Properties props = new Properties();
        props.put("bootstrap.servers", PropertiesUtil.getValue("kafka.bootstrap.servers", "192.168.30.101:9092"));
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        return props;
    }

    public static KafkaProducer<String, String> getProducer(){

        Properties configs = initConfig();
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(configs);

        return producer;

    }

    public static void main(String[] args)  {

        Random random = new Random();
        while (true){
            List<DataPointItem> dataPointItemList = Lists.newArrayList(new DataPointItem("device.status.demo", new Date().getTime(), random.nextDouble()));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dataPointItemList.add(new DataPointItem("device.status.demo", new Date().getTime(), random.nextDouble()));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dataPointItemList.add(new DataPointItem("device.status.demo", new Date().getTime() , random.nextDouble()));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dataPointItemList.add(new DataPointItem("device.status.demo", new Date().getTime(), random.nextDouble()));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            KafkaUtils.sendMessage(dataPointItemList);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
