package io.zensoft.hootka.api.internal.exception

import io.zensoft.hootka.api.internal.server.nio.http.request.CaretPosition

class InvalidCaretLocationException(
    actualPosition: CaretPosition,
    requiredPosition: CaretPosition
): RuntimeException("Invalid caret position: required $requiredPosition, but actual is $actualPosition")