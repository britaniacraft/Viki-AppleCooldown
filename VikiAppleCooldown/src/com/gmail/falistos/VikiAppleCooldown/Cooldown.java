package com.gmail.falistos.VikiAppleCooldown;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

public class Cooldown {

	private Map<String, Long> timer = new HashMap<String, Long>();
	private int cooldownBase;
	
	public Cooldown(int cooldownBase)
	{
		this.cooldownBase = cooldownBase;
	}
	
	public double getCooldownBase() { return this.cooldownBase; }
	
	public double getCooldownBonus(Player player) { return 0.0; }
	
	public double getRealCooldown(Player player) { return this.getCooldownBase() - this.getCooldownBonus(player); }
	
	public double getCurrentCooldown(Player player)
	{
		String playerName = player.getName();
		
	    if (!timer.containsKey(playerName)) {
		    timer.put(playerName, Long.valueOf(0L));
		    return 0;
		}

	    double baseCooldown = 1000.0D * getRealCooldown(player);
	    
	    double cooldown = (baseCooldown - System.currentTimeMillis() + ((Long)timer.get(playerName)).longValue()) / 1000L + 1L;
	
	    if (cooldown <= 0) { return 0; }
	    
	    return cooldown;
	}
	
	public boolean isUnderCooldown(Player player)
	{
		double cooldown = getCurrentCooldown(player);
		if (cooldown > 0) return true;
		return false;
	}
	
	public void resetCooldown(Player player) {
		timer.put(player.getName(), Long.valueOf(System.currentTimeMillis()));
	}
	
	public void reduceCooldown(Player player, long time)
	{
		if (!timer.containsKey(player.getName())) return;
		if (timer.get(player.getName()) <= 1L) return;

		timer.put(player.getName(), timer.get(player.getName()) - time);
	}
	
	public void increaseCooldown(Player player, long time)
	{
		if (!timer.containsKey(player.getName())) return;
		if (timer.get(player.getName()) + time > this.getRealCooldown(player) * 1000.0D) return;

		timer.put(player.getName(), timer.get(player.getName()) + time);
	}
	
}
