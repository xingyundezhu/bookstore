package com.bookstore.mapper;

import com.bookstore.entity.Book;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 图书数据访问层
 */
@Mapper
public interface BookMapper {
    
    /**
     * 根据ID查询图书（关联分类名称）
     */
    @Select("SELECT b.*, c.name as category_name FROM t_book b LEFT JOIN t_category c ON b.category_id = c.id WHERE b.id = #{id}")
    Book findById(@Param("id") Long id);
    
    /**
     * 查询热门图书
     */
    @Select("SELECT b.*, c.name as category_name FROM t_book b LEFT JOIN t_category c ON b.category_id = c.id WHERE b.status = 1 ORDER BY b.sales DESC LIMIT #{limit}")
    List<Book> findHotBooks(@Param("limit") int limit);
    
    /**
     * 查询新上架图书
     */
    @Select("SELECT b.*, c.name as category_name FROM t_book b LEFT JOIN t_category c ON b.category_id = c.id WHERE b.status = 1 ORDER BY b.create_time DESC LIMIT #{limit}")
    List<Book> findNewBooks(@Param("limit") int limit);
    
    /**
     * 查询推荐图书
     */
    @Select("SELECT b.*, c.name as category_name FROM t_book b LEFT JOIN t_category c ON b.category_id = c.id WHERE b.status = 1 AND b.is_recommend = 1 ORDER BY b.sales DESC LIMIT #{limit}")
    List<Book> findRecommendBooks(@Param("limit") int limit);
    
    /**
     * 根据分类查询图书
     */
    @Select("SELECT b.*, c.name as category_name FROM t_book b LEFT JOIN t_category c ON b.category_id = c.id WHERE b.status = 1 AND b.category_id = #{categoryId} ORDER BY b.sales DESC")
    List<Book> findByCategoryId(@Param("categoryId") Long categoryId);
    
    /**
     * 搜索图书（按标题或作者）
     */
    @Select("SELECT b.*, c.name as category_name FROM t_book b LEFT JOIN t_category c ON b.category_id = c.id WHERE b.status = 1 AND (b.title LIKE CONCAT('%', #{keyword}, '%') OR b.author LIKE CONCAT('%', #{keyword}, '%'))")
    List<Book> search(@Param("keyword") String keyword);
    
    /**
     * 查询所有图书（按销量降序）
     */
    @Select("SELECT b.*, c.name as category_name FROM t_book b LEFT JOIN t_category c ON b.category_id = c.id WHERE b.status = 1 ORDER BY b.sales DESC")
    List<Book> findAllBySalesDesc();
    
    /**
     * 查询所有图书（按销量升序）
     */
    @Select("SELECT b.*, c.name as category_name FROM t_book b LEFT JOIN t_category c ON b.category_id = c.id WHERE b.status = 1 ORDER BY b.sales ASC")
    List<Book> findAllBySalesAsc();
    
    /**
     * 查询所有图书（按价格降序）
     */
    @Select("SELECT b.*, c.name as category_name FROM t_book b LEFT JOIN t_category c ON b.category_id = c.id WHERE b.status = 1 ORDER BY b.price DESC")
    List<Book> findAllByPriceDesc();
    
    /**
     * 查询所有图书（按价格升序）
     */
    @Select("SELECT b.*, c.name as category_name FROM t_book b LEFT JOIN t_category c ON b.category_id = c.id WHERE b.status = 1 ORDER BY b.price ASC")
    List<Book> findAllByPriceAsc();
    
    /**
     * 查询所有图书（按创建时间降序-最新上架）
     */
    @Select("SELECT b.*, c.name as category_name FROM t_book b LEFT JOIN t_category c ON b.category_id = c.id WHERE b.status = 1 ORDER BY b.create_time DESC")
    List<Book> findAllByCreateTimeDesc();
    
    /**
     * 查询所有图书（按创建时间升序）
     */
    @Select("SELECT b.*, c.name as category_name FROM t_book b LEFT JOIN t_category c ON b.category_id = c.id WHERE b.status = 1 ORDER BY b.create_time ASC")
    List<Book> findAllByCreateTimeAsc();
    
    /**
     * 根据分类查询图书（按销量降序）
     */
    @Select("SELECT b.*, c.name as category_name FROM t_book b LEFT JOIN t_category c ON b.category_id = c.id WHERE b.status = 1 AND b.category_id = #{categoryId} ORDER BY b.sales DESC")
    List<Book> findByCategoryIdBySalesDesc(@Param("categoryId") Long categoryId);
    
    /**
     * 根据分类查询图书（按销量升序）
     */
    @Select("SELECT b.*, c.name as category_name FROM t_book b LEFT JOIN t_category c ON b.category_id = c.id WHERE b.status = 1 AND b.category_id = #{categoryId} ORDER BY b.sales ASC")
    List<Book> findByCategoryIdBySalesAsc(@Param("categoryId") Long categoryId);
    
    /**
     * 根据分类查询图书（按价格降序）
     */
    @Select("SELECT b.*, c.name as category_name FROM t_book b LEFT JOIN t_category c ON b.category_id = c.id WHERE b.status = 1 AND b.category_id = #{categoryId} ORDER BY b.price DESC")
    List<Book> findByCategoryIdByPriceDesc(@Param("categoryId") Long categoryId);
    
    /**
     * 根据分类查询图书（按价格升序）
     */
    @Select("SELECT b.*, c.name as category_name FROM t_book b LEFT JOIN t_category c ON b.category_id = c.id WHERE b.status = 1 AND b.category_id = #{categoryId} ORDER BY b.price ASC")
    List<Book> findByCategoryIdByPriceAsc(@Param("categoryId") Long categoryId);
    
