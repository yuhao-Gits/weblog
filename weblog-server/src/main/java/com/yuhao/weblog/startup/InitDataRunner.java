package com.yuhao.weblog.startup;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yuhao.weblog.common.enums.RoleType;
import com.yuhao.weblog.module.admin.mapper.StatisticsMapper;
import com.yuhao.weblog.module.admin.mapper.UserMapper;
import com.yuhao.weblog.module.admin.model.entity.Statistics;
import com.yuhao.weblog.module.admin.model.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 应用启动后初始化种子数据：默认账号 + 近 7 天访问统计
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InitDataRunner implements CommandLineRunner {

    private final UserMapper userMapper;
    private final StatisticsMapper statisticsMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initUsers();
        initStatistics();
    }

    private void initUsers() {
        long count = userMapper.selectCount(Wrappers.emptyWrapper());
        if (count > 0) {
            return;
        }
        List<User> users = new ArrayList<>();
        users.add(buildUser("admin", "123456", RoleType.ADMIN.getCode()));
        users.add(buildUser("test", "test", RoleType.GUEST.getCode()));
        userMapper.insert(users.get(0));
        userMapper.insert(users.get(1));
        log.info("已初始化默认账号：admin/123456（管理员）、test/test（游客，仅查询）");
    }

    private User buildUser(String username, String password, String role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        return user;
    }

    private void initStatistics() {
        long count = statisticsMapper.selectCount(Wrappers.emptyWrapper());
        if (count > 0) {
            return;
        }
        LocalDate today = LocalDate.now();
        long[] pvSeed = {320L, 410L, 280L, 500L, 460L, 620L, 540L};
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            Statistics s = new Statistics();
            s.setDate(date);
            s.setPv(pvSeed[6 - i]);
            s.setUv((long) (pvSeed[6 - i] * 0.6));
            s.setArticleCount(1L);
            statisticsMapper.insert(s);
        }
        log.info("已初始化近 7 天访问统计数据");
    }
}
