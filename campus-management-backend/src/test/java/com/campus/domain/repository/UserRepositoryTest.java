package com.campus.domain.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.campus.domain.entity.auth.User;
import com.campus.domain.repository.auth.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserRepository单元测试
 * 测试用户Repository的所有查询方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;
    private User testUser3;

    @BeforeEach
    void setUp() {
        // 创建测试用户数据
        testUser1 = new User();
        testUser1.setUsername("testuser1");
        testUser1.setPassword("password123");
        testUser1.setRealName("测试用户1");
        testUser1.setEmail("test1@example.com");
        testUser1.setPhone("13800138001");
        testUser1.setStatus(1);
        testUser1.setDeleted(0);
        testUser1.setCreatedAt(LocalDateTime.now());
        testUser1.setUpdatedAt(LocalDateTime.now());

        testUser2 = new User();
        testUser2.setUsername("testuser2");
        testUser2.setPassword("password123");
        testUser2.setRealName("测试用户2");
        testUser2.setEmail("test2@example.com");
        testUser2.setPhone("13800138002");
        testUser2.setStatus(0);
        testUser2.setDeleted(0);
        testUser2.setCreatedAt(LocalDateTime.now());
        testUser2.setUpdatedAt(LocalDateTime.now());

        testUser3 = new User();
        testUser3.setUsername("testuser3");
        testUser3.setPassword("password123");
        testUser3.setRealName("测试用户3");
        testUser3.setEmail("test3@example.com");
        testUser3.setPhone("13800138003");
        testUser3.setStatus(1);
        testUser3.setDeleted(1);
        testUser3.setCreatedAt(LocalDateTime.now());
        testUser3.setUpdatedAt(LocalDateTime.now());

        // 保存测试数据
        entityManager.persistAndFlush(testUser1);
        entityManager.persistAndFlush(testUser2);
        entityManager.persistAndFlush(testUser3);
    }

    @Test
    void testFindByUsername() {
        // 测试根据用户名查找用户
        Optional<User> result = userRepository.findByUsername("testuser1");
        
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser1");
        assertThat(result.get().getRealName()).isEqualTo("测试用户1");
    }

    @Test
    void testFindByUsernameAndStatus() {
        // 测试根据用户名和状态查找用户
        Optional<User> result1 = userRepository.findByUsernameAndStatus("testuser1", 1);
        Optional<User> result2 = userRepository.findByUsernameAndStatus("testuser2", 1);
        Optional<User> result3 = userRepository.findByUsernameAndStatus("testuser1", 0);
        
        assertThat(result1).isPresent();
        assertThat(result1.get().getUsername()).isEqualTo("testuser1");
        assertThat(result1.get().getStatus()).isEqualTo(1);
        
        assertThat(result2).isEmpty(); // testuser2的状态是0，不是1
        assertThat(result3).isEmpty(); // testuser1的状态是1，不是0
    }

    @Test
    void testFindByEmail() {
        // 测试根据邮箱查找用户
        Optional<User> result = userRepository.findByEmail("test1@example.com");
        
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test1@example.com");
        assertThat(result.get().getUsername()).isEqualTo("testuser1");
    }

    @Test
    void testFindByUsernameOrEmail() {
        // 测试根据用户名或邮箱查找用户
        Optional<User> result1 = userRepository.findByUsernameOrEmail("testuser1");
        Optional<User> result2 = userRepository.findByUsernameOrEmail("test2@example.com");
        
        assertThat(result1).isPresent();
        assertThat(result1.get().getUsername()).isEqualTo("testuser1");
        
        assertThat(result2).isPresent();
        assertThat(result2.get().getEmail()).isEqualTo("test2@example.com");
    }

    @Test
    void testFindByRealNameContaining() {
        // 测试根据真实姓名模糊查找用户
        List<User> result = userRepository.findByRealNameContaining("测试");
        
        assertThat(result).hasSize(2); // testuser1和testuser2，testuser3被软删除了
        assertThat(result).extracting(User::getRealName)
                .containsExactlyInAnyOrder("测试用户1", "测试用户2");
    }

    @Test
    void testFindByStatus() {
        // 测试根据状态查找用户
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> result = userRepository.findByStatus(1, pageable);
        
        assertThat(result.getContent()).hasSize(1); // 只有testuser1状态为1且未删除
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("testuser1");
    }

    @Test
    void testCountByStatus() {
        // 测试根据状态统计用户数量
        long count1 = userRepository.countByStatus(1);
        long count0 = userRepository.countByStatus(0);
        
        assertThat(count1).isEqualTo(1); // testuser1
        assertThat(count0).isEqualTo(1); // testuser2
    }

    @Test
    void testSearchUsers() {
        // 测试搜索用户
        List<User> result1 = userRepository.searchUsers("testuser");
        List<User> result2 = userRepository.searchUsers("测试");
        List<User> result3 = userRepository.searchUsers("example.com");
        
        assertThat(result1).hasSize(2); // testuser1和testuser2
        assertThat(result2).hasSize(2); // 测试用户1和测试用户2
        assertThat(result3).hasSize(2); // 邮箱包含example.com的用户
    }

    @Test
    void testExistsByUsername() {
        // 测试检查用户名是否存在
        boolean exists1 = userRepository.existsByUsername("testuser1");
        boolean exists2 = userRepository.existsByUsername("nonexistent");
        
        assertThat(exists1).isTrue();
        assertThat(exists2).isFalse();
    }

    @Test
    void testExistsByEmail() {
        // 测试检查邮箱是否存在
        boolean exists1 = userRepository.existsByEmail("test1@example.com");
        boolean exists2 = userRepository.existsByEmail("nonexistent@example.com");
        
        assertThat(exists1).isTrue();
        assertThat(exists2).isFalse();
    }

    @Test
    void testFindActiveUsers() {
        // 测试查找活跃用户
        List<User> result = userRepository.findByStatus(1);

        assertThat(result).hasSize(1); // 只有testuser1是活跃的（状态为1且未删除）
        assertThat(result.get(0).getUsername()).isEqualTo("testuser1");
    }

    @Test
    void testFindRecentUsers() {
        // 测试查找最近创建的用户
        Pageable pageable = PageRequest.of(0, 5);
        Page<User> result = userRepository.findAll(pageable);

        assertThat(result.getContent()).hasSize(2); // testuser1和testuser2（testuser3被软删除）
        // 应该按创建时间倒序排列
    }

    @Test
    void testSoftDelete() {
        // 测试软删除功能
        // 手动设置状态为-1（软删除）
        testUser1.setStatus(-1);
        userRepository.save(testUser1);
        entityManager.flush();
        entityManager.clear();

        // 验证用户被软删除
        Optional<User> result = userRepository.findByUsername("testuser1");
        assertThat(result).isEmpty(); // 软删除后查询不到
    }

    @Test
    void testBatchOperations() {
        // 测试批量操作
        List<User> users = List.of(testUser1, testUser2);

        // 批量保存
        userRepository.saveAll(users);
        entityManager.flush();
        entityManager.clear();

        // 验证批量保存成功
        List<User> result = userRepository.findByStatus(1);
        assertThat(result).hasSize(2);
    }

    @Test
    void testUpdateLastLoginInfo() {
        // 测试更新最后登录信息
        Long userId = testUser1.getId();
        LocalDateTime loginTime = LocalDateTime.now();
        String loginIp = "192.168.1.100";
        
        int updated = userRepository.updateLastLoginInfo(userId, loginTime, loginIp);
        entityManager.flush();
        entityManager.clear();
        
        assertThat(updated).isEqualTo(1);
        
        // 验证登录信息已更新
        Optional<User> result = userRepository.findById(userId);
        assertThat(result).isPresent();
        assertThat(result.get().getLastLoginIp()).isEqualTo(loginIp);
    }
}
