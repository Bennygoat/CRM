package com.example.demo.entity;

import com.example.demo.enums.BCustomerIndustry;
import com.example.demo.enums.BCustomerLevel;
import com.example.demo.enums.BCustomerType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "b_customers")
@DiscriminatorValue("B2B")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
//@ToString(exclude = {"contacts", "tags"})
//@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BCustomer extends CustomerBase{

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @EqualsAndHashCode.Include
//    private Long customerId;
//
//    @Column(nullable = false, length = 100)
//    private String customerName;

    @Enumerated(EnumType.STRING)
    @Column(length = 100)
    private BCustomerIndustry industry;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private BCustomerType BCustomerType;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private BCustomerLevel BCustomerLevel;

//    @Column(length = 255)
//    private String customerAddress;
//
//    @Column(length = 30)
//    private String customerTel;
//
//    @Column(length = 150)
//    private String customerEmail;

//    @CreatedDate
//    @Column(updatable = false)
//    private LocalDateTime createdAt;
//
//    @LastModifiedDate
//    private LocalDateTime updatedAt;
@Column(name = "tin_number", unique = true, nullable = true)
private String tinNumber;
    // ----- 一對多關聯：客戶擁有的聯絡人集合 -----
    @Builder.Default
    @OneToMany(mappedBy = "bCustomer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Contact> contacts = new HashSet<>();

    // ----- 多對多關聯：一個客戶可以有多個標籤 -----
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "customer_tags_link", // 客戶與標籤的中間表
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "customer_tag_id")
    )
    private Set<CustomerTag> tags = new HashSet<>();

    // ----- 關聯管理輔助方法 -----
    /**
     * 添加一個聯絡人到此客戶。
     * 同時維護聯絡人的客戶關聯（雙向），確保數據一致性。
     * @param contact 要添加的聯絡人實體
     */
    public void addContact(Contact contact) {
        contacts.add(contact);
        contact.setBCustomer(this); // 設定聯絡人所屬的客戶
    }

    /**
     * 從此客戶移除一個聯絡人。
     * 同時清除聯絡人的客戶關聯，確保數據一致性。
     * @param contact 要移除的聯絡人實體
     */
    public void removeContact(Contact contact) {
        contacts.remove(contact);
        contact.setBCustomer(null); // 清除聯絡人所屬的客戶
    }

    public void addTag(CustomerTag tag) {
        this.tags.add(tag);
        tag.getBCustomers().add(this);
    }

    public void removeTag(CustomerTag tag) {
        this.tags.remove(tag);
        tag.getBCustomers().remove(this);
    }
}
