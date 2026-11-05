Copyright 2018 AT&T Intellectual Property. All rights reserved.
This file is licensed under the CREATIVE COMMONS ATTRIBUTION 4.0 INTERNATIONAL LICENSE
Full license text at https://creativecommons.org/licenses/by/4.0/legalcode

This source repository contains the ONAP Policy SDC Service Distribution code.

To build it using Maven 3, run: mvn clean install

SDC notification tracing
------------------------

The SDC client 2.4.0 preserves each Kafka record's W3C `traceparent` and
`tracestate` headers. Policy Distribution creates a consumer span for each
notification, including its artifact decoding and policy forwarding. API, PAP,
and automation composition HTTP requests carry W3C headers from that consumer span.
Notifications without valid context start independent traces.

The service initializes OpenTelemetry with the JDK HTTP/protobuf OTLP exporter
and the service name `policy-distribution`. Configure it with standard OpenTelemetry environment
variables or Java system properties, for example:

```
OTEL_SERVICE_NAME=policy-distribution
OTEL_EXPORTER_OTLP_ENDPOINT=http://collector:4318
OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf
OTEL_TRACES_SAMPLER=parentbased_traceidratio
OTEL_TRACES_SAMPLER_ARG=0.1
```

Use `OTEL_SDK_DISABLED=true` to disable application telemetry. Metrics and logs
exporters default to `none`; existing metrics and logging remain configured by
their own settings. An embedding application or Java agent that registers
OpenTelemetry before startup supplies the SDK instead; Policy Distribution does
not create or close a second SDK. The application-owned SDK flushes and shuts down
with the service.

Upstream SDC producer header injection is required for continuity with the SDC
trace. Artifact downloads and distribution status publishing inside the SDC SDK
are not manually instrumented by Policy Distribution.
