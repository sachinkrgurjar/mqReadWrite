package com.sachin.mqReadWrite;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;

import com.ibm.mq.*;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.jms.MQQueueConnection;
import com.ibm.mq.jms.MQQueueSender;
import com.ibm.mq.jms.MQQueueSession;
import com.ibm.msg.client.wmq.compat.base.internal.MQC;
import com.ibm.msg.client.wmq.compat.jms.internal.JMSC;
import com.ibm.msg.client.wmq.compat.jms.internal.JMSTextMessage;
import com.sachin.mqReadWrite.properties.MQHelper;
import com.sachin.mqReadWrite.properties.SecurityProperties;
import org.apache.commons.lang3.StringUtils;
import com.sachin.mqReadWrite.properties.MQProperties;


import com.ibm.mq.jms.MQQueueConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MQReadWrite {
 private static final  String TMP_FOLDER = "/tmp/";
 private static MQQueueConnectionFactory cf = new MQQueueConnectionFactory();

 @Autowired
 MQProperties mqProperties;

 @Autowired
 SecurityProperties securityProperties;

 public void mqMessagePost() throws Exception
  {
    cf.setChannel(mqProperties.getQmgrs().getChannel());
    cf.setSSLCipherSuite(mqProperties.getCluster().getSsl());
    String queueJks = securityProperties.getQueueJks();
    cf.setHostName(mqProperties.getCluster().getServerhost());
    cf.setQueueManager(mqProperties.getQmgrs().getQmgrname());
    cf.setPort(Integer.parseInt(mqProperties.getCluster().getPort()));
    cf.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);

      MQHelper.setProperties(queueJks);

      MQQueueConnection connection = (MQQueueConnection) cf.createQueueConnection(mqProperties.getCluster().getUser(), mqProperties.getCluster().getPass());
      MQQueueSession session = (MQQueueSession) connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
      MQQueue queue = (MQQueue) session.createQueue(mqProperties.getQueues().get(0));
      MQQueueSender sender = (MQQueueSender) session.createSender((Queue) queue);

      Message message = session.createTextMessage("sample message");

      connection.start();

      sender.send(message);
      session.close();
      connection.close();

  }

    public void mqMessageRead() throws Exception
    {
        int GET_OPTIONS_CONSTANT = MQConstants.MQGMO_WAIT |
                           MQConstants.MQGMO_PROPERTIES_COMPATIBILITY |
                           MQConstants.MQGMO_ALL_SEGMENTS_AVAILABLE |
                           MQConstants.MQGMO_COMPLETE_MSG |
                           MQConstants.MQGMO_ALL_MSGS_AVAILABLE |
                           MQConstants.MQGMO_SYNCPOINT;

        String queueJKS = securityProperties.getQueueJks();
        MQEnvironment.hostname = mqProperties.getCluster().getServerhost();
        MQEnvironment.port = Integer.parseInt(mqProperties.getCluster().getPort());
        String queueManager = mqProperties.getQmgrs().getQmgrname();
        MQEnvironment.channel = mqProperties.getQmgrs().getChannel();
        List<String> queues = mqProperties.getQueues();
        int openOptions = MQC.MQOO_INPUT_AS_Q_DEF | MQC.MQOO_INQUIRE;
        MQHelper.setProperties(queueJKS);
        String queueName = "a.b.c";

        MQEnvironment.sslCipherSuite = mqProperties.getCluster().getSsl();
        StringBuilder builder = new StringBuilder();
        MQQueueManager mqQueueManager = new MQQueueManager(queueManager);
        MQQueue queue = mqQueueManager.accessQueue(queueName, openOptions, null, null, null);

        boolean isMessageAvailable = true;

        while(isMessageAvailable)
        {
            try{
                MQGetMessageOptions getMessageOptions = new MQGetMessageOptions();
                getMessageOptions.waitInterval = 1;
                getMessageOptions.options = GET_OPTIONS_CONSTANT;

                MQMessage message = new MQMessage();
                String strMsg;

                queue.get(message, getMessageOptions);
                byte[] b = new byte[message.getMessageLength()];
                message.readFully(b);
                strMsg = new String(b);
                System.out.println("message is "+ strMsg);
                message.clearMessage();
                message.messageId = MQConstants.MQMI_NONE;
                message.correlationId = MQConstants.MQCI_NONE;
            } catch (MQException e){
                if((e.completionCode == MQConstants.MQCC_FAILED) && (e.reasonCode == MQConstants.MQRC_NO_MSG_AVAILABLE)){
                    break;
                }

            } catch (IOException e){
                System.out.println("Issue in message read");
            }
        }
    }
}
