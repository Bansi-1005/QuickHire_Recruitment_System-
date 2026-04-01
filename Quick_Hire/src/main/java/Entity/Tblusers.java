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
@Table(name = "tblusers")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tblusers.findAll", query = "SELECT t FROM Tblusers t"),
    @NamedQuery(name = "Tblusers.findByUserId", query = "SELECT t FROM Tblusers t WHERE t.userId = :userId"),
    @NamedQuery(name = "Tblusers.findByUserName", query = "SELECT t FROM Tblusers t WHERE t.userName = :userName"),
    @NamedQuery(name = "Tblusers.findByUserEmail", query = "SELECT t FROM Tblusers t WHERE t.userEmail = :userEmail"),
    @NamedQuery(name = "Tblusers.findByUserPassword", query = "SELECT t FROM Tblusers t WHERE t.userPassword = :userPassword"),
    @NamedQuery(name = "Tblusers.findByUserStatus", query = "SELECT t FROM Tblusers t WHERE t.userStatus = :userStatus"),
    @NamedQuery(name = "Tblusers.findByCreatedDate", query = "SELECT t FROM Tblusers t WHERE t.createdDate = :createdDate"),
    @NamedQuery(name = "Tblusers.findByUpdatedDate", query = "SELECT t FROM Tblusers t WHERE t.updatedDate = :updatedDate"),
    @NamedQuery(name = "Tblusers.findByLastLoginDate", query = "SELECT t FROM Tblusers t WHERE t.lastLoginDate = :lastLoginDate"),
    
    @NamedQuery(name = "Tblusers.loginByRole", query = "SELECT t FROM Tblusers t WHERE t.userEmail = :email AND t.userPassword = :password AND t.roleId.roleId = :roleId"),
    @NamedQuery(name = "Tblusers.findByRole", query = "SELECT t FROM Tblusers t WHERE t.roleId.roleId = :roleId"),
        
    //@NamedQuery(name="Tblusers.searchUsers", query="SELECT t FROM Tblusers t WHERE t.email LIKE :keyword"),
    @NamedQuery(name="Tblusers.count", query="SELECT COUNT(t) FROM Tblusers t"),
    @NamedQuery(name = "Tblusers.findByuserName",query = "SELECT u FROM Tblusers u WHERE u.userName = :userName")})
public class Tblusers implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "userId")
    private Integer userId;
    @Size(max = 100)
    @Column(name = "userName")
    private String userName;
    @Size(max = 100)
    @Column(name = "userEmail")
    private String userEmail;
    @Size(max = 255)
    @Column(name = "userPassword")
    private String userPassword;
    @Size(max = 50)
    @Column(name = "userStatus")
    private String userStatus;
    @Basic(optional = false)
    @NotNull
    @Column(name = "createdDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "updatedDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "lastLoginDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginDate;
    @ManyToMany(mappedBy = "tblusersCollection")
    private Collection<Tblinterview> tblinterviewCollection;
    @OneToMany(mappedBy = "userId")
    private Collection<Tblcandidates> tblcandidatesCollection;
    @JoinColumn(name = "roleId", referencedColumnName = "roleId")
    @ManyToOne
    private Tblrolemaster roleId;
    @OneToMany(mappedBy = "userId")
    private Collection<Tblrecruiters> tblrecruitersCollection;
    @OneToMany(mappedBy = "userId")
    private Collection<Tblnotification> tblnotificationCollection;

    public Tblusers() {
    }

    public Tblusers(Integer userId) {
        this.userId = userId;
    }

    public Tblusers(Integer userId, Date createdDate, Date updatedDate, Date lastLoginDate) {
        this.userId = userId;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.lastLoginDate = lastLoginDate;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<Tblinterview> getTblinterviewCollection() {
        return tblinterviewCollection;
    }

    public void setTblinterviewCollection(Collection<Tblinterview> tblinterviewCollection) {
        this.tblinterviewCollection = tblinterviewCollection;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<Tblcandidates> getTblcandidatesCollection() {
        return tblcandidatesCollection;
    }

    public void setTblcandidatesCollection(Collection<Tblcandidates> tblcandidatesCollection) {
        this.tblcandidatesCollection = tblcandidatesCollection;
    }

    public Tblrolemaster getRoleId() {
        return roleId;
    }

    public void setRoleId(Tblrolemaster roleId) {
        this.roleId = roleId;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<Tblrecruiters> getTblrecruitersCollection() {
        return tblrecruitersCollection;
    }

    public void setTblrecruitersCollection(Collection<Tblrecruiters> tblrecruitersCollection) {
        this.tblrecruitersCollection = tblrecruitersCollection;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<Tblnotification> getTblnotificationCollection() {
        return tblnotificationCollection;
    }

    public void setTblnotificationCollection(Collection<Tblnotification> tblnotificationCollection) {
        this.tblnotificationCollection = tblnotificationCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userId != null ? userId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tblusers)) {
            return false;
        }
        Tblusers other = (Tblusers) object;
        if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Tblusers[ userId=" + userId + " ]";
    }
    
}
