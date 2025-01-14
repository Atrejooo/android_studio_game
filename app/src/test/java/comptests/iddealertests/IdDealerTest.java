package comptests.iddealertests;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import iddealer.IdDealer;
import iddealer.IdDealerFactory;
import iddealer.Idable;

public class IdDealerTest {

    private IdDealer idDealer;

    @Before
    public void setUp() {
        idDealer = IdDealerFactory.createIdDealer();
    }

    @Test
    public void testDefineType() {
        int typeId1 = idDealer.defineType(SampleIdable.class);
        int typeId2 = idDealer.defineType(AnotherIdable.class);

        assertNotEquals(typeId1, typeId2);
        assertEquals((int) typeId1, idDealer.defineType(SampleIdable.class)); // Check idempotency
    }

    @Test
    public void testAddIdableInstanceAndFromInstanceId() {
        // Define the type
        idDealer.defineType(SampleIdable.class);

        // Create and add an instance
        SampleIdable idable = new SampleIdable();
        idDealer.addIdableInstance(idable);

        // Retrieve the instance by ID
        Idable retrieved = idDealer.fromInstanceId(idable.instanceId());
        assertSame(idable, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddIdableInstanceWithoutDefiningType() {
        SampleIdable idable = new SampleIdable();
        idDealer.addIdableInstance(idable); // Should throw an exception
    }

    @Test
    public void testRemoveIdableInstance() {
        idDealer.defineType(SampleIdable.class);
        SampleIdable idable = new SampleIdable();
        idDealer.addIdableInstance(idable);

        assertNotNull(idDealer.fromInstanceId(idable.instanceId()));
        idDealer.removeIdableInstance(idable);
        assertNull(idDealer.fromInstanceId(idable.instanceId()));
    }

    @Test
    public void testFromTypeId() {
        int typeId = idDealer.defineType(SampleIdable.class);
        assertEquals(SampleIdable.class, idDealer.fromTypeId(typeId));
    }

    // Anonymous classes to test the Idable interface
    static class SampleIdable implements Idable {
        private int instanceId;
        private int typeId;

        @Override
        public int instanceId() {
            return instanceId;
        }

        @Override
        public int typeId() {
            return typeId;
        }

        @Override
        public void setInstanceId(int instanceId) {
            this.instanceId = instanceId;
        }

        @Override
        public void setTypeId(int typeId) {
            this.typeId = typeId;
        }
    }

    static class AnotherIdable implements Idable {
        private int instanceId;
        private int typeId;

        @Override
        public int instanceId() {
            return instanceId;
        }

        @Override
        public int typeId() {
            return typeId;
        }

        @Override
        public void setInstanceId(int instanceId) {
            this.instanceId = instanceId;
        }

        @Override
        public void setTypeId(int typeId) {
            this.typeId = typeId;
        }
    }
}

