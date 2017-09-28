package calendarioacademico.servicos;

import calendarioacademico.commons.Evento;
import calendarioacademico.utils.EManager;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.FileUploadEvent;
import org.apache.commons.io.IOUtils;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

/**
 *
 * @author moro
 */
@ManagedBean
@ViewScoped
public class CalendarioManager implements Serializable {

    private Evento novoEvento = new Evento();

    private List<Evento> eventos;
    private byte[] fileContent;

    private ScheduleModel eventModel;
    private ScheduleEvent event = new DefaultScheduleEvent();

    private boolean permiteAdicionar = true;

    @PostConstruct
    public void init() {
        this.eventos = EManager.getInstance().createNamedQuery("Evento.findAll").getResultList();
        eventModel = new DefaultScheduleModel();
        
        for (int i = 0; i < eventos.size(); i++) {
            eventModel.addEvent(new DefaultScheduleEvent(eventos.get(i).getNome(), eventos.get(i).getDatainicio(), eventos.get(i).getDatafim(), eventos.get(i)));
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            this.fileContent = IOUtils.toByteArray(event.getFile().getInputstream());
            if (this.fileContent.length > 0) {
                this.novoEvento.setDocumento(this.fileContent);
                popupMessageDocumentoUpload();
            }
        } catch (IOException e) {
            System.out.println("Ocorreu uma exceção do tipo IOException:" + e);
        }
    }

    public void addEvento() {
        this.novoEvento.setAutor("admin"); //Modificar apos construcao de opcao de login
//        Map<String, Double> coords;
//        coords = OpenStreetMapUtils.getInstance().getCoordinates(this.novoEvento.getEndereco());
//        this.novoEvento.setLat(coords.get("lat"));
//        this.novoEvento.setLon(coords.get("lon"));
        EManager.getInstance().getTransaction().begin();
        EManager.getInstance().persist(this.novoEvento);
        EManager.getInstance().getTransaction().commit();
        this.novoEvento = new Evento();
    }

    public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void popupMessageDocumentoUpload() {
        addMessage("Sucesso", "Documento carregado.");
    }

    public Evento getNovoEvento() {
        return novoEvento;
    }

    public void setNovoEvento(Evento novoEvento) {
        this.novoEvento = novoEvento;
    }

    public List<Evento> getEventos() {
        return eventos;
    }

    public void setEventos(List<Evento> eventos) {
        this.eventos = eventos;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }

    public ScheduleModel getEventModel() {
        return eventModel;
    }

    public void setEventModel(ScheduleModel eventModel) {
        this.eventModel = eventModel;
    }

    public ScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }

    public boolean isPermiteAdicionar() {
        return permiteAdicionar;
    }

    public void setPermiteAdicionar(boolean permiteAdicionar) {
        this.permiteAdicionar = permiteAdicionar;
    }

}
