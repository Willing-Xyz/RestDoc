package cn.willingxyz.restdoc.spring.examples.beanvalidation;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class BeanValidatedChild {
    /**
     * AssertFalse
     */
    @AssertFalse
    private Boolean _assertFalse;
    /**
     * AssertTrue
     */
    @AssertTrue
    private Boolean _assertTrue;
    /**
     * NotNull
     */
    @NotNull
    private String _notNull;
    /**
     * Null
     */
    @Null
    private String _isNull;
    /**
     * Email
     */
    @Email
    private String _email;
    /**
     * NotBlank
     */
    @NotBlank
    private String _notBlank;
    /**
     * NotEmpty
     */
    @NotEmpty
    private String _notEmpty;
    /**
     * Max
     */
    @Max(value = 12)
    private int _max;
    /**
     * Min
     */
    @Min(value = 10)
    private int _min;
    /**
     * DecimalMax0Inclusive
     */
    @DecimalMax(value = "10", inclusive = true)
    private String _decimalMaxInclusive;
    /**
     * DecimalMax-Exclusive
     */
    @DecimalMax(value = "10", inclusive = false)
    private String _decimalMaxExclusive;
    /**
     * Negative
     */
    @Negative
    private int _negative;
    /**
     * NegativeOrZero
     */
    @NegativeOrZero
    private int _negativeOrZero;
    /**
     * Positive
     */
    @Positive
    private int _positive;
    /**
     * PositiveOrZero
     */
    @PositiveOrZero
    private int _positiveOrZero;
    /**
     * Size
     */
    @Size(min = 1, max = 12)
    private String _size;

}
