package com.gastonlagaf.meccano.api.internal.validation

import com.gastonlagaf.meccano.api.ValidationProvider
import javax.validation.ConstraintViolationException
import javax.validation.Validation

class DefaultValidationProvider : ValidationProvider {

    private val validatorFactory = Validation.buildDefaultValidatorFactory()

    override fun validate(bean: Any) {
        val validator = validatorFactory.validator
        val violations = validator.validate(bean)
        if (violations.isNotEmpty()) {
            throw ConstraintViolationException(violations)
        }
    }

}