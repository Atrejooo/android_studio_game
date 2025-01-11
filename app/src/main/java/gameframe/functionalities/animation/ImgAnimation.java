package gameframe.functionalities.animation;

import gameframe.utils.Delay;

public class ImgAnimation {
    private Delay delay;

    private int start, end;
    private String name;

    public ImgAnimation(float FrameDelay, String name, int start, int end) {
        this.start = start;
        this.end = end;
        this.name = name;
        delay = new Delay(FrameDelay);
        reset();
    }

    public String getName() {
        return name;
    }

    public void reset() {
        i = start;
        delay.reset();
    }

    private int i = 0;

    public int getNumber(float timeDelta) {
        if (delay.use(timeDelta)) {
            if (i == end) {
                i = start;
            } else {
                i++;
            }
        }

        return i;
    }


}
