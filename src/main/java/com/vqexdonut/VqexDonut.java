package com.vqexdonut;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VqexDonut implements ModInitializer {
	public static final String MOD_ID = "vqex-donut";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("VqexDonut 1.21.4 Loaded!");
	}
}
