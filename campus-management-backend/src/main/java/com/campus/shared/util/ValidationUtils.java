package com.campus.shared.util;

import com.campus.shared.exception.BusinessException;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据验证工具类
 * 提供统一的数据验证方法，确保数据的完整性和安全性
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-18
 */
public class ValidationUtils {

    // 正则表达式常量
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^1[3-9]\\d{9}$"
    );
    
    private static final Pattern ID_CARD_PATTERN = Pattern.compile(
        "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$"
    );
    
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_]{4,20}$"
    );
    
    private static final Pattern STUDENT_NO_PATTERN = Pattern.compile(
        "^\\d{8,12}$"
    );

    /**
     * 验证字符串不为空
     */
    public static void validateNotEmpty(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(400, fieldName + "不能为空");
        }
    }

    /**
     * 验证字符串长度
     */
    public static void validateLength(String value, String fieldName, int minLength, int maxLength) {
        if (value == null) {
            throw new BusinessException(400, fieldName + "不能为空");
        }
        
        int length = value.trim().length();
        if (length < minLength || length > maxLength) {
            throw new BusinessException(400, 
                String.format("%s长度必须在%d-%d个字符之间", fieldName, minLength, maxLength));
        }
    }

    /**
     * 验证ID不为空且大于0
     */
    public static void validateId(Long id, String entityName) {
        if (id == null || id <= 0) {
            throw new BusinessException(400, entityName + "ID无效");
        }
    }

    /**
     * 验证邮箱格式
     */
    public static void validateEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new BusinessException(400, "邮箱不能为空");
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessException(400, "邮箱格式不正确");
        }
    }

    /**
     * 验证手机号格式
     */
    public static void validatePhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            throw new BusinessException(400, "手机号不能为空");
        }
        
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new BusinessException(400, "手机号格式不正确");
        }
    }

    /**
     * 验证身份证号格式
     */
    public static void validateIdCard(String idCard) {
        if (!StringUtils.hasText(idCard)) {
            throw new BusinessException(400, "身份证号不能为空");
        }
        
        if (!ID_CARD_PATTERN.matcher(idCard).matches()) {
            throw new BusinessException(400, "身份证号格式不正确");
        }
    }

    /**
     * 验证用户名格式
     */
    public static void validateUsername(String username) {
        if (!StringUtils.hasText(username)) {
            throw new BusinessException(400, "用户名不能为空");
        }
        
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new BusinessException(400, "用户名只能包含字母、数字和下划线，长度4-20位");
        }
    }

    /**
     * 验证学号格式
     */
    public static void validateStudentNo(String studentNo) {
        if (!StringUtils.hasText(studentNo)) {
            throw new BusinessException(400, "学号不能为空");
        }
        
        if (!STUDENT_NO_PATTERN.matcher(studentNo).matches()) {
            throw new BusinessException(400, "学号格式不正确，应为8-12位数字");
        }
    }

    /**
     * 验证密码强度
     */
    public static void validatePassword(String password, boolean requireStrong) {
        if (!StringUtils.hasText(password)) {
            throw new BusinessException(400, "密码不能为空");
        }
        
        if (password.length() < 6) {
            throw new BusinessException(400, "密码长度不能少于6位");
        }
        
        if (password.length() > 50) {
            throw new BusinessException(400, "密码长度不能超过50位");
        }
        
        if (requireStrong) {
            // 强密码要求：至少包含大小写字母、数字和特殊字符中的三种
            int typeCount = 0;
            if (password.matches(".*[a-z].*")) typeCount++;
            if (password.matches(".*[A-Z].*")) typeCount++;
            if (password.matches(".*\\d.*")) typeCount++;
            if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) typeCount++;
            
            if (typeCount < 3) {
                throw new BusinessException(400, "密码强度不够，至少包含大小写字母、数字和特殊字符中的三种");
            }
        }
    }

    /**
     * 验证分页参数
     */
    public static void validatePageParams(int page, int size) {
        if (page < 1) {
            throw new BusinessException(400, "页码必须大于0");
        }
        
        if (size < 1 || size > 100) {
            throw new BusinessException(400, "每页大小必须在1-100之间");
        }
    }

    /**
     * 验证分数范围
     */
    public static void validateScore(Double score, String fieldName) {
        if (score == null) {
            throw new BusinessException(400, fieldName + "不能为空");
        }
        
        if (score < 0 || score > 100) {
            throw new BusinessException(400, fieldName + "必须在0-100之间");
        }
    }

    /**
     * 验证日期范围
     */
    public static void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new BusinessException(400, "开始日期和结束日期不能为空");
        }
        
        if (startDate.isAfter(endDate)) {
            throw new BusinessException(400, "开始日期不能晚于结束日期");
        }
    }

    /**
     * 验证时间范围
     */
    public static void validateDateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new BusinessException(400, "开始时间和结束时间不能为空");
        }
        
        if (startTime.isAfter(endTime)) {
            throw new BusinessException(400, "开始时间不能晚于结束时间");
        }
    }

    /**
     * 验证列表不为空
     */
    public static void validateListNotEmpty(List<?> list, String fieldName) {
        if (list == null || list.isEmpty()) {
            throw new BusinessException(400, fieldName + "不能为空");
        }
    }

    /**
     * 验证列表大小
     */
    public static void validateListSize(List<?> list, String fieldName, int maxSize) {
        if (list == null) {
            throw new BusinessException(400, fieldName + "不能为空");
        }
        
        if (list.size() > maxSize) {
            throw new BusinessException(400, fieldName + "数量不能超过" + maxSize + "个");
        }
    }

    /**
     * 验证枚举值
     */
    public static void validateEnum(String value, String[] allowedValues, String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(400, fieldName + "不能为空");
        }
        
        for (String allowed : allowedValues) {
            if (allowed.equals(value)) {
                return;
            }
        }
        
        throw new BusinessException(400, 
            fieldName + "值无效，允许的值为: " + String.join(", ", allowedValues));
    }

    /**
     * 验证文件类型
     */
    public static void validateFileType(String fileName, String[] allowedExtensions) {
        if (!StringUtils.hasText(fileName)) {
            throw new BusinessException(400, "文件名不能为空");
        }
        
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1) {
            throw new BusinessException(400, "文件必须有扩展名");
        }
        
        String extension = fileName.substring(dotIndex + 1).toLowerCase();
        for (String allowed : allowedExtensions) {
            if (allowed.toLowerCase().equals(extension)) {
                return;
            }
        }
        
        throw new BusinessException(400, 
            "不支持的文件类型，允许的类型为: " + String.join(", ", allowedExtensions));
    }

    /**
     * 验证文件大小
     */
    public static void validateFileSize(long fileSize, long maxSize) {
        if (fileSize <= 0) {
            throw new BusinessException(400, "文件不能为空");
        }
        
        if (fileSize > maxSize) {
            throw new BusinessException(400, 
                String.format("文件大小不能超过%.1fMB", maxSize / 1024.0 / 1024.0));
        }
    }

    /**
     * 清理和验证搜索关键词
     */
    public static String sanitizeSearchKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return "";
        }
        
        // 移除特殊字符，防止SQL注入
        String cleaned = keyword.replaceAll("[<>\"'%;()&+]", "");
        
        // 限制长度
        if (cleaned.length() > 100) {
            cleaned = cleaned.substring(0, 100);
        }
        
        return cleaned.trim();
    }

    /**
     * 验证排序字段
     */
    public static void validateSortField(String sortField, String[] allowedFields) {
        if (!StringUtils.hasText(sortField)) {
            return; // 允许为空，使用默认排序
        }
        
        for (String allowed : allowedFields) {
            if (allowed.equals(sortField)) {
                return;
            }
        }
        
        throw new BusinessException(400, 
            "不支持的排序字段，允许的字段为: " + String.join(", ", allowedFields));
    }

    /**
     * 验证排序方向
     */
    public static void validateSortDirection(String sortDir) {
        if (!StringUtils.hasText(sortDir)) {
            return; // 允许为空，使用默认方向
        }
        
        if (!"asc".equalsIgnoreCase(sortDir) && !"desc".equalsIgnoreCase(sortDir)) {
            throw new BusinessException(400, "排序方向只能是asc或desc");
        }
    }
}
