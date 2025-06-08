-- =====================================================
-- 修复admin用户权限脚本
-- =====================================================
-- 文件名: fix_admin_permissions.sql
-- 描述: 确保admin用户具有正确的SUPER_ADMIN角色和权限
-- 版本: 1.0.0
-- =====================================================

USE campus_management_db;

-- 设置字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 开始事务
START TRANSACTION;

-- 1. 检查并创建SUPER_ADMIN角色（如果不存在）
INSERT IGNORE INTO tb_role (role_name, role_key, description, role_level, is_system, status, deleted, created_at, updated_at)
VALUES ('超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限', 1, 1, 1, 0, NOW(), NOW());

-- 2. 获取admin用户ID和SUPER_ADMIN角色ID
SET @admin_user_id = (SELECT id FROM tb_user WHERE username = 'admin' LIMIT 1);
SET @super_admin_role_id = (SELECT id FROM tb_role WHERE role_key = 'SUPER_ADMIN' LIMIT 1);

-- 3. 检查admin用户是否存在
SELECT 
    CASE 
        WHEN @admin_user_id IS NOT NULL THEN 'admin用户存在'
        ELSE 'admin用户不存在'
    END AS admin_user_status,
    @admin_user_id AS admin_user_id;

-- 4. 检查SUPER_ADMIN角色是否存在
SELECT 
    CASE 
        WHEN @super_admin_role_id IS NOT NULL THEN 'SUPER_ADMIN角色存在'
        ELSE 'SUPER_ADMIN角色不存在'
    END AS super_admin_role_status,
    @super_admin_role_id AS super_admin_role_id;

-- 5. 为admin用户分配SUPER_ADMIN角色（如果还没有）
INSERT IGNORE INTO tb_user_role (user_id, role_id, created_at, updated_at)
SELECT @admin_user_id, @super_admin_role_id, NOW(), NOW()
WHERE @admin_user_id IS NOT NULL AND @super_admin_role_id IS NOT NULL;

-- 6. 检查分配结果
SELECT 
    u.id AS user_id,
    u.username,
    u.real_name,
    r.id AS role_id,
    r.role_name,
    r.role_key,
    ur.created_at AS role_assigned_at
FROM tb_user u
JOIN tb_user_role ur ON u.id = ur.user_id
JOIN tb_role r ON ur.role_id = r.id
WHERE u.username = 'admin'
ORDER BY ur.created_at;

-- 7. 验证admin用户的所有角色
SELECT 
    '=== admin用户角色验证 ===' AS verification_title;

SELECT 
    u.username AS 用户名,
    u.real_name AS 真实姓名,
    u.status AS 用户状态,
    GROUP_CONCAT(r.role_name ORDER BY r.role_level) AS 拥有角色,
    GROUP_CONCAT(r.role_key ORDER BY r.role_level) AS 角色键值
FROM tb_user u
LEFT JOIN tb_user_role ur ON u.id = ur.user_id
LEFT JOIN tb_role r ON ur.role_id = r.id
WHERE u.username = 'admin'
GROUP BY u.id, u.username, u.real_name, u.status;

-- 8. 检查权限配置
SELECT 
    '=== SUPER_ADMIN角色权限检查 ===' AS permission_check_title;

SELECT 
    r.role_name AS 角色名称,
    r.role_key AS 角色键值,
    COUNT(rp.permission_id) AS 权限数量,
    r.description AS 角色描述
FROM tb_role r
LEFT JOIN tb_role_permission rp ON r.id = rp.role_id
WHERE r.role_key = 'SUPER_ADMIN'
GROUP BY r.id, r.role_name, r.role_key, r.description;

-- 9. 如果SUPER_ADMIN角色没有权限，为其分配所有权限
INSERT IGNORE INTO tb_role_permission (role_id, permission_id, created_at, updated_at)
SELECT @super_admin_role_id, p.id, NOW(), NOW()
FROM tb_permission p
WHERE @super_admin_role_id IS NOT NULL
AND NOT EXISTS (
    SELECT 1 FROM tb_role_permission rp 
    WHERE rp.role_id = @super_admin_role_id AND rp.permission_id = p.id
);

-- 10. 最终验证
SELECT 
    '=== 修复完成验证 ===' AS final_verification_title;

-- 检查admin用户是否有SUPER_ADMIN角色
SELECT 
    CASE 
        WHEN EXISTS (
            SELECT 1 FROM tb_user u
            JOIN tb_user_role ur ON u.id = ur.user_id
            JOIN tb_role r ON ur.role_id = r.id
            WHERE u.username = 'admin' AND r.role_key = 'SUPER_ADMIN'
        ) THEN '✓ admin用户已具有SUPER_ADMIN角色'
        ELSE '✗ admin用户缺少SUPER_ADMIN角色'
    END AS admin_super_admin_status;

-- 检查SUPER_ADMIN角色权限数量
SELECT 
    CONCAT('SUPER_ADMIN角色拥有 ', COUNT(rp.permission_id), ' 个权限') AS super_admin_permissions_count
FROM tb_role r
LEFT JOIN tb_role_permission rp ON r.id = rp.role_id
WHERE r.role_key = 'SUPER_ADMIN';

-- 提交事务
COMMIT;

-- 显示修复建议
SELECT 
    '=== 修复建议 ===' AS suggestions_title;

SELECT 
    CASE 
        WHEN @admin_user_id IS NULL THEN '1. 需要创建admin用户'
        WHEN @super_admin_role_id IS NULL THEN '2. 需要创建SUPER_ADMIN角色'
        WHEN NOT EXISTS (
            SELECT 1 FROM tb_user_role ur 
            WHERE ur.user_id = @admin_user_id AND ur.role_id = @super_admin_role_id
        ) THEN '3. 需要为admin用户分配SUPER_ADMIN角色'
        ELSE '✓ admin用户权限配置正常'
    END AS suggestion;

-- 显示登录测试信息
SELECT 
    '=== 登录测试信息 ===' AS login_test_title;

SELECT 
    'admin' AS 用户名,
    '123456' AS 密码,
    '请使用以上信息登录后台管理系统进行测试' AS 说明;
