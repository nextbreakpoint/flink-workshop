package com.nextbreakpoint.flink.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.flink.annotation.PublicEvolving;
import org.apache.flink.table.api.TableException;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.descriptors.DescriptorProperties;
import org.apache.flink.table.descriptors.FileSystemValidator;
import org.apache.flink.table.descriptors.OldCsvValidator;
import org.apache.flink.table.descriptors.SchemaValidator;
import org.apache.flink.table.factories.BatchTableSourceFactory;
import org.apache.flink.table.sources.BatchTableSource;
import org.apache.flink.types.Row;

@PublicEvolving
public class CsvBatchTableSourceFactory implements BatchTableSourceFactory<Row> {
    public CsvBatchTableSourceFactory() {
    }

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
        properties.add("format.line-delimiter");
        properties.add("format.quote-character");
        properties.add("format.comment-prefix");
        properties.add("format.ignore-first-line");
        properties.add("format.ignore-parse-errors");
        properties.add("connector.path");
        properties.add("schema.#.type");
        properties.add("schema.#.name");
        return properties;
    }

    protected CsvTableSource createTableSource(Boolean isStreaming, Map<String, String> properties) {
        DescriptorProperties params = new DescriptorProperties();
        params.putProperties(properties);
        (new FileSystemValidator()).validate(params);
        (new OldCsvValidator()).validate(params);
        (new SchemaValidator(isStreaming, false, false)).validate(params);
        CsvTableSource.Builder csvTableSourceBuilder = new CsvTableSource.Builder();
        TableSchema formatSchema = params.getTableSchema("format.fields");
        TableSchema tableSchema = params.getTableSchema("schema");
        if (!formatSchema.equals(tableSchema)) {
            throw new TableException("Encodings that differ from the schema are not supported yet for CsvTableSources.");
        } else {
            params.getOptionalString("connector.path").ifPresent(csvTableSourceBuilder::path);
            params.getOptionalString("format.field-delimiter").ifPresent(csvTableSourceBuilder::fieldDelimiter);
            params.getOptionalString("format.line-delimiter").ifPresent(csvTableSourceBuilder::lineDelimiter);

            for(int i = 0; i < formatSchema.getFieldCount(); ++i) {
                csvTableSourceBuilder.field(formatSchema.getFieldNames()[i], formatSchema.getFieldTypes()[i]);
            }

            params.getOptionalCharacter("format.quote-character").ifPresent(csvTableSourceBuilder::quoteCharacter);
            params.getOptionalString("format.comment-prefix").ifPresent(csvTableSourceBuilder::commentPrefix);
            params.getOptionalBoolean("format.ignore-first-line").ifPresent((flag) -> {
                if (flag) {
                    csvTableSourceBuilder.ignoreFirstLine();
                }

            });
            params.getOptionalBoolean("format.ignore-parse-errors").ifPresent((flag) -> {
                if (flag) {
                    csvTableSourceBuilder.ignoreParseErrors();
                }

            });
            return csvTableSourceBuilder.build();
        }
    }

    public BatchTableSource<Row> createBatchTableSource(Map<String, String> properties) {
        return this.createTableSource(false, properties);
    }
}
