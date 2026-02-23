# Postman 验收脚本

## 1) 环境准备
- 启动后端服务，确保 `http://localhost:8080` 可访问。
- 准备测试账号（默认初始化）：
  - 管理员：`admin / 123456`
  - 普通用户：`user / 123456`

## 2) 集合说明
- `mall_permission_acceptance.postman_collection.json`：权限验收（401/403/可访问）。
- `mall_e2e.postman_collection.json`：主链路验收（浏览->加购->下单->支付->发货->确认收货）。

## 3) 一键执行
```powershell
cd postman
./run_e2e.ps1
```

可选参数：
```powershell
./run_e2e.ps1 -SkipPermission
./run_e2e.ps1 -EnvironmentFile mall_local.postman_environment.json -OutputDir reports
```

执行后会在 `postman/reports` 生成 JSON 报告。
