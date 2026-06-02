/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entity;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
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
    @NamedQuery(name = "Tblcandidates.findByCandidateDOB", query = "SELECT t FROM Tblcandidates t WHERE t.candidateDOB = :candidateDOB"),
    @NamedQuery(name = "Tblcandidates.findByCandidateGender", query = "SELECT t FROM Tblcandidates t WHERE t.candidateGender = :candidateGender"),
    @NamedQuery(name = "Tblcandidates.findByCandidateExperience", query = "SELECT t FROM Tblcandidates t WHERE t.candidateExperience = :candidateExperience"),

    @NamedQuery(name = "Tblcandidates.findByUser", query = "SELECT t FROM Tblcandidates t WHERE t.userId.userId = :userId"),
    @NamedQuery(name="Tblcandidates.count", query="SELECT COUNT(t) FROM Tblcandidates t")
})
public class Tblcandidates implements Serializable {

    @Size(max = 15)
    @Column(name = "candidatePhone")
    private String candidatePhone;
    @Size(max = 20)
    @Column(name = "candidateGender")
    private String candidateGender;
    @Size(max = 100)
    @Column(name = "candidateArea")
    private String candidateArea;
    @Size(max = 100)
    @Column(name = "candidateCity")
    private String candidateCity;
    @Size(max = 100)
    @Column(name = "candidateState")
    private String candidateState;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "candidateId")
    private Collection<Tblcandidateeducation> tblcandidateeducationCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "candidateId")
    private Collection<Tblsavedjobs> tblsavedjobsCollection;
    @OneToMany(mappedBy = "candidateId")
    private Collection<Tblresume> tblresumeCollection;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "candidateId")
    private Integer candidateId;
    @Column(name = "candidateDOB")
    @Temporal(TemporalType.DATE)
    private Date candidateDOB;
    @Column(name = "candidateExperience")
    private Integer candidateExperience;
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

    public Integer getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Integer candidateId) {
        this.candidateId = candidateId;
    }

    public Date getCandidateDOB() {
        return candidateDOB;
    }

    public void setCandidateDOB(Date candidateDOB) {
        this.candidateDOB = candidateDOB;
    }

    public Integer getCandidateExperience() {
        return candidateExperience;
    }

    public void setCandidateExperience(Integer candidateExperience) {
        this.candidateExperience = candidateExperience;
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
    
    @XmlTransient
    @JsonbTransient
    public Collection<Tblresume> getTblresumeCollection() {
        return tblresumeCollection;
    }
    public void setTblresumeCollection(Collection<Tblresume> tblresumeCollection) {
        this.tblresumeCollection = tblresumeCollection;
    }
    
    @XmlTransient
    @JsonbTransient
    public Collection<Tblsavedjobs> getTblsavedjobsCollection() {
        return tblsavedjobsCollection;
    }
    public void setTblsavedjobsCollection(Collection<Tblsavedjobs> tblsavedjobsCollection) {
        this.tblsavedjobsCollection = tblsavedjobsCollection;
    }

    public String getCandidatePhone() {
        return candidatePhone;
    }

    public void setCandidatePhone(String candidatePhone) {
        this.candidatePhone = candidatePhone;
    }

    public String getCandidateGender() {
        return candidateGender;
    }

    public void setCandidateGender(String candidateGender) {
        this.candidateGender = candidateGender;
    }

    public String getCandidateArea() {
        return candidateArea;
    }

    public void setCandidateArea(String candidateArea) {
        this.candidateArea = candidateArea;
    }

    public String getCandidateCity() {
        return candidateCity;
    }

    public void setCandidateCity(String candidateCity) {
        this.candidateCity = candidateCity;
    }

    public String getCandidateState() {
        return candidateState;
    }

    public void setCandidateState(String candidateState) {
        this.candidateState = candidateState;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<Tblcandidateeducation> getTblcandidateeducationCollection() {
        return tblcandidateeducationCollection;
    }

    public void setTblcandidateeducationCollection(Collection<Tblcandidateeducation> tblcandidateeducationCollection) {
        this.tblcandidateeducationCollection = tblcandidateeducationCollection;
    }
    
}
