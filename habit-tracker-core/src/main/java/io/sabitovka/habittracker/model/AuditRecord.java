package io.sabitovka.habittracker.model;

import io.sabitovka.habittracker.persistence.annotation.Column;
import io.sabitovka.habittracker.persistence.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Table(name = "audit")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuditRecord {
    @Column(name = "id")
    Long id;

    @Column(name = "username")
    String username;

    @Column(name = "ip")
    String ip;

    @Column(name = "action")
    String action;

    @Column(name = "arguments")
    String arguments;

    @Column(name = "timestamp")
    LocalDateTime timestamp;
}
