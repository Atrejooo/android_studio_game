package gameframe.functionalities.animation;

import android.provider.ContactsContract;

import common.data.ImgRendererData;
import gameframe.functionalities.rendering.ImgRenderer;
import gameframe.functionalities.rendering.Renderer;
import gameframe.nodes.Comp;
import gameframe.nodes.Node;

public class Animator extends Comp {
    public Animator(Node node) {
        super(node);
    }

    @Override
    public void update() {
        if (!active() || rendererData == null || currentAnimation == null)
            return;

        rendererData.setImg(currentAnimation.getName(),
                currentAnimation.getNumber(conductor.delta()));
    }

    private ImgAnimation currentAnimation;

    public void setAnimation(ImgAnimation animation) {
        if (currentAnimation != null)
            currentAnimation.reset();
        currentAnimation = animation;
    }

    private ImgRendererData rendererData;

    @Override
    public void awake() {
        ImgRenderer renderer = node().get(ImgRenderer.class);
        if (renderer != null)
            rendererData = renderer.data();
    }
}
