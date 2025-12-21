package glz.hawk.codepoet.ddl;

import glz.hawk.codepoet.core.AbstractCodeWriter;
import glz.hawk.codepoet.ddl.dialect.DialectSupport;

import java.io.IOException;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

public class DatabaseCodeWriter extends AbstractCodeWriter<DatabaseCodeWriter> {
    public final DialectSupport dialectSupport;

    DatabaseCodeWriter(Appendable out, DialectSupport dialectSupport, String indent, String lineSeparator) {
        super(out, indent, lineSeparator);
        this.dialectSupport = argNotNull(dialectSupport, "dialectSupport");
    }

    public DatabaseCodeWriter emit(boolean booleanExpression, String format, Object... args) throws IOException {
        if (booleanExpression) {
            return emit(DatabaseCodeBlock.of(format, args));
        }
        return this;
    }

    public DatabaseCodeWriter emit(String format, Object... args) throws IOException {
        return emit(DatabaseCodeBlock.of(format, args));
    }

    public DatabaseCodeWriter emit(DatabaseCodeBlock codeBlock) throws IOException {
        int a = 0;
        for (String part : codeBlock.formatParts) {
            switch (part) {
                case "$L":
                case "$N":
                    emitAndIndent((String) codeBlock.args.get(a++));
                    break;
                case "$C":
                    emitAndIndent(stringLiteralWithSingleQuotes((String) codeBlock.args.get(a++)));
                    break;
                case "$D":
                    DataTypeSpec dataTypeSpec = (DataTypeSpec) (codeBlock.args.get(a++));
                    emitAndIndent(dialectSupport.getTypeName(dataTypeSpec));
                    break;
                case "$$":
                    emitAndIndent("$");
                    break;
                case "$>":
                    indent();
                    break;
                case "$<":
                    unindent();
                    break;
                case "$R":
                    emitNewLine();
                    break;
                default:
                    emitAndIndent(part);
                    break;
            }
        }
        return this;
    }

    String stringLiteralWithSingleQuotes(String value) {
        StringBuilder result = new StringBuilder(value.length() + 2);
        final char singleQuote = '\'';
        result.append(singleQuote);
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == singleQuote) {
                result.append(singleQuote).append(singleQuote);
            } else {
                result.append(c);
            }
        }
        result.append(singleQuote);
        return result.toString();
    }


}
