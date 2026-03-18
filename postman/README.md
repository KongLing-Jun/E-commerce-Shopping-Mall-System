# Postman 全面测试集合

本目录包含电商商城系统的完整 Postman 测试集合，支持正常场景、边界条件和错误处理的全面测试。

## 1) 环境准备

### 1.1 系统要求
- Node.js (建议 v14+)
- npm 或 yarn 包管理器
- PowerShell 5.0+ (Windows 系统)

### 1.2 后端服务准备
- 确保后端服务已启动，默认访问地址：`http://localhost:8080`
- 确认测试账号已配置（默认初始化）：
  - 管理员账号：`admin / 123456`
  - 普通用户账号：`user / 123456`

### 1.3 依赖安装
在 `postman` 目录下执行以下命令安装 Newman 及相关报告插件：

```bash
cd postman
npm install
```

或者使用 npx 直接运行（无需预先安装）。

## 2) 测试集合说明

| 文件名 | 描述 | 测试范围 |
|--------|------|----------|
| `mall_e2e.postman_collection.json` | 端到端主链路测试 | 浏览商品 → 加购 → 下单 → 支付 → 发货 → 确认收货 |
| `mall_permission_acceptance.postman_collection.json` | 权限验收测试 | 游客/普通用户/管理员的权限控制（401/403验证） |
| `mall_comprehensive.postman_collection.json` | 全面综合测试 | 包含正常场景、边界条件和错误处理的完整测试 |
| `mall_smoke.postman_collection.json` | 冒烟测试 | 快速验证核心功能可用性 |

## 3) 快速开始

### 3.1 方式一：使用 PowerShell 脚本（推荐）

#### 执行所有测试集合
```powershell
cd postman
./run_comprehensive_tests.ps1
```

#### 仅执行 E2E 测试
```powershell
./run_comprehensive_tests.ps1 -SkipPermission -SkipComprehensive
```

#### 仅执行权限测试
```powershell
./run_comprehensive_tests.ps1 -SkipE2E -SkipComprehensive
```

#### 仅执行全面综合测试
```powershell
./run_comprehensive_tests.ps1 -SkipE2E -SkipPermission
```

### 3.2 方式二：使用 npm 脚本

在 `postman` 目录下执行：

```bash
# 执行所有测试
npm test

# 仅执行 E2E 测试
npm run test:e2e

# 仅执行权限测试
npm run test:permission

# 仅执行全面综合测试
npm run test:comprehensive
```

## 4) 脚本参数详解

`run_comprehensive_tests.ps1` 支持以下参数：

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `-EnvironmentFile` | string | `mall_local.postman_environment.json` | 指定 Postman 环境文件 |
| `-OutputDir` | string | `reports` | 报告输出目录 |
| `-ReportFormats` | string[] | `@("html", "json", "cli")` | 报告格式，支持 html/json/cli |
| `-SkipE2E` | switch | false | 跳过 E2E 测试 |
| `-SkipPermission` | switch | false | 跳过权限测试 |
| `-SkipComprehensive` | switch | false | 跳过全面综合测试 |
| `-CollectionFilter` | string | "" | 按名称过滤执行的集合 |

### 参数使用示例

#### 指定报告格式
```powershell
# 仅生成 HTML 和 CLI 报告
./run_comprehensive_tests.ps1 -ReportFormats @("html", "cli")

# 仅生成 JSON 报告
./run_comprehensive_tests.ps1 -ReportFormats @("json")
```

#### 指定环境文件和输出目录
```powershell
./run_comprehensive_tests.ps1 `
  -EnvironmentFile "mall_prod.postman_environment.json" `
  -OutputDir "test-reports"
```

#### 过滤执行特定集合
```powershell
# 只执行名称包含 "comprehensive" 的集合
./run_comprehensive_tests.ps1 -CollectionFilter "comprehensive"
```

## 5) 测试报告

### 5.1 报告格式

执行完成后，报告将生成在指定的输出目录（默认为 `reports`）：

| 格式 | 文件名模式 | 特点 |
|------|-----------|------|
| HTML | `{集合名}_{时间戳}.html` | 美观的图形化报告，包含详细的执行数据 |
| JSON | `{集合名}_{时间戳}.json` | 结构化数据，便于程序解析 |
| CLI | 控制台输出 | 实时显示执行进度和结果 |

### 5.2 HTML 报告内容

使用 `newman-reporter-htmlextra` 生成的 HTML 报告包含：
- 执行摘要和总览
- 通过率统计
- 失败测试详情
- 请求/响应完整数据
- 响应时间分析图表
- 环境变量记录

### 5.3 查看报告

直接在浏览器中打开生成的 HTML 文件即可查看详细报告。

