# Playwright 功能核验报告（2026-04-11）

## 1. 核验范围

- 仓库：`F:\mall\E-commerce-Shopping-Mall-System`
- 核验方式：
  - 代码检查：前后端路由、页面、控制器、服务实现
  - Playwright 实测：前台公开页、用户登录后流程、后台管理页
  - 对照材料：你提供的系统模块图
- 实测地址：
  - `http://127.0.0.1:5173`
  - `http://localhost:5173`
  - `http://127.0.0.1:8080`

说明：
- 前端在 `localhost` 下可正常联调；在 `127.0.0.1` 下存在鉴权相关阻断。
- 报告中的“已实现”表示代码和页面基本具备；“部分实现”表示有页面/接口，但存在关键缺陷或闭环未打通；“未完成”表示需求目标未真正满足。

## 2. 总体结论

| 编号 | 功能 | 结论 | 说明 |
| --- | --- | --- | --- |
| F1 | 用户注册与登录 | 部分实现 | 登录态维护可用，但 `127.0.0.1` 下注册/登录被 403 阻断 |
| F2 | 商品首页展示 | 部分实现 | 推荐/热销/分类正常，轮播图接口返回业务 500，首页显示“暂无轮播图” |
| F3 | 商品分类与搜索 | 已实现 | 分类浏览、关键词搜索、分页组件均存在，实测搜索可用 |
| F4 | 商品详情查看 | 已实现 | 价格、库存、多图、详情、规格参数均已展示 |
| F5 | 购物车管理 | 已实现 | 加入购物车、数量修改、删除、勾选结算入口均可用 |
| F6 | 订单处理 | 未完成 | 预结算和地址管理可用，但创建订单返回 `code=500`，后续支付/订单查询/确认收货链路被阻断 |
| F7 | 用户管理 | 基本实现 | 用户查询、禁用、重置密码、角色分配、创建/编辑页面和接口均存在，未逐项执行破坏性操作 |
| F8 | 菜单权限管理 | 部分实现 | 菜单维护和按钮权限具备，但“动态路由控制”不彻底，后台路由被静态注册 |
| F9 | 商品管理 | 基本实现 | 商品新增/编辑/删除/上下架/图片上传代码齐全，后台页面可进入 |
| F10 | 订单管理与统计 | 部分实现 | 后台订单页、统计页、导出/发货接口存在，但依赖前台下单闭环，当前无法完成真实业务验收 |

## 3. Playwright 实测结果

### 3.1 前台公开流程

- 首页可打开，推荐商品、热门商品、分类入口正常渲染。
- 商品列表页可按关键词搜索，`Headset` 查询能正确筛出 `Gaming Headset H7`。
- 商品详情页可展示价格、库存、多图、详情 HTML、规格参数。
- 未登录状态下点击“加入购物车”会收到后端 403，鉴权拦截生效。

### 3.2 用户登录后流程

- 在 `http://localhost:5173/login` 使用 `user / 123456` 可成功登录，登录后顶部显示“个人信息/退出”，说明登录状态维护正常。
- 登录后商品详情页“加入购物车”成功，请求 `POST /api/cart/items` 返回 200。
- 购物车页可看到商品、数量、勾选和结算汇总。
- 地址页可新增默认地址，请求 `POST /api/addresses` 返回 200。
- 订单确认页可加载已勾选商品和地址，但创建订单时前端点击后调用 `POST /api/orders`，后端返回 JSON `{"code":500,"message":"Server error"}`，订单未生成、购物车未清空、用户订单页为空。

### 3.3 后台管理流程

- 在 `http://localhost:5173/login` 使用 `admin / 123456` 可成功进入 `/admin/stats`。
- 后台统计页、用户页、菜单页、商品页、订单页均可访问，对应接口返回 200。
- 后台订单页能看到现有订单统计与列表，说明后台模块本身基本接通。
- 但由于前台真实下单失败，无法完成“前台下单 -> 后台查询/发货 -> 前台确认收货”的闭环验收。

