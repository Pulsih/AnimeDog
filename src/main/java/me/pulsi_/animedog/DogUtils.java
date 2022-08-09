package me.pulsi_.animedog;

import org.bukkit.entity.Player;

public class DogUtils {

    public static String getDog(Player p) {
        return AnimeDog.getInstance().getPlayerData().getString(p.getUniqueId() + ".Dog");
    }

    public static boolean hasDog(Player p) {
        return getDog(p) != null;
    }
}