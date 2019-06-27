/*
 * Copyright 2019, Yahoo Inc.
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file in project root for terms.
 */

package com.yahoo.elide.datastores.search;

import com.yahoo.elide.core.DataStore;
import com.yahoo.elide.core.DataStoreTransaction;
import com.yahoo.elide.core.EntityDictionary;

import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;

import javax.persistence.EntityManagerFactory;

/**
 * Performs full text search when it can.  Otherwise delegates to a wrapped store.
 */
public class SearchDataStore implements DataStore {

   private DataStore wrapped;
   private EntityDictionary dictionary;
   private EntityManagerFactory entityManagerFactory;
   private boolean indexOnStartup = false;

   public SearchDataStore(DataStore wrapped, EntityManagerFactory entityManagerFactory, boolean indexOnStartup) {
       this.wrapped = wrapped;
       this.entityManagerFactory = entityManagerFactory;
       this.indexOnStartup = indexOnStartup;

   }

   @Override
   public void populateEntityDictionary(EntityDictionary entityDictionary) {
       wrapped.populateEntityDictionary(entityDictionary);

       if (indexOnStartup) {

           FullTextEntityManager em = Search.getFullTextEntityManager(entityManagerFactory.createEntityManager());
           try {
               for (Class<?> entityClass : entityDictionary.getBindings()) {
                   if (entityDictionary.getAnnotation(entityClass, Indexed.class) != null) {
                       em.createIndexer(entityClass).startAndWait();
                   }
               }
           } catch (InterruptedException e) {
               throw new IllegalStateException(e);
           } finally {
               em.close();
           }
       }

       this.dictionary = entityDictionary;
   }

   @Override
   public DataStoreTransaction beginTransaction() {
       return wrapped.beginTransaction();
   }

   @Override
   public DataStoreTransaction beginReadTransaction() {

       FullTextEntityManager em = Search.getFullTextEntityManager(entityManagerFactory.createEntityManager());

       return new SearchDataTransaction(wrapped.beginReadTransaction(), dictionary, em);
   }
}
