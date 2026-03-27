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
@Table(name = "tblrolemaster")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tblrolemaster.findAll", query = "SELECT t FROM Tblrolemaster t"),
    @NamedQuery(name = "Tblrolemaster.findByRoleId", query = "SELECT t FROM Tblrolemaster t WHERE t.roleId = :roleId"),
    @NamedQuery(name = "Tblrolemaster.findByRoleName", query = "SELECT t FROM Tblrolemaster t WHERE t.roleName = :roleName"),
    @NamedQuery(name = "Tblrolemaster.findByCreatedDate", query = "SELECT t FROM Tblrolemaster t WHERE t.createdDate = :createdDate")})
public class Tblrolemaster implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "roleId")
    private Integer roleId;
    @Size(max = 50)
    @Column(name = "roleName")
    private String roleName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "createdDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @OneToMany(mappedBy = "roleId")
    private Collection<Tblusers> tblusersCollection;

    public Tblrolemaster() {
    }

    public Tblrolemaster(Integer roleId) {
        this.roleId = roleId;
    }

    public Tblrolemaster(Integer roleId, Date createdDate) {
        this.roleId = roleId;
        this.createdDate = createdDate;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @XmlTransient
    public Collection<Tblusers> getTblusersCollection() {
        return tblusersCollection;
    }

    public void setTblusersCollection(Collection<Tblusers> tblusersCollection) {
        this.tblusersCollection = tblusersCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roleId != null ? roleId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tblrolemaster)) {
            return false;
        }
        Tblrolemaster other = (Tblrolemaster) object;
        if ((this.roleId == null && other.roleId != null) || (this.roleId != null && !this.roleId.equals(other.roleId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Tblrolemaster[ roleId=" + roleId + " ]";
    }
    
}
