//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.xiaowu.wpywebframework.core.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Character.UnicodeBlock;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    private static final Map<String, Pattern> PATTERN_CACHE = new ConcurrentHashMap();
    static final char CN_CHAR_START = '一';
    static final char CN_CHAR_END = '龥';

    public StringUtils() {
    }

    public static int toNumber(String str, int index, int length) {
        int result = 0;
        int i = length;

        for(int x = 0; i > 0; ++x) {
            char c = str.charAt(index + x);
            if (c < '0' || c > '9') {
                return -1;
            }

            int val = c - 48;
            result += (int)Math.pow(10.0, (double)(i - 1)) * val;
            --i;
        }

        return result;
    }

    public static Pattern compileRegex(String regex) {
        Pattern pattern = (Pattern)PATTERN_CACHE.get(regex);
        if (pattern == null) {
            pattern = Pattern.compile(regex);
            PATTERN_CACHE.put(regex, pattern);
        }

        return pattern;
    }

    public static String toLowerCaseFirstOne(String str) {
        if (Character.isLowerCase(str.charAt(0))) {
            return str;
        } else {
            char[] chars = str.toCharArray();
            chars[0] = Character.toLowerCase(chars[0]);
            return new String(chars);
        }
    }

    public static String toUpperCaseFirstOne(String str) {
        if (Character.isUpperCase(str.charAt(0))) {
            return str;
        } else {
            char[] chars = str.toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            return new String(chars);
        }
    }

    public static String underScoreCase2CamelCase(String str) {
        if (!str.contains("_")) {
            return str;
        } else {
            StringBuilder sb = new StringBuilder();
            char[] chars = str.toCharArray();
            boolean hitUnderScore = false;
            sb.append(chars[0]);

            for(int i = 1; i < chars.length; ++i) {
                char c = chars[i];
                if (c == '_') {
                    hitUnderScore = true;
                } else if (hitUnderScore) {
                    sb.append(Character.toUpperCase(c));
                    hitUnderScore = false;
                } else {
                    sb.append(c);
                }
            }

            return sb.toString();
        }
    }

    public static String camelCase2UnderScoreCase(String str) {
        StringBuilder sb = new StringBuilder();
        char[] chars = str.toCharArray();
        char[] var3 = chars;
        int var4 = chars.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            char c = var3[var5];
            if (Character.isUpperCase(c)) {
                sb.append("_").append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    public static String throwable2String(Throwable e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

    public static String concat(Object... more) {
        return concatSpiltWith("", more);
    }

    public static String concatSpiltWith(String split, Object... more) {
        StringBuilder buf = new StringBuilder();

        for(int i = 0; i < more.length; ++i) {
            if (i != 0) {
                buf.append(split);
            }

            buf.append(more[i]);
        }

        return buf.toString();
    }

    public static String toASCII(String str) {
        StringBuilder strBuf = new StringBuilder();
        byte[] bGBK = str.getBytes();
        byte[] var3 = bGBK;
        int var4 = bGBK.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            byte b = var3[var5];
            strBuf.append(Integer.toHexString(b & 255));
        }

        return strBuf.toString();
    }

    public static String toUnicode(String str) {
        StringBuilder strBuf = new StringBuilder();
        char[] chars = str.toCharArray();
        char[] var3 = chars;
        int var4 = chars.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            char aChar = var3[var5];
            strBuf.append("\\u").append(Integer.toHexString(aChar));
        }

        return strBuf.toString();
    }

    public static String toUnicodeString(char[] chars) {
        StringBuilder strBuf = new StringBuilder();
        char[] var2 = chars;
        int var3 = chars.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            char aChar = var2[var4];
            strBuf.append("\\u").append(Integer.toHexString(aChar));
        }

        return strBuf.toString();
    }

    public static boolean containsChineseChar(String str) {
        char[] chars = str.toCharArray();
        char[] var2 = chars;
        int var3 = chars.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            char aChar = var2[var4];
            if (aChar >= 19968 && aChar <= '龥') {
                return true;
            }
        }

        return false;
    }

    public static boolean isNullOrEmpty(Object obj) {
        return obj == null || "".equals(obj.toString());
    }

    public static boolean isNumber(Object obj) {
        if (obj instanceof Number) {
            return true;
        } else {
            return isInt(obj) || isDouble(obj);
        }
    }

    public static String matcherFirst(String patternStr, String text) {
        Pattern pattern = compileRegex(patternStr);
        Matcher matcher = pattern.matcher(text);
        String group = null;
        if (matcher.find()) {
            group = matcher.group();
        }

        return group;
    }

    public static boolean isInt(Object obj) {
        if (isNullOrEmpty(obj)) {
            return false;
        } else {
            return obj instanceof Integer ? true : obj.toString().matches("[-+]?\\d+");
        }
    }

    public static boolean isDouble(Object obj) {
        if (isNullOrEmpty(obj)) {
            return false;
        } else {
            return !(obj instanceof Double) && !(obj instanceof Float) ? compileRegex("[-+]?\\d+\\.\\d+").matcher(obj.toString()).matches() : true;
        }
    }

    public static boolean isBoolean(Object obj) {
        if (obj instanceof Boolean) {
            return true;
        } else {
            String strVal = String.valueOf(obj);
            return "true".equalsIgnoreCase(strVal) || "false".equalsIgnoreCase(strVal);
        }
    }

    public static boolean isTrue(Object obj) {
        return "true".equals(String.valueOf(obj));
    }

    public static boolean contains(Object[] arr, Object... obj) {
        return arr != null && obj != null && arr.length != 0 ? (new HashSet(Arrays.asList(arr))).containsAll(Arrays.asList(obj)) : false;
    }

    public static int toInt(Object object, int defaultValue) {
        if (object instanceof Number) {
            return ((Number)object).intValue();
        } else if (isInt(object)) {
            return Integer.parseInt(object.toString());
        } else {
            return isDouble(object) ? (int)Double.parseDouble(object.toString()) : defaultValue;
        }
    }

    public static int toInt(Object object) {
        return toInt(object, 0);
    }

    public static long toLong(Object object, long defaultValue) {
        if (object instanceof Number) {
            return ((Number)object).longValue();
        } else if (isInt(object)) {
            return Long.parseLong(object.toString());
        } else {
            return isDouble(object) ? (long)Double.parseDouble(object.toString()) : defaultValue;
        }
    }

    public static long toLong(Object object) {
        return toLong(object, 0L);
    }

    public static double toDouble(Object object, double defaultValue) {
        if (object instanceof Number) {
            return ((Number)object).doubleValue();
        } else if (isNumber(object)) {
            return Double.parseDouble(object.toString());
        } else {
            return null == object ? defaultValue : 0.0;
        }
    }

    public static double toDouble(Object object) {
        return toDouble(object, 0.0);
    }

    public static String[] splitFirst(String str, String regex) {
        return str.split(regex, 2);
    }

    public static String toString(Object object) {
        return toString(object, (String)null);
    }

    public static String toString(Object object, String defaultValue) {
        return object == null ? defaultValue : String.valueOf(object);
    }

    public static String[] toStringAndSplit(Object object, String regex) {
        return isNullOrEmpty(object) ? null : String.valueOf(object).split(regex);
    }

    private static boolean isChinese(char c) {
        UnicodeBlock ub = UnicodeBlock.of(c);
        return ub == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == UnicodeBlock.GENERAL_PUNCTUATION || ub == UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

    public static boolean isMessyCode(String strName) {
        Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
        Matcher m = p.matcher(strName);
        String after = m.replaceAll("");
        String temp = after.replaceAll("\\p{P}", "");
        char[] ch = temp.trim().toCharArray();
        float chLength = 0.0F;
        float count = 0.0F;
        char[] var8 = ch;
        int var9 = ch.length;

        for(int var10 = 0; var10 < var9; ++var10) {
            char c = var8[var10];
            if (!Character.isLetterOrDigit(c)) {
                if (!isChinese(c)) {
                    ++count;
                }

                ++chLength;
            }
        }

        float result = count / chLength;
        return (double)result > 0.4;
    }
}
