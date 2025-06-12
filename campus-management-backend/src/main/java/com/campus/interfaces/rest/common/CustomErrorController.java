package com.campus.interfaces.rest.common;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 错误页面控制器
 * 处理404、500等错误页面
 *
 * @author campus
 * @since 2025-06-06
 */
@Controller
public class CustomErrorController implements ErrorController {

    private static final String ERROR_PATH = "/error";

    @RequestMapping(ERROR_PATH)
    public String handleError(HttpServletRequest request, Model model) {
        // 获取错误状态码
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            
            switch (statusCode) {
                case 404:
                    model.addAttribute("errorCode", "404");
                    model.addAttribute("errorMessage", "页面未找到");
                    model.addAttribute("errorDescription", "您访问的页面不存在，请检查URL是否正确。");
                    break;
                case 403:
                    model.addAttribute("errorCode", "403");
                    model.addAttribute("errorMessage", "访问被拒绝");
                    model.addAttribute("errorDescription", "您没有权限访问此页面。");
                    break;
                case 500:
                    model.addAttribute("errorCode", "500");
                    model.addAttribute("errorMessage", "服务器内部错误");
                    model.addAttribute("errorDescription", "服务器遇到了一个错误，请稍后重试。");
                    break;
                default:
                    model.addAttribute("errorCode", String.valueOf(statusCode));
                    model.addAttribute("errorMessage", "发生了错误");
                    model.addAttribute("errorDescription", "系统遇到了一个未知错误。");
                    break;
            }
        } else {
            model.addAttribute("errorCode", "未知");
            model.addAttribute("errorMessage", "发生了错误");
            model.addAttribute("errorDescription", "系统遇到了一个未知错误。");
        }

        // 获取请求的URL
        String requestUrl = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        if (requestUrl != null) {
            model.addAttribute("requestUrl", requestUrl);
        }

        return "error";
    }

    public String getErrorPath() {
        return ERROR_PATH;
    }
}
