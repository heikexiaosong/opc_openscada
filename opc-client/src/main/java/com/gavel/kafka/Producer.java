package com.gavel.kafka;

import com.gavel.opcclient.DataPointItem;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.io.Files;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class Producer {

    static Logger log = Logger.getLogger(Producer.class);

    /*
初始化配置
 */
    private static Properties initConfig(){

        Properties props = new Properties();
        props.put("bootstrap.servers", KafkaEnv.BROKER_LIST);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        return props;
    }

    public static KafkaProducer<String, DataPointItem> getProducer(){

        Properties configs = initConfig();
        KafkaProducer<String, DataPointItem> producer = new KafkaProducer<String, DataPointItem>(configs);

        return producer;

    }

    public static void main(String[] args) throws Exception {

        KafkaProducer<String, DataPointItem> producer = getProducer();

        ProducerRecord<String, DataPointItem> record = new ProducerRecord<String, DataPointItem>("sl_env", null);
        //record.headers().add("name", name.getBytes());
        //record.headers().add("code", code.getBytes());

        // 发送消息
        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                if (null != e){
                    log.info("send error" + e.getMessage());
                }else {
                    System.out.println(String.format("offset:%s,partition:%s",recordMetadata.offset(),recordMetadata.partition()));
                }
            }
        });

        producer.close();
    }

}
