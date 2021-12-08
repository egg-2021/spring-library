package edu.egg.library.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE libro SET alta = false WHERE id = ?")
public class Libro {

    @Id
    @GeneratedValue
    private Integer id;

    @NotNull(message = "El ISBN es obligatorio")
    @Column(nullable = false, unique = true)
    private Long isbn;

    @NotBlank(message = "El título es obligatorio")
    @Column(nullable = false, length = 75)
    private String titulo;

    @NotNull(message = "El año es obligatorio")
    @Column(nullable = false)
    private Integer anio;

    @NotNull(message = "La cantidad de ejemplares es obligatoria")
    @Column(nullable = false)
    private Integer ejemplares;

    @NotNull(message = "La cantidad de prestados es obligatoria")
    @Column(nullable = false)
    private Integer prestados;

    @Column(nullable = false)
    private Integer restantes;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime creacion;

    @LastModifiedDate
    private LocalDateTime modificacion;

    @NotNull(message = "El autor no puede ser nulo")
    @ManyToOne
    @JoinColumn(nullable = false)
    private Autor autor;

    @NotNull(message = "La editorial no puede ser nula")
    @ManyToOne
    @JoinColumn(nullable = false)
    private Editorial editorial;

    private Boolean alta;

    @Override
    public String toString() {
        return String.format("LIBRO (id: %s, isbn: %s, titulo: %s, anio: %s, ejemplares: %s, prestados: %s, restantes: %s, autor: %s, editorial: %s)",
                id, isbn, titulo, anio, ejemplares, prestados, restantes, autor, editorial);
    }
}
