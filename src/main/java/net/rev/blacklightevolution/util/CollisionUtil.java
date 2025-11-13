package net.rev.blacklightevolution.util;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;

import java.util.UUID;

public class CollisionUtil {
    private static final String TEAM_NAME = "no_entity_collision";

    private CollisionUtil() {
    }

    public static void enableNoEntityCollision(Mob mob) {
        Scoreboard scoreboard = mob.level().getScoreboard();
        PlayerTeam team = scoreboard.getPlayerTeam(TEAM_NAME);
        if (team == null) {
            team = scoreboard.addPlayerTeam(TEAM_NAME);
            team.setCollisionRule(Team.CollisionRule.NEVER);
        }
        String entry = mob.getStringUUID();

        PlayerTeam current = scoreboard.getPlayersTeam(entry);
        if (current != team) {
            if (current != null) {
                scoreboard.removePlayerFromTeam(entry, current);
            }
            scoreboard.addPlayerToTeam(entry, team);
        }
    }

    public static void disableNoEntityCollision(Mob mob) {
        Scoreboard scoreboard = mob.level().getScoreboard();
        String entry = mob.getStringUUID();
        PlayerTeam current = scoreboard.getPlayersTeam(entry);
        if (current != null) {
            scoreboard.removePlayerFromTeam(entry, current);
        }
    }

    public static void disableNoEntityCollision(Scoreboard scoreboard, UUID mobUUID) {
        String entry = mobUUID.toString();
        PlayerTeam current = scoreboard.getPlayersTeam(entry);
        if (current != null) {
            scoreboard.removePlayerFromTeam(entry, current);
        }
    }

}
