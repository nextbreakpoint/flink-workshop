package com.nextbreakpoint.flink.jobs.batch;

import com.nextbreakpoint.flink.common.CsvBatchTableSinkFactory;
import com.nextbreakpoint.flink.common.CsvBatchTableSourceFactory;
import com.nextbreakpoint.flink.common.Environment;
import com.nextbreakpoint.flink.sensor.SensorEvent;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.sinks.TableSink;
import org.apache.flink.table.sources.TableSource;
import org.apache.flink.types.Row;

import java.util.HashMap;
import java.util.Map;

import static com.nextbreakpoint.flink.common.Arguments.*;
import static com.nextbreakpoint.flink.common.Parameters.getOptionalParam;

public class ReportJob {
    public static void main(String[] args) throws Exception {
        final ParameterTool parameters = ParameterTool.fromArgs(args);

        final boolean localEnvironment = Boolean.parseBoolean(getOptionalParam(parameters, LOCAL_ENVIRONMENT, "false"));
        final boolean enabledWebui = Boolean.parseBoolean(getOptionalParam(parameters, ENABLE_WEBUI, "false"));
        final int restPort = Integer.parseInt(getOptionalParam(parameters, REST_PORT, "8081"));
        final int parallelism = Integer.parseInt(getOptionalParam(parameters, PARALLELISM, "0"));
        final String sourceFsPath = getOptionalParam(parameters, SOURCE_FS_PATH, "file:///tmp/workshop/sensor-events");
        final String outputFsPath = getOptionalParam(parameters, OUTPUT_FS_PATH, "file:///tmp/workshop/report-events");

        final ExecutionEnvironment environment = Environment.getExecutionEnvironment(localEnvironment, enabledWebui, restPort);

        environment.setParallelism(parallelism > 0 ? parallelism : environment.getParallelism());

        environment.setRestartStrategy(RestartStrategies.noRestart());

        final EnvironmentSettings settings = EnvironmentSettings.newInstance().useBlinkPlanner().inBatchMode().build();

        final TableEnvironment tableEnv = TableEnvironment.create(settings);

        final CsvBatchTableSourceFactory sourceFactory = new CsvBatchTableSourceFactory();

        final Map<String, String> sourceProperties = new HashMap<>();
        sourceProperties.put("format.record-class", SensorEvent.class.getName());
        sourceProperties.put("connector.path", sourceFsPath);
        sourceProperties.put("format.fields.0.name", "eventId");
        sourceProperties.put("format.fields.0.type", "STRING");
        sourceProperties.put("format.fields.1.name", "sensorId");
        sourceProperties.put("format.fields.1.type", "STRING");
        sourceProperties.put("format.fields.2.name", "temperature");
        sourceProperties.put("format.fields.2.type", "DOUBLE");
        sourceProperties.put("format.fields.3.name", "timestamp");
        sourceProperties.put("format.fields.3.type", "STRING");
        sourceProperties.put("schema.0.name", "eventId");
        sourceProperties.put("schema.0.type", "STRING");
        sourceProperties.put("schema.1.name", "sensorId");
        sourceProperties.put("schema.1.type", "STRING");
        sourceProperties.put("schema.2.name", "temperature");
        sourceProperties.put("schema.2.type", "DOUBLE");
        sourceProperties.put("schema.3.name", "timestamp");
        sourceProperties.put("schema.3.type", "STRING");
        sourceProperties.putAll(sourceFactory.requiredContext());
        final TableSource<Row> source = sourceFactory.createTableSource(sourceProperties);
        tableEnv.registerTableSource("sensor_events", source);

        final CsvBatchTableSinkFactory sinkFactory = new CsvBatchTableSinkFactory();

        final Map<String, String> sinkProperties = new HashMap<>();
        sinkProperties.put("connector.path", outputFsPath);
        sinkProperties.put("connector.parallelism", String.valueOf(parallelism));
        sinkProperties.put("format.fields.0.name", "eventId");
        sinkProperties.put("format.fields.0.type", "STRING");
        sinkProperties.put("format.fields.1.name", "sensorId");
        sinkProperties.put("format.fields.1.type", "STRING");
        sinkProperties.put("format.fields.2.name", "temperature");
        sinkProperties.put("format.fields.2.type", "DOUBLE");
        sinkProperties.put("format.fields.3.name", "timestamp");
        sinkProperties.put("format.fields.3.type", "STRING");
        sinkProperties.put("schema.0.name", "eventId");
        sinkProperties.put("schema.0.type", "STRING");
        sinkProperties.put("schema.1.name", "sensorId");
        sinkProperties.put("schema.1.type", "STRING");
        sinkProperties.put("schema.2.name", "temperature");
        sinkProperties.put("schema.2.type", "DOUBLE");
        sinkProperties.put("schema.3.name", "timestamp");
        sinkProperties.put("schema.3.type", "STRING");
        sinkProperties.putAll(sinkFactory.requiredContext());
        final TableSink<Row> sink = sinkFactory.createTableSink(sinkProperties);
        tableEnv.registerTableSink("report_events", sink);

        final Table result = tableEnv.sqlQuery("SELECT eventId, sensorId, temperature, `timestamp` FROM sensor_events WHERE temperature > 30");

        result.insertInto("report_events");

        tableEnv.execute("sensor-event-report-job");
    }
}
