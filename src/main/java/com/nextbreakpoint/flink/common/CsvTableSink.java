package com.nextbreakpoint.flink.common;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.operators.DataSink;
import org.apache.flink.api.java.operators.MapOperator;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.core.fs.FileSystem.WriteMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.table.sinks.AppendStreamTableSink;
import org.apache.flink.table.sinks.BatchTableSink;
import org.apache.flink.table.sinks.TableSink;
import org.apache.flink.table.utils.TableConnectorUtils;
import org.apache.flink.types.Row;

public class CsvTableSink implements BatchTableSink<Row>, AppendStreamTableSink<Row> {
    private String path;
    private String fieldDelim;
    private int numFiles;
    private WriteMode writeMode;
    private String[] fieldNames;
    private TypeInformation<?>[] fieldTypes;

    public CsvTableSink(String path, String fieldDelim, int numFiles, WriteMode writeMode) {
        this.numFiles = -1;
        this.path = path;
        this.fieldDelim = fieldDelim;
        this.numFiles = numFiles;
        this.writeMode = writeMode;
    }

    public CsvTableSink(String path) {
        this(path, ",");
    }

    public CsvTableSink(String path, String fieldDelim) {
        this(path, fieldDelim, -1, (WriteMode)null);
    }

    public void emitDataSet(DataSet<Row> dataSet) {
        MapOperator<Row, String> csvRows = dataSet.map(new CsvTableSink.CsvFormatter(this.fieldDelim == null ? "," : this.fieldDelim));
        DataSink sink;
        String filePath = this.numFiles <= 1 ? this.path  + "/1" : this.path;
        if (this.writeMode != null) {
            sink = csvRows.writeAsText(filePath, this.writeMode);
        } else {
            sink = csvRows.writeAsText(filePath);
        }

        if (this.numFiles > 0) {
            csvRows.setParallelism(this.numFiles);
            sink.setParallelism(this.numFiles);
        }

        sink.name(TableConnectorUtils.generateRuntimeName(CsvTableSink.class, this.fieldNames));
    }

    public DataStreamSink<?> consumeDataStream(DataStream<Row> dataStream) {
        SingleOutputStreamOperator<String> csvRows = dataStream.map(new CsvTableSink.CsvFormatter(this.fieldDelim == null ? "," : this.fieldDelim));
        DataStreamSink sink;
        String filePath = this.numFiles <= 1 ? this.path  + "/1" : this.path;
        if (this.writeMode != null) {
            sink = csvRows.writeAsText(filePath, this.writeMode);
        } else {
            sink = csvRows.writeAsText(filePath);
        }

        if (this.numFiles > 0) {
            csvRows.setParallelism(this.numFiles);
            sink.setParallelism(this.numFiles);
        } else {
            csvRows.setParallelism(dataStream.getParallelism());
            sink.setParallelism(dataStream.getParallelism());
        }

        sink.name(TableConnectorUtils.generateRuntimeName(CsvTableSink.class, this.fieldNames));
        return sink;
    }

    public void emitDataStream(DataStream<Row> dataStream) {
        this.consumeDataStream(dataStream);
    }

    public TableSink<Row> configure(String[] fieldNames, TypeInformation<?>[] fieldTypes) {
        CsvTableSink configuredSink = new CsvTableSink(this.path, this.fieldDelim, this.numFiles, this.writeMode);
        configuredSink.fieldNames = fieldNames;
        configuredSink.fieldTypes = fieldTypes;
        return configuredSink;
    }

    public TypeInformation<Row> getOutputType() {
        return new RowTypeInfo(this.getFieldTypes(), this.getFieldNames());
    }

    public String[] getFieldNames() {
        return this.fieldNames;
    }

    public TypeInformation<?>[] getFieldTypes() {
        return this.fieldTypes;
    }

    public static class CsvFormatter implements MapFunction<Row, String> {
        private static final long serialVersionUID = 1L;
        private final String fieldDelim;

        CsvFormatter(String fieldDelim) {
            this.fieldDelim = fieldDelim;
        }

        public String map(Row row) {
            StringBuilder builder = new StringBuilder();

            for(int i = 0; i < row.getArity(); ++i) {
                if (i > 0) {
                    builder.append(this.fieldDelim);
                }

                Object o;
                if ((o = row.getField(i)) != null) {
                    builder.append(o);
                }
            }

            return builder.toString();
        }
    }
}
