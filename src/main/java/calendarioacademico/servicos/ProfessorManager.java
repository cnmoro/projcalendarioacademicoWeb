package calendarioacademico.servicos;

import calendarioacademico.commons.Usuario;
import calendarioacademico.utils.EManager;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author moro
 */
@ManagedBean
@ViewScoped
public class ProfessorManager implements Serializable {
    private List<Usuario> professores;
    
    @PostConstruct
    public void init() {
        this.professores = EManager.getInstance().createNamedQuery("Usuario.findProfessores").getResultList();
    }

    public List<Usuario> getProfessores() {
        return professores;
    }

    public void setProfessores(List<Usuario> professores) {
        this.professores = professores;
    }
    
}
