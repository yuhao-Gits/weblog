package com.yuhao.weblog.module.comment.service.impl;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 敏感词检测：基于配置的敏感词列表做包含匹配
 */
public class SensitiveWordChecker {

    private SensitiveWordChecker() {
    }

    /**
     * 检测内容是否含敏感词
     *
     * @param content     评论内容
     * @param wordsConfig 敏感词配置（逗号分隔）
     * @return true 表示含敏感词
     */
    public static boolean containsSensitive(String content, String wordsConfig) {
        if (StringUtils.isBlank(content) || StringUtils.isBlank(wordsConfig)) {
            return false;
        }
        List<String> words = Arrays.stream(wordsConfig.split("[,，;；]"))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        for (String w : words) {
            if (content.contains(w)) {
                return true;
            }
        }
        return false;
    }
}
