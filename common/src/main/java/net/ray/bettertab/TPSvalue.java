package net.ray.bettertab;

public class TPSvalue {
    private static double tps = 20.0;
    public static void setCurrentTPS(double t) {
        tps = t;
    }
    public static double getCurrentTPS() {
        return tps;
    }
    private static double mspt = 0;
    public static void setCurrentMSPT(double t) {
        mspt = t/2;
    }
    public static double getCurrentMSPT() {return mspt;}
}
