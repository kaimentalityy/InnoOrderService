package com.innowise.aspect;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * Aspect for automatic distributed tracing of controllers and services.
 * Creates spans for all HTTP requests and service method calls.
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class TracingAspect {

    private final Tracer tracer;

    /**
     * Traces all REST controller methods.
     */
    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object traceController(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String spanName = "controller." + signature.getDeclaringType().getSimpleName()
                + "." + signature.getName();

        Span span = tracer.spanBuilder(spanName)
                .setParent(Context.current())
                .startSpan();

        try (Scope scope = span.makeCurrent()) {
            span.setAttribute("component", "controller");
            span.setAttribute("class", signature.getDeclaringType().getName());
            span.setAttribute("method", signature.getName());

            log.debug("Starting controller span: {}", spanName);
            Object result = joinPoint.proceed();
            span.setStatus(StatusCode.OK);
            return result;

        } catch (Throwable e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            span.recordException(e);
            log.error("Error in controller span: {}", spanName, e);
            throw e;
        } finally {
            span.end();
            log.debug("Completed controller span: {}", spanName);
        }
    }

    /**
     * Traces all service methods.
     */
    @Around("@within(org.springframework.stereotype.Service)")
    public Object traceService(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String spanName = "service." + signature.getDeclaringType().getSimpleName()
                + "." + signature.getName();

        Span span = tracer.spanBuilder(spanName)
                .setParent(Context.current())
                .startSpan();

        try (Scope scope = span.makeCurrent()) {
            span.setAttribute("component", "service");
            span.setAttribute("class", signature.getDeclaringType().getName());
            span.setAttribute("method", signature.getName());

            log.debug("Starting service span: {}", spanName);
            Object result = joinPoint.proceed();
            span.setStatus(StatusCode.OK);
            return result;

        } catch (Throwable e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            span.recordException(e);
            log.error("Error in service span: {}", spanName, e);
            throw e;
        } finally {
            span.end();
            log.debug("Completed service span: {}", spanName);
        }
    }

    /**
     * Traces all repository methods.
     */
    @Around("execution(* org.springframework.data.repository.Repository+.*(..)) || @within(org.springframework.stereotype.Repository)")
    public Object traceRepository(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String spanName = "repository." + signature.getDeclaringType().getSimpleName()
                + "." + signature.getName();

        Span span = tracer.spanBuilder(spanName)
                .setParent(Context.current())
                .startSpan();

        try (Scope scope = span.makeCurrent()) {
            span.setAttribute("component", "repository");
            span.setAttribute("class", signature.getDeclaringType().getName());
            span.setAttribute("method", signature.getName());

            log.debug("Starting repository span: {}", spanName);
            Object result = joinPoint.proceed();
            span.setStatus(StatusCode.OK);
            return result;

        } catch (Throwable e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            span.recordException(e);
            log.error("Error in repository span: {}", spanName, e);
            throw e;
        } finally {
            span.end();
            log.debug("Completed repository span: {}", spanName);
        }
    }
}
