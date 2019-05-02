package com.nextbreakpoint.flink.jobs;

import com.nextbreakpoint.flink.common.DoubleGauge;
import com.nextbreakpoint.flink.common.FlinkUtil;
import com.nextbreakpoint.flink.common.StreamJob;
import com.nextbreakpoint.flink.sensor.SensorData;
import com.nextbreakpoint.flink.sensor.SensorDeserializationSchema;
import com.nextbreakpoint.flink.sensor.SensorSerializationSchema;
import com.nextbreakpoint.flink.sensor.SensorTimestampExtractor;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.MetricGroup;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.windowing.RichWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.nextbreakpoint.flink.common.Constants.BUCKET_BASE_PATH;
import static com.nextbreakpoint.flink.common.Constants.CONSUMER_GROUP_NAME;
import static com.nextbreakpoint.flink.common.Constants.SOURCE_TOPIC_NAME;
import static com.nextbreakpoint.flink.common.Constants.TARGET_TOPIC_NAME;
import static com.nextbreakpoint.flink.common.FlinkUtil.createKafkaSource;
import static java.lang.Integer.valueOf;

public class AggregateJob extends StreamJob {
    private final DataStream<SensorData> source;
    private final List<SinkFunction<SensorData>> sinks;

    public AggregateJob(
            StreamExecutionEnvironment env,
            String basePath,
            DataStream<SensorData> source,
            List<SinkFunction<SensorData>> sinks) {
        super(env, basePath);
        this.source = source;
        this.sinks = sinks;
    }

    public static void main(String[] args) throws Exception {
        final ParameterTool parameters = ParameterTool.fromArgs(args);

        final String bucketBasePath = getNonNullableParam(parameters, BUCKET_BASE_PATH);

        final String sourceTopicName = getNonNullableParam(parameters, SOURCE_TOPIC_NAME);

        final String targetTopicName = getNonNullableParam(parameters, TARGET_TOPIC_NAME);

        final String consumerGroupName = getNonNullableParam(parameters, CONSUMER_GROUP_NAME);

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        final DataStreamSource<SensorData> source = env.addSource(createKafkaSource(parameters, sourceTopicName, new SensorDeserializationSchema(), consumerGroupName, "latest"));

        final List<SinkFunction<SensorData>> sinks = new ArrayList<>();

        sinks.add(FlinkUtil.createKafkaSink(parameters, targetTopicName, new SensorSerializationSchema(targetTopicName)));

        final AggregateJob job = new AggregateJob(env, bucketBasePath, source, sinks);

        job.enableCheckpointing(600000);

        job.disableRestart();

        job.transform().run();
    }

    @Override
    public StreamJob transform() {
        final DataStream<SensorData> stream = source
                .assignTimestampsAndWatermarks(new SensorTimestampExtractor(Time.seconds(10)))
                .name("source")
                .keyBy(SensorData::getId)
                .window(SlidingEventTimeWindows.of(Time.seconds(60), Time.seconds(1)))
                .aggregate(getAggregateFunction(), getWindowFunction())
                .name("window");

        IntStream.range(0, sinks.size()).forEach(i -> stream.addSink(sinks.get(i)));

        return this;
    }

    private WindowFunction<Double, SensorData, String, TimeWindow> getWindowFunction() {
        return new SensorRichWindowFunction();
    }

    private AggregateFunction<SensorData, Double, Double> getAggregateFunction() {
        return new SensorAggregateFunction();
    }

    private static class SensorAggregateFunction implements AggregateFunction<SensorData, Double, Double> {
        @Override
        public Double createAccumulator() {
            return Double.MIN_VALUE;
        }

        @Override
        public Double add(SensorData message, Double aDouble) {
            return aDouble < message.getValue() ? message.getValue() : aDouble;
        }

        @Override
        public Double getResult(Double aDouble) {
            return aDouble;
        }

        @Override
        public Double merge(Double aDouble0, Double aDouble1) {
            return aDouble0 < aDouble1 ? aDouble1 : aDouble0;
        }
    }

    private static class SensorRichWindowFunction extends RichWindowFunction<Double, SensorData, String, TimeWindow> {
        private Logger log = LoggerFactory.getLogger(SensorRichWindowFunction.class);

        private Map<String, Counter> counters = new HashMap<>();
        private Map<String, DoubleGauge> gauges = new HashMap<>();

        private String counterPrefix;
        private String gaugePrefix;

        public void open(Configuration parameters) throws Exception {
            counterPrefix = "counter." + getRuntimeContext().getIndexOfThisSubtask() + ".";
            gaugePrefix = "gauge." + getRuntimeContext().getIndexOfThisSubtask() + ".";
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
                out.collect(new SensorData(key, value, ISODateTimeFormat.dateTime().print(window.maxTimestamp())));
            });
        }
    }
}
