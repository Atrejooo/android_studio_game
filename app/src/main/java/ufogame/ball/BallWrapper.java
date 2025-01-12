package ufogame.ball;

import android.util.Log;

import common.data.ImgRendererData;
import gameframe.conductors.Conductor;
import gameframe.conductors.PlayerManager;
import gameframe.conductors.Yell;
import gameframe.functionalities.animation.Animator;
import gameframe.functionalities.animation.ImgAnimation;
import gameframe.functionalities.collision.Collider;
import gameframe.functionalities.collision.CollisionObserver;
import gameframe.functionalities.hitbox.HitBox;
import gameframe.functionalities.movement.VelocityBounceMovement;
import gameframe.functionalities.rendering.ImgRenderer;
import gameframe.nodes.Node;
import gameframe.nodes.SyncableNodeWrapper;
import gameframe.scenes.Scene;
import gameframe.utils.Color;
import gameframe.utils.Vec2;
import iddealer.Idable;
import synchronizer.SyncableData;
import ufogame.ufoplayer.UfoPlayerWrapper;

public class BallWrapper extends SyncableNodeWrapper implements CollisionObserver {
    private static final String debugName = "BallWrapper";


    public BallWrapper(Conductor conductor) {
        super(conductor);
    }

    public BallWrapper(Conductor conductor, int instanceId) {
        super(conductor, instanceId);
    }

    private Node mainNode;
    private VelocityBounceMovement velocityMovement;
    private Animator animator;
    private Collider collider;

    @Override
    protected void init() {
        Scene scene = conductor.activeScene();

        mainNode = scene.add(new Vec2(0, 0), "ball");
        addNode(mainNode);

        collider = mainNode.add(Collider.class);
        collider.setCircle(1);
        collider.setTheyAre(0b0000000000000011);
        collider.setThisIs(0b0000000000000100);

        velocityMovement = mainNode.add(VelocityBounceMovement.class);
        velocityMovement.setaLinDrag(0.1f);
        velocityMovement.setLinDrag(10);
        velocityMovement.setBounceF(1);
        velocityMovement.setCollisionObserver(this);

        ImgRenderer renderer = mainNode.add(ImgRenderer.class);
        ImgRendererData rendererData = renderer.data();
        rendererData.setPxPerUnit(250);
        rendererData.setCenter(new Vec2(0.5f, 0.46f));
        rendererData.setImg("ball", 0);
        rendererData.setColor(new Color(1));
        rendererData.layer = 0;

        syncDelay = 0.5f;

        animator = mainNode.add(Animator.class);
        animator.setAnimation(new ImgAnimation(0.075f, "ball", 0, 0));
    }

    @Override
    public SyncableData getData() {
        BallSyncableData data = new BallSyncableData();

        data.accelDir = accelDir;
        data.pos = mainNode.transform().pos();
        data.vel = velocityMovement.velo();
        data.maxSpeed = maxSpeed;
        data.speedF = speedF;
        data.playerTargetInstanceId = playerTargetInstanceId;

        data.setInstanceId(this.instanceId());
        data.setTypeId(this.typeId());

        return data;
    }

    @Override
    public void processData(SyncableData data) {
        BallSyncableData ballData;
        if (data instanceof BallSyncableData) {
            ballData = (BallSyncableData) data;
        } else {
            throw new IllegalArgumentException("wrong data type for ball");
        }

        accelDir = ballData.accelDir;
        velocityMovement.setVelo(ballData.vel);
        mainNode.transform().setPos(ballData.pos);
        maxSpeed = ballData.maxSpeed;
        speedF = ballData.speedF;
        setTarget(ballData.playerTargetInstanceId);
    }

    @Override
    protected boolean defineSyncingCondition() {
        boolean remember = performSync;
        performSync = false;
        return remember;
    }

    private float maxSpeed = 8;
    private float speedF = 15;

    private Vec2 accelDir = new Vec2();

    private int playerTargetInstanceId;
    private Node target;
    private UfoPlayerWrapper currentPlayer;

    @Override
    public void onYell(Yell yell) {
        if (yell == Yell.SWITCH_TARGET) {
            PlayerManager playerManager = conductor.playerManager();
            SyncableNodeWrapper nextPlayer = playerManager.nextPlayer(currentPlayer.getPlayerId());
            if (nextPlayer != null)
                setTarget(nextPlayer);
            else
                target = null;
        }
    }

    public void setTarget(SyncableNodeWrapper player) {
        currentPlayer = ((UfoPlayerWrapper) player);
        setTarget(currentPlayer.instanceId());
    }

    public void setTarget(int playerId) {
        Idable instance = conductor.getIdDealer().fromInstanceId(playerId);

        if (instance != null) {
            this.playerTargetInstanceId = playerId;
            this.target = ((UfoPlayerWrapper) instance).playerNode();
            performSync = true;

            maxSpeed += 1;
            speedF += 2;
        } else {
            Log.e(debugName, "Instance of target not found!");
        }
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
        performSync = true;
    }

    private boolean performSync;

    @Override
    protected void enforcerUpdate() {

    }

    private Vec2 lastAccelDir = new Vec2(999);

    @Override
    protected void commonUpdate() {
        if (accelDir == null) {
            Log.e(debugName, "accelDir is null!");
            return;
        }

        if (target == null)
            return;

        Vec2 dir = target.transform().pos().sub(mainNode.transform().pos()).normal();

        if (!dir.equals(lastAccelDir)) {
            accelDir = dir;
            lastAccelDir = dir;
        }

        velocityMovement.addForce(accelDir.mult(speedF * conductor.delta()));

        velocityMovement.setVelo(velocityMovement.velo().clampMag(0, maxSpeed));
    }

    @Override
    public void onCollisionEnter(Node other) {
        HitBox hitBox = other.get(HitBox.class);
        if (hitBox != null) {
            hitBox.hit();
        }
    }

    @Override
    public void onCollisionStay(Node other) {

    }

    @Override
    public void onCollisionLeave(Node other) {

    }
}
