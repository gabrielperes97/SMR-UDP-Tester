package gabrielleopoldino.sddl.sectests.nodeProcesser;

import java.io.Serializable;

/**
 * Created by gabriel on 19/05/17.
 */
public class Wrapper implements Serializable {

    private Long time;
    private byte[] blooper;
    private boolean endOfCommunication;

    public Wrapper(byte[] blooper) {
        this.blooper = blooper;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public byte[] getBlooper() {
        return blooper;
    }

    public void setBlooper(byte[] blooper) {
        this.blooper = blooper;
    }

    public boolean isEndOfCommunication() {
        return endOfCommunication;
    }

    public void setEndOfCommunication(boolean endOfCommunication) {
        this.endOfCommunication = endOfCommunication;
    }
}
