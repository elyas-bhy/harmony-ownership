package fr.labri.harmony.core.config;

import static fr.labri.harmony.core.config.ConfigProperties.ANALYSES;
import static fr.labri.harmony.core.config.ConfigProperties.DATABASE;
import static fr.labri.harmony.core.config.ConfigProperties.FOLDERS;
import static fr.labri.harmony.core.config.ConfigProperties.MANAGE_CREATE_SOURCES;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import fr.labri.harmony.core.config.model.AnalysisConfiguration;
import fr.labri.harmony.core.config.model.DatabaseConfiguration;
import fr.labri.harmony.core.config.model.FoldersConfiguration;
import fr.labri.harmony.core.config.model.SchedulerConfiguration;
import fr.labri.harmony.core.log.HarmonyLogger;

/**
 * 
 * Reads a global configuration file and adds missing default values to the
 * config (e.g. number of threads, working folders)
 * 
 */
public class GlobalConfigReader {

	private JsonNode globalConfig;
	private ObjectMapper mapper;

	public GlobalConfigReader(String path) {
		mapper = new ObjectMapper();
		try {
			globalConfig = mapper.readTree(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public DatabaseConfiguration getDatabaseConfiguration() {
		JsonNode dbNode = globalConfig.get(DATABASE);
		if (dbNode != null) {
			try {
				return mapper.readValue(dbNode.toString(), DatabaseConfiguration.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new DatabaseConfiguration();
	}

	public List<AnalysisConfiguration> getAnalysisConfigurations() {
		ArrayList<AnalysisConfiguration> configs = new ArrayList<>();
		ArrayNode n = (ArrayNode) globalConfig.get(ANALYSES);
		for (JsonNode c : n) {
			try {
				AnalysisConfiguration config = mapper.readValue(c.toString(), AnalysisConfiguration.class);
				config.setFoldersConfiguration(getFoldersConfig());
				configs.add(config);
			} catch (IOException e) {
				HarmonyLogger.error("Analysis config error");
				e.printStackTrace();
			}
		}
		return configs;

	}

	public SchedulerConfiguration getSchedulerConfiguration() {
		JsonNode n = globalConfig.get(MANAGE_CREATE_SOURCES);
		try {
			return mapper.readValue(n.toString(), SchedulerConfiguration.class);
		} catch (Exception e) {
		}
		return new SchedulerConfiguration();
	}

	public FoldersConfiguration getFoldersConfig() {
		if (globalConfig.has(FOLDERS)) {
			JsonNode n = globalConfig.get(FOLDERS);
			try {
				return mapper.readValue(n.toString(), FoldersConfiguration.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new FoldersConfiguration();
	}

}
