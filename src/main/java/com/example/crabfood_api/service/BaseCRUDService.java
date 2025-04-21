package com.example.crabfood_api.service;

import java.util.List;


public interface BaseCRUDService<Q,R,D,I> {
  List<R> findAll();
  List<R> findAll(int page, int size, String sortBy, String sortDir);
  R create(Q request);
  R findById(I id);
  R update(I id, Q request);
  void deleteById(I id);
}
