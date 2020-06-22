package io.zensoft.hootka.api.internal

class InvalidCaretLocationException(
    actualPosition: CaretPosition,
    requiredPosition: CaretPosition
): RuntimeException("Invalid caret position: required $requiredPosition, but actual is $actualPosition")