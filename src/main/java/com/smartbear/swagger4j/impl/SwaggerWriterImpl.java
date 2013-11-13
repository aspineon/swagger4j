/**
 *  Copyright 2013 SmartBear Software, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.smartbear.swagger4j.impl;

import com.smartbear.swagger4j.*;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

/**
 * Default implementation of the SwaggerWriter interface
 *
 * @see SwaggerWriter
 */

public class SwaggerWriterImpl implements SwaggerWriter {

    private final SwaggerFormat format;

    public SwaggerWriterImpl(SwaggerFormat format) {
        this.format = format;
    }

    @Override
    public void writeApiDeclaration(ApiDeclaration declaration, Writer writer) throws IOException {
        SwaggerGenerator w = SwaggerGenerator.newGenerator( writer, format );

        SwaggerVersion swaggerVersion = declaration.getSwaggerVersion();
        Constants constants = Constants.get(swaggerVersion);
        w.addString(Constants.SWAGGER_VERSION, swaggerVersion.getIdentifier());
        w.addString(constants.API_VERSION, declaration.getApiVersion());
        w.addString(constants.BASE_PATH, declaration.getBasePath());
        w.addString(constants.RESOURCE_PATH, declaration.getResourcePath());

        if( swaggerVersion != SwaggerVersion.V1_1 )
        {
            Collection<String> produces = declaration.getProduces();
            if( !produces.isEmpty())
                w.addArray(constants.PRODUCES, produces.toArray(new String[produces.size()]));

            Collection<String> consumes = declaration.getConsumes();
            if( !consumes.isEmpty())
                w.addArray(constants.CONSUMES, consumes.toArray(new String[consumes.size()]));
        }

        for (Api api : declaration.getApis()) {
            SwaggerGenerator aw = w.addArrayObject(constants.APIS);
            aw.addString(constants.PATH, api.getPath());
            aw.addString(constants.DESCRIPTION, api.getDescription());

            for (Operation operation : api.getOperations()) {
                SwaggerGenerator ow = aw.addArrayObject(constants.OPERATIONS);
                ow.addString(constants.NICKNAME, operation.getNickName());
                ow.addString(constants.METHOD, operation.getMethod().name());
                ow.addString(constants.SUMMARY, operation.getSummary());
                ow.addString(constants.NOTES, operation.getNotes());
                ow.addString(constants.RESPONSE_CLASS, operation.getResponseClass());

                for (Parameter parameter : operation.getParameters()) {
                    SwaggerGenerator pw = ow.addArrayObject(constants.PARAMETERS);
                    pw.addString(constants.NAME, parameter.getName());
                    pw.addString(constants.PARAM_TYPE, parameter.getParamType().name());
                    pw.addBoolean(constants.ALLOW_MULTIPLE, parameter.isAllowMultiple());
                    pw.addString(constants.DESCRIPTION, parameter.getDescription());
                    pw.addBoolean(constants.REQUIRED, parameter.isRequired());
                    pw.addString(constants.TYPE, parameter.getType());
                }

                for (ResponseMessage responseMessage : operation.getResponseMessages()) {
                    SwaggerGenerator ew = ow.addArrayObject(constants.RESPONSE_MESSAGES);
                    ew.addInt(constants.CODE, responseMessage.getCode());
                    ew.addString(constants.MESSAGE, responseMessage.getMessage());

                    if( swaggerVersion != SwaggerVersion.V1_1)
                        ew.addString( constants.RESPONSE_MODEL, responseMessage.getResponseModel() );
                }

                Collection<String> produces = operation.getProduces();
                if( !produces.isEmpty())
                    ow.addArray(constants.PRODUCES, produces.toArray(new String[produces.size()]));

                Collection<String> consumes = operation.getConsumes();
                if( !consumes.isEmpty())
                    ow.addArray(constants.CONSUMES, consumes.toArray(new String[consumes.size()]));
            }
        }

        w.finish();
    }

    @Override
    public void writeResourceListing(ResourceListing listing, Writer writer) throws IOException {
        SwaggerGenerator w = SwaggerGenerator.newGenerator( writer, format );

        Constants constants = Constants.get(listing.getSwaggerVersion());
        w.addString(constants.API_VERSION, listing.getApiVersion());
        w.addString(Constants.SWAGGER_VERSION, listing.getSwaggerVersion().getIdentifier());
        w.addString(constants.BASE_PATH, listing.getBasePath());

        for (ResourceListing.ResourceListingApi api : listing.getApis()) {
            SwaggerGenerator sw = w.addArrayObject(constants.APIS);
            sw.addString(constants.DESCRIPTION, api.getDescription());
            sw.addString(constants.PATH, api.getPath());
        }

        if( listing.getSwaggerVersion() != SwaggerVersion.V1_1 )
        {
            Info info = listing.getInfo();
            SwaggerGenerator sw = w.addObject(constants.INFO);
            sw.addString( constants.INFO_TITLE, info.getTitle() );
            sw.addString( constants.INFO_DESCRIPTION, info.getDescription() );
            sw.addString( constants.INFO_TERMSOFSERVICEURL, info.getTermsOfServiceUrl() );
            sw.addString( constants.INFO_CONTACT, info.getContact() );
            sw.addString( constants.INFO_LICENSE, info.getLicense() );
            sw.addString( constants.INFO_LICENSE_URL, info.getLicenseUrl() );
        }

        w.finish();
    }

    @Override
    public SwaggerFormat getFormat() {
        return format;
    }

    @Override
    public void writeSwagger(SwaggerStore store, ResourceListing resourceListing) throws IOException {
        Writer writer = store.createResource("api-docs." + format.getExtension());
        writeResourceListing(resourceListing, writer);

        for (ResourceListing.ResourceListingApi api : resourceListing.getApis()) {
            ApiDeclaration declaration = api.getDeclaration();
            String path = Utils.createFileNameFromPath(api.getPath(), format);

            writer = store.createResource(path);
            writeApiDeclaration(declaration, writer);
        }
    }
}
