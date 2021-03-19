/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.softwarefactory.keycloak.providers.events.http;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.lang.Exception;

import okhttp3.*;
import okhttp3.OkHttpClient.Builder;

import java.io.IOException;

/**
 * @author <a href="mailto:jessy.lenne@stadline.com">Jessy Lenne</a>
 */
public class HTTPEventListenerProvider implements EventListenerProvider {
	private final OkHttpClient httpClient = new OkHttpClient();
    private Set<EventType> excludedEvents;
    private Set<OperationType> excludedAdminOperations;
    private String serverUri;
    private String username;
    private String password;
    public static final String publisherId = "keycloak";
    public String TOPIC;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public HTTPEventListenerProvider(Set<EventType> excludedEvents, Set<OperationType> excludedAdminOperations, String serverUri, String username, String password, String topic) {
        this.excludedEvents = excludedEvents;
        this.excludedAdminOperations = excludedAdminOperations;
        this.serverUri = serverUri;
        this.username = username;
        this.password = password;
        this.TOPIC = topic;
    }

    @Override
    public void onEvent(Event event) {
        // Ignore excluded events
        if (excludedEvents != null && excludedEvents.contains(event.getType())) {
            return;
        } else {
            String stringEvent = toString(event);
            try {
                RequestBody formBody = RequestBody.create(JSON, stringEvent);

                // RequestBody formBody = new FormBody.Builder()
                // .add("json", stringEvent)
                // .build();

                okhttp3.Request.Builder builder = new Request.Builder()
                        .url(this.serverUri)
                        .addHeader("User-Agent", "KeycloakHttp Bot");


                if (this.username != null && this.password != null) {
                	builder.addHeader("Authorization", "Basic " + this.username + ":" + this.password.toCharArray());
                }

                Request request = builder.post(formBody)
                        .build();

            	Response response = httpClient.newCall(request).execute();

            	if (!response.isSuccessful()) {
            		throw new IOException("Unexpected code " + response);
            	}

                // Get response body
                System.out.println(response.body().string());
            } catch(Exception e) {
                // ?
                System.out.println("UH OH!! " + e.toString());
                e.printStackTrace();
                return;
            }
        }
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        // Ignore excluded operations
        if (excludedAdminOperations != null && excludedAdminOperations.contains(event.getOperationType())) {
            return;
        } else {
            String stringEvent = toString(event);
            try {
                RequestBody formBody = RequestBody.create(JSON, stringEvent);

                // RequestBody formBody = new FormBody.Builder()
                // .add("json", stringEvent)
                // .build();

                okhttp3.Request.Builder builder = new Request.Builder()
                        .url(this.serverUri)
                        .addHeader("User-Agent", "KeycloakHttp Bot");


                if (this.username != null && this.password != null) {
                	builder.addHeader("Authorization", "Basic " + this.username + ":" + this.password.toCharArray());
                }

                Request request = builder.post(formBody)
                        .build();

            	Response response = httpClient.newCall(request).execute();

            	if (!response.isSuccessful()) {
            		throw new IOException("Unexpected code " + response);
            	}

                // Get response body
                System.out.println(response.body().string());
            } catch(Exception e) {
                // ?
                System.out.println("UH OH!! " + e.toString());
                e.printStackTrace();
                return;
            }
        }
    }


    private String toString(Event event) {
        JSONObject json = new JSONObject();
        json.put("type", event.getType());
        json.put("realm_id", event.getRealmId());
        json.put("client_id", event.getClientId());

        json.put("user_id", event.getUserId());
        json.put("ip_address", event.getIpAddress());
        json.put("client_id", event.getClientId());

        if (event.getError() != null) {
            json.put("error", event.getError());
        }

        JSONObject details = new JSONObject();

        if (event.getDetails() != null) {
            for (Map.Entry<String, String> e : event.getDetails().entrySet()) {
                details.put(e.getKey(), e.getValue());
            }
        }

        json.put("details", details);

        return json.toString(2);
    }

    private String toString(AdminEvent adminEvent) {
        JSONObject json = new JSONObject();
        json.put("type", adminEvent.getOperationType());
        json.put("realm_id", adminEvent.getAuthDetails().getRealmId());
        json.put("client_id", adminEvent.getAuthDetails().getClientId());

        json.put("user_id", adminEvent.getAuthDetails().getUserId());
        json.put("ip_address", adminEvent.getAuthDetails().getIpAddress());
        json.put("resource_path", adminEvent.getResourcePath());

        if (adminEvent.getError() != null) {
            json.put("error", adminEvent.getError());
        }

        return json.toString(2);
    }

    @Override
    public void close() {
    }

}
