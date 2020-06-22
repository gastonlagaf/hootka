package io.zensoft.hootka.api.internal.invoke

import io.zensoft.hootka.api.internal.support.HandlerMethodParameter
import java.util.*
import javax.tools.ToolProvider
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaType

class MethodInvocationProducer {

    private val compiler = ToolProvider.getSystemJavaCompiler()

    fun generateMethodInvocation(bean: Any, function: KFunction<*>, parameters: List<HandlerMethodParameter>): MethodInvocation {
        val className = function.name.capitalize()
        val fullClassName = String.format(CLASS_NAME_TEMPLATE, className)
        val source = generateSource(bean, function, parameters)

        val unit = StringJavaFileObject(fullClassName, source)
        val fileManager = TempFileManager(compiler.getStandardFileManager(null, null, null))

        val compilationTask = compiler.getTask(null, fileManager, null, null, null, listOf(unit))
        if (!compilationTask.call()) {
            throw ExceptionInInitializerError("Failed controller method compilation. Method ${function.name} in ${bean::class.simpleName}")
        }

        val classLoader = MethodInvocationsClassLoader(fileManager)
        val clazz = classLoader.loadClass(fullClassName)
        return clazz.constructors[0].newInstance(bean) as MethodInvocation
    }

    private fun generateSource(bean: Any, function: KFunction<*>, parameters: List<HandlerMethodParameter>): String {
        val className = function.name.capitalize()
        val beanType = bean::class.java.simpleName
        val imports = generateImports(bean, parameters)
        val implementation = implement(function, parameters)
        return String.format(SOURCE_TEMPLATE, imports, className, beanType, className, beanType, implementation)
    }

    private fun generateImports(bean: Any, parameters: List<HandlerMethodParameter>): String {
        val imports = mutableSetOf<String>()
        for (param in parameters) {
            imports.add("import ${param.clazz.name};")
        }
        imports.add("import ${bean::class.java.name};")
        return imports.joinToString("\n")
    }

    private fun implement(function: KFunction<*>, parameters: List<HandlerMethodParameter>): String {
        val result = mutableListOf<String>()
        parameters.forEachIndexed { idx, it ->
            val className = it.clazz.simpleName
            result.add("$className arg$idx = ($className)args[$idx];")
        }
        val args = mutableListOf<String>()
        for (i in 0 until result.size) {
            args.add("arg$i")
        }
        if ("void" == function.returnType.javaType.typeName) {
            result.add("bean.${function.name}(${args.joinToString()});")
            result.add("return null;")
        } else {
            result.add("return bean.${function.name}(${args.joinToString()});")
        }
        return result.joinToString("\n")
    }

    companion object {
        private const val CLASS_NAME_TEMPLATE = "io.zensoft.hootka.generated.%s"

        private const val SOURCE_TEMPLATE = """
            package io.zensoft.hootka.generated;

            import io.zensoft.hootka.api.internal.invoke.MethodInvocation;
            %s

            public class %s implements MethodInvocation {

                private %s bean;

                public %s(%s bean) {
                    this.bean = bean;
                }

                @Override
                public Object invoke(Object[] args) {
                    %s
                }

            }
        """
    }

}