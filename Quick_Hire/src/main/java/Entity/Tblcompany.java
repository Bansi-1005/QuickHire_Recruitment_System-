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
@Table(name = "tblcompany")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tblcompany.findAll", query = "SELECT t FROM Tblcompany t"),
    @NamedQuery(name = "Tblcompany.findByCompanyId", query = "SELECT t FROM Tblcompany t WHERE t.companyId = :companyId"),
    @NamedQuery(name = "Tblcompany.findByCompanyName", query = "SELECT t FROM Tblcompany t WHERE t.companyName = :companyName"),
    @NamedQuery(name = "Tblcompany.findByCompanyLocation", query = "SELECT t FROM Tblcompany t WHERE t.companyLocation = :companyLocation"),
    @NamedQuery(name = "Tblcompany.findByCompanyEmail", query = "SELECT t FROM Tblcompany t WHERE t.companyEmail = :companyEmail"),
    @NamedQuery(name = "Tblcompany.findByCompanyWebsite", query = "SELECT t FROM Tblcompany t WHERE t.companyWebsite = :companyWebsite"),
    @NamedQuery(name = "Tblcompany.findByCompanyStatus", query = "SELECT t FROM Tblcompany t WHERE t.companyStatus = :companyStatus"),
    @NamedQuery(name = "Tblcompany.findByCreatedDate", query = "SELECT t FROM Tblcompany t WHERE t.createdDate = :createdDate"),

    @NamedQuery(name="Tblcompany.count", query="SELECT COUNT(t) FROM Tblcompany t"),
    @NamedQuery(
    name = "Tblcompany.findByRecruiterId",
    query = "SELECT r.companyId FROM Tblrecruiters r WHERE r.recruiterId = :recruiterId")
})
public class Tblcompany implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "companyId")
    private Integer companyId;
    @Size(max = 200)
    @Column(name = "companyName")
    private String companyName;
    @Size(max = 500)
    @Column(name = "companyLocation")
    private String companyLocation;
    @Size(max = 150)
    @Column(name = "companyEmail")
    private String companyEmail;
    @Size(max = 200)
    @Column(name = "companyWebsite")
    private String companyWebsite;
    @Size(max = 50)
    @Column(name = "companyStatus")
    private String companyStatus;
    @Basic(optional = false)
    @NotNull
    @Column(name = "createdDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @OneToMany(mappedBy = "companyId")
    private Collection<Tblrecruiters> tblrecruitersCollection;

    public Tblcompany() {
    }

    public Tblcompany(Integer companyId) {
        this.companyId = companyId;
    }

    public Tblcompany(Integer companyId, Date createdDate) {
        this.companyId = companyId;
        this.createdDate = createdDate;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyLocation() {
        return companyLocation;
    }

    public void setCompanyLocation(String companyLocation) {
        this.companyLocation = companyLocation;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public String getCompanyWebsite() {
        return companyWebsite;
    }

    public void setCompanyWebsite(String companyWebsite) {
        this.companyWebsite = companyWebsite;
    }

    public String getCompanyStatus() {
        return companyStatus;
    }

    public void setCompanyStatus(String companyStatus) {
        this.companyStatus = companyStatus;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<Tblrecruiters> getTblrecruitersCollection() {
        return tblrecruitersCollection;
    }

    public void setTblrecruitersCollection(Collection<Tblrecruiters> tblrecruitersCollection) {
        this.tblrecruitersCollection = tblrecruitersCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (companyId != null ? companyId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tblcompany)) {
            return false;
        }
        Tblcompany other = (Tblcompany) object;
        if ((this.companyId == null && other.companyId != null) || (this.companyId != null && !this.companyId.equals(other.companyId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Tblcompany[ companyId=" + companyId + " ]";
    }
    
}
