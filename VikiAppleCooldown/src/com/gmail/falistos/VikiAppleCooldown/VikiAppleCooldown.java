package com.gmail.falistos.VikiAppleCooldown;

import java.io.File;
import java.text.DecimalFormat;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class VikiAppleCooldown extends JavaPlugin implements Listener {
	
	public String version = "0.1";
	
	public Cooldown cd;
	public Permission perm = null;
	ItemStack enchantedApple = new ItemStack(Material.GOLDEN_APPLE, 1, (short)1);

	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		
		this.setupPermissions();
		
		this.cd = new Cooldown(this.getConfig().getInt("cooldown"));
	}
	
	public void reload()
	{
		this.reloadConfig();
		this.saveConfigFile();
	}
	
	private void saveConfigFile()
	{
	    if (!new File(getDataFolder(), "config.yml").exists()) {
	        getConfig().options().copyDefaults(true);
	        this.saveDefaultConfig();
	        getLogger().info("Creation of default config file");
	    }
	    reloadConfig();
	}
	
	private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            this.perm = permissionProvider.getProvider();
        }
        return (this.perm != null);
    }
	
	@EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent e)
    {
		ItemStack inHand = e.getItem();
		if ((inHand.getType().equals(Material.GOLDEN_APPLE)) && (inHand.getDurability() == this.enchantedApple.getDurability()))
		{
			final Player player = e.getPlayer();
			
			if (!player.hasPermission("vikiapplecooldown.bypass")) {
				if (cd.isUnderCooldown(player))
				{
					e.setCancelled(true);
				}
				else
				{
					cd.resetCooldown(player);
				}
			}
		}
     }
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteract(PlayerInteractEvent e) {
		
		Action eAction = e.getAction();
		 
		if (eAction == Action.RIGHT_CLICK_AIR || eAction == Action.RIGHT_CLICK_BLOCK) {
			
			Player player = e.getPlayer();
			ItemStack inHand = e.getItem();
			
			if (inHand == null || inHand.getType() == Material.AIR)
			{
				return;
			}
			
			if ((inHand.getType().equals(Material.GOLDEN_APPLE)) && (inHand.getDurability() == this.enchantedApple.getDurability()))
			{
				if (!player.hasPermission("vikiapplecooldown.bypass")) {
					if (cd.isUnderCooldown(player))
					{
						e.setCancelled(true);
						player.sendMessage(this.getConfig().getString("messages.onCooldown").replace("%cooldown", this.getRoundDouble(cd.getCurrentCooldown(player))));
					}
				}
			}
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("vkapplecooldown")) {
			
			Player player = null;
			
			if (sender instanceof Player)
			{
				player = (Player) sender;
			}
			
			if (args.length < 1)
			{
				sender.sendMessage(ChatColor.RED+"Usage: /vkac [reload]");
			}
			
			// Reload
			if (args[0].equalsIgnoreCase("reload"))
			{
				 
				if (sender instanceof Player)
				{
					if (!this.perm.has(player, "vikiapplecooldown.reload") && !player.isOp())
					{
						player.sendMessage(ChatColor.RED+"You don't have permission to do this");
						return true;
					}
				}
				
				this.reloadConfig();
				sender.sendMessage(ChatColor.GREEN+"Viki-AppleCooldown successfully reloaded");
			}
			else if (args[0].equalsIgnoreCase("info"))
			{
				sender.sendMessage(ChatColor.GREEN+"Viki-AppleCooldown version " + this.version + " - Created by Falistos/BritaniaCraft (falistos@gmail.com)");
			}
			
		}
		return true;
	}
	  
    private String getRoundDouble(double value)
    {
    	DecimalFormat df = new DecimalFormat("#");
		return df.format(value);
    }
}