package net.einsteinsci.betterbeginnings.register.recipe;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;
import java.util.Map.Entry;

public class KilnRecipeHandler
{
	private static final KilnRecipeHandler INSTANCE = new KilnRecipeHandler();

	private Map<ItemStack, ItemStack> smeltingList = new HashMap<>();
	private Map<ItemStack, Float> experienceList = new HashMap<>();

	private KilnRecipeHandler()
	{
		// nothing here
	}

	public void addLists(Item input, ItemStack itemStack, float experience)
	{
		putLists(new ItemStack(input, 1, OreDictionary.WILDCARD_VALUE), itemStack, experience);
	}

	public static KilnRecipeHandler instance()
	{
		return INSTANCE;
	}

	public void putLists(ItemStack itemStack, ItemStack itemStack2, float experience)
	{
		smeltingList.put(itemStack, itemStack2);
		experienceList.put(itemStack2, experience);
	}

	public static void addRecipe(String input, ItemStack output, float experience)
	{
		for (ItemStack stack : OreDictionary.getOres(input))
		{
			instance().putLists(stack, output, experience);
		}
	}

	public static void addRecipe(Item input, ItemStack output, float experience)
	{
		instance().addLists(input, output, experience);
	}

	public static void addRecipe(Block input, ItemStack output, float experience)
	{
		instance().addLists(Item.getItemFromBlock(input), output, experience);
	}

	public static void addRecipe(ItemStack input, ItemStack output, float experience)
	{
		instance().putLists(input, output, experience);
	}

	public ItemStack getSmeltingResult(ItemStack stack)
	{
		Iterator<Entry<ItemStack, ItemStack> > iterator = smeltingList.entrySet().iterator();
		Entry<ItemStack, ItemStack> entry;

		do
		{
			if (!iterator.hasNext())
			{
				return null;
			}

			entry = iterator.next();
		} while (!canBeSmelted(stack, entry.getKey()));

		return entry.getValue();
	}

	public static Map getSmeltingList()
	{
		return instance().smeltingList;
	}

	private boolean canBeSmelted(ItemStack stack, ItemStack stack2)
	{
		return stack2.getItem() == stack.getItem()
				&& (stack2.getItemDamage() == OreDictionary.WILDCARD_VALUE || stack2.getItemDamage() == stack
				.getItemDamage());
	}

	public float giveExperience(ItemStack stack)
	{
		Iterator<Entry<ItemStack, Float> > iterator = experienceList.entrySet().iterator();
		Entry<ItemStack, Float> entry;

		do
		{
			if (!iterator.hasNext())
			{
				return 0.0f;
			}

			entry = iterator.next();
		} while (!canBeSmelted(stack, entry.getKey()));

		if (stack.getItem().getSmeltingExperience(stack) != -1)
		{
			return stack.getItem().getSmeltingExperience(stack);
		}

		return entry.getValue();
	}
}
