package com.Patane.util.formables;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.Patane.util.main.PataneUtil;

public class ParticlesUtil {

	public static void createFlameRings(Player player) {
        new BukkitRunnable() {
            double alpha = 0;

            public void run() {
                // Each cycle alpha gets increase by pi / 16 which divides the whole circle into 32 sections
            	// The higher this number, the more sections it is divided into.
                alpha += Math.PI / 16;

                /*
                 * You can add to the location based on the coordinates of a point on the circumference on a circle
                 * The y location is meanwhile altering in a slightly shifted sine curve
                 */
                // You can add to the location based on the coordinates of a point on the circumference on a circle
                Location location = player.getLocation();
                // Angled ring
                // Sin(alpha) on the Y coordinate puts the circle on a 45 degree angle
                Location firstRing = location.clone().add(Math.cos(alpha), Math.sin(alpha), Math.sin(alpha));
                Location secondRing = location.clone().add(Math.cos(alpha + Math.PI), Math.sin(alpha), Math.sin(alpha + Math.PI));
                // Flat ring
//                Location firstRing = location.clone().add(Math.cos(alpha), 0, Math.sin(alpha));
//                Location secondRing = location.clone().add(Math.cos(alpha + Math.PI), 0, Math.sin(alpha + Math.PI));
                
                player.spawnParticle( Particle.FLAME, firstRing, 0, 0, 0, 0, 0 );
                player.spawnParticle( Particle.FLAME, secondRing, 0, 0, 0, 0, 0 );
            }
        }.runTaskTimer(PataneUtil.getInstance(), 0, 1 );
    }
}
