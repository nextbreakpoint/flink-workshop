//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.nextbreakpoint.flink.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.io.RowCsvInputFormat;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.core.fs.FileInputSplit;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.functions.AsyncTableFunction;
import org.apache.flink.table.functions.FunctionContext;
import org.apache.flink.table.functions.TableFunction;
import org.apache.flink.table.sources.BatchTableSource;
import org.apache.flink.table.sources.LookupableTableSource;
import org.apache.flink.table.sources.ProjectableTableSource;
import org.apache.flink.table.sources.StreamTableSource;
import org.apache.flink.types.Row;

public class CsvTableSource implements StreamTableSource<Row>, BatchTableSource<Row>, LookupableTableSource<Row>, ProjectableTableSource<Row> {
    private final CsvTableSource.CsvInputFormatConfig config;

    public CsvTableSource(String path, String[] fieldNames, TypeInformation<?>[] fieldTypes) {
        this(path, fieldNames, fieldTypes, IntStream.range(0, fieldNames.length).toArray(), ",", "\n", (Character)null, false, (String)null, false);
    }

    public CsvTableSource(String path, String[] fieldNames, TypeInformation<?>[] fieldTypes, String fieldDelim, String lineDelim, Character quoteCharacter, boolean ignoreFirstLine, String ignoreComments, boolean lenient) {
        this(path, fieldNames, fieldTypes, IntStream.range(0, fieldNames.length).toArray(), fieldDelim, lineDelim, quoteCharacter, ignoreFirstLine, ignoreComments, lenient);
    }

    public CsvTableSource(String path, String[] fieldNames, TypeInformation<?>[] fieldTypes, int[] selectedFields, String fieldDelim, String lineDelim, Character quoteCharacter, boolean ignoreFirstLine, String ignoreComments, boolean lenient) {
        this(new CsvTableSource.CsvInputFormatConfig(path, fieldNames, fieldTypes, selectedFields, fieldDelim, lineDelim, quoteCharacter, ignoreFirstLine, ignoreComments, lenient));
    }

    private CsvTableSource(CsvTableSource.CsvInputFormatConfig config) {
        this.config = config;
    }

    public static CsvTableSource.Builder builder() {
        return new CsvTableSource.Builder();
    }

    public TypeInformation<Row> getReturnType() {
        return new RowTypeInfo(this.config.getSelectedFieldTypes(), this.config.getSelectedFieldNames());
    }

    public TableSchema getTableSchema() {
        return new TableSchema(this.config.fieldNames, this.config.fieldTypes);
    }

    public CsvTableSource projectFields(int[] fields) {
        if (fields.length == 0) {
            fields = new int[]{0};
        }

        return new CsvTableSource(this.config.select(fields));
    }

    public boolean isBounded() {
        return true;
    }

    public DataStream<Row> getDataStream(StreamExecutionEnvironment execEnv) {
        return execEnv.createInput(this.config.createInputFormat(), this.getReturnType()).name(this.explainSource());
    }

    public DataSet<Row> getDataSet(ExecutionEnvironment execEnv) {
        return execEnv.createInput(this.config.createInputFormat(), this.getReturnType()).name(this.explainSource());
    }

    public TableFunction<Row> getLookupFunction(String[] lookupKeys) {
        return new CsvTableSource.CsvLookupFunction(this.config, lookupKeys);
    }

    public AsyncTableFunction<Row> getAsyncLookupFunction(String[] lookupKeys) {
        throw new UnsupportedOperationException("CSV do not support async lookup");
    }

    public boolean isAsyncEnabled() {
        return false;
    }

