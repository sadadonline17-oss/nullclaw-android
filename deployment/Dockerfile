# NullClaw Backend Service
# Lightweight AI assistant backend for Android client

FROM alpine:3.20 AS builder

# Install build dependencies
RUN apk add --no-cache \
    zig \
    git \
    curl \
    sqlite-dev \
    build-base

# Clone and build NullClaw
WORKDIR /build
RUN git clone https://github.com/nullclaw/nullclaw.git .
RUN zig build -Doptimize=ReleaseSmall

# Runtime image
FROM alpine:3.20

# Install runtime dependencies
RUN apk add --no-cache \
    ca-certificates \
    sqlite-libs \
    curl \
    tzdata

# Create non-root user
RUN addgroup -g 1000 nullclaw && \
    adduser -u 1000 -G nullclaw -s /bin/sh -D nullclaw

# Copy binary from builder
COPY --from=builder /build/zig-out/bin/nullclaw /usr/local/bin/nullclaw

# Create directories
RUN mkdir -p /home/nullclaw/.nullclaw/workspace && \
    chown -R nullclaw:nullclaw /home/nullclaw

WORKDIR /home/nullclaw

# Expose gateway port
EXPOSE 3000

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:3000/health || exit 1

# Switch to non-root user
USER nullclaw

# Set environment variables
ENV NULLCLAW_WORKSPACE=/home/nullclaw/.nullclaw/workspace
ENV NULLCLAW_GATEWAY_PORT=3000

# Start gateway
ENTRYPOINT ["nullclaw"]
CMD ["gateway", "--port", "3000"]