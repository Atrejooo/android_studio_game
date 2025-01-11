package synchronizer;

public class SynchronizerFactory {
    public static Synchronizer createSynchronizer() {
        return new SynchronizerImpl();
    }
}
