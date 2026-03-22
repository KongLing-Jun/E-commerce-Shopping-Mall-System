# 任务6：后端核心代码片段

## 目录
- [1. 用户认证](#1-用户认证)
- [2. 订单事务](#2-订单事务)
- [3. 商品管理](#3-商品管理)
- [4. 权限控制](#4-权限控制)

---

## 1. 用户认证

### 1.1 认证控制器 (AuthController.java)

```java
package com.thinking.backendmall.controller;

import com.thinking.backendmall.common.Result;
import com.thinking.backendmall.config.security.AuthContext;
import com.thinking.backendmall.dto.AuthLoginRequest;
import com.thinking.backendmall.dto.AuthRegisterRequest;
import com.thinking.backendmall.service.AuthService;
import com.thinking.backendmall.service.MenuService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private MenuService menuService;

    @PostMapping("/register")
    // 功能：用户注册接口
    public Result<Map<String, Object>> register(@Valid @RequestBody AuthRegisterRequest body) {
        Map<String, Object> result = authService.register(
                body.getUsername(),
                body.getPhone(),
                body.getPassword(),
                body.getConfirmPassword());
        return Result.success(result);
    }

    @PostMapping("/login")
    // 功能：用户登录接口
    public Result<Map<String, Object>> login(@Valid @RequestBody AuthLoginRequest body) {
        Map<String, Object> result = authService.login(body.getUsername(), body.getPassword());
        return Result.success(result);
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    // 功能：用户退出登录接口
    public Result<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            authService.logout(header.substring(7));
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return Result.success();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    // 功能：获取当前用户信息接口
    public Result<Map<String, Object>> me() {
        Long userId = AuthContext.getUserId();
        String username = AuthContext.getUsername();
        String roleKey = AuthContext.getRoleKey();
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("userId", userId);
        result.put("username", username);
        result.put("roleKey", roleKey);
        result.put("menus", menuService.listMyMenus(roleKey));
        result.put("perms", menuService.listMyPerms(roleKey));
        return Result.success(result);
    }
}
```

### 1.2 认证服务实现 (AuthServiceImpl.java)

```java
package com.thinking.backendmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.thinking.backendmall.common.BusinessException;
import com.thinking.backendmall.config.JwtUtil;
import com.thinking.backendmall.config.security.AuthMemoryStore;
import com.thinking.backendmall.entity.Role;
import com.thinking.backendmall.entity.User;
import com.thinking.backendmall.repository.RoleRepository;
import com.thinking.backendmall.repository.UserRepository;
import com.thinking.backendmall.service.AuthService;
import com.thinking.backendmall.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MenuService menuService;

    @Autowired
    private AuthMemoryStore authMemoryStore;

    @Value("${app.login.max-attempts:5}")
    private int maxAttempts;

    @Value("${app.login.lock-seconds:900}")
    private long lockSeconds;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    // 功能：初始化认证服务所需的密码加密器
    public AuthServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    // 功能：注册账号并写入用户信息
    public Map<String, Object> register(String username, String phone, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new BusinessException("Passwords do not match");
        }
        User existingByUsername = userRepository.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (existingByUsername != null) {
            throw new BusinessException("Username already exists");
        }
        User existingByPhone = userRepository.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        if (existingByPhone != null) {
            throw new BusinessException("Phone already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPhone(phone);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setStatus(1);
        Role userRole = roleRepository.selectOne(
                new LambdaQueryWrapper<Role>().eq(Role::getRoleKey, "USER"));
        if (userRole != null) {
            user.setRoleId(userRole.getId());
        }
        user.setCreatedAt(LocalDateTime.now());
        userRepository.insert(user);

        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getId());
        return result;
    }

    @Override
    // 功能：校验账号密码并返回登录信息
    public Map<String, Object> login(String username, String password) {
        if (authMemoryStore.isLocked(username, maxAttempts)) {
            throw new BusinessException("Too many login attempts. Please try later.");
        }
        User user = userRepository.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            authMemoryStore.recordFailure(username, lockSeconds);
            throw new BusinessException("User not found");
        }
        if (user.getStatus() != 1) {
            authMemoryStore.recordFailure(username, lockSeconds);
            throw new BusinessException("User is disabled");
        }
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            authMemoryStore.recordFailure(username, lockSeconds);
            throw new BusinessException("Invalid password");
        }
        authMemoryStore.clearFailure(username);

        Role role = roleRepository.selectById(user.getRoleId());
        String roleKey = role != null ? role.getRoleKey() : "USER";

        String token = jwtUtil.generateToken(user.getUsername(), user.getId(), roleKey);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("roleKey", roleKey);
        result.put("menus", menuService.listMyMenus(roleKey));
        result.put("perms", menuService.listMyPerms(roleKey));
        return result;
    }

    @Override
    // 功能：登出时将令牌加入本地黑名单
    public void logout(String token) {
        if (token == null || token.isBlank()) {
            return;
        }
        try {
            var claims = jwtUtil.getClaimsFromToken(token);
            long ttl = claims.getExpiration().getTime() - System.currentTimeMillis();
            if (ttl > 0) {
                authMemoryStore.blacklistToken(token, ttl);
            }
        } catch (Exception ex) {
            // Ignore invalid token.
        }
    }
}
```

---

## 2. 订单事务

### 2.1 订单服务实现 (OrderServiceImpl.java - 核心事务)

```java
package com.thinking.backendmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.thinking.backendmall.common.BusinessException;
import com.thinking.backendmall.entity.Address;
import com.thinking.backendmall.entity.CartItem;
import com.thinking.backendmall.entity.Order;
import com.thinking.backendmall.entity.OrderItem;
import com.thinking.backendmall.entity.OrderTrackingEvent;
import com.thinking.backendmall.entity.Product;
import com.thinking.backendmall.entity.User;
import com.thinking.backendmall.repository.AddressRepository;
import com.thinking.backendmall.repository.CartItemRepository;
import com.thinking.backendmall.repository.OrderItemRepository;
import com.thinking.backendmall.repository.OrderRepository;
import com.thinking.backendmall.repository.OrderDeliveryRepository;
import com.thinking.backendmall.repository.OrderTrackingEventRepository;
import com.thinking.backendmall.repository.ProductRepository;
import com.thinking.backendmall.repository.UserRepository;
import com.thinking.backendmall.service.OrderService;
import com.thinking.backendmall.service.MerchantNoticeService;
import com.thinking.backendmall.vo.CartItemView;
import com.thinking.backendmall.vo.OrderView;
import com.thinking.backendmall.vo.OrderItemView;
import com.thinking.backendmall.vo.OrderTrackingEventView;
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
    private OrderTrackingEventRepository orderTrackingEventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MerchantNoticeService merchantNoticeService;

    @Override
    // 功能：生成订单确认页所需的商品清单与地址信息
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
    // 功能：创建订单并扣减库存，返回订单号（核心事务）
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

        // 扣减库存
        for (Map.Entry<Long, Integer> entry : productQuantityMap.entrySet()) {
            int updated = productRepository.deductStock(entry.getKey(), entry.getValue());
            if (updated == 0) {
                throw new BusinessException("Insufficient stock");
            }
        }

        // 创建订单主表
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(total);
        order.setPayAmount(total);
        order.setStatus(0);
        order.setAddressSnapshot(buildAddressSnapshot(address));
        order.setCreatedAt(LocalDateTime.now());
        orderRepository.insert(order);
        
        // 记录创建订单的首条物流轨迹
        recordTrackingEvent(order.getId(), order.getStatus(), "订单已创建", "订单创建成功，等待支付。", null, order.getCreatedAt());

        // 创建订单明细表
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

        // 清理购物车
        cartItemRepository.delete(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .eq(CartItem::getChecked, 1));
        return order.getOrderNo();
    }

    @Override
    // 功能：校验并完成订单支付状态更新
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
        // 记录支付完成事件
        recordTrackingEvent(order.getId(), order.getStatus(), "支付完成", "订单支付成功，等待发货。", null, order.getPaidAt());
        merchantNoticeService.notifyOrderPaid(orderNo, userId, order.getAddressSnapshot());
    }

    // 功能：批量加载订单涉及的商品信息
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

    // 功能：写入订单物流轨迹事件记录
    private void recordTrackingEvent(Long orderId, Integer status, String title, String description, String location, LocalDateTime eventTime) {
        if (orderId == null || title == null || title.isBlank()) {
            return;
        }
        OrderTrackingEvent event = new OrderTrackingEvent();
        event.setOrderId(orderId);
        event.setStatus(status);
        event.setTitle(title);
        event.setDescription(description);
        event.setLocation(location);
        event.setEventTime(eventTime == null ? LocalDateTime.now() : eventTime);
        event.setCreatedAt(LocalDateTime.now());
        orderTrackingEventRepository.insert(event);
    }

    // 功能：生成全局唯一订单编号
    private String generateOrderNo() {
        String raw = UUID.randomUUID().toString().replace("-", "");
        return "NO" + raw.substring(0, 18).toUpperCase();
    }

    // 功能：构建收货地址快照，避免地址变更影响历史订单
    private String buildAddressSnapshot(Address address) {
        return String.format("%s %s %s %s %s %s",
                safe(address.getReceiver()),
                safe(address.getPhone()),
                safe(address.getProvince()),
                safe(address.getCity()),
                safe(address.getArea()),
                safe(address.getDetail()));
    }

    // 功能：将空值安全转换为空字符串
    private String safe(String value) {
        return value == null ? "" : value;
    }

    // 功能：空价格兜底为 0，避免计算异常
    private BigDecimal priceOrZero(BigDecimal price) {
        return price == null ? BigDecimal.ZERO : price;
    }
}
```

### 2.2 后台订单控制器 (AdminOrderController.java)

```java
package com.thinking.backendmall.controller.admin;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.common.Result;
import com.thinking.backendmall.dto.AdminOrderTrackingRequest;
import com.thinking.backendmall.dto.AdminShipOrderRequest;
import com.thinking.backendmall.service.AdminOrderService;
import com.thinking.backendmall.service.OperationLogService;
import com.thinking.backendmall.vo.AdminOrderView;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    @Autowired
    private AdminOrderService adminOrderService;

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping
    @PreAuthorize("@permissionService.hasPerm('admin:orders:list')")
    // 功能：分页查询订单列表
    public Result<PageResult<AdminOrderView>> listOrders(@RequestParam(required = false) String orderNo,
                                                         @RequestParam(required = false) Long userId,
                                                         @RequestParam(required = false) Integer status,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size) {
        return Result.success(adminOrderService.listOrders(orderNo, userId, status, page, size));
    }

    @PostMapping("/{orderNo}/ship")
    @PreAuthorize("@permissionService.hasPerm('admin:orders:ship')")
    // 功能：发货订单
    public Result<Void> shipOrder(@PathVariable String orderNo,
                                  @RequestBody(required = false) AdminShipOrderRequest request) {
        String expressNo = request == null ? null : request.getExpressNo();
        String expressCompany = request == null ? null : request.getExpressCompany();
        adminOrderService.shipOrder(orderNo, expressNo, expressCompany);
        operationLogService.record("ORDER_SHIP", "order:" + orderNo,
                "ship expressNo=" + expressNo + ",expressCompany=" + expressCompany);
        return Result.success();
    }

    @PostMapping("/{orderNo}/tracking")
    @PreAuthorize("@permissionService.hasPerm('admin:orders:ship')")
    // 功能：新增订单物流轨迹记录
    public Result<Void> addTracking(@PathVariable String orderNo,
                                    @RequestBody(required = false) AdminOrderTrackingRequest request) {
        adminOrderService.addTrackingEvent(orderNo, request);
        operationLogService.record("ORDER_TRACKING_ADD", "order:" + orderNo,
                "tracking title=" + (request == null ? null : request.getTitle()));
        return Result.success();
    }

    @GetMapping("/export")
    @PreAuthorize("@permissionService.hasPerm('admin:orders:export')")
    // 功能：导出订单
    public void exportOrders(@RequestParam(required = false) String orderNo,
                             @RequestParam(required = false) Long userId,
                             @RequestParam(required = false) Integer status,
                             HttpServletResponse response) throws IOException {
        byte[] data = adminOrderService.exportOrders(orderNo, userId, status);
        operationLogService.record("ORDER_EXPORT", "order:export", "orderNo=" + orderNo + ",userId=" + userId + ",status=" + status);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=orders.xlsx");
        response.getOutputStream().write(data);
        response.flushBuffer();
    }
}
```

---

## 3. 商品管理

### 3.1 购物车服务实现 (CartServiceImpl.java)

```java
package com.thinking.backendmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.thinking.backendmall.common.BusinessException;
import com.thinking.backendmall.entity.CartItem;
import com.thinking.backendmall.entity.Product;
import com.thinking.backendmall.repository.CartItemRepository;
import com.thinking.backendmall.repository.ProductRepository;
import com.thinking.backendmall.service.CartService;
import com.thinking.backendmall.vo.CartItemView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    // 功能：新增到购物车
    public void addToCart(Long userId, Long productId, Integer quantity) {
        if (productId == null) {
            throw new BusinessException("ProductId is required");
        }
        int qty = quantity == null || quantity <= 0 ? 1 : quantity;
        Product product = productRepository.selectById(productId);
        if (product == null || !"ON".equals(product.getStatus())) {
            throw new BusinessException("Product not available");
        }

        CartItem existing = cartItemRepository.selectOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .eq(CartItem::getProductId, productId));

        int existingQty = existing == null ? 0 : existing.getQuantity();
        Integer stock = product.getStock();
        if (stock != null && existingQty + qty > stock) {
            throw new BusinessException("Insufficient stock");
        }

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + qty);
            cartItemRepository.updateById(existing);
            return;
        }

        CartItem item = new CartItem();
        item.setUserId(userId);
        item.setProductId(productId);
        item.setQuantity(qty);
        item.setChecked(1);
        item.setCreatedAt(LocalDateTime.now());
        cartItemRepository.insert(item);
    }

    @Override
    // 功能：查询购物车明细
    public List<CartItemView> listCartItems(Long userId) {
        List<CartItem> items = cartItemRepository.selectList(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId));
        if (items.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> productIds = new ArrayList<>();
        for (CartItem item : items) {
            productIds.add(item.getProductId());
        }
        List<Product> products = productRepository.selectBatchIds(productIds);
        Map<Long, Product> productMap = new HashMap<>();
        for (Product product : products) {
            productMap.put(product.getId(), product);
        }
        List<CartItemView> views = new ArrayList<>();
        for (CartItem item : items) {
            Product product = productMap.get(item.getProductId());
            if (product == null) {
                continue;
            }
            CartItemView view = new CartItemView();
            view.setCartItemId(item.getId());
            view.setProductId(item.getProductId());
            view.setName(product.getName());
            view.setPrice(product.getPrice() == null ? BigDecimal.ZERO : product.getPrice());
            view.setImage(product.getCoverUrl());
            view.setQuantity(item.getQuantity());
            view.setChecked(item.getChecked());
            views.add(view);
        }
        return views;
    }

    @Override
    @Transactional
    // 功能：更新购物车明细
    public void updateCartItem(Long userId, Long cartItemId, Integer quantity, Integer checked) {
        CartItem item = cartItemRepository.selectById(cartItemId);
        if (item == null || !userId.equals(item.getUserId())) {
            throw new BusinessException(404, "Cart item not found");
        }
        if (quantity != null) {
            if (quantity <= 0) {
                throw new BusinessException("Quantity must be positive");
            }
            Product product = productRepository.selectById(item.getProductId());
            if (product == null || !"ON".equals(product.getStatus())) {
                throw new BusinessException("Product not available");
            }
            Integer stock = product.getStock();
            if (stock != null && quantity > stock) {
                throw new BusinessException("Insufficient stock");
            }
            item.setQuantity(quantity);
        }
        if (checked != null) {
            item.setChecked(checked);
        }
        cartItemRepository.updateById(item);
    }

    @Override
    @Transactional
    // 功能：删除购物车明细
    public void deleteCartItem(Long userId, Long cartItemId) {
        CartItem item = cartItemRepository.selectById(cartItemId);
        if (item == null || !userId.equals(item.getUserId())) {
            throw new BusinessException(404, "Cart item not found");
        }
        cartItemRepository.deleteById(cartItemId);
    }
}
```

---

## 4. 权限控制

### 4.1 安全配置 (SecurityConfig.java)

```java
package com.thinking.backendmall.config.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    // 功能：配置Spring Security过滤器链
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter,
                                                   RestAuthenticationEntryPoint restAuthenticationEntryPoint,
                                                   RestAccessDeniedHandler restAccessDeniedHandler) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/health").permitAll()
                        .requestMatchers("/api/hello").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/home/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                        .requestMatchers("/error")
                        .permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    // 功能：配置密码加密器
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    // 功能：配置跨域配置源
    public CorsConfigurationSource corsConfigurationSource() {
        return (HttpServletRequest request) -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOriginPatterns(List.of("http://localhost:*"));
            configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            configuration.setAllowedHeaders(List.of("*"));
            configuration.setAllowCredentials(true);
            configuration.setMaxAge(3600L);
            return configuration;
        };
    }
}
```

### 4.2 JWT认证过滤器 (JwtAuthenticationFilter.java)

```java
package com.thinking.backendmall.config.security;

import com.thinking.backendmall.config.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthMemoryStore authMemoryStore;

    @Override
    // 功能：JWT认证过滤器，校验token并设置认证上下文
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (!authMemoryStore.isTokenBlacklisted(token)) {
                try {
                    var claims = jwtUtil.getClaimsFromToken(token);
                    String username = claims.getSubject();
                    Long userId = claims.get("userId", Long.class);
                    String roleKey = claims.get("roleKey", String.class);

                    AuthContext.setUserId(userId);
                    AuthContext.setUsername(username);
                    AuthContext.setRoleKey(roleKey);

                    List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + roleKey));
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e) {
                    SecurityContextHolder.clearContext();
                }
            }
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            AuthContext.clear();
        }
    }
}
```

### 4.3 权限服务 (PermissionService.java)

```java
package com.thinking.backendmall.config.security;

import com.thinking.backendmall.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService {

    @Autowired
    private MenuService menuService;

    // 功能：检查当前用户是否拥有指定权限
    public boolean hasPerm(String perm) {
        String roleKey = AuthContext.getRoleKey();
        if (roleKey == null) {
            return false;
        }
        List<String> perms = menuService.listMyPerms(roleKey);
        return perms != null && perms.contains(perm);
    }
}
```

---

## 文件清单

| 模块 | 文件路径 | 说明 |
|------|---------|------|
| 用户认证 | `backend-mall/src/main/java/com/thinking/backendmall/controller/AuthController.java` | 认证控制器 |
| 用户认证 | `backend-mall/src/main/java/com/thinking/backendmall/service/impl/AuthServiceImpl.java` | 认证服务实现 |
| 订单事务 | `backend-mall/src/main/java/com/thinking/backendmall/service/impl/OrderServiceImpl.java` | 订单服务实现 |
| 订单事务 | `backend-mall/src/main/java/com/thinking/backendmall/controller/admin/AdminOrderController.java` | 后台订单控制器 |
| 商品管理 | `backend-mall/src/main/java/com/thinking/backendmall/service/impl/CartServiceImpl.java` | 购物车服务 |
| 权限控制 | `backend-mall/src/main/java/com/thinking/backendmall/config/security/SecurityConfig.java` | 安全配置 |
| 权限控制 | `backend-mall/src/main/java/com/thinking/backendmall/config/security/JwtAuthenticationFilter.java` | JWT过滤器 |
| 权限控制 | `backend-mall/src/main/java/com/thinking/backendmall/config/security/PermissionService.java` | 权限服务 |
