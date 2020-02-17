package com.nextbreakpoint.flink.jobs.stream;

import com.nextbreakpoint.flink.common.*;
import com.nextbreakpoint.flink.sensor.*;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.sinks.CsvTableSink;
import org.apache.flink.table.sinks.TableSink;
import org.apache.flink.types.Row;

import java.util.Properties;

import static com.nextbreakpoint.flink.common.Arguments.*;
import static com.nextbreakpoint.flink.common.Parameters.getOptionalParam;

public class ArchiveJob {
    public static void main(String[] args) throws Exception {
        final ParameterTool parameters = ParameterTool.fromArgs(args);

        final boolean localEnvironment = Boolean.parseBoolean(getOptionalParam(parameters, LOCAL_ENVIRONMENT, "false"));
        final boolean enabledWebui = Boolean.parseBoolean(getOptionalParam(parameters, ENABLE_WEBUI, "false"));
        final int restPort = Integer.parseInt(getOptionalParam(parameters, REST_PORT, "8081"));
        final int parallelism = Integer.parseInt(getOptionalParam(parameters, PARALLELISM, "0"));
        final String bootstrapServers = getOptionalParam(parameters, BOOTSTRAP_SERVERS, "localhost:9093");
        final String sourceTopicName = getOptionalParam(parameters, SOURCE_TOPIC_NAME, "sensor-events");
        final String outputBucketName = getOptionalParam(parameters, OUTPUT_BUCKET_NAME, "file:///tmp/flink/sensor-events/csv");
        final String consumerGroupName = getOptionalParam(parameters, CONSUMER_GROUP_NAME, "sensor-event-archive-job");
        final String autoOffsetReset = getOptionalParam(parameters, AUTO_OFFSET_RESET, "earliest");
        final boolean consoleOutput = Boolean.parseBoolean(getOptionalParam(parameters, CONSOLE_OUTPUT, "false"));

        final StreamExecutionEnvironment environment = Environment.getStreamExecutionEnvironment(localEnvironment, enabledWebui, restPort);

        environment.setParallelism(parallelism > 0 ? parallelism : environment.getParallelism());

        environment.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        environment.setRestartStrategy(RestartStrategies.noRestart());

        Environment.configureCheckpointing(environment, 60000, 20000);

        final EnvironmentSettings settings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();

        final StreamTableEnvironment tableEnv = StreamTableEnvironment.create(environment, settings);

        final RowTypeInfo rowType = new RowTypeInfo(
            new TypeInformation[] {
                TypeInformation.of(String.class),
                TypeInformation.of(String.class),
                TypeInformation.of(Double.class),
                TypeInformation.of(String.class)
            },
            new String[] {
                "eventId",
                "sensorId",
                "temperature",
                "timestamp"
            }
        );

        final KafkaDeserializationSchema<SensorEvent> deserializationSchema = new SensorEventDeserializationSchema();

        final Properties consumerProperties = Kafka.defaultConsumerProperties(bootstrapServers, consumerGroupName, autoOffsetReset);

        final SourceFunction<SensorEvent> kafkaSource = Sources.createKafkaSource(sourceTopicName, deserializationSchema, consumerProperties);

        final DataStream<Row> pipeline = environment.addSource(kafkaSource)
                .assignTimestampsAndWatermarks(new SensorEventTimestampExtractor(Time.milliseconds(500)))
                .uid("kafka-source")
                .name("kafka")
                .map(new SensorEventToRowFunctiom())
                .returns(rowType)
                .uid("rows-source")
                .name("rows");

        tableEnv.registerDataStream("sensor_events", pipeline);

        final CsvTableSink csvSink = new CsvTableSink(outputBucketName + "/archive", ",", 1, FileSystem.WriteMode.OVERWRITE);

        final TableSink<Row> tableSink = csvSink.configure(rowType.getFieldNames(), rowType.getFieldTypes());

        tableEnv.registerTableSink("output_sensor_events", tableSink);

        final Table result = tableEnv.sqlQuery("SELECT eventId, sensorId, temperature, `timestamp` FROM sensor_events");

        result.insertInto("output_sensor_events");

        if (consoleOutput) {
            pipeline.addSink(new PrintSinkFunction<>("event", false))
                    .uid("console-sink")
                    .name("console");
        }

        environment.execute("sensor-event-archive-job");
    }
}
