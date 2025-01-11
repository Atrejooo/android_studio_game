package gameframe.functionalities.rendering;

import common.data.ImgRendererData;
import common.data.TxtRendererData;
import gameframe.nodes.Node;

public class TxtRenderer extends Renderer {
    public TxtRenderer(Node node) {
        super(node);
        data = new TxtRendererData(transform());
    }


    @Override
    public TxtRendererData data() {
        try {
            data.copyValues(transform());
            if (data instanceof TxtRendererData)
                return (TxtRendererData) data;
            else
                return null;
        } catch (Exception e) {
            return null;
        }
    }
}
