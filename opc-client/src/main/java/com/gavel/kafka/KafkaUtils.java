package com.gavel.kafka;

import com.gavel.opcclient.DataPointItem;
import com.google.gson.Gson;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.ItemState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class KafkaUtils {


    private static Gson gson = new Gson();

    private static Logger LOG = LoggerFactory.getLogger(Producer.class);

    public static boolean sendMessage(List<DataPointItem> dataPointItems){
        KafkaProducer<String, String> producer = Producer.getProducer();

        final AtomicBoolean result = new AtomicBoolean(true);
        ProducerRecord<String, String> record = new ProducerRecord<String, String>("device_status", gson.toJson(dataPointItems));
        //record.headers().add("name", name.getBytes());
        //record.headers().add("code", code.getBytes());
        // 发送消息
        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                if (null != e){
                    LOG.info("send error" + e.getMessage());
                    result.set(false);
                }else {
                    System.out.println(String.format("offset:%s,partition:%s",recordMetadata.offset(),recordMetadata.partition()));
                }
            }
        });
        producer.close();
        return result.get();
    }
}
