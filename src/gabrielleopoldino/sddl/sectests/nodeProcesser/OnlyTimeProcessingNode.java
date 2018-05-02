package gabrielleopoldino.sddl.sectests.nodeProcesser;

import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.serialization.Serialization;
import lac.cnet.sddl.objects.ApplicationObject;
import lac.cnet.sddl.objects.Message;
import lac.cnet.sddl.objects.PrivateMessage;
import lac.cnet.sddl.udi.core.SddlLayer;
import lac.cnet.sddl.udi.core.UniversalDDSLayerFactory;
import lac.cnet.sddl.udi.core.listener.UDIDataReaderListener;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by gabriel on 11/05/17.
 */
public class OnlyTimeProcessingNode implements UDIDataReaderListener<ApplicationObject> {

    private SddlLayer core;
    private Receiver receiver;

    /*
    tam = 100b
    ate = 1kb

    ssh -X
     */

    public static void main (String[] args)
    {
        new OnlyTimeProcessingNode();
    }

    public OnlyTimeProcessingNode()
    {
        core = UniversalDDSLayerFactory.getInstance();
        core.createParticipant(UniversalDDSLayerFactory.CNET_DOMAIN);

        core.createPublisher();
        core.createSubscriber();

        receiver = new Receiver() {
            @Override
            public void receive(ApplicationObject topicSample) {
                Message message = (Message) topicSample;
                String s = (String) Serialization.fromJavaByteStream(message.getContent());
                System.out.println(s);
                if (s.equals("S"))
                {
                    receiver = new Receiver() {
                        @Override
                        public void receive(ApplicationObject topicSample) {
                            long finalTime = System.nanoTime();
                            Message message = (Message) topicSample;
                            Serializable content = Serialization.fromJavaByteStream(message.getContent());
                            if (content instanceof Long)
                            {
                                Long initTime = (Long) content;
                                long time = finalTime - initTime;
                                System.out.println("Ping "+(double) time/1000000);
                                //csv.add(time););
                                sendMessage(message.getGatewayId(), message.getSenderId(), System.nanoTime());
                            }
                            else
                                System.out.println("Ping message in incorrect format");
                        }
                    };
                    sendMessage(message.getGatewayId(), message.getSenderId(), System.nanoTime());
                }
            }
        };

        Object receiveMessageTopic = core.createTopic(Message.class, Message.class.getSimpleName());
        core.createDataReader(this, receiveMessageTopic);

        Object toMobileNodeTopic = core.createTopic(PrivateMessage.class, PrivateMessage.class.getSimpleName());
        core.createDataWriter(toMobileNodeTopic);

        System.out.println("Aguardando...");
    }

    private void sendMessage(UUID gateway, UUID destiny, Serializable content)
    {
        PrivateMessage privateMessage = new PrivateMessage();
        privateMessage.setGatewayId(gateway);
        privateMessage.setNodeId(destiny);

        ApplicationMessage appMsg = new ApplicationMessage();
        appMsg.setContentObject(content);
        privateMessage.setMessage(Serialization.toProtocolMessage(appMsg));

        core.writeTopic(PrivateMessage.class.getSimpleName(), privateMessage);
    }


    @Override
    public void onNewData(ApplicationObject topicSample) {
        receiver.receive(topicSample);
    }

    private class Sender implements Runnable
    {
        private UUID gatewayId;
        private UUID id;

        public Sender(UUID gatewayId, UUID id) {
            this.gatewayId = gatewayId;
            this.id = id;
            System.out.println("Iniciando Ping");

        }

        @Override
        public void run() {
            //while(true) {
            Long time=System.nanoTime();
                sendMessage(gatewayId, id, time);
                /*try {
                    sleep(tx);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
           // }
        }
    }

    private interface Receiver
    {
        void receive(ApplicationObject topicSample);

    }
}
