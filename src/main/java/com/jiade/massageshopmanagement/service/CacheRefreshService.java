package com.jiade.massageshopmanagement.service;

import com.jiade.massageshopmanagement.config.CacheConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 定时强制刷新各类统计数据缓存的服务。
 * 建议 @EnableScheduling 配置在主程序类或配置类上。
 */
@Service
public class CacheRefreshService {

    @Autowired
    private StatsService statsService;

    @Autowired
    private CacheConfig cacheConfig;

    // 可根据业务实际调整，常用 period
    private static final List<String> PERIODS = Arrays.asList("week", "month", "year", "all");

    /**
     * 定时刷新收入趋势缓存
     */
    @Scheduled(fixedRateString = "#{${cache.refresh-interval-minutes:5} * 60 * 1000}")
    public void refreshIncomeTrendCache() {
        for (String period : PERIODS) {
            statsService.refreshIncomeTrend(period);
        }
    }

    /**
     * 定时刷新净收入趋势缓存
     */
    @Scheduled(fixedRateString = "#{${cache.refresh-interval-minutes:5} * 60 * 1000}")
    public void refreshNetIncomeTrendCache() {
        for (String period : PERIODS) {
            statsService.refreshNetIncomeTrend(period);
        }
    }

    /**
     * 定时刷新员工收益趋势缓存
     */
    @Scheduled(fixedRateString = "#{${cache.refresh-interval-minutes:5} * 60 * 1000}")
    public void refreshStaffIncomeTrendCache() {
        for (String period : PERIODS) {
            statsService.refreshStaffIncomeTrend(period);
        }
    }

    /**
     * 定时刷新消费占比缓存
     */
    @Scheduled(fixedRateString = "#{${cache.refresh-interval-minutes:5} * 60 * 1000}")
    public void refreshConsumptionRatioCache() {
        for (String period : PERIODS) {
            statsService.refreshConsumptionRatio(period);
        }
    }

    /**
     * 定时刷新项目收益占比缓存
     */
    @Scheduled(fixedRateString = "#{${cache.refresh-interval-minutes:5} * 60 * 1000}")
    public void refreshProjectIncomeRatioCache() {
        for (String period : PERIODS) {
            statsService.refreshProjectIncomeRatio(period);
        }
    }

    /**
     * 定时刷新统计汇总缓存
     */
    @Scheduled(fixedRateString = "#{${cache.refresh-interval-minutes:5} * 60 * 1000}")
    public void refreshSummaryCache() {
        for (String period : PERIODS) {
            statsService.refreshSummary(period);
        }
    }

    /**
     * 定时刷新所有会员本月消费缓存
     * 如需批量刷新所有会员，可以获取会员id列表后循环刷新。
     * 这里仅为演示，实际建议放到会员Service中批量获取所有会员id。
     */
//    @Scheduled(fixedRateString = "#{${cache.refresh-interval-minutes:30} * 60 * 1000}")
//    public void refreshAllMemberConsumptionCache() {
//        List<Long> memberIds = ... // 从数据库或缓存中查出所有会员id
//        for (Long memberId : memberIds) {
//            statsService.refreshMemberConsumption(memberId);
//        }
//    }

    /**
     * 定时刷新所有员工本周服务缓存
     * 如需批量刷新所有员工，可以获取员工id列表后循环刷新。
     * 这里仅为演示，实际建议放到员工Service中批量获取所有员工id。
     */
//    @Scheduled(fixedRateString = "#{${cache.refresh-interval-minutes:30} * 60 * 1000}")
//    public void refreshAllStaffServiceCache() {
//        List<Long> staffIds = ... // 从数据库或缓存中查出所有员工id
//        for (Long staffId : staffIds) {
//            statsService.refreshStaffService(staffId);
//        }
//    }

    /**
     * 定时刷新员工薪资统计缓存
     */
    @Scheduled(fixedRateString = "#{${cache.refresh-interval-minutes:5} * 60 * 1000}")
    public void refreshStaffSalariesCache() {
        statsService.refreshStaffSalaries();
    }
}