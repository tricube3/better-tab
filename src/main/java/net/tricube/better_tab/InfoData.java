package net.tricube.better_tab;

public class InfoData {
	private static String tpsSuffix = "?";
    private static double tps = 20.0;
	private static double mspt = 0;
	private static int ping = 0;
    public static void setCurrentTPS(double t) {
        tps = t;
    }
    public static double getCurrentTPS() {
        return tps;
    }

    public static void setCurrentMSPT(double t) {
        mspt = t/2;
    }
    public static double getCurrentMSPT() {return mspt;}

	public static void setCurrentPing(int p) {
		ping = p;
	}
	public static int getCurrentPing() {
		return ping;
	}

	public static String getTpsSuffix() {return tpsSuffix;}
	public static void setTpsSuffix(String tpsChar) {tpsSuffix = tpsChar;}
}
