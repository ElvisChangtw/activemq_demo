import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.xml.soap.Text;
import java.io.IOException;

/**
 * @author elvischang
 * @create 2022-04-17-上午 10:47
 **/
public class JmsConsumer_Topic {

    public static final String ACTIVEMQ_URL = "tcp://192.168.245.130:61616";
    public static final String TOPIC_NAME = "topic-persistent";


    public static void main(String[] args) throws JMSException, IOException {
        System.out.println("*****z3");

        // 1. 建立連接工廠，依照給定的url地址(使用預設username & password)
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);

        // 2. 通過連接工廠，獲得連接connection
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.setClientID("z3");

        // 3. 建立session
        // 兩個參數(交易 & 簽收)
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // 4. 建立destination(指名是queue還是topic)
        Topic topic = session.createTopic(TOPIC_NAME);

        TopicSubscriber topicSubscriber = session.createDurableSubscriber(topic, "remark...");

        connection.start();

        Message message = topicSubscriber.receive();
        while(null != message){
            TextMessage textMessage = (TextMessage) message;
            System.out.println("******收到的持久化topc: "+ textMessage.getText());
            message = topicSubscriber.receive(5000L);
        }

        // 5. 建立消費者
//        MessageConsumer messageConsumer = session.createConsumer(topic);

        // 法1.通過監聽方式消費消息
//        messageConsumer.setMessageListener(new MessageListener() {
//            @Override
//            public void onMessage(Message message) {
//                if (null != message && message instanceof TextMessage) {
//                    TextMessage textMessage = (TextMessage) message;
//                    try {
//                        System.out.println("Consumer接收到消息" + textMessage.getText());
//                    } catch (JMSException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
        // 法2. 設置監聽器方式
//        messageConsumer.setMessageListener((Message message) -> {
//            if (null != message && message instanceof TextMessage) {
//                TextMessage textMessage = (TextMessage) message;
//                try {
//                    System.out.println("Consumer接收到topic消息" + textMessage.getText());
//                } catch (JMSException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        System.in.read(); // 保證服務不滅(如果連接到一半跑完就往下走close會消費不到)


        // 6. 關閉資源
//        messageConsumer.close();
        session.close();
        connection.close();
    }
}