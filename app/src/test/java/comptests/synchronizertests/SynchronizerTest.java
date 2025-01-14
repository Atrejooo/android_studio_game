package comptests.synchronizertests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import singeltons.Randoms;
import synchronizer.Syncable;

public class SynchronizerTest {
    private TestSyncContext context1, context2;

    @BeforeEach
    public void before() {
        context1 = new TestSyncContext();
        context2 = new TestSyncContext();
    }

    @Test
    public void TestObjectReplication() {
        context1.startConnection(true);
        context2.startConnection(false);
        context2.executeSyncTest();

        //give time for context2.synchronizer to finish copying objects
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        assert (context2.syncables.size() > 0);

        for (int i = 0; i < context1.syncables.size(); i++) {
            TestSyncable testSyncableFrom1 = (TestSyncable) context1.syncables.get(i);
            TestSyncable testSyncableFrom2 = (TestSyncable) context2.idDealer.fromInstanceId(testSyncableFrom1.instanceId());

            assertNotNull(testSyncableFrom2);

            assertEquals(testSyncableFrom1.value1, testSyncableFrom2.value1, 1);
            assertEquals(testSyncableFrom1.value2, testSyncableFrom2.value2, 0.01f);
        }
    }

    @Test
    public void TestUpdatingValues(){
        context1.startConnection(true);
        context2.startConnection(false);
        context2.executeSyncTest();

        //give time for context2.synchronizer to finish copying objects
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        assert (context2.syncables.size() > 0);

        for (int i = 0; i < context1.syncables.size(); i++) {
            TestSyncable testSyncableFrom1 = (TestSyncable) context1.syncables.get(i);
            TestSyncable testSyncableFrom2 = (TestSyncable) context2.idDealer.fromInstanceId(testSyncableFrom1.instanceId());

            assertNotNull(testSyncableFrom2);

            assertEquals(testSyncableFrom1.value1, testSyncableFrom2.value1, 1);
            assertEquals(testSyncableFrom1.value2, testSyncableFrom2.value2, 0.01f);
        }

        for(Syncable syncable : context2.syncables){
            TestSyncable testSyncable = (TestSyncable) syncable;

            testSyncable.value2 = Randoms.range(23f,235235f);
            testSyncable.value1 = Randoms.randomInt();
        }

        context1.executeSyncTest();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        assert (context2.syncables.size() > 0);

        for (int i = 0; i < context1.syncables.size(); i++) {
            TestSyncable testSyncableFrom1 = (TestSyncable) context1.syncables.get(i);
            TestSyncable testSyncableFrom2 = (TestSyncable) context2.idDealer.fromInstanceId(testSyncableFrom1.instanceId());

            assertNotNull(testSyncableFrom2);

            assertEquals(testSyncableFrom1.value1, testSyncableFrom2.value1, 1);
            assertEquals(testSyncableFrom1.value2, testSyncableFrom2.value2, 0.01f);
        }
    }
}
