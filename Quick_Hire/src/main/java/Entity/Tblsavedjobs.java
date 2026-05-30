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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author tejan
 */
@Entity
@Table(name = "tblsavedjobs")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tblsavedjobs.findAll", query = "SELECT t FROM Tblsavedjobs t"),
    @NamedQuery(name = "Tblsavedjobs.findBySavedJobId", query = "SELECT t FROM Tblsavedjobs t WHERE t.savedJobId = :savedJobId"),
    @NamedQuery(name = "Tblsavedjobs.findBySavedDate", query = "SELECT t FROM Tblsavedjobs t WHERE t.savedDate = :savedDate")})
public class Tblsavedjobs implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "savedJobId")
    private Integer savedJobId;
    @Column(name = "savedDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date savedDate;
    @JoinColumn(name = "candidateId", referencedColumnName = "candidateId")
    @ManyToOne(optional = false)
    private Tblcandidates candidateId;
    @JoinColumn(name = "jobId", referencedColumnName = "jobId")
    @ManyToOne(optional = false)
    private Tbljob jobId;

    public Tblsavedjobs() {
    }

    public Tblsavedjobs(Integer savedJobId) {
        this.savedJobId = savedJobId;
    }

    public Integer getSavedJobId() {
        return savedJobId;
    }

    public void setSavedJobId(Integer savedJobId) {
        this.savedJobId = savedJobId;
    }

    public Date getSavedDate() {
        return savedDate;
    }

    public void setSavedDate(Date savedDate) {
        this.savedDate = savedDate;
    }

    public Tblcandidates getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Tblcandidates candidateId) {
        this.candidateId = candidateId;
    }

    public Tbljob getJobId() {
        return jobId;
    }

    public void setJobId(Tbljob jobId) {
        this.jobId = jobId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (savedJobId != null ? savedJobId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tblsavedjobs)) {
            return false;
        }
        Tblsavedjobs other = (Tblsavedjobs) object;
        if ((this.savedJobId == null && other.savedJobId != null) || (this.savedJobId != null && !this.savedJobId.equals(other.savedJobId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Tblsavedjobs[ savedJobId=" + savedJobId + " ]";
    }
    
}
