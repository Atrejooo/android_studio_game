package gameframe.utils;

import java.io.Serializable;

public class Delay implements Serializable {
    private float restTime;

    public void setDelay(float delay) {
        if (delay >= 0)
            this.delay = delay;
        else
            this.delay = -1 * delay;
    }

    private float delay;

    public Delay(float delay) {
        restTime = delay;
        setDelay(delay);
    }

    public float restTime() {
        return restTime;
    }

    public void reset() {
        restTime = delay;
    }

    public float progress(float minus) {
        reduce(minus);
        return progress();
    }

    public float progress() {
        return (delay - restTime) / delay;
    }

    public float reduce(float minus) {
        if (minus < 0)
            minus *= -1;
        restTime -= minus;
        if (restTime < 0)
            restTime = 0;
        return restTime;
    }

    public boolean use(float minus) {
        reduce(minus);

        if (restTime == 0) {
            restTime = delay;
            return true;
        } else {
            return false;
        }
    }

    public boolean ready(float minus) {
        reduce(minus);
        return ready();
    }

    public boolean ready() {
        return restTime == 0;
    }
}
