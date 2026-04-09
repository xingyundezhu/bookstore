package com.bookstore.mapper;

import com.bookstore.entity.Review;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 评价数据访问层
 *
 * @author bookstore
 * @since 1.0
 */
@Mapper
public interface ReviewMapper {
    
    /**
     * 根据ID查询评价（关联用户和图书信息）
     *
     * @param id 评价ID
     * @return 评价详情
     */
    @Select("SELECT r.*, u.username, u.nickname as user_nickname, u.avatar as user_avatar, " +
            "b.title as book_title, b.cover_image as book_cover " +
            "FROM t_review r " +
            "LEFT JOIN t_user u ON r.user_id = u.id " +
            "LEFT JOIN t_book b ON r.book_id = b.id " +
            "WHERE r.id = #{id}")
    Review findById(@Param("id") Long id);
    
    /**
     * 查询所有评价（管理员）- 分页
     *
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 评价列表
     */
    @Select("SELECT r.*, u.username, u.nickname as user_nickname, u.avatar as user_avatar, " +
            "b.title as book_title, b.cover_image as book_cover " +
            "FROM t_review r " +
            "LEFT JOIN t_user u ON r.user_id = u.id " +
            "LEFT JOIN t_book b ON r.book_id = b.id " +
            "ORDER BY r.create_time DESC " +
            "LIMIT #{limit} OFFSET #{offset}")
    List<Review> findAllPaged(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 统计所有评价数量
     *
     * @return 评价总数
     */
    @Select("SELECT COUNT(*) FROM t_review")
    Long countAll();

    @Select("SELECT r.*, u.username, u.nickname as user_nickname, u.avatar as user_avatar, " +
            "b.title as book_title, b.cover_image as book_cover " +
            "FROM t_review r " +
            "LEFT JOIN t_user u ON r.user_id = u.id " +
            "LEFT JOIN t_book b ON r.book_id = b.id " +
            "ORDER BY r.create_time DESC")
    List<Review> findAll();
    
    /**
     * 查询图书评价列表 - 分页
     *
     * @param bookId 图书ID
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 评价列表
     */
    @Select("SELECT r.*, u.username, u.nickname as user_nickname, u.avatar as user_avatar, " +
            "b.title as book_title, b.cover_image as book_cover " +
            "FROM t_review r " +
            "LEFT JOIN t_user u ON r.user_id = u.id " +
            "LEFT JOIN t_book b ON r.book_id = b.id " +
            "WHERE r.book_id = #{bookId} AND r.status = 1 " +
            "ORDER BY r.create_time DESC " +
            "LIMIT #{limit} OFFSET #{offset}")
    List<Review> findByBookIdPaged(@Param("bookId") Long bookId, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 查询图书评价列表
     *
     * @param bookId 图书ID
     * @return 评价列表
     */
    @Select("SELECT r.*, u.username, u.nickname as user_nickname, u.avatar as user_avatar, " +
            "b.title as book_title, b.cover_image as book_cover " +
            "FROM t_review r " +
            "LEFT JOIN t_user u ON r.user_id = u.id " +
            "LEFT JOIN t_book b ON r.book_id = b.id " +
            "WHERE r.book_id = #{bookId} AND r.status = 1 " +
            "ORDER BY r.create_time DESC")
    List<Review> findByBookId(@Param("bookId") Long bookId);
    
    /**
     * 查询用户评价列表 - 分页
     *
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 评价列表
     */
    @Select("SELECT r.*, u.username, u.nickname as user_nickname, u.avatar as user_avatar, " +
            "b.title as book_title, b.cover_image as book_cover " +
            "FROM t_review r " +
            "LEFT JOIN t_user u ON r.user_id = u.id " +
            "LEFT JOIN t_book b ON r.book_id = b.id " +
            "WHERE r.user_id = #{userId} " +
            "ORDER BY r.create_time DESC " +
            "LIMIT #{limit} OFFSET #{offset}")
    List<Review> findByUserIdPaged(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 查询用户评价列表
     *
     * @param userId 用户ID
     * @return 评价列表
     */
    @Select("SELECT r.*, u.username, u.nickname as user_nickname, u.avatar as user_avatar, " +
            "b.title as book_title, b.cover_image as book_cover " +
            "FROM t_review r " +
            "LEFT JOIN t_user u ON r.user_id = u.id " +
            "LEFT JOIN t_book b ON r.book_id = b.id " +
            "WHERE r.user_id = #{userId} " +
            "ORDER BY r.create_time DESC")
    List<Review> findByUserId(@Param("userId") Long userId);
    
    /**
     * 检查是否已评价
     *
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return 评价数量
     */
    @Select("SELECT COUNT(*) FROM t_review WHERE user_id = #{userId} AND order_id = #{orderId}")
    int countByUserIdAndOrderId(@Param("userId") Long userId, @Param("orderId") Long orderId);
    
    /**
     * 创建评价
     *
     * @param review 评价实体
     * @return 影响行数
     */
    @Insert("INSERT INTO t_review(user_id, book_id, order_id, rating, content, images, is_anonymous, status, create_time, update_time) " +
            "VALUES(#{userId}, #{bookId}, #{orderId}, #{rating}, #{content}, #{images}, #{isAnonymous}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Review review);
    
    /**
     * 更新评价
     *
     * @param review 评价实体
     * @return 影响行数
     */
    @Update("UPDATE t_review SET rating=#{rating}, content=#{content}, images=#{images}, update_time=NOW() WHERE id=#{id}")
    int update(Review review);
    
    /**
     * 删除评价
     *
     * @param id 评价ID
     * @return 影响行数
     */
    @Delete("DELETE FROM t_review WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    /**
     * 计算图书平均评分
     *
     * @param bookId 图书ID
     * @return 平均评分
     */
    @Select("SELECT AVG(rating) FROM t_review WHERE book_id = #{bookId} AND status = 1")
    Double getAverageRating(@Param("bookId") Long bookId);
    
    /**
     * 计算图书评价数量
     *
     * @param bookId 图书ID
     * @return 评价数量
     */
    @Select("SELECT COUNT(*) FROM t_review WHERE book_id = #{bookId} AND status = 1")
    Long getReviewCount(@Param("bookId") Long bookId);

    /**
     * 统计用户评价数量
     *
     * @param userId 用户ID
     * @return 评价数量
     */
    @Select("SELECT COUNT(*) FROM t_review WHERE user_id = #{userId}")
    Long countByUser(@Param("userId") Long userId);
}
