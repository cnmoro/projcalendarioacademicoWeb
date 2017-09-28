package calendarioacademico.servicos;

import calendarioacademico.commons.Evento;
import calendarioacademico.utils.EManager;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.FileUploadEvent;
import org.apache.commons.io.IOUtils;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

/**
 *
 * @author moro
 */
@ManagedBean
@ViewScoped
public class CalendarioManager implements Serializable {

    public final static long DUAS_HORAS = 2 * 60 * 60 * 1000L;

    private Evento novoEvento = new Evento();
    private Evento eventoSelecionado = new Evento();

    private List<Evento> eventos;
    private byte[] fileContent;

    private ScheduleModel eventModel;
    private ScheduleEvent event = new DefaultScheduleEvent();

    private MapModel mapModel;

    private boolean permiteAdicionar = true;

    private String mapBounds = "-25.4322517, -49.2672195";

    @PostConstruct
    public void init() {
        this.eventModel = new DefaultScheduleModel();
        this.mapModel = new DefaultMapModel();
        atualizaEventos();
    }

    public void atualizaEventos() {
        this.eventos = EManager.getInstance().createNamedQuery("Evento.findAll").getResultList();
        this.eventModel.clear();
        for (int i = 0; i < eventos.size(); i++) {
            eventModel.addEvent(new DefaultScheduleEvent(eventos.get(i).getNome(), eventos.get(i).getDatainicio(), eventos.get(i).getDatafim(), eventos.get(i)));
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            this.fileContent = IOUtils.toByteArray(event.getFile().getInputstream());
            if (this.fileContent.length > 0) {
                this.novoEvento.setDocumento(this.fileContent);
                this.eventoSelecionado.setDocumento(this.fileContent);
                popupMessageDocumentoUpload();
            }
        } catch (IOException e) {
            System.out.println("Ocorreu uma exceção do tipo IOException:" + e);
        }
    }

    public void onEventSelect(SelectEvent selectEvent) {
        this.mapModel.getMarkers().clear();
        this.mapBounds = "-25.4322517, -49.2672195";
        ScheduleEvent event = (ScheduleEvent) selectEvent.getObject();
        this.eventoSelecionado = (Evento) event.getData();
        if (this.eventoSelecionado.getLat() != null && this.eventoSelecionado.getLon() != null) {
            LatLng coordenada = new LatLng(this.eventoSelecionado.getLat(), this.eventoSelecionado.getLon());
            this.mapModel.addOverlay(new Marker(coordenada, this.eventoSelecionado.getNome()));
            this.mapBounds = coordenada.getLat() + ", " + coordenada.getLng();
        }
    }

    public void onDateSelect(SelectEvent selectEvent) {
        event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
        this.novoEvento.setDatainicio(event.getStartDate());
        this.novoEvento.setDatafim(event.getEndDate());
    }

    public void addEvento() {
        this.novoEvento.setAutor("admin"); //Modificar apos construcao de opcao de login
        //add geocoder
        EManager.getInstance().getTransaction().begin();
        EManager.getInstance().persist(this.novoEvento);
        EManager.getInstance().getTransaction().commit();
        this.novoEvento = new Evento();
        popupMessageCadastrado();
        atualizaEventos();
    }

    public void modificaEvento() {
        Date horaAtual = new Date();
        if (Math.abs(horaAtual.getTime() - this.eventoSelecionado.getDatafim().getTime()) < DUAS_HORAS) {
            popupMessageDuasHoras();
        } else {
            this.eventoSelecionado.setAutor("admin");
            EManager.getInstance().getTransaction().begin();
            EManager.getInstance().merge(this.eventoSelecionado);
            EManager.getInstance().getTransaction().commit();
            this.eventoSelecionado = new Evento();
            popupMessageModificado();
            atualizaEventos();
        }
    }

    public void excluiEvento() {
        EManager.getInstance().getTransaction().begin();
        EManager.getInstance().remove(this.eventoSelecionado);
        EManager.getInstance().getTransaction().commit();
        this.eventoSelecionado = new Evento();
        popupMessageExcluido();
        atualizaEventos();
    }

    public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void popupMessageDocumentoUpload() {
        addMessage("Sucesso", "Documento carregado.");
    }

    public void popupMessageCadastrado() {
        addMessage("Sucesso", "Evento cadastrado.");
    }

    public void popupMessageDuasHoras() {
        addMessage("Erro", "Evento muito próximo para ser alterado.");
    }

    public void popupMessageExcluido() {
        addMessage("Sucesso", "Evento excluído.");
    }

    public void popupMessageModificado() {
        addMessage("Sucesso", "Evento modificado.");
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

    public Evento getEventoSelecionado() {
        return eventoSelecionado;
    }

    public void setEventoSelecionado(Evento eventoSelecionado) {
        this.eventoSelecionado = eventoSelecionado;
    }

    public MapModel getMapModel() {
        return mapModel;
    }

    public void setMapModel(MapModel mapModel) {
        this.mapModel = mapModel;
    }

    public String getMapBounds() {
        return mapBounds;
    }

    public void setMapBounds(String mapBounds) {
        this.mapBounds = mapBounds;
    }

}
