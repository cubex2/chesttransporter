package cubex2.mods.chesttransporter;

public enum TransporterType
{
    WOOD(1, "", "stickWood"),
    IRON(9, "_iron", "ingotIron"),
    GOLD(19, "_gold", "ingotGold"),
    DIAMOND(79, "_diamond", "gemDiamond"),
    COPPER(7, "_copper", "ingotCopper"),
    SILVER(19, "_silver", "ingotSilver"),
    TIN(7, "_tin", "ingotTin"),
    OBSIDIAN(39, "_obsidian", "obsidian");

    public final int maxDamage;
    public final String iconName;
    public final String nameSuffix;
    public final String recipeMaterial;

    TransporterType(int maxDamage, String nameSuffix, String recipeMaterial)
    {
        this.maxDamage = maxDamage;
        this.iconName = name().toLowerCase();
        this.nameSuffix = nameSuffix;
        this.recipeMaterial = recipeMaterial;
    }

    public String spawnerConfigName()
    {
        return "spawnerWith" + iconName.substring(0, 1).toUpperCase() + iconName.substring(1);
    }
}
