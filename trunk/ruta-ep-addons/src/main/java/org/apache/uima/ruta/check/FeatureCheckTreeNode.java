/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.uima.ruta.check;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;

public class FeatureCheckTreeNode extends AnnotationCheckTreeNode {

  private Feature feature;

  private String value;

  public FeatureCheckTreeNode(IAnnotationCheckTreeNode parent, Feature feat, String value) {
    super(parent, null);
    this.feature = feat;
    this.value = value;
  }

  @Override
  public String getName() {
    return feature.getShortName() + ": " + value;
  }

  public Type getRange() {
    return feature.getRange();
  }

  public Feature getFeature() {
    return feature;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
