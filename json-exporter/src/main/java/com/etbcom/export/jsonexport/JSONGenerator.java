package com.etbcom.export.jsonexport;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.function.Failable;
import com.etbcom.export.AbstractGenerator;
import com.etbcom.export.ExportException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
public class JSONGenerator extends AbstractGenerator {

    @Override
    protected String getProlog(String root) {
        return root != null ? "{\"%s\": [".formatted(root.toLowerCase()) : "";
    }

    @Override
    protected String endOfRoot(String root) {
        return root != null ? "]}" : "";
    }

    @Override
    public String convert(@NonNull Object o) throws ExportException {
        try {
            StringBuilder sb = new StringBuilder();
            beforeProcess(o);
            process(o, sb);
            return sb.toString();
        } catch (Exception e) {
            log.warn(e);
            throw new ExportException(e.getMessage());
        }
    }

    protected void beforeProcess(@NonNull Object o) throws Exception {
        Class<?> clazz = o.getClass();
        Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(JSONInit.class))
                .toList()
                .forEach(Failable.asConsumer(m -> {
                    m.setAccessible(true);
                    m.invoke(o);
                }));
    }

    protected void process(@NonNull Object o, @NonNull StringBuilder sb) throws Exception {
        Class<?> clazz = o.getClass();
        JSONSerializable js = clazz.getAnnotation(JSONSerializable.class);
        if (js == null) {
            throw new ExportException("This class cannot be exported to JSON");
        }
        
        sb.append("{");
        
        AtomicBoolean first = new AtomicBoolean(true);
        
        getFields(clazz).stream()
                .filter(f -> f.isAnnotationPresent(JSONElement.class))
                .toList()
                .forEach(Failable.asConsumer(f -> {
                    if (!first.get()) {
                        sb.append(",");
                    } else {
                        first.set(false);
                    }
                    exportField(f, o, sb);
                }));
        
        sb.append("}");
    }

    protected List<Field> getFields(@NonNull Class<?> clazz) {
        List<Field> result = new ArrayList<>(Arrays.stream(clazz.getDeclaredFields())
                .toList());
        if (clazz.getSuperclass() != null && clazz.getSuperclass().isAnnotationPresent(JSONSerializable.class)) {
            result.addAll(getFields(clazz.getSuperclass()));
        }
        return result;
    }

    protected void exportField(@NonNull Field f, @NonNull Object o, @NonNull StringBuilder sb) throws Exception {
        f.setAccessible(true);
        Object value = f.get(o);
        
        JSONElement je = f.getAnnotation(JSONElement.class);
        String name = je.key().isEmpty() ? f.getName().toLowerCase() : je.key().toLowerCase();
        
        sb.append("\"").append(name).append("\":");
        
        if (value == null) {
            sb.append("null");
            return;
        }
        
        Class<?> valueClass = value.getClass();
        
        if (valueClass.isAnnotationPresent(JSONSerializable.class)) {
            StringBuilder innerSb = new StringBuilder();
            process(value, innerSb);
            sb.append(innerSb);
            return;
        }
        
        if (valueClass.isArray()) {
            sb.append("[");
            Object[] array = (Object[]) value;
            for (int i = 0; i < array.length; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                if (array[i] != null && array[i].getClass().isAnnotationPresent(JSONSerializable.class)) {
                    StringBuilder arraySb = new StringBuilder();
                    process(array[i], arraySb);
                    sb.append(arraySb);
                } else {
                    appendValue(array[i], sb);
                }
            }
            sb.append("]");
            return;
        }
        
        if (value instanceof Iterable<?> iterable) {
            sb.append("[");
            AtomicBoolean firstItem = new AtomicBoolean(true);
            iterable.forEach(Failable.asConsumer(item -> {
                if (!firstItem.get()) {
                    sb.append(",");
                } else {
                    firstItem.set(false);
                }
                
                if (item != null && item.getClass().isAnnotationPresent(JSONSerializable.class)) {
                    StringBuilder iterableSb = new StringBuilder();
                    process(item, iterableSb);
                    sb.append(iterableSb);
                } else {
                    appendValue(item, sb);
                }
            }));
            sb.append("]");
            return;
        }
        
        appendValue(value, sb);
    }
    
    private void appendValue(Object value, StringBuilder sb) {
        if (value == null) {
            sb.append("null");
        } else if (value instanceof Number || value instanceof Boolean) {
            sb.append(value);
        } else {
            sb.append("\"").append(escapeString(value.toString())).append("\"");
        }
    }
    
    private String escapeString(String input) {
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}