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

package org.apache.uima.jcas.cas;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

public class NonEmptyIntegerList_Type extends IntegerList_Type {
  protected FSGenerator getFSGenerator() {
    return fsGenerator;
  }

  private final FSGenerator fsGenerator = new FSGenerator() {
    public FeatureStructure createFS(int addr, CASImpl cas) {
      if (instanceOf_Type.useExistingInstance) {
        // Return eq fs instance if already created
        FeatureStructure fs = instanceOf_Type.jcas.getJfsFromCaddr(addr);
        if (null == fs) {
          fs = new NonEmptyIntegerList(addr, instanceOf_Type);
          instanceOf_Type.jcas.putJfsFromCaddr(addr, fs);
          return fs;
        }
        return fs;
      } else
        return new NonEmptyIntegerList(addr, instanceOf_Type);
    }
  };

  public final static int typeIndexID = NonEmptyIntegerList.typeIndexID;

  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uima.cas.NonEmptyIntegerList");

  final Feature casFeat_head;

  final int casFeatCode_head;

  public int getHead(int addr) {
    if (featOkTst && casFeat_head == null)
      this.jcas.throwFeatMissing("head", "uima.cas.NonEmptyIntegerList");
    return ll_cas.ll_getIntValue(addr, casFeatCode_head);
  }

  public void setHead(int addr, int v) {
    if (featOkTst && casFeat_head == null)
      this.jcas.throwFeatMissing("head", "uima.cas.NonEmptyIntegerList");
    ll_cas.ll_setIntValue(addr, casFeatCode_head, v);
  }

  final Feature casFeat_tail;

  final int casFeatCode_tail;

  public int getTail(int addr) {
    if (featOkTst && casFeat_tail == null)
      this.jcas.throwFeatMissing("tail", "uima.cas.NonEmptyIntegerList");
    return ll_cas.ll_getRefValue(addr, casFeatCode_tail);
  }

  public void setTail(int addr, int v) {
    if (featOkTst && casFeat_tail == null)
      this.jcas.throwFeatMissing("tail", "uima.cas.NonEmptyIntegerList");
    ll_cas.ll_setRefValue(addr, casFeatCode_tail, v);
  }

  // * initialize variables to correspond with Cas Type and Features
  public NonEmptyIntegerList_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());

    casFeat_head = jcas.getRequiredFeatureDE(casType, "head", "uima.cas.Integer", featOkTst);
    casFeatCode_head = (null == casFeat_head) ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_head).getCode();
    casFeat_tail = jcas.getRequiredFeatureDE(casType, "tail", "uima.cas.IntegerList", featOkTst);
    casFeatCode_tail = (null == casFeat_tail) ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_tail).getCode();
  }

  protected NonEmptyIntegerList_Type() { // block default new operator
    casFeat_head = null;
    casFeatCode_head = JCas.INVALID_FEATURE_CODE;
    casFeat_tail = null;
    casFeatCode_tail = JCas.INVALID_FEATURE_CODE;
    throw new RuntimeException("Internal Error-this constructor should never be called.");
  }

}