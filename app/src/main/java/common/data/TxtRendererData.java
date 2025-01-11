package common.data;

import gameframe.nodes.Transform;

public class TxtRendererData extends RendererData {
    public TxtRendererData(Transform transform) throws IllegalArgumentException {
        super(transform);
    }

    private String text = "";

    public String fontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    private String fontName = "casual";

    public int style() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    private int style = 1;

    public float textSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    private float textSize = 100;

    public String text() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
