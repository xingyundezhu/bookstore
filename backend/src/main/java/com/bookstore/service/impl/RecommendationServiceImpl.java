package com.bookstore.service.impl;

import com.bookstore.dto.BookDTO;
import com.bookstore.entity.Book;
import com.bookstore.entity.UserBehavior;
import com.bookstore.mapper.BookMapper;
import com.bookstore.mapper.UserBehaviorMapper;
import com.bookstore.service.RecommendationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐服务实现类
 */
@Service
public class RecommendationServiceImpl implements RecommendationService {
    
    private final BookMapper bookMapper;
    private final UserBehaviorMapper userBehaviorMapper;
    
    /**
     * 构造函数注入依赖
     *
     * @param bookMapper 图书数据访问层
     * @param userBehaviorMapper 用户行为数据访问层
     */
    public RecommendationServiceImpl(BookMapper bookMapper, UserBehaviorMapper userBehaviorMapper) {
        this.bookMapper = bookMapper;
        this.userBehaviorMapper = userBehaviorMapper;
    }
    
    @Override
    public List<BookDTO> getPersonalizedRecommendations(Long userId, int limit) {
        List<UserBehavior> behaviors = userBehaviorMapper.findByUserId(userId);
        
        if (behaviors.isEmpty()) {
            return getHotRecommendations(limit);
        }
        
        Set<Long> purchasedBookIds = new HashSet<>(
            userBehaviorMapper.findBookIdsByUserIdAndType(userId, UserBehavior.BEHAVIOR_PURCHASE)
        );
        
        List<Long> similarUserBookIds = new ArrayList<>();
        for (UserBehavior behavior : behaviors) {
            if (behavior.getBehaviorType() == UserBehavior.BEHAVIOR_PURCHASE) {
                List<UserBehavior> similarBehaviors = userBehaviorMapper.findSimilarUserBehaviors(
                    behavior.getBookId(), userId, 50
                );
                for (UserBehavior sb : similarBehaviors) {
                    if (!purchasedBookIds.contains(sb.getBookId())) {
                        similarUserBookIds.add(sb.getBookId());
                    }
                }
            }
        }
        
        Map<Long, Integer> bookScoreMap = new HashMap<>();
        for (Long bookId : similarUserBookIds) {
            bookScoreMap.merge(bookId, 1, Integer::sum);
        }
        
        List<Long> recommendedBookIds = bookScoreMap.entrySet().stream()
            .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
            .limit(limit)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        
        if (recommendedBookIds.isEmpty()) {
            return getHotRecommendations(limit);
        }
        
        return recommendedBookIds.stream()
            .map(bookMapper::findById)
            .filter(Objects::nonNull)
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<BookDTO> getSimilarBooks(Long bookId, int limit) {
        Book book = bookMapper.findById(bookId);
        if (book == null) {
            return Collections.emptyList();
        }
        
        List<Book> books = bookMapper.findByCategoryId(book.getCategoryId());
        return books.stream()
            .filter(b -> !b.getId().equals(bookId))
            .limit(limit)
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<BookDTO> getHotRecommendations(int limit) {
        return bookMapper.findHotBooks(limit).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<BookDTO> getNewRecommendations(int limit) {
        return bookMapper.findNewBooks(limit).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void recordUserBehavior(Long userId, Long bookId, Integer behaviorType) {
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setBookId(bookId);
        behavior.setBehaviorType(behaviorType);
        
        double score = 1.0;
        switch (behaviorType) {
            case UserBehavior.BEHAVIOR_VIEW: score = 1.0; break;
            case UserBehavior.BEHAVIOR_FAVORITE: score = 2.0; break;
            case UserBehavior.BEHAVIOR_CART: score = 3.0; break;
            case UserBehavior.BEHAVIOR_PURCHASE: score = 5.0; break;
            case UserBehavior.BEHAVIOR_REVIEW: score = 4.0; break;
        }
        behavior.setScore(score);
        
        userBehaviorMapper.insert(behavior);
    }
    
    private BookDTO convertToDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setPublisher(book.getPublisher());
        dto.setPublishDate(book.getPublishDate());
        dto.setIsbn(book.getIsbn());
        dto.setCategoryId(book.getCategoryId());
        dto.setCategoryName(book.getCategoryName());
        dto.setPrice(book.getPrice());
        dto.setOriginalPrice(book.getOriginalPrice());
        dto.setDiscount(book.getDiscount());
        dto.setStock(book.getStock());
        dto.setSales(book.getSales());
        dto.setCoverImage(book.getCoverImage());
        dto.setImages(book.getImages());
        dto.setDescription(book.getDescription());
        dto.setPages(book.getPages());
        dto.setLanguage(book.getLanguage());
        dto.setBinding(book.getBinding());
        dto.setStatus(book.getStatus());
        dto.setIsHot(book.getIsHot());
        dto.setIsNew(book.getIsNew());
        dto.setIsRecommend(book.getIsRecommend());
        dto.setCreateTime(book.getCreateTime());
        dto.setUpdateTime(book.getUpdateTime());
        return dto;
    }
}
