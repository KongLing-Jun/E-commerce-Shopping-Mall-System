-- Migration: user center extensions + role assignment permissions
-- Date: 2026-01-27

-- 1) Favorites table
CREATE TABLE IF NOT EXISTS user_favorite
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT      NOT NULL,
    product_id BIGINT      NOT NULL,
    created_at DATETIME    NULL,
    UNIQUE KEY uk_user_favorite (user_id, product_id),
    INDEX idx_user_favorite_user_id (user_id),
    INDEX idx_user_favorite_product_id (product_id)
) CHARSET = utf8mb4;

-- 2) Footprints table
CREATE TABLE IF NOT EXISTS user_footprint
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT      NOT NULL,
    product_id BIGINT      NOT NULL,
    viewed_at  DATETIME    NULL,
    UNIQUE KEY uk_user_footprint (user_id, product_id),
    INDEX idx_user_footprint_user_id (user_id),
    INDEX idx_user_footprint_viewed_at (viewed_at)
) CHARSET = utf8mb4;

-- 3) Backfill missing user roles to USER
UPDATE `user`
SET role_id = (SELECT id FROM role WHERE role_key = 'USER' LIMIT 1)
WHERE role_id IS NULL;

-- 4) Ensure role management menus (ADMIN)
INSERT INTO menu (parent_id, name, path, component, type, perm_code, sort, visible)
SELECT 0, 'Role Management', '/admin/roles', 'AdminRoles', 'MENU', 'admin:roles:list', 2, 1
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE perm_code = 'admin:roles:list');

INSERT INTO menu (parent_id, name, path, component, type, perm_code, sort, visible)
SELECT 0, 'Manage Role', NULL, NULL, 'BUTTON', 'admin:roles:edit', 0, 0
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE perm_code = 'admin:roles:edit');

INSERT INTO menu (parent_id, name, path, component, type, perm_code, sort, visible)
SELECT 0, 'Delete Role', NULL, NULL, 'BUTTON', 'admin:roles:delete', 0, 0
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE perm_code = 'admin:roles:delete');

-- 5) Ensure user role assignment permission (ADMIN)
INSERT INTO menu (parent_id, name, path, component, type, perm_code, sort, visible)
SELECT 0, 'Assign Role', NULL, NULL, 'BUTTON', 'admin:users:role', 0, 0
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE perm_code = 'admin:users:role');

-- 6) Bind new menus to ADMIN role
INSERT INTO role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM role r
JOIN menu m ON m.perm_code = 'admin:roles:list'
WHERE r.role_key = 'ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM role_menu rm WHERE rm.role_id = r.id AND rm.menu_id = m.id
  );

INSERT INTO role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM role r
JOIN menu m ON m.perm_code = 'admin:roles:edit'
WHERE r.role_key = 'ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM role_menu rm WHERE rm.role_id = r.id AND rm.menu_id = m.id
  );

INSERT INTO role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM role r
JOIN menu m ON m.perm_code = 'admin:roles:delete'
WHERE r.role_key = 'ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM role_menu rm WHERE rm.role_id = r.id AND rm.menu_id = m.id
  );

INSERT INTO role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM role r
JOIN menu m ON m.perm_code = 'admin:users:role'
WHERE r.role_key = 'ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM role_menu rm WHERE rm.role_id = r.id AND rm.menu_id = m.id
  );
