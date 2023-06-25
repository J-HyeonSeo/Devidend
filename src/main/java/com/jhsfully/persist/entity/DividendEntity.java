package com.jhsfully.persist.entity;

import com.jhsfully.model.Dividend;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "DIVIDEND")
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"companyId", "date"}
                )
        }
)
public class DividendEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long companyId;
    private LocalDateTime date;
    private String dividend;

    public static DividendEntity from(Dividend dividend, Long companyId){
        return DividendEntity.builder()
                .companyId(companyId)
                .date(dividend.getDate())
                .dividend(dividend.getDividend())
                .build();
    }

}
