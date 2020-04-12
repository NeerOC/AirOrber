package api;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.Definitions;
import org.rspeer.runetek.api.component.ItemTables;
import org.rspeer.runetek.api.query.ItemQueryBuilder;
import org.rspeer.runetek.event.types.ItemTableEvent;
import org.rspeer.runetek.providers.RSItemTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BankCache {
    private static RSItemTable bankTable = ItemTables.lookup(ItemTables.BANK);

    public static boolean contains(int... idsToFind) {
        return bankTable.contains(idsToFind);
    }

    public static boolean containsAll(int... idsToFind) {
        return bankTable.containsAll(idsToFind);
    }

    public static int getCount(boolean includeStacks, int... idsToFind) {
        return bankTable.getCount(includeStacks, idsToFind);
    }

    public static boolean contains(String... names) {
        return Arrays
                .stream(names)
                .anyMatch(name -> bankTable.contains(Definitions.getItem(name, a -> true).getId()));
    }

    public static boolean containsAll(String... names) {
        return Arrays
                .stream(names)
                .allMatch(name -> bankTable.contains(Definitions.getItem(name, a -> true).getId()));
    }

    public static int getCount(boolean includeStacks, String... names) {
        return Arrays
                .stream(names)
                .map(name -> bankTable.getCount(includeStacks, Definitions.getItem(name, a -> true).getId()))
                .reduce(0, Integer::sum);
    }

    public static ItemQueryBuilder newQuery() {
        List<Item> items = new ArrayList<>();

        for (int i = 0; i < bankTable.getIds().length; i++) {
            items.add(new Item(new InterfaceComponent(null), 0, bankTable.getIds()[i], bankTable.getStackSizes()[i]));
        }

        return new ItemQueryBuilder(() -> items);
    }

    public static boolean hasFetched(){
        return bankTable != null;
    }

    public static void notify(ItemTableEvent itemTableEvent) {
        if (itemTableEvent.getTableKey() == ItemTables.BANK)
            BankCache.bankTable = ItemTables.lookup(ItemTables.BANK);
    }
}