package gameframe.functionalities.rendering;

import gameframe.nodes.Comp;
import gameframe.nodes.Node;
import gameframe.utils.Color;
import gameframe.utils.Delay;
import gameframe.utils.Range;
import singeltons.Randoms;

public class FadeAndDie extends Comp {
    public FadeAndDie(Node node) {
        super(node);
    }

    private Delay startDelay;
    private Delay fadeTime;
    private boolean started;
    private Renderer renderer;
    private Color color;
    private float time;
    private Range alpha;

    public void start(Color color, float time, float startDelay, Range alpha) {
        if (!active() || color == null)
            return;
        renderer = node().get(Renderer.class);
        if (renderer == null) {
            return;
        }

        this.alpha = alpha;
        this.color = color;
        this.startDelay = new Delay(startDelay);
        fadeTime = new Delay(time);
        started = true;
    }

    @Override
    public void update() {
        if (!active() || !started)
            return;

        float delta = conductor.delta();
        if (!startDelay.ready(delta)) {
            color.setA(alpha.min);
            renderer.data().setColor(color);
        } else {
            float t = fadeTime.progress(delta);
            color.setA(alpha.lerp(t));
            renderer.data().setColor(color);

            if (t >= 1)
                node().dispose();
        }
    }
}
