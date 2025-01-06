package com.codey.fatcat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @UuidGenerator
  @Column(updatable = false, nullable = false)
  private UUID id;


  @CreatedDate
  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @UpdateTimestamp
  @Column(nullable = false)
  private LocalDateTime updatedAt;
}