    public String explainSource() {
        String[] fields = this.config.getSelectedFieldNames();
        return "CsvTableSource(read fields: " + String.join(", ", fields) + ")";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            CsvTableSource that = (CsvTableSource)o;
            return Objects.equals(this.config, that.config);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.config});
    }

    private static class CsvInputFormatConfig implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String path;
        private final String[] fieldNames;
        private final TypeInformation<?>[] fieldTypes;
        private final int[] selectedFields;
        private final String fieldDelim;
        private final String lineDelim;
        private final Character quoteCharacter;
        private final boolean ignoreFirstLine;
        private final String ignoreComments;
        private final boolean lenient;

        CsvInputFormatConfig(String path, String[] fieldNames, TypeInformation<?>[] fieldTypes, int[] selectedFields, String fieldDelim, String lineDelim, Character quoteCharacter, boolean ignoreFirstLine, String ignoreComments, boolean lenient) {
            this.path = path;
            this.fieldNames = fieldNames;
            this.fieldTypes = fieldTypes;
            this.selectedFields = selectedFields;
            this.fieldDelim = fieldDelim;
            this.lineDelim = lineDelim;
            this.quoteCharacter = quoteCharacter;
            this.ignoreFirstLine = ignoreFirstLine;
            this.ignoreComments = ignoreComments;
            this.lenient = lenient;
        }

        String[] getSelectedFieldNames() {
            String[] selectedFieldNames = new String[this.selectedFields.length];

            for(int i = 0; i < this.selectedFields.length; ++i) {
                selectedFieldNames[i] = this.fieldNames[this.selectedFields[i]];
            }

            return selectedFieldNames;
        }

        TypeInformation<?>[] getSelectedFieldTypes() {
            TypeInformation<?>[] selectedFieldTypes = new TypeInformation[this.selectedFields.length];

            for(int i = 0; i < this.selectedFields.length; ++i) {
                selectedFieldTypes[i] = this.fieldTypes[this.selectedFields[i]];
            }

            return selectedFieldTypes;
        }

        RowCsvInputFormat createInputFormat() {
            RowCsvInputFormat inputFormat = new RowCsvInputFormat(new Path(this.path), this.getSelectedFieldTypes(), this.lineDelim, this.fieldDelim, this.selectedFields);
            inputFormat.setSkipFirstLineAsHeader(this.ignoreFirstLine);
            inputFormat.setCommentPrefix(this.ignoreComments);
            inputFormat.setLenient(this.lenient);
            inputFormat.setNestedFileEnumeration(true);
            if (this.quoteCharacter != null) {
                inputFormat.enableQuotedStringParsing(this.quoteCharacter);
            }

            return inputFormat;
        }

        CsvTableSource.CsvInputFormatConfig select(int[] fields) {
            return new CsvTableSource.CsvInputFormatConfig(this.path, this.fieldNames, this.fieldTypes, fields, this.fieldDelim, this.lineDelim, this.quoteCharacter, this.ignoreFirstLine, this.ignoreComments, this.lenient);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o != null && this.getClass() == o.getClass()) {
                CsvTableSource.CsvInputFormatConfig that = (CsvTableSource.CsvInputFormatConfig)o;
                return this.ignoreFirstLine == that.ignoreFirstLine && this.lenient == that.lenient && Objects.equals(this.path, that.path) && Arrays.equals(this.fieldNames, that.fieldNames) && Arrays.equals(this.fieldTypes, that.fieldTypes) && Arrays.equals(this.selectedFields, that.selectedFields) && Objects.equals(this.fieldDelim, that.fieldDelim) && Objects.equals(this.lineDelim, that.lineDelim) && Objects.equals(this.quoteCharacter, that.quoteCharacter) && Objects.equals(this.ignoreComments, that.ignoreComments);
            } else {
                return false;
            }
        }

        public int hashCode() {
            int result = Objects.hash(new Object[]{this.path, this.fieldDelim, this.lineDelim, this.quoteCharacter, this.ignoreFirstLine, this.ignoreComments, this.lenient});
            result = 31 * result + Arrays.hashCode(this.fieldNames);
            result = 31 * result + Arrays.hashCode(this.fieldTypes);
            result = 31 * result + Arrays.hashCode(this.selectedFields);
            return result;
        }
    }

    public static class CsvLookupFunction extends TableFunction<Row> {
        private static final long serialVersionUID = 1L;
        private final CsvTableSource.CsvInputFormatConfig config;
        private final List<Integer> sourceKeys = new ArrayList();
        private final List<Integer> targetKeys = new ArrayList();
        private final Map<Object, List<Row>> dataMap = new HashMap();

        CsvLookupFunction(CsvTableSource.CsvInputFormatConfig config, String[] lookupKeys) {
            this.config = config;
            List<String> fields = Arrays.asList(config.getSelectedFieldNames());

            for(int i = 0; i < lookupKeys.length; ++i) {
                this.sourceKeys.add(i);
                int targetIdx = fields.indexOf(lookupKeys[i]);

                assert targetIdx != -1;

                this.targetKeys.add(targetIdx);
            }

        }

        public TypeInformation<Row> getResultType() {
            return new RowTypeInfo(this.config.getSelectedFieldTypes(), this.config.getSelectedFieldNames());
        }

        public void open(FunctionContext context) throws Exception {
            super.open(context);
            TypeInformation<Row> rowType = this.getResultType();
            RowCsvInputFormat inputFormat = this.config.createInputFormat();
            FileInputSplit[] inputSplits = inputFormat.createInputSplits(1);
            FileInputSplit[] var5 = inputSplits;
            int var6 = inputSplits.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                FileInputSplit split = var5[var7];
                inputFormat.open(split);
                Row row = new Row(rowType.getArity());

                while(true) {
                    Row r = (Row)inputFormat.nextRecord(row);
                    if (r == null) {
                        inputFormat.close();
                        break;
                    }

                    Object key = this.getTargetKey(r);
                    List<Row> rows = (List)this.dataMap.computeIfAbsent(key, (k) -> {
                        return new ArrayList();
                    });
                    rows.add(Row.copy(r));
                }
            }

        }

        public void eval(Object... values) {
            Object srcKey = this.getSourceKey(Row.of(values));
            if (this.dataMap.containsKey(srcKey)) {
                Iterator var3 = ((List)this.dataMap.get(srcKey)).iterator();

                while(var3.hasNext()) {
                    Row row1 = (Row)var3.next();
                    this.collect(row1);
                }
            }

        }

        private Object getSourceKey(Row source) {
            return this.getKey(source, this.sourceKeys);
        }

        private Object getTargetKey(Row target) {
            return this.getKey(target, this.targetKeys);
        }

        private Object getKey(Row input, List<Integer> keys) {
            if (keys.size() == 1) {
                int keyIdx = (Integer)keys.get(0);
                return input.getField(keyIdx) != null ? input.getField(keyIdx) : null;
            } else {
                Row key = new Row(keys.size());

                for(int i = 0; i < keys.size(); ++i) {
                    int keyIdx = (Integer)keys.get(i);
                    key.setField(i, input.getField(keyIdx));
                }

                return key;
            }
        }

        public void close() throws Exception {
            super.close();
        }
    }

    public static class Builder {
        private LinkedHashMap<String, TypeInformation<?>> schema = new LinkedHashMap();
        private Character quoteCharacter;
        private String path;
        private String fieldDelim = ",";
        private String lineDelim = "\n";
        private boolean isIgnoreFirstLine = false;
        private String commentPrefix;
        private boolean lenient = false;

        public Builder() {
        }

        public CsvTableSource.Builder path(String path) {
            this.path = path;
            return this;
        }

        public CsvTableSource.Builder fieldDelimiter(String delim) {
            this.fieldDelim = delim;
            return this;
        }

        public CsvTableSource.Builder lineDelimiter(String delim) {
            this.lineDelim = delim;
            return this;
        }

        public CsvTableSource.Builder field(String fieldName, TypeInformation<?> fieldType) {
            if (this.schema.containsKey(fieldName)) {
                throw new IllegalArgumentException("Duplicate field name " + fieldName);
            } else {
                this.schema.put(fieldName, fieldType);
                return this;
            }
        }

        public CsvTableSource.Builder quoteCharacter(Character quote) {
            this.quoteCharacter = quote;
            return this;
        }

        public CsvTableSource.Builder commentPrefix(String prefix) {
            this.commentPrefix = prefix;
            return this;
        }

        public CsvTableSource.Builder ignoreFirstLine() {
            this.isIgnoreFirstLine = true;
            return this;
        }

        public CsvTableSource.Builder ignoreParseErrors() {
            this.lenient = true;
            return this;
        }

        public CsvTableSource build() {
            if (this.path == null) {
                throw new IllegalArgumentException("Path must be defined.");
            } else if (this.schema.isEmpty()) {
                throw new IllegalArgumentException("Fields can not be empty.");
            } else {
                return new CsvTableSource(this.path, (String[])this.schema.keySet().toArray(new String[0]), (TypeInformation[])this.schema.values().toArray(new TypeInformation[0]), this.fieldDelim, this.lineDelim, this.quoteCharacter, this.isIgnoreFirstLine, this.commentPrefix, this.lenient);
            }
        }
    }
}
