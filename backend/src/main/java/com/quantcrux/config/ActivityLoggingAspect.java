package com.quantcrux.config;

import com.quantcrux.model.UserActivityLog;
import com.quantcrux.service.UserActivityService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class ActivityLoggingAspect {

    @Autowired
    private UserActivityService userActivityService;

    @AfterReturning(pointcut = "execution(* com.quantcrux.controller.StrategyController.createStrategy(..))", returning = "result")
    public void logStrategyCreation(JoinPoint joinPoint, Object result) {
        logActivity(UserActivityLog.ActivityType.STRATEGY_CREATED, "Created new strategy");
    }

    @AfterReturning(pointcut = "execution(* com.quantcrux.controller.ProductController.createProduct(..))", returning = "result")
    public void logProductCreation(JoinPoint joinPoint, Object result) {
        logActivity(UserActivityLog.ActivityType.PRODUCT_CREATED, "Created new product");
    }

    @AfterReturning(pointcut = "execution(* com.quantcrux.controller.TradeController.bookTrade(..))", returning = "result")
    public void logTradeBooking(JoinPoint joinPoint, Object result) {
        logActivity(UserActivityLog.ActivityType.TRADE_BOOKED, "Booked new trade");
    }

    @AfterReturning(pointcut = "execution(* com.quantcrux.controller.BacktestController.runBacktest(..))", returning = "result")
    public void logBacktestStart(JoinPoint joinPoint, Object result) {
        logActivity(UserActivityLog.ActivityType.BACKTEST_STARTED, "Started backtest");
    }

    @AfterReturning(pointcut = "execution(* com.quantcrux.controller.ReportsController.generateReport(..))", returning = "result")
    public void logReportGeneration(JoinPoint joinPoint, Object result) {
        logActivity(UserActivityLog.ActivityType.REPORT_GENERATED, "Generated report");
    }

    private void logActivity(String activityType, String description) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();
                
                HttpServletRequest request = null;
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    request = attributes.getRequest();
                }
                
                userActivityService.logActivity(username, activityType, description, null, null, request);
            }
        } catch (Exception e) {
            // Log error but don't fail the main operation
            System.err.println("Failed to log activity: " + e.getMessage());
        }
    }
}