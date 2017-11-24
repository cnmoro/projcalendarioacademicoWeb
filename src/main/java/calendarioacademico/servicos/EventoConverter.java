package calendarioacademico.servicos;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import models.Evento;

@FacesConverter(value = "eventoConverter")
public class EventoConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        if (value != null && !value.isEmpty()) {
            return (Evento) uiComponent.getAttributes().get(value);
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
        if (value instanceof Evento) {
            Evento entity = (Evento) value;
            if (entity != null && entity instanceof Evento && entity.getId()!= null) {
                uiComponent.getAttributes().put(entity.getId().toString(), entity);
                return entity.getId().toString();
            }
        }
        return "";
    }

}

