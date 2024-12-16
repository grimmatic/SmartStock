package yazlab.smartstock.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "logs")
@Data
@EqualsAndHashCode(callSuper = true)
public class Log extends BaseEntity {
    private Long customerId;
    private Long orderId;

    @Enumerated(EnumType.STRING)
    private LogType logType;

    private String logDetails;

    public enum LogType {
        ERROR, WARNING, INFO
    }
}