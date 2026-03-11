package com.innowise.aspect;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TracingAspectTest {

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    private TracingAspect tracingAspect;
    private Tracer tracer;

    @BeforeEach
    void setUp() {
        tracer = OpenTelemetry.noop().getTracer("test");
        tracingAspect = new TracingAspect(tracer);

        lenient().when(joinPoint.getSignature()).thenReturn(methodSignature);
        lenient().when(methodSignature.getDeclaringType()).thenReturn(String.class);
        lenient().when(methodSignature.getName()).thenReturn("testMethod");
    }


    @Test
    void traceController_success_returnsResult() throws Throwable {
        when(joinPoint.proceed()).thenReturn("result");

        Object result = tracingAspect.traceController(joinPoint);

        assertThat(result).isEqualTo("result");
        verify(joinPoint).proceed();
    }

    @Test
    void traceController_exception_rethrows() throws Throwable {
        RuntimeException ex = new RuntimeException("controller error");
        when(joinPoint.proceed()).thenThrow(ex);

        assertThatThrownBy(() -> tracingAspect.traceController(joinPoint))
                .isSameAs(ex);
    }

    @Test
    void traceController_nullResult_returnsNull() throws Throwable {
        when(joinPoint.proceed()).thenReturn(null);

        Object result = tracingAspect.traceController(joinPoint);
        assertThat(result).isNull();
    }


    @Test
    void traceService_success_returnsResult() throws Throwable {
        when(joinPoint.proceed()).thenReturn(42);

        Object result = tracingAspect.traceService(joinPoint);

        assertThat(result).isEqualTo(42);
        verify(joinPoint).proceed();
    }

    @Test
    void traceService_exception_rethrows() throws Throwable {
        IllegalStateException ex = new IllegalStateException("service error");
        when(joinPoint.proceed()).thenThrow(ex);

        assertThatThrownBy(() -> tracingAspect.traceService(joinPoint))
                .isSameAs(ex);
    }


    @Test
    void traceRepository_success_returnsResult() throws Throwable {
        when(joinPoint.proceed()).thenReturn("entity");

        Object result = tracingAspect.traceRepository(joinPoint);

        assertThat(result).isEqualTo("entity");
        verify(joinPoint).proceed();
    }

    @Test
    void traceRepository_exception_rethrows() throws Throwable {
        RuntimeException ex = new RuntimeException("repo error");
        when(joinPoint.proceed()).thenThrow(ex);

        assertThatThrownBy(() -> tracingAspect.traceRepository(joinPoint))
                .isSameAs(ex);
    }
}
