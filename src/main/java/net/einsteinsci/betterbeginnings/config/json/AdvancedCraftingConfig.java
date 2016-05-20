package net.einsteinsci.betterbeginnings.config.json;

import net.einsteinsci.betterbeginnings.util.LogUtil;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class AdvancedCraftingConfig implements IJsonConfig
{
	public static final AdvancedCraftingConfig INSTANCE = new AdvancedCraftingConfig();

	private JsonAdvancedCraftingHandler mainRecipes = new JsonAdvancedCraftingHandler();

	private List<JsonAdvancedCraftingHandler> includes = new ArrayList<>();

	@Override
	public String getSubFolder()
	{
		return "advancedcrafting";
	}

	@Override
	public String getMainJson(File subfolder)
	{
		File mainf = new File(subfolder, "main.json");
		if (!mainf.exists())
		{
			return "{}";
		}

		try
		{
			return new String(Files.readAllBytes(mainf.toPath()));
		}
		catch (IOException e)
		{
			LogUtil.log(Level.ERROR, "IOException occurred opening config/betterbeginnings/advancedcrafting/main.json!");
			LogUtil.log("");
			LogUtil.log(Level.ERROR, e.toString());

			return "{}";
		}
	}

	@Override
	public String getAutoJson(File subfolder)
	{
		return "{}";
	}

	@Override
	public String getCustomJson(File subfolder)
	{
		return "{}";
	}

	@Override
	public List<String> getIncludedJson(File subfolder)
	{
		List<String> res = new ArrayList<>();
		for (String fileName : mainRecipes.getIncludes())
		{
			File incf = new File(subfolder, fileName);
			if (!incf.exists())
			{
				LogUtil.log(Level.ERROR, "Included file not found: config/betterbeginnings/advancedcrafting/" +
					fileName + " - Skipping.");
				continue;
			}

			try
			{
				res.add(new String(Files.readAllBytes(incf.toPath())));
			}
			catch (IOException ex)
			{
				LogUtil.log(Level.ERROR, "IOException occurred opening config/betterbeginnings/advancedcrafting/" + fileName);
				LogUtil.log("");
				LogUtil.log(Level.ERROR, ex.toString());
			}
		}

		return res;
	}

	@Override
	public boolean isOnlyMain()
	{
		return true;
	}

	@Override
	public void loadJsonConfig(FMLInitializationEvent e, String mainJson, String autoJson, String customJson)
	{
		mainRecipes = BBJsonLoader.deserializeObject(mainJson, JsonAdvancedCraftingHandler.class);

		for (JsonAdvancedRecipe j : mainRecipes.getRecipes())
		{
			j.register();
		}
	}

	@Override
	public void loadIncludedConfig(FMLInitializationEvent e, List<String> includedJsons)
	{
		for (String json : includedJsons)
		{
			JsonAdvancedCraftingHandler handler = BBJsonLoader.deserializeObject(json, JsonAdvancedCraftingHandler.class);
			includes.add(handler);

			for (JsonAdvancedRecipe r : handler.getRecipes())
			{
				r.register();
			}
		}
	}

	@Override
	public void savePostLoad(File subfolder)
	{
		String json = BBJsonLoader.serializeObject(mainRecipes);
		try
		{
			File mainf = new File(subfolder, "main.json");
			Files.write(mainf.toPath(), json.getBytes(), StandardOpenOption.CREATE);
		}
		catch (IOException e)
		{
			LogUtil.log(Level.ERROR, "IOException occurred saving config/betterbeginnings/advancedcrafting/main.json!");
			LogUtil.log("");
			LogUtil.log(Level.ERROR, e.toString());
		}
	}

	public JsonAdvancedCraftingHandler getMainRecipes()
	{
		return mainRecipes;
	}
}
