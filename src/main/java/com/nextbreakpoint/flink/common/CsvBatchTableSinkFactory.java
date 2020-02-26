package com.nextbreakpoint.flink.common;

import org.apache.flink.annotation.PublicEvolving;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.table.api.TableException;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.descriptors.DescriptorProperties;
import org.apache.flink.table.descriptors.FileSystemValidator;
import org.apache.flink.table.descriptors.OldCsvValidator;
import org.apache.flink.table.descriptors.SchemaValidator;
import org.apache.flink.table.factories.BatchTableSinkFactory;
import org.apache.flink.table.sinks.BatchTableSink;
import org.apache.flink.types.Row;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PublicEvolving
public class CsvBatchTableSinkFactory implements BatchTableSinkFactory<Row> {
    public CsvBatchTableSinkFactory() {}

    public Map<String, String> requiredContext() {
        Map<String, String> context = new HashMap();
        context.put("connector.type", "filesystem");
        context.put("format.type", "csv");
        context.put("connector.property-version", "1");
        context.put("format.property-version", "1");
        return context;
    }

    public List<String> supportedProperties() {
        List<String> properties = new ArrayList();
        properties.add("connector.path");
        properties.add("format.fields.#.type");
        properties.add("format.fields.#.name");
        properties.add("format.field-delimiter");
        properties.add("connector.path");
        properties.add("schema.#.type");
        properties.add("schema.#.name");
        return properties;
    }

    public CsvTableSink createTableSink(Boolean isStreaming, Map<String, String> properties) {
        DescriptorProperties params = new DescriptorProperties();
        params.putProperties(properties);
        (new FileSystemValidator()).validate(params);
        (new OldCsvValidator()).validate(params);
        (new SchemaValidator(isStreaming, false, false)).validate(params);
        TableSchema formatSchema = params.getTableSchema("format.fields");
        TableSchema tableSchema = params.getTableSchema("schema");
        if (!formatSchema.equals(tableSchema)) {
            throw new TableException("Encodings that differ from the schema are not supported yet for CsvTableSink.");
        } else {
            String path = params.getString("connector.path");
            Integer parallelism = params.getOptionalInt("connector.parallelism").orElse(-1);
            String fieldDelimiter = params.getOptionalString("format.field-delimiter").orElse(",");
            CsvTableSink csvTableSink = new CsvTableSink(path, fieldDelimiter, parallelism, FileSystem.WriteMode.OVERWRITE);
            return (CsvTableSink)csvTableSink.configure(formatSchema.getFieldNames(), formatSchema.getFieldTypes());
        }
    }

    @Override
    public BatchTableSink<Row> createBatchTableSink(Map<String, String> properties) {
        return createTableSink(false, properties);
    }
}