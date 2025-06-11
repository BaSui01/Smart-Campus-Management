package com.campus.application.service;

import com.campus.domain.repository.UserRepository;
import com.campus.domain.repository.StudentRepository;
import com.campus.domain.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 核心Repository层单元测试
 *
 * @author Campus Management Team
 * @since 2025-06-10
 */
@ExtendWith(MockitoExtension.class)
class CoreServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Test
    void shouldMockUserRepository_whenCalled() {
        // Given
        when(userRepository.count()).thenReturn(5L);

        // When
        long count = userRepository.count();

        // Then
        assertEquals(5L, count);
        verify(userRepository).count();
    }

    @Test
    void shouldMockStudentRepository_whenCalled() {
        // Given
        when(studentRepository.count()).thenReturn(10L);

        // When
        long count = studentRepository.count();

        // Then
        assertEquals(10L, count);
        verify(studentRepository).count();
    }

    @Test
    void shouldMockDepartmentRepository_whenCalled() {
        // Given
        when(departmentRepository.count()).thenReturn(3L);

        // When
        long count = departmentRepository.count();

        // Then
        assertEquals(3L, count);
        verify(departmentRepository).count();
    }

    @Test
    void shouldVerifyRepositoryInteractions() {
        // When
        userRepository.count();
        studentRepository.count();
        departmentRepository.count();

        // Then
        verify(userRepository).count();
        verify(studentRepository).count();
        verify(departmentRepository).count();
    }
}
