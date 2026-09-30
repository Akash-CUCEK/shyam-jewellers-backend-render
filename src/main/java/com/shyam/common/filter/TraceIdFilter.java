package com.shyam.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter to extract or generate traceId and spanId and put them in MDC for logging. Supports B3 and
 * W3C Trace Context headers for traceId.
 */
@Component
public class TraceIdFilter extends OncePerRequestFilter {

  private static final String TRACE_ID_KEY = "traceId";
  private static final String SPAN_ID_KEY = "spanId";
  private static final String X_B3_TRACEID = "X-B3-Traceid";
  private static final String TRACEPARENT = "traceparent";

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String traceId = extractTraceId(request);
    if (traceId == null) {
      traceId = UUID.randomUUID().toString();
    }
    MDC.put(TRACE_ID_KEY, traceId);

    // Generate spanId for this request
    String spanId = UUID.randomUUID().toString();
    MDC.put(SPAN_ID_KEY, spanId);

    try {
      filterChain.doFilter(request, response);
    } finally {
      MDC.remove(TRACE_ID_KEY);
      MDC.remove(SPAN_ID_KEY);
    }
  }

  /**
   * Extract traceId from request headers. Checks for W3C Traceparent header first, then B3.
   *
   * @param request the HTTP request
   * @return traceId or null if not found
   */
  private String extractTraceId(HttpServletRequest request) {
    // W3C Trace Context: traceparent
    String traceparent = request.getHeader(TRACEPARENT);
    if (traceparent != null && !traceparent.isEmpty()) {
      // Format: 00-0af7651916cd43dd8448eb211c80319c-b7ad6b7169203331-01
      // We want the trace id (second part, 32 hex chars)
      String[] parts = traceparent.split("-");
      if (parts.length >= 2) {
        return parts[1];
      }
    }

    // B3: X-B3-Traceid
    String b3TraceId = request.getHeader(X_B3_TRACEID);
    if (b3TraceId != null && !b3TraceId.isEmpty()) {
      return b3TraceId;
    }

    return null;
  }
}
