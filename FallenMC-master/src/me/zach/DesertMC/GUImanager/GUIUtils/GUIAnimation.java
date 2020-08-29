package me.zach.DesertMC.GUImanager.GUIUtils;

import me.zach.DesertMC.DesertMain;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class GUIAnimation  implements Listener {
    int in = 0;
    int interval;
    int size;
    public GUIAnimation(int invSize, int tickInterval) throws Exception {
        if(tickInterval >= 1) interval = tickInterval;
        else throw new Exception("When setting GUIAnimation tickInterval, it must be greater than or equal to 1.");

        if((invSize) % 9 == 0) size = invSize;
        else throw new Exception("When setting the size of the GUIAnimation, it must be a multiple of nine.");
    }

    private final ArrayList<Inventory> frames = new ArrayList<>();
    public void addFrame(Inventory i) throws IndexOutOfBoundsException {
        if(i.getSize() == size) {
            frames.add(i);
        }else {
            throw new IndexOutOfBoundsException("Size of added inventory does not equal initialized size!");
        }
    }
    public void removeFrame(int frameIndex) throws IndexOutOfBoundsException {
        if(frames.size() - 1 >= frameIndex) {
            frames.remove(frameIndex);
        }else{
            throw new IndexOutOfBoundsException("Index must exist to remove frame!");
        }
    }
    public void removeFrame(Inventory toRemove) throws Exception {
        if(frames.contains(toRemove)) frames.remove(toRemove);
        else throw new Exception("Inventory must exist to remove it!");
    }
    public void setFrame(int index, Inventory frame) throws IndexOutOfBoundsException {
        if(frames.size() - 1 >= index) frames.set(index, frame);
        else throw new IndexOutOfBoundsException("Index must exist to set frame!");
    }
    public void setInterval(int ticks) throws Exception {
       if(ticks >= 1) interval = ticks;
       else throw new Exception("When setting GUIAnimation interval, ticks must be greater than or equal to one");
    }

    public void play(Inventory i) {

        if(i.getSize() == size) {

            new BukkitRunnable(){
                @Override
                public void run() {

                    Inventory refInv = frames.get(in);
                    for(int inc = 0; inc < size; inc++){
                        if(!refInv.getItem(inc).getType().equals(Material.AIR)) {
                            i.setItem(inc, refInv.getItem(inc));
                        }
                    }
                    if(frames.size() - 1 == in) cancel();
                    else in++;
                }
            }.runTaskTimerAsynchronously(DesertMain.getInstance,0, interval);

        }
    }


}
