package com.campus.domain.repository;

import com.campus.domain.entity.User;
import com.campus.domain.entity.Student;
import com.campus.domain.entity.Department;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Repository层集成测试
 * 
 * @author Campus Management Team
 * @since 2025-06-10
 */
@DataJpaTest
@ActiveProfiles("test")
class RepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    // ================================
    // UserRepository 测试
    // ================================

    @Test
    void shouldSaveAndFindUser() {
        // Given
        User user = createTestUser("testuser", "test@example.com");

        // When
        User savedUser = userRepository.save(user);
        entityManager.flush();
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }

    @Test
    void shouldFindUserByUsername() {
        // Given
        User user = createTestUser("finduser", "find@example.com");
        userRepository.save(user);
        entityManager.flush();

        // When
        Optional<User> foundUser = userRepository.findByUsername("finduser");

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("finduser", foundUser.get().getUsername());
    }

    @Test
    void shouldCheckUserExistsByUsername() {
        // Given
        User user = createTestUser("existsuser", "exists@example.com");
        userRepository.save(user);
        entityManager.flush();

        // When
        boolean exists = userRepository.existsByUsername("existsuser");
        boolean notExists = userRepository.existsByUsername("nonexistent");

        // Then
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void shouldCountUsers() {
        // Given
        userRepository.save(createTestUser("user1", "user1@example.com"));
        userRepository.save(createTestUser("user2", "user2@example.com"));
        entityManager.flush();

        // When
        long count = userRepository.count();

        // Then
        assertEquals(2, count);
    }

    // ================================
    // DepartmentRepository 测试
    // ================================

    @Test
    void shouldSaveAndFindDepartment() {
        // Given
        Department department = createTestDepartment("CS", "计算机学院");

        // When
        Department savedDept = departmentRepository.save(department);
        entityManager.flush();
        Optional<Department> foundDept = departmentRepository.findById(savedDept.getId());

        // Then
        assertTrue(foundDept.isPresent());
        assertEquals("CS", foundDept.get().getDeptCode());
        assertEquals("计算机学院", foundDept.get().getDeptName());
    }

    @Test
    void shouldFindDepartmentByCode() {
        // Given
        Department department = createTestDepartment("EE", "电子工程学院");
        departmentRepository.save(department);
        entityManager.flush();

        // When
        Optional<Department> foundDept = departmentRepository.findByDeptCodeAndDeleted("EE", 0);

        // Then
        assertTrue(foundDept.isPresent());
        assertEquals("EE", foundDept.get().getDeptCode());
    }

    @Test
    void shouldFindActiveDepartments() {
        // Given
        Department activeDept = createTestDepartment("ACTIVE", "活跃院系");
        activeDept.setStatus(1);
        
        Department inactiveDept = createTestDepartment("INACTIVE", "非活跃院系");
        inactiveDept.setStatus(0);
        
        departmentRepository.save(activeDept);
        departmentRepository.save(inactiveDept);
        entityManager.flush();

        // When
        List<Department> activeDepartments = departmentRepository.findActiveDepartments();

        // Then
        assertEquals(1, activeDepartments.size());
        assertEquals("ACTIVE", activeDepartments.get(0).getDeptCode());
    }

    // ================================
    // StudentRepository 测试
    // ================================

    @Test
    void shouldSaveAndFindStudent() {
        // Given
        User user = createTestUser("student1", "student1@example.com");
        User savedUser = userRepository.save(user);
        entityManager.flush();

        Student student = createTestStudent(savedUser.getId(), "STU001");

        // When
        Student savedStudent = studentRepository.save(student);
        entityManager.flush();
        Optional<Student> foundStudent = studentRepository.findById(savedStudent.getId());

        // Then
        assertTrue(foundStudent.isPresent());
        assertEquals("STU001", foundStudent.get().getStudentNo());
        assertEquals(savedUser.getId(), foundStudent.get().getUserId());
    }

    @Test
    void shouldFindStudentByStudentNo() {
        // Given
        User user = createTestUser("student2", "student2@example.com");
        User savedUser = userRepository.save(user);
        entityManager.flush();

        Student student = createTestStudent(savedUser.getId(), "STU002");
        studentRepository.save(student);
        entityManager.flush();

        // When
        Optional<Student> foundStudent = studentRepository.findByStudentNoAndDeleted("STU002", 0);

        // Then
        assertTrue(foundStudent.isPresent());
        assertEquals("STU002", foundStudent.get().getStudentNo());
    }

    @Test
    void shouldCheckStudentExistsByStudentNo() {
        // Given
        User user = createTestUser("student3", "student3@example.com");
        User savedUser = userRepository.save(user);
        entityManager.flush();

        Student student = createTestStudent(savedUser.getId(), "STU003");
        studentRepository.save(student);
        entityManager.flush();

        // When
        boolean exists = studentRepository.existsByStudentNoAndDeleted("STU003", 0);
        boolean notExists = studentRepository.existsByStudentNoAndDeleted("STU999", 0);

        // Then
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void shouldCountStudents() {
        // Given
        User user1 = userRepository.save(createTestUser("student4", "student4@example.com"));
        User user2 = userRepository.save(createTestUser("student5", "student5@example.com"));
        entityManager.flush();

        studentRepository.save(createTestStudent(user1.getId(), "STU004"));
        studentRepository.save(createTestStudent(user2.getId(), "STU005"));
        entityManager.flush();

        // When
        long count = studentRepository.count();

        // Then
        assertEquals(2, count);
    }

    // ================================
    // 辅助方法
    // ================================

    private User createTestUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setRealName("Test " + username);
        user.setPassword("password123");
        user.setStatus(1);
        user.setDeleted(0);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    private Student createTestStudent(Long userId, String studentNo) {
        Student student = new Student();
        student.setUserId(userId);
        student.setStudentNo(studentNo);
        student.setGrade("2024");
        student.setMajor("计算机科学与技术");
        student.setAcademicStatus(1);
        student.setStatus(1);
        student.setDeleted(0);
        student.setCreatedAt(LocalDateTime.now());
        student.setUpdatedAt(LocalDateTime.now());
        return student;
    }

    private Department createTestDepartment(String deptCode, String deptName) {
        Department department = new Department();
        department.setDeptCode(deptCode);
        department.setDeptName(deptName);
        department.setDescription("测试院系");
        department.setSortOrder(1);
        department.setStatus(1);
        department.setDeleted(0);
        department.setCreatedAt(LocalDateTime.now());
        department.setUpdatedAt(LocalDateTime.now());
        return department;
    }
}
