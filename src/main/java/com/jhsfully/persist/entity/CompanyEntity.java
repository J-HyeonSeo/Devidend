package com.jhsfully.persist.entity;

import com.jhsfully.model.Company;
import lombok.*;

import javax.persistence.*;

@Entity(name = "COMPANY")
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String ticker;
    private String name;

    public static CompanyEntity from(Company company){
        return CompanyEntity.builder()
                .ticker(company.getTicker())
                .name(company.getName())
                .build();
    }
}
