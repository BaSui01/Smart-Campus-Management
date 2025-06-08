#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Smart Campus Management System - 教师数据生成脚本
生成大量教师用户数据
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
    '天', '翔', '宁', '安', '康', '乐', '欣', '悦', '畅', '达',
    '学', '智', '才', '德', '仁', '义', '礼', '信', '忠', '孝'
]

GIVEN_NAMES_FEMALE = [
    '芳', '娜', '敏', '静', '丽', '洁', '美', '娟', '英', '华',
    '慧', '巧', '淑', '惠', '珠', '翠', '雅', '芝', '玉', '萍',
    '红', '娥', '玲', '芬', '燕', '彩', '春', '菊', '兰', '凤',
    '梅', '琳', '素', '云', '莲', '真', '环', '雪', '荣', '爱',
    '妹', '霞', '香', '月', '怡', '欣', '悦', '婷', '雯', '琪',
    '薇', '蕾', '晶', '颖', '瑶', '璐', '莎', '倩', '妍', '嘉'
]

# 省份城市列表
CITIES = [
    '辽宁省抚顺市', '辽宁省本溪市', '辽宁省丹东市', '辽宁省锦州市', '辽宁省营口市',
    '辽宁省阜新市', '辽宁省辽阳市', '辽宁省盘锦市', '辽宁省铁岭市', '辽宁省朝阳市',
    '吉林省长春市', '吉林省吉林市', '吉林省四平市', '吉林省辽源市', '吉林省通化市',
    '吉林省白山市', '吉林省松原市', '吉林省白城市', '黑龙江省哈尔滨市', '黑龙江省齐齐哈尔市',
    '黑龙江省鸡西市', '黑龙江省鹤岗市', '黑龙江省双鸭山市', '黑龙江省大庆市', '黑龙江省伊春市',
    '黑龙江省佳木斯市', '黑龙江省七台河市', '黑龙江省牡丹江市', '黑龙江省黑河市', '黑龙江省绥化市',
    '江苏省南京市', '江苏省无锡市', '江苏省徐州市', '江苏省常州市', '江苏省苏州市',
    '江苏省南通市', '江苏省连云港市', '江苏省淮安市', '江苏省盐城市', '江苏省扬州市',
    '浙江省杭州市', '浙江省宁波市', '浙江省温州市', '浙江省嘉兴市', '浙江省湖州市',
    '浙江省绍兴市', '浙江省金华市', '浙江省衢州市', '浙江省舟山市', '浙江省台州市',
    '安徽省合肥市', '安徽省芜湖市', '安徽省蚌埠市', '安徽省淮南市', '安徽省马鞍山市',
    '安徽省淮北市', '安徽省铜陵市', '安徽省安庆市', '安徽省黄山市', '安徽省滁州市',
    '福建省福州市', '福建省厦门市', '福建省莆田市', '福建省三明市', '福建省泉州市',
    '福建省漳州市', '福建省南平市', '福建省龙岩市', '福建省宁德市', '江西省南昌市',
    '江西省景德镇市', '江西省萍乡市', '江西省九江市', '江西省新余市', '江西省鹰潭市',
    '江西省赣州市', '江西省吉安市', '江西省宜春市', '江西省抚州市', '江西省上饶市'
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

def generate_email(username: str) -> str:
    """生成邮箱"""
    return f'{username}@campus.edu.cn'

def generate_teacher_data(start_num: int, count: int) -> List[str]:
    """生成教师数据"""
    teacher_sqls = []
    
    for i in range(count):
        teacher_num = start_num + i
        gender = random.choice(['男', '女'])
        real_name = generate_name(gender)
        username = f'teacher{teacher_num:03d}'
        phone = generate_phone()
        email = generate_email(username)
        
        # 生成生日（教师年龄在30-60岁之间）
        birth_year = random.randint(1964, 1994)
        birth_month = random.randint(1, 12)
        birth_day = random.randint(1, 28)
        birthday = f'{birth_year}-{birth_month:02d}-{birth_day:02d}'
        
        address = random.choice(CITIES)
        
        # 生成教师SQL
        teacher_sql = f"""('{username}', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIh9bdKO7L9OgEe8W2Cd.r/6Vy', '{email}', '{real_name}', '{phone}', '{gender}', '{birthday}', '{address}', 1, 1, 1)"""
        teacher_sqls.append(teacher_sql)
    
    return teacher_sqls

def main():
    """主函数"""
    print("开始生成教师数据...")
    
    # 生成教师数据（从teacher061开始，生成240个教师，总共达到300个）
    teacher_sqls = generate_teacher_data(61, 240)
    
    # 追加到现有的用户数据文件
    with open('05_insert_user_data.sql', 'a', encoding='utf-8') as f:
        f.write('\n-- 更多教师数据（自动生成）\n')
        
        # 分批写入教师数据
        batch_size = 50
        for i in range(0, len(teacher_sqls), batch_size):
            batch = teacher_sqls[i:i+batch_size]
            if i == 0:
                f.write(',\n-- 批次 ' + str(i//batch_size + 1) + '\n')
            else:
                f.write(',\n-- 批次 ' + str(i//batch_size + 1) + '\n')
            f.write(',\n'.join(batch))
        
        f.write(';\n\n')
        f.write('-- 更新教师角色分配统计\n')
        f.write('-- 为新增教师分配角色\n')
        f.write("INSERT INTO tb_user_role (user_id, role_id, assigned_by, assigned_time)\n")
        f.write("SELECT id, 6, 1, NOW() FROM tb_user WHERE username LIKE 'teacher%' AND id NOT IN (SELECT user_id FROM tb_user_role WHERE role_id = 6);\n\n")
    
    print(f"教师数据生成完成！总计生成 {len(teacher_sqls)} 条教师记录")
    print("数据已追加到: 05_insert_user_data.sql")

if __name__ == '__main__':
    main()
