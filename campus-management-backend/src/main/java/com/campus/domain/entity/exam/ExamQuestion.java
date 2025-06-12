package com.campus.domain.entity.exam;

import com.campus.domain.entity.infrastructure.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 考试题目实体类
 * 管理考试中的题目信息，包括题目内容、选项、答案等
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "tb_exam_question")
public class ExamQuestion extends BaseEntity {

    /**
     * 考试ID
     */
    @NotNull(message = "考试ID不能为空")
    @Column(name = "exam_id", nullable = false)
    private Long examId;

    /**
     * 题目类型 (single_choice/multiple_choice/true_false/fill_blank/essay)
     */
    @NotBlank(message = "题目类型不能为空")
    @Size(max = 20, message = "题目类型长度不能超过20个字符")
    @Column(name = "question_type", nullable = false, length = 20)
    private String questionType;

    /**
     * 题目内容
     */
    @NotBlank(message = "题目内容不能为空")
    @Size(max = 2000, message = "题目内容长度不能超过2000个字符")
    @Column(name = "question_content", nullable = false, length = 2000)
    private String questionContent;

    /**
     * 选项A
     */
    @Size(max = 500, message = "选项A长度不能超过500个字符")
    @Column(name = "option_a", length = 500)
    private String optionA;

    /**
     * 选项B
     */
    @Size(max = 500, message = "选项B长度不能超过500个字符")
    @Column(name = "option_b", length = 500)
    private String optionB;

    /**
     * 选项C
     */
    @Size(max = 500, message = "选项C长度不能超过500个字符")
    @Column(name = "option_c", length = 500)
    private String optionC;

    /**
     * 选项D
     */
    @Size(max = 500, message = "选项D长度不能超过500个字符")
    @Column(name = "option_d", length = 500)
    private String optionD;

    /**
     * 正确答案
     */
    @Size(max = 1000, message = "正确答案长度不能超过1000个字符")
    @Column(name = "correct_answer", length = 1000)
    private String correctAnswer;

    /**
     * 题目分值
     */
    @NotNull(message = "题目分值不能为空")
    @Column(name = "score", nullable = false, precision = 5, scale = 2)
    private BigDecimal score;

    /**
     * 题目序号
     */
    @NotNull(message = "题目序号不能为空")
    @Column(name = "question_order", nullable = false)
    private Integer questionOrder;

    /**
     * 难度等级 (easy/medium/hard)
     */
    @Size(max = 10, message = "难度等级长度不能超过10个字符")
    @Column(name = "difficulty_level", length = 10)
    private String difficultyLevel;

    /**
     * 解析说明
     */
    @Size(max = 1000, message = "解析说明长度不能超过1000个字符")
    @Column(name = "explanation", length = 1000)
    private String explanation;

    /**
     * 是否启用
     */
    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    @Column(name = "remarks", length = 500)
    private String remarks;

    // ================================
    // 关联关系
    // ================================

    /**
     * 所属考试
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", insertable = false, updatable = false)
    @JsonIgnore
    private Exam exam;

    // ================================
    // 构造函数
    // ================================

    public ExamQuestion() {
        super();
    }

    public ExamQuestion(Long examId, String questionType, String questionContent, BigDecimal score, Integer questionOrder) {
        this();
        this.examId = examId;
        this.questionType = questionType;
        this.questionContent = questionContent;
        this.score = score;
        this.questionOrder = questionOrder;
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 获取题目类型文本
     */
    public String getQuestionTypeText() {
        if (questionType == null) return "未知";
        return switch (questionType) {
            case "single_choice" -> "单选题";
            case "multiple_choice" -> "多选题";
            case "true_false" -> "判断题";
            case "fill_blank" -> "填空题";
            case "essay" -> "问答题";
            default -> questionType;
        };
    }

    /**
     * 获取难度等级文本
     */
    public String getDifficultyLevelText() {
        if (difficultyLevel == null) return "未设置";
        return switch (difficultyLevel) {
            case "easy" -> "简单";
            case "medium" -> "中等";
            case "hard" -> "困难";
            default -> difficultyLevel;
        };
    }

    /**
     * 检查是否为选择题
     */
    public boolean isChoiceQuestion() {
        return "single_choice".equals(questionType) || "multiple_choice".equals(questionType);
    }

    /**
     * 检查是否为判断题
     */
    public boolean isTrueFalseQuestion() {
        return "true_false".equals(questionType);
    }

    /**
     * 检查是否为主观题
     */
    public boolean isSubjectiveQuestion() {
        return "fill_blank".equals(questionType) || "essay".equals(questionType);
    }

    /**
     * 获取选项数组
     */
    public String[] getOptions() {
        return new String[]{optionA, optionB, optionC, optionD};
    }

    /**
     * 设置选项
     */
    public void setOptions(String[] options) {
        if (options != null && options.length >= 4) {
            this.optionA = options[0];
            this.optionB = options[1];
            this.optionC = options[2];
            this.optionD = options[3];
        }
    }

    /**
     * 验证答案
     */
    public boolean validateAnswer(String answer) {
        if (correctAnswer == null || answer == null) {
            return false;
        }
        
        if (isChoiceQuestion() || isTrueFalseQuestion()) {
            return correctAnswer.equalsIgnoreCase(answer.trim());
        }
        
        // 对于主观题，这里只做简单的非空检查
        return !answer.trim().isEmpty();
    }

    /**
     * 获取考试标题
     */
    public String getExamTitle() {
        return exam != null ? exam.getTitle() : null;
    }

    // ================================
    // 验证方法
    // ================================

    /**
     * 验证题目数据
     */
    @PrePersist
    @PreUpdate
    public void validateQuestion() {
        // 选择题必须有选项
        if (isChoiceQuestion()) {
            if (optionA == null || optionA.trim().isEmpty() ||
                optionB == null || optionB.trim().isEmpty()) {
                throw new IllegalArgumentException("选择题至少需要两个选项");
            }
        }
        
        // 确保分值为正数
        if (score != null && score.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("题目分值必须大于0");
        }
        
        // 确保题目序号为正数
        if (questionOrder != null && questionOrder <= 0) {
            throw new IllegalArgumentException("题目序号必须大于0");
        }
    }

    // ================================
    // Getter and Setter methods
    // ================================

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public Integer getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(Integer questionOrder) {
        this.questionOrder = questionOrder;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }
}
