package gabrielleopoldino.sddl.sectests.nodeProcesser;

import gabrielleopoldino.sddl.sectests.csv.CSVLogger;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.serialization.Serialization;
import lac.cnet.sddl.objects.ApplicationObject;
import lac.cnet.sddl.objects.Message;
import lac.cnet.sddl.objects.PrivateMessage;
import lac.cnet.sddl.udi.core.SddlLayer;
import lac.cnet.sddl.udi.core.UniversalDDSLayerFactory;
import lac.cnet.sddl.udi.core.listener.UDIDataReaderListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by gabriel on 11/05/17.
 */
public class TimerProcessingNode implements UDIDataReaderListener<ApplicationObject> {

    private SddlLayer core;
    private long startTime;
    private long finalTime;
    private CSVLogger csv;
    private long tx;
    private int tam;
    private int pings;
    private HashMap<UUID,Long> msgs;
    private byte[] blooper;
    private ScheduledExecutorService executor;
    private Sender sender;
    private Receiver receiver;
    private String logName;

    /*
    tam = 100b
    ate = 1kb

    ssh -X
     */

    public static void main (String[] args)
    {
        new TimerProcessingNode(10, 1024, "log.csv", 0);
    }


    /**
     *
     * @param tx
     *  Taxa de envio de mensagens. Tempo de espera entre o envio de uma mensagem e outra.
     * @param tam
     *  Tamanho dos pacotes enviados em bytes.
     * @param logName
     *  Nome do log gerado
     * @param pings
     *  Quantidade de pacotes ping enviados
     */
    public TimerProcessingNode(long tx, int tam, String logName, int pings)
    {
        this.tx = tx;
        this.tam = tam;
        this.logName = logName;
        this.pings = pings;

        blooper = new byte[tam];
        byte j=0;
        for (int i=0; i<tam; i++) {
            blooper[i] = j;
            j++;
            if (j == 255)
                j=0;
        }

        System.out.println("Blooper de "+blooper.length+" bytes gerado");
        System.out.println("tx="+tx);
        System.out.println("log salvo em "+logName);
        System.out.println("Quanddade de pings: "+pings);

        core = UniversalDDSLayerFactory.getInstance();
        core.createParticipant(UniversalDDSLayerFactory.CNET_DOMAIN);

        core.createPublisher();
        core.createSubscriber();

        executor = Executors.newScheduledThreadPool(16, new AbstractEchoClientForNode.ScheduleFactory("sender"));

        receiver = new Receiver() {
            @Override
            public void receive(ApplicationObject topicSample) {
                Message message = (Message) topicSample;
                String s = (String) Serialization.fromJavaByteStream(message.getContent());
                System.out.println(s);
                if (s.equals("S"))
                {
                    sender = new Sender(message.getGatewayId(), message.getSenderId());
                    receiver = new Receiver() {
                        @Override
                        public void receive(ApplicationObject topicSample) {
                            long finalTime = System.nanoTime();
                            Message message = (Message) topicSample;
                            Serializable content = Serialization.fromJavaByteStream(message.getContent());
                            if (content instanceof Wrapper) {
                                Wrapper wrapper = (Wrapper) content;
                                long time = finalTime - wrapper.getTime();
                                //System.out.println("Ping "+(double) time/1000000);
                                csv.add(time);
                            }
                            else
                                System.out.println("Ping message in incorrect format");
                        }
                    };
                    executor.scheduleAtFixedRate(sender, 0, tx, TimeUnit.MILLISECONDS);
                    //sender.start();
                }
            }
        };

        Object receiveMessageTopic = core.createTopic(Message.class, Message.class.getSimpleName());
        core.createDataReader(this, receiveMessageTopic);

        Object toMobileNodeTopic = core.createTopic(PrivateMessage.class, PrivateMessage.class.getSimpleName());
        core.createDataWriter(toMobileNodeTopic);

        csv = new CSVLogger(logName, pings);
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
                Wrapper msg = new Wrapper(blooper);
                msg.setTime(System.nanoTime());
                if (csv.getCount() >= pings && pings != 0) {
                    msg.setEndOfCommunication(true);
                }
                sendMessage(gatewayId, id, msg);
                if (csv.getCount() >= pings && pings != 0) {
                    csv.stop();
                    System.exit(0);
                }
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
