package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.BookDTO;
import com.bookstore.entity.Book;
import com.bookstore.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 图书控制器
 *
 */
@Tag(name = "图书管理", description = "图书浏览和管理功能")
@RestController
@RequestMapping("/books")
public class BookController {
    
    private final BookService bookService;
    
    /**
     * 构造函数注入BookService
     *
     * @param bookService 图书服务
     */
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
    
    /**
     * 获取图书列表
     *
     * @param page 页码
     * @param size 每页数量
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 图书分页列表
     */
    @Operation(summary = "获取图书列表", description = "分页获取图书列表")
    @GetMapping
    public ApiResponse<Page<BookDTO>> getBooks(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "12") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "sales") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ApiResponse.success(bookService.getAllBooks(pageable));
    }
    
    /**
     * 根据ID获取图书详情
     *
     * @param id 图书ID
     * @return 图书详情
     */
    @Operation(summary = "获取图书详情", description = "根据ID获取图书详细信息")
    @GetMapping("/{id}")
    public ApiResponse<BookDTO> getBook(@Parameter(description = "图书ID") @PathVariable Long id) {
        return ApiResponse.success(bookService.getBookDTOById(id));
    }
    
    /**
     * 搜索图书
     *
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页数量
     * @return 搜索结果
     */
    @Operation(summary = "搜索图书", description = "根据关键词搜索图书")
    @GetMapping("/search")
    public ApiResponse<Page<BookDTO>> searchBooks(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(bookService.searchBooks(keyword, pageable));
    }
    
    /**
     * 根据分类获取图书
     *
     * @param categoryId 分类ID
     * @param page 页码
     * @param size 每页数量
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 图书分页列表
     */
    @Operation(summary = "按分类获取图书", description = "根据分类ID获取图书列表")
    @GetMapping("/category/{categoryId}")
    public ApiResponse<Page<BookDTO>> getBooksByCategory(
            @Parameter(description = "分类ID") @PathVariable Long categoryId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "12") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "sales") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ApiResponse.success(bookService.getBooksByCategory(categoryId, pageable));
    }
    
    /**
     * 获取热门图书
     *
     * @param limit 获取数量
     * @return 热门图书列表
     */
    @Operation(summary = "获取热门图书", description = "获取销量最高的图书")
    @GetMapping("/hot")
    public ApiResponse<List<BookDTO>> getHotBooks(
            @Parameter(description = "获取数量") @RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.success(bookService.getHotBooks(limit));
    }
    
    /**
     * 获取新书上架
     *
     * @param limit 获取数量
     * @return 新书列表
     */
    @Operation(summary = "获取新书上架", description = "获取最新上架的图书")
    @GetMapping("/new")
    public ApiResponse<List<BookDTO>> getNewBooks(
            @Parameter(description = "获取数量") @RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.success(bookService.getNewBooks(limit));
    }
    
    /**
     * 添加图书
     *
     * @param book 图书信息
     * @return 添加后的图书
     */
    @Operation(summary = "添加图书", description = "管理员添加新图书")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ApiResponse<Book> createBook(@RequestBody Book book) {
        return ApiResponse.success("添加成功", bookService.createBook(book));
    }
    
    /**
     * 更新图书
     *
     * @param id 图书ID
     * @param book 更新的图书信息
     * @return 更新后的图书
     */
    @Operation(summary = "更新图书", description = "管理员更新图书信息")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<Book> updateBook(
            @Parameter(description = "图书ID") @PathVariable Long id, 
            @RequestBody Book book) {
        return ApiResponse.success("更新成功", bookService.updateBook(id, book));
    }
    
    /**
     * 删除图书
     *
     * @param id 图书ID
     * @return 操作结果
     */
    @Operation(summary = "删除图书", description = "管理员删除图书")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteBook(@Parameter(description = "图书ID") @PathVariable Long id) {
        bookService.deleteBook(id);
        return ApiResponse.success("删除成功", null);
    }
    
    /**
     * 上传图书封面
     *
     * @param file 封面图片文件
     * @return 图片URL
     */
    @Operation(summary = "上传图书封面", description = "管理员上传图书封面图片")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upload/cover")
    public ApiResponse<String> uploadCover(@RequestParam("file") MultipartFile file) throws IOException {
        String url = bookService.uploadCover(file);
        return ApiResponse.success("上传成功", url);
    }
}
