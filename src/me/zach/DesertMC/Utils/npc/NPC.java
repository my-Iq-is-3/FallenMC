package me.zach.DesertMC.Utils.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.*;
import net.minecraft.server.v1_8_R3.WorldSettings.EnumGamemode;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.*;
@SuppressWarnings("unused")
public class NPC {

	private Set<Player> recipients = new HashSet<>();
	int entityID;
	Location location;
	GameProfile gameprofile;
	String displayName;
	boolean tablist;

	public NPC(String name,Location location, Collection<Player> recipients){
		if(recipients != null) this.recipients.addAll(recipients);
		else this.recipients = null;
		displayName = name;
		entityID = (int)Math.ceil(Math.random() * 1000) + 2000;
		gameprofile = new GameProfile(UUID.randomUUID(), name);
		this.location = location;
	}

	public NPC(String name, Location location){
		this(name, location, null);
	}

	public void spawn(boolean tablist){
		this.tablist = tablist;
		PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn();

		setValue(packet, "a", entityID);
		setValue(packet, "b", gameprofile.getId());
		setValue(packet, "c", MathHelper.floor(location.getX() * 32.0D));
		setValue(packet, "d", MathHelper.floor(location.getY() * 32.0D));
		setValue(packet, "e", MathHelper.floor(location.getZ() * 32.0D));
		setValue(packet, "f", (byte) ((int) (location.getYaw() * 256.0F / 360.0F)));
		setValue(packet, "g", (byte) ((int) (location.getPitch() * 256.0F / 360.0F)));
		setValue(packet, "h", 0);
		DataWatcher w = new DataWatcher(null);
		w.a(6,(float)20);
		w.a(10,(byte)127);
		setValue(packet, "i", w);
		if(tablist) addToTablist();
		for(Player player : recipients){
			sendPacket(packet);
		}
	}

	public void destroy(){
		PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entityID);
		rmvFromTablist();
		sendPacket(packet);
	}

	public boolean isTablist(){
		return tablist;
	}

	public void addToTablist(){
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
		PacketPlayOutPlayerInfo.PlayerInfoData data = packet.new PlayerInfoData(gameprofile, 1, EnumGamemode.NOT_SET, CraftChatMessage.fromString(gameprofile.getName())[0]);
		@SuppressWarnings("unchecked")
		List<PacketPlayOutPlayerInfo.PlayerInfoData> players = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) getValue(packet, "b");
		players.add(data);

		setValue(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
		setValue(packet, "b", players);

		sendPacket(packet);
		tablist = true;
	}

	public void rmvFromTablist(){
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
		PacketPlayOutPlayerInfo.PlayerInfoData data = packet.new PlayerInfoData(gameprofile, 1, EnumGamemode.NOT_SET, CraftChatMessage.fromString(gameprofile.getName())[0]);
		@SuppressWarnings("unchecked")
		List<PacketPlayOutPlayerInfo.PlayerInfoData> players = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) getValue(packet, "b");
		players.add(data);

		setValue(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
		setValue(packet, "b", players);

		sendPacket(packet);
		tablist = false;
	}

	public void setSkin(String texture, String signature) {
		this.gameprofile.getProperties().put("textures", new Property("textures", texture, signature));
		reloadNpc();
	}

	/**
	 *  respawn the npc and refresh all comitted changes
	 */
	public void reloadNpc() {
		this.updateProfile();
		PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(this.entityID);
		this.sendPacket(packet);
		this.spawn(false);
	}
	/**
	 *  put items in inventory, see https://www.google.com/search?q=bukkit+inventory+slots&source=lnms&tbm=isch&sa=X&ved=0ahUKEwjr9v_FxvjaAhUFMuwKHQi7ALUQ_AUICigB&biw=1920&bih=974#imgrc=QUECAbUohgZxbM:
	 *  for more info
	 */
	public void setEquipment(NPCEquipmentSlot slot, ItemStack item) {
		PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment();
		this.setValue(packet, "a", this.entityID);
		this.setValue(packet, "b", slot.value);
		this.setValue(packet, "c", item);
		this.sendPacket(packet);
	}
	/**
	 *  Update/Refresh the gameprofile that contains UUID, Name, Skin.
	 */
	private void updateProfile() {
		Property skin = this.getSkin();
		this.gameprofile = new GameProfile(this.gameprofile.getId(), displayName);
		if (skin != null)
			this.setSkin(skin.getValue(), skin.getSignature());
	}

	/**
	 *  get the signature and skin from the gameprofile
	 */
	public Property getSkin() {
		if (this.gameprofile.getProperties().isEmpty())
			return null;
		return (Property) this.gameprofile.getProperties().get("textures").toArray()[0];
	}

	public enum NPCEquipmentSlot {
		MAINHAND(0),
		BOOTS(1),
		LEGGINGS(2),
		CHESTPLATE(3),
		HELMET(4);
		public int value;
		NPCEquipmentSlot(int value){
			this.value = value;
		}
	}

	public void setValue(Object obj,String name,Object value){
		try{
			Field field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);
			field.set(obj, value);
		}catch(Exception e){e.printStackTrace();}
	}

	public Object getValue(Object obj,String name){
		try{
			Field field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);
			return field.get(obj);
		}catch(Exception e){e.printStackTrace();}
		return null;
	}

	private void sendPacket(Packet<?> packet, Player player){
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	public void sendPacket(Packet<?> packet){
		for(Player player : recipients == null ? Bukkit.getOnlinePlayers() : recipients){
			sendPacket(packet,player);
		}
	}

	public void addRecipient(Player player){
		recipients.add(player);

	}

	public void removeRecipient(Player player){
		recipients.remove(player);
	}

	public void clearRecipients(){
		recipients.clear();
	}

	public void sendToAllOnline(boolean all){
		if(all && recipients != null) recipients = null;
		else if(recipients == null) recipients = new HashSet<>();
	}
}