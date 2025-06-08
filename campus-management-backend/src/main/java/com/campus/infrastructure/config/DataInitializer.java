// package com.campus.infrastructure.config;

// import com.campus.application.service.RoleService;
// import com.campus.application.service.FeeItemService;
// import com.campus.domain.entity.Role;
// import com.campus.domain.entity.FeeItem;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component;

// import java.math.BigDecimal;
// import java.time.LocalDate;


// /**
//  * 数据初始化器
//  * 系统启动时初始化基础数据
//  *
//  * @author Campus Management Team
//  * @version 1.0.0
//  * @since 2025-06-07
//  */
// @Component
// public class DataInitializer implements CommandLineRunner {

//     private final RoleService roleService;
//     private final FeeItemService feeItemService;

//     @Autowired
//     public DataInitializer(RoleService roleService, FeeItemService feeItemService) {
//         this.roleService = roleService;
//         this.feeItemService = feeItemService;
//     }

//     @Override
//     public void run(String... args) {
//         try {
//             System.out.println("🚀 开始初始化系统基础数据...");
            
//             initializeRoles();
//             initializeFeeItems();
            
//             System.out.println("✅ 系统基础数据初始化完成！");
//         } catch (Exception e) {
//             System.err.println("❌ 系统基础数据初始化失败: " + e.getMessage());
//             e.printStackTrace();
//         }
//     }

//     /**
//      * 初始化系统角色
//      */
//     private void initializeRoles() {
//         System.out.println("📋 正在初始化系统角色...");

//         // 检查并创建管理员角色
//         if (!roleService.existsByRoleKey("ADMIN")) {
//             Role adminRole = new Role();
//             adminRole.setRoleName("系统管理员");
//             adminRole.setRoleKey("ADMIN");
//             adminRole.setDescription("系统管理员，拥有全部权限");
//             adminRole.setStatus(1);
//             adminRole.setSortOrder(1);
//             roleService.createRole(adminRole);
//             System.out.println("  ✓ 创建管理员角色");
//         }

//         // 检查并创建教师角色
//         if (!roleService.existsByRoleKey("TEACHER")) {
//             Role teacherRole = new Role();
//             teacherRole.setRoleName("教师");
//             teacherRole.setRoleKey("TEACHER");
//             teacherRole.setDescription("教师角色，可以管理课程和学生");
//             teacherRole.setStatus(1);
//             teacherRole.setSortOrder(2);
//             roleService.createRole(teacherRole);
//             System.out.println("  ✓ 创建教师角色");
//         }

//         // 检查并创建学生角色
//         if (!roleService.existsByRoleKey("STUDENT")) {
//             Role studentRole = new Role();
//             studentRole.setRoleName("学生");
//             studentRole.setRoleKey("STUDENT");
//             studentRole.setDescription("学生角色，可以查看课程和缴费信息");
//             studentRole.setStatus(1);
//             studentRole.setSortOrder(3);
//             roleService.createRole(studentRole);
//             System.out.println("  ✓ 创建学生角色");
//         }

//         // 检查并创建财务角色
//         if (!roleService.existsByRoleKey("FINANCE")) {
//             Role financeRole = new Role();
//             financeRole.setRoleName("财务人员");
//             financeRole.setRoleKey("FINANCE");
//             financeRole.setDescription("财务人员角色，可以管理缴费项目和财务数据");
//             financeRole.setStatus(1);
//             financeRole.setSortOrder(4);
//             roleService.createRole(financeRole);
//             System.out.println("  ✓ 创建财务角色");
//         }

//         System.out.println("✅ 系统角色初始化完成");
//     }

//     /**
//      * 初始化缴费项目
//      */
//     private void initializeFeeItems() {
//         System.out.println("💰 正在初始化缴费项目...");

