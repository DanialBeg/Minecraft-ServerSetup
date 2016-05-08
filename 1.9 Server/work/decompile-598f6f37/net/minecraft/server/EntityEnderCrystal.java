package net.minecraft.server;

import com.google.common.base.Optional;

public class EntityEnderCrystal extends Entity {

    private static final DataWatcherObject<Optional<BlockPosition>> b = DataWatcher.a(EntityEnderCrystal.class, DataWatcherRegistry.k);
    private static final DataWatcherObject<Boolean> c = DataWatcher.a(EntityEnderCrystal.class, DataWatcherRegistry.h);
    public int a;

    public EntityEnderCrystal(World world) {
        super(world);
        this.i = true;
        this.setSize(2.0F, 2.0F);
        this.a = this.random.nextInt(100000);
    }

    public EntityEnderCrystal(World world, double d0, double d1, double d2) {
        this(world);
        this.setPosition(d0, d1, d2);
    }

    protected boolean playStepSound() {
        return false;
    }

    protected void i() {
        this.getDataWatcher().register(EntityEnderCrystal.b, Optional.absent());
        this.getDataWatcher().register(EntityEnderCrystal.c, Boolean.valueOf(true));
    }

    public void m() {
        this.lastX = this.locX;
        this.lastY = this.locY;
        this.lastZ = this.locZ;
        ++this.a;
        if (!this.world.isClientSide) {
            BlockPosition blockposition = new BlockPosition(this);

            if (this.world.worldProvider instanceof WorldProviderTheEnd && this.world.getType(blockposition).getBlock() != Blocks.FIRE) {
                this.world.setTypeUpdate(blockposition, Blocks.FIRE.getBlockData());
            }
        }

    }

    protected void b(NBTTagCompound nbttagcompound) {
        if (this.j() != null) {
            nbttagcompound.set("BeamTarget", GameProfileSerializer.a(this.j()));
        }

        nbttagcompound.setBoolean("ShowBottom", this.k());
    }

    protected void a(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.hasKeyOfType("BeamTarget", 10)) {
            this.a(GameProfileSerializer.c(nbttagcompound.getCompound("BeamTarget")));
        }

        if (nbttagcompound.hasKeyOfType("ShowBottom", 1)) {
            this.a(nbttagcompound.getBoolean("ShowBottom"));
        }

    }

    public boolean isInteractable() {
        return true;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (damagesource.getEntity() instanceof EntityEnderDragon) {
            return false;
        } else {
            if (!this.dead && !this.world.isClientSide) {
                this.die();
                if (!this.world.isClientSide) {
                    this.world.explode((Entity) null, this.locX, this.locY, this.locZ, 6.0F, true);
                    this.a(damagesource);
                }
            }

            return true;
        }
    }

    public void Q() {
        this.a(DamageSource.GENERIC);
        super.Q();
    }

    private void a(DamageSource damagesource) {
        if (this.world.worldProvider instanceof WorldProviderTheEnd) {
            WorldProviderTheEnd worldprovidertheend = (WorldProviderTheEnd) this.world.worldProvider;
            EnderDragonBattle enderdragonbattle = worldprovidertheend.s();

            if (enderdragonbattle != null) {
                enderdragonbattle.a(this, damagesource);
            }
        }

    }

    public void a(BlockPosition blockposition) {
        this.getDataWatcher().set(EntityEnderCrystal.b, Optional.fromNullable(blockposition));
    }

    public BlockPosition j() {
        return (BlockPosition) ((Optional) this.getDataWatcher().get(EntityEnderCrystal.b)).orNull();
    }

    public void a(boolean flag) {
        this.getDataWatcher().set(EntityEnderCrystal.c, Boolean.valueOf(flag));
    }

    public boolean k() {
        return ((Boolean) this.getDataWatcher().get(EntityEnderCrystal.c)).booleanValue();
    }
}
