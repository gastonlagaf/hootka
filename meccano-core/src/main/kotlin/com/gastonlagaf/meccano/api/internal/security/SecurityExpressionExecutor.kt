package com.gastonlagaf.meccano.api.internal.security

import com.gastonlagaf.meccano.api.SecurityExpressionInitializer
import com.gastonlagaf.meccano.api.SecurityProvider
import com.gastonlagaf.meccano.api.exceptions.PreconditionNotSatisfiedException
import com.gastonlagaf.meccano.api.internal.support.RequestContext
import com.gastonlagaf.meccano.api.model.SimpleAuthenticationDetails
import javax.el.ELContext
import javax.el.ELException
import javax.el.ExpressionFactory
import javax.el.StandardELContext

class SecurityExpressionExecutor(
    private val securityProvider: SecurityProvider<SimpleAuthenticationDetails>,
    private val securityExpressionInitializer: SecurityExpressionInitializer
) {

    private val expressionFactory = ExpressionFactory.newInstance()

    companion object {
        private const val EXPRESSIONS_PLACEHOLDER = "exp"
    }

    fun checkAllowance(expression: String, context: RequestContext) {
        if (null == context.session || null == securityProvider.findPrincipal(context)) {
            securityProvider.remindMe(context)
        }
        val elContext = createElContext(context)
        val elExpression = "\${$EXPRESSIONS_PLACEHOLDER.$expression}"
        val valueExpression = expressionFactory.createValueExpression(elContext, elExpression, Boolean::class.java)
        val result = try {
            valueExpression.getValue(elContext) as Boolean
        } catch (ex: ELException) {
            false
        }
        if (!result) {
            throw PreconditionNotSatisfiedException("Access Forbidden", true)
        }
    }

    private fun createElContext(context: RequestContext): ELContext {
        val elContext = StandardELContext(expressionFactory)
        val principal = securityProvider.findPrincipal(context)
        val expressions = securityExpressionInitializer.createSecurityExpressions(principal)
        val preconditionVar = expressionFactory.createValueExpression(expressions, expressions::class.java)
        elContext.variableMapper.setVariable(EXPRESSIONS_PLACEHOLDER, preconditionVar)
        return elContext
    }

}