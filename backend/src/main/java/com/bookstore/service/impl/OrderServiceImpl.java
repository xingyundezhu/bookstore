package com.bookstore.service.impl;

import com.bookstore.dto.CreateOrderRequest;
import com.bookstore.dto.OrderDTO;
import com.bookstore.entity.*;
import com.bookstore.mapper.*;
import com.bookstore.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl implements OrderService {
    
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartMapper cartMapper;
    private final BookMapper bookMapper;
    private final AddressMapper addressMapper;
    
    /**
     * 构造函数注入依赖
     *
     * @param orderMapper 订单数据访问层
     * @param orderItemMapper 订单项数据访问层
     * @param cartMapper 购物车数据访问层
     * @param bookMapper 图书数据访问层
     * @param addressMapper 地址数据访问层
     */
    public OrderServiceImpl(OrderMapper orderMapper, OrderItemMapper orderItemMapper, 
                           CartMapper cartMapper, BookMapper bookMapper, AddressMapper addressMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.cartMapper = cartMapper;
        this.bookMapper = bookMapper;
        this.addressMapper = addressMapper;
    }
    
    @Override
    @Transactional
    public Order createOrder(Long userId, CreateOrderRequest request) {
        List<Cart> selectedCarts = cartMapper.findSelectedByUserId(userId);
        if (selectedCarts.isEmpty()) {
            throw new RuntimeException("请选择要购买的商品");
        }
        
        Address address = addressMapper.findById(request.getAddressId());
        if (address == null || !address.getUserId().equals(userId)) {
            throw new RuntimeException("收货地址不存在");
        }
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Cart cart : selectedCarts) {
            totalAmount = totalAmount.add(cart.getPrice().multiply(new BigDecimal(cart.getQuantity())));
        }
        
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setFreight(BigDecimal.ZERO);
        order.setDiscount(BigDecimal.ZERO);
        order.setStatus(0);
        order.setPaymentType(request.getPaymentType());
        order.setReceiver(address.getReceiver());
        order.setReceiverPhone(address.getPhone());
        order.setReceiverAddress(address.getProvince() + address.getCity() + address.getDistrict() + address.getDetail());
        order.setRemark(request.getRemark());
        
        orderMapper.insert(order);
        
        for (Cart cart : selectedCarts) {
            Book book = bookMapper.findById(cart.getBookId());
            if (book.getStock() < cart.getQuantity()) {
                throw new RuntimeException("商品库存不足: " + book.getTitle());
            }
            
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setBookId(cart.getBookId());
            item.setBookTitle(cart.getBookTitle());
            item.setBookCover(cart.getBookCover());
            item.setPrice(cart.getPrice());
            item.setQuantity(cart.getQuantity());
            item.setSubtotal(cart.getPrice().multiply(new BigDecimal(cart.getQuantity())));
            orderItemMapper.insert(item);
            
            bookMapper.decreaseStock(cart.getBookId(), cart.getQuantity());
        }
        
        cartMapper.deleteSelectedByUserId(userId);
        
        return order;
    }
    
    @Override
    @Transactional
    public Order payOrder(Long userId, String orderNo) {
        Order order = orderMapper.findByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态不正确");
        }
        
        orderMapper.updateStatus(order.getId(), 1, LocalDateTime.now());
        order.setStatus(1);
        return order;
    }
    
    @Override
    @Transactional
    public void cancelOrder(Long userId, String orderNo) {
        Order order = orderMapper.findByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("只能取消待付款订单");
        }
        
        List<OrderItem> items = orderItemMapper.findByOrderId(order.getId());
        for (OrderItem item : items) {
            bookMapper.increaseStockAndDecreaseSales(item.getBookId(), item.getQuantity());
        }
        
        orderMapper.updateStatusByOrderNo(orderNo, 4);
    }
    
    @Override
    @Transactional
    public void deliverOrder(String orderNo) {
        Order order = orderMapper.findByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new RuntimeException("只能发货已付款订单");
        }
        orderMapper.deliver(orderNo, 2);
    }
    
    @Override
    @Transactional
    public void receiveOrder(Long userId, String orderNo) {
        Order order = orderMapper.findByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 2) {
            throw new RuntimeException("只能确认收货已发货订单");
        }
        orderMapper.receive(orderNo, 3);
    }
    
    @Override
    public Order getOrderByOrderNo(String orderNo) {
        Order order = orderMapper.findByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        return order;
    }
    
    @Override
    public Page<OrderDTO> getUserOrders(Long userId, Integer status, Pageable pageable) {
        List<Order> orders;
        if (status != null) {
            orders = orderMapper.findByUserIdAndStatus(userId, status);
        } else {
            orders = orderMapper.findByUserId(userId);
        }
        List<OrderDTO> dtos = orders.stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, orders.size());
    }
    
    @Override
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        List<Order> orders = orderMapper.findAll();
        List<OrderDTO> dtos = orders.stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, orders.size());
    }
    
    @Override
    @Transactional
    public void updateOrderStatus(String orderNo, Integer status) {
        orderMapper.updateStatusByOrderNo(orderNo, status);
    }
    
    @Override
    public Map<String, Object> getOrderStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("todayOrders", orderMapper.countToday());
        stats.put("todaySales", orderMapper.sumTodaySales());
        stats.put("monthOrders", orderMapper.countMonth());
        stats.put("monthSales", orderMapper.sumMonthSales());
        return stats;
    }
    
    @Override
    @Transactional
    public void deleteCompletedOrder(String orderNo) {
        Order order = orderMapper.findByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (order.getStatus() != 3) {
            throw new RuntimeException("只能删除已完成的订单");
        }
        
        orderItemMapper.deleteByOrderId(order.getId());
        orderMapper.deleteByOrderNo(orderNo);
    }
    
    private String generateOrderNo() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + 
               String.format("%04d", new Random().nextInt(10000));
    }
    
    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setUserId(order.getUserId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setPayAmount(order.getPayAmount());
        dto.setFreight(order.getFreight());
        dto.setDiscount(order.getDiscount());
        dto.setStatus(order.getStatus());
        dto.setStatusText(order.getStatusText());
        dto.setPaymentType(order.getPaymentType());
        dto.setPaymentTime(order.getPaymentTime());
        dto.setDeliveryTime(order.getDeliveryTime());
        dto.setReceiveTime(order.getReceiveTime());
        dto.setReceiver(order.getReceiver());
        dto.setReceiverPhone(order.getReceiverPhone());
        dto.setReceiverAddress(order.getReceiverAddress());
        dto.setRemark(order.getRemark());
        dto.setCreateTime(order.getCreateTime());
        dto.setUpdateTime(order.getUpdateTime());
        
        List<OrderItem> items = orderItemMapper.findByOrderId(order.getId());
        dto.setOrderItems(items);
        
        return dto;
    }
}
