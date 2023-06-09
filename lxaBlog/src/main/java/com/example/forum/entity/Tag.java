package com.example.forum.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.forum.common.base.BaseEntity;
import lombok.Data;


/**
 * <pre>
 *     文章标签
 * </pre>
 */
@Data
@TableName("tag")
public class Tag extends BaseEntity {

    /**
     * 标签名称
     */
    private String tagName;


    /**
     * 数量
     */
    @TableField(exist = false)
    private Integer count;

}
