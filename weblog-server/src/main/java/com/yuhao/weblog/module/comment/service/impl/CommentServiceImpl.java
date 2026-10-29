package com.yuhao.weblog.module.comment.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuhao.weblog.common.BizException;
import com.yuhao.weblog.common.PageResult;
import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.admin.mapper.BlogSettingsMapper;
import com.yuhao.weblog.module.admin.model.entity.BlogSettings;
import com.yuhao.weblog.module.comment.mapper.CommentMapper;
import com.yuhao.weblog.module.comment.model.entity.Comment;
import com.yuhao.weblog.module.comment.model.req.PublishCommentReq;
import com.yuhao.weblog.module.comment.model.req.QueryCommentReq;
import com.yuhao.weblog.module.comment.model.vo.CommentVO;
import com.yuhao.weblog.module.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final String DEFAULT_AVATAR = "https://q1.qlogo.cn/g?b=qq&nk=0&s=100";

    private final CommentMapper commentMapper;
    private final BlogSettingsMapper blogSettingsMapper;
    private final CommentMailNotifier mailNotifier;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> publish(PublishCommentReq req) {
        BlogSettings settings = blogSettingsMapper.selectById(1L);

        // 敏感词检测
        if (settings != null && SensitiveWordChecker.containsSensitive(req.getContent(), settings.getSensitiveWords())) {
            throw new BizException("评论内容包含敏感词，请修改后重试");
        }

        Comment comment = new Comment();
        comment.setArticleId(req.getArticleId());
        comment.setParentId(req.getParentId() == null ? 0L : req.getParentId());
        comment.setReplyUserName(req.getReplyUserName());
        comment.setNickname(req.getNickname());
        comment.setEmail(req.getEmail());
        comment.setQq(req.getQq());
        comment.setContent(req.getContent());
        // QQ 头像：优先 QQ 号，否则默认头像
        if (StringUtils.isNotBlank(req.getQq())) {
            comment.setAvatar("https://q1.qlogo.cn/g?b=qq&nk=" + req.getQq().trim() + "&s=100");
        } else {
            comment.setAvatar(DEFAULT_AVATAR);
        }
        // 审核状态：开启审核则待审核，否则直接通过
        boolean auditEnabled = settings != null && settings.getCommentAudit() != null && settings.getCommentAudit() == 1;
        comment.setStatus(auditEnabled ? 0 : 1);

        commentMapper.insert(comment);

        // 异步邮件通知博主（配置了邮箱时生效，失败不影响评论）
        mailNotifier.notifyAsync(comment);

        return Result.success();
    }

    @Override
    public Result<List<CommentVO>> listByArticle(Long articleId) {
        List<Comment> all = commentMapper.selectList(
                Wrappers.<Comment>lambdaQuery()
                        .eq(Comment::getArticleId, articleId)
                        .eq(Comment::getStatus, 1)
                        .orderByAsc(Comment::getCreateTime));
        return Result.success(buildTree(all));
    }

    @Override
    public Result<PageResult<CommentVO>> pageList(QueryCommentReq req) {
        Page<Comment> page = commentMapper.selectPage(
                new Page<>(req.getCurrent(), req.getSize()),
                Wrappers.<Comment>lambdaQuery()
                        .eq(req.getArticleId() != null, Comment::getArticleId, req.getArticleId())
                        .eq(req.getStatus() != null, Comment::getStatus, req.getStatus())
                        .orderByDesc(Comment::getCreateTime));
        List<CommentVO> vos = page.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return Result.success(PageResult.of(vos, page.getTotal()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> delete(Long commentId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BizException("评论不存在");
        }
        // 删除自身 + 其下所有子评论
        commentMapper.deleteById(commentId);
        commentMapper.delete(Wrappers.<Comment>lambdaQuery().eq(Comment::getParentId, commentId));
        return Result.success();
    }

    @Override
    public Result<?> audit(Long commentId, Integer status) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BizException("评论不存在");
        }
        Comment update = new Comment();
        update.setId(commentId);
        update.setStatus(status == null || status != 1 ? 0 : 1);
        commentMapper.updateById(update);
        return Result.success();
    }

    /**
     * 构建树形评论结构
     */
    private List<CommentVO> buildTree(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Long, CommentVO> map = comments.stream().collect(Collectors.toMap(Comment::getId, this::toVO));
        List<CommentVO> roots = new ArrayList<>();
        for (Comment c : comments) {
            CommentVO vo = map.get(c.getId());
            if (c.getParentId() == null || c.getParentId() == 0L) {
                roots.add(vo);
            } else {
                CommentVO parent = map.get(c.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(vo);
                }
            }
        }
        return roots;
    }

    private CommentVO toVO(Comment c) {
        CommentVO vo = new CommentVO();
        vo.setId(String.valueOf(c.getId()));
        vo.setArticleId(String.valueOf(c.getArticleId()));
        vo.setParentId(c.getParentId() == null ? "0" : String.valueOf(c.getParentId()));
        vo.setNickname(c.getNickname());
        vo.setReplyUserName(c.getReplyUserName());
        vo.setAvatar(c.getAvatar());
        vo.setContent(c.getContent());
        vo.setStatus(c.getStatus());
        vo.setCreateTime(c.getCreateTime());
        return vo;
    }
}
