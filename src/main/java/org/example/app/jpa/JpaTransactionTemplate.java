package org.example.app.jpa;

import lombok.RequiredArgsConstructor;
import org.example.app.domain.UserWithPassword;
import org.example.app.entity.UserEntity;
import org.example.app.jpa.exception.PersistenceException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

@RequiredArgsConstructor
public class JpaTransactionTemplate {
  private final EntityManagerFactory entityManagerFactory;

  public <T> T executeInTransaction(Callback<T> callback) {
    EntityManager entityManager = null;
    EntityTransaction entityTransaction = null;
    try {
      entityManager = entityManagerFactory.createEntityManager();
      entityTransaction = entityManager.getTransaction();
      entityTransaction.begin();

      final var result = callback.execute(entityManager, entityTransaction);

      entityTransaction.commit();

      return result;
    } catch (Exception e) {
      if (entityTransaction != null) {
        entityTransaction.rollback();
      }
      throw new PersistenceException(e);
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }
  }

  @FunctionalInterface
  public static interface Callback<T> {
     T execute(EntityManager entityManager, EntityTransaction transaction);
  }
}
