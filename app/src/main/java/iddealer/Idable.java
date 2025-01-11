package iddealer;

import java.io.Serializable;

public interface Idable extends Serializable {
    int instanceId();

    int typeId();

    void setInstanceId(int instanceId);

    void setTypeId(int typeId);
}