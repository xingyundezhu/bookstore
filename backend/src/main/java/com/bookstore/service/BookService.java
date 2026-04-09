package com.bookstore.service;

import com.bookstore.dto.BookDTO;
import com.bookstore.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * 图书服务接口
 */
public interface BookService {
    
    /**
     * 根据ID获取图书
     *
     * @param id 图书ID
     * @return 图书实体
     */
    Book getBookById(Long id);
    
    /**
     * 根据ID获取图书DTO
     *
     * @param id 图书ID
     * @return 图书DTO
     */
    BookDTO getBookDTOById(Long id);
    
    /**
     * 分页获取所有图书
     *
     * @param pageable 分页参数
     * @return 图书DTO分页列表
     */
    Page<BookDTO> getAllBooks(Pageable pageable);
    
    /**
     * 根据分类分页获取图书
     *
     * @param categoryId 分类ID
     * @param pageable 分页参数
     * @return 图书DTO分页列表
     */
    Page<BookDTO> getBooksByCategory(Long categoryId, Pageable pageable);
    
    /**
     * 搜索图书
     *
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 图书DTO分页列表
     */
    Page<BookDTO> searchBooks(String keyword, Pageable pageable);
    
    /**
     * 获取热门图书
     *
     * @param limit 数量限制
     * @return 热门图书列表
     */
    List<BookDTO> getHotBooks(int limit);
    
    /**
     * 获取新上架图书
     *
     * @param limit 数量限制
     * @return 新书列表
     */
    List<BookDTO> getNewBooks(int limit);
    
    /**
     * 获取推荐图书
     *
     * @param limit 数量限制
     * @return 推荐图书列表
     */
    List<BookDTO> getRecommendBooks(int limit);
    
    /**
     * 创建图书
     *
     * @param book 图书信息
     * @return 创建后的图书实体
     */
    Book createBook(Book book);
    
    /**
     * 更新图书
     *
     * @param id 图书ID
     * @param book 更新的图书信息
     * @return 更新后的图书实体
     */
    Book updateBook(Long id, Book book);
    
    /**
     * 删除图书
     *
     * @param id 图书ID
     */
    void deleteBook(Long id);
    
    /**
     * 更新库存
     *
     * @param id 图书ID
     * @param quantity 库存变化量（正数增加，负数减少）
     */
    void updateStock(Long id, Integer quantity);
    
    /**
     * 获取管理员图书列表
     *
     * @param pageable 分页参数
     * @return 图书DTO分页列表
     */
    Page<BookDTO> getAdminBooks(Pageable pageable);
    
    /**
     * 获取销量排行榜（分页）
     *
     * @param page 页码
     * @param size 每页数量
     * @param categoryId 分类ID（可选）
     * @return 销量排行榜分页数据
     */
    Page<BookDTO> getSalesRanking(int page, int size, Long categoryId);
    
    /**
     * 获取销量统计数据
     *
     * @return 销量统计数据
     */
    Object getSalesStatistics();
    
    /**
     * 更新图书销量
     *
     * @param id 图书ID
     * @param sales 销量
     */
    void updateBookSales(Long id, Integer sales);
    
    /**
     * 上传图书封面图片
     *
     * @param file 图片文件
     * @return 图片访问URL
     * @throws java.io.IOException IO异常
     */
    String uploadCover(org.springframework.web.multipart.MultipartFile file) throws java.io.IOException;
}
