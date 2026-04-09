package com.bookstore.service.impl;

import com.bookstore.dto.BookDTO;
import com.bookstore.entity.UserBehavior;
import com.bookstore.mapper.UserBehaviorMapper;
import com.bookstore.service.FavoriteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 收藏服务实现类
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {
    
    private final UserBehaviorMapper userBehaviorMapper;
    
    public FavoriteServiceImpl(UserBehaviorMapper userBehaviorMapper) {
        this.userBehaviorMapper = userBehaviorMapper;
    }
    
    @Override
    @Transactional
    public void addFavorite(Long userId, Long bookId) {
        if (!isFavorited(userId, bookId)) {
            userBehaviorMapper.insertBehavior(userId, bookId, UserBehavior.BEHAVIOR_FAVORITE);
        }
    }
    
    @Override
    @Transactional
    public void removeFavorite(Long userId, Long bookId) {
        userBehaviorMapper.deleteByUserIdAndBookIdAndType(userId, bookId, UserBehavior.BEHAVIOR_FAVORITE);
    }
    
    @Override
    public Page<BookDTO> getUserFavorites(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<BookDTO> favorites = userBehaviorMapper.findFavoritesByUserId(userId, pageable);
        long total = userBehaviorMapper.countFavoritesByUserId(userId);
        return new PageImpl<>(favorites, pageable, total);
    }
    
    @Override
    public boolean isFavorited(Long userId, Long bookId) {
        return userBehaviorMapper.existsByUserIdAndBookIdAndType(userId, bookId, UserBehavior.BEHAVIOR_FAVORITE) > 0;
    }
    
    @Override
    public long getFavoriteCount(Long userId) {
        return userBehaviorMapper.countFavoritesByUserId(userId);
    }
}
