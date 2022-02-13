package ua.com.foxminded.university.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class GenericEntity implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "created_timestamp")
    @CreationTimestamp
    private LocalDateTime createdTimestamp;

    @Column(name = "updated_timestamp")
    @UpdateTimestamp
    private LocalDateTime updatedTimestamp;

    protected GenericEntity(Integer id) {
        this.id = id;
    }
}
