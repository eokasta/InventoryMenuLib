package dev.arantes.inventorymenulib;

import dev.arantes.inventorymenulib.buttons.ClickAction;
import dev.arantes.inventorymenulib.buttons.ItemButton;
import dev.arantes.inventorymenulib.menus.InventoryGUI;
import dev.arantes.inventorymenulib.menus.PaginatedGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class PaginatedGUIBuilder {
    private String name;
    private final char[] shape;
    private List<ItemButton> content;
    private ClickAction contentDefaultAction;
    private ItemButton[] hotbar = new ItemButton[9];
    private ItemButton borderButton;
    private ItemStack nextPageItem;
    private ItemStack previousPageItem;

    public PaginatedGUIBuilder(String name, String shape) {
        this.name = name;
        this.shape = shape.toCharArray();

        if (this.shape.length > 45 || (this.shape.length % 9) != 0) {
            throw new IndexOutOfBoundsException();
        }
    }

    public PaginatedGUIBuilder setBorder(Material material, int amount, String name, String... lore) {
        return setBorder(new ItemButton(material, amount, name, lore));
    }

    public PaginatedGUIBuilder setBorder(ItemButton button) {
        this.borderButton = button;
        return this;
    }


    public PaginatedGUIBuilder setNextPageItem(Material material, int amount, String name, String... lore) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);

        return setNextPageItem(item);
    }

    public PaginatedGUIBuilder setNextPageItem(ItemStack button) {
        this.nextPageItem = button;
        return this;
    }

    public PaginatedGUIBuilder setPreviousPageItem(Material material, int amount, String name, String... lore) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);

        return setPreviousPageItem(item);
    }

    public PaginatedGUIBuilder setPreviousPageItem(ItemStack button) {
        this.previousPageItem = button;
        return this;
    }

    public PaginatedGUIBuilder setHotbarButton(byte pos, ItemButton button) {
        if (pos > 8) {
            throw new IndexOutOfBoundsException();
        }

        hotbar[pos] = button;
        return this;
    }

    public PaginatedGUIBuilder setContent(List<ItemButton> content) {
        this.content = content;
        return this;
    }

    public PaginatedGUIBuilder setContent(ItemButton... content) {
        this.content = Arrays.asList(content);
        return this;
    }

    public PaginatedGUIBuilder setContentDefaultAction(ClickAction action) {
        this.contentDefaultAction = action;
        return this;
    }

    public PaginatedGUI build() {
        int contentSize = 0;
        for (char c : shape) {
            if (c == '#') {
                contentSize++;
            }
        }

        final int amountOfPages = (int) ((content.size() / (float) contentSize) + 0.99);

        PaginatedGUI paginatedGUI = new PaginatedGUI();

        int currentItem = 0;
        for (int pageI = 0; pageI < amountOfPages; pageI++) {
            InventoryGUI page = new InventoryGUI(
                    name.replace("{page}", pageI + ""),
                    (shape.length + 9)
            );

            for (int i = 0; i < shape.length; i++) {
                final char current = shape[i];

                if (current == '>') {
                    final ItemButton btn = new ItemButton(nextPageItem);
                    btn.addAction(ClickType.RIGHT, (InventoryClickEvent e) ->
                            paginatedGUI.showNext((Player) e.getWhoClicked()));

                    btn.addAction(ClickType.LEFT, (InventoryClickEvent e) ->
                            paginatedGUI.showNext((Player) e.getWhoClicked()));

                    page.setButton(i, btn);
                    continue;
                }

                if (current == '<') {
                    final ItemButton btn = new ItemButton(previousPageItem);
                    btn.addAction(ClickType.RIGHT, (InventoryClickEvent e) ->
                            paginatedGUI.showPrevious((Player) e.getWhoClicked()));

                    btn.addAction(ClickType.LEFT, (InventoryClickEvent e) ->
                            paginatedGUI.showPrevious((Player) e.getWhoClicked()));

                    page.setButton(i, btn);
                    continue;
                }

                if (current == '#') {
                    if (currentItem < content.size()) {
                        ItemButton cItem = content.get(currentItem++);
                        if (cItem.getDefaultAction() == null) {
                            cItem.setDefaultAction(contentDefaultAction);
                        }

                        page.setButton(i, cItem);
                    }
                    continue;
                }
                if (borderButton != null) {
                    page.setButton(i, borderButton);
                }
            }

            for (int hotbarI = 0; hotbarI < hotbar.length; hotbarI++) {
                final ItemButton item = hotbar[hotbarI];
                if (item != null) {
                    page.setButton(shape.length + hotbarI, item);
                }
            }

            paginatedGUI.addPage(page);
        }

        return paginatedGUI;
    }
}