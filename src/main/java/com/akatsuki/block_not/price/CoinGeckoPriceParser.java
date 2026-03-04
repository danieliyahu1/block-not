package com.akatsuki.block_not.price;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;

public class CoinGeckoPriceParser {

	private final JsonParser jsonParser = JsonParserFactory.getJsonParser();

	public Map<String, BigDecimal> parseSimplePriceResponse(String jsonBody, Collection<String> coinIds,
			String vsCurrency) {
		Objects.requireNonNull(jsonBody, "jsonBody");
		Objects.requireNonNull(coinIds, "coinIds");
		Objects.requireNonNull(vsCurrency, "vsCurrency");

		String normalizedVsCurrency = vsCurrency.toLowerCase(Locale.ROOT);

		Map<String, Object> root = jsonParser.parseMap(jsonBody);
		Map<String, BigDecimal> out = new HashMap<>();

		for (String coinId : coinIds) {
			if (coinId == null || coinId.isBlank()) {
				continue;
			}

			Object coinNode = root.get(coinId);
			if (!(coinNode instanceof Map<?, ?> coinMap)) {
				continue;
			}

			Object priceNode = coinMap.get(normalizedVsCurrency);
			if (priceNode == null) {
				continue;
			}

			out.put(coinId, toBigDecimal(priceNode));
		}

		return out;
	}

	private static BigDecimal toBigDecimal(Object node) {
		if (node instanceof BigDecimal bd) {
			return bd;
		}
		if (node instanceof Number num) {
			return new BigDecimal(num.toString());
		}
		return new BigDecimal(String.valueOf(node));
	}
}
