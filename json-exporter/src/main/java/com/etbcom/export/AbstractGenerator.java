package com.etbcom.export;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public abstract class AbstractGenerator {

    protected static final String LINE_SEPARATOR = System.lineSeparator();

    protected abstract String getProlog(String root);

    public abstract String convert(@NonNull Object o);

    protected abstract String endOfRoot(String root);

    public void saveToFile(@NonNull String fileName, @NonNull List<Object> objs, String root) throws ExportException {
        try {
            if (root != null) {
                root = root.toUpperCase();
            }
            StringBuilder sb = new StringBuilder(getProlog(root));
            sb.append(LINE_SEPARATOR);
            sb.append(objs.stream()
                    .map(this::convert)
                    .collect(Collectors.joining(",")))
                    .append(endOfRoot(root));

            Files.writeString(Paths.get(fileName), sb.toString(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn(e);
            throw new ExportException(e.getMessage());
        }
    }
}