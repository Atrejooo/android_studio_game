package synchronizer;

import java.io.Serializable;

import iddealer.Idable;

public class EventData implements Serializable, Idable {


    @Override
    public int instanceId() {
        return 0;
    }

    @Override
    public int typeId() {
        return 0;
    }

    @Override
    public void setInstanceId(int instanceId) {

    }

    @Override
    public void setTypeId(int typeId) {

    }
}
