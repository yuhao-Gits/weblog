package com.yuhao.weblog.common.utils;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Markdown 转 HTML 工具类（后端封装，用于文章详情接口）
 */
@Component
public class MarkdownConverter {

    private static final Pattern WORD_COUNT_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]");
    private static final Pattern IMG_PATTERN = Pattern.compile("!\\[(.*?)\\]\\((.*?)\\)");

    private final Parser parser;
    private final HtmlRenderer renderer;

    public MarkdownConverter() {
        MutableDataSet options = new MutableDataSet()
                .set(Parser.EXTENSIONS, Arrays.asList(
                        TablesExtension.create(),
                        StrikethroughExtension.create(),
                        AutolinkExtension.create()))
                .set(HtmlRenderer.SOFT_BREAK, "<br/>");
        this.parser = Parser.builder(options).build();
        this.renderer = HtmlRenderer.builder(options).build();
    }

    /** Markdown -> HTML */
    public String toHtml(String markdown) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return "";
        }
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }

    /** 统计中文字数 */
    public int countChineseWords(String content) {
        if (content == null) return 0;
        Matcher matcher = WORD_COUNT_PATTERN.matcher(content);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    /** 从 Markdown 中提取第一张图片作为封面 */
    public String extractFirstImage(String content) {
        if (content == null) return null;
        Matcher matcher = IMG_PATTERN.matcher(content);
        if (matcher.find()) {
            return matcher.group(2);
        }
        return null;
    }

    /** 生成摘要（默认 100 字） */
    public String generateSummary(String content) {
        return generateSummary(content, 100);
    }

    /** 生成摘要（去除 Markdown 标记后的纯文本片段） */
    public String generateSummary(String content, int maxLength) {
        if (content == null) return "";
        String text = content
                .replaceAll("!\\[(.*?)\\]\\((.*?)\\)", "")   // 图片
                .replaceAll("```[\\s\\S]*?```", "")            // 代码块
                .replaceAll("[#>*`\\-_=+|]", "")               // 标记符号
                .replaceAll("\\s+", " ")
                .trim();
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }
}
