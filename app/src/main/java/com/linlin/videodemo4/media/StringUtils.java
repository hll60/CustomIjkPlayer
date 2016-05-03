//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.linlin.videodemo4.media;

import android.util.Log;

import java.util.Arrays;
import java.util.Iterator;

public class StringUtils {
    public StringUtils() {
    }

    public static String join(Object[] elements, CharSequence separator) {
        return join((Iterable)Arrays.asList(elements), separator);
    }

    public static String join(Iterable<? extends Object> elements, CharSequence separator) {
        StringBuilder builder = new StringBuilder();
        if(elements != null) {
            Iterator iter = elements.iterator();
            if(iter.hasNext()) {
                builder.append(String.valueOf(iter.next()));

                while(iter.hasNext()) {
                    builder.append(separator).append(String.valueOf(iter.next()));
                }
            }
        }

        return builder.toString();
    }

    public static String fixLastSlash(String str) {
        String res = str == null?"/":str.trim() + "/";
        if(res.length() > 2 && res.charAt(res.length() - 2) == 47) {
            res = res.substring(0, res.length() - 1);
        }

        return res;
    }

    public static int convertToInt(String str) throws NumberFormatException {
        int s;
        for(s = 0; s < str.length() && !Character.isDigit(str.charAt(s)); ++s) {
            ;
        }

        int e;
        for(e = str.length(); e > 0 && !Character.isDigit(str.charAt(e - 1)); --e) {
            ;
        }

        if(e > s) {
            try {
                return Integer.parseInt(str.substring(s, e));
            } catch (NumberFormatException var4) {
                Log.e("convertToInt", var4.getMessage());
                throw new NumberFormatException();
            }
        } else {
            throw new NumberFormatException();
        }
    }

    public static String generateTime(long time) {
        int totalSeconds = (int)(time / 1000L);
        int seconds = totalSeconds % 60;
        int minutes = totalSeconds / 60 % 60;
        int hours = totalSeconds / 3600;
        return hours > 0?String.format("%02d:%02d:%02d", new Object[]{Integer.valueOf(hours), Integer.valueOf(minutes), Integer.valueOf(seconds)}):String.format("%02d:%02d", new Object[]{Integer.valueOf(minutes), Integer.valueOf(seconds)});
    }
}
