package com.core;

import java.lang.annotation.Annotation;
import java.util.function.BiFunction;

/**
 *
 * @author martin on 11/4/25
 */
public record IntegrationTestValueDescriptor<AnnotationType extends Annotation, ValueType>(
        Class<AnnotationType> annotationType,
        Class<ValueType> valueType,
        BiFunction<? super IntegrationTestEnvironment, ? super AnnotationType, ? extends ValueType> valueGetter
)
{
}
