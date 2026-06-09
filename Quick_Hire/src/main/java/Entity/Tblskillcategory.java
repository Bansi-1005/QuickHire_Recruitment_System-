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
 * @author RINKAL
 */
@Entity
@Table(name = "tblskillcategory")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tblskillcategory.findAll", query = "SELECT t FROM Tblskillcategory t ORDER BY t.createdDate DESC"),
    @NamedQuery(name = "Tblskillcategory.findByCategoryId", query = "SELECT t FROM Tblskillcategory t WHERE t.categoryId = :categoryId"),
    @NamedQuery(name = "Tblskillcategory.findByCategoryName", query = "SELECT t FROM Tblskillcategory t WHERE t.categoryName = :categoryName"),
    @NamedQuery(name = "Tblskillcategory.findByCategoryStatus", query = "SELECT t FROM Tblskillcategory t WHERE t.categoryStatus = :categoryStatus"),
    @NamedQuery(name = "Tblskillcategory.findByCreatedByUserId", query = "SELECT t FROM Tblskillcategory t WHERE t.createdByUserId = :createdByUserId"),
    @NamedQuery(name = "Tblskillcategory.findByMergedIntoCategoryId", query = "SELECT t FROM Tblskillcategory t WHERE t.mergedIntoCategoryId = :mergedIntoCategoryId"),
    @NamedQuery(name = "Tblskillcategory.findByCreatedDate", query = "SELECT t FROM Tblskillcategory t WHERE t.createdDate = :createdDate"),
    
    @NamedQuery(name = "Tblskillcategory.findApprovedCategory",
    query = "SELECT c FROM Tblskillcategory c WHERE c.categoryStatus = 'APPROVED' ORDER BY c.categoryName")
})
public class Tblskillcategory implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "categoryId")
    private Integer categoryId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "categoryName")
    private String categoryName;
    @Size(max = 50)
    @Column(name = "categoryStatus")
    private String categoryStatus;
    @Column(name = "createdByUserId")
    private Integer createdByUserId;
    @Column(name = "mergedIntoCategoryId")
    private Integer mergedIntoCategoryId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "createdDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @OneToMany(mappedBy = "categoryId")
    private Collection<Tblskills> tblskillsCollection;

    public Tblskillcategory() {
    }

    public Tblskillcategory(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Tblskillcategory(Integer categoryId, String categoryName, Date createdDate) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.createdDate = createdDate;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryStatus() {
        return categoryStatus;
    }

    public void setCategoryStatus(String categoryStatus) {
        this.categoryStatus = categoryStatus;
    }

    public Integer getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(Integer createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public Integer getMergedIntoCategoryId() {
        return mergedIntoCategoryId;
    }

    public void setMergedIntoCategoryId(Integer mergedIntoCategoryId) {
        this.mergedIntoCategoryId = mergedIntoCategoryId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<Tblskills> getTblskillsCollection() {
        return tblskillsCollection;
    }

    public void setTblskillsCollection(Collection<Tblskills> tblskillsCollection) {
        this.tblskillsCollection = tblskillsCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (categoryId != null ? categoryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tblskillcategory)) {
            return false;
        }
        Tblskillcategory other = (Tblskillcategory) object;
        if ((this.categoryId == null && other.categoryId != null) || (this.categoryId != null && !this.categoryId.equals(other.categoryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Tblskillcategory[ categoryId=" + categoryId + " ]";
    }
    
}
