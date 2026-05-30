/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author RINKAL
 */
@Entity
@Table(name = "tbleducation")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tbleducation.findAll", query = "SELECT t FROM Tbleducation t"),
    @NamedQuery(name = "Tbleducation.findByEducationId", query = "SELECT t FROM Tbleducation t WHERE t.educationId = :educationId"),
    @NamedQuery(name = "Tbleducation.findByEducationName", query = "SELECT t FROM Tbleducation t WHERE t.educationName = :educationName"),
    @NamedQuery(name = "Tbleducation.findByCreatedDate", query = "SELECT t FROM Tbleducation t WHERE t.createdDate = :createdDate")})
public class Tbleducation implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "educationName")
    private String educationName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "createdDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "educationId")
    private Integer educationId;
    @ManyToMany(mappedBy = "tbleducationCollection")
    private Collection<Tbljob> tbljobCollection;


    public Tbleducation() {
    }

    public Tbleducation(Integer educationId) {
        this.educationId = educationId;
    }

    public Tbleducation(Integer educationId, String educationName, Date createdDate) {
        this.educationId = educationId;
        this.educationName = educationName;
        this.createdDate = createdDate;
    }

    public Integer getEducationId() {
        return educationId;
    }

    public void setEducationId(Integer educationId) {
        this.educationId = educationId;
    }


    @XmlTransient
    public Collection<Tbljob> getTbljobCollection() {
        return tbljobCollection;
    }

    public void setTbljobCollection(Collection<Tbljob> tbljobCollection) {
        this.tbljobCollection = tbljobCollection;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (educationId != null ? educationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tbleducation)) {
            return false;
        }
        Tbleducation other = (Tbleducation) object;
        if ((this.educationId == null && other.educationId != null) || (this.educationId != null && !this.educationId.equals(other.educationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Tbleducation[ educationId=" + educationId + " ]";
    }

    public String getEducationName() {
        return educationName;
    }

    public void setEducationName(String educationName) {
        this.educationName = educationName;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    
}
