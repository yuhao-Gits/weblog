package com.yuhao.weblog.common.utils;

import com.yuhao.weblog.module.admin.model.entity.Article;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Lucene 站内搜索工具类：索引维护 + 中文分词检索 + 关键词高亮
 * 使用 Lucene 自带 SmartChineseAnalyzer 中文分词，无需额外 IK 分词依赖
 */
@Slf4j
public class LuceneHelper {

    /** 索引存储目录 */
    private static final String INDEX_DIR = "./lucene-index";

    /** 文章标题字段 */
    private static final String FIELD_ID = "id";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_SUMMARY = "summary";
    private static final String FIELD_CONTENT = "content";

    private static final Analyzer ANALYZER = new SmartChineseAnalyzer();

    private LuceneHelper() {
    }

    /**
     * 重建全部文章索引（应用启动时调用）
     */
    public static void rebuildIndex(List<Article> articles) {
        try {
            Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));
            IndexWriterConfig config = new IndexWriterConfig(ANALYZER);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            try (IndexWriter writer = new IndexWriter(dir, config)) {
                for (Article a : articles) {
                    writer.addDocument(buildDoc(a));
                }
            }
            log.info("Lucene 索引重建完成，共 {} 篇文章", articles.size());
        } catch (IOException e) {
            log.error("Lucene 索引重建失败", e);
        }
    }

    /**
     * 新增/更新单篇文章索引
     */
    public static void upsertArticle(Article article) {
        try {
            Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));
            IndexWriterConfig config = new IndexWriterConfig(ANALYZER);
            try (IndexWriter writer = new IndexWriter(dir, config)) {
                writer.updateDocument(new Term(FIELD_ID, String.valueOf(article.getId())), buildDoc(article));
            }
        } catch (IOException e) {
            log.error("Lucene 索引更新失败, articleId={}", article.getId(), e);
        }
    }

    /**
     * 删除文章索引
     */
    public static void deleteArticle(Long articleId) {
        try {
            Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));
            IndexWriterConfig config = new IndexWriterConfig(ANALYZER);
            try (IndexWriter writer = new IndexWriter(dir, config)) {
                writer.deleteDocuments(new Term(FIELD_ID, String.valueOf(articleId)));
            }
        } catch (IOException e) {
            log.error("Lucene 索引删除失败, articleId={}", articleId, e);
        }
    }

    /**
     * 中文分词检索，返回结果 VO（含高亮摘要）
     */
    public static List<SearchResult> search(String keyword, int limit) {
        List<SearchResult> results = new ArrayList<>();
        try {
            Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));
            if (!DirectoryReader.indexExists(dir)) {
                return results;
            }
            try (DirectoryReader reader = DirectoryReader.open(dir)) {
                IndexSearcher searcher = new IndexSearcher(reader);
                String[] fields = {FIELD_TITLE, FIELD_SUMMARY, FIELD_CONTENT};
                QueryParser parser = new MultiFieldQueryParser(fields, ANALYZER);
                Query query = parser.parse(QueryParser.escape(keyword));

                TopDocs topDocs = searcher.search(query, limit);
                SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<em>", "</em>");
                Highlighter highlighter = new Highlighter(formatter, new QueryScorer(query));

                for (ScoreDoc sd : topDocs.scoreDocs) {
                    Document doc = searcher.doc(sd.doc);
                    SearchResult r = new SearchResult();
                    r.setId(doc.get(FIELD_ID));
                    String title = doc.get(FIELD_TITLE);
                    String summary = doc.get(FIELD_SUMMARY);
                    // 优先高亮标题，否则高亮正文/摘要片段
                    String highlight = null;
                    try {
                        highlight = highlighter.getBestFragment(ANALYZER, FIELD_TITLE, title);
                    } catch (Exception ignored) {
                    }
                    if (highlight == null) {
                        try {
                            highlight = highlighter.getBestFragment(ANALYZER, FIELD_CONTENT, doc.get(FIELD_CONTENT));
                        } catch (Exception ignored) {
                        }
                    }
                    r.setTitle(title);
                    r.setHighlight(highlight != null ? highlight : summary);
                    results.add(r);
                }
            }
        } catch (Exception e) {
            log.error("Lucene 检索失败, keyword={}", keyword, e);
        }
        return results;
    }

    private static Document buildDoc(Article a) {
        Document doc = new Document();
        doc.add(new StringField(FIELD_ID, String.valueOf(a.getId()), Field.Store.YES));
        doc.add(new TextField(FIELD_TITLE, a.getTitle() == null ? "" : a.getTitle(), Field.Store.YES));
        doc.add(new TextField(FIELD_SUMMARY, a.getSummary() == null ? "" : a.getSummary(), Field.Store.YES));
        doc.add(new TextField(FIELD_CONTENT, a.getContent() == null ? "" : a.getContent(), Field.Store.YES));
        return doc;
    }

    /**
     * 搜索结果
     */
    public static class SearchResult {
        private String id;
        private String title;
        private String highlight;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getHighlight() {
            return highlight;
        }

        public void setHighlight(String highlight) {
            this.highlight = highlight;
        }
    }
}
