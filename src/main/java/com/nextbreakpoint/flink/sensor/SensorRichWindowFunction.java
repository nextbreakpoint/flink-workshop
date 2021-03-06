package com.nextbreakpoint.flink.sensor;

import com.nextbreakpoint.flink.common.DoubleGauge;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.MetricGroup;
import org.apache.flink.streaming.api.functions.windowing.RichWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public class SensorRichWindowFunction extends RichWindowFunction<SensorEvent, MaxTemperatureEvent, String, TimeWindow> {
    private static final Logger log = LoggerFactory.getLogger(SensorRichWindowFunction.class);

    private transient Map<String, Counter> counters = new HashMap<>();
    private transient Map<String, DoubleGauge> gauges = new HashMap<>();

    public void open(Configuration parameters) throws Exception {
        counters = new HashMap<>();
        gauges = new HashMap<>();
        super.open(parameters);
    }

    @Override
    public void apply(String sensorId, TimeWindow window, Iterable<SensorEvent> input, Collector<MaxTemperatureEvent> out) {
        final String countMetricName = "sensor." + sensorId + ".count";
        final String valueMetricName = "sensor." + sensorId + ".value";
        final MetricGroup metricGroup = getRuntimeContext().getMetricGroup();
        final Counter counter = counters.computeIfAbsent(countMetricName, name -> {
            final Counter metric = metricGroup.counter(name);
            final String counterMetric = metricGroup.getMetricIdentifier(countMetricName);
            log.info("Created metric " + counterMetric);
            return metric;
        });
        final DoubleGauge gauge = gauges.computeIfAbsent(valueMetricName, name -> {
            final DoubleGauge metric = metricGroup.gauge(valueMetricName, new DoubleGauge());
            final String gaugeMetric = metricGroup.getMetricIdentifier(valueMetricName);
            log.info("Created metric " + gaugeMetric);
            return metric;
        });
        final Iterator<SensorEvent> iterator = input.iterator();
        iterator.forEachRemaining(event -> {
            counter.inc();
            gauge.setValue(event.getTemperature());
            out.collect(new MaxTemperatureEvent(sensorId, event.getTemperature(), ISO_DATE_TIME.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(window.maxTimestamp()), ZoneId.of("UTC")))));
        });
    }
}
