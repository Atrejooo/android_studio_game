package common.data;

import gameframe.nodes.Transform;

//issue that transforms pos/scale/rot can be set in view
public class ImgRendererData extends RendererData {
    private String imgName = "none";
    private int imgNumber = 0;

    public ImgRendererData(Transform transform) {
        super(transform);
    }

    public int getImgNumber() {
        return imgNumber;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String name) {
        if (name != null)
            this.imgName = name;
    }

    public void setImgNumber(int number) {
        if (imgNumber > -1)
            imgNumber = number;
    }

    public void setImg(String name, int number) {
        setImgName(name);
        setImgNumber(number);
    }
}
