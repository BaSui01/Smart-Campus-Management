#!/bin/bash

# 修复Swagger注解警告的脚本
# 将过时的 required = true 替换为 requiredMode = Schema.RequiredMode.REQUIRED

echo "开始修复Swagger注解警告..."

# 查找所有包含过时注解的Java文件
find src/main/java -name "*.java" -type f | while read file; do
    if grep -q "required = true" "$file"; then
        echo "修复文件: $file"
        
        # 替换 required = true 为 requiredMode = Schema.RequiredMode.REQUIRED
        sed -i 's/required = true/requiredMode = Schema.RequiredMode.REQUIRED/g' "$file"
        
        # 确保导入了Schema类
        if ! grep -q "import io.swagger.v3.oas.annotations.media.Schema;" "$file"; then
            # 在package声明后添加import
            sed -i '/^package /a\\nimport io.swagger.v3.oas.annotations.media.Schema;' "$file"
        fi
    fi
done

echo "Swagger注解警告修复完成！"
