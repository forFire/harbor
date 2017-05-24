package com.capcare.harbor.handler.device.t808;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class T808UpgradeStream {

    private static final Object                          obj              = new Object ();

    private static Hashtable <String, T808UpgradeStream> map;

    private static int                                   PACKAGE_PER_SIZE = 1024;

    private Logger                                       log              = LoggerFactory.getLogger (getClass ());

    private Reader                                       reader;

    private long                                         length;

    public static T808UpgradeStream getInstance (String hardware, String version) {

        synchronized (obj) {
            if (map == null) {
                map = new Hashtable <String, T808UpgradeStream> ();
            }
            String versionName = getVersionName (hardware, version);
            if (!map.containsKey (versionName)) {
                map.put (versionName, new T808UpgradeStream (versionName));
            }
        }
        return map.get (getVersionName (hardware, version));
    }

    public T808UpgradeStream (String versionName) {

        File file = new File ("upgrade/" + versionName);
        this.length = file.length ();
        FileReader readUpdateFile = this.readUpdateFile (file);
        this.reader = readUpdateFile;
    }

    private static String getVersionName (String hardware, String version) {

        return hardware + "-" + version;
    }

    private FileReader readUpdateFile (File file) {

        try {
            FileReader fileReader = new FileReader (file);
            return fileReader;
        }
        catch (FileNotFoundException e) {
            log.error ("", e);
        }
        return null;
    }

    public byte [] getBytes (int offset, int length) {

        char [] data = this.getChars (offset, length);
        return toBytes (data);
    }

    private char [] getChars (int offset, int length) {

        char [] data = new char [length];
        try {
            int read = 0;
            read = reader.read (data, offset, length);
            if (read <= -1) {

            }
        }
        catch (IOException e) {
            log.error ("", e);
        }
        return data;
    }

    private byte [] toBytes (char [] chars) {

        byte [] rs = new byte [chars.length];
        for (int num = 0; num < chars.length; num++) {
            rs [num] = (byte) chars [chars.length];
        }
        return rs;
    }

    public long getPackageCount () {

        return (this.length / PACKAGE_PER_SIZE + (this.length % PACKAGE_PER_SIZE > 0 ? 1 : 0));
    }
}
