package net.minecraft.server;

public class PlayerInteractManager {

    public World world;
    public EntityPlayer player;
    private WorldSettings.EnumGamemode gamemode;
    private boolean d;
    private int lastDigTick;
    private BlockPosition f;
    private int currentTick;
    private boolean h;
    private BlockPosition i;
    private int j;
    private int k;

    public PlayerInteractManager(World world) {
        this.gamemode = WorldSettings.EnumGamemode.NOT_SET;
        this.f = BlockPosition.ZERO;
        this.i = BlockPosition.ZERO;
        this.k = -1;
        this.world = world;
    }

    public void setGameMode(WorldSettings.EnumGamemode worldsettings_enumgamemode) {
        this.gamemode = worldsettings_enumgamemode;
        worldsettings_enumgamemode.a(this.player.abilities);
        this.player.updateAbilities();
        this.player.server.getPlayerList().sendAll(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_GAME_MODE, new EntityPlayer[] { this.player}));
        this.world.everyoneSleeping();
    }

    public WorldSettings.EnumGamemode getGameMode() {
        return this.gamemode;
    }

    public boolean c() {
        return this.gamemode.e();
    }

    public boolean isCreative() {
        return this.gamemode.isCreative();
    }

    public void b(WorldSettings.EnumGamemode worldsettings_enumgamemode) {
        if (this.gamemode == WorldSettings.EnumGamemode.NOT_SET) {
            this.gamemode = worldsettings_enumgamemode;
        }

        this.setGameMode(this.gamemode);
    }

    public void a() {
        ++this.currentTick;
        float f;
        int i;

        if (this.h) {
            int j = this.currentTick - this.j;
            IBlockData iblockdata = this.world.getType(this.i);
            Block block = iblockdata.getBlock();

            if (iblockdata.getMaterial() == Material.AIR) {
                this.h = false;
            } else {
                f = iblockdata.a((EntityHuman) this.player, this.player.world, this.i) * (float) (j + 1);
                i = (int) (f * 10.0F);
                if (i != this.k) {
                    this.world.c(this.player.getId(), this.i, i);
                    this.k = i;
                }

                if (f >= 1.0F) {
                    this.h = false;
                    this.breakBlock(this.i);
                }
            }
        } else if (this.d) {
            IBlockData iblockdata1 = this.world.getType(this.f);
            Block block1 = iblockdata1.getBlock();

            if (iblockdata1.getMaterial() == Material.AIR) {
                this.world.c(this.player.getId(), this.f, -1);
                this.k = -1;
                this.d = false;
            } else {
                int k = this.currentTick - this.lastDigTick;

                f = iblockdata1.a((EntityHuman) this.player, this.player.world, this.i) * (float) (k + 1);
                i = (int) (f * 10.0F);
                if (i != this.k) {
                    this.world.c(this.player.getId(), this.f, i);
                    this.k = i;
                }
            }
        }

    }

    public void a(BlockPosition blockposition, EnumDirection enumdirection) {
        if (this.isCreative()) {
            if (!this.world.douseFire((EntityHuman) null, blockposition, enumdirection)) {
                this.breakBlock(blockposition);
            }

        } else {
            IBlockData iblockdata = this.world.getType(blockposition);
            Block block = iblockdata.getBlock();

            if (this.gamemode.c()) {
                if (this.gamemode == WorldSettings.EnumGamemode.SPECTATOR) {
                    return;
                }

                if (!this.player.cU()) {
                    ItemStack itemstack = this.player.getItemInMainHand();

                    if (itemstack == null) {
                        return;
                    }

                    if (!itemstack.a(block)) {
                        return;
                    }
                }
            }

            this.world.douseFire((EntityHuman) null, blockposition, enumdirection);
            this.lastDigTick = this.currentTick;
            float f = 1.0F;

            if (iblockdata.getMaterial() != Material.AIR) {
                block.attack(this.world, blockposition, this.player);
                f = iblockdata.a((EntityHuman) this.player, this.player.world, blockposition);
            }

            if (iblockdata.getMaterial() != Material.AIR && f >= 1.0F) {
                this.breakBlock(blockposition);
            } else {
                this.d = true;
                this.f = blockposition;
                int i = (int) (f * 10.0F);

                this.world.c(this.player.getId(), blockposition, i);
                this.k = i;
            }

        }
    }

    public void a(BlockPosition blockposition) {
        if (blockposition.equals(this.f)) {
            int i = this.currentTick - this.lastDigTick;
            IBlockData iblockdata = this.world.getType(blockposition);

            if (iblockdata.getMaterial() != Material.AIR) {
                float f = iblockdata.a((EntityHuman) this.player, this.player.world, blockposition) * (float) (i + 1);

                if (f >= 0.7F) {
                    this.d = false;
                    this.world.c(this.player.getId(), blockposition, -1);
                    this.breakBlock(blockposition);
                } else if (!this.h) {
                    this.d = false;
                    this.h = true;
                    this.i = blockposition;
                    this.j = this.lastDigTick;
                }
            }
        }

    }

    public void e() {
        this.d = false;
        this.world.c(this.player.getId(), this.f, -1);
    }

    private boolean c(BlockPosition blockposition) {
        IBlockData iblockdata = this.world.getType(blockposition);

        iblockdata.getBlock().a(this.world, blockposition, iblockdata, (EntityHuman) this.player);
        boolean flag = this.world.setAir(blockposition);

        if (flag) {
            iblockdata.getBlock().postBreak(this.world, blockposition, iblockdata);
        }

        return flag;
    }

    public boolean breakBlock(BlockPosition blockposition) {
        if (this.gamemode.isCreative() && this.player.getItemInMainHand() != null && this.player.getItemInMainHand().getItem() instanceof ItemSword) {
            return false;
        } else {
            IBlockData iblockdata = this.world.getType(blockposition);
            TileEntity tileentity = this.world.getTileEntity(blockposition);

            if (iblockdata.getBlock() instanceof BlockCommand && !this.player.a(2, "")) {
                this.world.notify(blockposition, iblockdata, iblockdata, 3);
                return false;
            } else {
                if (this.gamemode.c()) {
                    if (this.gamemode == WorldSettings.EnumGamemode.SPECTATOR) {
                        return false;
                    }

                    if (!this.player.cU()) {
                        ItemStack itemstack = this.player.getItemInMainHand();

                        if (itemstack == null) {
                            return false;
                        }

                        if (!itemstack.a(iblockdata.getBlock())) {
                            return false;
                        }
                    }
                }

                this.world.a(this.player, 2001, blockposition, Block.getCombinedId(iblockdata));
                boolean flag = this.c(blockposition);

                if (this.isCreative()) {
                    this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(this.world, blockposition));
                } else {
                    ItemStack itemstack1 = this.player.getItemInMainHand();
                    ItemStack itemstack2 = itemstack1 == null ? null : itemstack1.cloneItemStack();
                    boolean flag1 = this.player.hasBlock(iblockdata);

                    if (itemstack1 != null) {
                        itemstack1.a(this.world, iblockdata, blockposition, this.player);
                        if (itemstack1.count == 0) {
                            this.player.a(EnumHand.MAIN_HAND, (ItemStack) null);
                        }
                    }

                    if (flag && flag1) {
                        iblockdata.getBlock().a(this.world, (EntityHuman) this.player, blockposition, iblockdata, tileentity, itemstack2);
                    }
                }

                return flag;
            }
        }
    }

    public EnumInteractionResult a(EntityHuman entityhuman, World world, ItemStack itemstack, EnumHand enumhand) {
        if (this.gamemode == WorldSettings.EnumGamemode.SPECTATOR) {
            return EnumInteractionResult.PASS;
        } else if (entityhuman.da().a(itemstack.getItem())) {
            return EnumInteractionResult.PASS;
        } else {
            int i = itemstack.count;
            int j = itemstack.getData();
            InteractionResultWrapper interactionresultwrapper = itemstack.a(world, entityhuman, enumhand);
            ItemStack itemstack1 = (ItemStack) interactionresultwrapper.b();

            if (itemstack1 == itemstack && itemstack1.count == i && itemstack1.l() <= 0 && itemstack1.getData() == j) {
                return interactionresultwrapper.a();
            } else {
                entityhuman.a(enumhand, itemstack1);
                if (this.isCreative()) {
                    itemstack1.count = i;
                    if (itemstack1.e()) {
                        itemstack1.setData(j);
                    }
                }

                if (itemstack1.count == 0) {
                    entityhuman.a(enumhand, (ItemStack) null);
                }

                if (!entityhuman.cs()) {
                    ((EntityPlayer) entityhuman).updateInventory(entityhuman.defaultContainer);
                }

                return interactionresultwrapper.a();
            }
        }
    }

    public EnumInteractionResult a(EntityHuman entityhuman, World world, ItemStack itemstack, EnumHand enumhand, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2) {
        if (this.gamemode == WorldSettings.EnumGamemode.SPECTATOR) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof ITileInventory) {
                Block block = world.getType(blockposition).getBlock();
                ITileInventory itileinventory = (ITileInventory) tileentity;

                if (itileinventory instanceof TileEntityChest && block instanceof BlockChest) {
                    itileinventory = ((BlockChest) block).c(world, blockposition);
                }

                if (itileinventory != null) {
                    entityhuman.openContainer(itileinventory);
                    return EnumInteractionResult.SUCCESS;
                }
            } else if (tileentity instanceof IInventory) {
                entityhuman.openContainer((IInventory) tileentity);
                return EnumInteractionResult.SUCCESS;
            }

            return EnumInteractionResult.PASS;
        } else {
            if (!entityhuman.isSneaking() || entityhuman.getItemInMainHand() == null && entityhuman.getItemInOffHand() == null) {
                IBlockData iblockdata = world.getType(blockposition);

                if (iblockdata.getBlock().interact(world, blockposition, iblockdata, entityhuman, enumhand, itemstack, enumdirection, f, f1, f2)) {
                    return EnumInteractionResult.SUCCESS;
                }
            }

            if (itemstack == null) {
                return EnumInteractionResult.PASS;
            } else if (entityhuman.da().a(itemstack.getItem())) {
                return EnumInteractionResult.PASS;
            } else if (itemstack.getItem() instanceof ItemBlock && ((ItemBlock) itemstack.getItem()).d() instanceof BlockCommand && !entityhuman.a(2, "")) {
                return EnumInteractionResult.FAIL;
            } else if (this.isCreative()) {
                int i = itemstack.getData();
                int j = itemstack.count;
                EnumInteractionResult enuminteractionresult = itemstack.placeItem(entityhuman, world, blockposition, enumhand, enumdirection, f, f1, f2);

                itemstack.setData(i);
                itemstack.count = j;
                return enuminteractionresult;
            } else {
                return itemstack.placeItem(entityhuman, world, blockposition, enumhand, enumdirection, f, f1, f2);
            }
        }
    }

    public void a(WorldServer worldserver) {
        this.world = worldserver;
    }
}