## 4. 未完成项

### 4.1 F6 订单处理未完成

未满足原因：
- 订单确认页虽然能拉取预结算数据，但提交订单调用后端失败。
- 因订单创建失败，支付页、订单列表、订单跟踪、确认收货无法形成真实闭环。

直接影响：
- 这是 P0 闭环缺陷。
- 会连带影响 F10 的订单管理、统计分析、导出验收真实性。

关联代码：
- [OrderConfirm.vue](F:\mall\E-commerce-Shopping-Mall-System\frontend-mall\frontend-mall\src\views\OrderConfirm.vue:149)
- [OrderController.java](F:\mall\E-commerce-Shopping-Mall-System\backend-mall\src\main\java\com\thinking\backendmall\controller\OrderController.java:31)
- [OrderServiceImpl.java](F:\mall\E-commerce-Shopping-Mall-System\backend-mall\src\main\java\com\thinking\backendmall\service\impl\OrderServiceImpl.java:94)

### 4.2 F8 动态路由控制未完全达标

未满足原因：
- 后台路由在前端启动时已经静态声明了 `/admin/stats`、`/admin/users`、`/admin/products`、`/admin/menus`、`/admin/orders` 等页面。
- 虽然后面又实现了 `addAdminRoutes(menus)`，但静态路由已存在，实际并不是严格依赖菜单数据动态注入。

直接影响：
- 更像“管理员角色校验”，不是严格的“菜单驱动动态路由控制”。
- 若后续存在多个后台角色，前端路由层面的细粒度隔离会失真。

关联代码：
- [router/index.js](F:\mall\E-commerce-Shopping-Mall-System\frontend-mall\frontend-mall\src\router\index.js:111)
- [router/index.js](F:\mall\E-commerce-Shopping-Mall-System\frontend-mall\frontend-mall\src\router\index.js:200)

## 5. Bug 清单

### Bug 1：`127.0.0.1` 下注册/登录被 403 阻断

- 严重级别：高
- 现象：
  - Playwright 在 `http://127.0.0.1:5173/register` 提交注册，请求 `POST /api/auth/register` 返回 403
  - Playwright 在 `http://127.0.0.1:5173/login` 提交登录，请求 `POST /api/auth/login` 返回 403
  - 同样请求直连后端 `http://127.0.0.1:8080/api/auth/register` 可返回 200
- 判断：
  - 前后端联调环境兼容性有问题，不是接口本身未写
- 疑似原因：
  - CORS 仅允许 `http://localhost:*`，未包含 `http://127.0.0.1:*`
- 关联代码：
  - [SecurityConfig.java](F:\mall\E-commerce-Shopping-Mall-System\backend-mall\src\main\java\com\thinking\backendmall\config\security\SecurityConfig.java:68)

### Bug 2：首页轮播图接口异常，首页回退为空态

- 严重级别：高
- 现象：
  - `GET /api/home/banners` 返回业务响应 `{"code":500,"message":"Server error"}`
  - 首页轮播区显示“暂无轮播图”
- 判断：
  - F2 只完成了推荐/热销/分类展示，轮播图未形成可用结果
- 关联代码：
  - [Home.vue](F:\mall\E-commerce-Shopping-Mall-System\frontend-mall\frontend-mall\src\views\Home.vue:11)
  - [Home.vue](F:\mall\E-commerce-Shopping-Mall-System\frontend-mall\frontend-mall\src\views\Home.vue:211)
  - [HomeController.java](F:\mall\E-commerce-Shopping-Mall-System\backend-mall\src\main\java\com\thinking\backendmall\controller\HomeController.java:21)
  - [HomeServiceImpl.java](F:\mall\E-commerce-Shopping-Mall-System\backend-mall\src\main\java\com\thinking\backendmall\service\impl\HomeServiceImpl.java:35)

