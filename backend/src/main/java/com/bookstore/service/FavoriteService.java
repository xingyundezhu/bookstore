package com.bookstore.service;

import com.bookstore.dto.BookDTO;
import org.springframework.data.domain.Page;

/**
 * 收藏服务接口
 */
public interface FavoriteService {
    
    /**
     * 添加收藏
     *
     * @param userId 用户ID
     * @param bookId 图书ID
     */
    void addFavorite(Long userId, Long bookId);
    
    /**
     * 取消收藏
     *
     * @param userId 用户ID
     * @param bookId 图书ID
     */
    void removeFavorite(Long userId, Long bookId);
    
    /**
     * 获取用户收藏列表
     *
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页数量
     * @return 收藏的图书分页列表
     */
    Page<BookDTO> getUserFavorites(Long userId, int page, int size);
    
    /**
     * 检查是否已收藏
     *
     * @param userId 用户ID
     * @param bookId 图书ID
     * @return 是否已收藏
     */
    boolean isFavorited(Long userId, Long bookId);
    
    /**
     * 获取用户收藏数量
     *
     * @param userId 用户ID
     * @return 收藏数量
     */
    long getFavoriteCount(Long userId);
}
