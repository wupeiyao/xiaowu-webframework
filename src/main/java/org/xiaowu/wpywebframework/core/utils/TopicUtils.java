//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.xiaowu.wpywebframework.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

public class TopicUtils {
    private static final PathMatcher pathMatcher = new AntPathMatcher();

    public TopicUtils() {
    }

    public static boolean match(String pattern, String topic) {
        if (pattern.equals(topic)) {
            return true;
        } else {
            return !pattern.contains("*") && !pattern.contains("#") && !pattern.contains("+") && !pattern.contains("{") ? false : pathMatcher.match(pattern.replace("#", "**").replace("+", "*"), topic);
        }
    }

    public static Map<String, String> getPathVariables(String template, String topic) {
        try {
            return pathMatcher.extractUriTemplateVariables(template, topic);
        } catch (Exception var3) {
            return Collections.emptyMap();
        }
    }

    public static String[] split(String topic) {
        return topic.split("/");
    }

    private static boolean matching(String str, String pattern) {
        return !str.equals(pattern) && !"*".equals(pattern) && !"*".equals(str);
    }

    public static boolean match(String[] pattern, String[] topicParts) {
        if (pattern.length == 0 && topicParts.length == 0) {
            return true;
        } else {
            int pattIdxStart = 0;
            int pattIdxEnd = pattern.length - 1;
            int pathIdxStart = 0;

            int pathIdxEnd;
            String pattDir;
            for(pathIdxEnd = topicParts.length - 1; pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd; ++pathIdxStart) {
                pattDir = pattern[pattIdxStart];
                if ("**".equals(pattDir)) {
                    break;
                }

                if (matching(pattDir, topicParts[pathIdxStart])) {
                    return false;
                }

                ++pattIdxStart;
            }

            int patIdxTmp;
            if (pathIdxStart > pathIdxEnd) {
                if (pattIdxStart > pattIdxEnd) {
                    return "/".equals(pattern[pattern.length - 1]) == "/".equals(topicParts[topicParts.length - 1]);
                } else if (pattIdxStart == pattIdxEnd && "*".equals(pattern[pattIdxStart]) && "/".equals(topicParts[topicParts.length - 1])) {
                    return true;
                } else {
                    for(patIdxTmp = pattIdxStart; patIdxTmp <= pattIdxEnd; ++patIdxTmp) {
                        if (!"**".equals(pattern[patIdxTmp])) {
                            return false;
                        }
                    }

                    return true;
                }
            } else if (pattIdxStart > pattIdxEnd) {
                return false;
            } else if ("**".equals(topicParts[pattIdxStart])) {
                return true;
            } else {
                while(pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
                    pattDir = pattern[pattIdxEnd];
                    if ("**".equals(pattDir)) {
                        break;
                    }

                    if (matching(pattDir, topicParts[pathIdxEnd])) {
                        return false;
                    }

                    --pattIdxEnd;
                    --pathIdxEnd;
                }

                if (pathIdxStart > pathIdxEnd) {
                    for(patIdxTmp = pattIdxStart; patIdxTmp <= pattIdxEnd; ++patIdxTmp) {
                        if (!"**".equals(pattern[patIdxTmp])) {
                            return false;
                        }
                    }

                    return true;
                } else {
                    while(pattIdxStart != pattIdxEnd && pathIdxStart <= pathIdxEnd) {
                        patIdxTmp = -1;

                        int patLength;
                        for(patLength = pattIdxStart + 1; patLength <= pattIdxEnd; ++patLength) {
                            if ("**".equals(pattern[patLength])) {
                                patIdxTmp = patLength;
                                break;
                            }
                        }

                        if (patIdxTmp == pattIdxStart + 1) {
                            ++pattIdxStart;
                        } else {
                            patLength = patIdxTmp - pattIdxStart - 1;
                            int strLength = pathIdxEnd - pathIdxStart + 1;
                            int foundIdx = -1;
                            int i = 0;

                            label133:
                            while(i <= strLength - patLength) {
                                for(int j = 0; j < patLength; ++j) {
                                    String subPat = pattern[pattIdxStart + j + 1];
                                    String subStr = topicParts[pathIdxStart + i + j];
                                    if (matching(subPat, subStr)) {
                                        ++i;
                                        continue label133;
                                    }
                                }

                                foundIdx = pathIdxStart + i;
                                break;
                            }

                            if (foundIdx == -1) {
                                return false;
                            }

                            pattIdxStart = patIdxTmp;
                            pathIdxStart = foundIdx + patLength;
                        }
                    }

                    for(patIdxTmp = pattIdxStart; patIdxTmp <= pattIdxEnd; ++patIdxTmp) {
                        if (!"**".equals(pattern[patIdxTmp])) {
                            return false;
                        }
                    }

                    return true;
                }
            }
        }
    }

    public static List<String> expand(String topic) {
        if (!topic.contains(",") && !topic.contains("{")) {
            return Collections.singletonList(topic);
        } else {
            if (topic.startsWith("/")) {
                topic = topic.substring(1);
            }

            String[] parts = topic.split("/", 2);
            String first = parts[0];
            List<String> expands = new ArrayList();
            int var6;
            if (parts.length == 1) {
                String[] var12 = first.split(",");
                int var13 = var12.length;

                for(var6 = 0; var6 < var13; ++var6) {
                    String split = var12[var6];
                    if (split.startsWith("{") && split.endsWith("}")) {
                        split = "*";
                    }

                    expands.add("/" + split);
                }

                return expands;
            } else {
                List<String> nextTopics = expand(parts[1]);
                String[] var5 = first.split(",");
                var6 = var5.length;

                for(int var7 = 0; var7 < var6; ++var7) {
                    String split = var5[var7];
                    if (split.startsWith("{") && split.endsWith("}")) {
                        split = "*";
                    }

                    Iterator var9 = nextTopics.iterator();

                    while(var9.hasNext()) {
                        String nextTopic = (String)var9.next();
                        StringJoiner joiner = new StringJoiner("");
                        joiner.add("/");
                        joiner.add(split);
                        if (!nextTopic.startsWith("/")) {
                            joiner.add("/");
                        }

                        joiner.add(nextTopic);
                        expands.add(joiner.toString());
                    }
                }

                return expands;
            }
        }
    }
}
