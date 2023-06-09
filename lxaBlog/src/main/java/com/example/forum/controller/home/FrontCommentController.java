package com.example.forum.controller.home;

import cn.hutool.http.HtmlUtil;
import com.example.forum.controller.common.BaseController;
import com.example.forum.entity.BlackWord;
import com.example.forum.entity.Comment;
import com.example.forum.entity.Post;
import com.example.forum.entity.User;
import com.example.forum.dto.JsonResult;
import com.example.forum.service.BlackWordService;
import com.example.forum.service.CommentService;
import com.example.forum.service.PostService;
import com.example.forum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;
import java.util.Objects;


@Controller
public class FrontCommentController extends BaseController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private BlackWordService blackWordService;

    /**
     * 发布回复
     *
     * @param postId         文章ID
     * @param commentId      上级回复ID
     * @param commentContent 回复的内容
     * @return 重定向到/admin/comment
     */
    @PostMapping(value = "/comment")
    @ResponseBody
    public JsonResult newComment(@RequestParam(value = "postId") Long postId,
                                 @RequestParam(value = "commentId", required = false) Long commentId,
                                 @RequestParam("commentContent") String commentContent) {


        // 判断是否登录
        User loginUser = getLoginUser();
        if (loginUser == null) {
            return JsonResult.error("请先登录");
        }

        // 判断文章是否存在
        Post post = postService.get(postId);
        if (post == null) {
            return JsonResult.error("文章不存在");
        }

        // 判断评论内容是否包含屏蔽字
        List<BlackWord> blackWordList = blackWordService.findAll();
        for (BlackWord blackWord : blackWordList) {
            if (commentContent.contains(blackWord.getContent())) {
                return JsonResult.error("评论内容包含违规字符");
            }
        }

        // 如果是回复
        Comment comment = new Comment();
        if (commentId != null) {
            //回复回复
            Comment parentComment = commentService.get(commentId);
            if (parentComment == null || !Objects.equals(parentComment.getPostId(), postId)) {
                return JsonResult.error("回复不存在");
            }
            User parentUser = userService.get(parentComment.getUserId());
            if (parentUser != null) {
                String lastContent = "<a href='#comment-id-" + parentComment.getId() + "'>@" + parentUser.getUserDisplayName() + "</a> ";
                comment.setCommentContent(lastContent + parentUser.getUserDisplayName() + ": " + HtmlUtil.escape(commentContent));
                comment.setCommentParent(parentComment.getId());
                comment.setAcceptUserId(parentComment.getUserId());
                comment.setPathTrace(parentComment.getPathTrace() + parentComment.getId() + "/");
            }
        } else {
            // 回复文章
            comment.setCommentContent(HtmlUtil.escape(commentContent));
            comment.setCommentParent(0L);
            comment.setAcceptUserId(post.getUserId());
            comment.setPathTrace("/");
        }
        comment.setUserId(loginUser.getId());
        comment.setPostId(postId);
        comment.setCreateTime(new Date());
        comment.setUpdateTime(new Date());
        commentService.insert(comment);

        // 修改评论数
        postService.resetCommentSize(postId);
        return JsonResult.success("回复成功", comment.getId());
    }

    /**
     * 点赞评论
     *
     * @param commentId
     * @return
     */
    @PostMapping("/comment/like")
    @ResponseBody
    public JsonResult likeComment(@RequestParam("commentId") Long commentId) {
        Comment comment = commentService.get(commentId);
        if (comment == null) {
            return JsonResult.error("回复不存在");
        }
        comment.setLikeCount(comment.getLikeCount() + 1);
        commentService.update(comment);
        return JsonResult.success();
    }

    /**
     * 点赞评论
     *
     * @param commentId
     * @return
     */
    @PostMapping("/comment/dislike")
    @ResponseBody
    public JsonResult dislikeComment(@RequestParam("commentId") Long commentId) {
        Comment comment = commentService.get(commentId);
        if (comment == null) {
            return JsonResult.error("回复不存在");
        }
        comment.setDislikeCount(comment.getDislikeCount() + 1);
        commentService.update(comment);
        return JsonResult.success();
    }
}
