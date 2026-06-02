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
@Table(name = "tblskills")
@XmlRootElement
//@NamedQueries({
//    @NamedQuery(name = "Tblskills.findAll", query = "SELECT t FROM Tblskills t"),
//    @NamedQuery(name = "Tblskills.findBySkillId", query = "SELECT t FROM Tblskills t WHERE t.skillId = :skillId"),
//    @NamedQuery(name = "Tblskills.findBySkillName", query = "SELECT t FROM Tblskills t WHERE t.skillName = :skillName"),
//    @NamedQuery(name = "Tblskills.findBySkillCategory", query = "SELECT t FROM Tblskills t WHERE t.skillCategory = :skillCategory"),
//
//    @NamedQuery(name = "Tblskills.findByCategory", query = "SELECT t FROM Tblskills t WHERE t.skillCategory = :category"),
//    @NamedQuery(name = "Tblskills.findCategory", query = "SELECT DISTINCT s.skillCategory FROM Tblskills s ORDER BY s.skillCategory")
//        
//})

@NamedQueries({
    @NamedQuery(name = "Tblskills.findAll",
            query = "SELECT t FROM Tblskills t"),

    @NamedQuery(name = "Tblskills.findBySkillId",
            query = "SELECT t FROM Tblskills t WHERE t.skillId = :skillId"),

    @NamedQuery(name = "Tblskills.findBySkillName",
            query = "SELECT t FROM Tblskills t WHERE t.skillName = :skillName"),

    @NamedQuery(name = "Tblskills.findByCategoryId",
            query = "SELECT t FROM Tblskills t WHERE t.categoryId.categoryId = :categoryId"),

    @NamedQuery(name = "Tblskills.findApprovedSkillsByCategory",
    query = "SELECT s FROM Tblskills s WHERE s.categoryId.categoryId = :categoryId AND s.skillStatus = 'APPROVED' ORDER BY s.skillName")
})
public class Tblskills implements Serializable {

    @Size(max = 200)
    @Column(name = "skillName")
    private String skillName;
    @Size(max = 50)
    @Column(name = "skillStatus")
    private String skillStatus;
    @Column(name = "createdByUserId")
    private Integer createdByUserId;
    @Column(name = "mergedIntoSkillId")
    private Integer mergedIntoSkillId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "createdDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Column(name = "approvedByUserId")
    private Integer approvedByUserId;
    @Column(name = "approvedDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date approvedDate;
    @JoinColumn(name = "categoryId", referencedColumnName = "categoryId")
    @ManyToOne
    private Tblskillcategory categoryId;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "skillId")
    private Integer skillId;
    @ManyToMany(mappedBy = "tblskillsCollection")
    private Collection<Tblcandidates> tblcandidatesCollection;
    @ManyToMany(mappedBy = "tblskillsCollection")
    private Collection<Tbljob> tbljobCollection;

    public Tblskills() {
    }

    public Tblskills(Integer skillId) {
        this.skillId = skillId;
    }

    public Integer getSkillId() {
        return skillId;
    }

    public void setSkillId(Integer skillId) {
        this.skillId = skillId;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (skillId != null ? skillId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tblskills)) {
            return false;
        }
        Tblskills other = (Tblskills) object;
        if ((this.skillId == null && other.skillId != null) || (this.skillId != null && !this.skillId.equals(other.skillId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Tblskills[ skillId=" + skillId + " ]";
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getSkillStatus() {
        return skillStatus;
    }

    public void setSkillStatus(String skillStatus) {
        this.skillStatus = skillStatus;
    }

    public Integer getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(Integer createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public Integer getMergedIntoSkillId() {
        return mergedIntoSkillId;
    }

    public void setMergedIntoSkillId(Integer mergedIntoSkillId) {
        this.mergedIntoSkillId = mergedIntoSkillId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getApprovedByUserId() {
        return approvedByUserId;
    }

    public void setApprovedByUserId(Integer approvedByUserId) {
        this.approvedByUserId = approvedByUserId;
    }

    public Date getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(Date approvedDate) {
        this.approvedDate = approvedDate;
    }

    public Tblskillcategory getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Tblskillcategory categoryId) {
        this.categoryId = categoryId;
    }
    
}