## 6) 测试集合详解

### 6.1 mall_comprehensive.postman_collection.json（全面综合测试）

该集合包含以下测试模块：

#### 00. 基础健康检查
- 健康检查正常
- 根路径检查

#### 01. 认证模块 - 正常场景
- 用户登录成功
- 管理员登录成功

#### 02. 认证模块 - 错误处理
- 用户不存在登录失败
- 密码错误登录失败
- 用户名为空登录失败
- 密码为空登录失败

#### 03. 权限控制 - 错误处理
- 游客访问受保护接口返回401
- 无效JWT访问返回401
- 普通用户访问管理员接口返回403

#### 04. 商品模块 - 正常场景
- 获取首页推荐商品
- 获取商品详情
- 获取商品列表

#### 05. 商品模块 - 边界条件
- 页面大小为1
- 页码很大（可能超出范围）

#### 06. 购物车模块 - 正常场景
- 添加商品到购物车
- 获取购物车列表

#### 07. 购物车模块 - 边界条件
- 添加数量为1的商品

#### 08. 地址模块 - 正常场景
- 创建收货地址
- 获取地址列表

#### 09. 订单模块 - 正常场景
- 获取订单预信息
- 创建订单
- 订单支付
- 获取订单列表

#### 10. 管理员模块 - 正常场景
- 获取用户列表
- 获取订单列表
- 获取菜单树
- 获取统计概览
- 订单发货
- 获取通知列表

#### 11. 用户确认收货
- 确认收货
- 验证订单最终状态

## 7) 环境变量配置

### 7.1 默认环境变量（mall_local.postman_environment.json）

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| `baseUrl` | `http://localhost:8080` | 后端 API 基础 URL |
| `userUsername` | `user` | 普通用户登录用户名 |
| `userPassword` | `123456` | 普通用户登录密码 |
| `adminUsername` | `admin` | 管理员登录用户名 |
| `adminPassword` | `123456` | 管理员登录密码 |
| `userToken` | (动态) | 普通用户登录后的 Token |
| `adminToken` | (动态) | 管理员登录后的 Token |
| `userId` | (动态) | 普通用户 ID |
| `adminId` | (动态) | 管理员 ID |
| `productId` | (动态) | 测试商品 ID |
| `addressId` | (动态) | 测试地址 ID |
| `orderNo` | (动态) | 测试订单号 |
| `orderPayAmount` | (动态) | 订单支付金额 |

### 7.2 自定义环境

可以复制 `mall_local.postman_environment.json` 并修改为自定义环境配置文件，例如 `mall_prod.postman_environment.json`，然后使用 `-EnvironmentFile` 参数指定。

## 8) 常见问题

### 8.1 依赖安装失败
确保已正确安装 Node.js 和 npm，尝试使用淘宝镜像：
```bash
npm install --registry=https://registry.npmmirror.com
```

### 8.2 权限问题（Windows）
如遇到 PowerShell 执行策略限制，使用以下命令临时允许脚本执行：
```powershell
Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process
```

### 8.3 测试失败
- 确认后端服务已启动且可访问
- 检查测试账号是否正确配置
- 查看测试报告中的失败详情定位问题

### 8.4 newman-reporter-htmlextra 相关问题
该报告插件可能需要额外配置，脚本已自动处理依赖安装，如遇问题可尝试手动安装：
```bash
npm install -g newman newman-reporter-htmlextra
```

## 9) 目录结构

```
postman/
├── mall_comprehensive.postman_collection.json  # 全面综合测试集合
├── mall_e2e.postman_collection.json            # E2E 主链路测试
├── mall_permission_acceptance.postman_collection.json  # 权限验收测试
├── mall_smoke.postman_collection.json          # 冒烟测试
├── mall_local.postman_environment.json         # 本地环境配置
├── run_comprehensive_tests.ps1                  # 增强版 PowerShell 执行脚本
├── run_e2e.ps1                                   # 原始 E2E 执行脚本
├── package.json                                  # npm 依赖配置
├── README.md                                     # 本文档
└── reports/                                      # 报告输出目录（自动创建）
    ├── comprehensive_20260318_143022.html
    ├── comprehensive_20260318_143022.json
    ├── e2e_20260318_143022.html
    ├── e2e_20260318_143022.json
    └── ...
```

## 10) 最佳实践

1. **定期运行全面测试**：在代码提交前或发布前运行所有测试
2. **使用版本控制**：将测试集合和环境配置纳入版本控制
3. **关注失败测试**：及时修复失败的测试用例
4. **维护测试数据**：定期更新测试账号和测试数据
5. **查看详细报告**：使用 HTML 报告深入分析失败原因
