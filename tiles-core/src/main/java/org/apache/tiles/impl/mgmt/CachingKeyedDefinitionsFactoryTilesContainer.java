/*
 * $Id$
 *
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
 *
 */

package org.apache.tiles.impl.mgmt;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.PageContext;

import org.apache.tiles.TilesException;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.definition.ComponentDefinition;
import org.apache.tiles.definition.DefinitionsFactory;
import org.apache.tiles.definition.DefinitionsFactoryException;
import org.apache.tiles.impl.KeyedDefinitionsFactoryTilesContainer;
import org.apache.tiles.mgmt.MutableTilesContainer;
import org.apache.tiles.mgmt.TileDefinition;

/**
 * Container that can be used to store multiple {@link DefinitionsFactory}
 * instances mapped to different keys, with the addition of being "mutable",
 * i.e.  caches (in memory) the definitions registered to it.  If a definition
 * is not found in cache, it will revert back to it's definitions factory.
 *
 * @version $Rev$ $Date$
 */
public class CachingKeyedDefinitionsFactoryTilesContainer extends
        KeyedDefinitionsFactoryTilesContainer implements MutableTilesContainer {

    private static final String DEFINITIONS_ATTRIBUTE_NAME_BASE =
        "org.apache.tiles.impl.mgmt.CachingKeyedDefinitionsFactoryTilesContainer.DEFINITIONS.";

    private DefinitionManager mgr = new DefinitionManager();
    
    private Map<String, DefinitionManager> key2definitionManager
            = new HashMap<String, DefinitionManager>();

    public void register(TileDefinition definition, PageContext context)
        throws TilesException {
        TilesRequestContext requestContext = getContextFactory().createRequestContext(
                getApplicationContext(), context
            );
        register(definition, requestContext);
    }

    public void register(TileDefinition definition, Object request,
            Object response) throws TilesException {
        TilesRequestContext requestContext = getContextFactory().createRequestContext(
                getApplicationContext(),
                request,
                response
            );
        register(definition, requestContext);
    }

    @Override
    protected ComponentDefinition getDefinition(String definition,
                                                TilesRequestContext context)
        throws DefinitionsFactoryException {
        DefinitionManager mgr = getProperDefinitionManager(
                getDefinitionsFactoryKey(context));
        return mgr.getDefinition(definition, context);
    }

    @Override
    public DefinitionsFactory getDefinitionsFactory() {
        return mgr.getFactory();
    }

    @Override
    public DefinitionsFactory getDefinitionsFactory(String key) {
        DefinitionManager mgr = getProperDefinitionManager(key);
        return mgr.getFactory();
    }

    @Override
    public void setDefinitionsFactory(DefinitionsFactory definitionsFactory) {
        super.setDefinitionsFactory(definitionsFactory);
        mgr.setFactory(definitionsFactory);
    }

    @Override
    public void setDefinitionsFactory(String key, DefinitionsFactory definitionsFactory,
            Map<String, String> initParameters) throws TilesException {
        if (key != null) {
            initializeDefinitionsFactory(definitionsFactory,
                    getResourceString(initParameters), initParameters);
        }
        DefinitionManager mgr = getOrCreateDefinitionManager(key);
        mgr.setFactory(definitionsFactory);
    }
    
    protected void register(TileDefinition definition,
            TilesRequestContext request) throws DefinitionsFactoryException {
        DefinitionManager mgr = getProperDefinitionManager(
                getDefinitionsFactoryKey(request));
        ComponentDefinition def = new ComponentDefinition(definition);
        mgr.addDefinition(def, request);
    }

    /**
     * Returns a definition manager if found, otherwise it will create a new
     * one.
     *
     * @param key The key of the definition manager.
     * @return The needed definition manager.
     */
    protected DefinitionManager getOrCreateDefinitionManager(String key) {
        DefinitionManager mgr = key2definitionManager.get(key);
        if (mgr == null) {
            mgr = new DefinitionManager(DEFINITIONS_ATTRIBUTE_NAME_BASE + key);
            key2definitionManager.put(key, mgr);
        }
        
        return mgr;
    }
    
    /**
     * Returns a definition manager if found.
     *
     * @param key The key of the definition manager.
     * @return The needed definition manager.
     */
    protected DefinitionManager getProperDefinitionManager(String key) {
        DefinitionManager mgr = null;
        
        if (key != null) {
            mgr = key2definitionManager.get(key);
        }
        if (mgr == null) {
            mgr = this.mgr;
        }
        
        return mgr;
    }
}
