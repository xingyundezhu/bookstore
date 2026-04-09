package com.bookstore.service.impl;

import com.bookstore.entity.Review;
import com.bookstore.mapper.ReviewMapper;
import com.bookstore.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 评价服务实现类
 */
@Service
public class ReviewServiceImpl implements ReviewService {
    
    private final ReviewMapper reviewMapper;
    
    /**
     * 构造函数注入依赖
     *
     * @param reviewMapper 评价数据访问层
     */
    public ReviewServiceImpl(ReviewMapper reviewMapper) {
        this.reviewMapper = reviewMapper;
    }
    
    @Override
    @Transactional
    public Review createReview(Long userId, Long bookId, Long orderId, Integer rating, String content, String images, Integer isAnonymous) {
        if (orderId != null && hasReviewed(userId, orderId)) {
            throw new RuntimeException("已评价过此订单");
        }
        
        Review review = new Review();
        review.setUserId(userId);
        review.setBookId(bookId);
        review.setOrderId(orderId);
        review.setRating(rating);
        review.setContent(content);
        review.setImages(images);
        review.setIsAnonymous(isAnonymous != null && isAnonymous == 1);
        review.setStatus(1);
        
        reviewMapper.insert(review);
        return review;
    }
    
    @Override
    @Transactional
    public Review updateReview(Long id, Integer rating, String content, String images) {
        Review review = reviewMapper.findById(id);
        if (review == null) {
            throw new RuntimeException("评价不存在");
        }
        if (rating != null) review.setRating(rating);
        if (content != null) review.setContent(content);
        if (images != null) review.setImages(images);
        reviewMapper.update(review);
        return review;
    }
    
    @Override
    @Transactional
    public void deleteReview(Long id) {
        reviewMapper.deleteById(id);
    }
    
    @Override
    public Page<Review> getReviewsByBookId(Long bookId, Pageable pageable) {
        int offset = (int) pageable.getOffset();
        int limit = pageable.getPageSize();
        List<Review> reviews = reviewMapper.findByBookIdPaged(bookId, offset, limit);
        long total = reviewMapper.getReviewCount(bookId);
        return new PageImpl<>(reviews, pageable, total);
    }
    
    @Override
    public Page<Review> getReviewsByUserId(Long userId, Pageable pageable) {
        int offset = (int) pageable.getOffset();
        int limit = pageable.getPageSize();
        List<Review> reviews = reviewMapper.findByUserIdPaged(userId, offset, limit);
        long total = reviewMapper.countByUser(userId);
        return new PageImpl<>(reviews, pageable, total);
    }
    
    @Override
    public Double getAverageRating(Long bookId) {
        return reviewMapper.getAverageRating(bookId);
    }
    
    @Override
    public long getReviewCount(Long bookId) {
        return reviewMapper.getReviewCount(bookId);
    }
    
    @Override
    public boolean hasReviewed(Long userId, Long orderId) {
        return reviewMapper.countByUserIdAndOrderId(userId, orderId) > 0;
    }
    
    @Override
    public Page<Review> getAllReviews(Pageable pageable) {
        int offset = (int) pageable.getOffset();
        int limit = pageable.getPageSize();
        List<Review> reviews = reviewMapper.findAllPaged(offset, limit);
        long total = reviewMapper.countAll();
        return new PageImpl<>(reviews, pageable, total);
    }
    
    @Override
    public void adminDeleteReview(Long id) {
        reviewMapper.deleteById(id);
    }
}
