package fr.labri.harmony.analysis.ownership;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import fr.labri.harmony.core.analysis.AbstractAnalysis;
import fr.labri.harmony.core.config.model.AnalysisConfiguration;
import fr.labri.harmony.core.dao.Dao;
import fr.labri.harmony.core.log.HarmonyLogger;
import fr.labri.harmony.core.model.Action;
import fr.labri.harmony.core.model.Author;
import fr.labri.harmony.core.model.Data;
import fr.labri.harmony.core.model.Item;
import fr.labri.harmony.core.model.Source;

public class OwnershipAnalysis extends AbstractAnalysis {

	// Contributors above this threshold value 
	// are considered as major contributors
	private final double THRESHOLD = 0.05;
	
	private final String BUGTRACKER_MAP = "/res/bugtracker_mapping.txt";
	private final String[] bugtrackers = {
			"/res/bugzilla/wine/08-09_08-10.xml",
			"/res/bugzilla/wine/08-10_08-11.xml",
			"/res/bugzilla/wine/08-11_08-12.xml",
			"/res/bugzilla/wine/08-12_10-12.xml",
			"/res/bugzilla/wine/10-12_12-12.xml",
			"/res/bugzilla/wine/01-13_05-13.xml"};

	private ArrayList<Component> components;
	private HashMap<Component, List<Item>> componentSource;

	public OwnershipAnalysis() {
		super();
		components = new ArrayList<Component>();
		componentSource = new HashMap<Component, List<Item>>();
	}

	public OwnershipAnalysis(AnalysisConfiguration config, Dao dao, Properties properties) {
		super(config, dao, properties);
		components = new ArrayList<Component>();
		componentSource = new HashMap<Component, List<Item>>();
	}

	private void initMapping() throws IOException {
		File input = new File(BUGTRACKER_MAP);
		BufferedReader reader = new BufferedReader(new FileReader(input));
		String line;
		String[] split;
		int id = 1;
		while ((line = reader.readLine()) != null) {
			split = line.split(":");
			components.add(new Component(id++, split[0], "^" + split[1].trim() + ".*$"));
		}
		reader.close();
	}

	private int getBugCount(String component) {
		int count = 0;
		try {
			for (String file : bugtrackers) {
				String[] cmd = { "/bin/sh", "-c", "grep \"<component>" + component + "</component>\" " + file + " | wc -l" };
				Process p = Runtime.getRuntime().exec(cmd);
				BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String result = r.readLine();
				p.waitFor();
				r.close();
				count += Integer.parseInt(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
			count = 0;
		}
		return count;
	}

	@Override
	public void runOn(Source src) {
		HarmonyLogger.info("Starting ownership analysis. " + src.getItems().size() + " items to analyze.");
		try {
			initMapping();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Assign items to components if any
		for (Item item : src.getItems()) {	
			for (Component c : components) {
				if (item.getNativeId().matches(c.getPath())) {
					if (componentSource.containsKey(c))
						componentSource.get(c).add(item);
					else {
						List<Item> al = new ArrayList<Item>();
						al.add(item);
						componentSource.put(c, al);
					}
				}
			}
		}
		
		HarmonyLogger.info("Analyzing " + componentSource.size() + " components.");
		
		for (Entry<Component, List<Item>> entry : componentSource.entrySet()) {
			Component c = entry.getKey();
			int actions = 0;
			int majorContributor = 0;
			int minorContributor = 0;
			double ownership = 0;
			Author owner = null;
			HashMap<Author, List<Action>> authors = new HashMap<Author, List<Action>>();
			
			// Retrieve all contributors of this component
			for (Item item : entry.getValue()) {
				actions += item.getActions().size();
				for (Action ac : item.getActions()) {
					for (Author a : ac.getEvent().getAuthors()) {
						if (authors.containsKey(a)) authors.get(a).add(ac);
						else {
							List<Action> al = new ArrayList<Action>();
							al.add(ac);
							authors.put(a, al);
						}
					}
				}
			}
			
			// Calculate ownership metrics for each author
			for (Author a : authors.keySet()) {
				int authorActions = authors.get(a).size();
				double ratio = (double) authorActions/actions; 
				if (ratio > THRESHOLD) majorContributor++;
				else minorContributor++;

				if (ratio > ownership) {
					owner = a;
					ownership = ratio;
				}

				ContributionData cdata = new ContributionData();
				cdata.setComponentId(c.getId());
				cdata.setContributor(a.getId());
				cdata.setContribution(ratio);
				dao.saveData(this, cdata, Data.AUTHOR, a.getId());
			}
			
			ComponentData data = new ComponentData();
			data.setName(c.getName());
			data.setBugCount(getBugCount(c.getName()));
			data.setMajorContributor(majorContributor);
			data.setMinorContributor(minorContributor);
			data.setOwnership(ownership);
			if (owner != null) 
				data.setOwner(owner.getId());
			dao.saveData(this, data, Data.ITEM, c.getId());
		}
		

		HarmonyLogger.info("Finished ownership analysis.");
	}

}
