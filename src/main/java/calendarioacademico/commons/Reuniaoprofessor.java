/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calendarioacademico.commons;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author moro
 */
@Entity
@Table(name = "reuniaoprofessor")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Reuniaoprofessor.findAll", query = "SELECT r FROM Reuniaoprofessor r")
    , @NamedQuery(name = "Reuniaoprofessor.findById", query = "SELECT r FROM Reuniaoprofessor r WHERE r.id = :id")
    , @NamedQuery(name = "Reuniaoprofessor.findByFeedback", query = "SELECT r FROM Reuniaoprofessor r WHERE r.feedback = :feedback")})
public class Reuniaoprofessor implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 500)
    @Column(name = "feedback")
    private String feedback;
    @JoinColumn(name = "idusuario", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario idusuario;
    @JoinColumn(name = "idprofatendimento", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Profatendimento idprofatendimento;

    public Reuniaoprofessor() {
    }

    public Reuniaoprofessor(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Usuario getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(Usuario idusuario) {
        this.idusuario = idusuario;
    }

    public Profatendimento getIdprofatendimento() {
        return idprofatendimento;
    }

    public void setIdprofatendimento(Profatendimento idprofatendimento) {
        this.idprofatendimento = idprofatendimento;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Reuniaoprofessor)) {
            return false;
        }
        Reuniaoprofessor other = (Reuniaoprofessor) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "calendarioacademico.commons.Reuniaoprofessor[ id=" + id + " ]";
    }
    
}
