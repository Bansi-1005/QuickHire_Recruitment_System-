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
import jakarta.persistence.Lob;
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
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author tejan
 */
@Entity
@Table(name = "tbljob")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tbljob.findAll", query = "SELECT t FROM Tbljob t"),
    @NamedQuery(name = "Tbljob.findByJobId", query = "SELECT t FROM Tbljob t WHERE t.jobId = :jobId"),
    @NamedQuery(name = "Tbljob.findByJobTitle", query = "SELECT t FROM Tbljob t WHERE t.jobTitle = :jobTitle"),
    //@NamedQuery(name = "Tbljob.findByJobLocation", query = "SELECT t FROM Tbljob t WHERE t.jobLocation = :jobLocation"),
    @NamedQuery(name = "Tbljob.findByExperienceRequired", query = "SELECT t FROM Tbljob t WHERE t.experienceRequired = :experienceRequired"),
    @NamedQuery(name = "Tbljob.findByJobType", query = "SELECT t FROM Tbljob t WHERE t.jobType = :jobType"),
    @NamedQuery(name = "Tbljob.findByJobCompensationType", query = "SELECT t FROM Tbljob t WHERE t.jobCompensationType = :jobCompensationType"),
    @NamedQuery(name = "Tbljob.findByJobCompensationMin", query = "SELECT t FROM Tbljob t WHERE t.jobCompensationMin = :jobCompensationMin"),
    @NamedQuery(name = "Tbljob.findByJobCompensationMax", query = "SELECT t FROM Tbljob t WHERE t.jobCompensationMax = :jobCompensationMax"),
    @NamedQuery(name = "Tbljob.findByJobCompensationPeriod", query = "SELECT t FROM Tbljob t WHERE t.jobCompensationPeriod = :jobCompensationPeriod"),
    @NamedQuery(name = "Tbljob.findByJobVacancies", query = "SELECT t FROM Tbljob t WHERE t.jobVacancies = :jobVacancies"),
    @NamedQuery(name = "Tbljob.findByJobStatus", query = "SELECT t FROM Tbljob t WHERE t.jobStatus = :jobStatus"),
    @NamedQuery(name = "Tbljob.findByJobPostedDate", query = "SELECT t FROM Tbljob t WHERE t.jobPostedDate = :jobPostedDate"),
    @NamedQuery(name = "Tbljob.findByJobExpiryDate", query = "SELECT t FROM Tbljob t WHERE t.jobExpiryDate = :jobExpiryDate"),
    
    @NamedQuery(name = "Tbljob.findActiveJobs", query = "SELECT t FROM Tbljob t WHERE t.jobStatus = 'Open'"),
    @NamedQuery(name = "Tbljob.findByRecruiter", query = "SELECT t FROM Tbljob t WHERE t.recruiterId.recruiterId = :recruiterId"),
    @NamedQuery(name = "Tbljob.findByLocation", query = "SELECT t FROM Tbljob t WHERE t.jobLocation LIKE :jobLocation"),
    @NamedQuery(name = "Tbljob.findBySkill", query = "SELECT t FROM Tbljob t JOIN t.tblskillsCollection s WHERE s.skillName LIKE :skillName"),
    
    @NamedQuery(name="Tbljob.count", query="SELECT COUNT(t) FROM Tbljob t"),
    @NamedQuery(
        name = "Tbljob.findJobsByCompany",
        query = "SELECT j FROM Tbljob j JOIN j.recruiterId r JOIN r.companyId c WHERE c.companyId = :companyId"
    ),
    @NamedQuery(
        name = "Tbljob.jobWiseApplications",
        query = "SELECT j.jobTitle, COUNT(a) FROM Tbljob j LEFT JOIN j.tblapplicationCollection a GROUP BY j.jobTitle"
    )
})
public class Tbljob implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "jobId")
    private Integer jobId;
    @Size(max = 150)
    @Column(name = "jobTitle")
    private String jobTitle;
    @Lob
    @Size(max = 65535)
    @Column(name = "jobDescription")
    private String jobDescription;
    @Size(max = 255)
    @Column(name = "jobLocation")
    private String jobLocation;
    @Column(name = "experienceRequired")
    private Integer experienceRequired;
    @Size(max = 50)
    @Column(name = "jobType")
    private String jobType;
    @Size(max = 50)
    @Column(name = "jobCompensationType")
    private String jobCompensationType;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "jobCompensationMin")
    private BigDecimal jobCompensationMin;
    @Column(name = "jobCompensationMax")
    private BigDecimal jobCompensationMax;
    @Size(max = 50)
    @Column(name = "jobCompensationPeriod")
    private String jobCompensationPeriod;
    @Column(name = "jobVacancies")
    private Integer jobVacancies;
    @Size(max = 50)
    @Column(name = "jobStatus")
    private String jobStatus;
    @Basic(optional = false)
    @NotNull
    @Column(name = "jobPostedDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date jobPostedDate;
    @Column(name = "jobExpiryDate")
    @Temporal(TemporalType.DATE)
    private Date jobExpiryDate;
    @JoinTable(name = "tbljob_skills", joinColumns = {
        @JoinColumn(name = "jobId", referencedColumnName = "jobId")}, inverseJoinColumns = {
        @JoinColumn(name = "skillId", referencedColumnName = "skillId")})
    @ManyToMany
    private Collection<Tblskills> tblskillsCollection;
    @ManyToMany(mappedBy = "tbljobCollection")
    private Collection<Tblcandidates> tblcandidatesCollection;
    @JoinColumn(name = "recruiterId", referencedColumnName = "recruiterId")
    @ManyToOne
    private Tblrecruiters recruiterId;
    @OneToMany(mappedBy = "jobId")
    private Collection<Tblapplication> tblapplicationCollection;

    public Tbljob() {
    }

    public Tbljob(Integer jobId) {
        this.jobId = jobId;
    }

    public Tbljob(Integer jobId, Date jobPostedDate) {
        this.jobId = jobId;
        this.jobPostedDate = jobPostedDate;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getJobLocation() {
        return jobLocation;
    }

    public void setJobLocation(String jobLocation) {
        this.jobLocation = jobLocation;
    }

    public Integer getExperienceRequired() {
        return experienceRequired;
    }

    public void setExperienceRequired(Integer experienceRequired) {
        this.experienceRequired = experienceRequired;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getJobCompensationType() {
        return jobCompensationType;
    }

    public void setJobCompensationType(String jobCompensationType) {
        this.jobCompensationType = jobCompensationType;
    }

    public BigDecimal getJobCompensationMin() {
        return jobCompensationMin;
    }

    public void setJobCompensationMin(BigDecimal jobCompensationMin) {
        this.jobCompensationMin = jobCompensationMin;
    }

    public BigDecimal getJobCompensationMax() {
        return jobCompensationMax;
    }

    public void setJobCompensationMax(BigDecimal jobCompensationMax) {
        this.jobCompensationMax = jobCompensationMax;
    }

    public String getJobCompensationPeriod() {
        return jobCompensationPeriod;
    }

    public void setJobCompensationPeriod(String jobCompensationPeriod) {
        this.jobCompensationPeriod = jobCompensationPeriod;
    }

    public Integer getJobVacancies() {
        return jobVacancies;
    }

    public void setJobVacancies(Integer jobVacancies) {
        this.jobVacancies = jobVacancies;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public Date getJobPostedDate() {
        return jobPostedDate;
    }

    public void setJobPostedDate(Date jobPostedDate) {
        this.jobPostedDate = jobPostedDate;
    }

    public Date getJobExpiryDate() {
        return jobExpiryDate;
    }

    public void setJobExpiryDate(Date jobExpiryDate) {
        this.jobExpiryDate = jobExpiryDate;
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
    public Collection<Tblcandidates> getTblcandidatesCollection() {
        return tblcandidatesCollection;
    }

    public void setTblcandidatesCollection(Collection<Tblcandidates> tblcandidatesCollection) {
        this.tblcandidatesCollection = tblcandidatesCollection;
    }

    public Tblrecruiters getRecruiterId() {
        return recruiterId;
    }

    public void setRecruiterId(Tblrecruiters recruiterId) {
        this.recruiterId = recruiterId;
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
        hash += (jobId != null ? jobId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tbljob)) {
            return false;
        }
        Tbljob other = (Tbljob) object;
        if ((this.jobId == null && other.jobId != null) || (this.jobId != null && !this.jobId.equals(other.jobId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Tbljob[ jobId=" + jobId + " ]";
    }
    
}
