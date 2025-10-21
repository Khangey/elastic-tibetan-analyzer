/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.tocharian;

import org.elasticsearch.index.analysis.AnalyzerProvider;
import org.elasticsearch.indices.analysis.AnalysisModule.AnalysisProvider;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;

import java.util.Map;

import static java.util.Collections.singletonMap;

/**
 * Tibetan analyzer plugin for Elasticsearch
 * Provides Tibetan language text analysis and tokenization
 */
public class TibetanAnalyzerPlugin extends Plugin implements AnalysisPlugin {
    
    @Override
    public Map<String, AnalysisProvider<AnalyzerProvider<?>>> getAnalyzers() {
        return singletonMap("tibetan_analyzer", TibetanAnalyzerFactory::new);
    }
}

