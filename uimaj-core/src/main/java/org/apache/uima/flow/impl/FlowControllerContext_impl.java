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

package org.apache.uima.flow.impl;

import java.util.Collections;
import java.util.Map;

import org.apache.uima.UimaContextAdmin;
import org.apache.uima.analysis_engine.impl.AnalysisEngineManagementImpl;
import org.apache.uima.analysis_engine.metadata.AnalysisEngineMetaData;
import org.apache.uima.flow.FlowControllerContext;
import org.apache.uima.impl.ChildUimaContext_impl;
import org.apache.uima.impl.UimaContext_ImplBase;

/**
 * Implementation of FlowControllerContext.
 */
public class FlowControllerContext_impl extends ChildUimaContext_impl implements
        FlowControllerContext {
//IC see: https://issues.apache.org/jira/browse/UIMA-48
//IC see: https://issues.apache.org/jira/browse/UIMA-48

  private Map<String, AnalysisEngineMetaData> mAnalysisEngineMetaDataMap;

  private AnalysisEngineMetaData mAggregateMetadata;

  /**
   * @param aParentContext -
   * @param aContextName -
   * @param aSofaMappings -
   * @param aAnalysisEngineMetaDataMap -
   * @param aAggregateMetadata -
   */
  public FlowControllerContext_impl(UimaContextAdmin aParentContext, String aContextName,
//IC see: https://issues.apache.org/jira/browse/UIMA-1452
          Map<String, String> aSofaMappings, Map<String, AnalysisEngineMetaData> aAnalysisEngineMetaDataMap,
//IC see: https://issues.apache.org/jira/browse/UIMA-48
          AnalysisEngineMetaData aAggregateMetadata) {
//IC see: https://issues.apache.org/jira/browse/UIMA-4769
    super(aParentContext, aContextName, ((UimaContext_ImplBase)aParentContext).combineSofaMappings(aSofaMappings));
//IC see: https://issues.apache.org/jira/browse/UIMA-327
    mAnalysisEngineMetaDataMap = Collections.unmodifiableMap(aAnalysisEngineMetaDataMap);
    mAggregateMetadata = aAggregateMetadata;

    // add our MBean to the tree
    ((AnalysisEngineManagementImpl) aParentContext.getManagementInterface()).addComponent(
            aContextName, this.mMBean);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.uima.flow.FlowControllerContext#getAnalysisEngineMetaDataMap()
   */
  public Map<String, AnalysisEngineMetaData> getAnalysisEngineMetaDataMap() {
    return mAnalysisEngineMetaDataMap;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.uima.flow.FlowControllerContext#getAggregateMetadata()
   */
  public AnalysisEngineMetaData getAggregateMetadata() {
    return mAggregateMetadata;
  }
}
