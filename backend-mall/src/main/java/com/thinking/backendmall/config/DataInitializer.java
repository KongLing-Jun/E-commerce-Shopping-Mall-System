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
        ensureCategoryMenus();
        ensureRoleMenus();
        ensureUserRolePermission();
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

        Menu adminRoles = new Menu();
        adminRoles.setName("Role Management");
        adminRoles.setPath("/admin/roles");
        adminRoles.setComponent("AdminRoles");
        adminRoles.setType("MENU");
        adminRoles.setPermCode("admin:roles:list");
        adminRoles.setSort(2);
        adminRoles.setVisible(1);
        menuRepository.insert(adminRoles);

        Menu adminProducts = new Menu();
        adminProducts.setName("Product Management");
        adminProducts.setPath("/admin/products");
        adminProducts.setComponent("AdminProducts");
        adminProducts.setType("MENU");
        adminProducts.setPermCode("admin:products:list");
        adminProducts.setSort(3);
        adminProducts.setVisible(1);
        menuRepository.insert(adminProducts);

        Menu adminCategories = new Menu();
        adminCategories.setName("Category Management");
        adminCategories.setPath("/admin/categories");
        adminCategories.setComponent("AdminCategories");
        adminCategories.setType("MENU");
        adminCategories.setPermCode("admin:categories:list");
        adminCategories.setSort(4);
        adminCategories.setVisible(1);
        menuRepository.insert(adminCategories);

        Menu adminOrders = new Menu();
        adminOrders.setName("Order Management");
        adminOrders.setPath("/admin/orders");
        adminOrders.setComponent("AdminOrders");
        adminOrders.setType("MENU");
        adminOrders.setPermCode("admin:orders:list");
        adminOrders.setSort(5);
        adminOrders.setVisible(1);
        menuRepository.insert(adminOrders);

        Menu adminStats = new Menu();
        adminStats.setName("Stats Dashboard");
        adminStats.setPath("/admin/stats");
        adminStats.setComponent("AdminStats");
        adminStats.setType("MENU");
        adminStats.setPermCode("admin:stats:view");
        adminStats.setSort(6);
        adminStats.setVisible(1);
        menuRepository.insert(adminStats);

        Menu adminBanners = new Menu();
        adminBanners.setName("Banner Management");
        adminBanners.setPath("/admin/banners");
        adminBanners.setComponent("AdminBanners");
        adminBanners.setType("MENU");
        adminBanners.setPermCode("admin:banners:list");
        adminBanners.setSort(7);
        adminBanners.setVisible(1);
        menuRepository.insert(adminBanners);

        Menu adminCarts = new Menu();
        adminCarts.setName("Cart Management");
        adminCarts.setPath("/admin/carts");
        adminCarts.setComponent("AdminCarts");
        adminCarts.setType("MENU");
        adminCarts.setPermCode("admin:carts:list");
        adminCarts.setSort(8);
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

        Menu userRole = new Menu();
        userRole.setName("Assign Role");
        userRole.setType("BUTTON");
        userRole.setPermCode("admin:users:role");
        userRole.setVisible(0);
        menuRepository.insert(userRole);

        Menu roleEdit = new Menu();
        roleEdit.setName("Manage Role");
        roleEdit.setType("BUTTON");
        roleEdit.setPermCode("admin:roles:edit");
        roleEdit.setVisible(0);
        menuRepository.insert(roleEdit);

        Menu roleDelete = new Menu();
        roleDelete.setName("Delete Role");
        roleDelete.setType("BUTTON");
        roleDelete.setPermCode("admin:roles:delete");
        roleDelete.setVisible(0);
        menuRepository.insert(roleDelete);

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

        Menu productDelete = new Menu();
        productDelete.setName("Delete Product");
        productDelete.setType("BUTTON");
        productDelete.setPermCode("admin:products:delete");
        productDelete.setVisible(0);
        menuRepository.insert(productDelete);

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

        Menu orderExport = new Menu();
        orderExport.setName("Export Orders");
        orderExport.setType("BUTTON");
        orderExport.setPermCode("admin:orders:export");
        orderExport.setVisible(0);
        menuRepository.insert(orderExport);

        Menu cartDelete = new Menu();
        cartDelete.setName("Delete Cart");
        cartDelete.setType("BUTTON");
        cartDelete.setPermCode("admin:carts:delete");
        cartDelete.setVisible(0);
        menuRepository.insert(cartDelete);

        Menu categoryEdit = new Menu();
        categoryEdit.setName("Manage Category");
        categoryEdit.setType("BUTTON");
        categoryEdit.setPermCode("admin:categories:edit");
        categoryEdit.setVisible(0);
        menuRepository.insert(categoryEdit);

        Menu categoryDelete = new Menu();
        categoryDelete.setName("Delete Category");
        categoryDelete.setType("BUTTON");
        categoryDelete.setPermCode("admin:categories:delete");
        categoryDelete.setVisible(0);
        menuRepository.insert(categoryDelete);

        Menu uploadImage = new Menu();
        uploadImage.setName("Upload File");
        uploadImage.setType("BUTTON");
        uploadImage.setPermCode("admin:upload");
        uploadImage.setVisible(0);
        menuRepository.insert(uploadImage);
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

    private void ensureCategoryMenus() {
        Role adminRole = roleRepository.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getRoleKey, "ADMIN"));
        if (adminRole == null) {
            return;
        }

        Menu categoryMenu = menuRepository.selectOne(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getPermCode, "admin:categories:list"));
        if (categoryMenu == null) {
            categoryMenu = new Menu();
            categoryMenu.setName("Category Management");
            categoryMenu.setPath("/admin/categories");
            categoryMenu.setComponent("AdminCategories");
            categoryMenu.setType("MENU");
            categoryMenu.setPermCode("admin:categories:list");
            categoryMenu.setSort(3);
            categoryMenu.setVisible(1);
            menuRepository.insert(categoryMenu);
        }
        ensureRoleMenu(adminRole.getId(), categoryMenu.getId());

        Menu categoryEdit = menuRepository.selectOne(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getPermCode, "admin:categories:edit"));
        if (categoryEdit == null) {
            categoryEdit = new Menu();
            categoryEdit.setName("Manage Category");
            categoryEdit.setType("BUTTON");
            categoryEdit.setPermCode("admin:categories:edit");
            categoryEdit.setVisible(0);
            menuRepository.insert(categoryEdit);
        }
        ensureRoleMenu(adminRole.getId(), categoryEdit.getId());

        Menu categoryDelete = menuRepository.selectOne(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getPermCode, "admin:categories:delete"));
        if (categoryDelete == null) {
            categoryDelete = new Menu();
            categoryDelete.setName("Delete Category");
            categoryDelete.setType("BUTTON");
            categoryDelete.setPermCode("admin:categories:delete");
            categoryDelete.setVisible(0);
            menuRepository.insert(categoryDelete);
        }
        ensureRoleMenu(adminRole.getId(), categoryDelete.getId());
    }

    private void ensureRoleMenus() {
        Role adminRole = roleRepository.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getRoleKey, "ADMIN"));
        if (adminRole == null) {
            return;
        }

        Menu roleMenu = menuRepository.selectOne(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getPermCode, "admin:roles:list"));
        if (roleMenu == null) {
            roleMenu = new Menu();
            roleMenu.setName("Role Management");
            roleMenu.setPath("/admin/roles");
            roleMenu.setComponent("AdminRoles");
            roleMenu.setType("MENU");
            roleMenu.setPermCode("admin:roles:list");
            roleMenu.setSort(2);
            roleMenu.setVisible(1);
            menuRepository.insert(roleMenu);
        }
        ensureRoleMenu(adminRole.getId(), roleMenu.getId());

        Menu roleEdit = menuRepository.selectOne(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getPermCode, "admin:roles:edit"));
        if (roleEdit == null) {
            roleEdit = new Menu();
            roleEdit.setName("Manage Role");
            roleEdit.setType("BUTTON");
            roleEdit.setPermCode("admin:roles:edit");
            roleEdit.setVisible(0);
            menuRepository.insert(roleEdit);
        }
        ensureRoleMenu(adminRole.getId(), roleEdit.getId());

        Menu roleDelete = menuRepository.selectOne(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getPermCode, "admin:roles:delete"));
        if (roleDelete == null) {
            roleDelete = new Menu();
            roleDelete.setName("Delete Role");
            roleDelete.setType("BUTTON");
            roleDelete.setPermCode("admin:roles:delete");
            roleDelete.setVisible(0);
            menuRepository.insert(roleDelete);
        }
        ensureRoleMenu(adminRole.getId(), roleDelete.getId());
    }

    private void ensureUserRolePermission() {
        Role adminRole = roleRepository.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getRoleKey, "ADMIN"));
        if (adminRole == null) {
            return;
        }
        Menu userRole = menuRepository.selectOne(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getPermCode, "admin:users:role"));
        if (userRole == null) {
            userRole = new Menu();
            userRole.setName("Assign Role");
            userRole.setType("BUTTON");
            userRole.setPermCode("admin:users:role");
            userRole.setVisible(0);
            menuRepository.insert(userRole);
        }
        ensureRoleMenu(adminRole.getId(), userRole.getId());
    }

    private void ensureRoleMenu(Long roleId, Long menuId) {
        if (roleId == null || menuId == null) {
            return;
        }
        Long count = roleMenuRepository.selectCount(new LambdaQueryWrapper<RoleMenu>()
                .eq(RoleMenu::getRoleId, roleId)
                .eq(RoleMenu::getMenuId, menuId));
        if (count == null || count == 0) {
            RoleMenu rm = new RoleMenu();
            rm.setRoleId(roleId);
            rm.setMenuId(menuId);
            roleMenuRepository.insert(rm);
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
        electronics.setName("Phones");
        electronics.setParentId(0L);
        electronics.setSort(1);
        electronics.setStatus(1);

        Category home = new Category();
        home.setName("Laptops");
        home.setParentId(0L);
        home.setSort(2);
        home.setStatus(1);

        Category fashion = new Category();
        fashion.setName("Accessories");
        fashion.setParentId(0L);
        fashion.setSort(3);
        fashion.setStatus(1);

        for (Category category : Arrays.asList(electronics, home, fashion)) {
            categoryRepository.insert(category);
        }
    }

    private void initProducts() {
        List<Category> categories = categoryRepository.selectList(new QueryWrapper<>());
        Long electronicsId = findCategoryId(categories, "Phones");
        Long homeId = findCategoryId(categories, "Laptops");
        Long fashionId = findCategoryId(categories, "Accessories");

        List<Product> products = new ArrayList<>();
        products.add(buildProduct(electronicsId, "5G Smartphone", "120Hz display, 64MP camera",
                new BigDecimal("699.00"), 120,
                "https://images.unsplash.com/photo-1556656793-08538906a9f8?auto=format&fit=crop&w=600&q=80"));
        products.add(buildProduct(electronicsId, "Flagship Phone Pro", "Fast charging, 8GB RAM",
                new BigDecimal("899.00"), 80,
                "https://images.unsplash.com/photo-1520923642038-b4259acecbd7?auto=format&fit=crop&w=600&q=80"));
        products.add(buildProduct(homeId, "Ultrabook Laptop", "13-inch, 16GB RAM, 512GB SSD",
                new BigDecimal("1299.00"), 60,
                "https://images.unsplash.com/photo-1498050108023-c5249f4df085?auto=format&fit=crop&w=600&q=80"));
        products.add(buildProduct(homeId, "Gaming Laptop", "RTX graphics, 144Hz display",
                new BigDecimal("1599.00"), 45,
                "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?auto=format&fit=crop&w=600&q=80"));
        products.add(buildProduct(fashionId, "Wireless Earbuds", "Active noise canceling",
                new BigDecimal("179.00"), 150,
                "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=600&q=80"));
        products.add(buildProduct(fashionId, "Smart Watch", "Health tracking and GPS",
                new BigDecimal("299.00"), 95,
                "https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=600&q=80"));

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
        banners.add(buildBanner(products.get(0), 1,
                "https://images.unsplash.com/photo-1556656793-08538906a9f8?auto=format&fit=crop&w=1200&q=80"));
        banners.add(buildBanner(products.get(Math.min(1, products.size() - 1)), 2,
                "https://images.unsplash.com/photo-1498050108023-c5249f4df085?auto=format&fit=crop&w=1200&q=80"));
        banners.add(buildBanner(products.get(Math.min(2, products.size() - 1)), 3,
                "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=1200&q=80"));
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
