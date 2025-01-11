package gameframe.functionalities.rendering;

import common.data.ImgRendererData;
import common.data.RendererData;
import gameframe.nodes.Node;

public class ImgRenderer extends Renderer {
    public ImgRenderer(Node node) {
        super(node);
        data = new ImgRendererData(node.transform());
    }

    @Override
    public ImgRendererData data() {
        try {
            data.copyValues(transform());
            if (data instanceof ImgRendererData)
                return (ImgRendererData) data;
            else
                return null;
        } catch (Exception e) {
            return null;
        }
    }

    public void setImgData(ImgRendererData data) {
        if (data != null)
            this.data = data;
    }

}
