package com.github.hervian.lambdas.util;

import java.util.ArrayList;
import java.util.List;

public class CombinatoricsUtil {

  public static <T> List<List<T>> createPermutationsWithRepetitionsRecursively(List<T> list, int chooseK){
    List<List<T>> res = new ArrayList<>();
    if (chooseK<1){
      res.add(new ArrayList<>());
      return res;
    }

    for (T t: list){
      if (chooseK<2){
        List<T> subList = new ArrayList<>();
        subList.add(t);
        res.add(subList);
      } else {
        List<List<T>> col = createPermutationsWithRepetitionsRecursively(list, chooseK-1);
        for (List subList: col){
          subList.add(t);
          res.add(subList);
        }
      }
    }
    return res;
  }

}
