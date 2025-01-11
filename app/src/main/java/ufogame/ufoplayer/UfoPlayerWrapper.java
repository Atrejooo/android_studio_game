package ufogame.ufoplayer;

import android.util.Log;

import common.data.ImgRendererData;
import gameframe.conductors.Conductor;
import gameframe.functionalities.animation.Animator;
import gameframe.functionalities.animation.ImgAnimation;
import gameframe.functionalities.collision.Collider;
import gameframe.functionalities.hitbox.HitBox;
import gameframe.functionalities.hitbox.HitBoxObserver;
import gameframe.functionalities.movement.VelocityBounceMovement;
import gameframe.functionalities.movement.VelocityMovement;
import gameframe.functionalities.rendering.ImgRenderer;
import gameframe.functionalities.syncing.SyncedEventExecutor;
import gameframe.nodes.Node;
import gameframe.nodes.SyncableNodeWrapper;
import gameframe.scenes.Scene;
import gameframe.utils.AddF;
import gameframe.utils.Color;
import gameframe.utils.Vec2;
import singeltons.Randoms;
import synchronizer.ActionPackage;
import synchronizer.SyncableData;

public class UfoPlayerWrapper extends SyncableNodeWrapper implements SyncedEventExecutor, HitBoxObserver {
    private static final String debugName = "UfoPlayerWrapper";

    private int playerId;
    private Vec2 currentInputDir = new Vec2();

    //nodes--------
    private Node mainPlayerNode;
    private VelocityMovement velocityMovement;
    private Animator animator;
    private final float spawnRadius = 7f;

    public Node playerNode() {
        return mainPlayerNode;
    }

    public UfoPlayerWrapper(Conductor conductor) {
        super(conductor);
    }

    public UfoPlayerWrapper(Conductor conductor, int instanceId) {
        super(conductor, instanceId);
    }

    @Override
    protected void init() {
        Scene scene = conductor.activeScene();

        float randomAngle = Randoms.range(0, AddF.PI * 2);
        Vec2 spawnPos = Vec2.fromAngle(randomAngle).mult(spawnRadius);
        mainPlayerNode = scene.add(spawnPos, "player node");
        addNode(mainPlayerNode);

        mainPlayerNode.transform().setPos(spawnPos);

        Collider collider = mainPlayerNode.add(Collider.class);
        collider.setCircle(1);
        collider.setTheyAre(0b0000000000000001);
        collider.setThisIs(0b0000000000000010);

        syncDelay = 1f;
        velocityMovement = mainPlayerNode.add(VelocityBounceMovement.class);
        velocityMovement.setaLinDrag(0.1f);
        velocityMovement.setLinDrag(13);
        ((VelocityBounceMovement) velocityMovement).setBounceF(0.5f);

        ImgRenderer renderer = mainPlayerNode.add(ImgRenderer.class);
        ImgRendererData rendererData = renderer.data();
        rendererData.setPxPerUnit(300);
        rendererData.setCenter(new Vec2(0.5f, 0.4f));
        rendererData.setImg("placeholder", 0);
        rendererData.setColor(new Color(1));
        rendererData.layer = 1;

        mainPlayerNode.add(HitBox.class).setObserver(this);

        animator = mainPlayerNode.add(Animator.class);
        animator.setAnimation(new ImgAnimation(0.075f, "PlayerShip", 0, 4));
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }


    @Override
    public SyncableData getData() {
        UfoPlayerSyncableData data = new UfoPlayerSyncableData();

        data.setInstanceId(instanceId());
        data.setTypeId(typeId());

        data.vel = velocityMovement.velo();

        data.pos = mainPlayerNode.transform().pos();

        data.inputDir = currentInputDir;

        data.playerId = playerId;

        return data;
    }

    @Override
    public void processData(SyncableData data) {
        UfoPlayerSyncableData ufoData = (UfoPlayerSyncableData) data;
        this.playerId = ufoData.playerId;

        //velo
        float diff = velocityMovement.velo().sub(ufoData.vel).mag();
        if (diff > 0.05) {
            //Log.e(debugName, "big velo diff: " + diff);
        }
        velocityMovement.setVelo(ufoData.vel);

        //pos
        diff = mainPlayerNode.transform().pos().sub(ufoData.pos).mag();
        if (diff > 0.2) {
            //Log.e(debugName, "big pos diff: " + diff);
        }
        mainPlayerNode.transform().setPos(ufoData.pos);

        currentInputDir = ufoData.inputDir;
    }

    boolean performSync = true;

    @Override
    protected boolean defineSyncingCondition() {
        boolean remeber = performSync;
        performSync = false;
        return remeber;
    }


    //because currentInput dir gets clamped
    private Vec2 lastInputDir = new Vec2(99);

    @Override
    protected void enforcerUpdate() {
        ActionPackage myActionPackage = conductor.playerManager().getActionPackageFromId(playerId);

        if (myActionPackage == null) {
            Log.d(debugName, "playerId: " + playerId + " action package is null");
            return;
        }
        //Log.d(playerId + "", myActionPackage + " id: "+ myActionPackage.hashCode());

        UfoActionPackage ufoActionPackage = (UfoActionPackage) myActionPackage;

        if (!lastInputDir.equals(ufoActionPackage.inputDir)) {
            lastInputDir = ufoActionPackage.inputDir;
            currentInputDir = ufoActionPackage.inputDir.clampMag(0, 1);
            performSync = true;
        }
    }

    private float speed = 20;

    @Override
    protected void commonUpdate() {
        if (currentInputDir == null) {
            Log.e(debugName, "currentInputDir is null!");
            return;
        }

        //TODO check if active works
        //does not seem to happen on clientPlayer

        //nvm. does get called!
        //but why tf does it not move?
        //is currentInput dir (0,0)? <- BINGO
        //why is currentInputDir 0,0? IDK!!

        //ok my fault
        //currentInput dir works
        //still glitching tho!

        /*possible causes:
            [?] 1. Velocity Bounce Movement not deterministic enough
                - when does it happen (only on high velocities)?
                - errors in calculation
                -> test did not show any clear signs of strong randomness
            [ ] 2. Error with packages and the times they are being read
                - too early?
                - too late?
                - wrong order?
            [ ] 3. Why dos the ball work?
            [x] 4. delta time not right
                - unlikely
                - would also not work on host
        */
        velocityMovement.addForce(currentInputDir.mult(speed * conductor.delta()));

        if (conductor == null)
            return;

        if (playerId == conductor.getMyPlayerId()) {
            conductor.camera().followObj = mainPlayerNode;
        }
    }

    @Override
    public void onHit() {
        //enforceDispose();
    }
}
