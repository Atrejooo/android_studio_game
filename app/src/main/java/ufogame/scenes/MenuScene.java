package ufogame.scenes;

import android.util.Log;

import common.data.ImgRendererData;
import common.data.TxtRendererData;
import gameframe.conductors.Conductor;
import gameframe.functionalities.input.InputObserver;
import gameframe.functionalities.input.RectInputReciever;
import gameframe.functionalities.movement.AnchorHover;
import gameframe.functionalities.movement.CameraAnchor;
import gameframe.functionalities.rendering.ImgRenderer;
import gameframe.functionalities.rendering.TxtRenderer;
import gameframe.nodes.Node;
import gameframe.scenes.Scene;
import gameframe.utils.ConnectionSource;
import gameframe.utils.Vec2;
import ufogame.prefabs.WarningSign;

public class MenuScene extends Scene {
    @Override
    public void init(Conductor conductor) {
        super.init(conductor);

        addCamera();
        createNumberButtons();
        createHostJoinBtns();
        addTextField();
    }

    private final float leftSide = 0.1f;
    private TxtRenderer txtRenderer;

    private void createHostJoinBtns() {
        for (int i = 0; i < 2; i++) {
            Node node = add(new Vec2(), "number btn");

            ImgRenderer imgRenderer = node.add(ImgRenderer.class);
            ImgRendererData imgRendererData = imgRenderer.data();
            imgRendererData.setImg("HostJoinBtn", i);
            imgRendererData.setPxPerUnit(500);
            imgRendererData.setCenter(new Vec2());

            RectInputReciever rectInputReciever = node.add(RectInputReciever.class);
            rectInputReciever.setSize(new Vec2(2, 1));
            rectInputReciever.setCenter(new Vec2());

            final int myNumber = i;
            boolean isHost = i == 1;
            rectInputReciever.setObserver(new InputObserver() {
                @Override
                public void onPressed() {
                    Log.d("HostJoinBtn", "Clicked Host Join, isHost: " + isHost);
                    if (isHost) {
                        host();
                    } else {
                        join();
                    }
                }

                @Override
                public void onRelease() {

                }

                @Override
                public void onHold() {

                }
            });

            CameraAnchor anchor = node.add(CameraAnchor.class);

            float spacing = 0.3f;
            Vec2 leftCorner = new Vec2(leftSide, 0.2f);

            float yPos = spacing * i;
            Vec2 newAnchorPos = leftCorner.add(new Vec2(0, yPos));
            anchor.setAnchor(newAnchorPos);
            anchor.setScale(new Vec2(1));


            node.add(AnchorHover.class);
        }
    }

    private void createNumberButtons() {
        for (int i = 0; i < 11; i++) {
            Node node = add(new Vec2(), "number btn");

            ImgRenderer imgRenderer = node.add(ImgRenderer.class);
            ImgRendererData imgRendererData = imgRenderer.data();
            imgRendererData.setImg("NumberButtons", i);
            imgRendererData.setPxPerUnit(500);
            imgRendererData.setCenter(new Vec2());

            RectInputReciever rectInputReciever = node.add(RectInputReciever.class);
            rectInputReciever.setSize(Vec2.one());
            rectInputReciever.setCenter(new Vec2());


            int myNumber = i + 1;
            if (myNumber == 10)
                myNumber = 0;
            final int myNumberFinal = myNumber;
            rectInputReciever.setObserver(new InputObserver() {
                @Override
                public void onPressed() {
                    Log.d("NumberButtons", "pressed button: " + myNumberFinal);
                    typedNumber(myNumberFinal);
                    setTextField(currentInput);
                }

                @Override
                public void onRelease() {

                }

                @Override
                public void onHold() {

                }
            });

            CameraAnchor anchor = node.add(CameraAnchor.class);
            int gridIndex = i;
            if (i >= 9)
                gridIndex++;
            float spacing = 0.09f;
            Vec2 leftCorner = new Vec2(0.55f, 0.2f);
            float xPos = spacing * (gridIndex % 3);
            float yPos = spacing * (gridIndex / 3) * 2f;
            Vec2 newAnchorPos = leftCorner.add(new Vec2(xPos, yPos));
            anchor.setAnchor(newAnchorPos);
            anchor.setScale(new Vec2(0.7f));

            node.add(AnchorHover.class);
        }
    }

    private void addTextField() {
        Node node = add();
        txtRenderer = node.add(TxtRenderer.class);
        TxtRendererData txtRendererData = txtRenderer.data();
        txtRendererData.setText("");
        txtRendererData.setTextSize(60);
        txtRendererData.setCenter(new Vec2());

        CameraAnchor anchor = node.add(CameraAnchor.class);
        Vec2 leftCorner = new Vec2(leftSide, 0.8f);
        Vec2 newAnchorPos = leftCorner;
        anchor.setAnchor(newAnchorPos);
        anchor.setScale(new Vec2(0.5f));


        node.add(AnchorHover.class);
    }

    private String currentInput = "01000000200254321";
    private final int maxInputLength = 17;

    protected void typedNumber(int number) {
        if (number == 11) {
            deletedLastNumber();
            return;
        }
        // Ensure the number is valid (0-9) and maxInputLength is not exceeded
        if (number < 0 || number > 9) {
            throw new IllegalArgumentException("Only single digits (0-9) are allowed.");
        }
        if (currentInput.length() < maxInputLength) {
            currentInput += number; // Append the number to the currentInput
        }
        Log.d("MenuScene", "current Input: " + currentInput);
    }

    protected void deletedLastNumber() {
        // Remove the last character if currentInput is not empty
        if (!currentInput.isEmpty()) {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
        } else {
            System.out.println("No input to delete.");
        }
    }

    private void setTextField(String text) {
        if (txtRenderer != null) {
            txtRenderer.data().setText(text);
        }
    }


    private void join() {
        try {
            ConnectionSource source = ConnectionSource.fromString(currentInput);
            source.open = false;

            conductor.setConnectionSource(source);

            conductor.startConnector(source, (result -> {
                if (result)
                    conductor.switchScene(1);
                else
                    WarningSign.createWarningSign(this);
            }));
        } catch (Exception e) {
            WarningSign.createWarningSign(this);
            Log.e("MenuScene", "some thing went wrong in the join attempt", e);
        }
    }

    private void host() {
        ConnectionSource source = conductor.getConnectionSource();
        source.port = 54321;
        source.open = true;
        conductor.setConnectionSource(source);

        conductor.startConnector(source, (result -> {
            if (result)
                conductor.switchScene(1);
            else
                WarningSign.createWarningSign(this);
        }));
    }
}

