package io.github.hristogochev.vortex.annotation

@Target(
    allowedTargets = [
        AnnotationTarget.CLASS,
        AnnotationTarget.PROPERTY,
        AnnotationTarget.FUNCTION,
        AnnotationTarget.TYPEALIAS,
        AnnotationTarget.CONSTRUCTOR
    ]
)
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "This API is experimental. It may be changed in the future without notice."
)
public annotation class ExperimentalVortexApi