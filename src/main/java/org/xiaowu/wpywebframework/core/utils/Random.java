package org.xiaowu.wpywebframework.core.utils;

public class Random extends java.util.Random {
    private final char[] chars = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    public Random() {
    }

    public String random(int len) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < len; ++i) {
            sb.append(this.chars[this.nextInt(this.chars.length)]);
        }

        return sb.toString();
    }

    public String random() {
        return this.random(8);
    }
}
