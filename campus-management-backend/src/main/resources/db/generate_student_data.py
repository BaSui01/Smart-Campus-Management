#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Smart Campus Management System - 学生数据生成脚本
生成大量学生用户数据和学生信息数据
"""

import random
import datetime
from typing import List, Tuple

# 中文姓氏列表
SURNAMES = [
    '王', '李', '张', '刘', '陈', '杨', '赵', '黄', '周', '吴',
    '徐', '孙', '胡', '朱', '高', '林', '何', '郭', '马', '罗',
    '梁', '宋', '郑', '谢', '韩', '唐', '冯', '于', '董', '萧',
    '程', '曹', '袁', '邓', '许', '傅', '沈', '曾', '彭', '吕',
    '苏', '卢', '蒋', '蔡', '贾', '丁', '魏', '薛', '叶', '阎',
    '余', '潘', '杜', '戴', '夏', '钟', '汪', '田', '任', '姜',
    '范', '方', '石', '姚', '谭', '廖', '邹', '熊', '金', '陆',
    '郝', '孔', '白', '崔', '康', '毛', '邱', '秦', '江', '史',
    '顾', '侯', '邵', '孟', '龙', '万', '段', '雷', '钱', '汤',
    '尹', '黎', '易', '常', '武', '乔', '贺', '赖', '龚', '文'
]

# 中文名字列表
GIVEN_NAMES_MALE = [
    '伟', '强', '磊', '军', '勇', '涛', '明', '超', '亮', '华',
    '建', '国', '峰', '鹏', '辉', '斌', '刚', '雷', '东', '波',
    '飞', '凯', '杰', '龙', '浩', '宇', '阳', '帆', '晨', '昊',
    '轩', '宸', '睿', '博', '文', '武', '志', '鸿', '俊', '豪',
    '天', '翔', '宁', '安', '康', '乐', '欣', '悦', '畅', '达'
]

GIVEN_NAMES_FEMALE = [
    '芳', '娜', '敏', '静', '丽', '强', '洁', '美', '娟', '英',
    '华', '慧', '巧', '美', '娜', '静', '淑', '惠', '珠', '翠',
    '雅', '芝', '玉', '萍', '红', '娥', '玲', '芬', '芳', '燕',
    '彩', '春', '菊', '兰', '凤', '洁', '梅', '琳', '素', '云',
    '莲', '真', '环', '雪', '荣', '爱', '妹', '霞', '香', '月'
]

# 专业列表
MAJORS = [
    '计算机科学与技术',
    '软件工程',
    '网络工程',
    '数据科学与大数据技术',
    '人工智能',
    '网络空间安全',
    '物联网工程',
    '信息安全',
    '电子信息工程',
    '通信工程',
    '自动化',
    '电气工程及其自动化'
]

# 省份列表
PROVINCES = [
    '北京市', '天津市', '河北省', '山西省', '内蒙古自治区',
    '辽宁省', '吉林省', '黑龙江省', '上海市', '江苏省',
    '浙江省', '安徽省', '福建省', '江西省', '山东省',
    '河南省', '湖北省', '湖南省', '广东省', '广西壮族自治区',
    '海南省', '重庆市', '四川省', '贵州省', '云南省',
    '西藏自治区', '陕西省', '甘肃省', '青海省', '宁夏回族自治区',
    '新疆维吾尔自治区'
]

def generate_name(gender: str) -> str:
    """生成中文姓名"""
    surname = random.choice(SURNAMES)
    if gender == '男':
        given_names = GIVEN_NAMES_MALE
    else:
        given_names = GIVEN_NAMES_FEMALE
    
    # 生成1-2个字的名字
    if random.random() < 0.7:  # 70%概率生成两个字的名字
        given_name = random.choice(given_names) + random.choice(given_names)
    else:  # 30%概率生成一个字的名字
        given_name = random.choice(given_names)
    
    return surname + given_name

def generate_phone() -> str:
    """生成手机号"""
    prefixes = ['130', '131', '132', '133', '134', '135', '136', '137', '138', '139',
                '150', '151', '152', '153', '155', '156', '157', '158', '159',
                '180', '181', '182', '183', '184', '185', '186', '187', '188', '189']
    prefix = random.choice(prefixes)
    suffix = ''.join([str(random.randint(0, 9)) for _ in range(8)])
    return prefix + suffix

def generate_id_card(birth_year: int, birth_month: int, birth_day: int) -> str:
    """生成身份证号"""
    # 地区码（使用北京的110101）
    area_code = '110101'
    # 出生日期
    birth_date = f'{birth_year:04d}{birth_month:02d}{birth_day:02d}'
    # 顺序码（随机3位数）
    sequence = f'{random.randint(100, 999):03d}'
    # 前17位
    id_17 = area_code + birth_date + sequence
    # 计算校验码
    weights = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2]
    check_codes = ['1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2']
    sum_val = sum(int(id_17[i]) * weights[i] for i in range(17))
    check_code = check_codes[sum_val % 11]
    return id_17 + check_code

def generate_email(username: str) -> str:
    """生成邮箱"""
    domains = ['qq.com', '163.com', '126.com', 'gmail.com', 'sina.com']
    domain = random.choice(domains)
    return f'{username}@{domain}'

def generate_address() -> str:
    """生成地址"""
    province = random.choice(PROVINCES)
    street_num = random.randint(1, 999)
    return f'{province}某市某区某街道{street_num}号'

def generate_student_data(start_id: int, count: int, grade: str, year: int) -> Tuple[List[str], List[str]]:
    """生成学生数据"""
    user_sqls = []
    student_sqls = []
    
    for i in range(count):
        user_id = start_id + i
        gender = random.choice(['男', '女'])
        real_name = generate_name(gender)
        username = f'student{user_id:06d}'
        phone = generate_phone()
        email = generate_email(username)
        
        # 生成生日（根据年级计算合理的出生年份）
        birth_year = year - 18 - random.randint(0, 2)  # 18-20岁
        birth_month = random.randint(1, 12)
        birth_day = random.randint(1, 28)
        birthday = f'{birth_year}-{birth_month:02d}-{birth_day:02d}'
        
        id_card = generate_id_card(birth_year, birth_month, birth_day)
        address = generate_address()
        
        # 生成用户SQL
        user_sql = f"""('{username}', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIh9bdKO7L9OgEe8W2Cd.r/6Vy', '{email}', '{real_name}', '{phone}', '{gender}', '{birthday}', '{id_card}', '{address}', 1, 1, 1)"""
        user_sqls.append(user_sql)
        
        # 生成学号
        student_no = f'{year}{random.randint(10, 99)}{i+1:04d}'
        major = random.choice(MAJORS)
        
        # 随机分配班级ID（根据年级和新增的班级）
        if grade == '2021级':
            class_id = random.randint(1, 16)  # 包含新增的AI、NS、IOT班级
        elif grade == '2022级':
            class_id = random.randint(17, 32)  # 包含新增的AI、NS、IOT班级
        elif grade == '2023级':
            class_id = random.randint(33, 50)  # 包含新增的AI、NS、IOT班级
        elif grade == '2024级':
            class_id = random.randint(51, 68)  # 包含新增的AI、NS、IOT班级
        else:  # 2025级
            class_id = random.randint(69, 76)  # 包含新增的AI、NS班级
        
        enrollment_date = f'{year}-09-01'
        graduation_date = f'{year + 4}-06-30'
        
        # 生成宿舍号
        building = random.randint(1, 10)
        room = random.randint(101, 699)
        dormitory_no = f'{building}号楼{room}'
        
        # 生成紧急联系人信息
        emergency_contact = generate_name(random.choice(['男', '女']))
        emergency_phone = generate_phone()
        
        hometown = random.choice(PROVINCES)
        political_status = random.choice(['群众', '共青团员', '中共党员', '民主党派'])
        family_address = generate_address()
        
        guardian_name = generate_name(random.choice(['男', '女']))
        guardian_phone = generate_phone()
        guardian_relation = random.choice(['父亲', '母亲', '祖父', '祖母', '其他'])
        
        # 生成学生SQL
        student_sql = f"""({user_id}, '{student_no}', '{grade}', '{major}', {class_id}, '{enrollment_date}', {year}, '{graduation_date}', 'UNDERGRADUATE', 'ENROLLED', '{dormitory_no}', '{emergency_contact}', '{emergency_phone}', '{hometown}', '{political_status}', '{family_address}', '{guardian_name}', '{guardian_phone}', '{guardian_relation}')"""
        student_sqls.append(student_sql)
    
    return user_sqls, student_sqls

def main():
    """主函数"""
    print("开始生成学生数据...")
    
    # 生成各年级学生数据
    grades_data = [
        ('2021级', 2021, 4000),  # 2021级4000人
        ('2022级', 2022, 4000),  # 2022级4000人
        ('2023级', 2023, 4000),  # 2023级4000人
        ('2024级', 2024, 4000),  # 2024级4000人
        ('2025级', 2025, 3000),  # 2025级3000人
    ]
    
    all_user_sqls = []
    all_student_sqls = []
    current_id = 1000  # 从1000开始，避免与管理员和教师ID冲突
    
    for grade, year, count in grades_data:
        print(f"生成{grade}学生数据...")
        user_sqls, student_sqls = generate_student_data(current_id, count, grade, year)
        all_user_sqls.extend(user_sqls)
        all_student_sqls.extend(student_sqls)
        current_id += count
    
    # 写入SQL文件
    with open('06_insert_student_data.sql', 'w', encoding='utf-8') as f:
        f.write("""-- =====================================================
-- Smart Campus Management System - 学生数据插入脚本
-- =====================================================
-- 文件名: 06_insert_student_data.sql
-- 描述: 插入大量学生数据（19,000+ 条记录）
-- 版本: 1.0.0
-- 创建时间: 2025-06-08
-- 兼容性: MySQL 8.0+
-- =====================================================

USE campus_management_db;

-- 设置字符集
SET NAMES utf8mb4;

-- 开始事务
START TRANSACTION;

-- =====================================================
-- 1. 学生用户数据
-- =====================================================

-- 插入学生用户数据
INSERT INTO tb_user (username, password, email, real_name, phone, gender, birthday, id_card, address, account_non_expired, account_non_locked, credentials_non_expired) VALUES
""")
        
        # 分批写入用户数据
        batch_size = 1000
        for i in range(0, len(all_user_sqls), batch_size):
            batch = all_user_sqls[i:i+batch_size]
            f.write(',\n'.join(batch))
            if i + batch_size < len(all_user_sqls):
                f.write(',\n')
            else:
                f.write(';\n\n')
        
        f.write("""-- =====================================================
-- 2. 学生信息数据
-- =====================================================

-- 插入学生信息数据
INSERT INTO tb_student (user_id, student_no, grade, major, class_id, enrollment_date, enrollment_year, graduation_date, student_type, student_status, dormitory_no, emergency_contact, emergency_phone, hometown, political_status, family_address, guardian_name, guardian_phone, guardian_relation) VALUES
""")
        
        # 分批写入学生数据
        for i in range(0, len(all_student_sqls), batch_size):
            batch = all_student_sqls[i:i+batch_size]
            f.write(',\n'.join(batch))
            if i + batch_size < len(all_student_sqls):
                f.write(',\n')
            else:
                f.write(';\n\n')
        
        f.write("""-- =====================================================
-- 3. 为学生分配角色
-- =====================================================

-- 为所有学生分配学生角色
INSERT INTO tb_user_role (user_id, role_id, assigned_by, assigned_time)
SELECT id, 6, 1, NOW() FROM tb_user WHERE username LIKE 'student%';

-- 提交事务
COMMIT;

-- 显示完成信息
SELECT '学生数据插入完成！' as '执行结果',
       (SELECT COUNT(*) FROM tb_user WHERE username LIKE 'student%') as '学生用户数量',
       (SELECT COUNT(*) FROM tb_student) as '学生信息数量';
""")
    
    print(f"学生数据生成完成！总计生成 {len(all_user_sqls)} 条学生记录")
    print("SQL文件已保存到: 06_insert_student_data.sql")

if __name__ == '__main__':
    main()