    /**
     * 根据分类查询图书（按创建时间降序）
     */
    @Select("SELECT b.*, c.name as category_name FROM t_book b LEFT JOIN t_category c ON b.category_id = c.id WHERE b.status = 1 AND b.category_id = #{categoryId} ORDER BY b.create_time DESC")
    List<Book> findByCategoryIdByCreateTimeDesc(@Param("categoryId") Long categoryId);
    
    /**
     * 根据分类查询图书（按创建时间升序）
     */
    @Select("SELECT b.*, c.name as category_name FROM t_book b LEFT JOIN t_category c ON b.category_id = c.id WHERE b.status = 1 AND b.category_id = #{categoryId} ORDER BY b.create_time ASC")
    List<Book> findByCategoryIdByCreateTimeAsc(@Param("categoryId") Long categoryId);
    
    /**
     * 插入新图书
     */
    @Insert("INSERT INTO t_book(title, author, publisher, publish_date, isbn, category_id, price, original_price, discount, stock, sales, cover_image, images, description, pages, language, binding, status, is_hot, is_new, is_recommend, create_time, update_time) " +
            "VALUES(#{title}, #{author}, #{publisher}, #{publishDate}, #{isbn}, #{categoryId}, #{price}, #{originalPrice}, #{discount}, #{stock}, 0, #{coverImage}, #{images}, #{description}, #{pages}, #{language}, #{binding}, #{status}, #{isHot}, #{isNew}, #{isRecommend}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Book book);
    
    /**
     * 更新图书信息
     */
    @Update("UPDATE t_book SET title=#{title}, author=#{author}, publisher=#{publisher}, publish_date=#{publishDate}, isbn=#{isbn}, category_id=#{categoryId}, " +
            "price=#{price}, original_price=#{originalPrice}, discount=#{discount}, stock=#{stock}, cover_image=#{coverImage}, images=#{images}, " +
            "description=#{description}, pages=#{pages}, language=#{language}, binding=#{binding}, status=#{status}, is_hot=#{isHot}, is_new=#{isNew}, is_recommend=#{isRecommend}, update_time=NOW() WHERE id=#{id}")
    int update(Book book);
    
    /**
     * 减少库存（同时增加销量）
     */
    @Update("UPDATE t_book SET stock = stock - #{quantity}, sales = sales + #{quantity}, update_time=NOW() WHERE id=#{id} AND stock >= #{quantity}")
    int decreaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    /**
     * 增加库存
     */
    @Update("UPDATE t_book SET stock = stock + #{quantity}, update_time=NOW() WHERE id=#{id}")
    int increaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    /**
     * 增加库存并减少销量（用于取消订单）
     */
    @Update("UPDATE t_book SET stock = stock + #{quantity}, sales = GREATEST(0, sales - #{quantity}), update_time=NOW() WHERE id=#{id}")
    int increaseStockAndDecreaseSales(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    /**
     * 更新销量
     */
    @Update("UPDATE t_book SET sales = #{sales}, update_time=NOW() WHERE id=#{id}")
    int updateSales(@Param("id") Long id, @Param("sales") Integer sales);
    
    /**
     * 删除图书
     */
    @Delete("DELETE FROM t_book WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    /**
     * 查询所有图书（管理员）
     */
    @Select("SELECT b.*, c.name as category_name FROM t_book b LEFT JOIN t_category c ON b.category_id = c.id ORDER BY b.create_time DESC")
    List<Book> findAll();
    
    /**
     * 统计上架图书数量
     */
    @Select("SELECT COUNT(*) FROM t_book WHERE status = 1")
    long countActive();
    
    /**
     * 统计总销量
     */
    @Select("SELECT IFNULL(SUM(sales), 0) FROM t_book")
    long sumTotalSales();
    
    /**
     * 统计分类销量
     */
    @Select("SELECT IFNULL(SUM(b.sales), 0) FROM t_book b WHERE b.category_id = #{categoryId}")
    long sumSalesByCategory(@Param("categoryId") Long categoryId);
    
    /**
     * 查询销量排行榜（按分类）
     */
    @Select("SELECT b.*, c.name as category_name FROM t_book b LEFT JOIN t_category c ON b.category_id = c.id WHERE b.status = 1 AND b.category_id = #{categoryId} ORDER BY b.sales DESC LIMIT #{limit}")
    List<Book> findTopSellingByCategory(@Param("categoryId") Long categoryId, @Param("limit") int limit);
    
    /**
     * 查询销量排行榜（全部）
     */
    @Select("SELECT b.*, c.name as category_name FROM t_book b LEFT JOIN t_category c ON b.category_id = c.id WHERE b.status = 1 ORDER BY b.sales DESC LIMIT #{limit}")
    List<Book> findTopSelling(@Param("limit") int limit);
    
    /**
     * 查询销量排行榜（分页，按分类）
     */
    @Select("SELECT b.*, c.name as category_name FROM t_book b LEFT JOIN t_category c ON b.category_id = c.id WHERE b.status = 1 AND b.category_id = #{categoryId} ORDER BY b.sales DESC")
    List<Book> findTopSellingByCategoryPaged(@Param("categoryId") Long categoryId);
    
    /**
     * 查询销量排行榜（分页，全部）
     */
    @Select("SELECT b.*, c.name as category_name FROM t_book b LEFT JOIN t_category c ON b.category_id = c.id WHERE b.status = 1 ORDER BY b.sales DESC")
    List<Book> findTopSellingPaged();
    
    /**
     * 统计上架图书数量（按分类）
     */
    @Select("SELECT COUNT(*) FROM t_book WHERE status = 1 AND category_id = #{categoryId}")
    long countActiveByCategory(@Param("categoryId") Long categoryId);
}
