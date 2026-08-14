package fr.shuvly.zm.component.wallbuy;

import fr.shuvly.zm.component.ComponentRegistry;
import fr.shuvly.zm.component.interaction.InteractionTrigger;
import fr.shuvly.zm.component.interaction.TriggerParser;
import fr.shuvly.zm.exception.MapParseException;
import fr.shuvly.zm.weapon.ZmWeaponRegistry;
import org.bukkit.configuration.ConfigurationSection;

public class WallbuyComponentParser
{

    private WallbuyComponentParser() {}


    public static void parse(
        ConfigurationSection wallbuysSection,
        ZmWeaponRegistry weaponRegistry,
        ComponentRegistry componentRegistry
    )
    {
        if (wallbuysSection == null) {
            return;
        }

        for (String wallbuyId : wallbuysSection.getKeys(false)) {
            final ConfigurationSection sec = wallbuysSection.getConfigurationSection(wallbuyId);

            if (sec == null) {
                continue;
            }

            final String weaponId = sec.getString("weapon_id");
            final String papWeaponId = sec.getString("pap_weapon_id");
            final int weaponCost = sec.getInt("weapon_cost");
            final int ammoCost = sec.getInt("ammo_cost");
            final int papAmmoCost = sec.getInt("pap_ammo_cost");
            final ConfigurationSection triggerSec = sec.getConfigurationSection("trigger");

            if (weaponId == null || papWeaponId == null || weaponCost == 0 || papAmmoCost == 0 || ammoCost == 0 || triggerSec == null) {
                throw new MapParseException("Wallbuy '" + wallbuyId + "' missing mandatory fields.");
            }

            final InteractionTrigger trigger = TriggerParser.parse(triggerSec);

            final WallbuyComponent wallbuy = new WallbuyComponent(
                wallbuyId,
                trigger,
                weaponId, papWeaponId,
                weaponCost, ammoCost, papAmmoCost,
                weaponRegistry
            );

            componentRegistry.register(wallbuy);
        }
    }

}
