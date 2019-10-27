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

public class SensorRichWindowFunction extends RichWindowFunction<Double, SensorData, String, TimeWindow> {
    private static final Logger log = LoggerFactory.getLogger(SensorRichWindowFunction.class);

    private transient Map<String, Counter> counters = new HashMap<>();
    private transient Map<String, DoubleGauge> gauges = new HashMap<>();

    private transient String counterPrefix;
    private transient String gaugePrefix;

    public void open(Configuration parameters) throws Exception {
        counters = new HashMap<>();
        gauges = new HashMap<>();
        counterPrefix = "sensor.counter." + getRuntimeContext().getIndexOfThisSubtask() + ".";
        gaugePrefix = "sensor.gauge." + getRuntimeContext().getIndexOfThisSubtask() + ".";
        super.open(parameters);
    }

    @Override
    public void apply(String key, TimeWindow window, Iterable<Double> input, Collector<SensorData> out) {
        final String counterName = counterPrefix + key;
        final String gaugeName = gaugePrefix + key;
        final MetricGroup metricGroup = getRuntimeContext().getMetricGroup();
        final Counter counter = counters.computeIfAbsent(counterName, name -> {
            final Counter metric = metricGroup.counter(name);
            final String counterMetric = metricGroup.getMetricIdentifier(counterName);
            log.info("Created metric " + counterMetric);
            return metric;
        });
        final DoubleGauge gauge = gauges.computeIfAbsent(gaugeName, name -> {
            final DoubleGauge metric = metricGroup.gauge(gaugeName, new DoubleGauge());
            final String gaugeMetric = metricGroup.getMetricIdentifier(gaugeName);
            log.info("Created metric " + gaugeMetric);
            return metric;
        });
        final Iterator<Double> iterator = input.iterator();
        iterator.forEachRemaining(value -> {
            counter.inc();
            gauge.setValue(value);
            out.collect(new SensorData(key, value, ISO_DATE_TIME.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(window.maxTimestamp()), ZoneId.of("UTC")))));
        });
    }
}