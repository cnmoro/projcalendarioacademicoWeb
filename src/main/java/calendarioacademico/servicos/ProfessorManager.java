package calendarioacademico.servicos;

import calendarioacademico.commons.Profatendimento;
import calendarioacademico.login.LoginBean;
import calendarioacademico.utils.EManager;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
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
import org.primefaces.model.map.DefaultMapModel;

/**
 *
 * @author moro
 */
@ManagedBean
@ViewScoped
public class ProfessorManager {
    private ScheduleModel eventModel;
    private ScheduleEvent event = new DefaultScheduleEvent();
    private boolean permiteAdicionar = true;
    private String dialogToShow = "dialogEventoModifica";
    private List<Profatendimento> atendimentos;
    private Profatendimento novoAtendimento = new Profatendimento();
    private Profatendimento atendimentoSelecionado = new Profatendimento();
    
    @PostConstruct
    public void init() {
        if (LoginBean.getNivelAcesso().equalsIgnoreCase("Professor")) {
            this.permiteAdicionar = true;
            this.dialogToShow = "dialogEventoModifica";
        } else {
            this.permiteAdicionar = false;
            this.dialogToShow = "dialogEventoVisualiza";
        }

        this.eventModel = new DefaultScheduleModel();
        atualizaHorarios();
    }
    
    public void atualizaHorarios() {
        this.eventModel = new DefaultScheduleModel();
        this.atendimentos = EManager.getInstance().createNamedQuery("Profatendimento.findAll").getResultList();
        this.eventModel.clear();
        for (int i = 0; i < atendimentos.size(); i++) {
            eventModel.addEvent(new DefaultScheduleEvent(atendimentos.get(i).getIdprofessor().getLogin(), atendimentos.get(i).getDatainicio(), atendimentos.get(i).getDatafim(), atendimentos.get(i)));
        }
    }
    
    public void onEventSelect(SelectEvent selectEvent) {
        ScheduleEvent event = (ScheduleEvent) selectEvent.getObject();
        this.atendimentoSelecionado = (Profatendimento) event.getData();
    }

    public void onDateSelect(SelectEvent selectEvent) {
        event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
        this.novoAtendimento.setDatainicio(event.getStartDate());
        this.novoAtendimento.setDatafim(event.getEndDate());
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

    public String getDialogToShow() {
        return dialogToShow;
    }

    public void setDialogToShow(String dialogToShow) {
        this.dialogToShow = dialogToShow;
    }

    public List<Profatendimento> getAtendimentos() {
        return atendimentos;
    }

    public void setAtendimentos(List<Profatendimento> atendimentos) {
        this.atendimentos = atendimentos;
    }

    public Profatendimento getNovoAtendimento() {
        return novoAtendimento;
    }

    public void setNovoAtendimento(Profatendimento novoAtendimento) {
        this.novoAtendimento = novoAtendimento;
    }

    public Profatendimento getAtendimentoSelecionado() {
        return atendimentoSelecionado;
    }

    public void setAtendimentoSelecionado(Profatendimento atendimentoSelecionado) {
        this.atendimentoSelecionado = atendimentoSelecionado;
    }
    
    
}
