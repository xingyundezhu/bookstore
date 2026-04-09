package com.bookstore.mapper;

import com.bookstore.dto.BookDTO;
import com.bookstore.entity.UserBehavior;
import org.apache.ibatis.annotations.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 用户行为数据访问层
 */
@Mapper
public interface UserBehaviorMapper {
    
    /**
     * 查询用户所有行为记录
     */
    @Select("SELECT * FROM t_user_behavior WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<UserBehavior> findByUserId(@Param("userId") Long userId);
    
    /**
     * 查询用户指定类型的行为记录
     */
    @Select("SELECT * FROM t_user_behavior WHERE user_id = #{userId} AND behavior_type = #{behaviorType} ORDER BY create_time DESC")
    List<UserBehavior> findByUserIdAndType(@Param("userId") Long userId, @Param("behaviorType") Integer behaviorType);
    
    /**
     * 查询图书相关的行为记录
     */
    @Select("SELECT * FROM t_user_behavior WHERE book_id = #{bookId} ORDER BY create_time DESC")
    List<UserBehavior> findByBookId(@Param("bookId") Long bookId);
    
    /**
     * 查询相似用户的行为记录（用于协同过滤推荐）
     */
    @Select("SELECT ub.* FROM t_user_behavior ub WHERE ub.user_id IN " +
            "(SELECT ub2.user_id FROM t_user_behavior ub2 WHERE ub2.book_id = #{bookId} AND ub2.user_id != #{excludeUserId}) " +
            "ORDER BY ub.create_time DESC LIMIT #{limit}")
    List<UserBehavior> findSimilarUserBehaviors(@Param("bookId") Long bookId, @Param("excludeUserId") Long excludeUserId, @Param("limit") int limit);
    
    /**
     * 记录用户行为
     */
    @Insert("INSERT INTO t_user_behavior(user_id, book_id, behavior_type, score, create_time) VALUES(#{userId}, #{bookId}, #{behaviorType}, #{score}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserBehavior behavior);
    
    /**
     * 插入用户行为（简化版）
     */
    @Insert("INSERT INTO t_user_behavior(user_id, book_id, behavior_type, score, create_time) VALUES(#{userId}, #{bookId}, #{behaviorType}, 1.0, NOW())")
    int insertBehavior(@Param("userId") Long userId, @Param("bookId") Long bookId, @Param("behaviorType") Integer behaviorType);
    
    /**
     * 查询用户指定类型行为涉及的图书ID列表
     */
    @Select("SELECT DISTINCT book_id FROM t_user_behavior WHERE user_id = #{userId} AND behavior_type = #{behaviorType}")
    List<Long> findBookIdsByUserIdAndType(@Param("userId") Long userId, @Param("behaviorType") Integer behaviorType);
    
    /**
     * 检查用户是否对某图书有指定类型的行为
     */
    @Select("SELECT COUNT(*) FROM t_user_behavior WHERE user_id = #{userId} AND book_id = #{bookId} AND behavior_type = #{behaviorType}")
    int existsByUserIdAndBookIdAndType(@Param("userId") Long userId, @Param("bookId") Long bookId, @Param("behaviorType") Integer behaviorType);
    
    /**
     * 删除用户对某图书的指定类型行为
     */
    @Delete("DELETE FROM t_user_behavior WHERE user_id = #{userId} AND book_id = #{bookId} AND behavior_type = #{behaviorType}")
    int deleteByUserIdAndBookIdAndType(@Param("userId") Long userId, @Param("bookId") Long bookId, @Param("behaviorType") Integer behaviorType);
    
    /**
     * 查询用户收藏的图书列表
     */
    @Select("SELECT b.*, c.name as category_name FROM t_book b " +
            "INNER JOIN t_user_behavior ub ON b.id = ub.book_id " +
            "LEFT JOIN t_category c ON b.category_id = c.id " +
            "WHERE ub.user_id = #{userId} AND ub.behavior_type = 2 " +
            "ORDER BY ub.create_time DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "title", column = "title"),
        @Result(property = "author", column = "author"),
        @Result(property = "price", column = "price"),
        @Result(property = "originalPrice", column = "original_price"),
        @Result(property = "coverImage", column = "cover_image"),
        @Result(property = "description", column = "description"),
        @Result(property = "stock", column = "stock"),
        @Result(property = "sales", column = "sales"),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "categoryName", column = "category_name"),
        @Result(property = "status", column = "status")
    })
    List<BookDTO> findFavoritesByUserId(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 统计用户收藏数量
     */
    @Select("SELECT COUNT(*) FROM t_user_behavior WHERE user_id = #{userId} AND behavior_type = 2")
    long countFavoritesByUserId(@Param("userId") Long userId);
}
