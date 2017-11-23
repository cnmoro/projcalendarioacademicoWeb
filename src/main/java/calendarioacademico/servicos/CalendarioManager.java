package calendarioacademico.servicos;

import calendarioacademico.commons.Evento;
import calendarioacademico.commons.Loginscricao;
import calendarioacademico.commons.Participacao;
import calendarioacademico.commons.Usuario;
import calendarioacademico.login.LoginBean;
import calendarioacademico.utils.EManager;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.FileUploadEvent;
import org.apache.commons.io.IOUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
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
    public final static long UM_DIA = 24 * 60 * 60 * 1000L;

    private Evento novoEvento = new Evento();
    private Evento eventoSelecionado = new Evento();

    private List<Evento> eventos;
    private byte[] fileContent;

    private ScheduleModel eventModel;
    private ScheduleEvent event = new DefaultScheduleEvent();

    private MapModel mapModel;

    private boolean permiteAdicionar = true;

    private boolean colaboradorSemanaAcademica = false;

    private String mapBounds = "-25.4322517, -49.2672195";

    private String dialogToShow = "dialogEventoModifica";

    @PostConstruct
    public void init() {
        if (LoginBean.getNivelAcesso().equalsIgnoreCase("Administrador")) {
            this.permiteAdicionar = true;
            this.dialogToShow = "dialogEventoModifica";
            this.colaboradorSemanaAcademica = false;
        } else if (LoginBean.getNivelAcesso().equalsIgnoreCase("Colaborador Semana Acadêmica")) {
            this.permiteAdicionar = true;
            this.dialogToShow = "dialogEventoModifica";
            this.colaboradorSemanaAcademica = true;
        } else {
            this.permiteAdicionar = false;
            this.dialogToShow = "dialogEventoVisualiza";
            this.colaboradorSemanaAcademica = false;
        }

        this.eventModel = new DefaultScheduleModel();
        this.mapModel = new DefaultMapModel();
        atualizaEventos();
    }

    public void atualizaEventos() {
        this.eventModel = new DefaultScheduleModel();
        this.mapModel = new DefaultMapModel();
        this.eventos = EManager.getInstance().createNamedQuery("Evento.findAll").getResultList();
        this.eventModel.clear();
        for (int i = 0; i < eventos.size(); i++) {
            eventModel.addEvent(new DefaultScheduleEvent(eventos.get(i).getNome(), eventos.get(i).getDatainicio(), eventos.get(i).getDatafim(), eventos.get(i)));
        }
    }
    
    public void refreshBySemanaAcademica() {
        this.eventModel = new DefaultScheduleModel();
        this.mapModel = new DefaultMapModel();
        this.eventos = EManager.getInstance().createNamedQuery("Evento.findByAutor").setParameter("autor", "Colaborador Semana Acadêmica").getResultList();
        this.eventModel.clear();
        for (int i = 0; i < eventos.size(); i++) {
            eventModel.addEvent(new DefaultScheduleEvent(eventos.get(i).getNome(), eventos.get(i).getDatainicio(), eventos.get(i).getDatafim(), eventos.get(i)));
        }
    }
    
    public void refreshByUniversidade() {
        this.eventModel = new DefaultScheduleModel();
        this.mapModel = new DefaultMapModel();
        this.eventos = EManager.getInstance().createNamedQuery("Evento.findByAutor").setParameter("autor", "Administrador").getResultList();
        this.eventModel.clear();
        for (int i = 0; i < eventos.size(); i++) {
            eventModel.addEvent(new DefaultScheduleEvent(eventos.get(i).getNome(), eventos.get(i).getDatainicio(), eventos.get(i).getDatafim(), eventos.get(i)));
        }
    }
    
    public void refreshByProfessor() {
        this.eventModel = new DefaultScheduleModel();
        this.mapModel = new DefaultMapModel();
        this.eventos = EManager.getInstance().createNamedQuery("Evento.findByAutor").setParameter("autor", "Professor").getResultList();
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
        if (this.colaboradorSemanaAcademica = true) {
            this.novoEvento.setSemanaacademica(true);
        }
    }
    
    public void confirmaPresenca() {
        Participacao p = new Participacao();
        Loginscricao log = new Loginscricao();
        p.setIdevento(this.eventoSelecionado);
        p.setIdusuario(LoginBean.getUsuarioAtual());
        log.setUsuario(LoginBean.getUsuarioAtual().getLogin());
        log.setData(new Date());
        log.setModificacao("Inscrito no evento '" + this.eventoSelecionado.getNome() + "'");
        EManager.getInstance().getTransaction().begin();
        EManager.getInstance().persist(p);
        EManager.getInstance().persist(log);
        EManager.getInstance().getTransaction().commit();
        popupMessageInscricao();
    }

    public void addEvento() {
        this.novoEvento.setAutor(LoginBean.getNivelAcesso());
        //TODO
        //ADD GEOCODER
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
            this.eventoSelecionado.setAutor(LoginBean.getNivelAcesso());
            //TODO
            //ADD GEOCODER
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

    public void avisaEventos() throws EmailException {
        try {
            List<Usuario> usuarios = EManager.getInstance().createNamedQuery("Usuario.findByNivelacesso").getResultList();
            List<String> emails = new ArrayList<>();

            for (int i = 0; i < usuarios.size(); i++) {
                emails.add(usuarios.get(i).getEmail());
            }

            Email email = new SimpleEmail();
            email.setHostName("smtp.gmail.com");
            email.setSmtpPort(587);
            email.setAuthenticator(new DefaultAuthenticator("calendarioeventosbsi@gmail.com", "disciplinabsi"));
            email.setTLS(true);
            email.setFrom("calendarioeventosbsi@gmail.com");
            email.setSubject("Calendário de Eventos - Aviso");

            StringBuilder sbuilder = new StringBuilder();
            sbuilder.append("Próximos eventos: ");
            sbuilder.append(System.getProperty("line.separator")).append(System.getProperty("line.separator"));

            Date horaAtual = new Date();
            for (int i = 0; i < eventos.size(); i++) {
                if (Math.abs(horaAtual.getTime() - eventos.get(i).getDatainicio().getTime()) <= UM_DIA) {
                    sbuilder.append(eventos.get(i).getNome()).append(", ").append(eventos.get(i).getHoras()).append(" horas complementares.");
                    sbuilder.append(System.getProperty("line.separator"));
                }
            }
            sbuilder.append(System.getProperty("line.separator")).append("Saiba mais em nosso site.");

            email.setMsg(sbuilder.toString());

            for (int i = 0; i < emails.size(); i++) {
                email.addTo(emails.get(i));
            }

            email.send();
            popupMessageSucessoBatchMail();
        } catch (Exception e) {
            popupMessageErroBatchMail();
        }

    }

    public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void popupMessageInscricao() {
        addMessage("Sucesso", "Incrição confirmada.");
    }
    
    public void popupMessageSucessoBatchMail() {
        addMessage("Sucesso", "Emails enviados.");
    }
    
    public void popupMessageErroBatchMail() {
        addMessage("Erro", "Problema ao enviar emails.");
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

    public String getDialogToShow() {
        return dialogToShow;
    }

    public void setDialogToShow(String dialogToShow) {
        this.dialogToShow = dialogToShow;
    }

    public boolean isColaboradorSemanaAcademica() {
        return colaboradorSemanaAcademica;
    }

    public void setColaboradorSemanaAcademica(boolean colaboradorSemanaAcademica) {
        this.colaboradorSemanaAcademica = colaboradorSemanaAcademica;
    }

}
