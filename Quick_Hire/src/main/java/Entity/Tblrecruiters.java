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
@Table(name = "tblrecruiters")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tblrecruiters.findAll", query = "SELECT t FROM Tblrecruiters t"),
    @NamedQuery(name = "Tblrecruiters.findByRecruiterId", query = "SELECT t FROM Tblrecruiters t WHERE t.recruiterId = :recruiterId"),
    @NamedQuery(name = "Tblrecruiters.findByDesignation", query = "SELECT t FROM Tblrecruiters t WHERE t.designation = :designation"),
    @NamedQuery(name = "Tblrecruiters.findByRecruiterPhone", query = "SELECT t FROM Tblrecruiters t WHERE t.recruiterPhone = :recruiterPhone"),
    @NamedQuery(name = "Tblrecruiters.findByRecruiterStatus", query = "SELECT t FROM Tblrecruiters t WHERE t.recruiterStatus = :recruiterStatus"),
    @NamedQuery(name = "Tblrecruiters.findByCreatedDate", query = "SELECT t FROM Tblrecruiters t WHERE t.createdDate = :createdDate"),

    @NamedQuery(
        name = "Tblrecruiters.findByUser",
        query = "SELECT t FROM Tblrecruiters t WHERE t.userId.userId = :userId"
    )
})
public class Tblrecruiters implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "recruiterId")
    private Integer recruiterId;
    @Size(max = 100)
    @Column(name = "designation")
    private String designation;
    @Size(max = 15)
    @Column(name = "recruiterPhone")
    private String recruiterPhone;
    @Size(max = 50)
    @Column(name = "recruiterStatus")
    private String recruiterStatus;
    @Basic(optional = false)
    @NotNull
    @Column(name = "createdDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @ManyToMany(mappedBy = "tblrecruitersCollection")
    private Collection<Tblcandidates> tblcandidatesCollection;
    @OneToMany(mappedBy = "recruiterId")
    private Collection<Tbljob> tbljobCollection;
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    @ManyToOne
    private Tblusers userId;
    @JoinColumn(name = "companyId", referencedColumnName = "companyId")
    @ManyToOne
    private Tblcompany companyId;

    public Tblrecruiters() {
    }

    public Tblrecruiters(Integer recruiterId) {
        this.recruiterId = recruiterId;
    }

    public Tblrecruiters(Integer recruiterId, Date createdDate) {
        this.recruiterId = recruiterId;
        this.createdDate = createdDate;
    }

    public Integer getRecruiterId() {
        return recruiterId;
    }

    public void setRecruiterId(Integer recruiterId) {
        this.recruiterId = recruiterId;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getRecruiterPhone() {
        return recruiterPhone;
    }

    public void setRecruiterPhone(String recruiterPhone) {
        this.recruiterPhone = recruiterPhone;
    }

    public String getRecruiterStatus() {
        return recruiterStatus;
    }

    public void setRecruiterStatus(String recruiterStatus) {
        this.recruiterStatus = recruiterStatus;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<Tblcandidates> getTblcandidatesCollection() {
        return tblcandidatesCollection;
    }

    public void setTblcandidatesCollection(Collection<Tblcandidates> tblcandidatesCollection) {
        this.tblcandidatesCollection = tblcandidatesCollection;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<Tbljob> getTbljobCollection() {
        return tbljobCollection;
    }

    public void setTbljobCollection(Collection<Tbljob> tbljobCollection) {
        this.tbljobCollection = tbljobCollection;
    }

    public Tblusers getUserId() {
        return userId;
    }

    public void setUserId(Tblusers userId) {
        this.userId = userId;
    }

    public Tblcompany getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Tblcompany companyId) {
        this.companyId = companyId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (recruiterId != null ? recruiterId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tblrecruiters)) {
            return false;
        }
        Tblrecruiters other = (Tblrecruiters) object;
        if ((this.recruiterId == null && other.recruiterId != null) || (this.recruiterId != null && !this.recruiterId.equals(other.recruiterId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Tblrecruiters[ recruiterId=" + recruiterId + " ]";
    }
    
}
