/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calendarioacademico.commons;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author moro
 */
@Entity
@Table(name = "loginscricao")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Loginscricao.findAll", query = "SELECT l FROM Loginscricao l")
    , @NamedQuery(name = "Loginscricao.findById", query = "SELECT l FROM Loginscricao l WHERE l.id = :id")
    , @NamedQuery(name = "Loginscricao.findByUsuario", query = "SELECT l FROM Loginscricao l WHERE l.usuario = :usuario")
    , @NamedQuery(name = "Loginscricao.findByModificacao", query = "SELECT l FROM Loginscricao l WHERE l.modificacao = :modificacao")
    , @NamedQuery(name = "Loginscricao.findByData", query = "SELECT l FROM Loginscricao l WHERE l.data = :data")})
public class Loginscricao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "usuario")
    private String usuario;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 250)
    @Column(name = "modificacao")
    private String modificacao;
    @Basic(optional = false)
    @NotNull
    @Column(name = "data")
    @Temporal(TemporalType.TIMESTAMP)
    private Date data;

    public Loginscricao() {
    }

    public Loginscricao(Integer id) {
        this.id = id;
    }

    public Loginscricao(Integer id, String usuario, String modificacao, Date data) {
        this.id = id;
        this.usuario = usuario;
        this.modificacao = modificacao;
        this.data = data;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getModificacao() {
        return modificacao;
    }

    public void setModificacao(String modificacao) {
        this.modificacao = modificacao;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
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
        if (!(object instanceof Loginscricao)) {
            return false;
        }
        Loginscricao other = (Loginscricao) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "calendarioacademico.commons.Loginscricao[ id=" + id + " ]";
    }
    
}
