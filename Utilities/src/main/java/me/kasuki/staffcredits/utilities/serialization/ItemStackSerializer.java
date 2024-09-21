package me.kasuki.staffcredits.utilities.serialization;

import com.cryptomorin.xseries.XMaterial;
import com.google.gson.*;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class ItemStackSerializer implements JsonDeserializer<ItemStack>, JsonSerializer<ItemStack> {

    public JsonElement serialize(final ItemStack item, final Type type, final JsonSerializationContext context) {
        return serialize(item);
    }

    public ItemStack deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        return deserialize(element);
    }

    public static JsonElement serialize(ItemStack item) {
        if (item == null) {
            item = new ItemStack(Material.AIR);
        }

        final JsonObject element = new JsonObject();

        XMaterial type = XMaterial.matchXMaterial(item);

        String material = type.toString();

        element.addProperty("id", material);
        element.addProperty("count", Math.max(1, item.getAmount()));

        if (item.getType() != Material.AIR && item.getAmount() > 0) {
            element.addProperty("nbt", new NBTItem(item).toString());
        }


        if (item.hasItemMeta()) {
            final ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) {
                element.addProperty("name", meta.getDisplayName());
            }
            if (meta.hasLore()) {
                element.add("lore", convertStringList(meta.getLore()));
            }

            if (meta.hasEnchants()) {
                JsonObject object = new JsonObject();

                for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                    object.addProperty(entry.getKey().getName(), entry.getValue());
                }

                element.add("enchants", object);
            }
            if (meta instanceof LeatherArmorMeta) {
                element.addProperty("color", ((LeatherArmorMeta) meta).getColor().asRGB());
            } else if (meta instanceof SkullMeta) {
                element.addProperty("skull", ((SkullMeta) meta).getOwner());
            } else if (meta instanceof BookMeta) {
                element.addProperty("title", ((BookMeta) meta).getTitle());
                element.addProperty("author", ((BookMeta) meta).getAuthor());
                element.add("pages", convertStringList(((BookMeta) meta).getPages()));
            } else if (meta instanceof PotionMeta) {
                if (!((PotionMeta) meta).getCustomEffects().isEmpty()) {
                    element.add("potion-effects", convertPotionEffectList(((PotionMeta) meta).getCustomEffects()));
                }
            } else if (meta instanceof MapMeta) {
                element.addProperty("scaling", ((MapMeta) meta).isScaling());
            } else if (meta instanceof EnchantmentStorageMeta) {
                final JsonObject storedEnchantments = new JsonObject();
                for (final Map.Entry<Enchantment, Integer> entry : ((EnchantmentStorageMeta) meta).getStoredEnchants().entrySet()) {
                    storedEnchantments.addProperty(entry.getKey().getName(), entry.getValue());
                }
                element.add("stored-enchants", storedEnchantments);
            }
        }
        if (!item.getEnchantments().isEmpty()) {
            final JsonObject enchantments = new JsonObject();
            for (final Map.Entry<Enchantment, Integer> entry2 : item.getEnchantments().entrySet()) {
                enchantments.addProperty(entry2.getKey().getName(), entry2.getValue());
            }
            element.add("enchants", enchantments);
        }
        return element;
    }

    public static ItemStack deserialize(final JsonElement object) {
        if (object == null || object.isJsonNull() || !object.isJsonObject()) {
            return new ItemStack(Material.AIR);
        }
        final JsonObject element = (JsonObject) object;
        int count = element.get("count").getAsInt();
        if (count < 1) {
            count = 1;
        }

        Optional<XMaterial> optional = XMaterial.matchXMaterial(element.get("id").getAsString());

        ItemStack item;

        if (optional.isPresent()) {
            item = optional.get().parseItem();
        } else {
            item = new ItemStack(Material.AIR);
        }

        if (item == null) {
            return new ItemStack(Material.AIR, 1);
        }

        item.setAmount(count);

        final ItemMeta meta = item.getItemMeta();
        if (element.has("name")) {
            meta.setDisplayName(element.get("name").getAsString());
        }
        if (element.has("lore")) {
            meta.setLore(convertStringList(element.get("lore")));
        }

        if (element.has("enchants")) {
            Map<Enchantment, Integer> enchantments = new HashMap<>();
            JsonObject jsonObject = element.get("enchants").getAsJsonObject();

            List<String> keys = jsonObject.entrySet()
                    .stream()
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            for (String key : keys) {
                Enchantment enchantment = Enchantment.getByName(key);
                if (enchantment == null) {
                    continue;
                }

                enchantments.put(Enchantment.getByName(key), jsonObject.get(key).getAsInt());
            }

            item.addUnsafeEnchantments(enchantments);
        }

        if (element.has("color")) {
            ((LeatherArmorMeta) meta).setColor(Color.fromRGB(element.get("color").getAsInt()));
        } else if (element.has("skull")) {
            JsonElement jsonElement = element.get("skull");

            if (jsonElement != null && !jsonElement.isJsonNull() && jsonElement.isJsonObject()) {
                ((SkullMeta) meta).setOwner(jsonElement.getAsString());
            }
        } else if (element.has("title")) {
            ((BookMeta) meta).setTitle(element.get("title").getAsString());
            ((BookMeta) meta).setAuthor(element.get("author").getAsString());
            ((BookMeta) meta).setPages(convertStringList(element.get("pages")));
        } else if (element.has("potion-effects")) {
            final PotionMeta potionMeta = (PotionMeta) meta;
            for (final PotionEffect effect : convertPotionEffectList(element.get("potion-effects"))) {
                potionMeta.addCustomEffect(effect, false);
            }
        } else if (element.has("scaling")) {
            ((MapMeta) meta).setScaling(element.get("scaling").getAsBoolean());
        } else if (element.has("stored-enchants")) {
            final JsonObject enchantments = (JsonObject) element.get("stored-enchants");
            for (final Enchantment enchantment : Enchantment.values()) {
                if (enchantments.has(enchantment.getName())) {
                    ((EnchantmentStorageMeta) meta).addStoredEnchant(enchantment, enchantments.get(enchantment.getName()).getAsInt(), true);
                }
            }
        }
        item.setItemMeta(meta);
        if (element.has("enchants")) {
            final JsonObject enchantments = (JsonObject) element.get("enchants");
            for (final Enchantment enchantment : Enchantment.values()) {
                if (enchantments.has(enchantment.getName())) {
                    item.addUnsafeEnchantment(enchantment, enchantments.get(enchantment.getName()).getAsInt());
                }
            }
        }

        if (element.has("nbt")) {
            NBT.modify(item, nbt -> {
                nbt.mergeCompound(NBT.parseNBT(element.get("nbt").getAsString()));
            });
        }

        return item;
    }

    public static JsonArray convertStringList(final Collection<String> strings) {
        final JsonArray ret = new JsonArray();
        for (final String string : strings) {
            ret.add(new JsonPrimitive(string));
        }
        return ret;
    }

    public static List<String> convertStringList(final JsonElement jsonElement) {
        final JsonArray array = jsonElement.getAsJsonArray();
        final List<String> ret = new ArrayList<>();
        for (final JsonElement element : array) {
            ret.add(element.getAsString());
        }
        return ret;
    }

    public static JsonArray convertPotionEffectList(final Collection<PotionEffect> potionEffects) {
        final JsonArray ret = new JsonArray();
        for (final PotionEffect e : potionEffects) {
            ret.add(PotionEffectSerializer.toJson(e));
        }
        return ret;
    }

    public static List<PotionEffect> convertPotionEffectList(final JsonElement jsonElement) {
        if (jsonElement == null) {
            return null;
        }
        if (!jsonElement.isJsonArray()) {
            return null;
        }
        final JsonArray array = jsonElement.getAsJsonArray();
        final List<PotionEffect> ret = new ArrayList<>();
        for (final JsonElement element : array) {
            final PotionEffect e = PotionEffectSerializer.fromJson(element);
            if (e == null) {
                continue;
            }
            ret.add(e);
        }
        return ret;
    }
}
