package com.bookstore.service;

import com.bookstore.dto.BookDTO;
import java.util.List;

/**
 * 推荐服务接口
 */
public interface RecommendationService {
    
    /**
     * 获取个性化推荐图书
     *
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 推荐图书列表
     */
    List<BookDTO> getPersonalizedRecommendations(Long userId, int limit);
    
    /**
     * 获取相似图书
     *
     * @param bookId 图书ID
     * @param limit 数量限制
     * @return 相似图书列表
     */
    List<BookDTO> getSimilarBooks(Long bookId, int limit);
    
    /**
     * 获取热门推荐图书
     *
     * @param limit 数量限制
     * @return 热门图书列表
     */
    List<BookDTO> getHotRecommendations(int limit);
    
    /**
     * 获取新书推荐
     *
     * @param limit 数量限制
     * @return 新书列表
     */
    List<BookDTO> getNewRecommendations(int limit);
    
    /**
     * 记录用户行为
     *
     * @param userId 用户ID
     * @param bookId 图书ID
     * @param behaviorType 行为类型：1-浏览，2-收藏，3-加购，4-购买，5-评价
     */
    void recordUserBehavior(Long userId, Long bookId, Integer behaviorType);
}
