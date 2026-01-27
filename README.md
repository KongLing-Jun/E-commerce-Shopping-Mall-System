# E-commerce-Shopping-Mall-System
基于Spring Boot+Vue的3c电子购物商城

------

## 1) 业务域模型与模块拆分

### 前台用户端模块（Vue + axios）

- 用户：注册/登录（JWT）
- 首页：轮播图 + 推荐/分类并行加载
- 搜索：关键词模糊查询 + 分页
- 商品详情：按 productId 获取基础信息/价格/库存/多图/规格参数
- 购物车：添加/查看/改数量/删除/勾选结算
- 订单：确认页（地址+清单+金额）→ 提交订单（事务）→ 模拟支付 → 状态追踪/确认收货

### 后台管理端模块（Vue + Element UI / 或你用Tailwind也行）

- 系统管理：用户分页、禁用、重置密码；菜单树、按钮权限；登录后按角色返回菜单做**动态路由**
- 商品管理：分页+分类筛选；新增/编辑；图片上传存URL；富文本HTML；上下架；轮播图配置
- 订单管理与统计：按时间段/订单号/用户ID筛选；发货（可录物流号）；统计聚合（今日订单/总销售/Top10）；导出Excel

------

## 2) 数据库表结构建议（够用版）

> 你文档只描述了流程，我这里给“最小可用字段”。后续你可以按需要扩展。

### 用户与权限（RBAC）

- `user`：id, username, phone, password_hash, status(1正常/0禁用), role_id, created_at
  - 禁用用户对应 `status = 0`
- `role`：id, role_key(ADMIN/USER), role_name
- `menu`：id, parent_id, name, path, component, type(MENU/BUTTON), perm_code, sort, visible
- `role_menu`：role_id, menu_id

### 商品与营销

- `category`：id, name, parent_id, sort, status
- `product`：id, category_id, name, brief, price, stock, status(ON/OFF), cover_url, detail_html, created_at
  - 商品详情需要：基础信息/价格/库存/多图/规格参数
- `product_image`：id, product_id, url, sort
- `banner`：id, image_url, link_type(PRODUCT/URL), link_target, sort, status
  - 轮播图：上传图片，关联跳转（商品ID/链接），设置排序

### 购物车与订单

- `cart_item`：id, user_id, product_id, quantity, checked(0/1), created_at
  - 购物车列表包含图/名/单价/数量（图名价来自product join）
- `address`：id, user_id, receiver, phone, province, city, area, detail, is_default
  - 订单确认页要选择收货地址
- `order`：id, order_no, user_id, total_amount, pay_amount, status(0/1/2/3), address_snapshot, created_at, paid_at, shipped_at, finished_at
  - 状态：0待支付/1待发货/2已发货/3已完成
- `order_item`：id, order_id, product_id, product_name_snapshot, price_snapshot, quantity, image_snapshot
  - 子订单记录“商品快照”
- （可选）`order_delivery`：order_id, express_no, express_company

------

## 3) 接口清单（按模块给到 URL/入参/出参/权限）

### 3.1 前台用户端 API

#### A. 认证

1. `POST /api/auth/register`

- body：username, phone, password, confirmPassword
- rules：账号/手机号唯一；密码加密存储（MD5/BCrypt）

1. `POST /api/auth/login`

- body：username, password
- resp：token(JWT)、userId、roleKey
- 前端保存 localStorage，后续请求 header 携带

#### B. 首页/搜索/详情

1. `GET /api/home/banners`（轮播图）
2. `GET /api/home/recommend`（推荐商品/分类）
3. `GET /api/products`（搜索+分页）

- query：keyword, page, size, categoryId(optional)
- 后端：`LIKE %keyword%`

1. `GET /api/products/{productId}`（商品详情）

- resp：name/price/stock/images/specs(可先JSON字段)/detailHtml

#### C. 购物车

1. `POST /api/cart/items`（加入购物车）

- body：productId, quantity(默认1)
- rules：登录校验→库存校验→存在则数量+1否则新增

1. `GET /api/cart/items`（查看购物车）

- resp：[{cartItemId, productId, name, price, image, quantity, checked}]

1. `PUT /api/cart/items/{id}`（改数量/勾选）

- body：quantity? checked?
- 对应“点击 +/- 更新数量；勾选计算总价”

1. `DELETE /api/cart/items/{id}`（删除）

#### D. 地址

1. `GET /api/addresses` / `POST /api/addresses` / `PUT /api/addresses/{id}` / `DELETE /api/addresses/{id}`

- 订单确认页依赖地址接口

#### E. 订单

1. `GET /api/orders/pre`（订单确认 Pre-Order）

- resp：checked cart items + address list + totalAmount

1. `POST /api/orders`（提交订单 Create Order）

- body：addressId
- 事务步骤：校验库存 → 创建Order → 创建OrderItem快照 → 扣库存 → 清已购购物车
- resp：orderNo

1. `POST /api/orders/{orderNo}/pay`（模拟支付 Mock Payment）

- body：payAmount(前端传或后端计算)
- rules：校验金额 → 支付成功状态改“已支付/待发货”

1. `GET /api/orders`（我的订单列表）

- query：status(0/1/2/3)、page、size
- 按订单状态返回

1. `POST /api/orders/{orderNo}/confirm`（确认收货→已完成）

------

### 3.2 后台管理 API（需 ADMIN 角色）

#### A. 用户与菜单（动态路由）

