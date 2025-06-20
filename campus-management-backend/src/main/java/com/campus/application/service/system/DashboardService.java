package com.campus.application.service.system;

import com.campus.interfaces.rest.dto.DashboardStatsDTO;

/**
 * 仪表盘服务接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
public interface DashboardService {

    /**
     * 获取仪表盘统计数据
     *
     * @return 仪表盘统计数据
     */
    DashboardStatsDTO getDashboardStats();

    /**
     * 获取实时统计数据
     *
     * @return 实时统计数据
     */
    DashboardStatsDTO getRealTimeStats();
}
