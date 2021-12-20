package yaya.miningbuddies.GUI.Widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

//Name sounds weird, but this List Entry is a Horizontal List so-
public class HorizontalListListEntry<T extends ElementListWidget.Entry<T>> extends ElementListWidget.Entry<HorizontalListListEntry<T>>
{
	MinecraftClient client;
	List<T> entries = new ArrayList<>();
	int itemWidth, offsetX;
	
	public HorizontalListListEntry(MinecraftClient client, int itemWidth, int offsetX)
	{
		this.client = client;
		this.itemWidth = itemWidth;
		this.offsetX = offsetX;
	}
	
	public void add(T entry)
	{
		entries.add(entry);
	}
	
	@Override
	public List<? extends Selectable> selectableChildren()
	{
		List<Selectable> list = new ArrayList<>();
		for(ElementListWidget.Entry<T> entry : entries)
		{
			list.addAll(entry.selectableChildren());
		}
		return list;
	}
	
	@Override
	public List<? extends Element> children()
	{
		List<Element> list = new ArrayList<>();
		for(ElementListWidget.Entry<T> entry : entries)
		{
			list.addAll(entry.children());
		}
		return list;
	}
	
	@Override
	public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta)
	{
		for(int row = 0; row < entries.size(); row++)
		{
			entries.get(row).render(matrices, index, y, x + row * this.itemWidth + offsetX, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);
		}
	}
}
