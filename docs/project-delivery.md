# 3C 电子商城平台交付文档

## 1. 需求完成清单（当前版本）
- 前台：注册/登录、首页（轮播+推荐+热销+促销）、商品搜索分页、商品详情（多图+规格）、购物车、地址管理、下单、支付、我的订单、确认收货。
- 后台：用户管理、菜单与角色权限（RBAC）、商品管理、轮播图管理、订单查询/发货、统计看板、订单导出。
- 权限：JWT 鉴权、未登录 401、无权限 403、按钮级权限码控制（前端 `v-permission` + 后端 `@PreAuthorize`）。
- 安全：BCrypt、上传类型/大小限制、富文本 XSS 清洗、登录失败限制（Redis 计数）、关键操作确认弹窗。
- 运维：Docker Compose（MySQL/Redis/Backend/Frontend/Nginx）、Postman 权限与 E2E 脚本。

## 2. 架构说明
- 后端：Spring Boot + Spring MVC + Spring Security + MyBatis-Plus + Redis。
- 前端：Vue 3 + Vue Router + Axios + Element Plus + TailwindCSS。
- 数据层：MySQL（核心业务表 + RBAC + 订单扩展 + 操作日志）。
- 网关：Nginx 反向代理，支持 HTTP 与 HTTPS（自签名证书自动生成）。

## 3. 关键接口概览
- 认证：`/api/auth/register`、`/api/auth/login`、`/api/auth/logout`、`/api/auth/me`
- 商城：`/api/home/*`、`/api/products`、`/api/products/{id}`
- 购物车：`/api/cart/items`（增删改查）
- 地址：`/api/addresses`（CRUD）
- 订单：`/api/orders/pre`、`/api/orders`、`/api/orders/{orderNo}/pay`、`/api/orders/{orderNo}/confirm`
- 后台订单：`/api/admin/orders`、`/api/admin/orders/{orderNo}/ship`、`/api/admin/orders/export`
- 商家通知：`/api/admin/notices`、`/api/admin/notices/{id}/read`

## 4. 数据库初始化与迁移
- 基础建表：`backend-mall/src/main/resources/sql/basic.sql`
- 商家通知表：`backend-mall/src/main/resources/sql/merchant_notice.sql`
- 历史迁移：`backend-mall/src/main/resources/sql/migration_20260127.sql`

## 5. 部署步骤（Docker Compose）
1. 准备 Docker / Docker Compose。
2. 在项目根目录执行：
   ```bash
   docker compose up -d --build
   ```
3. 访问地址：
   - HTTP：`http://localhost:5174`
   - HTTPS：`https://localhost:5443`（自签名证书，浏览器会提示风险）

## 6. 测试与验收
- 权限验收：`postman/mall_permission_acceptance.postman_collection.json`
- 主链路验收：`postman/mall_e2e.postman_collection.json`
- 一键执行：
  ```powershell
  cd postman
  ./run_e2e.ps1
  ```

## 7. 演示账号
- 管理员：`admin / 123456`
- 普通用户：`user / 123456`
