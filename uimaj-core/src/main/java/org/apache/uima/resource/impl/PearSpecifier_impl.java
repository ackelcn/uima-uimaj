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

package org.apache.uima.resource.impl;
import org.apache.uima.resource.Parameter;
import org.apache.uima.resource.PearSpecifier;
import org.apache.uima.resource.metadata.NameValuePair;
import org.apache.uima.resource.metadata.impl.MetaDataObject_impl;
import org.apache.uima.resource.metadata.impl.PropertyXmlInfo;
import org.apache.uima.resource.metadata.impl.XmlizationInfo;

/**
 * Reference implementation of {@link org.apache.uima.resource.PearSpecifier}.
 * 
 * 
 */
public class PearSpecifier_impl extends MetaDataObject_impl implements PearSpecifier {

  static final long serialVersionUID = -7910540167197537337L;

  /** PEAR path setting */
  private String mPearPath;

  private Parameter[] mParameters;
  private NameValuePair[] mPearParameters;

  /**
   * Creates a new <code>PearSpecifier_impl</code>.
   */
  public PearSpecifier_impl() {
  }

  @Deprecated
  public Parameter[] getParameters() {
    return this.mParameters;
  }
  
  public NameValuePair[] getPearParameters() {
//IC see: https://issues.apache.org/jira/browse/UIMA-5936
    return this.mPearParameters;
  }

  @Deprecated
  public void setParameters(Parameter... parameters) {
    this.mParameters = parameters;
  }
  
  public void setPearParameters(NameValuePair... pearParameters) {
    this.mPearParameters = pearParameters;
  }

  public String getPearPath() {
    return this.mPearPath;
  }

  public void setPearPath(String aPearPath) {
    this.mPearPath = aPearPath;    
  }

  protected XmlizationInfo getXmlizationInfo() {
    return XMLIZATION_INFO;
  }

  static final private XmlizationInfo XMLIZATION_INFO = new XmlizationInfo("pearSpecifier", new PropertyXmlInfo[] {
//IC see: https://issues.apache.org/jira/browse/UIMA-5936
      new PropertyXmlInfo("pearPath"), new PropertyXmlInfo("pearParameters"), new PropertyXmlInfo("parameters") });
}
