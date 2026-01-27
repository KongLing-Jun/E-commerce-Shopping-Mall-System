package com.thinking.backendmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thinking.backendmall.common.BusinessException;
import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.entity.Order;
import com.thinking.backendmall.entity.OrderItem;
import com.thinking.backendmall.entity.User;
import com.thinking.backendmall.repository.OrderItemRepository;
import com.thinking.backendmall.repository.OrderRepository;
import com.thinking.backendmall.repository.UserRepository;
import com.thinking.backendmall.service.AdminOrderService;
import com.thinking.backendmall.vo.AdminOrderView;
import com.thinking.backendmall.vo.OrderItemView;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminOrderServiceImpl implements AdminOrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public PageResult<AdminOrderView> listOrders(String orderNo, Long userId, Integer status, int page, int size) {
        Page<Order> pageResult = new Page<>(page + 1L, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (orderNo != null && !orderNo.isBlank()) {
            wrapper.eq(Order::getOrderNo, orderNo);
        }
        if (userId != null) {
            wrapper.eq(Order::getUserId, userId);
        }
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreatedAt);
        orderRepository.selectPage(pageResult, wrapper);

        List<Order> orders = pageResult.getRecords();
        Map<Long, String> userMap = loadUsers(orders);
        Map<Long, List<OrderItemView>> itemMap = loadItems(orders);

        List<AdminOrderView> views = new ArrayList<>();
        for (Order order : orders) {
            AdminOrderView view = new AdminOrderView();
            view.setOrderNo(order.getOrderNo());
            view.setUserId(order.getUserId());
            view.setUsername(userMap.get(order.getUserId()));
            view.setTotalAmount(order.getTotalAmount());
            view.setPayAmount(order.getPayAmount());
            view.setStatus(order.getStatus());
            view.setCreatedAt(order.getCreatedAt());
            view.setPaidAt(order.getPaidAt());
            view.setShippedAt(order.getShippedAt());
            view.setFinishedAt(order.getFinishedAt());
            view.setItems(itemMap.getOrDefault(order.getId(), new ArrayList<>()));
            views.add(view);
        }

        boolean last = pageResult.getCurrent() >= pageResult.getPages();
        return new PageResult<>(views, pageResult.getTotal(), pageResult.getPages(), page, size, last);
    }

    @Override
    public void shipOrder(String orderNo) {
        Order order = orderRepository.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, orderNo));
        if (order == null) {
            throw new BusinessException(404, "Order not found");
        }
        if (order.getStatus() == null || order.getStatus() != 1) {
            throw new BusinessException("Order cannot be shipped");
        }
        order.setStatus(2);
        order.setShippedAt(LocalDateTime.now());
        orderRepository.updateById(order);
    }

    @Override
    public byte[] exportOrders(String orderNo, Long userId, Integer status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (orderNo != null && !orderNo.isBlank()) {
            wrapper.eq(Order::getOrderNo, orderNo);
        }
        if (userId != null) {
            wrapper.eq(Order::getUserId, userId);
        }
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreatedAt);
        List<Order> orders = orderRepository.selectList(wrapper);
        Map<Long, String> userMap = loadUsers(orders);
        Map<Long, List<OrderItemView>> itemMap = loadItems(orders);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("orders");
            int rowIndex = 0;
            Row header = sheet.createRow(rowIndex++);
            header.createCell(0).setCellValue("Order No");
            header.createCell(1).setCellValue("User ID");
            header.createCell(2).setCellValue("Username");
            header.createCell(3).setCellValue("Status");
            header.createCell(4).setCellValue("Total Amount");
            header.createCell(5).setCellValue("Pay Amount");
            header.createCell(6).setCellValue("Created At");
            header.createCell(7).setCellValue("Product ID");
            header.createCell(8).setCellValue("Product Name");
            header.createCell(9).setCellValue("Quantity");
            header.createCell(10).setCellValue("Price");

            for (Order order : orders) {
                List<OrderItemView> items = itemMap.getOrDefault(order.getId(), new ArrayList<>());
                if (items.isEmpty()) {
                    Row row = sheet.createRow(rowIndex++);
                    fillOrderRow(row, order, userMap.get(order.getUserId()), null);
                    continue;
                }
                for (OrderItemView item : items) {
                    Row row = sheet.createRow(rowIndex++);
                    fillOrderRow(row, order, userMap.get(order.getUserId()), item);
                }
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception ex) {
            throw new BusinessException("Failed to export orders");
        }
    }

    private Map<Long, String> loadUsers(List<Order> orders) {
        Map<Long, String> userMap = new HashMap<>();
        if (orders.isEmpty()) {
            return userMap;
        }
        List<Long> userIds = new ArrayList<>();
        for (Order order : orders) {
            userIds.add(order.getUserId());
        }
        for (User user : userRepository.selectBatchIds(userIds)) {
            userMap.put(user.getId(), user.getUsername());
        }
        return userMap;
    }

    private Map<Long, List<OrderItemView>> loadItems(List<Order> orders) {
        Map<Long, List<OrderItemView>> itemMap = new HashMap<>();
        if (orders.isEmpty()) {
            return itemMap;
        }
        List<Long> orderIds = new ArrayList<>();
        for (Order order : orders) {
            orderIds.add(order.getId());
        }
        List<OrderItem> items = orderItemRepository.selectList(new LambdaQueryWrapper<OrderItem>()
                .in(OrderItem::getOrderId, orderIds));
        for (OrderItem item : items) {
            OrderItemView view = new OrderItemView();
            view.setProductId(item.getProductId());
            view.setProductName(item.getProductNameSnapshot());
            view.setPrice(item.getPriceSnapshot());
            view.setQuantity(item.getQuantity());
            view.setImage(item.getImageSnapshot());
            itemMap.computeIfAbsent(item.getOrderId(), key -> new ArrayList<>()).add(view);
        }
        return itemMap;
    }

    private void fillOrderRow(Row row, Order order, String username, OrderItemView item) {
        row.createCell(0).setCellValue(order.getOrderNo());
        row.createCell(1).setCellValue(order.getUserId() == null ? "" : String.valueOf(order.getUserId()));
        row.createCell(2).setCellValue(username == null ? "" : username);
        row.createCell(3).setCellValue(order.getStatus() == null ? "" : String.valueOf(order.getStatus()));
        row.createCell(4).setCellValue(order.getTotalAmount() == null ? "" : order.getTotalAmount().toString());
        row.createCell(5).setCellValue(order.getPayAmount() == null ? "" : order.getPayAmount().toString());
        row.createCell(6).setCellValue(order.getCreatedAt() == null ? "" : order.getCreatedAt().toString());
        if (item == null) {
            row.createCell(7).setCellValue("");
            row.createCell(8).setCellValue("");
            row.createCell(9).setCellValue("");
            row.createCell(10).setCellValue("");
        } else {
            row.createCell(7).setCellValue(item.getProductId() == null ? "" : String.valueOf(item.getProductId()));
            row.createCell(8).setCellValue(item.getProductName() == null ? "" : item.getProductName());
            row.createCell(9).setCellValue(item.getQuantity() == null ? "" : String.valueOf(item.getQuantity()));
            row.createCell(10).setCellValue(item.getPrice() == null ? "" : item.getPrice().toString());
        }
    }
}
