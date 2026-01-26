package com.thinking.backendmall.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.thinking.backendmall.entity.Banner;
import com.thinking.backendmall.entity.Category;
import com.thinking.backendmall.entity.Menu;
import com.thinking.backendmall.entity.Product;
import com.thinking.backendmall.entity.Role;
import com.thinking.backendmall.entity.RoleMenu;
import com.thinking.backendmall.entity.User;
import com.thinking.backendmall.repository.BannerRepository;
import com.thinking.backendmall.repository.CategoryRepository;
import com.thinking.backendmall.repository.MenuRepository;
import com.thinking.backendmall.repository.ProductRepository;
import com.thinking.backendmall.repository.RoleMenuRepository;
import com.thinking.backendmall.repository.RoleRepository;
import com.thinking.backendmall.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private RoleMenuRepository roleMenuRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BannerRepository bannerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (roleRepository.selectCount(new QueryWrapper<>()) == 0) {
            initRoles();
        }
        if (menuRepository.selectCount(new QueryWrapper<>()) == 0) {
            initMenus();
        }
        if (roleMenuRepository.selectCount(new QueryWrapper<>()) == 0) {
            initRoleMenus();
        }
        if (userRepository.selectCount(new QueryWrapper<>()) == 0) {
            initUsers();
        }
        if (categoryRepository.selectCount(new QueryWrapper<>()) == 0) {
            initCategories();
        }
        if (productRepository.selectCount(new QueryWrapper<>()) == 0) {
            initProducts();
        }
        if (bannerRepository.selectCount(new QueryWrapper<>()) == 0) {
            initBanners();
        }
    }

    private void initRoles() {
        Role admin = new Role();
        admin.setRoleKey("ADMIN");
        admin.setRoleName("Admin");
        roleRepository.insert(admin);

        Role user = new Role();
        user.setRoleKey("USER");
        user.setRoleName("User");
        roleRepository.insert(user);
    }

    private void initMenus() {
        // Frontend menus
        Menu home = new Menu();
        home.setName("Home");
        home.setPath("/");
        home.setComponent("Home");
        home.setType("MENU");
        home.setSort(1);
        home.setVisible(1);
        menuRepository.insert(home);

        Menu products = new Menu();
        products.setName("Products");
        products.setPath("/products");
        products.setComponent("ProductList");
        products.setType("MENU");
        products.setSort(2);
        products.setVisible(1);
        menuRepository.insert(products);

        // Admin menus
        Menu adminUsers = new Menu();
        adminUsers.setName("User Management");
        adminUsers.setPath("/admin/users");
        adminUsers.setComponent("AdminUsers");
        adminUsers.setType("MENU");
        adminUsers.setPermCode("admin:users:list");
        adminUsers.setSort(1);
        adminUsers.setVisible(1);
        menuRepository.insert(adminUsers);

        Menu adminProducts = new Menu();
        adminProducts.setName("Product Management");
        adminProducts.setPath("/admin/products");
        adminProducts.setComponent("AdminProducts");
        adminProducts.setType("MENU");
        adminProducts.setPermCode("admin:products:list");
        adminProducts.setSort(2);
        adminProducts.setVisible(1);
        menuRepository.insert(adminProducts);

        Menu adminOrders = new Menu();
        adminOrders.setName("Order Management");
        adminOrders.setPath("/admin/orders");
        adminOrders.setComponent("AdminOrders");
        adminOrders.setType("MENU");
        adminOrders.setPermCode("admin:orders:list");
        adminOrders.setSort(3);
        adminOrders.setVisible(1);
        menuRepository.insert(adminOrders);

        Menu adminBanners = new Menu();
        adminBanners.setName("Banner Management");
        adminBanners.setPath("/admin/banners");
        adminBanners.setComponent("AdminBanners");
        adminBanners.setType("MENU");
        adminBanners.setPermCode("admin:banners:list");
        adminBanners.setSort(4);
        adminBanners.setVisible(1);
        menuRepository.insert(adminBanners);

        Menu adminCarts = new Menu();
        adminCarts.setName("Cart Management");
        adminCarts.setPath("/admin/carts");
        adminCarts.setComponent("AdminCarts");
        adminCarts.setType("MENU");
        adminCarts.setPermCode("admin:carts:list");
        adminCarts.setSort(5);
        adminCarts.setVisible(1);
        menuRepository.insert(adminCarts);

        // Button permissions
        Menu userDisable = new Menu();
        userDisable.setName("Disable User");
        userDisable.setType("BUTTON");
        userDisable.setPermCode("admin:users:disable");
        userDisable.setVisible(0);
        menuRepository.insert(userDisable);

        Menu userReset = new Menu();
        userReset.setName("Reset Password");
        userReset.setType("BUTTON");
        userReset.setPermCode("admin:users:reset");
        userReset.setVisible(0);
        menuRepository.insert(userReset);

        Menu productOn = new Menu();
        productOn.setName("Enable Product");
        productOn.setType("BUTTON");
        productOn.setPermCode("admin:products:on");
        productOn.setVisible(0);
        menuRepository.insert(productOn);

        Menu productOff = new Menu();
        productOff.setName("Disable Product");
        productOff.setType("BUTTON");
        productOff.setPermCode("admin:products:off");
        productOff.setVisible(0);
        menuRepository.insert(productOff);

        Menu bannerEdit = new Menu();
        bannerEdit.setName("Manage Banner");
        bannerEdit.setType("BUTTON");
        bannerEdit.setPermCode("admin:banners:edit");
        bannerEdit.setVisible(0);
        menuRepository.insert(bannerEdit);

        Menu orderShip = new Menu();
        orderShip.setName("Ship Order");
        orderShip.setType("BUTTON");
        orderShip.setPermCode("admin:orders:ship");
        orderShip.setVisible(0);
        menuRepository.insert(orderShip);

        Menu cartDelete = new Menu();
        cartDelete.setName("Delete Cart");
        cartDelete.setType("BUTTON");
        cartDelete.setPermCode("admin:carts:delete");
        cartDelete.setVisible(0);
        menuRepository.insert(cartDelete);
    }

    private void initRoleMenus() {
        Role adminRole = roleRepository.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getRoleKey, "ADMIN"));
        Role userRole = roleRepository.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getRoleKey, "USER"));
        if (adminRole == null || userRole == null) {
            return;
        }

        List<Menu> allMenus = menuRepository.selectList(new QueryWrapper<>());

        // ADMIN has all permissions
        for (Menu menu : allMenus) {
            RoleMenu rm = new RoleMenu();
            rm.setRoleId(adminRole.getId());
            rm.setMenuId(menu.getId());
            roleMenuRepository.insert(rm);
        }

        // USER only gets frontend menus
        for (Menu menu : allMenus) {
            if ("MENU".equals(menu.getType()) && menu.getPermCode() == null) {
                RoleMenu rm = new RoleMenu();
                rm.setRoleId(userRole.getId());
                rm.setMenuId(menu.getId());
                roleMenuRepository.insert(rm);
            }
        }
    }

    private void initUsers() {
        Role adminRole = roleRepository.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getRoleKey, "ADMIN"));
        Role userRole = roleRepository.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getRoleKey, "USER"));
        if (adminRole == null || userRole == null) {
            return;
        }

        User admin = new User();
        admin.setUsername("admin");
        admin.setPhone("13800000000");
        admin.setPasswordHash(passwordEncoder.encode("123456"));
        admin.setStatus(1);
        admin.setRoleId(adminRole.getId());
        admin.setCreatedAt(LocalDateTime.now());
        userRepository.insert(admin);

        User testUser = new User();
        testUser.setUsername("user");
        testUser.setPhone("13800000001");
        testUser.setPasswordHash(passwordEncoder.encode("123456"));
        testUser.setStatus(1);
        testUser.setRoleId(userRole.getId());
        testUser.setCreatedAt(LocalDateTime.now());
        userRepository.insert(testUser);
    }

    private void initCategories() {
        Category electronics = new Category();
        electronics.setName("Electronics");
        electronics.setParentId(0L);
        electronics.setSort(1);
        electronics.setStatus(1);

        Category home = new Category();
        home.setName("Home");
        home.setParentId(0L);
        home.setSort(2);
        home.setStatus(1);

        Category fashion = new Category();
        fashion.setName("Fashion");
        fashion.setParentId(0L);
        fashion.setSort(3);
        fashion.setStatus(1);

        for (Category category : Arrays.asList(electronics, home, fashion)) {
            categoryRepository.insert(category);
        }
    }

    private void initProducts() {
        List<Category> categories = categoryRepository.selectList(new QueryWrapper<>());
        Long electronicsId = findCategoryId(categories, "Electronics");
        Long homeId = findCategoryId(categories, "Home");
        Long fashionId = findCategoryId(categories, "Fashion");

        List<Product> products = new ArrayList<>();
        products.add(buildProduct(electronicsId, "Wireless Headphones", "Noise cancelling, 30h battery",
                new BigDecimal("199.00"), 120, "https://picsum.photos/seed/headphones/600/400"));
        products.add(buildProduct(electronicsId, "Smart Watch", "Fitness tracking and notifications",
                new BigDecimal("249.00"), 80, "https://picsum.photos/seed/watch/600/400"));
        products.add(buildProduct(homeId, "Coffee Maker", "12-cup brewer with timer",
                new BigDecimal("89.00"), 60, "https://picsum.photos/seed/coffee/600/400"));
        products.add(buildProduct(homeId, "Air Purifier", "HEPA filtration for clean air",
                new BigDecimal("159.00"), 45, "https://picsum.photos/seed/purifier/600/400"));
        products.add(buildProduct(fashionId, "Canvas Backpack", "Lightweight daily carry",
                new BigDecimal("69.00"), 150, "https://picsum.photos/seed/backpack/600/400"));
        products.add(buildProduct(fashionId, "Running Shoes", "Cushioned and breathable",
                new BigDecimal("129.00"), 95, "https://picsum.photos/seed/shoes/600/400"));

        for (Product product : products) {
            productRepository.insert(product);
        }
    }

    private void initBanners() {
        List<Product> products = productRepository.selectList(new QueryWrapper<>());
        if (products.isEmpty()) {
            return;
        }
        List<Banner> banners = new ArrayList<>();
        banners.add(buildBanner(products.get(0), 1, "https://picsum.photos/seed/banner1/1200/400"));
        banners.add(buildBanner(products.get(Math.min(1, products.size() - 1)), 2,
                "https://picsum.photos/seed/banner2/1200/400"));
        banners.add(buildBanner(products.get(Math.min(2, products.size() - 1)), 3,
                "https://picsum.photos/seed/banner3/1200/400"));
        for (Banner banner : banners) {
            bannerRepository.insert(banner);
        }
    }

    private Long findCategoryId(List<Category> categories, String name) {
        Optional<Category> match = categories.stream()
                .filter(category -> name.equals(category.getName()))
                .findFirst();
        if (match.isPresent()) {
            return match.get().getId();
        }
        return categories.isEmpty() ? null : categories.get(0).getId();
    }

    private Product buildProduct(Long categoryId, String name, String brief, BigDecimal price, int stock,
            String coverUrl) {
        Product product = new Product();
        product.setCategoryId(categoryId);
        product.setName(name);
        product.setBrief(brief);
        product.setPrice(price);
        product.setStock(stock);
        product.setStatus("ON");
        product.setCoverUrl(coverUrl);
        product.setDetailHtml("<p>Sample detail for " + name + "</p>");
        product.setCreatedAt(LocalDateTime.now());
        return product;
    }

    private Banner buildBanner(Product product, int sort, String imageUrl) {
        Banner banner = new Banner();
        banner.setImageUrl(imageUrl);
        banner.setLinkType("PRODUCT");
        banner.setLinkTarget(product.getId().toString());
        banner.setSort(sort);
        banner.setStatus(1);
        return banner;
    }
}
