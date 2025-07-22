package com.invadermonky.survivaltools.compat.bloodmagic;

import WayofTime.bloodmagic.compat.guideapi.GuideBloodMagic;
import WayofTime.bloodmagic.compat.guideapi.entry.EntryText;
import amerifrance.guideapi.api.IPage;
import amerifrance.guideapi.api.impl.Book;
import amerifrance.guideapi.api.impl.abstraction.CategoryAbstract;
import amerifrance.guideapi.api.impl.abstraction.EntryAbstract;
import amerifrance.guideapi.api.util.PageHelper;
import amerifrance.guideapi.api.util.TextHelper;
import amerifrance.guideapi.page.PageText;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BloodMagicUtils {
    private static final Map<String, Map<ResourceLocation, EntryAbstract>> guideAdditions = new LinkedHashMap<>();

    /**
     * Adds an entry to the Blood Magic Sanguine Scientiem guide.
     *
     * @param category       valid categories - alchemy, architect, demon, ritual
     * @param guideEntryName The entry name. This will generate the localization key for the book text.
     */
    public static void addGuideEntry(String category, String guideEntryName) {
        String categoryName = "guide.bloodmagic.category." + category;
        String keyBase = "guide.bloodmagic.entry." + category + ".";
        List<IPage> pages = new ArrayList<>(PageHelper.pagesForLongText(TextHelper.localize(keyBase + guideEntryName + ".info"), 370));
        if (!guideAdditions.containsKey(categoryName))
            guideAdditions.put(categoryName, new LinkedHashMap<>());
        guideAdditions.get(categoryName).put(new ResourceLocation(keyBase + guideEntryName), new EntryText(pages, TextHelper.localize(keyBase + guideEntryName), true));
    }

    public static void buildGuideEntries() {
        guideAdditions.forEach((category, entries) -> {
            entries.forEach((loc, entry) -> {
                for (IPage page : entry.pageList) {
                    ((PageText) page).setUnicodeFlag(true);
                }
            });
        });

        Book guideBook = GuideBloodMagic.GUIDE_BOOK;
        List<CategoryAbstract> categoryList = guideBook.getCategoryList();
        for (CategoryAbstract category : categoryList) {
            Map<ResourceLocation, EntryAbstract> entries = guideAdditions.get(category.name);
            if (entries != null && !entries.isEmpty()) {
                category.addEntries(entries);
            }
        }
    }

}
