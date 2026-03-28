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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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
@Table(name = "tblinterview")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tblinterview.findAll", query = "SELECT t FROM Tblinterview t"),
    @NamedQuery(name = "Tblinterview.findByInterviewId", query = "SELECT t FROM Tblinterview t WHERE t.interviewId = :interviewId"),
    @NamedQuery(name = "Tblinterview.findByInterviewDate", query = "SELECT t FROM Tblinterview t WHERE t.interviewDate = :interviewDate"),
    @NamedQuery(name = "Tblinterview.findByInterviewRound", query = "SELECT t FROM Tblinterview t WHERE t.interviewRound = :interviewRound"),
    @NamedQuery(name = "Tblinterview.findByInterviewerName", query = "SELECT t FROM Tblinterview t WHERE t.interviewerName = :interviewerName"),
    @NamedQuery(name = "Tblinterview.findByInterviewerMode", query = "SELECT t FROM Tblinterview t WHERE t.interviewerMode = :interviewerMode"),
    @NamedQuery(name = "Tblinterview.findByFeedback", query = "SELECT t FROM Tblinterview t WHERE t.feedback = :feedback"),
    @NamedQuery(name = "Tblinterview.findByResult", query = "SELECT t FROM Tblinterview t WHERE t.result = :result"),
    @NamedQuery(name = "Tblinterview.findByInterviewStatus", query = "SELECT t FROM Tblinterview t WHERE t.interviewStatus = :interviewStatus"),

    @NamedQuery(name = "Tblinterview.findByApplication", query = "SELECT t FROM Tblinterview t WHERE t.applicationId.applicationId = :applicationId")
})
public class Tblinterview implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "interviewId")
    private Integer interviewId;
    @Column(name = "interviewDate")
    @Temporal(TemporalType.DATE)
    private Date interviewDate;
    @Column(name = "interviewRound")
    private Integer interviewRound;
    @Size(max = 150)
    @Column(name = "interviewerName")
    private String interviewerName;
    @Size(max = 50)
    @Column(name = "interviewerMode")
    private String interviewerMode;
    @Size(max = 200)
    @Column(name = "feedback")
    private String feedback;
    @Size(max = 200)
    @Column(name = "result")
    private String result;
    @Size(max = 50)
    @Column(name = "interviewStatus")
    private String interviewStatus;
    @JoinTable(name = "tblinterview_users", joinColumns = {
        @JoinColumn(name = "interviewId", referencedColumnName = "interviewId")}, inverseJoinColumns = {
        @JoinColumn(name = "userId", referencedColumnName = "userId")})
    @ManyToMany
    private Collection<Tblusers> tblusersCollection;
    @JoinColumn(name = "applicationId", referencedColumnName = "applicationId")
    @ManyToOne
    private Tblapplication applicationId;

    public Tblinterview() {
    }

    public Tblinterview(Integer interviewId) {
        this.interviewId = interviewId;
    }

    public Integer getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Integer interviewId) {
        this.interviewId = interviewId;
    }

    public Date getInterviewDate() {
        return interviewDate;
    }

    public void setInterviewDate(Date interviewDate) {
        this.interviewDate = interviewDate;
    }

    public Integer getInterviewRound() {
        return interviewRound;
    }

    public void setInterviewRound(Integer interviewRound) {
        this.interviewRound = interviewRound;
    }

    public String getInterviewerName() {
        return interviewerName;
    }

    public void setInterviewerName(String interviewerName) {
        this.interviewerName = interviewerName;
    }

    public String getInterviewerMode() {
        return interviewerMode;
    }

    public void setInterviewerMode(String interviewerMode) {
        this.interviewerMode = interviewerMode;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getInterviewStatus() {
        return interviewStatus;
    }

    public void setInterviewStatus(String interviewStatus) {
        this.interviewStatus = interviewStatus;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<Tblusers> getTblusersCollection() {
        return tblusersCollection;
    }

    public void setTblusersCollection(Collection<Tblusers> tblusersCollection) {
        this.tblusersCollection = tblusersCollection;
    }

    public Tblapplication getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Tblapplication applicationId) {
        this.applicationId = applicationId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (interviewId != null ? interviewId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tblinterview)) {
            return false;
        }
        Tblinterview other = (Tblinterview) object;
        if ((this.interviewId == null && other.interviewId != null) || (this.interviewId != null && !this.interviewId.equals(other.interviewId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Tblinterview[ interviewId=" + interviewId + " ]";
    }
    
}
