package net.minecraft.server;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import java.util.UUID;

public class TileEntitySkull extends TileEntity implements ITickable {

    private int a;
    private int rotation;
    private GameProfile g = null;
    private int h;
    private boolean i;
    private static UserCache j;
    private static MinecraftSessionService k;

    public TileEntitySkull() {}

    public static void a(UserCache usercache) {
        TileEntitySkull.j = usercache;
    }

    public static void a(MinecraftSessionService minecraftsessionservice) {
        TileEntitySkull.k = minecraftsessionservice;
    }

    public void save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        nbttagcompound.setByte("SkullType", (byte) (this.a & 255));
        nbttagcompound.setByte("Rot", (byte) (this.rotation & 255));
        if (this.g != null) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            GameProfileSerializer.serialize(nbttagcompound1, this.g);
            nbttagcompound.set("Owner", nbttagcompound1);
        }

    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.a = nbttagcompound.getByte("SkullType");
        this.rotation = nbttagcompound.getByte("Rot");
        if (this.a == 3) {
            if (nbttagcompound.hasKeyOfType("Owner", 10)) {
                this.g = GameProfileSerializer.deserialize(nbttagcompound.getCompound("Owner"));
            } else if (nbttagcompound.hasKeyOfType("ExtraType", 8)) {
                String s = nbttagcompound.getString("ExtraType");

                if (!UtilColor.b(s)) {
                    this.g = new GameProfile((UUID) null, s);
                    this.g();
                }
            }
        }

    }

    public void c() {
        if (this.a == 5) {
            if (this.world.isBlockIndirectlyPowered(this.position)) {
                this.i = true;
                ++this.h;
            } else {
                this.i = false;
            }
        }

    }

    public GameProfile getGameProfile() {
        return this.g;
    }

    public Packet<?> getUpdatePacket() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        this.save(nbttagcompound);
        return new PacketPlayOutTileEntityData(this.position, 4, nbttagcompound);
    }

    public void setSkullType(int i) {
        this.a = i;
        this.g = null;
    }

    public void setGameProfile(GameProfile gameprofile) {
        this.a = 3;
        this.g = gameprofile;
        this.g();
    }

    private void g() {
        this.g = b(this.g);
        this.update();
    }

    public static GameProfile b(GameProfile gameprofile) {
        if (gameprofile != null && !UtilColor.b(gameprofile.getName())) {
            if (gameprofile.isComplete() && gameprofile.getProperties().containsKey("textures")) {
                return gameprofile;
            } else if (TileEntitySkull.j != null && TileEntitySkull.k != null) {
                GameProfile gameprofile1 = TileEntitySkull.j.getProfile(gameprofile.getName());

                if (gameprofile1 == null) {
                    return gameprofile;
                } else {
                    Property property = (Property) Iterables.getFirst(gameprofile1.getProperties().get("textures"), (Object) null);

                    if (property == null) {
                        gameprofile1 = TileEntitySkull.k.fillProfileProperties(gameprofile1, true);
                    }

                    return gameprofile1;
                }
            } else {
                return gameprofile;
            }
        } else {
            return gameprofile;
        }
    }

    public int getSkullType() {
        return this.a;
    }

    public void setRotation(int i) {
        this.rotation = i;
    }
}
