/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calendarioacademico.commons;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author moro
 */
@Entity
@Table(name = "profatendimento")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Profatendimento.findAll", query = "SELECT p FROM Profatendimento p")
    , @NamedQuery(name = "Profatendimento.findById", query = "SELECT p FROM Profatendimento p WHERE p.id = :id")
    , @NamedQuery(name = "Profatendimento.findByDatainicio", query = "SELECT p FROM Profatendimento p WHERE p.datainicio = :datainicio")
    , @NamedQuery(name = "Profatendimento.findByDatafim", query = "SELECT p FROM Profatendimento p WHERE p.datafim = :datafim")})
public class Profatendimento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "datainicio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datainicio;
    @Basic(optional = false)
    @NotNull
    @Column(name = "datafim")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datafim;
    @JoinColumn(name = "idprofessor", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario idprofessor;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idprofatendimento")
    private Collection<Reuniaoprofessor> reuniaoprofessorCollection;

    public Profatendimento() {
    }

    public Profatendimento(Integer id) {
        this.id = id;
    }

    public Profatendimento(Integer id, Date datainicio, Date datafim) {
        this.id = id;
        this.datainicio = datainicio;
        this.datafim = datafim;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDatainicio() {
        return datainicio;
    }

    public void setDatainicio(Date datainicio) {
        this.datainicio = datainicio;
    }

    public Date getDatafim() {
        return datafim;
    }

    public void setDatafim(Date datafim) {
        this.datafim = datafim;
    }

    public Usuario getIdprofessor() {
        return idprofessor;
    }

    public void setIdprofessor(Usuario idprofessor) {
        this.idprofessor = idprofessor;
    }

    @XmlTransient
    public Collection<Reuniaoprofessor> getReuniaoprofessorCollection() {
        return reuniaoprofessorCollection;
    }

    public void setReuniaoprofessorCollection(Collection<Reuniaoprofessor> reuniaoprofessorCollection) {
        this.reuniaoprofessorCollection = reuniaoprofessorCollection;
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
        if (!(object instanceof Profatendimento)) {
            return false;
        }
        Profatendimento other = (Profatendimento) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "calendarioacademico.commons.Profatendimento[ id=" + id + " ]";
    }
    
}
