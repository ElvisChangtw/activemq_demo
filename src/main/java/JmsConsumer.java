import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.IOException;

/**
 * @author elvischang
 * @create 2022-04-16-下午 10:04
 **/
public class JmsConsumer {


    public static final String ACTIVEMQ_URL = "tcp://192.168.245.130:61616";
    public static final String QUEUE_NAME = "queue01";


    public static void main(String[] args) throws JMSException, IOException {
        System.out.println("我是2號消費者");

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

        // 5. 建立消費者
        MessageConsumer messageConsumer = session.createConsumer(queue);

        /*
        // 同步阻塞方式(receive())
        // 訂閱者或接收者調用MessageConsumer的receive()方法接收消息，接收到消息前一直阻塞
        while (true) {
            TextMessage textMessage = (TextMessage) messageConsumer.receive(4000L);
            if (null != textMessage) {
                System.out.println("Consumer接收到消息" + textMessage.getText());
            } else {
                break;
            }
        }

        */
        /*
         * 通過監聽方式消費消息
         * 異部非阻塞方式(監聽器onMessage())
         * 訂閱者或接收者通過MessageConsumer的onMessage方法
        */
        messageConsumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                if (null != message && message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        System.out.println("Consumer接收到消息" + textMessage.getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        System.in.read(); // 保證服務不滅(如果連接到一半跑完就往下走close會消費不到)
        // 6. 關閉資源
        messageConsumer.close();
        session.close();
        connection.close();

        /*
         * 1. 先生產，只啟動1號消費者，問題: 1號消費者能消費消息嗎?
         *
         * 2. 先生產，啟動1號消費者，再啟動2號消費者，問題: 2號消費者還能消費消息嗎?
         *  2.1 1號可以消費? Y
         *  2.2 2號可以消費? N
         *
         * 3. 先啟動兩個消費者，再生產六條消息，請問，消費情況如何?
         * 3.1. 兩個消費者都有六條
         * 3.2. 先到先得，6個全給一個
         * 3.3. 一人一半 Y
         */
    }
}
