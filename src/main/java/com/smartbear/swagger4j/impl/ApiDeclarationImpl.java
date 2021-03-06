/**
 * Copyright 2013 SmartBear Software, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.smartbear.swagger4j.impl;

import com.smartbear.swagger4j.Api;
import com.smartbear.swagger4j.ApiDeclaration;
import com.smartbear.swagger4j.Model;
import com.smartbear.swagger4j.SwaggerVersion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation of the ApiDeclaration interface
 *
 * @see ApiDeclaration
 */

public class ApiDeclarationImpl implements ApiDeclaration {

    private String apiVersion = DEFAULT_API_VERSION;
    private String basePath;
    private SwaggerVersion swaggerVersion = SwaggerVersion.DEFAULT_VERSION;
    private String resourcePath;
    private final List<Api> apiList = new ArrayList<Api>();
    private final Set<String> produces = new HashSet<String>();
    private final Set<String> consumes = new HashSet<String>();

    private final Map<String, Model> models = new HashMap<String, Model>();

    ApiDeclarationImpl(String basePath, String resourcePath) {
        this.basePath = basePath;
        this.resourcePath = resourcePath;
    }

    @Override
    public SwaggerVersion getSwaggerVersion() {
        return swaggerVersion;
    }

    @Override
    public void setSwaggerVersion(SwaggerVersion swaggerVersion) {
        assert swaggerVersion != null : "swaggerVersion can not be null";

        this.swaggerVersion = swaggerVersion;
    }

    @Override
    public String getApiVersion() {
        return apiVersion;
    }

    @Override
    public void setApiVersion(String apiVersion) {
        assert apiVersion != null : "apiVersion can not be null";

        this.apiVersion = apiVersion;
    }

    @Override
    public String getBasePath() {
        return basePath;
    }

    @Override
    public void setBasePath(String basePath) {
        assert basePath != null : "basePath can not be null";

        this.basePath = basePath;
    }

    @Override
    public String getResourcePath() {
        return resourcePath;
    }

    @Override
    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override
    public List<Api> getApis() {
        return Collections.unmodifiableList(apiList);
    }

    @Override
    public void removeApi(Api api) {
        assert api != null : "api can not be null";

        synchronized (apiList) {
            apiList.remove(api);
        }
    }

    @Override
    public Api addApi(String path) {
        assert path != null : "Can not add api with null path";
        assert getApi(path) == null : "Api already exists at path [" + path + "]";

        synchronized (apiList) {
            ApiImpl api = new ApiImpl(this, path);
            apiList.add(api);
            return api;
        }
    }

    @Override
    public Api getApi(String path) {
        assert path != null : "api path can not be null";

        synchronized (apiList) {
            for (Api api : apiList) {
                if (api.getPath().equals(path)) {
                    return api;
                }
            }

            return null;
        }
    }

    @Override
    public Collection<String> getProduces() {
        return Collections.unmodifiableCollection(produces);
    }

    @Override
    public void removeProduces(String produces) {
        this.produces.remove(produces);
    }

    @Override
    public void addProduces(String produces) {
        assert produces != null : "produces can not be null";

        this.produces.add(produces);
    }

    @Override
    public Collection<String> getConsumes() {
        return Collections.unmodifiableCollection(consumes);
    }

    @Override
    public void removeConsumes(String consumes) {
        this.produces.remove(consumes);
    }

    @Override
    public void addConsumes(String consumes) {
        assert consumes != null : "consumes can not be null";
        this.consumes.add(consumes);
    }

    public Collection<Model> getModels() {
        return models.values();
    }

    public Model getModel(String id) {
        return models.get(id);
    }

    public void addModel(Model model) {
        models.put(model.getId(), model);
    }
}
