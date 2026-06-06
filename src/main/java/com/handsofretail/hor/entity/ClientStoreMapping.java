package com.handsofretail.hor.entity;

import com.handsofretail.hor.entity.base.BaseEntity;
import com.handsofretail.hor.enums.StoreRole;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "client_store_mapping")
public class ClientStoreMapping extends BaseEntity {

    @EmbeddedId
    private ClientStoreId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("clientId")
    @JoinColumn(name = "client_id")
    private ClientUser client;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("storeId")
    @JoinColumn(name = "store_id")
    private Store store;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StoreRole role;
}
