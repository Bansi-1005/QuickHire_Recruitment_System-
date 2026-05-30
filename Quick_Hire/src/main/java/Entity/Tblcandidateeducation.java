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
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author tejan
 */
@Entity
@Table(name = "tblcandidateeducation")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tblcandidateeducation.findAll", query = "SELECT t FROM Tblcandidateeducation t"),
    @NamedQuery(name = "Tblcandidateeducation.findByCandidateEducationId", query = "SELECT t FROM Tblcandidateeducation t WHERE t.candidateEducationId = :candidateEducationId"),
    @NamedQuery(name = "Tblcandidateeducation.findByEducationName", query = "SELECT t FROM Tblcandidateeducation t WHERE t.educationName = :educationName"),
    @NamedQuery(name = "Tblcandidateeducation.findByInstituteName", query = "SELECT t FROM Tblcandidateeducation t WHERE t.instituteName = :instituteName"),
    @NamedQuery(name = "Tblcandidateeducation.findBySpecialization", query = "SELECT t FROM Tblcandidateeducation t WHERE t.specialization = :specialization"),
    @NamedQuery(name = "Tblcandidateeducation.findByStartYear", query = "SELECT t FROM Tblcandidateeducation t WHERE t.startYear = :startYear"),
    @NamedQuery(name = "Tblcandidateeducation.findByEndYear", query = "SELECT t FROM Tblcandidateeducation t WHERE t.endYear = :endYear"),
    @NamedQuery(name = "Tblcandidateeducation.findByPercentage", query = "SELECT t FROM Tblcandidateeducation t WHERE t.percentage = :percentage"),
    @NamedQuery(name = "Tblcandidateeducation.findByCgpa", query = "SELECT t FROM Tblcandidateeducation t WHERE t.cgpa = :cgpa"),
    @NamedQuery(name = "Tblcandidateeducation.findByGrade", query = "SELECT t FROM Tblcandidateeducation t WHERE t.grade = :grade"),
    @NamedQuery(name = "Tblcandidateeducation.findByCreatedDate", query = "SELECT t FROM Tblcandidateeducation t WHERE t.createdDate = :createdDate")})
public class Tblcandidateeducation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "candidateEducationId")
    private Integer candidateEducationId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "educationName")
    private String educationName;
    @Size(max = 200)
    @Column(name = "instituteName")
    private String instituteName;
    @Size(max = 150)
    @Column(name = "specialization")
    private String specialization;
    @Column(name = "startYear")
    @Temporal(TemporalType.DATE)
    private Date startYear;
    @Column(name = "endYear")
    @Temporal(TemporalType.DATE)
    private Date endYear;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "percentage")
    private BigDecimal percentage;
    @Column(name = "cgpa")
    private BigDecimal cgpa;
    @Size(max = 20)
    @Column(name = "grade")
    private String grade;
    @Lob
    @Size(max = 65535)
    @Column(name = "educationDescription")
    private String educationDescription;
    @Column(name = "createdDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @JoinColumn(name = "candidateId", referencedColumnName = "candidateId")
    @ManyToOne(optional = false)
    private Tblcandidates candidateId;

    public Tblcandidateeducation() {
    }

    public Tblcandidateeducation(Integer candidateEducationId) {
        this.candidateEducationId = candidateEducationId;
    }

    public Tblcandidateeducation(Integer candidateEducationId, String educationName) {
        this.candidateEducationId = candidateEducationId;
        this.educationName = educationName;
    }

    public Integer getCandidateEducationId() {
        return candidateEducationId;
    }

    public void setCandidateEducationId(Integer candidateEducationId) {
        this.candidateEducationId = candidateEducationId;
    }

    public String getEducationName() {
        return educationName;
    }

    public void setEducationName(String educationName) {
        this.educationName = educationName;
    }

    public String getInstituteName() {
        return instituteName;
    }

    public void setInstituteName(String instituteName) {
        this.instituteName = instituteName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Date getStartYear() {
        return startYear;
    }

    public void setStartYear(Date startYear) {
        this.startYear = startYear;
    }

    public Date getEndYear() {
        return endYear;
    }

    public void setEndYear(Date endYear) {
        this.endYear = endYear;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public BigDecimal getCgpa() {
        return cgpa;
    }

    public void setCgpa(BigDecimal cgpa) {
        this.cgpa = cgpa;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getEducationDescription() {
        return educationDescription;
    }

    public void setEducationDescription(String educationDescription) {
        this.educationDescription = educationDescription;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Tblcandidates getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Tblcandidates candidateId) {
        this.candidateId = candidateId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (candidateEducationId != null ? candidateEducationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tblcandidateeducation)) {
            return false;
        }
        Tblcandidateeducation other = (Tblcandidateeducation) object;
        if ((this.candidateEducationId == null && other.candidateEducationId != null) || (this.candidateEducationId != null && !this.candidateEducationId.equals(other.candidateEducationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Tblcandidateeducation[ candidateEducationId=" + candidateEducationId + " ]";
    }
    
}
