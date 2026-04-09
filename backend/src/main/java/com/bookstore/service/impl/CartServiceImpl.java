package com.bookstore.service.impl;

import com.bookstore.dto.CartDTO;
import com.bookstore.entity.Book;
import com.bookstore.entity.Cart;
import com.bookstore.mapper.BookMapper;
import com.bookstore.mapper.CartMapper;
import com.bookstore.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 购物车服务实现类
 */
@Service
public class CartServiceImpl implements CartService {
    
    private final CartMapper cartMapper;
    private final BookMapper bookMapper;
    
    /**
     * 构造函数注入依赖
     *
     * @param cartMapper 购物车数据访问层
     * @param bookMapper 图书数据访问层
     */
    public CartServiceImpl(CartMapper cartMapper, BookMapper bookMapper) {
        this.cartMapper = cartMapper;
        this.bookMapper = bookMapper;
    }
    
    @Override
    public List<CartDTO> getCartByUserId(Long userId) {
        List<Cart> carts = cartMapper.findByUserId(userId);
        return carts.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public Cart addToCart(Long userId, Long bookId, Integer quantity) {
        Book book = bookMapper.findById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }
        
        Cart existingCart = cartMapper.findByUserIdAndBookId(userId, bookId);
        if (existingCart != null) {
            int newQuantity = existingCart.getQuantity() + quantity;
            cartMapper.updateQuantity(userId, bookId, newQuantity);
            existingCart.setQuantity(newQuantity);
            return existingCart;
        }
        
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setBookId(bookId);
        cart.setQuantity(quantity);
        cart.setSelected(1);
        cartMapper.insert(cart);
        return cart;
    }
    
    @Override
    @Transactional
    public void updateQuantity(Long userId, Long bookId, Integer quantity) {
        if (quantity <= 0) {
            cartMapper.deleteByUserIdAndBookId(userId, bookId);
        } else {
            cartMapper.updateQuantity(userId, bookId, quantity);
        }
    }
    
    @Override
    @Transactional
    public void removeFromCart(Long userId, Long bookId) {
        cartMapper.deleteByUserIdAndBookId(userId, bookId);
    }
    
    @Override
    @Transactional
    public void clearCart(Long userId) {
        cartMapper.deleteByUserId(userId);
    }
    
    @Override
    @Transactional
    public void updateSelected(Long userId, Long bookId, Integer selected) {
        cartMapper.updateSelected(userId, bookId, selected);
    }
    
    @Override
    @Transactional
    public void updateAllSelected(Long userId, Integer selected) {
        cartMapper.updateAllSelected(userId, selected);
    }
    
    @Override
    @Transactional
    public void deleteSelectedItems(Long userId) {
        cartMapper.deleteSelectedByUserId(userId);
    }
    
    @Override
    public long getCartCount(Long userId) {
        return cartMapper.countByUserId(userId);
    }
    
    private CartDTO convertToDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUserId());
        dto.setBookId(cart.getBookId());
        dto.setBookTitle(cart.getBookTitle());
        dto.setBookCover(cart.getBookCover());
        dto.setPrice(cart.getPrice());
        dto.setQuantity(cart.getQuantity());
        dto.setStock(cart.getStock());
        dto.setSelected(cart.getSelected());
        dto.setSubtotal(cart.getPrice().multiply(new BigDecimal(cart.getQuantity())));
        return dto;
    }
}
