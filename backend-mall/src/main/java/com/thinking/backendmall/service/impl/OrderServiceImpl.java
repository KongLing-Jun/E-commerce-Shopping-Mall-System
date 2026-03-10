package com.thinking.backendmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thinking.backendmall.common.BusinessException;
import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.entity.Address;
import com.thinking.backendmall.entity.CartItem;
import com.thinking.backendmall.entity.Order;
import com.thinking.backendmall.entity.OrderItem;
import com.thinking.backendmall.entity.Product;
import com.thinking.backendmall.entity.User;
import com.thinking.backendmall.repository.AddressRepository;
import com.thinking.backendmall.repository.CartItemRepository;
import com.thinking.backendmall.repository.OrderItemRepository;
import com.thinking.backendmall.repository.OrderRepository;
import com.thinking.backendmall.repository.OrderDeliveryRepository;
import com.thinking.backendmall.repository.ProductRepository;
import com.thinking.backendmall.repository.UserRepository;
import com.thinking.backendmall.service.OrderService;
import com.thinking.backendmall.service.MerchantNoticeService;
import com.thinking.backendmall.vo.CartItemView;
import com.thinking.backendmall.vo.OrderInvoiceView;
import com.thinking.backendmall.vo.OrderItemView;
import com.thinking.backendmall.vo.OrderPreResponse;
import com.thinking.backendmall.vo.OrderView;
import com.thinking.backendmall.vo.RebuyItemView;
import com.thinking.backendmall.vo.RebuyResponse;
import com.thinking.backendmall.entity.OrderDelivery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderDeliveryRepository orderDeliveryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MerchantNoticeService merchantNoticeService;

    @Override
    public OrderPreResponse preOrder(Long userId) {
        List<CartItem> cartItems = cartItemRepository.selectList(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .eq(CartItem::getChecked, 1));
        if (cartItems.isEmpty()) {
            throw new BusinessException("No items selected");
        }

        Map<Long, Product> productMap = loadProducts(cartItems);
        List<CartItemView> itemViews = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            Product product = productMap.get(item.getProductId());
            if (product == null) {
                continue;
            }
            CartItemView view = new CartItemView();
            view.setCartItemId(item.getId());
            view.setProductId(item.getProductId());
            view.setName(product.getName());
            view.setPrice(priceOrZero(product.getPrice()));
            view.setImage(product.getCoverUrl());
            view.setQuantity(item.getQuantity());
            view.setChecked(item.getChecked());
            itemViews.add(view);
            total = total.add(priceOrZero(product.getPrice())
                    .multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        OrderPreResponse response = new OrderPreResponse();
        response.setItems(itemViews);
        response.setAddresses(addressRepository.selectList(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId)
                .orderByDesc(Address::getIsDefault)));
        response.setTotalAmount(total);
        return response;
    }

    @Override
    @Transactional
    public String createOrder(Long userId, Long addressId) {
        Address address = addressRepository.selectById(addressId);
        if (address == null || !userId.equals(address.getUserId())) {
            throw new BusinessException("Address not found");
        }
        List<CartItem> cartItems = cartItemRepository.selectList(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .eq(CartItem::getChecked, 1));
        if (cartItems.isEmpty()) {
            throw new BusinessException("No items selected");
        }
        Map<Long, Product> productMap = loadProducts(cartItems);
        BigDecimal total = BigDecimal.ZERO;
        Map<Long, Integer> productQuantityMap = new HashMap<>();
        for (CartItem item : cartItems) {
            Product product = productMap.get(item.getProductId());
            if (product == null || !"ON".equals(product.getStatus())) {
                throw new BusinessException("Product not available");
            }
            productQuantityMap.merge(item.getProductId(), item.getQuantity(), Integer::sum);
            total = total.add(priceOrZero(product.getPrice())
                    .multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        for (Map.Entry<Long, Integer> entry : productQuantityMap.entrySet()) {
            int updated = productRepository.deductStock(entry.getKey(), entry.getValue());
            if (updated == 0) {
                throw new BusinessException("Insufficient stock");
            }
        }

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(total);
        order.setPayAmount(total);
        order.setStatus(0);
        order.setAddressSnapshot(buildAddressSnapshot(address));
        order.setCreatedAt(LocalDateTime.now());
        orderRepository.insert(order);

        for (CartItem item : cartItems) {
            Product product = productMap.get(item.getProductId());
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductNameSnapshot(product.getName());
            orderItem.setPriceSnapshot(priceOrZero(product.getPrice()));
            orderItem.setQuantity(item.getQuantity());
            orderItem.setImageSnapshot(product.getCoverUrl());
            orderItemRepository.insert(orderItem);
        }

        cartItemRepository.delete(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .eq(CartItem::getChecked, 1));
        return order.getOrderNo();
    }

    @Override
    public void payOrder(Long userId, String orderNo, BigDecimal payAmount) {
        Order order = orderRepository.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, orderNo)
                .eq(Order::getUserId, userId));
        if (order == null) {
            throw new BusinessException(404, "Order not found");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("Order cannot be paid");
        }
        if (payAmount != null && order.getPayAmount() != null && payAmount.compareTo(order.getPayAmount()) != 0) {
            throw new BusinessException("Pay amount mismatch");
        }
        order.setStatus(1);
        order.setPaidAt(LocalDateTime.now());
        orderRepository.updateById(order);
        merchantNoticeService.notifyOrderPaid(orderNo, userId, order.getAddressSnapshot());
    }

    @Override
    public OrderView getOrder(Long userId, String orderNo) {
        Order order = orderRepository.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, orderNo)
                .eq(Order::getUserId, userId));
        if (order == null) {
            throw new BusinessException(404, "Order not found");
        }
        List<OrderView> views = buildOrderViews(List.of(order));
        return views.isEmpty() ? null : views.get(0);
    }

    @Override
    public PageResult<OrderView> listOrders(Long userId, Integer status, int page, int size) {
        Page<Order> orderPage = new Page<>(page + 1L, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreatedAt);
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        orderRepository.selectPage(orderPage, wrapper);
        List<OrderView> views = buildOrderViews(orderPage.getRecords());
        boolean last = orderPage.getCurrent() >= orderPage.getPages();
        return new PageResult<>(views, orderPage.getTotal(), orderPage.getPages(), page, size, last);
    }

    @Override
    public void confirmOrder(Long userId, String orderNo) {
        Order order = orderRepository.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, orderNo)
                .eq(Order::getUserId, userId));
        if (order == null) {
            throw new BusinessException(404, "Order not found");
        }
        if (order.getStatus() != 2) {
            throw new BusinessException("Order cannot be confirmed");
        }
        order.setStatus(3);
        order.setFinishedAt(LocalDateTime.now());
        orderRepository.updateById(order);
    }

    @Override
    public OrderInvoiceView getInvoice(Long userId, String orderNo) {
        Order order = orderRepository.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, orderNo)
                .eq(Order::getUserId, userId));
        if (order == null) {
            throw new BusinessException(404, "Order not found");
        }
        if (order.getStatus() == null || order.getStatus() == 0) {
            throw new BusinessException(400, "Invoice not available before payment");
        }
        OrderInvoiceView view = new OrderInvoiceView();
        view.setInvoiceNo("INV-" + order.getOrderNo());
        view.setOrderNo(order.getOrderNo());
        view.setUserId(order.getUserId());
        User user = userRepository.selectById(order.getUserId());
        view.setUsername(user == null ? null : user.getUsername());
        view.setStatus(order.getStatus());
        view.setTotalAmount(order.getTotalAmount());
        view.setPayAmount(order.getPayAmount());
        view.setAddressSnapshot(order.getAddressSnapshot());
        view.setCreatedAt(order.getCreatedAt());
        view.setPaidAt(order.getPaidAt());
        view.setItems(loadOrderItemViews(order.getId()));
        return view;
    }

    @Override
    @Transactional
    public RebuyResponse rebuy(Long userId, String orderNo) {
        Order order = orderRepository.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, orderNo)
                .eq(Order::getUserId, userId));
        if (order == null) {
            throw new BusinessException(404, "Order not found");
        }
        List<OrderItem> items = orderItemRepository.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, order.getId()));
        if (items.isEmpty()) {
            throw new BusinessException(400, "Order has no items");
        }

        RebuyResponse response = new RebuyResponse();
        for (OrderItem item : items) {
            Product product = productRepository.selectById(item.getProductId());
            int requestedQty = item.getQuantity() == null ? 0 : item.getQuantity();
            if (product == null) {
                response.getSkippedItems().add(buildRebuyItem(item, requestedQty, 0, "Product not found"));
                continue;
            }
            if (!"ON".equals(product.getStatus())) {
                response.getSkippedItems().add(buildRebuyItem(item, requestedQty, 0, "Product off shelf"));
                continue;
            }
            Integer stock = product.getStock();
            int available = stock == null ? 0 : stock;
            if (available <= 0) {
                response.getSkippedItems().add(buildRebuyItem(item, requestedQty, 0, "Out of stock"));
                continue;
            }

            CartItem existing = cartItemRepository.selectOne(new LambdaQueryWrapper<CartItem>()
                    .eq(CartItem::getUserId, userId)
                    .eq(CartItem::getProductId, product.getId()));
            int existingQty = existing == null ? 0 : existing.getQuantity();
            int canAdd = Math.max(0, available - existingQty);
            int addQty = Math.min(requestedQty, canAdd);
            if (addQty <= 0) {
                response.getSkippedItems().add(buildRebuyItem(item, requestedQty, 0, "Cart already full for stock"));
                continue;
            }

            if (existing != null) {
                existing.setQuantity(existingQty + addQty);
                cartItemRepository.updateById(existing);
            } else {
                CartItem cartItem = new CartItem();
                cartItem.setUserId(userId);
                cartItem.setProductId(product.getId());
                cartItem.setQuantity(addQty);
                cartItem.setChecked(1);
                cartItem.setCreatedAt(LocalDateTime.now());
                cartItemRepository.insert(cartItem);
            }
            response.getAddedItems().add(buildRebuyItem(item, requestedQty, addQty, null));
        }
        return response;
    }

    private Map<Long, Product> loadProducts(List<CartItem> cartItems) {
        List<Long> productIds = new ArrayList<>();
        for (CartItem item : cartItems) {
            productIds.add(item.getProductId());
        }
        List<Product> products = productRepository.selectBatchIds(productIds);
        Map<Long, Product> map = new HashMap<>();
        for (Product product : products) {
            map.put(product.getId(), product);
        }
        return map;
    }

    private List<OrderView> buildOrderViews(List<Order> orders) {
        List<OrderView> views = new ArrayList<>();
        if (orders.isEmpty()) {
            return views;
        }
        List<Long> orderIds = new ArrayList<>();
        for (Order order : orders) {
            orderIds.add(order.getId());
        }
        List<OrderItem> items = orderItemRepository.selectList(new LambdaQueryWrapper<OrderItem>()
                .in(OrderItem::getOrderId, orderIds));
        List<OrderDelivery> deliveries = orderDeliveryRepository.selectList(new LambdaQueryWrapper<OrderDelivery>()
                .in(OrderDelivery::getOrderId, orderIds));
        Map<Long, OrderDelivery> deliveryMap = new HashMap<>();
        for (OrderDelivery delivery : deliveries) {
            deliveryMap.put(delivery.getOrderId(), delivery);
        }
        Map<Long, List<OrderItemView>> itemMap = new HashMap<>();
        for (OrderItem item : items) {
            OrderItemView view = new OrderItemView();
            view.setProductId(item.getProductId());
            view.setProductName(item.getProductNameSnapshot());
            view.setPrice(item.getPriceSnapshot());
            view.setQuantity(item.getQuantity());
            view.setImage(item.getImageSnapshot());
            itemMap.computeIfAbsent(item.getOrderId(), key -> new ArrayList<>()).add(view);
        }

        for (Order order : orders) {
            OrderView view = new OrderView();
            view.setOrderNo(order.getOrderNo());
            view.setTotalAmount(order.getTotalAmount());
            view.setPayAmount(order.getPayAmount());
            view.setStatus(order.getStatus());
            view.setCreatedAt(order.getCreatedAt());
            view.setPaidAt(order.getPaidAt());
            view.setShippedAt(order.getShippedAt());
            view.setFinishedAt(order.getFinishedAt());
            OrderDelivery delivery = deliveryMap.get(order.getId());
            if (delivery != null) {
                view.setExpressNo(delivery.getExpressNo());
                view.setExpressCompany(delivery.getExpressCompany());
            }
            view.setItems(itemMap.getOrDefault(order.getId(), new ArrayList<>()));
            views.add(view);
        }
        return views;
    }

    private List<OrderItemView> loadOrderItemViews(Long orderId) {
        List<OrderItem> items = orderItemRepository.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId));
        List<OrderItemView> views = new ArrayList<>();
        for (OrderItem item : items) {
            OrderItemView view = new OrderItemView();
            view.setProductId(item.getProductId());
            view.setProductName(item.getProductNameSnapshot());
            view.setPrice(item.getPriceSnapshot());
            view.setQuantity(item.getQuantity());
            view.setImage(item.getImageSnapshot());
            views.add(view);
        }
        return views;
    }

    private RebuyItemView buildRebuyItem(OrderItem item, int requestedQty, int addedQty, String reason) {
        RebuyItemView view = new RebuyItemView();
        view.setProductId(item.getProductId());
        view.setProductName(item.getProductNameSnapshot());
        view.setRequestedQty(requestedQty);
        view.setAddedQty(addedQty);
        view.setReason(reason);
        return view;
    }

    private String generateOrderNo() {
        String raw = UUID.randomUUID().toString().replace("-", "");
        return "NO" + raw.substring(0, 18).toUpperCase();
    }

    private String buildAddressSnapshot(Address address) {
        return String.format("%s %s %s %s %s %s",
                safe(address.getReceiver()),
                safe(address.getPhone()),
                safe(address.getProvince()),
                safe(address.getCity()),
                safe(address.getArea()),
                safe(address.getDetail()));
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private BigDecimal priceOrZero(BigDecimal price) {
        return price == null ? BigDecimal.ZERO : price;
    }
}
