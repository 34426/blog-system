package com.example.forum.service;


import com.example.forum.common.base.BaseService;
import com.example.forum.entity.Comment;

import java.util.List;

/**
 * <pre>
 *     回复业务逻辑接口
 * </pre>
 *
 */
public interface CommentService extends BaseService<Comment, Long> {

    /**
     * 根据用户Id删除回复
     *
     * @param userId 用户Id
     */
    Integer deleteByUserId(Long userId);

    /**
     * 根据回复接受人Id删除回复
     *
     * @param acceptId 用户Id
     */
    Integer deleteByAcceptUserId(Long acceptId);

    /**
     * 根据文章ID获得评论列表
     * @param postId
     * @return
     */
    List<Comment> findByPostId(Long postId);
}
