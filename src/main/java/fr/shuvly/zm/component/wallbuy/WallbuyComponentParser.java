package fr.shuvly.zm.component.wallbuy;

import fr.shuvly.zm.component.ComponentFactory;
import fr.shuvly.zm.component.ComponentRegistry;
import fr.shuvly.zm.component.interaction.InteractionTrigger;
import fr.shuvly.zm.component.interaction.TriggerParser;
import fr.shuvly.zm.exception.MapParseException;
import fr.shuvly.zm.map.MapParsingContext;
import fr.shuvly.zm.weapon.ZmWeaponRegistry;
import org.bukkit.configuration.ConfigurationSection;

public class WallbuyComponentParser
    implements ComponentFactory<WallbuyComponent>
{

    @Override
    public WallbuyComponent parse(
        String id,
        ConfigurationSection config,
        MapParsingContext context
    )
    {
        final String weaponId = config.getString("weapon_id");
        final String papWeaponId = config.getString("pap_weapon_id");
        final int weaponCost = config.getInt("weapon_cost");
        final int ammoCost = config.getInt("ammo_cost");
        final int papAmmoCost = config.getInt("pap_ammo_cost");
        final ConfigurationSection triggerSec = config.getConfigurationSection("trigger");

        if (weaponId == null || papWeaponId == null || weaponCost == 0 || ammoCost == 0 || papAmmoCost == 0 || triggerSec == null) {
            throw new MapParseException("Wallbuy '" + id + "' missing mandatory fields.");
        }

        final InteractionTrigger trigger = TriggerParser.parse(triggerSec);

        return new WallbuyComponent(
            id,
            trigger,
            weaponId,
            papWeaponId,
            weaponCost,
            ammoCost,
            papAmmoCost,
            context.weaponRegistry() // Fetch the weapon registry directly from our context record
        );
    }

}
