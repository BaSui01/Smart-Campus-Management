package com.campus.shared.exception;

/**
 * 资源未找到异常类
 * 用于处理资源不存在的异常情况
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
public class ResourceNotFoundException extends RuntimeException {

    private String resourceType;
    private Object resourceId;

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceType, Object resourceId) {
        super(String.format("%s not found with id: %s", resourceType, resourceId));
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceNotFoundException(String resourceType, Object resourceId, Throwable cause) {
        super(String.format("%s not found with id: %s", resourceType, resourceId), cause);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Object getResourceId() {
        return resourceId;
    }

    public void setResourceId(Object resourceId) {
        this.resourceId = resourceId;
    }
}