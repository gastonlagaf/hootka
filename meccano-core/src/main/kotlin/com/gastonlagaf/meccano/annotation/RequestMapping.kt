package com.gastonlagaf.meccano.annotation

import com.gastonlagaf.meccano.api.model.MimeType
import com.gastonlagaf.meccano.api.support.HttpMethod

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.CLASS)
annotation class RequestMapping(
    val value: Array<String> = [],
    val produces: MimeType = MimeType.APPLICATION_JSON,
    val method: HttpMethod = HttpMethod.GET
)
