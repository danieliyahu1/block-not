package com.akatsuki.block_not.price;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class CoinGeckoPriceParserTests {

	@Test
	void parsesMultipleCoinsUsd() {
		String body = "{\"cardano\":{\"usd\":0.4567},\"bitcoin\":{\"usd\":64000}}";

		CoinGeckoPriceParser parser = new CoinGeckoPriceParser();
		Map<String, BigDecimal> prices = parser.parseSimplePriceResponse(body, List.of("cardano", "bitcoin"), "usd");

		assertThat(prices).containsEntry("cardano", new BigDecimal("0.4567"));
		assertThat(prices).containsEntry("bitcoin", new BigDecimal("64000"));
	}
}
