import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * @author elvischang
 * @create 2022-04-16-下午 07:17
 **/
public class JmsProduce {

    public static final String ACTIVEMQ_URL = "tcp://192.168.245.130:61616";
    public static final String QUEUE_NAME = "queue01";

    public static void main(String[] args) throws JMSException {
        // 1. 建立連接工廠，依照給定的url地址(使用預設username & password)
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);

        // 2. 通過連接工廠，獲得連接connection
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();

        // 3. 建立session
        // 兩個參數(交易 & 簽收)
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // 4. 建立destination(指名是queue還是topic)
        // 在此可宣告多型父類別Destination或Queue
//        Destination destination = session.createQueue(QUEUE_NAME);
        Queue queue = session.createQueue(QUEUE_NAME);

        // 5. 建立消息生產者
        MessageProducer messageProducer = session.createProducer(queue);
        messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        // 6. 通過使用messageProducer生產三條消息發送到MQ的queue裡面
        for (int i = 1; i <= 6; i++) {
            // 7. 建立消息
            TextMessage textMessage = session.createTextMessage("msg---" + i);// 理解為一個String
            textMessage.setStringProperty("c01", "vip");
            // 8. 通過messageProducer發送給MQ
            messageProducer.send(textMessage);
        }

        // 9. 關閉資源
        messageProducer.close();
        session.close();
        connection.close();
        System.out.println("訊息發佈到MQ完成");


    }
}
