package singeltons;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PerformanceTester {

    private static int lastTimeInMillis = 0;  // Start with the current time
    private static int overviewCountDown = 10;
    private static LinkedList<Integer> waitingTimes = new LinkedList<>();
    private static LinkedList<Float> avgWaitingTime = new LinkedList<Float>();
    private static LinkedList<Float> colliderCounts = new LinkedList<>();
    private static LinkedList<Float> colliderPaiCounts = new LinkedList<>();
    public static boolean active = false;

    public static void passTimeData(int time, int waitingTime, int maxWaitingTime) {
        if (!active) return;
        waitingTimes.add(maxWaitingTime - waitingTime);

        if (time - lastTimeInMillis >= 1000) {
            float sum = 0;

            // Calculate the average waiting time
            for (int wait : waitingTimes) {
                sum += wait;
            }
            float averageWait = waitingTimes.size() > 0 ? sum / waitingTimes.size() : 0;

            waitingTimes.clear();


            float percentageUsed = (averageWait / maxWaitingTime) * 100;
            String msg = String.format("%.2f%% used", percentageUsed);
            avgWaitingTime.add(percentageUsed);
            Log.d("PerformanceTester", msg);

            lastTimeInMillis = time;
        }
        if (overviewCountDown < avgWaitingTime.size()) {
            float sum = 0;

            for (float entry : avgWaitingTime) {
                sum += entry;
            }
            float avgTime = avgWaitingTime.size() > 0 ? sum / (float) avgWaitingTime.size() : 0;
            float avgVerts = getAvg(vertCounts);
            float avgColliders = getAvg(colliderCounts);
            float avgPairCounts = getAvg(colliderPaiCounts);

            Log.d(
                    "PerformanceTester",
                    String.format(
                            "----overview: [%.2f%%, max delay: %d, actual delay: %.2f], [verts: %.2f], [colliders: %.2f, pairs: %.2f]----",
                            roundTo2Didgt(avgTime),
                            maxWaitingTime,
                            roundTo2Didgt(avgTime / 100f * maxWaitingTime),
                            roundTo2Didgt(avgVerts),
                            avgColliders,
                            avgPairCounts
                    )
            );
            vertCounts.clear();
            colliderCounts.clear();
            avgWaitingTime.clear();
            colliderPaiCounts.clear();
        }
    }

    private static ArrayList<Float> vertCounts = new ArrayList<Float>();

    public static void passRendereingData(int vertCount) {
        if (!active) return;
        vertCounts.add((float) vertCount);
    }

    private static float roundTo2Didgt(float value) {
        return Math.round(value * 100f) / 100f;
    }

    public static void passColliderCounts(float colliderCount) {
        colliderCounts.add(colliderCount);
    }

    public static void passPairCount(float pairCount) {
        colliderPaiCounts.add(pairCount);
    }

    private static float getAvg(List<Float> list) {
        if (list.size() == 0) return 0;
        float sum = 0;
        for (float entry : list) {
            sum += entry;
        }

        return sum / list.size();
    }
}
