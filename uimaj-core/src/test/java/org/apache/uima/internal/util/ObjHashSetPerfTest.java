/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.uima.internal.util;

import java.util.Random;

import junit.framework.TestCase;

public class ObjHashSetPerfTest extends TestCase {
  /**
   * Set to false to run the performance test
   * 
   * Tests both IntHashSet and IntBitSet
   */
  final boolean SKIP = false;
  
  static int cacheLoadSize;
  
  static long seed = 3737463135938899369L;
//      new Random().nextLong();
  static {
    System.out.println("Random seed for IntHashSetPerfTest: "  + seed);
  }
  Random r = new Random(seed);

//  Set<Integer> keys = new HashSet<Integer>(1000);
  
  int dmv = 0;
  
  ObjHashSet<Integer> m1;
  
  final int[] keys10000 = new int[511111];
  int k10ki = 0;
  
  
  public void testPerf() {
    if (SKIP) return;
    m1 = new ObjHashSet<>(16, Integer.class, Integer.MIN_VALUE);
     
    for (int i = 0; i < keys10000.length; i++) {
      int k = r.nextInt(511110);
     
      keys10000[i] = k + 1;
    }

    System.out.format("%n%n W A R M U P %n%n");
    cacheLoadSize = 0;
    warmup(m1);
    
    System.out.format("%n%n Time 100 %n%n");
    timelp(100);
    System.out.format("%n%n Time 1000 %n%n");
    timelp(1000);
    System.out.format("%n%n Time 10000 %n%n");
    timelp(10000);
    System.out.format("%n%n Time 100000 %n%n");
    timelp(100000);
    cacheLoadSize = 0; // 1 * 256 * 1;
    System.out.format("%n%n Time 100000 %n%n");
    timelp(100000);
    
    System.out.format("%n%n Time 500000 %n%n");
    timelp(500000);

    System.out.format("%n%n For Yourkit: Time 500000 %n%n");
    timelp(500000);
//    System.out.format("%n%n For Yourkit: Time 500000 %n%n");
//    timelp(500000);
//    System.out.format("%n%n For Yourkit: Time 500000 %n%n");
//    timelp(500000);

    
    System.out.println(dmv);
  }

  private void time2(int n) {
    float f1 = time(m1, n);
  }
  
  private void timelp(int n) {
    time2(n);
    time2(n);
    time2(n);
  }

  private void warmup(ObjHashSet<Integer> m) {
    for (int i = 0; i < 500; i++) {
      inner(m,true, 1000) ; // warm up
    }
  }
  
  private float time(ObjHashSet<Integer> m, int ss) {
    long start = System.nanoTime();
    for (int i = 0; i < 500; i++) {
      inner(m,false, ss);
    }
    float t = (System.nanoTime() - start) / 1000000.0F;
    System.out.format("time for %,d:  %s is %.3f milliseconds %n", ss,
        m.getClass().getSimpleName(),
        t);
    return t;
  }
  
  private int nextKey() {
    int r = keys10000[k10ki++];
    if (k10ki >= keys10000.length) {
      k10ki = 0;
    }
    return r;
  }
  
  private void inner(ObjHashSet<Integer> m, boolean check, int ss) {
    CS cs = new CS(m);     

    for (int i = 0; i < ss; i++) {
      
      int k = keys10000[i];
//      System.out.print(" " + k);
//      if (i %100 == 0) System.out.println("");
//      keys.add(k);
      cs.add(k);
      cacheLoad(i);
      if (check) {
        assertTrue(cs.contains(k));
      }
    }
    for (int i = 0; i < ss; i++) {
      boolean v = cs.contains(keys10000[i]); 
      if (!v) {
        throw new RuntimeException("never happen");
      }
      dmv += 1;
      cacheLoad(i);
    }
    cs.clear();
    

//    for (int k : keys) {     
//      assertEquals(10000 + k, (m instanceof IntHashSet) ? 
//          ((IntHashSet)m).get(k) :
//          ((IntArrayRBT)m).getMostlyClose(k));
//    }

  }
  
  static class CS {
    final ObjHashSet<Integer> set;
    
    CS(ObjHashSet<Integer> set) {
      this.set = set;
    }
    
    boolean contains(Integer i) {
      return set.contains(i);
    }
    
    void add(int i) {
      set.add(i);
    }
    
    void clear() {
      set.clear();
    }

  }
  
  void cacheLoad(int i) {
    if (cacheLoadSize > 0) {
      int[] cl = new int[cacheLoadSize];
      if (i != 100000) {
        cl = null;
      }
    }
  }
}
