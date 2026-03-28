/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entity;

import jakarta.json.bind.annotation.JsonbTransient;
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
import jakarta.persistence.OneToMany;
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
 * @author tejan
 */
@Entity
@Table(name = "tblapplication")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tblapplication.findAll", query = "SELECT t FROM Tblapplication t"),
    @NamedQuery(name = "Tblapplication.findByApplicationId", query = "SELECT t FROM Tblapplication t WHERE t.applicationId = :applicationId"),
    @NamedQuery(name = "Tblapplication.findByApplicationAppliedDate", query = "SELECT t FROM Tblapplication t WHERE t.applicationAppliedDate = :applicationAppliedDate"),
    @NamedQuery(name = "Tblapplication.findByApplicationStatus", query = "SELECT t FROM Tblapplication t WHERE t.applicationStatus = :applicationStatus"),
    @NamedQuery(name = "Tblapplication.findByResumeSnapshot", query = "SELECT t FROM Tblapplication t WHERE t.resumeSnapshot = :resumeSnapshot"),
    @NamedQuery(name = "Tblapplication.findByLastUpdatedDate", query = "SELECT t FROM Tblapplication t WHERE t.lastUpdatedDate = :lastUpdatedDate"),

    @NamedQuery(name = "Tblapplication.findByCandidate", query = "SELECT t FROM Tblapplication t WHERE t.candidateId.candidateId = :candidateId"),
    @NamedQuery(name = "Tblapplication.findByJob", query = "SELECT t FROM Tblapplication t WHERE t.jobId.jobId = :jobId"),
    
    @NamedQuery(name="Tblapplication.count", query="SELECT COUNT(t) FROM Tblapplication t"),
    @NamedQuery(
        name = "Tblapplication.countByJob",
        query = "SELECT COUNT(a) FROM Tblapplication a WHERE a.jobId.jobId = :jobId"
    ),
    @NamedQuery(
        name = "Tblapplication.countSelected",
        query = "SELECT COUNT(a) FROM Tblapplication a WHERE a.applicationStatus = 'Selected'"
    ),
    
    @NamedQuery(
        name = "Tblapplication.findTopCandidates",
        query = "SELECT t FROM Tblapplication t JOIN t.tblscreeningscoreCollection s WHERE t.jobId.jobId = :jid ORDER BY s.matchingScore DESC"
    )
       
})
public class Tblapplication implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "applicationId")
    private Integer applicationId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "applicationAppliedDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date applicationAppliedDate;
    @Size(max = 50)
    @Column(name = "applicationStatus")
    private String applicationStatus;
    @Size(max = 50)
    @Column(name = "resumeSnapshot")
    private String resumeSnapshot;
    @Basic(optional = false)
    @NotNull
    @Column(name = "lastUpdatedDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdatedDate;
    @OneToMany(mappedBy = "applicationId")
    private Collection<Tblinterview> tblinterviewCollection;
    @OneToMany(mappedBy = "applicationId")
    private Collection<Tblapplicationstatushistory> tblapplicationstatushistoryCollection;
    @OneToMany(mappedBy = "applicationId")
    private Collection<Tblscreeningscore> tblscreeningscoreCollection;
    @JoinColumn(name = "candidateId", referencedColumnName = "candidateId")
    @ManyToOne
    private Tblcandidates candidateId;
    @JoinColumn(name = "jobId", referencedColumnName = "jobId")
    @ManyToOne
    private Tbljob jobId;

    public Tblapplication() {
    }

    public Tblapplication(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public Tblapplication(Integer applicationId, Date applicationAppliedDate, Date lastUpdatedDate) {
        this.applicationId = applicationId;
        this.applicationAppliedDate = applicationAppliedDate;
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public Date getApplicationAppliedDate() {
        return applicationAppliedDate;
    }

    public void setApplicationAppliedDate(Date applicationAppliedDate) {
        this.applicationAppliedDate = applicationAppliedDate;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(String applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public String getResumeSnapshot() {
        return resumeSnapshot;
    }

    public void setResumeSnapshot(String resumeSnapshot) {
        this.resumeSnapshot = resumeSnapshot;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<Tblinterview> getTblinterviewCollection() {
        return tblinterviewCollection;
    }

    public void setTblinterviewCollection(Collection<Tblinterview> tblinterviewCollection) {
        this.tblinterviewCollection = tblinterviewCollection;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<Tblapplicationstatushistory> getTblapplicationstatushistoryCollection() {
        return tblapplicationstatushistoryCollection;
    }

    public void setTblapplicationstatushistoryCollection(Collection<Tblapplicationstatushistory> tblapplicationstatushistoryCollection) {
        this.tblapplicationstatushistoryCollection = tblapplicationstatushistoryCollection;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<Tblscreeningscore> getTblscreeningscoreCollection() {
        return tblscreeningscoreCollection;
    }

    public void setTblscreeningscoreCollection(Collection<Tblscreeningscore> tblscreeningscoreCollection) {
        this.tblscreeningscoreCollection = tblscreeningscoreCollection;
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
        hash += (applicationId != null ? applicationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tblapplication)) {
            return false;
        }
        Tblapplication other = (Tblapplication) object;
        if ((this.applicationId == null && other.applicationId != null) || (this.applicationId != null && !this.applicationId.equals(other.applicationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Tblapplication[ applicationId=" + applicationId + " ]";
    }
    
}
