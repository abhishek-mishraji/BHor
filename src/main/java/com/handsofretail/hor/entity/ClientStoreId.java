package com.handsofretail.hor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ClientStoreId implements Serializable {

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "store_id")
    private Long storeId;
}
