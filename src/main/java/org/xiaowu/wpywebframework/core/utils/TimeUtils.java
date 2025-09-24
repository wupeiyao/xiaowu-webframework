//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.xiaowu.wpywebframework.core.utils;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeUtils {
    private static final Logger logger = LoggerFactory.getLogger(TimeUtils.class);
    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("GMT+8");
    private static final SimpleDateFormat DEFAULT_TEMPLATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public TimeUtils() {
    }

    public static String getFormatBeginTime(int days) {
        return DEFAULT_TEMPLATE.format(getBeginTime(days));
    }

    public static String getFormatBeginTime() {
        return DEFAULT_TEMPLATE.format(getBeginTime());
    }

    public static String getFormatBeginTime(int days, SimpleDateFormat format) {
        return format.format(getBeginTime(days));
    }

    public static String getFormatBeginTime(SimpleDateFormat format) {
        return format.format(getBeginTime());
    }

    public static Date getBeginTime(int days) {
        Calendar calendar = Calendar.getInstance(TIME_ZONE);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        calendar.add(5, -days);
        return calendar.getTime();
    }

    public static Date getBeginTime() {
        Calendar calendar = Calendar.getInstance(TIME_ZONE);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTime();
    }

    public static String getFormatEndTime(int days) {
        return DEFAULT_TEMPLATE.format(getEndTime(days));
    }

    public static String getFormatEndTime() {
        return DEFAULT_TEMPLATE.format(getEndTime());
    }

    public static String getFormatEndTime(int days, SimpleDateFormat format) {
        return format.format(getEndTime(days));
    }

    public static String getFormatEndTime(SimpleDateFormat format) {
        return format.format(getEndTime());
    }

    public static Date getEndTime(int days) {
        Calendar calendar = Calendar.getInstance(TIME_ZONE);
        calendar.set(11, 23);
        calendar.set(12, 59);
        calendar.set(13, 59);
        calendar.set(14, 999);
        calendar.add(5, -days);
        return calendar.getTime();
    }

    public static Date getEndTime() {
        Calendar calendar = Calendar.getInstance(TIME_ZONE);
        calendar.set(11, 23);
        calendar.set(12, 59);
        calendar.set(13, 59);
        calendar.set(14, 999);
        return calendar.getTime();
    }

    public static List<String> findDates(String beginTime, String endTime) {
        List<String> list = new ArrayList();

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date begin = format.parse(beginTime);
            Date end = format.parse(endTime);
            Calendar calendar = Calendar.getInstance(TIME_ZONE);
            calendar.setTime(begin);
            list.add(format.format(calendar.getTime()));
            int days = (int)((end.getTime() - begin.getTime()) / 86400000L);

            for(int i = 1; i <= days; ++i) {
                calendar.add(5, 1);
                list.add(format.format(calendar.getTime()));
            }
        } catch (Exception var9) {
            Exception e = var9;
            logger.error(e.getMessage(), e);
        }

        return list;
    }

    public static List<String> findHours(String beginTime, String endTime) {
        List<String> list = new ArrayList();

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
            Date begin = format.parse(beginTime);
            Date end = format.parse(endTime);
            Calendar calendar = Calendar.getInstance(TIME_ZONE);
            calendar.setTime(begin);
            list.add(format.format(calendar.getTime()));
            int hours = (int)((end.getTime() - begin.getTime()) / 3600000L);

            for(int i = 1; i <= hours; ++i) {
                calendar.add(11, 1);
                list.add(format.format(calendar.getTime()));
            }
        } catch (Exception var9) {
            Exception e = var9;
            logger.error(e.getMessage(), e);
        }

        return list;
    }

    public static boolean isSameDay(String beginTime, String endTime) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date begin = format.parse(beginTime);
            Date end = format.parse(endTime);
            return format.format(begin).equals(format.format(end));
        } catch (Exception var5) {
            Exception e = var5;
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    public static String conversion(String source, SimpleDateFormat format, SimpleDateFormat target) {
        try {
            return target.format(format.parse(source));
        } catch (Exception var4) {
            Exception e = var4;
            logger.error(e.getMessage(), e);
            return source;
        }
    }

    public static Duration parse(String timeString) {
        char[] all = timeString.toCharArray();
        if (all[0] != 'P' && (all[0] != '-' || all[1] != 'P')) {
            Duration duration = Duration.ofSeconds(0L);
            char[] tmp = new char[32];
            int numIndex = 0;
            char[] var5 = all;
            int var6 = all.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                char c = var5[var7];
                if (c != '-' && (c < '0' || c > '9')) {
                    long val = (new BigDecimal(tmp, 0, numIndex)).longValue();
                    numIndex = 0;
                    Duration plus = null;
                    if (c != 'D' && c != 'd') {
                        if (c != 'H' && c != 'h') {
                            if (c != 'M' && c != 'm') {
                                if (c == 's') {
                                    plus = Duration.ofSeconds(val);
                                } else if (c == 'S') {
                                    plus = Duration.ofMillis(val);
                                } else if (c == 'W' || c == 'w') {
                                    plus = Duration.ofDays(val * 7L);
                                }
                            } else {
                                plus = Duration.ofMinutes(val);
                            }
                        } else {
                            plus = Duration.ofHours(val);
                        }
                    } else {
                        plus = Duration.ofDays(val);
                    }

                    if (plus != null) {
                        duration = duration.plus(plus);
                    }
                } else {
                    tmp[numIndex++] = c;
                }
            }

            if (numIndex != 0) {
                duration = duration.plus(Duration.ofMillis((new BigDecimal(tmp, 0, numIndex)).longValue()));
            }

            return duration;
        } else {
            return Duration.parse(timeString);
        }
    }

    public static ChronoUnit parseUnit(String expr) {
        expr = expr.toUpperCase();
        if ("MILLENNIA".equals(expr)) {
            return ChronoUnit.MILLENNIA;
        } else if ("FOREVER".equals(expr)) {
            return ChronoUnit.FOREVER;
        } else {
            if (!expr.endsWith("S")) {
                expr = expr + "S";
            }

            return ChronoUnit.valueOf(expr);
        }
    }
}
