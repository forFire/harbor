package module.util;

public class Tools {

    public static long toLong (Object obj) {

        try {
            return Long.parseLong (obj.toString ());
        }
        catch (Exception e) {
            return 0;
        }

    }

    public static double toDouble (Object obj) {

        try {
            return Double.parseDouble (obj.toString ());
        }
        catch (Exception e) {
            return 0;
        }

    }

    public static int toInt (Object obj) {

        try {
            return Integer.parseInt (obj.toString ());
        }
        catch (Exception e) {
            return 0;
        }

    }

    public static float toFloat (Object obj) {

        try {
            return Float.parseFloat (obj.toString ());
        }
        catch (Exception e) {
            return 0;
        }

    }

    public static boolean checkNull (Object obj) {

        return obj == null;
    }

    public static boolean checkNotNull (Object obj) {

        return !checkNull (obj);
    }

    public static boolean checkEmpty (Object obj) {

        return checkNull (obj) || obj.toString ().isEmpty ();
    }

    public static boolean checkNotEmpty (Object obj) {

        return !checkEmpty (obj);
    }

    public static String toString (Object obj) {

        return checkEmpty (obj) ? "" : obj.toString ();
    }
}
