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
@Table(name = "tblcandidates")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tblcandidates.findAll", query = "SELECT t FROM Tblcandidates t"),
    @NamedQuery(name = "Tblcandidates.findByCandidateId", query = "SELECT t FROM Tblcandidates t WHERE t.candidateId = :candidateId"),
    @NamedQuery(name = "Tblcandidates.findByCandidatePhone", query = "SELECT t FROM Tblcandidates t WHERE t.candidatePhone = :candidatePhone"),
    @NamedQuery(name = "Tblcandidates.findByCandidateLocation", query = "SELECT t FROM Tblcandidates t WHERE t.candidateLocation = :candidateLocation"),
    @NamedQuery(name = "Tblcandidates.findByCandidateDOB", query = "SELECT t FROM Tblcandidates t WHERE t.candidateDOB = :candidateDOB"),
    @NamedQuery(name = "Tblcandidates.findByCandidateGender", query = "SELECT t FROM Tblcandidates t WHERE t.candidateGender = :candidateGender"),
    @NamedQuery(name = "Tblcandidates.findByCandidateExperience", query = "SELECT t FROM Tblcandidates t WHERE t.candidateExperience = :candidateExperience"),
    @NamedQuery(name = "Tblcandidates.findByCandidateResume", query = "SELECT t FROM Tblcandidates t WHERE t.candidateResume = :candidateResume"),
    @NamedQuery(name = "Tblcandidates.findByResumeUploadDate", query = "SELECT t FROM Tblcandidates t WHERE t.resumeUploadDate = :resumeUploadDate"),

    @NamedQuery(name = "Tblcandidates.findByUser", query = "SELECT t FROM Tblcandidates t WHERE t.userId.userId = :userId"),
    @NamedQuery(name="Tblcandidates.count", query="SELECT COUNT(t) FROM Tblcandidates t")
})
public class Tblcandidates implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "candidateId")
    private Integer candidateId;
    @Size(max = 15)
    @Column(name = "candidatePhone")
    private String candidatePhone;
    @Size(max = 100)
    @Column(name = "candidateLocation")
    private String candidateLocation;
    @Column(name = "candidateDOB")
    @Temporal(TemporalType.DATE)
    private Date candidateDOB;
    @Size(max = 20)
    @Column(name = "candidateGender")
    private String candidateGender;
    @Column(name = "candidateExperience")
    private Integer candidateExperience;
    @Size(max = 255)
    @Column(name = "candidateResume")
    private String candidateResume;
    @Basic(optional = true)
    @Column(name = "resumeUploadDate", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date resumeUploadDate;
    @JoinTable(name = "tblcandidate_skills", joinColumns = {
        @JoinColumn(name = "candidateId", referencedColumnName = "candidateId")}, inverseJoinColumns = {
        @JoinColumn(name = "skillId", referencedColumnName = "skillId")})
    @ManyToMany
    private Collection<Tblskills> tblskillsCollection;
    @JoinTable(name = "tblcandidate_job", joinColumns = {
        @JoinColumn(name = "candidateId", referencedColumnName = "candidateId")}, inverseJoinColumns = {
        @JoinColumn(name = "jobId", referencedColumnName = "jobId")})
    @ManyToMany
    private Collection<Tbljob> tbljobCollection;
    @JoinTable(name = "tblcandidate_recruiters", joinColumns = {
        @JoinColumn(name = "candidateId", referencedColumnName = "candidateId")}, inverseJoinColumns = {
        @JoinColumn(name = "recruiterId", referencedColumnName = "recruiterId")})
    @ManyToMany
    private Collection<Tblrecruiters> tblrecruitersCollection;
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    @ManyToOne
    private Tblusers userId;
    @OneToMany(mappedBy = "candidateId")
    private Collection<Tblapplication> tblapplicationCollection;

    public Tblcandidates() {
    }

    public Tblcandidates(Integer candidateId) {
        this.candidateId = candidateId;
    }

    public Tblcandidates(Integer candidateId, Date resumeUploadDate) {
        this.candidateId = candidateId;
        this.resumeUploadDate = resumeUploadDate;
    }

    public Integer getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Integer candidateId) {
        this.candidateId = candidateId;
    }

    public String getCandidatePhone() {
        return candidatePhone;
    }

    public void setCandidatePhone(String candidatePhone) {
        this.candidatePhone = candidatePhone;
    }

    public String getCandidateLocation() {
        return candidateLocation;
    }

    public void setCandidateLocation(String candidateLocation) {
        this.candidateLocation = candidateLocation;
    }

    public Date getCandidateDOB() {
        return candidateDOB;
    }

    public void setCandidateDOB(Date candidateDOB) {
        this.candidateDOB = candidateDOB;
    }

    public String getCandidateGender() {
        return candidateGender;
    }

    public void setCandidateGender(String candidateGender) {
        this.candidateGender = candidateGender;
    }

    public Integer getCandidateExperience() {
        return candidateExperience;
    }

    public void setCandidateExperience(Integer candidateExperience) {
        this.candidateExperience = candidateExperience;
    }

    public String getCandidateResume() {
        return candidateResume;
    }

    public void setCandidateResume(String candidateResume) {
        this.candidateResume = candidateResume;
    }

    public Date getResumeUploadDate() {
        return resumeUploadDate;
    }

    public void setResumeUploadDate(Date resumeUploadDate) {
        this.resumeUploadDate = resumeUploadDate;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<Tblskills> getTblskillsCollection() {
        return tblskillsCollection;
    }

    public void setTblskillsCollection(Collection<Tblskills> tblskillsCollection) {
        this.tblskillsCollection = tblskillsCollection;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<Tbljob> getTbljobCollection() {
        return tbljobCollection;
    }

    public void setTbljobCollection(Collection<Tbljob> tbljobCollection) {
        this.tbljobCollection = tbljobCollection;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<Tblrecruiters> getTblrecruitersCollection() {
        return tblrecruitersCollection;
    }

    public void setTblrecruitersCollection(Collection<Tblrecruiters> tblrecruitersCollection) {
        this.tblrecruitersCollection = tblrecruitersCollection;
    }

    public Tblusers getUserId() {
        return userId;
    }

    public void setUserId(Tblusers userId) {
        this.userId = userId;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<Tblapplication> getTblapplicationCollection() {
        return tblapplicationCollection;
    }

    public void setTblapplicationCollection(Collection<Tblapplication> tblapplicationCollection) {
        this.tblapplicationCollection = tblapplicationCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (candidateId != null ? candidateId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tblcandidates)) {
            return false;
        }
        Tblcandidates other = (Tblcandidates) object;
        if ((this.candidateId == null && other.candidateId != null) || (this.candidateId != null && !this.candidateId.equals(other.candidateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Tblcandidates[ candidateId=" + candidateId + " ]";
    }
    
}