//         // 检查并创建学费项目
//         if (!feeItemService.existsByItemCode("XF20250601001")) {
//             FeeItem tuitionFee = new FeeItem();
//             tuitionFee.setItemName("学费");
//             tuitionFee.setItemCode("XF20250601001");
//             tuitionFee.setAmount(new BigDecimal("5000.00"));
//             tuitionFee.setFeeType("学费");
//             tuitionFee.setApplicableGrade("全年级");
//             tuitionFee.setAcademicYear(2025);
//             tuitionFee.setSemester("全年");
//             tuitionFee.setDueDate(LocalDate.of(2025, 9, 30));
//             tuitionFee.setDescription("2025学年学费");
//             tuitionFee.setStatus(1);
//             tuitionFee.setDeleted(0);
//             feeItemService.createFeeItem(tuitionFee);
//             System.out.println("  ✓ 创建学费项目");
//         }

//         // 检查并创建住宿费项目
//         if (!feeItemService.existsByItemCode("ZSF20250601001")) {
//             FeeItem accommodationFee = new FeeItem();
//             accommodationFee.setItemName("住宿费");
//             accommodationFee.setItemCode("ZSF20250601001");
//             accommodationFee.setAmount(new BigDecimal("1200.00"));
//             accommodationFee.setFeeType("住宿费");
//             accommodationFee.setApplicableGrade("全年级");
//             accommodationFee.setDueDate(LocalDate.of(2025, 9, 30));
//             accommodationFee.setDescription("2025学年住宿费");
//             accommodationFee.setStatus(1);
//             accommodationFee.setDeleted(0);
//             feeItemService.createFeeItem(accommodationFee);
//             System.out.println("  ✓ 创建住宿费项目");
//         }

//         // 检查并创建教材费项目
//         if (!feeItemService.existsByItemCode("SF20250601001")) {
//             FeeItem textbookFee = new FeeItem();
//             textbookFee.setItemName("教材费");
//             textbookFee.setItemCode("SF20250601001");
//             textbookFee.setAmount(new BigDecimal("300.00"));
//             textbookFee.setFeeType("教材费");
//             textbookFee.setApplicableGrade("全年级");
//             textbookFee.setDueDate(LocalDate.of(2025, 9, 15));
//             textbookFee.setDescription("2025学年教材费");
//             textbookFee.setStatus(1);
//             textbookFee.setDeleted(0);
//             feeItemService.createFeeItem(textbookFee);
//             System.out.println("  ✓ 创建教材费项目");
//         }

//         // 检查并创建实验费项目
//         if (!feeItemService.existsByItemCode("SYF20250601001")) {
//             FeeItem experimentFee = new FeeItem();
//             experimentFee.setItemName("实验费");
//             experimentFee.setItemCode("SYF20250601001");
//             experimentFee.setAmount(new BigDecimal("500.00"));
//             experimentFee.setFeeType("实验费");
//             experimentFee.setApplicableGrade("理工科专业");
//             experimentFee.setDueDate(LocalDate.of(2025, 10, 15));
//             experimentFee.setDescription("2025学年实验费（理工科专业）");
//             experimentFee.setStatus(1);
//             experimentFee.setDeleted(0);
//             feeItemService.createFeeItem(experimentFee);
//             System.out.println("  ✓ 创建实验费项目");
//         }

//         // 检查并创建活动费项目
//         if (!feeItemService.existsByItemCode("ZF20250601001")) {
//             FeeItem activityFee = new FeeItem();
//             activityFee.setItemName("活动费");
//             activityFee.setItemCode("ZF20250601001");
//             activityFee.setAmount(new BigDecimal("200.00"));
//             activityFee.setFeeType("杂费");
//             activityFee.setApplicableGrade("全年级");
//             activityFee.setDueDate(LocalDate.of(2025, 9, 30));
//             activityFee.setDescription("2025学年学生活动费");
//             activityFee.setStatus(1);
//             activityFee.setDeleted(0);
//             feeItemService.createFeeItem(activityFee);
//             System.out.println("  ✓ 创建活动费项目");
//         }

//         System.out.println("✅ 缴费项目初始化完成");
//     }
// }