### Bug 3：订单创建失败，返回业务 500

- 严重级别：致命
- 现象：
  - 用户已登录
  - 已加入购物车
  - 已新增默认地址
  - 订单确认页提交后命中 `POST /api/orders`
  - 后端返回 `{"code":500,"message":"Server error","data":null}`
  - 用户订单页仍为空，购物车商品未被清空
- 判断：
  - F6 当前未通过验收
- 关联代码：
  - [OrderConfirm.vue](F:\mall\E-commerce-Shopping-Mall-System\frontend-mall\frontend-mall\src\views\OrderConfirm.vue:149)
  - [OrderController.java](F:\mall\E-commerce-Shopping-Mall-System\backend-mall\src\main\java\com\thinking\backendmall\controller\OrderController.java:31)
  - [OrderServiceImpl.java](F:\mall\E-commerce-Shopping-Mall-System\backend-mall\src\main\java\com\thinking\backendmall\service\impl\OrderServiceImpl.java:100)

### Bug 4：订单确认按钮存在重复提交风险

- 严重级别：高
- 现象：
  - Playwright 连续点击“提交并复核”后，网络面板出现两次 `POST /api/orders`
  - 前端只设置了 `:loading="loading"`，没有显式防重入
- 判断：
  - 即使后端后续修复，仍可能出现重复下单
- 关联代码：
  - [OrderConfirm.vue](F:\mall\E-commerce-Shopping-Mall-System\frontend-mall\frontend-mall\src\views\OrderConfirm.vue:61)
  - [OrderConfirm.vue](F:\mall\E-commerce-Shopping-Mall-System\frontend-mall\frontend-mall\src\views\OrderConfirm.vue:158)

### Bug 5：无地址时订单确认页缺少空态引导

- 严重级别：中
- 现象：
  - 首次进入订单确认页时，地址区为空白，仅有“编辑”按钮
  - 没有明显“暂无地址”或“请先新增地址”的提示
- 判断：
  - 不阻断功能，但会降低可用性和演示效果
- 关联代码：
  - [OrderConfirm.vue](F:\mall\E-commerce-Shopping-Mall-System\frontend-mall\frontend-mall\src\views\OrderConfirm.vue:21)
  - [Address.vue](F:\mall\E-commerce-Shopping-Mall-System\frontend-mall\frontend-mall\src\views\Address.vue:1)

### Bug 6：后台页面标题未更新，仍显示 `Login`

- 严重级别：低
- 现象：
  - Playwright 在 `/admin/stats`、`/admin/users`、`/admin/orders` 页面读取到的标题仍是 `Login`
- 判断：
  - 不影响业务，但影响系统完整度和演示观感
- 关联代码：
  - [router/index.js](F:\mall\E-commerce-Shopping-Mall-System\frontend-mall\frontend-mall\src\router\index.js:118)

## 6. 建议优先级

### P0 立即修复

- 修复订单创建 500，打通 F6 完整闭环
- 修复首页轮播图接口异常，补齐 F2
- 放宽或统一本地联调来源，解决 `127.0.0.1` 与 `localhost` 行为不一致

### P1 下一步修复

- 去掉后台静态路由或补齐基于菜单权限的真实动态注入
- 订单确认提交按钮增加防重入
- 无地址场景补空态与引导

### P2 可优化

- 修复后台页面标题
- 清理 Element Plus 已废弃 API 警告

## 7. 结论摘要

当前项目并不是“没做”，而是“主体功能框架基本齐全，但关键 P0 闭环未打通”。最核心的问题集中在两处：

- 首页轮播图不可用，F2 只能算部分实现
- 订单创建失败，导致 F6 无法验收，同时拖累 F10 的真实性

如果用于课程答辩或验收，建议先优先修复上述两项，再补上 `127.0.0.1/localhost` 联调兼容和动态路由控制一致性。
