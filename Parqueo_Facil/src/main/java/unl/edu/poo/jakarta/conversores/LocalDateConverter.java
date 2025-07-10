package unl.edu.poo.jakarta.conversores;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@FacesConverter(forClass = LocalDate.class)
public class LocalDateConverter implements Converter<LocalDate> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LocalDate getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) return null;
        return LocalDate.parse(value, formatter);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, LocalDate value) {
        return value != null ? value.format(formatter) : "";
    }
}
