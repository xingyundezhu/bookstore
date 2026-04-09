package com.bookstore.service;

import com.bookstore.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 评价服务接口
 */
public interface ReviewService {
    
    /**
     * 创建评价
     *
     * @param userId 用户ID
     * @param bookId 图书ID
     * @param orderId 订单ID
     * @param rating 评分（1-5）
     * @param content 评价内容
     * @param images 评价图片
     * @param isAnonymous 是否匿名
     * @return 创建的评价实体
     */
    Review createReview(Long userId, Long bookId, Long orderId, Integer rating, String content, String images, Integer isAnonymous);
    
    /**
     * 更新评价
     *
     * @param id 评价ID
     * @param rating 评分（可选）
     * @param content 评价内容（可选）
     * @param images 评价图片（可选）
     * @return 更新后的评价实体
     */
    Review updateReview(Long id, Integer rating, String content, String images);
    
    /**
     * 删除评价
     *
     * @param id 评价ID
     */
    void deleteReview(Long id);
    
    /**
     * 获取图书评价列表
     *
     * @param bookId 图书ID
     * @param pageable 分页参数
     * @return 评价分页列表
     */
    Page<Review> getReviewsByBookId(Long bookId, Pageable pageable);
    
    /**
     * 获取用户评价列表
     *
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 评价分页列表
     */
    Page<Review> getReviewsByUserId(Long userId, Pageable pageable);
    
    /**
     * 获取图书平均评分
     *
     * @param bookId 图书ID
     * @return 平均评分
     */
    Double getAverageRating(Long bookId);
    
    /**
     * 获取图书评价数量
     *
     * @param bookId 图书ID
     * @return 评价数量
     */
    long getReviewCount(Long bookId);
    
    /**
     * 检查用户是否已评价某订单
     *
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return 是否已评价
     */
    boolean hasReviewed(Long userId, Long orderId);
    
    /**
     * 获取所有评价列表（管理员）
     *
     * @param pageable 分页参数
     * @return 评价分页列表
     */
    Page<Review> getAllReviews(Pageable pageable);
    
    /**
     * 管理员删除评价
     *
     * @param id 评价ID
     */
    void adminDeleteReview(Long id);
}
