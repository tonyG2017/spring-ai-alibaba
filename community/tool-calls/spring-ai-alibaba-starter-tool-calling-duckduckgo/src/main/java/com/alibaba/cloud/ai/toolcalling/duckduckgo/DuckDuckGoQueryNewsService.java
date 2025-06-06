/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.cloud.ai.toolcalling.duckduckgo;

import com.alibaba.cloud.ai.toolcalling.common.CommonToolCallUtils;
import com.alibaba.cloud.ai.toolcalling.common.WebClientTool;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;

/**
 * @author 北极星
 * @author sixiyida
 */
public class DuckDuckGoQueryNewsService
		implements Function<DuckDuckGoQueryNewsService.DuckDuckGoQueryNewsRequest, Map<String, Object>> {

	private static final Logger logger = LoggerFactory.getLogger(DuckDuckGoQueryNewsService.class);

	private final WebClientTool webClientTool;

	private final DuckDuckGoProperties properties;

	public DuckDuckGoQueryNewsService(DuckDuckGoProperties properties, WebClientTool webClientTool) {
		this.properties = properties;
		this.webClientTool = webClientTool;
	}

	@Override
	public Map<String, Object> apply(DuckDuckGoQueryNewsRequest request) {
		if (CommonToolCallUtils.isInvalidateRequestParams(request, request.query)) {
			return null;
		}
		return CommonToolCallUtils.handleServiceError("DuckDuckGo", () -> {
			return webClientTool.getWebClient()
				.get()
				.uri(uriBuilder -> uriBuilder.queryParam("api_key", properties.getApiKey())
					.queryParam("engine", "duckduckgo_news")
					.queryParam("q", request.query)
					.queryParam("kl", request.kl())
					.build())
				.acceptCharset(StandardCharsets.UTF_8)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
				})
				.block();
		}, logger);
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonClassDescription("duckduckgo search request")
	public record DuckDuckGoQueryNewsRequest(
			@JsonProperty(required = true,
					value = "q") @JsonPropertyDescription("The query " + "keyword e.g. spring-ai-alibaba") String query,
			@JsonProperty(defaultValue = "us-en") String kl) {
	}

}
