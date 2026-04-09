package com.bookstore.service.impl;

import com.bookstore.dto.BookDTO;
import com.bookstore.entity.Book;
import com.bookstore.entity.Category;
import com.bookstore.mapper.BookMapper;
import com.bookstore.mapper.CategoryMapper;
import com.bookstore.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 图书服务实现类
 */
@Service
public class BookServiceImpl implements BookService {
    
    private final BookMapper bookMapper;
    private final CategoryMapper categoryMapper;
    
    public BookServiceImpl(BookMapper bookMapper, CategoryMapper categoryMapper) {
        this.bookMapper = bookMapper;
        this.categoryMapper = categoryMapper;
    }
    
    @Override
    public Book getBookById(Long id) {
        Book book = bookMapper.findById(id);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }
        return book;
    }
    
    @Override
    public BookDTO getBookDTOById(Long id) {
        Book book = getBookById(id);
        return convertToDTO(book);
    }
    
    @Override
    public Page<BookDTO> getAllBooks(Pageable pageable) {
        String sortBy = "sales";
        boolean isAsc = false;
        
        if (pageable.getSort().isSorted()) {
            for (org.springframework.data.domain.Sort.Order order : pageable.getSort()) {
                sortBy = order.getProperty();
                isAsc = order.isAscending();
                break;
            }
        }
        
        List<Book> books = getBooksWithSort(null, sortBy, isAsc);
        return createPagedResult(books, pageable);
    }
    
    @Override
    public Page<BookDTO> getBooksByCategory(Long categoryId, Pageable pageable) {
        String sortBy = "sales";
        boolean isAsc = false;
        
        if (pageable.getSort().isSorted()) {
            for (org.springframework.data.domain.Sort.Order order : pageable.getSort()) {
                sortBy = order.getProperty();
                isAsc = order.isAscending();
                break;
            }
        }
        
        List<Book> books = getBooksWithSort(categoryId, sortBy, isAsc);
        return createPagedResult(books, pageable);
    }
    
    private List<Book> getBooksWithSort(Long categoryId, String sortBy, boolean isAsc) {
        if (categoryId == null) {
            switch (sortBy) {
                case "price":
                    return isAsc ? bookMapper.findAllByPriceAsc() : bookMapper.findAllByPriceDesc();
                case "createTime":
                    return isAsc ? bookMapper.findAllByCreateTimeAsc() : bookMapper.findAllByCreateTimeDesc();
                case "sales":
                default:
                    return isAsc ? bookMapper.findAllBySalesAsc() : bookMapper.findAllBySalesDesc();
            }
        } else {
            switch (sortBy) {
                case "price":
                    return isAsc ? bookMapper.findByCategoryIdByPriceAsc(categoryId) : bookMapper.findByCategoryIdByPriceDesc(categoryId);
                case "createTime":
                    return isAsc ? bookMapper.findByCategoryIdByCreateTimeAsc(categoryId) : bookMapper.findByCategoryIdByCreateTimeDesc(categoryId);
                case "sales":
                default:
                    return isAsc ? bookMapper.findByCategoryIdBySalesAsc(categoryId) : bookMapper.findByCategoryIdBySalesDesc(categoryId);
            }
        }
    }
    
    private Page<BookDTO> createPagedResult(List<Book> books, Pageable pageable) {
        long total = books.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), books.size());
        List<Book> pagedBooks = start < books.size() ? books.subList(start, end) : new java.util.ArrayList<>();
        List<BookDTO> dtos = pagedBooks.stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, total);
    }
    
    @Override
    public Page<BookDTO> searchBooks(String keyword, Pageable pageable) {
        List<Book> books = bookMapper.search(keyword);
        List<BookDTO> dtos = books.stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, books.size());
    }
    
    @Override
    public List<BookDTO> getHotBooks(int limit) {
        return bookMapper.findHotBooks(limit).stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    @Override
    public List<BookDTO> getNewBooks(int limit) {
        return bookMapper.findNewBooks(limit).stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    @Override
    public List<BookDTO> getRecommendBooks(int limit) {
        return bookMapper.findRecommendBooks(limit).stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public Book createBook(Book book) {
        book.setStatus(1);
        book.setSales(0);
        bookMapper.insert(book);
        return book;
    }
    
    @Override
    @Transactional
    public Book updateBook(Long id, Book book) {
        Book existingBook = getBookById(id);
        book.setId(id);
        if (book.getTitle() == null) book.setTitle(existingBook.getTitle());
        if (book.getAuthor() == null) book.setAuthor(existingBook.getAuthor());
        if (book.getPublisher() == null) book.setPublisher(existingBook.getPublisher());
        if (book.getPublishDate() == null) book.setPublishDate(existingBook.getPublishDate());
        if (book.getIsbn() == null) book.setIsbn(existingBook.getIsbn());
        if (book.getCategoryId() == null) book.setCategoryId(existingBook.getCategoryId());
        if (book.getPrice() == null) book.setPrice(existingBook.getPrice());
        if (book.getOriginalPrice() == null) book.setOriginalPrice(existingBook.getOriginalPrice());
        if (book.getDiscount() == null) book.setDiscount(existingBook.getDiscount());
        if (book.getStock() == null) book.setStock(existingBook.getStock());
        if (book.getCoverImage() == null) book.setCoverImage(existingBook.getCoverImage());
        if (book.getImages() == null) book.setImages(existingBook.getImages());
        if (book.getDescription() == null) book.setDescription(existingBook.getDescription());
        if (book.getPages() == null) book.setPages(existingBook.getPages());
        if (book.getLanguage() == null) book.setLanguage(existingBook.getLanguage());
        if (book.getBinding() == null) book.setBinding(existingBook.getBinding());
        if (book.getStatus() == null) book.setStatus(existingBook.getStatus());
        if (book.getIsHot() == null) book.setIsHot(existingBook.getIsHot());
        if (book.getIsNew() == null) book.setIsNew(existingBook.getIsNew());
        if (book.getIsRecommend() == null) book.setIsRecommend(existingBook.getIsRecommend());
        bookMapper.update(book);
        return book;
    }
    
    @Override
    @Transactional
    public void deleteBook(Long id) {
        bookMapper.deleteById(id);
    }
    
    @Override
    @Transactional
    public void updateStock(Long id, Integer quantity) {
        if (quantity > 0) {
            bookMapper.increaseStock(id, quantity);
        } else {
            bookMapper.decreaseStock(id, -quantity);
        }
    }
    
    @Override
    public Page<BookDTO> getAdminBooks(Pageable pageable) {
        List<Book> allBooks = bookMapper.findAll();
        long total = allBooks.size();
        
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allBooks.size());
        List<Book> pagedBooks = start < allBooks.size() ? allBooks.subList(start, end) : new java.util.ArrayList<>();
        
        List<BookDTO> dtos = pagedBooks.stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, total);
    }
    
    @Override
    public Page<BookDTO> getSalesRanking(int page, int size, Long categoryId) {
        List<Book> allBooks;
        long total;
        
        if (categoryId != null) {
            allBooks = bookMapper.findTopSellingByCategoryPaged(categoryId);
            total = bookMapper.countActiveByCategory(categoryId);
        } else {
            allBooks = bookMapper.findTopSellingPaged();
            total = bookMapper.countActive();
        }
        
        int start = page * size;
        int end = Math.min(start + size, allBooks.size());
        List<Book> pagedBooks = allBooks.subList(start, end);
        
        List<BookDTO> dtos = pagedBooks.stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageImpl<>(dtos, org.springframework.data.domain.PageRequest.of(page, size), total);
    }
    
    @Override
    public Object getSalesStatistics() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalSales", bookMapper.sumTotalSales());
        stats.put("activeBooks", bookMapper.countActive());
        
        List<Category> categories = categoryMapper.findAll();
        java.util.List<java.util.Map<String, Object>> categorySales = new java.util.ArrayList<>();
        for (Category category : categories) {
            java.util.Map<String, Object> catStat = new java.util.HashMap<>();
            catStat.put("categoryId", category.getId());
            catStat.put("categoryName", category.getName());
            catStat.put("sales", bookMapper.sumSalesByCategory(category.getId()));
            categorySales.add(catStat);
        }
        stats.put("categorySales", categorySales);
        
        return stats;
    }
    
    @Override
    @Transactional
    public void updateBookSales(Long id, Integer sales) {
        if (sales < 0) {
            throw new RuntimeException("销量不能为负数");
        }
        bookMapper.updateSales(id, sales);
    }
    
    @Override
    public String uploadCover(org.springframework.web.multipart.MultipartFile file) throws java.io.IOException {
        String uploadDir = "uploads/covers/";
        java.nio.file.Path dirPath = java.nio.file.Paths.get(uploadDir);
        if (!java.nio.file.Files.exists(dirPath)) {
            java.nio.file.Files.createDirectories(dirPath);
        }
        
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : ".jpg";
        String filename = java.util.UUID.randomUUID().toString() + extension;
        
        java.nio.file.Path filePath = dirPath.resolve(filename);
        file.transferTo(filePath.toFile());
        
        return "/uploads/covers/" + filename;
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