1. `GET /admin/users`（分页列表）
2. `PUT /admin/users/{id}/disable`（禁用 status=0）
3. `PUT /admin/users/{id}/reset-password`
4. `GET /admin/menus/tree`（菜单树）
5. `POST /admin/menus` / `PUT /admin/menus/{id}` / `DELETE /admin/menus/{id}`（菜单/按钮权限）
6. `GET /admin/menus/my`（按角色返回可访问菜单，用于前端动态生成侧边栏路由）

#### B. 商品/轮播图

1. `GET /admin/products`（分页+分类筛选）
2. `POST /admin/products` / `PUT /admin/products/{id}`（新增/编辑：名称描述价格库存；图片上传URL；富文本HTML）
3. `PUT /admin/products/{id}/on` / `PUT /admin/products/{id}/off`（上下架）
4. `POST /admin/upload`（上传图片，返回URL）
5. `GET /admin/banners` / `POST /admin/banners` / `PUT /admin/banners/{id}` / `DELETE /admin/banners/{id}`

- 轮播图：图片 + 跳转目标 + 排序

#### C. 订单与统计/导出

1. `GET /admin/orders`（时间段/订单号/用户ID筛选）
2. `POST /admin/orders/{orderNo}/ship`（发货，录物流号可选，状态改已发货）
3. `GET /admin/stats/overview`（今日订单数、总销售额、热销Top10）
4. `GET /admin/orders/export`（POI 导出 Excel 下载）

------

## 4) 关键业务规则（你写代码时最容易踩坑的点）

### 4.1 下单事务边界（必须做成一个Service事务）

- 事务内依次：库存校验 → 主单 → 子单快照 → 扣库存 → 清购物车已购项
  **建议加分点**
- 扣库存用“乐观扣减”（`update product set stock=stock-? where id=? and stock>=?`）避免超卖
- 订单号 `order_no`：时间+随机/雪花（面试讲得清即可）

### 4.2 订单状态机

- 状态枚举：0待支付 → 1待发货 → 2已发货 → 3已完成
  **状态流转约束（建议你写在代码里校验）**
- 待支付才能 pay；待发货才能 ship；已发货才能 confirm

### 4.3 动态路由 RBAC

- 后端按角色返回菜单列表，前端动态生成侧边栏与路由
  **建议：**
- `menu.type=BUTTON` 用 `perm_code` 做按钮级权限（前端指令/组件控制显示）

------

## 5) 最小验收用例（接口测试脚本就按它写）

### 前台主链路（必须全通）

1. 注册→登录拿JWT
2. 首页并行拉轮播图+推荐
3. 搜索关键词分页返回
4. 进商品详情，展示库存/多图/参数
5. 加购（存在则+1）→ 查看购物车 → 改数量/勾选/删除
6. 去结算进入确认页，选择地址，金额正确
7. 提交订单（事务回滚验证：故意让某一步失败）
8. 模拟支付成功，订单状态变“待发货”
9. 我的订单按状态筛选；确认收货变“已完成”

### 后台主链路

1. 用户分页、禁用用户 status=0，禁用后前台登录/下单受限
2. 菜单树 + 按角色下发菜单，前端动态路由生效
3. 商品新增/编辑（上传图片得URL、富文本HTML），前台可见
4. 轮播图配置排序与跳转目标
5. 订单筛选查询；发货更新为已发货
6. 统计聚合+ECharts展示；导出Excel可下载

------

## 6) 继续把“20天计划”细到每天要写哪些接口/页面（执行清单）

如果你按你原来的20天复习节奏不变，我建议把**编码交付**按下面拆（每天都能“提交一版可运行”）：

- Day1：后端骨架 + 前端骨架 + Result/异常/拦截器（为JWT铺路）
- Day2：RBAC表 + user/role/menu 初始化数据（至少 ADMIN/USER 两个角色）
- Day3：注册/登录JWT + 前端登录页联调
- Day4：首页：banners + recommend 接口 + 前端并行请求渲染
- Day5：商品搜索分页接口 + 列表页
- Day6：商品详情接口 + 详情页（多图/库存/参数）
- Day7：购物车：加购/列表/改数量/删/勾选结算
- Day8：地址CRUD + 确认页/选择地址
- Day9：提交订单事务（主单/子单/扣库存/清购物车）
- Day10：模拟支付 + 状态机约束 + 我的订单列表/筛选
- Day11：后台登录 + /admin/menus/my 动态路由落地
- Day12：后台用户管理（分页/禁用/重置密码）
- Day13：后台商品管理 CRUD + 上下架 + 富文本HTML字段
- Day14：上传接口 + 商品图片URL存储联调
- Day15：轮播图管理（上传/排序/跳转目标）
- Day16：后台订单查询筛选 + 发货（物流号可选）
- Day17：统计接口（Count/Sum/Top10）+ ECharts面板
- Day18：POI导出Excel下载
- Day19：Redis缓存（商品列表/详情/首页推荐）+ 缓存失效策略（你自己定义）
- Day20：Docker compose（mysql+redis+后端+前端，可选nginx）+ Jenkins流水线 + Postman/Newman冒烟用例

------

如果你下一步想更“像真实项目”，我建议你告诉我你准备选的组合（比如：**Spring Security 还是拦截器JWT**、前端后台是否都用 Vue），我可以直接给你：

- **接口返回JSON样例（含字段名）**
- **每张表的建表SQL（MySQL 8）**
- **订单提交的事务伪代码（按你选的技术栈写成可复制的Service模板）**
